# AIDE — `com.krisoft.aide`

**AIDE** adalah IDE Android untuk membangun aplikasi Android (berbasis Gradle) langsung dari perangkat Android — lanjutan spirit dari **AIDE** (Android IDE) lama yang sudah tidak dikembangkan lagi, dengan basis arsitektur mengikuti **[AndroidIDE](https://github.com/AndroidIDEOfficial/AndroidIDE)** (open source, aktif, GPLv3).

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
- **Basis**: fork & rebrand dari [AndroidIDEOfficial/AndroidIDE](https://github.com/AndroidIDEOfficial/AndroidIDE)
- **Lisensi**: GPLv3 (mengikuti lisensi upstream — lihat bagian Legal di master plan)
- **Kenapa fork, bukan tulis ulang**: AndroidIDE sudah punya editor (sora-editor), LSP Java/XML, terminal (Termux), build system Gradle-in-app yang matang selama bertahun-tahun. Menulis ulang dari nol akan menghabiskan waktu bertahun-tahun untuk mencapai titik yang sama.
- **Nilai tambah dibanding AndroidIDE**: lihat bagian "Diferensiasi" di `docs/FEATURES.md` (Kotlin LSP, Compose preview, asisten AI opsional, dll).

## Kontribusi

Belum ada guideline formal — akan ditambahkan begitu Phase 0 (lihat roadmap) selesai.
