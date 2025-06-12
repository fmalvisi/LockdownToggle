package com.comalv.lockdowntoggle

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class LockdownTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()

        // Update tile appearance based on admin status
        val policyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val component = ComponentName(this, LockReceiver::class.java)
        qsTile.state = if (policyManager.isAdminActive(component)) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_UNAVAILABLE
        }
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()

        val policyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val component = ComponentName(this, LockReceiver::class.java)

        if (policyManager.isAdminActive(component)) {
            policyManager.lockNow()
        } else {
            // Optionally show a toast or open the app
            startActivityAndCollapse(packageManager.getLaunchIntentForPackage(packageName))
        }
    }
}