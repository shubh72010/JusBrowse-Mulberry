package com.jusdots.jusbrowse.security

object DownloadValidator {

    private val dangerousExtensions = setOf(
        "dex", "exe", "msi", "bat", "cmd", "com", "scr", "pif",
        "sh", "bash", "ps1", "vbs", "js", "jse", "wsf", "wsh",
        "jar", "war", "dll", "sys", "drv", "bin"
    )

    private val warnExtensions = setOf(
        "zip", "rar", "7z", "tar", "gz",
        "iso", "img", "dmg",
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
        "apk", "aab"
    )

    data class DownloadValidationResult(
        val isAllowed: Boolean,
        val requiresWarning: Boolean,
        val warningMessage: String?,
        val fileName: String,
        val mimeType: String?
    )

    fun validateDownload(
        url: String,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String?,
        contentLength: Long
    ): DownloadValidationResult {
        val fileName = guessFileName(url, contentDisposition, mimeType)
        val extension = getFileExtension(fileName).lowercase()

        if (extension in dangerousExtensions) {
            return DownloadValidationResult(
                isAllowed = false,
                requiresWarning = true,
                warningMessage = buildBlockedMessage(fileName, extension),
                fileName = fileName,
                mimeType = mimeType
            )
        }

        if (extension in warnExtensions) {
            return DownloadValidationResult(
                isAllowed = true,
                requiresWarning = true,
                warningMessage = buildWarningMessage(fileName, contentLength),
                fileName = fileName,
                mimeType = mimeType
            )
        }

        val expectedMime = getMimeTypeFromExtension(extension)
        if (mimeType != null && expectedMime != null) {
            val cleanMime = mimeType.lowercase().substringBefore(";")
            val cleanExpected = expectedMime.lowercase()

            val isGenericServerMime = cleanMime == "application/octet-stream" || cleanMime == "application/x-download"

            if (!isGenericServerMime && cleanMime != cleanExpected) {
                 return DownloadValidationResult(
                    isAllowed = true,
                    requiresWarning = true,
                    warningMessage = "Security Warning: File type mismatch.\n\nFile: $fileName\nExtension suggests: $expectedMime\nServer sent: $cleanMime\n\nThis could be an attempt to disguise a dangerous file.",
                    fileName = fileName,
                    mimeType = mimeType
                )
            }
        }

        return DownloadValidationResult(
            isAllowed = true,
            requiresWarning = false,
            warningMessage = null,
            fileName = fileName,
            mimeType = mimeType
        )
    }

    private fun sanitizeFileName(name: String): String {
        var safe = name
        safe = safe.replace("../", "").replace("..\\", "")
        safe = safe.replace("/", "_").replace("\\", "_")
        safe = safe.replace("\u0000", "")
        safe = safe.trimStart('.', '/', '\\')
        return safe.ifBlank { "download" }
    }

    private fun guessFileName(url: String, contentDisposition: String?, mimeType: String?): String {
        if (contentDisposition != null) {
            val filenameMatch = Regex("filename[^;=\\n]*=((['\"]).*?\\2|[^;\\n]*)").find(contentDisposition)
            val filename = filenameMatch?.groupValues?.get(1)?.trim('"', '\'')
            if (!filename.isNullOrBlank()) return sanitizeFileName(filename)
        }

        val path = url.substringBefore("?").substringAfterLast("/")
        if (path.isNotBlank() && !path.contains(".")) {
            val ext = when {
                mimeType?.contains("pdf") == true -> ".pdf"
                mimeType?.contains("zip") == true -> ".zip"
                mimeType?.contains("png") == true -> ".png"
                mimeType?.contains("jpg") == true || mimeType?.contains("jpeg") == true -> ".jpg"
                mimeType?.contains("gif") == true -> ".gif"
                mimeType?.contains("mp4") == true -> ".mp4"
                mimeType?.contains("mp3") == true -> ".mp3"
                mimeType?.contains("html") == true -> ".html"
                else -> ""
            }
            return if (ext.isNotEmpty()) "$path$ext" else path
        }

        if (path.isNotBlank()) return sanitizeFileName(path)
        return "download"
    }

    private fun getMimeTypeFromExtension(extension: String): String? {
        return when (extension.lowercase()) {
            "html", "htm" -> "text/html"
            "css" -> "text/css"
            "js" -> "application/javascript"
            "json" -> "application/json"
            "xml" -> "application/xml"
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "svg" -> "image/svg+xml"
            "ico" -> "image/x-icon"
            "bmp" -> "image/bmp"
            "pdf" -> "application/pdf"
            "zip" -> "application/zip"
            "rar" -> "application/vnd.rar"
            "7z" -> "application/x-7z-compressed"
            "tar" -> "application/x-tar"
            "gz" -> "application/gzip"
            "mp4" -> "video/mp4"
            "webm" -> "video/webm"
            "avi" -> "video/x-msvideo"
            "mp3" -> "audio/mpeg"
            "wav" -> "audio/wav"
            "ogg" -> "audio/ogg"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            "apk" -> "application/vnd.android.package-archive"
            "dex" -> "application/octet-stream"
            "exe" -> "application/x-msdownload"
            "sh" -> "application/x-sh"
            "jar" -> "application/java-archive"
            "iso" -> "application/x-iso9660-image"
            "dmg" -> "application/x-apple-diskimage"
            "ttf" -> "font/ttf"
            "otf" -> "font/otf"
            "woff" -> "font/woff"
            "woff2" -> "font/woff2"
            else -> null
        }
    }

    private fun getFileExtension(fileName: String): String {
        val lastDot = fileName.lastIndexOf('.')
        return if (lastDot >= 0) fileName.substring(lastDot + 1) else ""
    }

    private fun buildBlockedMessage(fileName: String, extension: String): String {
        return when (extension) {
            "apk", "aab" -> "Blocked: Android app files (.apk) may contain malware."
            "exe", "msi", "bat", "cmd" -> "Blocked: Executable files (.${extension}) cannot run on Android."
            "jar", "dex" -> "Blocked: Code files (.${extension}) can be dangerous."
            else -> "Blocked: File type .$extension blocked for security."
        }
    }

    private fun buildWarningMessage(fileName: String, contentLength: Long): String {
        val sizeStr = formatFileSize(contentLength)
        val extension = getFileExtension(fileName).lowercase()
        return if (extension == "apk" || extension == "aab") {
            "This file ($fileName) is an Android App. Only download if you trust this site. Size: $sizeStr"
        } else {
            "Download $fileName ($sizeStr)? Make sure you trust this file."
        }
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 0 -> "Unknown size"
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }

    fun isAutoOpenAllowed(fileName: String): Boolean {
        val extension = getFileExtension(fileName).lowercase()
        return extension !in dangerousExtensions && extension !in warnExtensions
    }
}
