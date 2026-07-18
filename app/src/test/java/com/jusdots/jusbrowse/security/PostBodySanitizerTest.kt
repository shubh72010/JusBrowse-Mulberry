package com.jusdots.jusbrowse.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PostBodySanitizerTest {

    @Test
    fun `should block known tracker endpoints`() {
        assertTrue(PostBodySanitizer.shouldBlockPost("https://www.google-analytics.com/collect?v=1"))
        assertTrue(PostBodySanitizer.shouldBlockPost("https://www.google-analytics.com/g/collect"))
        assertTrue(PostBodySanitizer.shouldBlockPost("https://facebook.com/tr/?id=123"))
        assertTrue(PostBodySanitizer.shouldBlockPost("https://bat.bing.com/bat.js"))
        assertTrue(PostBodySanitizer.shouldBlockPost("https://analytics.tiktok.com/api"))
        assertTrue(PostBodySanitizer.shouldBlockPost("https://snap.licdn.com/collect"))
    }

    @Test
    fun `should not block legitimate endpoints`() {
        assertFalse(PostBodySanitizer.shouldBlockPost("https://example.com/api/login"))
        assertFalse(PostBodySanitizer.shouldBlockPost("https://mysite.com/collect"))
        assertFalse(PostBodySanitizer.shouldBlockPost("https://api.github.com/repos"))
    }

    @Test
    fun `should strip GA tracking fields from URL-encoded body`() {
        val body = "email=user@example.com&password=secret&_ga=GA1.2.123&cid=12345&name=John"
        val result = PostBodySanitizer.sanitize("https://example.com/login", body)
        assertEquals("email=user@example.com&password=secret&name=John", result)
    }

    @Test
    fun `should strip fbclid from URL-encoded body`() {
        val body = "email=user@example.com&fbclid=abc123&_fbp=xyz&action=login"
        val result = PostBodySanitizer.sanitize("https://example.com/login", body)
        assertEquals("email=user@example.com&action=login", result)
    }

    @Test
    fun `should pass through JSON bodies unchanged`() {
        val body = """{"email":"user@example.com","_ga":"GA1.2.123","cid":"12345"}"""
        val result = PostBodySanitizer.sanitize("https://example.com/login", body)
        assertEquals(body, result)
    }

    @Test
    fun `should handle empty body`() {
        assertEquals("", PostBodySanitizer.sanitize("https://example.com", ""))
    }

    @Test
    fun `should handle body with no tracking fields`() {
        val body = "email=user@example.com&password=secret&name=John"
        assertEquals(body, PostBodySanitizer.sanitize("https://example.com/login", body))
    }
}
