package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.local.SampleDataProvider
import com.example.data.model.QariDirectory
import com.example.data.model.QariReference
import com.example.data.model.QuranData
import com.example.data.model.RecitationRecord
import com.example.data.model.Student
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranError
import com.example.ui.theme.QuranErrorLight
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.ui.theme.QuranSuccess
import com.example.ui.theme.QuranSuccessLight
import com.example.ui.theme.QuranSurfaceVariant
import com.example.ui.theme.QuranWarning
import com.example.ui.theme.QuranWarningLight
import com.example.util.AudioRecordManager
import com.example.util.AudioState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecitationEvaluationDialog(
  initialStudent: Student? = null,
  studentsList: List<Student> = emptyList(),
  onDismiss: () -> Unit,
  onSave: (RecitationRecord) -> Unit
) {
  val context = LocalContext.current
  val audioManager = remember { AudioRecordManager(context) }

  DisposableEffect(Unit) {
    onDispose {
      audioManager.release()
    }
  }

  var selectedStudent by remember { mutableStateOf(initialStudent ?: studentsList.firstOrNull()) }
  var isStudentPickerExpanded by remember { mutableStateOf(false) }

  var recitationType by remember { mutableStateOf("حفظ جديد") }
  var surahName by remember { mutableStateOf(selectedStudent?.currentSurah ?: "البقرة") }
  var surahSearchQuery by remember { mutableStateOf("") }
  var isSurahPickerExpanded by remember { mutableStateOf(false) }

  var fromAyah by remember { mutableIntStateOf(selectedStudent?.currentAyah ?: 1) }
  var toAyah by remember { mutableIntStateOf((selectedStudent?.currentAyah ?: 1) + 10) }

  // Ten Recitations / Qira'at selection
  val qiraatOptions = listOf(
    "حفص عن عاصم (المشهورة)",
    "شعبة عن عاصم",
    "ورش عن نافع المدني",
    "قالون عن نافع المدني",
    "الدوري عن أبي عمرو البصري",
    "السوسي عن أبي عمرو البصري",
    "البزي عن ابن كثير المكي",
    "قنبل عن ابن كثير المكي",
    "هشام عن ابن عامر الشامي",
    "ابن ذكوان عن ابن عامر الشامي",
    "خلف عن حمزة الكوفي",
    "خلاد عن حمزة الكوفي",
    "أبو الحارث عن الكسائي",
    "الدوري عن الكسائي",
    "ابن وردان عن أبي جعفر",
    "ابن جماز عن أبي جعفر",
    "رويس عن يعقوب الحضرمي",
    "رَوح عن يعقوب الحضرمي",
    "إسحاق عن خلف العاشر",
    "إدريس عن خلف العاشر"
  )
  var selectedQiraah by remember { mutableStateOf(qiraatOptions.first()) }
  var isQiraahDropdownExpanded by remember { mutableStateOf(false) }

  // Audio Recording State
  var recordedAudioPath by remember { mutableStateOf<String?>(null) }
  var isRecording by remember { mutableStateOf(false) }
  var isPlayingRecording by remember { mutableStateOf(false) }

  // Reference Qari State
  var selectedQari by remember { mutableStateOf(QariDirectory.qaris.first()) }
  var isPlayingQariAudio by remember { mutableStateOf(false) }
  var isQariDropdownExpanded by remember { mutableStateOf(false) }

  // Level assessment metrics (out of 10)
  var makharijScore by remember { mutableIntStateOf(9) }
  var tajweedScore by remember { mutableIntStateOf(9) }
  var fluencyScore by remember { mutableIntStateOf(9) }
  var voiceTuneScore by remember { mutableIntStateOf(9) }

  val levelAssessment = remember(makharijScore, tajweedScore, fluencyScore, voiceTuneScore) {
    QariDirectory.calculateStudentLevel(makharijScore, tajweedScore, fluencyScore, voiceTuneScore)
  }

  // Evaluation Score: 0 to 100%
  var scorePercent by remember { mutableFloatStateOf(levelAssessment.percentage.toFloat()) }
  val evaluationGrade by remember(scorePercent) {
    derivedStateOf {
      when {
        scorePercent >= 90f -> "ممتاز"
        scorePercent >= 80f -> "جيد جداً"
        scorePercent >= 70f -> "جيد"
        scorePercent >= 60f -> "مقبول"
        else -> "يحتاج إعادة"
      }
    }
  }
  var teacherNotes by remember { mutableStateOf("") }
  var validationError by remember { mutableStateOf<String?>(null) }

  val maxAyahInSurah = QuranData.getSurahAyahCount(surahName)
  val surahNumber = QuranData.surahs.firstOrNull { it.name == surahName }?.number ?: 1

  // Permission Launcher for Audio Recording
  val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      val path = audioManager.startRecording()
      if (path != null) {
        isRecording = true
        recordedAudioPath = path
      }
    } else {
      validationError = "يرجى منح إذن الميكروفون لتسجيل صوت الطالب."
    }
  }

  val recitationTypes = listOf("حفظ جديد", "مراجعة صغرى", "مراجعة كبرى", "تلاوة وتجويد")

  val filteredSurahs = remember(surahSearchQuery) {
    if (surahSearchQuery.isBlank()) {
      QuranData.surahs
    } else {
      QuranData.surahs.filter {
        it.name.contains(surahSearchQuery.trim()) ||
            it.number.toString().contains(surahSearchQuery.trim())
      }
    }
  }

  AlertDialog(
    onDismissRequest = {
      audioManager.release()
      onDismiss()
    },
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(QuranEmeraldLight),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Grade,
              contentDescription = null,
              tint = QuranEmeraldPrimary,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "نموذج تقييم التسميع والتسجيل الصوتي",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Text(
              text = "ربط القراءات العشر وتحديد مستوى الطالب",
              style = MaterialTheme.typography.bodySmall,
              color = QuranEmeraldPrimary,
              fontSize = 11.sp
            )
          }
        }
        IconButton(
          onClick = {
            audioManager.release()
            onDismiss()
          },
          modifier = Modifier.size(28.dp)
        ) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // 1. Student Selection
        if (studentsList.isNotEmpty()) {
          Text(text = "١. تحديد الطالب:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          ExposedDropdownMenuBox(
            expanded = isStudentPickerExpanded,
            onExpandedChange = { isStudentPickerExpanded = !isStudentPickerExpanded }
          ) {
            OutlinedTextField(
              value = selectedStudent?.name ?: "اختر الطالب المسجل",
              onValueChange = {},
              readOnly = true,
              label = { Text("اسم الطالب *") },
              leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = QuranEmeraldPrimary)
              },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStudentPickerExpanded) },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .testTag("evaluation_student_picker")
            )
            ExposedDropdownMenu(
              expanded = isStudentPickerExpanded,
              onDismissRequest = { isStudentPickerExpanded = false }
            ) {
              studentsList.forEach { st ->
                DropdownMenuItem(
                  text = {
                    Column {
                      Text(text = st.name, fontWeight = FontWeight.SemiBold)
                      Text(
                        text = "${st.groupName} • ${st.level}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  },
                  onClick = {
                    selectedStudent = st
                    surahName = st.currentSurah
                    fromAyah = st.currentAyah
                    toAyah = st.currentAyah + 10
                    isStudentPickerExpanded = false
                  }
                )
              }
            }
          }
        }

        // 2. Surah & Ayahs Selection
        Text(text = "٢. تحديد السورة والآيات:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        ExposedDropdownMenuBox(
          expanded = isSurahPickerExpanded,
          onExpandedChange = { isSurahPickerExpanded = !isSurahPickerExpanded }
        ) {
          OutlinedTextField(
            value = "سورة $surahName ($maxAyahInSurah آية)",
            onValueChange = {},
            readOnly = true,
            label = { Text("السورة الكريمة *") },
            leadingIcon = {
              Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = QuranEmeraldPrimary)
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSurahPickerExpanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
              .testTag("evaluation_surah_picker")
          )
          ExposedDropdownMenu(
            expanded = isSurahPickerExpanded,
            onDismissRequest = {
              isSurahPickerExpanded = false
              surahSearchQuery = ""
            }
          ) {
            Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
              OutlinedTextField(
                value = surahSearchQuery,
                onValueChange = { surahSearchQuery = it },
                placeholder = { Text("بحث باسم السورة...") },
                leadingIcon = {
                  Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
            }
            filteredSurahs.take(15).forEach { s ->
              DropdownMenuItem(
                text = {
                  Text(text = "${s.number}. سورة ${s.name} (${s.ayahCount} آية)")
                },
                onClick = {
                  surahName = s.name
                  fromAyah = 1
                  toAyah = minOf(15, s.ayahCount)
                  isSurahPickerExpanded = false
                  surahSearchQuery = ""
                }
              )
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = fromAyah.toString(),
            onValueChange = {
              fromAyah = it.toIntOrNull()?.coerceIn(1, maxAyahInSurah) ?: 1
            },
            label = { Text("من آية") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = toAyah.toString(),
            onValueChange = {
              toAyah = it.toIntOrNull()?.coerceIn(1, maxAyahInSurah) ?: maxAyahInSurah
            },
            label = { Text("إلى آية") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        // 3. Selection of Qira'ah from Ten Recitations (القراءات العشر المتواترة)
        Text(text = "٣. القراءة والرواية (من القراءات العشر):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        ExposedDropdownMenuBox(
          expanded = isQiraahDropdownExpanded,
          onExpandedChange = { isQiraahDropdownExpanded = !isQiraahDropdownExpanded }
        ) {
          OutlinedTextField(
            value = selectedQiraah,
            onValueChange = {},
            readOnly = true,
            label = { Text("الرواية القرآنية") },
            leadingIcon = {
              Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = QuranGoldSecondary)
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isQiraahDropdownExpanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = isQiraahDropdownExpanded,
            onDismissRequest = { isQiraahDropdownExpanded = false }
          ) {
            qiraatOptions.forEach { qiraah ->
              DropdownMenuItem(
                text = { Text(qiraah, fontSize = 13.sp) },
                onClick = {
                  selectedQiraah = qiraah
                  isQiraahDropdownExpanded = false
                }
              )
            }
          }
        }

        // 4. Student Audio Recording Section (ميزة التسجيل الصوتي للطالب)
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = QuranEmeraldLight.copy(alpha = 0.6f))
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Mic,
                  contentDescription = null,
                  tint = if (isRecording) QuranError else QuranEmeraldPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "تسجيل صوت الطالب أثناء التسميع:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }
              if (isRecording) {
                Text(
                  text = "جارٍ التسجيل...",
                  color = QuranError,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Record / Stop Record Button
              Button(
                onClick = {
                  if (isRecording) {
                    val path = audioManager.stopRecording()
                    isRecording = false
                    recordedAudioPath = path
                  } else {
                    val permissionCheck = ContextCompat.checkSelfPermission(
                      context,
                      Manifest.permission.RECORD_AUDIO
                    )
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                      val path = audioManager.startRecording()
                      if (path != null) {
                        isRecording = true
                        recordedAudioPath = path
                      }
                    } else {
                      permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                  }
                },
                modifier = Modifier
                  .weight(1f)
                  .height(40.dp)
                  .testTag("record_audio_toggle_btn"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isRecording) QuranError else QuranEmeraldPrimary
                ),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(
                  imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (isRecording) "إيقاف التسجيل" else "بدء تسجيل صوت الطالب",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              // Play recording button
              recordedAudioPath?.let { audioPath ->
                OutlinedButton(
                  onClick = {
                    if (isPlayingRecording) {
                      audioManager.stopPlayback()
                      isPlayingRecording = false
                    } else {
                      isPlayingRecording = true
                      audioManager.playAudio(audioPath) {
                        isPlayingRecording = false
                      }
                    }
                  },
                  modifier = Modifier.height(40.dp),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(
                    imageVector = if (isPlayingRecording) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = if (isPlayingRecording) "إيقاف" else "سماع التسجيل",
                    fontSize = 11.sp
                  )
                }
              }
            }
          }
        }

        // 5. Connect to Qari & Level Assessment (ربط القراء لمعرفة مستوى الطالب)
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = QuranGoldLight.copy(alpha = 0.5f))
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Headphones, contentDescription = null, tint = QuranGoldSecondary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "القارئ المرجعي لمقارنة الأداء:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }

              // Play Qari recitation online button
              Button(
                onClick = {
                  if (isPlayingQariAudio) {
                    audioManager.stopPlayback()
                    isPlayingQariAudio = false
                  } else {
                    isPlayingQariAudio = true
                    val qariUrl = selectedQari.getSurahAudioUrl(surahNumber)
                    audioManager.playAudio(qariUrl) {
                      isPlayingQariAudio = false
                    }
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = QuranGoldSecondary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp)
              ) {
                Icon(
                  imageVector = if (isPlayingQariAudio) Icons.Default.Pause else Icons.Default.VolumeUp,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (isPlayingQariAudio) "إيقاف" else "استمع للشيخ",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            // Qari Selection dropdown
            ExposedDropdownMenuBox(
              expanded = isQariDropdownExpanded,
              onExpandedChange = { isQariDropdownExpanded = !isQariDropdownExpanded }
            ) {
              OutlinedTextField(
                value = "${selectedQari.name} (${selectedQari.style})",
                onValueChange = {},
                readOnly = true,
                label = { Text("اختر القارئ للمقارنة") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isQariDropdownExpanded) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .menuAnchor()
                  .fillMaxWidth()
              )
              ExposedDropdownMenu(
                expanded = isQariDropdownExpanded,
                onDismissRequest = { isQariDropdownExpanded = false }
              ) {
                QariDirectory.qaris.forEach { qari ->
                  DropdownMenuItem(
                    text = {
                      Column {
                        Text(qari.name, fontWeight = FontWeight.Bold)
                        Text(qari.style, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                      }
                    },
                    onClick = {
                      selectedQari = qari
                      isQariDropdownExpanded = false
                      if (isPlayingQariAudio) {
                        audioManager.stopPlayback()
                        isPlayingQariAudio = false
                      }
                    }
                  )
                }
              }
            }

            // Level assessment Rubric
            Text(
              text = "محاور تقييم مستوى الطالب مقابل أداء القارئ:",
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              OutlinedTextField(
                value = makharijScore.toString(),
                onValueChange = { makharijScore = it.toIntOrNull()?.coerceIn(1, 10) ?: 10 },
                label = { Text("المخارج") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
              )
              OutlinedTextField(
                value = tajweedScore.toString(),
                onValueChange = { tajweedScore = it.toIntOrNull()?.coerceIn(1, 10) ?: 10 },
                label = { Text("التجويد") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
              )
              OutlinedTextField(
                value = fluencyScore.toString(),
                onValueChange = { fluencyScore = it.toIntOrNull()?.coerceIn(1, 10) ?: 10 },
                label = { Text("الطلاقة") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
              )
              OutlinedTextField(
                value = voiceTuneScore.toString(),
                onValueChange = { voiceTuneScore = it.toIntOrNull()?.coerceIn(1, 10) ?: 10 },
                label = { Text("الترتيل") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
              )
            }

            // Resulting Level Badge
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(QuranEmeraldPrimary)
                .padding(10.dp)
            ) {
              Column {
                Text(
                  text = "🎯 تصنيف مستوى الطالب: ${levelAssessment.levelTitle}",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
                Text(
                  text = "💡 التوجيه: ${levelAssessment.advice}",
                  color = QuranGoldSecondary,
                  fontSize = 11.sp
                )
              }
            }
          }
        }

        // 6. Overall Evaluation Score & Grade
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "٦. الدرجة الإجمالية للتسميع:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(
            text = "${scorePercent.toInt()}%  ($evaluationGrade)",
            fontWeight = FontWeight.Bold,
            color = QuranEmeraldPrimary,
            fontSize = 14.sp
          )
        }
        Slider(
          value = scorePercent,
          onValueChange = { scorePercent = it },
          valueRange = 40f..100f,
          steps = 11,
          colors = SliderDefaults.colors(
            thumbColor = QuranEmeraldPrimary,
            activeTrackColor = QuranEmeraldPrimary
          )
        )

        // Teacher Notes
        OutlinedTextField(
          value = teacherNotes,
          onValueChange = { teacherNotes = it },
          label = { Text("ملاحظات وتوجيهات المعلم") },
          placeholder = { Text("ملاحظات الأداء وتنبيهات الحفظ...") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          minLines = 2
        )

        validationError?.let { err ->
          Text(text = err, color = QuranError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val student = selectedStudent
          if (student == null) {
            validationError = "يرجى اختيار الطالب."
            return@Button
          }
          if (fromAyah > toAyah) {
            validationError = "رقم آية البداية يجب أن يكون أقل من أو يساوي آية النهاية."
            return@Button
          }

          val notesWithQari = if (teacherNotes.isNotBlank()) {
            "$teacherNotes • [الرواية: $selectedQiraah | القارئ المرجعي: ${selectedQari.name}]"
          } else {
            "الرواية: $selectedQiraah • القارئ المرجعي: ${selectedQari.name}"
          }

          val record = RecitationRecord(
            studentId = student.id,
            studentName = student.name,
            date = SampleDataProvider.todayDate,
            recitationType = recitationType,
            surahName = surahName,
            fromAyah = fromAyah,
            toAyah = toAyah,
            evaluation = evaluationGrade,
            scorePercent = scorePercent.toInt(),
            mistakesCount = 0,
            tajweedScore = tajweedScore,
            qiraahName = selectedQiraah,
            qariReferenceName = selectedQari.name,
            audioFilePath = recordedAudioPath,
            teacherNotes = notesWithQari
          )
          audioManager.release()
          onSave(record)
        },
        colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
        shape = RoundedCornerShape(10.dp)
      ) {
        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("اعتماد وحفظ التقييم", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(
        onClick = {
          audioManager.release()
          onDismiss()
        }
      ) {
        Text("إلغاء")
      }
    }
  )
}
