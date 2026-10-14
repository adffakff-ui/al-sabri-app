package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldSecondary
import com.example.ui.theme.QuranSuccess

@Composable
fun ApkDownloadDialog(
  downloadUrl: String,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = modifier.testTag("apk_download_dialog"),
    confirmButton = {},
    dismissButton = {},
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(QuranEmeraldPrimary),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Android,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "حزمة تثبيت التطبيق (APK)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = QuranEmeraldDark
            )
            Text(
              text = "حلقات هائل سعيد أنعم • نظام مستقل",
              style = MaterialTheme.typography.bodySmall,
              color = Color.Gray,
              fontSize = 11.sp
            )
          }
        }

        IconButton(onClick = onDismiss) {
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
        // Build specs card
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = QuranEmeraldLight.copy(alpha = 0.4f))
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = QuranEmeraldPrimary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "جاهزية الحزمة للنشر والتثبيت المباشر",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = QuranEmeraldDark
              )
            }
            Text(
              text = "• معرف الحزمة: com.aistudio.akramquran.jtwkp\n• الإصدار: v1.0.0 (Release APK Architecture)\n• التوافق: أندرويد 7.0 حتى أندرويد 15+\n• الربط: متصل مباشرة بقاعدة وسيرفر Firebase ومتصفح WebView مدمج",
              fontSize = 11.sp,
              color = QuranEmeraldDark.copy(alpha = 0.9f),
              lineHeight = 16.sp
            )
          }
        }

        // Action buttons: Download & Open in Browser
        Button(
          onClick = {
            try {
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
              context.startActivity(intent)
              Toast.makeText(context, "جاري فتح رابط التحميل المباشر...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
              Toast.makeText(context, "تعذر فتح الرابط: ${e.message}", Toast.LENGTH_LONG).show()
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("download_apk_primary_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.CloudDownload,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "تحميل حزمة APK مباشرة الآن",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              clipboardManager.setText(AnnotatedString(downloadUrl))
              Toast.makeText(context, "تم نسخ رابط التحميل إلى الحافظة", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("نسخ الرابط", fontSize = 12.sp)
          }

          OutlinedButton(
            onClick = {
              try {
                val sendIntent = Intent().apply {
                  action = Intent.ACTION_SEND
                  putExtra(Intent.EXTRA_TEXT, "رابط تطبيق حلقات هائل سعيد أنعم لتحفيظ القرآن الكريم:\n$downloadUrl")
                  type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "مشاركة رابط التطبيق"))
              } catch (e: Exception) {
                Toast.makeText(context, "تعذر المشاركة", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("مشاركة", fontSize = 12.sp)
          }
        }

        // Installation instructions guide
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = QuranGoldSecondary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "طريقة تثبيت الحزمة على جوالك:",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFF334155)
              )
            }
            Text(
              text = "1. بعد انتهاء التحميل انقر على ملف الـ APK المُنزل.\n2. إذا ظهر تنبيه الأمان، اختر \"الإعدادات\" ثم فعّل \"السماح من هذا المصدر\".\n3. انقر \"تثبيت\" لتبدأ المنظومة بالعمل فوراً مع متصفح الويب وقاعدة البيانات.",
              fontSize = 10.sp,
              color = Color(0xFF475569),
              lineHeight = 15.sp
            )
          }
        }
      }
    }
  )
}
