package com.jusdots.jusbrowse.security

import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/**
 * Layer 11: BYOK Zero-Day Malware Scanner
 * Performs hash-based analysis using VirusTotal and Koodous APIs
 */
object SecurityScanner {
    private const val NETWORK_TIMEOUT_MS = 15_000

    data class ScanResult(
        val status: String, // Clean, Malicious, Error
        val detail: String
    )

    /**
     * Entry point for scanning a file
     */
    fun scanFile(filePath: String, vtApiKey: String, koodousApiKey: String): ScanResult {
        val file = File(filePath)
        if (!file.exists()) return ScanResult("Error", "File not found at $filePath")

        return try {
            val hash = calculateSHA256(file)
            val extension = file.extension.lowercase()

            if (extension == "apk" && koodousApiKey.isNotBlank()) {
                checkKoodous(hash, koodousApiKey)
            } else if (vtApiKey.isNotBlank()) {
                checkVirusTotal(hash, vtApiKey)
            } else {
                ScanResult("Not Scanned", "No API keys provided")
            }
        } catch (e: Exception) {
            ScanResult("Error", "Scan failed: ${e.message}")
        }
    }

    private fun calculateSHA256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val fis = FileInputStream(file)
        val buffer = ByteArray(8192)
        var bytesRead: Int
        while (fis.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }
        fis.close()
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    /**
     * Check VirusTotal API v3
     */
    private fun checkVirusTotal(hash: String, apiKey: String): ScanResult {
        val url = URL("https://www.virustotal.com/api/v3/files/$hash")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = NETWORK_TIMEOUT_MS
        connection.readTimeout = NETWORK_TIMEOUT_MS
        connection.setRequestProperty("x-apikey", apiKey)

        return try {
            if (connection.responseCode == 200) {
                val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JsonParser.parseString(responseBody).asJsonObject
                val attributes = json.getAsJsonObject("data")?.getAsJsonObject("attributes")
                val lastAnalysisStats = attributes?.getAsJsonObject("last_analysis_stats")
                
                val maliciousCount = lastAnalysisStats?.get("malicious")?.asInt ?: 0
                val suspiciousCount = lastAnalysisStats?.get("suspicious")?.asInt ?: 0

                if (maliciousCount > 0 || suspiciousCount > 0) {
                    ScanResult("Malicious", "Detected by $maliciousCount engines on VirusTotal")
                } else {
                    ScanResult("Clean", "No threats detected by VirusTotal")
                }
            } else if (connection.responseCode == 404) {
                ScanResult("Unknown", "File not seen before on VirusTotal")
            } else {
                ScanResult("Error", "VirusTotal API Error: ${connection.responseCode}")
            }
        } finally {
            connection.disconnect()
        }
    }

    /**
     * Check Koodous API (for APKs)
     */
    private fun checkKoodous(hash: String, apiKey: String): ScanResult {
        val url = URL("https://api.koodous.com/apks/$hash")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = NETWORK_TIMEOUT_MS
        connection.readTimeout = NETWORK_TIMEOUT_MS
        connection.setRequestProperty("Authorization", "Token $apiKey")

        return try {
            if (connection.responseCode == 200) {
                val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JsonParser.parseString(responseBody).asJsonObject
                
                val isDetected = json.get("detected")?.asBoolean ?: false
                val rating = json.get("rating")?.asInt ?: 0

                if (isDetected || rating < 0) {
                    ScanResult("Malicious", "Koodous flag: Risky/Malicious APK detected")
                } else {
                    ScanResult("Clean", "Koodous: No known issues with this APK")
                }
            } else if (connection.responseCode == 404) {
                ScanResult("Unknown", "Koodous: APK not in database")
            } else {
                ScanResult("Error", "Koodous API Error: ${connection.responseCode}")
            }
        } finally {
            connection.disconnect()
        }
    }
}
