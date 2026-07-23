package com.jusdots.jusbrowse.ui.components

import android.view.ViewGroup
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.os.Build
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.jusdots.jusbrowse.ui.components.JusBrowseIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.BackHandler
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jusdots.jusbrowse.R
import com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel
import com.jusdots.jusbrowse.ui.theme.*
import androidx.compose.ui.res.painterResource
import android.view.DragEvent
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.ContentBlocking
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.viewinterop.AndroidView
import org.mozilla.geckoview.GeckoView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.graphics.vector.ImageVector
import com.jusdots.jusbrowse.security.GeckoSessionFactory
import com.jusdots.jusbrowse.data.models.BrowserTab
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.core.content.ContextCompat
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddressBarWithGeckoView(
    viewModel: BrowserViewModel,
    tab: BrowserTab?,
    onOpenAirlockGallery: () -> Unit,
    alwaysShowUrl: Boolean = false,
    reduceAnim: Boolean = false,
    forceStatic: Boolean = false,
    pillBottomMarginDp: Int = 90,
    pillCollapsedWidthDp: Int = 260,
    showProgressBar: Boolean = true,
    startPageBranding: String = "full",
    scrimDarkness: String = "normal",
    pillBlurOpacity: Float = 0.7f,
    modifier: Modifier = Modifier,
    stickerContent: @Composable () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val tabIndex = tab?.id?.let { viewModel.tabDescriptors.indexOfFirst { d -> d.id == it } }?.coerceAtLeast(0) ?: 0
    val searchEngine by viewModel.searchEngine.collectAsStateWithLifecycle(initialValue = "DuckDuckGo")
    val customSearchEngineUrl by viewModel.customSearchEngineUrl.collectAsStateWithLifecycle(initialValue = "")
    val protectionWhitelist by viewModel.protectionWhitelist.collectAsStateWithLifecycle(initialValue = "")

    // PILL BAR STATES (Preserved from original)
    var isPillExpanded by remember { mutableStateOf(false) }
    var showPillMenu by remember { mutableStateOf(false) }

    // Local state for the address bar text
    var urlTextFieldValue by remember {
        mutableStateOf(TextFieldValue(tab?.url?.replace("about:blank", "") ?: ""))
    }

    // GeckoSession specific states
    var sessionSecurityInfo by remember { mutableStateOf<GeckoSession.ProgressDelegate.SecurityInformation?>(null) }
    var pageProgress by remember { mutableStateOf(0f) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    
    val errorPageDelegate = remember { com.jusdots.jusbrowse.ui.delegate.StraitErrorPageDelegate() }
    val permissionDelegate = remember {
        com.jusdots.jusbrowse.ui.delegate.StraitPermissionDelegate(
            com.jusdots.jusbrowse.BrowserApplication.database.siteSettingsDao()
        )
    }
    
    // Defers session creation to avoid 6s main thread lock during UI initialization
    val sessionState = remember { mutableStateOf<GeckoSession?>(null) }
    
    LaunchedEffect(tab?.id) {
        if (tab != null) {
            var existing = viewModel.getGeckoSession(tab.id)
            if (existing == null) {
                // Removed 1500ms delay to restore instant tab switching for background tabs
                existing = viewModel.getOrCreateGeckoSession(tab.id, tab.isPrivate, tab.containerId)
                if (tab.url != "about:blank") {
                    existing.loadUri(tab.url)
                }
            }
            sessionState.value = existing
        } else {
            sessionState.value = null
        }
    }
    val session = sessionState.value

    // System Back Handler
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
    var hasGainedFocus by remember { mutableStateOf(false) }


    // Fullscreen state (GeckoView fullscreen delegate)
    var isFullscreen by remember { mutableStateOf(false) }

    // Focus Requester for Search Bar
    val focusRequester = remember { FocusRequester() }

    // Auto-focus when expanded
    LaunchedEffect(isPillExpanded) {
        if (isPillExpanded) {
            hasGainedFocus = false
            kotlinx.coroutines.delay(100)
            focusRequester.requestFocus()
        }
    }

    // Download confirmation states
    var showDownloadWarning by remember { mutableStateOf(false) }
    val vtApiKey by viewModel.virusTotalApiKey.collectAsStateWithLifecycle(initialValue = "")
    val koodousApiKey by viewModel.koodousApiKey.collectAsStateWithLifecycle(initialValue = "")
    var pendingDownloadInfo by remember { mutableStateOf<com.jusdots.jusbrowse.security.DownloadValidator.DownloadValidationResult?>(null) }
    var pendingDownloadUrl by remember { mutableStateOf<String?>(null) }

    var showTrackerDetails by remember { mutableStateOf(false) }
    val trackers = if (tab != null) viewModel.blockedTrackers[tab.id] ?: emptyList() else emptyList()
    val multiMediaPlaybackEnabled by viewModel.multiMediaPlaybackEnabled.collectAsStateWithLifecycle(initialValue = false)
    val isBoomerMode by viewModel.isBoomerMode.collectAsStateWithLifecycle()

    // Whitelist check
    val isWhitelisted = remember(tab?.url, protectionWhitelist) {
        val host = tab?.url?.let { android.net.Uri.parse(it).host } ?: ""
        if (host.isEmpty()) false
        else protectionWhitelist.split(",").map { it.trim() }.any { 
            it.isNotEmpty() && (host == it || host.endsWith(".$it"))
        }
    }

    // Elastic Swipe State (Preserved)
    val pillOffset = remember { Animatable(0f) }
    val pillVerticalOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // File Upload Handling for GeckoView
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraPhotoFile by remember { mutableStateOf<File?>(null) }
    var pendingGeckoFilePrompt by remember { mutableStateOf<org.mozilla.geckoview.GeckoSession.PromptDelegate.FilePrompt?>(null) }
    var geckoPromptResult by remember { mutableStateOf<GeckoResult<org.mozilla.geckoview.GeckoSession.PromptDelegate.PromptResponse>?>(null) }

    val filePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uriList = mutableListOf<Uri>()
        val data = result.data
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            when {
                data?.data != null -> uriList.add(data.data!!)
                data?.clipData != null -> {
                    val clipData = data.clipData!!
                    val count = clipData.itemCount
                    for (i in 0 until count) {
                        uriList.add(clipData.getItemAt(i).uri)
                    }
                }
                else -> {
                    val photo = cameraPhotoFile
                    if (photo != null && photo.exists() && photo.length() > 0) {
                        cameraImageUri?.let { uriList.add(it) }
                    }
                }
            }
        }
        
        val prompt = pendingGeckoFilePrompt
        val geckoresult = geckoPromptResult
        if (uriList.isNotEmpty() && prompt != null && geckoresult != null) {
            geckoresult.complete(prompt.confirm(context, uriList.toTypedArray()))
        } else if (prompt != null && geckoresult != null) {
            geckoresult.complete(prompt.dismiss())
        }
        
        geckoPromptResult = null
        pendingGeckoFilePrompt = null
        cameraImageUri = null
        cameraPhotoFile = null
    }

    val speechLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                urlTextFieldValue = TextFieldValue(matches[0], selection = TextRange(matches[0].length))
                val query = matches[0].trim()
                if (query.isNotEmpty()) {
                    val targetUrl = if (viewModel.isUrlQuery(query)) viewModel.getSearchUrl(query, searchEngine, customSearchEngineUrl) else if (!query.contains("://")) "https://$query" else query
                    viewModel.navigateToUrlForIndex(tabIndex, targetUrl)
                    session?.loadUri(targetUrl)
                    isPillExpanded = false
                    focusManager.clearFocus()
                }
            }
        }
    }

    val hasSpeechRecognizer = remember {
        val pm = context.packageManager
        val voiceIntent = RecognizerIntent.getVoiceDetailsIntent(context)
        pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE) &&
            voiceIntent != null &&
            pm.queryIntentActivities(voiceIntent, 0).isNotEmpty()
    }

    fun launchGeckoChooser(ctx: android.content.Context, prompt: org.mozilla.geckoview.GeckoSession.PromptDelegate.FilePrompt, hasCameraPermission: Boolean) {
        val intentList = mutableListOf<android.content.Intent>()
        if (hasCameraPermission) {
            val takePictureIntent = android.content.Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            val photoFile = File(ctx.filesDir, "upload_captured_${System.currentTimeMillis()}.jpg")
            val uri = androidx.core.content.FileProvider.getUriForFile(
                ctx, "${ctx.packageName}.provider", photoFile
            )
            cameraImageUri = uri
            cameraPhotoFile = photoFile
            takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)
            intentList.add(takePictureIntent)
        }

        val contentSelectionIntent = android.content.Intent(android.content.Intent.ACTION_GET_CONTENT).apply {
            addCategory(android.content.Intent.CATEGORY_OPENABLE)
            val mimeTypes = prompt.mimeTypes
            type = if (mimeTypes?.isNotEmpty() == true) mimeTypes[0] else "*/*"
            if (prompt.type == org.mozilla.geckoview.GeckoSession.PromptDelegate.FilePrompt.Type.MULTIPLE) {
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
            val resultGecko = geckoPromptResult
            if (resultGecko != null && prompt != null) {
                resultGecko.complete(prompt.dismiss())
            }
            geckoPromptResult = null
            pendingGeckoFilePrompt = null
        }
    }

    val mediaPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[android.Manifest.permission.CAMERA] ?: false
        // The prompt details need to be stored separately or kept alive during permission loop.
        // For simplicity, we just launch the chooser.
    }

    // GeckoSession Delegates Setup
    DisposableEffect(session) {
        if (session != null) {
            val progressDelegate = object : GeckoSession.ProgressDelegate {
                override fun onPageStart(session: GeckoSession, url: String) {
                    pageProgress = 0f
                    viewModel.updateTabLoadingState(tabIndex, true)
                }
                override fun onPageStop(geckoSession: GeckoSession, success: Boolean) {
                    pageProgress = 100f
                    viewModel.updateTabLoadingState(tabIndex, false)
                }
                override fun onProgressChange(geckoSession: GeckoSession, progress: Int) {
                    pageProgress = progress.toFloat()
                    viewModel.updateTabLoadingState(tabIndex, true, pageProgress)
                }
                override fun onSecurityChange(geckoSession: GeckoSession, securityInfo: GeckoSession.ProgressDelegate.SecurityInformation) {
                    sessionSecurityInfo = securityInfo
                }
            }

            val navigationDelegate = object : GeckoSession.NavigationDelegate {
                override fun onLocationChange(session: GeckoSession, url: String?, permits: List<GeckoSession.PermissionDelegate.ContentPermission>, hasUserGesture: Boolean) {
                    url?.let {
                        if (it != tab?.url) {
                            if (it == "about:blank" && tab?.url != "about:blank") {
                                // Ignore ghost about:blank navigation when a restored session is created
                                return
                            }
                            viewModel.navigateToUrlForIndex(tabIndex, it)
                        }
                        // Set favicon from domain
                        val host = try { android.net.Uri.parse(it).host } catch (e: Exception) { null }
                        if (host != null) {
                            viewModel.updateTabFavicon(tabIndex, "https://www.google.com/s2/favicons?domain=$host&sz=64")
                        }
                    }
                }
                
                override fun onLoadError(session: GeckoSession, uri: String?, error: org.mozilla.geckoview.WebRequestError): GeckoResult<String>? {
                    return errorPageDelegate.onLoadError(session, uri, error)
                }
                
                override fun onCanGoBack(session: GeckoSession, canBack: Boolean) {
                    canGoBack = canBack
                    viewModel.updateTabNavigationState(tabIndex, canGoBack, canGoForward)
                }

                override fun onCanGoForward(session: GeckoSession, canForward: Boolean) {
                    canGoForward = canForward
                    viewModel.updateTabNavigationState(tabIndex, canGoBack, canGoForward)
                }
            }

            val contentDelegate = object : GeckoSession.ContentDelegate {
                override fun onTitleChange(session: GeckoSession, title: String?) {
                    title?.let { viewModel.updateTabTitle(tabIndex, it) }
                }

                override fun onFullScreen(session: GeckoSession, fullScreen: Boolean) {
                    isFullscreen = fullScreen
                }

                override fun onExternalResponse(session: GeckoSession, response: org.mozilla.geckoview.WebResponse) {
                    val url = response.uri
                    val mimeType = response.headers["Content-Type"] ?: "application/octet-stream"
                    val contentDisposition = response.headers["Content-Disposition"] ?: ""
                    val contentLength = response.headers["Content-Length"]?.toLongOrNull() ?: 0L

                    // Intercept XPI downloads — route to extension installer
                    val isXpi = url.endsWith(".xpi", ignoreCase = true) ||
                        mimeType == "application/x-xpinstall" ||
                        contentDisposition.contains(".xpi", ignoreCase = true)
                    if (isXpi) {
                        viewModel.installExtensionFromUrl(url)
                        return
                    }

                    val validation = com.jusdots.jusbrowse.security.DownloadValidator.validateDownload(
                        url, null, contentDisposition, mimeType, contentLength
                    )
                    pendingDownloadUrl = url
                    pendingDownloadInfo = validation
                    showDownloadWarning = true
                }
            }


            val promptDelegate = object : GeckoSession.PromptDelegate {
                override fun onFilePrompt(
                    session: GeckoSession,
                    prompt: GeckoSession.PromptDelegate.FilePrompt
                ): GeckoResult<GeckoSession.PromptDelegate.PromptResponse> {
                    val promise = GeckoResult<GeckoSession.PromptDelegate.PromptResponse>()
                    geckoPromptResult = promise
                    pendingGeckoFilePrompt = prompt
                    
                    val cameraPermission = ContextCompat.checkSelfPermission(
                        context, android.Manifest.permission.CAMERA
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    
                    val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    } else {
                        ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    }
                    
                    if (cameraPermission && storagePermission) {
                        launchGeckoChooser(context, prompt, true)
                    } else {
                        val perms = mutableListOf(android.Manifest.permission.CAMERA)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            perms.add(android.Manifest.permission.READ_MEDIA_IMAGES)
                        } else {
                            perms.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                        mediaPermissionLauncher.launch(perms.toTypedArray())
                        // Launch chooser without camera if pending permission delays promise
                        launchGeckoChooser(context, prompt, false)
                    }
                    
                    return promise
                }
            }

            session.progressDelegate = progressDelegate
            session.navigationDelegate = navigationDelegate
            session.contentDelegate = contentDelegate
            session.promptDelegate = promptDelegate
            session.permissionDelegate = permissionDelegate
        }
        onDispose {
            session?.progressDelegate = null
            session?.navigationDelegate = null
            session?.contentDelegate = null
            session?.permissionDelegate = null
        }
    }

    // Sync offsets with global reveal
    LaunchedEffect(Unit) {
        viewModel.revealBottomBarEvent.collectLatest {
            scope.launch {
                pillOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                pillVerticalOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }
        }
    }

    // Single normalized animation progress for the pill.
    // Level 0 = collapsed, 1 = expanded (width only), 2 = menu (height + radius).
    // One animation clock instead of three independent animateDpAsState calls.
    val pillTargetLevel: Float = when {
        showPillMenu -> 2f
        isPillExpanded -> 1f
        else -> 0f
    }
    val snapAnim = forceStatic || reduceAnim
    val pillLevel by animateFloatAsState(
        targetValue = pillTargetLevel,
        animationSpec = if (snapAnim) tween<Float>(0) else spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "pillLevel"
    )

    val collapsedWidth = pillCollapsedWidthDp.dp
    val widthProgress = pillLevel.coerceIn(0f, 1f)
    val menuProgress = ((pillLevel - 1f) / 1f).coerceIn(0f, 1f)

    val animatedPillWidthDp = collapsedWidth + (360.dp - collapsedWidth) * widthProgress
    val animatedPillHeight = if (pillLevel <= 1f) 56.dp else 56.dp + (580.dp - 56.dp) * menuProgress
    val animatedCornerRadius = if (pillLevel <= 1f) 28.dp else 28.dp + (32.dp - 28.dp) * menuProgress

    val bottomBarHeightPx = with(androidx.compose.ui.platform.LocalDensity.current) { 200.dp.toPx() }
    val bottomBarOffsetHeightPxState by viewModel.bottomBarOffsetHeightPx.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .let { m ->
                if (forceStatic) m else {
                    val nestedScrollConnection = remember(isPillExpanded) {
                        object : NestedScrollConnection {
                            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                                if (isPillExpanded) return Offset.Zero
                                val delta = available.y
                                val newOffset = bottomBarOffsetHeightPxState + (-delta)
                                viewModel.updateBottomBarOffset(newOffset.coerceIn(0f, bottomBarHeightPx))
                                return Offset.Zero
                            }
                            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                                if (available.y > 10f && bottomBarOffsetHeightPxState > 0f) {
                                    viewModel.triggerRevealBottomBar()
                                }
                                return Offset.Zero
                            }
                        }
                    }
                    m
                        .nestedScroll(nestedScrollConnection)
                        .pointerInput(session) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Final)
                                    val nativeEvent = event.motionEvent

                                    if (event.type == PointerEventType.Scroll && !isPillExpanded) {
                                        val delta = event.changes.first().scrollDelta
                                        if (delta.x > 2.0f) {
                                            if (canGoBack) session?.goBack()
                                        } else if (delta.x < -2.0f) {
                                            if (canGoForward) session?.goForward()
                                        }
                                    } else if (nativeEvent != null && event.type == PointerEventType.Press) {
                                        if (nativeEvent.isButtonPressed(MotionEvent.BUTTON_BACK)) {
                                            if (canGoBack) session?.goBack()
                                        } else if (nativeEvent.isButtonPressed(MotionEvent.BUTTON_FORWARD)) {
                                            if (canGoForward) session?.goForward()
                                        }
                                    }
                                }
                            }
                        }
                }
            }
    ) {
        // 1. GeckoView Content Layer
        Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            if (tab != null && tab.url != "about:blank") {
                key(tab.id) {
                    if (session != null) {
                        val density = androidx.compose.ui.platform.LocalDensity.current
                        val swipeZonePx = with(density) { 120.dp.toPx() }
                        GeckoWebView(
                            session = session,
                            modifier = Modifier.fillMaxSize(),
                            onViewCreated = { geckoView ->
                                if (!forceStatic) {
                                    var touchStartY = -1f
                                    var swipeDetected = false
                                    geckoView.setOnTouchListener { v, event ->
                                        when (event.action) {
                                            MotionEvent.ACTION_DOWN -> {
                                                val h = v.height.toFloat()
                                                if (event.y > h - swipeZonePx) {
                                                    touchStartY = event.y
                                                    swipeDetected = false
                                                } else {
                                                    touchStartY = -1f
                                                }
                                                false
                                            }
                                            MotionEvent.ACTION_MOVE -> {
                                                if (touchStartY >= 0f && !swipeDetected &&
                                                    event.y - touchStartY < -30f
                                                ) {
                                                    swipeDetected = true
                                                    viewModel.triggerRevealBottomBar()
                                                }
                                                false
                                            }
                                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                                touchStartY = -1f
                                                swipeDetected = false
                                                false
                                            }
                                            else -> false
                                        }
                                    }
                                }
                            }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(36.dp),
                                color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    StartPageHero(startPageBranding)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .align(Alignment.BottomCenter)
                            .pointerInput(Unit) {
                                detectTapGestures { viewModel.triggerRevealBottomBar() }
                            }
                    )
                }
            }
        }

        // Dismiss Scrim
        val scrimAlpha = when (scrimDarkness) {
            "light" -> 0.4f
            "dark" -> 0.9f
            else -> 0.7f
        }
        if (isPillExpanded || showPillMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (showPillMenu) Color.Black.copy(alpha = scrimAlpha) else Color.Transparent)
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
        
        // --- DIALOGS ---
        // Download Confirmation Dialog with Scan buttons
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
                                pendingDownloadUrl?.let { url: String ->
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

        // --- END DIALOGS ---


        stickerContent()

        var isPillHovered by remember { mutableStateOf(false) }
        val animatedProgress by animateFloatAsState(
            targetValue = if (tab?.isLoading == true) pageProgress / 100f else 0f,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
            label = "pillProgress"
        )

        // 2. Floating Pill Bar (Bottom) - PRESERVED STYLING
        val primaryColor = MaterialTheme.colorScheme.primary
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .imePadding()
                .padding(bottom = pillBottomMarginDp.dp)
                .offset {
                    androidx.compose.ui.unit.IntOffset(
                        pillOffset.value.roundToInt(),
                        (bottomBarOffsetHeightPxState + pillVerticalOffset.value).roundToInt()
                    )
                }
                .width(animatedPillWidthDp)
                .height(animatedPillHeight)
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
                            RoundRect(
                                rect = Rect(Offset.Zero, size),
                                cornerRadius = CornerRadius(animatedCornerRadius.toPx())
                            )
                        )
                    }
                    val pathMeasure = android.graphics.PathMeasure()
                    val androidPath = path.asAndroidPath()
                    pathMeasure.setPath(androidPath, false)
                    val length = pathMeasure.length

                    val paint = android.graphics.Paint().apply {
                        style = android.graphics.Paint.Style.STROKE
                        strokeCap = android.graphics.Paint.Cap.ROUND
                        isAntiAlias = false
                    }

                    onDrawWithContent {
                        drawContent()
                        if (tab?.isLoading == true && animatedProgress > 0f && showProgressBar) {
                            val progressStrokeWidth = 7.dp.toPx()
                            val stopDistance = length * animatedProgress
                            val resultPath = android.graphics.Path()
                            pathMeasure.getSegment(0f, stopDistance, resultPath, true)
                            
                            paint.color = primaryColor.toArgb()
                            paint.strokeWidth = progressStrokeWidth
                            drawIntoCanvas { canvas ->
                                canvas.nativeCanvas.drawPath(resultPath, paint)
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
                                if (pillVerticalOffset.value > 80f) showPillMenu = false
                                scope.launch {
                                    pillVerticalOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                            }
                        )
                    } else if (!isPillExpanded) {
                        detectDragGestures(
                            onDragEnd = {
                                val hOffset = pillOffset.value
                                val vOffset = pillVerticalOffset.value
                                val threshold = 150f
                                scope.launch {
                                    if (hOffset > threshold) session?.goBack()
                                    else if (hOffset < -threshold) session?.goForward()
                                    
                                    if (vOffset < -100f && !showPillMenu) showPillMenu = true
                                    
                                    pillOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    pillVerticalOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    pillOffset.snapTo(pillOffset.value + (dragAmount.x * 0.6f))
                                    if (dragAmount.y < 0) {
                                        pillVerticalOffset.snapTo(pillVerticalOffset.value + (dragAmount.y * 0.4f))
                                    } else if (dragAmount.y > 10f) {
                                        viewModel.triggerHideBottomBar()
                                    }
                                }
                            }
                        )
                    }
                }
                .then(
                    if (showPillMenu) {
                        Modifier.shadow(elevation = 20.dp, shape = RoundedCornerShape(animatedCornerRadius))
                    } else if (!isPillExpanded) {
                        Modifier.combinedClickable(
                            onClick = { isPillExpanded = true },
                            onLongClick = { session?.reload() }
                        )
                    } else Modifier
                )
        ) {
            // Glass fill — tinted surface + highlight gradient
            val glassSurfaceAlpha = when {
                showPillMenu -> (pillBlurOpacity + 0.26f).coerceAtMost(1f)
                isPillHovered -> (pillBlurOpacity - 0.20f).coerceAtLeast(0.1f)
                else -> pillBlurOpacity
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        if (showPillMenu) MaterialTheme.colorScheme.surface.copy(alpha = (pillBlurOpacity + 0.26f).coerceAtMost(1f))
                        else if (isPillHovered) MaterialTheme.colorScheme.primaryContainer.copy(alpha = (pillBlurOpacity - 0.20f).coerceAtLeast(0.1f))
                        else MaterialTheme.colorScheme.surface.copy(alpha = pillBlurOpacity)
                    )
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = if (isWhitelisted) {
                                listOf(SecureGreen.copy(alpha = if (isPillHovered) 0.20f else 0.12f), SecureGreen.copy(alpha = 0.06f))
                            } else {
                                listOf(MaterialTheme.colorScheme.primary.copy(alpha = if (isPillHovered) 0.14f else 0.09f), MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f))
                            }
                        )
                    )
            )

            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = showPillMenu,
                    transitionSpec = {
                        if (reduceAnim) {
                            fadeIn(tween(0)) togetherWith fadeOut(tween(0))
                        } else {
                            fadeIn(animationSpec = tween(220, delayMillis = 90)) + scaleIn(initialScale = 0.92f) togetherWith
                            fadeOut(animationSpec = tween(90))
                        }
                    },
                    label = "pillContent"
                ) { isMenuOpen ->
                    if (isMenuOpen) {
                        // ── Full JusBrowse Menu ──
                        Column(
                            modifier = Modifier.fillMaxSize().padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            var showContainers by remember { mutableStateOf(false) }
                            val menuSession = tab?.id?.let { viewModel.getGeckoSession(it) }

                            Box(modifier = Modifier.size(width = 32.dp, height = 4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (showContainers) "Select Container" else "JusBrowse Menu",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            AnimatedContent(
                                targetState = showContainers,
                                transitionSpec = {
                                    (fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) + scaleIn(initialScale = 0.92f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)))
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
                                            com.jusdots.jusbrowse.security.ContainerManager.AVAILABLE_CONTAINERS.forEach { containerId ->
                                                val name = com.jusdots.jusbrowse.security.ContainerManager.getContainerName(containerId)
                                                val color = when (containerId) {
                                                    "personal" -> ContainerPersonal
                                                    "work" -> ContainerWork
                                                    "banking" -> ContainerBanking
                                                    "shopping" -> ContainerShopping
                                                    else -> MaterialTheme.colorScheme.primary
                                                }
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier.width(80.dp).clip(RoundedCornerShape(16.dp))
                                                        .combinedClickable(onClick = { viewModel.createNewTab(containerId = containerId); showPillMenu = false })
                                                        .padding(8.dp)
                                                ) {
                                                    Surface(shape = CircleShape, color = color.copy(alpha = 0.2f), modifier = Modifier.size(48.dp), border = BorderStroke(1.dp, color.copy(alpha = 0.4f))) {
                                                        Box(contentAlignment = Alignment.Center) { Icon(JusBrowseIcons.Layers, null, tint = color, modifier = Modifier.size(24.dp)) }
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(text = name, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        TextButton(onClick = { showContainers = false }) {
                                            Icon(JusBrowseIcons.ArrowBack, null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Back to Menu")
                                        }
                                    }
                                } else {
                                    val currentDomain = try { android.net.Uri.parse(tab?.url ?: "").host ?: "" } catch (e: Exception) { "" }
                                     val menuItems = listOf<Triple<Any, String, () -> Unit>>(
                                         Triple(JusBrowseIcons.Home, "Home", { viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.BROWSER); showPillMenu = false }),
                                         Triple("LOGO", if (isWhitelisted) "Unwhitelist" else "Whitelist", {
                                             if (currentDomain.isNotEmpty()) viewModel.toggleDomainWhitelist(currentDomain)
                                             showPillMenu = false
                                         }),
                                         Triple(JusBrowseIcons.History, "History", { viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.HISTORY); showPillMenu = false }),
                                         Triple(JusBrowseIcons.Download, "Downloads", { viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.DOWNLOADS); showPillMenu = false }),
                                         Triple(JusBrowseIcons.Extension, "Extensions", { viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.EXTENSIONS); showPillMenu = false }),
                                         Triple(JusBrowseIcons.VpnKey, "Private", { viewModel.createNewTab(isPrivate = true); showPillMenu = false }),
                                         Triple(JusBrowseIcons.Assignment, "Trackers", { showTrackerDetails = true; showPillMenu = false }),
                                         Triple(JusBrowseIcons.Layers, "Container", { showContainers = true }),
                                         Triple(JusBrowseIcons.Settings, "Settings", { viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.SETTINGS); showPillMenu = false }),
                                         Triple(JusBrowseIcons.Warning, "Boomer", { viewModel.toggleBoomerMode(); showPillMenu = false })
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
                                                LaunchedEffect(Unit) { visible = true }
                                                AnimatedVisibility(
                                                    visible = visible,
                                                    enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                                                    exit = scaleOut() + fadeOut()
                                                ) {
                                                    var isItemHovered by remember { mutableStateOf(false) }
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        modifier = Modifier.width(80.dp).clip(RoundedCornerShape(16.dp))
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
                                                            item.second == "Boomer" && isBoomerMode -> MaterialTheme.colorScheme.error
                                                            item.second.contains("Whitelist") && isWhitelisted -> SecureGreen
                                                            else -> MaterialTheme.colorScheme.primary
                                                        }
                                                        Surface(
                                                            shape = CircleShape, color = iconBgColor, modifier = Modifier.size(56.dp),
                                                            border = BorderStroke(1.dp, iconTintColor.copy(alpha = 0.25f))
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
                                                        Text(text = item.second, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // ── Address Bar / URL Content ─────────────────
                        if (isPillExpanded) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.weight(1f).height(38.dp).clip(RoundedCornerShape(20.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.55f))
                                        .padding(horizontal = 14.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    BasicTextField(
                                        value = urlTextFieldValue,
                                        onValueChange = { urlTextFieldValue = it },
                                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                                            .onFocusChanged { focusState ->
                                                if (focusState.isFocused && !hasGainedFocus) {
                                                    val currentText = urlTextFieldValue.text
                                                    urlTextFieldValue = if (currentText == "about:blank") {
                                                        TextFieldValue("", selection = TextRange.Zero)
                                                    } else {
                                                        TextFieldValue(text = currentText, selection = TextRange(0, currentText.length))
                                                    }
                                                    hasGainedFocus = true
                                                }
                                            },
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Normal),
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search, autoCorrect = false, capitalization = KeyboardCapitalization.None, keyboardType = KeyboardType.Uri),
                                        keyboardActions = KeyboardActions(onSearch = {
                                            val query = urlTextFieldValue.text.trim()
                                            if (query.isNotEmpty()) {
                                                val targetUrl = if (viewModel.isUrlQuery(query)) viewModel.getSearchUrl(query, searchEngine, customSearchEngineUrl) else if (!query.contains("://")) "https://$query" else query
                                                viewModel.navigateToUrlForIndex(tabIndex, targetUrl)
                                                session?.loadUri(targetUrl)
                                                isPillExpanded = false
                                                focusManager.clearFocus()
                                            }
                                        }),
                                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                                    )
                                }
                                if (urlTextFieldValue.text.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    FilledTonalIconButton(onClick = { urlTextFieldValue = TextFieldValue("") }, modifier = Modifier.size(34.dp)) {
                                        Icon(JusBrowseIcons.Clear, null, modifier = Modifier.size(16.dp))
                                    }
                                }
                                if (hasSpeechRecognizer && urlTextFieldValue.text.isEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    FilledTonalIconButton(
                                        onClick = {
                                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
                                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Search with voice")
                                            }
                                            speechLauncher.launch(intent)
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(JusBrowseIcons.Mic, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        } else {
                            // Collapsed address bar with tracker badge
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier.clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { showTrackerDetails = true }
                                ) {
                                    val isSecure = sessionSecurityInfo?.isSecure == true
                                    Icon(
                                        imageVector = if (tab?.isPrivate == true) JusBrowseIcons.VpnKey else if (isWhitelisted) JusBrowseIcons.VerifiedUser else if (isSecure) JusBrowseIcons.Lock else JusBrowseIcons.LockOpen,
                                        contentDescription = "Privacy Status",
                                        tint = if (isWhitelisted) SecureGreen else if (trackers.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    if (trackers.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier.size(8.dp).align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp)
                                                .background(MaterialTheme.colorScheme.error, CircleShape)
                                                .border(1.dp, Color.White, CircleShape)
                                        )
                                    }
                                }
                                if (alwaysShowUrl) {
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = urlTextFieldValue.text,
                                        color = Color.White,
                                        modifier = Modifier.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen, blendMode = BlendMode.Difference),
                                        maxLines = 1, overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Tracker Details ModalBottomSheet
        if (showTrackerDetails && tab != null) {
            val trackerList = viewModel.blockedTrackers[tab.id] ?: emptyList()
            ModalBottomSheet(
                onDismissRequest = { showTrackerDetails = false },
                sheetState = rememberModalBottomSheetState(),
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()
                ) {
                    val totalHits = viewModel.blockedTrackersCount[tab.id] ?: 0
                    Text(text = "Trackers Blocked", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "JusBrowse has blocked $totalHits trackers so far, sed :wilted_rose_emoji:",
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (trackerList.isEmpty()) {
                        Text("No trackers detected on this page")
                    } else {
                        androidx.compose.foundation.lazy.LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(trackerList.size) { index ->
                                val tracker = trackerList[index]
                                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f), modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(JusBrowseIcons.Block, null, tint = MaterialTheme.colorScheme.error)
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

        // Fullscreen video overlay — transparent, only shows exit button
        if (isFullscreen) {
            Box(modifier = Modifier.fillMaxSize().clickable { session?.exitFullScreen() }) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(84.dp)
                        .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)))
                        .statusBarsPadding().padding(horizontal = 8.dp)
                ) {
                    IconButton(
                        onClick = { session?.exitFullScreen() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(imageVector = JusBrowseIcons.Close, contentDescription = "Exit Fullscreen", tint = Color.White)
                    }
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

        // Drop Zone Overlay for Downloads
        if (isDragging) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 96.dp, end = 16.dp)
                    .size(120.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        shape = CircleShape
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = JusBrowseIcons.Download,
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

                AndroidView<FrameLayout>(
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
                                            val url = clipData.getItemAt(0)?.text?.toString() ?: ""
                                            
                                            // Validate URL first
                                            val validation = com.jusdots.jusbrowse.security.DownloadValidator.validateDownload(
                                                url, null, null, null, 0
                                            )
                                            if (validation.isAllowed) {
                                                // Sanitize filename from URL
                                                val uri = android.net.Uri.parse(url)
                                                var baseName = uri.lastPathSegment ?: "download"
                                                
                                                // Strip directory traversal characters
                                                baseName = baseName.replace(Regex("[/\\\\\\\\]"), "")
                                                baseName = baseName.replace("..", "")
                                                baseName = baseName.replace("\u0000", "")
                                                
                                                // Limit to alphanumeric, hyphens, underscores, and one extension
                                                baseName = baseName.replace(Regex("[^a-zA-Z0-9.\\-_]"), "_")
                                                
                                                val fileName = "${System.currentTimeMillis()}_$baseName"
                                                viewModel.addDownload(fileName, url, "Downloads/$fileName", 0L)
                                                
                                                val msg = if (validation.requiresWarning) validation.warningMessage ?: "Download started" else "Download started"
                                                android.widget.Toast.makeText(ctx, msg, android.widget.Toast.LENGTH_SHORT).show()
                                            } else {
                                                android.widget.Toast.makeText(ctx, "Download blocked: ${validation.warningMessage}", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        isDragging = false
                                        true
                                    }
                                    DragEvent.ACTION_DRAG_ENDED -> {
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
    }
}





@Composable
private fun StartPageHero(branding: String = "full") {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (branding != "clean") {
            Icon(
                painter = painterResource(R.drawable.ic_launcher_playstore),
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        if (branding == "full") {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "JusBrowse",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "by JusDots",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}
