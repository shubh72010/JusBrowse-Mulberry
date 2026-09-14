#!/usr/bin/env sh
# update-upstream.sh — test JusBrowse patches against newer upstream revision
# Usage: ./scripts/update-upstream.sh [new-rev]
set -eu

ROOT=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd)
SRC="$ROOT/source/mozilla-central"
CFG="$ROOT/config/upstream.yml"

current=$(sed -n 's/.*revision:[[:space:]]*//p' "$CFG" | tr -d ' "')
target=${1:-origin/master}

if [ ! -d "$SRC/.git" ]; then echo "Missing $SRC — run ./scripts/fetch-upstream.sh" >&2; exit 1; fi

git -C "$SRC" fetch origin --prune 2>/dev/null || true

if ! git -C "$SRC" cat-file -e "$target^{commit}" 2>/dev/null; then
  echo "Target $target not found" >&2; exit 1
fi

target_hash=$(git -C "$SRC" rev-parse "$target")

echo "Fetching Mozilla Central..."
echo "Current: $current"
echo "Target:  $target_hash ($target)"
echo ""

# checkout target detached
git -C "$SRC" checkout --detach "$target_hash" 2>/dev/null
git -C "$SRC" am --abort 2>/dev/null || true

echo "Applying JusBrowse patches:"
if "$ROOT/scripts/apply-patches.sh"; then
  echo ""
  echo "All patches applied successfully."
  echo "Running build..."
  if "$ROOT/scripts/build.sh" 2>&1 | tail -n 20; then
    echo ""
    echo "Build successful."
    echo "Update $CFG to accept new revision:"
    echo "  revision: $target_hash"
    # ponytail: do not auto-write config, human confirms
  else
    echo "Build failed — not updating pinned revision" >&2; exit 1
  fi
else
  echo ""
  echo "Patch conflict detected. Status: NEEDS_REBASE"
  echo "Fix the failing patch, then update $CFG."
  exit 1
fi
