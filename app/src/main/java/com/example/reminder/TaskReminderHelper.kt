package com.example.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.AttendanceDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TaskReminderHelper {
    const val CHANNEL_ID = "task_reminders_channel"
    const val CHANNEL_NAME = "Shop Task Reminders"
    const val CHANNEL_DESCRIPTION = "Alerts and reminders for shop opening, daily duties, and staff tasks"

    const val ACTION_TASK_REMINDER = "com.example.ACTION_TASK_REMINDER"
    const val ACTION_MARK_TASK_DONE = "com.example.ACTION_MARK_TASK_DONE"

    const val EXTRA_TASK_ID = "extra_task_id"
    const val EXTRA_TASK_TITLE = "extra_task_title"
    const val EXTRA_STAFF_NAME = "extra_staff_name"
    const val EXTRA_DUE_TIME = "extra_due_time"
    const val EXTRA_NOTES = "extra_notes"

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

    fun scheduleReminder(
        context: Context,
        taskId: Long,
        taskTitle: String,
        staffName: String,
        dueTime: String,
        notes: String,
        triggerAtMillis: Long
    ) {
        createNotificationChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            action = ACTION_TASK_REMINDER
            putExtra(EXTRA_TASK_ID, taskId)
            putExtra(EXTRA_TASK_TITLE, taskTitle)
            putExtra(EXTRA_STAFF_NAME, staffName)
            putExtra(EXTRA_DUE_TIME, dueTime)
            putExtra(EXTRA_NOTES, notes)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            flags
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Fallback for devices restricting exact alarms
            try {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } catch (_: Exception) { }
        } catch (e: Exception) {
            // Fallback
            try {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } catch (_: Exception) { }
        }
    }

    fun cancelReminder(context: Context, taskId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            action = ACTION_TASK_REMINDER
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            flags
        )
        try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        } catch (_: Exception) { }
    }

    fun showTaskNotification(
        context: Context,
        taskId: Long,
        taskTitle: String,
        staffName: String,
        dueTime: String,
        notes: String
    ) {
        createNotificationChannel(context)

        // Open App Intent
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TASK_ID, taskId)
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            (taskId * 10).toInt(),
            openAppIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Mark Done Intent
        val markDoneIntent = Intent(context, TaskActionReceiver::class.java).apply {
            action = ACTION_MARK_TASK_DONE
            putExtra(EXTRA_TASK_ID, taskId)
        }
        val markDonePendingIntent = PendingIntent.getBroadcast(
            context,
            (taskId * 10 + 1).toInt(),
            markDoneIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val assignedSubtitle = if (staffName.isNotBlank()) "👤 Assigned: $staffName" else "🏪 Shop Duty"
        val dueSubtitle = if (dueTime.isNotBlank()) " • ⏰ Due: $dueTime" else ""
        val contentSubtitle = "$assignedSubtitle$dueSubtitle"

        val bigText = buildString {
            append("📌 $taskTitle\n")
            if (staffName.isNotBlank()) append("👤 Staff: $staffName\n")
            if (dueTime.isNotBlank()) append("⏰ Target Time: $dueTime\n")
            if (notes.isNotBlank()) append("📝 Notes: $notes\n")
            append("\nChobbare Wali Dukan - Task Manager")
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("⏰ Task Reminder: $taskTitle")
            .setContentText(contentSubtitle)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(defaultSoundUri)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(
                android.R.drawable.checkbox_on_background,
                "✓ Mark Done",
                markDonePendingIntent
            )

        try {
            NotificationManagerCompat.from(context).notify(taskId.toInt(), builder.build())
        } catch (_: SecurityException) {
            // Notifications not allowed
        } catch (_: Exception) { }
    }
}

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(TaskReminderHelper.EXTRA_TASK_ID, 0L)
        val taskTitle = intent.getStringExtra(TaskReminderHelper.EXTRA_TASK_TITLE) ?: "Pending Shop Task"
        val staffName = intent.getStringExtra(TaskReminderHelper.EXTRA_STAFF_NAME) ?: ""
        val dueTime = intent.getStringExtra(TaskReminderHelper.EXTRA_DUE_TIME) ?: ""
        val notes = intent.getStringExtra(TaskReminderHelper.EXTRA_NOTES) ?: ""

        if (taskId > 0L) {
            TaskReminderHelper.showTaskNotification(
                context = context,
                taskId = taskId,
                taskTitle = taskTitle,
                staffName = staffName,
                dueTime = dueTime,
                notes = notes
            )
        }
    }
}

class TaskActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TaskReminderHelper.ACTION_MARK_TASK_DONE) {
            val taskId = intent.getLongExtra(TaskReminderHelper.EXTRA_TASK_ID, 0L)
            if (taskId > 0L) {
                // Cancel active notification
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                notificationManager?.cancel(taskId.toInt())

                // Mark task completed in database
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = AttendanceDatabase.getDatabase(context)
                        db.checklistItemDao().updateCompletion(taskId, true, System.currentTimeMillis())
                    } catch (_: Exception) { }
                }
            }
        }
    }
}
