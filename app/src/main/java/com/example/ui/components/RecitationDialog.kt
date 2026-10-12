package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SampleDataProvider
import com.example.data.model.QuranData
import com.example.data.model.RecitationRecord
import com.example.data.model.Student
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.ui.theme.QuranSuccess
import com.example.ui.theme.QuranSuccessLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecitationDialog(
  student: Student,
  onDismiss: () -> Unit,
  onSave: (RecitationRecord) -> Unit
) {
  var recitationType by remember { mutableStateOf("حفظ جديد") }
  var surahName by remember { mutableStateOf(student.currentSurah) }
  var fromAyah by remember { mutableIntStateOf(student.currentAyah) }
  var toAyah by remember { mutableIntStateOf(student.currentAyah + 10) }
  var evaluation by remember { mutableStateOf("ممتاز") }
  var mistakesCount by remember { mutableIntStateOf(0) }
  var tajweedScore by remember { mutableFloatStateOf(10f) }
  var teacherNotes by remember { mutableStateOf("") }

  var isSurahExpanded by remember { mutableStateOf(false) }

  val recitationTypes = listOf("حفظ جديد", "مراجعة صغرى", "مراجعة كبرى", "تلاوة وتجويد")
  val evaluations = listOf("ممتاز", "جيد جداً", "جيد", "مقبول", "يحتاج إعادة")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column {
        Text(
          text = "جلسة تسميع قرآن كريم",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
        Text(
          text = "الطالب: ${student.name}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Recitation Type selector
        Text(text = "نوع الجلسة:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          recitationTypes.forEach { type ->
            val isSelected = recitationType == type
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) QuranEmeraldPrimary else QuranEmeraldLight)
                .clickable { recitationType = type }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = type,
                color = if (isSelected) Color.White else QuranEmeraldPrimary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
              )
            }
          }
        }

        // Surah selector
        ExposedDropdownMenuBox(
          expanded = isSurahExpanded,
          onExpandedChange = { isSurahExpanded = !isSurahExpanded }
        ) {
          OutlinedTextField(
            value = "سورة $surahName",
            onValueChange = {},
            readOnly = true,
            label = { Text("السورة الكريمة") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSurahExpanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = isSurahExpanded,
            onDismissRequest = { isSurahExpanded = false }
          ) {
            QuranData.surahs.forEach { s ->
              DropdownMenuItem(
                text = { Text("${s.number}. سورة ${s.name} (${s.ayahCount} آية)") },
                onClick = {
                  surahName = s.name
                  isSurahExpanded = false
                }
              )
            }
          }
        }

        // Ayah range
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = fromAyah.toString(),
            onValueChange = { fromAyah = it.toIntOrNull() ?: 1 },
            label = { Text("من آية") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = toAyah.toString(),
            onValueChange = { toAyah = it.toIntOrNull() ?: 1 },
            label = { Text("إلى آية") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        // Evaluation selector
        Text(text = "التقدير والتقييم:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          evaluations.forEach { eval ->
            val isSelected = evaluation == eval
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) QuranGoldSecondary else QuranGoldLight)
                .clickable { evaluation = eval }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = eval,
                color = if (isSelected) Color.White else Color(0xFF6B4E00),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
              )
            }
          }
        }

        // Tajweed score slider
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "درجة التجويد والإتقان:", fontSize = 12.sp)
            Text(
              text = "${tajweedScore.toInt()} / 10",
              fontWeight = FontWeight.Bold,
              color = QuranEmeraldPrimary,
              fontSize = 13.sp
            )
          }
          Slider(
            value = tajweedScore,
            onValueChange = { tajweedScore = it },
            valueRange = 1f..10f,
            steps = 8,
            colors = SliderDefaults.colors(
              thumbColor = QuranEmeraldPrimary,
              activeTrackColor = QuranEmeraldPrimary
            )
          )
        }

        // Mistakes count
        OutlinedTextField(
          value = mistakesCount.toString(),
          onValueChange = { mistakesCount = it.toIntOrNull() ?: 0 },
          label = { Text("عدد الملاحظات والأخطاء") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        // Teacher Notes
        OutlinedTextField(
          value = teacherNotes,
          onValueChange = { teacherNotes = it },
          label = { Text("ملاحظات وتوجيهات المعلم") },
          modifier = Modifier.fillMaxWidth(),
          minLines = 2
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val record = RecitationRecord(
            studentId = student.id,
            studentName = student.name,
            date = SampleDataProvider.todayDate,
            recitationType = recitationType,
            surahName = surahName,
            fromAyah = fromAyah,
            toAyah = toAyah,
            evaluation = evaluation,
            mistakesCount = mistakesCount,
            tajweedScore = tajweedScore.toInt(),
            teacherNotes = teacherNotes
          )
          onSave(record)
        },
        colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("save_recitation_btn")
      ) {
        Text("حفظ التسميع", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("إلغاء")
      }
    }
  )
}
