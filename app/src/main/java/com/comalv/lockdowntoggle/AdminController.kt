package com.comalv.lockdowntoggle

interface AdminController {
    fun isAdminActive(): Boolean
    fun lockNow()
    fun removeAdmin()
}