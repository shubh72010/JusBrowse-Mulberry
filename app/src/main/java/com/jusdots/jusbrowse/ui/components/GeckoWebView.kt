package com.jusdots.jusbrowse.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView

/**
 * Compose wrapper for Mozilla GeckoView.
 */
@Composable
fun GeckoWebView(
    session: GeckoSession,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            GeckoView(context).apply {
                setSession(session)
            }
        },
        update = { view ->
            if (view.session != session) {
                view.releaseSession()
                view.setSession(session)
            }
        }
    )
}
