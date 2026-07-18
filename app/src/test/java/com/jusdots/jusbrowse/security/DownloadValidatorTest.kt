package com.jusdots.jusbrowse.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DownloadValidatorTest {

    @Test
    fun `should block dangerous extensions`() {
        val result = DownloadValidator.validateDownload(
            url = "https://example.com/virus.exe",
            userAgent = null,
            contentDisposition = null,
            mimeType = "application/x-msdownload",
            contentLength = 1000
        )
        assertFalse(result.isAllowed)
        assertTrue(result.requiresWarning)
        assertNotNull(result.warningMessage)
    }

    @Test
    fun `should block dex files`() {
        val result = DownloadValidator.validateDownload(
            url = "https://example.com/classes.dex",
            userAgent = null,
            contentDisposition = null,
            mimeType = "application/octet-stream",
            contentLength = 50000
        )
        assertFalse(result.isAllowed)
    }

    @Test
    fun `should warn on apk files`() {
        val result = DownloadValidator.validateDownload(
            url = "https://example.com/app.apk",
            userAgent = null,
            contentDisposition = null,
            mimeType = "application/vnd.android.package-archive",
            contentLength = 10_000_000
        )
        assertTrue(result.isAllowed)
        assertTrue(result.requiresWarning)
        assertTrue(result.warningMessage?.contains("Android App") == true)
    }

    @Test
    fun `should warn on zip files`() {
        val result = DownloadValidator.validateDownload(
            url = "https://example.com/files.zip",
            userAgent = null,
            contentDisposition = null,
            mimeType = "application/zip",
            contentLength = 5000
        )
        assertTrue(result.isAllowed)
        assertTrue(result.requiresWarning)
    }

    @Test
    fun `should allow safe image files`() {
        val result = DownloadValidator.validateDownload(
            url = "https://example.com/photo.jpg",
            userAgent = null,
            contentDisposition = null,
            mimeType = "image/jpeg",
            contentLength = 100000
        )
        assertTrue(result.isAllowed)
        assertFalse(result.requiresWarning)
        assertNull(result.warningMessage)
    }

    @Test
    fun `should warn on MIME type mismatch`() {
        val result = DownloadValidator.validateDownload(
            url = "https://example.com/document.png",
            userAgent = null,
            contentDisposition = null,
            mimeType = "text/html",
            contentLength = 5000
        )
        assertTrue(result.isAllowed)
        assertTrue(result.requiresWarning)
        assertTrue(result.warningMessage?.contains("type mismatch") == true)
    }

    @Test
    fun `should extract filename from content-disposition`() {
        val result = DownloadValidator.validateDownload(
            url = "https://example.com/download",
            userAgent = null,
            contentDisposition = "attachment; filename=\"report.pdf\"",
            mimeType = "application/pdf",
            contentLength = 5000
        )
        assertEquals("report.pdf", result.fileName)
    }

    @Test
    fun `should sanitize path traversal in filename`() {
        val result = DownloadValidator.validateDownload(
            url = "https://example.com/download",
            userAgent = null,
            contentDisposition = "attachment; filename=\"../../etc/passwd\"",
            mimeType = null,
            contentLength = 100
        )
        assertFalse(result.fileName.contains("../"))
    }

    @Test
    fun `should allow png files`() {
        assertTrue(DownloadValidator.isAutoOpenAllowed("image.png"))
    }

    @Test
    fun `should not auto-open apk files`() {
        assertFalse(DownloadValidator.isAutoOpenAllowed("app.apk"))
    }

    @Test
    fun `should not auto-open exe files`() {
        assertFalse(DownloadValidator.isAutoOpenAllowed("setup.exe"))
    }
}
