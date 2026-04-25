# Privacy Policy

**Last Updated: March 11, 2026**

JusBrowse is built with a "Privacy First" philosophy. We believe your browsing data belongs to you and no one else.

## 1. Data Collection (The "Zero Policy")
JusBrowse **does not collect, transmit, or share** any of your personal data. 
- **No Telemetry**: We explicitly disable GeckoView's internal telemetry and data submission engines.
- **No Analytics SDKs**: The application contains no Google Analytics, Firebase, or other third-party tracking libraries.
- **No Crash Reporting**: We do not automatically send crash logs to any server.
- **No Accounts**: JusBrowse does not require or support cloud accounts or server-side sync.

## 2. Local Data Storage & Isolation
All browsing data is stored **strictly on your device** and is never transmitted to us or any third party.
- **Container Isolation**: Cookies, cache, and local storage are siloed per Persona using GeckoView contextual identities.
- **Volatile Storage**: GhostCookieJar ensures native request cookies are kept in-memory for the duration of the session.
- **Wipe on Switch**: Switching Personas ensures a hard boundary between data silos.

You can wipe this data at any time through the "Settings" menu.

## 3. Third-Party Services
While we do not collect your data, certain web features rely on third-party infrastructure:
- **Search Engines**: Your queries are sent to your chosen engine (default: DuckDuckGo).
- **DNS-over-HTTPS (DoH)**: For secure lookups and tracking protection, JusBrowse pings `cloudflare-dns.com` (default) or your custom provider.
- **GeckoView Engine**: JusBrowse uses the GeckoView engine (Mozilla). Site rendering and low-level cookie management are subject to its security boundaries.
- **Security Scanning**: If you provide API keys for VirusTotal or Koodous, file hashes are sent for analysis upon your request.

## 4. Permissions
JusBrowse asks for explicit permission before sharing sensitive info (Location, Camera, Microphone) with any website. We do not access these sensors in the background.

## 5. Policy Changes
If we change this policy, we will update the `Last Updated` date above. Since we do not have your email or account info, it is your responsibility to check this file for updates.

## 6. Contact
For privacy-related concerns or technical inquiries, please reach out via the official project repository.
