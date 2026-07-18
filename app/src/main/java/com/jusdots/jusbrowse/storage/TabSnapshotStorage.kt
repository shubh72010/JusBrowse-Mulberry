package com.jusdots.jusbrowse.storage

import android.content.Context
import com.jusdots.jusbrowse.data.models.BrowserTab
import com.jusdots.jusbrowse.data.models.Shortcut
import com.jusdots.jusbrowse.data.models.Sticker
import com.jusdots.jusbrowse.storage.proto.BrowserTabProto
import com.jusdots.jusbrowse.storage.proto.ShortcutProto
import com.jusdots.jusbrowse.storage.proto.StickerProto
import com.jusdots.jusbrowse.storage.proto.TabsSnapshotProto
import com.jusdots.jusbrowse.storage.proto.WorkspaceSnapshotProto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class TabsSnapshot(
    val tabs: List<BrowserTab>,
    val activeTabIndex: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val version: Int = 1
)

data class WorkspaceSnapshot(
    val shortcuts: List<Shortcut>,
    val stickers: List<Sticker>,
    val timestamp: Long = System.currentTimeMillis()
)

class TabSnapshotStorage(private val context: Context) {

    private var cachedSession: TabsSnapshot? = null
    private val snapshotDir: File
        get() = File(context.filesDir, "tab_snapshots").also { it.mkdirs() }

    private val sessionFile: File
        get() = File(snapshotDir, "current_session.pb")

    private val workspaceFile: File
        get() = File(snapshotDir, "workspace.pb")

    fun serializeTab(tab: BrowserTab): ByteArray {
        return toProto(tab).toByteArray()
    }

    fun deserializeTab(bytes: ByteArray): BrowserTab? {
        return try {
            fromProto(BrowserTabProto.parseFrom(bytes))
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveSessionSnapshot(snapshot: TabsSnapshot) = withContext(Dispatchers.IO) {
        cachedSession = snapshot
        val proto = TabsSnapshotProto.newBuilder()
            .setActiveTabIndex(snapshot.activeTabIndex)
            .setTimestamp(snapshot.timestamp)
            .setVersion(snapshot.version)
            .apply {
                snapshot.tabs.forEach { addTabs(toProto(it)) }
            }
            .build()
        sessionFile.writeBytes(proto.toByteArray())
    }

    suspend fun loadSessionSnapshot(): TabsSnapshot? = withContext(Dispatchers.IO) {
        if (cachedSession != null) return@withContext cachedSession
        try {
            if (!sessionFile.exists()) return@withContext null
            val bytes = sessionFile.readBytes()
            val proto = TabsSnapshotProto.parseFrom(bytes)
            TabsSnapshot(
                tabs = proto.tabsList.map { fromProto(it) },
                activeTabIndex = proto.activeTabIndex,
                timestamp = proto.timestamp,
                version = proto.version
            ).also { cachedSession = it }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveWorkspaceSnapshot(snapshot: WorkspaceSnapshot) = withContext(Dispatchers.IO) {
        val proto = WorkspaceSnapshotProto.newBuilder()
            .setTimestamp(snapshot.timestamp)
            .apply {
                snapshot.shortcuts.forEach { addShortcuts(toShortcutProto(it)) }
                snapshot.stickers.forEach { addStickers(toStickerProto(it)) }
            }
            .build()
        workspaceFile.writeBytes(proto.toByteArray())
    }

    suspend fun loadWorkspaceSnapshot(): WorkspaceSnapshot? = withContext(Dispatchers.IO) {
        try {
            if (!workspaceFile.exists()) return@withContext null
            val bytes = workspaceFile.readBytes()
            val proto = WorkspaceSnapshotProto.parseFrom(bytes)
            WorkspaceSnapshot(
                shortcuts = proto.shortcutsList.map { fromShortcutProto(it) },
                stickers = proto.stickersList.map { fromStickerProto(it) },
                timestamp = proto.timestamp
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun removeTab(tabId: String) = withContext(Dispatchers.IO) {
        val snapshot = cachedSession ?: loadSessionSnapshot() ?: return@withContext
        val updatedTabs = snapshot.tabs.filter { it.id != tabId }
        saveSessionSnapshot(snapshot.copy(tabs = updatedTabs))
    }

    fun clearAll() {
        cachedSession = null
        sessionFile.delete()
        workspaceFile.delete()
    }

    fun getStorageSizeBytes(): Long {
        return snapshotDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    }

    private fun toProto(tab: BrowserTab): BrowserTabProto {
        val builder = BrowserTabProto.newBuilder()
            .setId(tab.id)
            .setUrl(tab.url)
            .setTitle(tab.title)
            .setIsLoading(tab.isLoading)
            .setProgress(tab.progress)
            .setCanGoBack(tab.canGoBack)
            .setCanGoForward(tab.canGoForward)
            .setIsPrivate(tab.isPrivate)
            .setContainerId(tab.containerId)
            .setIsDesktopMode(tab.isDesktopMode)
            .setIsGroupMaster(tab.isGroupMaster)
        tab.favicon?.let { builder.setFavicon(it) }
        tab.parentGroupId?.let { builder.setParentGroupId(it) }
        return builder.build()
    }

    private fun fromProto(proto: BrowserTabProto): BrowserTab {
        return BrowserTab(
            id = proto.id,
            url = proto.url,
            title = proto.title,
            isLoading = proto.isLoading,
            progress = proto.progress,
            canGoBack = proto.canGoBack,
            canGoForward = proto.canGoForward,
            favicon = if (proto.hasFavicon()) proto.favicon else null,
            isPrivate = proto.isPrivate,
            containerId = proto.containerId,
            isDesktopMode = proto.isDesktopMode,
            parentGroupId = if (proto.hasParentGroupId()) proto.parentGroupId else null,
            isGroupMaster = proto.isGroupMaster
        )
    }

    private fun toShortcutProto(sc: Shortcut): ShortcutProto {
        val builder = ShortcutProto.newBuilder()
            .setId(sc.id)
            .setTitle(sc.title)
            .setUrl(sc.url)
        sc.iconUrl?.let { builder.setIconUrl(it) }
        return builder.build()
    }

    private fun fromShortcutProto(proto: ShortcutProto): Shortcut {
        return Shortcut(
            id = proto.id,
            title = proto.title,
            url = proto.url,
            iconUrl = if (proto.hasIconUrl()) proto.iconUrl else null
        )
    }

    private fun toStickerProto(st: Sticker): StickerProto {
        val builder = StickerProto.newBuilder()
            .setId(st.id)
            .setImageUri(st.imageUri)
            .setX(st.x)
            .setY(st.y)
            .setWidthDp(st.widthDp)
            .setHeightDp(st.heightDp)
            .setRotation(st.rotation)
        st.link?.let { builder.setLink(it) }
        return builder.build()
    }

    private fun fromStickerProto(proto: StickerProto): Sticker {
        return Sticker(
            id = proto.id,
            imageUri = proto.imageUri,
            x = proto.x,
            y = proto.y,
            widthDp = proto.widthDp,
            heightDp = proto.heightDp,
            rotation = proto.rotation,
            link = if (proto.hasLink()) proto.link else null
        )
    }
}
