#!/usr/bin/env bash
# Bangun satu file .deb dari direktori staging + file control.
#
# Format kompresi SELALU .xz (control.tar.xz, data.tar.xz) - bukan .zst/.gz -
# supaya bisa diekstrak oleh SdkInstaller AIDE tanpa dependency tambahan,
# sebelum ada apt/dpkg sungguhan di Terminal (lihat docs/PACKAGE_REPO.md §2).
#
# Usage:
#   build-deb.sh <staging-dir> <control-file> <output.deb>
#
# <staging-dir> berisi payload paket dengan path relatif ke root filesystem
# target, misal: <staging-dir>/usr/lib/jvm/17/bin/java
#
# <control-file> adalah file control Debian standar (Package, Version,
# Architecture, Maintainer, Description, dst).

set -euo pipefail

if [[ $# -ne 3 ]]; then
  echo "Usage: $0 <staging-dir> <control-file> <output.deb>" >&2
  exit 1
fi

staging_dir="$1"
control_file="$2"
output_deb="$3"

work_dir="$(mktemp -d)"
trap 'rm -rf "$work_dir"' EXIT

echo "2.0" > "$work_dir/debian-binary"

control_dir="$work_dir/control"
mkdir -p "$control_dir"
cp "$control_file" "$control_dir/control"
(cd "$staging_dir" && find . -type f -exec md5sum {} \;) > "$control_dir/md5sums"
tar -C "$control_dir" -cJf "$work_dir/control.tar.xz" .

tar -C "$staging_dir" -cJf "$work_dir/data.tar.xz" .

mkdir -p "$(dirname "$output_deb")"
ar cr "$output_deb" "$work_dir/debian-binary" "$work_dir/control.tar.xz" "$work_dir/data.tar.xz"

echo "Dibuat: $output_deb"
