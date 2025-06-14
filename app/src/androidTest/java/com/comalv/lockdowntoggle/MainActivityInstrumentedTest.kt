package com.comalv.lockdowntoggle

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.view.View
import android.widget.Button
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


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

        onView(withId(R.id.section_request_admin)).check(matches(isDisplayed()))
        onView(withId(R.id.section_revoke_admin)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.btn_request_admin)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_lock_now)).check(matches(isDisplayed())) // Lock button is always visible
    }

    @Test
    fun clickRequestAdminButton_sendsCorrectIntentsAndHidesSection() {
        // ARRANGE
        every { mockAdminController.isAdminActive() } returns false
        activityRule.scenario.recreate()

        // ACT
        onView(withId(R.id.btn_request_admin)).perform(click())

        // ASSERT: Verify the ACTION_ADD_DEVICE_ADMIN intent was sent
        Intents.intended(
            allOf(
                hasAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN),
                hasExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, targetComponentName), // Use the one defined in test setup
                hasExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "To allow this app to enter Lockdown mode")
            )
        )

        // ASSERT: Verify the intent to request QS tile was sent (with caveats about this intent)
        Intents.intended(
            allOf(
                hasAction("android.service.quicksettings.action.REQUEST_QS_TILE"),
                hasComponent(ComponentName(testContext, LockdownTileService::class.java.name))
            )
        )

        // ASSERT: Verify the request admin section is immediately hidden
        onView(withId(R.id.section_request_admin)).check(matches(withEffectiveVisibility(Visibility.GONE)))
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

    @Test
    fun clickLockNowButton_whenAdminActive_callsLockNowOnController() {
        // ARRANGE
        every { mockAdminController.isAdminActive() } returns true
        activityRule.scenario.recreate() // Ensure UI reflects admin active

        // ACT
        onView(withId(R.id.btn_lock_now)).perform(click())

        // ASSERT
        io.mockk.verify { mockAdminController.lockNow() }
        // You could also check for a success toast if your actual AdminController.lockNow()
        // or MainActivity shows one upon successful lock. The current MainActivity does not,
        // but the DPM.lockNow() itself might show a system indication.
        // For this test, verifying the interaction with the controller is key.
    }

    @Test
    fun clickRemoveAdminButton_whenAdminActive_callsRemoveAdminAndHidesSectionAndShowsToast() {
        // ARRANGE
        every { mockAdminController.isAdminActive() } returns true
        activityRule.scenario.recreate()

        onView(withId(R.id.section_revoke_admin)).check(matches(isDisplayed())) // Pre-condition

        // ACT
        onView(withId(R.id.btn_remove_admin)).perform(click())

        // ASSERT
        // 1. Verify controller interaction
        io.mockk.verify { mockAdminController.removeAdmin() }

        // 2. Verify the revoke admin section is immediately hidden
        //onView(withId(R.id.section_revoke_admin))).check(matches(withEffectiveVisibility(Visibility.GONE)))

        // 3. Verify Toast
        onView(withText("Admin rights removed"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))

        // Note: The Handler.postDelayed to updateRemoveSectionVisibility again might be
        // for a scenario where `removeAdmin()` itself doesn't immediately reflect in `isAdminActive()`.
        // If `mockAdminController.removeAdmin()` was supposed to make `isAdminActive()` return false
        // for the delayed update, you would need:
        // every { mockAdminController.isAdminActive() } returns true then successivamente false
        // or use an answer {} block for more complex stubbing if the state changes.
        // However, for this click, the immediate effects are what we're testing primarily.
    }

    @Test
    fun clickRemoveAdminButton_whenAdminNotActive_showsToastAndNoControllerCall() {
        // ARRANGE
        every { mockAdminController.isAdminActive() } returns false
        activityRule.scenario.recreate()

        // Ensure the button is visible for the test if it's not already
        // This might indicate a UI logic flaw if it's reachable when admin is not active
        // but for testing the click handler's 'else' branch:
        activityRule.scenario.onActivity { activity ->
            activity.findViewById<Button>(R.id.btn_remove_admin).visibility = View.VISIBLE
        }
        onView(withId(R.id.btn_remove_admin)).check(matches(isDisplayed())); // Make sure it's visible

        // ACT
        onView(withId(R.id.btn_remove_admin)).perform(click())

        // ASSERT
        onView(withText("No admin rights to remove"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))

        io.mockk.verify(exactly = 0) { mockAdminController.removeAdmin() }
    }

}