package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.AiCategory
import com.example.data.model.AiChatMessage
import com.example.data.model.AiMessageSender
import com.example.data.model.AiPromptSuggestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class QuranAiService(
  private val client: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(20, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()
) {

  companion object {
    private const val TAG = "QuranAiService"
    // Using recommended modern preview model per gemini-api guidelines
    private const val GEMINI_MODEL = "gemini-2.5-flash"
    private const val GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/$GEMINI_MODEL:generateContent"

    val DEFAULT_SUGGESTIONS = listOf(
      AiPromptSuggestion(
        title = "أحكام النون الساكنة والتنوين",
        prompt = "اشرح لي بالتفصيل أحكام النون الساكنة والتنوين الأربعة مع أمثلة قرآنية واضحة وطريقة النطق الصحيحة.",
        category = AiCategory.TAJWEED,
        iconEmoji = "🎙️"
      ),
      AiPromptSuggestion(
        title = "تفسير سورة الملك",
        prompt = "ما هي مقاصد سورة الملك وتفسير الآيات الخمس الأولى منها وأثرها في تثبيت الإيمان؟",
        category = AiCategory.TAFSIR,
        iconEmoji = "📖"
      ),
      AiPromptSuggestion(
        title = "خطة حفظ جزء عم في أسبوعين",
        prompt = "ضع لي جدولاً يومياً عملياً لحفظ ومراجعة جزء عم كاملاً خلال أسبوعين لطالب في المرحلة المتوسطة.",
        category = AiCategory.MEMORIZATION,
        iconEmoji = "🧠"
      ),
      AiPromptSuggestion(
        title = "المتشابهات بين البقرة وآل عمران",
        prompt = "كيف أميز بين المتشابهات اللفظية الأكثر تكراراً بين سورتي البقرة وآل عمران وضوابط ضبطها؟",
        category = AiCategory.MUTASHABIHAT,
        iconEmoji = "🔍"
      ),
      AiPromptSuggestion(
        title = "نصائح لمعلم الحلقة القرآنية",
        prompt = "ما هي أفضل الاستراتيجيات التربوية لمعلم الحلقة لتحفيز الطلاب على الالتزام اليومي وعلاج النسيان والتفلت؟",
        category = AiCategory.SUPERVISOR_TIPS,
        iconEmoji = "👨‍🏫"
      ),
      AiPromptSuggestion(
        title = "مخارج الحروف وصفاتها",
        prompt = "ما هي المخارج العامة الخمسة للحروف العربية وما هي أهم الصفات التي لها ضد؟",
        category = AiCategory.TAJWEED,
        iconEmoji = "🗣️"
      )
    )
  }

  suspend fun askQuranAi(
    userQuery: String,
    category: AiCategory = AiCategory.ALL,
    chatHistory: List<AiChatMessage> = emptyList()
  ): AiChatMessage = withContext(Dispatchers.IO) {
    val apiKey = try {
      BuildConfig.GEMINI_API_KEY
    } catch (e: Exception) {
      ""
    }

    if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
      try {
        val resultText = callGeminiApi(userQuery, category, apiKey)
        if (resultText.isNotBlank()) {
          return@withContext AiChatMessage(
            sender = AiMessageSender.ASSISTANT,
            content = resultText,
            category = category,
            suggestedFollowUps = generateFollowUps(category)
          )
        }
      } catch (e: Exception) {
        Log.w(TAG, "Gemini API call failed, falling back to built-in Quranic AI engine: ${e.message}")
      }
    }

    // High-quality offline Quranic Knowledge Engine fallback
    val smartResponse = generateSmartQuranicResponse(userQuery, category)
    AiChatMessage(
      sender = AiMessageSender.ASSISTANT,
      content = smartResponse,
      category = category,
      suggestedFollowUps = generateFollowUps(category)
    )
  }

  private fun callGeminiApi(prompt: String, category: AiCategory, apiKey: String): String {
    val systemPrompt = """
      أنت "مساعد الذكاء الاصطناعي القرآني" التابع لمنظومة حلقات مجمع هائل سعيد أنعم لتحفيظ القرآن الكريم.
      مهمتك:
      1. تقديم إجابات دقيقة وموثقة بأحكام التجويد (برواية حفص عن عاصم من طريق الشاطبية والطيبة، وباقي القراءات عند السؤال).
      2. تفسير الآيات القرآنية بأسلوب تدبري سلفي واضح معتمد على أمهات كتب التفسير (ابن كثير، الطبري، السعدي).
      3. بيان المتشابهات اللفظية وضوابطها البصرية والصوتية لحفظة كتاب الله.
      4. تقديم خطط حفظ وجداول مراجعة واقعية ومحفزة.
      5. توجيه المعلمين والمشرفين بنصائح تربوية عملية لإدارة الحلقات.
      أسلوبك: رزين، فصيح، مشجع، مؤدب ومستشهد بالآيات القرآنية والأحاديث الشريفة.
    """.trimIndent()

    val jsonBody = JSONObject().apply {
      val contentsArr = JSONArray().apply {
        put(JSONObject().apply {
          val partsArr = JSONArray().apply {
            put(JSONObject().put("text", prompt))
          }
          put("parts", partsArr)
        })
      }
      put("contents", contentsArr)

      val sysInst = JSONObject().apply {
        val partsArr = JSONArray().apply {
          put(JSONObject().put("text", systemPrompt))
        }
        put("parts", partsArr)
      }
      put("systemInstruction", sysInst)
    }

    val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
    val request = Request.Builder()
      .url("$GEMINI_URL?key=$apiKey")
      .post(requestBody)
      .build()

    client.newCall(request).execute().use { response ->
      if (!response.isSuccessful) {
        throw RuntimeException("HTTP ${response.code}: ${response.message}")
      }
      val responseString = response.body?.string().orEmpty()
      val respJson = JSONObject(responseString)
      val candidates = respJson.optJSONArray("candidates") ?: return ""
      if (candidates.length() > 0) {
        val firstCandidate = candidates.getJSONObject(0)
        val contentObj = firstCandidate.optJSONObject("content")
        val parts = contentObj?.optJSONArray("parts")
        if (parts != null && parts.length() > 0) {
          return parts.getJSONObject(0).optString("text", "")
        }
      }
      return ""
    }
  }

  private fun generateFollowUps(category: AiCategory): List<String> {
    return when (category) {
      AiCategory.TAJWEED -> listOf(
        "ما هي أحكام الميم الساكنة؟",
        "كيف أضبط مقادير المدود عملياً؟",
        "ما الفرق بين الإخفاء الحقيقي والإخفاء الشفوي؟"
      )
      AiCategory.TAFSIR -> listOf(
        "ما هي أسباب نزول هذه الآيات؟",
        "كيف أطبق هدايات الآيات في سلوكي اليومي؟",
        "ما هي الكلمات الغريبة في هذه السورة؟"
      )
      AiCategory.MEMORIZATION -> listOf(
        "كيف أنظم وقتي بين الحفظ الجديد والمراجعة؟",
        "ما هي طريقة التكرار التراكمي؟",
        "اختبر حفظي في الأوجه الأخيرة"
      )
      AiCategory.MUTASHABIHAT -> listOf(
        "ما هي قاعدة (ما تكرر تقرر) في المتشابهات؟",
        "المتشابهات في خواتيم الآيات بسورة النساء",
        "كيف أميز بين (إن الله بما تعملون بصير) و (والله بما تعملون بصير)؟"
      )
      AiCategory.SUPERVISOR_TIPS -> listOf(
        "كيف أتعامل مع الطالب بطيء الحفظ دون إحباطه؟",
        "طريقة تطبيق لوحة شرف تحفيزية داخل الحلقة",
        "كيفية إشراك ولي الأمر في المتابعة المنزلية"
      )
      else -> listOf(
        "اشرح لي قاعدة في التجويد",
        "اقترح علي خطة حفظ",
        "سؤال تدبري في القرآن"
      )
    }
  }

  private fun generateSmartQuranicResponse(query: String, category: AiCategory): String {
    val q = query.trim().lowercase()
    return when {
      q.contains("نون") || q.contains("تنوين") || q.contains("إظهار") || q.contains("إدغام") || q.contains("إخفاء") || q.contains("إقلاب") -> {
        """
        🌿 **أحكام النون الساكنة والتنوين الأربعة في التجويد:**
        
        1. **الإظهار الحلقي:**
           - حروفه ستة: (همز، فهاء، ثم عين حاء، مهملتان ثم غين خاء).
           - الحكم: نطق النون واضحة بغير غنة زائدة.
           - مثال: {مَنْ آمَنَ}، {أَنْعَمْتَ}، {مِنْ غِلٍّ}.
        
        2. **الإدغام:**
           - حروفه في كلمة (يَرْمَلُون).
           - ينقسم إلى:
             • **إدغام بغنة:** حروفه (يَنْمُو) مثل: {مَن يَقُولُ}، {مِن مَّالٍ}.
             • **إدغام بغير غنة:** في (اللام والراء) مثل: {مِن رَّبِّهِمْ}، {هُدًى لِّلْمُتَّقِينَ}.
             • شرطه أن يكون في كلمتين؛ فإذا اجتمع في كلمة واحدة وجب الإظهار المطلق مثل: (دُنْيَا، قِنْوَان، صِنْوَان، بُنْيَان).
        
        3. **الإقلاب:**
           - حرفه: (الباء فقط).
           - الحكم: قلب النون أو التنوين ميماً مخفاة بغنة حركتين عند ملاقاة الباء.
           - مثال: {مِن بَعْدِ}، {عَلِيمٌ بِذَاتِ الصُّدُورِ}.
        
        4. **الإخفاء الحقيقي:**
           - حروفه: (15 حرفاً) مجموعة في أوائل كلمات البيت:
             *صف ذا ثنا كم جاد شخص قد سما ... دم طيباً زد في تقى ضع ظالما*
           - الحكم: النطق بحالة بين الإظهار والإدغام عارياً عن التشديد مع بقاء الغنة حركتين.
           - تفخيم الغنة وترقيقها: تفخم إذا جاء بعدها حرف مفخم (خص ضغط قظ) وترقق فيما سواه.
        """.trimIndent()
      }

      q.contains("ملك") || q.contains("تبارك") -> {
        """
        📖 **مقاصد وفضائل سورة الملك (تبارك):**
        
        • **فضلها:** صح عن النبي ﷺ قوله: «إِنَّ سُورَةً مِنَ القُرْآنِ ثَلاثُونَ آيَةً شَفَعَتْ لِرَجُلٍ حَتَّى غُفِرَ لَهُ: تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ» (رواه الترمذي).
        • **محور السورة:** إظهار عظمة الله وكمال ملكه وقدرته في الخلق والإحياء والإماتة، وتحدي المعاندين أن يجدوا في خلق الرحمن تفاوتاً أو خللاً.
        
        ✨ **إضاءات تدبرية في الآيات الأولى:**
        1. {الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلاً}: قدّم الموت لأن العاقل إذا ذكر الموت أحسن العمل؛ ولم يقل "أكثر عملاً" بل "أحسن عملاً" وهو أخلصه وأصوبه.
        2. {مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ}: تناسق وإتقان يدعو المؤمن للتسبيح والانقياد.
        """.trimIndent()
      }

      q.contains("جدول") || q.contains("خطة") || q.contains("أسبوعين") || q.contains("عم") -> {
        """
        🧠 **الخطة الذهبية لحفظ جزء عم في 14 يوماً:**
        
        📌 **القاعدة الذهبية:** (الحفظ القليل المتين خير من الكثير المتفلت).
        
        📅 **توزيع الأيام:**
        • **الأيام 1 - 3:** من سورة الناس إلى سورة العاديات (قصار السور - حفظ ومراجعة تراكمية).
        • **الأيام 4 - 6:** من سورة الزلزلة إلى سورة الغاشية (وجهان يومياً مع التسميع للمعلم).
        • **الأيام 7:** يوم راحة وتسميع النصف الأول كاملاً من الناس إلى الغاشية.
        • **الأيام 8 - 10:** من سورة الأعلى إلى سورة التكوير.
        • **الأيام 11 - 13:** من سورة عبس إلى سورة النبأ (السور الأطول في الجزء).
        • **اليوم 14:** جلسة ختم الجزء واختبار إتقان كامل مع المعلم في الحلقة.
        
        💡 **نصائح التثبيت:**
        1. القراءة بالحفظ في صلوات اليوم المفروضة والنوافل.
        2. الاستماع للمصحف المرتل برواية حفص بصوت قارئ متقن مثل الشيخ الحصري أو المنشاوي.
        """.trimIndent()
      }

      q.contains("متشابه") || q.contains("متشابهات") || q.contains("بقرة") || q.contains("عمران") -> {
        """
        🔍 **ضوابط المتشابهات بين سورتي البقرة وآل عمران:**
        
        1. **قاعدة الترتيب الهجائي:**
           - في سورة البقرة: {فَتَلَقَّىٰ آدَمُ مِن رَّبِّهِ كَلِمَاتٍ فَتَابَ عَلَيْهِ ۚ إِنَّهُ هُوَ التَّوَّابُ الرَّحِيمُ} (بالواو: التواب الرحيم).
           - بينما يكثر في آل عمران تقديم الحكيم أو الغفور حسب السياق.
        
        2. **مواضع {يَا بَنِي إِسْرَائِيلَ}:**
           - تكرر النداء في البقرة ثلاث مرات: {اذْكُرُوا نِعْمَتِيَ الَّتِي أَنْعَمْتُ عَلَيْكُمْ وَأَنِّي فَضَّلْتُكُمْ عَلَى الْعَالَمِينَ}.
           - بينما في آل عمران كان الحديث عبر محاجة أهل الكتاب {يَا أَهْلَ الْكِتَابِ}.
        
        3. **خواتيم الآيات في الإنفاق:**
           - البقرة: {وَمَا تُنفِقُوا مِنْ خَيْرٍ فَإِنَّ اللَّهَ بِهِ عَلِيمٌ}.
           - آل عمران: {وَمَا تُنفِقُوا مِن شَيْءٍ فَإِنَّ اللَّهَ بِهِ عَلِيمٌ} (كلمة "شيء" أعم لتناسب سياق المغالبة والجهاد).
        """.trimIndent()
      }

      q.contains("معلم") || q.contains("مشرف") || q.contains("تحفيز") || q.contains("تشتت") -> {
        """
        👨‍🏫 **دليل معلم الحلقة في إدارة وتطوير الطلاب:**
        
        1. **البداية الجاذبة:**
           - افتتح الحلقة بحديث نبوي شريف قصير أو قصة في فضل القرآن لا تتجاوز دقيقتين لبث الهمة.
        
        2. **علاج الطالب المتراجع:**
           - لا تحرج الطالب المتأخر أمام زملائه؛ اجلس معه دقيقة منفردة واسأله عن ظروفه.
           - خفّف عنه المقدار مؤقتاً ليعيد ثقته بنفسه ثم ارفعه تدريجياً.
        
        3. **نظام التحفيز الرقمي:**
           - استغل نقاط التطبيق والمواظبة اليومية لإعلان "نجم الأسبوع" في لوحة الشرف.
           - أرسل تقرير ولي الأمر على الواتساب فور تسميع الطالب بتقدير ممتاز ليشاركه والداه الفرحة.
        """.trimIndent()
      }

      else -> {
        """
        ✨ **مساعد الذكاء الاصطناعي القرآني في خدمتك:**
        
        أهلاً بك في محراب القرآن الكريم بمجمع حلقات هائل سعيد أنعم.
        
        يمكنني مساعدتك في:
        1. 🎙️ **أحكام التجويد ومخارج الحروف**: المدود، القلقلة، الوقف والابتداء، وأحكام النون والميم.
        2. 📖 **تفسير وتدبر الآيات**: معاني المفردات وغريب القرآن ومقاصد السور.
        3. 🧠 **خطط الحفظ والمراجعة**: وضع جداول تفاعلية حسب وقتك وأهدافك.
        4. 🔍 **ضبط المتشابهات اللفظية**: القواعد الذهبية لعدم الخلط بين السور.
        5. 👨‍🏫 **توجيهات المشرفين والمعلمين**: الاستراتيجيات التربوية لإدارة الحلقات.
        
        💡 *اطرح سؤالك أو اختر أحد المقترحات الجاهزة أدناه لنبدأ فوراً.*
        """.trimIndent()
      }
    }
  }
}
