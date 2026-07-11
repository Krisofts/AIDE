#!/usr/bin/env bash
# Generate ulang index apt repo (Packages, Packages.gz, Release) dari isi pool/.
#
# Memakai dpkg-scanpackages & apt-ftparchive (tool standar proyek Debian,
# tersedia di GitHub Actions Ubuntu runner) - dijalankan sebagai CLI eksternal
# saat build CI, TIDAK di-embed/link ke aplikasi AIDE, jadi tidak menimbulkan
# masalah lisensi untuk app Android-nya sendiri (lihat docs/PACKAGE_REPO.md §7).
#
# Usage: generate-index.sh <repo-root>
#   <repo-root> berisi pool/ dan dists/stable/

set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 <repo-root>" >&2
  exit 1
fi

repo_root="$1"
dist_dir="$repo_root/dists/stable/main/binary-aarch64"

mkdir -p "$dist_dir"
cd "$repo_root"

dpkg-scanpackages --arch aarch64 pool/main > "$dist_dir/Packages"
gzip -9c "$dist_dir/Packages" > "$dist_dir/Packages.gz"

release_file="$repo_root/dists/stable/Release"
{
  echo "Origin: AIDE"
  echo "Label: AIDE Package Repository"
  echo "Suite: stable"
  echo "Codename: stable"
  echo "Architectures: aarch64"
  echo "Components: main"
  echo "Date: $(date -Ru)"
} > "$release_file"

echo "MD5Sum:" >> "$release_file"
for f in "main/binary-aarch64/Packages" "main/binary-aarch64/Packages.gz"; do
  if [[ -f "$repo_root/dists/stable/$f" ]]; then
    size=$(stat -c%s "$repo_root/dists/stable/$f")
    sum=$(md5sum "$repo_root/dists/stable/$f" | cut -d' ' -f1)
    echo " $sum $size $f" >> "$release_file"
  fi
done

echo "Index diperbarui di $dist_dir"
