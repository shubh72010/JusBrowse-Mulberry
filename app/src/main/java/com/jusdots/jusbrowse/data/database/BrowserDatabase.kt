package com.jusdots.jusbrowse.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jusdots.jusbrowse.data.models.Bookmark
import com.jusdots.jusbrowse.data.models.HistoryItem

@Database(
    entities = [
        Bookmark::class, 
        HistoryItem::class, 
        com.jusdots.jusbrowse.data.models.DownloadItem::class,
        com.jusdots.jusbrowse.data.models.SiteSettings::class
    ],
    version = 8,
    exportSchema = true
)
abstract class BrowserDatabase : RoomDatabase() {

    companion object {
        val MIGRATION_7_8 = Migration(7, 8) { db ->
            db.execSQL("ALTER TABLE site_settings ADD COLUMN credentialAllowed INTEGER NOT NULL DEFAULT 1")
        }
    }
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
    abstract fun downloadDao(): DownloadDao
    abstract fun siteSettingsDao(): SiteSettingsDao
}
