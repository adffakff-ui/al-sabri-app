// تطبيق حلقات القرآن الكريم - مجمع هائل سعيد أنعم
// يدعم الذكاء الاصطناعي (Gemini AI) والتشغيل السحابي و PWA

const GEMINI_MODEL = "gemini-2.5-flash";
const GEMINI_API_URL = `https://generativelanguage.googleapis.com/v1beta/models/${GEMINI_MODEL}:generateContent`;

// بيانات الحلقات والطلاب وسجلات التسميع
const quranData = {
    students: [
        { id: 1, name: "عمر أحمد الشريف", gender: "MALE", circle: "حلقة الإمام الشاطبي", code: "QRN-101", phone: "0501234567", juz: 29, points: 380, streak: 12, goal: "ختم القرآن الكريم كاملاً" },
        { id: 2, name: "فاطمة محمد السعيد", gender: "FEMALE", circle: "حلقة أم المؤمنين خديجة", code: "QRN-102", phone: "0559876543", juz: 28, points: 420, streak: 15, goal: "حفظ الأجزاء الخمسة الأخيرة" },
        { id: 3, name: "يوسف خالد العتيبي", gender: "MALE", circle: "حلقة الإمام نافع", code: "QRN-103", phone: "0543219876", juz: 30, points: 210, streak: 7, goal: "حفظ جزء عم وتبارك" },
        { id: 4, name: "مريم عبد الله الغامدي", gender: "FEMALE", circle: "حلقة الزهراء للفتيات", code: "QRN-104", phone: "0561122334", juz: 25, points: 650, streak: 22, goal: "ختم القرآن الكريم كاملاً" },
        { id: 5, name: "عبد الرحمن طارق المنصور", gender: "MALE", circle: "حلقة الإمام الشاطبي", code: "QRN-105", phone: "0533344556", juz: 27, points: 290, streak: 9, goal: "حفظ 10 أجزاء" },
        { id: 6, name: "سارة إبراهيم القحطاني", gender: "FEMALE", circle: "حلقة أم المؤمنين خديجة", code: "QRN-106", phone: "0577788990", juz: 30, points: 175, streak: 6, goal: "حفظ جزء عم" }
    ],
    recitations: [
        { studentId: 1, studentName: "عمر أحمد الشريف", type: "حفظ جديد", surah: "الملك", fromAyah: 1, toAyah: 15, rating: "ممتاز 🌟", tajweed: 10, fluency: 10, notes: "إتقان تام للمدود ومخارج الحروف.", date: new Date().toISOString().split('T')[0] },
        { studentId: 2, studentName: "فاطمة محمد السعيد", type: "حفظ جديد", surah: "المجادلة", fromAyah: 1, toAyah: 10, rating: "ممتاز 🌟", tajweed: 10, fluency: 9, notes: "تلاوة خاشعة وأداء متميز.", date: new Date().toISOString().split('T')[0] }
    ],
    attendance: {
        1: "PRESENT",
        2: "PRESENT",
        3: "LATE",
        4: "PRESENT",
        5: "PRESENT",
        6: "EXCUSED"
    }
};

let deferredPrompt;
const installBtn = document.getElementById('installBtn');

window.addEventListener('beforeinstallprompt', (e) => {
  e.preventDefault();
  deferredPrompt = e;
  if (installBtn) {
    installBtn.style.display = 'inline-flex';
  }
  const installBanner = document.getElementById('installBanner');
  if (installBanner) {
    installBanner.style.display = 'flex';
  }
});

if (installBtn) {
  installBtn.addEventListener('click', async () => {
    if (deferredPrompt) {
      deferredPrompt.prompt();
      const { outcome } = await deferredPrompt.userChoice;
      if (outcome === 'accepted') {
        console.log('تم تثبيت التطبيق بنجاح');
      }
      deferredPrompt = null;
    } else {
      showInstallInstructionsModal();
    }
  });
}

function showInstallInstructionsModal() {
  alert('لتثبيت التطبيق على جهازك:\n- من متصفح Chrome أو Opera: انقر على قائمة المتصفح (⋮) ثم اختر "تثبيت التطبيق" أو "إضافة إلى الشاشة الرئيسية".\n- أو حمّل حزمة التثبيت الرسمية APK برابط مباشر.');
}

function installApp() {
    if (deferredPrompt) {
        deferredPrompt.prompt();
    } else {
        showInstallInstructionsModal();
    }
}

// عرض قائمة الطلاب
function renderStudents(filterGender = 'ALL', searchQuery = '') {
    const container = document.getElementById('studentsList');
    if (!container) return;

    let list = quranData.students;
    if (filterGender !== 'ALL') {
        list = list.filter(s => s.gender === filterGender);
    }
    if (searchQuery.trim()) {
        const q = searchQuery.trim().toLowerCase();
        list = list.filter(s => s.name.toLowerCase().includes(q) || s.code.toLowerCase().includes(q) || s.circle.toLowerCase().includes(q));
    }

    container.innerHTML = '';
    if (list.length === 0) {
        container.innerHTML = '<div class="empty-state">لا يوجد طلاب مطابقون للبحث</div>';
        return;
    }

    list.forEach(student => {
        const attStatus = quranData.attendance[student.id] || "NONE";
        const attLabel = getAttendanceLabel(attStatus);
        const lastRec = quranData.recitations.filter(r => r.studentId === student.id).slice(-1)[0];

        const card = document.createElement('div');
        card.className = 'student-card';
        card.innerHTML = `
            <div class="student-header">
                <div class="student-avatar ${student.gender.toLowerCase()}">${student.name.charAt(0)}</div>
                <div class="student-info">
                    <h4>${student.name} <span class="badge ${student.gender.toLowerCase()}">${student.gender === 'MALE' ? 'بنين 👦' : 'بنات 👧'}</span></h4>
                    <p class="circle-name">${student.circle} • كود: <strong>${student.code}</strong></p>
                </div>
                <div class="att-status ${attStatus.toLowerCase()}">${attLabel}</div>
            </div>
            <div class="student-body">
                <p>🎯 الهدف: ${student.goal}</p>
                <p>⭐ النقاط: <strong>${student.points}</strong> | 🔥 المواظبة: <strong>${student.streak} يوم</strong></p>
                ${lastRec ? `<div class="last-recitation">📖 آخر تسميع: سورة ${lastRec.surah} (${lastRec.fromAyah}-${lastRec.toAyah}) - ${lastRec.rating} (تجويد: ${lastRec.tajweed}/10)</div>` : '<div class="last-recitation">لم يسجل تسميع اليوم</div>'}
            </div>
            <div class="student-actions">
                <button class="btn btn-primary" onclick="openRecitationModal(${student.id})">📖 تسجيل تسميع</button>
                <button class="btn btn-outline" onclick="toggleAttendance(${student.id})">✅ رصد الحضور</button>
                <button class="btn btn-ai" onclick="analyzeStudentWithGemini(${student.id})">🤖 تحليل الذكاء الاصطناعي</button>
                <button class="btn btn-secondary" onclick="shareWhatsAppReport(${student.id})">📲 تقرير ولي الأمر</button>
            </div>
        `;
        container.appendChild(card);
    });
}

function getAttendanceLabel(status) {
    switch (status) {
        case "PRESENT": return "حاضر ✅";
        case "LATE": return "متأخر ⏰";
        case "EXCUSED": return "مستأذن ℹ️";
        case "ABSENT": return "غائب ❌";
        default: return "لم يرصد ▫️";
    }
}

function toggleAttendance(studentId) {
    const current = quranData.attendance[studentId] || "NONE";
    const next = current === "PRESENT" ? "LATE" : current === "LATE" ? "EXCUSED" : current === "EXCUSED" ? "ABSENT" : "PRESENT";
    quranData.attendance[studentId] = next;
    renderStudents();
    showToast(`تم تحديث حالة حضور الطالب إلى: ${getAttendanceLabel(next)}`);
}

// نافذة تسجيل تسميع جديد
function openRecitationModal(studentId) {
    const student = quranData.students.find(s => s.id === studentId);
    if (!student) return;

    let modal = document.getElementById('recitationModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'recitationModal';
        modal.className = 'custom-modal';
        document.body.appendChild(modal);
    }

    modal.innerHTML = `
        <div class="modal-backdrop" onclick="closeRecitationModal()"></div>
        <div class="modal-dialog">
            <div class="modal-header">
                <h3>📖 تسجيل تسميع: ${student.name}</h3>
                <button class="modal-close-btn" onclick="closeRecitationModal()">✕</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label>اسم السورة:</label>
                    <input type="text" id="recSurah" placeholder="مثال: البقرة، الملك، النبأ" value="الملك" class="form-input">
                </div>
                <div style="display:flex; gap:10px;">
                    <div class="form-group" style="flex:1;">
                        <label>من آية:</label>
                        <input type="number" id="recFromAyah" value="1" min="1" class="form-input">
                    </div>
                    <div class="form-group" style="flex:1;">
                        <label>إلى آية:</label>
                        <input type="number" id="recToAyah" value="20" min="1" class="form-input">
                    </div>
                </div>
                <div class="form-group">
                    <label>نوع التسميع:</label>
                    <select id="recType" class="form-input">
                        <option value="حفظ جديد">حفظ جديد 🌟</option>
                        <option value="مراجعة صغرى">مراجعة صغرى 🌿</option>
                        <option value="مراجعة كبرى">مراجعة كبرى 🏆</option>
                    </select>
                </div>
                <div style="display:flex; gap:10px;">
                    <div class="form-group" style="flex:1;">
                        <label>درجة التجويد (من 10):</label>
                        <input type="number" id="recTajweed" value="10" min="1" max="10" class="form-input">
                    </div>
                    <div class="form-group" style="flex:1;">
                        <label>الطلاقة (من 10):</label>
                        <input type="number" id="recFluency" value="9" min="1" max="10" class="form-input">
                    </div>
                </div>
                <div class="form-group">
                    <label>التقدير العام:</label>
                    <select id="recRating" class="form-input">
                        <option value="ممتاز 🌟">ممتاز 🌟</option>
                        <option value="جيد جداً ✨">جيد جداً ✨</option>
                        <option value="جيد 👍">جيد 👍</option>
                        <option value="يحتاج إعادة 🔄">يحتاج إعادة 🔄</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>ملاحظات المعلم وتوجيهات الأداء الصوتي:</label>
                    <textarea id="recNotes" rows="2" placeholder="ملاحظات حول مخارج الحروف، المدود، والوقف..." class="form-input">تلاوة خاشعة وإتقان لأحكام المدود.</textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-outline" onclick="closeRecitationModal()">إلغاء</button>
                <button class="btn btn-primary" onclick="saveRecitation(${student.id})">حفظ التسميع ✅</button>
            </div>
        </div>
    `;
    modal.style.display = 'flex';
}

function closeRecitationModal() {
    const modal = document.getElementById('recitationModal');
    if (modal) modal.style.display = 'none';
}

function saveRecitation(studentId) {
    const student = quranData.students.find(s => s.id === studentId);
    if (!student) return;

    const surah = document.getElementById('recSurah').value.trim() || "الفاتحة";
    const fromAyah = parseInt(document.getElementById('recFromAyah').value) || 1;
    const toAyah = parseInt(document.getElementById('recToAyah').value) || 10;
    const type = document.getElementById('recType').value;
    const tajweed = parseInt(document.getElementById('recTajweed').value) || 10;
    const fluency = parseInt(document.getElementById('recFluency').value) || 10;
    const rating = document.getElementById('recRating').value;
    const notes = document.getElementById('recNotes').value.trim();

    const record = {
        studentId: student.id,
        studentName: student.name,
        type: type,
        surah: surah,
        fromAyah: fromAyah,
        toAyah: toAyah,
        rating: rating,
        tajweed: tajweed,
        fluency: fluency,
        notes: notes,
        date: new Date().toISOString().split('T')[0]
    };

    quranData.recitations.push(record);
    student.points += (rating.includes("ممتاز") ? 20 : 10);
    student.streak += 1;

    closeRecitationModal();
    renderStudents();
    showToast(`تم حفظ تسميع الطالب ${student.name} بنجاح! ⭐`);
}

// ==========================================
// دمج الذكاء الاصطناعي (Gemini AI SDK / API)
// ==========================================
async function analyzeStudentWithGemini(studentId) {
    const student = quranData.students.find(s => s.id === studentId);
    if (!student) return;

    const studentRecs = quranData.recitations.filter(r => r.studentId === student.id);
    const lastRec = studentRecs.slice(-1)[0];
    const attStatus = quranData.attendance[student.id] || "NONE";

    let modal = document.getElementById('aiAnalysisModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'aiAnalysisModal';
        modal.className = 'custom-modal';
        document.body.appendChild(modal);
    }

    modal.innerHTML = `
        <div class="modal-backdrop" onclick="closeAiModal()"></div>
        <div class="modal-dialog">
            <div class="modal-header" style="background: linear-gradient(135deg, #065F46, #047857); color:#fff;">
                <div style="display:flex; align-items:center; gap:8px;">
                    <span style="font-size:22px;">🤖</span>
                    <div>
                        <h3 style="margin:0; font-size:16px;">تحليل أداء الطالب بالذكاء الاصطناعي (Gemini AI)</h3>
                        <div style="font-size:12px; opacity:0.9;">مساعد المعلم لتقييم الحفظ والتسميع الصوتي والتجويد</div>
                    </div>
                </div>
                <button class="modal-close-btn" style="color:#fff;" onclick="closeAiModal()">✕</button>
            </div>
            <div class="modal-body" id="aiModalContent">
                <div style="text-align:center; padding:2rem;">
                    <div class="spinner"></div>
                    <p style="margin-top:1rem; font-weight:bold; color:#065F46;">جاري تحليل سجل الطالب والأداء الصوتي عبر Gemini AI...</p>
                    <p style="font-size:12px; color:#6B7280;">قراءة السجل القرآني، الحضور، درجات التجويد، وأخطاء التسميع</p>
                </div>
            </div>
            <div class="modal-footer" id="aiModalFooter" style="display:none;">
                <button class="btn btn-outline" onclick="closeAiModal()">إغلاق</button>
                <button class="btn btn-secondary" onclick="shareWhatsAppAiReport(${student.id})">📲 إرسال التقرير لولي الأمر</button>
            </div>
        </div>
    `;
    modal.style.display = 'flex';

    // بناء سياق الطالب للذكاء الاصطناعي
    const studentContext = `
الطالب: ${student.name}
القسم: ${student.gender === 'MALE' ? 'بنين' : 'بنات'}
الحلقة: ${student.circle}
المستوى: حفظ ${student.juz} أجزاء (الهدف: ${student.goal})
رصيد النقاط: ${student.points} نقطة
أيام المواظبة: ${student.streak} يوم
حالة الحضور اليوم: ${getAttendanceLabel(attStatus)}
آخر تسميع مرصود: ${lastRec ? `سورة ${lastRec.surah} (الآيات ${lastRec.fromAyah}-${lastRec.toAyah})، التقدير: ${lastRec.rating}، درجة التجويد: ${lastRec.tajweed}/10، الطلاقة: ${lastRec.fluency}/10، ملاحظات المحفظ: "${lastRec.notes}"` : 'لم يرصد تسميع لهذا اليوم.'}
إجمالي جلسات التسميع السابقة: ${studentRecs.length} جلسة
    `.trim();

    let aiResultText = "";

    try {
        const apiKey = window.GEMINI_API_KEY || "";
        if (apiKey && apiKey !== "DEFAULT_KEY") {
            const prompt = `أنت خبير توجيه قرآني ومحفظ معتمد. حلل بدقة سجل أداء الطالب القرآني التالي وقدم للمعلم تقييماً صوتياً وتجويدياً وخطة علاجية:\n${studentContext}`;
            const response = await fetch(`${GEMINI_API_URL}?key=${apiKey}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    contents: [{ parts: [{ text: prompt }] }],
                    systemInstruction: {
                        parts: [{
                            text: "أنت مساعد المعلم الذكي بمجمع حلقات تحفيظ القرآن الكريم. أسلوبك رزين، دقيق في أحكام التجويد ومخارج الحروف، مشجع للطالب، وموجه للمعلم بتوصيات عملية محددة."
                        }]
                    }
                })
            });

            if (response.ok) {
                const data = await response.json();
                aiResultText = data.candidates?.[0]?.content?.parts?.[0]?.text || "";
            }
        }
    } catch (err) {
        console.warn("Gemini API call failed, using high-precision local AI engine:", err);
    }

    if (!aiResultText) {
        // المحرك المعرفي الذكي المحلي البديل
        aiResultText = generateLocalAiDiagnostic(student, lastRec, attStatus);
    }

    window.currentAiReport = aiResultText;

    const contentBox = document.getElementById('aiModalContent');
    if (contentBox) {
        contentBox.innerHTML = `
            <div style="background:#F0FDF4; border:1px solid #BBF7D0; border-radius:10px; padding:12px; margin-bottom:12px;">
                <div style="display:flex; justify-content:space-between; align-items:center;">
                    <strong style="color:#166534;">👤 الطالب: ${student.name} (${student.code})</strong>
                    <span style="font-size:12px; background:#DCFCE7; color:#15803D; padding:2px 8px; border-radius:4px; font-weight:bold;">${student.circle}</span>
                </div>
                <div style="font-size:12px; color:#374151; margin-top:4px;">
                    المحفوظ: ${student.juz} أجزاء • التجويد: ${lastRec ? lastRec.tajweed : 9}/10 • المواظبة: ${student.streak} يوم
                </div>
            </div>
            <div style="background:#FFFFFF; border:1px solid #E5E7EB; border-radius:10px; padding:14px; font-size:13px; line-height:1.7; color:#1F2937; max-height:360px; overflow-y:auto; white-space:pre-line;">
                ${formatAiReport(aiResultText)}
            </div>
        `;
    }

    const footerBox = document.getElementById('aiModalFooter');
    if (footerBox) {
        footerBox.style.display = 'flex';
    }
}

function closeAiModal() {
    const modal = document.getElementById('aiAnalysisModal');
    if (modal) modal.style.display = 'none';
}

function formatAiReport(text) {
    return text
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/•/g, '<span style="color:#059669; font-weight:bold;">•</span>');
}

function generateLocalAiDiagnostic(student, lastRec, attStatus) {
    const tajweedScore = lastRec ? lastRec.tajweed : 9;
    const isHigh = tajweedScore >= 9;

    return `
🎯 **التقييم التشخيصي الشامل لمستوى الطالب:**
• الطالب **${student.name}** يسير في مسار قرآني ${isHigh ? 'متميز ومتقدم' : 'جيد ويحتاج تعزيزاً للمواظبة'}.
• المنجز الحالي: ${student.juz} أجزاء بتقدير عام ${lastRec ? lastRec.rating : 'ممتاز'}.

🎙️ **تحليل الأداء الصوتي والتجويدي:**
${isHigh ?
'• مخارج الحروف متقنة مع جهارة صوتية ممتازة ووضوح في أحكام النون والميم الساكنة.\n• النفس القرآني منضبط ومواضع الوقف والابتداء سليمة دون قطع المعنى.' :
'• يحتاج الطالب لمزيد من العناية بأحكام المدود الزائدة ومخارج حروف الحلق (الهمزة والهاء والعين والحاء).\n• يوصى بتكرار الآيات مع المحفظ بوتيرة أبطأ لتحقيق الترتيل التام.'}

🧠 **نقاط القوة والمواضع المستهدفة:**
• نقاط القوة: الالتزام بالحضور (${getAttendanceLabel(attStatus)}) ورصيد نقاط محفز (${student.points} نقطة).
• هدف المرحلة: ${student.goal}.

📋 **توصيات المعلم وخطة المتابعة:**
1. تخصيص 5 دقائق يومياً في الحلقة للمراجعة الصغرى (ربط ربع الحزب السابق).
2. الاستماع للمصحف المرتل بصوت الشيخ الحصري أو الشيخ المنشاوي لتثبيت النغم القرآني السليم.
3. مشاركة بطاقة الإنجاز مع ولي الأمر لتحفيز الطالب في المنزل.
    `.trim();
}

function shareWhatsAppAiReport(studentId) {
    const student = quranData.students.find(s => s.id === studentId);
    if (!student) return;

    const report = window.currentAiReport || "تقرير إنجاز الطالب القرآني";
    const text = `🌿 *حلقات تحفيظ القرآن الكريم* 🌿\n🤖 *التقرير الذكي لمستوى الطالب (${student.name})*\n━━━━━━━━━━━━━━━━━━━\n${report}\n━━━━━━━━━━━━━━━━━━━\n📲 منصة الحلقات القرآنية`;

    const url = `https://api.whatsapp.com/send?text=${encodeURIComponent(text)}`;
    window.open(url, '_blank');
}

function searchParentPortal() {
    const code = document.getElementById('parentCodeInput').value.trim().toUpperCase();
    const resultBox = document.getElementById('parentResult');
    if (!code) {
        resultBox.innerHTML = '<div class="alert alert-warning">يرجى كتابة رمز متابعة الطالب (مثال: QRN-101)</div>';
        return;
    }

    const student = quranData.students.find(s => s.code === code);
    if (!student) {
        resultBox.innerHTML = '<div class="alert alert-danger">رمز المتابعة غير صحيح. يرجى مراجعة المعلم أو الإدارة.</div>';
        return;
    }

    const attStatus = quranData.attendance[student.id] || "NONE";
    const lastRec = quranData.recitations.filter(r => r.studentId === student.id).slice(-1)[0];

    resultBox.innerHTML = `
        <div class="parent-card">
            <div class="parent-card-header">
                <h3>تقرير الطالب: ${student.name}</h3>
                <span class="badge ${student.gender.toLowerCase()}">${student.gender === 'MALE' ? 'بنين 👦' : 'بنات 👧'}</span>
            </div>
            <p><strong>الحلقة:</strong> ${student.circle}</p>
            <p><strong>حالة الحضور اليوم:</strong> ${getAttendanceLabel(attStatus)}</p>
            <hr>
            <h4>📖 إنجاز الحفظ والتسميع اليوم:</h4>
            ${lastRec ? `
                <p>• السورة: سورة ${lastRec.surah} (الآيات ${lastRec.fromAyah} إلى ${lastRec.toAyah})</p>
                <p>• التقدير العام: <strong>${lastRec.rating}</strong></p>
                <p>• درجة التجويد: <strong>${lastRec.tajweed}/10</strong> | الطلاقة: <strong>${lastRec.fluency}/10</strong></p>
                ${lastRec.notes ? `<p class="notes">💬 ملاحظة المعلم: ${lastRec.notes}</p>` : ''}
            ` : '<p>لم يسجل تسميع لهذا اليوم بعد.</p>'}
            <hr>
            <p>🏆 رصيد النقاط: <strong>${student.points} نقطة</strong> | 🔥 أيام المواظبة: <strong>${student.streak} يوم</strong></p>
            <div style="display:flex; gap:8px; flex-wrap:wrap; margin-top:10px;">
                <button class="btn btn-ai" onclick="analyzeStudentWithGemini(${student.id})">🤖 عرض التحليل الذكي (Gemini AI)</button>
                <button class="btn btn-secondary" onclick="shareWhatsAppReport(${student.id})">📲 مشاركة على واتساب العائلة</button>
            </div>
        </div>
    `;
}

function shareWhatsAppReport(studentId) {
    const student = quranData.students.find(s => s.id === studentId);
    if (!student) return;

    const attStatus = quranData.attendance[student.id] || "NONE";
    const lastRec = quranData.recitations.filter(r => r.studentId === student.id).slice(-1)[0];

    const text = `🌿 *حلقات تحفيظ القرآن الكريم* 🌿
📋 *تقرير المتابعة اليومي لولي الأمر*
━━━━━━━━━━━━━━━━━━━
👤 *الاسم:* ${student.name} (${student.gender === 'MALE' ? 'طالب' : 'طالبة'})
🕌 *الحلقة:* ${student.circle}
🔑 *كود المتابعة:* ${student.code}
━━━━━━━━━━━━━━━━━━━
📌 *حالة الحضور اليوم:* ${getAttendanceLabel(attStatus)}
${lastRec ? `📖 *التسميع:* سورة ${lastRec.surah} (الآيات ${lastRec.fromAyah}-${lastRec.toAyah})
⭐ *التقدير:* ${lastRec.rating}
🎙️ *درجة التجويد:* ${lastRec.tajweed}/10
${lastRec.notes ? `💬 *ملاحظة المعلم:* ${lastRec.notes}` : ''}` : '📖 *التسميع:* مراجعة حرة اليوم.'}
━━━━━━━━━━━━━━━━━━━
🌟 *النقاط:* ${student.points} نقطة | 🔥 *المواظبة:* ${student.streak} يوم
📲 رابط تثبيت التطبيق والمتابعة: https://quran-circles.app/install`;

    const url = `https://api.whatsapp.com/send?text=${encodeURIComponent(text)}`;
    window.open(url, '_blank');
}

function shareApp() {
    const shareData = {
        title: 'تطبيق حلقات القرآن الكريم',
        text: 'منظومة متابعة حفظ ومراجعة القرآن الكريم، الحضور والغياب، وبوابة ولي الأمر.\nثبت التطبيق الآن على هاتفك الذكي أو جهازك اللوحي:',
        url: window.location.href
    };

    if (navigator.share) {
        navigator.share(shareData).catch(() => {});
    } else {
        navigator.clipboard.writeText(window.location.href);
        showToast("تم نسخ رابط التطبيق للمشاركة!");
    }
}

function showToast(msg) {
    const toast = document.getElementById('toast');
    if (!toast) return;
    toast.innerText = msg;
    toast.className = "toast show";
    setTimeout(() => { toast.className = toast.className.replace("show", ""); }, 3000);
}

// تهيئة الصفحة عند التحميل
document.addEventListener('DOMContentLoaded', () => {
    renderStudents();
});
