const { logger } = require("firebase-functions");
const admin = require("firebase-admin");
const utils = require('./utils');

/**
 * Verifica y envía notificaciones de sesiones próximas
 */
async function checkAndSendSessionNotifications(db) {
  const now = new Date();
  const sentNotifications = new Set();
  const processedCategories = new Set();

  try {
    const categories = ["f1", "motogp", "indycar"];

    // Obtener notificaciones enviadas en las últimas 2 horas
    const sentNotificationsSnapshot = await db.collection("sent_notifications")
      .where("timestamp", ">", admin.firestore.Timestamp.fromDate(
        new Date(now.getTime() - 2 * 60 * 60 * 1000)
      ))
      .get();
    
    sentNotificationsSnapshot.forEach(doc => {
      sentNotifications.add(doc.id);
    });

    for (const category of categories) {
      if (processedCategories.has(category)) continue;

      const scheduleRef = db.collection(`${category}_schedule`);
      const scheduleSnapshot = await scheduleRef.get();

      if (scheduleSnapshot.empty) {
        logger.log(`⚠️ No hay eventos en ${category}_schedule`);
        continue;
      }

      const events = [];
      scheduleSnapshot.forEach(doc => {
        const eventData = doc.data();
        if (eventData.sessions) {
          events.push({
            id: doc.id,
            data: eventData
          });
        }
      });

      // Ordenar eventos por la sesión más próxima
      events.sort((a, b) => {
        const aNextSession = getNextSessionTime(a.data.sessions);
        const bNextSession = getNextSessionTime(b.data.sessions);
        if (!aNextSession) return 1;
        if (!bNextSession) return -1;
        return aNextSession - bNextSession;
      });

      let foundUpcomingSession = false;
      
      for (const event of events.slice(0, 3)) {
        const eventData = event.data;
        const sessions = eventData.sessions;

        if (!sessions) continue;

        const sessionEntries = Object.entries(sessions)
          .filter(([_, sessionData]) => sessionData && sessionData.isoDateTime)
          .map(([sessionType, sessionData]) => ({
            type: sessionType,
            data: sessionData,
            time: new Date(sessionData.isoDateTime)
          }))
          .filter(session => session.time > now)
          .sort((a, b) => a.time - b.time);

        for (const session of sessionEntries) {
          const sessionTime = session.time;
          const diffMinutes = Math.floor((sessionTime - now) / (1000 * 60));
          
          if (diffMinutes >= 1 && diffMinutes <= 15) {
            const notificationKey = `${category}_${event.id}_${session.type}_${Math.floor(sessionTime.getTime() / 60000)}`;
            
            if (sentNotifications.has(notificationKey)) {
              continue;
            }

            logger.log(`🚨 ENVIANDO NOTIFICACIÓN: ${category.toUpperCase()} - ${utils.formatSessionName(session.type)} - ${eventData.gp} - En ${diffMinutes} minutos`);

            await sendSessionNotifications(
              db, 
              category, 
              eventData.gp || "Gran Premio", 
              session.type, 
              diffMinutes,
              notificationKey
            );
            
            foundUpcomingSession = true;
            break;
          } else if (diffMinutes > 15) {
            break;
          }
        }
        
        if (foundUpcomingSession) {
          processedCategories.add(category);
          break;
        }
      }

      if (!foundUpcomingSession) {
        logger.log(`✅ No hay sesiones próximas para ${category.toUpperCase()}`);
      }
    }

    await utils.cleanOldNotifications(db);

  } catch (error) {
    logger.error("❌ Error en checkAndSendSessionNotifications:", error);
  }
}

/**
 * Obtiene el tiempo de la próxima sesión
 */
function getNextSessionTime(sessions) {
  const now = new Date();
  let nextTime = null;

  Object.values(sessions).forEach(session => {
    if (session && session.isoDateTime) {
      const sessionTime = new Date(session.isoDateTime);
      if (sessionTime > now && (!nextTime || sessionTime < nextTime)) {
        nextTime = sessionTime;
      }
    }
  });

  return nextTime;
}

/**
 * Envía notificaciones de sesiones a usuarios suscritos
 */
async function sendSessionNotifications(db, category, gpName, sessionType, diffMinutes, notificationKey) {
  try {
    const usersSnapshot = await db.collection("user_preferences")
      .where("fcmToken", "!=", null)
      .get();
    
    if (usersSnapshot.empty) {
      logger.log(`⚠️ No se encontraron usuarios con FCM tokens`);
      return;
    }

    const sessionName = utils.formatSessionName(sessionType);
    const messages = [];
    let eligibleUsers = 0;

    for (const userDoc of usersSnapshot.docs) {
      const userData = userDoc.data();
      const preferences = userData.preferences || [];
      const fcmToken = userData.fcmToken;

      if (!fcmToken) continue;

      const pref = preferences.find(
        (p) => p.category.toLowerCase() === category.toLowerCase() && p.notificationsEnabled
      );
      
      if (pref) {
        eligibleUsers++;
        
        // Mensaje simple sin mencionar piloto favorito
        const bodyMessage = `¡La sesión comienza en ${diffMinutes} minutos!`;

        messages.push({
          notification: {
            title: `🏁 ${sessionName} - ${gpName}`,
            body: bodyMessage,
          },
          data: {
            category,
            sessionType,
            gpName,
            diffMinutes: diffMinutes.toString(),
            title: `🏁 ${sessionName} - ${gpName}`,
            body: bodyMessage,
          },
          token: fcmToken,
          android: {
            priority: "high",
            notification: {
              channelId: "session_channel",
              priority: "high",
              defaultSound: true,
            }
          }
        });
      }
    }

    if (messages.length === 0) {
      logger.log(`📱 No hay usuarios suscritos a ${category.toUpperCase()} (${eligibleUsers} usuarios verificados)`);
      return;
    }

    logger.log(`📤 Enviando ${messages.length} notificaciones para ${category.toUpperCase()} - ${sessionName}`);
    
    const result = await utils.sendMessagesInBatches(messages);
    
    // Registrar la notificación como enviada
    await db
      .collection("sent_notifications")
      .doc(notificationKey)
      .set({
        category,
        eventId: notificationKey.split('_')[1],
        sessionType,
        gpName,
        diffMinutes,
        sentTo: messages.length,
        successCount: result.totalSuccess,
        failureCount: result.totalFailures,
        timestamp: admin.firestore.FieldValue.serverTimestamp(),
      });

  } catch (error) {
    logger.error(`❌ Error enviando notificaciones de sesión para ${category}:`, error);
  }
}

module.exports = {
  checkAndSendSessionNotifications
};