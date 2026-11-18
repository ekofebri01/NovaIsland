package com.example.service

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.model.NotchNotification
import com.example.model.NotificationCategory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

object LiveNotificationBridge {
    private val _incomingNotifications = MutableSharedFlow<NotchNotification>(extraBufferCapacity = 32)
    val incomingNotifications = _incomingNotifications.asSharedFlow()

    private val _isListenerConnected = MutableStateFlow(false)
    val isListenerConnected = _isListenerConnected.asStateFlow()

    fun emitSystemNotification(notification: NotchNotification) {
        _incomingNotifications.tryEmit(notification)
    }

    fun setConnected(connected: Boolean) {
        _isListenerConnected.value = connected
    }

    fun isNotificationAccessEnabled(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        ) ?: return false
        val componentName = ComponentName(context, NovaNotificationListenerService::class.java)
        return enabledListeners.contains(componentName.flattenToString()) ||
            enabledListeners.contains(componentName.flattenToShortString())
    }
}

class NovaNotificationListenerService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        LiveNotificationBridge.setConnected(true)
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        LiveNotificationBridge.setConnected(false)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification = sbn?.notification ?: return
        if (sbn.packageName == packageName) return

        val extras = notification.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim()
            ?: return
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.trim()
            ?: extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()?.trim()
            ?: "Notifikasi masuk"

        val category = when (notification.category) {
            Notification.CATEGORY_MESSAGE, Notification.CATEGORY_SOCIAL -> NotificationCategory.MESSAGE
            Notification.CATEGORY_TRANSPORT -> NotificationCategory.MUSIC
            Notification.CATEGORY_CALL -> NotificationCategory.CALL
            Notification.CATEGORY_ALARM, Notification.CATEGORY_REMINDER -> NotificationCategory.TIMER
            else -> NotificationCategory.SYSTEM
        }

        val appLabel = runCatching {
            val appInfo = packageManager.getApplicationInfo(sbn.packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        }.getOrDefault(sbn.packageName.substringAfterLast("."))

        val mapped = NotchNotification(
            id = "sys_${sbn.key}_${sbn.postTime}",
            title = title,
            subtitle = text,
            appName = appLabel,
            category = category,
            timestampMillis = sbn.postTime,
            accentHex = when (category) {
                NotificationCategory.MESSAGE -> 0xFF10B981
                NotificationCategory.MUSIC -> 0xFF8B5CF6
                NotificationCategory.CALL -> 0xFF38BDF8
                NotificationCategory.TIMER -> 0xFFF59E0B
                else -> 0xFF22D3EE
            },
            isLiveActivity = category == NotificationCategory.MUSIC || category == NotificationCategory.CALL,
            isFromSystemListener = true,
            primaryActionLabel = "Buka",
            secondaryActionLabel = "Tutup"
        )

        LiveNotificationBridge.emitSystemNotification(mapped)
    }
}
