package com.example.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

/**
 * نماذج بيانات Firebase Firestore لتخزين ومزامنة الحلقات والطلاب مع مراعاة هيكلية الصلاحيات المعتمدة
 * (Role-Based Access Control: المشرف العام، مشرف القطاع، المعلم، الطالب، ولي الأمر).
 */

data class FirestoreHalaqah(
  @DocumentId val id: String = "",
  val name: String = "",
  val section: String = "BOYS", // BOYS أو GIRLS
  val teacherId: String = "",
  val teacherName: String = "",
  val teacherPhone: String = "",
  val location: String = "",
  val timeSlot: String = "",
  val studentCount: Int = 0,
  val capacity: Int = 20,
  val targetLevel: String = "",
  val days: String = "",
  val currentCurriculum: String = "",
  val accessCode: String = "",
  val colorHex: Long = 0xFF0D6850,
  @get:PropertyName("isActive") val isActive: Boolean = true,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

data class FirestoreStudent(
  @DocumentId val id: String = "",
  val name: String = "",
  val phone: String = "",
  val parentName: String = "",
  val parentPhone: String = "",
  val groupName: String = "",
  val halaqahId: String = "",
  val section: String = "BOYS", // BOYS أو GIRLS
  val level: String = "متوسط",
  val currentSurah: String = "البقرة",
  val currentAyah: Int = 1,
  val memorizedJuzCount: Int = 3,
  val studentAccessCode: String = "",
  val parentAccessCode: String = "",
  val teacherUid: String = "",
  val notes: String = "",
  val isGirlsSection: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

data class FirestoreRecitationRecord(
  @DocumentId val id: String = "",
  val studentId: String = "",
  val studentName: String = "",
  val halaqahId: String = "",
  val halaqahName: String = "",
  val section: String = "BOYS",
  val surahName: String = "",
  val fromAyah: Int = 1,
  val toAyah: Int = 1,
  val recitationType: String = "حفظ جديد",
  val evaluation: String = "ممتاز",
  val mistakesCount: Int = 0,
  val tajweedScore: Int = 10,
  val teacherNotes: String = "",
  val teacherName: String = "",
  val date: String = "",
  val audioUrl: String? = null,
  val timestamp: Long = System.currentTimeMillis()
)

data class FirestoreScheduleItem(
  @DocumentId val id: String = "",
  val studentId: String = "",
  val studentName: String = "",
  val parentPhone: String = "",
  val halaqahName: String = "",
  val scheduledDate: String = "",
  val scheduledTime: String = "",
  val surahName: String = "",
  val fromAyah: Int = 1,
  val toAyah: Int = 1,
  val recitationType: String = "حفظ جديد",
  val teacherName: String = "",
  val status: String = "مجدول", // مجدول, مكتمل, ملغي
  val fcmMessageId: String = "",
  val fcmNotified: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)

data class FirestoreUserPermission(
  @DocumentId val uid: String = "",
  val role: String = "TEACHER", // SUPERVISOR, TEACHER, STUDENT, PARENT
  val userName: String = "",
  val accessCode: String = "",
  val assignedHalaqah: String? = null,
  val assignedStudentId: String? = null,
  val assignedSection: String? = "ALL", // BOYS, GIRLS, ALL
  val canManageStudents: Boolean = false,
  val canRecordRecitation: Boolean = false,
  val canTakeAttendance: Boolean = false,
  val canScheduleFCM: Boolean = true,
  val canViewOtherHalaqat: Boolean = false,
  val canBroadcastAnnouncements: Boolean = false,
  val scopeNotes: String = ""
)

data class FirestoreAttendanceRecord(
  @DocumentId val id: String = "",
  val studentId: Long = 0,
  val studentName: String = "",
  val halaqahName: String = "",
  val section: String = "BOYS", // BOYS أو GIRLS
  val date: String = "",
  val status: String = "حاضر", // حاضر, غائب, متأخر, مستأذن
  val notes: String = "",
  val fcmAlertSent: Boolean = false,
  val parentPhone: String = "",
  val recordedAt: Long = System.currentTimeMillis()
)
