package com.example.data

import android.content.Context
import com.example.model.BackupSnapshot
import com.example.model.NotchNotification
import com.example.model.NotchSettings
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class BackupManager(private val context: Context) {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val snapshotAdapter = moshi.adapter(BackupSnapshot::class.java)

    private val backupDir: File
        get() = File(context.filesDir, "backups").apply { if (!exists()) mkdirs() }

    suspend fun createBackup(
        settings: NotchSettings,
        notifications: List<NotchNotification>
    ): BackupSnapshot = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date(now))
        val fileName = "novaisland_backup_$stamp.json"
        val id = UUID.randomUUID().toString().take(8)

        val rawPayload = BackupSnapshot(
            id = id,
            createdAtMillis = now,
            appVersionName = OfflineDocsRepository.CURRENT_VERSION_NAME,
            appVersionCode = OfflineDocsRepository.CURRENT_VERSION_CODE,
            notificationCount = notifications.size,
            sha256Checksum = "",
            fileName = fileName,
            settings = settings,
            notifications = notifications
        )
        val payloadWithoutHash = snapshotAdapter.toJson(rawPayload)
        val digest = sha256Hex(payloadWithoutHash.toByteArray(Charsets.UTF_8))

        val finalized = rawPayload.copy(sha256Checksum = digest)
        val json = snapshotAdapter.toJson(finalized)
        File(backupDir, fileName).writeText(json, Charsets.UTF_8)
        finalized
    }

    suspend fun listBackups(): List<BackupSnapshot> = withContext(Dispatchers.IO) {
        backupDir.listFiles { file -> file.extension == "json" }
            ?.sortedByDescending { it.lastModified() }
            ?.mapNotNull { file ->
                runCatching { snapshotAdapter.fromJson(file.readText(Charsets.UTF_8)) }.getOrNull()
            }
            ?: emptyList()
    }

    suspend fun restoreBackup(fileName: String): BackupSnapshot? = withContext(Dispatchers.IO) {
        val file = File(backupDir, fileName)
        if (!file.exists()) return@withContext null
        runCatching { snapshotAdapter.fromJson(file.readText(Charsets.UTF_8)) }.getOrNull()
    }

    suspend fun deleteBackup(fileName: String): Boolean = withContext(Dispatchers.IO) {
        val file = File(backupDir, fileName)
        if (file.exists()) file.delete() else false
    }

    companion object {
        fun sha256Hex(bytes: ByteArray): String {
            val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
            return digest.joinToString("") { "%02x".format(it) }
        }
    }
}
