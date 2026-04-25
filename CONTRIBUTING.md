# Contributing to JusBrowse

We welcome contributions from researchers, developers, and privacy advocates! Help us win the war against tracking.

---

## 🤝 How to Help

There are many ways to contribute to JusBrowse:
- **Bug Reporting**: Found a leak? A rendering issue? Open a [GitHub Issue](https://github.com/shubh72010/JusBrowse-GeckoView/issues).
- **Security Research**: Test our fingerprinting defenses on sites like BrowserLeaks or Cover Your Tracks and report the results.

- **Documentation**: Help us improve our guides and technical deep-dives.

---

## 💻 Development Workflow

1. **Fork & Clone**: Fork the repository and clone it to your machine.
2. **Setup**: Follow the [Installation Guide](INSTALLATION.md).
3. **Branching**: Create a new branch for your feature or fix (e.g., `feature/onion-routing`).
4. **Code Style**: We follow standard Kotlin style guidelines. Ensure your code is clean and commented, especially in the `security` layer.
5. **PR Templates**: (Coming soon) For now, clearly describe what your PR adds or fixes and include screenshots if relevant.

### A Note on Privacy-First Code
When adding new features:
- **Controlled Telemetry**: Explicitly explain the telemetry and an option to disable it.
- **No Magic Values**: Use named constants and document their origin.
- **Fail Closed**: If a security check fails, the browser should stop, not ignore it.

---

## 🔒 Security Disclosures
If you find a critical security vulnerability or an identity leak, please do **not** open a public issue. Instead, contact the maintainer directly through the project's security channels (see `SECURITY.md`).

---

## ⚖️ License
By contributing to JusBrowse, you agree that your contributions will be licensed under the **GNU GPL v3.0**.

---
*Thank you for helping us build a more private web.*
