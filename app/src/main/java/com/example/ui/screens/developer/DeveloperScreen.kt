package com.example.ui.screens.developer

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.StatCard
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranError
import com.example.ui.theme.QuranErrorLight
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.ui.theme.QuranSuccess
import com.example.ui.theme.QuranSuccessLight
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun DeveloperScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  val students by viewModel.students.collectAsStateWithLifecycle()
  val attendance by viewModel.attendance.collectAsStateWithLifecycle()
  val recitations by viewModel.recitations.collectAsStateWithLifecycle()
  val notifications by viewModel.notifications.collectAsStateWithLifecycle()

  var messageBanner by remember { mutableStateOf<String?>(null) }
  var showApkDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Installation Package (حزمة التثبيت APK) Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = QuranEmeraldDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(QuranGoldSecondary.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Android,
                contentDescription = null,
                tint = QuranGoldSecondary,
                modifier = Modifier.size(28.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "حزمة التثبيت (APK Package)",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
              )
              Text(
                text = "جاهزة للبناء والتثبيت على أجهزة أندرويد",
                color = QuranGoldSecondary,
                fontSize = 12.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Package Spec Details Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White.copy(alpha = 0.1f))
              .padding(12.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "📦 معرّف الحزمة: com.aistudio.akramquran.jtwkp",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = "🏷️ اسم التطبيق: حلقات هائل سعيد أنعم لتحفيظ القرآن الكريم (إصدار 1.0)",
                color = Color.White,
                fontSize = 12.sp
              )
              Text(
                text = "⚙️ التوافق: Android 7.0+ (API 24 - 36)",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp
              )
              Text(
                text = "📥 التنزيل: عبر زر الإعدادات في أعلى AI Studio > تصدير APK أو تنزيل ملف المشروع.",
                color = QuranGoldSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = {
              showApkDialog = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("package_build_info_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = QuranGoldSecondary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = null,
              tint = Color(0xFF382900)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "التحقق من جاهزية حزمة التثبيت",
              fontWeight = FontWeight.Bold,
              color = Color(0xFF382900)
            )
          }
        }
      }
    }

    // Status Banner if any
    messageBanner?.let { msg ->
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = QuranSuccessLight)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = QuranSuccess,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = msg,
              color = QuranSuccess,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    // Database Statistics
    item {
      Text(
        text = "إحصائيات قاعدة البيانات المحلية (Room)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatCard(
          title = "الطلاب",
          value = "${students.size}",
          icon = Icons.Default.People,
          tintColor = QuranEmeraldPrimary,
          bgColor = QuranEmeraldLight,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "الحضور",
          value = "${attendance.size}",
          icon = Icons.Default.CheckCircle,
          tintColor = QuranSuccess,
          bgColor = QuranSuccessLight,
          modifier = Modifier.weight(1f)
        )
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatCard(
          title = "جلسات التسميع",
          value = "${recitations.size}",
          icon = Icons.Default.MenuBook,
          tintColor = QuranGoldSecondary,
          bgColor = QuranGoldLight,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "الإشعارات",
          value = "${notifications.size}",
          icon = Icons.Default.Bookmark,
          tintColor = Color(0xFF2563EB),
          bgColor = Color(0xFFEFF6FF),
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Data Management Actions
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Storage,
              contentDescription = null,
              tint = QuranEmeraldPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "إدارة البيانات والحلقات",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          Text(
            text = "يمكنك إعادة تعيين السجلات التجريبية لحلقات عثمان وعلي وأبي بكر أو مسح البيانات بالكامل.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Button(
            onClick = {
              viewModel.resetToSampleData()
              messageBanner = "تمت إعادة تعيين البيانات النموذجية بنجاح لكافة الطلاب والسجلات."
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(42.dp)
              .testTag("reset_sample_data_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("إعادة تعيين البيانات النموذجية", fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = {
              viewModel.clearAllData()
              messageBanner = "تم مسح كافة البيانات من قاعدة البيانات."
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(42.dp)
              .testTag("clear_data_btn"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = QuranError),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("مسح كافة البيانات", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // System Information
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = QuranEmeraldPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "عن التطبيق", fontWeight = FontWeight.Bold)
          }
          Text(text = "• التطبيق: حلقات هائل سعيد أنعم لتحفيظ القرآن الكريم", fontSize = 12.sp)
          Text(text = "• المحرك: Jetpack Compose + Material 3 + Room DB", fontSize = 12.sp)
          Text(text = "• الخطوط: دعم أصيل للخط العربي وأسماء السور القرآنية", fontSize = 12.sp)
          Text(text = "• حالة الحزمة: جاهزة للتصدير كحزمة تثبيت أندرويد APK", fontSize = 12.sp)
        }
      }
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
