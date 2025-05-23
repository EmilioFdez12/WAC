const { onSchedule } = require("firebase-functions/v2/scheduler");
const { logger } = require("firebase-functions");
const admin = require("firebase-admin");

// Importar módulos separados
const sessionNotifications = require('./src/sessionNotifications');
const standingsNotifications = require('./src/standingsNotifications');
const utils = require('./src/utils');

admin.initializeApp();

/**
 * Función principal para verificar sesiones próximas
 */
exports.checkUpcomingSessions = onSchedule(
  {
    schedule: "*/5 * * * *",
    region: "europe-west1",
    timeoutSeconds: 180,
    memory: "512MiB",
  },
  async (event) => {
    const db = admin.firestore();
    
    try {
      logger.log(`🔍 Verificando sesiones próximas: ${new Date().toISOString()}`);
      await sessionNotifications.checkAndSendSessionNotifications(db);
      return null;
    } catch (error) {
      logger.error("❌ Error al verificar sesiones próximas:", error);
      return null;
    }
  }
);

/**
 * Función para verificar cambios en standings y notificar
 */
exports.checkStandingsChanges = onSchedule(
  {
    schedule: "0 */2 * * *",
    region: "europe-west1",
    timeoutSeconds: 300,
    memory: "512MiB",
  },
  async (event) => {
    const db = admin.firestore();
    
    try {
      logger.log(`📊 Verificando cambios en standings: ${new Date().toISOString()}`);
      await standingsNotifications.checkAndSendStandingsNotifications(db);
      return null;
    } catch (error) {
      logger.error("❌ Error al verificar cambios en standings:", error);
      return null;
    }
  }
);

/**
 * Función para limpiar tokens FCM inválidos
 */
exports.cleanInvalidTokens = onSchedule(
  {
    schedule: "0 2 * * 0",
    region: "europe-west1",
    timeoutSeconds: 300,
    memory: "256MiB",
  },
  async (event) => {
    const db = admin.firestore();
    
    try {
      logger.log("🧹 Iniciando limpieza de tokens FCM inválidos");
      await utils.cleanInvalidTokens(db);
    } catch (error) {
      logger.error("❌ Error en limpieza de tokens:", error);
    }
  }
);