package com.emi.wac.data.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import com.emi.wac.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random

/**
 * Service for handling push notifications.
 *
 * This service manages the reception and display of push notifications
 * related to race sessions and updates the user's FCM token in Firestore.
 */
class PushNotificationService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Get the current user's ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        // Update the user's FCM token in Firestore
        FirebaseFirestore.getInstance()
            .collection("user_preferences")
            .document(userId)
            .set(mapOf("fcmToken" to token), SetOptions.merge())
            .addOnSuccessListener { Log.d("FCM", "Token updated: $token") }
            .addOnFailureListener { Log.e("FCM", "Failed to update token", it) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Extract the title and body from the received message
        val title = message.data["title"] ?: "New Session"
        val body = message.data["body"] ?: "A session is about to start!"
        // Send a notification with the extracted title and body
        sendNotification(title, body)
    }

    // Sends a notification with the given title and body
    private fun sendNotification(title: String, body: String) {
        val channelId = "session_channel"
        // Get the notification manager service
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel if it doesn't exist
        val channel = NotificationChannel(
            channelId,
            "Session Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Notifications for upcoming race sessions" }
        notificationManager.createNotificationChannel(channel)

        // Convert drawable resource to Bitmap for large icon
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.wac_logo)

        // Build the notification with title, body, and icons
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_wac_notification)
            .setLargeIcon(largeIcon)
            .setAutoCancel(true)
            .build()

        // Display the notification
        notificationManager.notify(Random().nextInt(), notification)
    }
}