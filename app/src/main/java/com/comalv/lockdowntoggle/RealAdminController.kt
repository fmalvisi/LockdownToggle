package com.comalv.lockdowntoggle

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context

class RealAdminController(
    private val context: Context,
    private val componentName: ComponentName
) : AdminController {

    private val dpm =
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    override fun isAdminActive(): Boolean = dpm.isAdminActive(componentName)

    override fun lockNow() {
        dpm.lockNow()
    }

    override fun removeAdmin() {
        dpm.removeActiveAdmin(componentName)
    }
}