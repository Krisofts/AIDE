# Master Plan — AIDE (`com.krisoft.aide`)

## 1. Latar Belakang & Tujuan

**AIDE** (Android IDE, `com.aide.ui`) — aplikasi yang memungkinkan orang menulis, membangun, dan menjalankan aplikasi Android langsung dari HP Android — sudah **tidak dilanjutkan pengembangannya**. Kebutuhan akan IDE Android mobile masih ada, dan dua proyek mengisi ruang itu saat ini:

- **[AndroidIDE](https://github.com/AndroidIDEOfficial/AndroidIDE)** — open source (GPLv3), aktif dikembangkan, fokus native Android (Java/Kotlin + Gradle), dengan editor, LSP, dan terminal terintegrasi.
- **[ACSIDE](https://github.com/AndroidCSIDE/ACSIDE)** (Android Code Studio) — lebih ambisius: dukungan Flutter/Dart selain Android native, multi-language server (Java, Kotlin, Python, Bash, Clang, Dart), terminal dengan lingkungan Ubuntu + Termux-X11 (bisa jalankan aplikasi GUI), sistem plugin, dan AI Agent dengan dukungan Model Context Protocol (MCP). **Saat ini closed-source** (rencana open-source setelah stabil menurut deskripsi proyeknya) — artinya tidak ada source code yang bisa dilihat sama sekali; referensi hanya dari README, deskripsi fitur publik, dan pengalaman memakai aplikasinya.

Tujuan proyek ini: membangun **AIDE** versi baru dengan package `com.krisoft.aide`, **ditulis ulang dari nol (clean-room)**, terinspirasi dari fitur & pengalaman pengguna kedua proyek di atas — bukan hasil fork/salinan source code manapun. Status closed-source ACSIDE justru menegaskan pendekatan ini: untuk ACSIDE, "clean-room" bukan pilihan strategi, melainkan satu-satunya cara — tidak ada source untuk difork sama sekali.

## 2. Strategi: Clean-Room Rewrite (bukan fork)

> **Keputusan terbaru**: proyek ini dibangun sebagai karya orisinal, terinspirasi AndroidIDE dari sisi fitur & UX, **tanpa menyalin source code AndroidIDE**. Ini mengubah beberapa hal mendasar dibanding rencana fork sebelumnya — lihat perbandingan di bawah.

| Aspek | Fork (rencana lama) | Clean-room (rencana saat ini) |
|---|---|---|
| Kecepatan MVP | Cepat (minggu) | Lambat — estimasi realistis **18–24 bulan** untuk versi matang |
| Lisensi | Wajib GPLv3 | **Bebas dipilih** (rekomendasi Apache 2.0/MIT, bisa juga proprietary nanti) |
| Kewajiban legal | Harus tetap open source, atribusi wajib | Tidak ada kewajiban ke AndroidIDE — tapi tetap wajib patuh lisensi tiap library pihak ketiga yang benar-benar dipakai |
| Kualitas awal | Tinggi (warisan bertahun-tahun) | Rendah di awal — semua logika inti (editor glue, LSP client, build orchestration, terminal, UI designer) ditulis sendiri |

### Prinsip "Clean-Room" — wajib dipatuhi tim

Supaya tidak dianggap *derivative work* dari kode GPLv3 AndroidIDE (yang akan memaksa proyek ini ikut GPLv3 secara hukum):

1. **Dilarang membaca atau menyalin source code AndroidIDE** saat menulis komponen inti (editor integration, LSP client, build orchestrator, terminal, UI designer). Referensi hanya boleh dari: pengalaman memakai aplikasinya (behavior/UX), dokumentasi publik, dan spesifikasi format file standar (mis. format proyek Gradle, `AndroidManifest.xml` — ini bukan milik AndroidIDE).
2. Requirement/spec fitur ditulis dalam bahasa fungsional ("saat user mengetik `.`, tampilkan daftar method yang valid") — bukan dengan menyalin struktur kode/algoritma dari AndroidIDE.
3. Kalau ada kontributor yang pernah membaca source AndroidIDE secara mendalam (mis. pernah jadi kontributor upstream), sebaiknya **tidak** menulis modul yang setara langsung — untuk menghindari risiko "tainted" clean-room secara hukum.
4. **Boleh** menyebut AndroidIDE sebagai inspirasi/referensi produk secara terbuka (di README, marketing) — itu bukan masalah hukum, yang jadi masalah adalah menyalin *kode*-nya.

### Boleh pakai library pihak ketiga independen

Menulis ulang "dari nol" **bukan berarti tidak boleh pakai library apapun**. AndroidIDE sendiri juga menyusun aplikasinya dari komponen open source pihak ketiga yang independen — kita boleh memakai komponen yang sama, karena itu bukan "menyalin AndroidIDE", melainkan memakai dependency terpisah yang tersedia untuk siapa saja:

| Komponen | Lisensi | Aman dipakai? |
|---|---|---|
| [sora-editor](https://github.com/Rosemoe/sora-editor) (Rosemoe) | Apache 2.0 | ✅ Aman — permissive, bisa dipakai langsung sebagai dependency |
| Eclipse JDT (Java language tooling) | EPL 2.0 | ✅ Umumnya aman dipakai sebagai library terpisah (weak copyleft di level file, bukan "menular" ke seluruh app) — tetap perlu review legal kalau target lisensi akhir proprietary |
| Gradle Tooling API | Apache 2.0 | ✅ Aman |
| AAPT2 / Android build tools (bagian Android SDK) | Apache 2.0 | ✅ Aman |
| **Termux terminal-emulator / terminal-view** | **GPLv3** | ⚠️ **Hindari** kalau target lisensi AIDE non-GPL — GPLv3 "menular" ke aplikasi yang me-link-nya. Perlu cari **alternatif** (lib terminal emulator permissive lain) atau **tulis emulator terminal sendiri** (VT100/xterm subset) |

Setiap dependency baru yang mau dipakai **wajib dicek lisensinya dulu** sebelum diintegrasikan — masuk ke checklist Phase 0.

## 3. Lisensi AIDE

Karena tidak lagi terikat GPLv3 dari AndroidIDE, lisensi AIDE **bebas ditentukan sendiri**. Rekomendasi:

- **Apache 2.0** — permissive, mengizinkan penggunaan komersial oleh siapapun, ada klausul paten (perlindungan tambahan), dan kompatibel dengan sebagian besar dependency di atas (termasuk sora-editor yang juga Apache 2.0).
- Alternatif: **MIT** (lebih sederhana, tanpa klausul paten) kalau ingin lebih minimal.
- Kalau suatu saat ingin closed-source/komersial murni (proprietary), itu juga memungkinkan — tapi hindari dependency GPL (seperti Termux di atas) sejak awal supaya opsi ini tidak tertutup.

**Keputusan final lisensi harus diambil di Phase 0** sebelum kode pertama ditulis, karena mengubah lisensi setelah banyak kontributor masuk jadi jauh lebih rumit (butuh persetujuan semua kontributor code sebelumnya).

## 4. Tech Stack (target, bukan warisan langsung)

- **Bahasa**: Kotlin + Java untuk aplikasi; Gradle (Kotlin DSL) sebagai build system proyek yang dibuat/dibuka di dalam IDE
- **Editor**: [sora-editor](https://github.com/Rosemoe/sora-editor) sebagai dependency (bukan menyalin kode integrasinya dari AndroidIDE) — glue code (bagaimana editor terhubung ke LSP, file system, dsb.) ditulis sendiri
- **Code intelligence**: klien LSP ditulis sendiri, berbicara ke language server independen (Eclipse JDT untuk Java, riset lebih lanjut untuk Kotlin — lihat §7 Risiko)
- **Terminal**: **belum ditentukan** — riset alternatif non-GPL di Phase 0 (opsi: tulis emulator terminal minimal sendiri, atau cari library permissive lain)
- **Build tooling**: Gradle Tooling API (Apache 2.0) untuk orkestrasi build in-app; logika orkestrasi & UI progress ditulis sendiri
- **UI Designer**: layout inflater & drag-drop editor ditulis sendiri, menggunakan Android `LayoutInflater` API standar (bagian platform Android, bukan kode AndroidIDE)
- **Minimum**: Android Gradle Plugin ≥ 7.2.0 untuk proyek yang dibuka; JDK 11 & 17 didukung untuk build

## 5. Arsitektur — Dirancang Sendiri (boleh terinspirasi struktur, bukan kode)

Modularisasi Gradle multi-module tetap masuk akal secara arsitektur (ini pola umum, bukan kekayaan intelektual AndroidIDE), tapi isi & implementasi tiap modul ditulis dari nol:

- **App shell**: `app`, `common`, `preferences`, `resources`
- **Editor**: `editor` (wrapper di atas sora-editor), `syntax-highlighting`
- **Code intelligence**: `lsp-client`, `lsp-java`, `lsp-xml`, (nanti) `lsp-kotlin`
- **Build & tooling**: `build-orchestrator` (di atas Gradle Tooling API), `project-model`
- **Project management**: `projects`, `templates`
- **UI Designer**: `ui-designer`, `xml-preview`
- **Terminal**: `terminal` (implementasi/lib terpilih hasil riset Phase 0)
- **Infrastruktur internal**: `eventbus`, `logger`

Struktur ini **starting point**, bukan final — akan berevolusi seiring implementasi nyata (berbeda dengan rencana fork sebelumnya yang mempertahankan struktur modul AndroidIDE apa adanya).

## 6. Package `com.krisoft.aide`

Karena dibangun dari nol, tidak ada proses "rename" — package `com.krisoft.aide` dipakai sejak commit pertama:

- `applicationId` & `namespace`: `com.krisoft.aide` (+ sub-package per modul, mis. `com.krisoft.aide.editor`, `com.krisoft.aide.lsp.java`)
- Identitas visual (nama "AIDE", ikon, splash, warna tema) didesain sejak awal, tidak perlu "ganti dari identitas AndroidIDE"
- Signing key milik proyek sendiri sejak awal

## 7. Risiko & Tantangan (jauh lebih besar dibanding rencana fork)

- **Estimasi waktu jauh lebih panjang** — semua komponen inti (editor glue, LSP client, build orchestrator, terminal, UI designer) dibangun dari nol. Realistis: 18–24 bulan untuk versi yang benar-benar dipakai harian, bukan 9–12 bulan seperti rencana fork.
- **Risiko "clean-room tercemar"** — kalau ada kontributor yang tanpa sadar meniru struktur/algoritma dari AndroidIDE karena pernah baca source-nya, status clean-room bisa dipertanyakan secara hukum. Perlu proses review yang disiplin (lihat §2).
- **Setup wizard/onboarding rawan dead-end** — percobaan langsung terhadap ACSIDE (Juli 2026) menunjukkan proses setup environment (download & ekstraksi rootfs Ubuntu, lalu step pemilihan versi JDK) bisa berhenti total dengan pesan generik ("JDK selection cancelled or invalid") tanpa jalan retry yang jelas — user harus mulai ulang dari awal. Pelajaran untuk AIDE: setiap step onboarding/setup environment (termasuk pemilihan JDK, ekstraksi rootfs/terminal env) **wajib punya jalur retry eksplisit** dan pesan error yang menjelaskan apa yang bisa dilakukan user, bukan cuma gagal diam-diam. Ini masuk sebagai kriteria QA wajib di Phase 1 (build orchestrator) & Phase 3 (setup terminal) di roadmap.
- **Terminal emulator dari nol/cari alternatif** — ini pekerjaan non-trivial (parsing escape sequence VT100/xterm, PTY handling di Android). Salah satu risiko teknis terbesar kalau memutuskan tidak pakai Termux.
- **Cara JDK & Android build-tools benar-benar dijalankan di Android belum diputuskan** — build resmi JDK/aapt2/d8 dari upstream (Temurin, Google) dikompilasi untuk Linux/Mac/Windows desktop (glibc), bukan untuk Android (bionic libc), jadi tidak bisa didownload & langsung dieksekusi apa adanya. Dua opsi: (a) jalankan di dalam Linux userland/proot (glibc lengkap, kompatibel dengan build standar, tapi berat — pendekatan mirip sandbox Ubuntu ACSIDE), atau (b) sumber/build sendiri JDK & build-tools yang dikompilasi native untuk bionic (lebih ringan, tapi effort tinggi & sumber binary harus jelas lisensi & keasliannya). **Keputusan ini satu paket dengan riset terminal di Phase 0** — kemungkinan besar solusi teknis yang sama (userland/proot) menjawab kebutuhan terminal maupun eksekusi JDK/build-tools sekaligus.
  - **Temuan dari riset publik (Juli 2026)**: berdasarkan README & issue publik proyek `AndroidCSOfficial/android-code-studio` (GPLv3, satu lineage dengan AndroidIDE), pola yang dipakai bukan "custom installer untuk tiap komponen SDK", melainkan: bootstrap satu JDK yang bisa jalan + download Android *command-line tools* resmi dari Google (paket ini berisi kode Java portable + script launcher, jadi tidak terikat masalah bionic/glibc seperti JDK), lalu **pasang platform/build-tools/platform-tools lewat tool `sdkmanager` resmi itu sendiri** (dijalankan via Terminal), bukan reimplementasi sendiri. Mereka juga secara eksplisit mencatat NDK tidak bisa dipasang lewat jalur ini karena toolchain-nya tidak dikompilasi untuk Android — mengkonfirmasi batasan bionic/glibc di atas.
  - **Keputusan desain AIDE mengikuti pola ini**: `SdkComponent` sekarang punya field `installMethod` (`DIRECT_DOWNLOAD` vs `VIA_SDKMANAGER`). Hanya JDK & command-line tools yang dipasang langsung oleh AIDE; platform/build-tools/platform-tools ditandai `VIA_SDKMANAGER` dan baru bisa dipasang setelah Terminal (Phase 3) selesai — AIDE tidak perlu (dan sebaiknya tidak) membangun & memelihara mirror sendiri untuk setiap versi Android platform, karena Google sudah menyediakan & memelihara itu lewat `sdkmanager`.
- **Kotlin LSP** — bahkan AndroidIDE (dengan tim lebih besar & warisan kode lama) belum menyelesaikan ini. Dengan mulai dari nol, ini pekerjaan besar tersendiri — perlu keputusan: pakai `kotlin-lsp` resmi JetBrains sebagai backend (bukan menyalin, tapi memakai sebagai proses terpisah) vs bangun sendiri.
- **Kompatibilitas Android versi baru** — scoped storage, permission runtime, restriksi background process — perlu didesain benar sejak awal, bukan warisan asumsi lama.
- **Device rendah spesifikasi** — build Gradle in-app + indexing LSP berat di RAM; profiling di device low-end wajib bagian dari QA berkelanjutan, bukan cuma di akhir.
- **NDK** — kompleksitas tinggi untuk toolchain native di Android; jangan janjikan fitur ini di versi awal.
- **Risiko motivasi/skala tim** — proyek jangka panjang seperti ini butuh komitmen konsisten; perlu realistis soal kapasitas tim sebelum commit ke roadmap 18-24 bulan.

## 8. Definisi Selesai untuk Phase 0 (fondasi)

- [ ] Lisensi final dipilih (rekomendasi Apache 2.0) dan file `LICENSE` ditambahkan
- [ ] Audit lisensi semua dependency kandidat (sora-editor, JDT, Gradle Tooling API, dll.) — dikonfirmasi kompatibel dengan lisensi AIDE
- [ ] Keputusan strategi terminal (tulis sendiri vs cari lib permissive alternatif — **bukan Termux**)
- [ ] Setup CI dasar (build + lint) untuk skeleton project `com.krisoft.aide`
- [ ] Identitas visual (nama, ikon, warna) didesain
- [ ] Spesifikasi fungsional fitur inti ditulis (bukan menyalin dari source AndroidIDE — lihat §2 prinsip clean-room)
- [ ] Prototipe/spike kecil: buka file → syntax highlight dasar → jalankan 1 Gradle task sederhana end-to-end, untuk validasi arsitektur inti sebelum investasi besar
