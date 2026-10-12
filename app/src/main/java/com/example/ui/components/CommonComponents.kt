package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.RecitationRecord
import com.example.data.model.Student
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranError
import com.example.ui.theme.QuranErrorLight
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.ui.theme.QuranSuccess
import com.example.ui.theme.QuranSuccessLight
import com.example.ui.theme.QuranSurfaceVariant
import com.example.ui.theme.QuranWarning
import com.example.ui.theme.QuranWarningLight

@Composable
fun HeroQuranBanner(
  title: String = "حلقات تحفيظ القرآن الكريم",
  subtitle: String = "﴿ وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا ﴾",
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(130.dp),
    shape = RoundedCornerShape(18.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      Image(
        painter = painterResource(id = R.drawable.quran_hero_banner),
        contentDescription = "بانر القرآن الكريم",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
      // Gradient Overlay for pristine text readability
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            brush = Brush.horizontalGradient(
              colors = listOf(
                QuranEmeraldDark.copy(alpha = 0.88f),
                QuranEmeraldPrimary.copy(alpha = 0.65f),
                Color.Transparent
              )
            )
          )
      )
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = title,
          color = QuranGoldSecondary,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = subtitle,
          color = Color.White,
          fontWeight = FontWeight.Medium,
          fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Bookmark,
            contentDescription = null,
            tint = QuranGoldSecondary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "متابعة الحفظ والتسميع اليومي بإتقان",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

@Composable
fun StatCard(
  title: String,
  value: String,
  icon: ImageVector,
  tintColor: Color = QuranEmeraldPrimary,
  bgColor: Color = QuranEmeraldLight,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(bgColor),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = tintColor,
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = value,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = title,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
fun StudentCard(
  student: Student,
  onReciteClick: () -> Unit,
  onAttendanceClick: () -> Unit,
  onCardClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onCardClick)
      .testTag("student_card_${student.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          // Student Avatar
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(Color(student.avatarColorHex)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = student.name.firstOrNull()?.toString() ?: "ط",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = student.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = student.groupName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
              )
              Text(
                text = " • ",
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = student.level,
                style = MaterialTheme.typography.bodySmall,
                color = QuranEmeraldPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
              )
            }
          }
        }

        // Memorized Juz badge
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = QuranGoldLight)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Bookmark,
              contentDescription = null,
              tint = QuranGoldSecondary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${student.memorizedJuzCount} أجزاء",
              color = Color(0xFF6B4E00),
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Current Surah & Progress Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(QuranSurfaceVariant.copy(alpha = 0.5f))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            tint = QuranEmeraldPrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "الموضع الحالي: سورة ${student.currentSurah} (آية ${student.currentAyah})",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
          )
        }
        Icon(
          imageVector = Icons.Default.ChevronLeft,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onReciteClick,
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .testTag("recite_btn_${student.id}"),
          colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "تسميع جديد", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onAttendanceClick,
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .testTag("attendance_btn_${student.id}"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "تسجيل حضور", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun EvaluationBadge(evaluation: String, modifier: Modifier = Modifier) {
  val (bgColor, textColor) = when (evaluation) {
    "ممتاز" -> QuranSuccessLight to QuranSuccess
    "جيد جداً" -> QuranEmeraldLight to QuranEmeraldPrimary
    "جيد" -> QuranGoldLight to QuranGoldSecondary
    "مقبول" -> QuranWarningLight to QuranWarning
    else -> QuranErrorLight to QuranError
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 3.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = evaluation,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun AttendanceStatusBadge(status: String, modifier: Modifier = Modifier) {
  val (bgColor, textColor) = when (status) {
    "حاضر" -> QuranSuccessLight to QuranSuccess
    "متأخر" -> QuranWarningLight to QuranWarning
    "مستأذن" -> QuranGoldLight to Color(0xFF8C6800)
    else -> QuranErrorLight to QuranError
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 3.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = status,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun RecitationRecordCard(
  record: RecitationRecord,
  onShareClick: () -> Unit,
  onPlayRecording: ((String) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "سورة ${record.surahName}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(QuranEmeraldLight)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = record.qiraahName,
                color = QuranEmeraldDark,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
          Text(
            text = "الآيات: من ${record.fromAyah} إلى ${record.toAyah} • ${record.recitationType} (${record.scorePercent}%)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          EvaluationBadge(evaluation = record.evaluation)
          Spacer(modifier = Modifier.width(4.dp))

          // Audio recording playback button if student recorded audio
          record.audioFilePath?.let { audioPath ->
            IconButton(
              onClick = { onPlayRecording?.invoke(audioPath) },
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(QuranEmeraldLight)
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "استماع للتسجيل الصوتي",
                tint = QuranEmeraldPrimary,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(4.dp))
          }

          IconButton(
            onClick = onShareClick,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "مشاركة التقرير",
              tint = QuranEmeraldPrimary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      if (record.teacherNotes.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(QuranSurfaceVariant.copy(alpha = 0.5f))
            .padding(8.dp)
        ) {
          Text(
            text = "ملاحظة المعلم: ${record.teacherNotes}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "التجويد: ${record.tajweedScore}/10  |  الأخطاء: ${record.mistakesCount}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
        Text(
          text = record.date,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
      }
    }
  }
}
