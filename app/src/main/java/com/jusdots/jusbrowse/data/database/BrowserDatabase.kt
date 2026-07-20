package com.jusdots.jusbrowse.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jusdots.jusbrowse.data.models.Bookmark
import com.jusdots.jusbrowse.data.models.ExtensionEntity
import com.jusdots.jusbrowse.data.models.HistoryItem

@Database(
    entities = [
        Bookmark::class, 
        HistoryItem::class, 
        com.jusdots.jusbrowse.data.models.DownloadItem::class,
        com.jusdots.jusbrowse.data.models.SiteSettings::class,
        ExtensionEntity::class
    ],
    version = 9,
    exportSchema = true
)
abstract class BrowserDatabase : RoomDatabase() {

    companion object {
        val MIGRATION_7_8 = Migration(7, 8) { db ->
            db.execSQL("ALTER TABLE site_settings ADD COLUMN credentialAllowed INTEGER NOT NULL DEFAULT 1")
        }

        val MIGRATION_8_9 = Migration(8, 9) { db ->
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS extensions (
                    id TEXT NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL,
                    version TEXT NOT NULL,
                    description TEXT NOT NULL DEFAULT '',
                    creatorName TEXT NOT NULL DEFAULT '',
                    creatorUrl TEXT,
                    homePageUrl TEXT,
                    enabled INTEGER NOT NULL DEFAULT 1,
                    installUrl TEXT NOT NULL DEFAULT '',
                    installedAt INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent())
        }
    }
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
    abstract fun downloadDao(): DownloadDao
    abstract fun siteSettingsDao(): SiteSettingsDao
    abstract fun extensionDao(): ExtensionDao
}
