#!/usr/bin/env sh
# apply-patches.sh — apply patches/*.patch sequentially via git am
set -eu

ROOT=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd)
SRC="$ROOT/source/mozilla-central"
PATCH_DIR="$ROOT/patches"

if [ ! -d "$SRC/.git" ]; then
  echo "Missing $SRC — run ./scripts/fetch-upstream.sh first" >&2
  exit 1
fi

# ensure clean tree (no leftover am)
git -C "$SRC" am --abort 2>/dev/null || true

count=0
failed=""

for p in "$PATCH_DIR"/*.patch; do
  [ -e "$p" ] || { echo "No patches in $PATCH_DIR"; exit 0; }
  base=$(basename "$p")
  printf "Applying %-36s ... " "$base"
  if git -C "$SRC" am --3way --keep-non-patch "$p" >/tmp/jusbrowse-am.log 2>&1; then
    echo "✓"
    count=$((count+1))
  else
    echo "✗ CONFLICT"
    echo "  log: /tmp/jusbrowse-am.log"
    echo "  fix inside $SRC, then:"
    echo "    git -C source/mozilla-central am --continue"
    echo "  or abort:"
    echo "    git -C source/mozilla-central am --abort"
    failed="$failed $base"
    break
  fi
done

if [ -n "$failed" ]; then
  echo ""
  echo "Patch conflict detected. Status: NEEDS_REBASE"
  echo "Failed:$failed"
  exit 1
fi

echo ""
echo "All patches applied ($count): $(git -C "$SRC" rev-parse --short HEAD)"
