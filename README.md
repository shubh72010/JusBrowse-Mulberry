# JusBrowse — Firefox Android Patch-Based Architecture

> JusBrowse = Mozilla Firefox Android + JusDots downstream patchset  
> Not a giant fork — a small, well-isolated delta against mozilla-central.

Mozilla moved Firefox Android (Fenix, Android Components, GeckoView) into **mozilla-central** (`mobile/android/`). JusBrowse tracks upstream as a **disposable source tree** plus a **patch series**.

## Architecture

```
Mozilla Central (hg.mozilla.org / gecko-dev mirror)
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

## Repository Layout

```
JusBrowse-Mul/
├── patches/                  # 13 logical patches (lexicographic order = apply order)
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
│   └── README.md             # per-patch table + policy
├── config/
│   ├── mozconfig             # copied to source/mozilla-central/mozconfig before build
│   └── upstream.yml          # pinned mozilla-central revision
├── scripts/
│   ├── bootstrap.sh          # fetch + apply
│   ├── fetch-upstream.sh     # clone/fetch mozilla-central at pinned rev
│   ├── apply-patches.sh      # git am patches/*.patch
│   ├── refresh-patches.sh    # git format-patch base..HEAD → patches/
│   ├── update-upstream.sh    # try patches against newer rev
│   └── build.sh              # ./mach gradle fenix:assembleDebug
├── jusbrowse                 # CLI: ./jusbrowse {bootstrap,fetch,apply,build,update,clean}
└── source/                   # DISPOSABLE — gitignored, never commit
    └── mozilla-central/      # fresh checkout + applied patches
```

## Quick Start — Phase 1 Proof of Concept

```sh
# 1. Fetch upstream at pinned revision
./jusbrowse bootstrap
# or step by step:
./scripts/fetch-upstream.sh
./scripts/apply-patches.sh

# 2. Build vanilla+patched Fenix
./jusbrowse build
# artifact: source/mozilla-central/mobile/android/fenix/app/build/outputs/apk/

# 3. Verify patch is present
grep -r "JusBrowse" source/mozilla-central/mobile/android/fenix/app/src/main/res/values/static_strings.xml

# 4. Prove disposable tree
rm -rf source/mozilla-central
./jusbrowse bootstrap
./jusbrowse build   # same result, patch reapplied
```

## Making a Patch (one at a time)

```sh
cd source/mozilla-central
git checkout <pinned-revision>   # from config/upstream.yml

# ONE focused change, e.g. toolbar behavior
# edit mobile/android/fenix/.../Toolbar.kt
git add mobile/android/fenix/...
git commit -m "JusBrowse: change toolbar behavior"

# export as patch (outside)
git format-patch -1 HEAD -o ../../patches/
cd ../..
# rename to next number: 0002-jusbrowse-toolbar.patch
```

Rules per patch:
- Only files for that feature
- No GeckoView changes unless required
- No unrelated refactor
- Must be reversible (`git am` / `git am --abort`)

## Updating Upstream

```sh
./jusbrowse update              # tries origin/master
./jusbrowse update <rev>        # tries specific rev

# on conflict:
# ✓ 0001-branding
# ✓ 0002-app-name
# ✗ 0003-toolbar  CONFLICT  → fix only that patch
```

`config/upstream.yml` is updated **only after** patches + build succeed.

## CI (concept)

Scheduled job: fetch → apply → build. Success = green; conflict = report failing patch number. See `.github/workflows/ci.yml`.

## Phases (final patch mapping)

| Phase | Patches |
|-------|---------|
| 1 — POC / shell | `0001` app_name, `0002` startpage bg, `0003` web shell (always-static card) |
| 2 — Branding | `0006` strings+locales, `0009` wordmark, `0010` single-logo purge |
| 3 — UI | `0004` segmented lists, `0007` 2nd toolbar shortcut, `0008` wallpapers, `0012` toolbar toggle, `0013` palettes |
| 4 — Privacy | `0005` telemetry hard-disable |
| 5 — Identity | `0011` applicationId + release signing |

Each phase is small focused patches, never one giant fork. See
`patches/README.md` for the full per-patch table.

## Why this works for AI agents

Never: "understand all of mozilla-central and turn it into JusBrowse"  
Instead: "patch 0005 no longer applies — inspect old vs new upstream, preserve behavior, update only that patch"

Bounded problem → debuggable, reviewable, rebaseable.
