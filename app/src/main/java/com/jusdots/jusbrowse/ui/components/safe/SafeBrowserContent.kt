package com.jusdots.jusbrowse.ui.components.safe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.jusdots.jusbrowse.data.models.BrowserTab
import com.jusdots.jusbrowse.data.models.Shortcut
import com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel
import kotlinx.coroutines.launch
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.WebRequestError

@Composable
fun SafeBrowserContent(
    viewModel: BrowserViewModel,
    activeTab: BrowserTab?,
    shortcuts: List<Shortcut>,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isStartPage = activeTab == null
        || activeTab.url.isBlank()
        || activeTab.url == "about:blank"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isStartPage) {
            SafeStartPage(
                searchEngine = "DuckDuckGo",
                shortcuts = shortcuts,
                onSearch = onNavigate,
                onShortcutClick = { onNavigate(it.url) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        } else {
            activeTab?.let { tab ->
                SafeGeckoView(
                    viewModel = viewModel,
                    tab = tab,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SafeGeckoView(
    viewModel: BrowserViewModel,
    tab: BrowserTab,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val tabIndex = remember(tab.id) {
        viewModel.tabDescriptors.indexOfFirst { it.id == tab.id }.coerceAtLeast(0)
    }
    var isLoading by remember { mutableStateOf(true) }
    var session by remember(tab.id) { mutableStateOf(viewModel.getGeckoSession(tab.id)) }

    LaunchedEffect(tab.id) {
        if (session == null) {
            session = viewModel.getOrCreateGeckoSession(tab.id, tab.isPrivate, tab.containerId)
        }
        if (tab.url.isNotBlank() && tab.url != "about:blank") {
            scope.launch { session?.loadUri(tab.url) }
        }
    }

    DisposableEffect(session) {
        val s = session ?: return@DisposableEffect onDispose {}
        val progressDelegate = object : GeckoSession.ProgressDelegate {
            override fun onPageStart(session: GeckoSession, url: String) {
                isLoading = true
                viewModel.updateTabLoadingState(tabIndex, true)
            }
            override fun onPageStop(geckoSession: GeckoSession, success: Boolean) {
                isLoading = false
                viewModel.updateTabLoadingState(tabIndex, false)
            }
            override fun onProgressChange(geckoSession: GeckoSession, progress: Int) {
                viewModel.updateTabLoadingState(tabIndex, true, progress.toFloat())
            }
            override fun onSecurityChange(geckoSession: GeckoSession, securityInfo: GeckoSession.ProgressDelegate.SecurityInformation) {}
        }
        val navigationDelegate = object : GeckoSession.NavigationDelegate {
            override fun onLocationChange(session: GeckoSession, url: String?, permits: List<GeckoSession.PermissionDelegate.ContentPermission>, hasUserGesture: Boolean) {
                url?.let {
                    val currentTab = tab
                    if (it != currentTab.url) {
                        if (it == "about:blank" && currentTab.url != "about:blank") return
                        viewModel.navigateToUrlForIndex(tabIndex, it)
                    }
                }
            }
            override fun onLoadError(session: GeckoSession, uri: String?, error: WebRequestError): GeckoResult<String>? = null
        }
        val contentDelegate = object : GeckoSession.ContentDelegate {
            override fun onTitleChange(session: GeckoSession, title: String?) {
                title?.let { viewModel.updateTabTitle(tabIndex, it) }
            }
            override fun onFullScreen(session: GeckoSession, fullScreen: Boolean) {}
            override fun onExternalResponse(session: GeckoSession, response: org.mozilla.geckoview.WebResponse) {}
        }
        s.progressDelegate = progressDelegate
        s.navigationDelegate = navigationDelegate
        s.contentDelegate = contentDelegate

        onDispose {
            s.progressDelegate = null
            s.navigationDelegate = null
            s.contentDelegate = null
        }
    }

    Box(modifier = modifier) {
        val s = session
        if (s != null) {
            AndroidView(
                factory = { ctx ->
                    org.mozilla.geckoview.GeckoView(ctx).apply { setSession(s) }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp),
                strokeWidth = 2.dp
            )
        }
    }
}
