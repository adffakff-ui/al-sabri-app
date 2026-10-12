package com.example.data.local

import com.example.data.model.AttendanceRecord
import com.example.data.model.ParentNotification
import com.example.data.model.RecitationRecord
import com.example.data.model.Student
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SampleDataProvider {
  private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
  val todayDate: String get() = dateFormat.format(Date())

  val sampleStudents = listOf(
    Student(
      id = 1,
      name = "عبد الرحمن أحمد العتيبي",
      phone = "0501234567",
      parentName = "أحمد العتيبي",
      parentPhone = "0551234567",
      groupName = "حلقة عثمان بن عفان",
      level = "متقدم",
      currentSurah = "البقرة",
      currentAyah = 142,
      memorizedJuzCount = 7,
      avatarColorHex = 0xFF0D6850,
      notes = "طالب متميز في التجويد ومخارج الحروف"
    ),
    Student(
      id = 2,
      name = "محمد عبد الله الغامدي",
      phone = "0502345678",
      parentName = "عبد الله الغامدي",
      parentPhone = "0552345678",
      groupName = "حلقة عثمان بن عفان",
      level = "متوسط",
      currentSurah = "النساء",
      currentAyah = 58,
      memorizedJuzCount = 4,
      avatarColorHex = 0xFF195E63,
      notes = "يحتاج تركيز على أحكام النون الساكنة"
    ),
    Student(
      id = 3,
      name = "يوسف إبراهيم القرني",
      phone = "0503456789",
      parentName = "إبراهيم القرني",
      parentPhone = "0553456789",
      groupName = "حلقة علي بن أبي طالب",
      level = "خاتم",
      currentSurah = "آل عمران",
      currentAyah = 100,
      memorizedJuzCount = 30,
      avatarColorHex = 0xFFC99700,
      notes = "يراجع للختمة الثانية بإتقان وسند"
    ),
    Student(
      id = 4,
      name = "عمر خالد الدوسري",
      phone = "0504567890",
      parentName = "خالد الدوسري",
      parentPhone = "0554567890",
      groupName = "حلقة علي بن أبي طالب",
      level = "مبتدئ",
      currentSurah = "النبأ",
      currentAyah = 25,
      memorizedJuzCount = 1,
      avatarColorHex = 0xFF2563EB,
      notes = "حفظ جديد لجزء عم بهمة عالية"
    ),
    Student(
      id = 5,
      name = "أنس سعد القحطاني",
      phone = "0505678901",
      parentName = "سعد القحطاني",
      parentPhone = "0555678901",
      groupName = "حلقة أبي بكر الصديق",
      level = "متوسط",
      currentSurah = "الكهف",
      currentAyah = 60,
      memorizedJuzCount = 5,
      avatarColorHex = 0xFF7C3AED,
      notes = "صوت ندي وتطبيق ممتاز للترتيل"
    ),
    Student(
      id = 6,
      name = "بلال طارق الشمري",
      phone = "0506789012",
      parentName = "طارق الشمري",
      parentPhone = "0556789012",
      groupName = "حلقة أبي بكر الصديق",
      level = "متقدم",
      currentSurah = "يس",
      currentAyah = 40,
      memorizedJuzCount = 12,
      avatarColorHex = 0xFFD97706,
      notes = "مواظب ومنتظم في الحضور اليومي"
    )
  )

  val sampleAttendance = listOf(
    AttendanceRecord(
      id = 1,
      studentId = 1,
      studentName = "عبد الرحمن أحمد العتيبي",
      date = todayDate,
      status = "حاضر",
      notes = "حضور مبكر"
    ),
    AttendanceRecord(
      id = 2,
      studentId = 2,
      studentName = "محمد عبد الله الغامدي",
      date = todayDate,
      status = "حاضر",
      notes = ""
    ),
    AttendanceRecord(
      id = 3,
      studentId = 3,
      studentName = "يوسف إبراهيم القرني",
      date = todayDate,
      status = "حاضر",
      notes = ""
    ),
    AttendanceRecord(
      id = 4,
      studentId = 4,
      studentName = "عمر خالد الدوسري",
      date = todayDate,
      status = "متأخر",
      notes = "تأخر 15 دقيقة بعذر"
    ),
    AttendanceRecord(
      id = 5,
      studentId = 5,
      studentName = "أنس سعد القحطاني",
      date = todayDate,
      status = "حاضر",
      notes = ""
    ),
    AttendanceRecord(
      id = 6,
      studentId = 6,
      studentName = "بلال طارق الشمري",
      date = todayDate,
      status = "مستأذن",
      notes = "ظرف عائلي"
    )
  )

  val sampleRecitations = listOf(
    RecitationRecord(
      id = 1,
      studentId = 1,
      studentName = "عبد الرحمن أحمد العتيبي",
      date = todayDate,
      recitationType = "حفظ جديد",
      surahName = "البقرة",
      fromAyah = 142,
      toAyah = 152,
      evaluation = "ممتاز",
      mistakesCount = 0,
      tajweedScore = 10,
      teacherNotes = "ما شاء الله، إتقان تام وتلاوة متقنة بخشوع"
    ),
    RecitationRecord(
      id = 2,
      studentId = 2,
      studentName = "محمد عبد الله الغامدي",
      date = todayDate,
      recitationType = "مراجعة صغرى",
      surahName = "النساء",
      fromAyah = 50,
      toAyah = 70,
      evaluation = "جيد جداً",
      mistakesCount = 2,
      tajweedScore = 8,
      teacherNotes = "تنبيه على غنة الإخفاء وترقيق الراء المكسورة"
    ),
    RecitationRecord(
      id = 3,
      studentId = 3,
      studentName = "يوسف إبراهيم القرني",
      date = todayDate,
      recitationType = "مراجعة كبرى",
      surahName = "آل عمران",
      fromAyah = 1,
      toAyah = 50,
      evaluation = "ممتاز",
      mistakesCount = 0,
      tajweedScore = 10,
      teacherNotes = "سرد متقن بدون تردد، تبارك الله"
    ),
    RecitationRecord(
      id = 4,
      studentId = 4,
      studentName = "عمر خالد الدوسري",
      date = todayDate,
      recitationType = "حفظ جديد",
      surahName = "النبأ",
      fromAyah = 1,
      toAyah = 20,
      evaluation = "جيد",
      mistakesCount = 3,
      tajweedScore = 7,
      teacherNotes = "جهد طيب، يحتاج تكرار التسميع في البيت"
    ),
    RecitationRecord(
      id = 5,
      studentId = 5,
      studentName = "أنس سعد القحطاني",
      date = todayDate,
      recitationType = "تلاوة وتجويد",
      surahName = "الكهف",
      fromAyah = 50,
      toAyah = 74,
      evaluation = "ممتاز",
      mistakesCount = 1,
      tajweedScore = 9,
      teacherNotes = "تطبيق رائع لأحكام المدود"
    )
  )

  val sampleNotifications = listOf(
    ParentNotification(
      id = 1,
      studentId = 1,
      studentName = "عبد الرحمن أحمد العتيبي",
      parentPhone = "0551234567",
      title = "تقرير التسميع اليومي",
      message = "السلام عليكم ورحمة الله، أتم ابنكم عبد الرحمن اليوم تسميع سورة البقرة من الآية 142 إلى 152 بتقدير (ممتاز). نشكركم على حسن المتابعة.",
      notificationType = "تقرير يومي",
      date = todayDate,
      isSent = true
    ),
    ParentNotification(
      id = 2,
      studentId = 3,
      studentName = "يوسف إبراهيم القرني",
      parentPhone = "0553456789",
      title = "إنجاز وتفوق قرآني",
      message = "نهنئكم بإتقان ابنكم يوسف لمراجعة الجزء الثالث كاملاً دون أي خطأ، نسأل الله أن يجعله من أهل القرآن وخاصته.",
      notificationType = "إنجاز وتفوق",
      date = todayDate,
      isSent = true
    )
  )
}
