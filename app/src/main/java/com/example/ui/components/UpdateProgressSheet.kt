package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.UpdatePhase
import com.example.model.UpdateReleaseInfo
import com.example.ui.theme.NeonEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProgressSheet(
    visible: Boolean,
    phase: UpdatePhase,
    repoLabel: String,
    onRetryCheck: () -> Unit,
    onStartUpdate: (UpdateReleaseInfo) -> Unit,
    onRunLocalVerificationPreview: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CloudDownload,
                        contentDescription = "Pembaruan GitHub",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(
                        text = "GitHub In-App Update",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Repositori: $repoLabel",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            when (phase) {
                is UpdatePhase.Idle -> {
                    Text(
                        text = "Tekan tombol di bawah untuk memeriksa rilis terbaru dari GitHub Releases.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = onRetryCheck,
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("sheet_check_update_button")
                    ) {
                        Text("Periksa Sekarang")
                    }
                }

                is UpdatePhase.Checking -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Menghubungi GitHub Releases API...", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                is UpdatePhase.UpToDate -> {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = NeonEmerald.copy(alpha = 0.14f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Sudah terbaru",
                                tint = NeonEmerald
                            )
                            Column {
                                Text(
                                    text = "Anda Menggunakan Versi Terbaru (v${phase.currentVersion})",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = if (phase.isOfflineMode) {
                                        "Mode Offline aktif — menggunakan status versi lokal."
                                    } else {
                                        "Tidak ada rilis baru di repositori GitHub Anda."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = onRunLocalVerificationPreview,
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("preview_ota_pipeline_button")
                    ) {
                        Text("Uji Alur Auto-Backup & Verifikasi SHA-256")
                    }
                }

                is UpdatePhase.Available -> {
                    ReleaseSummaryCard(release = phase.release)
                    Button(
                        onClick = { onStartUpdate(phase.release) },
                        modifier = Modifier.fillMaxWidth().height(52.dp).testTag("start_ota_download_button")
                    ) {
                        Text("Cadangkan & Unduh Rilis (${phase.release.tagName})")
                    }
                }

                is UpdatePhase.BackingUp -> {
                    PipelineStepRow(
                        title = "1. Mencadangkan Data Otomatis...",
                        subtitle = "Menyimpan pengaturan notch & riwayat notifikasi ke JSON lokal",
                        progress = null,
                        icon = Icons.Rounded.Backup
                    )
                }

                is UpdatePhase.Downloading -> {
                    val animatedProgress by animateFloatAsState(
                        targetValue = phase.progress,
                        animationSpec = tween(180),
                        label = "dlProg"
                    )
                    PipelineStepRow(
                        title = "2. Mengunduh Paket Rilis ${phase.release.tagName}",
                        subtitle = "${phase.downloadedBytes / 1024} KB / ${(phase.totalBytes / 1024).coerceAtLeast(1)} KB (${(animatedProgress * 100).toInt()}%)",
                        progress = animatedProgress,
                        icon = Icons.Rounded.CloudDownload
                    )
                }

                is UpdatePhase.VerifyingHash -> {
                    PipelineStepRow(
                        title = "3. Memverifikasi Integritas SHA-256...",
                        subtitle = "Menghitung digest kriptografi paket rilis",
                        progress = phase.progress,
                        icon = Icons.Rounded.Security
                    )
                }

                is UpdatePhase.VerifiedReady -> {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = NeonEmerald.copy(alpha = 0.14f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NeonEmerald.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.VerifiedUser,
                                    contentDescription = "SHA-256 Terverifikasi",
                                    tint = NeonEmerald
                                )
                                Text(
                                    text = "Integritas Paket Terverifikasi",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (phase.backupFileName != null) {
                                Text(
                                    text = "Auto-Backup: ${phase.backupFileName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "SHA-256 Terhitung:\n${phase.computedSha256}",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonEmerald
                            )
                        }
                    }

                    ReleaseSummaryCard(release = phase.release)

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(phase.release.releaseUrl))
                            runCatching { context.startActivity(intent) }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp).testTag("open_verified_release_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buka Halaman Rilis GitHub Terverifikasi")
                    }
                }

                is UpdatePhase.Error -> {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ErrorOutline,
                                contentDescription = "Kesalahan",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = phase.message,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRetryCheck,
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Coba Lagi")
                        }
                        Button(
                            onClick = onRunLocalVerificationPreview,
                            modifier = Modifier.weight(1f).height(48.dp).testTag("fallback_verify_button")
                        ) {
                            Text("Uji Verifikasi")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReleaseSummaryCard(release: UpdateReleaseInfo) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Versi ${release.tagName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = release.publishedAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = release.releaseNotes,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Expected SHA-256: ${release.expectedSha256.take(24)}...",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PipelineStepRow(
    title: String,
    subtitle: String,
    progress: Float?,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (progress != null) {
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                )
            } else {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}
