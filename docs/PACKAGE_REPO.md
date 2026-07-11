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

Ini pekerjaan paling berat & butuh riset khusus — cross-compile OpenJDK agar target `aarch64-linux-android` (bionic libc) bukan pekerjaan trivial. **Belum diselesaikan di sesi ini** — dicatat sebagai item riset Phase 0 tersendiri:

- [ ] Cari & audit lisensi resep build yang sudah ada untuk referensi teknik (bukan nyalin kode) — mis. build recipe `openjdk-17` di proyek sejenis biasanya berbasis Android NDK toolchain + patch source OpenJDK untuk bionic. **Resep build (build script) dan hasil binary JDK adalah dua hal terpisah secara lisensi** — OpenJDK sendiri berlisensi GPLv2 dengan Classpath Exception (bebas dipakai/didistribusikan ulang, tidak memaksa aplikasi yang jalan di atasnya ikut GPL), tapi *script/resep* cross-compile milik proyek lain mungkin py lisensi sendiri yang perlu dicek sebelum diadaptasi.
- [ ] Setup toolchain: Android NDK (untuk cross-compile C/C++ bagian JVM) + source OpenJDK 17
- [ ] Hasil build dikemas jadi `.deb` sesuai struktur §2-3 di atas

## 7. Pipeline CI (Skeleton)

`.github/workflows/build-packages.yml` (dibuat di repo ini) — kerangka pipeline yang MEMBANGUN & MEMPUBLIKASIKAN paket begitu resep build tersedia. Saat ini isinya placeholder untuk step cross-compile (lihat §6) — job packaging (bikin `.deb` dari direktori hasil build, generate ulang `Packages`/`Release`, deploy ke GitHub Pages) sudah fungsional dan bisa dipakai begitu ada artifact untuk dikemas.

## 8. Konsumsi di AIDE (Sekarang vs Nanti)

- **Sekarang (sebelum Terminal/Phase 3 ada)**: `SdkInstaller` (`com.krisoft.aide.sdkmanager`) diperluas mendukung ekstraksi `.deb` langsung (unwrap `ar` → ambil `data.tar.xz` → ekstrak seperti tar.xz biasa) — SDK Manager bisa pasang JDK dari repo ini tanpa butuh `apt` sama sekali.
- **Nanti (setelah Terminal ada)**: repo yang sama bisa langsung dipakai oleh `apt`/`dpkg` sungguhan di dalam Terminal (`apt install openjdk-17` dari `sources.list` yang menunjuk ke domain AIDE) — tidak perlu ubah format, cukup tambah client apt yang sesungguhnya.

## 9. Status

🏗️ Kerangka struktur repo & script pembantu (`packaging/scripts/`) sudah dibuat. **Belum ada paket nyata** — `pool/` masih kosong, item riset di §6 (cross-compile OpenJDK) belum dikerjakan. Ini fondasi struktural, bukan repo paket siap pakai.
