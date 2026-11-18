package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.UpdateNotificationHelper
import com.example.model.BackupSnapshot
import com.example.model.NotchSettings
import com.example.model.ThemeMode
import com.example.ui.theme.NovaColorPresets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    settings: NotchSettings,
    backups: List<BackupSnapshot>,
    isNotificationAccessGranted: Boolean,
    onUpdateSettings: ((NotchSettings) -> NotchSettings) -> Unit,
    onResetDefaults: () -> Unit,
    onCheckUpdate: () -> Unit,
    onCreateBackup: () -> Unit,
    onRestoreBackup: (BackupSnapshot) -> Unit,
    onDeleteBackup: (BackupSnapshot) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { }
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 680.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 74.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isWide) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            NotchGeometrySection(
                                settings = settings,
                                onUpdateSettings = onUpdateSettings,
                                onResetDefaults = onResetDefaults
                            )
                            SystemPermissionsSection(
                                settings = settings,
                                isNotificationAccessGranted = isNotificationAccessGranted,
                                hasPostNotificationPermission = UpdateNotificationHelper.hasNotificationPermission(context),
                                onRequestPostNotification = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                },
                                onOpenNotificationAccess = {
                                    runCatching {
                                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                                    }
                                },
                                onUpdateSettings = onUpdateSettings
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ThemeAndColorsSection(
                                settings = settings,
                                onUpdateSettings = onUpdateSettings
                            )
                            GitHubUpdateAndBackupSection(
                                settings = settings,
                                backups = backups,
                                onUpdateSettings = onUpdateSettings,
                                onCheckUpdate = onCheckUpdate,
                                onCreateBackup = onCreateBackup,
                                onRestoreBackup = onRestoreBackup,
                                onDeleteBackup = onDeleteBackup
                            )
                        }
                    }
                }
            } else {
                item {
                    NotchGeometrySection(
                        settings = settings,
                        onUpdateSettings = onUpdateSettings,
                        onResetDefaults = onResetDefaults
                    )
                }
                item {
                    ThemeAndColorsSection(
                        settings = settings,
                        onUpdateSettings = onUpdateSettings
                    )
                }
                item {
                    SystemPermissionsSection(
                        settings = settings,
                        isNotificationAccessGranted = isNotificationAccessGranted,
                        hasPostNotificationPermission = UpdateNotificationHelper.hasNotificationPermission(context),
                        onRequestPostNotification = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        },
                        onOpenNotificationAccess = {
                            runCatching {
                                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            }
                        },
                        onUpdateSettings = onUpdateSettings
                    )
                }
                item {
                    GitHubUpdateAndBackupSection(
                        settings = settings,
                        backups = backups,
                        onUpdateSettings = onUpdateSettings,
                        onCheckUpdate = onCheckUpdate,
                        onCreateBackup = onCreateBackup,
                        onRestoreBackup = onRestoreBackup,
                        onDeleteBackup = onDeleteBackup
                    )
                }
            }
        }
    }
}

@Composable
private fun NotchGeometrySection(
    settings: NotchSettings,
    onUpdateSettings: ((NotchSettings) -> NotchSettings) -> Unit,
    onResetDefaults: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Rounded.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Posisi, Ukuran & Durasi Notch", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                IconButton(
                    onClick = onResetDefaults,
                    modifier = Modifier.testTag("settings_reset_button")
                ) {
                    Icon(Icons.Rounded.RestartAlt, contentDescription = "Reset default")
                }
            }

            LabeledSlider(
                label = "Posisi Horizontal (X)",
                valueText = when {
                    settings.xOffsetFraction < -0.25f -> "Kiri"
                    settings.xOffsetFraction > 0.25f -> "Kanan"
                    else -> "Tengah"
                },
                value = settings.xOffsetFraction,
                range = -1f..1f,
                tag = "slider_x_offset",
                onValueChange = { v -> onUpdateSettings { it.copy(xOffsetFraction = v) } }
            )

            LabeledSlider(
                label = "Jarak Vertikal dari Atas (Y)",
                valueText = "${settings.yOffsetDp.roundToInt()} dp",
                value = settings.yOffsetDp,
                range = 2f..36f,
                tag = "slider_y_offset",
                onValueChange = { v -> onUpdateSettings { it.copy(yOffsetDp = v) } }
            )

            LabeledSlider(
                label = "Skala Lebar Notch",
                valueText = "${(settings.widthScale * 100).roundToInt()}%",
                value = settings.widthScale,
                range = 0.75f..1.40f,
                tag = "slider_width_scale",
                onValueChange = { v -> onUpdateSettings { it.copy(widthScale = v) } }
            )

            LabeledSlider(
                label = "Skala Tinggi Notch",
                valueText = "${(settings.heightScale * 100).roundToInt()}%",
                value = settings.heightScale,
                range = 0.85f..1.45f,
                tag = "slider_height_scale",
                onValueChange = { v -> onUpdateSettings { it.copy(heightScale = v) } }
            )

            LabeledSlider(
                label = "Radius Sudut Membulat",
                valueText = "${settings.cornerRadiusDp.roundToInt()} dp",
                value = settings.cornerRadiusDp,
                range = 14f..32f,
                tag = "slider_corner_radius",
                onValueChange = { v -> onUpdateSettings { it.copy(cornerRadiusDp = v) } }
            )

            LabeledSlider(
                label = "Durasi Tampil Notifikasi",
                valueText = "${settings.durationSeconds} detik",
                value = settings.durationSeconds.toFloat(),
                range = 2f..15f,
                tag = "slider_duration",
                onValueChange = { v -> onUpdateSettings { it.copy(durationSeconds = v.roundToInt()) } }
            )
        }
    }
}

@Composable
private fun ThemeAndColorsSection(
    settings: NotchSettings,
    onUpdateSettings: ((NotchSettings) -> NotchSettings) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Tema & Warna Personal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                val modes = ThemeMode.entries
                modes.forEachIndexed { idx, mode ->
                    SegmentedButton(
                        selected = settings.themeMode == mode,
                        onClick = { onUpdateSettings { it.copy(themeMode = mode) } },
                        shape = SegmentedButtonDefaults.itemShape(index = idx, count = modes.size),
                        modifier = Modifier.testTag("theme_mode_${mode.name.lowercase()}")
                    ) {
                        Text(
                            text = when (mode) {
                                ThemeMode.AUTO -> "Otomatis"
                                ThemeMode.LIGHT -> "Terang"
                                ThemeMode.DARK -> "Gelap"
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            ColorSwatchPicker(
                title = "Warna Latar Notch",
                options = NovaColorPresets.NotchBackgrounds,
                selectedHex = settings.notchBgHex,
                onSelect = { hex -> onUpdateSettings { it.copy(notchBgHex = hex) } }
            )

            ColorSwatchPicker(
                title = "Tint Kaca Glass Drawer",
                options = NovaColorPresets.DrawerTints,
                selectedHex = settings.drawerTintHex,
                onSelect = { hex -> onUpdateSettings { it.copy(drawerTintHex = hex) } }
            )

            ColorSwatchPicker(
                title = "Warna Aksen & Cahaya Notch",
                options = NovaColorPresets.AccentGlows,
                selectedHex = settings.accentHex,
                onSelect = { hex -> onUpdateSettings { it.copy(accentHex = hex) } }
            )

            LabeledSlider(
                label = "Intensitas Blur Glassmorphism",
                valueText = "${settings.glassBlurRadiusDp.roundToInt()} dp",
                value = settings.glassBlurRadiusDp,
                range = 8f..36f,
                tag = "slider_glass_blur",
                onValueChange = { v -> onUpdateSettings { it.copy(glassBlurRadiusDp = v) } }
            )
        }
    }
}

@Composable
private fun SystemPermissionsSection(
    settings: NotchSettings,
    isNotificationAccessGranted: Boolean,
    hasPostNotificationPermission: Boolean,
    onRequestPostNotification: () -> Unit,
    onOpenNotificationAccess: () -> Unit,
    onUpdateSettings: ((NotchSettings) -> NotchSettings) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Integrasi & Preferensi Sistem", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Umpan Balik Haptic", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Getar halus saat mengetuk notch atau drawer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.hapticsEnabled,
                    onCheckedChange = { enabled -> onUpdateSettings { it.copy(hapticsEnabled = enabled) } }
                )
            }

            OutlinedButton(
                onClick = onOpenNotificationAccess,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(
                    if (isNotificationAccessGranted) "Akses Notifikasi Sistem: AKTIF"
                    else "Buka Pengaturan Akses Notifikasi"
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                OutlinedButton(
                    onClick = onRequestPostNotification,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(
                        if (hasPostNotificationPermission) "Izin Notifikasi Push Update: DIIZINKAN"
                        else "Izinkan Notifikasi Push Update"
                    )
                }
            }
        }
    }
}

@Composable
private fun GitHubUpdateAndBackupSection(
    settings: NotchSettings,
    backups: List<BackupSnapshot>,
    onUpdateSettings: ((NotchSettings) -> NotchSettings) -> Unit,
    onCheckUpdate: () -> Unit,
    onCreateBackup: () -> Unit,
    onRestoreBackup: (BackupSnapshot) -> Unit,
    onDeleteBackup: (BackupSnapshot) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.CloudDownload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Pembaruan GitHub & Cadangan Data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = settings.githubRepoOwner,
                    onValueChange = { v -> onUpdateSettings { it.copy(githubRepoOwner = v) } },
                    label = { Text("GitHub Owner") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = settings.githubRepoName,
                    onValueChange = { v -> onUpdateSettings { it.copy(githubRepoName = v) } },
                    label = { Text("Nama Repo") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Notifikasi Push Update", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Beri tahu saat versi baru tersedia di GitHub",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.updateNotificationsEnabled,
                    onCheckedChange = { on -> onUpdateSettings { it.copy(updateNotificationsEnabled = on) } }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Auto-Backup Sebelum Update", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Simpan JSON + hash SHA-256 otomatis sebelum unduhan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.autoBackupBeforeUpdate,
                    onCheckedChange = { on -> onUpdateSettings { it.copy(autoBackupBeforeUpdate = on) } }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCheckUpdate,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("settings_check_update_button")
                ) {
                    Text("Cek Update")
                }
                OutlinedButton(
                    onClick = onCreateBackup,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("settings_create_backup_button")
                ) {
                    Icon(Icons.Rounded.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cadangkan")
                }
            }

            if (backups.isNotEmpty()) {
                Text(
                    text = "Cadangan Lokal (${backups.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                backups.take(4).forEach { snap ->
                    val dateStr = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(Date(snap.createdAtMillis))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(snap.fileName, style = MaterialTheme.typography.labelMedium)
                                Text(
                                    "$dateStr • SHA256: ${snap.sha256Checksum.take(12)}...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { onRestoreBackup(snap) }) {
                                Icon(Icons.Rounded.Restore, contentDescription = "Pulihkan cadangan")
                            }
                            IconButton(onClick = { onDeleteBackup(snap) }) {
                                Icon(Icons.Rounded.DeleteOutline, contentDescription = "Hapus cadangan")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LabeledSlider(
    label: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    tag: String,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(valueText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
        Slider(
            value = value.coerceIn(range.start, range.endInclusive),
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
private fun ColorSwatchPicker(
    title: String,
    options: List<Pair<Long, String>>,
    selectedHex: Long,
    onSelect: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(options) { (hex, name) ->
                val isSelected = hex == selectedHex
                val color = Color(hex)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                            .clickable { onSelect(hex) }
                            .testTag("swatch_${hex.toString(16)}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = name,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = name.split(" ").first(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
