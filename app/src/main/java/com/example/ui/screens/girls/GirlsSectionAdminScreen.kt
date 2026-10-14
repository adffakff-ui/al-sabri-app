package com.example.ui.screens.girls

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.data.model.GirlsVerificationResult
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.SampleDataProvider
import com.example.data.model.AttendanceRecord
import com.example.data.model.HalaqahSection
import com.example.data.model.QuranHalaqah
import com.example.data.model.RecitationRecord
import com.example.data.model.Student
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.UserRole

// Deep aesthetic colors tailored for Girls Section (Dignified Islamic Emerald with Rose/Blush & Gold accents)
val GirlsRosePrimary = Color(0xFFBE185D) // Deep rose
val GirlsRoseLight = Color(0xFFFDF2F8)   // Very soft blush
val GirlsRoseBorder = Color(0xFFFBCFE8)  // Light rose border
val GirlsRoseDark = Color(0xFF831843)    // Deep wine rose
val GirlsAccentGold = Color(0xFFC99700)   // Warm gold

@Composable
fun GirlsSectionAdminScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val currentAccessCode by viewModel.currentAccessCode.collectAsStateWithLifecycle()
  val authenticatedRole by viewModel.authenticatedRole.collectAsStateWithLifecycle()
  val girlsVerification by viewModel.girlsVerificationResult.collectAsStateWithLifecycle()

  // Gatekeeper check: is user already authenticated with SUP-GIRLS-200 or as GIRLS_ADMIN or DEVELOPER?
  val isInitiallyUnlocked = girlsVerification is GirlsVerificationResult.Success ||
      currentAccessCode?.code == "SUP-GIRLS-200" ||
      currentAccessCode?.section == HalaqahSection.GIRLS ||
      authenticatedRole == UserRole.GIRLS_ADMIN ||
      authenticatedRole == UserRole.DEVELOPER

  var isUnlocked by remember(isInitiallyUnlocked) { mutableStateOf(isInitiallyUnlocked) }

  if (!isUnlocked) {
    GirlsAdminGatekeeperScreen(
      viewModel = viewModel,
      onUnlockSuccess = { isUnlocked = true }
    )
  } else {
    GirlsAdminMainDashboard(
      viewModel = viewModel,
      modifier = modifier,
      onLockClick = {
        viewModel.logoutGirlsAdmin()
        isUnlocked = false
      }
    )
  }
}

/**
 * شاشة الدخول الإداري المقفلة بكود دخول المشرفة مع التحقق الصارم من Firestore
 */
@Composable
private fun GirlsAdminGatekeeperScreen(
  viewModel: MainViewModel,
  onUnlockSuccess: () -> Unit
) {
  val scope = rememberCoroutineScope()
  var enteredCode by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isBoySectionViolation by remember { mutableStateOf(false) }
  var isVerifying by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            QuranEmeraldDark,
            Color(0xFF831843),
            Color(0xFF2C0B1B)
          )
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Icon badge
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(GirlsRoseLight),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = GirlsRosePrimary,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "بوابة إدارة حلقات البنات",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = GirlsRoseDark,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "القطاع النسائي • حلقات هائل سعيد أنعم",
          style = MaterialTheme.typography.bodyMedium,
          color = GirlsAccentGold,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Badge: Firestore Security Isolated
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFECFDF5))
            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.VerifiedUser,
              contentDescription = null,
              tint = Color(0xFF059669),
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "حماية Firestore نشطة • عزل بيانات تام",
              color = Color(0xFF065F46),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Privacy note
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = GirlsRosePrimary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "هذه اللوحة مخصصة لمشرفات ومعلمات قطاع البنات فقط. يتم التحقق عبر Firestore مع حظر تام لأي وصول لبيانات البنين.",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF9F1239),
              lineHeight = 18.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
          value = enteredCode,
          onValueChange = {
            enteredCode = it
            errorMessage = null
            isBoySectionViolation = false
          },
          label = { Text("أدخلي كود المشرفة أو كود المعلمة") },
          placeholder = { Text("مثال: SUP-GIRLS-200 أو TCH-KHADIJA-33") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Key,
              contentDescription = null,
              tint = GirlsRosePrimary
            )
          },
          singleLine = true,
          enabled = !isVerifying,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("girls_gate_code_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GirlsRosePrimary,
            focusedLabelColor = GirlsRosePrimary,
            cursorColor = GirlsRosePrimary
          )
        )

        AnimatedVisibility(visible = errorMessage != null) {
          if (isBoySectionViolation) {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
              shape = RoundedCornerShape(10.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Shield,
                  contentDescription = null,
                  tint = Color(0xFFDC2626),
                  modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = errorMessage ?: "",
                  color = Color(0xFF991B1B),
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          } else {
            Text(
              text = errorMessage ?: "",
              color = MaterialTheme.colorScheme.error,
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(top = 8.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            scope.launch {
              isVerifying = true
              errorMessage = null
              val (success, message) = viewModel.verifyAndAuthorizeGirlsAccessAsync(enteredCode)
              isVerifying = false
              if (success) {
                onUnlockSuccess()
              } else {
                errorMessage = message
                isBoySectionViolation = message.contains("البنين")
              }
            }
          },
          enabled = !isVerifying && enteredCode.isNotBlank(),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("girls_gate_unlock_btn"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GirlsRosePrimary)
        ) {
          if (isVerifying) {
            CircularProgressIndicator(
              modifier = Modifier.size(22.dp),
              color = Color.White,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "جاري التحقق عبر Firestore...", fontWeight = FontWeight.Bold)
          } else {
            Icon(imageVector = Icons.Default.LockOpen, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "التحقق وفتح اللوحة الإدارية",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Divider(color = Color(0xFFF1F5F9))

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "نماذج الاختبار السريع والتحقق من الصلاحيات:",
          style = MaterialTheme.typography.labelMedium,
          color = Color.Gray,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Demo 1: Full supervisor
        OutlinedButton(
          onClick = {
            enteredCode = "SUP-GIRLS-200"
            scope.launch {
              isVerifying = true
              errorMessage = null
              val (success, message) = viewModel.verifyAndAuthorizeGirlsAccessAsync("SUP-GIRLS-200")
              isVerifying = false
              if (success) onUnlockSuccess() else errorMessage = message
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("girls_gate_quick_demo_btn"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = GirlsRoseDark)
        ) {
          Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            tint = GirlsAccentGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "مشرفة قطاع البنات (SUP-GIRLS-200) • إشراف شامل",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Demo 2: Teacher scope
        OutlinedButton(
          onClick = {
            enteredCode = "TCH-KHADIJA-33"
            scope.launch {
              isVerifying = true
              errorMessage = null
              val (success, message) = viewModel.verifyAndAuthorizeGirlsAccessAsync("TCH-KHADIJA-33")
              isVerifying = false
              if (success) onUnlockSuccess() else errorMessage = message
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("demo_teacher_btn"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F5132))
        ) {
          Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            tint = Color(0xFF0F5132),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "معلمة حلقة خديجة (TCH-KHADIJA-33) • نطاق مقيد",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Demo 3: Blocked Boys code test
        OutlinedButton(
          onClick = {
            enteredCode = "SUP-BOYS-100"
            scope.launch {
              isVerifying = true
              errorMessage = null
              val (success, message) = viewModel.verifyAndAuthorizeGirlsAccessAsync("SUP-BOYS-100")
              isVerifying = false
              if (success) onUnlockSuccess() else {
                errorMessage = message
                isBoySectionViolation = true
              }
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("demo_boys_block_test_btn"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
        ) {
          Icon(
            imageVector = Icons.Default.Block,
            contentDescription = null,
            tint = Color(0xFFDC2626),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "فحص الحظر: كود مشرف البنين (SUP-BOYS-100) 🚫",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}

/**
 * لوحة القيادة المتكاملة لإدارة قسم حلقات البنات
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GirlsAdminMainDashboard(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier,
  onLockClick: () -> Unit = {}
) {
  val context = LocalContext.current

  // Strictly isolated girls data
  val allGirlsHalaqat by viewModel.allGirlsHalaqat.collectAsStateWithLifecycle()
  val allStudents by viewModel.students.collectAsStateWithLifecycle()
  val allAttendance by viewModel.attendance.collectAsStateWithLifecycle()
  val allRecitations by viewModel.recitations.collectAsStateWithLifecycle()

  // Dynamic scoping & verified identity from Firestore
  val isTeacherScopeOnly by viewModel.isTeacherScopeOnly.collectAsStateWithLifecycle()
  val assignedTeacherHalaqahName by viewModel.assignedTeacherHalaqahName.collectAsStateWithLifecycle()
  val girlsVerification by viewModel.girlsVerificationResult.collectAsStateWithLifecycle()
  val currentAccessCode by viewModel.currentAccessCode.collectAsStateWithLifecycle()

  // Filtered strictly to female students
  val girlsStudents = remember(allStudents, isTeacherScopeOnly, assignedTeacherHalaqahName) {
    val base = allStudents.filter { it.isGirlsSection }
    if (isTeacherScopeOnly && assignedTeacherHalaqahName != null) {
      base.filter { it.groupName.contains(assignedTeacherHalaqahName!!) || assignedTeacherHalaqahName!!.contains(it.groupName) }
    } else {
      base
    }
  }
  val girlsStudentIds = remember(girlsStudents) {
    girlsStudents.map { it.id }.toSet()
  }
  val girlsAttendance = remember(allAttendance, girlsStudentIds) {
    allAttendance.filter { it.studentId in girlsStudentIds }
  }
  val girlsRecitations = remember(allRecitations, girlsStudentIds) {
    allRecitations.filter { it.studentId in girlsStudentIds }
  }

  var selectedTab by remember { mutableIntStateOf(0) }

  // Dialog states
  var showAddHalaqahDialog by remember { mutableStateOf(false) }
  var showAddStudentDialog by remember { mutableStateOf(false) }
  var showRecordRecitationDialog by remember { mutableStateOf(false) }
  var studentForRecitation by remember { mutableStateOf<Student?>(null) }
  var halaqahForViewDetails by remember { mutableStateOf<QuranHalaqah?>(null) }

  // Search & filter states
  var studentSearchQuery by remember { mutableStateOf("") }
  var selectedHalaqahFilter by remember { mutableStateOf("الكل") }

  val supervisorSubtitle = when (val res = girlsVerification) {
    is GirlsVerificationResult.Success -> "${res.name} • ${res.roleTitle}"
    else -> currentAccessCode?.userName ?: "الشيخة أم عبد الله الأنصاري • مشرفة القطاع النسائي"
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFFAF7F5))
  ) {
    // Top Islamic Header tailored for Girls Sector
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          brush = Brush.horizontalGradient(
            colors = listOf(
              Color(0xFF0F5132),
              Color(0xFF831843),
              Color(0xFFBE185D)
            )
          )
        )
        .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🧕", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "لوحة إدارة حلقات البنات",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 17.sp
              )
              Text(
                text = supervisorSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = GirlsAccentGold,
                fontSize = 12.sp
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // Strict Isolation Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.18f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = null,
                  tint = GirlsAccentGold,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "بيانات معزولة",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Lock / Exit button
            IconButton(
              onClick = onLockClick,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
                .testTag("girls_lock_session_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "قفل اللوحة والخروج",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }

    // Teacher Scope Warning Banner if logged in as a single halaqah teacher
    if (isTeacherScopeOnly) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            tint = Color(0xFF15803D),
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "نطاق خصوصية الحلقة مفعل: الاطلاع محصور على طالبات ${assignedTeacherHalaqahName ?: "حلقتك المسندة"} فقط، وسائر حلقات البنات وقسم البنين محجوبة تماماً.",
            color = Color(0xFF166534),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // 4 Functional Tabs
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = Color.White,
      contentColor = GirlsRosePrimary,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = GirlsRosePrimary,
          height = 3.dp
        )
      }
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Text(
            text = "حلقات البنات 🌸",
            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
          )
        }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Text(
            text = "شؤون الطالبات 👧",
            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
          )
        }
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = {
          Text(
            text = "رصد التسميع 📖",
            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
          )
        }
      )
      Tab(
        selected = selectedTab == 3,
        onClick = { selectedTab = 3 },
        text = {
          Text(
            text = "الحضور والغياب ⏱️",
            fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
          )
        }
      )
    }

    // Tab content
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      when (selectedTab) {
        0 -> GirlsHalaqatManagementTab(
          halaqat = allGirlsHalaqat,
          canManageHalaqat = !isTeacherScopeOnly,
          onAddNewHalaqahClick = { showAddHalaqahDialog = true },
          onViewHalaqahDetails = { halaqahForViewDetails = it },
          onDeleteHalaqah = { viewModel.deleteGirlsHalaqah(it.id) }
        )
        1 -> GirlsStudentsManagementTab(
          students = girlsStudents,
          allHalaqat = allGirlsHalaqat,
          searchQuery = studentSearchQuery,
          onSearchQueryChange = { studentSearchQuery = it },
          selectedHalaqah = selectedHalaqahFilter,
          onHalaqahFilterChange = { selectedHalaqahFilter = it },
          onAddNewStudentClick = { showAddStudentDialog = true },
          onRecordRecitationClick = {
            studentForRecitation = it
            showRecordRecitationDialog = true
          },
          onDeleteStudent = { viewModel.deleteStudent(it) }
        )
        2 -> GirlsRecitationTrackingTab(
          students = girlsStudents,
          recitations = girlsRecitations,
          onOpenRecordDialog = {
            studentForRecitation = girlsStudents.firstOrNull()
            showRecordRecitationDialog = true
          }
        )
        3 -> GirlsAttendanceTrackingTab(
          students = girlsStudents,
          attendanceRecords = girlsAttendance,
          onRecordAttendance = { record -> viewModel.recordAttendance(record) },
          onSendAbsenceAlert = { student, reason ->
            viewModel.sendAbsenceNotification(student, SampleDataProvider.todayDate, reason)
          }
        )
      }
    }
  }

  // Dialog: Add New Girls Halaqah
  if (showAddHalaqahDialog) {
    AddGirlsHalaqahDialog(
      onDismiss = { showAddHalaqahDialog = false },
      onConfirm = { newHalaqah ->
        viewModel.addGirlsHalaqah(newHalaqah)
        showAddHalaqahDialog = false
      }
    )
  }

  // Dialog: Add New Girls Student
  if (showAddStudentDialog) {
    AddGirlsStudentDialog(
      halaqatList = allGirlsHalaqat,
      onDismiss = { showAddStudentDialog = false },
      onConfirm = { newStudent ->
        viewModel.enrollGirlsStudent(newStudent)
        showAddStudentDialog = false
      }
    )
  }

  // Dialog: Record Daily Recitation for a Girl
  if (showRecordRecitationDialog && studentForRecitation != null) {
    RecordGirlsRecitationDialog(
      student = studentForRecitation!!,
      onDismiss = {
        showRecordRecitationDialog = false
        studentForRecitation = null
      },
      onConfirm = { record ->
        viewModel.recordRecitation(record)
        showRecordRecitationDialog = false
        studentForRecitation = null
      }
    )
  }

  // Dialog: View Halaqah Details
  halaqahForViewDetails?.let { halaqah ->
    GirlsHalaqahDetailsDialog(
      halaqah = halaqah,
      onDismiss = { halaqahForViewDetails = null }
    )
  }
}

// =========================================================================
// Tab 1: إدارة حلقات البنات والمحفظات
// =========================================================================
@Composable
private fun GirlsHalaqatManagementTab(
  halaqat: List<QuranHalaqah>,
  canManageHalaqat: Boolean = true,
  onAddNewHalaqahClick: () -> Unit,
  onViewHalaqahDetails: (QuranHalaqah) -> Unit,
  onDeleteHalaqah: (QuranHalaqah) -> Unit
) {
  val totalCapacity = halaqat.sumOf { it.capacity }
  val totalStudents = halaqat.sumOf { it.studentCount }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      // Summary Cards Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        GirlsStatCard(
          title = "الحلقات القائمة",
          value = "${halaqat.size}",
          icon = Icons.Default.Groups,
          color = GirlsRosePrimary,
          modifier = Modifier.weight(1f)
        )
        GirlsStatCard(
          title = "المحفظات المجازات",
          value = "${halaqat.map { it.teacherName }.distinct().size}",
          icon = Icons.Default.School,
          color = Color(0xFF0F5132),
          modifier = Modifier.weight(1f)
        )
        GirlsStatCard(
          title = "الطالبات المسجلات",
          value = "$totalStudents",
          icon = Icons.Default.Female,
          color = GirlsAccentGold,
          modifier = Modifier.weight(1f)
        )
      }
    }

    item {
      if (canManageHalaqat) {
        // Add Circle Action Button
        Button(
          onClick = onAddNewHalaqahClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("add_girls_halaqah_btn"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GirlsRosePrimary)
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "إضافة حلقة جديدة وتعيين محفظة معتمدة بالسند",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      } else {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = Color(0xFFB45309),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "صلاحية إدارة وتعيين الحلقات مخصصة لمشرفة قطاع البنات. تقتصر صلاحيتك كمعلمة على إدارة طالبات وتسميع حلقتك المسندة.",
              color = Color(0xFF92400E),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "قائمة حلقات القطاع النسائي (${halaqat.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = GirlsRoseDark
        )
        Text(
          text = "جناح مريم النسائي",
          style = MaterialTheme.typography.bodySmall,
          color = GirlsAccentGold,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    items(halaqat) { halaqah ->
      GirlsHalaqahCard(
        halaqah = halaqah,
        onDetailsClick = { onViewHalaqahDetails(halaqah) },
        onDeleteClick = { onDeleteHalaqah(halaqah) }
      )
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun GirlsHalaqahCard(
  halaqah: QuranHalaqah,
  onDetailsClick: () -> Unit,
  onDeleteClick: () -> Unit
) {
  val context = LocalContext.current

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onDetailsClick)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = halaqah.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = GirlsRoseDark
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = GirlsRosePrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "المحفظة: ${halaqah.teacherName}",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF1F2937)
            )
          }
        }

        // Section badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GirlsRoseLight)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "حلقة بنات 🌸",
            color = GirlsRosePrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Sanad / Chain details (as requested by user)
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            tint = GirlsAccentGold,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "سند القراءة: ${halaqah.teacherSanad.ifBlank { halaqah.teacherTitle }}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF92400E)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Details row: Phone & Recitation Days
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = Color(0xFF6B7280),
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = halaqah.recitationDays.ifBlank { halaqah.days },
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF4B5563)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = Color(0xFF6B7280),
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = halaqah.timeSlot,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF4B5563)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Student Capacity Indicator & Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "الطالبات: ${halaqah.studentCount} / ${halaqah.capacity}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF374151)
          )
          Spacer(modifier = Modifier.height(4.dp))
          val progress = (halaqah.studentCount.toFloat() / halaqah.capacity.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
          LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
              .width(140.dp)
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = GirlsRosePrimary,
            trackColor = GirlsRoseBorder
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Direct Call Muhaffiza
          IconButton(
            onClick = {
              val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${halaqah.teacherPhone}")
              }
              context.startActivity(intent)
            },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(GirlsRoseLight)
          ) {
            Icon(
              imageVector = Icons.Default.Call,
              contentDescription = "اتصال بالمحفظة",
              tint = GirlsRosePrimary,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          // View Details
          TextButton(
            onClick = onDetailsClick,
            colors = ButtonDefaults.textButtonColors(contentColor = GirlsRoseDark)
          ) {
            Text("التفاصيل 🔍", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

// =========================================================================
// Tab 2: شؤون الطالبات وأولياء الأمور
// =========================================================================
@Composable
private fun GirlsStudentsManagementTab(
  students: List<Student>,
  allHalaqat: List<QuranHalaqah>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  selectedHalaqah: String,
  onHalaqahFilterChange: (String) -> Unit,
  onAddNewStudentClick: () -> Unit,
  onRecordRecitationClick: (Student) -> Unit,
  onDeleteStudent: (Student) -> Unit
) {
  val context = LocalContext.current

  val halaqahNames = listOf("الكل") + allHalaqat.map { it.name }

  val filteredStudents = students.filter { student ->
    val matchesSearch = student.name.contains(searchQuery, ignoreCase = true) ||
        student.parentName.contains(searchQuery, ignoreCase = true) ||
        student.parentPhone.contains(searchQuery)
    val matchesHalaqah = selectedHalaqah == "الكل" || student.groupName.contains(selectedHalaqah) || selectedHalaqah.contains(student.groupName)
    matchesSearch && matchesHalaqah
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      // Action Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "سجل طالبات قطاع البنات (${filteredStudents.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = GirlsRoseDark
        )

        Button(
          onClick = onAddNewStudentClick,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GirlsRosePrimary),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
          modifier = Modifier.testTag("add_girls_student_btn")
        ) {
          Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("تسجيل طالبة جديدة", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    item {
      // Search Box
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("بحث باسم الطالبة أو ولي الأمر أو الهاتف...") },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = GirlsRosePrimary
          )
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("girls_student_search_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = GirlsRosePrimary,
          cursorColor = GirlsRosePrimary
        )
      )
    }

    item {
      // Filter Chips by Halaqah
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
      ) {
        items(halaqahNames) { halaqah ->
          val isSelected = selectedHalaqah == halaqah
          FilterChip(
            selected = isSelected,
            onClick = { onHalaqahFilterChange(halaqah) },
            label = { Text(halaqah, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = GirlsRosePrimary,
              selectedLabelColor = Color.White
            )
          )
        }
      }
    }

    if (filteredStudents.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "🌸", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "لا توجد نتائج مطابقة في سجل طالبات البنات",
              fontWeight = FontWeight.Bold,
              color = Color(0xFF6B7280)
            )
          }
        }
      }
    } else {
      items(filteredStudents) { student ->
        GirlsStudentCard(
          student = student,
          onRecordRecitationClick = { onRecordRecitationClick(student) },
          onDeleteClick = { onDeleteStudent(student) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun GirlsStudentCard(
  student: Student,
  onRecordRecitationClick: () -> Unit,
  onDeleteClick: () -> Unit
) {
  val context = LocalContext.current

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(GirlsRoseLight),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "🌸", fontSize = 22.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = student.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = GirlsRoseDark
            )
            Text(
              text = student.groupName,
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF4B5563)
            )
          }
        }

        // Level badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GirlsRoseLight)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = student.level,
            color = GirlsRosePrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Guardian Linking Card (Essential requirement from user)
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.FamilyRestroom,
              contentDescription = null,
              tint = Color(0xFF4B5563),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "ولي الأمر: ${student.parentName.ifBlank { "غير مسجل" }}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F2937)
              )
              Text(
                text = "هاتف: ${student.parentPhone.ifBlank { student.phone }}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6B7280)
              )
            }
          }

          if (student.parentPhone.isNotBlank() || student.phone.isNotBlank()) {
            IconButton(
              onClick = {
                val targetPhone = student.parentPhone.ifBlank { student.phone }
                val intent = Intent(Intent.ACTION_DIAL).apply {
                  data = Uri.parse("tel:$targetPhone")
                }
                context.startActivity(intent)
              },
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFE5E7EB))
            ) {
              Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "اتصال بولي الأمر",
                tint = Color(0xFF1F2937),
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Current Surah & Memorization
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Bookmark,
            contentDescription = null,
            tint = GirlsAccentGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "المقرر: سورة ${student.currentSurah} (آية ${student.currentAyah})",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151)
          )
        }

        Text(
          text = "${student.memorizedJuzCount} أجزاء محفوظة",
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Bold,
          color = GirlsRosePrimary
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action row: Record Recitation & Delete
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(
          onClick = onRecordRecitationClick,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GirlsRosePrimary),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("رصد التسميع والتقييم ⭐", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        IconButton(
          onClick = onDeleteClick,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "حذف الطالبة",
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

// =========================================================================
// Tab 3: رصد التسميع اليومي والمتابعة
// =========================================================================
@Composable
private fun GirlsRecitationTrackingTab(
  students: List<Student>,
  recitations: List<RecitationRecord>,
  onOpenRecordDialog: () -> Unit
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "سجل التسميع والتقييم اليومي (بنات)",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = GirlsRoseDark
        )

        Button(
          onClick = onOpenRecordDialog,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GirlsRosePrimary),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("رصد تسميع جديد", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    if (recitations.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "📖", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "لم يتم رصد تسميعات اليوم في قطاع البنات بعد",
              fontWeight = FontWeight.Bold,
              color = Color(0xFF6B7280)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "اضغطي على 'رصد تسميع جديد' لتسجيل مقاطع الحفظ والمراجعة",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF9CA3AF)
            )
          }
        }
      }
    } else {
      items(recitations) { record ->
        GirlsRecitationCard(record = record)
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun GirlsRecitationCard(record: RecitationRecord) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "🌸", fontSize = 18.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = record.studentName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = GirlsRoseDark
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFECFDF5))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = record.evaluation,
            color = Color(0xFF047857),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "سورة ${record.surahName} (الآيات ${record.fromAyah}-${record.toAyah})",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF1F2937)
        )
        Text(
          text = record.recitationType,
          style = MaterialTheme.typography.bodySmall,
          color = GirlsRosePrimary,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "درجة التجويد: ${record.tajweedScore}/10 | الأخطاء: ${record.mistakesCount}",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF6B7280)
        )
        Text(
          text = record.date,
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF9CA3AF)
        )
      }

      if (record.teacherNotes.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "ملاحظة المحفظة: ${record.teacherNotes}",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF4B5563)
        )
      }
    }
  }
}

// =========================================================================
// Tab 4: رصد الحضور والغياب وإشعارات أولياء الأمور
// =========================================================================
@Composable
private fun GirlsAttendanceTrackingTab(
  students: List<Student>,
  attendanceRecords: List<AttendanceRecord>,
  onRecordAttendance: (AttendanceRecord) -> Unit,
  onSendAbsenceAlert: (Student, String) -> Unit
) {
  val today = SampleDataProvider.todayDate

  val todayRecordsMap = remember(attendanceRecords, today) {
    attendanceRecords.filter { it.date == today }.associateBy { it.studentId }
  }

  val presentCount = todayRecordsMap.values.count { it.status == "حاضر" }
  val absentCount = todayRecordsMap.values.count { it.status == "غائب" }

  var studentForAbsenceAlert by remember { mutableStateOf<Student?>(null) }
  var absenceReason by remember { mutableStateOf("") }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "تحضير طالبات البنات اليوم ($today)",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = GirlsRoseDark
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFECFDF5))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("حاضرات: $presentCount", color = Color(0xFF047857), fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFFEF2F2))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("غائبات: $absentCount", color = Color(0xFFB91C1C), fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    items(students) { student ->
      val record = todayRecordsMap[student.id]
      val currentStatus = record?.status ?: "غير محدد"

      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = student.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GirlsRoseDark
              )
              Text(
                text = "${student.groupName} • ولي الأمر: ${student.parentName.ifBlank { "غير مسجل" }}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6B7280)
              )
            }

            // Quick Status badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(
                  when (currentStatus) {
                    "حاضر" -> Color(0xFFECFDF5)
                    "غائب" -> Color(0xFFFEF2F2)
                    "مستأذن" -> Color(0xFFFFFBEB)
                    else -> Color(0xFFF3F4F6)
                  }
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = currentStatus,
                color = when (currentStatus) {
                  "حاضر" -> Color(0xFF047857)
                  "غائب" -> Color(0xFFB91C1C)
                  "مستأذن" -> Color(0xFFB45309)
                  else -> Color(0xFF4B5563)
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 4 Action Buttons: حاضر / غائب / مستأذن / متأخر
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            AttendanceButton(
              label = "حاضرة",
              icon = "✅",
              isSelected = currentStatus == "حاضر",
              activeColor = Color(0xFF047857),
              modifier = Modifier.weight(1f),
              onClick = {
                onRecordAttendance(
                  AttendanceRecord(
                    studentId = student.id,
                    studentName = student.name,
                    date = today,
                    status = "حاضر"
                  )
                )
              }
            )

            AttendanceButton(
              label = "غائبة",
              icon = "❌",
              isSelected = currentStatus == "غائب",
              activeColor = Color(0xFFB91C1C),
              modifier = Modifier.weight(1f),
              onClick = {
                studentForAbsenceAlert = student
                absenceReason = "غياب بدون عذر مسبق"
              }
            )

            AttendanceButton(
              label = "مستأذنة",
              icon = "📝",
              isSelected = currentStatus == "مستأذن",
              activeColor = Color(0xFFB45309),
              modifier = Modifier.weight(1f),
              onClick = {
                onRecordAttendance(
                  AttendanceRecord(
                    studentId = student.id,
                    studentName = student.name,
                    date = today,
                    status = "مستأذن"
                  )
                )
              }
            )

            AttendanceButton(
              label = "متأخرة",
              icon = "⏱️",
              isSelected = currentStatus == "متأخر",
              activeColor = Color(0xFF6B21A8),
              modifier = Modifier.weight(1f),
              onClick = {
                onRecordAttendance(
                  AttendanceRecord(
                    studentId = student.id,
                    studentName = student.name,
                    date = today,
                    status = "متأخر"
                  )
                )
              }
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Absence Alert Confirmation Dialog
  studentForAbsenceAlert?.let { student ->
    AlertDialog(
      onDismissRequest = { studentForAbsenceAlert = null },
      title = {
        Text("تأكيد رصد الغياب وإرسال تنبيه لولي الأمر", fontWeight = FontWeight.Bold, color = GirlsRoseDark)
      },
      text = {
        Column {
          Text("سيتم تسجيل غياب الطالبة (${student.name}) وإرسال إشعار فوري لولي الأمر (${student.parentName}) على الهاتف (${student.parentPhone.ifBlank { student.phone }}).")
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = absenceReason,
            onValueChange = { absenceReason = it },
            label = { Text("سبب الغياب (ملاحظة لولي الأمر)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onSendAbsenceAlert(student, absenceReason)
            studentForAbsenceAlert = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C))
        ) {
          Text("تسجيل الغياب وإرسال التنبيه")
        }
      },
      dismissButton = {
        TextButton(onClick = { studentForAbsenceAlert = null }) {
          Text("إلغاء")
        }
      }
    )
  }
}

@Composable
private fun AttendanceButton(
  label: String,
  icon: String,
  isSelected: Boolean,
  activeColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) activeColor else Color(0xFFF3F4F6))
      .clickable(onClick = onClick)
      .padding(vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(text = icon, fontSize = 11.sp)
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = label,
        color = if (isSelected) Color.White else Color(0xFF4B5563),
        fontSize = 11.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      )
    }
  }
}

// =========================================================================
// Dialogs: إضافة حلقة بنات / تسجيل طالبة / رصد تسميع / تفاصيل
// =========================================================================

/**
 * حوار إضافة حلقة بنات جديدة وتعيين محفظة
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddGirlsHalaqahDialog(
  onDismiss: () -> Unit,
  onConfirm: (QuranHalaqah) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var teacherName by remember { mutableStateOf("") }
  var teacherPhone by remember { mutableStateOf("") }
  var teacherSanad by remember { mutableStateOf("مجازة بالقراءات العشر الصغرى والكبرى") }
  var recitationDays by remember { mutableStateOf("الأحد إلى الخميس") }
  var timeSlot by remember { mutableStateOf("العصر (4:00 م - 5:45 م)") }
  var location by remember { mutableStateOf("مجمع هائل سعيد أنعم - جناح مريم النسائي (قاعة الصديقة)") }
  var capacity by remember { mutableStateOf("18") }
  var targetLevel by remember { mutableStateOf("حفظ متقدم وإتقان التجويد") }

  var validationError by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "🌸", fontSize = 22.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text("إنشاء حلقة جديدة وتعيين محفظة", fontWeight = FontWeight.Bold, color = GirlsRoseDark)
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("اسم الحلقة (بنات)") },
          placeholder = { Text("مثال: حلقة أم المؤمنين جويرية") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = teacherName,
          onValueChange = { teacherName = it },
          label = { Text("اسم المحفظة") },
          placeholder = { Text("مثال: الشيخة أسماء بنت عبد الرحمن") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = teacherPhone,
          onValueChange = { teacherPhone = it },
          label = { Text("رقم هاتف المحفظة") },
          placeholder = { Text("055xxxxxxx") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = teacherSanad,
          onValueChange = { teacherSanad = it },
          label = { Text("سند القراءة والإجازة") },
          placeholder = { Text("مثال: إجازة برواية حفص عن عاصم بالسند المتصل") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = recitationDays,
          onValueChange = { recitationDays = it },
          label = { Text("أيام التسميع") },
          placeholder = { Text("مثال: السبت والإثنين والأربعاء") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = timeSlot,
          onValueChange = { timeSlot = it },
          label = { Text("فترة التسميع") },
          placeholder = { Text("العصر (4:00 م - 5:45 م)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = location,
          onValueChange = { location = it },
          label = { Text("القاعة / الجناح") },
          placeholder = { Text("جناح مريم النسائي") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = capacity,
          onValueChange = { capacity = it },
          label = { Text("السعة الاستيعابية") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        validationError?.let {
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isBlank() || teacherName.isBlank()) {
            validationError = "يرجى كتابة اسم الحلقة واسم المحفظة"
            return@Button
          }
          val capInt = capacity.toIntOrNull() ?: 18
          val newHalaqah = QuranHalaqah(
            id = System.currentTimeMillis(),
            name = name.trim(),
            teacherName = teacherName.trim(),
            teacherTitle = teacherSanad.trim(),
            teacherPhone = teacherPhone.trim().ifBlank { "0550000000" },
            location = location.trim(),
            timeSlot = timeSlot.trim(),
            studentCount = 0,
            capacity = capInt,
            targetLevel = targetLevel.trim(),
            days = recitationDays.trim(),
            currentCurriculum = "المصحف الشريف",
            colorHex = 0xFFBE185D,
            section = HalaqahSection.GIRLS,
            teacherSanad = teacherSanad.trim(),
            recitationDays = recitationDays.trim()
          )
          onConfirm(newHalaqah)
        },
        colors = ButtonDefaults.buttonColors(containerColor = GirlsRosePrimary)
      ) {
        Text("حفظ وتعيين المحفظة")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("إلغاء")
      }
    }
  )
}

/**
 * حوار تسجيل طالبة جديدة مع ربط ولي الأمر
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddGirlsStudentDialog(
  halaqatList: List<QuranHalaqah>,
  onDismiss: () -> Unit,
  onConfirm: (Student) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var studentPhone by remember { mutableStateOf("") }
  var parentName by remember { mutableStateOf("") }
  var parentPhone by remember { mutableStateOf("") }
  var selectedHalaqah by remember { mutableStateOf(halaqatList.firstOrNull()?.name ?: "حلقة خديجة بنت خويلد") }
  var isHalaqahExpanded by remember { mutableStateOf(false) }
  var level by remember { mutableStateOf("متوسط") }
  var currentSurah by remember { mutableStateOf("البقرة") }
  var currentAyah by remember { mutableStateOf("1") }
  var memorizedJuz by remember { mutableStateOf("3") }
  var notes by remember { mutableStateOf("") }

  var validationError by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "👧", fontSize = 22.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text("تسجيل طالبة جديدة وربط ولي الأمر", fontWeight = FontWeight.Bold, color = GirlsRoseDark)
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("اسم الطالبة الكامل") },
          placeholder = { Text("مثال: سارة بنت عبد الله السالم") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = studentPhone,
          onValueChange = { studentPhone = it },
          label = { Text("رقم هاتف الطالبة (اختياري)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = parentName,
          onValueChange = { parentName = it },
          label = { Text("اسم ولي الأمر") },
          placeholder = { Text("مثال: عبد الله السالم") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = parentPhone,
          onValueChange = { parentPhone = it },
          label = { Text("رقم هاتف ولي الأمر (لإشعارات FCM)") },
          placeholder = { Text("055xxxxxxx") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Halaqah selector
        ExposedDropdownMenuBox(
          expanded = isHalaqahExpanded,
          onExpandedChange = { isHalaqahExpanded = !isHalaqahExpanded }
        ) {
          OutlinedTextField(
            value = selectedHalaqah,
            onValueChange = {},
            readOnly = true,
            label = { Text("الحلقة المرتبطة (بنات)") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isHalaqahExpanded) },
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor()
          )
          ExposedDropdownMenu(
            expanded = isHalaqahExpanded,
            onDismissRequest = { isHalaqahExpanded = false }
          ) {
            halaqatList.forEach { halaqah ->
              DropdownMenuItem(
                text = { Text(halaqah.name) },
                onClick = {
                  selectedHalaqah = halaqah.name
                  isHalaqahExpanded = false
                }
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = currentSurah,
            onValueChange = { currentSurah = it },
            label = { Text("السورة الحالية") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = currentAyah,
            onValueChange = { currentAyah = it },
            label = { Text("الآية") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = memorizedJuz,
            onValueChange = { memorizedJuz = it },
            label = { Text("الأجزاء المحفوظة") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = level,
            onValueChange = { level = it },
            label = { Text("المستوى") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("ملاحظات التسميع والسلوك") },
          modifier = Modifier.fillMaxWidth()
        )

        validationError?.let {
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isBlank() || parentName.isBlank()) {
            validationError = "يرجى كتابة اسم الطالبة واسم ولي الأمر"
            return@Button
          }
          val ayahInt = currentAyah.toIntOrNull() ?: 1
          val juzInt = memorizedJuz.toIntOrNull() ?: 0
          val student = Student(
            id = System.currentTimeMillis(),
            name = name.trim(),
            phone = studentPhone.trim(),
            parentName = parentName.trim(),
            parentPhone = parentPhone.trim().ifBlank { studentPhone.trim() },
            groupName = selectedHalaqah,
            level = level.trim(),
            currentSurah = currentSurah.trim(),
            currentAyah = ayahInt,
            memorizedJuzCount = juzInt,
            avatarColorHex = 0xFFBE185D,
            notes = notes.trim(),
            isGirlsSection = true
          )
          onConfirm(student)
        },
        colors = ButtonDefaults.buttonColors(containerColor = GirlsRosePrimary)
      ) {
        Text("تسجيل الطالبة")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("إلغاء")
      }
    }
  )
}

/**
 * حوار رصد التسميع اليومي والتقييم
 */
@Composable
private fun RecordGirlsRecitationDialog(
  student: Student,
  onDismiss: () -> Unit,
  onConfirm: (RecitationRecord) -> Unit
) {
  var surahName by remember { mutableStateOf(student.currentSurah) }
  var fromAyah by remember { mutableStateOf("${student.currentAyah}") }
  var toAyah by remember { mutableStateOf("${student.currentAyah + 10}") }
  var recitationType by remember { mutableStateOf("حفظ جديد") }
  var evaluation by remember { mutableStateOf("ممتاز") }
  var tajweedScore by remember { mutableStateOf("10") }
  var mistakesCount by remember { mutableStateOf("0") }
  var teacherNotes by remember { mutableStateOf("") }

  val recitationTypes = listOf("حفظ جديد", "مراجعة صغرى", "مراجعة كبرى", "تلاوة وتجويد")
  val evaluations = listOf("ممتاز", "جيد جداً", "جيد", "مقبول", "يحتاج إعادة")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "⭐", fontSize = 22.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text("رصد تسميع الطالبة: ${student.name}", fontWeight = FontWeight.Bold, color = GirlsRoseDark)
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = "نوع التسميع:",
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF374151)
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          recitationTypes.forEach { type ->
            FilterChip(
              selected = recitationType == type,
              onClick = { recitationType = type },
              label = { Text(type, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = GirlsRosePrimary,
                selectedLabelColor = Color.White
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = surahName,
          onValueChange = { surahName = it },
          label = { Text("اسم السورة") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = fromAyah,
            onValueChange = { fromAyah = it },
            label = { Text("من آية") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = toAyah,
            onValueChange = { toAyah = it },
            label = { Text("إلى آية") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "التقييم العام:",
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF374151)
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          evaluations.forEach { eval ->
            FilterChip(
              selected = evaluation == eval,
              onClick = { evaluation = eval },
              label = { Text(eval, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFF047857),
                selectedLabelColor = Color.White
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = tajweedScore,
            onValueChange = { tajweedScore = it },
            label = { Text("درجة التجويد (من 10)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = mistakesCount,
            onValueChange = { mistakesCount = it },
            label = { Text("عدد الأخطاء") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = teacherNotes,
          onValueChange = { teacherNotes = it },
          label = { Text("ملاحظات المحفظة والتوجيهات") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val fAyah = fromAyah.toIntOrNull() ?: 1
          val tAyah = toAyah.toIntOrNull() ?: fAyah
          val taj = tajweedScore.toIntOrNull() ?: 10
          val mis = mistakesCount.toIntOrNull() ?: 0

          val record = RecitationRecord(
            id = System.currentTimeMillis(),
            studentId = student.id,
            studentName = student.name,
            date = SampleDataProvider.todayDate,
            recitationType = recitationType,
            surahName = surahName.trim(),
            fromAyah = fAyah,
            toAyah = tAyah,
            evaluation = evaluation,
            tajweedScore = taj,
            mistakesCount = mis,
            qiraahName = "حفص عن عاصم",
            teacherNotes = teacherNotes.trim()
          )
          onConfirm(record)
        },
        colors = ButtonDefaults.buttonColors(containerColor = GirlsRosePrimary)
      ) {
        Text("حفظ التسميع")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("إلغاء")
      }
    }
  )
}

/**
 * حوار تفاصيل الحلقة النسائية
 */
@Composable
private fun GirlsHalaqahDetailsDialog(
  halaqah: QuranHalaqah,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "🌸", fontSize = 22.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(halaqah.name, fontWeight = FontWeight.Bold, color = GirlsRoseDark)
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        DetailRow(label = "المحفظة المعتمدة", value = halaqah.teacherName)
        DetailRow(label = "رقم هاتف المحفظة", value = halaqah.teacherPhone)
        DetailRow(label = "سند القراءة والإجازة", value = halaqah.teacherSanad.ifBlank { halaqah.teacherTitle })
        DetailRow(label = "أيام التسميع", value = halaqah.recitationDays.ifBlank { halaqah.days })
        DetailRow(label = "الفترة والموعد", value = halaqah.timeSlot)
        DetailRow(label = "المقر والقاعة", value = halaqah.location)
        DetailRow(label = "عدد الطالبات الحالي", value = "${halaqah.studentCount} طالبة")
        DetailRow(label = "السعة الإجمالية", value = "${halaqah.capacity} مقعد")
        DetailRow(label = "المستوى القرآني", value = halaqah.targetLevel)
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${halaqah.teacherPhone}")
          }
          context.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(containerColor = GirlsRosePrimary)
      ) {
        Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("اتصال بالمحفظة")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("إغلاق")
      }
    }
  )
}

@Composable
private fun DetailRow(label: String, value: String) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = Color(0xFF6B7280),
      fontWeight = FontWeight.SemiBold
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      color = Color(0xFF1F2937),
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
private fun GirlsStatCard(
  title: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(color.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = color,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = color
      )
      Text(
        text = title,
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF6B7280),
        fontSize = 10.sp,
        textAlign = TextAlign.Center
      )
    }
  }
}
