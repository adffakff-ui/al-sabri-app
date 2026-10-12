package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.R

object NotificationHelper {
  private const val CHANNEL_ID = "quran_notifications"
  private const val CHANNEL_NAME = "إشعارات الحلقات القرآنية"

  fun showNotification(context: Context, title: String, message: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      ?: return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "إشعارات حضور وتسميع القرآن للطلاب وأولياء الأمور"
      }
      notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle(title)
      .setContentText(message)
      .setStyle(NotificationCompat.BigTextStyle().bigText(message))
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setAutoCancel(true)
      .build()

    try {
      notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    } catch (_: SecurityException) {
      // Handled gracefully if notification permission not granted
    }
  }
}
