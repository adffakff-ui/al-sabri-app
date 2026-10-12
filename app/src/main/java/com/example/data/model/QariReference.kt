package com.example.data.model

data class QariReference(
  val id: Int,
  val name: String,
  val title: String,
  val style: String, // المصحف المعلم, المرتل, المجود
  val baseUrl: String,
  val levelFocus: String, // مبتدئ, متوسط, متقدم, خاتم
  val recommendationText: String,
  val colorHex: Long = 0xFF0D6850
) {
  val avatarColorHex: Long get() = colorHex

  fun getSurahAudioUrl(surahNumber: Int): String {
    val formattedNumber = String.format("%03d", surahNumber)
    return "$baseUrl$formattedNumber.mp3"
  }
}

object QariDirectory {
  val qaris = listOf(
    QariReference(
      id = 1,
      name = "الشيخ محمود خليل الحصري",
      title = "شيخ عموم المقارئ المصرية الأسبق",
      style = "المصحف المعلم (ترتيل بطيء مع الوقف)",
      baseUrl = "https://server13.mp3quran.net/husr/",
      levelFocus = "جميع المستويات (المرجع الأول للتجويد)",
      recommendationText = "المرجع العالمي لضبط مخارج الحروف وأحكام التجويد والمدود والغنن بدون تكلف.",
      colorHex = 0xFF0D6850
    ),
    QariReference(
      id = 2,
      name = "الشيخ عبد الباسط عبد الصمد",
      title = "صوت مكة وقارئ العالم الإسلامي",
      style = "المرتل المحقق",
      baseUrl = "https://server7.mp3quran.net/basit/",
      levelFocus = "المستوى المتقدم والخاتمين",
      recommendationText = "يتميز بقوة النفس وإتقان درجات الصوت والمدود وتحقيق صفات الحروف بدقة.",
      colorHex = 0xFF195E63
    ),
    QariReference(
      id = 3,
      name = "الشيخ محمد صديق المنشاوي",
      title = "صوت الخشوع والترتيل الباكي",
      style = "المصحف المرتل",
      baseUrl = "https://server10.mp3quran.net/minsh/",
      levelFocus = "المستوى المتوسط والمتقدم",
      recommendationText = "مدرسة في تدبر معاني الآيات والوقف والابتداء وحسن التعبير النغمي القرآني.",
      colorHex = 0xFFC99700
    ),
    QariReference(
      id = 4,
      name = "الشيخ مشاري بن راشد العفاسي",
      title = "قارئ المسجد الكبير بالكويت",
      style = "المرتل المعتدل",
      baseUrl = "https://server8.mp3quran.net/afs/",
      levelFocus = "المستوى المبتدئ والمتوسط",
      recommendationText = "سرعة معتدلة ونغم واضح يساعد الطالب على الحفظ والترديد بانتظام وسهولة.",
      colorHex = 0xFF2563EB
    ),
    QariReference(
      id = 5,
      name = "الشيخ عبد الرحمن السديس",
      title = "إمام وخطيب المسجد الحرام",
      style = "تلاوة الحرم المكي (الحدر المتقن)",
      baseUrl = "https://server11.mp3quran.net/sds/",
      levelFocus = "المراجعة والتثبيت السريع",
      recommendationText = "مثالي لتدريب الطالب على الحدر ومراجعة الأجزاء الكبيرة مع الحفاظ على الأحكام.",
      colorHex = 0xFFD97706
    )
  )

  fun calculateStudentLevel(
    makharijScore: Int, // 1-10
    tajweedScore: Int,  // 1-10
    fluencyScore: Int,  // 1-10
    voiceTuneScore: Int // 1-10
  ): LevelAssessmentResult {
    val totalScore = makharijScore + tajweedScore + fluencyScore + voiceTuneScore // out of 40
    val percentage = (totalScore / 40.0) * 100

    val levelTitle = when {
      percentage >= 90 -> "حافظ متقن (مؤهل للإجازة بالسند)"
      percentage >= 80 -> "مستوى متقدم (تلاوة ممتازة مع تجويد دقيق)"
      percentage >= 70 -> "مستوى متوسط (حفظ جيد مع ضبط الأحكام الأساسية)"
      percentage >= 60 -> "مستوى ناشئ (يحتاج تركيز على أحكام التجويد والغنن)"
      else -> "مستوى مبتدئ (يحتاج تلقين مستمر وتصحيح مخارج الحروف)"
    }

    val matchedQari = when {
      percentage >= 90 -> qaris[0] // الحصري
      percentage >= 80 -> qaris[1] // عبد الباسط
      percentage >= 70 -> qaris[2] // المنشاوي
      else -> qaris[3] // العفاسي
    }

    val advice = when {
      percentage >= 90 -> "ما شاء الله، أداء رفيع يقارب أداء كبار القراء، يوصى بالبدء في عرض الختمة بالسند."
      percentage >= 80 -> "أداء متميز وثابت، احرص على محاكاة الشيخ الحصري في ضبط أزمنة الغنن والمدود المنفصلة."
      percentage >= 70 -> "حفظ طيب، استمع يومياً للشيخ المنشاوي وكرر الآيات معه لتحسين الوقف والابتداء."
      else -> "أنت في بداية الطريق المبارك، ردد خلف المصحف المعلم للشيخ الحصري لتثبيت مخارج الحروف."
    }

    return LevelAssessmentResult(
      totalScore = totalScore,
      percentage = percentage.toInt(),
      levelTitle = levelTitle,
      recommendedQari = matchedQari,
      advice = advice
    )
  }
}

data class LevelAssessmentResult(
  val totalScore: Int,
  val percentage: Int,
  val levelTitle: String,
  val recommendedQari: QariReference,
  val advice: String
)
