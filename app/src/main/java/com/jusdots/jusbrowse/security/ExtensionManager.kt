package com.jusdots.jusbrowse.security

import android.util.Log
import com.jusdots.jusbrowse.BrowserApplication
import com.jusdots.jusbrowse.data.database.ExtensionDao
import com.jusdots.jusbrowse.data.models.ExtensionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import org.mozilla.geckoview.WebExtension
import org.mozilla.geckoview.WebExtensionController

class ExtensionManager(private val extensionDao: ExtensionDao) {

    companion object {
        private const val TAG = "ExtensionManager"
    }

    val allExtensions: Flow<List<ExtensionEntity>> = extensionDao.getAllExtensions()

    private val controller: WebExtensionController?
        get() = BrowserApplication.runtime?.webExtensionController

    suspend fun reinstallPersistedExtensions(onExtensionReady: (WebExtension) -> Unit = {}) {
        val extensions = try {
            extensionDao.getAllExtensions().first()
        } catch (e: Exception) {
            emptyList()
        }
        val alreadyInstalled = getInstalledExtensions().map { it.id }.toSet()
        for (ext in extensions.filter { it.enabled }) {
            if (ext.id in alreadyInstalled) continue
            try {
                installExtension(ext.installUrl, ext.id, skipDao = true, onExtensionReady = onExtensionReady)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to re-register extension ${ext.id}", e)
            }
        }
    }

    suspend fun installExtension(
        url: String,
        expectedId: String? = null,
        skipDao: Boolean = false,
        onExtensionReady: (WebExtension) -> Unit = {},
    ): Result<WebExtension> = withContext(Dispatchers.Main) {
        val ctrl = controller
        if (ctrl == null) {
            return@withContext Result.failure(IllegalStateException("GeckoRuntime not initialized"))
        }
        try {
            val geckoResult = ctrl.install(url) ?: return@withContext Result.failure(Exception("install returned null"))
            val extension = awaitGeckoResult<WebExtension>(geckoResult)
            if (extension != null) {
                if (expectedId != null && extension.id != expectedId) {
                    controller?.uninstall(extension)?.let { awaitGeckoResult<Void>(it) }
                    return@withContext Result.failure(
                        SecurityException("Installed extension ID does not match expected ID")
                    )
                }
                if (!skipDao) {
                    saveToDatabase(extension, url)
                }
                onExtensionReady(extension)
                return@withContext Result.success(extension)
            }
            return@withContext Result.failure(Exception("Extension installation returned null"))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to install extension from $url", e)
            return@withContext Result.failure(e)
        }
    }

    suspend fun uninstallExtension(extensionId: String): Result<Unit> = withContext(Dispatchers.Main) {
        try {
            val ext = findInstalledExtension(extensionId)
            if (ext != null) {
                val geckoResult = controller?.uninstall(ext)
                if (geckoResult != null) {
                    awaitGeckoResult<Void>(geckoResult)
                }
            }
            extensionDao.deleteExtension(extensionId)
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to uninstall extension $extensionId", e)
            extensionDao.deleteExtension(extensionId)
            return@withContext Result.failure(e)
        }
    }

    suspend fun setEnabled(extensionId: String, enabled: Boolean): Result<Unit> = withContext(Dispatchers.Main) {
        try {
            val ext = findInstalledExtension(extensionId)
            if (ext != null) {
                val result = if (enabled) {
                    controller?.enable(ext, 0)
                } else {
                    controller?.disable(ext, org.mozilla.geckoview.WebExtension.DisabledFlags.USER)
                }
                if (result != null) {
                    awaitGeckoResult(result)
                }
            }
            extensionDao.setEnabled(extensionId, enabled)
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set enabled state for $extensionId", e)
            return@withContext Result.failure(e)
        }
    }

    suspend fun getInstalledExtensions(): List<WebExtension> = withContext(Dispatchers.Main) {
        try {
            val geckoResult = controller?.list() ?: return@withContext emptyList()
            val result = awaitGeckoResult<List<WebExtension>>(geckoResult)
            result?.toList() ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to list extensions", e)
            emptyList()
        }
    }

    private suspend fun findInstalledExtension(id: String): WebExtension? {
        return try {
            getInstalledExtensions().find { it.id == id }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun saveToDatabase(ext: WebExtension, installUrl: String) {
        val meta = ext.metaData ?: return
        extensionDao.upsertExtension(
            ExtensionEntity(
                id = ext.id,
                name = meta.name ?: ext.id,
                version = meta.version ?: "unknown",
                description = meta.description ?: "",
                creatorName = meta.creatorName ?: "Unknown",
                creatorUrl = meta.creatorUrl,
                homePageUrl = meta.homepageUrl,
                enabled = true,
                installUrl = installUrl
            )
        )
    }

    private suspend fun <T> awaitGeckoResult(geckoResult: org.mozilla.geckoview.GeckoResult<T>): T? {
        return suspendCancellableCoroutine { continuation ->
            geckoResult.accept(
                { value ->
                    if (continuation.isActive) continuation.resume(value)
                },
                { error ->
                    if (continuation.isActive) continuation.resume(null)
                }
            )
        }
    }
}
