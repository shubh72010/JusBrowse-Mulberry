package com.jusdots.jusbrowse.security

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import com.jusdots.jusbrowse.data.repository.DownloadRepository
import com.jusdots.jusbrowse.data.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Layer 12: Automated Security Guard
 * Listens for download completion and triggers Layer 11 Security Scanner
 */
class DownloadReceiver(
    private val downloadRepository: DownloadRepository,
    private val preferencesRepository: PreferencesRepository
) : BroadcastReceiver() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadId == -1L) return

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor: Cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                if (statusIndex != -1 && cursor.getInt(statusIndex) == DownloadManager.STATUS_SUCCESSFUL) {
                    val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                    val filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)
                    
                    if (uriIndex != -1) {
                        val fileUri = cursor.getString(uriIndex)
                        val fileName = if (filenameIndex != -1) cursor.getString(filenameIndex) else "unknown_file"
                        
                        // Convert URI to File Path (Robust)
                        val filePath = resolveFilePath(context, fileUri)
                        
                        // Trigger Scan — validates download ownership inside coroutine
                        performSecurityScan(context, downloadId, fileName, filePath)
                    }
                }
            }
            cursor.close()
        }
    }

    private fun installApk(context: Context, filePath: String) {
        try {
            val file = java.io.File(filePath)
            if (!file.exists()) return

            val intent = Intent(Intent.ACTION_VIEW)
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } catch (_: Exception) {
            // Silently handle — no logcat leaks
        }
    }

    private fun resolveFilePath(context: Context, uriString: String): String {
        return try {
            val uri = android.net.Uri.parse(uriString)
            if (uri.scheme == "file") {
                uri.path ?: uriString.removePrefix("file://")
            } else if (uri.scheme == "content") {
                // Try to query MediaStore for real path if possible, or just use cached path logic
                val projection = arrayOf(android.provider.MediaStore.Files.FileColumns.DATA)
                context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                     if (cursor.moveToFirst()) {
                         val idx = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Files.FileColumns.DATA)
                         cursor.getString(idx)
                     } else null
                } ?: uriString // Fallback
            } else {
                uriString
            }
        } catch (e: Exception) {
            uriString.removePrefix("file://")
        }
    }

    fun cleanup() {
        scope.cancel()
    }

    private fun performSecurityScan(context: Context, downloadId: Long, fileName: String, filePath: String) {
        scope.launch {
            try {
                // Validate this download was initiated by our app
                val ourDownload = downloadRepository.allDownloads.first().find { it.systemDownloadId == downloadId }
                if (ourDownload == null) return@launch

                // Get API Keys from Preferences
                val vtKey = preferencesRepository.virusTotalApiKey.first()
                val koodousKey = preferencesRepository.koodousApiKey.first()

                // Update status to Scanning
                updateDownloadStatus(downloadId, fileName, "Scanning", "Scan in progress...")

                // Check if file exists before scanning
                val file = java.io.File(filePath)
                if (!file.exists()) {
                     updateDownloadStatus(downloadId, fileName, "Error", "File not found at $filePath")
                     return@launch
                }

                // Run Scanner
                val result = SecurityScanner.scanFile(filePath, vtKey, koodousKey)

                // Update Database with Result
                updateDownloadStatus(downloadId, fileName, result.status, result.detail)
            } catch (_: Exception) {
                updateDownloadStatus(downloadId, fileName, "Error", "Scan failed")
            }
        }
    }

    private suspend fun updateDownloadStatus(downloadId: Long, fileName: String, status: String, result: String) {
        val allDownloads = downloadRepository.allDownloads.first()
        
        // Try finding by ID first (New Logic)
        var item = allDownloads.find { it.systemDownloadId == downloadId }
        
        // Fallback to filename (Old Logic - Migration support)
        if (item == null) {
            item = allDownloads.find { it.fileName == fileName }
        }

        if (item != null) {
            // Update ID if missing
            val updatedItem = item.copy(
                securityStatus = status, 
                scanResult = result,
                systemDownloadId = if (item.systemDownloadId == -1L) downloadId else item.systemDownloadId
            )
            downloadRepository.addDownload(updatedItem)
        }
    }
}
