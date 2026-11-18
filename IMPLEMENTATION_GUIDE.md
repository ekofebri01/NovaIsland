# Panduan Implementasi Bertahap (Step-by-Step Roadmap)

Dokumen ini dirancang agar pengembangan **NovaIsland** dapat diikuti dan dilanjutkan kapan saja secara bertahap.

## Ringkasan Status Fase

| Fase | Fokus Utama | Status | Berkas Kunci |
| :--- | :--- | :---: | :--- |
| **Fase 1** | Fondasi Tema, Tipografi, Ikon & DataStore Offline | Selesai | `Theme.kt`, `Color.kt`, `Type.kt`, `SettingsDataStore.kt` |
| **Fase 2** | Mesin Dynamic Notch & Live Activities | Selesai | `DynamicNotchOverlay.kt`, `NotchStudioScreen.kt` |
| **Fase 3** | Glassmorphism Notification Drawer & System Bridge | Selesai | `GlassNotificationDrawer.kt`, `NovaNotificationListenerService.kt` |
| **Fase 4** | GitHub Update Checker, Auto-Backup & SHA-256 | Selesai | `GitHubUpdateService.kt`, `BackupManager.kt`, `UpdateProgressSheet.kt` |
| **Fase 5** | Onboarding Interaktif, What's New & UI Adaptif | Selesai | `InteractiveOnboardingOverlay.kt`, `WhatsNewDialog.kt`, `DocsScreen.kt` |

---

## Langkah Lanjutan yang Dapat Dikembangkan Berikutnya (Fase 6+)

1. **Preset Tema Komunitas (Import/Export JSON)**
   - Menambahkan fitur berbagi preset warna dan geometri notch melalui Storage Access Framework (`ActivityResultContracts.CreateDocument` / `OpenDocument`).
2. **Aturan Filter Per-Aplikasi**
   - Mengizinkan pengguna memilih aplikasi mana saja yang diprioritaskan tampil di Dynamic Notch dan warna aksen khusus per paket aplikasi.
3. **Widget & Quick Settings Tile**
   - Menambahkan `TileService` di Quick Settings Android untuk membuka Glass Drawer atau menjeda Dynamic Notch dalam satu ketukan.
4. **Rilis Publik GitHub**
   - Membuat rilis bertag `v1.1.0` di repositori GitHub Anda dan menyertakan baris `SHA-256: <64-hex>` pada deskripsi rilis agar `GitHubUpdateService` langsung memverifikasi rilis produksi Anda secara otomatis.
