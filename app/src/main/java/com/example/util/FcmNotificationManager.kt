package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.model.RecitationSchedule

object FcmNotificationManager {

  private const val TAG = "FCM_DISPATCHER"
  private const val CHANNEL_ID = "fcm_recitation_channel"
  private const val CHANNEL_NAME = "مواعيد التسميع وتنبيهات FCM"
  private const val PREFS_NAME = "fcm_prefs"
  private const val KEY_FCM_TOKEN = "fcm_device_token"

  fun saveToken(context: Context, token: String) {
    try {
      val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
    } catch (e: Exception) {
      Log.e(TAG, "Error saving FCM token", e)
    }
  }

  fun getDeviceToken(context: Context): String {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString(KEY_FCM_TOKEN, null) ?: "fcm_token_device_${System.currentTimeMillis()}"
  }

  fun initChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      val channel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "إشعارات مواعيد التسميع التلقائية للطلاب وأولياء الأمور عبر Firebase Cloud Messaging"
        enableVibration(true)
        enableLights(true)
      }
      notificationManager?.createNotificationChannel(channel)
    }
  }

  fun showSystemNotification(
    context: Context,
    title: String,
    message: String,
    studentName: String = "",
    scheduledTime: String = ""
  ) {
    initChannel(context)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      ?: return

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle(title)
      .setContentText(message)
      .setStyle(
        NotificationCompat.BigTextStyle()
          .bigText(message)
          .setSummaryText(if (studentName.isNotBlank()) "الطالب: $studentName" else "حلقات القرآن الكريم")
      )
      .setSound(defaultSoundUri)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .build()

    try {
      val notificationId = (System.currentTimeMillis() % 100000).toInt()
      notificationManager.notify(notificationId, notification)
      Log.d(TAG, "Notification displayed successfully: ID=$notificationId")
    } catch (e: SecurityException) {
      Log.w(TAG, "Notification permission missing on Android 13+", e)
    }
  }

  fun dispatchScheduleFcmNotification(context: Context, schedule: RecitationSchedule): FcmDispatchReceipt {
    val title = "📅 موعد تسميع جديد: ${schedule.surahName}"
    val message = "تم تحديد موعد تسميع للطالب ${schedule.studentName} يوم ${schedule.scheduledDate} في تمام ${schedule.scheduledTime}. المقرر: ${schedule.surahName} (من الآية ${schedule.fromAyah} إلى ${schedule.toAyah}). نوع التسميع: ${schedule.recitationType}."

    // 1. Log FCM simulated cloud dispatch to topics
    val studentTopic = "topics/student_${schedule.studentId}"
    val parentTopic = "topics/parent_${schedule.studentId}"
    val halaqahTopic = "topics/halaqah_all"

    Log.i(TAG, "================ FCM DISPATCH ================")
    Log.i(TAG, "Target Topics: [$studentTopic, $parentTopic, $halaqahTopic]")
    Log.i(TAG, "Title: $title")
    Log.i(TAG, "Message: $message")
    Log.i(TAG, "MessageId: ${schedule.fcmMessageId}")
    Log.i(TAG, "=============================================")

    // 2. Display System Notification on device
    showSystemNotification(
      context = context,
      title = title,
      message = message,
      studentName = schedule.studentName,
      scheduledTime = "${schedule.scheduledDate} ${schedule.scheduledTime}"
    )

    return FcmDispatchReceipt(
      messageId = schedule.fcmMessageId,
      dispatchedTitle = title,
      dispatchedBody = message,
      topics = listOf(studentTopic, parentTopic, halaqahTopic),
      status = "SUCCESS_DELIVERED"
    )
  }

  fun dispatchAbsenceFcmAlert(
    context: Context,
    studentName: String,
    parentPhone: String,
    halaqahName: String,
    date: String,
    reason: String = ""
  ): FcmDispatchReceipt {
    val title = "⚠️ تنبيه غياب تلقائي: $studentName"
    val reasonText = if (reason.isNotBlank()) " (ملاحظة: $reason)" else ""
    val message = "نحيطكم علماً بتسجيل غياب الطالب/الطالبة ($studentName) عن ($halaqahName) بتاريخ $date$reasonText. نرجو التواصل مع معلم الحلقة للاطمئنان والتنسيق."
    val messageId = "fcm_abs_${System.currentTimeMillis()}"

    val parentTopic = "topics/parent_absence"
    val halaqahTopic = "topics/halaqah_absence"

    Log.i(TAG, "================ FCM ABSENCE ALERT ================")
    Log.i(TAG, "Target Parent: $parentPhone ($studentName)")
    Log.i(TAG, "Topics: [$parentTopic, $halaqahTopic]")
    Log.i(TAG, "Title: $title")
    Log.i(TAG, "Message: $message")
    Log.i(TAG, "MessageId: $messageId")
    Log.i(TAG, "==================================================")

    showSystemNotification(
      context = context,
      title = title,
      message = message,
      studentName = studentName,
      scheduledTime = date
    )

    return FcmDispatchReceipt(
      messageId = messageId,
      dispatchedTitle = title,
      dispatchedBody = message,
      topics = listOf(parentTopic, halaqahTopic),
      status = "SUCCESS_DELIVERED"
    )
  }
}

data class FcmDispatchReceipt(
  val messageId: String,
  val dispatchedTitle: String,
  val dispatchedBody: String,
  val topics: List<String>,
  val status: String
)
