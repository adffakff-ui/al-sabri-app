package com.example.data.model

import com.example.viewmodel.UserRole

data class AccessCode(
  val code: String,
  val role: UserRole,
  val userName: String,
  val groupName: String? = null,
  val studentId: Long? = null,
  val section: HalaqahSection = HalaqahSection.ALL,
  val scopeDescription: String,
  val allowedPrivileges: List<String>,
  val restrictedPrivileges: List<String>
)

object AccessCodeRegistry {

  val defaultCodes: List<AccessCode> = listOf(
    // 1. المشرف العام (كامل الصلاحيات)
    AccessCode(
      code = "SUP-GENERAL-777",
      role = UserRole.SUPERVISOR,
      userName = "د. عبد الرحمن سعيد أنعم (المشرف العام)",
      groupName = null,
      studentId = null,
      section = HalaqahSection.ALL,
      scopeDescription = "صلاحيات المشرف العام: رقابة شاملة على كافة أقسام البنين والبنات، مراجعة التسميع، إرسال التعاميم، والمزامنة السريعة.",
      allowedPrivileges = listOf("معاينة كافة حلقات البنين والبنات", "إصدار التعاميم الإشرافية العامة", "مراجعة تقييمات التسميع لجميع الطلاب", "التحكم في مصفوفة الصلاحيات"),
      restrictedPrivileges = emptyList()
    ),

    // 2. مشرف قطاع البنين
    AccessCode(
      code = "SUP-BOYS-100",
      role = UserRole.SUPERVISOR,
      userName = "الشيخ إبراهيم الغامدي (مشرف قطاع البنين)",
      groupName = null,
      studentId = null,
      section = HalaqahSection.BOYS,
      scopeDescription = "مشرف قطاع البنين: صلاحيات رقابية على حلقات البنين فقط. تم غلق وحجب بيانات حلقات وأقسام البنات تماماً.",
      allowedPrivileges = listOf("متابعة حلقات البنين الستة", "مراجعة حضور وتسميع طلاب البنين", "إرسال توجيهات لمعلمي البنين"),
      restrictedPrivileges = listOf("حلقات وأقسام البنات مغلقة تماماً", "تعديل إعدادات المشرف العام")
    ),

    // 3. مشرفة قطاع البنات
    AccessCode(
      code = "SUP-GIRLS-200",
      role = UserRole.GIRLS_ADMIN,
      userName = "الشيخة أم عبد الله الأنصاري (مشرفة قطاع البنات)",
      groupName = null,
      studentId = null,
      section = HalaqahSection.GIRLS,
      scopeDescription = "مشرفة قطاع البنات: صلاحيات رقابية وإدارية على حلقات وقاعات البنات فقط. تم غلق وحجب بيانات قطاع البنين تماماً.",
      allowedPrivileges = listOf("متابعة وإدارة حلقات البنات", "تعيين وتوثيق المحفظات بالسند", "تسجيل وإدارة بيانات الطالبات", "رصد الحفظ والغياب اليومي"),
      restrictedPrivileges = listOf("حلقات وأقسام البنين مغلقة تماماً", "تعديل إعدادات المشرف العام")
    ),

    // 4. معلّم حلقة عثمان بن عفان (بنين)
    AccessCode(
      code = "TCH-OTHMAN-11",
      role = UserRole.TEACHER,
      userName = "الشيخ عبد الله بن فهد (معلم حلقة عثمان)",
      groupName = "حلقة عثمان بن عفان رضي الله عنه",
      studentId = null,
      section = HalaqahSection.BOYS,
      scopeDescription = "معلم حلقة عثمان بن عفان: صلاحية مقتصرة حصرياً على طلاب حلقته. تم غلق بيانات الحلقات الأخرى وقسم البنات.",
      allowedPrivileges = listOf("رصد تسميع وتقييم طلاب حلقة عثمان", "تحضير الحضور والغياب لطلاب الحلقة", "جدولة مواعيد التسميع وإرسال تنبيهات FCM", "إرسال تقارير لأولياء أمور طلاب الحلقة"),
      restrictedPrivileges = listOf("بيانات باقي حلقات البنين مغلقة", "قسم البنات بالكامل مغلق ومحجوب", "لوحة المشرف العام محجوبة")
    ),

    // 5. معلمة حلقة خديجة بنت خويلد (بنات)
    AccessCode(
      code = "TCH-KHADIJA-33",
      role = UserRole.TEACHER,
      userName = "الشيخة أم عبد الله (معلمة حلقة خديجة - بنات)",
      groupName = "حلقة خديجة بنت خويلد رضي الله عنها",
      studentId = null,
      section = HalaqahSection.GIRLS,
      scopeDescription = "معلمة حلقة خديجة بنت خويلد (بنات): صلاحية مقتصرة حصرياً على طالبات حلقتها النسائية.",
      allowedPrivileges = listOf("رصد تسميع وتقييم طالبات حلقة خديجة", "تحضير حضور وغياب طالبات الحلقة", "جدولة مواعيد التسميع وإرسال تنبيهات FCM للأمهات", "إرسال تقارير التسميع اليومية"),
      restrictedPrivileges = listOf("بيانات باقي حلقات البنات مغلقة", "قسم البنين بالكامل مغلق ومحجوب", "لوحة المشرف العام محجوبة")
    ),

    // 6. الطالب عبد الرحمن العتيبي (بنين)
    AccessCode(
      code = "STU-ABDELRAHMAN-01",
      role = UserRole.STUDENT,
      userName = "عبد الرحمن أحمد العتيبي",
      groupName = "حلقة عثمان بن عفان رضي الله عنه",
      studentId = 1L,
      section = HalaqahSection.BOYS,
      scopeDescription = "حساب الطالب عبد الرحمن العتيبي: عرض سجله الشخصي، خطة الحفظ، درجات التسميع، والتسجيلات الصوتية.",
      allowedPrivileges = listOf("استعراض سجل الحفظ اليومي ودرجات التسميع", "الاستماع لتسجيلات القراء وربط المستوى", "استعراض مواعيد التسميع المجدولة"),
      restrictedPrivileges = listOf("غلق بيانات سائر الطلاب الآخرين", "منع التعديل على التقييمات أو الدرجات", "غلق أدوات المعلمين والمشرفين")
    ),

    // 7. الطالبة مريم العتيبي (بنات)
    AccessCode(
      code = "STU-MARYAM-101",
      role = UserRole.STUDENT,
      userName = "مريم عبد الله العتيبي",
      groupName = "حلقة خديجة بنت خويلد رضي الله عنها",
      studentId = 101L,
      section = HalaqahSection.GIRLS,
      scopeDescription = "حساب الطالبة مريم العتيبي (قسم البنات): عرض سجل الحفظ، درجات الإتقان، ومواعيد التسميع التلقائية.",
      allowedPrivileges = listOf("استعراض سجل الحفظ والتسميع الخاص بها", "الاستماع للمصحف المرتل والمجود", "استعراض إشعارات وتنبيهات المعلمة"),
      restrictedPrivileges = listOf("غلق بيانات سائر الطالبات الأخريات", "غلق قسم البنين", "منع التعديل على الدرجات")
    ),

    // 8. ولي أمر الطالب عبد الرحمن العتيبي
    AccessCode(
      code = "PAR-ABDELRAHMAN-01",
      role = UserRole.PARENT,
      userName = "أحمد العتيبي (ولي أمر عبد الرحمن)",
      groupName = "حلقة عثمان بن عفان رضي الله عنه",
      studentId = 1L,
      section = HalaqahSection.BOYS,
      scopeDescription = "ولي أمر الطالب عبد الرحمن العتيبي: صلاحيات محصورة في متابعة ابنه فقط واستلام إشعارات التسميع والغياب.",
      allowedPrivileges = listOf("متابعة بطاقة إنجاز وتسميع الابن عبد الرحمن", "استلام تنبيهات FCM لمواعيد التسميع والغياب", "الاستماع للتسجيل الصوتي المعتمد للتسميع"),
      restrictedPrivileges = listOf("غلق بيانات جميع الطلاب الآخرين", "منع التعديل على أي سجلات", "غلق أقسام المعلمين والإشراف")
    ),

    // 9. ولي أمر الطالبة مريم العتيبي (بنات)
    AccessCode(
      code = "PAR-MARYAM-101",
      role = UserRole.PARENT,
      userName = "عبد الله العتيبي (ولي أمر الطالبة مريم)",
      groupName = "حلقة خديجة بنت خويلد رضي الله عنها",
      studentId = 101L,
      section = HalaqahSection.GIRLS,
      scopeDescription = "ولي أمر الطالبة مريم (قسم البنات): متابعة إنجاز ابنته واستلام تقارير التسميع والتنبيهات المخصصة لها.",
      allowedPrivileges = listOf("متابعة بطاقة إنجاز الابنة مريم", "استلام تنبيهات FCM لمواعيد التسميع والغياب", "الاطلاع على تقارير الشيخة أم عبد الله"),
      restrictedPrivileges = listOf("غلق بيانات الطالبات والطلاب الآخرين", "منع التعديل على السجلات", "غلق أدوات الإشراف")
    )
  )

  fun findCode(inputCode: String): AccessCode? {
    val clean = inputCode.trim().uppercase()
    return defaultCodes.firstOrNull { it.code.uppercase() == clean }
  }

  fun generateCustomCode(
    role: UserRole,
    name: String,
    groupName: String?,
    studentId: Long?,
    section: HalaqahSection
  ): AccessCode {
    val prefix = when (role) {
      UserRole.SUPERVISOR -> if (section == HalaqahSection.GIRLS) "SUP-GIRLS" else "SUP-BOYS"
      UserRole.GIRLS_ADMIN -> "SUP-GIRLS"
      UserRole.TEACHER -> if (section == HalaqahSection.GIRLS) "TCH-G" else "TCH-B"
      UserRole.STUDENT -> if (section == HalaqahSection.GIRLS) "STU-G" else "STU-B"
      UserRole.PARENT -> if (section == HalaqahSection.GIRLS) "PAR-G" else "PAR-B"
      else -> "ACC"
    }
    val randomSuffix = (100..999).random()
    val codeStr = "$prefix-$randomSuffix"

    val scopeDesc = when (role) {
      UserRole.GIRLS_ADMIN -> "مشرفة قطاع البنات: إدارة حلقات ومحفظات وطالبات قطاع البنات فقط."
      UserRole.TEACHER -> "معلم/معلمة: صلاحيات محصورة في إدارة طلاب $groupName فقط، مع حجب سائر الحلقات الأخرى."
      UserRole.STUDENT -> "طالب/طالبة: صلاحيات مقتصرة على ملف $name الشخصي فقط."
      UserRole.PARENT -> "ولي أمر: صلاحيات مقتصرة على متابعة $name فقط."
      UserRole.SUPERVISOR -> "مشرف قطاع: إشراف على ${section.title} فقط."
      else -> "صلاحيات مخصصة مقيدة."
    }

    return AccessCode(
      code = codeStr,
      role = role,
      userName = name,
      groupName = groupName,
      studentId = studentId,
      section = section,
      scopeDescription = scopeDesc,
      allowedPrivileges = listOf("الوصول الحصري للبيانات المرتبطة بالكود"),
      restrictedPrivileges = listOf("سائر بيانات الحلقات والأقسام الأخرى مغلقة تماماً")
    )
  }
}
