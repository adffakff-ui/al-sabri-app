package com.example.data.repository

import android.util.Log
import com.example.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * مستودع Firestore ثنائي الجمعيات (Multi-Tenant Scoped Repository):
 * - يمنع تداخل البيانات بين جمعية هائل سعيد أنعم وجمعية معاذ الحروي
 * - يوجه كافة استعلامات القراءة والكتابة إلى المسار المخصص لكل جمعية:
 *   associations/{associationId}/students
 *   associations/{associationId}/recitations
 *   associations/{associationId}/attendance
 *   associations/{associationId}/notifications
 *   associations/{associationId}/schedules
 * - يفرض معايير العزل الصارم وحظر تسريب بيانات الجمعيات الأخرى
 */
class AssociationFirestoreRepository(
  private val firestore: FirebaseFirestore = try {
    FirebaseFirestore.getInstance()
  } catch (e: Exception) {
    Log.w(TAG, "FirebaseFirestore instance init fallback: ${e.message}")
    throw e
  }
) {

  companion object {
    private const val TAG = "AssociationFirestoreRepo"
    const val COLL_ASSOCIATIONS = "associations"
    const val SUB_STUDENTS = "students"
    const val SUB_RECITATIONS = "recitations"
    const val SUB_ATTENDANCE = "attendance"
    const val SUB_NOTIFICATIONS = "notifications"
    const val SUB_SCHEDULES = "schedules"
    const val SUB_SUPERVISORS = "supervisors"
  }

  /**
   * الحصول على مسار جذر الجمعية المعزول تماماً في Firestore
   */
  fun getAssociationRootPath(associationId: String): String {
    val sanitized = if (associationId.equals("MUADH_JARWI", ignoreCase = true)) "MUADH_JARWI" else "HAIEL_SAEED"
    return "$COLL_ASSOCIATIONS/$sanitized"
  }

  /**
   * التحقق الموجه من كود المشرف في الجمعية المحددة ومنع الاختراق المتبادل
   */
  suspend fun verifySupervisorCode(inputCode: String, targetAssocId: String): Pair<Boolean, AccessCode?> {
    val clean = inputCode.trim().uppercase()
    val localCheck = AccessCodeRegistry.findCodeForAssociation(clean, targetAssocId)
    if (localCheck.first != null) {
      return Pair(true, localCheck.first)
    }

    return try {
      val doc = firestore.collection(COLL_ASSOCIATIONS)
        .document(targetAssocId)
        .collection(SUB_SUPERVISORS)
        .document(clean)
        .get()
        .await()

      if (doc.exists()) {
        val name = doc.getString("name") ?: "مشرف معتمد"
        val codeObj = AccessCode(
          code = clean,
          role = com.example.viewmodel.UserRole.SUPERVISOR,
          userName = name,
          scopeDescription = "مشرف معتمد عبر سحابة Firestore للجمعية",
          allowedPrivileges = listOf("إدارة الجمعية وعزل البيانات"),
          restrictedPrivileges = listOf("حجب بيانات الجمعية الأخرى"),
          associationId = targetAssocId
        )
        Pair(true, codeObj)
      } else {
        Pair(false, null)
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error checking supervisor code in Firestore: ${e.message}")
      Pair(false, null)
    }
  }

  /**
   * مزامنة وحفظ الطلاب في المسار المعزول للجمعية
   */
  suspend fun saveStudentToAssociation(associationId: String, student: Student): Boolean {
    return try {
      val data = hashMapOf(
        "id" to student.id,
        "name" to student.name,
        "phone" to student.phone,
        "parentName" to student.parentName,
        "parentPhone" to student.parentPhone,
        "groupName" to student.groupName,
        "level" to student.level,
        "currentSurah" to student.currentSurah,
        "currentAyah" to student.currentAyah,
        "memorizedJuzCount" to student.memorizedJuzCount,
        "avatarColorHex" to student.avatarColorHex,
        "notes" to student.notes,
        "isGirlsSection" to student.isGirlsSection,
        "associationId" to associationId
      )

      firestore.collection(COLL_ASSOCIATIONS)
        .document(associationId)
        .collection(SUB_STUDENTS)
        .document(student.id.toString())
        .set(data, SetOptions.merge())
        .await()
      true
    } catch (e: Exception) {
      Log.e(TAG, "Error saving student to association $associationId: ${e.message}")
      false
    }
  }

  /**
   * حفظ تقييم التسميع في المسار المعزول للجمعية
   */
  suspend fun saveRecitationToAssociation(associationId: String, record: RecitationRecord): Boolean {
    return try {
      val data = hashMapOf(
        "id" to record.id,
        "studentId" to record.studentId,
        "studentName" to record.studentName,
        "surahName" to record.surahName,
        "fromAyah" to record.fromAyah,
        "toAyah" to record.toAyah,
        "evaluation" to record.evaluation,
        "scorePercent" to record.scorePercent,
        "mistakesCount" to record.mistakesCount,
        "tajweedScore" to record.tajweedScore,
        "qiraahName" to record.qiraahName,
        "qariReferenceName" to record.qariReferenceName,
        "audioFilePath" to (record.audioFilePath ?: ""),
        "teacherNotes" to record.teacherNotes,
        "date" to record.date,
        "recitationType" to record.recitationType,
        "associationId" to associationId
      )

      firestore.collection(COLL_ASSOCIATIONS)
        .document(associationId)
        .collection(SUB_RECITATIONS)
        .document(record.id.toString())
        .set(data, SetOptions.merge())
        .await()
      true
    } catch (e: Exception) {
      Log.e(TAG, "Error saving recitation to association $associationId: ${e.message}")
      false
    }
  }

  /**
   * الاستماع للطلاب في الجمعية المحددة حصراً (منع تداخل الاستعلامات)
   */
  fun observeAssociationStudents(associationId: String): Flow<List<Student>> = callbackFlow {
    val registration: ListenerRegistration = firestore.collection(COLL_ASSOCIATIONS)
      .document(associationId)
      .collection(SUB_STUDENTS)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.e(TAG, "Error observing students for $associationId: ${error.message}")
          return@addSnapshotListener
        }

        val list = snapshot?.documents?.mapNotNull { doc ->
          try {
            Student(
              id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L,
              name = doc.getString("name") ?: "",
              phone = doc.getString("phone") ?: "",
              parentName = doc.getString("parentName") ?: "",
              parentPhone = doc.getString("parentPhone") ?: "",
              groupName = doc.getString("groupName") ?: "",
              level = doc.getString("level") ?: "متوسط",
              currentSurah = doc.getString("currentSurah") ?: "الفاتحة",
              currentAyah = doc.getLong("currentAyah")?.toInt() ?: 1,
              memorizedJuzCount = doc.getLong("memorizedJuzCount")?.toInt() ?: 1,
              avatarColorHex = doc.getLong("avatarColorHex") ?: 0xFF0D6850,
              notes = doc.getString("notes") ?: "",
              isGirlsSection = doc.getBoolean("isGirlsSection") ?: false,
              associationId = associationId
            )
          } catch (e: Exception) {
            null
          }
        } ?: emptyList()

        trySend(list)
      }

    awaitClose { registration.remove() }
  }
}
