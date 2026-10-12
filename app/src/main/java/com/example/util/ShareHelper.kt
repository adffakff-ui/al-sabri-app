package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.model.RecitationRecord
import com.example.data.model.Student

object ShareHelper {
  fun shareViaWhatsApp(context: Context, phoneNumber: String, message: String) {
    try {
      val cleanPhone = phoneNumber.replace(Regex("[^0-9]"), "")
      val formattedPhone = if (cleanPhone.startsWith("0")) {
        "966" + cleanPhone.substring(1) // default to Saudi format if starting with 0
      } else {
        cleanPhone
      }
      val uri = Uri.parse("https://api.whatsapp.com/send?phone=$formattedPhone&text=${Uri.encode(message)}")
      val intent = Intent(Intent.ACTION_VIEW, uri)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intent)
    } catch (e: Exception) {
      // Fallback to general share
      shareGeneral(context, message)
    }
  }

  fun shareGeneral(context: Context, text: String, title: String = "مشاركة تقرير الطالب") {
    val sendIntent: Intent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, text)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, title)
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(shareIntent)
  }

  fun generateRecitationReport(student: Student, recitation: RecitationRecord): String {
    return """
      *تقرير إنجاز قرآني - حلقات هائل سعيد أنعم لتحفيظ القرآن الكريم* 📖✨
      ━━━━━━━━━━━━━━━━━━━━
      👤 *اسم الطالب:* ${student.name}
      🏛️ *الحلقة:* ${student.groupName}
      📅 *التاريخ:* ${recitation.date}
      
      📜 *نوع التسميع:* ${recitation.recitationType}
      📖 *السورة:* ${recitation.surahName}
      🔢 *الآيات:* من الآية (${recitation.fromAyah}) إلى (${recitation.toAyah})
      ⭐ *التقدير العام:* ${recitation.evaluation}
      🎯 *درجة التجويد:* ${recitation.tajweedScore} / 10
      ⚠️ *عدد الملاحظات:* ${recitation.mistakesCount}
      
      📝 *ملاحظات المعلم:*
      ${if (recitation.teacherNotes.isNotBlank()) recitation.teacherNotes else "أداء متميز وتلاوة مباركة بارك الله فيه."}
      
      جزاكم الله خيراً على حسن متابعتكم وتشجيعكم لحامل كتاب الله.
    """.trimIndent()
  }
}
