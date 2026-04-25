/*package com.jusdots.jusbrowse.ui.components
import android.view.ViewGroup
import android.view.MotionEvent
import android.view.KeyEvent
import android.webkit.WebView
import kotlinx.coroutines.flow.*
import android.webkit.WebViewClient
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jusdots.jusbrowse.R
import com.google.gson.Gson
import com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebChromeClient
import android.webkit.CookieManager
import java.io.ByteArrayInputStream
import com.jusdots.jusbrowse.security.NetworkSurgeon
import android.content.ClipData
import android.view.DragEvent
import android.view.View
import android.widget.FrameLayout
import android.os.Build
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.abs
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.activity.compose.BackHandler
import com.jusdots.jusbrowse.ui.theme.GlassCardShape
import com.jusdots.jusbrowse.ui.theme.InsecureRed
import com.jusdots.jusbrowse.ui.theme.PrivatePurple
import com.jusdots.jusbrowse.ui.theme.SecureGreen
import com.jusdots.jusbrowse.ui.theme.SecureGreenContainer
import com.jusdots.jusbrowse.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddressBarWithWebView(
    viewModel: BrowserViewModel,
    tabIndex: Int,
    onOpenAirlockGallery: () -> Unit,
    modifier: Modifier = Modifier,
    stickerContent: @Composable () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val tab = if (tabIndex in viewModel.tabs.indices) viewModel.tabs[tabIndex] else null
    val searchEngine by viewModel.searchEngine.collectAsStateWithLifecycle(initialValue = "DuckDuckGo")
    val customSearchEngineUrl by viewModel.customSearchEngineUrl.collectAsStateWithLifecycle(initialValue = "")
    val adBlockEnabled by viewModel.adBlockEnabled.collectAsStateWithLifecycle(initialValue = true)
    val httpsOnly by viewModel.httpsOnly.collectAsStateWithLifecycle(initialValue = false)
    val follianMode by viewModel.follianMode.collectAsStateWithLifecycle(initialValue = false)

    // Engines
    val defaultEngineEnabled by viewModel.defaultEngineEnabled.collectAsStateWithLifecycle(initialValue = true)
    val jusFakeEnabled by viewModel.jusFakeEngineEnabled.collectAsStateWithLifecycle(initialValue = false)
    val boringEnabled by viewModel.boringEngineEnabled.collectAsStateWithLifecycle(initialValue = false)

    // PILL BAR STATES
    var isPillExpanded by remember { mutableStateOf(false) }
    var showPillMenu by remember { mutableStateOf(false) }

    // Local state for the address bar text using TextFieldValue for selection control
    var urlTextFieldValue by remember {
        mutableStateOf(TextFieldValue(tab?.url?.replace("about:blank", "") ?: ""))
    }

    // FIX #2: Explicitly typed fullscreen state (was inferred as Nothing?)
    var fullscreenView by remember { mutableStateOf<View?>(null) }
    var fullscreenCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }


    // System Back Handler for Menu Dismissal
    BackHandler(showPillMenu || isPillExpanded) {
        if (showPillMenu) showPillMenu = false
        else if (isPillExpanded) {
            isPillExpanded = false
            focusManager.clearFocus()
        }
    }


    // Sync URL text from tab changes, but ONLY if not expanded (not typing)
    LaunchedEffect(tab?.url, isPillExpanded) {
        if (!isPillExpanded) {
            urlTextFieldValue = TextFieldValue(tab?.url?.replace("about:blank", "") ?: "")
        }
    }

    var isDragging by remember { mutableStateOf(false) }

    // Download confirmation states
    var showDownloadWarning by remember { mutableStateOf(false) }
    val vtApiKey by viewModel.virusTotalApiKey.collectAsStateWithLifecycle(initialValue = "")
    val koodousApiKey by viewModel.koodousApiKey.collectAsStateWithLifecycle(initialValue = "")

    // FIX #1: pendingDownloadUrl typed as nullable String to match ?.let usage.
    // pendingDownloadInfo kept as Any? to avoid coupling to DownloadValidator's inner class name;
    // cast to the concrete type via a local val wherever fields are accessed.
    var pendingDownloadInfo by remember { mutableStateOf<com.jusdots.jusbrowse.security.DownloadValidator.DownloadValidationResult?>(null) }
    var pendingDownloadUrl by remember { mutableStateOf<String?>(null) }

    var showTrackerDetails by remember { mutableStateOf(false) }
    val trackers = if (tab != null) viewModel.blockedTrackers[tab.id] ?: emptyList() else emptyList()
    val multiMediaPlaybackEnabled by viewModel.multiMediaPlaybackEnabled.collectAsStateWithLifecycle(initialValue = false)

    // FIX #12: Collect isBoomerMode ONCE here; do not call collectAsState() again inside lambdas
    val isBoomerMode by viewModel.isBoomerMode.collectAsStateWithLifecycle()
    val protectionWhitelist by viewModel.protectionWhitelist.collectAsStateWithLifecycle(initialValue = "")

    // Calculate if the current domain is whitelisted
    val isWhitelisted = remember(tab?.url, protectionWhitelist) {
        val host = tab?.url?.let { android.net.Uri.parse(it).host } ?: ""
        if (host.isEmpty()) false
        else protectionWhitelist.split(",").map { it.trim() }.any { 
            it.isNotEmpty() && (host == it || host.endsWith(".$it"))
        }
    }

    // Elastic Swipe State (Animatable for smooth physics)
    val pillOffset = remember { androidx.compose.animation.core.Animatable(0f) }
    val pillVerticalOffset = remember { androidx.compose.animation.core.Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Focus Requester for Search Bar
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    // Sync local offsets with global reveal trigger
    LaunchedEffect(Unit) {
        viewModel.revealBottomBarEvent.collectLatest {
            scope.launch {
                pillOffset.animateTo(0f, spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy))
                pillVerticalOffset.animateTo(0f, spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy))
            }
        }
    }

    // File Upload Handling
    var cameraImageUri by remember { mutableStateOf<android.net.Uri?>(null) }

    // FIX #6: Store a reference to the actual camera File so we can check existence correctly
    var cameraPhotoFile by remember { mutableStateOf<java.io.File?>(null) }

    var filePathCallback by remember { mutableStateOf<android.webkit.ValueCallback<Array<android.net.Uri>>?>(null) }
    var pendingFileChooserParams by remember { mutableStateOf<android.webkit.WebChromeClient.FileChooserParams?>(null) }

    val filePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (filePathCallback == null) return@rememberLauncherForActivityResult
        val data = result.data
        val results: Array<android.net.Uri>? = if (result.resultCode == android.app.Activity.RESULT_OK) {
            when {
                data?.data != null -> arrayOf(data.data!!)
                data?.clipData != null -> {
                    val count = data.clipData!!.itemCount
                    Array(count) { i -> data.clipData!!.getItemAt(i).uri }
                }
                // FIX #6: Check the actual File reference, not a reconstructed path from URI
                cameraPhotoFile != null -> {
                    if (cameraPhotoFile!!.exists() && cameraPhotoFile!!.length() > 0) {
                        arrayOf(cameraImageUri!!)
                    } else null
                }
                else -> null
            }
        } else null

        filePathCallback?.onReceiveValue(results)
        filePathCallback = null
        pendingFileChooserParams = null
        // Clear camera refs after use to prevent accidental reuse
        cameraImageUri = null
        cameraPhotoFile = null
    }

    // Helper to launch the chooser - MUST be defined before use in other launchers
    fun launchChooser(ctx: android.content.Context, params: android.webkit.WebChromeClient.FileChooserParams, hasCameraPermission: Boolean) {
        val intentList = mutableListOf<android.content.Intent>()
        if (hasCameraPermission) {
            val takePictureIntent = android.content.Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            // FIX #6: Store the File reference so the result handler can check it directly
            val photoFile = java.io.File(ctx.filesDir, "upload_captured_${System.currentTimeMillis()}.jpg")
            val uri = androidx.core.content.FileProvider.getUriForFile(
                ctx,
                "${ctx.packageName}.provider",
                photoFile
            )
            cameraImageUri = uri
            cameraPhotoFile = photoFile
            takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)
            intentList.add(takePictureIntent)
        }

        val contentSelectionIntent = android.content.Intent(android.content.Intent.ACTION_GET_CONTENT).apply {
            addCategory(android.content.Intent.CATEGORY_OPENABLE)
            type = params.acceptTypes?.firstOrNull { it.isNotEmpty() } ?:
            if (params.mode == android.webkit.WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE) {
                putExtra(android.content.Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
        }

        val chooserIntent = android.content.Intent(android.content.Intent.ACTION_CHOOSER).apply {
            putExtra(android.content.Intent.EXTRA_INTENT, contentSelectionIntent)
            putExtra(android.content.Intent.EXTRA_TITLE, "Upload from")
            if (intentList.isNotEmpty()) {
                putExtra(android.content.Intent.EXTRA_INITIAL_INTENTS, intentList.toTypedArray())
            }
        }

        try {
            filePickerLauncher.launch(chooserIntent)
        } catch (e: Exception) {
            filePathCallback?.onReceiveValue(null)
            filePathCallback = null
            pendingFileChooserParams = null
        }
    }

    val mediaPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[android.Manifest.permission.CAMERA] ?: false
        // After permission results, launch the chooser
        val params = pendingFileChooserParams
        if (params != null) {
            launchChooser(context, params, cameraGranted)
        }
    }

    // Auto-focus when expanded
    var hasGainedFocus by remember { mutableStateOf(false) }
    LaunchedEffect(isPillExpanded) {
        if (isPillExpanded) {
            hasGainedFocus = false
            kotlinx.coroutines.delay(100)
            focusRequester.requestFocus()
        }
    }

    // Elastic Width Animation for Pill Bar
    val animatedPillWidth by animateFloatAsState(
        targetValue = if (isPillExpanded) 1.0f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pillWidth"
    )

    // Morphing Menu Animations
    val animatedPillHeight by animateDpAsState(
        targetValue = if (showPillMenu) 580.dp else 56.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "pillHeight"
    )
    val animatedPillWidthDp by animateDpAsState(
        targetValue = if (showPillMenu) 360.dp else if (isPillExpanded) 360.dp else 260.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "pillWidthDp"
    )
    val animatedCornerRadius by animateDpAsState(
        targetValue = if (showPillMenu) 32.dp else 28.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "pillCorner"
    )

    // Scroll Hide State - Sync with Global ViewModel state
    val bottomBarHeightPx = with(androidx.compose.ui.platform.LocalDensity.current) { 200.dp.toPx() }
    val bottomBarOffsetHeightPxState by viewModel.bottomBarOffsetHeightPx.collectAsStateWithLifecycle()

    val nestedScrollConnection = remember(isPillExpanded) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isPillExpanded) return Offset.Zero
                val delta = available.y
                val newOffset = bottomBarOffsetHeightPxState + (-delta)
                viewModel.updateBottomBarOffset(newOffset.coerceIn(0f, bottomBarHeightPx))
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // available.y > 10f means the user swiped DOWN (finger down) past the bottom
                if (available.y > 10f && bottomBarOffsetHeightPxState > 0f) {
                    viewModel.triggerRevealBottomBar()
                }
                return Offset.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val nativeEvent = event.motionEvent
                        
                        // 1. Mouse Buttons (Back/Forward)
                        if (nativeEvent != null && event.type == PointerEventType.Press) {
                            if (nativeEvent.isButtonPressed(MotionEvent.BUTTON_BACK)) {
                                viewModel.getWebView(tab?.id ?: "")?.let {
                                    if (it.canGoBack()) it.goBack()
                                }
                            } else if (nativeEvent.isButtonPressed(MotionEvent.BUTTON_FORWARD)) {
                                viewModel.getWebView(tab?.id ?: "")?.let {
                                    if (it.canGoForward()) it.goForward()
                                }
                            }
                        }

                        // 2. Trackpad Horizontal Scroll (Back/Forward Gestures)
                        if (event.type == PointerEventType.Scroll && !isPillExpanded) {
                            val delta = event.changes.first().scrollDelta
                            if (delta.x > 2.0f) { // Swipe Right -> Back
                                viewModel.getWebView(tab?.id ?: "")?.let {
                                    if (it.canGoBack()) it.goBack()
                                }
                            } else if (delta.x < -2.0f) { // Swipe Left -> Forward
                                viewModel.getWebView(tab?.id ?: "")?.let {
                                    if (it.canGoForward()) it.goForward()
                                }
                            }
                        }
                    }
                }
            }
    ) {

        // 1. WebView Content Layer (Full Screen with padding for status bar)
        Box(
            modifier = Modifier.fillMaxSize().statusBarsPadding()
        ) {
            if (tab != null && tab.url != "about:blank") {
                key(tab.id) {
                    AndroidView(
                        factory = { ctx ->
                            val existing = viewModel.getWebView(tab.id)
                            if (existing != null) {
                                (existing.parent as? ViewGroup)?.removeView(existing)
                                existing
                            } else {
                                WebView(ctx).apply {
                                    com.jusdots.jusbrowse.security.ContainerManager.applyContainer(this, tab.containerId ?: "default")

                                    if (follianMode) {
                                        com.jusdots.jusbrowse.security.FollianBlocker.applyToWebView(this)
                                    } else {
                                        settings.javaScriptEnabled = true
                                    }

                                    settings.domStorageEnabled = true
                                    
                                    // Advanced Privacy: Strip X-Requested-With (Aggressive)
                                    if (androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.REQUESTED_WITH_HEADER_ALLOW_LIST)) {
                                        androidx.webkit.WebSettingsCompat.setRequestedWithHeaderOriginAllowList(settings, setOf("https://none.none"))
                                    }

                                    // Advanced Privacy: Apply UserAgentMetadata for Boring/Fake Mode
                                    val webViewVersion = androidx.webkit.WebViewCompat.getCurrentWebViewPackage(ctx)?.versionName ?: "131.0.0.0"
                                    val metadata = if (boringEnabled) {
                                        com.jusdots.jusbrowse.security.BoringEngine.getUserAgentMetadata(webViewVersion)
                                    } else if (jusFakeEnabled) {
                                        com.jusdots.jusbrowse.security.FakeModeManager.getUserAgentMetadata()
                                    } else null

                                    if (metadata != null && androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.USER_AGENT_METADATA)) {
                                        androidx.webkit.WebSettingsCompat.setUserAgentMetadata(settings, metadata)
                                    }

                                    // Enforce explicit User-Agent string for alignment
                                        when {
                                            tab.isDesktopMode -> {
                                                settings.userAgentString = com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel.DESKTOP_USER_AGENT
                                                settings.useWideViewPort = true
                                                settings.loadWithOverviewMode = true
                                            }
                                            boringEnabled -> {
                                                settings.userAgentString = com.jusdots.jusbrowse.security.BoringEngine.getFormattedUserAgent(webViewVersion)
                                                settings.useWideViewPort = false
                                                settings.loadWithOverviewMode = false
                                            }
                                            jusFakeEnabled -> {
                                                settings.userAgentString = com.jusdots.jusbrowse.security.FakeModeManager.getUserAgent()
                                                settings.useWideViewPort = false
                                                settings.loadWithOverviewMode = false
                                            }
                                            else -> {
                                                settings.userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Mobile Safari/537.36"
                                                settings.useWideViewPort = false
                                                settings.loadWithOverviewMode = false
                                            }
                                        }

                                    val fpScript = com.jusdots.jusbrowse.security.FakeModeManager.generateFingerprintScript(
                                        webViewVersion = webViewVersion,
                                        defaultEnabled = !jusFakeEnabled && !boringEnabled,
                                        jusFakeEnabled = jusFakeEnabled,
                                        boringEnabled = boringEnabled,
                                        whitelist = protectionWhitelist.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                    )

                                    android.util.Log.d("JusBrowse", "DOCUMENT_START_SCRIPT supported: ${androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.DOCUMENT_START_SCRIPT)}")
                                    android.util.Log.d("JusBrowse", "FP Script mode: jusFake=$jusFakeEnabled boring=$boringEnabled default=${!jusFakeEnabled && !boringEnabled}")

                                    if (androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.DOCUMENT_START_SCRIPT)) {
                                        androidx.webkit.WebViewCompat.addDocumentStartJavaScript(this, fpScript, setOf("*"))
                                    }

                                    addJavascriptInterface(
                                        com.jusdots.jusbrowse.security.FakeModeManager.PrivacyBridge(),
                                        com.jusdots.jusbrowse.security.FakeModeManager.bridgeNamePrivacy
                                    )

                                    setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
                                        val validation = com.jusdots.jusbrowse.security.DownloadValidator.validateDownload(
                                            url, userAgent, contentDisposition, mimeType, contentLength
                                        )
                                        pendingDownloadUrl = url
                                        pendingDownloadInfo = validation
                                        showDownloadWarning = true
                                    }

                                    settings.safeBrowsingEnabled = true
                                    settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
                                    CookieManager.getInstance().setAcceptThirdPartyCookies(this, false)
                                    settings.mediaPlaybackRequiresUserGesture = !multiMediaPlaybackEnabled

                                    if (tab.isPrivate) {
                                        settings.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                                    }

                                    setOnLongClickListener { v ->
                                        val hitTest = (v as WebView).hitTestResult
                                        if (hitTest.type == WebView.HitTestResult.IMAGE_TYPE ||
                                            hitTest.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
                                        ) {
                                            val url = hitTest.extra
                                            if (url != null) {
                                                val item = ClipData.Item(url)
                                                val data = ClipData("Image", arrayOf("text/plain"), item)
                                                val shadow = View.DragShadowBuilder(v)
                                                if (Build.VERSION.SDK_INT >= 24) {
                                                    v.startDragAndDrop(data, shadow, null, 0)
                                                } else {
                                                    @Suppress("DEPRECATION")
                                                    v.startDrag(data, shadow, null, 0)
                                                }
                                                isDragging = true
                                                true
                                            } else false
                                        } else false
                                    }

                                    webViewClient = object : WebViewClient() {
                                        override fun shouldInterceptRequest(
                                            view: WebView?,
                                            request: WebResourceRequest?
                                        ): WebResourceResponse? {
                                            val url = request?.url?.toString() ?: return null

                                            // Content blocking + HTTPS-only + header surgery all handled by NetworkSurgeon with Neutral Responses
                                            val whitelistedDomains = protectionWhitelist.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                            
                                            // DETERMINING UA WITHOUT CALLING VIEW.SETTINGS (THREAD SAFETY)
                                            val currentUA = when {
                                                tab.isDesktopMode -> com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel.DESKTOP_USER_AGENT
                                                boringEnabled -> com.jusdots.jusbrowse.security.BoringEngine.getFormattedUserAgent(webViewVersion)
                                                jusFakeEnabled -> com.jusdots.jusbrowse.security.FakeModeManager.getUserAgent()
                                                else -> null
                                            }

                                            val containerId = if (tab.isPrivate) "private_${tab.id}" else tab.containerId ?: "default"
                                            val blocker = if (adBlockEnabled) viewModel.contentBlocker else null
                                            val surgicallyCleanedResponse = NetworkSurgeon.intercept(request, whitelistedDomains, currentUA, httpsOnly, containerId, blocker)
                                            if (surgicallyCleanedResponse != null) {
                                                return surgicallyCleanedResponse
                                            }

                                            return super.shouldInterceptRequest(view, request)
                                        }

                                        override fun onPageStarted(
                                            view: WebView?,
                                            url: String?,
                                            favicon: android.graphics.Bitmap?
                                        ) {
                                            super.onPageStarted(view, url, favicon)
                                            if (!androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.DOCUMENT_START_SCRIPT)) {
                                                view?.evaluateJavascript(com.jusdots.jusbrowse.security.FakeModeManager.generateFingerprintScript(
                                                    webViewVersion = androidx.webkit.WebViewCompat.getCurrentWebViewPackage(context)?.versionName ?: "133.0.0.0",
                                                    defaultEnabled = !jusFakeEnabled && !boringEnabled,
                                                    jusFakeEnabled = jusFakeEnabled,
                                                    boringEnabled = boringEnabled,
                                                    whitelist = protectionWhitelist.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                                ), null)
                                            }
                                            com.jusdots.jusbrowse.security.SuspicionScorer.reset()

                                            if (!tab.isPrivate) {
                                                view?.settings?.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                                            }

                                            viewModel.updateTabLoadingState(tabIndex, true)
                                            url?.let {
                                                if (it != tab.url) {
                                                    viewModel.navigateToUrlForIndex(tabIndex, it)
                                                }
                                            }
                                        }

                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            if (viewModel.isBoomerMode.value) {
                                                view?.evaluateJavascript(
                                                    com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel.ENABLE_BOOMER_MODE_SCRIPT,
                                                    null
                                                )
                                            } else {
                                                view?.evaluateJavascript(
                                                    com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel.DISABLE_BOOMER_MODE_SCRIPT,
                                                    null
                                                )
                                            }
                                            viewModel.updateTabLoadingState(tabIndex, false)
                                            view?.title?.let { viewModel.updateTabTitle(tabIndex, it) }
                                            viewModel.updateTabNavigationState(
                                                tabIndex,
                                                view?.canGoBack() == true,
                                                view?.canGoForward() == true
                                            )
                                        }
                                    }
                                }
                            }.also { webView ->
                                webView.webChromeClient = com.jusdots.jusbrowse.security.SecureWebChromeClient(
                                    onPermissionRequest = { /* Handle permission requests */ },
                                    onProgressChanged = { progress ->
                                        viewModel.updateTabLoadingState(tabIndex, true, progress.toFloat())
                                    },
                                    onShowCustomViewCallback = { view, callback ->
                                        fullscreenView = view
                                        fullscreenCallback = callback
                                    },
                                    onHideCustomViewCallback = {
                                        fullscreenView = null
                                        fullscreenCallback = null
                                    }
                                ).apply {
                                    onShowFileChooser = { _, callback, params ->
                                        if (params == null) {
                                            false
                                        } else {
                                            filePathCallback = callback
                                            pendingFileChooserParams = params

                                            val cameraPermission = ContextCompat.checkSelfPermission(
                                                context, android.Manifest.permission.CAMERA
                                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                                            val storagePermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                                ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                            } else {
                                                ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                            }

                                            if (cameraPermission && storagePermission) {
                                                launchChooser(context, params, true)
                                            } else {
                                                val perms = mutableListOf(android.Manifest.permission.CAMERA)
                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                                    perms.add(android.Manifest.permission.READ_MEDIA_IMAGES)
                                                } else {
                                                    perms.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                                }
                                                mediaPermissionLauncher.launch(perms.toTypedArray())
                                            }
                                            true
                                        }
                                    }
                                }

                                viewModel.registerWebView(tab.id, webView)
                                val headers = com.jusdots.jusbrowse.security.FakeModeManager.getHeaders()
                                if (headers.isNotEmpty()) {
                                    webView.loadUrl(tab.url, headers)
                                } else {
                                    webView.loadUrl(tab.url)
                                }
                            }
                        },
                        update = { webView ->
                            if (isBoomerMode) {
                                webView.evaluateJavascript(
                                    com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel.ENABLE_BOOMER_MODE_SCRIPT,
                                    null
                                )
                            } else {
                                webView.evaluateJavascript(
                                    com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel.DISABLE_BOOMER_MODE_SCRIPT,
                                    null
                                )
                            }

                            // Advanced Privacy: Handle dynamic UA/Metadata updates if engine state changed
                            val wvVersion = androidx.webkit.WebViewCompat.getCurrentWebViewPackage(webView.context)?.versionName ?: "133.0.0.0"
                            val targetUA = when {
                                tab.isDesktopMode -> com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel.DESKTOP_USER_AGENT
                                boringEnabled -> com.jusdots.jusbrowse.security.BoringEngine.getFormattedUserAgent(wvVersion)
                                jusFakeEnabled -> com.jusdots.jusbrowse.security.FakeModeManager.getUserAgent()
                                else -> "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Mobile Safari/537.36"
                            }

                            if (webView.settings.userAgentString != targetUA) {
                                webView.settings.userAgentString = targetUA
                            }

                            webView.onResume()
                        },
                        onRelease = { webView ->
                            if (!multiMediaPlaybackEnabled) {
                                webView.onPause()
                            }
                        }
                    )
                }
            } else {
                // ── Immersive Start Page ────────────────────────────────────
                StartPageHero()
            }
        }

        // Dismiss Scrim
        if (isPillExpanded || showPillMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (showPillMenu) Color.Black.copy(alpha = 0.7f) else Color.Transparent)
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) {
                        isPillExpanded = false
                        showPillMenu = false
                        focusManager.clearFocus()
                    }
            )
        }


        // Sticker Content Layer
        stickerContent()
     var urlText by remember(tab?.url) { mutableStateOf(tab?.url ?: "") }
    var isPillHovered by remember { mutableStateOf(false) }
    
    val animatedProgress by animateFloatAsState(
        targetValue = if (tab?.isLoading == true) tab.progress / 100f else 0f,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "pillProgress"
    )

    // 2. Floating Pill Bar (Bottom)
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .imePadding()
            .padding(bottom = 90.dp)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Enter -> isPillHovered = true
                            PointerEventType.Exit -> isPillHovered = false
                        }
                    }
                }
            }
            .offset {
                    androidx.compose.ui.unit.IntOffset(
                        pillOffset.value.roundToInt(),
                        (bottomBarOffsetHeightPxState + pillVerticalOffset.value).roundToInt()
                    )
                }
                .width(animatedPillWidthDp)
                .height(animatedPillHeight)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            Color.White.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
                        )
                    ),
                    shape = RoundedCornerShape(animatedCornerRadius)
                )
                .clip(RoundedCornerShape(animatedCornerRadius))
                .drawWithCache {
                    val path = Path().apply {
                        addRoundRect(
                            androidx.compose.ui.geometry.RoundRect(
                                rect = Rect(Offset.Zero, size),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(animatedCornerRadius.toPx())
                            )
                        )
                    }
                    val pathMeasure = android.graphics.PathMeasure()
                    val androidPath = path.asAndroidPath()
                    pathMeasure.setPath(androidPath, false)
                    val length = pathMeasure.length

                    onDrawWithContent {
                        drawContent()
                        if (tab?.isLoading == true && animatedProgress > 0f) {
                            val progressStrokeWidth = 7.dp.toPx()
                            val stopDistance = length * animatedProgress
                            val resultPath = android.graphics.Path()
                            pathMeasure.getSegment(0f, stopDistance, resultPath, true)
                            
                            drawIntoCanvas { canvas ->
                                canvas.nativeCanvas.drawPath(
                                    resultPath,
                                    android.graphics.Paint().apply {
                                        color = SecureGreen.toArgb()
                                        style = android.graphics.Paint.Style.STROKE
                                        this.strokeWidth = progressStrokeWidth
                                        strokeCap = android.graphics.Paint.Cap.ROUND
                                        isAntiAlias = true
                                        // Even more bold glow
                                        setShadowLayer(
                                            20f, 0f, 0f, SecureGreen.toArgb()
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                .pointerInput(showPillMenu, isPillExpanded) {
                    if (showPillMenu) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    pillVerticalOffset.snapTo(pillVerticalOffset.value + dragAmount.y)
                                }
                            },
                            onDragEnd = {
                                if (pillVerticalOffset.value > 80f) {
                                    showPillMenu = false
                                }
                                scope.launch {
                                    pillVerticalOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                            },
                            onDragCancel = {
                                scope.launch { pillVerticalOffset.animateTo(0f, spring()) }
                            }
                        )
                    } else if (!isPillExpanded) {
                        detectDragGestures(
                            onDragEnd = {
                                val hOffset = pillOffset.value
                                val vOffset = pillVerticalOffset.value
                                val threshold = 150f
                                scope.launch {
                                    if (hOffset > threshold) {
                                        viewModel.getWebView(tab?.id ?: "")?.let {
                                            if (it.canGoBack()) it.goBack()
                                        }
                                    } else if (hOffset < -threshold) {
                                        viewModel.getWebView(tab?.id ?: "")?.let {
                                            if (it.canGoForward()) it.goForward()
                                        }
                                    }
                                    if (vOffset < -100f && !showPillMenu) {
                                        showPillMenu = true
                                    } else if (vOffset > 100f && showPillMenu) {
                                        showPillMenu = false
                                    }
                                    pillOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    pillVerticalOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                            },
                            onDragCancel = {
                                scope.launch {
                                    pillOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    pillVerticalOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                            },
                            onDrag = { change: PointerInputChange, dragAmount: Offset ->
                                change.consume()
                                scope.launch {
                                    pillOffset.snapTo(pillOffset.value + (dragAmount.x * 0.6f))
                                    if (dragAmount.y < 0) {
                                        pillVerticalOffset.snapTo(pillVerticalOffset.value + (dragAmount.y * 0.4f))
                                    } else {
                                        if (dragAmount.y > 10f) {
                                            viewModel.triggerHideBottomBar()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
                .then(
                    if (showPillMenu) {
                        Modifier.shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(animatedCornerRadius),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    } else if (!isPillExpanded) {
                        Modifier.combinedClickable(
                            onClick = { isPillExpanded = true },
                            onLongClick = { viewModel.getWebView(tab?.id ?: "")?.reload() }
                        )
                    } else Modifier
                )
        ) {
            // ── Glass Fill Layers ────────────────────────────────────────────
            // Layer 1: frosted-glass tint base
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        if (showPillMenu)
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
                        else if (isPillHovered)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                        else
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                    )
            )
            // Layer 2: gradient colour wash
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = if (isWhitelisted) {
                                listOf(
                                    SecureGreen.copy(alpha = if (isPillHovered) 0.15f else 0.10f),
                                    SecureGreen.copy(alpha = 0.04f)
                                )
                            } else {
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = if (isPillHovered) 0.12f else 0.07f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.03f)
                                )
                            }
                        )
                    )
            )

            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = showPillMenu,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220, delayMillis = 90)) + scaleIn(initialScale = 0.92f) togetherWith
                        fadeOut(animationSpec = tween(90))
                    },
                    label = "pillContent"
                ) { isMenuOpen ->
                    if (isMenuOpen) {
                        // ── JusBrowse Menu Content ──────────────────────────────────
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            var showContainers by remember { mutableStateOf(false) }

                            // Drag Handle for Swipe Context
                            Box(
                                modifier = Modifier
                                    .size(width = 32.dp, height = 4.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = if (showContainers) "Select Container" else "JusBrowse Menu",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            AnimatedContent(
                                targetState = showContainers,
                                transitionSpec = {
                                    (fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) +
                                            scaleIn(initialScale = 0.92f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)))
                                        .togetherWith(fadeOut() + scaleOut(targetScale = 0.92f))
                                },
                                label = "menuContent"
                            ) { isShowingContainers ->
                                if (isShowingContainers) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        androidx.compose.foundation.layout.FlowRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalArrangement = Arrangement.spacedBy(16.dp),
                                            maxItemsInEachRow = 3
                                        ) {
                                            com.jusdots.jusbrowse.security.ContainerManager.AVAILABLE_CONTAINERS.forEachIndexed { index, containerId ->
                                                val name = com.jusdots.jusbrowse.security.ContainerManager.getContainerName(containerId)
                                                val color = when (containerId) {
                                                    "personal" -> Color(0xFF4CAF50)
                                                    "work" -> Color(0xFF2196F3)
                                                    "banking" -> Color(0xFFFFC107)
                                                    "shopping" -> Color(0xFFE91E63)
                                                    else -> MaterialTheme.colorScheme.primary
                                                }
                                                var isContainerHovered by remember { mutableStateOf(false) }
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier
                                                        .width(80.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(if (isContainerHovered) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                                                        .pointerInput(Unit) {
                                                            awaitPointerEventScope {
                                                                while (true) {
                                                                    val event = awaitPointerEvent()
                                                                    when (event.type) {
                                                                        PointerEventType.Enter -> isContainerHovered = true
                                                                        PointerEventType.Exit -> isContainerHovered = false
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        .combinedClickable(onClick = {
                                                            viewModel.createNewTab(containerId = containerId)
                                                            showPillMenu = false
                                                        })
                                                        .padding(8.dp)
                                                ) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = color.copy(alpha = 0.2f),
                                                        modifier = Modifier.size(48.dp),
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Icon(Icons.Default.Layers, null, tint = color, modifier = Modifier.size(24.dp))
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = name,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        textAlign = TextAlign.Center,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        TextButton(onClick = { showContainers = false }) {
                                            Icon(Icons.Default.ArrowBack, null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Back to Menu")
                                        }
                                    }
                                } else {
                                    val currentDomain = try { android.net.Uri.parse(tab?.url ?: "").host ?: "" } catch (e: Exception) { "" }
                                    
                                     val menuItems = listOf<Triple<Any, String, () -> Unit>>(
                                        Triple(Icons.Default.Refresh, "Refresh", { viewModel.getWebView(tab?.id ?: "")?.reload(); showPillMenu = false }),
                                        Triple(
                                            "LOGO",
                                            if (isWhitelisted) "Unwhitelist" else "Whitelist",
                                            {
                                                if (currentDomain.isNotEmpty()) {
                                                    viewModel.toggleDomainWhitelist(currentDomain)
                                                }
                                                showPillMenu = false
                                            }
                                        ),
                                        Triple(Icons.Default.History, "History", { viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.HISTORY); showPillMenu = false }),
                                        Triple(Icons.Default.Download, "Downloads", { viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.DOWNLOADS); showPillMenu = false }),
                                        Triple(Icons.Default.PhotoLibrary, "Gallery", { onOpenAirlockGallery(); showPillMenu = false }),
                                        Triple(Icons.Default.VpnKey, "Private", { viewModel.createNewTab(isPrivate = true); showPillMenu = false }),
                                        Triple(Icons.Default.Assignment, "Trackers", { showTrackerDetails = true; showPillMenu = false }),
                                        Triple(Icons.Default.Layers, "Container", { showContainers = true }),
                                        Triple(Icons.Default.Settings, "Settings", { viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.SETTINGS); showPillMenu = false }),
                                        Triple(Icons.Default.Warning, "Boomer", { viewModel.toggleBoomerMode(); showPillMenu = false })
                                    )

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        androidx.compose.foundation.layout.FlowRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalArrangement = Arrangement.spacedBy(20.dp),
                                            maxItemsInEachRow = 3
                                        ) {
                                            menuItems.forEachIndexed { index, item ->
                                                var visible by remember { mutableStateOf(false) }
                                                LaunchedEffect(Unit) {
                                                    visible = true
                                                }
                                                AnimatedVisibility(
                                                    visible = visible,
                                                    enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                                                    exit = scaleOut() + fadeOut()
                                                ) {
                                                    var isItemHovered by remember { mutableStateOf(false) }
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        modifier = Modifier
                                                            .width(80.dp)
                                                            .clip(RoundedCornerShape(16.dp))
                                                            .background(if (isItemHovered) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f) else Color.Transparent)
                                                            .pointerInput(Unit) {
                                                                awaitPointerEventScope {
                                                                    while (true) {
                                                                        val event = awaitPointerEvent()
                                                                        when (event.type) {
                                                                            PointerEventType.Enter -> isItemHovered = true
                                                                            PointerEventType.Exit -> isItemHovered = false
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            .combinedClickable(onClick = { item.third() })
                                                            .padding(8.dp)
                                                    ) {
                                                        val iconBgColor = Color.White.copy(alpha = 0.05f)
                                                        val iconTintColor = when {
                                                            item.second == "Boomer" && isBoomerMode ->
                                                                MaterialTheme.colorScheme.error
                                                            item.second.contains("Whitelist") && isWhitelisted ->
                                                                SecureGreen
                                                            else ->
                                                                MaterialTheme.colorScheme.primary
                                                        }
                                                        Surface(
                                                            shape = CircleShape,
                                                            color = iconBgColor,
                                                            modifier = Modifier.size(56.dp),
                                                            border = androidx.compose.foundation.BorderStroke(1.dp, iconTintColor.copy(alpha = 0.25f))
                                                        ) {
                                                            Box(contentAlignment = Alignment.Center) {
                                                                if (item.first == "LOGO") {
                                                                    Icon(painterResource(R.drawable.ic_launcher_foreground), null, tint = iconTintColor, modifier = Modifier.size(32.dp))
                                                                } else {
                                                                    Icon(item.first as ImageVector, null, tint = iconTintColor, modifier = Modifier.size(28.dp))
                                                                }
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Text(
                                                            text = item.second,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            textAlign = TextAlign.Center
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // ── Address Bar / URL Content ───────────────────────────
                        if (isPillExpanded) {
                            // Expanded Row Logic
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.55f))
                                        .padding(horizontal = 14.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    BasicTextField(
                                        value = urlTextFieldValue,
                                        onValueChange = { urlTextFieldValue = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .focusRequester(focusRequester)
                                            .onFocusChanged { focusState ->
                                                if (focusState.isFocused && !hasGainedFocus) {
                                                    val currentText = urlTextFieldValue.text
                                                    urlTextFieldValue = if (currentText == "about:blank") {
                                                        TextFieldValue("", selection = TextRange.Zero)
                                                    } else {
                                                        TextFieldValue(
                                                            text = currentText,
                                                            selection = TextRange(0, currentText.length)
                                                        )
                                                    }
                                                    hasGainedFocus = true
                                                }
                                            },
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Normal
                                        ),
                                        keyboardOptions = KeyboardOptions(
                                            imeAction = ImeAction.Search,
                                            autoCorrect = false,
                                            capitalization = KeyboardCapitalization.None,
                                            keyboardType = KeyboardType.Uri
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onSearch = {
                                                val query = urlTextFieldValue.text.trim()
                                                if (query.isNotEmpty() && tab != null) {
                                                    val targetUrl = if (viewModel.isUrlQuery(query)) viewModel.getSearchUrl(query, searchEngine, customSearchEngineUrl) else query
                                                    viewModel.getWebView(tab.id)?.loadUrl(targetUrl)
                                                    viewModel.navigateToUrlForIndex(tabIndex, targetUrl)
                                                    isPillExpanded = false
                                                    focusManager.clearFocus()
                                                }
                                            }
                                        ),
                                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                                    )
                                }
                                if (urlTextFieldValue.text.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    FilledTonalIconButton(
                                        onClick = { urlTextFieldValue = TextFieldValue("") },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(Icons.Default.Clear, null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        } else {
                            // Collapsed Row Logic
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 18.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier.clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { showTrackerDetails = true }
                                    ) {
                                        Icon(
                                            imageVector = if (tab?.isPrivate == true) Icons.Default.VpnKey else if (isWhitelisted) Icons.Default.VerifiedUser else Icons.Default.Lock,
                                            contentDescription = "Privacy Status",
                                            tint = if (isWhitelisted) SecureGreen else if (trackers.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        if (trackers.isNotEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = 4.dp, y = (-4).dp)
                                                    .background(MaterialTheme.colorScheme.error, CircleShape)
                                                    .border(1.dp, Color.White, CircleShape)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = urlTextFieldValue.text,
                                        color = Color.White,
                                        modifier = Modifier
                                            .graphicsLayer(
                                                compositingStrategy = CompositingStrategy.Offscreen,
                                                blendMode = BlendMode.Difference
                                            ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                        }
                    }
                }
            }
        }


        // Drop Zone Overlay
        if (isDragging) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 96.dp, end = 16.dp)
                    .size(120.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Drop",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Drop to\nDownload",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                AndroidView(
                    factory = { ctx ->
                        FrameLayout(ctx).apply {
                            setOnDragListener { _, event ->
                                when (event.action) {
                                    DragEvent.ACTION_DRAG_STARTED -> true
                                    DragEvent.ACTION_DRAG_ENTERED -> true
                                    DragEvent.ACTION_DRAG_EXITED -> true
                                    DragEvent.ACTION_DROP -> {
                                        val clipData = event.clipData
                                        if (clipData != null && clipData.itemCount > 0) {
                                            val url = clipData.getItemAt(0).text.toString()
                                            val fileName = "download_${System.currentTimeMillis()}.jpg"
                                            viewModel.addDownload(fileName, url, "Downloads/$fileName", 0L)
                                            android.widget.Toast.makeText(ctx, "Download started", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                        // FIX #10: Reset isDragging on DROP as well as ENDED
                                        isDragging = false
                                        true
                                    }
                                    DragEvent.ACTION_DRAG_ENDED -> {
                                        // FIX #10: Always reset isDragging when drag ends,
                                        // including cancelled drags, so the drop zone never
                                        // gets stuck visible.
                                        isDragging = false
                                        true
                                    }
                                    else -> false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Download Confirmation Dialog
        // FIX #11: Clear pendingDownloadInfo and pendingDownloadUrl on every dismiss path.
        //
        // FIX smart-cast: Kotlin cannot smart-cast delegated `by remember` properties even
        // after a null-check — the delegate could theoretically change between reads. Snapshot
        // to a local val first; the local val IS smart-castable and safe inside composable lambdas.
        val localDownloadInfo = pendingDownloadInfo
        if (showDownloadWarning && localDownloadInfo != null) {

            fun clearDownloadState() {
                showDownloadWarning = false
                pendingDownloadInfo = null
                pendingDownloadUrl = null
            }

            AlertDialog(
                onDismissRequest = { clearDownloadState() },
                title = { Text("Download File") },
                text = {
                    Column {
                        Text(localDownloadInfo.warningMessage ?: "Do you want to download this file?")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = localDownloadInfo.fileName,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                confirmButton = {
                    Column(horizontalAlignment = Alignment.End) {
                        Button(
                            onClick = {
                                pendingDownloadUrl?.let { url ->
                                    viewModel.startDownload(context, url, localDownloadInfo.fileName)
                                }
                                clearDownloadState()
                            }
                        ) {
                            Text("Download")
                        }

                        if (vtApiKey.isNotBlank()) {
                            TextButton(onClick = {
                                pendingDownloadUrl?.let { url ->
                                    viewModel.scanFile(url, "VirusTotal", context)
                                }
                                clearDownloadState()
                            }) {
                                Text("Scan with VirusTotal")
                            }
                        }

                        if (koodousApiKey.isNotBlank()) {
                            TextButton(onClick = {
                                pendingDownloadUrl?.let { url ->
                                    viewModel.scanFile(url, "Koodous", context)
                                }
                                clearDownloadState()
                            }) {
                                Text("Scan with Koodous")
                            }
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { clearDownloadState() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    // Tracker Details Modal

    if (showTrackerDetails && tab != null) {
        val trackerList = viewModel.blockedTrackers[tab.id] ?: emptyList()
        ModalBottomSheet(
            onDismissRequest = { showTrackerDetails = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                val totalHits = viewModel.blockedTrackersCount[tab.id] ?: 0
                Text(
                    text = "Trackers Blocked",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "JusBrowse has blocked $totalHits trackers on this page",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (trackerList.isEmpty()) {
                    Text("No trackers detected on this page")
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(trackerList.size) { index ->
                            val tracker = trackerList[index]
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Block, null, tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = tracker.domain, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Fullscreen overlay (Airlock Style)
    if (fullscreenView != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { fullscreenView!! },
                modifier = Modifier.fillMaxSize()
            )

            // Top Gradient Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp)
            ) {
                // Close Button (Top-Left, Airlock Style)
                IconButton(
                    onClick = { fullscreenCallback?.onCustomViewHidden() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Fullscreen",
                        tint = Color.White
                    )
                }
                
                // Optional: We could add the site title here if we had it easily accessible in this scope
                // for absolute symmetry with AirlockViewer
            }
        }
    }

    // Scan Result Dialog
    if (viewModel.showScanResultDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showScanResultDialog = false },
            title = { Text("Scan Result") },
            text = { Text(viewModel.scanResultMessage) },
            confirmButton = {
                TextButton(onClick = { viewModel.showScanResultDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

/**
 * Immersive start page hero — communicates protection visually through
 * a breathing shield pulse and radial glow, never through alarming text.
 */
@Composable
private fun StartPageHero() {
    val primary = MaterialTheme.colorScheme.primary

    // Infinite pulsing scale for the shield icon
    val pulseAnim = rememberInfiniteTransition(label = "shieldPulse")
    val shieldScale by pulseAnim.animateFloat(
        initialValue = 0.94f,
        targetValue  = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shieldScale"
    )
    val glowAlpha by pulseAnim.animateFloat(
        initialValue = 0.10f,
        targetValue  = 0.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Staggered fade-in for tagline
    var taglineVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(400)
        taglineVisible = true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Radial glow halo behind the shield
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(120.dp)
        ) {
            // Glow ring drawn behind the icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    primary.copy(alpha = glowAlpha),
                                    Color.Transparent
                                )
                            )
                        )
                    }
            )

            // Pulsing JusBrowse logo
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.ic_launcher_playstore)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(68.dp)
                    .scale(shieldScale)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "JusBrowse",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = taglineVisible,
            enter = fadeIn(animationSpec = tween(600)) +
                    slideInVertically(animationSpec = tween(500)) { it / 3 }
        ) {
            Text(
                text = "A product of JusDots.",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Light),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}*/