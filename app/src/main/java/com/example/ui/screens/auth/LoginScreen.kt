package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Student
import com.example.ui.components.CodeLoginCard
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  viewModel: MainViewModel,
  onLoginSuccess: () -> Unit,
  modifier: Modifier = Modifier,
  onBackToAssociationSelect: (() -> Unit)? = null
) {
  val scope = rememberCoroutineScope()
  val students by viewModel.students.collectAsStateWithLifecycle()
  val currentAssoc by viewModel.selectedAssociation.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Supervisor, 1: Teacher, 2: Student/Parent

  var supervisorEmail by remember { mutableStateOf("supervisor@haelsaeed.org") }
  var supervisorPassword by remember { mutableStateOf("123456") }

  var teacherEmail by remember { mutableStateOf("teacher@haelsaeed.org") }
  var teacherPassword by remember { mutableStateOf("123456") }
  var isPasswordVisible by remember { mutableStateOf(false) }

  var studentPhone by remember { mutableStateOf("") }
  var selectedStudentForLogin by remember { mutableStateOf<Student?>(null) }
  var isStudentDropdownExpanded by remember { mutableStateOf(false) }
  var girlsAccessCode by remember { mutableStateOf("SUP-GIRLS-200") }
  var showApkDialog by remember { mutableStateOf(false) }

  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            QuranEmeraldDark,
            QuranEmeraldPrimary,
            Color(0xFFF9F7F2)
          ),
          startY = 0f,
          endY = 700f
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (onBackToAssociationSelect != null) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Start
        ) {
          OutlinedButton(
            onClick = onBackToAssociationSelect,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("← تغيير الجمعية / الدخول بالكود الموجه", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Logo & Brand Header
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(QuranGoldSecondary.copy(alpha = 0.2f))
          .padding(4.dp),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_quran_logo),
          contentDescription = "شعار التطبيق",
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape),
          contentScale = ContentScale.Crop
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = currentAssoc.fullName,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center
      )
      Text(
        text = "بوابة تسجيل الدخول وتحديد الصلاحيات المخصصة",
        style = MaterialTheme.typography.bodyMedium,
        color = QuranGoldSecondary,
        fontSize = 13.sp
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Main Login Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("login_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          // Tab Row: الكود الأمني / المشرف العام / المعلم / الطالب / مشرفة البنات
          ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 2.dp,
            containerColor = QuranEmeraldLight.copy(alpha = 0.5f),
            indicator = { tabPositions ->
              TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = if (selectedTab == 4) Color(0xFFBE185D) else QuranEmeraldPrimary,
                height = 3.dp
              )
            },
            divider = {},
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
          ) {
            Tab(
              selected = selectedTab == 0,
              onClick = {
                selectedTab = 0
                errorMessage = null
              },
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Key,
                  contentDescription = null,
                  tint = if (selectedTab == 0) QuranEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "كود الأمان",
                  fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == 0) QuranEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              }
            }

            Tab(
              selected = selectedTab == 1,
              onClick = {
                selectedTab = 1
                errorMessage = null
              },
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.AdminPanelSettings,
                  contentDescription = null,
                  tint = if (selectedTab == 1) QuranEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "المشرف",
                  fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == 1) QuranEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              }
            }

            Tab(
              selected = selectedTab == 2,
              onClick = {
                selectedTab = 2
                errorMessage = null
              },
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.School,
                  contentDescription = null,
                  tint = if (selectedTab == 2) QuranEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "المعلم",
                  fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == 2) QuranEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              }
            }

            Tab(
              selected = selectedTab == 3,
              onClick = {
                selectedTab = 3
                errorMessage = null
              },
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.MenuBook,
                  contentDescription = null,
                  tint = if (selectedTab == 3) QuranEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "الطالب",
                  fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == 3) QuranEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              }
            }

            Tab(
              selected = selectedTab == 4,
              onClick = {
                selectedTab = 4
                errorMessage = null
              },
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Security,
                  contentDescription = null,
                  tint = if (selectedTab == 4) Color(0xFFBE185D) else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "مشرفة البنات 🌸",
                  fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == 4) Color(0xFFBE185D) else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Error Message Banner
          errorMessage?.let { err ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFFEE2E2))
                .padding(10.dp)
            ) {
              Text(
                text = err,
                color = Color(0xFFDC2626),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
            }
            Spacer(modifier = Modifier.height(12.dp))
          }

          // Tab 0: Access Code Login (Instant Verification & Role Scoping)
          AnimatedVisibility(visible = selectedTab == 0) {
            CodeLoginCard(
              onCodeLoginSuccess = { accessCode ->
                viewModel.loginWithAccessCode(accessCode)
                onLoginSuccess()
              }
            )
          }

          // Tab 1: General Supervisor Login
          AnimatedVisibility(visible = selectedTab == 1) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(QuranGoldLight)
                  .padding(10.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = QuranEmeraldDark, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "صلاحيات المشرف العام: إدارة عليا شاملة لكافة الحلقات، مراجعة التسميع، إرسال التعاميم، والمزامنة السريعة.",
                    fontSize = 11.sp,
                    color = Color(0xFF4A3700),
                    lineHeight = 16.sp
                  )
                }
              }

              OutlinedTextField(
                value = supervisorEmail,
                onValueChange = { supervisorEmail = it },
                label = { Text("البريد الإلكتروني للمشرف العام") },
                leadingIcon = {
                  Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = QuranEmeraldPrimary)
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("supervisor_email_field"),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
              )

              OutlinedTextField(
                value = supervisorPassword,
                onValueChange = { supervisorPassword = it },
                label = { Text("كلمة المرور") },
                leadingIcon = {
                  Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = QuranEmeraldPrimary)
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("supervisor_password_field"),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
              )

              Button(
                onClick = {
                  isLoading = true
                  scope.launch {
                    delay(400)
                    viewModel.login(UserRole.SUPERVISOR, "د. عبد الرحمن سعيد أنعم (المشرف العام)")
                    isLoading = false
                    onLoginSuccess()
                  }
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("supervisor_login_submit_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading
              ) {
                if (isLoading) {
                  CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                  Text(text = "تسجيل الدخول كمشرف عام ⭐", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
              }
            }
          }

          // Tab 2: Content for Teacher Login
          AnimatedVisibility(visible = selectedTab == 2) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFE8F5E9))
                  .padding(10.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = QuranEmeraldDark, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "صلاحيات مقيدة: رصد تسميع الطلاب، تحضير الغياب، وإرسال إشعارات أولياء الأمور فقط.",
                    fontSize = 11.sp,
                    color = QuranEmeraldDark
                  )
                }
              }

              OutlinedTextField(
                value = teacherEmail,
                onValueChange = { teacherEmail = it },
                label = { Text("البريد الإلكتروني للمعلم") },
                leadingIcon = {
                  Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = QuranEmeraldPrimary)
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("teacher_email_field"),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
              )

              OutlinedTextField(
                value = teacherPassword,
                onValueChange = { teacherPassword = it },
                label = { Text("كلمة المرور") },
                leadingIcon = {
                  Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = QuranEmeraldPrimary)
                },
                trailingIcon = {
                  IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                      imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                      contentDescription = null
                    )
                  }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("teacher_password_field"),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
              )

              Button(
                onClick = {
                  isLoading = true
                  scope.launch {
                    delay(400)
                    viewModel.login(UserRole.TEACHER, "الشيخ عبد الله (معلم الحلقة)")
                    isLoading = false
                    onLoginSuccess()
                  }
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("teacher_login_submit_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading
              ) {
                if (isLoading) {
                  CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                  Text(text = "تسجيل الدخول كمعلم (صلاحيات الحلقة)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
              }
            }
          }

          // Tab 3: Content for Student Login
          AnimatedVisibility(visible = selectedTab == 3) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFEFF6FF))
                  .padding(10.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "صلاحيات مقيدة للطالب: متابعة الخطة، مراجعة التقييمات، والاستماع والتسجيل الصوتي.",
                    fontSize = 11.sp,
                    color = Color(0xFF1D4ED8)
                  )
                }
              }

              // Student quick selector
              ExposedDropdownMenuBox(
                expanded = isStudentDropdownExpanded,
                onExpandedChange = { isStudentDropdownExpanded = !isStudentDropdownExpanded }
              ) {
                OutlinedTextField(
                  value = selectedStudentForLogin?.name ?: "اختر الطالب من القائمة",
                  onValueChange = {},
                  readOnly = true,
                  label = { Text("اختيار الطالب المسجل") },
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
                  students.forEach { st ->
                    DropdownMenuItem(
                      text = { Text("${st.name} (${st.groupName})") },
                      onClick = {
                        selectedStudentForLogin = st
                        studentPhone = st.phone
                        isStudentDropdownExpanded = false
                      }
                    )
                  }
                }
              }

              OutlinedTextField(
                value = studentPhone,
                onValueChange = { studentPhone = it },
                label = { Text("رقم هاتف الطالب أو ولي الأمر") },
                leadingIcon = {
                  Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = QuranEmeraldPrimary)
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("student_phone_field"),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
              )

              Button(
                onClick = {
                  val targetStudent = selectedStudentForLogin ?: students.firstOrNull()
                  if (targetStudent == null) {
                    errorMessage = "يرجى اختيار طالب مسجل"
                    return@Button
                  }
                  isLoading = true
                  scope.launch {
                    delay(400)
                    viewModel.selectStudent(targetStudent)
                    viewModel.login(UserRole.STUDENT, targetStudent.name)
                    isLoading = false
                    onLoginSuccess()
                  }
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("student_login_submit_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading
              ) {
                if (isLoading) {
                  CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                  Text(text = "تسجيل الدخول كطالب (سجل الحفظ)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
              }
            }
          }

          // Tab 4: Content for Girls Section Supervisor Login (Limited Access & Section Verification)
          AnimatedVisibility(visible = selectedTab == 4) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFFDF2F8))
                  .padding(10.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFFBE185D),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "نظام الوصول المحدود: هذه البوابة مخصصة لمشرفات قطاع البنات. يتم عزل كافة البيانات والتحقق الصارم من كود المصادقة الخاص بالقسم.",
                    fontSize = 11.sp,
                    color = Color(0xFF831843),
                    lineHeight = 16.sp
                  )
                }
              }

              OutlinedTextField(
                value = girlsAccessCode,
                onValueChange = {
                  girlsAccessCode = it.uppercase()
                  errorMessage = null
                },
                label = { Text("كود مصادقة مشرفة البنات") },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = Color(0xFFBE185D)
                  )
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("girls_supervisor_code_input"),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
              )

              // Quick preset chips
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFCE7F3))
                    .clickable { girlsAccessCode = "SUP-GIRLS-200"; errorMessage = null }
                    .padding(vertical = 6.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text("SUP-GIRLS-200 🌸", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF831843))
                }
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFCE7F3))
                    .clickable { girlsAccessCode = "TCH-KHADIJA-33"; errorMessage = null }
                    .padding(vertical = 6.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text("TCH-KHADIJA-33 🧕", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF831843))
                }
              }

              Button(
                onClick = {
                  isLoading = true
                  scope.launch {
                    delay(300)
                    val (success, message) = viewModel.verifyAndAuthorizeGirlsAccess(girlsAccessCode)
                    isLoading = false
                    if (success) {
                      onLoginSuccess()
                    } else {
                      errorMessage = message
                    }
                  }
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("girls_supervisor_login_submit_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE185D)),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading
              ) {
                if (isLoading) {
                  CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                  Text(text = "التحقق ودخول لوحة المشرفات 🌸", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Quick Demo Access Section
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
            )
            Text(
              text = " الدخول التجريبي السريع للأدوار ",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 8.dp)
            )
            Box(
              modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Demo Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Button(
              onClick = {
                viewModel.login(UserRole.SUPERVISOR, "د. عبد الرحمن سعيد أنعم (المشرف العام)")
                onLoginSuccess()
              },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = QuranGoldSecondary),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("المشرف العام ⭐", fontSize = 11.sp, color = Color(0xFF382900), fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = {
                viewModel.login(UserRole.TEACHER, "الشيخ عبد الله (معلم الحلقة)")
                onLoginSuccess()
              },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("المعلم 🔒", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = {
                val firstStudent = students.firstOrNull()
                viewModel.selectStudent(firstStudent)
                viewModel.login(UserRole.STUDENT, firstStudent?.name ?: "طالب الحلقة")
                onLoginSuccess()
              },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("الطالب 🔒", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            OutlinedButton(
              onClick = {
                viewModel.login(UserRole.PARENT, "ولي أمر الطالب")
                onLoginSuccess()
              },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("ولي الأمر 🔒", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = {
                viewModel.login(UserRole.CIRCLES, "إشراف الحلقات")
                onLoginSuccess()
              },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("قسم الحلقات 🔒", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Button(
            onClick = {
              viewModel.verifyAndAuthorizeGirlsAccess("SUP-GIRLS-200")
              onLoginSuccess()
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("demo_girls_admin_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE185D)),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("🌸 مشرفة قطاع البنات • SUP-GIRLS-200 (وصول محدود)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Button(
              onClick = {
                viewModel.login(UserRole.AI_ASSISTANT, "مستخدم الذكاء الاصطناعي")
                onLoginSuccess()
              },
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .testTag("login_launch_ai_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldDark),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("🤖 الذكاء الاصطناعي", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = {
                showApkDialog = true
              },
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .testTag("login_download_apk_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = QuranGoldSecondary),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("⬇️ تحميل APK (32 ميغابايت)", fontSize = 10.sp, color = Color(0xFF382900), fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "حلقات هائل سعيد أنعم لتحفيظ القرآن الكريم • ميزة المزامنة السريعة وحماية الصلاحيات",
        color = Color.White.copy(alpha = 0.7f),
        fontSize = 11.sp,
        textAlign = TextAlign.Center
      )
    }
  }

  if (showApkDialog) {
    val apkUrl by viewModel.apkDownloadUrl.collectAsStateWithLifecycle()
    com.example.ui.components.ApkDownloadDialog(
      downloadUrl = apkUrl,
      onDismiss = { showApkDialog = false }
    )
  }
}
