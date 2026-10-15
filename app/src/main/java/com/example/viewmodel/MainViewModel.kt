package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SampleDataProvider
import com.example.data.local.SyncManager
import com.example.data.local.SyncState
import com.example.data.model.AccessCode
import com.example.data.model.AccessCodeRegistry
import com.example.data.model.AttendanceRecord
import com.example.data.model.HalaqahSection
import com.example.data.model.ParentNotification
import com.example.data.model.QuranHalaqah
import com.example.data.model.QuranHalaqatData
import com.example.data.model.RecitationRecord
import com.example.data.model.RecitationSchedule
import com.example.data.model.Student
import com.example.data.model.GirlsVerificationResult
import com.example.data.model.FirestoreGirlsHalaqah
import com.example.data.model.FirestoreGirlsStudent
import com.example.data.model.FirestoreGirlsRecitation
import com.example.data.model.FirestoreGirlsAttendance
import com.example.data.repository.GirlsFirestoreRepository
import com.example.util.FcmNotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class UserRole(val title: String, val subtitle: String) {
  SUPERVISOR("المشرف العام", "الإدارة العليا والرقابة الشاملة وإدارة الصلاحيات"),
  AI_ASSISTANT("الذكاء الاصطناعي القرآني 🤖", "مساعد التجويد الذكي، تفسير وتدبر الآيات، واختبارات الحفظ الذكية"),
  GIRLS_ADMIN("إدارة حلقات البنات", "اللوحة الإدارية المستقلة لمشرفات ومعلمات قطاع البنات"),
  TEACHER("المعلم", "إدارة الحلقات والتسميع والغياب"),
  CIRCLES("الحلقات", "دليل حلقات التحفيظ والمعلمين"),
  QIRAAT("القراءات العشر", "أئمة القراءات العشر للمذاهب الإسلامية"),
  STUDENT("الطالب", "متابعة الحفظ والسجل اليومي"),
  PARENT("ولي الأمر", "متابعة الأبناء والتقارير"),
  DEVELOPER("المطور", "إعدادات النظام والبيانات")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application)
  private val studentDao = database.studentDao()
  private val attendanceDao = database.attendanceDao()
  private val recitationDao = database.recitationDao()
  private val notificationDao = database.notificationDao()
  private val scheduleDao = database.scheduleDao()
  private val syncManager = SyncManager(application, database)

  val girlsRepository: GirlsFirestoreRepository by lazy {
    GirlsFirestoreRepository()
  }

  // Verification state from Firestore for Girls section
  private val _girlsVerificationResult = MutableStateFlow<GirlsVerificationResult?>(null)
  val girlsVerificationResult: StateFlow<GirlsVerificationResult?> = _girlsVerificationResult.asStateFlow()

  private val _isTeacherScopeOnly = MutableStateFlow(false)
  val isTeacherScopeOnly: StateFlow<Boolean> = _isTeacherScopeOnly.asStateFlow()

  private val _assignedTeacherHalaqahId = MutableStateFlow<String?>(null)
  val assignedTeacherHalaqahId: StateFlow<String?> = _assignedTeacherHalaqahId.asStateFlow()

  private val _assignedTeacherHalaqahName = MutableStateFlow<String?>(null)
  val assignedTeacherHalaqahName: StateFlow<String?> = _assignedTeacherHalaqahName.asStateFlow()

  private val _isGirlsVerifying = MutableStateFlow(false)
  val isGirlsVerifying: StateFlow<Boolean> = _isGirlsVerifying.asStateFlow()

  val syncState: StateFlow<SyncState> = syncManager.syncState

  private val _authenticatedRole = MutableStateFlow(UserRole.TEACHER)
  val authenticatedRole: StateFlow<UserRole> = _authenticatedRole.asStateFlow()

  private val _currentRole = MutableStateFlow(UserRole.TEACHER)
  val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

  private val _isLoggedIn = MutableStateFlow(false)
  val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  private val _loggedInUserName = MutableStateFlow<String?>("الشيخ عبد الله (معلم الحلقة)")
  val loggedInUserName: StateFlow<String?> = _loggedInUserName.asStateFlow()

  private val _currentAccessCode = MutableStateFlow<AccessCode?>(null)
  val currentAccessCode: StateFlow<AccessCode?> = _currentAccessCode.asStateFlow()

  private val _activeSection = MutableStateFlow(HalaqahSection.ALL)
  val activeSection: StateFlow<HalaqahSection> = _activeSection.asStateFlow()

  // APK Installation Package Management
  val defaultApkDownloadUrl = "https://github.com/adffakff-ui/al-sabri-app/releases/latest/download/app-debug.apk"
  val defaultApkReleasePage = "https://github.com/adffakff-ui/al-sabri-app/releases/tag/v1.0.0"

  private val _apkDownloadUrl = MutableStateFlow(defaultApkDownloadUrl)
  val apkDownloadUrl: StateFlow<String> = _apkDownloadUrl.asStateFlow()

  fun updateApkDownloadUrl(newUrl: String) {
    if (newUrl.isNotBlank()) {
      _apkDownloadUrl.value = newUrl.trim()
    }
  }

  fun login(role: UserRole, userName: String) {
    _authenticatedRole.value = role
    _currentRole.value = role
    _loggedInUserName.value = userName
    _isLoggedIn.value = true
    performSync()
  }

  fun loginWithAccessCode(accessCode: AccessCode) {
    _currentAccessCode.value = accessCode
    val effectiveRole = if (accessCode.role == UserRole.GIRLS_ADMIN || accessCode.code == "SUP-GIRLS-200") {
      UserRole.GIRLS_ADMIN
    } else {
      accessCode.role
    }
    _authenticatedRole.value = effectiveRole
    _currentRole.value = effectiveRole
    _loggedInUserName.value = accessCode.userName
    _activeSection.value = accessCode.section
    _selectedGroup.value = accessCode.groupName
    _isLoggedIn.value = true

    viewModelScope.launch {
      if (accessCode.studentId != null) {
        val st = studentDao.getStudentById(accessCode.studentId)
        _selectedStudent.value = st
      } else if (accessCode.groupName != null) {
        val groupStudents = studentDao.getAllStudents().first().filter { it.groupName.contains(accessCode.groupName) }
        _selectedStudent.value = groupStudents.firstOrNull()
      }
    }
    performSync()
  }

  fun loginWithCodeString(codeStr: String): Boolean {
    val found = AccessCodeRegistry.findCode(codeStr) ?: return false
    loginWithAccessCode(found)
    return true
  }

  fun logout() {
    _isLoggedIn.value = false
    _currentAccessCode.value = null
  }

  fun performSync() {
    viewModelScope.launch {
      syncManager.performInstantSync()
    }
  }

  fun isRoleSwitchingAllowed(): Boolean {
    val auth = _authenticatedRole.value
    // Only General Supervisor can switch between views freely
    return (auth == UserRole.SUPERVISOR && _currentAccessCode.value?.section == HalaqahSection.ALL) || auth == UserRole.DEVELOPER
  }

  fun isGirlsSectionAuthorized(): Boolean {
    val auth = _authenticatedRole.value
    val code = _currentAccessCode.value
    return auth == UserRole.GIRLS_ADMIN ||
        code?.section == HalaqahSection.GIRLS ||
        code?.code == "SUP-GIRLS-200" ||
        auth == UserRole.DEVELOPER
  }

  suspend fun verifyAndAuthorizeGirlsAccessAsync(codeStr: String): Pair<Boolean, String> {
    _isGirlsVerifying.value = true
    val trimmed = codeStr.trim().uppercase()
    if (trimmed.isEmpty()) {
      _isGirlsVerifying.value = false
      return Pair(false, "يرجى إدخال رمز الأمان أو كود المشرفة")
    }

    val result = girlsRepository.verifySupervisorIdentity(trimmed)
    _girlsVerificationResult.value = result
    _isGirlsVerifying.value = false

    return when (result) {
      is GirlsVerificationResult.Success -> {
        _isTeacherScopeOnly.value = !result.isFullSupervisor
        _assignedTeacherHalaqahId.value = result.assignedHalaqahId
        _assignedTeacherHalaqahName.value = result.assignedHalaqahName

        val accessCode = AccessCodeRegistry.findCode(result.code) ?: AccessCode(
          code = result.code,
          role = if (result.isFullSupervisor) UserRole.GIRLS_ADMIN else UserRole.TEACHER,
          userName = result.name,
          groupName = result.assignedHalaqahName,
          section = HalaqahSection.GIRLS,
          scopeDescription = if (result.isFullSupervisor) "مشرفة قطاع البنات: إدارة شاملة وعزل تام عن البنين" else "معلمة: خصوصية مقيدة بحلقة ${result.assignedHalaqahName}",
          allowedPrivileges = listOf("الوصول الآمن لحلقات قسم البنات"),
          restrictedPrivileges = listOf("حظر كامل لبيانات قطاع البنين")
        )
        loginWithAccessCode(accessCode)
        Pair(true, "مرحباً ${result.name} • ${result.securityBadgeMessage}")
      }
      is GirlsVerificationResult.Denied -> {
        Pair(false, result.reason)
      }
    }
  }

  fun verifyAndAuthorizeGirlsAccess(codeStr: String): Pair<Boolean, String> {
    val trimmed = codeStr.trim().uppercase()
    if (trimmed.isEmpty()) {
      return Pair(false, "يرجى إدخال رمز الأمان أو كود المشرفة")
    }

    // Immediate check for boy section attempts
    if (trimmed.startsWith("SUP-BOYS") || trimmed.startsWith("TCH-OTHMAN") ||
        trimmed.startsWith("TCH-ALI") || trimmed.startsWith("STU-ABDELRAHMAN") ||
        trimmed == "100" || trimmed == "BOYS") {
      val denied = GirlsVerificationResult.Denied(
        reason = "صلاحية مرفوضة: هذا الكود يتبع قطاع البنين. تخضع بيانات قسم البنات لعزل تام ومحمي بقواعد أمان Firestore.",
        isBoySectionViolation = true
      )
      _girlsVerificationResult.value = denied
      return Pair(false, denied.reason)
    }

    // Match approved teacher
    val matchedTeacher = GirlsFirestoreRepository.APPROVED_TEACHERS.firstOrNull { it.code.uppercase() == trimmed }
    if (matchedTeacher != null) {
      val success = GirlsVerificationResult.Success(
        name = matchedTeacher.name,
        code = matchedTeacher.code,
        roleTitle = "معلمة حلقة نسائية (نطاق مقيد)",
        isFullSupervisor = false,
        assignedHalaqahId = matchedTeacher.assignedHalaqahId,
        assignedHalaqahName = matchedTeacher.assignedHalaqahName,
        securityBadgeMessage = "معلمة معتمدة في Firestore • خصوصية الحلقة مفعلة"
      )
      _girlsVerificationResult.value = success
      _isTeacherScopeOnly.value = true
      _assignedTeacherHalaqahId.value = matchedTeacher.assignedHalaqahId
      _assignedTeacherHalaqahName.value = matchedTeacher.assignedHalaqahName

      val accessCode = AccessCodeRegistry.findCode(trimmed) ?: AccessCode(
        code = matchedTeacher.code,
        role = UserRole.TEACHER,
        userName = matchedTeacher.name,
        groupName = matchedTeacher.assignedHalaqahName,
        section = HalaqahSection.GIRLS,
        scopeDescription = "معلمة: خصوصية مقيدة بحلقة ${matchedTeacher.assignedHalaqahName}",
        allowedPrivileges = listOf("رصد التسميع والغياب لطالبات الحلقة"),
        restrictedPrivileges = listOf("حجب سائر الحلقات الأخرى وقسم البنين")
      )
      loginWithAccessCode(accessCode)
      return Pair(true, "مرحباً ${matchedTeacher.name} • تم التحقق من اعتماد المعلمة بحلقة ${matchedTeacher.assignedHalaqahName}")
    }

    // Match approved supervisor
    val matchedSupervisor = GirlsFirestoreRepository.APPROVED_SUPERVISORS.firstOrNull {
      it.code.uppercase() == trimmed || (trimmed in listOf("2024", "200", "GIRLS2024") && it.code == "SUP-GIRLS-200")
    }
    if (matchedSupervisor != null) {
      val success = GirlsVerificationResult.Success(
        name = matchedSupervisor.name,
        code = matchedSupervisor.code,
        roleTitle = "مشرفة قطاع البنات (إدارة شاملة)",
        isFullSupervisor = true,
        assignedHalaqahId = null,
        assignedHalaqahName = null,
        securityBadgeMessage = "هوية معتمدة في Firestore • أمان معزول"
      )
      _girlsVerificationResult.value = success
      _isTeacherScopeOnly.value = false
      _assignedTeacherHalaqahId.value = null
      _assignedTeacherHalaqahName.value = null

      val accessCode = AccessCodeRegistry.findCode(matchedSupervisor.code) ?: AccessCode(
        code = matchedSupervisor.code,
        role = UserRole.GIRLS_ADMIN,
        userName = matchedSupervisor.name,
        section = HalaqahSection.GIRLS,
        scopeDescription = "مشرفة قطاع البنات: إدارة شاملة وعزل تام عن البنين",
        allowedPrivileges = listOf("إدارة وتوثيق حلقات البنات"),
        restrictedPrivileges = listOf("حجب كامل لبيانات قطاع البنين")
      )
      loginWithAccessCode(accessCode)
      return Pair(true, "مرحباً ${matchedSupervisor.name} • هوية معتمدة في Firestore")
    }

    val denied = GirlsVerificationResult.Denied("رمز الدخول غير صحيح أو غير مسجل في منظومة مشرفات ومعلمات البنات")
    _girlsVerificationResult.value = denied
    return Pair(false, denied.reason)
  }

  fun logoutGirlsAdmin() {
    _girlsVerificationResult.value = null
    _isTeacherScopeOnly.value = false
    _assignedTeacherHalaqahId.value = null
    _assignedTeacherHalaqahName.value = null
    _currentAccessCode.value = null
    _isLoggedIn.value = false
    _activeSection.value = HalaqahSection.ALL
  }

  val students: StateFlow<List<Student>> = studentDao.getAllStudents()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val attendance: StateFlow<List<AttendanceRecord>> = attendanceDao.getAllAttendance()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val recitations: StateFlow<List<RecitationRecord>> = recitationDao.getAllRecitations()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val notifications: StateFlow<List<ParentNotification>> = notificationDao.getAllNotifications()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val schedules: StateFlow<List<RecitationSchedule>> = scheduleDao.getAllSchedules()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dynamic state for custom Girls Halaqat added by supervisors
  private val _customGirlsHalaqat = MutableStateFlow<List<QuranHalaqah>>(emptyList())
  val customGirlsHalaqat: StateFlow<List<QuranHalaqah>> = _customGirlsHalaqat.asStateFlow()

  val allGirlsHalaqat: StateFlow<List<QuranHalaqah>> = combine(
    _customGirlsHalaqat,
    _isTeacherScopeOnly,
    _assignedTeacherHalaqahName
  ) { custom, isTeacherOnly, teacherHalaqahName ->
    val base = QuranHalaqatData.getHalaqatBySection(HalaqahSection.GIRLS)
    val all = (custom + base).distinctBy { it.name }
    if (isTeacherOnly && teacherHalaqahName != null) {
      all.filter { it.name.contains(teacherHalaqahName) || teacherHalaqahName.contains(it.name) }
    } else {
      all
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), QuranHalaqatData.getHalaqatBySection(HalaqahSection.GIRLS))

  val girlsStudents: StateFlow<List<Student>> = combine(
    students,
    _customGirlsHalaqat,
    _isTeacherScopeOnly,
    _assignedTeacherHalaqahName
  ) { list, _, isTeacherOnly, teacherHalaqahName ->
    val girlsOnly = list.filter { it.isGirlsSection }
    if (isTeacherOnly && teacherHalaqahName != null) {
      girlsOnly.filter { it.groupName.contains(teacherHalaqahName) || teacherHalaqahName.contains(it.groupName) }
    } else {
      girlsOnly
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _selectedStudent = MutableStateFlow<Student?>(null)
  val selectedStudent: StateFlow<Student?> = _selectedStudent.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedGroup = MutableStateFlow<String?>(null)
  val selectedGroup: StateFlow<String?> = _selectedGroup.asStateFlow()

  // Filtered students according to Active Code and Isolation boundaries
  val scopedStudents: StateFlow<List<Student>> = combine(
    students,
    _currentAccessCode,
    _selectedGroup,
    _isTeacherScopeOnly,
    _assignedTeacherHalaqahName
  ) { allStudents, code, group, isTeacherOnly, teacherHalaqahName ->
    when {
      // Teacher scope in girls section -> restrict strictly to her assigned halaqah
      isTeacherOnly && teacherHalaqahName != null ->
        allStudents.filter { it.isGirlsSection && (it.groupName.contains(teacherHalaqahName) || teacherHalaqahName.contains(it.groupName)) }
      // 1. Single student or parent view -> isolated strictly to their student
      code?.studentId != null -> allStudents.filter { it.id == code.studentId }
      // 2. Specific halaqah teacher -> isolated strictly to their halaqah
      code?.groupName != null -> allStudents.filter { it.groupName.contains(code.groupName) || code.groupName.contains(it.groupName) }
      // 3. Sectional supervisor -> isolated to Girls or Boys
      code?.section == HalaqahSection.GIRLS || _currentRole.value == UserRole.GIRLS_ADMIN -> allStudents.filter { it.isGirlsSection }
      code?.section == HalaqahSection.BOYS -> allStudents.filter { !it.isGirlsSection }
      // 4. Filter by group if selected
      group != null -> allStudents.filter { it.groupName.contains(group) }
      else -> allStudents
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Filtered halaqat according to Active Code and Section
  val scopedHalaqat: StateFlow<List<QuranHalaqah>> = combine(
    _currentAccessCode,
    _activeSection,
    _customGirlsHalaqat,
    _isTeacherScopeOnly,
    _assignedTeacherHalaqahName
  ) { code, section, customGirls, isTeacherOnly, teacherHalaqahName ->
    val allGirls = (customGirls + QuranHalaqatData.getHalaqatBySection(HalaqahSection.GIRLS)).distinctBy { it.name }
    when {
      isTeacherOnly && teacherHalaqahName != null ->
        allGirls.filter { it.name.contains(teacherHalaqahName) || teacherHalaqahName.contains(it.name) }
      code?.groupName != null -> (QuranHalaqatData.halaqatList + customGirls).filter { it.name.contains(code.groupName) || code.groupName.contains(it.name) }
      code?.section == HalaqahSection.GIRLS || _currentRole.value == UserRole.GIRLS_ADMIN -> allGirls
      code?.section == HalaqahSection.BOYS -> QuranHalaqatData.getHalaqatBySection(HalaqahSection.BOYS)
      section == HalaqahSection.GIRLS -> allGirls
      section == HalaqahSection.BOYS -> QuranHalaqatData.getHalaqatBySection(HalaqahSection.BOYS)
      else -> (QuranHalaqatData.halaqatList + customGirls).distinctBy { it.name }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), QuranHalaqatData.halaqatList)


  init {
    viewModelScope.launch {
      // Check if empty, preload sample data
      studentDao.getAllStudents().collect { list ->
        if (list.isEmpty()) {
          preloadInitialData()
        } else if (_selectedStudent.value == null && list.isNotEmpty()) {
          _selectedStudent.value = list.first()
        }
      }
    }
  }

  fun setRole(role: UserRole): Boolean {
    val auth = _authenticatedRole.value
    if (auth == UserRole.SUPERVISOR || auth == UserRole.DEVELOPER || role == UserRole.AI_ASSISTANT || auth == role) {
      _currentRole.value = role
      return true
    }
    return false
  }

  fun broadcastSupervisorAnnouncement(title: String, message: String) {
    viewModelScope.launch {
      val studentsList = studentDao.getAllStudents().first()
      studentsList.forEach { student ->
        notificationDao.insertNotification(
          ParentNotification(
            studentId = student.id,
            studentName = student.name,
            parentPhone = student.phone,
            title = title,
            message = message,
            date = SampleDataProvider.todayDate,
            notificationType = "تعميم إشرافي"
          )
        )
      }
      performSync()
    }
  }

  fun selectStudent(student: Student?) {
    _selectedStudent.value = student
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSelectedGroup(group: String?) {
    _selectedGroup.value = group
  }

  fun addStudent(student: Student) {
    viewModelScope.launch {
      studentDao.insertStudent(student)
      performSync()
    }
  }

  fun enrollGirlsStudent(student: Student) {
    viewModelScope.launch {
      studentDao.insertStudent(student.copy(isGirlsSection = true))
      performSync()
    }
  }

  fun addGirlsHalaqah(halaqah: QuranHalaqah) {
    _customGirlsHalaqat.value = listOf(halaqah.copy(section = HalaqahSection.GIRLS)) + _customGirlsHalaqat.value
    performSync()
  }

  fun updateGirlsHalaqah(halaqah: QuranHalaqah) {
    _customGirlsHalaqat.value = _customGirlsHalaqat.value.map {
      if (it.id == halaqah.id) halaqah else it
    }
    performSync()
  }

  fun deleteGirlsHalaqah(id: Long) {
    _customGirlsHalaqat.value = _customGirlsHalaqat.value.filter { it.id != id }
    performSync()
  }

  fun updateStudent(student: Student) {
    viewModelScope.launch {
      studentDao.updateStudent(student)
      if (_selectedStudent.value?.id == student.id) {
        _selectedStudent.value = student
      }
      performSync()
    }
  }

  fun deleteStudent(student: Student) {
    viewModelScope.launch {
      studentDao.deleteStudent(student)
      if (_selectedStudent.value?.id == student.id) {
        _selectedStudent.value = null
      }
      performSync()
    }
  }

  fun recordAttendance(record: AttendanceRecord) {
    viewModelScope.launch {
      attendanceDao.deleteByStudentAndDate(record.studentId, record.date)
      attendanceDao.insertAttendance(record)
      performSync()
    }
  }

  fun sendAbsenceNotification(student: Student, date: String, reason: String = "") {
    viewModelScope.launch {
      // 1. Record attendance as absent
      attendanceDao.deleteByStudentAndDate(student.id, date)
      attendanceDao.insertAttendance(
        AttendanceRecord(
          studentId = student.id,
          studentName = student.name,
          date = date,
          status = "غائب",
          notes = reason
        )
      )

      // 2. Dispatch Firebase Cloud Messaging (FCM) automated push notification
      FcmNotificationManager.dispatchAbsenceFcmAlert(
        context = getApplication(),
        studentName = student.name,
        parentPhone = student.parentPhone.ifBlank { student.phone },
        halaqahName = student.groupName,
        date = date,
        reason = reason
      )

      // 3. Record in ParentNotification table
      notificationDao.insertNotification(
        ParentNotification(
          studentId = student.id,
          studentName = student.name,
          parentPhone = student.parentPhone.ifBlank { student.phone },
          title = "⚠️ تنبيه غياب تلقائي عبر FCM",
          message = "نحيطكم علماً بتسجيل غياب الطالب/الطالبة (${student.name}) عن (${student.groupName}) بتاريخ $date. نرجو التواصل مع معلم الحلقة للاطمئنان والتنسيق.",
          notificationType = "تنبيه غياب",
          date = date,
          isSent = true
        )
      )

      performSync()
    }
  }

  fun recordRecitation(record: RecitationRecord) {
    viewModelScope.launch {
      recitationDao.insertRecitation(record)
      val student = studentDao.getStudentById(record.studentId)
      if (student != null && record.recitationType == "حفظ جديد") {
        val updated = student.copy(
          currentSurah = record.surahName,
          currentAyah = record.toAyah
        )
        studentDao.updateStudent(updated)
        if (_selectedStudent.value?.id == student.id) {
          _selectedStudent.value = updated
        }
      }
      performSync()
    }
  }

  fun sendNotification(notification: ParentNotification) {
    viewModelScope.launch {
      notificationDao.insertNotification(notification)
      performSync()
    }
  }

  fun scheduleRecitation(schedule: RecitationSchedule, sendFcmAlert: Boolean = true) {
    viewModelScope.launch {
      val id = scheduleDao.insertSchedule(schedule)
      if (sendFcmAlert) {
        val savedSchedule = schedule.copy(id = id)
        // 1. Dispatch Firebase Cloud Messaging (FCM) automated push notification
        FcmNotificationManager.dispatchScheduleFcmNotification(getApplication(), savedSchedule)

        // 2. Automatically record in ParentNotification
        val notifMessage = "تم تحديد موعد تسميع للطالب ${schedule.studentName} يوم ${schedule.scheduledDate} في تمام ${schedule.scheduledTime}. المقرر: ${schedule.surahName} (الآيات ${schedule.fromAyah}-${schedule.toAyah}). نوع التسميع: ${schedule.recitationType}."
        notificationDao.insertNotification(
          ParentNotification(
            studentId = schedule.studentId,
            studentName = schedule.studentName,
            parentPhone = schedule.parentPhone,
            title = "📅 موعد تسميع مجدول عبر FCM",
            message = notifMessage,
            notificationType = "موعد تسميع مجدول",
            date = SampleDataProvider.todayDate,
            isSent = true
          )
        )
      }
      performSync()
    }
  }

  fun updateScheduleStatus(schedule: RecitationSchedule, newStatus: String) {
    viewModelScope.launch {
      scheduleDao.updateSchedule(schedule.copy(status = newStatus))
      performSync()
    }
  }

  fun deleteSchedule(schedule: RecitationSchedule) {
    viewModelScope.launch {
      scheduleDao.deleteSchedule(schedule)
      performSync()
    }
  }

  fun preloadInitialData() {
    viewModelScope.launch {
      studentDao.insertAll(SampleDataProvider.sampleStudents)
      attendanceDao.insertAll(SampleDataProvider.sampleAttendance)
      recitationDao.insertAll(SampleDataProvider.sampleRecitations)
      notificationDao.insertAll(SampleDataProvider.sampleNotifications)
      scheduleDao.insertAll(SampleDataProvider.sampleSchedules)
      _selectedStudent.value = SampleDataProvider.sampleStudents.firstOrNull()
    }
  }

  fun resetToSampleData() {
    viewModelScope.launch {
      studentDao.deleteAll()
      attendanceDao.deleteAll()
      recitationDao.deleteAll()
      notificationDao.deleteAll()
      scheduleDao.deleteAll()
      preloadInitialData()
    }
  }

  fun clearAllData() {
    viewModelScope.launch {
      studentDao.deleteAll()
      attendanceDao.deleteAll()
      recitationDao.deleteAll()
      notificationDao.deleteAll()
      scheduleDao.deleteAll()
      _selectedStudent.value = null
    }
  }
}
