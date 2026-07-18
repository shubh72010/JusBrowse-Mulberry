package com.jusdots.jusbrowse.security

import org.mozilla.geckoview.GeckoSession

object FollianBlocker {

    fun applyToSession(session: GeckoSession) {
        session.settings.allowJavascript = false
    }
}
