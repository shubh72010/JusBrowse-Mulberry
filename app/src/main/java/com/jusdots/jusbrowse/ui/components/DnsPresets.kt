package com.jusdots.jusbrowse.ui.components

data class DnsProvider(
    val name: String,
    val description: String,
    val dohUrl: String,
    val category: String,
    val icon: String = "🌐"
)

object DnsPresets {
    val providers = listOf(
        // 1. The Global Leaders
        DnsProvider("Cloudflare", "Fastest overall; strict privacy.", "https://cloudflare-dns.com/dns-query", "Global Leaders", "⚡"),
        DnsProvider("Google", "Maximum compatibility; global reach.", "https://dns.google/resolve", "Global Leaders", "🔍"),
        DnsProvider("OpenDNS", "Cisco-backed reliability.", "https://doh.opendns.com/dns-query", "Global Leaders", "⚓"),
        DnsProvider("Quad9", "High security; blocks malware.", "https://dns.quad9.net/dns-query", "Global Leaders", "🛡️"),
        DnsProvider("Gcore", "Optimized for edge performance.", "https://ns1.gcore.com/dns-query", "Global Leaders", "🚀"),

        // 2. Privacy & Ad-Blocking Specialists
        DnsProvider("AdGuard (Default)", "Standard ad and tracker blocking.", "https://dns.adguard.com/dns-query", "Privacy & Ad-Blocking", "🚫"),
        DnsProvider("AdGuard (Unfiltered)", "No blocking, just fast resolution.", "https://unfiltered.adguard-dns.com/dns-query", "Privacy & Ad-Blocking", "🔓"),
        DnsProvider("NextDNS", "Fully customizable via dashboard.", "https://dns.nextdns.io/", "Privacy & Ad-Blocking", "⚙️"),
        DnsProvider("Mullvad (Ads)", "VPN-grade privacy; blocks ads.", "https://adblock.dns.mullvad.net/dns-query", "Privacy & Ad-Blocking", "🛡️"),
        DnsProvider("Mullvad (Base)", "Privacy without the ad-filtering.", "https://base.dns.mullvad.net/dns-query", "Privacy & Ad-Blocking", "🕵️"),
        DnsProvider("Mullvad (Extended)", "Blocks ads, trackers, and malware.", "https://extended.dns.mullvad.net/dns-query", "Privacy & Ad-Blocking", "🛡️"),
        DnsProvider("Control D (Unfiltered)", "Fast, raw resolution.", "https://p0.freedns.controld.com/dns-query", "Privacy & Ad-Blocking", "⚡"),
        DnsProvider("Control D (Ads/Trackers)", "Blocks marketing and telemetry.", "https://p2.freedns.controld.com/dns-query", "Privacy & Ad-Blocking", "🚫"),
        DnsProvider("Control D (Malware)", "Strictly security-focused.", "https://p1.freedns.controld.com/dns-query", "Privacy & Ad-Blocking", "🛡️"),
        DnsProvider("DNS.SB", "Privacy-focused; supports DoQ.", "https://doh.sb/dns-query", "Privacy & Ad-Blocking", "🏁"),
        DnsProvider("AhaDNS", "Community-driven; no logging.", "https://doh.ahadns.net/dns-query", "Privacy & Ad-Blocking", "🌱"),
        DnsProvider("LibreDNS", "Zero-logging node in Germany.", "https://doh.libredns.gr/dns-query", "Privacy & Ad-Blocking", "🔓"),

        // 3. Family & Parental Control
        DnsProvider("Cloudflare Family", "Blocks Malware + Adult content.", "https://family.cloudflare-dns.com/dns-query", "Family & Parental Control", "👨‍👩‍👧‍👦"),
        DnsProvider("Cloudflare Security", "Blocks Malware only.", "https://security.cloudflare-dns.com/dns-query", "Family & Parental Control", "🛡️"),
        DnsProvider("CleanBrowsing (Family)", "Highest filter; blocks VPNs/Proxies.", "https://doh.cleanbrowsing.org/doh/family-filter/", "Family & Parental Control", "👨‍👩‍👧‍👦"),
        DnsProvider("CleanBrowsing (Adult)", "Blocks adult content only.", "https://doh.cleanbrowsing.org/doh/adult-filter/", "Family & Parental Control", "🔞"),
        DnsProvider("AdGuard Family", "Ad-blocking + Kid-safe filtering.", "https://family.adguard-dns.com/dns-query", "Family & Parental Control", "👨‍👩‍👧‍👦"),
        DnsProvider("Control D (Family)", "One-click family protection.", "https://family.freedns.controld.com/dns-query", "Family & Parental Control", "👨‍👩‍👧‍👦"),

        // 4. Regional & Community Nodes
        DnsProvider("DNS4EU", "Official EU project (Child/No Ads).", "https://dns4eu.eu/dns-query", "Regional & Community", "🇪🇺"),
        DnsProvider("AliDNS", "Alibaba’s public DNS (China).", "https://dns.alidns.com/dns-query", "Regional & Community", "🇨🇳"),
        DnsProvider("DNSPod", "Tencent/DNSPod (China).", "https://doh.pub/dns-query", "Regional & Community", "🇨🇳")
    )
}
