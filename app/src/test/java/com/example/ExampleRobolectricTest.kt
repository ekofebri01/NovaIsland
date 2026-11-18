package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BackupManager
import com.example.data.OfflineDocsRepository
import com.example.model.NotchSettings
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("NovaIsland", appName)
    }

    @Test
    fun `backup manager creates and restores json with sha256`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val backupManager = BackupManager(context)
        val settings = NotchSettings(durationSeconds = 9, widthScale = 1.2f)
        val notifications = OfflineDocsRepository.starterNotifications()

        val snapshot = backupManager.createBackup(settings, notifications)
        assertEquals(64, snapshot.sha256Checksum.length)
        assertEquals(notifications.size, snapshot.notificationCount)

        val restored = backupManager.restoreBackup(snapshot.fileName)
        assertNotNull(restored)
        assertEquals(9, restored?.settings?.durationSeconds)
        assertTrue(backupManager.deleteBackup(snapshot.fileName))
    }
}
