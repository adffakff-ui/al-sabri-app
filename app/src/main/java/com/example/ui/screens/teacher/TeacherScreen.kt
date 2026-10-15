package com.example.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.RecitationSchedule
import com.example.data.model.Student
import com.example.ui.components.AddStudentDialog
import com.example.ui.components.AttendanceDialog
import com.example.ui.components.HeroQuranBanner
import com.example.ui.components.RecitationEvaluationDialog
import com.example.ui.components.ScheduleRecitationDialog
import com.example.ui.components.StatCard
import com.example.ui.components.StudentCard
import com.example.ui.components.StudentProgressChartsCard
import com.example.ui.screens.attendance.AttendanceReportsScreen
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.ui.theme.QuranSuccess
import com.example.ui.theme.QuranSuccessLight
import com.example.util.ShareHelper
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun TeacherScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  val students by viewModel.scopedStudents.collectAsStateWithLifecycle()
  val attendance by viewModel.attendance.collectAsStateWithLifecycle()
  val recitations by viewModel.recitations.collectAsStateWithLifecycle()
  val schedules by viewModel.schedules.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedGroup by viewModel.selectedGroup.collectAsStateWithLifecycle()

  var showAddDialog by remember { mutableStateOf(false) }
  var showScheduleDialog by remember { mutableStateOf(false) }
  var showEvaluationForm by remember { mutableStateOf(false) }
  var showAttendanceReports by remember { mutableStateOf(false) }
  var showProgressCharts by remember { mutableStateOf(false) }
  var studentForRecitation by remember { mutableStateOf<Student?>(null) }
  var studentForAttendance by remember { mutableStateOf<Student?>(null) }
  var studentToEdit by remember { mutableStateOf<Student?>(null) }
  var studentForAiEvaluation by remember { mutableStateOf<Student?>(null) }

  if (showAttendanceReports) {
    AttendanceReportsScreen(
      viewModel = viewModel,
      onBack = { showAttendanceReports = false }
    )
    return
  }

  val groups = listOf("الكل", "حلقة عثمان بن عفان", "حلقة علي بن أبي طالب", "حلقة أبي بكر الصديق")

  // Filter students
  val filteredStudents = students.filter { s ->
    val matchesSearch = s.name.contains(searchQuery, ignoreCase = true) ||
        s.currentSurah.contains(searchQuery, ignoreCase = true) ||
        s.phone.contains(searchQuery)
    val matchesGroup = selectedGroup == null || selectedGroup == "الكل" || s.groupName == selectedGroup
    matchesSearch && matchesGroup
  }

  val todayAttendanceCount = attendance.count { it.status == "حاضر" }
  val todayRecitationsCount = recitations.size

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = QuranEmeraldPrimary,
        contentColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("add_student_fab")
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = "طالب جديد", fontWeight = FontWeight.Bold)
      }
    },
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Hero Banner
      item {
        HeroQuranBanner(
          title = "لوحة المعلم والمحفظ",
          subtitle = "﴿ وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا ﴾ • إدارة الحلقة اليومية"
        )
      }

      // Quick Stats Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatCard(
            title = "إجمالي الطلاب",
            value = "${students.size}",
            icon = Icons.Default.People,
            tintColor = QuranEmeraldPrimary,
            bgColor = QuranEmeraldLight,
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "حضور اليوم",
            value = "$todayAttendanceCount",
            icon = Icons.Default.CheckCircle,
            tintColor = QuranSuccess,
            bgColor = QuranSuccessLight,
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "جلسات التسميع",
            value = "$todayRecitationsCount",
            icon = Icons.Default.MenuBook,
            tintColor = QuranGoldSecondary,
            bgColor = QuranGoldLight,
            modifier = Modifier.weight(1f)
          )
        }
      }

      // Quick Recitation Evaluation Action Banner
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showEvaluationForm = true }
            .testTag("evaluation_form_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = QuranEmeraldLight),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(QuranEmeraldPrimary),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Grade,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "نموذج تسجيل تقييم التسميع والتسجيل الصوتي",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = QuranEmeraldDark,
                  fontSize = 14.sp
                )
                Text(
                  text = "تحديد السورة، الآيات، درجة التقييم، وربط القراءات العشر",
                  style = MaterialTheme.typography.bodySmall,
                  color = QuranEmeraldPrimary,
                  fontSize = 11.sp
                )
              }
            }
            Button(
              onClick = { showEvaluationForm = true },
              colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("بدء التقييم", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }

      // Recitation Scheduling Banner & FCM Auto Alerts
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showScheduleDialog = true }
            .testTag("schedule_action_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = QuranGoldLight),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(QuranGoldSecondary),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Campaign,
                  contentDescription = null,
                  tint = Color(0xFF382900),
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "جدولة مواعيد التسميع وتنبيهات FCM",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF4A3700),
                  fontSize = 14.sp
                )
                Text(
                  text = "تحديد الموعد وإرسال إشعار فوري تلقائي للطالب وولي أمره",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color(0xFF6B4E00),
                  fontSize = 11.sp
                )
              }
            }
            Button(
              onClick = { showScheduleDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("جدولة موعد 📅", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
          }
        }
      }

      // Attendance Reports & FCM Absence Alerts Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showAttendanceReports = true }
            .testTag("attendance_reports_action_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF16A34A)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.FactCheck,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "تقارير الحضور والغياب وتنبيهات FCM",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF14532D),
                  fontSize = 14.sp
                )
                Text(
                  text = "تصفية حسب التاريخ والحلقة وإرسال تنبيهات غياب فورية للأولياء",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color(0xFF15803D),
                  fontSize = 11.sp
                )
              }
            }
            Button(
              onClick = { showAttendanceReports = true },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("عرض التقرير 📋", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
          }
        }
      }

      // Student Progress Charts Card (Recharts style)
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Equalizer, contentDescription = null, tint = QuranEmeraldPrimary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "الرسوم البيانية لمستوى تقدم الطلاب (Recharts)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = QuranEmeraldDark
              )
            }
            Button(
              onClick = { showProgressCharts = !showProgressCharts },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (showProgressCharts) QuranGoldSecondary else QuranEmeraldPrimary
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.height(32.dp)
            ) {
              Text(if (showProgressCharts) "إخفاء الرسم" else "عرض الرسم 📊", fontSize = 11.sp)
            }
          }

          if (showProgressCharts) {
            Spacer(modifier = Modifier.height(8.dp))
            StudentProgressChartsCard(student = filteredStudents.firstOrNull())
          }
        }
      }

      // Upcoming Scheduled Appointments
      if (schedules.isNotEmpty()) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "المواعيد المجدولة عبر FCM (${schedules.size})",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
          }
        }

        items(schedules.take(3), key = { it.id }) { schedule ->
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(text = schedule.studentName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  Text(
                    text = "${schedule.groupName} • ${schedule.scheduledDate} (${schedule.scheduledTime})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(QuranEmeraldLight)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = schedule.status,
                    color = QuranEmeraldDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                }
              }

              Text(
                text = "المقرر: سورة ${schedule.surahName} (الآيات ${schedule.fromAyah}-${schedule.toAyah}) • ${schedule.recitationType}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = QuranEmeraldPrimary
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = QuranGoldSecondary,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "تم إرسال إشعار تلقائي عبر FCM",
                    fontSize = 10.sp,
                    color = Color(0xFF6B4E00),
                    fontWeight = FontWeight.Bold
                  )
                }

                Button(
                  onClick = {
                    val st = students.firstOrNull { it.id == schedule.studentId }
                    studentForRecitation = st
                    showEvaluationForm = true
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.height(32.dp)
                ) {
                  Text("بدء التسميع", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }

      // Search & Filters Header
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("ابحث عن اسم الطالب أو السورة...") },
            leadingIcon = {
              Icon(imageVector = Icons.Default.Search, contentDescription = null)
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("teacher_search_field"),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = QuranEmeraldPrimary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
          )

          // Group Filter Chips
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(groups) { group ->
              val isSelected = (selectedGroup == null && group == "الكل") || selectedGroup == group
              FilterChip(
                selected = isSelected,
                onClick = {
                  viewModel.setSelectedGroup(if (group == "الكل") null else group)
                },
                label = { Text(group, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = QuranEmeraldPrimary,
                  selectedLabelColor = Color.White
                )
              )
            }
          }
        }
      }

      // AI Student Evaluation Chatbot Banner for Teachers
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFBF8EE)
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, QuranGoldSecondary.copy(alpha = 0.5f)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              val target = filteredStudents.firstOrNull() ?: students.firstOrNull()
              studentForAiEvaluation = target
            }
            .testTag("ai_student_evaluation_banner")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(QuranGoldSecondary),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = Color(0xFF382900),
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "دردشة الذكاء الاصطناعي لتقييم الطلاب 🤖",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF4A3700)
                )
                Text(
                  text = "تحليل فوري لأداء الطالب وسجل تسميعه الصوتي وتقديم توصيات للمعلم",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color(0xFF785600),
                  fontSize = 11.sp
                )
              }
            }
            Button(
              onClick = {
                val target = filteredStudents.firstOrNull() ?: students.firstOrNull()
                studentForAiEvaluation = target
              },
              colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.height(34.dp)
            ) {
              Text("تقييم ذكي ⚡", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // Students List Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "قائمة الطلاب (${filteredStudents.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
        }
      }

      // Empty State
      if (filteredStudents.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.People,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "لا توجد نتائج مطابقة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "جرب تغيير مصطلح البحث أو أضف طالباً جديداً",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        items(filteredStudents, key = { it.id }) { student ->
          StudentCard(
            student = student,
            onReciteClick = { studentForRecitation = student },
            onAttendanceClick = { studentForAttendance = student },
            onCardClick = {
              viewModel.selectStudent(student)
              scope.launch {
                snackbarHostState.showSnackbar("تم تحديد الطالب: ${student.name}")
              }
            },
            onAiEvaluateClick = { studentForAiEvaluation = student }
          )
        }
      }

      // Bottom Spacer for FAB
      item {
        Spacer(modifier = Modifier.height(60.dp))
      }
    }
  }

  // Add / Edit Student Dialog
  if (showAddDialog) {
    AddStudentDialog(
      onDismiss = { showAddDialog = false },
      onSave = { newStudent ->
        viewModel.addStudent(newStudent)
        showAddDialog = false
        scope.launch {
          snackbarHostState.showSnackbar("تمت إضافة الطالب ${newStudent.name} بنجاح")
        }
      }
    )
  }

  // Schedule Recitation Dialog
  if (showScheduleDialog) {
    ScheduleRecitationDialog(
      students = students,
      onDismiss = { showScheduleDialog = false },
      onScheduleCreated = { newSchedule, sendFcm ->
        viewModel.scheduleRecitation(newSchedule, sendFcm)
        showScheduleDialog = false
        scope.launch {
          snackbarHostState.showSnackbar("تمت جدولة موعد ${newSchedule.studentName} وإرسال إشعار FCM بنجاح!")
        }
      }
    )
  }

  // Recitation Evaluation Dialog
  if (showEvaluationForm || studentForRecitation != null) {
    RecitationEvaluationDialog(
      initialStudent = studentForRecitation,
      studentsList = students,
      onDismiss = {
        showEvaluationForm = false
        studentForRecitation = null
      },
      onSave = { record ->
        viewModel.recordRecitation(record)
        showEvaluationForm = false
        studentForRecitation = null
        scope.launch {
          snackbarHostState.showSnackbar("تم حفظ تقييم سورة ${record.surahName} بنجاح (${record.evaluation})")
        }
      }
    )
  }

  // Attendance Dialog
  studentForAttendance?.let { student ->
    AttendanceDialog(
      student = student,
      onDismiss = { studentForAttendance = null },
      onSave = { record ->
        viewModel.recordAttendance(record)
        studentForAttendance = null
        scope.launch {
          snackbarHostState.showSnackbar("تم تسجيل حضور الطالب ${record.studentName} (${record.status})")
        }
      }
    )
  }

  // Student AI Evaluation Chatbot Dialog
  studentForAiEvaluation?.let { st ->
    com.example.ui.components.StudentAiEvaluationDialog(
      student = st,
      recitations = recitations,
      attendance = attendance,
      onDismiss = { studentForAiEvaluation = null }
    )
  }
}
