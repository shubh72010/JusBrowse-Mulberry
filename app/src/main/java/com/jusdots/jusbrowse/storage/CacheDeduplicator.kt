package com.jusdots.jusbrowse.storage

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

data class CacheEntry(
    val key: String,
    val size: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val referenceCount: Int = 1
)

class CacheDeduplicator(
    private val maxCacheSizeMb: Int = 50
) {
    private val contentHashes = ConcurrentHashMap<String, String>()
    private val keyToContentHash = ConcurrentHashMap<String, String>()
    private val referenceCounts = ConcurrentHashMap<String, Int>()
    private val cacheTimestamps = ConcurrentHashMap<String, Long>()

    private val entries = CopyOnWriteArrayList<CacheEntry>()

    fun store(key: String, content: String, size: Long): Boolean {
        val contentHash = content.hashCode().toString()

        val existingKey = contentHashes[contentHash]
        if (existingKey != null && existingKey != key) {
            referenceCounts[contentHash] = (referenceCounts[contentHash] ?: 1) + 1
            keyToContentHash[key] = contentHash
            return true
        }

        if (getTotalCacheSize() + size > maxCacheSizeMb * 1024L * 1024L) {
            evictLRU()
        }

        contentHashes[contentHash] = key
        keyToContentHash[key] = contentHash
        referenceCounts[contentHash] = 1
        cacheTimestamps[key] = System.currentTimeMillis()
        entries.add(CacheEntry(key, size))
        return false
    }

    fun release(key: String) {
        val contentHash = keyToContentHash[key] ?: return
        val count = (referenceCounts[contentHash] ?: 1) - 1
        if (count <= 0) {
            referenceCounts.remove(contentHash)
            contentHashes.remove(contentHash)
            cacheTimestamps.remove(key)
            entries.removeAll { it.key == key }
        } else {
            referenceCounts[contentHash] = count
        }
        keyToContentHash.remove(key)
    }

    fun getReferenceCount(contentHash: String): Int {
        return referenceCounts[contentHash] ?: 0
    }

    fun isDeduplicated(key: String): Boolean {
        val contentHash = keyToContentHash[key] ?: return false
        return (referenceCounts[contentHash] ?: 1) > 1
    }

    fun getTotalCacheSize(): Long {
        return entries.sumOf { it.size }
    }

    fun getEntryCount(): Int = entries.size

    fun clear() {
        contentHashes.clear()
        keyToContentHash.clear()
        referenceCounts.clear()
        cacheTimestamps.clear()
        entries.clear()
    }

    private fun evictLRU() {
        val sorted = entries.sortedBy { cacheTimestamps[it.key] ?: 0L }
        var evicted = 0L
        val targetReclaim = (maxCacheSizeMb * 1024L * 1024L * 0.2).toLong()

        for (entry in sorted) {
            if (evicted >= targetReclaim) break
            val contentHash = keyToContentHash[entry.key] ?: continue
            val count = (referenceCounts[contentHash] ?: 1) - 1
            if (count <= 0) {
                referenceCounts.remove(contentHash)
                contentHashes.remove(contentHash)
                cacheTimestamps.remove(entry.key)
                evicted += entry.size
            } else {
                referenceCounts[contentHash] = count
            }
            keyToContentHash.remove(entry.key)
            entries.remove(entry)
        }
    }

    fun invalidateStale(maxAgeMs: Long = 86400000L) {
        val now = System.currentTimeMillis()
        val staleKeys = cacheTimestamps.filter { (_, ts) -> now - ts > maxAgeMs }.keys
        staleKeys.forEach { key ->
            val contentHash = keyToContentHash[key] ?: return@forEach
            referenceCounts.remove(contentHash)
            contentHashes.remove(contentHash)
            keyToContentHash.remove(key)
            cacheTimestamps.remove(key)
            entries.removeAll { it.key == key }
        }
    }
}
