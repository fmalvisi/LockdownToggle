package com.comalv.lockdowntoggle

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var policyManager: DevicePolicyManager
    private lateinit var componentName: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        policyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(this, LockReceiver::class.java)

        val requestAdminButton = findViewById<Button>(R.id.btn_request_admin)
        val lockNowButton = findViewById<Button>(R.id.btn_lock_now)
        val removeAdminButton = findViewById<Button>(R.id.btn_remove_admin)
        updateRemoveSectionVisibility()
        requestAdminButton.setOnClickListener {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "To allow this app to lock the device down")
            startActivity(intent)
        }

        lockNowButton.setOnClickListener {
            if (policyManager.isAdminActive(componentName)) {
                policyManager.lockNow()
            } else {
                Toast.makeText(this, "Admin permission not granted", Toast.LENGTH_SHORT).show()
            }
        }

        removeAdminButton.setOnClickListener {
            if (policyManager.isAdminActive(componentName)) {
                policyManager.removeActiveAdmin(componentName)
                Toast.makeText(this, "Admin rights removed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No admin rights to remove", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateRemoveSectionVisibility()
    }

    private fun updateRemoveSectionVisibility() {
        val requestAdminSection = findViewById<LinearLayout>(R.id.section_request_admin)
        val removeAdminSection = findViewById<LinearLayout>(R.id.section_revoke_admin)
        if (policyManager.isAdminActive(componentName)) {
            removeAdminSection.visibility = View.VISIBLE
            requestAdminSection.visibility = View.GONE
        } else {
            removeAdminSection.visibility = View.GONE
            requestAdminSection.visibility = View.VISIBLE
        }
    }
}
