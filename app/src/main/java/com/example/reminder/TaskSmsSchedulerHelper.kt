package com.example.reminder

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.data.AttendanceDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data model for a scheduled SMS task
 */
data class TaskScheduledSmsItem(
    val id: Long = System.currentTimeMillis(),
    val taskId: Long,
    val taskTitle: String,
    val staffName: String = "",
    val recipientPhone: String,
    val recipientName: String,
    val customMessage: String,
    val scheduledTimestamp: Long,
    val autoSend: Boolean = true,
    val status: String = "SCHEDULED", // "SCHEDULED", "SENT", "CANCELLED", "FAILED"
    val createdAt: Long = System.currentTimeMillis(),
    val lastError: String = ""
)

object TaskSmsSchedulerHelper {
    const val CHANNEL_ID = "task_scheduled_sms_channel"
    const val CHANNEL_NAME = "Scheduled Task SMS Alerts"
    const val CHANNEL_DESCRIPTION = "Alerts and delivery confirmations for scheduled task SMS messages"

    const val ACTION_SCHEDULED_TASK_SMS = "com.example.reminder.ACTION_SCHEDULED_TASK_SMS"
    const val ACTION_MARK_TASK_DONE = "com.example.reminder.ACTION_SCHEDULED_MARK_TASK_DONE"
    const val ACTION_DISPATCH_SMS_NOW = "com.example.reminder.ACTION_DISPATCH_SMS_NOW"

    const val EXTRA_SMS_ID = "extra_sms_id"
    const val EXTRA_TASK_ID = "extra_task_id"

    private const val PREFS_NAME = "scheduled_task_sms_prefs"
    private const val KEY_SCHEDULED_LIST = "scheduled_sms_items_json"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * Retrieve all scheduled SMS items from storage
     */
    fun getAllScheduledSms(context: Context): List<TaskScheduledSmsItem> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_SCHEDULED_LIST, null) ?: return emptyList()
        return try {
            val arr = JSONArray(jsonStr)
            val list = mutableListOf<TaskScheduledSmsItem>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    TaskScheduledSmsItem(
                        id = obj.optLong("id", 0L),
                        taskId = obj.optLong("taskId", 0L),
                        taskTitle = obj.optString("taskTitle", ""),
                        staffName = obj.optString("staffName", ""),
                        recipientPhone = obj.optString("recipientPhone", ""),
                        recipientName = obj.optString("recipientName", ""),
                        customMessage = obj.optString("customMessage", ""),
                        scheduledTimestamp = obj.optLong("scheduledTimestamp", 0L),
                        autoSend = obj.optBoolean("autoSend", true),
                        status = obj.optString("status", "SCHEDULED"),
                        createdAt = obj.optLong("createdAt", 0L),
                        lastError = obj.optString("lastError", "")
                    )
                )
            }
            list.sortedByDescending { it.scheduledTimestamp }
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Save all scheduled SMS items to storage
     */
    @Synchronized
    private fun saveAllScheduledSms(context: Context, list: List<TaskScheduledSmsItem>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val arr = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("taskId", item.taskId)
                put("taskTitle", item.taskTitle)
                put("staffName", item.staffName)
                put("recipientPhone", item.recipientPhone)
                put("recipientName", item.recipientName)
                put("customMessage", item.customMessage)
                put("scheduledTimestamp", item.scheduledTimestamp)
                put("autoSend", item.autoSend)
                put("status", item.status)
                put("createdAt", item.createdAt)
                put("lastError", item.lastError)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_SCHEDULED_LIST, arr.toString()).apply()
    }

    /**
     * Get active scheduled SMS for a specific task
     */
    fun getScheduledSmsForTask(context: Context, taskId: Long): TaskScheduledSmsItem? {
        val list = getAllScheduledSms(context)
        return list.firstOrNull { it.taskId == taskId && it.status == "SCHEDULED" && it.scheduledTimestamp > System.currentTimeMillis() }
    }

    /**
     * Schedule a new SMS dispatch for a task
     */
    fun scheduleTaskSms(context: Context, item: TaskScheduledSmsItem): Boolean {
        createNotificationChannel(context)

        // Cancel any existing schedule for this task
        val currentList = getAllScheduledSms(context).filterNot {
            it.taskId == item.taskId && it.status == "SCHEDULED"
        }.toMutableList()
        currentList.add(item)
        saveAllScheduledSms(context, currentList)

        // Set AlarmManager exact wakeup
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return false
        val intent = Intent(context, TaskScheduledSmsReceiver::class.java).apply {
            action = ACTION_SCHEDULED_TASK_SMS
            putExtra(EXTRA_SMS_ID, item.id)
            putExtra(EXTRA_TASK_ID, item.taskId)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (item.id % 1000000).toInt(),
            intent,
            flags
        )

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    item.scheduledTimestamp,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    item.scheduledTimestamp,
                    pendingIntent
                )
            }
            true
        } catch (e: SecurityException) {
            try {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    item.scheduledTimestamp,
                    pendingIntent
                )
                true
            } catch (_: Exception) {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Cancel an active scheduled SMS
     */
    fun cancelScheduledSms(context: Context, smsId: Long) {
        val list = getAllScheduledSms(context).toMutableList()
        val index = list.indexOfFirst { it.id == smsId }
        if (index != -1) {
            list[index] = list[index].copy(status = "CANCELLED")
            saveAllScheduledSms(context, list)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, TaskScheduledSmsReceiver::class.java).apply {
            action = ACTION_SCHEDULED_TASK_SMS
            putExtra(EXTRA_SMS_ID, smsId)
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (smsId % 1000000).toInt(),
            intent,
            flags
        )
        try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        } catch (_: Exception) { }
    }

    /**
     * Cancel active scheduled SMS by task ID
     */
    fun cancelScheduledSmsForTask(context: Context, taskId: Long) {
        val item = getScheduledSmsForTask(context, taskId)
        if (item != null) {
            cancelScheduledSms(context, item.id)
        }
    }

    /**
     * Reschedule all pending alarms after device reboot
     */
    fun rescheduleAllPending(context: Context) {
        val now = System.currentTimeMillis()
        val list = getAllScheduledSms(context)
        for (item in list) {
            if (item.status == "SCHEDULED" && item.scheduledTimestamp > now) {
                scheduleTaskSms(context, item)
            }
        }
    }

    /**
     * Direct SMS dispatch using SmsManager (if permitted) or fallback Intent
     */
    fun sendSmsImmediately(context: Context, phoneNumber: String, message: String): Boolean {
        val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "").trim()
        if (cleanNumber.isBlank()) return false

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            return try {
                val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.getSystemService(SmsManager::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }
                val parts = smsManager.divideMessage(message)
                if (parts.size > 1) {
                    smsManager.sendMultipartTextMessage(cleanNumber, null, parts, null, null)
                } else {
                    smsManager.sendTextMessage(cleanNumber, null, message, null, null)
                }
                true
            } catch (_: Exception) {
                openSmsComposer(context, cleanNumber, message)
                true
            }
        } else {
            openSmsComposer(context, cleanNumber, message)
            return true
        }
    }

    fun openSmsComposer(context: Context, phoneNumber: String, message: String) {
        try {
            val uri = if (phoneNumber.isNotBlank()) Uri.parse("smsto:$phoneNumber") else Uri.parse("smsto:")
            val smsIntent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(smsIntent)
        } catch (_: Exception) { }
    }

    /**
     * Executes when the alarm fires: sends SMS or triggers alert notification
     */
    fun executeScheduledSms(context: Context, smsId: Long) {
        createNotificationChannel(context)

        val list = getAllScheduledSms(context).toMutableList()
        val index = list.indexOfFirst { it.id == smsId }
        if (index == -1) return
        val item = list[index]

        val cleanNumber = item.recipientPhone.replace(Regex("[^0-9+]"), "").trim()
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

        var didSendAutomatically = false
        var failureReason = ""

        if (item.autoSend && hasPermission && cleanNumber.isNotBlank()) {
            try {
                val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.getSystemService(SmsManager::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }
                val parts = smsManager.divideMessage(item.customMessage)
                if (parts.size > 1) {
                    smsManager.sendMultipartTextMessage(cleanNumber, null, parts, null, null)
                } else {
                    smsManager.sendTextMessage(cleanNumber, null, item.customMessage, null, null)
                }
                didSendAutomatically = true
            } catch (e: Exception) {
                failureReason = e.localizedMessage ?: "Failed to dispatch via SMS service"
            }
        }

        if (didSendAutomatically) {
            // Update status to SENT
            list[index] = item.copy(status = "SENT")
            saveAllScheduledSms(context, list)

            // Show delivery confirmation notification
            showDeliveryConfirmationNotification(context, item)
        } else {
            // Either autoSend was false, or permission was missing, or dispatch had an error
            list[index] = item.copy(
                status = if (item.autoSend) "FAILED" else "READY",
                lastError = failureReason
            )
            saveAllScheduledSms(context, list)

            // Show actionable prompt notification so user can tap to send immediately
            showReadyToSendNotification(context, item, failureReason)
        }
    }

    private fun showDeliveryConfirmationNotification(context: Context, item: TaskScheduledSmsItem) {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("extra_task_id", item.taskId)
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            (item.id % 1000000).toInt(),
            openAppIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notifId = ((item.id % 900000) + 100000).toInt()

        val recipientDisplay = if (item.recipientName.isNotBlank()) "${item.recipientName} (${item.recipientPhone})" else item.recipientPhone

        val bigText = buildString {
            append("✅ SMS sent to: $recipientDisplay\n")
            append("📌 Task: ${item.taskTitle}\n")
            append("💬 Message: ${item.customMessage}\n\n")
            append("Chobbare Wali Dukan - Scheduled Task SMS")
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("📤 Scheduled SMS Sent: ${item.taskTitle}")
            .setContentText("Delivered to $recipientDisplay")
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(notifId, builder.build())
        } catch (_: Exception) { }
    }

    private fun showReadyToSendNotification(context: Context, item: TaskScheduledSmsItem, reason: String) {
        val uri = if (item.recipientPhone.isNotBlank()) Uri.parse("smsto:${item.recipientPhone}") else Uri.parse("smsto:")
        val smsIntent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", item.customMessage)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val smsPendingIntent = PendingIntent.getActivity(
            context,
            ((item.id % 900000) + 200000).toInt(),
            smsIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Mark Done Intent
        val markDoneIntent = Intent(context, TaskActionReceiver::class.java).apply {
            action = TaskReminderHelper.ACTION_MARK_TASK_DONE
            putExtra(TaskReminderHelper.EXTRA_TASK_ID, item.taskId)
        }
        val markDonePendingIntent = PendingIntent.getBroadcast(
            context,
            ((item.id % 900000) + 300000).toInt(),
            markDoneIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notifId = ((item.id % 900000) + 100000).toInt()

        val recipientDisplay = if (item.recipientName.isNotBlank()) "${item.recipientName} (${item.recipientPhone})" else item.recipientPhone

        val bigText = buildString {
            append("⏰ Scheduled Time arrived for Task SMS!\n")
            append("👤 To: $recipientDisplay\n")
            append("📌 Task: ${item.taskTitle}\n")
            append("💬 Message: ${item.customMessage}\n")
            if (reason.isNotBlank()) append("\nNote: $reason (Tap 'Send SMS Now' to send via SMS app)\n")
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("⏰ Scheduled Task SMS Alert: ${item.taskTitle}")
            .setContentText("Tap to send SMS to $recipientDisplay")
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setContentIntent(smsPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_send,
                "📤 Send SMS Now",
                smsPendingIntent
            )
            .addAction(
                android.R.drawable.checkbox_on_background,
                "✓ Mark Done",
                markDonePendingIntent
            )

        try {
            NotificationManagerCompat.from(context).notify(notifId, builder.build())
        } catch (_: Exception) { }
    }
}

/**
 * Receiver for Scheduled SMS Alarm triggers and Boot completion
 */
class TaskScheduledSmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            TaskSmsSchedulerHelper.rescheduleAllPending(context)
            return
        }

        if (intent.action == TaskSmsSchedulerHelper.ACTION_SCHEDULED_TASK_SMS) {
            val smsId = intent.getLongExtra(TaskSmsSchedulerHelper.EXTRA_SMS_ID, 0L)
            if (smsId > 0L) {
                TaskSmsSchedulerHelper.executeScheduledSms(context, smsId)
            }
        }
    }
}
