# Patches

Each file is a `git format-patch` commit against the pinned mozilla-central revision in `config/upstream.yml`.

## Ordering

Lexicographic order = apply order (`git am patches/*.patch`).

```
0001-jusbrowse-branding.patch   # app_name, applicationId
0002-jusbrowse-icon.patch        # launcher icons (future)
0003-jusbrowse-toolbar.patch     # toolbar behavior (future)
...
```

## Creating a new patch

```sh
# inside source/mozilla-central
git checkout <pinned-revision>
# ... make ONE focused change ...
git add <files>
git commit -m "JusBrowse: <what>"
git format-patch -1 HEAD -o ../../patches/
# rename to next number, e.g. 0004-jusbrowse-*.patch
```

## Refreshing after rebase

```sh
./scripts/refresh-patches.sh   # regenerates 0001.. from commits after base
```

## Applying

```sh
./scripts/apply-patches.sh
```
