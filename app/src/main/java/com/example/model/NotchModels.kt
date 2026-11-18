package com.example.model

import com.squareup.moshi.JsonClass

enum class ThemeMode(val label: String) {
    AUTO("Otomatis (Sistem)"),
    LIGHT("Mode Terang"),
    DARK("Mode Gelap")
}

enum class NotificationCategory(val label: String) {
    MESSAGE("Pesan"),
    MUSIC("Musik"),
    TIMER("Timer"),
    CALL("Panggilan"),
    DELIVERY("Kurir"),
    BATTERY("Baterai"),
    UPDATE("Pembaruan"),
    SYSTEM("Sistem")
}

enum class NotchVisualState {
    COMPACT,
    ACTIVE_PILL,
    EXPANDED
}

@JsonClass(generateAdapter = true)
data class NotchNotification(
    val id: String,
    val title: String,
    val subtitle: String,
    val appName: String,
    val category: NotificationCategory,
    val timestampMillis: Long,
    val accentHex: Long = 0xFF22D3EE,
    val progressFraction: Float? = null,
    val isLiveActivity: Boolean = false,
    val isFromSystemListener: Boolean = false,
    val isPlaying: Boolean = true,
    val isRead: Boolean = false,
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null
)

@JsonClass(generateAdapter = true)
data class NotchSettings(
    val xOffsetFraction: Float = 0f,        // -1f (kiri) .. 0f (tengah) .. +1f (kanan)
    val yOffsetDp: Float = 8f,              // 2f .. 36f
    val widthScale: Float = 1.0f,           // 0.75f .. 1.40f
    val heightScale: Float = 1.0f,          // 0.85f .. 1.45f
    val cornerRadiusDp: Float = 24f,        // 14f .. 32f
    val durationSeconds: Int = 5,           // 2 .. 15
    val themeMode: ThemeMode = ThemeMode.AUTO,
    val notchBgHex: Long = 0xFF05070B,
    val drawerTintHex: Long = 0xFF0F172A,
    val accentHex: Long = 0xFF22D3EE,
    val glassBlurRadiusDp: Float = 24f,     // 8f .. 36f
    val hapticsEnabled: Boolean = true,
    val autoBackupBeforeUpdate: Boolean = true,
    val updateNotificationsEnabled: Boolean = true,
    val githubRepoOwner: String = "ekofebriarianto",
    val githubRepoName: String = "NovaIsland",
    val hasCompletedOnboarding: Boolean = false,
    val lastSeenVersionCode: Int = 0
)

data class DrawerQuickToggle(
    val id: String,
    val label: String,
    val isActive: Boolean
)

@JsonClass(generateAdapter = true)
data class BackupSnapshot(
    val id: String,
    val createdAtMillis: Long,
    val appVersionName: String,
    val appVersionCode: Int,
    val notificationCount: Int,
    val sha256Checksum: String,
    val fileName: String,
    val settings: NotchSettings,
    val notifications: List<NotchNotification>
)

@JsonClass(generateAdapter = true)
data class UpdateReleaseInfo(
    val versionName: String,
    val versionCode: Int,
    val tagName: String,
    val publishedAt: String,
    val releaseNotes: String,
    val assetName: String,
    val assetSizeBytes: Long,
    val expectedSha256: String,
    val releaseUrl: String,
    val downloadUrl: String,
    val isFromNetwork: Boolean = false
)

sealed interface UpdatePhase {
    data object Idle : UpdatePhase
    data object Checking : UpdatePhase
    data class UpToDate(val currentVersion: String, val isOfflineMode: Boolean) : UpdatePhase
    data class Available(val release: UpdateReleaseInfo) : UpdatePhase
    data class BackingUp(val release: UpdateReleaseInfo) : UpdatePhase
    data class Downloading(
        val release: UpdateReleaseInfo,
        val progress: Float,
        val downloadedBytes: Long,
        val totalBytes: Long
    ) : UpdatePhase
    data class VerifyingHash(
        val release: UpdateReleaseInfo,
        val progress: Float
    ) : UpdatePhase
    data class VerifiedReady(
        val release: UpdateReleaseInfo,
        val computedSha256: String,
        val backupFileName: String?
    ) : UpdatePhase
    data class Error(val message: String) : UpdatePhase
}

data class ChangelogEntry(
    val version: String,
    val date: String,
    val codename: String,
    val isLatest: Boolean = false,
    val sha256Short: String,
    val highlights: String,
    val added: List<String>,
    val improved: List<String>,
    val fixed: List<String>
)

data class GuideStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val tip: String
)

data class ImplementationPhaseDoc(
    val phaseNumber: Int,
    val title: String,
    val status: String,
    val summary: String,
    val deliverables: List<String>
)
