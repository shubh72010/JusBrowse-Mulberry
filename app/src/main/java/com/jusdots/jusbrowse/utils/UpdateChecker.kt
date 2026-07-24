package com.jusdots.jusbrowse.utils

import android.util.Log
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class UpdateInfo(
    val latestVersion: String,
    val downloadUrl: String,
    val releaseNotesUrl: String,
    val isNewer: Boolean
)

private data class GitHubRelease(
    @SerializedName("tag_name") val tagName: String,
    @SerializedName("html_url") val htmlUrl: String,
    val assets: List<GitHubAsset>?
)

private data class GitHubAsset(
    @SerializedName("browser_download_url") val browserDownloadUrl: String?,
    val name: String?
)

object UpdateChecker {

    private const val TAG = "UpdateChecker"
    private const val GITHUB_API = "https://api.github.com/repos/shubh72010/JusBrowse-Mulberry/releases/latest"
    private const val FALLBACK_RELEASE_URL = "https://github.com/shubh72010/JusBrowse-Mulberry/releases/latest"

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val gson = com.google.gson.Gson()

    fun check(installedVersion: String): UpdateInfo? {
        return try {
            val request = Request.Builder()
                .url(GITHUB_API)
                .header("Accept", "application/vnd.github.v3+json")
                .build()

            val response = client.newCall(request).execute()
            if (response.code == 404) {
                Log.i(TAG, "No releases found — treating as up to date")
                return UpdateInfo(
                    latestVersion = installedVersion,
                    downloadUrl = FALLBACK_RELEASE_URL,
                    releaseNotesUrl = FALLBACK_RELEASE_URL,
                    isNewer = false
                )
            }
            if (!response.isSuccessful) {
                Log.w(TAG, "GitHub API returned ${response.code}")
                return null
            }

            val body = response.body?.string() ?: return null
            val release = gson.fromJson(body, GitHubRelease::class.java) ?: return null

            val latestTag = release.tagName.removePrefix("v")
            val isNewer = compareVersions(latestTag, installedVersion) > 0

            val downloadUrl = if (release.assets != null && release.assets.isNotEmpty()) {
                val apkAsset = release.assets.find { it.name?.endsWith(".apk") == true }
                apkAsset?.browserDownloadUrl ?: release.htmlUrl
            } else {
                release.htmlUrl
            }

            UpdateInfo(
                latestVersion = latestTag,
                downloadUrl = downloadUrl,
                releaseNotesUrl = release.htmlUrl,
                isNewer = isNewer
            )
        } catch (e: Exception) {
            Log.e(TAG, "Update check failed", e)
            null
        }
    }

    private fun compareVersions(a: String, b: String): Int {
        val aParts = a.split(".").map { it.toIntOrNull() ?: 0 }
        val bParts = b.split(".").map { it.toIntOrNull() ?: 0 }
        val maxLen = maxOf(aParts.size, bParts.size)
        for (i in 0 until maxLen) {
            val diff = (aParts.getOrElse(i) { 0 }) - (bParts.getOrElse(i) { 0 })
            if (diff != 0) return diff
        }
        return 0
    }
}
