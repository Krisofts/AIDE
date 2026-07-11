# AIDE — `com.krisoft.aide`

**AIDE** adalah IDE Android untuk membangun aplikasi Android (berbasis Gradle) langsung dari perangkat Android — lanjutan spirit dari **AIDE** (Android IDE) lama yang sudah tidak dikembangkan lagi, **terinspirasi** dari fitur & pengalaman pengguna **[AndroidIDE](https://github.com/AndroidIDEOfficial/AndroidIDE)** (open source, aktif, GPLv3) — tapi **ditulis ulang dari nol (clean-room)**, bukan fork/salinan source code-nya.

Repo ini baru masuk **Phase 0** (fondasi & skeleton project) — lihat `docs/ROADMAP.md`. Dokumen di `docs/` adalah master plan; kode di `app/` adalah skeleton awal (belum ada fitur, baru satu Activity kosong).

## Status

🏗️ Phase 0 — skeleton project Android sudah ada (belum ada fitur IDE). Lisensi: **Apache 2.0**. Lihat `docs/ROADMAP.md` untuk fase berikutnya.

## Menjalankan Project

Prasyarat: Android Studio (atau JDK 17+ & Android SDK terpasang manual).

```bash
./gradlew assembleDebug
```

> Catatan: skeleton ini dibuat & divalidasi strukturnya di lingkungan sandbox tanpa akses ke Android SDK / `dl.google.com`, jadi build **belum diverifikasi end-to-end** di sana. Build pertama sebaiknya dilakukan di Android Studio atau lewat CI (`.github/workflows/android-ci.yml`) yang punya akses internet penuh — laporkan kalau ada error konfigurasi Gradle/AGP yang perlu diperbaiki.

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
