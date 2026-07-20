package com.jusdots.jusbrowse.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import com.jusdots.jusbrowse.data.models.HistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY visitedAt DESC LIMIT 100")
    fun getAllHistory(): Flow<List<HistoryItem>>

    @Query("SELECT * FROM history WHERE title LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%' ORDER BY visitedAt DESC")
    fun searchHistory(query: String): Flow<List<HistoryItem>>

    @Query("SELECT * FROM history WHERE url = :url LIMIT 1")
    suspend fun getHistoryByUrl(url: String): HistoryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(historyItem: HistoryItem)

    @Delete
    suspend fun deleteHistory(historyItem: HistoryItem)

    @Query("DELETE FROM history")
    suspend fun deleteAllHistory()

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT *, MAX(visitedAt) AS last_visited FROM history GROUP BY url ORDER BY last_visited DESC LIMIT :limit")
    fun getRecentHistory(limit: Int): Flow<List<HistoryItem>>

    @Query("UPDATE history SET title = :title WHERE url = :url")
    suspend fun updateHistoryTitle(url: String, title: String)
}
