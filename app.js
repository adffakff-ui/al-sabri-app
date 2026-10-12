// تطبيق حلقات القرآن الكريم - الملف البرمجي الرئيسي
// يدعم التثبيت الفوري PWA على جميع الهواتف الذكية والأجهزة اللوحية

// بيانات الحلقات والطلاب
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
const installBtn = document.getElementById('installBtn'); // تأكد من إعطاء زر التثبيت id="installBtn"

window.addEventListener('beforeinstallprompt', (e) => {
  e.preventDefault();
  deferredPrompt = e;
  if (installBtn) {
    installBtn.style.display = 'inline-flex'; // إظهار الزر
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
    alert("📱 طريقة تثبيت التطبيق على الأجهزة المختلفة:\n\n• الأندرويد والكمبيوتر (Chrome / Edge): يظهر زر التثبيت تلقائياً أو عبر الضغط على القائمة (⋮) ثم 'تثبيت التطبيق' أو 'إضافة إلى الشاشة الرئيسية'.\n\n• الآيفون والآيباد (Safari): متصفح Safari لا يدعم التثبيت المباشر بالزر البرمجي؛ اضغط على زر المشاركة (Share ⬆️) ثم اختر 'الإضافة إلى الشاشة الرئيسية (Add to Home Screen)'.");
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
        const lastRec = quranData.recitations.find(r => r.studentId === student.id);

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
                ${lastRec ? `<div class="last-recitation">📖 آخر تسميع: سورة ${lastRec.surah} (${lastRec.fromAyah}-${lastRec.toAyah}) - ${lastRec.rating}</div>` : '<div class="last-recitation">لم يسجل تسميع اليوم</div>'}
            </div>
            <div class="student-actions">
                <button class="btn btn-primary" onclick="openRecitationModal(${student.id})">📖 تسجيل تسميع</button>
                <button class="btn btn-outline" onclick="toggleAttendance(${student.id})">✅ رصد الحضور</button>
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
    const lastRec = quranData.recitations.find(r => r.studentId === student.id);

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
            <button class="btn btn-whatsapp" onclick="shareWhatsAppReport(${student.id})">📲 مشاركة التقرير على واتساب العائلة</button>
        </div>
    `;
}

function shareWhatsAppReport(studentId) {
    const student = quranData.students.find(s => s.id === studentId);
    if (!student) return;

    const attStatus = quranData.attendance[student.id] || "NONE";
    const lastRec = quranData.recitations.find(r => r.studentId === student.id);

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
