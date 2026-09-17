<div align="center">

<img src="assets/hero.svg" width="100%" alt="JusBrowse — Firefox Android, reimagined" />

[![Download](https://img.shields.io/github/v/release/shubh72010/JusBrowse-Mulberry?include_prereleases&label=download&color=A6E3A1)](https://github.com/shubh72010/JusBrowse-Mulberry/releases)
![Downloads](https://img.shields.io/github/downloads/shubh72010/JusBrowse-Mulberry/total?label=downloads)
![Stars](https://img.shields.io/github/stars/shubh72010/JusBrowse-Mulberry?style=flat&label=stars)
![Patches](https://img.shields.io/badge/patches-13-blue)
![Platform](https://img.shields.io/badge/platform-Android-green)
![Status](https://img.shields.io/badge/status-pre--alpha-orange)

**Mozilla Firefox Android + a small JusDots patchset — not a giant fork, a well-isolated delta.**

<a href="https://github.com/shubh72010/JusBrowse-Mulberry/releases"><b>⬇ Get JusBrowse</b></a>

</div>

---

## ✨ Features

| | What you get |
|---|---|
| 🃏 | **Web-card shell** — pages render inside an inset rounded card above the toolbar |
| 🎨 | **6 color palettes** — Catppuccin, Tokyo Night, Gruvbox, Nord, Dracula, Rosé Pine, with matching launcher icons |
| 🖼️ | **Bundled wallpapers** — native picker with included art sets |
| 🔧 | **Toolbar your way** — second shortcut slot, background toggle, top/bottom placement |
| 🔇 | **Zero telemetry** — Glean/adjust/metrics hard-disabled, nothing phones home |
| 🆔 | **Own identity** — `com.jusdots.jusbrowse`, JusBrowse branding in all locales |

## ⬇ Download

Grab the latest **Pre-Alpha** from
[Releases](https://github.com/shubh72010/JusBrowse-Mulberry/releases):

| Your device | File |
|-------------|------|
| Most modern phones (arm64) | `fenix-arm64-v8a-release.apk` |
| Older 32-bit phones (armv7) | `fenix-armeabi-v7a-release.apk` |

Allow *Install unknown apps* when prompted. Updates install over the previous
version — same signing key, your data is kept.

## ⚠️ Known limitations

- **Passkeys** usually fail until the release package is enrolled in Google's
  FIDO2 allowlist (works only on sites publishing `assetlinks.json` for this
  package). Tracked in `handoff.md`.
- Pre-alpha — expect rough edges. Reports welcome via
  [Issues](https://github.com/shubh72010/JusBrowse-Mulberry/issues).

---

<details>
<summary><b>🛠 For developers</b> — patch-stack architecture, build, contributing</summary>

<br>

Mozilla moved Firefox Android (Fenix, Android Components, GeckoView) into
**mozilla-central** (`mobile/android/`). JusBrowse tracks upstream as a
**disposable source tree** plus a **patch series**:

```
Mozilla Central (gecko-dev mirror)
      ↓  clean checkout at pinned revision
JusBrowse patch stack  (patches/*.patch)
      ↓  git am
Patched source tree  (source/mozilla-central/)
      ↓  ./mach gradle fenix:assembleDebug
JusBrowse APK
```

Source tree is **disposable**:

```sh
rm -rf source/mozilla-central
./jusbrowse bootstrap   # re-creates it
```

### Repository layout

```
JusBrowse-Mul/
├── patches/            # 13 logical patches (lexicographic order = apply order)
│   ├── 0001-...-branding-rename-app_name.patch
│   ├── 0002-...-startpage-background-home-wiring.patch
│   ├── 0003-...-web-rendering-shell-...-always-static.patch
│   ├── 0004-...-segmented-connect-list-styling.patch
│   ├── 0005-...-hard-disable-ALL-telemetry.patch
│   ├── 0006-...-branding-strings-all-locales.patch
│   ├── 0007-...-editable-2nd-toolbar-shortcut-....patch
│   ├── 0008-...-wallpaper-selector-bundled-wallpapers.patch  # ~24MB shipped art
│   ├── 0009-...-wordmark-on-startpage.patch
│   ├── 0010-...-single-logo-purge-....patch
│   ├── 0011-...-application-identity-release-signing.patch
│   ├── 0012-...-toolbar-background-toggle-defaults.patch
│   ├── 0013-...-UI-color-palettes-....patch
│   └── README.md       # per-patch table + policy
├── config/
│   ├── mozconfig       # copied to source/mozilla-central/mozconfig before build
│   └── upstream.yml    # pinned mozilla-central revision
├── scripts/            # bootstrap / fetch / apply / refresh / update / build
├── jusbrowse           # CLI: ./jusbrowse {bootstrap,fetch,apply,build,update,clean}
└── source/             # DISPOSABLE — gitignored, never commit
    └── mozilla-central/
```

### Quick start

```sh
./jusbrowse bootstrap   # fetch upstream at pinned rev + apply patches
./jusbrowse build       # ./mach gradle fenix:assembleDebug
# artifact: source/mozilla-central/obj-*/gradle/build/.../apk/debug/
```

### Making a patch (one at a time)

```sh
cd source/mozilla-central
# ONE focused change, e.g. toolbar behavior
git add mobile/android/fenix/... && git commit -m "JusBrowse: change toolbar behavior"
git format-patch -1 HEAD -o ../../patches/
cd ../..   # rename to next number: 0014-jusbrowse-*.patch
```

Rules per patch: only files for that feature, no GeckoView changes unless
required, no unrelated refactor, must be reversible.

### Updating upstream

```sh
./jusbrowse update              # tries origin/master
./jusbrowse update <rev>        # tries specific rev
# on conflict, fix only the failing patch number
```

`config/upstream.yml` is updated **only after** patches + build succeed.

### Patch map

| Area | Patches |
|------|---------|
| Shell / start | `0001` app_name, `0002` startpage bg, `0003` web shell |
| Branding | `0006` strings+locales, `0009` wordmark, `0010` single-logo purge |
| UI | `0004` segmented lists, `0007` 2nd shortcut, `0008` wallpapers, `0012` toolbar toggle, `0013` palettes |
| Privacy | `0005` telemetry hard-disable |
| Identity | `0011` applicationId + release signing |

See `patches/README.md` for the full per-patch table.

### Why this works for AI agents

Never: *"understand all of mozilla-central and turn it into JusBrowse"*
Instead: *"patch 0005 no longer applies — inspect old vs new upstream,
preserve behavior, update only that patch"*

Bounded problem → debuggable, reviewable, rebaseable.

</details>
