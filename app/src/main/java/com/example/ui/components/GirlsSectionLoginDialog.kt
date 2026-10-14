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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.viewmodel.MainViewModel

// Distinctive aesthetic colors for Girls Section
private val GirlsRosePrimary = Color(0xFFBE185D)
private val GirlsRoseDark = Color(0xFF831843)
private val GirlsRoseLight = Color(0xFFFDF2F8)
private val GirlsGoldAccent = Color(0xFFC99700)

/**
 * واجهة تسجيل دخول خاصة بمشرفات البنات عند محاولة الوصول لأي بيانات تتعلق بحلقات البنات
 * مع تفعيل منطق "الوصول المحدود" والتحقق من صلاحيات القسم.
 */
@Composable
fun GirlsSectionLoginDialog(
  viewModel: MainViewModel,
  onDismiss: () -> Unit,
  onAccessGranted: () -> Unit,
  promptReason: String = "محاولة الوصول لبيانات حلقات أو طالبات قسم البنات"
) {
  var enteredCode by remember { mutableStateOf("SUP-GIRLS-200") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var successMessage by remember { mutableStateOf<String?>(null) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      dismissOnBackPress = true,
      dismissOnClickOutside = false,
      usePlatformDefaultWidth = false
    )
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(16.dp)
        .testTag("girls_section_login_dialog"),
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top bar with close button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(GirlsRoseLight)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = GirlsRosePrimary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "نظام الوصول المحدود",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GirlsRoseDark
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "إغلاق",
              tint = Color.Gray
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Shield Lock Icon
        Box(
          modifier = Modifier
            .size(68.dp)
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

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "تسجيل دخول مشرفات قسم البنات 🌸",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = GirlsRoseDark,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = promptReason,
          style = MaterialTheme.typography.bodySmall,
          color = GirlsGoldAccent,
          fontWeight = FontWeight.Medium,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Warning & Privacy note
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = GirlsRosePrimary,
              modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "تنبيه أمني: بيانات حلقات البنات خاضعة لعزل تام وخصوصية مشددة. يتطلب استعراض البيانات مصادقة مشرفة القطاع والتحقق من صلاحيات القسم.",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF9F1239),
              fontSize = 11.sp,
              lineHeight = 17.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input field for code
        OutlinedTextField(
          value = enteredCode,
          onValueChange = {
            enteredCode = it.uppercase()
            errorMessage = null
            successMessage = null
          },
          label = { Text("كود المصادقة الخاص بمشرفة البنات") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Key,
              contentDescription = null,
              tint = GirlsRosePrimary
            )
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("girls_access_code_input"),
          shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Sample Quick Pick Chips
        Text(
          text = "نماذج للاختبار والمصادقة المباشرة:",
          style = MaterialTheme.typography.bodySmall,
          color = Color.Gray,
          fontSize = 11.sp,
          modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(GirlsRoseLight)
              .clickable { enteredCode = "SUP-GIRLS-200"; errorMessage = null }
              .padding(vertical = 8.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("SUP-GIRLS-200", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GirlsRosePrimary)
              Text("مشرفة القطاع (مصرح)", fontSize = 9.sp, color = GirlsRoseDark)
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(GirlsRoseLight)
              .clickable { enteredCode = "TCH-KHADIJA-33"; errorMessage = null }
              .padding(vertical = 8.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("TCH-KHADIJA-33", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GirlsRosePrimary)
              Text("معلمة بنات (مصرح)", fontSize = 9.sp, color = GirlsRoseDark)
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFF3F4F6))
              .clickable { enteredCode = "SUP-BOYS-100"; errorMessage = null }
              .padding(vertical = 8.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("SUP-BOYS-100", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
              Text("بنين (مرفوض)", fontSize = 9.sp, color = Color(0xFFDC2626))
            }
          }
        }

        // Error message if any
        AnimatedVisibility(visible = errorMessage != null) {
          errorMessage?.let { err ->
            Spacer(modifier = Modifier.height(12.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFFEE2E2))
                .padding(10.dp)
            ) {
              Text(
                text = "⚠️ $err",
                color = Color(0xFFDC2626),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp
              )
            }
          }
        }

        // Success message if verified
        AnimatedVisibility(visible = successMessage != null) {
          successMessage?.let { succ ->
            Spacer(modifier = Modifier.height(12.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFDCFCE7))
                .padding(10.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = Color(0xFF16A34A),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = succ,
                  color = Color(0xFF166534),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Action buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("إلغاء", color = Color.DarkGray)
          }

          Button(
            onClick = {
              val (isSuccess, message) = viewModel.verifyAndAuthorizeGirlsAccess(enteredCode)
              if (isSuccess) {
                successMessage = message
                errorMessage = null
                onAccessGranted()
              } else {
                errorMessage = message
                successMessage = null
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GirlsRosePrimary),
            modifier = Modifier
              .weight(1.6f)
              .testTag("verify_girls_code_btn"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(
              text = "تحقق ودخول 🌸",
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }
  }
}
