package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PermissionManager
import com.example.data.model.QariDirectory
import com.example.data.model.TenRecitationsData
import com.example.viewmodel.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("حلقات هائل سعيد أنعم", appName)
  }

  @Test
  fun `verify ten recitations and islamic madhhabs data`() {
    assertEquals(10, TenRecitationsData.qiraatList.size)
    assertEquals(7, TenRecitationsData.madhhabsList.size)
    assertTrue(TenRecitationsData.fiqhImpactExamples.isNotEmpty())
  }

  @Test
  fun `verify qari directory and student level calculation`() {
    assertTrue(QariDirectory.qaris.isNotEmpty())
    val assessment = QariDirectory.calculateStudentLevel(9, 9, 9, 9)
    assertEquals(90, assessment.percentage)
    assertTrue(assessment.levelTitle.contains("متقن"))
  }

  @Test
  fun `verify role based access control and supervisor permissions`() {
    val supervisorPerms = PermissionManager.getPermissions(UserRole.SUPERVISOR)
    assertTrue(supervisorPerms.canSwitchRoles)
    assertTrue(supervisorPerms.canAuditRecitations)
    assertTrue(supervisorPerms.canBroadcastAnnouncements)

    val teacherPerms = PermissionManager.getPermissions(UserRole.TEACHER)
    assertFalse(teacherPerms.canSwitchRoles)
    assertFalse(teacherPerms.canBroadcastAnnouncements)
    assertTrue(teacherPerms.canRecordRecitation)

    val studentPerms = PermissionManager.getPermissions(UserRole.STUDENT)
    assertFalse(studentPerms.canSwitchRoles)
    assertFalse(studentPerms.canRecordRecitation)
    assertFalse(studentPerms.canTakeAttendance)
  }
}
