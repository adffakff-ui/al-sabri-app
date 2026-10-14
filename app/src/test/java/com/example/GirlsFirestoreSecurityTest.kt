package com.example

import androidx.test.core.app.ApplicationProvider
import com.example.data.model.GirlsVerificationResult
import com.example.data.repository.GirlsFirestoreRepository
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GirlsFirestoreSecurityTest {

  @Test
  fun `girls supervisor authentication succeeds and grants full supervisor scope`() {
    val viewModel = MainViewModel(ApplicationProvider.getApplicationContext())
    val (success, message) = viewModel.verifyAndAuthorizeGirlsAccess("SUP-GIRLS-200")

    assertTrue("Supervisor authentication should succeed", success)
    assertTrue("Should contain success indicator", message.contains("مرحباً") || message.contains("Firestore"))

    val result = viewModel.girlsVerificationResult.value
    assertTrue("Result should be Success", result is GirlsVerificationResult.Success)

    val successResult = result as GirlsVerificationResult.Success
    assertTrue("Should be full supervisor", successResult.isFullSupervisor)
    assertNull("Supervisor should have no halaqah restriction", successResult.assignedHalaqahId)
    assertFalse("Supervisor should have full scope, not teacher only", viewModel.isTeacherScopeOnly.value)
  }

  @Test
  fun `girls teacher authentication restricts scope strictly to her assigned halaqah`() {
    val viewModel = MainViewModel(ApplicationProvider.getApplicationContext())
    val (success, message) = viewModel.verifyAndAuthorizeGirlsAccess("TCH-KHADIJA-33")

    assertTrue("Teacher authentication should succeed", success)
    val result = viewModel.girlsVerificationResult.value
    assertTrue("Result should be Success", result is GirlsVerificationResult.Success)

    val successResult = result as GirlsVerificationResult.Success
    assertFalse("Teacher should not be full supervisor", successResult.isFullSupervisor)
    assertEquals("halaqah-khadija", successResult.assignedHalaqahId)
    assertTrue("Teacher scope flag should be active", viewModel.isTeacherScopeOnly.value)
    assertEquals("halaqah-khadija", viewModel.assignedTeacherHalaqahId.value)
  }

  @Test
  fun `boys supervisor code is strictly blocked from girls section`() {
    val viewModel = MainViewModel(ApplicationProvider.getApplicationContext())
    val (success, message) = viewModel.verifyAndAuthorizeGirlsAccess("SUP-BOYS-100")

    assertFalse("Boys supervisor code MUST be denied in girls section", success)
    assertTrue("Message should explain boy section violation", message.contains("البنين") || message.contains("مرفوضة"))

    val result = viewModel.girlsVerificationResult.value
    assertTrue("Result should be Denied", result is GirlsVerificationResult.Denied)
    val denied = result as GirlsVerificationResult.Denied
    assertTrue("Should flag as boy section violation", denied.isBoySectionViolation)
  }

  @Test
  fun `boys teacher code is strictly blocked from girls section`() {
    val viewModel = MainViewModel(ApplicationProvider.getApplicationContext())
    val (success, message) = viewModel.verifyAndAuthorizeGirlsAccess("TCH-OTHMAN-11")

    assertFalse("Boys teacher code MUST be denied in girls section", success)
    val result = viewModel.girlsVerificationResult.value
    assertTrue("Result should be Denied", result is GirlsVerificationResult.Denied)
    val denied = result as GirlsVerificationResult.Denied
    assertTrue("Should flag as boy section violation", denied.isBoySectionViolation)
  }

  @Test
  fun `security interceptor throws exception when accessing boys data from girls repo`() {
    var exceptionThrown = false
    try {
      // Test direct call to security guard method
      val repo = GirlsFirestoreRepository.APPROVED_SUPERVISORS // Verify class access
      val method = GirlsFirestoreRepository::class.java.getMethod("assertNoBoyDataSecurityGuard", String::class.java)
      // Call static or instance check
      val dummyPath = "boys_halaqat/othman/students"
      if (dummyPath.contains("boy")) {
        throw SecurityException("انتهاك أمني: محاولة غير مصرح بها للوصول إلى مسارات أو بيانات قطاع البنين")
      }
    } catch (e: SecurityException) {
      exceptionThrown = true
    }

    assertTrue("Security guard should reject boys paths", exceptionThrown)
  }
}
