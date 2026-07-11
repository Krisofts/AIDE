# Daftar Fitur — AIDE (`com.krisoft.aide`)

Referensi produk (inspirasi fitur/UX, **bukan sumber kode** — lihat prinsip clean-room di `docs/MASTER_PLAN.md` §2):

- **[AndroidIDE](https://github.com/AndroidIDEOfficial/AndroidIDE)** — open source (GPLv3), fokus native Android (Java/Kotlin + Gradle)
- **[ACSIDE](https://github.com/AndroidCSIDE/ACSIDE)** (Android Code Studio / AndroidCSIDE) — **saat ini closed-source**, lebih ambisius: multi-bahasa (termasuk Flutter/Dart), multi-LSP, terminal dengan GUI (Termux-X11), dan AI Agent dengan dukungan MCP. Karena closed-source, tidak ada source untuk dilihat sama sekali — referensi murni dari README/dokumentasi publik & pengalaman pakai aplikasinya.

Karena ACSIDE sudah punya beberapa fitur yang tadinya kita rencanakan sebagai "diferensiasi" (Kotlin LSP, Compose preview, asisten AI), standar baseline naik — beberapa fitur itu dipindah dari kolom diferensiasi ke baseline supaya AIDE tidak ketinggalan dari kompetitor terbaru, bukan cuma dari AndroidIDE.

Fitur dibagi tiga kelompok:
1. **Baseline** — wajib ada supaya AIDE setara kompetitor (AndroidIDE + ACSIDE); **dibangun sendiri dari nol** (clean-room)
2. **Diferensiasi** — nilai tambah yang belum dipunyai kompetitor manapun
3. **Stretch / jangka panjang** — ambisius, belum tentu masuk versi 1.0

> Karena strategi pengembangan clean-room rewrite (bukan fork), fitur baseline di bawah **bukan "sudah ada tinggal rebrand"** — ini target implementasi yang perlu ditulis satu per satu. Urutan prioritas & timeline realistis ada di `docs/ROADMAP.md`.

## 1. Baseline (target implementasi — wajib ada)

### Manajemen Proyek
- Buka & buat proyek Android berbasis Gradle sungguhan (bukan build system tiruan)
- Template proyek (Empty Activity, Compose, dsb.)
- File tree view dengan operasi file standar (create/rename/delete/move)

### Editor Kode
- Code editor performa tinggi (sora-editor) dengan syntax highlighting
- Code folding, multi-cursor, search & replace (termasuk lintas file)
- Auto-indent, bracket matching, line numbers, minimap
- Highlighting berbasis grammar TextMate/tree-sitter (evaluasi mana yang lebih ringan di device rendah spek)

### Code Intelligence (LSP) — multi-bahasa sejak awal
- **Java Language Server**: auto-complete, diagnostics, quick fix, go-to-definition, find references
- **Kotlin Language Server**: sudah masuk baseline (bukan lagi diferensiasi) — evaluasi `kotlin-lsp` resmi JetBrains sebagai backend
- **XML Language Server**: auto-complete atribut, resolusi referensi resource (`@string/…`, `@drawable/…`)
- Dokumentasi API saat hover (`@since`/`@deprecated`/`@removed`)
- Format otomatis (auto-format on save)

### Build & Run
- Jalankan Gradle build langsung di device (assemble/build variant debug & release)
- Pilih versi JDK untuk build (minimal 17, evaluasi dukungan 21)
- Install & jalankan APK hasil build langsung ke device
- Logcat viewer terintegrasi (real-time app log reader)
- SDK Manager untuk kelola build tools (🏗️ kerangka UI & logika install sudah ada, lihat `com.krisoft.aide.sdkmanager`; sumber manifest produksi masih menunggu keputusan arsitektur di `docs/MASTER_PLAN.md` §7)

### UI Designer & Preview
- Layout inflater — preview visual dari XML
- Drag-and-drop widget ke canvas
- Visual attribute editor untuk widget Android standar
- **Jetpack Compose preview** — sudah masuk baseline (bukan lagi diferensiasi), karena ACSIDE sudah punya ini

### Terminal
- Terminal terintegrasi, sesi persisten & multi-sesi
- Akses SDK Manager & package manager dari terminal
- Environment variable kustom untuk build & terminal
- ⚠️ **Catatan implementasi**: AndroidIDE memakai terminal-emulator Termux (GPLv3) — kalau AIDE mau lisensi non-GPL, komponen ini **tidak boleh dipakai langsung**. Perlu riset alternatif permissive atau tulis emulator terminal sendiri (lihat `docs/MASTER_PLAN.md` §2 & §7). Ini salah satu item riset prioritas di Phase 0. Dukungan GUI di terminal (mis. Termux-X11 di ACSIDE) dievaluasi setelah terminal dasar stabil, bukan di baseline awal.

### Lainnya
- Integrasi Git dasar (clone, commit, push, pull, diff)
- Preferensi: tema terang/gelap, ukuran font editor, key binding
- Signing APK (keystore management)

## 2. Diferensiasi (nilai tambah dibanding AndroidIDE **dan** ACSIDE)

| Fitur | Kenapa penting |
|---|---|
| **Dukungan proyek Flutter/Dart** | ACSIDE sudah punya ini, AndroidIDE tidak — kalau mau bersaing langsung dengan ACSIDE, ini perlu dipertimbangkan naik dari stretch (lihat §3), tapi scope-nya besar (LSP Dart + toolchain Flutter terpisah dari Android SDK) |
| **Asisten/Agent AI dengan MCP** | ACSIDE sudah punya AI Agent + dukungan MCP. AIDE bisa berdiferensiasi lewat kualitas integrasi (opt-in jelas, privasi terjaga, pilihan model) daripada sekadar "ada AI-nya" |
| **UI Designer versi lanjut** | Live preview lebih akurat, dukungan constraint layout lebih baik, preview di berbagai ukuran layar |
| **Asset Studio** | Generator ikon adaptif & drawable dari vector |
| **String Translator** | Bantu terjemahkan `strings.xml` ke banyak locale |
| **Build performa untuk device low-end** | Caching lebih agresif, indexing LSP lebih malas (lazy), mode "ringan" untuk RAM ≤4GB — baik AndroidIDE maupun ACSIDE (Ubuntu+Termux-X11) cukup berat untuk device low-end |
| **Wireless ADB / device manager** | Debug ke device lain via jaringan |
| **Material 3 UI + tema kustom** | Tampilan modern, dukungan dynamic color (Android 12+) |
| **Privasi & transparansi AI by default** | Kode user tidak pernah dikirim ke layanan AI tanpa opt-in eksplisit per-sesi — nilai jual dibanding kompetitor yang AI-nya sudah built-in tanpa kontrol granular |

## 3. Stretch / Jangka Panjang

- Plugin/extension system (marketplace komunitas) — ACSIDE sudah mengarah ke sini (format `.acp`), AIDE bisa menyusul di jangka panjang
- Companion desktop app untuk sinkronisasi proyek lintas device
- Multi-language server tambahan (Python, Bash, Clang/C++) — ACSIDE sudah punya, AIDE evaluasi setelah Java/Kotlin/XML/Dart matang
- Terminal dengan dukungan GUI app (setara Termux-X11 di ACSIDE)
- AI pair-programming offline dengan model kecil on-device
- Cloud backup proyek opsional (privacy-respecting, end-to-end encrypted)

## Catatan Prioritas

Urutan implementasi mengikuti dependency teknis: **editor dasar → build & run (native Android/Gradle) → code intelligence Java+Kotlin+XML → UI Designer & Compose preview → terminal**, baru masuk diferensiasi & scope Flutter/Dart. Flutter/Dart sengaja **tidak** masuk baseline awal meski ACSIDE sudah punya — menambah dua toolchain (Android + Flutter) sekaligus di fase awal terlalu berisiko untuk tim clean-room yang mulai dari nol; direncanakan sebagai fase ekspansi setelah baseline native Android stabil (lihat `docs/ROADMAP.md`).
