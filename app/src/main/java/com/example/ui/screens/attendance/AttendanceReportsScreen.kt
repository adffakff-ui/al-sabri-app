package com.example.ui.screens.attendance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.SampleDataProvider
import com.example.data.model.AttendanceRecord
import com.example.data.model.QuranHalaqatData
import com.example.data.model.Student
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldSecondary
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AttendanceReportsScreen(
  viewModel: MainViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  val students by viewModel.students.collectAsStateWithLifecycle()
  val attendanceList by viewModel.attendance.collectAsStateWithLifecycle()

  var selectedDate by remember { mutableStateOf(SampleDataProvider.todayDate) }
  var selectedHalaqah by remember { mutableStateOf<String?>("الكل") }

  // Filter students by selected halaqah
  val filteredStudents = remember(students, selectedHalaqah) {
    if (selectedHalaqah == null || selectedHalaqah == "الكل") {
      students
    } else {
      students.filter { it.groupName.contains(selectedHalaqah!!) || selectedHalaqah!!.contains(it.groupName) }
    }
  }

  // Map student attendance for the selected date
  val attendanceMap = remember(attendanceList, selectedDate) {
    attendanceList.filter { it.date == selectedDate }.associateBy { it.studentId }
  }

  // Statistics
  val presentCount = filteredStudents.count {
    val status = attendanceMap[it.id]?.status ?: "حاضر"
    status.contains("حاضر")
  }
  val absentCount = filteredStudents.count {
    val status = attendanceMap[it.id]?.status
    status != null && status.contains("غائب")
  }
  val lateCount = filteredStudents.count {
    val status = attendanceMap[it.id]?.status
    status != null && status.contains("متأخر")
  }
  val excusedCount = filteredStudents.count {
    val status = attendanceMap[it.id]?.status
    status != null && status.contains("مستأذن")
  }

  val totalStudents = filteredStudents.size
  val attendanceRate = if (totalStudents > 0) ((presentCount * 100) / totalStudents) else 100

  Scaffold(
    modifier = modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "تقرير الحضور والغياب اليومي",
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = Color.White
            )
            Text(
              text = "نظام الرصد الفوري والتنبيهات التلقائية عبر FCM",
              fontSize = 11.sp,
              color = QuranGoldSecondary
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = QuranEmeraldDark)
      )
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Filter section: Date and Halaqah
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.FilterList, contentDescription = null, tint = QuranEmeraldPrimary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "تصفية التقارير حسب الحلقة والتاريخ",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = QuranEmeraldDark
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Halaqah Selector Chips (بنين وبنات)
            Text(text = "اختر الحلقة:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              item {
                FilterChip(
                  selected = selectedHalaqah == "الكل",
                  onClick = { selectedHalaqah = "الكل" },
                  label = { Text("كافة الحلقات (بنين وبنات)") },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = QuranEmeraldPrimary,
                    selectedLabelColor = Color.White
                  )
                )
              }
              items(QuranHalaqatData.halaqatList) { halaqah ->
                val isSelected = selectedHalaqah == halaqah.name
                val isGirls = halaqah.isGirlsSection
                FilterChip(
                  selected = isSelected,
                  onClick = { selectedHalaqah = halaqah.name },
                  label = { Text("${if (isGirls) "👧 " else "👦 "}${halaqah.name.take(18)}...") },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = if (isGirls) Color(0xFFBE185D) else QuranEmeraldPrimary,
                    selectedLabelColor = Color.White
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Date selector
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = QuranEmeraldPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "تاريخ التقرير: $selectedDate", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }

              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                  onClick = { selectedDate = SampleDataProvider.todayDate },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedDate == SampleDataProvider.todayDate) QuranEmeraldPrimary else Color(0xFFE2E8F0),
                    contentColor = if (selectedDate == SampleDataProvider.todayDate) Color.White else Color(0xFF334155)
                  ),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.height(32.dp)
                ) {
                  Text("اليوم", fontSize = 11.sp)
                }
              }
            }
          }
        }
      }

      // 2. Summary KPI Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Attendance Rate
          Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
          ) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "$attendanceRate%", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = QuranEmeraldDark)
              Text(text = "نسبة الحضور", fontSize = 11.sp, color = QuranEmeraldDark)
            }
          }

          // Present Count
          Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
          ) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "$presentCount", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1E40AF))
              Text(text = "الحاضرون", fontSize = 11.sp, color = Color(0xFF1E40AF))
            }
          }

          // Absent Count
          Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
          ) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "$absentCount", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFFDC2626))
              Text(text = "الغائبون", fontSize = 11.sp, color = Color(0xFFDC2626))
            }
          }

          // Late Count
          Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
          ) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "$lateCount", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFFB45309))
              Text(text = "المتأخرون", fontSize = 11.sp, color = Color(0xFFB45309))
            }
          }
        }
      }

      // 3. Section Title
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "قائمة الطلاب والرصد اليومي (${filteredStudents.size} طالب/طالبة)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = QuranEmeraldDark
          )
        }
      }

      // 4. Students attendance cards
      items(filteredStudents) { student ->
        val record = attendanceMap[student.id]
        val currentStatus = record?.status ?: "حاضر"
        val isAbsent = currentStatus.contains("غائب")

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isAbsent) Color(0xFFFFF1F2) else MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
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
                    .background(Color(student.avatarColorHex)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = student.name.take(1),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = student.name,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      color = QuranEmeraldDark
                    )
                    if (student.isGirlsSection) {
                      Spacer(modifier = Modifier.width(6.dp))
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(Color(0xFFFCE7F3))
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text("بنات", fontSize = 9.sp, color = Color(0xFFBE185D), fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                  Text(
                    text = "${student.groupName} • ولي الأمر: ${student.parentName} (${student.parentPhone})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              // Status Badge
              val badgeColor = when {
                currentStatus.contains("حاضر") -> Color(0xFF16A34A)
                currentStatus.contains("غائب") -> Color(0xFFDC2626)
                currentStatus.contains("متأخر") -> Color(0xFFD97706)
                else -> Color(0xFF2563EB)
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(badgeColor.copy(alpha = 0.15f))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = currentStatus,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = badgeColor
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons for Teacher: Toggle Status (حاضر، غائب، متأخر)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              OutlinedButton(
                onClick = {
                  viewModel.recordAttendance(
                    AttendanceRecord(
                      studentId = student.id,
                      studentName = student.name,
                      date = selectedDate,
                      status = "حاضر",
                      notes = "حضور نظامي"
                    )
                  )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("حاضر", fontSize = 10.sp)
              }

              OutlinedButton(
                onClick = {
                  viewModel.recordAttendance(
                    AttendanceRecord(
                      studentId = student.id,
                      studentName = student.name,
                      date = selectedDate,
                      status = "متأخر",
                      notes = "تأخر عن بدء الحلقة"
                    )
                  )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("متأخر", fontSize = 10.sp)
              }

              OutlinedButton(
                onClick = {
                  viewModel.recordAttendance(
                    AttendanceRecord(
                      studentId = student.id,
                      studentName = student.name,
                      date = selectedDate,
                      status = "غائب",
                      notes = "غائب بدون عذر مسبق"
                    )
                  )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                  contentColor = Color(0xFFDC2626)
                )
              ) {
                Text("غائب", fontSize = 10.sp)
              }
            }

            // If absent, show FCM alert trigger button
            if (isAbsent) {
              Spacer(modifier = Modifier.height(8.dp))
              Button(
                onClick = {
                  viewModel.sendAbsenceNotification(student, selectedDate, "غياب غير مبرر عن حلقة اليوم")
                  scope.launch {
                    snackbarHostState.showSnackbar(
                      "تم إرسال إشعار وتنبيه غياب فوري لولي أمر الطالب (${student.name}) عبر FCM"
                    )
                  }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.NotificationsActive,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "إرسال تنبيه غياب تلقائي لولي الأمر عبر FCM 📲",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }
}
