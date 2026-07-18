# Current State

## Completed Work
- Integrated lightweight privacy-safe analytics using Supabase REST API via `AnalyticsManager.kt`
- Implemented robust UUID generation and offline/online syncing capabilities with `DataStore` backing (install/streak).
- Updated `SettingsScreen.kt` and `BrowserViewModel.kt` to expose a "Share Anonymous Analytics" toggle.
- Attached tracker to `MainActivity.kt` lifecycle for daily heartbeat checking.

## System Architecture (Current Reality)
- `BrowserViewModel`: State coordinator collecting from `PreferencesRepository`.
- `PreferencesRepository`: Asynchronous local store via Android `DataStore`. Holds tracking toggles and sync metadata.
- `AnalyticsManager`: Handles Supabase network endpoints utilizing OkHttp on the IO dispatcher with Mutex to prevent sync races. Exclusively patches three data points: installation, date, and streak.
- *Anti-Fingerprinting Engine*: Currently leveraging native GeckoView Resist Fingerprinting (RFP). (JS overrides have been migrated out).

## Next Three Priorities
1. Test analytics behavior end-to-end to confirm streak transitions and UUID uniqueness.
2. Monitor OkHttp telemetry requests to ensure absolutely zero overhead delays at startup.
3. Finalize per-site storage partitioning.
