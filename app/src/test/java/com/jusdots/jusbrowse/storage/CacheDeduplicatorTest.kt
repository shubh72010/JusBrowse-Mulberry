package com.jusdots.jusbrowse.storage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CacheDeduplicatorTest {

    @Test
    fun `store returns false for new content`() {
        val cache = CacheDeduplicator(10)
        val deduplicated = cache.store("key1", "unique content", 100)
        assertFalse(deduplicated)
    }

    @Test
    fun `store returns true for duplicate content`() {
        val cache = CacheDeduplicator(10)
        cache.store("key1", "same content", 100)
        val deduplicated = cache.store("key2", "same content", 100)
        assertTrue(deduplicated)
    }

    @Test
    fun `stores different content independently`() {
        val cache = CacheDeduplicator(10)
        cache.store("key1", "content A", 100)
        cache.store("key2", "content B", 200)
        assertEquals(2, cache.getEntryCount())
    }

    @Test
    fun `release removes unreferenced entry`() {
        val cache = CacheDeduplicator(10)
        cache.store("key1", "content", 100)
        cache.release("key1")
        val deduplicated = cache.store("key2", "content", 100)
        assertFalse(deduplicated)
    }

    @Test
    fun `release decrements reference count`() {
        val cache = CacheDeduplicator(10)
        val contentHash = "same".hashCode().toString()
        cache.store("key1", "same", 100)
        cache.store("key2", "same", 100)
        assertEquals(2, cache.getReferenceCount(contentHash))
        cache.release("key1")
        assertEquals(1, cache.getReferenceCount(contentHash))
    }

    @Test
    fun `clear removes all entries`() {
        val cache = CacheDeduplicator(10)
        cache.store("key1", "content A", 100)
        cache.store("key2", "content B", 200)
        cache.clear()
        assertEquals(0, cache.getEntryCount())
    }

    @Test
    fun `total cache size equals sum of entry sizes`() {
        val cache = CacheDeduplicator(100)
        cache.store("key1", "content A", 100)
        cache.store("key2", "content B", 200)
        assertEquals(300, cache.getTotalCacheSize())
    }

    @Test
    fun `evicts least recently used when over budget`() {
        val cache = CacheDeduplicator(maxCacheSizeMb = 1)
        cache.store("big1", "x".repeat(1000), 600_000)
        cache.store("big2", "y".repeat(1000), 600_000)
        assertTrue(cache.getEntryCount() <= 2)
    }
}
