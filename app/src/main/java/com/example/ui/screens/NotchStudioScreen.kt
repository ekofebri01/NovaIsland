package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.SecurityUpdateGood
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.NotchNotification
import com.example.model.NotchSettings
import com.example.model.NotificationCategory
import com.example.ui.components.categoryIcon
import com.example.ui.theme.NeonEmerald

@Composable
fun NotchStudioScreen(
    settings: NotchSettings,
    activeNotification: NotchNotification?,
    notificationCount: Int,
    isOnline: Boolean,
    isNotificationAccessGranted: Boolean,
    onTriggerPreset: (NotificationCategory) -> Unit,
    onOpenDrawer: () -> Unit,
    onCheckUpdate: () -> Unit,
    onOpenSettingsTab: () -> Unit,
    onSendTestPushNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accent = Color(settings.accentHex)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 640.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 74.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeroNotchBanner(
                    accent = accent,
                    isOnline = isOnline,
                    activeNotification = activeNotification,
                    notificationCount = notificationCount,
                    settings = settings,
                    onOpenDrawer = onOpenDrawer,
                    onCheckUpdate = onCheckUpdate
                )
            }

            if (isWide) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1.2f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TriggerMatrixCard(
                                accent = accent,
                                columns = 4,
                                onTriggerPreset = onTriggerPreset
                            )
                        }
                        Column(
                            modifier = Modifier.weight(0.8f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SystemBridgeCard(
                                isNotificationAccessGranted = isNotificationAccessGranted,
                                onOpenSystemNotificationAccess = {
                                    runCatching {
                                        context.startActivity(
                                            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                                        )
                                    }
                                },
                                onSendTestPush = onSendTestPushNotification,
                                onOpenSettings = onOpenSettingsTab
                            )
                        }
                    }
                }
            } else {
                item {
                    TriggerMatrixCard(
                        accent = accent,
                        columns = 2,
                        onTriggerPreset = onTriggerPreset
                    )
                }
                item {
                    SystemBridgeCard(
                        isNotificationAccessGranted = isNotificationAccessGranted,
                        onOpenSystemNotificationAccess = {
                            runCatching {
                                context.startActivity(
                                    Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                                )
                            }
                        },
                        onSendTestPush = onSendTestPushNotification,
                        onOpenSettings = onOpenSettingsTab
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroNotchBanner(
    accent: Color,
    isOnline: Boolean,
    activeNotification: NotchNotification?,
    notificationCount: Int,
    settings: NotchSettings,
    onOpenDrawer: () -> Unit,
    onCheckUpdate: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = accent.copy(alpha = 0.35f),
                shape = RoundedCornerShape(28.dp)
            )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_cosmos),
                contentDescription = "Latar Kosmos NovaIsland",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF060913).copy(alpha = 0.58f),
                                Color(0xFF060913).copy(alpha = 0.88f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(20.dp),
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
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "NovaIsland Studio",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.14f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = if (isOnline) Icons.Rounded.CloudDone else Icons.Rounded.CloudOff,
                                contentDescription = if (isOnline) "Online" else "Offline",
                                tint = if (isOnline) NeonEmerald else accent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isOnline) "Online" else "Offline Ready",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                Text(
                    text = "Ketuk pil notch di atas untuk memperluas animasi iPhone 18, atau geser turun untuk membuka Glassmorphism Notification Drawer.",
                    color = Color.White.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodyMedium
                )

                // Telemetry chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TelemetryChip(
                        label = "Aktif",
                        value = activeNotification?.appName ?: "Siaga",
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryChip(
                        label = "Antrean",
                        value = "$notificationCount item",
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryChip(
                        label = "Durasi",
                        value = "${settings.durationSeconds} dtk",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onOpenDrawer,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent,
                            contentColor = Color(0xFF041018)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("studio_open_drawer_button")
                    ) {
                        Icon(Icons.Rounded.Layers, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Glass Drawer", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onCheckUpdate,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("studio_check_update_button")
                    ) {
                        Icon(
                            Icons.Rounded.SecurityUpdateGood,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Update GitHub", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.11f),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.65f),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TriggerMatrixCard(
    accent: Color,
    columns: Int,
    onTriggerPreset: (NotificationCategory) -> Unit
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
            Text(
                text = "Simulasi Notifikasi & Live Activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Uji animasi transisi Dynamic Notch untuk berbagai skenario secara instan (berfungsi offline).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val categories = listOf(
                NotificationCategory.MESSAGE to "Pesan Masuk",
                NotificationCategory.MUSIC to "Musik Live",
                NotificationCategory.TIMER to "Timer Fokus",
                NotificationCategory.DELIVERY to "Lacak Kurir",
                NotificationCategory.CALL to "Panggilan HD",
                NotificationCategory.BATTERY to "Pengisian Daya",
                NotificationCategory.UPDATE to "Notif Update",
                NotificationCategory.SYSTEM to "Mode Fokus"
            )

            val rows = categories.chunked(columns)
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { (category, label) ->
                        Surface(
                            onClick = { onTriggerPreset(category) },
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .testTag("trigger_${category.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(accent.copy(alpha = 0.16f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = categoryIcon(category),
                                        contentDescription = label,
                                        tint = accent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    if (rowItems.size < columns) {
                        repeat(columns - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemBridgeCard(
    isNotificationAccessGranted: Boolean,
    onOpenSystemNotificationAccess: () -> Unit,
    onSendTestPush: () -> Unit,
    onOpenSettings: () -> Unit
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.NotificationsActive,
                    contentDescription = null,
                    tint = if (isNotificationAccessGranted) NeonEmerald else MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = "Bridge Notifikasi Perangkat",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isNotificationAccessGranted) {
                            "Aktif — Menangkap notifikasi sistem secara real-time"
                        } else {
                            "Belum aktif — Izinkan Akses Notifikasi agar notifikasi app lain masuk ke Notch"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenSystemNotificationAccess,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("studio_notification_access_button")
                ) {
                    Text(if (isNotificationAccessGranted) "Status Izin" else "Aktifkan Akses")
                }
                Button(
                    onClick = onSendTestPush,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("studio_test_push_button")
                ) {
                    Icon(Icons.Rounded.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Push Uji")
                }
            }

            OutlinedButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("studio_customize_shortcut")
            ) {
                Icon(Icons.Rounded.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Atur Posisi, Ukuran & Warna Notch")
            }
        }
    }
}
