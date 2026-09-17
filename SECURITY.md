# Security Policy

JusBrowse is **pre-alpha** software. It has not had a security audit. Do not
rely on it for high-risk browsing yet.

## Reporting a vulnerability

**Do not open a public issue.** Use GitHub's private reporting:

**Security tab → Report a vulnerability** (or go to
`https://github.com/shubh72010/JusBrowse-Mulberry/security/advisories/new`).

Include:

- JusBrowse version (release tag, e.g. `v0.0.3A-2`) and device/Android version
- What you did, what you expected, what happened
- Whether upstream Firefox is also affected (check first if you can — if yes,
  it should go to Mozilla via [Bugzilla](https://bugzilla.mozilla.org/) too)

## What happens next

- Acknowledgement within a few days, fix or mitigation plan as soon as
  practical.
- Most JusBrowse issues are **patch-stack issues**: if the bug is in our
  patches, we fix the patch; if it's upstream, we rebase onto Mozilla's fix.
- Credit in the release notes if you want it.

## Scope notes

- The release signing key lives outside this repo (environment-only). Key
  handling questions go through private reporting, never issues.
- `source/` is a disposable upstream checkout — vulnerabilities there belong
  to Mozilla; tell us and we'll rebase.
