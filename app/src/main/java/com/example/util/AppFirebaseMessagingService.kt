package com.example.util

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AppFirebaseMessagingService : FirebaseMessagingService() {

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    Log.d("FCM_SERVICE", "Refreshed FCM Token: $token")
    FcmNotificationManager.saveToken(applicationContext, token)
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)
    Log.d("FCM_SERVICE", "From: ${remoteMessage.from}")

    val title = remoteMessage.notification?.title
      ?: remoteMessage.data["title"]
      ?: "تنبيه موعد تسميع جديد"

    val body = remoteMessage.notification?.body
      ?: remoteMessage.data["body"]
      ?: "تم تحديد موعد تسميع جديد لك في حلقة القرآن الكريم"

    val studentName = remoteMessage.data["studentName"] ?: ""
    val scheduledDate = remoteMessage.data["scheduledDate"] ?: ""
    val scheduledTime = remoteMessage.data["scheduledTime"] ?: ""
    val surahName = remoteMessage.data["surahName"] ?: ""

    FcmNotificationManager.showSystemNotification(
      context = applicationContext,
      title = title,
      message = body,
      studentName = studentName,
      scheduledTime = "$scheduledDate $scheduledTime"
    )
  }
}
