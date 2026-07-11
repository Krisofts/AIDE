# AIDE — `com.krisoft.aide`

**AIDE** adalah IDE Android untuk membangun aplikasi Android (berbasis Gradle) langsung dari perangkat Android — lanjutan spirit dari **AIDE** (Android IDE) lama yang sudah tidak dikembangkan lagi, **terinspirasi** dari fitur & pengalaman pengguna **[AndroidIDE](https://github.com/AndroidIDEOfficial/AndroidIDE)** (open source, aktif, GPLv3) — tapi **ditulis ulang dari nol (clean-room)**, bukan fork/salinan source code-nya.

Repo ini masih di **tahap perencanaan**. Belum ada kode — dokumen di `docs/` adalah master plan sebelum implementasi dimulai.

## Status

📋 Planning — belum ada rilis. Lihat roadmap untuk fase saat ini.

## Dokumen Perencanaan

| Dokumen | Isi |
|---|---|
| [`docs/MASTER_PLAN.md`](docs/MASTER_PLAN.md) | Strategi, arsitektur, tech stack, legal, rencana rebranding package |
| [`docs/FEATURES.md`](docs/FEATURES.md) | Daftar fitur — baseline (parity) & diferensiasi |
| [`docs/ROADMAP.md`](docs/ROADMAP.md) | Roadmap bertahap dengan target waktu |

## Ringkasan Cepat

- **Package name**: `com.krisoft.aide`
- **Basis**: karya orisinal, terinspirasi fitur/UX [AndroidIDEOfficial/AndroidIDE](https://github.com/AndroidIDEOfficial/AndroidIDE) — bukan fork
- **Lisensi**: belum wajib GPLv3 karena bukan fork — rekomendasi **Apache 2.0** (keputusan final di Phase 0, lihat master plan §3)
- **Kenapa tulis ulang, bukan fork**: bebas menentukan lisensi sendiri (termasuk opsi proprietary di masa depan) dan tidak terikat kewajiban copyleft GPLv3. Konsekuensinya: timeline jauh lebih panjang (~18–24 bulan) karena semua komponen inti (editor glue, LSP client, build orchestrator, terminal, UI designer) ditulis sendiri — lihat trade-off lengkap di `docs/MASTER_PLAN.md` §2.
- **Prinsip clean-room**: tim tidak membaca/menyalin source code AndroidIDE saat menulis komponen inti — hanya observasi UX & spesifikasi fungsional sendiri (wajib dibaca sebelum kontribusi, lihat master plan §2).
- **Nilai tambah dibanding AndroidIDE**: lihat bagian "Diferensiasi" di `docs/FEATURES.md` (Kotlin LSP, Compose preview, asisten AI opsional, dll).

## Kontribusi

Belum ada guideline formal — akan ditambahkan begitu Phase 0 (lihat roadmap) selesai.
