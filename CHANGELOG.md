# Changelog NovaIsland

Semua perubahan penting pada proyek **NovaIsland** dicatat dalam berkas ini.

## [1.0.0] - 2026-11-18 — *Genesis Aurora*

### Ditambahkan
- **Dynamic Notch Overlay**: Pil notifikasi dinamis bergaya iPhone 18 di area status bar dengan 3 mode morphing (`Compact`, `Active Pill`, `Expanded`) menggunakan animasi spring physics.
- **Glassmorphism Notification Drawer**: Panel notifikasi layar penuh dengan efek blur kaca (`RenderEffect`), kartu membulat `24.dp`, *swipe-to-dismiss*, dan Control Center mini.
- **Bridge Notifikasi Sistem**: `NovaNotificationListenerService` untuk menangkap notifikasi aplikasi perangkat secara real-time.
- **Studio Simulasi Offline**: Pemicu cepat untuk Pesan, Musik Live, Timer Fokus, Kurir, Panggilan HD, Pengisian Daya, Pembaruan, dan Mode Fokus.
- **Pengaturan Lengkap & Personal**: Pengaturan posisi X/Y, lebar, tinggi, radius sudut, durasi tampil (2–15 detik), intensitas blur, warna latar notch/drawer/aksen, serta tema Otomatis/Terang/Gelap.
- **GitHub In-App Update Engine**: Pemeriksaan rilis GitHub via Retrofit + OkHttp, notifikasi push update (`POST_NOTIFICATIONS`), animasi progres unduhan, verifikasi hash **SHA-256**, dan pembukaan tautan rilis terverifikasi.
- **Auto-Backup & Pemulihan Lokal**: Ekspor/impor konfigurasi dan notifikasi ke JSON dengan checksum SHA-256 sebelum proses update.
- **Onboarding, What's New & Docs**: Tutorial interaktif perdana, dialog *What's New* pasca-rilis, panduan penggunaan, dan dokumentasi implementasi bertahap.

### Ditingkatkan
- Layout adaptif untuk orientasi **Portrait** dan **Landscape/Tablet** (`NavigationBar` vs `NavigationRail` dan grid fleksibel).
- Dukungan offline menyeluruh untuk seluruh preferensi, cadangan, dan dokumentasi.

### Diperbaiki
- Penanganan `WindowInsets` edge-to-edge agar notch tidak berbenturan dengan status bar maupun navigation bar sistem.

---

## [0.9.0-beta] - 2026-11-10 — *Glass Prototype*

### Ditambahkan
- Prototipe awal animasi pil Dynamic Island dan kartu kaca translucent.
- Simulator skenario Live Activity dasar.
