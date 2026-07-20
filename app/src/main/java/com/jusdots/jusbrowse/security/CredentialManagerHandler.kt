package com.jusdots.jusbrowse.security

import android.app.Activity
import android.util.Log
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException

class CredentialManagerHandler(private val activity: Activity) {

    companion object {
        private const val TAG = "CredentialMgrHandler"
    }

    private val credentialManager = CredentialManager.create(activity)

    suspend fun handleCreate(requestJson: String, clientDataHashB64: String = ""): Result<String> {
        Log.d(TAG, "handleCreate: requestJson starts with=${requestJson.take(80)}")
        return try {
            val request = CreatePublicKeyCredentialRequest(
                requestJson = requestJson,
                preferImmediatelyAvailableCredentials = false
            )
            val response = credentialManager.createCredential(activity, request)
            Log.d(TAG, "createCredential returned: ${response::class.java.name}")
            val publicKeyResponse = response as CreatePublicKeyCredentialResponse
            Log.d(TAG, "Credential creation succeeded")
            Result.success(publicKeyResponse.registrationResponseJson)
        } catch (e: CreateCredentialException) {
            Log.w(TAG, "CreateCredentialException: class=${e::class.java.name} msg=${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Create unexpected error: ${e::class.java.name} msg=${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun handleGet(requestJson: String, clientDataHashB64: String = ""): Result<String> {
        Log.d(TAG, "handleGet: requestJson starts with=${requestJson.take(80)}")
        return try {
            val request = GetCredentialRequest(
                credentialOptions = listOf(GetPublicKeyCredentialOption(requestJson))
            )
            val response = credentialManager.getCredential(activity, request)
            Log.d(TAG, "getCredential returned: class=${response::class.java.name}")
            val credential = response.credential
            Log.d(TAG, "Credential type=${credential::class.java.name}")
            when (credential) {
                is PublicKeyCredential -> {
                    val authJson = credential.authenticationResponseJson
                    Log.d(TAG, "Passkey credential retrieved")
                    Result.success(authJson)
                }
                else -> {
                    Log.w(TAG, "Unexpected credential type: ${credential::class.java.name}")
                    Result.failure(IllegalStateException(
                        "Unexpected credential type: ${credential::class.java.name}"
                    ))
                }
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "GetCredentialCancellationException: class=${e::class.java.name} msg=${e.message}")
            Result.failure(e)
        } catch (e: GetCredentialException) {
            Log.w(TAG, "GetCredentialException: class=${e::class.java.name} msg=${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Get unexpected error: ${e::class.java.name} msg=${e.message}", e)
            Result.failure(e)
        }
    }
}
