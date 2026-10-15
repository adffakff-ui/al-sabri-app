package com.example.ui.screens.ai

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.ai.QuranAiService
import com.example.data.model.AiCategory
import com.example.data.model.AiChatMessage
import com.example.data.model.AiMessageSender
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldSecondary
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranAiAssistantScreen(
  viewModel: MainViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onNavigateBack() }

  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  val aiService = remember { QuranAiService() }

  var selectedCategory by remember { mutableStateOf(AiCategory.ALL) }
  var inputText by remember { mutableStateOf("") }
  var isGenerating by remember { mutableStateOf(false) }

  val messages = remember {
    mutableStateListOf(
      AiChatMessage(
        sender = AiMessageSender.ASSISTANT,
        content = "أهلاً بك في **مساعد الذكاء الاصطناعي القرآني** بمجمع حلقات هائل سعيد أنعم 🌿\n\nأنا هنا لمساعدتك في استفسارات التجويد، تفسير وتدبر الآيات، ضبط المتشابهات اللفظية، ووضع خطط الحفظ المتقنة.\n\nاختر أحد المقترحات أدناه أو اطرح سؤالك مباشرة!",
        category = AiCategory.ALL,
        suggestedFollowUps = listOf(
          "اشرح لي أحكام النون الساكنة",
          "تفسير سورة الملك",
          "كيف أضبط المتشابهات؟"
        )
      )
    )
  }

  fun sendMessage(promptText: String, cat: AiCategory = selectedCategory) {
    val clean = promptText.trim()
    if (clean.isEmpty() || isGenerating) return

    val userMsg = AiChatMessage(
      sender = AiMessageSender.USER,
      content = clean,
      category = cat
    )
    messages.add(userMsg)
    inputText = ""
    isGenerating = true

    coroutineScope.launch {
      listState.animateScrollToItem(messages.size - 1)
      val response = aiService.askQuranAi(clean, cat, messages.toList())
      messages.add(response)
      isGenerating = false
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Scaffold(
    modifier = modifier.testTag("quran_ai_assistant_screen"),
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    listOf(QuranGoldSecondary, QuranEmeraldPrimary)
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "الذكاء الاصطناعي القرآني",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = QuranEmeraldDark
              )
              Text(
                text = "مساعد التجويد والتفسير والحفظ الذكي ⚡",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 11.sp
              )
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "الرجوع"
            )
          }
        },
        actions = {
          IconButton(onClick = {
            messages.clear()
            messages.add(
              AiChatMessage(
                sender = AiMessageSender.ASSISTANT,
                content = "تم بدء جلسة محادثة جديدة. تفضل بطرح سؤالك القرآني 🌿",
                category = AiCategory.ALL
              )
            )
            Toast.makeText(context, "تم مسح المحادثة وبدء جلسة جديدة", Toast.LENGTH_SHORT).show()
          }) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "جلسة جديدة")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      Surface(
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = inputText,
              onValueChange = { inputText = it },
              modifier = Modifier
                .weight(1f)
                .testTag("ai_input_text_field"),
              placeholder = {
                Text(
                  "اسأل عن حكم تجويدي، تفسير، أو خطة حفظ...",
                  fontSize = 12.sp,
                  color = Color.Gray
                )
              },
              shape = RoundedCornerShape(24.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = QuranEmeraldPrimary,
                unfocusedBorderColor = Color(0xFFCBD5E1)
              ),
              maxLines = 3,
              keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
              keyboardActions = KeyboardActions(onSend = { sendMessage(inputText) })
            )

            FloatingActionButton(
              onClick = { sendMessage(inputText) },
              modifier = Modifier
                .size(48.dp)
                .testTag("ai_send_button"),
              containerColor = if (isGenerating) Color.Gray else QuranEmeraldPrimary,
              contentColor = Color.White,
              shape = CircleShape
            ) {
              if (isGenerating) {
                CircularProgressIndicator(
                  modifier = Modifier.size(20.dp),
                  color = Color.White,
                  strokeWidth = 2.dp
                )
              } else {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Send,
                  contentDescription = "إرسال",
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Categories horizontal selector
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFFF8FAFC))
          .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        items(AiCategory.values()) { cat ->
          val isSelected = selectedCategory == cat
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = cat },
            label = {
              Text(
                text = "${cat.iconEmoji} ${cat.title}",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = QuranEmeraldPrimary,
              selectedLabelColor = Color.White
            )
          )
        }
      }

      // Default quick suggestions bar
      AnimatedVisibility(visible = messages.size <= 2) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            text = "⚡ أسئلة وموضوعات مقترحة سريعة:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = QuranEmeraldDark,
            modifier = Modifier.padding(bottom = 6.dp)
          )
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
          ) {
            val filteredSuggestions = if (selectedCategory == AiCategory.ALL) {
              QuranAiService.DEFAULT_SUGGESTIONS
            } else {
              QuranAiService.DEFAULT_SUGGESTIONS.filter { it.category == selectedCategory }
            }

            items(filteredSuggestions) { suggestion ->
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = QuranEmeraldLight.copy(alpha = 0.35f)),
                modifier = Modifier
                  .widthIn(max = 240.dp)
                  .clickable { sendMessage(suggestion.prompt, suggestion.category) }
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(text = suggestion.iconEmoji, fontSize = 18.sp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = suggestion.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = QuranEmeraldDark
                  )
                }
              }
            }
          }
        }
      }

      // Chat message stream
      LazyColumn(
        state = listState,
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(messages) { msg ->
          AiMessageBubble(
            message = msg,
            onCopy = {
              clipboardManager.setText(AnnotatedString(msg.content))
              Toast.makeText(context, "تم نسخ النص إلى الحافظة", Toast.LENGTH_SHORT).show()
            },
            onFollowUpClick = { followUp ->
              sendMessage(followUp, msg.category)
            }
          )
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
                text = "الذكاء الاصطناعي القرآني يكتب الإجابة الآن...",
                fontSize = 12.sp,
                color = Color.Gray
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun AiMessageBubble(
  message: AiChatMessage,
  onCopy: () -> Unit,
  onFollowUpClick: (String) -> Unit
) {
  val isUser = message.sender == AiMessageSender.USER

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
  ) {
    Row(
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
      modifier = Modifier.fillMaxWidth(0.95f)
    ) {
      if (!isUser) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(QuranEmeraldPrimary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
      }

      Card(
        shape = RoundedCornerShape(
          topStart = 16.dp,
          topEnd = 16.dp,
          bottomStart = if (isUser) 16.dp else 4.dp,
          bottomEnd = if (isUser) 4.dp else 16.dp
        ),
        colors = CardDefaults.cardColors(
          containerColor = if (isUser) QuranEmeraldPrimary else Color(0xFFF1F5F9)
        ),
        modifier = Modifier.weight(1f, fill = false)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = message.content,
            color = if (isUser) Color.White else Color(0xFF1E293B),
            fontSize = 13.sp,
            lineHeight = 20.sp
          )

          if (!isUser) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              IconButton(
                onClick = onCopy,
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.ContentCopy,
                  contentDescription = "نسخ الإجابة",
                  tint = Color.Gray,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }
    }

    // Follow up chips
    if (!isUser && message.suggestedFollowUps.isNotEmpty()) {
      Spacer(modifier = Modifier.height(6.dp))
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(start = 40.dp)
      ) {
        items(message.suggestedFollowUps) { followUp ->
          SuggestionChip(
            onClick = { onFollowUpClick(followUp) },
            label = { Text(followUp, fontSize = 11.sp, color = QuranEmeraldPrimary) },
            shape = RoundedCornerShape(16.dp),
            colors = SuggestionChipDefaults.suggestionChipColors(
              containerColor = QuranEmeraldLight.copy(alpha = 0.5f)
            )
          )
        }
      }
    }
  }
}
