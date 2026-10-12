package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SampleDataProvider
import com.example.data.local.SyncManager
import com.example.data.local.SyncState
import com.example.data.model.AttendanceRecord
import com.example.data.model.ParentNotification
import com.example.data.model.RecitationRecord
import com.example.data.model.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

  fun login(role: UserRole, userName: String) {
    _authenticatedRole.value = role
    _currentRole.value = role
    _loggedInUserName.value = userName
    _isLoggedIn.value = true
    performSync()
  }

  fun logout() {
    _isLoggedIn.value = false
  }

  fun performSync() {
    viewModelScope.launch {
      syncManager.performInstantSync()
    }
  }

  fun isRoleSwitchingAllowed(): Boolean {
    val auth = _authenticatedRole.value
    return auth == UserRole.SUPERVISOR || auth == UserRole.DEVELOPER
  }

  val students: StateFlow<List<Student>> = studentDao.getAllStudents()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val attendance: StateFlow<List<AttendanceRecord>> = attendanceDao.getAllAttendance()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val recitations: StateFlow<List<RecitationRecord>> = recitationDao.getAllRecitations()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val notifications: StateFlow<List<ParentNotification>> = notificationDao.getAllNotifications()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _selectedStudent = MutableStateFlow<Student?>(null)
  val selectedStudent: StateFlow<Student?> = _selectedStudent.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedGroup = MutableStateFlow<String?>(null)
  val selectedGroup: StateFlow<String?> = _selectedGroup.asStateFlow()

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
    }
  }

  fun updateStudent(student: Student) {
    viewModelScope.launch {
      studentDao.updateStudent(student)
      if (_selectedStudent.value?.id == student.id) {
        _selectedStudent.value = student
      }
    }
  }

  fun deleteStudent(student: Student) {
    viewModelScope.launch {
      studentDao.deleteStudent(student)
      if (_selectedStudent.value?.id == student.id) {
        _selectedStudent.value = null
      }
    }
  }

  fun recordAttendance(record: AttendanceRecord) {
    viewModelScope.launch {
      // Delete any existing attendance for this student on the same date to avoid duplicates
      attendanceDao.deleteByStudentAndDate(record.studentId, record.date)
      attendanceDao.insertAttendance(record)
    }
  }

  fun recordRecitation(record: RecitationRecord) {
    viewModelScope.launch {
      recitationDao.insertRecitation(record)
      // Update student's current progress
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
    }
  }

  fun sendNotification(notification: ParentNotification) {
    viewModelScope.launch {
      notificationDao.insertNotification(notification)
    }
  }

  fun preloadInitialData() {
    viewModelScope.launch {
      studentDao.insertAll(SampleDataProvider.sampleStudents)
      attendanceDao.insertAll(SampleDataProvider.sampleAttendance)
      recitationDao.insertAll(SampleDataProvider.sampleRecitations)
      notificationDao.insertAll(SampleDataProvider.sampleNotifications)
      _selectedStudent.value = SampleDataProvider.sampleStudents.firstOrNull()
    }
  }

  fun resetToSampleData() {
    viewModelScope.launch {
      studentDao.deleteAll()
      attendanceDao.deleteAll()
      recitationDao.deleteAll()
      notificationDao.deleteAll()
      preloadInitialData()
    }
  }

  fun clearAllData() {
    viewModelScope.launch {
      studentDao.deleteAll()
      attendanceDao.deleteAll()
      recitationDao.deleteAll()
      notificationDao.deleteAll()
      _selectedStudent.value = null
    }
  }
}
