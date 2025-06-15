package com.comalv.lockdowntoggle

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class RealAdminControllerTest {

    // Mocks
    private lateinit var mockContext: Context
    private lateinit var mockDevicePolicyManager: DevicePolicyManager
    private lateinit var mockComponentName: ComponentName

    // Class Under Test
    private lateinit var realAdminController: RealAdminController

    @Before
    fun setUp() {
        // Create mocks for dependencies
        mockContext = mockk()
        mockDevicePolicyManager = mockk(relaxed = true) // relaxed = true to avoid mocking every DPM call if not relevant
        mockComponentName = mockk() // ComponentName can often be a simple mock if its methods aren't used

        // Stubbing Context.getSystemService to return our mock DevicePolicyManager
        every { mockContext.getSystemService(Context.DEVICE_POLICY_SERVICE) } returns mockDevicePolicyManager

        // Create the instance of the class under test
        realAdminController = RealAdminController(mockContext, mockComponentName)
    }

    @After
    fun tearDown() {
// Optional: unmock any static mocks or clear all mocks if needed,
//        // but for instance mocks, MockK usually handles this well.
//        // clearAllMocks() // If you have global/static mocks affecting other tests
    }

    @Test
    fun `isAdminActive - when DPM says admin is active - returns true`() {
        // Arrange: Configure the mock DPM
        every { mockDevicePolicyManager.isAdminActive(mockComponentName) } returns true

        // Act: Call the method under test
        val isActive = realAdminController.isAdminActive()

        // Assert: Verify the result and DPM interaction
        assertTrue("isAdminActive should return true when DPM says so", isActive)
        verify(exactly = 1) { mockDevicePolicyManager.isAdminActive(mockComponentName) }
    }

    @Test
    fun `isAdminActive - when DPM says admin is not active - returns false`() {
        // Arrange
        every { mockDevicePolicyManager.isAdminActive(mockComponentName) } returns false

        // Act
        val isActive = realAdminController.isAdminActive()

        // Assert
        assertFalse("isAdminActive should return false when DPM says so", isActive)
        verify(exactly = 1) { mockDevicePolicyManager.isAdminActive(mockComponentName) }
    }

    @Test
    fun `lockNow - calls DPM lockNow`() {
        // Arrange (nothing specific to arrange for DPM.lockNow() itself unless it returns a value)
        // DPM.lockNow() is void, so we just need to verify it's called.

        // Act
        realAdminController.lockNow()

        // Assert
        verify(exactly = 1) { mockDevicePolicyManager.lockNow() }
    }

    @Test
    fun `removeAdmin - calls DPM removeActiveAdmin with correct ComponentName`() {
        // Arrange
        // DPM.removeActiveAdmin() is void.

        // Act
        realAdminController.removeAdmin()

        // Assert
        verify(exactly = 1) { mockDevicePolicyManager.removeActiveAdmin(mockComponentName) }
    }

    @Test
    fun `constructor - requests DEVICE_POLICY_SERVICE from context`() {
        // This test specifically verifies that the constructor correctly called getSystemService.
        // The setUp() method already creates an instance, triggering the constructor.

        // Assert
        verify(exactly = 1) { mockContext.getSystemService(Context.DEVICE_POLICY_SERVICE) }
        // We can also assert that the returned DPM is the one we mocked,
        // though this is implicitly covered by other tests.
    }
}