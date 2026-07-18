package com.jusdots.jusbrowse.ui.delegate

import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.WebRequestError

class StraitErrorPageDelegate : GeckoSession.NavigationDelegate {

    override fun onLoadError(
        session: GeckoSession,
        uri: String?,
        error: WebRequestError
    ): GeckoResult<String>? {
        val title = when (error.code) {
            WebRequestError.ERROR_UNKNOWN_HOST -> "Site not found"
            WebRequestError.ERROR_CONNECTION_REFUSED -> "Can't connect"
            WebRequestError.ERROR_NET_TIMEOUT -> "Timed out"
            else -> "Error"
        }

        val safeUri = uri?.replace("&", "&amp;")?.replace("<", "&lt;")?.replace(">", "&gt;") ?: ""

        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Error</title>
                <style>
                    body { font-family: system-ui, -apple-system, sans-serif; margin: 0; padding: 2rem; background: #f5f5f5; color: #333; }
                    .box { max-width: 600px; margin: 4rem auto; text-align: center; }
                    h1 { color: #d32f2f; margin: 0 0 1rem; font-size: 1.5rem; }
                    code { background: #eee; padding: 0.5rem; display: block; margin: 1rem 0; word-break: break-all; font-size: 0.9rem; }
                    p { margin-top: 1.5rem; }
                    a { color: #1976d2; text-decoration: none; font-size: 1rem; }
                </style>
            </head>
            <body>
                <div class="box">
                    <h1>$title</h1>
                    <code>$safeUri</code>
                    <p><a href="javascript:history.back()">&larr; Back</a></p>
                </div>
            </body>
            </html>
        """.trimIndent()

        return GeckoResult.fromValue(html)
    }
}
