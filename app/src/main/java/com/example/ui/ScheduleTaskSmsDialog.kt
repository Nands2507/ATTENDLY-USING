package com.example.ui

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.ChecklistItemEntity
import com.example.data.StudentEntity
import com.example.data.displayName
import com.example.data.getSubtasks
import com.example.data.todayComment
import com.example.reminder.TaskScheduledSmsItem
import com.example.reminder.TaskSmsSchedulerHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Builds the default formatted SMS text for a task
 */
fun buildDefaultTaskSmsMessage(item: ChecklistItemEntity, studentName: String): String {
    val subtasks = item.getSubtasks()
    val priorityTag = when (item.priority) {
        "URGENT" -> "[URGENT] "
        "HIGH" -> "[HIGH] "
        else -> ""
    }
    val assignedTag = if (studentName.isNotBlank()) "👤 For: $studentName\n" else ""
    val dueTag = if (item.timeDue.isNotBlank()) "⏰ Due: ${item.timeDue}\n" else ""
    val notesTag = if (item.notes.isNotBlank()) "📝 Notes: ${item.notes}\n" else ""
    val todayNote = if (item.todayComment.isNotBlank()) "📌 Today's Note: ${item.todayComment}\n" else ""

    val subtasksTag = if (subtasks.isNotEmpty()) {
        val stStr = subtasks.joinToString("\n") { "  • ${if (it.isCompleted) "[✓]" else "[ ]"} ${it.title}" }
        "Subtasks:\n$stStr\n"
    } else ""

    return buildString {
        append("📌 $priorityTag${item.title.trim()}\n")
        append(assignedTag)
        append(dueTag)
        append(notesTag)
        append(todayNote)
        append(subtasksTag)
        append("Chobbare Wali Dukan")
    }.trim()
}

/**
 * Dialog allowing complete customization of date, time, recipient, message,
 * and dispatch mode for scheduling a task via SMS.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTaskSmsDialog(
    item: ChecklistItemEntity,
    student: StudentEntity?,
    allStudents: List<StudentEntity>,
    onDismiss: () -> Unit,
    onScheduled: (TaskScheduledSmsItem) -> Unit,
    onSendImmediately: (phoneNumber: String, message: String) -> Unit,
    onCancelSchedule: (Long) -> Unit
) {
    val context = LocalContext.current

    // Look for existing active scheduled SMS for this task
    var existingSchedule by remember {
        mutableStateOf(TaskSmsSchedulerHelper.getScheduledSmsForTask(context, item.id))
    }

    // Recipient phone & name
    val defaultPhone = remember(student, existingSchedule) {
        existingSchedule?.recipientPhone?.takeIf { it.isNotBlank() }
            ?: student?.phone?.trim()
            ?: ""
    }
    val defaultName = remember(student, existingSchedule) {
        existingSchedule?.recipientName?.takeIf { it.isNotBlank() }
            ?: student?.displayName
            ?: item.assignedStaffName.takeIf { it.isNotBlank() }
            ?: "Shop Staff"
    }

    var recipientPhone by remember { mutableStateOf(defaultPhone) }
    var recipientName by remember { mutableStateOf(defaultName) }
    var isPhoneError by remember { mutableStateOf(false) }

    // Message text
    val defaultMsg = remember(item, defaultName) {
        existingSchedule?.customMessage?.takeIf { it.isNotBlank() }
            ?: buildDefaultTaskSmsMessage(item, defaultName)
    }
    var customMessage by remember { mutableStateOf(defaultMsg) }

    // Date selection
    // 0 = Today, 1 = Tomorrow, 2 = Custom Date
    var dateChoice by remember {
        mutableStateOf(
            if (existingSchedule != null && existingSchedule!!.scheduledTimestamp > System.currentTimeMillis()) {
                val cal = Calendar.getInstance().apply { timeInMillis = existingSchedule!!.scheduledTimestamp }
                val nowCal = Calendar.getInstance()
                if (cal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                    cal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR)
                ) {
                    0
                } else if (cal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                    cal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR) + 1
                ) {
                    1
                } else {
                    2
                }
            } else {
                0
            }
        )
    }

    var customDateCalendar by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                if (existingSchedule != null && existingSchedule!!.scheduledTimestamp > System.currentTimeMillis()) {
                    timeInMillis = existingSchedule!!.scheduledTimestamp
                }
            }
        )
    }

    // Time selection
    var selectedHour by remember {
        mutableStateOf(
            if (existingSchedule != null && existingSchedule!!.scheduledTimestamp > System.currentTimeMillis()) {
                Calendar.getInstance().apply { timeInMillis = existingSchedule!!.scheduledTimestamp }.get(Calendar.HOUR_OF_DAY)
            } else {
                val cal = Calendar.getInstance().apply { add(Calendar.MINUTE, 30) }
                cal.get(Calendar.HOUR_OF_DAY)
            }
        )
    }

    var selectedMinute by remember {
        mutableStateOf(
            if (existingSchedule != null && existingSchedule!!.scheduledTimestamp > System.currentTimeMillis()) {
                Calendar.getInstance().apply { timeInMillis = existingSchedule!!.scheduledTimestamp }.get(Calendar.MINUTE)
            } else {
                val cal = Calendar.getInstance().apply { add(Calendar.MINUTE, 30) }
                cal.get(Calendar.MINUTE)
            }
        )
    }

    // Auto-send vs Notification reminder
    var autoSend by remember {
        mutableStateOf(existingSchedule?.autoSend ?: true)
    }

    // Permission launcher for SEND_SMS
    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "SMS permission granted! Auto-send enabled.", Toast.LENGTH_SHORT).show()
            autoSend = true
        } else {
            Toast.makeText(context, "SMS permission denied. Tasks will notify you with 1-tap to send.", Toast.LENGTH_LONG).show()
            autoSend = false
        }
    }

    // Formatted time display
    val selectedTimeDisplayStr = remember(selectedHour, selectedMinute) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
        }
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
    }

    // Formatted date display
    val selectedDateDisplayStr = remember(dateChoice, customDateCalendar) {
        when (dateChoice) {
            0 -> "Today"
            1 -> "Tomorrow"
            else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(customDateCalendar.time)
        }
    }

    // Compute target trigger millis
    fun calculateTargetMillis(): Long {
        val targetCal = Calendar.getInstance().apply {
            when (dateChoice) {
                0 -> {} // Today
                1 -> add(Calendar.DAY_OF_YEAR, 1) // Tomorrow
                2 -> {
                    set(Calendar.YEAR, customDateCalendar.get(Calendar.YEAR))
                    set(Calendar.MONTH, customDateCalendar.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, customDateCalendar.get(Calendar.DAY_OF_MONTH))
                }
            }
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If target time is already in the past today, automatically shift to tomorrow
        if (dateChoice == 0 && targetCal.timeInMillis <= System.currentTimeMillis()) {
            targetCal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return targetCal.timeInMillis
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF2563EB).copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Sms,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Schedule Task via SMS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Customize date, time & message content",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Task Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = item.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (item.notes.isNotBlank()) {
                            Text(
                                text = "Note: ${item.notes}",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Active schedule warning/info banner
                if (existingSchedule != null && existingSchedule!!.status == "SCHEDULED" && existingSchedule!!.scheduledTimestamp > System.currentTimeMillis()) {
                    val formattedSched = remember(existingSchedule!!.scheduledTimestamp) {
                        SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault()).format(Date(existingSchedule!!.scheduledTimestamp))
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Active SMS Scheduled:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8)
                                )
                                Text(
                                    text = "$formattedSched → ${existingSchedule!!.recipientName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E40AF)
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    onCancelSchedule(existingSchedule!!.id)
                                    existingSchedule = null
                                    Toast.makeText(context, "Scheduled SMS cancelled", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Cancel", fontSize = 10.5.sp)
                            }
                        }
                    }
                }

                // SECTION 1: Recipient Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "1. Recipient Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Quick staff chips
                    if (allStudents.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(allStudents) { st ->
                                val isSelected = recipientName.equals(st.displayName, ignoreCase = true) || recipientPhone == st.phone.trim()
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        recipientName = st.displayName
                                        recipientPhone = st.phone.trim()
                                        isPhoneError = false
                                        customMessage = buildDefaultTaskSmsMessage(item, st.displayName)
                                    },
                                    label = {
                                        Text(st.name, fontSize = 11.sp)
                                    },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp)) }
                                    } else null
                                )
                            }
                        }
                    }

                    // Phone input field
                    OutlinedTextField(
                        value = recipientPhone,
                        onValueChange = {
                            recipientPhone = it
                            isPhoneError = false
                        },
                        label = { Text("Recipient Phone Number") },
                        placeholder = { Text("e.g. 9876543210") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (recipientPhone.isNotBlank()) {
                                IconButton(onClick = { recipientPhone = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        isError = isPhoneError,
                        supportingText = if (isPhoneError) {
                            { Text("Please enter a valid mobile number", color = MaterialTheme.colorScheme.error) }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_sms_phone")
                    )

                    // Name input field
                    OutlinedTextField(
                        value = recipientName,
                        onValueChange = { recipientName = it },
                        label = { Text("Recipient Name (Optional)") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // SECTION 2: Customized Date & Time
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "2. Schedule Date & Time",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Date selector chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = dateChoice == 0,
                            onClick = { dateChoice = 0 },
                            label = { Text("Today", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = dateChoice == 1,
                            onClick = { dateChoice = 1 },
                            label = { Text("Tomorrow", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = dateChoice == 2,
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    { _, y, m, d ->
                                        customDateCalendar = Calendar.getInstance().apply {
                                            set(Calendar.YEAR, y)
                                            set(Calendar.MONTH, m)
                                            set(Calendar.DAY_OF_MONTH, d)
                                        }
                                        dateChoice = 2
                                    },
                                    customDateCalendar.get(Calendar.YEAR),
                                    customDateCalendar.get(Calendar.MONTH),
                                    customDateCalendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            },
                            label = {
                                Text(
                                    if (dateChoice == 2) selectedDateDisplayStr else "Pick Date...",
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            },
                            modifier = Modifier.weight(1.3f)
                        )
                    }

                    // Interactive Clock Trigger Card
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.2.dp, Color(0xFF2563EB).copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                TimePickerDialog(
                                    context,
                                    { _, h, m ->
                                        selectedHour = h
                                        selectedMinute = m
                                    },
                                    selectedHour,
                                    selectedMinute,
                                    false
                                ).show()
                            }
                            .testTag("btn_schedule_sms_clock")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "SMS Dispatch Time (Tap to open clock):",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$selectedTimeDisplayStr • $selectedDateDisplayStr",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF2563EB)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF2563EB).copy(alpha = 0.12f),
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Clock Dial",
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Clock",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2563EB)
                                    )
                                }
                            }
                        }
                    }

                    // Quick Presets
                    Text(
                        text = "Quick Presets:",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val now = Calendar.getInstance()
                        val presets = listOf(
                            "+15m" to 15,
                            "+30m" to 30,
                            "+1 hr" to 60,
                            "09:30 AM" to -1,
                            "02:00 PM" to -2,
                            "06:00 PM" to -3,
                            "08:30 PM" to -4
                        )

                        items(presets) { (label, code) ->
                            FilterChip(
                                selected = false,
                                onClick = {
                                    when (code) {
                                        15, 30, 60 -> {
                                            val c = Calendar.getInstance().apply { add(Calendar.MINUTE, code) }
                                            selectedHour = c.get(Calendar.HOUR_OF_DAY)
                                            selectedMinute = c.get(Calendar.MINUTE)
                                            dateChoice = 0
                                        }
                                        -1 -> { selectedHour = 9; selectedMinute = 30 }
                                        -2 -> { selectedHour = 14; selectedMinute = 0 }
                                        -3 -> { selectedHour = 18; selectedMinute = 0 }
                                        -4 -> { selectedHour = 20; selectedMinute = 30 }
                                    }
                                },
                                label = { Text(label, fontSize = 10.5.sp) }
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // SECTION 3: Customized SMS Message
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "3. Customize SMS Message",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Reset button
                        TextButton(
                            onClick = {
                                customMessage = buildDefaultTaskSmsMessage(item, recipientName)
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Reset Text", fontSize = 10.5.sp)
                        }
                    }

                    OutlinedTextField(
                        value = customMessage,
                        onValueChange = { customMessage = it },
                        label = { Text("SMS Text Message Body") },
                        minLines = 3,
                        maxLines = 6,
                        supportingText = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tip: You can freely edit this message")
                                Text("${customMessage.length} chars")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_sms_body")
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // SECTION 4: Dispatch Mode
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (autoSend) "⚡ Auto-Send SMS via SIM" else "🔔 Remind Me & Open SMS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = if (autoSend) Color(0xFF1D4ED8) else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (autoSend)
                                    "Dispatches SMS automatically in the background at scheduled time."
                                else
                                    "Shows high-priority alert notification with 1-tap to open SMS composer.",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = autoSend,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.SEND_SMS
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (!hasPermission) {
                                        smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                                    } else {
                                        autoSend = true
                                    }
                                } else {
                                    autoSend = false
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF2563EB),
                                checkedTrackColor = Color(0xFF93C5FD)
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Primary Schedule Button
                Button(
                    onClick = {
                        val cleanPhone = recipientPhone.replace(Regex("[^0-9+]"), "").trim()
                        if (cleanPhone.isBlank()) {
                            isPhoneError = true
                            Toast.makeText(context, "Please enter recipient phone number", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (customMessage.isBlank()) {
                            Toast.makeText(context, "Please enter an SMS message", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val targetMillis = calculateTargetMillis()
                        val scheduledItem = TaskScheduledSmsItem(
                            id = existingSchedule?.id ?: System.currentTimeMillis(),
                            taskId = item.id,
                            taskTitle = item.title,
                            staffName = item.assignedStaffName,
                            recipientPhone = cleanPhone,
                            recipientName = recipientName.ifBlank { "Staff" },
                            customMessage = customMessage.trim(),
                            scheduledTimestamp = targetMillis,
                            autoSend = autoSend,
                            status = "SCHEDULED"
                        )

                        val success = TaskSmsSchedulerHelper.scheduleTaskSms(context, scheduledItem)
                        if (success) {
                            val sdf = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault())
                            Toast.makeText(
                                context,
                                "✅ Task SMS scheduled for ${sdf.format(Date(targetMillis))} to ${scheduledItem.recipientName}",
                                Toast.LENGTH_LONG
                            ).show()
                            onScheduled(scheduledItem)
                        } else {
                            Toast.makeText(context, "Failed to schedule SMS alarm", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_confirm_schedule_sms")
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "📅 Schedule SMS for $selectedTimeDisplayStr ($selectedDateDisplayStr)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Send Immediately Button
                OutlinedButton(
                    onClick = {
                        val cleanPhone = recipientPhone.replace(Regex("[^0-9+]"), "").trim()
                        if (cleanPhone.isBlank()) {
                            isPhoneError = true
                            Toast.makeText(context, "Please enter recipient phone number", Toast.LENGTH_SHORT).show()
                            return@OutlinedButton
                        }
                        onSendImmediately(cleanPhone, customMessage.trim())
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1D4ED8)),
                    border = BorderStroke(1.dp, Color(0xFF2563EB)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("💬 Send Now via SMS", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

/**
 * Dialog displaying all active and historical scheduled task SMS dispatches
 */
@Composable
fun ScheduledSmsListDialog(
    allStudents: List<StudentEntity>,
    onDismiss: () -> Unit,
    onCancelItem: (Long) -> Unit,
    onSendNow: (TaskScheduledSmsItem) -> Unit
) {
    val context = LocalContext.current
    var scheduledList by remember {
        mutableStateOf(TaskSmsSchedulerHelper.getAllScheduledSms(context))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scheduled Task SMS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
                IconButton(
                    onClick = {
                        scheduledList = TaskSmsSchedulerHelper.getAllScheduledSms(context)
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(18.dp))
                }
            }
        },
        text = {
            if (scheduledList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Sms,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No scheduled SMS tasks",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "You can schedule any task via SMS with custom date & time",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(scheduledList, key = { it.id }) { smsItem ->
                        val isUpcoming = smsItem.status == "SCHEDULED" && smsItem.scheduledTimestamp > System.currentTimeMillis()
                        val sdf = SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault())
                        val timeStr = sdf.format(Date(smsItem.scheduledTimestamp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isUpcoming) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(
                                1.dp,
                                if (isUpcoming) Color(0xFF3B82F6).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Status Badge
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (smsItem.status) {
                                            "SENT" -> Color(0xFFDCFCE7)
                                            "SCHEDULED" -> if (isUpcoming) Color(0xFFDBEAFE) else Color(0xFFFEF3C7)
                                            "FAILED" -> Color(0xFFFEE2E2)
                                            else -> Color(0xFFF3F4F6)
                                        }
                                    ) {
                                        Text(
                                            text = when (smsItem.status) {
                                                "SENT" -> "✓ DELIVERED"
                                                "SCHEDULED" -> if (isUpcoming) "⏰ SCHEDULED" else "PASSED"
                                                "FAILED" -> "⚠ FAILED"
                                                else -> smsItem.status
                                            },
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (smsItem.status) {
                                                "SENT" -> Color(0xFF15803D)
                                                "SCHEDULED" -> if (isUpcoming) Color(0xFF1D4ED8) else Color(0xFF92400E)
                                                "FAILED" -> Color(0xFFB91C1C)
                                                else -> Color(0xFF4B5563)
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = timeStr,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isUpcoming) Color(0xFF1E40AF) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = smsItem.taskTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = "To: ${smsItem.recipientName} (${smsItem.recipientPhone})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = smsItem.customMessage,
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                if (isUpcoming) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(
                                            onClick = {
                                                onCancelItem(smsItem.id)
                                                scheduledList = TaskSmsSchedulerHelper.getAllScheduledSms(context)
                                            },
                                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFDC2626)),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Cancel Schedule", fontSize = 11.sp)
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        OutlinedButton(
                                            onClick = { onSendNow(smsItem) },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2563EB)),
                                            border = BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.4f)),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("Send Now", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
