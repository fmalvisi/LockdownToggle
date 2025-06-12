// QuickLockdownActivityTest.kt
package com.comalv.lockdowntoggle

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class QuickLockdownActivityTest {

    private lateinit var activity: QuickLockdownActivity
    private lateinit var mockDevicePolicyManager: DevicePolicyManager
    private lateinit var componentName: ComponentName

    @Before
    fun setup() {
        // Mock the DevicePolicyManager
        mockDevicePolicyManager = mockk<DevicePolicyManager>()

        // Mock getSystemService to return our mock DevicePolicyManager
        val application = RuntimeEnvironment.getApplication()
        val shadowApplication = shadowOf(application)
        shadowApplication.setSystemService(Context.DEVICE_POLICY_SERVICE, mockDevicePolicyManager)

        componentName = ComponentName(application, LockReceiver::class.java)
    }

    @Test
    fun `onCreate should lock device when admin is active and finish activity`() {
        // Given
        every { mockDevicePolicyManager.isAdminActive(any()) } returns true
        every { mockDevicePolicyManager.lockNow() } just Runs

        // When
        val activityController = Robolectric.buildActivity(QuickLockdownActivity::class.java)
        activity = activityController.create().get()

        // Then
        verify { mockDevicePolicyManager.lockNow() }
        assert(activity.isFinishing)
    }

    @Test
    fun `onCreate should not lock device when admin is not active but still finish activity`() {
        // Given
        every { mockDevicePolicyManager.isAdminActive(any()) } returns false

        // When
        val activityController = Robolectric.buildActivity(QuickLockdownActivity::class.java)
        activity = activityController.create().get()

        // Then
        verify(exactly = 0) { mockDevicePolicyManager.lockNow() }
        assert(activity.isFinishing)
    }

    @Test
    fun `onCreate should use correct component name for admin check`() {
        // Given
        every { mockDevicePolicyManager.isAdminActive(any()) } returns false

        // When
        val activityController = Robolectric.buildActivity(QuickLockdownActivity::class.java)
        activity = activityController.create().get()

        // Then
        verify { mockDevicePolicyManager.isAdminActive(match {
            it.className == LockReceiver::class.java.name
        }) }
    }

    @Test
    fun `activity should finish immediately after onCreate`() {
        // Given
        every { mockDevicePolicyManager.isAdminActive(any()) } returns true
        every { mockDevicePolicyManager.lockNow() } just Runs

        // When
        val activityController = Robolectric.buildActivity(QuickLockdownActivity::class.java)
        activity = activityController.create().get()

        // Then
        assert(activity.isFinishing)
    }
}