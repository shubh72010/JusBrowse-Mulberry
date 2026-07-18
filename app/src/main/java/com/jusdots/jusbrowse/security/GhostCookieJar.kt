package com.jusdots.jusbrowse.security

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

object GhostCookieJar : CookieJar {

    private val containerCookies = ConcurrentHashMap<String, MutableList<Cookie>>()

    private val activeContainerId = ThreadLocal<String>()

    fun setThreadContainerId(id: String) {
        activeContainerId.set(id)
    }

    fun clearContainer(containerId: String) {
        containerCookies.remove(containerId)
    }

    fun clearAll() {
        containerCookies.clear()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val containerId = activeContainerId.get() ?: "default"
        val now = System.currentTimeMillis()
        val store = containerCookies.getOrPut(containerId) { mutableListOf() }

        cookies.forEach { newCookie ->
            store.removeAll { it.name == newCookie.name && it.domain == newCookie.domain && it.path == newCookie.path }
            if (newCookie.expiresAt > now) {
                store.add(newCookie)
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val containerId = activeContainerId.get() ?: "default"
        val now = System.currentTimeMillis()
        val store = containerCookies[containerId]
        if (store == null) return emptyList()

        val valid = store.filter { it.expiresAt > now && it.matches(url) }
        store.removeAll { it.expiresAt <= now }
        return valid
    }
}
