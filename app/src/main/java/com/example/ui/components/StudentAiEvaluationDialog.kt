package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ai.QuranAiService
import com.example.data.model.*
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import kotlinx.coroutines.launch

@Composable
fun StudentAiEvaluationDialog(
  student: Student,
  recitations: List<RecitationRecord>,
  attendance: List<AttendanceRecord>,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  val aiService = remember { QuranAiService() }

  val studentRecitations = remember(student.id, recitations) {
    recitations.filter { it.studentId == student.id }
  }
  val studentAttendance = remember(student.id, attendance) {
    attendance.filter { it.studentId == student.id }
  }

  val lastRecitation = studentRecitations.lastOrNull()
  val attendedDaysCount = studentAttendance.count { it.status == "حاضر" }

  var inputText by remember { mutableStateOf("") }
  var isGenerating by remember { mutableStateOf(false) }

  // Initial greeting and diagnostic synthesis
  val initialPrompt = remember(student.id) {
    val lastRecText = if (lastRecitation != null) {
      "آخر تسميع: سورة ${lastRecitation.surahName} (الآيات ${lastRecitation.fromAyah}-${lastRecitation.toAyah})، التقدير: ${lastRecitation.evaluation}، أخطاء التجويد: ${lastRecitation.mistakesCount}، درجة التجويد: ${lastRecitation.tajweedScore}/10، ملاحظات المحفظ: \"${lastRecitation.teacherNotes}\""
    } else {
      "لا توجد تسجيلات تسميع مرصودة اليوم."
    }

    "مرحباً بك يا شيخنا الفاضل! أنا **المساعد الذكي لتقييم الطلاب** 🤖\n\n" +
        "لقد قمت بتحليل السجل الأدائي والتسميع الصوتي للطالب **${student.name}**:\n" +
        "• **الحلقة:** ${student.groupName} • **المستوى:** ${student.level}\n" +
        "• **المحفوظ:** ${student.memorizedJuzCount} أجزاء • **الموضع الحالي:** سورة ${student.currentSurah} (آية ${student.currentAyah})\n" +
        "• **$lastRecText**\n\n" +
        "💡 **التوصية المبدئية:** ${
          if ((lastRecitation?.tajweedScore ?: 10) >= 9) {
            "أداء الطالب الصوتي متميز ومخارج الحروف متقنة. يُنصح بمواصلة حفظ الأوجه الجديدة مع ربط السور السابقة."
          } else {
            "يحتاج الطالب لتركيز إضافي على أحكام المدود ومخارج حروف الحلق. ينصح بجلسة تسميع بطيئة مع التكرار التراكمي."
          }
        }\n\n" +
        "تفضل باختيار أحد التحليلات السريعة أو اكتب أي استفسار تريده حول الطالب!"
  }

  val messages = remember {
    mutableStateListOf(
      AiChatMessage(
        sender = AiMessageSender.ASSISTANT,
        content = initialPrompt,
        category = AiCategory.SUPERVISOR_TIPS,
        suggestedFollowUps = listOf(
          "حلل لي أداءه التجويدي الصوتي",
          "ضع له خطة أسبوعية لعلاج الأخطاء",
          "كيف أتعامل مع تشتته أثناء التسميع؟"
        )
      )
    )
  }

  fun sendQuery(text: String) {
    val clean = text.trim()
    if (clean.isEmpty() || isGenerating) return

    val userMsg = AiChatMessage(
      sender = AiMessageSender.USER,
      content = clean,
      category = AiCategory.SUPERVISOR_TIPS
    )
    messages.add(userMsg)
    inputText = ""
    isGenerating = true

    coroutineScope.launch {
      listState.animateScrollToItem(messages.size - 1)
      val contextualQuery = "بخصوص الطالب ${student.name} (المحفوظ: ${student.memorizedJuzCount} أجزاء، السورة الحالية: ${student.currentSurah}، آخر تقييم: ${lastRecitation?.evaluation ?: "جيد"}، درجة التجويد: ${lastRecitation?.tajweedScore ?: 8}/10):\n$clean"
      val response = aiService.askQuranAi(contextualQuery, AiCategory.SUPERVISOR_TIPS, messages.toList())
      messages.add(response)
      isGenerating = false
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.9f)
        .clip(RoundedCornerShape(24.dp))
        .testTag("student_ai_evaluation_dialog"),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Dialog Header
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                listOf(QuranEmeraldDark, QuranEmeraldPrimary)
              )
            )
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(QuranGoldSecondary),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = Color(0xFF382900),
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "المساعد الذكي لتقييم الطالب",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Text(
                  text = "${student.name} • ${student.groupName}",
                  style = MaterialTheme.typography.bodySmall,
                  color = QuranGoldSecondary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            IconButton(onClick = onDismiss) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "إغلاق",
                tint = Color.White
              )
            }
          }
        }

        // Student Quick Telemetry Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(QuranEmeraldLight.copy(alpha = 0.5f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "📖 المحفوظ: ${student.memorizedJuzCount} أجزاء",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = QuranEmeraldDark
          )
          Text(
            text = "📍 الموضع: سورة ${student.currentSurah}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = QuranEmeraldDark
          )
          Text(
            text = "⭐ التجويد: ${lastRecitation?.tajweedScore ?: 9}/10",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB45309)
          )
        }

        // Quick Analysis Buttons
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          item {
            SuggestionChip(
              onClick = { sendQuery("حلل لي أداءه التجويدي الصوتي ومخارج الحروف بدقة") },
              label = { Text("🎙️ تحليل التجويد والصوت", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = SuggestionChipDefaults.suggestionChipColors(containerColor = QuranGoldLight)
            )
          }
          item {
            SuggestionChip(
              onClick = { sendQuery("ما هي الخطة العلاجية والواجب المنزلي المقترح للطالب؟") },
              label = { Text("📝 خطة علاجية مخصصة", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = SuggestionChipDefaults.suggestionChipColors(containerColor = QuranEmeraldLight)
            )
          }
          item {
            SuggestionChip(
              onClick = { sendQuery("توليد رسالة تشجيعية وتوجيهية لولي أمر الطالب") },
              label = { Text("📲 تقرير ذكي لولي الأمر", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFF1F5F9))
            )
          }
        }

        // Chat Message List
        LazyColumn(
          state = listState,
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
          contentPadding = PaddingValues(vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(messages) { msg ->
            val isUser = msg.sender == AiMessageSender.USER
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
            ) {
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isUser) QuranEmeraldPrimary else Color(0xFFF8FAFC)
                ),
                modifier = Modifier.widthIn(max = 320.dp)
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(
                    text = msg.content,
                    color = if (isUser) Color.White else Color(0xFF1E293B),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                  )

                  if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.End
                    ) {
                      IconButton(
                        onClick = {
                          clipboardManager.setText(AnnotatedString(msg.content))
                          Toast.makeText(context, "تم نسخ التقرير", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(20.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.ContentCopy,
                          contentDescription = "نسخ",
                          tint = Color.Gray,
                          modifier = Modifier.size(14.dp)
                        )
                      }
                    }
                  }
                }
              }

              if (!isUser && msg.suggestedFollowUps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                  items(msg.suggestedFollowUps) { followUp ->
                    SuggestionChip(
                      onClick = { sendQuery(followUp) },
                      label = { Text(followUp, fontSize = 10.sp, color = QuranEmeraldPrimary) }
                    )
                  }
                }
              }
            }
          }

          if (isGenerating) {
            item {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
              ) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  color = QuranEmeraldPrimary,
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "جاري إعداد التقييم الذكي للطالب...",
                  fontSize = 11.sp,
                  color = Color.Gray
                )
              }
            }
          }
        }

        // Bottom input bar
        Surface(
          tonalElevation = 4.dp,
          color = MaterialTheme.colorScheme.surface
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = inputText,
              onValueChange = { inputText = it },
              placeholder = { Text("اسأل الذكاء الاصطناعي عن الطالب...", fontSize = 11.sp) },
              modifier = Modifier
                .weight(1f)
                .testTag("ai_student_dialog_input"),
              shape = RoundedCornerShape(20.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = QuranEmeraldPrimary,
                unfocusedBorderColor = Color(0xFFCBD5E1)
              ),
              singleLine = true,
              keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
              keyboardActions = KeyboardActions(onSend = { sendQuery(inputText) })
            )

            FloatingActionButton(
              onClick = { sendQuery(inputText) },
              modifier = Modifier
                .size(42.dp)
                .testTag("ai_student_dialog_send"),
              containerColor = QuranEmeraldPrimary,
              contentColor = Color.White,
              shape = CircleShape
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "إرسال",
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }
}
