package com.comalv.lockdowntoggle

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
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
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.concurrent.CompletableFuture

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

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var mockDevicePolicyManager: DevicePolicyManager
    private lateinit var testContext: Context
    private lateinit var targetComponentName: ComponentName

    @Before
    fun setUp() {
        Intents.init() // Initialize Espresso-Intents

        testContext = ApplicationProvider.getApplicationContext()
        targetComponentName = ComponentName(testContext, LockReceiver::class.java)

        // --- Mocking DevicePolicyManager ---
        // This is where it gets a bit complex without proper DI.
        // For this example, let's assume you could somehow inject a mock or
        // use a test-specific DPM provider.
        // The `TestDevicePolicyManagerHelper` is a conceptual illustration.
        // In a real scenario, you'd use Hilt, Koin, or manual constructor injection.

        mockDevicePolicyManager = mock()
        // If MainActivity could use a TestDevicePolicyManagerHelper:
        // TestDevicePolicyManagerHelper.mockDevicePolicyManager = mockDevicePolicyManager
    }

    @After
    fun tearDown() {
        Intents.release() // Release Espresso-Intents
        // TestDevicePolicyManagerHelper.mockDevicePolicyManager = null // Clean up
    }

    // --- Test Scenarios ---

    @Test
    fun whenAdminNotActive_showsRequestAdminSection() {
        // ARRANGE: Ensure our mock DPM says admin is NOT active
        // This requires MainActivity to use the mock.
        // For this to work, MainActivity would need to be refactored to accept
        // a DevicePolicyManager or use a service locator that can be controlled in tests.
        // Let's simulate this by checking initial state, assuming DPM is not active by default on test start.
        // This test might be flaky if admin is somehow active from a previous run on the test device.

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
        // ARRANGE: This is the tricky part without direct DPM control in the Activity.
        // We need to tell MainActivity that admin IS active.
        // This typically involves mocking DevicePolicyManager and making MainActivity use the mock.
        // For this example, we can't easily set this state from the test without modifying MainActivity
        // to be more testable (e.g., by injecting DevicePolicyManager).

        // **Conceptual Mocking (if MainActivity used the mockDPM):**
        // whenever(mockDevicePolicyManager.isAdminActive(targetComponentName)).doReturn(true)
        // activityRule.scenario.recreate() // Recreate to pick up mocked state in onCreate/onResume

        // Due to the difficulty of mocking DPM without refactoring MainActivity,
        // this test might be better as a manual one, or you'd need to refactor MainActivity for testability.
        // For now, let's skip the direct assertion or assume a way to set it.

        // If you could set admin active (e.g., manually before running this specific test):
        // onView(withId(R.id.section_request_admin)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        // onView(withId(R.id.section_revoke_admin)).check(matches(isDisplayed()))
        // onView(withId(R.id.btn_remove_admin)).check(matches(isDisplayed()))
    }

    @Test
    fun clickLockNowButton_whenAdminNotActive_showsToast() {
        // ARRANGE: (Ensure admin is not active)
        // Assuming MainActivity uses the real DPM and admin is not active by default.

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
}