package com.example.data.model

data class QuranHalaqah(
  val id: Long,
  val name: String,
  val teacherName: String,
  val teacherTitle: String = "محفظ معتمد ومجاز بالقراءات",
  val teacherPhone: String = "0501234567",
  val location: String,
  val timeSlot: String,
  val studentCount: Int,
  val capacity: Int,
  val targetLevel: String,
  val days: String,
  val currentCurriculum: String,
  val colorHex: Long = 0xFF0D6850
) {
  val mosqueLocation: String get() = location
  val meetingTime: String get() = timeSlot
}

typealias QuranHalaqatData = MockHalaqatData

object MockHalaqatData {
  val halaqatList = listOf(
    QuranHalaqah(
      id = 1,
      name = "حلقة عثمان بن عفان رضي الله عنه",
      teacherName = "الشيخ عبد الله بن فهد السالم",
      teacherTitle = "مجاز بحفص وشعبة، خبرة 15 عاماً",
      teacherPhone = "0551122334",
      location = "مجمع هائل سعيد أنعم القرآني - القاعة الكبرى",
      timeSlot = "بعد صلاة العصر (4:30 م - 6:00 م)",
      studentCount = 18,
      capacity = 20,
      targetLevel = "حفظ متقدم (مراجعة وتثبيت)",
      days = "الأحد إلى الخميس",
      currentCurriculum = "من سورة البقرة إلى سورة الأنعام",
      colorHex = 0xFF0D6850
    ),
    QuranHalaqah(
      id = 2,
      name = "حلقة علي بن أبي طالب رضي الله عنه",
      teacherName = "الشيخ د. محمد عبد الرحمن الشنقيطي",
      teacherTitle = "أستاذ القراءات والتجويد بجامعة أم القرى",
      teacherPhone = "0552233445",
      location = "مجمع هائل سعيد أنعم القرآني - قاعة ابن الجزري",
      timeSlot = "بعد صلاة المغرب (6:30 م - 8:00 م)",
      studentCount = 22,
      capacity = 25,
      targetLevel = "الختمة الثانية والإجازة بالسند",
      days = "السبت والإثنين والأربعاء",
      currentCurriculum = "مراجعة المصحف كاملاً مع التجويد",
      colorHex = 0xFF195E63
    ),
    QuranHalaqah(
      id = 3,
      name = "حلقة أبي بكر الصديق رضي الله عنه",
      teacherName = "الشيخ أحمد بن إبراهيم المنشاوي",
      teacherTitle = "معلم مجاز في التلقين والترتيل",
      teacherPhone = "0553344556",
      location = "مجمع هائل سعيد أنعم القرآني - رواق الترتيل",
      timeSlot = "بعد صلاة الفجر (5:30 ص - 6:45 ص)",
      studentCount = 15,
      capacity = 15,
      targetLevel = "حفظ جديد للمرحلة المتوسطة",
      days = "الأحد إلى الخميس",
      currentCurriculum = "جزء تبارك وجزء قد سمع",
      colorHex = 0xFFC99700
    ),
    QuranHalaqah(
      id = 4,
      name = "حلقة عمر بن الخطاب رضي الله عنه",
      teacherName = "الشيخ يوسف بن خالد القاسمي",
      teacherTitle = "محفظ وخطيب جامع التقوى",
      teacherPhone = "0554455667",
      location = "مجمع هائل سعيد أنعم القرآني - مصلى الشباب",
      timeSlot = "بين المغرب والعشاء (6:40 م - 8:15 م)",
      studentCount = 14,
      capacity = 18,
      targetLevel = "تلقين وتصحيح التلاوة للمبتدئين",
      days = "الأحد والثلاثاء والخميس",
      currentCurriculum = "جزء عم مع أحكام التجويد الأساسية",
      colorHex = 0xFF2563EB
    ),
    QuranHalaqah(
      id = 5,
      name = "حلقة زيد بن ثابت رضي الله عنه",
      teacherName = "الشيخ إبراهيم بن سعد الغامدي",
      teacherTitle = "مشرف حلقات تحفيظ القرآن الكريم",
      teacherPhone = "0555566778",
      location = "مجمع هائل سعيد أنعم القرآني - قاعة الإتقان",
      timeSlot = "بعد صلاة العصر (4:45 م - 6:15 م)",
      studentCount = 19,
      capacity = 20,
      targetLevel = "حفظ مكثف وتثبيت الحفاظ",
      days = "الأحد إلى الخميس",
      currentCurriculum = "من سورة الإسراء إلى سورة يس",
      colorHex = 0xFF7C3AED
    ),
    QuranHalaqah(
      id = 6,
      name = "حلقة عبد الله بن مسعود رضي الله عنه",
      teacherName = "الشيخ بلال بن طارق الحذيفي",
      teacherTitle = "إمام مسجد ومجاز في متن الجزرية",
      teacherPhone = "0556677889",
      location = "مجمع هائل سعيد أنعم القرآني - مركز الإسناد",
      timeSlot = "بعد صلاة العشاء (8:30 م - 9:45 م)",
      studentCount = 12,
      capacity = 16,
      targetLevel = "دراسة التجويد النظري والتطبيق العملي",
      days = "السبت والإثنين والأربعاء",
      currentCurriculum = "شرح تحفة الأطفال وتطبيق السور الطوال",
      colorHex = 0xFFD97706
    )
  )
}
