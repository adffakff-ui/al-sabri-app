package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.SyncState
import com.example.data.local.SyncStatus
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldSecondary
import com.example.ui.theme.QuranSuccess
import com.example.viewmodel.UserRole

@Composable
fun AppTopBar(
  currentRole: UserRole,
  authenticatedRole: UserRole = currentRole,
  syncState: SyncState? = null,
  onSyncClick: (() -> Unit)? = null,
  onRoleSelected: (UserRole) -> Unit,
  onLogoutClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val isSupervisorOrDev = authenticatedRole == UserRole.SUPERVISOR || authenticatedRole == UserRole.DEVELOPER

  val infiniteTransition = rememberInfiniteTransition(label = "top_sync_spin")
  val rotationAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rotation"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            QuranEmeraldDark,
            QuranEmeraldPrimary
          )
        )
      )
  ) {
    // Header Row with App Logo, Title, Sync button, and User Info
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
      ) {
        // App Logo
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(QuranGoldSecondary.copy(alpha = 0.2f))
            .padding(2.dp),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_quran_logo),
            contentDescription = "شعار حلقات هائل سعيد أنعم",
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Text(
            text = "حلقات هائل سعيد أنعم",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
          )
          Text(
            text = "لتحفيظ القرآن الكريم • نظام المزامنة والصلاحيات",
            style = MaterialTheme.typography.bodySmall,
            color = QuranGoldSecondary,
            fontSize = 11.sp
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // Instant Sync Button with animation
        onSyncClick?.let { onSync ->
          IconButton(
            onClick = onSync,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.15f))
              .testTag("topbar_sync_btn")
          ) {
            Icon(
              imageVector = if (syncState?.status == SyncStatus.SYNCING) Icons.Default.CloudSync else Icons.Default.CloudDone,
              contentDescription = "مزامنة سريعة",
              tint = if (syncState?.status == SyncStatus.SYNCING) QuranGoldSecondary else Color.White,
              modifier = Modifier
                .size(18.dp)
                .then(
                  if (syncState?.status == SyncStatus.SYNCING) Modifier.rotate(rotationAngle) else Modifier
                )
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
        }

        // Active Role Badge
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isSupervisorOrDev) QuranGoldSecondary else Color.White.copy(alpha = 0.18f)
          )
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = when (currentRole) {
                UserRole.SUPERVISOR -> Icons.Default.AdminPanelSettings
                UserRole.AI_ASSISTANT -> Icons.Default.AutoAwesome
                UserRole.GIRLS_ADMIN -> Icons.Default.AdminPanelSettings
                UserRole.TEACHER -> Icons.Default.School
                UserRole.CIRCLES -> Icons.Default.MenuBook
                UserRole.QIRAAT -> Icons.Default.AutoStories
                UserRole.STUDENT -> Icons.Default.Bookmark
                UserRole.PARENT -> Icons.Default.FamilyRestroom
                UserRole.DEVELOPER -> Icons.Default.Code
              },
              contentDescription = null,
              tint = if (isSupervisorOrDev) Color(0xFF382900) else QuranGoldSecondary,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = currentRole.title,
              color = if (isSupervisorOrDev) Color(0xFF382900) else Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }

        // Direct Quick AI Assistant button for instant guidance
        Spacer(modifier = Modifier.width(6.dp))
        IconButton(
          onClick = { onRoleSelected(UserRole.AI_ASSISTANT) },
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.15f))
            .testTag("topbar_quick_ai_btn")
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "مساعد الذكاء الاصطناعي القرآني",
            tint = QuranGoldSecondary,
            modifier = Modifier.size(18.dp)
          )
        }

        onLogoutClick?.let { onLogout ->
          Spacer(modifier = Modifier.width(6.dp))
          IconButton(
            onClick = onLogout,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.15f))
              .testTag("topbar_logout_btn")
          ) {
            Icon(
              imageVector = Icons.Default.ExitToApp,
              contentDescription = "تسجيل الخروج",
              tint = QuranGoldSecondary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }

    // Role-Based Navigation or Scope Lockdown Banner
    if (isSupervisorOrDev) {
      // General Supervisor has Universal Access to inspect all roles & sections
      val roles = listOf(
        UserRole.SUPERVISOR,
        UserRole.AI_ASSISTANT,
        UserRole.GIRLS_ADMIN,
        UserRole.TEACHER,
        UserRole.CIRCLES,
        UserRole.QIRAAT,
        UserRole.STUDENT,
        UserRole.PARENT,
        UserRole.DEVELOPER
      )
      val selectedIndex = roles.indexOf(currentRole).coerceAtLeast(0)

      ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        contentColor = Color.White,
        edgePadding = 12.dp,
        indicator = {},
        divider = {},
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 6.dp)
          .testTag("role_tab_row")
      ) {
        roles.forEachIndexed { index, role ->
          val isSelected = index == selectedIndex
          Tab(
            selected = isSelected,
            onClick = { onRoleSelected(role) },
            modifier = Modifier
              .padding(horizontal = 3.dp, vertical = 3.dp)
              .clip(RoundedCornerShape(20.dp))
              .background(
                if (isSelected) QuranGoldSecondary else Color.White.copy(alpha = 0.12f)
              )
              .padding(horizontal = 12.dp, vertical = 6.dp)
              .testTag("tab_${role.name.lowercase()}")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = when (role) {
                  UserRole.SUPERVISOR -> Icons.Default.AdminPanelSettings
                  UserRole.AI_ASSISTANT -> Icons.Default.AutoAwesome
                  UserRole.GIRLS_ADMIN -> Icons.Default.AdminPanelSettings
                  UserRole.TEACHER -> Icons.Default.School
                  UserRole.CIRCLES -> Icons.Default.MenuBook
                  UserRole.QIRAAT -> Icons.Default.AutoStories
                  UserRole.STUDENT -> Icons.Default.Bookmark
                  UserRole.PARENT -> Icons.Default.FamilyRestroom
                  UserRole.DEVELOPER -> Icons.Default.Code
                },
                contentDescription = null,
                tint = if (isSelected) Color(0xFF382900) else Color.White,
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = when (role) {
                  UserRole.SUPERVISOR -> "لوحة المشرف ⭐"
                  UserRole.AI_ASSISTANT -> "الذكاء الاصطناعي 🤖"
                  UserRole.GIRLS_ADMIN -> "حلقات البنات 🌸"
                  else -> role.title
                },
                color = if (isSelected) Color(0xFF382900) else Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    } else {
      // Locked Mode for standard roles (Teacher, Student, Parent, Circles, Qira'at)
      // Rest of app is closed and protected
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(Color.Black.copy(alpha = 0.15f))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = QuranGoldSecondary,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "صلاحيات مقيدة لقسم ${currentRole.title} • باقي أقسام التطبيق مغلقة ومحمية",
              color = Color.White.copy(alpha = 0.95f),
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }

          syncState?.let { sync ->
            Text(
              text = "⚡ مزامنة: ${sync.speedImprovementFactor}",
              color = QuranGoldSecondary,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(4.dp))
    }
  }
}
