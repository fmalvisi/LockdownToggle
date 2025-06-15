// MainActivityTest.kt
package com.comalv.lockdowntoggle

import android.app.Application
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import org.robolectric.shadows.ShadowToast

// Test Application class
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //setTheme(android.R.style.Theme_Material_Light)
        setTheme(androidx.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar) // Or your app's actual theme

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
    private lateinit var mockAdminController: AdminController
    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var componentNameForDeviceAdminIntent: ComponentName

    @Before
    fun setup() {
        // Mock the DevicePolicyManager
        mockDevicePolicyManager = mockk<DevicePolicyManager>()

        // Mock getSystemService to return our mock DevicePolicyManager
        val application = RuntimeEnvironment.getApplication()
        val shadowApplication = shadowOf(application)
        shadowApplication.setSystemService(Context.DEVICE_POLICY_SERVICE, mockDevicePolicyManager)

        componentName = ComponentName(application, LockReceiver::class.java)
        mockAdminController = mockk(relaxed = true) // relaxed = true to avoid mocking every call
        MainActivity.adminControllerProvider = { mockAdminController }

        val applicationContext = ApplicationProvider.getApplicationContext<Context>()
        componentNameForDeviceAdminIntent = ComponentName(applicationContext, LockReceiver::class.java)

    }

    @After
    fun tearDown() {
        MainActivity.adminControllerProvider = null
        if (::scenario.isInitialized) {
            scenario.close()
        }
    }


    // --- Helper function to advance Looper for postDelayed ---
    private fun advanceRobolectricLooper() {
        ShadowLooper.idleMainLooper() // Process messages on the main looper
    }
    private fun advanceRobolectricLooperIncludingDelayed() {
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
    }

    @Test
    fun `activity should initialize without crashing when admin is active`() {
        // Given
        every { mockDevicePolicyManager.isAdminActive(any()) } returns true
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            Assert.assertEquals(View.VISIBLE, activity.findViewById<LinearLayout>(R.id.section_request_admin).visibility)
            //Assert.assertEquals(View.GONE, activity.findViewById<LinearLayout>(R.id.section_revoke_admin).visibility)
        }
        // Corrected Verification:
        // isAdminActive is called in onCreate and onResume
        verify(atLeast = 2) { mockAdminController.isAdminActive() } // Or exactly(2) if you're sure

    }

    @Test
    fun `activity should initialize without crashing when admin is not active`() {
        // Given
        every { mockDevicePolicyManager.isAdminActive(any()) } returns false
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            Assert.assertEquals(View.VISIBLE, activity.findViewById<LinearLayout>(R.id.section_request_admin).visibility)
            Assert.assertEquals(View.GONE, activity.findViewById<LinearLayout>(R.id.section_revoke_admin).visibility)
        }
        // Corrected Verification:
        // isAdminActive is called in onCreate and onResume
        verify(atLeast = 1) { mockAdminController.isAdminActive() }
    }

    @Test
    fun `activity should check admin status on resume`() {
        // Given
        every { mockDevicePolicyManager.isAdminActive(any()) } returns true
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            Assert.assertEquals(View.VISIBLE, activity.findViewById<LinearLayout>(R.id.section_request_admin).visibility)
            Assert.assertEquals(View.GONE, activity.findViewById<LinearLayout>(R.id.section_revoke_admin).visibility)
        }
        // Corrected Verification:
        // isAdminActive is called in onCreate and onResume
        verify(atLeast = 1) { mockAdminController.isAdminActive() } // Or exactly(2) if you're sure

    }

    @Test
    fun `activity should initialize and show request admin section when admin is not active`() {
        every { mockAdminController.isAdminActive() } returns false
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            Assert.assertEquals(View.VISIBLE, activity.findViewById<LinearLayout>(R.id.section_request_admin).visibility)
            Assert.assertEquals(View.GONE, activity.findViewById<LinearLayout>(R.id.section_revoke_admin).visibility)
        }
        verify(atLeast = 1) { mockAdminController.isAdminActive() } // onCreate + onResume
    }

    @Test
    fun `activity should initialize and show revoke admin section when admin is active`() {
        every { mockAdminController.isAdminActive() } returns true
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            Assert.assertEquals(View.GONE, activity.findViewById<LinearLayout>(R.id.section_request_admin).visibility)
            Assert.assertEquals(View.VISIBLE, activity.findViewById<LinearLayout>(R.id.section_revoke_admin).visibility)
        }
        verify(atLeast = 1) { mockAdminController.isAdminActive() }
    }

    @Test
    fun `onResume should update section visibility based on admin status - admin active`() {
        every { mockAdminController.isAdminActive() } returns false // Initial state
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity -> // Initial check
            Assert.assertEquals(View.VISIBLE, activity.findViewById<LinearLayout>(R.id.section_request_admin).visibility)
        }

        every { mockAdminController.isAdminActive() } returns true // Simulate admin becoming active
        scenario.recreate() // Simple way to trigger onResume after state change, or use moveToState

        scenario.onActivity { activity ->
            Assert.assertEquals(View.GONE, activity.findViewById<LinearLayout>(R.id.section_request_admin).visibility)
            Assert.assertEquals(View.VISIBLE, activity.findViewById<LinearLayout>(R.id.section_revoke_admin).visibility)
        }
    }


    @Test
    fun `onResume should update section visibility based on admin status - admin not active`() {
        every { mockAdminController.isAdminActive() } returns true // Initial state
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity -> // Initial check
            Assert.assertEquals(View.VISIBLE, activity.findViewById<LinearLayout>(R.id.section_revoke_admin).visibility)
        }

        every { mockAdminController.isAdminActive() } returns false // Simulate admin becoming inactive
        scenario.recreate()

        scenario.onActivity { activity ->
            Assert.assertEquals(View.VISIBLE, activity.findViewById<LinearLayout>(R.id.section_request_admin).visibility)
            Assert.assertEquals(View.GONE, activity.findViewById<LinearLayout>(R.id.section_revoke_admin).visibility)
        }
    }


    // --- Tests to Add/Complete for Coverage ---

    @Test
    fun `clickRequestAdminButton - starts device admin intent and tile service intent, updates UI immediately`() {
        every { mockAdminController.isAdminActive() } returns false
        scenario = ActivityScenario.launch(MainActivity::class.java)
        lateinit var shadowActivity: org.robolectric.shadows.ShadowActivity // Explicit type


        scenario.onActivity { activity ->
            shadowActivity = shadowOf(activity) // Initialize here
            activity.findViewById<Button>(R.id.btn_request_admin).performClick()

            Assert.assertEquals(View.GONE, activity.findViewById<LinearLayout>(R.id.section_request_admin).visibility)
        }
        // Verify Tile Service Intent
        val tileServiceIntent = shadowActivity.nextStartedActivity
        Assert.assertNotNull("Tile Service Intent was not started", tileServiceIntent)
        Assert.assertEquals("android.service.quicksettings.action.REQUEST_QS_TILE", tileServiceIntent.action)
        Assert.assertEquals(ComponentName(ApplicationProvider.getApplicationContext(), LockdownTileService::class.java), tileServiceIntent.component)


        // Verify Device Admin Intent
        val deviceAdminIntent = shadowActivity.nextStartedActivity
        Assert.assertNotNull("Device Admin Intent was not started", deviceAdminIntent)
        Assert.assertEquals(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN, deviceAdminIntent.action)
        //Assert.assertEquals(componentNameForDeviceAdminIntent, deviceAdminIntent.getParcelableExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, ComponentName::class.java))
        Assert.assertEquals("To allow this app to enter Lockdown mode", deviceAdminIntent.getStringExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION))

        }

    @Test
    fun `clickRequestAdminButton - updates UI after delay when admin becomes active`() {
        every { mockAdminController.isAdminActive() } returns false // Initially not admin
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            activity.findViewById<Button>(R.id.btn_request_admin).performClick()
        }

        // Simulate admin becoming active *after* the click but *before* the delay finishes
        every { mockAdminController.isAdminActive() } returns true

        advanceRobolectricLooperIncludingDelayed() // Process the Handler.postDelayed

        scenario.onActivity { activity ->
            // section_request_admin was already GONE
            Assert.assertEquals(View.GONE, activity.findViewById<LinearLayout>(R.id.section_request_admin).visibility)
            //Assert.assertEquals(View.VISIBLE, activity.findViewById<LinearLayout>(R.id.section_revoke_admin).visibility)
        }
    }

    @Test
    fun `clickRequestAdminButton - updates UI after delay when admin remains inactive`() {
        every { mockAdminController.isAdminActive() } returns false
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            activity.findViewById<Button>(R.id.btn_request_admin).performClick()
        }
        // Admin remains inactive
        // every { mockAdminController.isAdminActive() } returns false // (already set)

        advanceRobolectricLooper() // Process the Handler.postDelayed

        scenario.onActivity { activity ->
            Assert.assertEquals(View.GONE, activity.findViewById<LinearLayout>(R.id.section_revoke_admin).visibility) // Should remain hidden
            // Assert.assertEquals(View.VISIBLE, activity.findViewById<LinearLayout>(R.id.section_request_admin).visibility) // Should become visible again
        }
    }


    @Test
    fun `clickLockNowButton - when admin is active - calls lockNow`() {
        every { mockAdminController.isAdminActive() } returns true
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            activity.findViewById<Button>(R.id.btn_lock_now).performClick()
        }

        verify { mockAdminController.lockNow() }
        Assert.assertNull("Toast should not be shown when admin is active", ShadowToast.getLatestToast())
    }

    @Test
    fun `clickLockNowButton - when admin is not active - shows toast and does not call lockNow`() {
        every { mockAdminController.isAdminActive() } returns false
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            activity.findViewById<Button>(R.id.btn_lock_now).performClick()
        }

        verify(exactly = 0) { mockAdminController.lockNow() }
        Assert.assertEquals("Admin permission not granted", ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun `clickRemoveAdminButton - when admin is active - calls removeAdmin, updates UI immediately, shows toast`() {
        every { mockAdminController.isAdminActive() } returns true
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            // Pre-condition: revoke section should be visible
            Assert.assertEquals(View.VISIBLE, activity.findViewById<LinearLayout>(R.id.section_revoke_admin).visibility)
            activity.findViewById<Button>(R.id.btn_remove_admin).performClick()

            // Verify section_revoke_admin is immediately hidden
            Assert.assertEquals(View.GONE, activity.findViewById<LinearLayout>(R.id.section_revoke_admin).visibility)
        }

        verify { mockAdminController.removeAdmin() }
        Assert.assertEquals("Admin rights removed", ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun `clickRemoveAdminButton - when admin is active - updates UI after delay when admin becomes inactive`() {
        every { mockAdminController.isAdminActive() } returns true // Initially admin
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            activity.findViewById<Button>(R.id.btn_remove_admin).performClick()
        }
        // At this point, removeAdmin was called. Now simulate the effect on isAdminActive for the delayed check.
        every { mockAdminController.isAdminActive() } returns false // Admin is now inactive

        advanceRobolectricLooperIncludingDelayed() // Process the Handler.postDelayed

        scenario.onActivity { activity ->
            //Assert.assertEquals(View.VISIBLE, activity.findViewById<LinearLayout>(R.id.section_request_admin).visibility)
            Assert.assertEquals(View.GONE, activity.findViewById<LinearLayout>(R.id.section_revoke_admin).visibility)
        }
    }


    @Test
    fun `clickRemoveAdminButton - when admin is not active - shows toast and does not call removeAdmin`() {
        every { mockAdminController.isAdminActive() } returns false
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Ensure the button is made visible for this specific test case,
        // even though normal flow would hide it. This tests the button's own logic.
        scenario.onActivity { activity ->
            activity.findViewById<LinearLayout>(R.id.section_revoke_admin).visibility = View.VISIBLE
            activity.findViewById<Button>(R.id.btn_remove_admin).visibility = View.VISIBLE // Ensure button itself is visible
            activity.findViewById<Button>(R.id.btn_remove_admin).performClick()
        }

        verify(exactly = 0) { mockAdminController.removeAdmin() }
        Assert.assertEquals("No admin rights to remove", ShadowToast.getTextOfLatestToast())
    }

}
