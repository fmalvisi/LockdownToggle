package com.comalv.lockdowntoggle

import android.content.Context
import android.content.Intent
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowToast
import org.junit.Assert.*
import org.robolectric.annotation.Config

// If your app has a custom Application class and you need it for context,
// you might specify it in @Config. Otherwise, Robolectric's default is fine.
// @Config(application = TestApplication::class) // Example
@RunWith(RobolectricTestRunner::class) // RobolectricTestRunner is good for framework access
@Config(sdk = [28])
class LockReceiverTest {

    private lateinit var lockReceiver: LockReceiver
    private lateinit var mockContext: Context
    private lateinit var mockIntent: Intent // Though intent isn't used by the methods, it's good practice to pass one

    @Before
    fun setUp() {
        lockReceiver = LockReceiver()
        mockContext = mockk(relaxed = true) // Use a relaxed mock for context
        mockIntent = mockk() // A simple mock for Intent is fine
    }

    @Test
    fun `onEnabled - shows 'Device Admin Enabled' toast`() {
        // Arrange (Context and Intent are already mocked in setUp)

        // Act: Call the method under test
        lockReceiver.onEnabled(mockContext, mockIntent)

        // Assert: Verify that the correct toast was shown
        val latestToastText = ShadowToast.getTextOfLatestToast()
        assertEquals("Device Admin Enabled", latestToastText)

        // You can also check the duration if it were different, but LENGTH_SHORT is default for ShadowToast.
        // ShadowToast.getLatestToast() would return the Toast object itself for more detailed checks.
        assertNotNull("Toast should have been shown", ShadowToast.getLatestToast())
    }

    @Test
    fun `onDisabled - shows 'Device Admin Disabled' toast`() {
        // Arrange

        // Act
        lockReceiver.onDisabled(mockContext, mockIntent)

        // Assert
        val latestToastText = ShadowToast.getTextOfLatestToast()
        assertEquals("Device Admin Disabled", latestToastText)
        assertNotNull("Toast should have been shown", ShadowToast.getLatestToast())
    }

    @Test
    fun `onEnabled - no toast shown before calling onEnabled`() {
        // This test ensures that toasts aren't lingering from other tests or static initializers.
        // It also implicitly tests that ShadowToast.getTextOfLatestToast() returns null if no toast.
        assertNull("No toast should be present before onEnabled is called", ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun `onDisabled - no toast shown before calling onDisabled`() {
        assertNull("No toast should be present before onDisabled is called", ShadowToast.getTextOfLatestToast())
    }
}