#!/usr/bin/env sh
# refresh-patches.sh — regenerate patches/*.patch from commits after base
set -eu

ROOT=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd)
SRC="$ROOT/source/mozilla-central"
CFG="$ROOT/config/upstream.yml"
PATCH_DIR="$ROOT/patches"

base=$(sed -n 's/.*revision:[[:space:]]*//p' "$CFG" | tr -d ' "')
if [ -z "$base" ]; then echo "No revision in $CFG" >&2; exit 1; fi

if [ ! -d "$SRC/.git" ]; then echo "Missing $SRC" >&2; exit 1; fi

if git -C "$SRC" rev-parse --verify "$base" >/dev/null 2>&1; then
  : # ok
else
  echo "Base $base not found in $SRC — fetch first" >&2; exit 1
fi

# ensure commits exist after base
count=$(git -C "$SRC" rev-list --count "$base"..HEAD 2>/dev/null || echo 0)
if [ "$count" = "0" ]; then echo "No commits after $base — nothing to export" >&2; exit 0; fi

echo "Exporting $count commit(s) after $base..."

# backup existing patches
mkdir -p "$PATCH_DIR/.bak"
mv "$PATCH_DIR"/*.patch "$PATCH_DIR/.bak/" 2>/dev/null || true

git -C "$SRC" format-patch "$base"..HEAD -o "$PATCH_DIR"

# rename to JusBrowse numbering if needed (keep git numbering)
ls -1 "$PATCH_DIR"/*.patch
echo "Done. Review patches, then delete $PATCH_DIR/.bak/"
