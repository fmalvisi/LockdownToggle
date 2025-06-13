package com.comalv.lockdowntoggle

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import io.mockk.mockk
import io.mockk.every


// A helper to replace the real DevicePolicyManager with a mock during tests
// This is a simplified example. In a real app, you might use a proper DI framework
// to make this cleaner.
object TestDevicePolicyManagerHelper {
    var mockDevicePolicyManager: DevicePolicyManager? = null

    fun getTestableDevicePolicyManager(context: Context): DevicePolicyManager {
        return mockDevicePolicyManager ?: context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }
}

// You would need to modify MainActivity to use this helper for testing,
// or use a proper Dependency Injection framework.
// For example, in MainActivity:
// policyManager = TestDevicePolicyManagerHelper.getTestableDevicePolicyManager(this)
// THIS IS A SIMPLIFICATION. Production code shouldn't ideally have test-specific helpers directly.
// A better approach is to inject dependencies.

@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {
/*
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var testContext: Context
    private lateinit var targetComponentName: ComponentName
    lateinit var mockAdminController: AdminController

    @Before
    fun setUp() {
        Intents.init()

        testContext = ApplicationProvider.getApplicationContext()
        targetComponentName = ComponentName(testContext, LockReceiver::class.java)

        // Create and inject a mock DevicePolicyManager
        mockAdminController = mockk<AdminController>(relaxed = true)
        MainActivity.adminControllerProvider = { mockAdminController }
    }

    @After
    fun tearDown() {
        Intents.release()
        MainActivity.adminControllerProvider = null
    }

    @Test
    fun sanityCheck_appLaunchesAndButtonVisible() {
        onView(withId(R.id.btn_lock_now)).check(matches(isDisplayed()))
    }

    // --- Test Scenarios ---

    @Test
    fun whenAdminNotActive_showsRequestAdminSection() {
        every { mockAdminController.isAdminActive() } returns false

        activityRule.scenario.recreate()

        activityRule.scenario.onActivity { activity ->
            // Simulate DPM returning false (requires MainActivity to use a mockable DPM)
            val realDpm = activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            if (realDpm.isAdminActive(targetComponentName)) {
                // To make this test reliable, you'd ideally clear admin rights before the test
                // or ensure MainActivity uses a mock that you control.
                println("WARNING: Device admin is active. Test assertion might be incorrect.")
            }
        }

        onView(withId(R.id.section_request_admin)).check(matches(isDisplayed()))
        onView(withId(R.id.section_revoke_admin)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.btn_request_admin)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_lock_now)).check(matches(isDisplayed())) // Lock button is always visible
    }

    @Test
    fun clickRequestAdminButton_sendsCorrectIntent() {
        // ARRANGE: (Ensure admin is not active - relies on default state or previous test cleanup)
        every { mockAdminController.isAdminActive() } returns false

        activityRule.scenario.recreate()
        // ACT
        onView(withId(R.id.btn_request_admin)).perform(click())

        // ASSERT: Verify the ACTION_ADD_DEVICE_ADMIN intent was sent
        Intents.intended(
            allOf(
                hasAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN),
                hasExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, targetComponentName),
                hasExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "To allow this app to lock the device down")
            )
        )

        // ASSERT: Verify the intent to request QS tile was sent
        Intents.intended(
            allOf(
                hasAction("android.service.quicksettings.action.REQUEST_QS_TILE"),
                hasComponent(ComponentName(testContext, LockdownTileService::class.java.name)) // Ensure LockdownTileService exists
            )
        )
    }

    @Test
    fun whenAdminActive_showsRevokeAdminSection() {
        every { mockAdminController.isAdminActive() } returns true

        activityRule.scenario.recreate()

        onView(withId(R.id.section_request_admin)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.section_revoke_admin)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_remove_admin)).check(matches(isDisplayed()))
    }

    @Test
    fun clickLockNowButton_whenAdminNotActive_showsToast() {
        every { mockAdminController.isAdminActive() } returns false

        activityRule.scenario.recreate()

        // ACT
        onView(withId(R.id.btn_lock_now)).perform(click())

        // ASSERT: Check for the Toast. This requires a custom Toast matcher or careful handling.
        // Espresso doesn't have great built-in Toast matchers that work reliably across all API levels
        // and without "dangerous permissions" for tests.
        // A common way is to check if the text appeared on screen shortly after the click.
        // This is a simplified check; robust Toast testing is more involved.
        onView(withText("Admin permission not granted"))
            .inRoot(ToastMatcher()) // You'll need to define ToastMatcher
            .check(matches(isDisplayed()))
    }

    // More tests would be needed for:
    // - clickLockNowButton_whenAdminActive_callsLockNow (requires mocking DPM)
    // - clickRemoveAdminButton_whenAdminActive_callsRemoveActiveAdmin (requires mocking DPM)
    // - clickRemoveAdminButton_whenAdminNotActive_showsToast

*/
}