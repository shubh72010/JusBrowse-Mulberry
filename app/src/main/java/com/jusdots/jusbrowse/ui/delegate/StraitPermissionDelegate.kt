package com.jusdots.jusbrowse.ui.delegate

import android.net.Uri
import com.jusdots.jusbrowse.data.database.SiteSettingsDao
import com.jusdots.jusbrowse.data.models.SiteSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSession.PermissionDelegate
import org.mozilla.geckoview.GeckoSession.PermissionDelegate.ContentPermission

data class PendingContentPermission(
    val session: GeckoSession,
    val permission: ContentPermission,
    val result: GeckoResult<Int>
)

class StraitPermissionDelegate(
    private val siteSettingsDao: SiteSettingsDao
) : PermissionDelegate {

    // IO-scoped coroutine scope — must NOT use runBlocking on GeckoView callbacks
    // (delivered on main thread), which is a guaranteed ANR on low-end 3GB devices.
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Volatile
    var pendingContentPermission: PendingContentPermission? = null

    override fun onContentPermissionRequest(
        session: GeckoSession,
        perm: ContentPermission
    ): GeckoResult<Int>? {
        val origin = extractOrigin(perm.uri)
            ?: return GeckoResult.fromValue(ContentPermission.VALUE_DENY)

        val result = GeckoResult<Int>()

        scope.launch {
            val stored = siteSettingsDao.getSettingsForOrigin(origin).first()
            if (stored != null) {
                val granted = when (perm.permission) {
                    PermissionDelegate.PERMISSION_GEOLOCATION -> stored.geolocationAllowed
                    else -> null
                }
                if (granted != null) {
                    result.complete(
                        if (granted) ContentPermission.VALUE_ALLOW else ContentPermission.VALUE_DENY
                    )
                    return@launch
                }
            }
            // No stored preference — hand off to UI for user prompt
            withContext(Dispatchers.Main) {
                pendingContentPermission = PendingContentPermission(session, perm, result)
            }
        }

        return result
    }

    override fun onMediaPermissionRequest(
        session: GeckoSession,
        uri: String,
        video: Array<PermissionDelegate.MediaSource>?,
        audio: Array<PermissionDelegate.MediaSource>?,
        callback: PermissionDelegate.MediaCallback
    ) {
        val origin = extractOrigin(uri) ?: run {
            callback.reject()
            return
        }

        scope.launch {
            val stored = siteSettingsDao.getSettingsForOrigin(origin).first()
            val cameraOk = stored?.cameraAllowed == true
            val micOk = stored?.microphoneAllowed == true

            val videoSource = if (cameraOk) video?.firstOrNull() else null
            val audioSource = if (micOk) audio?.firstOrNull() else null

            withContext(Dispatchers.Main) {
                if (videoSource != null || audioSource != null) {
                    callback.grant(videoSource, audioSource)
                } else {
                    callback.reject()
                }
            }
        }
    }

    override fun onAndroidPermissionsRequest(
        session: GeckoSession,
        permissions: Array<out String>?,
        callback: PermissionDelegate.Callback
    ) {
        // Android runtime permissions require an explicit, origin-bound UI flow.
        callback.reject()
    }

    fun resolveContentPermission(granted: Boolean) {
        val pending = pendingContentPermission ?: return
        pendingContentPermission = null

        val origin = extractOrigin(pending.permission.uri)
        if (origin != null) {
            scope.launch {
                val stored = siteSettingsDao.getSettingsForOrigin(origin).first()
                val updated = (stored ?: SiteSettings(origin = origin)).copy(
                    geolocationAllowed = if (pending.permission.permission == PermissionDelegate.PERMISSION_GEOLOCATION)
                        granted else stored?.geolocationAllowed ?: false
                )
                siteSettingsDao.updateSettings(updated)
            }
        }

        pending.result.complete(
            if (granted) ContentPermission.VALUE_ALLOW else ContentPermission.VALUE_DENY
        )
    }

    private fun extractOrigin(uri: String?): String? {
        if (uri == null) return null
        return try {
            val u = Uri.parse(uri)
            "${u.scheme}://${u.host}"
        } catch (_: Exception) { null }
    }
}
