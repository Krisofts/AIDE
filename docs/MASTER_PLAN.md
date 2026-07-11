# Master Plan — AIDE (`com.krisoft.aide`)

## 1. Latar Belakang & Tujuan

**AIDE** (Android IDE, `com.aide.ui`) — aplikasi yang memungkinkan orang menulis, membangun, dan menjalankan aplikasi Android langsung dari HP Android — sudah **tidak dilanjutkan pengembangannya**. Kebutuhan akan IDE Android mobile masih ada, dan **[AndroidIDE](https://github.com/AndroidIDEOfficial/AndroidIDE)** telah mengisi ruang itu: open source, aktif dikembangkan, berbasis Gradle sungguhan (bukan build system tiruan), dengan editor, LSP, dan terminal terintegrasi.

Tujuan proyek ini: membangun **AIDE** versi baru dengan package `com.krisoft.aide`, menggunakan AndroidIDE sebagai fondasi arsitektur/kode, lalu di-rebrand dan dikembangkan lebih lanjut dengan fitur diferensiasi.

## 2. Strategi: Fork & Rebrand (bukan tulis ulang dari nol)

Dua opsi dipertimbangkan:

| Opsi | Kecepatan | Legal | Kualitas awal |
|---|---|---|---|
| **A. Fork AndroidIDE, rebrand, lanjutkan** ✅ direkomendasikan | Cepat — MVP dalam hitungan minggu | Wajib GPLv3, source harus tetap terbuka | Tinggi — mewarisi bertahun-tahun matang |
| B. Clean-room rewrite (tulis ulang tanpa lihat source) | Sangat lambat (tahun) | Bebas pilih lisensi sendiri | Rendah di awal, banyak fitur harus dibangun ulang (LSP, editor, aapt compiler in-app) |

**Rekomendasi: Opsi A.** AndroidIDE berlisensi **GPLv3**. Konsekuensinya:

- Source code AIDE **harus tetap open source** (GPLv3 atau kompatibel).
- Wajib menyertakan file `LICENSE` (GPLv3), `NOTICE`/atribusi ke AndroidIDE Official & kontributor upstream, dan riwayat perubahan (changelog) yang jujur bahwa proyek ini adalah fork.
- Tidak boleh mengklaim proyek ini "buatan sendiri dari nol" — harus transparan sebagai fork/rebrand di README & about page.
- Nama & package **boleh** diganti sepenuhnya (`com.krisoft.aide`) — itu bukan pelanggaran lisensi, hanya perlu jaga atribusi.

Jika suatu saat ingin lisensi non-copyleft (mis. untuk closed-source fork komersial), itu **hanya mungkin** dengan clean-room rewrite modul yang bersangkutan — dicatat sebagai opsi jangka panjang, bukan prioritas sekarang.

## 3. Tech Stack (mewarisi dari AndroidIDE)

- **Bahasa**: Kotlin + Java, Gradle (Kotlin DSL) sebagai build system proyek maupun proyek yang dibuat di dalam IDE
- **Editor**: [sora-editor](https://github.com/Rosemoe/sora-editor) (Rosemoe) — code editor performa tinggi untuk Android, dengan tree-sitter untuk syntax highlighting
- **LSP**: implementasi berbasis Eclipse JDT untuk Java, LSP4XML untuk XML; skema `lsp:api`, `lsp:models`, `lsp:java`, `lsp:xml`
- **Terminal**: integrasi [Termux](https://termux.dev/) (terminal-emulator, terminal-view) — sesi persisten, akses `apt`/SDK Manager
- **Build tooling**: Gradle wrapper dijalankan in-app, `tooling-api` untuk komunikasi Gradle, `aaptcompiler`, `java-compiler`, `jdt` sebagai modul kompilasi
- **UI Designer**: `xml-inflater`, `uidesigner` — preview layout XML & drag-drop widget
- **Minimum**: Android Gradle Plugin ≥ 7.2.0 untuk proyek yang dibuka; JDK 11 & 17 didukung untuk build
- **Analytics/logging opsional**: `logsender`, `idestats` (harus opt-in & privacy-respecting di versi kita)

## 4. Arsitektur Modul (peta dari AndroidIDE, ~80+ modul Gradle)

Dikelompokkan berdasarkan tanggung jawab:

- **App shell**: `app`, `common`, `shared`, `preferences`, `resources`
- **Editor**: `editor`, `editor-api`, `editor-treesitter`, `lexers`, `lookup`
- **LSP**: `lsp:api`, `lsp:models`, `lsp:java`, `lsp:xml`
- **Build & tooling**: `gradle-plugin`, `gradle-plugin-config`, `tooling-api`, `build-info`, `aaptcompiler`, `java-compiler`, `jdt`
- **Project management**: `projects`, `templates-api`, `templates-impl`
- **UI Designer**: `uidesigner`, `xml-inflater`, `treeview`
- **Terminal**: modul-modul Termux (4 modul: terminal-emulator, terminal-view, dll.)
- **Infrastruktur internal**: `eventbus`, `eventbus-android`, `eventbus-events`, `annotations`, `annotation-processors`, `annotation-processors-ksp`, `actions`
- **Analytics/logging**: `logger`, `logsender`, `idestats`
- **Testing**: modul test untuk Android, LSP, tooling, unit

Rencana kita: **pertahankan struktur modul ini di awal** (memudahkan sinkronisasi dengan upstream bila perlu cherry-pick perbaikan/security fix), baru pecah/gabung modul setelah rebranding stabil.

## 5. Rebranding — Package `com.krisoft.aide`

Langkah teknis rename (dilakukan di Phase 1 roadmap):

1. Ganti `applicationId` & `namespace` di semua `build.gradle.kts` dari base package AndroidIDE ke `com.krisoft.aide` (+ sub-package per modul, mis. `com.krisoft.aide.editor`, `com.krisoft.aide.lsp.java`, dst.)
2. Pindahkan seluruh source Kotlin/Java ke struktur direktori paket baru, update semua `import`
3. Update `AndroidManifest.xml` (package, exported activities/providers/services, FileProvider authority)
4. Ganti resource identitas: nama aplikasi (`strings.xml` → "AIDE"), ikon launcher, splash screen, warna tema (`colors.xml`), font jika ada
5. Ganti signing key (keystore baru milik proyek ini — **jangan** pakai keystore AndroidIDE)
6. Update semua referensi hardcoded ke domain/URL AndroidIDE (docs link, update-checker endpoint, crash reporting endpoint, F-Droid metadata) ke domain/endpoint milik proyek ini
7. Audit string "AndroidIDE" di seluruh UI/about page → ganti jadi "AIDE", tapi **tetap cantumkan atribusi** ("based on AndroidIDE by AndroidIDE Official, GPLv3") di halaman About/Licenses

## 6. Infrastruktur

- **CI/CD**: GitHub Actions — build matrix (debug/release), lint, unit test per PR; build APK/AAB artifact
- **Signing**: keystore baru, disimpan sebagai GitHub Secret, tidak pernah commit ke repo
- **Distribusi**: mulai dari GitHub Releases (APK langsung), evaluasi F-Droid setelah stabil (F-Droid punya syarat reproducible build & no proprietary deps — perlu audit dependency)
- **Versioning**: semantic versioning (`MAJOR.MINOR.PATCH`), mulai dari `0.1.0-alpha`
- **Issue tracking**: GitHub Issues dengan label `bug`, `feature`, `good-first-issue`, `upstream-sync`

## 7. Risiko & Tantangan

- **Ukuran codebase besar (~80+ modul)** — butuh waktu untuk audit penuh sebelum rebranding aman dilakukan
- **Kotlin LSP belum selesai di upstream** — jika ingin jadi diferensiator, ini kerja besar (butuh riset `kotlin-lsp` resmi JetBrains atau `fwcd/kotlin-language-server`)
- **Kompatibilitas Android versi baru** — perubahan scoped storage, permission runtime, dan restriksi background process bisa mematahkan asumsi lama di codebase
- **Device rendah spesifikasi** — build Gradle in-app + LSP indexing berat di RAM; perlu profiling di perangkat low-end sebagai bagian dari QA
- **Sinkronisasi dengan upstream** — jika AndroidIDE merilis security fix, kita perlu proses untuk cherry-pick tanpa merusak rebranding
- **NDK tidak didukung** di AndroidIDE karena keterbatasan toolchain native di Android — jangan janjikan fitur ini di awal

## 8. Definisi Selesai untuk Phase 0 (fondasi)

- [ ] Lisensi & atribusi (`LICENSE`, `NOTICE`) sudah di tempat
- [ ] Source AndroidIDE ter-fork & bisa di-build ulang tanpa modifikasi (baseline hijau)
- [ ] CI dasar jalan (build + lint)
- [ ] Rencana rename package terdokumentasi per-modul (checklist)
- [ ] Identitas visual (nama, ikon, warna) disetujui
