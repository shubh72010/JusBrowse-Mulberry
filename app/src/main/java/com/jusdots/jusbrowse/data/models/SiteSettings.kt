package com.jusdots.jusbrowse.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Per-origin site permissions for Layer 3: Origin-Based Permissions
 * Stores user preferences for each domain
 */
@Entity(tableName = "site_settings")
data class SiteSettings(
    @PrimaryKey
    val origin: String, // e.g., "https://google.com"
    
    // Core permissions (Layer 1 & 2)
    val javascriptEnabled: Boolean = true,
    val domStorageEnabled: Boolean = true,
    val cookiesEnabled: Boolean = true,
    
    // Extended permissions (Layer 3)
    val localStorageEnabled: Boolean = true,
    val popupsAllowed: Boolean = false,
    val mediaAutoplayAllowed: Boolean = false,
    val clipboardAccessAllowed: Boolean = false,
    
    // Sensor/API permissions (Layer 8)
    val geolocationAllowed: Boolean = false,
    val cameraAllowed: Boolean = false,
    val microphoneAllowed: Boolean = false,

    // WebAuthn/Passkey permissions (Layer 9)
    val credentialAllowed: Boolean = true
)
