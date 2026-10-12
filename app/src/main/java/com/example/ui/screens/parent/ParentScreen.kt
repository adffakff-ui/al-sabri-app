package com.example.ui.screens.parent

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AttendanceStatusBadge
import com.example.ui.components.EvaluationBadge
import com.example.ui.components.HeroQuranBanner
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.ui.theme.QuranSurfaceVariant
import com.example.util.ShareHelper
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val students by viewModel.students.collectAsStateWithLifecycle()
  val selectedStudent by viewModel.selectedStudent.collectAsStateWithLifecycle()
  val recitations by viewModel.recitations.collectAsStateWithLifecycle()
  val attendance by viewModel.attendance.collectAsStateWithLifecycle()
  val notifications by viewModel.notifications.collectAsStateWithLifecycle()

  var isStudentDropdownExpanded by remember { mutableStateOf(false) }

  val student = selectedStudent ?: students.firstOrNull()

  val studentRecitations = student?.let { s ->
    recitations.filter { it.studentId == s.id }
  } ?: emptyList()

  val studentAttendance = student?.let { s ->
    attendance.filter { it.studentId == s.id }
  } ?: emptyList()

  val studentNotifications = student?.let { s ->
    notifications.filter { it.studentId == s.id }
  } ?: emptyList()

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Child selector
    item {
      ExposedDropdownMenuBox(
        expanded = isStudentDropdownExpanded,
        onExpandedChange = { isStudentDropdownExpanded = !isStudentDropdownExpanded }
      ) {
        OutlinedTextField(
          value = student?.name ?: "اختر الابن/الابنة",
          onValueChange = {},
          readOnly = true,
          label = { Text("متابعة تقرير الابن:") },
          leadingIcon = {
            Icon(imageVector = Icons.Default.FamilyRestroom, contentDescription = null, tint = QuranEmeraldPrimary)
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
              text = { Text("${s.name} (ولي الأمر: ${s.parentName.ifBlank { "مسجل" }})") },
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
      // Child Progress Overview Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Box(
                modifier = Modifier
                  .size(52.dp)
                  .clip(CircleShape)
                  .background(Color(student.avatarColorHex)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = student.name.firstOrNull()?.toString() ?: "ط",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 22.sp
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = student.name,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "${student.groupName} • ${student.level}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Details box
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(QuranEmeraldLight)
                .padding(12.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                  text = "📖 الورد الحالي: سورة ${student.currentSurah} (آية ${student.currentAyah})",
                  color = QuranEmeraldDark,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 13.sp
                )
                Text(
                  text = "⭐ الأجزاء المتقنة: ${student.memorizedJuzCount} أجزاء",
                  color = QuranEmeraldDark,
                  fontSize = 12.sp
                )
                Text(
                  text = "📞 هاتف التواصل لولي الأمر: ${student.parentPhone.ifBlank { "غير مسجل" }}",
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 12.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // WhatsApp direct share button
            Button(
              onClick = {
                val latestRecitation = studentRecitations.firstOrNull()
                val msg = if (latestRecitation != null) {
                  ShareHelper.generateRecitationReport(student, latestRecitation)
                } else {
                  "تقرير الطالب ${student.name} في حلقات هائل سعيد أنعم لتحفيظ القرآن الكريم: الحفظ الحالي سورة ${student.currentSurah}."
                }
                ShareHelper.shareViaWhatsApp(context, student.parentPhone, msg)
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("whatsapp_share_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "مشاركة التقرير مع الأسرة عبر واتساب",
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }

      // Teacher Notifications Section
      item {
        Text(
          text = "إشعارات وتقارير المعلم (${studentNotifications.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      if (studentNotifications.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(
              text = "لا توجد رسائل جديدة من المعلم حالياً",
              modifier = Modifier.padding(20.dp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        items(studentNotifications) { notif ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = QuranGoldSecondary,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = notif.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                }
                Text(
                  text = notif.date,
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = notif.message,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }

      // Attendance Summary
      item {
        Text(
          text = "سجل الحضور والغياب (${studentAttendance.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      if (studentAttendance.isEmpty()) {
        item {
          Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text(
              text = "لا توجد سجلات حضور مسجلة لهذا الطالب بعد",
              modifier = Modifier.padding(20.dp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        items(studentAttendance) { att ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CalendarMonth,
                  contentDescription = null,
                  tint = QuranEmeraldPrimary,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(text = "التاريخ: ${att.date}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                  if (att.notes.isNotBlank()) {
                    Text(text = att.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                }
              }
              AttendanceStatusBadge(status = att.status)
            }
          }
        }
      }
    }
  }
}
