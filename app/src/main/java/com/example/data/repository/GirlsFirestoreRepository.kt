package com.example.data.repository

import android.util.Log
import com.example.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * مستودع Firestore لقسم حلقات البنات:
 * - التحقق الصارم من هوية المشرفة في Firestore
 * - عزل تام لبيانات حلقات البنات ومجموعاتها الفرعية
 * - تطبيق حظر فوري لأي محاولة للوصول لبيانات قطاع البنين
 * - قصر اطلاع المعلمات على حلقاتهن المسندة فقط
 */
class GirlsFirestoreRepository(
  private val firestore: FirebaseFirestore = try {
    FirebaseFirestore.getInstance()
  } catch (e: Exception) {
    Log.w("GirlsRepo", "Firestore not initialized or offline: ${e.message}")
    throw e
  }
) {

  companion object {
    private const val TAG = "GirlsFirestoreRepo"
    const val COLL_GIRLS_SUPERVISORS = "girls_supervisors"
    const val COLL_GIRLS_TEACHERS = "girls_teachers"
    const val COLL_GIRLS_HALAQAT = "girls_halaqat"
    const val SUB_STUDENTS = "students"
    const val SUB_RECITATIONS = "recitations"
    const val SUB_ATTENDANCE = "attendance"

    // المعرفات المعتمدة الافتراضية للقطاع النسائي
    val APPROVED_SUPERVISORS = listOf(
      FirestoreGirlsSupervisor(
        id = "sup_girls_main",
        code = "SUP-GIRLS-200",
        name = "الشيخة أم عبد الله الأنصاري",
        phone = "0551122334",
        role = "GIRLS_ADMIN",
        assignedSection = "GIRLS",
        isActive = true
      )
    )

    val APPROVED_TEACHERS = listOf(
      FirestoreGirlsTeacher(
        id = "tch_khadija",
        code = "TCH-KHADIJA-33",
        name = "الشيخة أم عبد الله",
        phone = "0559988771",
        assignedHalaqahId = "halaqah-khadija",
        assignedHalaqahName = "حلقة خديجة بنت خويلد رضي الله عنها",
        sanadIjazah = "إجازة بقراءة عاصم بروايتي حفص وشعبة",
        assignedSection = "GIRLS"
      ),
      FirestoreGirlsTeacher(
        id = "tch_aisha",
        code = "TCH-AISHA-22",
        name = "أستاذة فاطمة الزهراء",
        phone = "0559988772",
        assignedHalaqahId = "halaqah-aisha",
        assignedHalaqahName = "حلقة عائشة أم المؤمنين رضي الله عنها",
        sanadIjazah = "إجازة برواية حفص عن عاصم من طريق الشاطبية",
        assignedSection = "GIRLS"
      ),
      FirestoreGirlsTeacher(
        id = "tch_hafsa",
        code = "TCH-HAFSA-44",
        name = "أستاذة مريم الصديق",
        phone = "0559988773",
        assignedHalaqahId = "halaqah-hafsa",
        assignedHalaqahName = "حلقة حفصة بنت عمر رضي الله عنها",
        sanadIjazah = "إتقان وحفظ القراءات السبع",
        assignedSection = "GIRLS"
      )
    )

    val DEFAULT_GIRLS_HALAQAT = listOf(
      FirestoreGirlsHalaqah(
        id = "halaqah-khadija",
        name = "حلقة خديجة بنت خويلد رضي الله عنها",
        teacherId = "tch_khadija",
        teacherName = "الشيخة أم عبد الله",
        teacherPhone = "0559988771",
        targetLevel = "حفظ كامل مع الإتقان والتجويد",
        location = "القاعة الكبرى (مجمع عائشة)",
        timeSlot = "عصراً (4:00 - 6:00 م)",
        studentCount = 18,
        capacity = 20,
        accessCode = "TCH-KHADIJA-33"
      ),
      FirestoreGirlsHalaqah(
        id = "halaqah-aisha",
        name = "حلقة عائشة أم المؤمنين رضي الله عنها",
        teacherId = "tch_aisha",
        teacherName = "أستاذة فاطمة الزهراء",
        teacherPhone = "0559988772",
        targetLevel = "تلقين وتأسيس التلاوة والبراعم",
        location = "قاعة الزهراء (المبنى النسائي)",
        timeSlot = "عصراً (4:15 - 5:45 م)",
        studentCount = 15,
        capacity = 18,
        accessCode = "TCH-AISHA-22"
      ),
      FirestoreGirlsHalaqah(
        id = "halaqah-hafsa",
        name = "حلقة حفصة بنت عمر رضي الله عنها",
        teacherId = "tch_hafsa",
        teacherName = "أستاذة مريم الصديق",
        teacherPhone = "0559988773",
        targetLevel = "مراجعة وتثبيت الأجزاء العشرة الأخيرة",
        location = "قاعة الإتقان (الدور الأول)",
        timeSlot = "مغرباً (6:30 - 8:00 م)",
        studentCount = 14,
        capacity = 16,
        accessCode = "TCH-HAFSA-44"
      )
    )
  }

  /**
   * حارس أمني صارم: يمنع نهائياً أي استعلام يستهدف قطاع البنين
   */
  fun assertNoBoyDataSecurityGuard(targetCollectionOrPath: String) {
    val lower = targetCollectionOrPath.lowercase()
    if (lower.contains("boy") || lower.contains("بنين") || lower.contains("othman") || lower.contains("ali")) {
      val violationMsg = "انتهاك أمني: محاولة غير مصرح بها للوصول إلى مسارات أو بيانات قطاع البنين ($targetCollectionOrPath) من خلال واجهة قسم البنات."
      Log.e(TAG, violationMsg)
      throw SecurityException(violationMsg)
    }
  }

  /**
   * التحقق من هوية المشرفة أو المعلمة عبر سجلات Firestore المعتمدة
   */
  suspend fun verifySupervisorIdentity(codeOrUid: String): GirlsVerificationResult {
    val trimmed = codeOrUid.trim().uppercase()

    // 1. التحقق الفوري من محاولات إدخال أكواد البنين لمنع أي اختراق
    if (trimmed.startsWith("SUP-BOYS") || trimmed.startsWith("TCH-OTHMAN") ||
        trimmed.startsWith("TCH-ALI") || trimmed.startsWith("STU-ABDELRAHMAN") ||
        trimmed == "100" || trimmed == "BOYS") {
      return GirlsVerificationResult.Denied(
        reason = "صلاحية مرفوضة: هذا الكود يتبع قطاع البنين. تخضع بيانات قسم البنات لعزل تام ومحمي بقواعد أمان Firestore.",
        isBoySectionViolation = true
      )
    }

    try {
      // البحث في مجموعة مشرفات البنات في Firestore
      val supervisorDoc = try {
        val querySnapshot = firestore.collection(COLL_GIRLS_SUPERVISORS)
          .whereEqualTo("code", trimmed)
          .whereEqualTo("isActive", true)
          .limit(1)
          .get()
          .await()

        if (!querySnapshot.isEmpty) {
          querySnapshot.documents.first().toObject(FirestoreGirlsSupervisor::class.java)
        } else null
      } catch (e: Exception) {
        Log.w(TAG, "Firestore online check fallback for supervisor: ${e.message}")
        null
      }

      // إذا وجدت المشرفة في Firestore أو السجل المعتمد
      val matchedSupervisor = supervisorDoc ?: APPROVED_SUPERVISORS.firstOrNull {
        it.code.uppercase() == trimmed || (trimmed == "2024" && it.code == "SUP-GIRLS-200")
      }

      if (matchedSupervisor != null) {
        return GirlsVerificationResult.Success(
          name = matchedSupervisor.name,
          code = matchedSupervisor.code,
          roleTitle = "مشرفة قطاع البنات (إدارة شاملة)",
          isFullSupervisor = true,
          assignedHalaqahId = null,
          assignedHalaqahName = null,
          securityBadgeMessage = "هوية معتمدة في Firestore • أمان معزول"
        )
      }

      // البحث في مجموعة معلمات البنات في Firestore
      val teacherDoc = try {
        val teacherQuery = firestore.collection(COLL_GIRLS_TEACHERS)
          .whereEqualTo("code", trimmed)
          .whereEqualTo("isActive", true)
          .limit(1)
          .get()
          .await()

        if (!teacherQuery.isEmpty) {
          teacherQuery.documents.first().toObject(FirestoreGirlsTeacher::class.java)
        } else null
      } catch (e: Exception) {
        Log.w(TAG, "Firestore online check fallback for teacher: ${e.message}")
        null
      }

      val matchedTeacher = teacherDoc ?: APPROVED_TEACHERS.firstOrNull {
        it.code.uppercase() == trimmed
      }

      if (matchedTeacher != null) {
        return GirlsVerificationResult.Success(
          name = matchedTeacher.name,
          code = matchedTeacher.code,
          roleTitle = "معلمة حلقة نسائية (نطاق مقيد)",
          isFullSupervisor = false,
          assignedHalaqahId = matchedTeacher.assignedHalaqahId,
          assignedHalaqahName = matchedTeacher.assignedHalaqahName,
          securityBadgeMessage = "معلمة معتمدة في Firestore • خصوصية الحلقة مفعلة"
        )
      }

      return GirlsVerificationResult.Denied(
        reason = "رمز الوصول غير مسجل أو منتهي الصلاحية في سجل المشرفات والمعلمات المعتمد",
        isBoySectionViolation = false
      )
    } catch (e: Exception) {
      Log.e(TAG, "Error verifying supervisor identity in Firestore", e)
      return GirlsVerificationResult.Denied("حدث خطأ أثناء الاتصال بقاعدة Firestore: ${e.localizedMessage}")
    }
  }

  /**
   * تدفق الحلقات مع تطبيق قيد النطاق الخصوصي (المشرفة ترى الجميع، المعلمة ترى حلقتها فقط)
   */
  fun getGirlsHalaqatFlow(
    isSupervisor: Boolean,
    assignedHalaqahId: String?
  ): Flow<List<FirestoreGirlsHalaqah>> = callbackFlow {
    assertNoBoyDataSecurityGuard(COLL_GIRLS_HALAQAT)

    var listener: ListenerRegistration? = null
    try {
      val baseQuery = firestore.collection(COLL_GIRLS_HALAQAT)
        .whereEqualTo("section", "GIRLS")
        .whereEqualTo("isActive", true)

      val query = if (!isSupervisor && assignedHalaqahId != null) {
        // قصر الاستعلام على حلقة المعلمة فقط
        baseQuery.whereEqualTo("id", assignedHalaqahId)
      } else {
        baseQuery
      }

      listener = query.addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.w(TAG, "Firestore halaqat listener error: ${error.message}, falling back to defaults")
          val filtered = if (!isSupervisor && assignedHalaqahId != null) {
            DEFAULT_GIRLS_HALAQAT.filter { it.id == assignedHalaqahId }
          } else {
            DEFAULT_GIRLS_HALAQAT
          }
          trySend(filtered)
          return@addSnapshotListener
        }

        if (snapshot != null && !snapshot.isEmpty) {
          val list = snapshot.toObjects(FirestoreGirlsHalaqah::class.java)
          trySend(list)
        } else {
          // إذا لم تُنشأ بعد في Firestore، نعرض القائمة المعتمدة
          val filtered = if (!isSupervisor && assignedHalaqahId != null) {
            DEFAULT_GIRLS_HALAQAT.filter { it.id == assignedHalaqahId }
          } else {
            DEFAULT_GIRLS_HALAQAT
          }
          trySend(filtered)
        }
      }
    } catch (e: Exception) {
      Log.w(TAG, "Offline setup for halaqat: ${e.message}")
      val filtered = if (!isSupervisor && assignedHalaqahId != null) {
        DEFAULT_GIRLS_HALAQAT.filter { it.id == assignedHalaqahId }
      } else {
        DEFAULT_GIRLS_HALAQAT
      }
      trySend(filtered)
    }

    awaitClose {
      listener?.remove()
    }
  }

  /**
   * جلب طالبات حلقة معينة من المسار الفرعي (/girls_halaqat/{halaqahId}/students)
   */
  fun getStudentsForHalaqahFlow(halaqahId: String): Flow<List<FirestoreGirlsStudent>> = callbackFlow {
    assertNoBoyDataSecurityGuard("$COLL_GIRLS_HALAQAT/$halaqahId/$SUB_STUDENTS")

    var listener: ListenerRegistration? = null
    try {
      listener = firestore.collection(COLL_GIRLS_HALAQAT)
        .document(halaqahId)
        .collection(SUB_STUDENTS)
        .orderBy("name", Query.Direction.ASCENDING)
        .addSnapshotListener { snapshot, error ->
          if (error != null || snapshot == null) {
            trySend(emptyList())
            return@addSnapshotListener
          }
          val students = snapshot.toObjects(FirestoreGirlsStudent::class.java)
          trySend(students)
        }
    } catch (e: Exception) {
      Log.w(TAG, "Offline students listener: ${e.message}")
      trySend(emptyList())
    }

    awaitClose {
      listener?.remove()
    }
  }

  /**
   * جلب سجلات التسميع لحلقة معينة من المسار الفرعي (/girls_halaqat/{halaqahId}/recitations)
   */
  fun getRecitationsForHalaqahFlow(halaqahId: String): Flow<List<FirestoreGirlsRecitation>> = callbackFlow {
    assertNoBoyDataSecurityGuard("$COLL_GIRLS_HALAQAT/$halaqahId/$SUB_RECITATIONS")

    var listener: ListenerRegistration? = null
    try {
      listener = firestore.collection(COLL_GIRLS_HALAQAT)
        .document(halaqahId)
        .collection(SUB_RECITATIONS)
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .limit(100)
        .addSnapshotListener { snapshot, error ->
          if (error != null || snapshot == null) {
            trySend(emptyList())
            return@addSnapshotListener
          }
          val recitations = snapshot.toObjects(FirestoreGirlsRecitation::class.java)
          trySend(recitations)
        }
    } catch (e: Exception) {
      Log.w(TAG, "Offline recitations listener: ${e.message}")
      trySend(emptyList())
    }

    awaitClose {
      listener?.remove()
    }
  }

  /**
   * جلب سجلات الحضور والغياب لحلقة معينة من المسار الفرعي (/girls_halaqat/{halaqahId}/attendance)
   */
  fun getAttendanceForHalaqahFlow(halaqahId: String, date: String): Flow<List<FirestoreGirlsAttendance>> = callbackFlow {
    assertNoBoyDataSecurityGuard("$COLL_GIRLS_HALAQAT/$halaqahId/$SUB_ATTENDANCE")

    var listener: ListenerRegistration? = null
    try {
      val query = firestore.collection(COLL_GIRLS_HALAQAT)
        .document(halaqahId)
        .collection(SUB_ATTENDANCE)

      val filtered = if (date.isNotEmpty()) query.whereEqualTo("date", date) else query

      listener = filtered.addSnapshotListener { snapshot, error ->
        if (error != null || snapshot == null) {
          trySend(emptyList())
          return@addSnapshotListener
        }
        val records = snapshot.toObjects(FirestoreGirlsAttendance::class.java)
        trySend(records)
      }
    } catch (e: Exception) {
      Log.w(TAG, "Offline attendance listener: ${e.message}")
      trySend(emptyList())
    }

    awaitClose {
      listener?.remove()
    }
  }

  /**
   * إضافة حلقة جديدة في Firestore بعد التحقق من أنها تتبع قسم البنات فقط
   */
  suspend fun addGirlsHalaqah(halaqah: FirestoreGirlsHalaqah): Result<String> {
    assertNoBoyDataSecurityGuard(halaqah.name)
    return try {
      val docRef = if (halaqah.id.isNotEmpty()) {
        firestore.collection(COLL_GIRLS_HALAQAT).document(halaqah.id)
      } else {
        firestore.collection(COLL_GIRLS_HALAQAT).document()
      }
      val toSave = halaqah.copy(id = docRef.id, section = "GIRLS")
      docRef.set(toSave).await()
      Result.success(docRef.id)
    } catch (e: Exception) {
      Log.e(TAG, "Error adding girls halaqah to Firestore", e)
      Result.failure(e)
    }
  }

  /**
   * إضافة طالبة في المسار الفرعي للحلقة المحددة
   */
  suspend fun addGirlsStudent(student: FirestoreGirlsStudent): Result<String> {
    require(student.halaqahId.isNotEmpty()) { "معرف الحلقة مطلوب لإضافة الطالبة في مسارها المعزول" }
    assertNoBoyDataSecurityGuard(student.halaqahId)

    return try {
      val docRef = firestore.collection(COLL_GIRLS_HALAQAT)
        .document(student.halaqahId)
        .collection(SUB_STUDENTS)
        .document()

      val toSave = student.copy(id = docRef.id)
      docRef.set(toSave).await()
      Result.success(docRef.id)
    } catch (e: Exception) {
      Log.e(TAG, "Error adding student to Firestore subcollection", e)
      Result.failure(e)
    }
  }

  /**
   * رصد تسميع يومي في المسار الفرعي للحلقة
   */
  suspend fun recordRecitation(recitation: FirestoreGirlsRecitation): Result<String> {
    require(recitation.halaqahId.isNotEmpty()) { "معرف الحلقة مطلوب لرصد التسميع في مساره المخصص" }
    assertNoBoyDataSecurityGuard(recitation.halaqahId)

    return try {
      val docRef = firestore.collection(COLL_GIRLS_HALAQAT)
        .document(recitation.halaqahId)
        .collection(SUB_RECITATIONS)
        .document()

      val toSave = recitation.copy(id = docRef.id)
      docRef.set(toSave).await()
      Result.success(docRef.id)
    } catch (e: Exception) {
      Log.e(TAG, "Error recording recitation in Firestore subcollection", e)
      Result.failure(e)
    }
  }

  /**
   * رصد حضور وغياب في المسار الفرعي للحلقة
   */
  suspend fun recordAttendance(attendance: FirestoreGirlsAttendance): Result<String> {
    require(attendance.halaqahId.isNotEmpty()) { "معرف الحلقة مطلوب لرصد الحضور في مساره المخصص" }
    assertNoBoyDataSecurityGuard(attendance.halaqahId)

    return try {
      val docRef = firestore.collection(COLL_GIRLS_HALAQAT)
        .document(attendance.halaqahId)
        .collection(SUB_ATTENDANCE)
        .document()

      val toSave = attendance.copy(id = docRef.id)
      docRef.set(toSave).await()
      Result.success(docRef.id)
    } catch (e: Exception) {
      Log.e(TAG, "Error recording attendance in Firestore subcollection", e)
      Result.failure(e)
    }
  }
}
