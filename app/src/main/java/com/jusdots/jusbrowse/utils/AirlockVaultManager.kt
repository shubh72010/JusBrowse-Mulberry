package com.jusdots.jusbrowse.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import com.jusdots.jusbrowse.ui.components.MediaData
import com.jusdots.jusbrowse.ui.components.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.UUID

/**
 * Isolated storage for Airlock media.
 * Downloads remote media into private app storage, converts images to WebP
 * and keeps everything isolated from the system gallery and other apps.
 */
object AirlockVaultManager {
    private const val VAULT_DIR = "airlock_vault"

    private fun getVaultDir(context: Context): File {
        val dir = File(context.filesDir, VAULT_DIR)
        if (!dir.exists()) dir.mkdirs()
        // Layer 13: Prevent media scanners from indexing this folder
        val nomedia = File(dir, ".nomedia")
        if (!nomedia.exists()) nomedia.createNewFile()
        return dir
    }

    /**
     * Downloads and processes remote media into the vault.
     * Returns a new MediaData with local file paths.
     */
    suspend fun processAndVaultMedia(
        context: Context, 
        data: MediaData,
        onProgress: (Int, Int) -> Unit = { _, _ -> }
    ): MediaData = withContext(Dispatchers.IO) {
        val vaultDir = getVaultDir(context)
        
        // Purge old session for absolute isolation
        vaultDir.listFiles()?.forEach { 
            if (it.name != ".nomedia") it.delete() 
        }

        val total = data.images.size + data.videos.size + data.audio.size
        var current = 0

        val vaultedImages = data.images.map { item ->
            val localFile = downloadAndConvertImage(item.url, vaultDir)
            current++
            onProgress(current, total)
            if (localFile != null) {
                item.copy(url = localFile.absolutePath, metadata = "${item.metadata} [Extracted]")
            } else item
        }

        val vaultedVideos = data.videos.map { item ->
            val localFile = downloadFile(item.url, vaultDir, "vid")
            current++
            onProgress(current, total)
            if (localFile != null) {
                item.copy(url = localFile.absolutePath, metadata = "${item.metadata} [Extracted]")
            } else item
        }
        
        val vaultedAudio = data.audio.map { item ->
            val localFile = downloadFile(item.url, vaultDir, "aud")
            current++
            onProgress(current, total)
            if (localFile != null) {
                item.copy(url = localFile.absolutePath, metadata = "${item.metadata} [Extracted]")
            } else item
        }

        MediaData(vaultedImages, vaultedVideos, vaultedAudio)
    }

    private fun downloadAndConvertImage(urlString: String, dir: File): File? {
        return try {
            val url = URL(urlString)
            if (url.protocol != "https") return null
            val connection = url.openConnection()
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            val input = connection.getInputStream()
            val bitmap = BitmapFactory.decodeStream(input)
            input.close()
            
            if (bitmap == null) return null
            
            val fileName = "img_${UUID.randomUUID()}.webp"
            val file = File(dir, fileName)
            val out = FileOutputStream(file)
            
            // Layer 14: Radical compression for tiny footprint
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 60, out)
            } else {
                bitmap.compress(Bitmap.CompressFormat.WEBP, 60, out)
            }
            out.close()
            bitmap.recycle()
            file
        } catch (e: Exception) {
            Log.e("AirlockVaultManager", "Failed to download and convert image from $urlString", e)
            null
        }
    }

    private fun downloadFile(urlString: String, dir: File, prefix: String): File? {
        return try {
            val url = URL(urlString)
            if (url.protocol != "https") return null
            val connection = url.openConnection()
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            
            val input = connection.getInputStream()
            val extension = urlString.substringAfterLast('.', "dat")
            val fileName = "${prefix}_${UUID.randomUUID()}.$extension"
            val file = File(dir, fileName)
            val out = FileOutputStream(file)
            input.copyTo(out)
            out.close()
            input.close()
            file
        } catch (e: Exception) {
            Log.e("AirlockVaultManager", "Failed to download file from $urlString", e)
            null
        }
    }
}
