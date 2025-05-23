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

class PushNotificationService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("user_preferences")
            .document(userId)
            .set(mapOf("fcmToken" to token), SetOptions.merge())
            .addOnSuccessListener { Log.d("FCM", "Token updated: $token") }
            .addOnFailureListener { Log.e("FCM", "Failed to update token", it) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.data["title"] ?: "New Session"
        val body = message.data["body"] ?: "A session is about to start!"
        sendNotification(title, body)
    }

    private fun sendNotification(title: String, body: String) {
        val channelId = "session_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Session Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Notifications for upcoming race sessions" }
        notificationManager.createNotificationChannel(channel)

        // Convert drawable resource to Bitmap for large icon
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.wac_logo)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_wac_notification)
            .setLargeIcon(largeIcon)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random().nextInt(), notification)
    }
}