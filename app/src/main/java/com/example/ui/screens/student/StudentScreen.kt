package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.HeroQuranBanner
import com.example.ui.components.RecitationRecordCard
import com.example.ui.components.StatCard
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.util.ShareHelper
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val students by viewModel.students.collectAsStateWithLifecycle()
  val selectedStudent by viewModel.selectedStudent.collectAsStateWithLifecycle()
  val recitations by viewModel.recitations.collectAsStateWithLifecycle()
  val attendance by viewModel.attendance.collectAsStateWithLifecycle()

  var isStudentDropdownExpanded by remember { mutableStateOf(false) }

  // Current active student
  val student = selectedStudent ?: students.firstOrNull()

  // Student's recitations
  val studentRecitations = student?.let { s ->
    recitations.filter { it.studentId == s.id }
  } ?: emptyList()

  // Student's attendance count
  val studentAttendanceCount = student?.let { s ->
    attendance.count { it.studentId == s.id && it.status == "حاضر" }
  } ?: 0

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Student Switcher
    item {
      ExposedDropdownMenuBox(
        expanded = isStudentDropdownExpanded,
        onExpandedChange = { isStudentDropdownExpanded = !isStudentDropdownExpanded }
      ) {
        OutlinedTextField(
          value = student?.name ?: "اختر طالباً للعرض",
          onValueChange = {},
          readOnly = true,
          label = { Text("عرض صفحة الطالب:") },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = QuranEmeraldPrimary)
          },
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStudentDropdownExpanded) },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .menuAnchor()
            .fillMaxWidth()
        )
        ExposedDropdownMenu(
          expanded = isStudentDropdownExpanded,
          onDismissRequest = { isStudentDropdownExpanded = false }
        ) {
          students.forEach { s ->
            DropdownMenuItem(
              text = { Text("${s.name} (${s.groupName})") },
              onClick = {
                viewModel.selectStudent(s)
                isStudentDropdownExpanded = false
              }
            )
          }
        }
      }
    }

    if (student != null) {
      // Student Profile Hero Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                brush = Brush.verticalGradient(
                  colors = listOf(
                    QuranEmeraldPrimary.copy(alpha = 0.08f),
                    Color.Transparent
                  )
                )
              )
              .padding(18.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Box(
                modifier = Modifier
                  .size(60.dp)
                  .clip(CircleShape)
                  .background(Color(student.avatarColorHex)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = student.name.firstOrNull()?.toString() ?: "ط",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 24.sp
                )
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = student.name,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  fontSize = 18.sp
                )
                Text(
                  text = "${student.groupName} • المستوى ${student.level}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar for Memorization
            val progress = (student.memorizedJuzCount / 30f).coerceIn(0f, 1f)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "إنجاز القرآن الكريم (30 جزء):",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "${student.memorizedJuzCount} / 30 جزء (${(progress * 100).toInt()}%)",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = QuranEmeraldPrimary
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
              progress = { progress },
              modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
              color = QuranEmeraldPrimary,
              trackColor = QuranEmeraldLight
            )
          }
        }
      }

      // Quick Metrics
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatCard(
            title = "الأجزاء المتقنة",
            value = "${student.memorizedJuzCount}",
            icon = Icons.Default.Bookmark,
            tintColor = QuranGoldSecondary,
            bgColor = QuranGoldLight,
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "جلسات التسميع",
            value = "${studentRecitations.size}",
            icon = Icons.Default.MenuBook,
            tintColor = QuranEmeraldPrimary,
            bgColor = QuranEmeraldLight,
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "أيام الحضور",
            value = "$studentAttendanceCount",
            icon = Icons.Default.CheckCircle,
            tintColor = Color(0xFF1B8A5A),
            bgColor = Color(0xFFE5F7EE),
            modifier = Modifier.weight(1f)
          )
        }
      }

      // Current Memorization Target Banner
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = QuranEmeraldDark)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "الورد القرآني الحالي",
                color = QuranGoldSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "سورة ${student.currentSurah} • الآية (${student.currentAyah})",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
              Text(
                text = "استمر في الحفظ والمراجعة لنيل شرف الخاتمين",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
              )
            }
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = QuranGoldSecondary,
              modifier = Modifier.size(36.dp)
            )
          }
        }
      }

      // Recitation History Header
      item {
        Text(
          text = "سجل التسميع والمراجعات (${studentRecitations.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      if (studentRecitations.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(
              text = "لم يتم تسجيل جلسات تسميع لهذا الطالب بعد",
              modifier = Modifier.padding(24.dp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        items(studentRecitations) { record ->
          RecitationRecordCard(
            record = record,
            onShareClick = {
              val report = ShareHelper.generateRecitationReport(student, record)
              ShareHelper.shareGeneral(context, report, "مشاركة تقرير تسميع الطالب")
            }
          )
        }
      }
    }
  }
}
