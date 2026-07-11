# Daftar Fitur — AIDE (`com.krisoft.aide`)

Fitur dibagi tiga kelompok:
1. **Baseline (parity)** — wajib ada supaya AIDE minimal setara AndroidIDE dari sisi fungsi; **dibangun sendiri dari nol** (clean-room, lihat `docs/MASTER_PLAN.md` §2), terinspirasi fitur AndroidIDE — bukan warisan kode langsung
2. **Diferensiasi** — nilai tambah dibanding AndroidIDE & AIDE lama, alasan orang pindah/pakai
3. **Stretch / jangka panjang** — ambisius, belum tentu masuk versi 1.0

> Karena strategi pengembangan sekarang clean-room rewrite (bukan fork), fitur baseline di bawah **bukan "sudah ada tinggal rebrand"** — ini adalah target implementasi yang perlu ditulis satu per satu. Urutan prioritas & timeline realistis ada di `docs/ROADMAP.md`.

## 1. Baseline (target implementasi awal — wajib ada)

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
- Terminal terintegrasi, sesi persisten & multi-sesi
- Akses SDK Manager & package manager dari terminal
- Environment variable kustom untuk build & terminal
- ⚠️ **Catatan implementasi**: AndroidIDE memakai terminal-emulator Termux (GPLv3) — kalau AIDE mau lisensi non-GPL, komponen ini **tidak boleh dipakai langsung**. Perlu riset alternatif permissive atau tulis emulator terminal sendiri (lihat `docs/MASTER_PLAN.md` §2 & §7). Ini salah satu item riset prioritas di Phase 0.

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

Karena semua fitur baseline dibangun dari nol, urutan implementasi realistis (bukan asal ikut daftar di atas) mengikuti dependency teknis: **editor dasar → build & run → code intelligence (LSP) → UI Designer → terminal**, baru masuk ke fitur diferensiasi. Urutan lengkap & estimasi waktu per tahap ada di `docs/ROADMAP.md`. Kotlin LSP sengaja dimasukkan lebih awal dibanding rencana lama (bukan "fase 2 setelah Java selesai") karena tanpa keunggulan warisan kode dari fork, tidak ada alasan menunda differensiator utama ini terlalu lama.
