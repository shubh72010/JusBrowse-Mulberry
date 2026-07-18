package com.jusdots.jusbrowse.lifecycle

import com.jusdots.jusbrowse.data.models.BrowserTab
import com.jusdots.jusbrowse.storage.TabSnapshotStorage
import org.mozilla.geckoview.GeckoSession
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

enum class TabLifecycleState {
    ACTIVE,
    SUSPENDED,
    SERIALIZED,
    EVICTED
}

data class TabLifecycleEntry(
    val tab: BrowserTab,
    val state: TabLifecycleState,
    val session: GeckoSession?,
    val lastActiveTime: Long = System.currentTimeMillis(),
    val serializedSnapshot: ByteArray? = null
)

class TabLifecycleManager(
    private val snapshotStorage: TabSnapshotStorage
) {
    private val entries = ConcurrentHashMap<String, TabLifecycleEntry>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun getEntry(tabId: String): TabLifecycleEntry? = entries[tabId]

    fun registerTab(tab: BrowserTab, session: GeckoSession) {
        entries[tab.id] = TabLifecycleEntry(
            tab = tab,
            state = TabLifecycleState.ACTIVE,
            session = session,
            lastActiveTime = System.currentTimeMillis()
        )
    }

    fun activateTab(tabId: String) {
        val entry = entries[tabId] ?: return
        entries[tabId] = entry.copy(
            state = TabLifecycleState.ACTIVE,
            lastActiveTime = System.currentTimeMillis()
        )
    }

    fun suspendTab(tabId: String) {
        val entry = entries[tabId] ?: return
        entries[tabId] = entry.copy(
            state = TabLifecycleState.SUSPENDED,
            lastActiveTime = System.currentTimeMillis()
        )
    }

    suspend fun serializeTab(tabId: String) {
        val entry = entries[tabId] ?: return
        val snapshot = snapshotStorage.serializeTab(entry.tab)
        entries[tabId] = entry.copy(
            state = TabLifecycleState.SERIALIZED,
            session = null,
            serializedSnapshot = snapshot,
            lastActiveTime = System.currentTimeMillis()
        )
    }

    suspend fun evictTab(tabId: String) {
        val entry = entries[tabId] ?: return
        snapshotStorage.removeTab(tabId)
        entries[tabId] = entry.copy(
            state = TabLifecycleState.EVICTED,
            session = null,
            serializedSnapshot = null
        )
    }

    suspend fun removeEntry(tabId: String) {
        snapshotStorage.removeTab(tabId)
        entries.remove(tabId)
    }

    fun hydrateTab(tabId: String, session: GeckoSession): BrowserTab? {
        val entry = entries[tabId] ?: return null
        val snapshot = entry.serializedSnapshot
        val tab = if (snapshot != null) {
            snapshotStorage.deserializeTab(snapshot) ?: entry.tab
        } else {
            entry.tab
        }
        entries[tabId] = TabLifecycleEntry(
            tab = tab,
            state = TabLifecycleState.ACTIVE,
            session = session,
            lastActiveTime = System.currentTimeMillis(),
            serializedSnapshot = null
        )
        return tab
    }

    fun getActiveTabIds(): Set<String> = entries.entries
        .filter { it.value.state == TabLifecycleState.ACTIVE }
        .sortedByDescending { it.value.lastActiveTime }
        .map { it.key }
        .toSet()

    fun getSuspendedTabIds(): Set<String> = entries.entries
        .filter { it.value.state == TabLifecycleState.SUSPENDED }
        .sortedByDescending { it.value.lastActiveTime }
        .map { it.key }
        .toSet()

    fun getSerializedTabIds(): Set<String> = entries.entries
        .filter { it.value.state == TabLifecycleState.SERIALIZED }
        .sortedByDescending { it.value.lastActiveTime }
        .map { it.key }
        .toSet()

    fun getActiveSessionCount(): Int = entries.count { it.value.session != null }

    fun getAllSessions(): Map<String, GeckoSession> = entries
        .filter { it.value.session != null }
        .mapValues { it.value.session!! }

    fun closeAllSessions() {
        entries.values.forEach { it.session?.close() }
    }

    suspend fun saveAllSnapshots() {
        val keys = entries.keys.toList()
        for (id in keys) {
            val entry = entries[id] ?: continue
            if (entry.state != TabLifecycleState.EVICTED) {
                entries[id] = entry.copy(
                    serializedSnapshot = snapshotStorage.serializeTab(entry.tab)
                )
            }
        }
    }

    fun getAllTabs(): List<BrowserTab> = entries.values
        .filter { it.state != TabLifecycleState.EVICTED }
        .map { it.tab }

    fun getAllEntries(): List<TabLifecycleEntry> = entries.values.toList()

    fun clear() {
        closeAllSessions()
        entries.clear()
    }
}
