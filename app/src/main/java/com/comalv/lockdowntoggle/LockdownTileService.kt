package com.comalv.lockdowntoggle

import android.annotation.SuppressLint
import android.app.PendingIntent
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

    @SuppressLint("StartActivityAndCollapseDeprecated")
    override fun onClick() {
        super.onClick()

        val policyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val component = ComponentName(this, LockReceiver::class.java)

        if (policyManager.isAdminActive(component)) {
            policyManager.lockNow()
        } else {
            // Optionally show a toast or open the app
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.TIRAMISU) {
                    // Android 13+ (API 33): Use PendingIntent
                    val pendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        launchIntent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    startActivityAndCollapse(pendingIntent)
                } else {
                    // Android 12 and below: Use direct Intent
                    @Suppress("DEPRECATION")
                    startActivityAndCollapse(launchIntent)
                }
            }
        }
    }
}