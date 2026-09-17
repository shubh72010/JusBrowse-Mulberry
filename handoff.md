# Handoff — JusBrowse Web Rendering Box + Passkey

Date: 2026-09-17
Device: AIN065 (Nothing Pong), SDK 36, users 0:Shubh primary, 11 Private space, 999 DualApps
Package: `org.mozilla.fenix.debug` (debug), `org.mozilla.firefox` (release)
Source: `source/mozilla-central` (mozilla-central + patches `patches/0005`-`0013`)

## 1. Web Rendering Box Patch — Context

Original shell `patches/0005` (`fragment_browser.xml:43-50`, `BrowserShell.kt:107`) creates inset rounded card:
- `VerticalSwipeRefreshLayout#swipeRefresh` `layout_margin="10dp"` + `jusbrowse_card_bg` (#1E2022, 28dp radius) inside `CoordinatorLayout#browserLayout` (#0A0C0F)
- `ComposeView#jusBrowsePill` outside CoordinatorLayout

Iterative fixes `0007`-`0013` moved engineView above toolbar via margins:
- `BaseBrowserFragment.kt:1728-1751` `initializeEngineView(top,bottom)` — dynamic branch sets `EngineViewClippingBehavior` + `setDynamicToolbarMaxHeight(top+bottom)`, static branch sets `swipeRefresh` `top/bottom = toolbarHeight+10dp, left/right=10dp`
- `BrowserFragment.kt:163-258` `initializeUI` posts `OnPreDrawListener` on `binding.browserLayout` collecting `ScrollableToolbar` children, `bottomVisible = (height - translationY)`, then `swipeRefresh` `bottomMargin = bottomVisible+10dp else 10dp`, `top/left/right=10dp`. Handles PDF special case.
- `BaseBrowserFragment.kt:2292-2314` `expandBrowserView()` clears behavior, sets margins 10dp

Reported bug: card correctly above toolbar, but “padding inside toolbar in bottom toolbar mode which was before is still present”.

### Dump evidence (uiautomator, after rebuild 2026-09-16)
- `browserLayout` [0,126][1080,2349], `swipeRefresh` [26,152][1054,2155], `engineView` [26,152][1054,2155] — gap 26px (10dp) to `composable_toolbar` [0,2181][1080,2349] (168px tall, 64dp). Margin logic correct.
- But inner `WebView` (GeckoView) on webauthn.io/error page: [26,152][1054,1987] — 168px short vs `engineView` 2155. Gecko reserves toolbar height inside viewport even though card already margins. That blank strip appears as “padding inside toolbar/card”.
- Root cause: `setDynamicToolbarMaxHeight(top+bottom)` and `setVerticalClipping` double-count. `isToolbarDynamic` true ( `!shouldUseFixedTopToolbar && isDynamicToolbarEnabled` `BaseBrowserFragment.kt:1896` ) makes Gecko reserve 168px.

### Fixes applied (ponytail — shortest diff)
- `BottomToolbarContainerView.kt:52-77` init: `toolbarContainerView.setPadding(0,0,0,0)`, `clipToPadding=false`, `bottomMargin=0` before `parent.addView()`
- `BrowserFragment.kt:188-215` added `clearToolbarInnerPadding()` (zero padding/margin on `_bottomToolbarContainerView.toolbarContainerView`, `_browserToolbar.layout`, `parent`) called pre and each PreDraw; plus `runCatching { binding.engineView.setVerticalClipping(0); setDynamicToolbarMaxHeight(0) }` each frame
- `BaseBrowserFragment.kt:1728-1729` `initializeEngineView` dynamic branch now `setDynamicToolbarMaxHeight(0)` (was `top+bottom`)
- `BaseBrowserFragment.kt:2156-2161` `configureEngineViewWithDynamicToolbarsMaxHeight()` dynamic branch now `setDynamicToolbarMaxHeight(0)` + `setVerticalClipping(0)` unconditionally (was conditional on `!isInteractiveWidgetDefaultResizesVisual`)
- Briefly forced `isToolbarDynamic=false` to prove gap removed (engineView then filled), then reverted to `true` — gap returned for inner WebView on error pages, indicating Gecko viewport still reserves.

Current state after last build (pid 16532): `engineView` matches `swipeRefresh` (2155), but inner `WebView` on error page still 1987 (168 gap). Need to disable dynamic reserve or make Gecko not reserve when shell margins active.

Next step for toolbar: force `isToolbarDynamic=false` for JusBrowse shell, or make `initializeEngineView`/`configureEngineView` always static path, or patch `EngineViewClippingBehavior.kt:102-122` to not set `translationY`/`setVerticalClipping` when shell active. Verify via `adb shell uiautomator dump` that `WebView` bounds == `engineView` bounds.

## 2. Passkey — Context

`BaseBrowserFragment.kt:1324-1337` originally gated `WebAuthnFeature` with `if (BuildConfig.MOZILLA_OFFICIAL)` — JusBrowse debug (`org.mozilla.fenix.debug`) never installed feature, so passkey never prompted.

Fix 1: `BaseBrowserFragment.kt:1321-1331` removed gate, always `webAuthnFeature.set(...)`, removed unused `import org.mozilla.fenix.BuildConfig:162`.

Then `WebAuthnTokenManager.java:201-223` distinguished:
- official: `Fido2PrivilegedApiClient.getRegisterPendingIntent(browserOptions)` with origin — whitelisted, no per-site assetlinks
- non-official: `Fido2ApiClient.getRegisterPendingIntent(requestOptions)` without origin — requires site `assetlinks.json` for that package

JusBrowse debug non-official on `webauthn.io` (no assetlinks) gave `DATA_ERR [50152] RP ID cannot be validated` (log 2026-09-16 23:50:27, pid 14116).

Fix 2: `WebAuthnTokenManager.java:203-208,444-453,608-616,637-640` forced privileged path for all builds. On AIN065 this then gave `ApiException 17: Fido.FIDO2_PRIVILEGED_API is not available, ConnectionResult{statusCode=RESTRICTED_PROFILE}` — device considers debug not whitelisted, privileged API unavailable.

Fallback added in `WebAuthnTokenManager.java:274-321,533-580` — privileged failure falls back to `Fido2ApiClient` with `requestOptions`, which again gives `DATA_ERR`. User reports now “sends abort signal” (`ABORT_ERR` from `result.completeExceptionally(...ABORT_ERR)` on privileged failure).

Credential Manager path (`WebAuthnCredentialManager.java:122-224`) should handle Android 14+ without GMS whitelist, but on this device it returns `TYPE_NO_CREATE_OPTIONS: No create options available` for webauthn.io (log 2026-09-17 00:14:01, pid 12007, also 00:17:40 pid 16532):
```
makeCredential origin=https://webauthn.io requestJSON={"attestation":"none","authenticatorSelection":{...},"pubKeyCredParams":[{"alg":-8},{"alg":-7},{"alg":-257}],"rp":{"id":"webauthn.io"},...}
makeCredential requestBundle ok
CreateCredential failed TYPE_NO_CREATE_OPTIONS
```
Filtered `-8` (Ed25519) to `-7/-257` only (`WebAuthnCredentialManager.java:71-97` now filters `pubKeyCredParams` to `-7,-257,-37,-35` and logs “Filtered…”) — still `NO_CREATE_OPTIONS` after rebuild (00:17:40). Same after setting JusBrowse as default browser (`cmd role add-role-holder --user 0 android.app.role.BROWSER org.mozilla.fenix.debug` now holds for user 0, dumped `dumpsys role`).

Device state:
- `pm list features` has `android.software.credentials`, SDK 36, GMS 26.33.32
- `dumpsys credential` no output, `dumpsys role` holders `org.mozilla.firefox` (release) vs `org.mozilla.fenix.debug` after fix
- Nothing OS may have disabled Credential Manager provider per https://issuetracker.google.com/issues/349310440, or Google provider requires Google account with passkey enabled / screen lock. No provider listed via `cmd credential`.

Next step for passkey:
- Verify official `org.mozilla.firefox` (release) on same device does webauthn.io Register succeed via privileged (whitelisted) — test with `adb shell am start --user 0 -a VIEW -d https://webauthn.io -n org.mozilla.firefox/...` and check for credential chooser vs same error. If official succeeds, JusBrowse needs same whitelist/package/signature or to make Credential Manager succeed.
- Make Credential Manager succeed: ensure device has screen lock, Google account with passkeys enabled, and provider is Google. Try `webauthn.me` / `demo.yubico.com` to see if any RP gives create options. Check `adb shell dumpsys credential` alternative.
- Or change JusBrowse `applicationId` for credential manager to be considered browser without asset links: make debug `applicationId` be `org.mozilla.firefox` (whitelisted package) by editing `mobile/android/fenix/app/build.gradle: applicationIdSuffix` or build `firefoxDebug` variant and sign with release key, or add JusBrowse package+SHA to a test site’s `assetlinks.json` (local server) and test passkey against `https://10.0.2.2:8080`.
- Keep fallback chain: Credential Manager -> privileged -> regular with `browserOptions` (currently fallback uses `requestOptions`, should try `browserOptions` if regular supports it — but `Fido2ApiClient` lacks that overload, so need to keep privileged as only browser-aware path).

## 3. Build / Install

Artifact build (`config/mozconfig: ac_add_options --enable-artifact-builds`, `mozconfig` copied to `source/mozilla-central/mozconfig`, `sdk.dir=/home/flakesofsmth/Android/Sdk`):
```
python3 source/mozilla-central/mach --log-no-times gradle fenix:assembleDebug
# APK: obj-aarch64-unknown-linux-android/gradle/build/mobile/android/fenix/app/outputs/apk/debug/fenix-arm64-v8a-debug.apk (also armeabi-v7a, x86_64)
adb -s 192.168.1.50:41553 install -r <apk>
adb -s 192.168.1.50:41553 shell am force-stop org.mozilla.fenix.debug
adb -s 192.168.1.50:41553 shell am start --user 0 -a VIEW -d https://webauthn.io -n org.mozilla.fenix.debug/org.mozilla.fenix.IntentReceiverActivity
adb shell uiautomator dump /sdcard/window_dump.xml; cat ...
adb logcat -d --pid=$(pidof org.mozilla.fenix.debug) | grep -E "WebAuthnCredMan|WebAuthnTokenManager"
```

Last successful builds: 2m (first), 26s/21s/5s incremental. Install Success on 192.168.1.50:41553 (arm64-v8a). Second device `adb-1c9c43ef…` same physical via TLS, ignore.

## 4. Current Git State

`source/mozilla-central` diff (outer `JusBrowse-Mul` patches not yet refreshed via `./scripts/refresh-patches.sh`):
- `BottomToolbarContainerView.kt` +3
- `BrowserFragment.kt` +28 (clear padding + setDynamic/Clipping)
- `BaseBrowserFragment.kt` WebAuthn gate removed, `initializeEngineView`/`configureEngineView` set 0
- `geckoview/WebAuthnTokenManager.java` forced privileged + fallback, `hasCredentialInGMS`/`isUVPAA` fallback, `geckoview/WebAuthnCredentialManager.java` DEBUG true + filtered pubKeyCredParams + logging

## 5. TODO

- Toolbar: decide static vs dynamic for shell. If keep dynamic, ensure `WebView` inner bounds == `engineView` (check `EngineViewClippingBehavior` + `setDynamicToolbarMaxHeight`/`setVerticalClipping` interaction on SDK 36 `isInteractiveWidgetDefaultResizesVisual` true path). Verify via dump after each fix.
- Passkey: get a working create on JusBrowse debug. Test official Firefox passkey on same device; if official works via privileged, JusBrowse needs same package/signature whitelist or a working Credential Manager provider. Try local test server with assetlinks for `org.mozilla.fenix.debug` SHA (from `keytool -list -v -keystore ~/.android/debug.keystore`) and RP `10.0.2.2`. Or make debug build `MOZILLA_OFFICIAL=true` + sign with official key if available, or switch to `firefox` variant `applicationId org.mozilla.firefox`.
- Refresh patches after final fix: `./scripts/refresh-patches.sh` will squash `source/mozilla-central` diff into `patches/`.

## 6. Results 2026-09-17 (device 192.168.1.50:41861)

- Web box: FIXED. `initializeEngineView` + `configureEngineViewWithDynamicToolbarsMaxHeight` always static (`behavior=null`, margins, maxHeight 0); PreDraw keeps margin updates only. Build 1m15s OK, installed, user confirmed.
- Passkey: CLOSED as unfixable in client code. Side-by-side `logcat CredentialManager` proof, same provider, same request shape:
  - `org.mozilla.fenix.debug` 11:53:37 `executeCreateCredential` -> provider `CANCELED` -> `NO_CREATE_OPTIONS`; privileged `RESTRICTED_PROFILE`; regular `DATA_ERR [50152] RP ID cannot be validated`.
  - `org.mozilla.firefox` 156.0 (sideloaded after `adb uninstall` of DELETE_KEEP_DATA remnant; Play Store refused) 11:55:07 -> `SAVE_ENTRIES_RECEIVED` -> `Final credential received`, user registration succeeded.
  - So Google allowlists callers by package+signature on BOTH CredMan provider and privileged API; regular API needs per-site assetlinks. Release JusBrowse under its own package/key hits the same wall.
- Real fix: enroll JusBrowse release package+cert in Google's FIDO2 allowlist; until then document passkeys as known limitation (works only on sites with assetlinks for the JusBrowse package).

## 7. Allowlist mechanism CONFIRMED 2026-09-17 (no code fix needed)

- Google Password Manager reads an openly-available allowlist `https://www.gstatic.com/gpm-passkeys-privileged-apps/apps.json` (73 entries, saved at `/tmp/opencode/gpm-allowlist.json`) via `CallingAppInfo.getOrigin(privilegedAllowlist)`: origin is returned only if packageName + newest-signature SHA-256 match; `userdebug` entries are honored only on `user`-type==userdebug devices.
- List contains `org.mozilla.firefox` + `org.mozilla.fenix` (release certs) and `org.mozilla.fenix.debug` (Mozilla CI debug cert `BD:AE:82...`, userdebug-only).
- Our device is `ro.build.type=user`, so the debug entry is ignored -> provider CANCELED -> NO_CREATE_OPTIONS. Our local debug APK is signed with the machine-local Android Debug cert (`DB:F3:6C...`), which matches nothing on the list anyway.
- Enrollment: form linked from `developer.android.com/identity/sign-in/privileged-apps` ("complete the request form to open a ticket"). Same approval unblocks `Fido2PrivilegedApiClient` (RESTRICTED_PROFILE) per Chromium dev report 2026-01.
- Action plan: (1) finalize JusBrowse release package + signing key, (2) submit form with package + SHA-256 (request release entry; optionally a userdebug entry for a shared debug cert + test on userdebug emulator), (3) no client-code changes needed — CredMan->privileged->regular chain already correct. Stopgap without ticket: per-site assetlinks.json for sites you control (regular API path).

## 8. Independent identity DONE 2026-09-17 — `patches/0030`

- `fenix/app/build.gradle`: base `applicationId com.jusdots.jusbrowse`; suffixes debug `.debug`, nightly `.nightly`, beta `.beta`, release none (= com.jusdots.jusbrowse), benchmark `.nightly`; deep-link schemes `jusbrowse{-dev,-nightly,-beta,}`; dropped Mozilla `sharedUserId` (beta+release).
- `res/xml/shortcuts.xml`: removed hardcoded `targetPackage="org.mozilla.fenix"` x3 (own-package default), kept `targetClass` (Java namespace unchanged).
- Left untouched by design: internal action/extra/channel/work strings, manifest class names, all `${applicationId}` authorities, `MozillaProductDetector` (detects other apps), Nimbus `appName`, test fixtures, dynamic `BuildConfig.APPLICATION_ID` users.
- Verified: `assembleDebug` OK, APK badging `com.jusdots.jusbrowse.debug`, installed alongside old id, activity launches, shortcuts registered. WebAuthn caller identity is now the JusBrowse package (still unenrolled -> expected failures until Google ticket).

## 9. Release signing DONE 2026-09-17 — `patches/0031`

- Permanent key: `~/.keystores/jusbrowse-release.keystore` (alias `jusbrowse`, RSA-4096, 9125d, CN=JusBrowse/O=JusDots), password in `~/.keystores/jusbrowse-release.pw` (600). NOT in git. BACK UP both or the identity is lost.
- Fingerprint: `E4:46:FB:8B:E1:43:49:B3:46:C6:29:6B:32:BA:0F:AC:A8:DC:3C:6B:AE:4A:BE:CA:CD:BF:30:DF:7A:6F:39:23`
- `build.gradle`: `jusbrowseRelease` signingConfig from `JUSBROWSE_KEYSTORE_PATH/_PASSWORD/_ALIAS/_PASSWORD` env (falls back to debug signing when unset); deleted beta+release `AndroidManifest.xml` overlays that existed only for Mozilla's sharedUserId.
- `fenix:assembleRelease` OK (8m38s); APK `com.jusdots.jusbrowse` (no suffix), apksigner SHA-256 matches keystore.
- Form answers: Q2 `https://github.com/shubh72010/JusBrowse-Mulberry/releases` (upload release APK there first); Q4 package `com.jusdots.jusbrowse`, build `release`, cert as above.

## 10. Patch-stack cleanup DONE 2026-09-17 — 42 commits -> 13 patches

- Squashed in `source/mozilla-central` (base `b14ad10e3f75` untouched), regenerated `patches/` via `format-patch`. See `patches/README.md` for the final table.
- Folded: DotPill add/wire/remove -> net Homepage wiring in 0002; shell iterations 0007-0013 + 0018 shell hunks + device-verified always-static fix -> 0003; 0018 segmented hunk -> 0004; 0016+0017 -> 0006; 0021+0022 -> 0009; 0023-0029 -> 0010; 0030+0031 -> 0011; 0032+0033 -> 0012; 0036-0042 (minus 0040) -> 0013.
- Dropped: 0034+0035+0040 status-bar toggle (add+fix+remove). Tree comparison old-vs-new shows only 2 trivial diffs: an inlined `backgroundChangeListener` in `CustomizationFragment` (0034's refactor died with it) and one unused `Settings` import gone from `ThemeManager`. No behavior change; new tree is cleaner.
- Discarded uncommitted WebAuthn experiments (`WebAuthnTokenManager` privileged-forcing + `WebAuthnCredentialManager` debug filter): caused ABORT_ERR, superseded by §6-§7 conclusion (allowlist, no client fix). Passkeys remain a known limitation pending the Google ticket. Revisit after enrollment.
- Binaries audit: 0008 wallpapers 24M (11 files), 0010 logos 1.9M (104 files), 0013 palette art 108K (6 files), 0006 locales 4.5M text-only. All are shipped app assets — belong in git, no LFS, no generated files in patches/.
- Verified: all 13 patches apply from base into an empty index (`git apply --cached` chain) reproducing the source HEAD tree byte-for-byte (`fe949c2`). Backup of pre-cleanup history: branch `jusbrowse-pre-cleanup` in `source/mozilla-central` (disposable tree) + old patch files in outer git history.
- Post-cleanup build catch (same day): first incremental build failed `compileDebugKotlin` — `ThemeManager.kt:78 Settings(activity).themePalette` (palette feature, 0037) had relied on `import Settings` added by dropped 0034 without declaring its own. Fixed by adding the import to the palette patch (0013). Rebuilt: `fenix:assembleDebug` BUILD SUCCESSFUL (3m28s, 9 tasks), APKs (arm64-v8a, armeabi-v7a, x86_64) present. Lesson: cross-patch implicit dependencies like this are exactly why the stack must be build-verified after squashing.
