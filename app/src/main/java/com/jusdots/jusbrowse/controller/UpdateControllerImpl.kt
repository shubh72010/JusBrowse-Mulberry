package com.jusdots.jusbrowse.controller

import com.jusdots.jusbrowse.BuildConfig
import com.jusdots.jusbrowse.controller.UpdateController.CheckResult
import com.jusdots.jusbrowse.utils.UpdateChecker
import com.jusdots.jusbrowse.utils.UpdateInfo

class UpdateControllerImpl : UpdateController {

    override suspend fun checkForUpdates(force: Boolean): CheckResult {
        return try {
            val info = UpdateChecker.check(BuildConfig.VERSION_NAME)
            when {
                info == null -> CheckResult.Failed
                info.isNewer -> CheckResult.Available(info)
                else -> CheckResult.UpToDate
            }
        } catch (_: Exception) {
            CheckResult.Failed
        }
    }

    override fun cancel() {
        // No-op: UpdateChecker uses synchronous OkHttp calls
    }
}