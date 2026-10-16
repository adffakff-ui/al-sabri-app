package com.example.data.model

enum class AssociationType(
  val id: String,
  val fullName: String,
  val shortName: String,
  val motto: String,
  val primaryColorHex: Long,
  val secondaryColorHex: Long,
  val accentColorHex: Long,
  val iconEmoji: String,
  val defaultCircles: List<String>,
  val supervisorName: String,
  val contactPhone: String
) {
  HAIEL_SAEED(
    id = "HAIEL_SAEED",
    fullName = "جمعية هائل سعيد أنعم لتحفيظ القرآن الكريم",
    shortName = "جمعية هائل سعيد أنعم",
    motto = "منظومة الحلقات القرآنية والتسميع المنهجي ورعاية الحفاظ وإجازات القراءات",
    primaryColorHex = 0xFF0D6850, // الزمردي القرآني
    secondaryColorHex = 0xFFC99700, // الذهبي الفاخر
    accentColorHex = 0xFFE6F4EA,
    iconEmoji = "🌿",
    defaultCircles = listOf(
      "حلقة الإمام الشاطبي",
      "حلقة عثمان بن عفان رضي الله عنه",
      "حلقة أم المؤمنين خديجة",
      "حلقة الزهراء للفتيات"
    ),
    supervisorName = "فضيلة الشيخ د. عبد الرحمن الشريف",
    contactPhone = "0501234567"
  ),

  MUADH_JARWI(
    id = "MUADH_JARWI",
    fullName = "جمعية معاذ الحروي لصيانة وخدمة القرآن الكريم",
    shortName = "جمعية معاذ الحروي",
    motto = "صيانة المصاحف وتدريب معالجة المخطوطات والتعليم القرآني المتخصص والإتقان الصوتي",
    primaryColorHex = 0xFF1E3A8A, // كحلي ملكي عميق
    secondaryColorHex = 0xFF0284C7, // أزرق سماوي ياقوتي
    accentColorHex = 0xFFE0F2FE,
    iconEmoji = "📖",
    defaultCircles = listOf(
      "حلقة الإمام نافع المدني",
      "حلقة معاذ بن جبل رضي الله عنه",
      "حلقة الترتيل والإتقان الصوتي",
      "حلقة صيانة المصحف والضبط"
    ),
    supervisorName = "فضيلة الشيخ معاذ بن عبد الله الحروي",
    contactPhone = "0559876543"
  );

  companion object {
    fun fromId(id: String?): AssociationType {
      return values().find { it.id.equals(id, ignoreCase = true) } ?: HAIEL_SAEED
    }
  }
}
