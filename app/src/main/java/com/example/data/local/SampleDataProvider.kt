package com.example.data.local

import com.example.data.model.AttendanceRecord
import com.example.data.model.ParentNotification
import com.example.data.model.RecitationRecord
import com.example.data.model.RecitationSchedule
import com.example.data.model.Student
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SampleDataProvider {
  private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
  val todayDate: String get() = dateFormat.format(Date())

  val sampleStudents = listOf(
    // ================= طلاب منظومة حلقات هائل سعيد أنعم (QRN) =================
    Student(
      id = 1010,
      name = "عمر أحمد الشريف",
      phone = "0501234567",
      parentName = "أحمد الشريف",
      parentPhone = "0551122334",
      groupName = "حلقة الإمام الشاطبي",
      level = "خاتم",
      currentSurah = "الملك",
      currentAyah = 15,
      memorizedJuzCount = 29,
      avatarColorHex = 0xFF0D6850,
      notes = "كود: QRN-101 • إتقان تام للمدود ومخارج الحروف • 380 نقطة",
      isGirlsSection = false
    ),
    Student(
      id = 1011,
      name = "فاطمة محمد السعيد",
      phone = "0559876543",
      parentName = "محمد السعيد",
      parentPhone = "0559876544",
      groupName = "حلقة أم المؤمنين خديجة",
      level = "متقدم",
      currentSurah = "المجادلة",
      currentAyah = 10,
      memorizedJuzCount = 28,
      avatarColorHex = 0xFF9333EA,
      notes = "كود: QRN-102 • تلاوة خاشعة وأداء متميز • 420 نقطة",
      isGirlsSection = true
    ),
    Student(
      id = 1012,
      name = "يوسف خالد العتيبي",
      phone = "0543219876",
      parentName = "خالد العتيبي",
      parentPhone = "0543219877",
      groupName = "حلقة الإمام نافع",
      level = "مبتدئ",
      currentSurah = "النبأ",
      currentAyah = 30,
      memorizedJuzCount = 30,
      avatarColorHex = 0xFF195E63,
      notes = "كود: QRN-103 • حفظ جزء عم وتبارك • 210 نقطة",
      isGirlsSection = false
    ),
    Student(
      id = 1013,
      name = "مريم عبد الله الغامدي",
      phone = "0561122334",
      parentName = "عبد الله الغامدي",
      parentPhone = "0561122335",
      groupName = "حلقة الزهراء للفتيات",
      level = "متقدم",
      currentSurah = "البقرة",
      currentAyah = 286,
      memorizedJuzCount = 25,
      avatarColorHex = 0xFFC99700,
      notes = "كود: QRN-104 • ختم القرآن الكريم كاملاً • 650 نقطة",
      isGirlsSection = true
    ),

    // ================= طلاب قسم البنين =================
    Student(
      id = 1,
      name = "عبد الرحمن أحمد العتيبي",
      phone = "0501234567",
      parentName = "أحمد العتيبي",
      parentPhone = "0551234567",
      groupName = "حلقة عثمان بن عفان رضي الله عنه",
      level = "متقدم",
      currentSurah = "البقرة",
      currentAyah = 142,
      memorizedJuzCount = 7,
      avatarColorHex = 0xFF0D6850,
      notes = "طالب متميز في التجويد ومخارج الحروف",
      isGirlsSection = false
    ),
    Student(
      id = 2,
      name = "محمد عبد الله الغامدي",
      phone = "0502345678",
      parentName = "عبد الله الغامدي",
      parentPhone = "0552345678",
      groupName = "حلقة عثمان بن عفان رضي الله عنه",
      level = "متوسط",
      currentSurah = "النساء",
      currentAyah = 58,
      memorizedJuzCount = 4,
      avatarColorHex = 0xFF195E63,
      notes = "يحتاج تركيز على أحكام النون الساكنة",
      isGirlsSection = false
    ),
    Student(
      id = 3,
      name = "يوسف إبراهيم القرني",
      phone = "0503456789",
      parentName = "إبراهيم القرني",
      parentPhone = "0553456789",
      groupName = "حلقة علي بن أبي طالب رضي الله عنه",
      level = "خاتم",
      currentSurah = "آل عمران",
      currentAyah = 100,
      memorizedJuzCount = 30,
      avatarColorHex = 0xFFC99700,
      notes = "يراجع للختمة الثانية بإتقان وسند",
      isGirlsSection = false
    ),
    Student(
      id = 4,
      name = "عمر خالد الدوسري",
      phone = "0504567890",
      parentName = "خالد الدوسري",
      parentPhone = "0554567890",
      groupName = "حلقة علي بن أبي طالب رضي الله عنه",
      level = "مبتدئ",
      currentSurah = "النبأ",
      currentAyah = 25,
      memorizedJuzCount = 1,
      avatarColorHex = 0xFF2563EB,
      notes = "حفظ جديد لجزء عم بهمة عالية",
      isGirlsSection = false
    ),
    Student(
      id = 5,
      name = "أنس سعد القحطاني",
      phone = "0505678901",
      parentName = "سعد القحطاني",
      parentPhone = "0555678901",
      groupName = "حلقة أبي بكر الصديق رضي الله عنه",
      level = "متوسط",
      currentSurah = "الكهف",
      currentAyah = 60,
      memorizedJuzCount = 5,
      avatarColorHex = 0xFF7C3AED,
      notes = "صوت ندي وتطبيق ممتاز للترتيل",
      isGirlsSection = false
    ),
    Student(
      id = 6,
      name = "بلال طارق الشمري",
      phone = "0506789012",
      parentName = "طارق الشمري",
      parentPhone = "0556789012",
      groupName = "حلقة أبي بكر الصديق رضي الله عنه",
      level = "متقدم",
      currentSurah = "يس",
      currentAyah = 40,
      memorizedJuzCount = 12,
      avatarColorHex = 0xFFD97706,
      notes = "مواظب ومنتظم في الحضور اليومي",
      isGirlsSection = false
    ),

    // ================= طالبات قسم البنات =================
    Student(
      id = 101,
      name = "مريم عبد الله العتيبي",
      phone = "0507788991",
      parentName = "عبد الله العتيبي",
      parentPhone = "0557788991",
      groupName = "حلقة خديجة بنت خويلد رضي الله عنها",
      level = "متقدم",
      currentSurah = "البقرة",
      currentAyah = 200,
      memorizedJuzCount = 8,
      avatarColorHex = 0xFFBE185D,
      notes = "إتقان تام لأحكام المد المتصل والمنفصل",
      isGirlsSection = true
    ),
    Student(
      id = 102,
      name = "فاطمة أحمد الغامدي",
      phone = "0508899002",
      parentName = "أحمد الغامدي",
      parentPhone = "0558899002",
      groupName = "حلقة خديجة بنت خويلد رضي الله عنها",
      level = "متوسط",
      currentSurah = "آل عمران",
      currentAyah = 75,
      memorizedJuzCount = 5,
      avatarColorHex = 0xFF9D174D,
      notes = "مواظبة وحريصة على المراجعة الصغرى اليومية",
      isGirlsSection = true
    ),
    Student(
      id = 103,
      name = "عائشة إبراهيم القرني",
      phone = "0509900113",
      parentName = "إبراهيم القرني",
      parentPhone = "0559900113",
      groupName = "حلقة عائشة أم المؤمنين رضي الله عنها",
      level = "خاتمة",
      currentSurah = "النساء",
      currentAyah = 176,
      memorizedJuzCount = 30,
      avatarColorHex = 0xFFE11D48,
      notes = "تستعد لاختبار الإجازة بالسند المتصل",
      isGirlsSection = true
    ),
    Student(
      id = 104,
      name = "نور خالد الدوسري",
      phone = "0500011224",
      parentName = "خالد الدوسري",
      parentPhone = "0550011224",
      groupName = "حلقة عائشة أم المؤمنين رضي الله عنها",
      level = "مبتدئة",
      currentSurah = "تبارك",
      currentAyah = 15,
      memorizedJuzCount = 2,
      avatarColorHex = 0xFFA21CAF,
      notes = "صوت ندي في ترتيل قصار السور",
      isGirlsSection = true
    ),
    Student(
      id = 105,
      name = "سارة محمد القحطاني",
      phone = "0501122335",
      parentName = "محمد القحطاني",
      parentPhone = "0551122335",
      groupName = "حلقة فاطمة الزهراء رضي الله عنها",
      level = "متوسط",
      currentSurah = "مريم",
      currentAyah = 30,
      memorizedJuzCount = 6,
      avatarColorHex = 0xFFDB2777,
      notes = "تحسن كبير في أحكام القلقلة",
      isGirlsSection = true
    ),
    Student(
      id = 106,
      name = "زينب بلال الشمري",
      phone = "0502233446",
      parentName = "بلال الشمري",
      parentPhone = "0552233446",
      groupName = "حلقة مريم بنت عمران عليها السلام",
      level = "متقدم",
      currentSurah = "الإسراء",
      currentAyah = 50,
      memorizedJuzCount = 10,
      avatarColorHex = 0xFF831843,
      notes = "همة عالية ومراجعة مستمرة بدون أخطاء",
      isGirlsSection = true
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
    ),
    // حضور قسم البنات
    AttendanceRecord(
      id = 101,
      studentId = 101,
      studentName = "مريم عبد الله العتيبي",
      date = todayDate,
      status = "حاضرة",
      notes = "حضور مبكر ونشاط ممتاز"
    ),
    AttendanceRecord(
      id = 102,
      studentId = 102,
      studentName = "فاطمة أحمد الغامدي",
      date = todayDate,
      status = "حاضرة",
      notes = ""
    ),
    AttendanceRecord(
      id = 103,
      studentId = 103,
      studentName = "عائشة إبراهيم القرني",
      date = todayDate,
      status = "حاضرة",
      notes = ""
    ),
    AttendanceRecord(
      id = 104,
      studentId = 104,
      studentName = "نور خالد الدوسري",
      date = todayDate,
      status = "غائبة",
      notes = "عذر مرضي بعلم المعلمة"
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
    ),
    // تسميعات قسم البنات
    RecitationRecord(
      id = 101,
      studentId = 101,
      studentName = "مريم عبد الله العتيبي",
      date = todayDate,
      recitationType = "حفظ جديد",
      surahName = "البقرة",
      fromAyah = 196,
      toAyah = 205,
      evaluation = "ممتاز",
      mistakesCount = 0,
      tajweedScore = 10,
      teacherNotes = "تبارك الرحمن، إتقان لأحكام التجويد والوقف والابتداء"
    ),
    RecitationRecord(
      id = 102,
      studentId = 102,
      studentName = "فاطمة أحمد الغامدي",
      date = todayDate,
      recitationType = "مراجعة صغرى",
      surahName = "آل عمران",
      fromAyah = 60,
      toAyah = 75,
      evaluation = "جيد جداً",
      mistakesCount = 1,
      tajweedScore = 9,
      teacherNotes = "مراجعة قوية مع مراعاة أزمنة الحروف الرخوة"
    ),
    RecitationRecord(
      id = 103,
      studentId = 103,
      studentName = "عائشة إبراهيم القرني",
      date = todayDate,
      recitationType = "مراجعة كبرى",
      surahName = "النساء",
      fromAyah = 150,
      toAyah = 176,
      evaluation = "ممتاز",
      mistakesCount = 0,
      tajweedScore = 10,
      teacherNotes = "إتقان فائق وختام السورة بالسند المعتمد"
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
    ),
    ParentNotification(
      id = 101,
      studentId = 101,
      studentName = "مريم عبد الله العتيبي",
      parentPhone = "0557788991",
      title = "تقرير التسميع اليومي (قسم البنات)",
      message = "السلام عليكم ورحمة الله، أتمت ابنتكم مريم اليوم تسميع سورة البقرة (الآيات 196-205) بدرجة كاملة وتجويد متقن، جزاكم الله خيراً.",
      notificationType = "تقرير يومي",
      date = todayDate,
      isSent = true
    ),
    ParentNotification(
      id = 104,
      studentId = 104,
      studentName = "نور خالد الدوسري",
      parentPhone = "0550011224",
      title = "تنبيه غياب عبر FCM",
      message = "نحيطكم علماً بتسجيل غياب ابنتكم نور عن حلقة عائشة أم المؤمنين لهذا اليوم، نرجو الاطمئنان على صحتها.",
      notificationType = "تنبيه غياب",
      date = todayDate,
      isSent = true
    )
  )

  val sampleSchedules = listOf(
    RecitationSchedule(
      id = 1,
      studentId = 1,
      studentName = "عبد الرحمن أحمد العتيبي",
      parentPhone = "0551234567",
      groupName = "حلقة عثمان بن عفان رضي الله عنه",
      scheduledDate = todayDate,
      scheduledTime = "04:45 م",
      surahName = "البقرة",
      fromAyah = 142,
      toAyah = 160,
      recitationType = "حفظ جديد",
      teacherName = "الشيخ عبد الله بن فهد",
      teacherNotes = "التركيز على ضبط أحكام القبلة ومخارج الحروف",
      status = "مجدول",
      fcmNotificationSent = true,
      fcmMessageId = "fcm_msg_1001"
    ),
    RecitationSchedule(
      id = 2,
      studentId = 2,
      studentName = "محمد عبد الله الغامدي",
      parentPhone = "0552345678",
      groupName = "حلقة عثمان بن عفان رضي الله عنه",
      scheduledDate = todayDate,
      scheduledTime = "05:15 م",
      surahName = "النساء",
      fromAyah = 58,
      toAyah = 70,
      recitationType = "مراجعة صغرى",
      teacherName = "الشيخ عبد الله بن فهد",
      teacherNotes = "مراجعة متأنية مع مراعاة أزمنة الغنن",
      status = "مجدول",
      fcmNotificationSent = true,
      fcmMessageId = "fcm_msg_1002"
    ),
    RecitationSchedule(
      id = 3,
      studentId = 3,
      studentName = "يوسف إبراهيم القرني",
      parentPhone = "0553456789",
      groupName = "حلقة علي بن أبي طالب رضي الله عنه",
      scheduledDate = todayDate,
      scheduledTime = "06:30 م",
      surahName = "آل عمران",
      fromAyah = 100,
      toAyah = 120,
      recitationType = "اختبار مرحلي",
      teacherName = "الشيخ د. محمد الشنقيطي",
      teacherNotes = "اختبار تثبيت الربع الرابع",
      status = "مجدول",
      fcmNotificationSent = true,
      fcmMessageId = "fcm_msg_1003"
    ),
    // مواعيد تسميع قسم البنات
    RecitationSchedule(
      id = 101,
      studentId = 101,
      studentName = "مريم عبد الله العتيبي",
      parentPhone = "0557788991",
      groupName = "حلقة خديجة بنت خويلد رضي الله عنها",
      scheduledDate = todayDate,
      scheduledTime = "04:30 م",
      surahName = "البقرة",
      fromAyah = 206,
      toAyah = 220,
      recitationType = "حفظ جديد",
      teacherName = "الشيخة أم عبد الله الأنصاري",
      teacherNotes = "متابعة أوجه القراءات وضبط التلاوة",
      status = "مجدول",
      fcmNotificationSent = true,
      fcmMessageId = "fcm_msg_2001"
    ),
    RecitationSchedule(
      id = 102,
      studentId = 102,
      studentName = "فاطمة أحمد الغامدي",
      parentPhone = "0558899002",
      groupName = "حلقة خديجة بنت خويلد رضي الله عنها",
      scheduledDate = todayDate,
      scheduledTime = "05:00 م",
      surahName = "آل عمران",
      fromAyah = 76,
      toAyah = 90,
      recitationType = "مراجعة صغرى",
      teacherName = "الشيخة أم عبد الله الأنصاري",
      teacherNotes = "مراجعة سريعة مع التحضير المسبق",
      status = "مجدول",
      fcmNotificationSent = true,
      fcmMessageId = "fcm_msg_2002"
    )
  )
}
