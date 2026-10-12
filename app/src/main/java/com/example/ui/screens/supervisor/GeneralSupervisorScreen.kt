package com.example.ui.screens.supervisor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.SyncStatus
import com.example.data.model.PermissionManager
import com.example.data.model.QuranHalaqatData
import com.example.data.model.RecitationRecord
import com.example.ui.components.HeroQuranBanner
import com.example.ui.components.StatCard
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.ui.theme.QuranSuccess
import com.example.ui.theme.QuranSurfaceVariant
import com.example.util.AudioRecordManager
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.UserRole

@Composable
fun GeneralSupervisorScreen(
  viewModel: MainViewModel,
  onInspectRole: (UserRole) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val audioManager = remember { AudioRecordManager(context) }

  DisposableEffect(Unit) {
    onDispose {
      audioManager.release()
    }
  }

  val students by viewModel.students.collectAsStateWithLifecycle()
  val attendance by viewModel.attendance.collectAsStateWithLifecycle()
  val recitations by viewModel.recitations.collectAsStateWithLifecycle()
  val notifications by viewModel.notifications.collectAsStateWithLifecycle()
  val syncState by viewModel.syncState.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableIntStateOf(0) }
  var currentlyPlayingUrl by remember { mutableStateOf<String?>(null) }
  var showAnnouncementDialog by remember { mutableStateOf(false) }
  var announcementTitle by remember { mutableStateOf("") }
  var announcementBody by remember { mutableStateOf("") }
  var announcementSentFeedback by remember { mutableStateOf(false) }

  val presentStudentsCount = attendance.count { it.status == "حاضر" }
  val attendanceRate = if (students.isNotEmpty()) {
    ((presentStudentsCount.toDouble() / students.size.toDouble()) * 100).toInt()
  } else 90

  val averageScore = if (recitations.isNotEmpty()) {
    recitations.map { it.scorePercent }.average().toInt()
  } else 92

  // Sync button rotation animation when syncing
  val infiniteTransition = rememberInfiniteTransition(label = "sync_spin")
  val rotationAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rotation"
  )

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Executive Hero Banner
    item {
      HeroQuranBanner(
        title = "لوحة قيادة المشرف العام • الإدارة الشاملة",
        subtitle = "حلقات هائل سعيد أنعم لتحفيظ القرآن الكريم • كامل الصلاحيات الإشرافية والرقابة العامة"
      )
    }

    // High-Speed Synchronization & Cache Status Card
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (syncState.status == SyncStatus.SYNCING) QuranGoldLight else QuranEmeraldLight
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("supervisor_sync_card")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(QuranEmeraldPrimary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (syncState.status == SyncStatus.SYNCING) Icons.Default.CloudSync else Icons.Default.CloudDone,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                  .size(24.dp)
                  .then(
                    if (syncState.status == SyncStatus.SYNCING) Modifier.rotate(rotationAngle) else Modifier
                  )
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "المزامنة السريعة الفورية",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = QuranEmeraldDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(QuranSuccess.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = syncState.speedImprovementFactor,
                    color = QuranEmeraldDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
              Text(
                text = "${syncState.statusMessage} • آخر مزامنة: ${syncState.lastSyncTime}",
                style = MaterialTheme.typography.bodySmall,
                color = QuranEmeraldDark.copy(alpha = 0.85f),
                fontSize = 11.sp
              )
            }
          }

          Button(
            onClick = { viewModel.performSync() },
            colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
            shape = RoundedCornerShape(10.dp),
            enabled = syncState.status != SyncStatus.SYNCING,
            modifier = Modifier.testTag("instant_sync_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "مزامنة الآن",
              modifier = Modifier
                .size(16.dp)
                .then(
                  if (syncState.status == SyncStatus.SYNCING) Modifier.rotate(rotationAngle) else Modifier
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "مزامنة فورية", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Executive KPI Summary Cards
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StatCard(
          title = "إجمالي الطلاب",
          value = "${students.size} طالب",
          icon = Icons.Default.Group,
          tintColor = QuranEmeraldPrimary,
          bgColor = QuranEmeraldLight,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "الحلقات النشطة",
          value = "${QuranHalaqatData.halaqatList.size} حلقات",
          icon = Icons.Default.MenuBook,
          tintColor = QuranGoldSecondary,
          bgColor = QuranGoldLight,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "الحضور العام",
          value = "$attendanceRate%",
          icon = Icons.Default.CheckCircle,
          tintColor = QuranSuccess,
          bgColor = Color(0xFFE8F5E9),
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "معدل الإتقان",
          value = "$averageScore%",
          icon = Icons.Default.Grade,
          tintColor = Color(0xFF2563EB),
          bgColor = Color(0xFFEFF6FF),
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Tabs for General Supervisor
    item {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = QuranSurfaceVariant.copy(alpha = 0.5f),
        contentColor = QuranEmeraldPrimary,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = QuranEmeraldPrimary,
            height = 3.dp
          )
        },
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .testTag("supervisor_tabs")
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Text(
              text = "الرقابة والمتابعة",
              fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
              fontSize = 11.sp
            )
          }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Text(
              text = "مصفوفة الصلاحيات",
              fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
              fontSize = 11.sp
            )
          }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = {
            Text(
              text = "الحلقات والمعلمون",
              fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
              fontSize = 11.sp
            )
          }
        )
        Tab(
          selected = selectedTab == 3,
          onClick = { selectedTab = 3 },
          text = {
            Text(
              text = "التعاميم والتوجيهات",
              fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
              fontSize = 11.sp
            )
          }
        )
      }
    }

    // Tab 0: Field Oversight & Recitation Audit
    if (selectedTab == 0) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "تدقيق جلسات التسميع والتسجيلات الصوتية (${recitations.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "رقابة جودة التلاوة والأحكام",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      if (recitations.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
              Text(
                text = "لا توجد جلسات تسميع مسجلة بعد اليوم.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        items(recitations, key = { it.id }) { rec ->
          SupervisorRecitationAuditCard(
            record = rec,
            isPlayingAudio = currentlyPlayingUrl == rec.audioFilePath,
            onPlayAudio = {
              rec.audioFilePath?.let { path ->
                if (currentlyPlayingUrl == path) {
                  audioManager.stopPlayback()
                  currentlyPlayingUrl = null
                } else {
                  currentlyPlayingUrl = path
                  audioManager.playAudio(path) {
                    currentlyPlayingUrl = null
                  }
                }
              }
            }
          )
        }
      }
    }

    // Tab 1: Role-Based Access Control (RBAC) & Scope Matrix
    if (selectedTab == 1) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = QuranEmeraldLight),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = QuranEmeraldPrimary,
              modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "نظام الصلاحيات المقيدة حسب الدور",
                fontWeight = FontWeight.Bold,
                color = QuranEmeraldDark,
                fontSize = 14.sp
              )
              Text(
                text = "تم غلق كافة الأقسام والصلاحيات خارج الدور المخصص لكل مستخدم، باستثناء المشرف العام الذي يملك حق الرقابة والتنقل.",
                fontSize = 11.sp,
                color = QuranEmeraldDark.copy(alpha = 0.85f)
              )
            }
          }
        }
      }

      items(UserRole.values()) { role ->
        val permissions = PermissionManager.getPermissions(role)
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
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (role == UserRole.SUPERVISOR) QuranGoldLight else QuranEmeraldLight),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (role == UserRole.SUPERVISOR) Icons.Default.AdminPanelSettings else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (role == UserRole.SUPERVISOR) QuranGoldSecondary else QuranEmeraldPrimary,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = role.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                  Text(
                    text = role.subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(
                    if (role == UserRole.SUPERVISOR) QuranEmeraldPrimary.copy(alpha = 0.15f)
                    else Color(0xFFFEE2E2)
                  )
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = if (role == UserRole.SUPERVISOR) "كامل الصلاحيات ⭐" else "صلاحيات مقيدة 🔒",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (role == UserRole.SUPERVISOR) QuranEmeraldPrimary else Color(0xFFDC2626)
                )
              }
            }

            Text(
              text = permissions.description,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Quick Inspection Action for Supervisor
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              OutlinedButton(
                onClick = {
                  viewModel.setRole(role)
                  onInspectRole(role)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp)
              ) {
                Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "معاينة القسم إشرافياً", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // Tab 2: Halaqat & Teachers
    if (selectedTab == 2) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "حلقات مجمع هائل سعيد أنعم (${QuranHalaqatData.halaqatList.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          OutlinedButton(
            onClick = {
              viewModel.setRole(UserRole.CIRCLES)
              onInspectRole(UserRole.CIRCLES)
            },
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("فتح دليل الحلقات الكامل", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      items(QuranHalaqatData.halaqatList) { halaqah ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(text = halaqah.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text(
                text = "معلم الحلقة: ${halaqah.teacherName}",
                fontSize = 12.sp,
                color = QuranEmeraldPrimary,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "${halaqah.mosqueLocation} • ${halaqah.meetingTime}",
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
                text = "${halaqah.studentCount} طالب",
                color = QuranEmeraldDark,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }

    // Tab 3: Broadcast Announcements
    if (selectedTab == 3) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Campaign,
                contentDescription = null,
                tint = QuranEmeraldPrimary,
                modifier = Modifier.size(26.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "إصدار وتعميم إشرافي عام",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
            Text(
              text = "يصل هذا التعميم مباشرة لكافة المعلمين والطلاب وأولياء الأمور في مجمع حلقات هائل سعيد أنعم ويُحدث فورياً في قاعدة البيانات.",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
              value = announcementTitle,
              onValueChange = { announcementTitle = it },
              label = { Text("عنوان التعميم الإشرافي") },
              placeholder = { Text("مثال: موعد اختبارات الأجزاء ونصف العام...") },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = announcementBody,
              onValueChange = { announcementBody = it },
              label = { Text("نص التوجيه أو التعميم") },
              placeholder = { Text("اكتب تفاصيل التوجيه الإشرافي...") },
              modifier = Modifier.fillMaxWidth(),
              minLines = 3,
              shape = RoundedCornerShape(12.dp)
            )

            Button(
              onClick = {
                if (announcementTitle.isNotBlank() && announcementBody.isNotBlank()) {
                  viewModel.broadcastSupervisorAnnouncement(announcementTitle, announcementBody)
                  announcementTitle = ""
                  announcementBody = ""
                  announcementSentFeedback = true
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(imageVector = Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("إرسال التعميم لكافة الحلقات والطلاب", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            AnimatedVisibility(visible = announcementSentFeedback) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFE8F5E9))
                  .padding(10.dp)
              ) {
                Text(
                  text = "✅ تم بث التعميم الإشرافي ومزامنته بنجاح مع كافة حسابات الحلقات وأولياء الأمور!",
                  color = QuranSuccess,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      }

      item {
        Text(
          text = "سجل الإشعارات والتعاميم الصادرة (${notifications.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      items(notifications.take(8)) { notif ->
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text(text = notif.date, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = notif.message, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "الطالب: ${notif.studentName} • ${notif.notificationType}",
              fontSize = 10.sp,
              color = QuranEmeraldPrimary,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }
  }
}

@Composable
fun SupervisorRecitationAuditCard(
  record: RecitationRecord,
  isPlayingAudio: Boolean,
  onPlayAudio: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = record.studentName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text(
            text = "${record.surahName} (الآيات ${record.fromAyah} - ${record.toAyah}) • ${record.recitationType}",
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
            text = "${record.scorePercent}% (${record.evaluation})",
            color = QuranEmeraldDark,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "الرواية: ${record.qiraahName} • التاريخ: ${record.date}",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Audio preview button if audio recording exists
        if (record.audioFilePath != null) {
          OutlinedButton(
            onClick = onPlayAudio,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(32.dp)
          ) {
            Icon(
              imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = null,
              modifier = Modifier.size(14.dp),
              tint = QuranEmeraldPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isPlayingAudio) "إيقاف" else "استماع للتسجيل",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = QuranEmeraldPrimary
            )
          }
        }
      }

      if (record.teacherNotes.isNotBlank()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(QuranSurfaceVariant.copy(alpha = 0.5f))
            .padding(8.dp)
        ) {
          Text(
            text = "ملاحظات المعلم: ${record.teacherNotes}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}
