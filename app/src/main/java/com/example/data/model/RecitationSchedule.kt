package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recitation_schedules")
data class RecitationSchedule(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val studentId: Long,
  val studentName: String,
  val parentPhone: String,
  val groupName: String,
  val scheduledDate: String, // e.g. "2026-10-15"
  val scheduledTime: String, // e.g. "04:30 م"
  val surahName: String,
  val fromAyah: Int = 1,
  val toAyah: Int = 10,
  val recitationType: String = "حفظ جديد", // حفظ جديد, مراجعة صغرى, مراجعة كبرى, اختبار مرحلي
  val teacherName: String = "الشيخ عبد الله بن فهد",
  val teacherNotes: String = "",
  val status: String = "مجدول", // مجدول, مكتمل, معتذر, مؤجل
  val fcmNotificationSent: Boolean = true,
  val fcmMessageId: String = "fcm_msg_${System.currentTimeMillis()}",
  val associationId: String = "HAIEL_SAEED",
  val timestamp: Long = System.currentTimeMillis()
)
