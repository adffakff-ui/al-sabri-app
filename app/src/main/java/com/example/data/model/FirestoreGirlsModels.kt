package com.example.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

/**
 * سجل المشرفة المعتمد في Firebase Firestore بقسم البنات (/girls_supervisors/{supervisorId})
 */
data class FirestoreGirlsSupervisor(
  @DocumentId val id: String = "",
  val uid: String = "",
  val code: String = "", // e.g. SUP-GIRLS-200
  val name: String = "",
  val phone: String = "",
  val role: String = "GIRLS_ADMIN",
  val assignedSection: String = "GIRLS", // عزل قطاع البنات
  @get:PropertyName("isActive") val isActive: Boolean = true,
  val verifiedAt: Long = System.currentTimeMillis()
)

/**
 * سجل المعلمة في قسم البنات (/girls_teachers/{teacherId}) مع تقييد الصلاحية بالحلقة المسندة
 */
data class FirestoreGirlsTeacher(
  @DocumentId val id: String = "",
  val uid: String = "",
  val code: String = "", // e.g. TCH-KHADIJA-33
  val name: String = "",
  val phone: String = "",
  val assignedHalaqahId: String = "", // قصر الصلاحية على هذه الحلقة فقط
  val assignedHalaqahName: String = "",
  val sanadIjazah: String = "إجازة بقراءة عاصم بروايتي شعبة وحفص",
  val assignedSection: String = "GIRLS",
  val days: String = "السبت إلى الأربعاء (عصراً)",
  @get:PropertyName("isActive") val isActive: Boolean = true
)

/**
 * وثيقة حلقة التحفيظ النسائية في Firestore (/girls_halaqat/{halaqahId})
 */
data class FirestoreGirlsHalaqah(
  @DocumentId val id: String = "",
  val name: String = "",
  val section: String = "GIRLS", // عزل صارم: GIRLS
  val teacherId: String = "",
  val teacherName: String = "",
  val teacherPhone: String = "",
  val location: String = "القاعة الكبرى - مجمع عائشة",
  val timeSlot: String = "العصر (4:00 - 6:00 م)",
  val studentCount: Int = 0,
  val capacity: Int = 20,
  val targetLevel: String = "حفظ وإتقان",
  val days: String = "السبت - الإثنين - الأربعاء",
  val accessCode: String = "",
  val colorHex: Long = 0xFF9D174D, // وردي ملكي مخصص لقسم البنات
  @get:PropertyName("isActive") val isActive: Boolean = true,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

/**
 * وثيقة الطالبة في المسار الفرعي (/girls_halaqat/{halaqahId}/students/{studentId})
 */
data class FirestoreGirlsStudent(
  @DocumentId val id: String = "",
  val halaqahId: String = "",
  val name: String = "",
  val phone: String = "",
  val parentName: String = "",
  val parentPhone: String = "",
  val level: String = "متوسط",
  val currentSurah: String = "البقرة",
  val currentAyah: Int = 1,
  val memorizedJuzCount: Int = 3,
  val studentAccessCode: String = "",
  val parentAccessCode: String = "",
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

/**
 * وثيقة التسميع اليومي في المسار الفرعي (/girls_halaqat/{halaqahId}/recitations/{recitationId})
 */
data class FirestoreGirlsRecitation(
  @DocumentId val id: String = "",
  val halaqahId: String = "",
  val studentId: String = "",
  val studentName: String = "",
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
  val timestamp: Long = System.currentTimeMillis()
)

/**
 * وثيقة الحضور والغياب في المسار الفرعي (/girls_halaqat/{halaqahId}/attendance/{attendanceId})
 */
data class FirestoreGirlsAttendance(
  @DocumentId val id: String = "",
  val halaqahId: String = "",
  val studentId: String = "",
  val studentName: String = "",
  val date: String = "",
  val status: String = "حاضر", // حاضر, غائب, متأخر, مستأذن
  val notes: String = "",
  val recordedAt: Long = System.currentTimeMillis()
)

/**
 * نتيجة التحقق الأمني من هوية المشرفة وصلاحيات القسم
 */
sealed class GirlsVerificationResult {
  data class Success(
    val name: String,
    val code: String,
    val roleTitle: String,
    val isFullSupervisor: Boolean, // true = مشرفة عامة على كل الحلقات، false = معلمة مقيدة بحلقتها
    val assignedHalaqahId: String? = null,
    val assignedHalaqahName: String? = null,
    val securityBadgeMessage: String = "هوية معتمدة في Firestore • أمان معزول"
  ) : GirlsVerificationResult()

  data class Denied(
    val reason: String,
    val isBoySectionViolation: Boolean = false
  ) : GirlsVerificationResult()
}
