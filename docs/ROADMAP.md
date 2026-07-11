# Roadmap — AIDE (`com.krisoft.aide`)

> **Strategi**: clean-room rewrite, terinspirasi AndroidIDE & ACSIDE, bukan fork (lihat `docs/MASTER_PLAN.md` §1–§2). Konsekuensinya, roadmap ini **jauh lebih panjang** dibanding rencana fork sebelumnya — realistis **18–24 bulan** untuk versi yang matang setara baseline AndroidIDE/ACSIDE (native Android saja, belum termasuk Flutter/Dart — lihat Phase 7), dihitung dari Juli 2026. Estimasi indikatif, direvisi begitu Phase 0 selesai dan kecepatan tim aktual diketahui.

---

## Phase 0 — Fondasi, Legal & Riset Teknis (2–3 bulan | Q3 2026)

Tujuan: validasi kelayakan teknis & legal sebelum investasi besar ditulis.

- [ ] Tentukan lisensi final AIDE (rekomendasi Apache 2.0), tambahkan `LICENSE`
- [ ] Audit lisensi setiap dependency kandidat (sora-editor, Eclipse JDT, Gradle Tooling API, dll.) — pastikan kompatibel dengan lisensi pilihan
- [ ] **Riset terminal**: putuskan tulis emulator sendiri vs cari library permissive alternatif (bukan Termux/GPLv3) — ini item risiko teknis terbesar, selesaikan lebih dulu
- [ ] Tetapkan prinsip & proses clean-room untuk tim (siapa boleh menulis modul apa, dokumentasi spec fungsional, review process)
- [ ] Setup skeleton project `com.krisoft.aide` + CI dasar (build, lint)
- [ ] Desain identitas visual (nama "AIDE", ikon, splash, palet warna)
- [ ] **Spike/prototipe validasi arsitektur**: buka file teks → syntax highlight dasar (sora-editor) → jalankan 1 Gradle task sederhana end-to-end di device. Ini bukan fitur produk, murni pembuktian bahwa arsitektur inti bisa berjalan.

**Keluaran**: keputusan legal & teknis final, skeleton project jalan, risiko terbesar (terminal, arsitektur inti) sudah tervalidasi lewat spike.

**Go/no-go checkpoint**: kalau spike gagal atau riset terminal tidak menemukan opsi layak, revisi rencana di sini — jangan lanjut ke Phase 1 dengan asumsi belum tervalidasi.

---

## Phase 1 — MVP: Editor + Build Inti (4–5 bulan | Q4 2026 – Q1 2027)

Tujuan: siklus paling dasar berfungsi — buka proyek, edit teks, build, install, lihat log — **tanpa** code intelligence dulu.

- [ ] Editor teks fungsional (sora-editor + glue code sendiri): syntax highlighting, code folding, search & replace, multi-cursor
- [ ] File tree view + operasi file dasar (create/rename/delete/move)
- [ ] Buka & buat proyek Gradle nyata, minimal 1 template (Empty Activity)
- [ ] Build orchestrator di atas Gradle Tooling API — assemble debug/release, pilih JDK (minimal 17, evaluasi 21)
- [ ] Install & jalankan APK ke device
- [ ] Logcat viewer dasar
- [ ] Git dasar (clone, commit, push, pull, diff)

**Keluaran**: AIDE 0.1.0 — bisa dipakai untuk alur paling dasar "tulis kode tanpa bantuan LSP → build → run", diuji internal di device fisik.

---

## Phase 2 — Code Intelligence / LSP (4–5 bulan | Q1–Q2 2027)

Tujuan: editor jadi benar-benar produktif dengan bantuan LSP. Kotlin dimasukkan di sini sebagai baseline (bukan diferensiasi) karena kompetitor (ACSIDE) sudah punya ini — tanpa Kotlin LSP, AIDE kalah standar minimum, bukan cuma kalah fitur tambahan.

- [ ] LSP client sendiri, terhubung ke Eclipse JDT sebagai backend Java (auto-complete, diagnostics, go-to-definition, find references)
- [ ] XML completion & resolusi resource reference (`@string/…`, `@drawable/…`)
- [ ] Riset & mulai integrasi **Kotlin LSP** (evaluasi `kotlin-lsp` resmi JetBrains sebagai backend proses terpisah) — dimulai di sini, bukan ditunda ke fase diferensiasi terpisah
- [ ] Dokumentasi API saat hover

**Keluaran**: AIDE 0.3.0 — code intelligence Java berfungsi penuh, progres awal Kotlin LSP (mungkin belum lengkap).

---

## Phase 3 — Terminal & Tooling Tambahan (2–3 bulan | Q2–Q3 2027)

- [ ] Implementasi terminal (hasil riset Phase 0 — tulis sendiri atau integrasi lib permissive), sesi persisten & multi-sesi
- [ ] Akses SDK Manager dari terminal
- [ ] Environment variable kustom untuk build & terminal

**Keluaran**: AIDE 0.5.0 — siklus dev-tool lengkap (editor + LSP + build + terminal).

---

## Phase 4 — UI Designer & Compose Preview (3–4 bulan | Q3–Q4 2027)

- [ ] Layout inflater — preview visual XML (pakai `LayoutInflater` Android standar)
- [ ] Drag-and-drop widget ke canvas
- [ ] Visual attribute editor untuk widget standar
- [ ] Jetpack Compose preview (static) — masuk baseline di sini, bukan fase diferensiasi, karena ACSIDE sudah punya ini

**Keluaran**: AIDE 0.6.0-beta — mulai dibuka untuk **beta publik terbatas**.

---

## Phase 5 — Diferensiasi & Polish (3–4 bulan | Q4 2027 – Q1 2028)

- [ ] Asisten/Agent AI opsional (opt-in, integrasi Claude API, evaluasi dukungan MCP agar setara ACSIDE dari sisi kapabilitas, dengan fokus diferensiasi di transparansi & kontrol privasi)
- [ ] Asset Studio (generator ikon/drawable)
- [ ] String Translator
- [ ] Mode performa untuk device low-end (RAM ≤4GB)
- [ ] Wireless ADB / device manager

**Keluaran**: AIDE 0.9.0 — feature-complete untuk rilis 1.0, beta publik lebih luas.

---

## Phase 6 — Stabilisasi & Rilis 1.0 (2–3 bulan | Q1–Q2 2028)

- [ ] Bug bash & stabilisasi menyeluruh
- [ ] Guideline kontribusi komunitas + dokumentasi lengkap
- [ ] Terjemahan UI (Bahasa Indonesia + Inggris minimal)
- [ ] Rilis **AIDE 1.0.0** stabil

---

## Phase 7 — Ekspansi Flutter/Dart (opsional, pasca-1.0 | belum dijadwalkan)

ACSIDE sudah mendukung proyek Flutter/Dart selain Android native — kalau AIDE mau bersaing head-to-head, ini jadi ekspansi scope terbesar setelah 1.0:

- [ ] LSP Dart terpisah dari LSP Java/Kotlin
- [ ] Integrasi toolchain Flutter (terpisah dari Android SDK)
- [ ] Flutter instant preview
- [ ] Evaluasi multi-language server tambahan (Python, Bash, Clang) kalau ada demand

Sengaja dipisah dari roadmap 1.0 karena menambah toolchain kedua di tengah pengembangan native Android akan memperlambat & meningkatkan risiko Phase 0–6. Keputusan lanjut/tidak diambil berdasarkan traksi pengguna AIDE 1.0 dan kapasitas tim.

## Setelah Phase 7 (belum dijadwalkan)

Sisa fitur stretch dari `docs/FEATURES.md` §3 (plugin marketplace, companion desktop app, terminal dengan GUI app, AI on-device, cloud backup) — dipertimbangkan berdasarkan traksi & kapasitas tim.

---

## Metrik Keberhasilan per Fase

| Fase | Metrik |
|---|---|
| Phase 0 | Spike arsitektur berhasil end-to-end di device fisik; keputusan lisensi & terminal final |
| Phase 1 | Bisa buat proyek → edit → build → install → run, 100% tanpa crash pada alur dasar |
| Phase 2 | Completion Java akurat di proyek nyata (bukan hello-world); progres Kotlin LSP terukur |
| Phase 3 | Terminal dipakai untuk instal SDK component tanpa masalah stabilitas |
| Phase 4 | Beta tester eksternal ≥20 orang bisa selesaikan alur "buat UI sederhana + lihat Compose preview" tanpa panduan |
| Phase 5 | Beta tester ≥100 orang, retensi pemakaian mingguan terukur |
| Phase 6 | Zero known critical bug saat rilis 1.0.0 |
| Phase 7 | (jika dilanjutkan) Proyek Flutter contoh bisa dibuat → edit → preview instan tanpa crash |

---

## Langkah Berikutnya (immediate action items)

1. Finalisasi keputusan lisensi (Apache 2.0 direkomendasikan) — blocking semua langkah lain
2. Mulai riset terminal non-GPL — ini risiko teknis terbesar, jangan ditunda
3. Susun tim/kapasitas riil yang tersedia, validasi ulang estimasi 18–24 bulan terhadap kapasitas tersebut
4. Jalankan spike arsitektur (editor + 1 Gradle task end-to-end) sebagai validasi paling awal
