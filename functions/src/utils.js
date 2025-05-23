const { logger } = require("firebase-functions");
const admin = require("firebase-admin");

/**
 * Formatea el nombre de la sesión para mostrar
 */
function formatSessionName(sessionType) {
    switch (sessionType.toLowerCase()) {
        case 'fp1':
            return 'Práctica Libre 1';
        case 'fp2':
            return 'Práctica Libre 2';
        case 'fp3':
            return 'Práctica Libre 3';
        case 'qualifying':
            return 'Clasificación';
        case 'sprint':
            return 'Sprint';
        case 'race':
            return 'Carrera';
        default:
            return sessionType;
    }
}

/**
 * Envía mensajes en lotes para evitar límites de FCM
 */
async function sendMessagesInBatches(messages) {
    let successCount = 0;
    let failureCount = 0;

    // Enviar mensajes uno por uno en lugar de usar sendAll
    for (const message of messages) {
        try {
            await admin.messaging().send(message);
            successCount++;
        } catch (error) {
            logger.error(`Error enviando mensaje FCM: ${error.message}`);
            failureCount++;
        }
    }

    return {
        totalSuccess: successCount,
        totalFailures: failureCount
    };
}

/**
 * Limpia notificaciones antiguas
 */
async function cleanOldNotifications(db) {
    try {
        const oldNotificationsSnapshot = await db.collection("sent_notifications")
            .where("timestamp", "<", admin.firestore.Timestamp.fromDate(
                new Date(Date.now() - 7 * 24 * 60 * 60 * 1000) // 7 días
            ))
            .get();

        const batch = db.batch();
        oldNotificationsSnapshot.forEach(doc => {
            batch.delete(doc.ref);
        });

        await batch.commit();
        logger.log(`🧹 Se eliminaron ${oldNotificationsSnapshot.size} notificaciones antiguas`);
    } catch (error) {
        logger.error("Error limpiando notificaciones antiguas:", error);
    }
}

/**
 * Limpia tokens FCM inválidos
 */
async function cleanInvalidTokens(db) {
    try {
        const usersSnapshot = await db.collection("user_preferences")
            .where("fcmToken", "!=", null)
            .get();

        let cleanedTokens = 0;
        const batch = db.batch();

        for (const doc of usersSnapshot.docs) {
            const token = doc.data().fcmToken;
            try {
                await admin.messaging().send({ token }, true);
            } catch (error) {
                if (error.code === 'messaging/registration-token-not-registered') {
                    batch.update(doc.ref, { fcmToken: null });
                    cleanedTokens++;
                }
            }
        }

        if (cleanedTokens > 0) {
            await batch.commit();
            logger.log(`🧹 Se limpiaron ${cleanedTokens} tokens FCM inválidos`);
        }
    } catch (error) {
        logger.error("Error limpiando tokens FCM:", error);
    }
}

module.exports = {
    formatSessionName,
    sendMessagesInBatches,
    cleanOldNotifications,
    cleanInvalidTokens
};