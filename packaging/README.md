# AIDE Package Repo

Kerangka repo apt bionic-native milik AIDE sendiri — lihat rancangan lengkap di [`docs/PACKAGE_REPO.md`](../docs/PACKAGE_REPO.md).

- `repo/` — struktur repo apt (`dists/`, `pool/`) yang di-hosting statis (GitHub Pages)
- `scripts/build-deb.sh` — bangun satu `.deb` dari direktori staging + file control
- `scripts/generate-index.sh` — generate ulang `Packages`/`Release` dari isi `pool/`

**Status**: kerangka struktural, belum ada paket nyata. Item terbesar yang belum dikerjakan: resep build cross-compile OpenJDK 17 ke bionic libc (`aarch64-linux-android`) — lihat `docs/PACKAGE_REPO.md` §6.
