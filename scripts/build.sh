#!/usr/bin/env sh
# build.sh — build Fenix from patched source
set -eu

ROOT=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd)
SRC="$ROOT/source/mozilla-central"

if [ ! -d "$SRC/.git" ]; then echo "Missing $SRC — run ./scripts/fetch-upstream.sh" >&2; exit 1; fi

# ensure mozconfig exists
if [ ! -f "$SRC/mozconfig" ] && [ -f "$ROOT/config/mozconfig" ]; then
  cp "$ROOT/config/mozconfig" "$SRC/mozconfig"
fi

echo "Building Fenix (this may take a while)..."
exec "$SRC/mach" --log-no-times gradle fenix:assembleDebug "$@"
