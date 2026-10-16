package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recitations")
data class RecitationRecord(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val studentId: Long,
  val studentName: String,
  val date: String,
  val recitationType: String = "حفظ جديد", // حفظ جديد, مراجعة صغرى, مراجعة كبرى, تلاوة وتجويد
  val surahName: String,
  val fromAyah: Int,
  val toAyah: Int,
  val evaluation: String = "ممتاز", // ممتاز, جيد جداً, جيد, مقبول, يحتاج إعادة
  val scorePercent: Int = 95,
  val mistakesCount: Int = 0,
  val tajweedScore: Int = 10, // out of 10
  val qiraahName: String = "حفص عن عاصم", // القراءة من القراءات العشر
  val qariReferenceName: String = "الشيخ محمود خليل الحصري",
  val audioFilePath: String? = null, // مسار التسجيل الصوتي للطالب
  val teacherNotes: String = "",
  val associationId: String = "HAIEL_SAEED",
  val timestamp: Long = System.currentTimeMillis()
)
