#!/usr/bin/env sh
# bootstrap.sh — one-shot: fetch upstream + apply patches
set -eu
ROOT=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd)
echo "JusBrowse bootstrap"
echo "==================="
"$ROOT/scripts/fetch-upstream.sh"
echo ""
"$ROOT/scripts/apply-patches.sh"
echo ""
echo "Ready. Next: ./scripts/build.sh"
