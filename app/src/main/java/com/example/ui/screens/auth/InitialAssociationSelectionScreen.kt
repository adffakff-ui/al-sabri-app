package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.AccessCodeRegistry
import com.example.data.model.AssociationType
import com.example.ui.components.ApkDownloadDialog
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InitialAssociationSelectionScreen(
  viewModel: MainViewModel,
  onLoginSuccess: () -> Unit,
  onSwitchToAdvancedLogin: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  val currentAssoc by viewModel.selectedAssociation.collectAsStateWithLifecycle()

  var chosenAssoc by remember { mutableStateOf(currentAssoc) }
  var supervisorCodeInput by remember(chosenAssoc) {
    mutableStateOf(if (chosenAssoc == AssociationType.HAIEL_SAEED) "SUP-GENERAL-777" else "MJ-ADMIN-990")
  }
  var validationError by remember { mutableStateOf<String?>(null) }
  var successMessage by remember { mutableStateOf<String?>(null) }
  var isVerifying by remember { mutableStateOf(false) }
  var showApkDialog by remember { mutableStateOf(false) }

  val primaryThemeColor = Color(chosenAssoc.primaryColorHex)
  val secondaryThemeColor = Color(chosenAssoc.secondaryColorHex)

  val quickChipsForAssoc = if (chosenAssoc == AssociationType.HAIEL_SAEED) {
    listOf(
      "SUP-GENERAL-777" to "👑 المشرف العام (هائل سعيد)",
      "SUP-BOYS-100" to "👦 مشرف قطاع البنين",
      "SUP-GIRLS-200" to "🌸 مشرفة قطاع البنات",
      "TCH-OTHMAN-11" to "🕌 معلم حلقة عثمان",
      "STU-ABDELRAHMAN-01" to "📖 الطالب عبد الرحمن",
      "PAR-ABDELRAHMAN-01" to "👨‍👦 ولي أمر عبد الرحمن"
    )
  } else {
    listOf(
      "MJ-ADMIN-990" to "👑 المشرف العام (معاذ الحروي)",
      "MJ-CARE-100" to "🛠️ مشرف صيانة المصاحف",
      "MJ-TCH-01" to "🕌 معلم حلقة معاذ بن جبل",
      "MJ-TCH-02" to "📖 معلم حلقة نافع المدني",
      "MJ-STU-2001" to "🌟 الطالب بلال الحروي",
      "MJ-PAR-2001" to "👨‍👦 ولي أمر بلال الحروي"
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            QuranEmeraldDark,
            primaryThemeColor.copy(alpha = 0.85f),
            Color(0xFFF9F7F2)
          ),
          startY = 0f,
          endY = 800f
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
      Spacer(modifier = Modifier.height(14.dp))

      // Logo & Brand Header
      Box(
        modifier = Modifier
          .size(76.dp)
          .clip(CircleShape)
          .background(QuranGoldSecondary.copy(alpha = 0.2f))
          .padding(3.dp),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_quran_logo),
          contentDescription = "شعار المنظومة القرآنية",
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape),
          contentScale = ContentScale.Crop
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "بوابة المنظومة القرآنية الذكية",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center
      )

      Text(
        text = "يرجى تحديد الجمعية وإدخال كود المشرف المعتمد لتفعيل العزل الأمني في Firestore",
        style = MaterialTheme.typography.bodySmall,
        color = QuranGoldSecondary,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Step 1 Card: Selection of Association
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("association_selector_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(primaryThemeColor.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "١", fontWeight = FontWeight.Bold, color = primaryThemeColor, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "الخطوة الأولى: اختر الجمعية التابع لها",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = QuranEmeraldDark
              )
              Text(
                text = "يتم عزل قاعدة البيانات ومسارات Firestore تماماً بناءً على اختيارك",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Association Option 1: جمعية هائل سعيد أنعم
          AssociationOptionCard(
            association = AssociationType.HAIEL_SAEED,
            isSelected = chosenAssoc == AssociationType.HAIEL_SAEED,
            onSelect = {
              chosenAssoc = AssociationType.HAIEL_SAEED
              viewModel.setAssociation(AssociationType.HAIEL_SAEED)
              validationError = null
              successMessage = null
            },
            modifier = Modifier.testTag("select_haiel_saeed_card")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Association Option 2: جمعية معاذ الحروي
          AssociationOptionCard(
            association = AssociationType.MUADH_JARWI,
            isSelected = chosenAssoc == AssociationType.MUADH_JARWI,
            onSelect = {
              chosenAssoc = AssociationType.MUADH_JARWI
              viewModel.setAssociation(AssociationType.MUADH_JARWI)
              validationError = null
              successMessage = null
            },
            modifier = Modifier.testTag("select_muadh_harwi_card")
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Step 2 Card: Supervisor / Access Code Input
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("supervisor_code_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(primaryThemeColor.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "٢", fontWeight = FontWeight.Bold, color = primaryThemeColor, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "الخطوة الثانية: كود المشرف / رمز الدخول الأمني",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = QuranEmeraldDark
              )
              Text(
                text = "مخصص لـ: ${chosenAssoc.shortName}",
                fontSize = 11.sp,
                color = primaryThemeColor,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = supervisorCodeInput,
            onValueChange = {
              supervisorCodeInput = it.uppercase()
              validationError = null
              successMessage = null
            },
            label = {
              Text("كود الدخول المعتمد (مثال: ${if (chosenAssoc == AssociationType.HAIEL_SAEED) "SUP-GENERAL-777" else "MJ-ADMIN-990"})")
            },
            leadingIcon = {
              Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = primaryThemeColor)
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("supervisor_code_input_field"),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = primaryThemeColor,
              focusedLabelColor = primaryThemeColor
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Quick testing chips for current association
          Text(
            text = "أكواد التجربة السريعة المعتمدة للجمعية:",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(quickChipsForAssoc) { (code, label) ->
              val isSelectedChip = supervisorCodeInput == code
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelectedChip) primaryThemeColor else primaryThemeColor.copy(alpha = 0.08f))
                  .border(
                    width = 1.dp,
                    color = if (isSelectedChip) primaryThemeColor else primaryThemeColor.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(12.dp)
                  )
                  .clickable {
                    supervisorCodeInput = code
                    validationError = null
                    successMessage = null
                  }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = label,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelectedChip) Color.White else primaryThemeColor
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Error Alert Banner
          AnimatedVisibility(visible = validationError != null) {
            Card(
              colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = null,
                  tint = Color(0xFFDC2626),
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = validationError ?: "",
                  color = Color(0xFF991B1B),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  lineHeight = 16.sp
                )
              }
            }
          }

          // Success Message Banner
          AnimatedVisibility(visible = successMessage != null) {
            Card(
              colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = Color(0xFF16A34A),
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = successMessage ?: "",
                  color = Color(0xFF166534),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  lineHeight = 16.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Primary Entry Button
          Button(
            onClick = {
              if (supervisorCodeInput.isBlank()) {
                validationError = "يرجى كتابة كود المشرف أو الرمز الأمني للدخول."
                return@Button
              }

              isVerifying = true
              validationError = null
              successMessage = null

              scope.launch {
                delay(300)
                val (isValid, msg) = viewModel.verifyAndLoginWithCode(
                  codeStr = supervisorCodeInput,
                  targetAssoc = chosenAssoc
                )

                isVerifying = false

                if (isValid) {
                  successMessage = "$msg\n🔒 تم تفعيل مسار Firestore المعزول: (associations/${chosenAssoc.id})"
                  delay(450)
                  onLoginSuccess()
                } else {
                  validationError = msg
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("submit_association_login_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryThemeColor),
            enabled = !isVerifying
          ) {
            if (isVerifying) {
              CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("جاري التحقق وتوجيه قاعدة البيانات...", fontSize = 13.sp)
            } else {
              Icon(imageVector = Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "دخول وتفعيل عزل البيانات في Firestore 🛡️",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Isolation & Cloud Architecture Info Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CloudDone,
              contentDescription = null,
              tint = QuranEmeraldDark,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "الأمان السحابي والعزل التام في Firestore:",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = QuranEmeraldDark
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "• كل جمعية تعمل ضمن حاوية بيانات مستقلة (Multi-Tenant Scoped Path).\n• حجب تام لأي محاولة لقراءة أو تعديل بيانات الجمعية الأخرى.\n• توجيه تلقائي لمسار: associations/${chosenAssoc.id}/...",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Switch to Advanced Login Button & APK Chip
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextButton(
          onClick = onSwitchToAdvancedLogin,
          modifier = Modifier.testTag("open_advanced_login_button")
        ) {
          Text(
            text = "⚙️ الدخول التقليدي بالبريد / الحسابات",
            fontSize = 11.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(QuranGoldSecondary)
            .clickable { showApkDialog = true }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("initial_screen_apk_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = null,
              tint = Color(0xFF382900),
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "APK (32 ميغابايت)",
              color = Color(0xFF382900),
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }

  if (showApkDialog) {
    ApkDownloadDialog(
      downloadUrl = viewModel.apkDownloadUrl.collectAsStateWithLifecycle().value,
      onDismiss = { showApkDialog = false }
    )
  }
}

@Composable
private fun AssociationOptionCard(
  association: AssociationType,
  isSelected: Boolean,
  onSelect: () -> Unit,
  modifier: Modifier = Modifier
) {
  val primaryColor = Color(association.primaryColorHex)
  val secondaryColor = Color(association.secondaryColorHex)

  val borderColor by animateColorAsState(
    targetValue = if (isSelected) primaryColor else Color(0xFFE2E8F0),
    label = "assoc_border_color"
  )
  val borderWidth by animateDpAsState(
    targetValue = if (isSelected) 2.5.dp else 1.dp,
    label = "assoc_border_width"
  )
  val backgroundColor by animateColorAsState(
    targetValue = if (isSelected) primaryColor.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface,
    label = "assoc_bg_color"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onSelect),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    border = androidx.compose.foundation.BorderStroke(borderWidth, borderColor),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Radio Icon
      Icon(
        imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
        contentDescription = null,
        tint = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(22.dp)
      )

      Spacer(modifier = Modifier.width(10.dp))

      // Association Icon Avatar
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(primaryColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Text(text = association.iconEmoji, fontSize = 22.sp)
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Details
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = association.shortName,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(secondaryColor.copy(alpha = 0.18f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = association.id,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = secondaryColor
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = association.motto,
          fontSize = 10.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2,
          lineHeight = 14.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "المشرف: ${association.supervisorName}",
          fontSize = 10.sp,
          fontWeight = FontWeight.SemiBold,
          color = primaryColor
        )
      }
    }
  }
}
