package com.example.data.model

enum class HalaqahSection(val title: String, val shortLabel: String, val badgeColorHex: Long) {
  BOYS("قسم البنين", "بنين 👦", 0xFF0D6850),
  GIRLS("قسم البنات", "بنات 👧", 0xFFBE185D),
  ALL("كافة الأقسام", "عام 🌐", 0xFFC99700)
}

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
  val colorHex: Long = 0xFF0D6850,
  val section: HalaqahSection = HalaqahSection.BOYS,
  val teacherSanad: String = teacherTitle,
  val recitationDays: String = days
) {
  val mosqueLocation: String get() = location
  val meetingTime: String get() = timeSlot
  val isGirlsSection: Boolean get() = section == HalaqahSection.GIRLS
}

typealias QuranHalaqatData = MockHalaqatData

object MockHalaqatData {
  val halaqatList = listOf(
    // ================= حلقات قسم البنين =================
    QuranHalaqah(
      id = 1,
      name = "حلقة عثمان بن عفان رضي الله عنه",
      teacherName = "الشيخ عبد الله بن فهد السالم",
      teacherTitle = "مجاز بحفص وشعبة، خبرة 15 عاماً",
      teacherPhone = "0551122334",
      location = "مجمع هائل سعيد أنعم القرآني - القاعة الكبرى (بنين)",
      timeSlot = "بعد صلاة العصر (4:30 م - 6:00 م)",
      studentCount = 18,
      capacity = 20,
      targetLevel = "حفظ متقدم (مراجعة وتثبيت)",
      days = "الأحد إلى الخميس",
      currentCurriculum = "من سورة البقرة إلى سورة الأنعام",
      colorHex = 0xFF0D6850,
      section = HalaqahSection.BOYS
    ),
    QuranHalaqah(
      id = 2,
      name = "حلقة علي بن أبي طالب رضي الله عنه",
      teacherName = "الشيخ د. محمد عبد الرحمن الشنقيطي",
      teacherTitle = "أستاذ القراءات والتجويد بجامعة أم القرى",
      teacherPhone = "0552233445",
      location = "مجمع هائل سعيد أنعم القرآني - قاعة ابن الجزري (بنين)",
      timeSlot = "بعد صلاة المغرب (6:30 م - 8:00 م)",
      studentCount = 22,
      capacity = 25,
      targetLevel = "الختمة الثانية والإجازة بالسند",
      days = "السبت والإثنين والأربعاء",
      currentCurriculum = "مراجعة المصحف كاملاً مع التجويد",
      colorHex = 0xFF195E63,
      section = HalaqahSection.BOYS
    ),
    QuranHalaqah(
      id = 3,
      name = "حلقة أبي بكر الصديق رضي الله عنه",
      teacherName = "الشيخ أحمد بن إبراهيم المنشاوي",
      teacherTitle = "معلم مجاز في التلقين والترتيل",
      teacherPhone = "0553344556",
      location = "مجمع هائل سعيد أنعم القرآني - رواق الترتيل (بنين)",
      timeSlot = "بعد صلاة الفجر (5:30 ص - 6:45 ص)",
      studentCount = 15,
      capacity = 15,
      targetLevel = "حفظ جديد للمرحلة المتوسطة",
      days = "الأحد إلى الخميس",
      currentCurriculum = "جزء تبارك وجزء قد سمع",
      colorHex = 0xFFC99700,
      section = HalaqahSection.BOYS
    ),
    QuranHalaqah(
      id = 4,
      name = "حلقة عمر بن الخطاب رضي الله عنه",
      teacherName = "الشيخ يوسف بن خالد القاسمي",
      teacherTitle = "محفظ وخطيب جامع التقوى",
      teacherPhone = "0554455667",
      location = "مجمع هائل سعيد أنعم القرآني - مصلى الشباب (بنين)",
      timeSlot = "بين المغرب والعشاء (6:40 م - 8:15 م)",
      studentCount = 14,
      capacity = 18,
      targetLevel = "تلقين وتصحيح التلاوة للمبتدئين",
      days = "الأحد والثلاثاء والخميس",
      currentCurriculum = "جزء عم مع أحكام التجويد الأساسية",
      colorHex = 0xFF2563EB,
      section = HalaqahSection.BOYS
    ),
    QuranHalaqah(
      id = 5,
      name = "حلقة زيد بن ثابت رضي الله عنه",
      teacherName = "الشيخ إبراهيم بن سعد الغامدي",
      teacherTitle = "مشرف حلقات تحفيظ القرآن الكريم",
      teacherPhone = "0555566778",
      location = "مجمع هائل سعيد أنعم القرآني - قاعة الإتقان (بنين)",
      timeSlot = "بعد صلاة العصر (4:45 م - 6:15 م)",
      studentCount = 19,
      capacity = 20,
      targetLevel = "حفظ مكثف وتثبيت الحفاظ",
      days = "الأحد إلى الخميس",
      currentCurriculum = "من سورة الإسراء إلى سورة يس",
      colorHex = 0xFF7C3AED,
      section = HalaqahSection.BOYS
    ),
    QuranHalaqah(
      id = 6,
      name = "حلقة عبد الله بن مسعود رضي الله عنه",
      teacherName = "الشيخ بلال بن طارق الحذيفي",
      teacherTitle = "إمام مسجد ومجاز في متن الجزرية",
      teacherPhone = "0556677889",
      location = "مجمع هائل سعيد أنعم القرآني - مركز الإسناد (بنين)",
      timeSlot = "بعد صلاة العشاء (8:30 م - 9:45 م)",
      studentCount = 12,
      capacity = 16,
      targetLevel = "دراسة التجويد النظري والتطبيق العملي",
      days = "السبت والإثنين والأربعاء",
      currentCurriculum = "شرح تحفة الأطفال وتطبيق السور الطوال",
      colorHex = 0xFFD97706,
      section = HalaqahSection.BOYS
    ),

    // ================= حلقات قسم البنات =================
    QuranHalaqah(
      id = 101,
      name = "حلقة خديجة بنت خويلد رضي الله عنها",
      teacherName = "الشيخة أم عبد الله الأنصاري",
      teacherTitle = "مجازة بالقراءات العشر الصغرى والكبرى",
      teacherPhone = "0557788990",
      location = "مجمع هائل سعيد أنعم - جناح مريم النسائي (قاعة الصديقة)",
      timeSlot = "العصر (4:00 م - 5:45 م)",
      studentCount = 16,
      capacity = 18,
      targetLevel = "حفظ متقدم وإتقان التجويد",
      days = "الأحد إلى الخميس",
      currentCurriculum = "من سورة البقرة إلى سورة المائدة",
      colorHex = 0xFFBE185D,
      section = HalaqahSection.GIRLS
    ),
    QuranHalaqah(
      id = 102,
      name = "حلقة عائشة أم المؤمنين رضي الله عنها",
      teacherName = "الشيخة فاطمة الزهراء الشامي",
      teacherTitle = "محفظة معتمدة ومتخصصة في مخارج الحروف",
      teacherPhone = "0558899001",
      location = "مجمع هائل سعيد أنعم - جناح مريم النسائي (قاعة النور)",
      timeSlot = "المغرب (6:15 م - 7:45 م)",
      studentCount = 20,
      capacity = 22,
      targetLevel = "الختمة وتثبيت القرآن الكريم كاملاً",
      days = "السبت والإثنين والأربعاء",
      currentCurriculum = "مراجعة المصحف كاملاً بالسند",
      colorHex = 0xFF9D174D,
      section = HalaqahSection.GIRLS
    ),
    QuranHalaqah(
      id = 103,
      name = "حلقة فاطمة الزهراء رضي الله عنها",
      teacherName = "الشيخة مريم بنت إبراهيم السالم",
      teacherTitle = "معلمة متخصصة في تلقين الزهراوين",
      teacherPhone = "0559900112",
      location = "مجمع هائل سعيد أنعم - جناح مريم النسائي (رواق الفردوس)",
      timeSlot = "الصباح (8:00 ص - 10:00 ص)",
      studentCount = 14,
      capacity = 15,
      targetLevel = "حفظ جديد للأمهات والفتيات",
      days = "الأحد والثلاثاء والخميس",
      currentCurriculum = "جزء عم وجزء تبارك وتصحيح التلاوة",
      colorHex = 0xFFE11D48,
      section = HalaqahSection.GIRLS
    ),
    QuranHalaqah(
      id = 104,
      name = "حلقة مريم بنت عمران عليها السلام",
      teacherName = "الشيخة سارة بنت عبد الرحمن",
      teacherTitle = "محفظة مجازة برواية حفص عن عاصم",
      teacherPhone = "0550011223",
      location = "مجمع هائل سعيد أنعم - جناح مريم النسائي (قاعة البيان)",
      timeSlot = "العصر (4:15 م - 5:45 م)",
      studentCount = 15,
      capacity = 16,
      targetLevel = "المرحلة المتوسطة وإتقان الغنن والمدود",
      days = "الأحد إلى الخميس",
      currentCurriculum = "من سورة مريم إلى سورة الفرقان",
      colorHex = 0xFF831843,
      section = HalaqahSection.GIRLS
    ),
    QuranHalaqah(
      id = 105,
      name = "حلقة حفصة بنت عمر رضي الله عنها",
      teacherName = "الشيخة أسماء بنت خالد الدوسري",
      teacherTitle = "محفظة براعم القرآن الكريم",
      teacherPhone = "0551122445",
      location = "مجمع هائل سعيد أنعم - جناح مريم النسائي (قاعة البراعم)",
      timeSlot = "بين العصر والمغرب (4:30 م - 6:00 م)",
      studentCount = 18,
      capacity = 20,
      targetLevel = "تلقين البراعم والناشئة وحفظ قصار السور",
      days = "السبت والأحد والثلاثاء",
      currentCurriculum = "قصار السور وأذكار اليوم والليلة",
      colorHex = 0xFFA21CAF,
      section = HalaqahSection.GIRLS
    )
  )

  fun getHalaqatBySection(section: HalaqahSection): List<QuranHalaqah> {
    return if (section == HalaqahSection.ALL) halaqatList else halaqatList.filter { it.section == section }
  }

  fun findHalaqahByName(name: String): QuranHalaqah? {
    return halaqatList.firstOrNull { it.name.trim() == name.trim() }
  }
}
