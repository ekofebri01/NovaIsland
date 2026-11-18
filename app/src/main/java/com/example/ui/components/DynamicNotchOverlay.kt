package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.MarkChatUnread
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NotchNotification
import com.example.model.NotchSettings
import com.example.model.NotchVisualState
import com.example.model.NotificationCategory
import kotlin.math.roundToInt

@Composable
fun DynamicNotchOverlay(
    notification: NotchNotification?,
    visualState: NotchVisualState,
    settings: NotchSettings,
    onNotchTap: () -> Unit,
    onCollapse: () -> Unit,
    onOpenDrawer: () -> Unit,
    onPrimaryAction: (NotchNotification) -> Unit,
    onDismissNotification: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(notification?.accentHex ?: settings.accentHex)
    val notchBgColor = Color(settings.notchBgHex)

    val springSpec = spring<androidx.compose.ui.unit.Dp>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = settings.yOffsetDp.dp, start = 12.dp, end = 12.dp)
            .pointerInput(Unit) {
                var dragAccumulated = 0f
                detectVerticalDragGestures(
                    onDragStart = { dragAccumulated = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        dragAccumulated += dragAmount
                        if (dragAccumulated > 54f) {
                            dragAccumulated = 0f
                            onOpenDrawer()
                        }
                    }
                )
            }
    ) {
        val maxAvailWidth = maxWidth
        val targetWidth = when (visualState) {
            NotchVisualState.COMPACT -> (132.dp * settings.widthScale).coerceIn(104.dp, maxAvailWidth)
            NotchVisualState.ACTIVE_PILL -> (258.dp * settings.widthScale).coerceIn(190.dp, maxAvailWidth)
            NotchVisualState.EXPANDED -> (356.dp * settings.widthScale).coerceIn(260.dp, maxAvailWidth)
        }

        val targetHeight = when (visualState) {
            NotchVisualState.COMPACT -> (34.dp * settings.heightScale).coerceIn(28.dp, 54.dp)
            NotchVisualState.ACTIVE_PILL -> (42.dp * settings.heightScale).coerceIn(34.dp, 64.dp)
            NotchVisualState.EXPANDED -> (162.dp * settings.heightScale).coerceIn(136.dp, 230.dp)
        }

        val targetCorner = when (visualState) {
            NotchVisualState.COMPACT, NotchVisualState.ACTIVE_PILL -> settings.cornerRadiusDp.dp
            NotchVisualState.EXPANDED -> (settings.cornerRadiusDp + 8f).coerceAtMost(36f).dp
        }

        val animatedWidth by animateDpAsState(targetValue = targetWidth, animationSpec = springSpec, label = "notchWidth")
        val animatedHeight by animateDpAsState(targetValue = targetHeight, animationSpec = springSpec, label = "notchHeight")
        val animatedCorner by animateDpAsState(targetValue = targetCorner, animationSpec = springSpec, label = "notchCorner")

        val alignment = BiasAlignment(
            horizontalBias = settings.xOffsetFraction.coerceIn(-1f, 1f),
            verticalBias = -1f
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = alignment
        ) {
            val shape = RoundedCornerShape(animatedCorner)
            Surface(
                modifier = Modifier
                    .width(animatedWidth)
                    .height(animatedHeight)
                    .shadow(
                        elevation = if (visualState == NotchVisualState.EXPANDED) 22.dp else 12.dp,
                        shape = shape,
                        ambientColor = accentColor.copy(alpha = 0.45f),
                        spotColor = accentColor.copy(alpha = 0.55f)
                    )
                    .clip(shape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                notchBgColor,
                                notchBgColor.copy(alpha = 0.94f),
                                accentColor.copy(alpha = if (visualState == NotchVisualState.EXPANDED) 0.14f else 0.08f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.24f),
                                accentColor.copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.08f)
                            )
                        ),
                        shape = shape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onNotchTap
                    )
                    .testTag("dynamic_notch_pill"),
                color = Color.Transparent,
                shape = shape
            ) {
                AnimatedContent(
                    targetState = visualState,
                    transitionSpec = {
                        fadeIn(tween(200)) togetherWith fadeOut(tween(140))
                    },
                    label = "notchContent"
                ) { state ->
                    when (state) {
                        NotchVisualState.COMPACT -> CompactNotchContent(
                            notification = notification,
                            accentColor = accentColor
                        )
                        NotchVisualState.ACTIVE_PILL -> ActivePillContent(
                            notification = notification,
                            accentColor = accentColor
                        )
                        NotchVisualState.EXPANDED -> ExpandedNotchContent(
                            notification = notification,
                            accentColor = accentColor,
                            onCollapse = onCollapse,
                            onOpenDrawer = onOpenDrawer,
                            onPrimaryAction = onPrimaryAction,
                            onDismiss = onDismissNotification
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactNotchContent(
    notification: NotchNotification?,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Simulated iPhone 18 Front Camera / FaceID Lens Ring
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(Color(0xFF111827))
                .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E3A8A))
            )
        }

        if (notification != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (notification.category == NotificationCategory.MUSIC && notification.isPlaying) {
                    MiniEqualizerBars(color = accentColor)
                } else {
                    PulsingDot(color = accentColor)
                }
                Icon(
                    imageVector = categoryIcon(notification.category),
                    contentDescription = notification.category.label,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        } else {
            PulsingDot(color = accentColor.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun ActivePillContent(
    notification: NotchNotification?,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon(notification?.category ?: NotificationCategory.SYSTEM),
                    contentDescription = notification?.appName ?: "NovaIsland",
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
            }

            Text(
                text = notification?.title ?: "NovaIsland Siap",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (notification?.category == NotificationCategory.MUSIC && notification.isPlaying) {
            MiniEqualizerBars(color = accentColor)
        } else if (notification?.progressFraction != null) {
            Text(
                text = "${(notification.progressFraction * 100).roundToInt()}%",
                color = accentColor,
                style = MaterialTheme.typography.labelSmall
            )
        } else {
            PulsingDot(color = accentColor)
        }
    }
}

@Composable
private fun ExpandedNotchContent(
    notification: NotchNotification?,
    accentColor: Color,
    onCollapse: () -> Unit,
    onOpenDrawer: () -> Unit,
    onPrimaryAction: (NotchNotification) -> Unit,
    onDismiss: (String) -> Unit
) {
    if (notification == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tidak ada notifikasi aktif",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.22f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon(notification.category),
                    contentDescription = notification.appName,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = notification.appName.uppercase(),
                        color = accentColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp
                    )
                    if (notification.isLiveActivity) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(accentColor.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "LIVE",
                                color = accentColor,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
                Text(
                    text = notification.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = notification.subtitle,
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onCollapse,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("notch_collapse_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Perkecil notch",
                    tint = Color.White.copy(alpha = 0.75f)
                )
            }
        }

        AnimatedVisibility(visible = notification.progressFraction != null) {
            notification.progressFraction?.let { progress ->
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = accentColor,
                    trackColor = Color.White.copy(alpha = 0.14f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val actionLabel = notification.primaryActionLabel ?: "Aksi"
            Surface(
                onClick = { onPrimaryAction(notification) },
                shape = RoundedCornerShape(14.dp),
                color = accentColor.copy(alpha = 0.22f),
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .testTag("notch_primary_action")
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (notification.category == NotificationCategory.MUSIC) {
                        Icon(
                            imageVector = if (notification.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = actionLabel,
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = actionLabel,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }

            Surface(
                onClick = onOpenDrawer,
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.12f),
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .testTag("notch_open_drawer_action")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Glass Drawer",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                }
            }

            Surface(
                onClick = { onDismiss(notification.id) },
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.1f),
                modifier = Modifier
                    .size(36.dp)
                    .testTag("notch_dismiss_action")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Hapus notifikasi",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MiniEqualizerBars(color: Color) {
    val transition = rememberInfiniteTransition(label = "eq")
    val bar1 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b1"
    )
    val bar2 by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(tween(340, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b2"
    )
    val bar3 by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(14.dp)
    ) {
        listOf(bar1, bar2, bar3).forEach { level ->
            Box(
                modifier = Modifier
                    .width(2.5.dp)
                    .height((14f * level).dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
private fun PulsingDot(color: Color) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulseAlpha"
    )
    Canvas(modifier = Modifier.size(8.dp)) {
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = size.minDimension / 2f,
            center = Offset(size.width / 2f, size.height / 2f)
        )
    }
}

fun categoryIcon(category: NotificationCategory): ImageVector = when (category) {
    NotificationCategory.MESSAGE -> Icons.Rounded.MarkChatUnread
    NotificationCategory.MUSIC -> Icons.Rounded.MusicNote
    NotificationCategory.TIMER -> Icons.Rounded.Timer
    NotificationCategory.CALL -> Icons.Rounded.Call
    NotificationCategory.DELIVERY -> Icons.Rounded.LocalShipping
    NotificationCategory.BATTERY -> Icons.Rounded.BatteryChargingFull
    NotificationCategory.UPDATE -> Icons.Rounded.SystemUpdate
    NotificationCategory.SYSTEM -> Icons.Rounded.Tune
}
