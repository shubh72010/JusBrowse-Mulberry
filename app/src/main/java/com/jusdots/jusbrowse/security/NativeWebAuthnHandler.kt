package com.jusdots.jusbrowse.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.ECGenParameterSpec

class NativeWebAuthnHandler(private val context: Context) {

    companion object {
        private const val TAG = "NativeWebAuthn"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_PREFIX = "wa_"
        private const val EC_CURVE = "secp256r1"
        private const val SIGNATURE_ALGO = "SHA256withECDSA"
        private const val DIGEST_ALGO = "SHA-256"
    }

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
    }
    private val prefs = context.getSharedPreferences("web_authn_v1", Context.MODE_PRIVATE)
    private val random = SecureRandom()

    private fun aliasFor(credentialIdHex: String): String = "$KEY_PREFIX$credentialIdHex"

    private fun keyExists(alias: String): Boolean {
        return try { keyStore.containsAlias(alias) } catch (e: Exception) { false }
    }

    suspend fun handleCreate(requestJson: String): Result<String> {
        Log.d(TAG, "handleCreate")
        return try {
            val options = JSONObject(requestJson)
            val rpId = options.optString("rpId").ifEmpty {
                options.optJSONObject("rp")?.optString("id") ?: return Result.failure(IllegalArgumentException("Missing rpId"))
            }
            val challenge = options.optString("challenge")
            if (challenge.isEmpty()) return Result.failure(IllegalArgumentException("Missing challenge"))

            val credentialId = ByteArray(32).also { random.nextBytes(it) }
            val credentialIdHex = bytesToHex(credentialId)
            val alias = aliasFor(credentialIdHex)

            if (keyExists(alias)) {
                return Result.failure(IllegalStateException("Credential ID collision"))
            }

            val spec = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN)
                .setAlgorithmParameterSpec(ECGenParameterSpec(EC_CURVE))
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setKeySize(256)
                .setInvalidatedByBiometricEnrollment(false)
                .setUserAuthenticationRequired(false)
                .build()

            KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, KEYSTORE_PROVIDER)
                .apply { initialize(spec) }
                .generateKeyPair()

            val publicKey = loadPublicKey(alias) as java.security.interfaces.ECPublicKey
            val w = publicKey.w
            val xBytes = w.affineX.toByteArray().normalizeTo(32)
            val yBytes = w.affineY.toByteArray().normalizeTo(32)

            val rpIdHash = MessageDigest.getInstance(DIGEST_ALGO).digest(rpId.toByteArray(Charsets.UTF_8))
            val authData = buildAuthDataCreate(rpIdHash, credentialId, xBytes, yBytes)
            val attestationObject = buildAttestationNone(authData)

            storeCredentialMeta(credentialIdHex, rpId)

            val resultJson = JSONObject().apply {
                put("id", credentialIdHex)
                put("rawId", base64UrlEncode(credentialId))
                put("type", "public-key")
                put("clientDataJSON", "")
                put("attestationObject", base64UrlEncode(attestationObject))
                put("transports", org.json.JSONArray(listOf("internal")))
            }

            Log.d(TAG, "Credential created for rpId=$rpId")
            Result.success(resultJson.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Create error", e)
            Result.failure(e)
        }
    }

    suspend fun handleGet(requestJson: String, clientDataHashB64: String): Result<String> {
        Log.d(TAG, "handleGet")
        return try {
            val options = JSONObject(requestJson)
            val rpId = options.optString("rpId")
            if (rpId.isEmpty()) return Result.failure(IllegalArgumentException("Missing rpId"))

            val clientDataHash = base64UrlDecode(clientDataHashB64)
                ?: return Result.failure(IllegalArgumentException("Missing or invalid clientDataHash"))

            val credentialIdHex: String
            val credentialId: ByteArray

            val allowCredentials = options.optJSONArray("allowCredentials")
            if (allowCredentials != null && allowCredentials.length() > 0) {
                val firstCred = allowCredentials.getJSONObject(0)
                credentialIdHex = firstCred.optString("id", "")
                credentialId = hexToBytes(credentialIdHex)
            } else {
                val matching = findMatchingCredentials(rpId)
                if (matching.isEmpty()) {
                    return Result.failure(IllegalStateException("No credentials found for $rpId"))
                }
                credentialIdHex = matching[0]
                credentialId = hexToBytes(credentialIdHex)
            }

            val alias = aliasFor(credentialIdHex)
            if (!keyExists(alias)) {
                return Result.failure(IllegalStateException("Key not found"))
            }

            val privateKey = (keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry).privateKey
            val rpIdHash = MessageDigest.getInstance(DIGEST_ALGO).digest(rpId.toByteArray(Charsets.UTF_8))
            val authData = buildAuthDataGet(rpIdHash)
            val signedData = authData + clientDataHash

            val signature = Signature.getInstance(SIGNATURE_ALGO).apply {
                initSign(privateKey)
                update(signedData)
            }.sign()

            val resultJson = JSONObject().apply {
                put("id", credentialIdHex)
                put("rawId", base64UrlEncode(credentialId))
                put("type", "public-key")
                put("clientDataJSON", "")
                put("authenticatorData", base64UrlEncode(authData))
                put("signature", base64UrlEncode(signature))
                put("userHandle", base64UrlEncode(rpId.toByteArray(Charsets.UTF_8)))
            }

            Log.d(TAG, "Assertion for rpId=$rpId")
            Result.success(resultJson.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Get error", e)
            Result.failure(e)
        }
    }

    private fun buildAuthDataCreate(rpIdHash: ByteArray, credentialId: ByteArray, x: ByteArray, y: ByteArray): ByteArray {
        val flags = byteArrayOf(0x41)
        val signCount = ByteArray(4)
        val aaguid = ByteArray(16)
        val credIdLen = byteArrayOf(((credentialId.size ushr 8) and 0xFF).toByte(), (credentialId.size and 0xFF).toByte())

        val baos = ByteArrayOutputStream()
        baos.write(rpIdHash); baos.write(flags)
        baos.write(signCount); baos.write(aaguid)
        baos.write(credIdLen); baos.write(credentialId)
        baos.write(encodeCoseEc2Key(x, y))
        return baos.toByteArray()
    }

    private fun buildAuthDataGet(rpIdHash: ByteArray): ByteArray {
        val flags = byteArrayOf(0x05)
        val signCount = ByteArray(4)
        return ByteArrayOutputStream().apply {
            write(rpIdHash); write(flags); write(signCount)
        }.toByteArray()
    }

    private fun buildAttestationNone(authData: ByteArray): ByteArray {
        val baos = ByteArrayOutputStream()
        baos.write(0xA3)
        writeCborTextKey(baos, "fmt")
        writeCborText(baos, "none")
        writeCborTextKey(baos, "authData")
        writeCborBytes(baos, authData)
        writeCborTextKey(baos, "attStmt")
        baos.write(0xA0)
        return baos.toByteArray()
    }

    private fun encodeCoseEc2Key(x: ByteArray, y: ByteArray): ByteArray {
        val baos = ByteArrayOutputStream()
        baos.write(0xA5)
        writeCborIntKey(baos, 1)
        writeCborInt(baos, 2)
        writeCborIntKey(baos, 3)
        writeCborInt(baos, -7)
        writeCborIntKey(baos, -1)
        writeCborInt(baos, 1)
        writeCborIntKey(baos, -2)
        writeCborBytes(baos, x)
        writeCborIntKey(baos, -3)
        writeCborBytes(baos, y)
        return baos.toByteArray()
    }

    private fun writeCborInt(baos: ByteArrayOutputStream, value: Int) {
        when {
            value >= 0 -> {
                if (value <= 23) baos.write(value)
                else if (value <= 0xFF) { baos.write(0x18); baos.write(value) }
                else if (value <= 0xFFFF) { baos.write(0x19); baos.write(value ushr 8); baos.write(value) }
                else { baos.write(0x1A); baos.write(value ushr 24); baos.write(value ushr 16); baos.write(value ushr 8); baos.write(value) }
            }
            else -> {
                val n = -(value + 1)
                if (n <= 23) baos.write(0x20 or n)
                else if (n <= 0xFF) { baos.write(0x38); baos.write(n) }
                else if (n <= 0xFFFF) { baos.write(0x39); baos.write(n ushr 8); baos.write(n) }
                else { baos.write(0x3A); baos.write(n ushr 24); baos.write(n ushr 16); baos.write(n ushr 8); baos.write(n) }
            }
        }
    }

    private fun writeCborIntKey(baos: ByteArrayOutputStream, key: Int) = writeCborInt(baos, key)

    private fun writeCborBytes(baos: ByteArrayOutputStream, data: ByteArray) {
        when {
            data.size <= 23 -> baos.write(0x40 or data.size)
            data.size <= 0xFF -> { baos.write(0x58); baos.write(data.size) }
            data.size <= 0xFFFF -> { baos.write(0x59); baos.write(data.size ushr 8); baos.write(data.size) }
            else -> { baos.write(0x5A); baos.write(data.size ushr 24); baos.write(data.size ushr 16); baos.write(data.size ushr 8); baos.write(data.size) }
        }
        baos.write(data)
    }

    private fun writeCborTextKey(baos: ByteArrayOutputStream, text: String) {
        val bytes = text.toByteArray(Charsets.UTF_8)
        when {
            bytes.size <= 23 -> baos.write(0x60 or bytes.size)
            bytes.size <= 0xFF -> { baos.write(0x78); baos.write(bytes.size) }
            bytes.size <= 0xFFFF -> { baos.write(0x79); baos.write(bytes.size ushr 8); baos.write(bytes.size) }
            else -> { baos.write(0x7A); baos.write(bytes.size ushr 24); baos.write(bytes.size ushr 16); baos.write(bytes.size ushr 8); baos.write(bytes.size) }
        }
        baos.write(bytes)
    }

    private fun writeCborText(baos: ByteArrayOutputStream, text: String) = writeCborTextKey(baos, text)

    private fun findMatchingCredentials(rpId: String): List<String> {
        return prefs.all.filterValues { v ->
            try {
                val jsonStr = v as? String ?: return@filterValues false
                JSONObject(jsonStr).optString("rpId") == rpId
            } catch (e: Exception) {
                false
            }
        }.keys.toList()
    }

    private fun storeCredentialMeta(credentialIdHex: String, rpId: String) {
        prefs.edit().putString(credentialIdHex, JSONObject().apply {
            put("rpId", rpId)
        }.toString()).apply()
    }

    private fun loadPublicKey(alias: String): java.security.PublicKey {
        return keyStore.getCertificate(alias).publicKey
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun hexToBytes(hex: String): ByteArray {
        return ByteArray(hex.length / 2) { i ->
            ((Character.digit(hex[i * 2], 16) shl 4) + Character.digit(hex[i * 2 + 1], 16)).toByte()
        }
    }

    private fun base64UrlEncode(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun base64UrlDecode(s: String): ByteArray? {
        return try {
            Base64.decode(s, Base64.URL_SAFE or Base64.NO_WRAP)
        } catch (e: Exception) { null }
    }

    private fun ByteArray.normalizeTo(size: Int): ByteArray {
        return if (this.size >= size) copyOfRange(this.size - size, this.size)
        else ByteArray(size - this.size) + this
    }
}
