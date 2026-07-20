package com.jusdots.jusbrowse.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jusdots.jusbrowse.data.models.ExtensionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtensionDao {
    @Query("SELECT * FROM extensions ORDER BY installedAt DESC")
    fun getAllExtensions(): Flow<List<ExtensionEntity>>

    @Query("SELECT * FROM extensions WHERE id = :id LIMIT 1")
    suspend fun getExtensionById(id: String): ExtensionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExtension(extension: ExtensionEntity)

    @Query("DELETE FROM extensions WHERE id = :id")
    suspend fun deleteExtension(id: String)

    @Query("UPDATE extensions SET enabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: String, enabled: Boolean)
}
