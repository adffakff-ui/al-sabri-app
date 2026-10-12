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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccessCode
import com.example.data.model.AccessCodeRegistry
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.viewmodel.UserRole

@Composable
fun CodeLoginCard(
  onCodeLoginSuccess: (AccessCode) -> Unit,
  modifier: Modifier = Modifier
) {
  var inputCode by remember { mutableStateOf("TCH-OTHMAN-11") }
  var validationError by remember { mutableStateOf<String?>(null) }

  val resolvedCode by remember(inputCode) {
    derivedStateOf { AccessCodeRegistry.findCode(inputCode) }
  }

  val demoChips = listOf(
    "TCH-OTHMAN-11" to "🕌 معلم (عثمان - بنين)",
    "TCH-KHADIJA-33" to "🧕 معلمة (خديجة - بنات)",
    "STU-ABDELRAHMAN-01" to "📖 طالب (عبد الرحمن)",
    "STU-MARYAM-101" to "🌸 طالبة (مريم)",
    "PAR-ABDELRAHMAN-01" to "👨‍👦 ولي أمر (عبد الرحمن)",
    "PAR-MARYAM-101" to "👨‍👧 ولي أمر (مريم)",
    "SUP-BOYS-100" to "👦 مشرف قطاع البنين",
    "SUP-GIRLS-200" to "👧 مشرفة قطاع البنات",
    "SUP-GENERAL-777" to "👑 المشرف العام"
  )

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // Header
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(QuranEmeraldPrimary.copy(alpha = 0.12f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Key,
            contentDescription = null,
            tint = QuranEmeraldPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "الدخول برمز الأمان والكود المخصص",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = QuranEmeraldDark
          )
          Text(
            text = "أدخل الكود المرسل من المعلم أو المشرف لتحديد صلاحياتك وعزل بياناتك",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Input field
      OutlinedTextField(
        value = inputCode,
        onValueChange = {
          inputCode = it.uppercase()
          validationError = null
        },
        label = { Text("رمز الكود الأمني (مثال: TCH-OTHMAN-11)") },
        leadingIcon = {
          Icon(imageVector = Icons.Default.Pin, contentDescription = null, tint = QuranEmeraldPrimary)
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("access_code_input_field"),
        singleLine = true,
        shape = RoundedCornerShape(14.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Quick Chips for immediate testing
      Text(
        text = "أكواد التجربة السريعة (انقر للتعبئة الفورية):",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(6.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(demoChips) { (code, label) ->
          val isSelected = inputCode.trim().equals(code, ignoreCase = true)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(
                if (isSelected) QuranEmeraldPrimary else Color(0xFFF1F5F9)
              )
              .clickable {
                inputCode = code
                validationError = null
              }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = label,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) Color.White else Color(0xFF334155)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Live Scoped Resolution Preview Card
      AnimatedVisibility(visible = resolvedCode != null) {
        resolvedCode?.let { codeInfo ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAF8)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "تم التحقق من الكود بنجاح",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF16A34A)
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(QuranGoldLight)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = codeInfo.role.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF785700)
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "صاحب الحساب: ${codeInfo.userName}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = QuranEmeraldDark
              )

              codeInfo.groupName?.let { grp ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "الحلقة المعتمدة: $grp (${codeInfo.section.title})",
                  fontSize = 12.sp,
                  color = QuranEmeraldPrimary,
                  fontWeight = FontWeight.Medium
                )
              }

              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = codeInfo.scopeDescription,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
              )

              if (codeInfo.restrictedPrivileges.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "القيود المفروضة: ${codeInfo.restrictedPrivileges.joinToString(" • ")}",
                    fontSize = 10.sp,
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }
        }
      }

      // Error message if invalid
      validationError?.let { err ->
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = err, color = Color(0xFFDC2626), fontSize = 11.sp)
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Submit Button
      Button(
        onClick = {
          val codeObj = resolvedCode
          if (codeObj == null) {
            validationError = "رمز الكود غير صحيح، يرجى التأكد من الرمز وإعادة المحاولة."
          } else {
            onCodeLoginSuccess(codeObj)
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("code_login_submit_btn"),
        colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "تأكيد الدخول بالكود وتفعيل الصلاحيات المخصصة",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }
    }
  }
}
