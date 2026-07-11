# AIDE Package Repo — Rancangan Repo Apt Bionic-Native

## 1. Kenapa AIDE Butuh Ini

JDK & Android build-tools resmi dikompilasi untuk glibc desktop, bukan bionic libc Android (lihat `docs/MASTER_PLAN.md` §7). Riset menunjukkan pola yang terbukti jalan di ekosistem ini: paket **native bionic** (bukan userland Ubuntu/proot — proot GPLv3 & rusak di Android 15 tanpa root) didistribusikan lewat repo apt statis yang di-hosting sebagai file biasa (mis. GitHub/GitLab Pages), diinstal langsung tanpa container.

**Keputusan**: AIDE membangun & meng-host repo apt bionic-native **milik sendiri** — bukan bergantung ke repo pihak ketiga (rapuh, di luar kendali, dan tidak pantas dipakai produk kompetitor).

## 2. Format: Debian `.deb` Asli, Bukan Format Custom

Supaya repo ini otomatis kompatibel begitu Terminal (Phase 3) punya `apt`/`dpkg` sungguhan, paket dibuat dalam **format `.deb` standar** (bukan custom), dengan aturan tetap:

- `debian-binary` — teks `2.0\n`
- `control.tar.xz` — metadata paket (`control`, `md5sums`)
- `data.tar.xz` — payload (isi file sebenarnya, path relatif ke root, mis. `./usr/lib/jvm/17/bin/java`)

**Kompresi selalu `.xz`** (bukan `.zst`/`.gz`) — pilihan sengaja supaya installer kita (`SdkInstaller`, sudah ada dependency `commons-compress` + `xz-java`) bisa ekstrak `.deb` tanpa dependency tambahan (mis. `zstd-jni`), *sebelum* ada `apt`/`dpkg` sungguhan di Phase 3.

## 3. Struktur Direktori Repo (`packaging/repo/`)

Layout standar Debian apt repo (flat repository, satu component `main`, satu arsitektur `binary-aarch64` untuk awal — mayoritas device Android target adalah arm64):

```
packaging/repo/
  dists/
    stable/
      Release                          # index rilis: daftar komponen, checksum Packages
      main/
        binary-aarch64/
          Packages                     # daftar semua paket + metadata (versi, checksum, path)
          Packages.gz
  pool/
    main/
      o/
        openjdk-17/
          openjdk-17_17.0.9_aarch64.deb
      a/
        androidide-tools-clone/        # nama beda dari upstream - lihat catatan clean-room §5
          ...
```

Konvensi path `pool/main/<huruf-pertama>/<nama-paket>/<paket>_<versi>_<arch>.deb` mengikuti konvensi umum repo Debian/APT (bukan kekayaan intelektual siapapun — ini format terbuka yang didokumentasikan publik oleh proyek Debian).

## 4. Hosting

**GitHub Pages** dari branch/direktori khusus di repo ini (atau repo terpisah `krisofts/aide-packages` kalau sudah besar — mengikuti pola AndroidIDE yang memisahkan `androidide-tools` dari app utama, tapi mulai di repo ini dulu supaya sederhana). URL akhir: `https://krisofts.github.io/AIDE/packaging/repo/` (atau domain khusus kalau ada nanti).

## 5. Nama Paket — Hindari Klaim Identitas Proyek Lain

Prinsip clean-room (`docs/MASTER_PLAN.md` §2) juga berlaku di sini: kalau AIDE membangun ulang alat bantu setara `androidide-tools` (mis. script setup SDK), **nama paketnya harus beda** (mis. `aide-sdk-tools`, bukan `androidide-tools`) supaya tidak menyiratkan itu produk/berasal dari AndroidIDE.

## 6. Paket Pertama: OpenJDK 17 (bionic-native)

### 6.1 Riset lisensi & kelayakan (selesai, Juli 2026)

`termux-packages` (repo yang memelihara build recipe untuk ratusan paket bionic-native, termasuk `openjdk-17`) memakai **lisensi ganda yang eksplisit**: infrastruktur build-nya sendiri Apache 2.0, sedangkan script/patch untuk tiap paket **mengikuti lisensi software yang dibungkus**. Untuk `openjdk-17`, itu berarti patch-nya ikut lisensi OpenJDK sendiri — **GPLv2 dengan Classpath Exception**, lisensi yang secara desain mengizinkan JDK dipakai aplikasi apapun (termasuk closed-source atau berlisensi permissive seperti AIDE/Apache 2.0) tanpa "menular" ke aplikasi tersebut.

**Kesimpulan**: AIDE boleh secara legal mengadaptasi patch `openjdk-17` dari `termux-packages` sebagai titik awal (bukan menulis ~35 patch dari nol), dengan syarat: hasil adaptasi patch itu sendiri tetap GPLv2+Classpath Exception (mengikuti OpenJDK), dan atribusi ke `termux-packages` dicantumkan. Ini **tidak mengubah lisensi AIDE** (Apache 2.0) — sama seperti bundling OpenJDK resmi, batasannya hanya berlaku ke source JDK itu sendiri, bukan ke aplikasi AIDE yang menjalankannya.

### 6.2 Skala & bentuk pekerjaan (dari riset publik build recipe termux-packages)

- Sekitar **35 patch** dibutuhkan (turun dari 100+ setelah disederhanakan bertahun-tahun oleh komunitas) — bounded, bukan open-ended.
- Adaptasi teknis kunci yang diketahui: flag compiler khusus (mis. penanda target Android), library pengganti untuk fungsi yang tidak tersedia di bionic (shim shared-memory POSIX, shim process-spawn, shim iconv — bionic tidak menyediakan implementasi native untuk beberapa API POSIX yang diasumsikan glibc), dan path runtime yang di-hardcode ke lokasi spesifik Termux — **harus di-retarget ke `com.krisoft.aide`**, bukan dipakai apa adanya.
- Mendukung target Android 7+ — cocok dengan `minSdk 26` (Android 8) AIDE.
- Dependency build mengarah ke stack desktop GUI penuh (font, X11, print, audio) karena Termux menyasar JDK desktop lengkap (Swing/AWT). **Peluang untuk AIDE**: kita hanya butuh `javac` + JVM + class library inti untuk menjalankan Gradle/JDT/`sdkmanager` — bukan GUI Java penuh — jadi kemungkinan bisa dipangkas jadi build **headless**, mengurangi dependency & waktu build dibanding target Termux.

### 6.3 Yang belum bisa diselesaikan di sesi ini (keterbatasan sandbox, bukan diabaikan)

Sandbox tempat saya bekerja **tidak punya Android NDK, tidak punya device/emulator untuk uji biner hasil cross-compile, dan proxy jaringannya memblokir domain yang dibutuhkan** (source OpenJDK, NDK, dll). Cross-compile sungguhan butuh jam-an waktu build + validasi di device fisik — tidak realistis diselesaikan sebagai satu langkah di sini. Yang saya siapkan: kerangka langkah kerja (`packaging/scripts/build-openjdk17.sh`) yang mendokumentasikan urutan kerja berdasarkan riset di atas, supaya siap dieksekusi di lingkungan dengan NDK terpasang (mesin dev lokal atau GitHub Actions runner khusus).

- [ ] Eksekusi nyata: jalankan skeleton di lingkungan ber-NDK, uji biner di device fisik aarch64
- [ ] Retarget semua path hardcoded dari `com.termux` ke `com.krisoft.aide`
- [ ] Evaluasi build headless (tanpa AWT/Swing/X11) untuk kurangi scope dependency
- [ ] Hasil build dikemas jadi `.deb` sesuai struktur §2-3 di atas, pakai `packaging/scripts/build-deb.sh`

## 7. Pipeline CI (Skeleton)

`.github/workflows/build-packages.yml` (dibuat di repo ini) — kerangka pipeline yang MEMBANGUN & MEMPUBLIKASIKAN paket begitu resep build tersedia. Saat ini isinya placeholder untuk step cross-compile (lihat §6) — job packaging (bikin `.deb` dari direktori hasil build, generate ulang `Packages`/`Release`, deploy ke GitHub Pages) sudah fungsional dan bisa dipakai begitu ada artifact untuk dikemas.

## 8. Konsumsi di AIDE (Sekarang vs Nanti)

- **Sekarang (sebelum Terminal/Phase 3 ada)**: `SdkInstaller` (`com.krisoft.aide.sdkmanager`) diperluas mendukung ekstraksi `.deb` langsung (unwrap `ar` → ambil `data.tar.xz` → ekstrak seperti tar.xz biasa) — SDK Manager bisa pasang JDK dari repo ini tanpa butuh `apt` sama sekali.
- **Nanti (setelah Terminal ada)**: repo yang sama bisa langsung dipakai oleh `apt`/`dpkg` sungguhan di dalam Terminal (`apt install openjdk-17` dari `sources.list` yang menunjuk ke domain AIDE) — tidak perlu ubah format, cukup tambah client apt yang sesungguhnya.

## 9. Status

🏗️ Kerangka struktur repo & script pembantu (`packaging/scripts/`) sudah dibuat. **Belum ada paket nyata** — `pool/` masih kosong, item riset di §6 (cross-compile OpenJDK) belum dikerjakan. Ini fondasi struktural, bukan repo paket siap pakai.
