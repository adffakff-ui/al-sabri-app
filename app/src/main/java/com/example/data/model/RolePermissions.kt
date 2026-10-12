package com.example.data.model

import com.example.viewmodel.UserRole

data class RolePermissions(
  val role: UserRole,
  val canManageStudents: Boolean,
  val canRecordRecitation: Boolean,
  val canTakeAttendance: Boolean,
  val canSendNotifications: Boolean,
  val canViewAllCircles: Boolean,
  val canSwitchRoles: Boolean,
  val canManageSystemRoles: Boolean,
  val canBroadcastAnnouncements: Boolean,
  val canAccessQiraatAcademic: Boolean,
  val canAuditRecitations: Boolean,
  val canTriggerManualSync: Boolean,
  val description: String
)

object PermissionManager {
  fun getPermissions(role: UserRole): RolePermissions {
    return when (role) {
      UserRole.SUPERVISOR -> RolePermissions(
        role = role,
        canManageStudents = true,
        canRecordRecitation = true,
        canTakeAttendance = true,
        canSendNotifications = true,
        canViewAllCircles = true,
        canSwitchRoles = true, // المشرف العام يملك حق التنقل الرقابي والإشرافي بين جميع الأقسام
        canManageSystemRoles = true,
        canBroadcastAnnouncements = true,
        canAccessQiraatAcademic = true,
        canAuditRecitations = true,
        canTriggerManualSync = true,
        description = "كامل الصلاحيات الإشرافية والتنفيذية والرقابة العامة على الحلقات والمعلمين والطلاب"
      )
      UserRole.TEACHER -> RolePermissions(
        role = role,
        canManageStudents = true,
        canRecordRecitation = true,
        canTakeAttendance = true,
        canSendNotifications = true,
        canViewAllCircles = false,
        canSwitchRoles = false, // مقيد لحلقته فقط ومغلق باقي أقسام التطبيق
        canManageSystemRoles = false,
        canBroadcastAnnouncements = false,
        canAccessQiraatAcademic = true,
        canAuditRecitations = false,
        canTriggerManualSync = true,
        description = "صلاحيات المعلم: رصد التسميع والدرجات، تحضير الطلاب، والتواصل مع أولياء الأمور"
      )
      UserRole.STUDENT -> RolePermissions(
        role = role,
        canManageStudents = false,
        canRecordRecitation = false,
        canTakeAttendance = false,
        canSendNotifications = false,
        canViewAllCircles = false,
        canSwitchRoles = false, // مقيد لسجل الطالب فقط ومغلق باقي أقسام التطبيق
        canManageSystemRoles = false,
        canBroadcastAnnouncements = false,
        canAccessQiraatAcademic = true,
        canAuditRecitations = false,
        canTriggerManualSync = true,
        description = "صلاحيات الطالب: الاطلاع على سجل الحفظ الشخصي، التقييمات، الاستماع والتسجيل التجريبي"
      )
      UserRole.PARENT -> RolePermissions(
        role = role,
        canManageStudents = false,
        canRecordRecitation = false,
        canTakeAttendance = false,
        canSendNotifications = false,
        canViewAllCircles = false,
        canSwitchRoles = false, // مقيد لتقارير الأبناء فقط
        canManageSystemRoles = false,
        canBroadcastAnnouncements = false,
        canAccessQiraatAcademic = false,
        canAuditRecitations = false,
        canTriggerManualSync = true,
        description = "صلاحيات ولي الأمر: متابعة حضور وتسميع الأبناء واستلام الإشعارات والتنبيهات"
      )
      UserRole.CIRCLES -> RolePermissions(
        role = role,
        canManageStudents = false,
        canRecordRecitation = false,
        canTakeAttendance = false,
        canSendNotifications = false,
        canViewAllCircles = true,
        canSwitchRoles = false, // مقيد لدليل الحلقات فقط
        canManageSystemRoles = false,
        canBroadcastAnnouncements = false,
        canAccessQiraatAcademic = false,
        canAuditRecitations = false,
        canTriggerManualSync = true,
        description = "صلاحيات قسم الحلقات: الاطلاع على دليل ومواعيد ومواقع حلقات التحفيظ والمعلمين"
      )
      UserRole.QIRAAT -> RolePermissions(
        role = role,
        canManageStudents = false,
        canRecordRecitation = false,
        canTakeAttendance = false,
        canSendNotifications = false,
        canViewAllCircles = false,
        canSwitchRoles = false, // مقيد لأكاديمية القراءات فقط
        canManageSystemRoles = false,
        canBroadcastAnnouncements = false,
        canAccessQiraatAcademic = true,
        canAuditRecitations = false,
        canTriggerManualSync = true,
        description = "صلاحيات أكاديمية القراءات: استعراض القراءات العشر والروايات وأصول التلاوة والمذاهب الفقهية"
      )
      UserRole.DEVELOPER -> RolePermissions(
        role = role,
        canManageStudents = true,
        canRecordRecitation = true,
        canTakeAttendance = true,
        canSendNotifications = true,
        canViewAllCircles = true,
        canSwitchRoles = true,
        canManageSystemRoles = true,
        canBroadcastAnnouncements = true,
        canAccessQiraatAcademic = true,
        canAuditRecitations = true,
        canTriggerManualSync = true,
        description = "صلاحيات المطور التقني: إدارة البيانات والنسخ الاحتياطي وإعدادات النظام"
      )
    }
  }
}
