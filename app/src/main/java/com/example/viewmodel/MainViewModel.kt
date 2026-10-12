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

  fun login(role: UserRole, userName: String) {
    _authenticatedRole.value = role
    _currentRole.value = role
    _loggedInUserName.value = userName
    _isLoggedIn.value = true
    performSync()
  }

  fun loginWithAccessCode(accessCode: AccessCode) {
    _currentAccessCode.value = accessCode
    _authenticatedRole.value = accessCode.role
    _currentRole.value = accessCode.role
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
    _selectedGroup
  ) { allStudents, code, group ->
    when {
      // 1. Single student or parent view -> isolated strictly to their student
      code?.studentId != null -> allStudents.filter { it.id == code.studentId }
      // 2. Specific halaqah teacher -> isolated strictly to their halaqah
      code?.groupName != null -> allStudents.filter { it.groupName.contains(code.groupName) || code.groupName.contains(it.groupName) }
      // 3. Sectional supervisor -> isolated to Girls or Boys
      code?.section == HalaqahSection.GIRLS -> allStudents.filter { it.isGirlsSection }
      code?.section == HalaqahSection.BOYS -> allStudents.filter { !it.isGirlsSection }
      // 4. Filter by group if selected
      group != null -> allStudents.filter { it.groupName.contains(group) }
      else -> allStudents
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Filtered halaqat according to Active Code and Section
  val scopedHalaqat: StateFlow<List<QuranHalaqah>> = combine(
    _currentAccessCode,
    _activeSection
  ) { code, section ->
    when {
      code?.groupName != null -> QuranHalaqatData.halaqatList.filter { it.name.contains(code.groupName) || code.groupName.contains(it.name) }
      code?.section == HalaqahSection.GIRLS -> QuranHalaqatData.getHalaqatBySection(HalaqahSection.GIRLS)
      code?.section == HalaqahSection.BOYS -> QuranHalaqatData.getHalaqatBySection(HalaqahSection.BOYS)
      section != HalaqahSection.ALL -> QuranHalaqatData.getHalaqatBySection(section)
      else -> QuranHalaqatData.halaqatList
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
    if (auth == UserRole.SUPERVISOR || auth == UserRole.DEVELOPER || auth == role) {
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
