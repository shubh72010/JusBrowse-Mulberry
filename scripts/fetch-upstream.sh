#!/usr/bin/env sh
# fetch-upstream.sh — create or update disposable mozilla-central checkout
set -eu

ROOT=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd)
SRC="$ROOT/source/mozilla-central"
CFG="$ROOT/config/upstream.yml"

rev=$(sed -n 's/.*revision:[[:space:]]*//p' "$CFG" | tr -d ' "')
remote=$(sed -n 's/.*remote:[[:space:]]*//p' "$CFG" | tr -d ' "')
branch=$(sed -n 's/.*branch:[[:space:]]*//p' "$CFG" | tr -d ' "')

rev=${rev:-HEAD}
remote=${remote:-https://github.com/mozilla/gecko-dev}
branch=${branch:-master}

if [ ! -d "$SRC/.git" ]; then
  echo "Cloning $remote -> $SRC (this is large, ~GB)..."
  # shallow clone by default; use --no-shallow to get full history if needed
  shallow=${JUSBROWSE_SHALLOW:-1}
  if [ "$shallow" = "1" ]; then
    git clone --depth 1 --branch "$branch" "$remote" "$SRC"
    git -C "$SRC" fetch --depth 1 origin "$rev" 2>/dev/null || git -C "$SRC" fetch origin "$rev" --depth 1 2>/dev/null || true
  else
    git clone "$remote" "$SRC"
  fi
else
  echo "Fetching $remote..."
  git -C "$SRC" fetch origin --prune 2>/dev/null || git -C "$SRC" fetch origin 2>/dev/null || true
fi

echo "Checking out $rev..."
if git -C "$SRC" cat-file -e "$rev^{commit}" 2>/dev/null; then
  git -C "$SRC" checkout --detach "$rev"
else
  echo "Revision $rev not found locally, trying origin/$branch..."
  git -C "$SRC" checkout --detach "origin/$branch"
fi

# ensure mozconfig is present
if [ -f "$ROOT/config/mozconfig" ]; then
  cp "$ROOT/config/mozconfig" "$SRC/mozconfig"
  echo "Installed mozconfig"
fi

echo "Ready: $(git -C "$SRC" rev-parse --short HEAD) at $SRC"
