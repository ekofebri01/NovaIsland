# NovaIsland — Dynamic Notification Notch & Glassmorphism Drawer

**NovaIsland** adalah aplikasi Android modern yang menghadirkan pengalaman **Dynamic Notch** bergaya iPhone 18 dan **Glassmorphism Notification Drawer** dengan animasi spring physics yang halus, kustomisasi penuh, dukungan offline, serta sistem pemeriksaan pembaruan GitHub terverifikasi SHA-256.

---

## Fitur Utama

1. **Dynamic Notch iPhone 18**
   - 3 tahap animasi transisi spring physics: `Compact Pill` → `Active Live Pill` → `Expanded Card`.
   - Equalizer musik animasi, indikator progres timer/kurir/baterai, dan aksi interaktif langsung dari notch.
   - Integrasi notifikasi sistem nyata melalui `NotificationListenerService`.

2. **Glassmorphism Notification Drawer**
   - Geser turun dari area notch atau ketuk tombol **Glass Drawer** untuk membuka panel notifikasi kaca buram (`RenderEffect` blur pada Android 12+).
   - Sudut membulat modern, kartu dapat digeser untuk dihapus (*swipe-to-dismiss*), Control Center mini, dan tombol Bersihkan Semua.

3. **Kustomisasi Intuitif & Tema Adaptif**
   - Atur **posisi X/Y**, **skala lebar/tinggi**, **radius sudut**, dan **durasi tampil (2–15 detik)**.
   - Pilih mode tema **Otomatis (Sistem)**, **Mode Terang**, atau **Mode Gelap**.
   - Kustomisasi warna latar notch, tint kaca drawer, dan warna aksen cahaya.
   - Layout adaptif untuk layar **Portrait** (`NavigationBar`) maupun **Landscape/Tablet** (`NavigationRail` + grid 2 kolom).

4. **GitHub In-App Update + Auto-Backup + Verifikasi SHA-256**
   - Memeriksa rilis terbaru via GitHub Releases API (`Retrofit` + `OkHttp`) dengan opsi pembaruan manual dan notifikasi push lokal.
   - **Auto-Backup JSON** otomatis sebelum proses pembaruan dimulai, lengkap dengan checksum SHA-256 dan fitur pemulihan lokal.
   - Animasi progres unduhan dan verifikasi integritas hash **SHA-256** (`MessageDigest`) sebelum membuka tautan rilis GitHub terverifikasi.

5. **100% Offline-Ready & Dokumentasi Lengkap**
   - Pengaturan tersimpan di **Jetpack DataStore**, cadangan tersimpan lokal, serta tutorial interaktif, *What's New*, *Changelog*, dan panduan penggunaan selalu tersedia offline.

---

## Struktur Proyek

```
app/src/main/java/com/example/
├── MainActivity.kt                          # Navigasi adaptif & overlay utama
├── model/NotchModels.kt                     # Model data Notch, Settings, Backup, Update
├── data/
│   ├── SettingsDataStore.kt                 # Persistensi DataStore Preferences
│   ├── OfflineDocsRepository.kt             # Panduan, Changelog, What's New & Starter data
│   ├── BackupManager.kt                     # Auto-Backup & Restore JSON + SHA-256
│   ├── GitHubUpdateService.kt               # Retrofit GitHub API + SHA-256 verifier
│   └── UpdateNotificationHelper.kt          # NotificationChannel untuk push update
├── service/
│   └── NovaNotificationListenerService.kt   # Bridge notifikasi sistem Android
├── viewmodel/
│   └── NovaIslandViewModel.kt               # StateFlow & orkestrasi logika bisnis
└── ui/
    ├── theme/                               # Color.kt, Type.kt (Plus Jakarta Sans & JetBrains Mono), Theme.kt
    ├── components/                          # DynamicNotchOverlay, GlassNotificationDrawer, UpdateProgressSheet, Onboarding, WhatsNew
    └── screens/                             # NotchStudioScreen, DrawerFeedScreen, SettingsScreen, DocsScreen
```
