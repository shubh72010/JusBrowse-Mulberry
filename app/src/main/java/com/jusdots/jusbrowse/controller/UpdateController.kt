package com.jusdots.jusbrowse.controller

import com.jusdots.jusbrowse.utils.UpdateInfo

interface UpdateController {

    sealed interface CheckResult {
        data object Idle : CheckResult
        data object Checking : CheckResult
        data class Available(val info: UpdateInfo) : CheckResult
        data object UpToDate : CheckResult
        data object Failed : CheckResult
    }

    suspend fun checkForUpdates(force: Boolean = false): CheckResult
    fun cancel()
}
