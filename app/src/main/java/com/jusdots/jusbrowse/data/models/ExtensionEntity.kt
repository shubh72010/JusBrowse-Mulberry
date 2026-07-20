package com.jusdots.jusbrowse.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "extensions")
data class ExtensionEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val version: String,
    val description: String,
    val creatorName: String,
    val creatorUrl: String?,
    val homePageUrl: String?,
    val enabled: Boolean,
    val installUrl: String,
    val installedAt: Long = System.currentTimeMillis()
)
