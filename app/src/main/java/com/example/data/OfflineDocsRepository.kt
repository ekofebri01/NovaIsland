package com.example.data

import com.example.model.ChangelogEntry
import com.example.model.DrawerQuickToggle
import com.example.model.GuideStep
import com.example.model.ImplementationPhaseDoc
import com.example.model.NotchNotification
import com.example.model.NotificationCategory

object OfflineDocsRepository {

    const val CURRENT_VERSION_NAME = "1.0.0"
    const val CURRENT_VERSION_CODE = 1

    fun defaultQuickToggles(): List<DrawerQuickToggle> = listOf(
        DrawerQuickToggle("wifi", "Wi-Fi", true),
        DrawerQuickToggle("bt", "Bluetooth", true),
        DrawerQuickToggle("focus", "Mode Fokus", false),
        DrawerQuickToggle("dnd", "Jangan Ganggu", false),
        DrawerQuickToggle("sync", "Auto-Sinkron", true),
        DrawerQuickToggle("haptic", "Haptic Notch", true)
    )

    fun starterNotifications(): List<NotchNotification> {
        val now = System.currentTimeMillis()
        return listOf(
            NotchNotification(
                id = "live_music_1",
                title = "Midnight Aurora — Synthwave Mix",
                subtitle = "NovaMusic • Lossless Spatial Audio",
                appName = "Musik",
                category = NotificationCategory.MUSIC,
                timestampMillis = now - 45_000L,
                accentHex = 0xFF8B5CF6,
                progressFraction = 0.62f,
                isLiveActivity = true,
                isPlaying = true,
                primaryActionLabel = "Jeda",
                secondaryActionLabel = "Berikutnya"
            ),
            NotchNotification(
                id = "msg_1",
                title = "Raka Pratama",
                subtitle = "Desain Dynamic Notch barunya udah mulus banget!",
                appName = "Pesan",
                category = NotificationCategory.MESSAGE,
                timestampMillis = now - 120_000L,
                accentHex = 0xFF10B981,
                primaryActionLabel = "Balas",
                secondaryActionLabel = "Tandai Dibaca"
            ),
            NotchNotification(
                id = "delivery_1",
                title = "Kurir Tiba dalam 4 Menit",
                subtitle = "Paket Elektronik • 450m dari lokasi Anda",
                appName = "NovaExpress",
                category = NotificationCategory.DELIVERY,
                timestampMillis = now - 300_000L,
                accentHex = 0xFFF59E0B,
                progressFraction = 0.84f,
                isLiveActivity = true,
                primaryActionLabel = "Lacak",
                secondaryActionLabel = "Selesai"
            ),
            NotchNotification(
                id = "battery_1",
                title = "Pengisian Cepat Aktif • 88%",
                subtitle = "Estimasi penuh dalam 11 menit",
                appName = "Sistem",
                category = NotificationCategory.BATTERY,
                timestampMillis = now - 600_000L,
                accentHex = 0xFF22D3EE,
                progressFraction = 0.88f,
                isLiveActivity = false,
                primaryActionLabel = "Detail",
                secondaryActionLabel = "Tutup"
            )
        )
    }

    val whatsNewHighlights: List<String> = listOf(
        "Dynamic Notch bergaya iPhone 18 dengan animasi spring physics super halus saat muncul dan diketuk.",
        "Glassmorphism Notification Drawer layar penuh dengan efek frosted blur, kartu membulat, dan kontrol cepat.",
        "Kustomisasi lengkap: posisi X/Y, lebar, tinggi, radius sudut, durasi tampil, warna latar, serta mode Gelap/Terang otomatis.",
        "GitHub In-App Update Engine dengan auto-backup JSON sebelum update, animasi progres unduhan, dan verifikasi integritas SHA-256.",
        "Dukungan 100% offline dengan riwayat lokal, tutorial interaktif, panduan pengguna, changelog, dan dokumentasi bertahap."
    )

    val changelogEntries: List<ChangelogEntry> = listOf(
        ChangelogEntry(
            version = "v1.0.0",
            date = "18 November 2026",
            codename = "Genesis Aurora",
            isLatest = true,
            sha256Short = "e3b0c442...98fc1c14",
            highlights = "Rilis perdana NovaIsland dengan Dynamic Notch responsif, Glassmorphism Notification Drawer, kustomisasi penuh, dan sistem pembaruan GitHub terverifikasi SHA-256.",
            added = listOf(
                "Dynamic Notch interaktif 3 tahap (Compact Pill, Active Live Pill, Expanded Card) dengan animasi spring.",
                "Glassmorphism Notification Drawer dengan efek blur, kartu membulat, swipe-to-dismiss, dan Control Center mini.",
                "Bridge NotificationListenerService untuk menangkap notifikasi perangkat secara real-time.",
                "Pengaturan posisi, ukuran, radius sudut, durasi, warna personal, serta tema Otomatis/Terang/Gelap.",
                "GitHub Releases Update Checker + animasi progres unduhan + verifikasi hash SHA-256 + notifikasi push update.",
                "Auto-Backup JSON otomatis sebelum pembaruan dan pemulihan cadangan secara offline.",
                "Tutorial interaktif saat pertama kali dijalankan, dialog What's New, Panduan, dan Dokumentasi bertahap."
            ),
            improved = listOf(
                "Tata letak adaptif untuk layar Portrait maupun Landscape (NavigationBar vs NavigationRail + Grid dinamis).",
                "Dukungan offline penuh menggunakan Jetpack DataStore dan cache rilis lokal."
            ),
            fixed = listOf(
                "Optimasi area aman status bar & kamera notch dengan edge-to-edge WindowInsets."
            )
        ),
        ChangelogEntry(
            version = "v0.9.0-beta",
            date = "10 November 2026",
            codename = "Glass Prototype",
            isLatest = false,
            sha256Short = "9f86d081...d6b7c5a1",
            highlights = "Pratinjau arsitektur antarmuka kaca (glassmorphism) dan sistem pratinjau Live Activity.",
            added = listOf(
                "Prototipe animasi morphing pil Dynamic Island.",
                "Pemicu simulasi Pesan, Musik, Timer, Kurir, dan Panggilan."
            ),
            improved = listOf(
                "Kinerja rendering gradien dan blur pada GPU Android."
            ),
            fixed = listOf(
                "Perbaikan tumpang tindih sudut pada rasio layar ultra-wide."
            )
        )
    )

    val userGuideSteps: List<GuideStep> = listOf(
        GuideStep(
            stepNumber = 1,
            title = "Ketuk & Perluas Dynamic Notch",
            description = "Notch di bagian atas layar menampilkan Live Activity seperti musik, timer, atau pesan masuk. Ketuk pil notch untuk memperluas menjadi kartu interaktif.",
            tip = "Anda dapat mengubah posisi X/Y, lebar, tinggi, dan durasi tampil di tab Pengaturan."
        ),
        GuideStep(
            stepNumber = 2,
            title = "Buka Glassmorphism Notification Drawer",
            description = "Geser turun dari area notch atas atau tekan tombol 'Buka Glass Drawer' untuk melihat panel notifikasi bergaya iPhone dengan efek kaca buram.",
            tip = "Geser kartu notifikasi ke kiri/kanan untuk menghapus, atau ketuk kartu untuk melihat aksi cepat."
        ),
        GuideStep(
            stepNumber = 3,
            title = "Hubungkan Notifikasi Sistem Nyata",
            description = "Aktifkan izin Akses Notifikasi di Pengaturan > Integrasi Sistem agar notifikasi asli dari aplikasi lain langsung tampil di NovaIsland.",
            tip = "Semua data notifikasi dan pengaturan disimpan aman di perangkat Anda (offline-first)."
        ),
        GuideStep(
            stepNumber = 4,
            title = "Pembaruan GitHub & Keamanan SHA-256",
            description = "Gunakan menu Cek Pembaruan GitHub untuk memeriksa rilis terbaru. Sebelum pembaruan dimulai, NovaIsland otomatis mencadangkan pengaturan Anda dan memverifikasi hash SHA-256.",
            tip = "Anda juga bisa memulihkan cadangan kapan saja di bagian Cadangan Lokal."
        )
    )

    val implementationPhases: List<ImplementationPhaseDoc> = listOf(
        ImplementationPhaseDoc(
            phaseNumber = 1,
            title = "Fondasi Tema, Tipografi & Persistency Offline",
            status = "Selesai",
            summary = "Setup NovaIsland dengan Jetpack Compose Material 3, font lokal Plus Jakarta Sans & JetBrains Mono, tema Auto/Gelap/Terang, dan Jetpack DataStore.",
            deliverables = listOf(
                "DataStore Preferences untuk semua konfigurasi notch & tema",
                "Palet warna personal (Notch, Drawer Glass, Accent Glow)",
                "Adaptive Launcher Icon & Edge-to-Edge WindowInsets"
            )
        ),
        ImplementationPhaseDoc(
            phaseNumber = 2,
            title = "Mesin Dynamic Notch & Live Activities",
            status = "Selesai",
            summary = "Implementasi pil notch interaktif di area status bar dengan 3 state animasi spring physics dan auto-collapse timer.",
            deliverables = listOf(
                "Morphing Compact -> Active Pill -> Expanded Card",
                "Kontrol Live Activity (Musik, Timer, Panggilan, Kurir, Pesan)",
                "Slider real-time posisi X/Y, ukuran, radius, dan durasi"
            )
        ),
        ImplementationPhaseDoc(
            phaseNumber = 3,
            title = "Glassmorphism Notification Drawer & System Bridge",
            status = "Selesai",
            summary = "Panel notifikasi layar penuh bergaya iPhone dengan efek blur glassmorphism, quick toggles, dan NotificationListenerService.",
            deliverables = listOf(
                "Drawer pull-down responsif dengan RenderEffect blur",
                "Kartu notifikasi membulat dengan swipe-to-dismiss",
                "Bridge notifikasi real-time dari Android NotificationListenerService"
            )
        ),
        ImplementationPhaseDoc(
            phaseNumber = 4,
            title = "GitHub OTA Checker, Auto-Backup & Verifikasi SHA-256",
            status = "Selesai",
            summary = "Integrasi GitHub Releases API dengan auto-backup JSON sebelum update, animasi progres unduhan, verifikasi digest SHA-256, dan notifikasi push.",
            deliverables = listOf(
                "Retrofit + OkHttp GitHub Releases API + fallback cache offline",
                "BackupManager JSON dengan checksum SHA-256",
                "NotificationChannel untuk push notification pembaruan"
            )
        ),
        ImplementationPhaseDoc(
            phaseNumber = 5,
            title = "Dokumentasi, Tutorial Interaktif & UI Adaptif",
            status = "Selesai",
            summary = "Penyempurnaan layout Portrait/Landscape, tutorial interaktif perdana, dialog What's New, Changelog, dan README dalam aplikasi.",
            deliverables = listOf(
                "NavigationBar (Portrait) & NavigationRail + Grid (Landscape/Tablet)",
                "Onboarding interaktif & What's New otomatis",
                "README.md, CHANGELOG.md, dan IMPLEMENTATION_GUIDE.md"
            )
        )
    )
}
