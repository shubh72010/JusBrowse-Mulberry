# Security Policy & Data Handling

This document outlines how JusBrowse handles security vulnerabilities and our commitment to user data protection.

---

## 🔒 Reporting a Vulnerability

If you've discovered a security vulnerability or a privacy leak (e.g., an IP leak or fingerprinting bypass), please message the lead developer directly at his discord (found on GitHub profile) or use the GitHub private security report feature.

**Please do not open a public issue for critical security flaws.** We prefer a coordinated disclosure to ensure our users are protected before the fix is public.

---

## 🛡️ Our Security Model

### 1. C1ollection Policy
JusBrowse is built on the principle that **user data is a liability, not an asset.**
- **No Cloud Services**: There is no "sync account" or "location service" built into the browser.
- **Engine Stripping**: We have explicitly disabled GeckoView's internal data collection and background health reporting.

### 2. Deep Engine Isolation
We use **GeckoView** because it allows us to create isolated "Engine Contexts." 
- **Volatile Cookies**: For sensitive personas, session data is kept in memory and never written to the Android filesystem.
- **Sandboxed Tabs**: Each tab runs in its own process-like sandbox, reducing the risk of cross-site request forgery (CSRF) and side-channel attacks.

### 3. Fingerprinting Resistance
We treat fingerprinting as a security hazard. Our defenses are designed to reduce your "Identity Entropy" to a level where you blend into the wider pool of flagship Android devices.

---

## 🕵️ Data We *Don't* Collect

| Data Type | Handling |
| :--- | :--- |
| **Browsing History** | Stored locally in an encrypted SQLite database. Never uploaded. |
| **Passwords** | Stored via Android Keystore system. Never uploaded. |
| **IP Address** | We do not proxy or log your IP. For network anonymity, use a VPN or the planned Tor integration. |
| **Cookies** | Stored locally (or in memory). Never synced or shared. |

---

## 🧩 External Components

JusBrowse utilizes the following major external libraries:
- **Mozilla GeckoView**: For the rendering engine.
- **OkHttp/ExoPlayer**: For networking and media.
- **Jetpack Compose**: For the UI.

We regularly audit these dependencies for security advisories and update them promptly.

---
*Last Updated: April 2026, v0.0.6-T2*
