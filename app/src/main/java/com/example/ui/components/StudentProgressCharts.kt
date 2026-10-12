package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Student
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldSecondary

@Composable
fun StudentProgressChartsCard(
  student: Student?,
  modifier: Modifier = Modifier
) {
  val targetStudent = student ?: return

  val juzPercent = (targetStudent.memorizedJuzCount / 30f).coerceIn(0f, 1f)
  val animatedJuzPercent by animateFloatAsState(
    targetValue = juzPercent,
    animationSpec = tween(durationMillis = 800),
    label = "juz_progress"
  )

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(QuranEmeraldPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Equalizer,
              contentDescription = null,
              tint = QuranEmeraldPrimary,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "لوحة الرسوم البيانية لتقدم الحفظ",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = QuranEmeraldDark
            )
            Text(
              text = "متابعة أداء الطالب: ${targetStudent.name}",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(QuranGoldSecondary.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "${targetStudent.memorizedJuzCount} / 30 جزءاً",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF785700)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 1. Circular / Arc Progress for 30 Juz Completion
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0xFFF8FAF8))
          .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier.size(90.dp),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.size(90.dp)) {
            val strokeWidth = 10.dp.toPx()
            // Track circle
            drawArc(
              color = Color(0xFFE2E8F0),
              startAngle = 135f,
              sweepAngle = 270f,
              useCenter = false,
              style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            // Progress arc
            drawArc(
              brush = Brush.horizontalGradient(listOf(QuranEmeraldPrimary, QuranGoldSecondary)),
              startAngle = 135f,
              sweepAngle = 270f * animatedJuzPercent,
              useCenter = false,
              style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${(targetStudent.memorizedJuzCount * 100) / 30}%",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = QuranEmeraldDark
            )
            Text(
              text = "من المصحف",
              fontSize = 9.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "إنجاز الختمة المباركة",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = QuranEmeraldDark
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "أتم الطالب حفظ وتثبيت ${targetStudent.memorizedJuzCount} أجزاء معتمدة من المصحف الشريف في ${targetStudent.groupName}.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = QuranEmeraldPrimary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "السورة الحالية: سورة ${targetStudent.currentSurah} (آية ${targetStudent.currentAyah})",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = QuranEmeraldPrimary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Bar Chart: Weekly Recitation Progress (صفحات التسميع الأسبوعية)
      Text(
        text = "📊 معدل التسميع الأسبوعي (صفحات الحفظ والمراجعة)",
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = QuranEmeraldDark
      )
      Spacer(modifier = Modifier.height(8.dp))

      val days = listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس")
      val pages = listOf(4, 6, 5, 8, 7) // sample weekly data
      val maxPages = 10f

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(110.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFFF8FAFC))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        days.forEachIndexed { index, dayName ->
          val count = pages[index]
          val barRatio = count / maxPages

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
          ) {
            Text(
              text = "$count",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = QuranEmeraldDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
              modifier = Modifier
                .width(18.dp)
                .height((65 * barRatio).dp)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .background(
                  brush = Brush.verticalGradient(
                    listOf(
                      QuranEmeraldPrimary,
                      QuranEmeraldPrimary.copy(alpha = 0.65f)
                    )
                  )
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = dayName,
              fontSize = 9.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Tajweed Skills Progress Breakdown
      Text(
        text = "⭐ مؤشرات إتقان التجويد والأداء",
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = QuranEmeraldDark
      )
      Spacer(modifier = Modifier.height(8.dp))

      TajweedSkillBar(label = "مخارج الحروف وصفاتها", score = 95, color = QuranEmeraldPrimary)
      Spacer(modifier = Modifier.height(6.dp))
      TajweedSkillBar(label = "أحكام النون والميم الساكنة والغنن", score = 90, color = Color(0xFF195E63))
      Spacer(modifier = Modifier.height(6.dp))
      TajweedSkillBar(label = "المدود وأزمنتها والوقف والابتداء", score = 88, color = QuranGoldSecondary)
    }
  }
}

@Composable
private fun TajweedSkillBar(
  label: String,
  score: Int,
  color: Color
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
      Text(text = "$score%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
    }
    Spacer(modifier = Modifier.height(3.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(7.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(Color(0xFFE2E8F0))
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(score / 100f)
          .height(7.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(color)
      )
    }
  }
}
