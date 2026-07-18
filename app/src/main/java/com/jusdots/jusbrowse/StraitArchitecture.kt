package com.jusdots.jusbrowse

import android.content.Context
import com.jusdots.jusbrowse.data.models.BrowserTab
import com.jusdots.jusbrowse.data.models.Shortcut
import com.jusdots.jusbrowse.data.models.Sticker
import com.jusdots.jusbrowse.lifecycle.MemoryBudgetController
import com.jusdots.jusbrowse.lifecycle.TabLifecycleManager
import com.jusdots.jusbrowse.storage.CacheDeduplicator
import com.jusdots.jusbrowse.storage.StorageWritePolicyEngine
import com.jusdots.jusbrowse.storage.TabSnapshotStorage
import com.jusdots.jusbrowse.storage.TabsSnapshot
import com.jusdots.jusbrowse.storage.WorkspaceSnapshot
import org.mozilla.geckoview.GeckoSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StraitArchitecture(private val context: Context) {

    val snapshotStorage = TabSnapshotStorage(context)
    val lifecycleManager = TabLifecycleManager(snapshotStorage)
    val memoryBudget = MemoryBudgetController(context)
    val writePolicy = StorageWritePolicyEngine()
    val cacheDeduplicator = CacheDeduplicator()

    private val archScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun initialize() {
        writePolicy.setForcedFlushHandler { _ ->
            archScope.launch { lifecycleManager.saveAllSnapshots() }
        }

        writePolicy.startCrashSafetyTimer()
    }

    suspend fun loadSession(): Pair<List<BrowserTab>, Int>? {
        val snapshot = snapshotStorage.loadSessionSnapshot() ?: return null
        return Pair(snapshot.tabs, snapshot.activeTabIndex)
    }

    suspend fun saveSession(tabs: List<BrowserTab>, activeIndex: Int) = withContext(Dispatchers.IO) {
        val snapshot = TabsSnapshot(tabs = tabs, activeTabIndex = activeIndex)
        snapshotStorage.saveSessionSnapshot(snapshot)
    }

    suspend fun loadWorkspace(): WorkspaceSnapshot? {
        return snapshotStorage.loadWorkspaceSnapshot()
    }

    suspend fun saveWorkspace(shortcuts: List<Shortcut>, stickers: List<Sticker>) = withContext(Dispatchers.IO) {
        val snapshot = WorkspaceSnapshot(shortcuts = shortcuts, stickers = stickers)
        snapshotStorage.saveWorkspaceSnapshot(snapshot)
    }

    suspend fun registerTab(tab: BrowserTab, session: GeckoSession) {
        lifecycleManager.registerTab(tab, session)
        enforceMemoryBudget()
    }

    suspend fun switchTab(tabId: String) {
        lifecycleManager.activateTab(tabId)
        enforceMemoryBudget()
    }

    suspend fun closeTab(tabId: String) {
        lifecycleManager.removeEntry(tabId)
    }

    fun closeAllTabs() {
        lifecycleManager.clear()
    }

    suspend fun enforceMemoryBudget() {
        val budget = memoryBudget.getCurrentBudget()
        val activeCount = lifecycleManager.getActiveSessionCount()

        if (activeCount > budget.maxActiveGeckoSessions) {
            val activeIds = lifecycleManager.getActiveTabIds().toList()
            val toSerialize = activeIds.drop(budget.maxActiveGeckoSessions)
            for (tabId in toSerialize) {
                lifecycleManager.serializeTab(tabId)
            }
        }

        if (memoryBudget.isUnderMemoryPressure()) {
            val activeTabIds = lifecycleManager.getActiveTabIds()
            lifecycleManager.getAllSessions().keys
                .filter { it !in activeTabIds }
                .forEach { tabId ->
                    lifecycleManager.serializeTab(tabId)
                }
        }
    }

    suspend fun onTrimMemory(level: Int) {
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            val activeTabIds = lifecycleManager.getActiveTabIds()
            lifecycleManager.getAllSessions().keys
                .filter { it !in activeTabIds }
                .forEach { tabId ->
                    lifecycleManager.serializeTab(tabId)
                }
        }
    }

    suspend fun forceFlush() = withContext(Dispatchers.IO) {
        lifecycleManager.saveAllSnapshots()
        writePolicy.forceFlush()
    }

    fun clearAll() {
        lifecycleManager.clear()
        cacheDeduplicator.clear()
        snapshotStorage.clearAll()
        writePolicy.cancelAllPending()
    }
}
