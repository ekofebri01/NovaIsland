package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.NotchSettings
import com.example.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.novaSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "novaisland_settings"
)

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val X_OFFSET = floatPreferencesKey("x_offset_fraction")
        val Y_OFFSET = floatPreferencesKey("y_offset_dp")
        val WIDTH_SCALE = floatPreferencesKey("width_scale")
        val HEIGHT_SCALE = floatPreferencesKey("height_scale")
        val CORNER_RADIUS = floatPreferencesKey("corner_radius_dp")
        val DURATION_SECONDS = intPreferencesKey("duration_seconds")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val NOTCH_BG_HEX = longPreferencesKey("notch_bg_hex")
        val DRAWER_TINT_HEX = longPreferencesKey("drawer_tint_hex")
        val ACCENT_HEX = longPreferencesKey("accent_hex")
        val GLASS_BLUR_DP = floatPreferencesKey("glass_blur_dp")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val AUTO_BACKUP_BEFORE_UPDATE = booleanPreferencesKey("auto_backup_before_update")
        val UPDATE_NOTIFICATIONS_ENABLED = booleanPreferencesKey("update_notifications_enabled")
        val GITHUB_OWNER = stringPreferencesKey("github_owner")
        val GITHUB_REPO = stringPreferencesKey("github_repo")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val LAST_SEEN_VERSION_CODE = intPreferencesKey("last_seen_version_code")
    }

    val settingsFlow: Flow<NotchSettings> = context.novaSettingsDataStore.data.map { prefs ->
        val themeName = prefs[Keys.THEME_MODE] ?: ThemeMode.AUTO.name
        val themeMode = ThemeMode.entries.firstOrNull { it.name == themeName } ?: ThemeMode.AUTO
        NotchSettings(
            xOffsetFraction = prefs[Keys.X_OFFSET] ?: 0f,
            yOffsetDp = prefs[Keys.Y_OFFSET] ?: 8f,
            widthScale = prefs[Keys.WIDTH_SCALE] ?: 1.0f,
            heightScale = prefs[Keys.HEIGHT_SCALE] ?: 1.0f,
            cornerRadiusDp = prefs[Keys.CORNER_RADIUS] ?: 24f,
            durationSeconds = prefs[Keys.DURATION_SECONDS] ?: 5,
            themeMode = themeMode,
            notchBgHex = prefs[Keys.NOTCH_BG_HEX] ?: 0xFF05070B,
            drawerTintHex = prefs[Keys.DRAWER_TINT_HEX] ?: 0xFF0F172A,
            accentHex = prefs[Keys.ACCENT_HEX] ?: 0xFF22D3EE,
            glassBlurRadiusDp = prefs[Keys.GLASS_BLUR_DP] ?: 24f,
            hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true,
            autoBackupBeforeUpdate = prefs[Keys.AUTO_BACKUP_BEFORE_UPDATE] ?: true,
            updateNotificationsEnabled = prefs[Keys.UPDATE_NOTIFICATIONS_ENABLED] ?: true,
            githubRepoOwner = prefs[Keys.GITHUB_OWNER] ?: "ekofebriarianto",
            githubRepoName = prefs[Keys.GITHUB_REPO] ?: "NovaIsland",
            hasCompletedOnboarding = prefs[Keys.HAS_COMPLETED_ONBOARDING] ?: false,
            lastSeenVersionCode = prefs[Keys.LAST_SEEN_VERSION_CODE] ?: 0
        )
    }

    suspend fun saveSettings(settings: NotchSettings) {
        context.novaSettingsDataStore.edit { prefs ->
            prefs[Keys.X_OFFSET] = settings.xOffsetFraction
            prefs[Keys.Y_OFFSET] = settings.yOffsetDp
            prefs[Keys.WIDTH_SCALE] = settings.widthScale
            prefs[Keys.HEIGHT_SCALE] = settings.heightScale
            prefs[Keys.CORNER_RADIUS] = settings.cornerRadiusDp
            prefs[Keys.DURATION_SECONDS] = settings.durationSeconds
            prefs[Keys.THEME_MODE] = settings.themeMode.name
            prefs[Keys.NOTCH_BG_HEX] = settings.notchBgHex
            prefs[Keys.DRAWER_TINT_HEX] = settings.drawerTintHex
            prefs[Keys.ACCENT_HEX] = settings.accentHex
            prefs[Keys.GLASS_BLUR_DP] = settings.glassBlurRadiusDp
            prefs[Keys.HAPTICS_ENABLED] = settings.hapticsEnabled
            prefs[Keys.AUTO_BACKUP_BEFORE_UPDATE] = settings.autoBackupBeforeUpdate
            prefs[Keys.UPDATE_NOTIFICATIONS_ENABLED] = settings.updateNotificationsEnabled
            prefs[Keys.GITHUB_OWNER] = settings.githubRepoOwner
            prefs[Keys.GITHUB_REPO] = settings.githubRepoName
            prefs[Keys.HAS_COMPLETED_ONBOARDING] = settings.hasCompletedOnboarding
            prefs[Keys.LAST_SEEN_VERSION_CODE] = settings.lastSeenVersionCode
        }
    }

    suspend fun update(transform: (NotchSettings) -> NotchSettings) {
        context.novaSettingsDataStore.edit { prefs ->
            val themeName = prefs[Keys.THEME_MODE] ?: ThemeMode.AUTO.name
            val current = NotchSettings(
                xOffsetFraction = prefs[Keys.X_OFFSET] ?: 0f,
                yOffsetDp = prefs[Keys.Y_OFFSET] ?: 8f,
                widthScale = prefs[Keys.WIDTH_SCALE] ?: 1.0f,
                heightScale = prefs[Keys.HEIGHT_SCALE] ?: 1.0f,
                cornerRadiusDp = prefs[Keys.CORNER_RADIUS] ?: 24f,
                durationSeconds = prefs[Keys.DURATION_SECONDS] ?: 5,
                themeMode = ThemeMode.entries.firstOrNull { it.name == themeName } ?: ThemeMode.AUTO,
                notchBgHex = prefs[Keys.NOTCH_BG_HEX] ?: 0xFF05070B,
                drawerTintHex = prefs[Keys.DRAWER_TINT_HEX] ?: 0xFF0F172A,
                accentHex = prefs[Keys.ACCENT_HEX] ?: 0xFF22D3EE,
                glassBlurRadiusDp = prefs[Keys.GLASS_BLUR_DP] ?: 24f,
                hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true,
                autoBackupBeforeUpdate = prefs[Keys.AUTO_BACKUP_BEFORE_UPDATE] ?: true,
                updateNotificationsEnabled = prefs[Keys.UPDATE_NOTIFICATIONS_ENABLED] ?: true,
                githubRepoOwner = prefs[Keys.GITHUB_OWNER] ?: "ekofebriarianto",
                githubRepoName = prefs[Keys.GITHUB_REPO] ?: "NovaIsland",
                hasCompletedOnboarding = prefs[Keys.HAS_COMPLETED_ONBOARDING] ?: false,
                lastSeenVersionCode = prefs[Keys.LAST_SEEN_VERSION_CODE] ?: 0
            )
            val updated = transform(current)
            prefs[Keys.X_OFFSET] = updated.xOffsetFraction
            prefs[Keys.Y_OFFSET] = updated.yOffsetDp
            prefs[Keys.WIDTH_SCALE] = updated.widthScale
            prefs[Keys.HEIGHT_SCALE] = updated.heightScale
            prefs[Keys.CORNER_RADIUS] = updated.cornerRadiusDp
            prefs[Keys.DURATION_SECONDS] = updated.durationSeconds
            prefs[Keys.THEME_MODE] = updated.themeMode.name
            prefs[Keys.NOTCH_BG_HEX] = updated.notchBgHex
            prefs[Keys.DRAWER_TINT_HEX] = updated.drawerTintHex
            prefs[Keys.ACCENT_HEX] = updated.accentHex
            prefs[Keys.GLASS_BLUR_DP] = updated.glassBlurRadiusDp
            prefs[Keys.HAPTICS_ENABLED] = updated.hapticsEnabled
            prefs[Keys.AUTO_BACKUP_BEFORE_UPDATE] = updated.autoBackupBeforeUpdate
            prefs[Keys.UPDATE_NOTIFICATIONS_ENABLED] = updated.updateNotificationsEnabled
            prefs[Keys.GITHUB_OWNER] = updated.githubRepoOwner
            prefs[Keys.GITHUB_REPO] = updated.githubRepoName
            prefs[Keys.HAS_COMPLETED_ONBOARDING] = updated.hasCompletedOnboarding
            prefs[Keys.LAST_SEEN_VERSION_CODE] = updated.lastSeenVersionCode
        }
    }
}
