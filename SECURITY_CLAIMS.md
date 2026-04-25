# Privacy & Security Claims

JusBrowse is made to protrct the user's real hardware from the privacy-breaching tactics of trackers and malicious websites that makes the user vunreble.
---

## 1. Fingerprinting Defenses

Fingerprinting is the process of collecting small pieces of data (entropy) to uniquely identify a device. JusBrowse reduces this entropy by normalizing or spoofing high-value APIs.

### 🧬 API Normalization (The "Tor" Alignment)
To a tracker, every JusBrowse instance looks like a generic, high-end flagship or a budget device.

- **Screen & Viewport**: We enforce custom `screen.height` always which equals `window.innerHeight`. This prevents trackers from seeing the system bars or real resolutions.
- **Locale & Timezone**: Hard-locked to `en-US` and UTC (or persona-specified) at both the navigator and Intl API levels.
- **Hardware Concurrency**: Clamped to `4`, regardless of the actual CPU core count.

### 🎨 Canvas & WebGL Protection
Sites often use invisible canvas drawings to exploit unique GPU rendering artifacts. (the hardest one to protect against)
- **Noise Injection**: JusBrowse injects per-session stable noise into `toDataURL` and `getImageData`.
- **WebGL Spoofing**: Overrides the WebGL Renderer and Vendor to generic "Mozilla" values.
- **Deterministic Randomness**: Uses a `Mulberry32` PRNG seeded per-session, ensuring that noise is stable for a single site visit but different for the next.

### 🔊 Audio & Multimedia
- **AudioContext**: Injects subtle noise into the audio stack.
- **SpeechSynthesis**: Returns an empty list of voices to prevent OS-level language pack leaks.
- **MediaDevices**: Fully disabled/undefined to prevent enumeration of microphones or cameras.

---

## 2. Network & Connection Security

- **HTTPS-Only**: JusBrowse refuses to connect to insecure HTTP sites.
- **DoH (DNS-over-HTTPS)**: Integrated Cloudflare DoH to prevent your ISP from seeing your browsing history.
- **WebRTC Isolation**: STUN/TURN requests are filtered to prevent local IP leakage.

---

## 3. Telemetry & Local Storage
- **No Analytics**: No Google Analytics, Firebase, or Sentry.
- **Anonymized Metrics**: We collect install events and daily active user data 
  (anonymized, non-opt-out) to measure product health. Usage tracking is opt-out 
  via Settings → Privacy.
- **No Cloud Sync**: Bookmarks and history are stored locally in an encrypted 
  Room database.
- **Engine Stripping**: GeckoView's internal telemetry and "Health Report" 
  features are explicitly disabled via engine flags.

---
*Verified against Alpha 6 test 2 (v0.0.6-T2).*