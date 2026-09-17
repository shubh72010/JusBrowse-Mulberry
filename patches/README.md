# Patches

One `.patch` per logical change, against the pinned mozilla-central revision in
`config/upstream.yml`. Lexicographic order = apply order
(`./scripts/apply-patches.sh` runs `git am` over `patches/*.patch`).

## Current stack (13 patches, cleaned 2026-09-17)

| # | File | What |
|---|------|------|
| 1 | `0001-...-branding-rename-app_name` | Base en-US `app_name` string |
| 2 | `0002-...-startpage-background-home-wiring` | `StartPageBackground` composable wired into `Homepage` (DotPill overlay experiment added then removed; net = background wiring) |
| 3 | `0003-...-web-rendering-shell-...-always-static` | Webpage inside inset rounded card above toolbar; always-static (no Gecko dynamic reserve, toolbar padding cleared). Folds all toolbar-gap iterations + device-verified final fix |
| 4 | `0004-...-segmented-connect-list-styling` | Unified grouped-list style for settings screens, incl. edge/placement/lag fixes |
| 5 | `0005-...-hard-disable-ALL-telemetry` | Glean/adjust/metrics stubbed — no data to Mozilla |
| 6 | `0006-...-branding-strings-all-locales` | Firefox→JusBrowse across en-US + all locale `strings.xml`, drawables, about page |
| 7 | `0007-...-editable-2nd-toolbar-shortcut-...` | Second configurable shortcut slot in expanded toolbar |
| 8 | `0008-...-wallpaper-selector-bundled-wallpapers` | Native picker + bundled `jusbrowse_wallpapers` assets (~24 MB binaries, shipped art — belongs in git, no LFS) |
| 9 | `0009-...-wordmark-on-startpage` | JusBrowse wordmark on startpage, green2 default |
| 10 | `0010-...-single-logo-purge-...` | One JusBrowse logo everywhere; alt icons + all Firefox drawables removed (binary replacements are shipped launcher art) |
| 11 | `0011-...-application-identity-release-signing` | `com.jusdots.jusbrowse` applicationId + release signing via env keystore |
| 12 | `0012-...-toolbar-background-toggle-defaults` | Toolbar-background toggle + adjusted defaults |
| 13 | `0013-...-UI-color-palettes-...` | Catppuccin/TokyoNight/Gruvbox/Nord/Dracula/Rosé Pine themes + matching wallpapers/icons + restart confirmation |

Dropped during cleanup: status-bar background toggle add/fix/remove
(old 0034/0035/0040 — net zero, verified by tree comparison), DotPill
overlay files (net zero; Homepage wiring kept in 0002), WebAuthn
privileged-API experiments (documented dead end, see `handoff.md`).

## Creating a new patch

```sh
# inside source/mozilla-central
git checkout <pinned-revision>
# ... make ONE focused change ...
git add <files>
git commit -m "JusBrowse: <what>"
git format-patch -1 HEAD -o ../../patches/
# rename to next number, e.g. 0014-jusbrowse-*.patch
```

## Refreshing after rebase

```sh
./scripts/refresh-patches.sh   # regenerates 0001.. from commits after base
```

## Applying

```sh
./scripts/apply-patches.sh
```
