const { logger } = require("firebase-functions");
const admin = require("firebase-admin");
const utils = require('./utils');

/**
 * Sistemas de puntuación por categoría
 */
const POINTS_SYSTEMS = {
  f1: {
    race: [25, 18, 15, 12, 10, 8, 6, 4, 2, 1],
    sprint: [8, 7, 6, 5, 4, 3, 2, 1]
  },
  motogp: {
    race: [25, 20, 16, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1],
    sprint: [12, 9, 7, 6, 5, 4, 3, 2, 1]
  },
  indycar: {
    race: [50, 40, 35, 32, 30, 28, 26, 24, 22, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5]
  }
};

/**
 * Verifica cambios en standings y envía notificaciones
 */
async function checkAndSendStandingsNotifications(db) {
  try {
    const categories = ["f1", "motogp", "indycar"];

    for (const category of categories) {
      await checkCategoryStandingsChanges(db, category);
    }

  } catch (error) {
    logger.error("❌ Error en checkAndSendStandingsNotifications:", error);
  }
}

/**
 * Verifica cambios en standings de una categoría específica
 */
async function checkCategoryStandingsChanges(db, category) {
  try {
    // Obtener standings actuales
    const currentStandingsSnapshot = await db.collection(`${category}_standings`).get();

    if (currentStandingsSnapshot.empty) {
      logger.log(`⚠️ No hay standings para ${category}`);
      return;
    }

    // Obtener standings anteriores guardados
    const previousStandingsDoc = await db.collection("previous_standings").doc(category).get();
    const previousStandings = previousStandingsDoc.exists ? previousStandingsDoc.data().drivers || [] : [];

    // Crear mapa de standings actuales
    const currentStandings = [];
    currentStandingsSnapshot.forEach(doc => {
      const driverData = doc.data();

      // Validate that required fields exist and are not undefined
      if (driverData.name && driverData.name !== undefined) {
        currentStandings.push({
          id: doc.id,
          name: driverData.name,
          points: driverData.points || 0,
          position: driverData.position || 999
        });
      } else {
        logger.warn(`⚠️ Skipping driver with undefined name in ${category}: ${doc.id}`);
      }
    });

    // Ordenar por puntos (descendente)
    currentStandings.sort((a, b) => b.points - a.points);

    // Detectar cambios y enviar notificaciones
    await detectAndNotifyChanges(db, category, previousStandings, currentStandings);

    // Guardar standings actuales como anteriores
    const validStandings = currentStandings.filter(driver =>
      driver.name &&
      driver.name !== undefined &&
      driver.points !== undefined &&
      driver.position !== undefined
    );

    if (validStandings.length > 0) {
      await db.collection("previous_standings").doc(category).set({
        drivers: validStandings,
        lastUpdate: admin.firestore.FieldValue.serverTimestamp()
      });
    } else {
      logger.warn(`⚠️ No valid standings to save for ${category}`);
    }

  } catch (error) {
    logger.error(`❌ Error verificando standings de ${category}:`, error);
  }
}

/**
 * Detecta cambios y envía notificaciones a usuarios con pilotos favoritos
 */
async function detectAndNotifyChanges(db, category, previousStandings, currentStandings) {
  try {
    // Obtener usuarios con pilotos favoritos en esta categoría
    const usersSnapshot = await db.collection("user_preferences")
      .where("fcmToken", "!=", null)
      .get();

    if (usersSnapshot.empty) return;

    const messages = [];

    for (const userDoc of usersSnapshot.docs) {
      const userData = userDoc.data();
      const preferences = userData.preferences || [];
      const fcmToken = userData.fcmToken;

      if (!fcmToken) continue;

      const pref = preferences.find(
        (p) => p.category.toLowerCase() === category.toLowerCase() &&
          p.notificationsEnabled &&
          p.favoriteDriver
      );

      if (!pref) continue;

      const favoriteDriver = pref.favoriteDriver;

      // Buscar el piloto en standings actuales y anteriores
      const currentDriver = currentStandings.find(d => d.name === favoriteDriver);
      const previousDriver = previousStandings.find(d => d.name === favoriteDriver);

      if (!currentDriver) continue;

      // Calcular cambios
      const pointsChange = previousDriver ?
        currentDriver.points - previousDriver.points : 0;

      if (pointsChange > 0) {
        // Determinar posición basada en puntos ganados
        const position = determinePositionFromPoints(category, pointsChange);
        const positionText = getPositionText(position);

        const title = `🏆 ${favoriteDriver} - ${category.toUpperCase()}`;
        const body = `¡${favoriteDriver} ${positionText} y ganó ${pointsChange} puntos!`;

        messages.push({
          notification: {
            title,
            body,
          },
          data: {
            type: "standings_update",
            category,
            driverName: favoriteDriver,
            pointsChange: pointsChange.toString(),
            position: position.toString(),
            title,
            body,
          },
          token: fcmToken,
          android: {
            priority: "high",
            notification: {
              channelId: "standings_channel",
              priority: "high",
              defaultSound: true,
            }
          }
        });
      } else if (pointsChange === 0 && previousDriver) {
        // No ganó puntos = fuera del top 10
        const title = `📊 ${favoriteDriver} - ${category.toUpperCase()}`;
        const body = `${favoriteDriver} terminó fuera de los puntos`;

        messages.push({
          notification: {
            title,
            body,
          },
          data: {
            type: "standings_update",
            category,
            driverName: favoriteDriver,
            pointsChange: "0",
            position: "0",
            title,
            body,
          },
          token: fcmToken,
          android: {
            priority: "normal",
            notification: {
              channelId: "standings_channel",
              priority: "normal",
              defaultSound: true,
            }
          }
        });
      }
    }

    if (messages.length > 0) {
      logger.log(`📊 Enviando ${messages.length} notificaciones de standings para ${category.toUpperCase()}`);
      await utils.sendMessagesInBatches(messages);
    }

  } catch (error) {
    logger.error(`❌ Error detectando cambios en standings de ${category}:`, error);
  }
}

/**
 * Determina la posición basada en los puntos ganados
 */
function determinePositionFromPoints(category, points) {
  const system = POINTS_SYSTEMS[category];
  if (!system) return 0;

  // Verificar en carrera principal
  const racePosition = system.race.indexOf(points);
  if (racePosition !== -1) {
    return racePosition + 1; // +1 porque indexOf devuelve 0-based
  }

  // Verificar en sprint si existe
  if (system.sprint) {
    const sprintPosition = system.sprint.indexOf(points);
    if (sprintPosition !== -1) {
      return sprintPosition + 1;
    }
  }

  return 0; // No se encontró la posición
}

/**
 * Convierte la posición numérica en texto descriptivo
 */
function getPositionText(position) {
  switch (position) {
    case 1: return "terminó 1º";
    case 2: return "terminó 2º";
    case 3: return "terminó 3º";
    case 4: return "terminó 4º";
    case 5: return "terminó 5º";
    case 6: return "terminó 6º";
    case 7: return "terminó 7º";
    case 8: return "terminó 8º";
    case 9: return "terminó 9º";
    case 10: return "terminó 10º";
    case 11: return "terminó 11º";
    case 12: return "terminó 12º";
    case 13: return "terminó 13º";
    case 14: return "terminó 14º";
    case 15: return "terminó 15º";
    default: return "sumó puntos";
  }
}

module.exports = {
  checkAndSendStandingsNotifications
};