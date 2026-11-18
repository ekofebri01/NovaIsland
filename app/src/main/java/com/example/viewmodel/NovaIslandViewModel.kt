package com.example.viewmodel

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BackupManager
import com.example.data.GitHubUpdateService
import com.example.data.OfflineDocsRepository
import com.example.data.SettingsDataStore
import com.example.data.UpdateCheckResult
import com.example.data.UpdateNotificationHelper
import com.example.model.BackupSnapshot
import com.example.model.DrawerQuickToggle
import com.example.model.NotchNotification
import com.example.model.NotchSettings
import com.example.model.NotchVisualState
import com.example.model.NotificationCategory
import com.example.model.ThemeMode
import com.example.model.UpdatePhase
import com.example.model.UpdateReleaseInfo
import com.example.service.LiveNotificationBridge
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class NovaIslandViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsDataStore = SettingsDataStore(application)
    private val backupManager = BackupManager(application)
    private val updateService = GitHubUpdateService(application)

    val settings: StateFlow<NotchSettings> = settingsDataStore.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotchSettings())

    private val _notifications = MutableStateFlow(OfflineDocsRepository.starterNotifications())
    val notifications: StateFlow<List<NotchNotification>> = _notifications.asStateFlow()

    private val _activeNotchNotification = MutableStateFlow<NotchNotification?>(
        OfflineDocsRepository.starterNotifications().firstOrNull()
    )
    val activeNotchNotification: StateFlow<NotchNotification?> = _activeNotchNotification.asStateFlow()

    private val _notchVisualState = MutableStateFlow(NotchVisualState.ACTIVE_PILL)
    val notchVisualState: StateFlow<NotchVisualState> = _notchVisualState.asStateFlow()

    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen: StateFlow<Boolean> = _isDrawerOpen.asStateFlow()

    private val _quickToggles = MutableStateFlow(OfflineDocsRepository.defaultQuickToggles())
    val quickToggles: StateFlow<List<DrawerQuickToggle>> = _quickToggles.asStateFlow()

    private val _backups = MutableStateFlow<List<BackupSnapshot>>(emptyList())
    val backups: StateFlow<List<BackupSnapshot>> = _backups.asStateFlow()

    private val _updatePhase = MutableStateFlow<UpdatePhase>(UpdatePhase.Idle)
    val updatePhase: StateFlow<UpdatePhase> = _updatePhase.asStateFlow()

    private val _showUpdateSheet = MutableStateFlow(false)
    val showUpdateSheet: StateFlow<Boolean> = _showUpdateSheet.asStateFlow()

    private val _showWhatsNewDialog = MutableStateFlow(false)
    val showWhatsNewDialog: StateFlow<Boolean> = _showWhatsNewDialog.asStateFlow()

    private val _isOnline = MutableStateFlow(updateService.isNetworkAvailable())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _isNotificationAccessGranted = MutableStateFlow(
        LiveNotificationBridge.isNotificationAccessEnabled(application)
    )
    val isNotificationAccessGranted: StateFlow<Boolean> = _isNotificationAccessGranted.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private var autoCollapseJob: Job? = null

    init {
        UpdateNotificationHelper.ensureChannel(application)
        refreshBackups()
        checkWhatsNewOnStart()
        observeSystemNotifications()
    }

    private fun checkWhatsNewOnStart() {
        viewModelScope.launch {
            val currentSettings = settingsDataStore.settingsFlow.first()
            if (currentSettings.hasCompletedOnboarding &&
                currentSettings.lastSeenVersionCode < OfflineDocsRepository.CURRENT_VERSION_CODE
            ) {
                _showWhatsNewDialog.value = true
            }
        }
    }

    private fun observeSystemNotifications() {
        viewModelScope.launch {
            LiveNotificationBridge.incomingNotifications.collect { incoming ->
                pushNotchNotification(incoming)
            }
        }
    }

    fun refreshSystemPermissions() {
        _isNotificationAccessGranted.value =
            LiveNotificationBridge.isNotificationAccessEnabled(getApplication())
        _isOnline.value = updateService.isNetworkAvailable()
    }

    fun pushNotchNotification(notification: NotchNotification) {
        _notifications.value = listOf(notification) + _notifications.value.filterNot { it.id == notification.id }
        _activeNotchNotification.value = notification
        _notchVisualState.value = NotchVisualState.ACTIVE_PILL
        performLightHaptic()
        scheduleAutoCollapse(notification)
    }

    fun triggerPresetNotification(category: NotificationCategory) {
        val now = System.currentTimeMillis()
        val id = "${category.name.lowercase()}_${UUID.randomUUID().toString().take(6)}"
        val item = when (category) {
            NotificationCategory.MESSAGE -> NotchNotification(
                id = id,
                title = "Nadia Kusuma",
                subtitle = "Jangan lupa review UI NovaIsland jam 16:00 ya!",
                appName = "Pesan",
                category = NotificationCategory.MESSAGE,
                timestampMillis = now,
                accentHex = 0xFF10B981,
                primaryActionLabel = "Balas",
                secondaryActionLabel = "Tandai Dibaca"
            )
            NotificationCategory.MUSIC -> NotchNotification(
                id = "live_music_1",
                title = "Neon Horizon — Orbital",
                subtitle = "NovaMusic • Hi-Res Spatial Audio",
                appName = "Musik",
                category = NotificationCategory.MUSIC,
                timestampMillis = now,
                accentHex = 0xFF8B5CF6,
                progressFraction = 0.48f,
                isLiveActivity = true,
                isPlaying = true,
                primaryActionLabel = "Jeda",
                secondaryActionLabel = "Lanjut"
            )
            NotificationCategory.TIMER -> NotchNotification(
                id = "live_timer_1",
                title = "Timer Fokus • 14:32",
                subtitle = "Sesi Deep Work berlangsung",
                appName = "Jam",
                category = NotificationCategory.TIMER,
                timestampMillis = now,
                accentHex = 0xFFF59E0B,
                progressFraction = 0.72f,
                isLiveActivity = true,
                primaryActionLabel = "Jeda",
                secondaryActionLabel = "Reset"
            )
            NotificationCategory.CALL -> NotchNotification(
                id = id,
                title = "Panggilan Masuk • Dimas",
                subtitle = "Audio HD Terenkripsi",
                appName = "Telepon",
                category = NotificationCategory.CALL,
                timestampMillis = now,
                accentHex = 0xFF38BDF8,
                isLiveActivity = true,
                primaryActionLabel = "Terima",
                secondaryActionLabel = "Tolak"
            )
            NotificationCategory.DELIVERY -> NotchNotification(
                id = "delivery_1",
                title = "Kurir Dalam Perjalanan",
                subtitle = "Tiba estimasi 3 menit lagi",
                appName = "NovaExpress",
                category = NotificationCategory.DELIVERY,
                timestampMillis = now,
                accentHex = 0xFFF59E0B,
                progressFraction = 0.90f,
                isLiveActivity = true,
                primaryActionLabel = "Lacak",
                secondaryActionLabel = "Selesai"
            )
            NotificationCategory.BATTERY -> NotchNotification(
                id = "battery_1",
                title = "MagCharge Aktif • 94%",
                subtitle = "Daya penuh dalam 6 menit",
                appName = "Sistem",
                category = NotificationCategory.BATTERY,
                timestampMillis = now,
                accentHex = 0xFF22D3EE,
                progressFraction = 0.94f,
                primaryActionLabel = "Oke",
                secondaryActionLabel = "Tutup"
            )
            NotificationCategory.UPDATE -> NotchNotification(
                id = id,
                title = "Pembaruan GitHub Tersedia",
                subtitle = "Verifikasi SHA-256 siap dijalankan",
                appName = "NovaUpdater",
                category = NotificationCategory.UPDATE,
                timestampMillis = now,
                accentHex = 0xFF22D3EE,
                primaryActionLabel = "Periksa",
                secondaryActionLabel = "Nanti"
            )
            NotificationCategory.SYSTEM -> NotchNotification(
                id = id,
                title = "Mode Fokus Diaktifkan",
                subtitle = "Notifikasi non-prioritas dibisukan",
                appName = "Sistem",
                category = NotificationCategory.SYSTEM,
                timestampMillis = now,
                accentHex = 0xFF8B5CF6,
                primaryActionLabel = "Atur",
                secondaryActionLabel = "Tutup"
            )
        }
        pushNotchNotification(item)
    }

    fun onNotchTapped() {
        performLightHaptic()
        val current = _activeNotchNotification.value
        if (current == null) {
            val first = _notifications.value.firstOrNull()
            if (first != null) {
                _activeNotchNotification.value = first
                _notchVisualState.value = NotchVisualState.EXPANDED
                autoCollapseJob?.cancel()
            } else {
                openDrawer()
            }
            return
        }

        _notchVisualState.value = when (_notchVisualState.value) {
            NotchVisualState.COMPACT -> NotchVisualState.EXPANDED
            NotchVisualState.ACTIVE_PILL -> NotchVisualState.EXPANDED
            NotchVisualState.EXPANDED -> NotchVisualState.ACTIVE_PILL
        }

        if (_notchVisualState.value == NotchVisualState.EXPANDED) {
            autoCollapseJob?.cancel()
        } else {
            scheduleAutoCollapse(current)
        }
    }

    fun collapseNotchToCompact() {
        autoCollapseJob?.cancel()
        _notchVisualState.value = NotchVisualState.COMPACT
    }

    fun toggleMusicPlayback() {
        val current = _activeNotchNotification.value ?: return
        if (current.category != NotificationCategory.MUSIC) return
        val toggled = current.copy(
            isPlaying = !current.isPlaying,
            primaryActionLabel = if (!current.isPlaying) "Jeda" else "Putar"
        )
        _activeNotchNotification.value = toggled
        _notifications.value = _notifications.value.map { if (it.id == toggled.id) toggled else it }
        performLightHaptic()
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filterNot { it.id == id }
        if (_activeNotchNotification.value?.id == id) {
            val next = _notifications.value.firstOrNull()
            _activeNotchNotification.value = next
            _notchVisualState.value = if (next != null) NotchVisualState.ACTIVE_PILL else NotchVisualState.COMPACT
        }
    }

    fun clearAllNotifications() {
        autoCollapseJob?.cancel()
        _notifications.value = emptyList()
        _activeNotchNotification.value = null
        _notchVisualState.value = NotchVisualState.COMPACT
        performLightHaptic()
    }

    fun selectNotificationInNotch(notification: NotchNotification) {
        _activeNotchNotification.value = notification
        _notchVisualState.value = NotchVisualState.EXPANDED
        _isDrawerOpen.value = false
        performLightHaptic()
    }

    fun openDrawer() {
        performLightHaptic()
        _isDrawerOpen.value = true
    }

    fun closeDrawer() {
        _isDrawerOpen.value = false
    }

    fun toggleQuickControl(toggleId: String) {
        performLightHaptic()
        _quickToggles.value = _quickToggles.value.map { toggle ->
            if (toggle.id == toggleId) toggle.copy(isActive = !toggle.isActive) else toggle
        }
    }

    private fun scheduleAutoCollapse(notification: NotchNotification) {
        autoCollapseJob?.cancel()
        autoCollapseJob = viewModelScope.launch {
            val durationMs = (settings.value.durationSeconds.coerceIn(2, 15)) * 1000L
            delay(durationMs)
            if (_notchVisualState.value != NotchVisualState.EXPANDED) {
                _notchVisualState.value = if (notification.isLiveActivity) {
                    NotchVisualState.ACTIVE_PILL
                } else {
                    NotchVisualState.COMPACT
                }
            }
        }
    }

    // Settings mutations
    fun updateSettings(transform: (NotchSettings) -> NotchSettings) {
        viewModelScope.launch {
            settingsDataStore.update(transform)
        }
    }

    fun updateThemeMode(mode: ThemeMode) = updateSettings { it.copy(themeMode = mode) }

    fun resetNotchDefaults() = updateSettings {
        it.copy(
            xOffsetFraction = 0f,
            yOffsetDp = 8f,
            widthScale = 1.0f,
            heightScale = 1.0f,
            cornerRadiusDp = 24f,
            durationSeconds = 5,
            notchBgHex = 0xFF05070B,
            drawerTintHex = 0xFF0F172A,
            accentHex = 0xFF22D3EE,
            glassBlurRadiusDp = 24f
        )
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsDataStore.update {
                it.copy(
                    hasCompletedOnboarding = true,
                    lastSeenVersionCode = OfflineDocsRepository.CURRENT_VERSION_CODE
                )
            }
        }
    }

    fun replayOnboarding() {
        viewModelScope.launch {
            settingsDataStore.update { it.copy(hasCompletedOnboarding = false) }
        }
    }

    fun openWhatsNew() {
        _showWhatsNewDialog.value = true
    }

    fun dismissWhatsNew() {
        _showWhatsNewDialog.value = false
        viewModelScope.launch {
            settingsDataStore.update {
                it.copy(lastSeenVersionCode = OfflineDocsRepository.CURRENT_VERSION_CODE)
            }
        }
    }

    // Backup & Restore
    fun refreshBackups() {
        viewModelScope.launch {
            _backups.value = backupManager.listBackups()
        }
    }

    fun createManualBackup() {
        viewModelScope.launch {
            val snapshot = backupManager.createBackup(settings.value, _notifications.value)
            refreshBackups()
            _statusBanner.value = "Cadangan dibuat: ${snapshot.fileName} (SHA-256 terverifikasi)"
        }
    }

    fun restoreFromBackup(snapshot: BackupSnapshot) {
        viewModelScope.launch {
            val loaded = backupManager.restoreBackup(snapshot.fileName)
            if (loaded != null) {
                settingsDataStore.saveSettings(loaded.settings)
                _notifications.value = loaded.notifications
                _activeNotchNotification.value = loaded.notifications.firstOrNull()
                _statusBanner.value = "Berhasil memulihkan cadangan ${loaded.fileName}"
            } else {
                _statusBanner.value = "Gagal membaca berkas cadangan."
            }
        }
    }

    fun deleteBackup(snapshot: BackupSnapshot) {
        viewModelScope.launch {
            backupManager.deleteBackup(snapshot.fileName)
            refreshBackups()
        }
    }

    // GitHub In-App Update Engine
    fun openUpdateSheet() {
        _showUpdateSheet.value = true
    }

    fun closeUpdateSheet() {
        _showUpdateSheet.value = false
    }

    fun checkGitHubUpdate(openSheet: Boolean = true) {
        if (openSheet) _showUpdateSheet.value = true
        _isOnline.value = updateService.isNetworkAvailable()
        viewModelScope.launch {
            _updatePhase.value = UpdatePhase.Checking
            val currentSettings = settings.value
            when (
                val result = updateService.checkForUpdates(
                    owner = currentSettings.githubRepoOwner,
                    repo = currentSettings.githubRepoName
                )
            ) {
                is UpdateCheckResult.UpdateAvailable -> {
                    _updatePhase.value = UpdatePhase.Available(result.release)
                    if (currentSettings.updateNotificationsEnabled) {
                        UpdateNotificationHelper.showUpdateAvailableNotification(
                            getApplication(),
                            result.release.tagName,
                            result.release.releaseNotes
                        )
                    }
                    pushNotchNotification(
                        NotchNotification(
                            id = "update_${result.release.tagName}",
                            title = "Rilis Baru ${result.release.tagName}",
                            subtitle = "Ketuk untuk backup otomatis & verifikasi SHA-256",
                            appName = "GitHub OTA",
                            category = NotificationCategory.UPDATE,
                            timestampMillis = System.currentTimeMillis(),
                            accentHex = 0xFF22D3EE,
                            primaryActionLabel = "Perbarui",
                            secondaryActionLabel = "Nanti"
                        )
                    )
                }
                is UpdateCheckResult.AlreadyUpToDate -> {
                    _updatePhase.value = UpdatePhase.UpToDate(
                        currentVersion = result.currentVersion,
                        isOfflineMode = result.isOffline
                    )
                }
                is UpdateCheckResult.Failure -> {
                    _updatePhase.value = UpdatePhase.Error(result.message)
                }
            }
        }
    }

    fun startUpdatePipeline(release: UpdateReleaseInfo) {
        viewModelScope.launch {
            var backupFileName: String? = null
            if (settings.value.autoBackupBeforeUpdate) {
                _updatePhase.value = UpdatePhase.BackingUp(release)
                val backup = backupManager.createBackup(settings.value, _notifications.value)
                backupFileName = backup.fileName
                refreshBackups()
                delay(450L)
            }

            _updatePhase.value = UpdatePhase.Downloading(release, 0f, 0L, release.assetSizeBytes)
            val (_, computedSha) = updateService.downloadAndVerifyReleaseBundle(
                release = release,
                onDownloadProgress = { progress, down, total ->
                    _updatePhase.value = UpdatePhase.Downloading(release, progress, down, total)
                },
                onVerifyProgress = { progress ->
                    _updatePhase.value = UpdatePhase.VerifyingHash(release, progress)
                }
            )

            _updatePhase.value = UpdatePhase.VerifiedReady(
                release = release,
                computedSha256 = computedSha,
                backupFileName = backupFileName
            )
            performLightHaptic()
        }
    }

    fun runLocalVerificationPipelinePreview() {
        val currentSettings = settings.value
        val previewRelease = UpdateReleaseInfo(
            versionName = "1.1.0-preview",
            versionCode = 10100,
            tagName = "v1.1.0",
            publishedAt = "2026-11-18",
            releaseNotes = "Uji Verifikasi Paket Rilis Lokal: Backup otomatis sebelum pembaruan, kalkulasi hash SHA-256 nyata, dan tautan rilis GitHub terverifikasi.",
            assetName = "NovaIsland-v1.1.0-manifest.json",
            assetSizeBytes = 256_000L,
            expectedSha256 = BackupManager.sha256Hex(
                "v1.1.0|1.1.0-preview|Uji Verifikasi Paket Rilis Lokal: Backup otomatis sebelum pembaruan, kalkulasi hash SHA-256 nyata, dan tautan rilis GitHub terverifikasi.".toByteArray()
            ),
            releaseUrl = "https://github.com/${currentSettings.githubRepoOwner}/${currentSettings.githubRepoName}/releases",
            downloadUrl = "https://github.com/${currentSettings.githubRepoOwner}/${currentSettings.githubRepoName}/releases",
            isFromNetwork = false
        )
        _showUpdateSheet.value = true
        startUpdatePipeline(previewRelease)
    }

    fun sendTestPushNotification() {
        UpdateNotificationHelper.showTestSystemNotification(
            getApplication(),
            "NovaIsland Dynamic Notch",
            "Notifikasi push pembaruan & bridge sistem berfungsi optimal."
        )
        triggerPresetNotification(NotificationCategory.UPDATE)
    }

    fun clearStatusBanner() {
        _statusBanner.value = null
    }

    private fun performLightHaptic() {
        if (!settings.value.hapticsEnabled) return
        val app = getApplication<Application>()
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = app.getSystemService(VibratorManager::class.java)
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            app.getSystemService(Vibrator::class.java)
        }
        if (vibrator?.hasVibrator() == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(18L, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
