package com.example.data.model

import java.util.UUID

enum class AiMessageSender {
  USER,
  ASSISTANT,
  SYSTEM
}

enum class AiCategory(val title: String, val iconEmoji: String) {
  ALL("الكل", "✨"),
  TAJWEED("أحكام التجويد", "🎙️"),
  TAFSIR("التفسير والتدبر", "📖"),
  MEMORIZATION("خطة الحفظ والمسابقات", "🧠"),
  MUTASHABIHAT("المتشابهات اللفظية", "🔍"),
  SUPERVISOR_TIPS("إرشادات المشرفين والمعلمين", "👨‍🏫")
}

data class AiChatMessage(
  val id: String = UUID.randomUUID().toString(),
  val sender: AiMessageSender,
  val content: String,
  val timestamp: Long = System.currentTimeMillis(),
  val category: AiCategory = AiCategory.ALL,
  val suggestedFollowUps: List<String> = emptyList(),
  val isLoading: Boolean = false
)

data class AiPromptSuggestion(
  val title: String,
  val prompt: String,
  val category: AiCategory,
  val iconEmoji: String
)
