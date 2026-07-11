# Daftar Fitur — AIDE (`com.krisoft.aide`)

Fitur dibagi tiga kelompok:
1. **Baseline (parity)** — wajib ada, diwarisi dari AndroidIDE, supaya AIDE minimal setara dengan yang sudah ada
2. **Diferensiasi** — nilai tambah dibanding AndroidIDE & AIDE lama, alasan orang pindah/pakai
3. **Stretch / jangka panjang** — ambisius, belum tentu masuk versi 1.0

## 1. Baseline (Phase 1 — wajib ada)

### Manajemen Proyek
- Buka & buat proyek Android berbasis Gradle sungguhan (bukan build system tiruan)
- Template proyek (Empty Activity, Compose, dsb.)
- File tree view dengan operasi file standar (create/rename/delete/move)

### Editor Kode
- Code editor performa tinggi (sora-editor) dengan syntax highlighting via tree-sitter
- Code folding, multi-cursor, search & replace (termasuk lintas file)
- Auto-indent, bracket matching, line numbers, minimap

### Code Intelligence (LSP)
- Java Language Server: auto-complete, diagnostics/error underline, quick fix, go-to-definition, find references
- XML Language Server: auto-complete atribut, resolusi referensi resource (`@string/…`, `@drawable/…`, dst.)
- Dokumentasi API saat hover (info `@since`/`@deprecated`/`@removed`)

### Build & Run
- Jalankan Gradle build langsung di device (assemble/build variant debug & release)
- Pilih versi JDK (11/17) untuk build
- Install & jalankan APK hasil build langsung ke device
- Logcat viewer terintegrasi (real-time app log reader)

### UI Designer
- Layout inflater — preview visual dari XML
- Drag-and-drop widget ke canvas
- Visual attribute editor untuk widget Android standar

### Terminal
- Terminal terintegrasi berbasis Termux, sesi persisten & multi-sesi
- Akses SDK Manager & package manager (`apt`) dari terminal
- Environment variable kustom untuk build & terminal

### Lainnya
- Integrasi Git dasar (clone, commit, push, pull, diff)
- Preferensi: tema terang/gelap, ukuran font editor, key binding
- Signing APK (keystore management)

## 2. Diferensiasi (Phase 2–3 — nilai tambah)

Ini yang membedakan AIDE dari sekadar "AndroidIDE dengan nama beda":

| Fitur | Kenapa penting |
|---|---|
| **Kotlin Language Server penuh** | AndroidIDE baru punya Java+XML LSP; mayoritas proyek Android modern pakai Kotlin. Ini gap terbesar yang bisa jadi alasan utama orang pindah. |
| **Jetpack Compose preview** | Compose adalah standar UI modern Android; belum ada preview visual untuk Compose di AndroidIDE. |
| **Asisten AI opsional (opt-in)** | Code completion/chat berbasis LLM untuk bantu debugging & generate boilerplate — bisa pakai Claude API. Wajib opt-in & jelas soal privasi (kode user tidak dikirim tanpa izin eksplisit). |
| **UI Designer versi lanjut** | Live preview yang lebih akurat, dukungan constraint layout lebih baik, preview di berbagai ukuran layar. |
| **Asset Studio** | Generator ikon adaptif & drawable dari vector — di AndroidIDE baru direncanakan, belum jadi. |
| **String Translator** | Bantu terjemahkan `strings.xml` ke banyak locale — juga baru rencana di AndroidIDE. |
| **Build performa untuk device low-end** | Caching lebih agresif, indexing LSP lebih malas (lazy), mode "ringan" untuk RAM ≤4GB. |
| **Wireless ADB / device manager** | Debug ke device lain via jaringan, tidak cuma di device itu sendiri. |
| **Material 3 UI + tema kustom** | Tampilan lebih modern, dukungan dynamic color (Android 12+). |

## 3. Stretch / Jangka Panjang (belum prioritas)

- Plugin/extension system (marketplace komunitas)
- Companion desktop app untuk sinkronisasi proyek lintas device
- Dukungan proyek Flutter/React Native (di luar scope Gradle-native Android)
- AI pair-programming offline dengan model kecil on-device
- Cloud backup proyek opsional (privacy-respecting, end-to-end encrypted)

## Catatan Prioritas

Fitur baseline **tidak perlu dibangun dari nol** — semua sudah ada di codebase AndroidIDE dan "otomatis ikut" begitu fork & rebrand selesai (lihat `docs/ROADMAP.md` Phase 1). Effort riil ada di kolom **Diferensiasi**, terutama Kotlin LSP dan Compose preview — dua fitur ini yang paling layak jadi fokus tim setelah rebranding stabil.
