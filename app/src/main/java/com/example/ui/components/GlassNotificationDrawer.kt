package com.example.ui.components

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.DoNotDisturbOn
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DrawerQuickToggle
import com.example.model.NotchNotification
import com.example.model.NotchSettings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GlassNotificationDrawer(
    visible: Boolean,
    notifications: List<NotchNotification>,
    quickToggles: List<DrawerQuickToggle>,
    settings: NotchSettings,
    isOnline: Boolean,
    onToggleQuickControl: (String) -> Unit,
    onSelectNotification: (NotchNotification) -> Unit,
    onDismissNotification: (String) -> Unit,
    onClearAll: () -> Unit,
    onClose: () -> Unit
) {
    if (visible) {
        BackHandler(onBack = onClose)
    }

    val drawerTint = Color(settings.drawerTintHex)
    val accentColor = Color(settings.accentHex)

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it / 4 },
            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)
        ) + fadeIn() + scaleIn(initialScale = 0.96f),
        exit = slideOutVertically(
            targetOffsetY = { -it / 4 }
        ) + fadeOut() + scaleOut(targetScale = 0.96f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    var dragUp = 0f
                    detectVerticalDragGestures(
                        onDragStart = { dragUp = 0f },
                        onVerticalDrag = { _, dragAmount ->
                            dragUp += dragAmount
                            if (dragUp < -90f) {
                                dragUp = 0f
                                onClose()
                            }
                        }
                    )
                }
        ) {
            // Blurred ambient frosted wallpaper layer (API 31+ RenderEffect blur + translucent glass gradient)
            val blurModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Modifier.blur(settings.glassBlurRadiusDp.dp)
            } else {
                Modifier
            }

            Image(
                painter = painterResource(id = R.drawable.img_hero_cosmos),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .then(blurModifier)
                    .alpha(0.75f)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                drawerTint.copy(alpha = 0.84f),
                                Color(0xFF060913).copy(alpha = 0.88f),
                                drawerTint.copy(alpha = 0.78f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header Clock + Close
                DrawerHeaderBar(
                    accentColor = accentColor,
                    isOnline = isOnline,
                    notificationCount = notifications.size,
                    onClearAll = onClearAll,
                    onClose = onClose
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Control Center Quick Glass Pills
                QuickTogglesStrip(
                    toggles = quickToggles,
                    accentColor = accentColor,
                    onToggle = onToggleQuickControl
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Responsive Notification Feed (1 col on portrait, 2 cols on landscape/wide)
                BoxWithConstraints(modifier = Modifier.weight(1f)) {
                    val isWide = maxWidth >= 600.dp
                    if (notifications.isEmpty()) {
                        EmptyGlassNotificationsState(
                            accentColor = accentColor,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (isWide) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(notifications, key = { it.id }) { item ->
                                GlassNotificationCard(
                                    notification = item,
                                    onTap = { onSelectNotification(item) },
                                    onDismiss = { onDismissNotification(item.id) }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(notifications, key = { it.id }) { item ->
                                GlassNotificationCard(
                                    notification = item,
                                    onTap = { onSelectNotification(item) },
                                    onDismiss = { onDismissNotification(item.id) }
                                )
                            }
                        }
                    }
                }

                // Bottom iPhone Home / Close Pill Handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        onClick = onClose,
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier
                            .width(124.dp)
                            .height(6.dp)
                            .testTag("drawer_bottom_handle")
                    ) {}
                }
            }
        }
    }
}

@Composable
private fun DrawerHeaderBar(
    accentColor: Color,
    isOnline: Boolean,
    notificationCount: Int,
    onClearAll: () -> Unit,
    onClose: () -> Unit
) {
    val timeText = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }
    val dateText = remember {
        SimpleDateFormat("EEEE, d MMMM", Locale("id", "ID")).format(Date())
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = timeText,
                color = Color.White,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 38.sp),
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = dateText,
                    color = Color.White.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.22f))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isOnline) "ONLINE" else "OFFLINE",
                        color = accentColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (notificationCount > 0) {
                Surface(
                    onClick = onClearAll,
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.14f),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("drawer_clear_all_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteSweep,
                            contentDescription = "Hapus semua notifikasi",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Bersihkan",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Surface(
                onClick = onClose,
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.16f),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("drawer_close_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Tutup Glass Drawer",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickTogglesStrip(
    toggles: List<DrawerQuickToggle>,
    accentColor: Color,
    onToggle: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        toggles.take(6).forEach { toggle ->
            val icon = when (toggle.id) {
                "wifi" -> Icons.Rounded.Wifi
                "bt" -> Icons.Rounded.Bluetooth
                "focus" -> Icons.Rounded.CenterFocusStrong
                "dnd" -> Icons.Rounded.DoNotDisturbOn
                "sync" -> Icons.Rounded.Sync
                else -> Icons.Rounded.Vibration
            }

            Surface(
                onClick = { onToggle(toggle.id) },
                shape = RoundedCornerShape(18.dp),
                color = if (toggle.isActive) accentColor.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.10f),
                modifier = Modifier
                    .weight(1f)
                    .height(58.dp)
                    .border(
                        width = 1.dp,
                        color = if (toggle.isActive) accentColor.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .testTag("drawer_toggle_${toggle.id}")
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = toggle.label,
                        tint = if (toggle.isActive) accentColor else Color.White.copy(alpha = 0.72f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = toggle.label.split(" ").first(),
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassNotificationCard(
    notification: NotchNotification,
    onTap: () -> Unit,
    onDismiss: () -> Unit
) {
    val accent = Color(notification.accentHex)
    var isExpanded by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart || value == SwipeToDismissBoxValue.StartToEnd) {
                onDismiss()
                true
            } else {
                false
            }
        }
    )

    val timeLabel = remember(notification.timestampMillis) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(notification.timestampMillis))
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {},
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White.copy(alpha = 0.13f),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.34f),
                            accent.copy(alpha = 0.28f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .clip(RoundedCornerShape(24.dp))
                .clickable { isExpanded = !isExpanded }
                .testTag("glass_card_${notification.id}")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(accent.copy(alpha = 0.24f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryIcon(notification.category),
                            contentDescription = notification.appName,
                            tint = accent,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = notification.appName,
                                color = accent,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = timeLabel,
                                color = Color.White.copy(alpha = 0.65f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Text(
                            text = notification.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = if (isExpanded) 3 else 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = notification.subtitle,
                            color = Color.White.copy(alpha = 0.78f),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = if (isExpanded) 5 else 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                AnimatedVisibility(visible = isExpanded) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = onTap,
                            shape = RoundedCornerShape(14.dp),
                            color = accent.copy(alpha = 0.28f),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Tampilkan di Notch",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        Surface(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.14f),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Hapus",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyGlassNotificationsState(
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.18f))
                .border(1.dp, accentColor.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.NotificationsOff,
                contentDescription = "Tidak ada notifikasi",
                tint = accentColor,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Glass Drawer Bersih",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Picu notifikasi di Studio atau aktifkan Akses Notifikasi agar notifikasi baru muncul dengan efek kaca buram.",
            color = Color.White.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
