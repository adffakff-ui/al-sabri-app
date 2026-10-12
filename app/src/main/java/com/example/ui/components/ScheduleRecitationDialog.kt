package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SampleDataProvider
import com.example.data.model.QuranData
import com.example.data.model.RecitationSchedule
import com.example.data.model.Student
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranError
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleRecitationDialog(
  students: List<Student>,
  initialStudent: Student? = null,
  onDismiss: () -> Unit,
  onScheduleCreated: (RecitationSchedule, Boolean) -> Unit
) {
  var selectedStudent by remember { mutableStateOf(initialStudent ?: students.firstOrNull()) }
  var isStudentDropdownExpanded by remember { mutableStateOf(false) }

  // Date management with quick presets
  val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
  val todayStr = remember { sdf.format(Date()) }
  val tomorrowStr = remember {
    val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    sdf.format(cal.time)
  }
  val dayAfterStr = remember {
    val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2) }
    sdf.format(cal.time)
  }

  var scheduledDate by remember { mutableStateOf(todayStr) }

  // Time Slot with quick presets
  val timePresets = listOf("04:30 م (بعد العصر)", "06:30 م (بعد المغرب)", "08:00 م (بعد العشاء)")
  var scheduledTime by remember { mutableStateOf("04:30 م") }

  // Surah & Ayah range
  var surahName by remember { mutableStateOf(selectedStudent?.currentSurah ?: "البقرة") }
  var surahSearchQuery by remember { mutableStateOf("") }
  var isSurahPickerExpanded by remember { mutableStateOf(false) }

  var fromAyah by remember { mutableIntStateOf(selectedStudent?.currentAyah ?: 1) }
  var toAyah by remember { mutableIntStateOf((selectedStudent?.currentAyah ?: 1) + 15) }

  val recitationTypes = listOf("حفظ جديد", "مراجعة صغرى", "مراجعة كبرى", "اختبار مرحلي")
  var selectedRecitationType by remember { mutableStateOf(recitationTypes.first()) }

  var teacherNotes by remember { mutableStateOf("") }
  var sendFcmPushAlert by remember { mutableStateOf(true) }
  var validationError by remember { mutableStateOf<String?>(null) }

  val filteredSurahs = remember(surahSearchQuery) {
    if (surahSearchQuery.isBlank()) {
      QuranData.surahs
    } else {
      QuranData.surahs.filter {
        it.name.contains(surahSearchQuery.trim()) ||
            it.number.toString().contains(surahSearchQuery.trim())
      }
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(QuranEmeraldLight),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = null,
              tint = QuranEmeraldPrimary,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "جدولة موعد تسميع جديد",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Text(
              text = "إشعار تلقائي عبر Firebase Cloud Messaging",
              style = MaterialTheme.typography.bodySmall,
              color = QuranEmeraldPrimary,
              fontSize = 11.sp
            )
          }
        }

        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // 1. Student Selector
        Text(text = "١. اختيار الطالب:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        ExposedDropdownMenuBox(
          expanded = isStudentDropdownExpanded,
          onExpandedChange = { isStudentDropdownExpanded = !isStudentDropdownExpanded }
        ) {
          OutlinedTextField(
            value = selectedStudent?.let { "${it.name} (${it.groupName})" } ?: "اختر الطالب",
            onValueChange = {},
            readOnly = true,
            leadingIcon = {
              Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = QuranEmeraldPrimary)
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStudentDropdownExpanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
              .testTag("schedule_student_selector")
          )
          ExposedDropdownMenu(
            expanded = isStudentDropdownExpanded,
            onDismissRequest = { isStudentDropdownExpanded = false }
          ) {
            students.forEach { st ->
              DropdownMenuItem(
                text = { Text("${st.name} • ${st.groupName}") },
                onClick = {
                  selectedStudent = st
                  surahName = st.currentSurah
                  fromAyah = st.currentAyah
                  toAyah = st.currentAyah + 15
                  isStudentDropdownExpanded = false
                }
              )
            }
          }
        }

        // 2. Date Selection with quick chips
        Text(text = "٢. تاريخ موعد التسميع:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          FilterChip(
            selected = scheduledDate == todayStr,
            onClick = { scheduledDate = todayStr },
            label = { Text("اليوم", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = QuranEmeraldPrimary,
              selectedLabelColor = Color.White
            ),
            modifier = Modifier.weight(1f)
          )
          FilterChip(
            selected = scheduledDate == tomorrowStr,
            onClick = { scheduledDate = tomorrowStr },
            label = { Text("غداً", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = QuranEmeraldPrimary,
              selectedLabelColor = Color.White
            ),
            modifier = Modifier.weight(1f)
          )
          FilterChip(
            selected = scheduledDate == dayAfterStr,
            onClick = { scheduledDate = dayAfterStr },
            label = { Text("بعد يومين", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = QuranEmeraldPrimary,
              selectedLabelColor = Color.White
            ),
            modifier = Modifier.weight(1f)
          )
        }
        OutlinedTextField(
          value = scheduledDate,
          onValueChange = { scheduledDate = it },
          label = { Text("التاريخ (YYYY-MM-DD)") },
          leadingIcon = { Icon(imageVector = Icons.Default.Event, contentDescription = null, tint = QuranEmeraldPrimary) },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        // 3. Time Slot with quick presets
        Text(text = "٣. وقت التسميع المقترح:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          timePresets.forEach { preset ->
            val cleanTime = preset.substringBefore(" ")
            FilterChip(
              selected = scheduledTime.startsWith(cleanTime),
              onClick = { scheduledTime = cleanTime },
              label = { Text(preset.substringBefore("(").trim(), fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = QuranEmeraldPrimary,
                selectedLabelColor = Color.White
              ),
              modifier = Modifier.weight(1f)
            )
          }
        }
        OutlinedTextField(
          value = scheduledTime,
          onValueChange = { scheduledTime = it },
          label = { Text("الوقت المحدد") },
          leadingIcon = { Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = QuranEmeraldPrimary) },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        // 4. Surah and Ayah Range
        Text(text = "٤. المقرر القرآني المستهدف:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        ExposedDropdownMenuBox(
          expanded = isSurahPickerExpanded,
          onExpandedChange = { isSurahPickerExpanded = !isSurahPickerExpanded }
        ) {
          OutlinedTextField(
            value = surahName,
            onValueChange = {
              surahName = it
              surahSearchQuery = it
              isSurahPickerExpanded = true
            },
            label = { Text("السورة المقررة") },
            leadingIcon = { Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = QuranEmeraldPrimary) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSurahPickerExpanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = isSurahPickerExpanded,
            onDismissRequest = { isSurahPickerExpanded = false }
          ) {
            filteredSurahs.take(8).forEach { s ->
              DropdownMenuItem(
                text = { Text("${s.number}. سورة ${s.name} (${s.ayahCount} آية)") },
                onClick = {
                  surahName = s.name
                  fromAyah = 1
                  toAyah = minOf(15, s.ayahCount)
                  isSurahPickerExpanded = false
                }
              )
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = fromAyah.toString(),
            onValueChange = { it.toIntOrNull()?.let { v -> fromAyah = v } },
            label = { Text("من آية") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = toAyah.toString(),
            onValueChange = { it.toIntOrNull()?.let { v -> toAyah = v } },
            label = { Text("إلى آية") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        // 5. Recitation Type
        Text(text = "٥. نوع جلسة التسميع:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          recitationTypes.forEach { type ->
            FilterChip(
              selected = selectedRecitationType == type,
              onClick = { selectedRecitationType = type },
              label = { Text(type, fontSize = 10.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = QuranEmeraldPrimary,
                selectedLabelColor = Color.White
              ),
              modifier = Modifier.weight(1f)
            )
          }
        }

        // 6. Teacher notes / instructions
        OutlinedTextField(
          value = teacherNotes,
          onValueChange = { teacherNotes = it },
          label = { Text("ملاحظات وتوجيهات للمعلم أو الطالب") },
          placeholder = { Text("توجيهات الاستعداد، ضبط الأحكام...") },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth(),
          minLines = 2
        )

        // 7. FCM Cloud Messaging Push Notification Toggle
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = QuranGoldLight),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Campaign,
                  contentDescription = null,
                  tint = Color(0xFF6B4E00),
                  modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "إرسال تنبيه فوري عبر FCM",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF4A3700)
                  )
                  Text(
                    text = "يصل إشعار فوري لهاتف الطالب وولي أمره",
                    fontSize = 10.sp,
                    color = Color(0xFF4A3700).copy(alpha = 0.85f)
                  )
                }
              }

              Checkbox(
                checked = sendFcmPushAlert,
                onCheckedChange = { sendFcmPushAlert = it },
                colors = CheckboxDefaults.colors(
                  checkedColor = QuranEmeraldPrimary,
                  checkmarkColor = Color.White
                )
              )
            }

            if (sendFcmPushAlert) {
              Spacer(modifier = Modifier.height(6.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color.White.copy(alpha = 0.8f))
                  .padding(8.dp)
              ) {
                Text(
                  text = "🔔 معاينة التنبيه: «موعد تسميع مجدول: سورة $surahName (الآيات $fromAyah - $toAyah) يوم $scheduledDate الساعة $scheduledTime للطالب ${selectedStudent?.name ?: ""}»",
                  fontSize = 10.sp,
                  color = QuranEmeraldDark,
                  lineHeight = 15.sp
                )
              }
            }
          }
        }

        validationError?.let { err ->
          Text(text = err, color = QuranError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val student = selectedStudent
          if (student == null) {
            validationError = "يرجى اختيار الطالب لتحديد الموعد."
            return@Button
          }
          if (fromAyah > toAyah) {
            validationError = "رقم آية البداية يجب أن يكون أقل من أو يساوي آية النهاية."
            return@Button
          }

          val schedule = RecitationSchedule(
            studentId = student.id,
            studentName = student.name,
            parentPhone = student.parentPhone.ifBlank { student.phone },
            groupName = student.groupName,
            scheduledDate = scheduledDate,
            scheduledTime = scheduledTime,
            surahName = surahName,
            fromAyah = fromAyah,
            toAyah = toAyah,
            recitationType = selectedRecitationType,
            teacherName = "الشيخ عبد الله بن فهد",
            teacherNotes = teacherNotes,
            status = "مجدول",
            fcmNotificationSent = sendFcmPushAlert
          )

          onScheduleCreated(schedule, sendFcmPushAlert)
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("submit_schedule_btn")
      ) {
        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("تأكيد الجدولة وإرسال إشعار FCM", fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("إلغاء")
      }
    }
  )
}
