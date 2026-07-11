#!/usr/bin/env bash
# Kerangka kerja cross-compile OpenJDK 17 -> aarch64-linux-android (bionic libc).
#
# STATUS: belum tereksekusi/tervalidasi. Ditulis berdasarkan riset publik
# terhadap struktur build recipe openjdk-17 di proyek termux-packages
# (lisensi patch tersebut mengikuti OpenJDK sendiri - GPLv2 dengan Classpath
# Exception, jadi boleh diadaptasi tanpa mengubah lisensi AIDE - lihat
# docs/PACKAGE_REPO.md §6.1). Skeleton ini TIDAK menyalin isi patch siapapun;
# baru mendokumentasikan urutan langkah kerja dari riset teknis publik.
#
# Prasyarat yang TIDAK tersedia di sandbox tempat skeleton ini ditulis:
#   - Android NDK (untuk cross-compile bagian native JVM/hotspot)
#   - Source OpenJDK 17 (perlu akses jaringan ke openjdk.org / GitHub OpenJDK)
#   - Device/emulator aarch64 untuk validasi hasil build
# Jalankan skeleton ini di lingkungan dev lokal atau GitHub Actions runner
# yang sudah dilengkapi prasyarat di atas.
#
# Usage: build-openjdk17.sh <ndk-path> <output-staging-dir>

set -euo pipefail

if [[ $# -ne 2 ]]; then
  echo "Usage: $0 <ndk-path> <output-staging-dir>" >&2
  exit 1
fi

ndk_path="$1"
staging_dir="$2"
target_triple="aarch64-linux-android"
android_api_level=26   # samakan dengan minSdk di app/build.gradle.kts

echo "=== [1/6] Validasi prasyarat ==="
if [[ ! -d "$ndk_path" ]]; then
  echo "NDK tidak ditemukan di $ndk_path - unduh dari https://developer.android.com/ndk" >&2
  exit 1
fi

echo "=== [2/6] Ambil source OpenJDK 17 ==="
# TODO: git clone / download tarball source resmi OpenJDK 17
#   (mis. dari repo openjdk/jdk17u, tag jdk-17.0.9+... - pilih tag rilis stabil)
echo "TODO: belum diimplementasikan - lihat komentar di atas"

echo "=== [3/6] Terapkan patch bionic ==="
# TODO: terapkan set patch retarget-bionic. TIDAK menyalin patch termux-packages
# apa adanya - adaptasi wajib retarget path dari /data/data/com.termux/...
# ke path privat AIDE (mis. berbasis Context.getFilesDir() aplikasi, bukan
# path hardcoded - lihat docs/PACKAGE_REPO.md §6.2).
# Kategori patch yang diketahui dibutuhkan (dari riset publik):
#   - definisi target platform Android/bionic di build system OpenJDK
#   - shim untuk fungsi POSIX yang tidak ada di bionic (shared memory,
#     process spawn, iconv)
#   - evaluasi: hapus dependency AWT/Swing/X11/print/audio untuk build
#     headless (AIDE hanya butuh javac + JVM + class library inti)
echo "TODO: belum diimplementasikan - lihat komentar di atas"

echo "=== [4/6] Configure & build ==="
# TODO: invoke OpenJDK build system (configure + make) dengan cross-compiler
# dari $ndk_path menyasar $target_triple, --with-toolchain-type=... dst.
# Referensi umum (bukan dari proyek manapun secara spesifik): dokumentasi
# resmi "Building OpenJDK" di openjdk.org menjelaskan opsi cross-compile.
echo "TODO: belum diimplementasikan - lihat komentar di atas"

echo "=== [5/6] Staging hasil build ==="
mkdir -p "$staging_dir/usr/lib/jvm/17"
# TODO: copy hasil build (bin/, lib/, dst.) ke $staging_dir/usr/lib/jvm/17

echo "=== [6/6] Selesai ==="
echo "Staging siap di $staging_dir - lanjutkan dengan:"
echo "  ./packaging/scripts/build-deb.sh $staging_dir packaging/control/openjdk-17.control <output.deb>"
