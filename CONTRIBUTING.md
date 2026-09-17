# Contributing to JusBrowse

JusBrowse is a **patch stack**, not a fork. You never edit mozilla-central
directly in this repo — you add one focused `.patch` at a time. Read the
[README](README.md) first.

## What helps most

1. **Bug reports with repro steps** (device, build, exact taps, `logcat` snippet)
2. **One-feature patches** against the pinned upstream revision
3. **Upstream rebase help** ("patch 0007 no longer applies on new rev")

## Workflow

```sh
./jusbrowse bootstrap   # fetch upstream + apply patches
./jusbrowse build       # must stay green

# make your change inside source/mozilla-central
cd source/mozilla-central
git add <only your feature's files>
git commit -m "JusBrowse: <what it does>"
git format-patch -1 HEAD -o ../../patches/
cd ../..  # rename to next number: 0014-jusbrowse-*.patch
```

## Patch rules

- One logical change per patch. No drive-by refactors.
- No GeckoView changes unless the feature genuinely requires them.
- Must apply cleanly (`./scripts/apply-patches.sh`) **and** build
  (`./jusbrowse build`) — CI-equivalent, run both before opening a PR.
- Name it `NNNN-jusbrowse-<slug>.patch`, next free number.
- Update `patches/README.md`'s table and the README patch map.

## Pull requests

- Fill in the PR template. Small PRs merge fast; giant ones sit.
- One patch per PR unless the patches are useless apart.
- All discussion in English (reports may include logs in any language).

## Release process (maintainers)

Release APKs are built from a clean tree (`fetch → apply → build`), signed
with the permanent JusBrowse key, and published as pre-releases with notes.
Never commit the keystore or passwords — signing comes from environment only.

## License

By contributing, you agree your work is licensed under the
[Mozilla Public License 2.0](LICENSE), same as upstream Firefox.
