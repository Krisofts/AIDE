# Roadmap — AIDE (`com.krisoft.aide`)

Estimasi waktu dihitung dari **Juli 2026**. Bersifat indikatif — direvisi begitu Phase 0 selesai dan kecepatan tim aktual diketahui.

---

## Phase 0 — Fondasi & Legal (4–6 minggu | Q3 2026)

Tujuan: siap secara legal & teknis sebelum kode ditulis.

- [ ] Fork repository AndroidIDE, verifikasi build baseline hijau (build tanpa modifikasi)
- [ ] Tambahkan `LICENSE` (GPLv3) + `NOTICE` atribusi ke AndroidIDE Official
- [ ] Setup CI dasar (GitHub Actions): build + lint + unit test per PR
- [ ] Setup keystore signing baru (bukan milik AndroidIDE)
- [ ] Desain identitas: nama "AIDE", ikon, palet warna, splash screen
- [ ] Buat checklist rename package per-modul (~80+ modul → `com.krisoft.aide.*`)

**Keluaran**: repo siap di-develop, identitas visual final, rencana rename tervalidasi.

---

## Phase 1 — Rebranding & Parity Build (2–3 bulan | Q3–Q4 2026)

Tujuan: AIDE bisa di-build, terinstall, dan menjalankan semua fitur baseline (lihat `docs/FEATURES.md` §1) dengan identitas sendiri.

- [ ] Rename `applicationId`/`namespace` di seluruh modul ke `com.krisoft.aide`
- [ ] Pindahkan source ke struktur paket baru, perbaiki semua import
- [ ] Update `AndroidManifest.xml`, `FileProvider` authority, exported components
- [ ] Ganti resource identitas (nama app, ikon launcher, warna tema)
- [ ] Update endpoint hardcoded (update-checker, crash reporting, docs link) ke milik sendiri
- [ ] QA menyeluruh: buat proyek → edit kode → build Gradle → install APK → jalankan → cek Logcat, di device fisik nyata
- [ ] Tulis halaman About/Licenses dengan atribusi jujur ke AndroidIDE
- [ ] Rilis **alpha internal** (0.1.0-alpha) — belum publik

**Keluaran**: AIDE 0.1.0-alpha — setara fungsional dengan AndroidIDE, identitas sudah berbeda.

**Milestone kunci**: ini titik paling berisiko dari sisi effort — kesalahan rename package di 80+ modul gampang bikin build pecah. Sisihkan waktu ekstra untuk QA di sini.

---

## Phase 2 — Fitur Diferensiasi Awal (3 bulan | Q4 2026 – Q1 2027)

Tujuan: mulai punya alasan konkret kenapa orang pakai AIDE, bukan AndroidIDE langsung.

- [ ] Riset & integrasi Kotlin Language Server (evaluasi `kotlin-lsp` JetBrains vs `fwcd/kotlin-language-server`)
- [ ] Jetpack Compose preview (tahap awal: static preview, belum interaktif)
- [ ] Selesaikan Asset Studio (generator ikon/drawable)
- [ ] Selesaikan String Translator
- [ ] Perbaikan UI Designer (constraint layout lebih akurat)
- [ ] Rilis **beta publik** (0.5.0-beta) di GitHub Releases

**Keluaran**: AIDE 0.5.0-beta — Kotlin LSP & Compose preview jadi headline feature.

---

## Phase 3 — AI & Produktivitas (3 bulan | Q1–Q2 2027)

- [ ] Asisten AI opsional (opt-in), integrasi Claude API untuk code completion/chat
- [ ] Mode performa untuk device low-end (RAM ≤4GB): lazy indexing, caching build lebih agresif
- [ ] Wireless ADB / device manager
- [ ] Plugin system v1 (fondasi, belum marketplace)
- [ ] Evaluasi distribusi via F-Droid (audit dependency untuk syarat reproducible build)

**Keluaran**: AIDE 0.8.0 — fitur AI & produktivitas, mulai dipertimbangkan untuk F-Droid.

---

## Phase 4 — Stabilisasi & Rilis 1.0 (2–3 bulan | Q2–Q3 2027)

- [ ] Bug bash & stabilisasi menyeluruh
- [ ] Guideline kontribusi komunitas + dokumentasi lengkap
- [ ] Terjemahan UI ke bahasa lain (mulai dari Bahasa Indonesia + Inggris)
- [ ] Proses sinkronisasi upstream terdokumentasi (cara cherry-pick security fix dari AndroidIDE)
- [ ] Rilis **AIDE 1.0.0** stabil

---

## Setelah 1.0 (belum dijadwalkan)

Fitur stretch dari `docs/FEATURES.md` §3 (marketplace plugin, companion desktop app, dukungan Flutter/React Native, AI on-device) dipertimbangkan berdasarkan traksi & kapasitas tim pasca-1.0.

---

## Metrik Keberhasilan per Fase

| Fase | Metrik |
|---|---|
| Phase 1 | Build sukses 100% di CI, bisa buat+build+jalankan proyek Compose sederhana end-to-end di device fisik |
| Phase 2 | Kotlin completion akurat untuk proyek Kotlin nyata (bukan cuma hello-world), beta dipakai ≥50 tester eksternal |
| Phase 3 | Asisten AI dipakai tanpa crash pada sesi ≥30 menit, mode low-end teruji di device RAM 3–4GB |
| Phase 4 | Zero known critical bug saat rilis, dokumentasi kontribusi cukup untuk onboarding kontributor baru tanpa tanya langsung |

---

## Langkah Berikutnya (immediate action items)

1. Putuskan siapa yang audit lisensi/legal (Phase 0)
2. Fork source AndroidIDE ke repo ini, verifikasi build baseline
3. Finalisasi identitas visual (nama sudah fix: **AIDE**, tinggal ikon & warna)
4. Mulai checklist rename package modul demi modul
