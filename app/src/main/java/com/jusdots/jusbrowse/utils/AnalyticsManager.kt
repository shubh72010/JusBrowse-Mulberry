package com.jusdots.jusbrowse.utils

import android.util.Log
import com.jusdots.jusbrowse.data.repository.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object AnalyticsManager {
    private const val TAG = "AnalyticsManager"
    private const val SUPABASE_URL = "https://acvlfctshipsgbfaxxbg.supabase.co/rest/v1/usage_stats"
    private const val API_KEY = "sb_publishable_WOyij2kmRSUAwojZb39QTQ_3dNMbiL5"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val JSON = "application/json; charset=utf-8".toMediaType()
    
    private val trackMutex = Mutex()

    suspend fun trackAppOpen(prefs: PreferencesRepository) {
        trackMutex.withLock {
            try {
                // 1. Check opt-out
                val analyticsEnabled = prefs.analyticsEnabled.first()
                if (!analyticsEnabled) {
                    Log.d(TAG, "Analytics disabled by user.")
                    return
                }

                // 2. Check if we already synced successfully today
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val lastSyncDate = prefs.analyticsLastSyncDate.first()
                val lastSyncSuccess = prefs.analyticsLastSyncSuccess.first()
                
                if (lastSyncDate == today && lastSyncSuccess) {
                    Log.d(TAG, "Already synced successfully today.")
                    return
                }

                // 3. Get or generate user_id
                var userId = prefs.analyticsUserId.first()
                if (userId.isNullOrEmpty()) {
                    userId = UUID.randomUUID().toString()
                    prefs.setAnalyticsUserId(userId)
                    Log.d(TAG, "Generated new user_id: $userId")
                }

                // Run network logic on IO dispatcher
                withContext(Dispatchers.IO) {
                    val getRequest = Request.Builder()
                        .url("$SUPABASE_URL?user_id=eq.$userId")
                        .addHeader("apikey", API_KEY)
                        .addHeader("Authorization", "Bearer $API_KEY")
                        .addHeader("Accept", "application/json")
                        .get()
                        .build()

                    val getResponse = client.newCall(getRequest).execute()
                    val responseBody = getResponse.body?.string() ?: "[]"
                    
                    if (!getResponse.isSuccessful) {
                        Log.e(TAG, "GET request failed: ${getResponse.code} - $responseBody")
                        prefs.setAnalyticsLastSyncSuccess(false)
                        return@withContext
                    }

                    val jsonArray = org.json.JSONArray(responseBody)
                    val isNewUser = jsonArray.length() == 0

                    if (isNewUser) {
                        // First launch / no row exists -> INSERT
                        val bodyJson = JSONObject().apply {
                            put("user_id", userId)
                            put("installed", true)
                            put("last_open_date", today)
                            put("streak", 1)
                        }

                        val postRequest = Request.Builder()
                            .url(SUPABASE_URL)
                            .addHeader("apikey", API_KEY)
                            .addHeader("Authorization", "Bearer $API_KEY")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Prefer", "return=minimal")
                            .post(bodyJson.toString().toRequestBody(JSON))
                            .build()

                        val postResponse = client.newCall(postRequest).execute()
                        if (postResponse.isSuccessful) {
                            Log.d(TAG, "Successfully tracked install/new user.")
                            prefs.setAnalyticsLastSyncDate(today)
                            prefs.setAnalyticsLastSyncSuccess(true)
                        } else {
                            Log.e(TAG, "POST request failed: ${postResponse.code}")
                            prefs.setAnalyticsLastSyncSuccess(false)
                        }
                    } else {
                        // Returning user -> PATCH
                        val existingRow = jsonArray.getJSONObject(0)
                        val lastOpenDateStr = existingRow.optString("last_open_date", "")
                        var streak = existingRow.optInt("streak", 0)

                        val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

                        if (lastOpenDateStr == yesterday) {
                            streak += 1
                        } else if (lastOpenDateStr != today) {
                            streak = 1
                        }

                        val patchBodyJson = JSONObject().apply {
                            put("last_open_date", today)
                            put("streak", streak)
                        }

                        val patchRequest = Request.Builder()
                            .url("$SUPABASE_URL?user_id=eq.$userId")
                            .addHeader("apikey", API_KEY)
                            .addHeader("Authorization", "Bearer $API_KEY")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Prefer", "return=minimal")
                            .patch(patchBodyJson.toString().toRequestBody(JSON))
                            .build()

                        val patchResponse = client.newCall(patchRequest).execute()
                        if (patchResponse.isSuccessful) {
                            Log.d(TAG, "Successfully updated daily streak: $streak")
                            prefs.setAnalyticsLastSyncDate(today)
                            prefs.setAnalyticsLastSyncSuccess(true)
                        } else {
                            Log.e(TAG, "PATCH request failed: ${patchResponse.code}")
                            prefs.setAnalyticsLastSyncSuccess(false)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during analytics tracking", e)
                prefs.setAnalyticsLastSyncSuccess(false)
            }
        }
    }
}
