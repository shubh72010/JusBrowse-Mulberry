# Security Policy

## Reporting a Vulnerability

Report security vulnerabilities via GitHub's private security advisory tool. Do not open a public issue for critical flaws.

## Security Model

- **No cloud services**: All data stored locally. No sync, no accounts.
- **Engine isolation**: GeckoView `contextId` for per-container cookie/cache separation.
- **No telemetry**: GeckoView's internal data collection is disabled. No analytics SDKs.
- **HTTPS-only**: Engine-level enforcement, togglable in settings.
- **DNS over HTTPS**: Default Cloudflare, 23 presets + custom URL.
- **Download validation**: MIME checks, APK/JS warnings, optional VirusTotal/Koodous scanning.

## External Components

- GeckoView (Mozilla) — rendering engine
- OkHttp — networking
- Media3/ExoPlayer — media playback
- Jetpack Compose — UI
