// MainActivityTest.kt
package com.comalv.lockdowntoggle

import android.app.Application
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

// Test Application class
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setTheme(android.R.style.Theme_Material_Light)
    }
}

@RunWith(AndroidJUnit4::class)
@Config(
    sdk = [28],
    application = TestApplication::class
)
class MainActivityTest {

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
    fun `activity should initialize without crashing when admin is active`() {
        // Given
        every { mockDevicePolicyManager.isAdminActive(any()) } returns true

        // When
        val activityController = Robolectric.buildActivity(MainActivity::class.java)
        val activity = activityController.create().get()

        // Then
        assert(!activity.isFinishing)
        verify { mockDevicePolicyManager.isAdminActive(any()) }
    }

    @Test
    fun `activity should initialize without crashing when admin is not active`() {
        // Given
        every { mockDevicePolicyManager.isAdminActive(any()) } returns false

        // When
        val activityController = Robolectric.buildActivity(MainActivity::class.java)
        val activity = activityController.create().get()

        // Then
        assert(!activity.isFinishing)
        verify { mockDevicePolicyManager.isAdminActive(any()) }
    }

    @Test
    fun `activity should check admin status on resume`() {
        // Given
        every { mockDevicePolicyManager.isAdminActive(any()) } returns true

        // When
        val activityController = Robolectric.buildActivity(MainActivity::class.java)
        activityController.create().resume()

        // Then - verify admin status is checked (called in onCreate and onResume)
        verify(atLeast = 2) { mockDevicePolicyManager.isAdminActive(any()) }
    }

    @Test
    fun `should use correct component name for admin checks`() {
        // Given
        every { mockDevicePolicyManager.isAdminActive(any()) } returns false

        // When
        val activityController = Robolectric.buildActivity(MainActivity::class.java)
        activityController.create()

        // Then
        verify { mockDevicePolicyManager.isAdminActive(match {
            it.className == LockReceiver::class.java.name
        }) }
    }
}
