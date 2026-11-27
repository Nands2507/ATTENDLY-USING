package com.example.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.filled.EditNote
import com.example.data.ChecklistItemEntity
import com.example.data.SubtaskItem
import com.example.data.getSubtasks
import com.example.data.toJson
import com.example.data.StudentEntity
import com.example.data.displayName
import com.example.data.isChecklist
import com.example.data.isTask
import com.example.data.isDoneToday
import com.example.data.todayComment
import com.example.data.DailyCommentHistoryItem
import com.example.data.getDailyCommentHistory
import androidx.compose.material.icons.filled.Schedule
import com.example.reminder.TaskReminderHelper
import com.example.reminder.TaskScheduledSmsItem
import com.example.reminder.TaskSmsSchedulerHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Scope identifier for Checklist View:
 * - SCOPE_GENERAL (0L) : General Shop Checklist
 * - SCOPE_ALL (-1L)    : All Tasks combined across Shop & Employees
 * - Student ID (> 0L)  : Specific Employee Checklist
 */
const val CHECKLIST_SCOPE_GENERAL = 0L
const val CHECKLIST_SCOPE_ALL = -1L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistView(
    checklistItems: List<ChecklistItemEntity>,
    allStudents: List<StudentEntity>,
    onToggleItem: (ChecklistItemEntity) -> Unit,
    onAddItem: (studentId: Long, title: String, category: String, notes: String, assignedStaffName: String, reminderTimestamp: Long, itemType: String, subtasksJson: String) -> Unit,
    onUpdateItem: (ChecklistItemEntity) -> Unit,
    onDeleteItem: (ChecklistItemEntity) -> Unit,
    onSetReminder: (ChecklistItemEntity, Long) -> Unit,
    onCancelReminder: (ChecklistItemEntity) -> Unit,
    onResetScope: (studentId: Long?) -> Unit,
    onRestoreDefaults: (studentId: Long, studentName: String) -> Unit,
    onMoveUp: (ChecklistItemEntity) -> Unit = {},
    onMoveDown: (ChecklistItemEntity) -> Unit = {},
    onSwapItems: (ChecklistItemEntity, ChecklistItemEntity) -> Unit = { _, _ -> },
    onMoveToPosition: (ChecklistItemEntity, Int) -> Unit = { _, _ -> },
    onReorderItems: (List<ChecklistItemEntity>) -> Unit = {},
    onToggleSubtask: (ChecklistItemEntity, String) -> Unit = { _, _ -> },
    onAddSubtask: (ChecklistItemEntity, String) -> Unit = { _, _ -> },
    onDeleteSubtask: (ChecklistItemEntity, String) -> Unit = { _, _ -> },
    onUpdateDailyComment: (ChecklistItemEntity, String) -> Unit = { _, _ -> },
    initialScopeId: Long = CHECKLIST_SCOPE_ALL,
    onScopeSelected: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Initialize notification channel
    LaunchedEffect(Unit) {
        TaskReminderHelper.createNotificationChannel(context)
    }

    // Permission launcher for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Notification permission is needed for task reminder alerts", Toast.LENGTH_SHORT).show()
        }
    }

    fun ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Active Scope: 0L = General Shop, -1L = All Combined, >0L = Specific Employee
    var selectedScopeId by remember(initialScopeId) { mutableStateOf(initialScopeId) }

    // 2-LISTS ONLY: "CHECKLIST" (Daily items) vs "TASK" (Tagged tasks)
    var selectedListType by rememberSaveable { mutableStateOf("CHECKLIST") } // "CHECKLIST" or "TASK"
    var selectedTaskTag by rememberSaveable { mutableStateOf("ALL") } // For Tasks tag filter
    var selectedStatusFilter by rememberSaveable { mutableStateOf("ALL") } // ALL, PENDING, COMPLETED
    var searchQuery by remember { mutableStateOf("") }

    var showAddItemDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<ChecklistItemEntity?>(null) }
    var itemForDailyComment by remember { mutableStateOf<ChecklistItemEntity?>(null) }
    var reminderItem by remember { mutableStateOf<ChecklistItemEntity?>(null) }
    var itemForScheduleSms by remember { mutableStateOf<ChecklistItemEntity?>(null) }
    var showScheduledSmsList by remember { mutableStateOf(false) }
    var scheduledSmsRefreshKey by remember { mutableStateOf(0) }
    var itemToDelete by remember { mutableStateOf<ChecklistItemEntity?>(null) }
    var rearrangeDialogItem by remember { mutableStateOf<ChecklistItemEntity?>(null) }
    var rearrangeScopeList by remember { mutableStateOf<List<ChecklistItemEntity>>(emptyList()) }
    var bulkReorderScopeTitle by remember { mutableStateOf("") }
    var bulkReorderTaskList by remember { mutableStateOf<List<ChecklistItemEntity>?>(null) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showRestoreDefaultsDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    val todayDateFormatted = remember {
        val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
        sdf.format(Date())
    }

    val selectedStudent = remember(selectedScopeId, allStudents) {
        if (selectedScopeId > 0L) allStudents.find { it.id == selectedScopeId } else null
    }

    // Items for current scope
    val scopeItems = remember(checklistItems, selectedScopeId) {
        when (selectedScopeId) {
            CHECKLIST_SCOPE_GENERAL -> checklistItems.filter { it.studentId == 0L }
            CHECKLIST_SCOPE_ALL -> checklistItems
            else -> checklistItems.filter { it.studentId == selectedScopeId }
        }
    }

    // Segregated into 2 lists: CHECKLIST (Daily) & TASKS (Tagged)
    val checklistScopeItems = remember(scopeItems) {
        scopeItems.filter { it.isChecklist }
    }
    val tasksScopeItems = remember(scopeItems) {
        scopeItems.filter { it.isTask }
    }

    // Dynamic tags discovered from tasks (excluding predefined system keywords)
    val dynamicTaskTags = remember(tasksScopeItems) {
        val predefined = setOf("OPENING", "DAILY", "STOCK", "SALES", "CLOSING", "URGENT", "GENERAL", "TASK", "CHECKLIST")
        tasksScopeItems.map { it.category.trim() }
            .filter { it.isNotBlank() && it.uppercase() !in predefined }
            .distinct()
    }

    // Filtered items based on active list type (CHECKLIST vs TASKS)
    // Sinks completed tasks to the end/last, and brings incomplete tasks forward
    val filteredItems = remember(
        selectedListType,
        checklistScopeItems,
        tasksScopeItems,
        selectedTaskTag,
        selectedStatusFilter,
        searchQuery
    ) {
        val rawItems = if (selectedListType == "CHECKLIST") {
            checklistScopeItems.filter { item ->
                val matchesStatus = when (selectedStatusFilter) {
                    "PENDING" -> !item.isDoneToday
                    "COMPLETED" -> item.isDoneToday
                    else -> true
                }
                val matchesSearch = if (searchQuery.isBlank()) true else {
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.assignedStaffName.contains(searchQuery, ignoreCase = true) ||
                    item.notes.contains(searchQuery, ignoreCase = true) ||
                    item.todayComment.contains(searchQuery, ignoreCase = true)
                }
                matchesStatus && matchesSearch
            }
        } else {
            tasksScopeItems.filter { item ->
                val matchesTag = when (selectedTaskTag) {
                    "ALL" -> true
                    else -> item.category.equals(selectedTaskTag, ignoreCase = true)
                }
                val matchesStatus = when (selectedStatusFilter) {
                    "PENDING" -> !item.isDoneToday
                    "COMPLETED" -> item.isDoneToday
                    else -> true
                }
                val matchesSearch = if (searchQuery.isBlank()) true else {
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true) ||
                    item.assignedStaffName.contains(searchQuery, ignoreCase = true) ||
                    item.notes.contains(searchQuery, ignoreCase = true) ||
                    item.todayComment.contains(searchQuery, ignoreCase = true)
                }
                matchesTag && matchesStatus && matchesSearch
            }
        }

        // Incomplete/pending tasks come forward (first), completed tasks go to last
        rawItems.sortedWith(
            compareBy<ChecklistItemEntity> { it.isDoneToday }
                .thenBy { it.displayOrder }
                .thenBy { it.id }
        )
    }

    val activeListItems = if (selectedListType == "CHECKLIST") checklistScopeItems else tasksScopeItems
    val totalCount = activeListItems.size
    val completedCount = activeListItems.count { it.isDoneToday }
    val pendingCount = totalCount - completedCount
    val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val animatedProgress by animateFloatAsState(targetValue = progressFraction, label = "progress")

    val checklistTotal = checklistScopeItems.size
    val checklistCompleted = checklistScopeItems.count { it.isDoneToday }
    val tasksTotal = tasksScopeItems.size
    val tasksCompleted = tasksScopeItems.count { it.isDoneToday }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. SCOPE SELECTOR ROW (Shop General | Staff Members | All Tasks)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp, start = 2.dp, end = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SELECT VIEW / SCOPE",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (selectedScopeId != CHECKLIST_SCOPE_ALL) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.clickable {
                                    selectedScopeId = CHECKLIST_SCOPE_ALL
                                    onScopeSelected(CHECKLIST_SCOPE_ALL)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Checklist,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "View All Tasks",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Option 1: All Tasks (FIRST & PROMINENT)
                        item {
                            val isSelected = selectedScopeId == CHECKLIST_SCOPE_ALL
                            val allDone = checklistItems.count { it.isCompleted }
                            val allTotal = checklistItems.size

                            ScopeSelectorChip(
                                title = "📋 All Tasks",
                                subtitle = "$allDone/$allTotal Tasks",
                                isSelected = isSelected,
                                isCompleted = allTotal > 0 && allDone == allTotal,
                                onClick = { 
                                    selectedScopeId = CHECKLIST_SCOPE_ALL
                                    onScopeSelected(CHECKLIST_SCOPE_ALL)
                                },
                                testTag = "scope_all_combined"
                            )
                        }

                        // Option 2: Shop General
                        item {
                            val isSelected = selectedScopeId == CHECKLIST_SCOPE_GENERAL
                            val generalItems = checklistItems.filter { it.studentId == 0L }
                            val genDone = generalItems.count { it.isCompleted }
                            val genTotal = generalItems.size

                            ScopeSelectorChip(
                                title = "🏪 General Shop",
                                subtitle = "$genDone/$genTotal Done",
                                isSelected = isSelected,
                                isCompleted = genTotal > 0 && genDone == genTotal,
                                onClick = { 
                                    selectedScopeId = CHECKLIST_SCOPE_GENERAL
                                    onScopeSelected(CHECKLIST_SCOPE_GENERAL)
                                },
                                testTag = "scope_general_shop"
                            )
                        }

                        // Option 3: Individual Employees
                        items(allStudents, key = { it.id }) { student ->
                            val isSelected = selectedScopeId == student.id
                            val empItems = checklistItems.filter { it.studentId == student.id }
                            val empDone = empItems.count { it.isCompleted }
                            val empTotal = empItems.size

                            ScopeSelectorChip(
                                title = "👤 ${student.name}",
                                subtitle = if (empTotal > 0) "$empDone/$empTotal Done" else "No Tasks",
                                isSelected = isSelected,
                                isCompleted = empTotal > 0 && empDone == empTotal,
                                onClick = { 
                                    selectedScopeId = student.id
                                    onScopeSelected(student.id)
                                },
                                testTag = "scope_student_${student.id}"
                            )
                        }
                    }
                }
            }

            // 2. SCOPE DETAIL & PROGRESS HERO CARD WITH QUICK MESSAGING BUTTONS
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checklist_progress_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (selectedScopeId) {
                            CHECKLIST_SCOPE_GENERAL -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                            CHECKLIST_SCOPE_ALL -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)
                            else -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Header title, subtitle, and action menu
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (selectedScopeId) {
                                                CHECKLIST_SCOPE_GENERAL -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                                CHECKLIST_SCOPE_ALL -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                                else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (selectedScopeId) {
                                            CHECKLIST_SCOPE_GENERAL -> Icons.Default.Storefront
                                            CHECKLIST_SCOPE_ALL -> Icons.Default.Groups
                                            else -> Icons.Default.Person
                                        },
                                        contentDescription = null,
                                        tint = when (selectedScopeId) {
                                            CHECKLIST_SCOPE_GENERAL -> MaterialTheme.colorScheme.primary
                                            CHECKLIST_SCOPE_ALL -> MaterialTheme.colorScheme.tertiary
                                            else -> MaterialTheme.colorScheme.secondary
                                        },
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = when (selectedScopeId) {
                                            CHECKLIST_SCOPE_GENERAL -> "General Shop Checklist"
                                            CHECKLIST_SCOPE_ALL -> "All Tasks (Store & Staff)"
                                            else -> "${selectedStudent?.name ?: "Employee"}'s Tasks"
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = when (selectedScopeId) {
                                            CHECKLIST_SCOPE_GENERAL -> "Opening, Cash, Stock & Store Closing"
                                            CHECKLIST_SCOPE_ALL -> "$todayDateFormatted"
                                            else -> "${if (selectedStudent?.phone?.isNotBlank() == true) "📞 ${selectedStudent.phone} • " else ""}$todayDateFormatted"
                                        },
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Options menu
                            Box {
                                IconButton(onClick = { showMoreMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Options"
                                    )
                                }
                                DropdownMenu(
                                    expanded = showMoreMenu,
                                    onDismissRequest = { showMoreMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                when (selectedScopeId) {
                                                    CHECKLIST_SCOPE_GENERAL -> "Reset Shop Tasks (New Day)"
                                                    CHECKLIST_SCOPE_ALL -> "Reset All Tasks (New Day)"
                                                    else -> "Reset ${selectedStudent?.name ?: "Employee"}'s Tasks"
                                                }
                                            )
                                        },
                                        leadingIcon = { Icon(Icons.Default.RestartAlt, contentDescription = null) },
                                        onClick = {
                                            showMoreMenu = false
                                            showResetConfirmDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                when (selectedScopeId) {
                                                    CHECKLIST_SCOPE_GENERAL -> "Restore Standard Shop Tasks"
                                                    CHECKLIST_SCOPE_ALL -> "Restore Standard Shop & Staff Tasks"
                                                    else -> "Apply Standard Tasks for ${selectedStudent?.name ?: "Employee"}"
                                                }
                                            )
                                        },
                                        leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                                        onClick = {
                                            showMoreMenu = false
                                            showRestoreDefaultsDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Send All via WhatsApp") },
                                        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF25D366)) },
                                        onClick = {
                                            showMoreMenu = false
                                            shareChecklistToWhatsApp(
                                                context = context,
                                                dateStr = todayDateFormatted,
                                                scopeId = selectedScopeId,
                                                student = selectedStudent,
                                                items = scopeItems,
                                                allItems = checklistItems,
                                                allStudents = allStudents
                                            )
                                        }
                                    )
                                    if (selectedScopeId != CHECKLIST_SCOPE_ALL && activeListItems.size > 1) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    if (selectedScopeId == CHECKLIST_SCOPE_GENERAL) "Reorder Shop Tasks"
                                                    else "Reorder ${selectedStudent?.name ?: "Employee"}'s Tasks"
                                                )
                                            },
                                            leadingIcon = { Icon(Icons.Default.SwapVert, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                            onClick = {
                                                showMoreMenu = false
                                                val sortedActive = activeListItems.sortedWith(
                                                    compareBy<ChecklistItemEntity> { it.isDoneToday }
                                                        .thenBy { it.displayOrder }
                                                        .thenBy { it.id }
                                                )
                                                bulkReorderScopeTitle = if (selectedScopeId == CHECKLIST_SCOPE_GENERAL) "General Shop" else (selectedStudent?.name ?: "Employee")
                                                bulkReorderTaskList = sortedActive
                                            }
                                        )
                                    }
                                    if (selectedScopeId > 0L && selectedStudent?.phone?.isNotBlank() == true) {
                                        DropdownMenuItem(
                                            text = { Text("Send All via SMS Text") },
                                            leadingIcon = { Icon(Icons.Default.Sms, contentDescription = null, tint = Color(0xFF2563EB)) },
                                            onClick = {
                                                showMoreMenu = false
                                                sendFullTaskListViaSms(
                                                    context = context,
                                                    student = selectedStudent,
                                                    items = scopeItems,
                                                    dateStr = todayDateFormatted
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Meter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$completedCount of $totalCount completed",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "${(progressFraction * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (progressFraction == 1.0f) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (progressFraction == 1.0f) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round
                        )

                        // 100% Celebration banner
                        if (totalCount > 0 && completedCount == totalCount) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFDCFCE7),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when (selectedScopeId) {
                                            CHECKLIST_SCOPE_GENERAL -> "🎉 Great! All General Shop tasks are done!"
                                            CHECKLIST_SCOPE_ALL -> "🎉 Outstanding! All store and staff tasks completed!"
                                            else -> "🎉 Great job! ${selectedStudent?.name ?: "Staff"} finished all assigned tasks!"
                                        },
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D)
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Metric badges & Quick Sending Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFDCFCE7),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("DONE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                                    Text("$completedCount", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF15803D))
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFEF3C7),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("PENDING", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                    Text("$pendingCount", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFB45309))
                                }
                            }

                            // Quick Reorder Tasks button (when viewing a specific employee or General Shop with 2+ items)
                            if (selectedScopeId != CHECKLIST_SCOPE_ALL && activeListItems.size > 1) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .clickable {
                                            val sortedActive = activeListItems.sortedWith(
                                                compareBy<ChecklistItemEntity> { it.isDoneToday }
                                                    .thenBy { it.displayOrder }
                                                    .thenBy { it.id }
                                            )
                                            bulkReorderScopeTitle = if (selectedScopeId == CHECKLIST_SCOPE_GENERAL) "General Shop" else (selectedStudent?.name ?: "Employee")
                                            bulkReorderTaskList = sortedActive
                                        }
                                        .testTag("btn_reorder_scope_tasks")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SwapVert,
                                            contentDescription = "Reorder Tasks",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Reorder",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            // Quick WhatsApp Send button
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF25D366).copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .weight(1.25f)
                                    .clickable {
                                        shareChecklistToWhatsApp(
                                            context = context,
                                            dateStr = todayDateFormatted,
                                            scopeId = selectedScopeId,
                                            student = selectedStudent,
                                            items = scopeItems,
                                            allItems = checklistItems,
                                            allStudents = allStudents
                                        )
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "WhatsApp",
                                        tint = Color(0xFF0F9D58),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (selectedScopeId > 0L) "WhatsApp" else "Share List",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF0F9D58),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. 2-LISTS SELECTOR, SEARCH & TAGS
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // 2-LISTS SELECTOR: CHECKLIST & TASKS
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // CHECKLIST TAB (Daily routines)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (selectedListType == "CHECKLIST") Color(0xFF7C3AED) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(
                                1.2.dp,
                                if (selectedListType == "CHECKLIST") Color(0xFF6D28D9) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedListType = "CHECKLIST" }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(
                                            if (selectedListType == "CHECKLIST") Color.White.copy(alpha = 0.2f) else Color(0xFF7C3AED).copy(alpha = 0.15f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Checklist,
                                        contentDescription = "Checklist",
                                        tint = if (selectedListType == "CHECKLIST") Color.White else Color(0xFF7C3AED),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "CHECKLIST",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = if (selectedListType == "CHECKLIST") Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Daily ($checklistCompleted/$checklistTotal)",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (selectedListType == "CHECKLIST") Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // TASKS TAB (With category tags)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (selectedListType == "TASK") Color(0xFFEA580C) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(
                                1.2.dp,
                                if (selectedListType == "TASK") Color(0xFFC2410C) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedListType = "TASK" }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(
                                            if (selectedListType == "TASK") Color.White.copy(alpha = 0.2f) else Color(0xFFEA580C).copy(alpha = 0.15f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TaskAlt,
                                        contentDescription = "Tasks",
                                        tint = if (selectedListType == "TASK") Color.White else Color(0xFFEA580C),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "TASKS",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = if (selectedListType == "TASK") Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Tagged ($tasksCompleted/$tasksTotal)",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (selectedListType == "TASK") Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // CHECKLIST Daily Reset Info Notice
                    if (selectedListType == "CHECKLIST") {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF7C3AED).copy(alpha = 0.09f),
                            border = BorderStroke(1.dp, Color(0xFF7C3AED).copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Today,
                                    contentDescription = null,
                                    tint = Color(0xFF7C3AED),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Daily checklist: Once ticked today, items automatically reset unticked tomorrow.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF5B21B6),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // TASKS Category Tags Bar (Matches user reference layout with tag pills)
                    if (selectedListType == "TASK") {
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(
                                text = "CATEGORY TAGS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            val allTaskTags = remember(dynamicTaskTags) {
                                val base = listOf(
                                    "ALL" to "All",
                                    "OPENING" to "🌅 Opening",
                                    "DAILY" to "📋 Daily",
                                    "STOCK" to "📦 Stock",
                                    "SALES" to "💰 Sales",
                                    "CLOSING" to "🌙 Closing"
                                )
                                val custom = dynamicTaskTags.map { it to "🏷️ $it" }
                                base + custom
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(allTaskTags) { (tagKey, tagLabel) ->
                                    val isSelected = selectedTaskTag.equals(tagKey, ignoreCase = true)
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (isSelected) Color(0xFFEA580C) else Color(0xFF1E293B),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) Color(0xFFEA580C) else Color(0xFF475569)
                                        ),
                                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                                        modifier = Modifier.clickable { selectedTaskTag = tagKey }
                                    ) {
                                        Text(
                                            text = tagLabel,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color.White else Color(0xFFE2E8F0),
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checklist_search_input"),
                        placeholder = {
                            Text(
                                when (selectedScopeId) {
                                    CHECKLIST_SCOPE_GENERAL -> if (selectedListType == "CHECKLIST") "Search shop daily checklist..." else "Search shop tasks..."
                                    CHECKLIST_SCOPE_ALL -> if (selectedListType == "CHECKLIST") "Search daily routines across staff..." else "Search tasks across all staff..."
                                    else -> if (selectedListType == "CHECKLIST") "Search ${selectedStudent?.name ?: "employee"}'s daily checklist..." else "Search ${selectedStudent?.name ?: "employee"}'s tasks..."
                                }
                            )
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Status Chips: All / Pending / Done / Scheduled SMS
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            "ALL" to "All ($totalCount)",
                            "PENDING" to "Pending ($pendingCount)",
                            "COMPLETED" to "Done ($completedCount)"
                        ).forEach { (statusKey, statusLabel) ->
                            val isSel = selectedStatusFilter == statusKey
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSel) {
                                    if (selectedListType == "CHECKLIST") Color(0xFF7C3AED).copy(alpha = 0.18f) else Color(0xFFEA580C).copy(alpha = 0.18f)
                                } else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSel) {
                                        if (selectedListType == "CHECKLIST") Color(0xFF7C3AED) else Color(0xFFEA580C)
                                    } else Color.Transparent
                                ),
                                modifier = Modifier
                                    .clickable { selectedStatusFilter = statusKey }
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = statusLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) {
                                        if (selectedListType == "CHECKLIST") Color(0xFF6D28D9) else Color(0xFFC2410C)
                                    } else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Scheduled SMS Chip
                        val activeSchedCount = remember(scheduledSmsRefreshKey) {
                            TaskSmsSchedulerHelper.getAllScheduledSms(context).count {
                                it.status == "SCHEDULED" && it.scheduledTimestamp > System.currentTimeMillis()
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (activeSchedCount > 0) Color(0xFFDBEAFE) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (activeSchedCount > 0) Color(0xFF3B82F6) else Color.Transparent),
                            modifier = Modifier
                                .clickable { showScheduledSmsList = true }
                                .padding(vertical = 2.dp)
                                .testTag("btn_scheduled_sms_filter")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (activeSchedCount > 0) Color(0xFF1D4ED8) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Scheduled SMS ($activeSchedCount)",
                                    fontSize = 11.sp,
                                    fontWeight = if (activeSchedCount > 0) FontWeight.Bold else FontWeight.Medium,
                                    color = if (activeSchedCount > 0) Color(0xFF1E40AF) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 4. TASK ITEMS LIST (Grouped or Direct)
            if (filteredItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = when (selectedScopeId) {
                                    CHECKLIST_SCOPE_GENERAL -> Icons.Default.Storefront
                                    CHECKLIST_SCOPE_ALL -> Icons.Default.Checklist
                                    else -> Icons.Default.Person
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (searchQuery.isNotBlank() || (selectedListType == "TASK" && selectedTaskTag != "ALL") || selectedStatusFilter != "ALL") {
                                    "No items match your filters."
                                } else {
                                    when (selectedScopeId) {
                                        CHECKLIST_SCOPE_GENERAL -> "No General Shop tasks added yet!"
                                        CHECKLIST_SCOPE_ALL -> "No checklist tasks found!"
                                        else -> "No tasks assigned to ${selectedStudent?.name ?: "this employee"} yet!"
                                    }
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = when (selectedScopeId) {
                                    CHECKLIST_SCOPE_GENERAL -> "Add shop opening, display, cash register, and lockup checklists."
                                    CHECKLIST_SCOPE_ALL -> "Create general shop tasks or assign specific duties to each employee."
                                    else -> "Assign daily customer assistance, saree shelf dusting, or billing duties to ${selectedStudent?.name ?: "this employee"}."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { showAddItemDialog = true },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Task")
                                }

                                OutlinedButton(
                                    onClick = {
                                        val targetId = if (selectedScopeId == CHECKLIST_SCOPE_ALL) CHECKLIST_SCOPE_GENERAL else selectedScopeId
                                        val targetName = selectedStudent?.name ?: ""
                                        onRestoreDefaults(targetId, targetName)
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Apply Standard Tasks")
                                }
                            }
                        }
                    }
                }
            } else if (selectedScopeId == CHECKLIST_SCOPE_ALL) {
                // ALL COMBINED VIEW: Group by Scope (Shop General, followed by Each Employee)
                val generalList = filteredItems.filter { it.studentId == 0L }
                if (generalList.isNotEmpty()) {
                    item {
                        ChecklistSectionHeader(
                            title = "🏪 General Shop Tasks",
                            count = "${generalList.count { it.isDoneToday }}/${generalList.size} Done",
                            color = MaterialTheme.colorScheme.primary,
                            onReorderClick = if (generalList.size > 1) {
                                {
                                    bulkReorderScopeTitle = "General Shop"
                                    bulkReorderTaskList = generalList
                                }
                            } else null
                        )
                    }
                    items(generalList, key = { it.id }) { item ->
                        val itemIndex = generalList.indexOf(item)
                        ChecklistItemCard(
                            item = item,
                            student = null,
                            showStaffName = true,
                            canMoveUp = itemIndex > 0,
                            canMoveDown = itemIndex < generalList.size - 1,
                            onMoveUp = {
                                if (itemIndex > 0) {
                                    val target = generalList[itemIndex - 1]
                                    onSwapItems(item, target)
                                } else {
                                    onMoveUp(item)
                                }
                            },
                            onMoveDown = {
                                if (itemIndex < generalList.size - 1) {
                                    val target = generalList[itemIndex + 1]
                                    onSwapItems(item, target)
                                } else {
                                    onMoveDown(item)
                                }
                            },
                            onOpenRearrange = {
                                rearrangeScopeList = generalList
                                rearrangeDialogItem = item
                            },
                            onToggle = { onToggleItem(item) },
                            onEdit = { editingItem = item },
                            onDelete = { itemToDelete = item },
                            onOpenReminder = {
                                ensureNotificationPermission()
                                reminderItem = item
                            },
                            onSendWhatsApp = {
                                sendTaskViaWhatsApp(context, item, null)
                            },
                            onSendSms = {
                                sendTaskViaSms(context, item, null)
                            },
                            onScheduleSms = {
                                itemForScheduleSms = item
                            },
                            onToggleSubtask = { subtaskId -> onToggleSubtask(item, subtaskId) },
                            onOpenDailyComment = { itemForDailyComment = item }
                        )
                    }
                }

                // Group by Employees
                allStudents.forEach { student ->
                    val studentTasks = filteredItems.filter { it.studentId == student.id }
                    if (studentTasks.isNotEmpty()) {
                        item {
                            ChecklistSectionHeader(
                                title = "👤 ${student.name}'s Tasks",
                                count = "${studentTasks.count { it.isDoneToday }}/${studentTasks.size} Done",
                                color = MaterialTheme.colorScheme.secondary,
                                onReorderClick = if (studentTasks.size > 1) {
                                    {
                                        bulkReorderScopeTitle = student.name
                                        bulkReorderTaskList = studentTasks
                                    }
                                } else null
                            )
                        }
                        items(studentTasks, key = { it.id }) { item ->
                            val itemIndex = studentTasks.indexOf(item)
                            ChecklistItemCard(
                                item = item,
                                student = student,
                                showStaffName = false,
                                canMoveUp = itemIndex > 0,
                                canMoveDown = itemIndex < studentTasks.size - 1,
                                onMoveUp = {
                                    if (itemIndex > 0) {
                                        val target = studentTasks[itemIndex - 1]
                                        onSwapItems(item, target)
                                    } else {
                                        onMoveUp(item)
                                    }
                                },
                                onMoveDown = {
                                    if (itemIndex < studentTasks.size - 1) {
                                        val target = studentTasks[itemIndex + 1]
                                        onSwapItems(item, target)
                                    } else {
                                        onMoveDown(item)
                                    }
                                },
                                onOpenRearrange = {
                                    rearrangeScopeList = studentTasks
                                    rearrangeDialogItem = item
                                },
                                onToggle = { onToggleItem(item) },
                                onEdit = { editingItem = item },
                                onDelete = { itemToDelete = item },
                                onOpenReminder = {
                                    ensureNotificationPermission()
                                    reminderItem = item
                                },
                                onSendWhatsApp = {
                                    sendTaskViaWhatsApp(context, item, student)
                                },
                                onSendSms = {
                                    sendTaskViaSms(context, item, student)
                                },
                                onScheduleSms = {
                                    itemForScheduleSms = item
                                },
                                onToggleSubtask = { subtaskId -> onToggleSubtask(item, subtaskId) },
                                onOpenDailyComment = { itemForDailyComment = item }
                            )
                        }
                    }
                }

                // Group any tasks assigned to staff members not in the current active list
                val knownStudentIds = allStudents.map { it.id }.toSet()
                val unmappedTasks = filteredItems.filter { it.studentId != 0L && it.studentId !in knownStudentIds }
                if (unmappedTasks.isNotEmpty()) {
                    item {
                        ChecklistSectionHeader(
                            title = "👤 Other Staff Tasks",
                            count = "${unmappedTasks.count { it.isDoneToday }}/${unmappedTasks.size} Done",
                            color = MaterialTheme.colorScheme.tertiary,
                            onReorderClick = if (unmappedTasks.size > 1) {
                                {
                                    bulkReorderScopeTitle = "Other Staff"
                                    bulkReorderTaskList = unmappedTasks
                                }
                            } else null
                        )
                    }
                    items(unmappedTasks, key = { it.id }) { item ->
                        val itemIndex = unmappedTasks.indexOf(item)
                        ChecklistItemCard(
                            item = item,
                            student = null,
                            showStaffName = true,
                            canMoveUp = itemIndex > 0,
                            canMoveDown = itemIndex < unmappedTasks.size - 1,
                            onMoveUp = {
                                if (itemIndex > 0) {
                                    val target = unmappedTasks[itemIndex - 1]
                                    onSwapItems(item, target)
                                } else {
                                    onMoveUp(item)
                                }
                            },
                            onMoveDown = {
                                if (itemIndex < unmappedTasks.size - 1) {
                                    val target = unmappedTasks[itemIndex + 1]
                                    onSwapItems(item, target)
                                } else {
                                    onMoveDown(item)
                                }
                            },
                            onOpenRearrange = {
                                rearrangeScopeList = unmappedTasks
                                rearrangeDialogItem = item
                            },
                            onToggle = { onToggleItem(item) },
                            onEdit = { editingItem = item },
                            onDelete = { itemToDelete = item },
                            onOpenReminder = {
                                ensureNotificationPermission()
                                reminderItem = item
                            },
                            onSendWhatsApp = {
                                sendTaskViaWhatsApp(context, item, null)
                            },
                            onSendSms = {
                                sendTaskViaSms(context, item, null)
                            },
                            onScheduleSms = {
                                itemForScheduleSms = item
                            },
                            onToggleSubtask = { subtaskId -> onToggleSubtask(item, subtaskId) },
                            onOpenDailyComment = { itemForDailyComment = item }
                        )
                    }
                }
            } else {
                // DIRECT LIST VIEW (Shop General or Single Employee)
                items(filteredItems, key = { it.id }) { item ->
                    val taskStudent = remember(item.studentId, allStudents) {
                        if (item.studentId > 0L) allStudents.find { it.id == item.studentId } else selectedStudent
                    }
                    val itemIndex = filteredItems.indexOf(item)
                    ChecklistItemCard(
                        item = item,
                        student = taskStudent,
                        showStaffName = selectedScopeId == CHECKLIST_SCOPE_GENERAL,
                        canMoveUp = itemIndex > 0,
                        canMoveDown = itemIndex < filteredItems.size - 1,
                        onMoveUp = {
                            if (itemIndex > 0) {
                                val target = filteredItems[itemIndex - 1]
                                onSwapItems(item, target)
                            } else {
                                onMoveUp(item)
                            }
                        },
                        onMoveDown = {
                            if (itemIndex < filteredItems.size - 1) {
                                val target = filteredItems[itemIndex + 1]
                                onSwapItems(item, target)
                            } else {
                                onMoveDown(item)
                            }
                        },
                        onOpenRearrange = {
                            rearrangeScopeList = filteredItems
                            rearrangeDialogItem = item
                        },
                        onToggle = { onToggleItem(item) },
                        onEdit = { editingItem = item },
                        onDelete = { itemToDelete = item },
                        onOpenReminder = {
                            ensureNotificationPermission()
                            reminderItem = item
                        },
                        onSendWhatsApp = {
                            sendTaskViaWhatsApp(context, item, taskStudent)
                        },
                        onSendSms = {
                            sendTaskViaSms(context, item, taskStudent)
                        },
                        onScheduleSms = {
                            itemForScheduleSms = item
                        },
                        onToggleSubtask = { subtaskId -> onToggleSubtask(item, subtaskId) },
                        onOpenDailyComment = { itemForDailyComment = item }
                    )
                }
            }
        }

        // FLOATING ACTION BUTTON (Add Checklist or Task)
        FloatingActionButton(
            onClick = { showAddItemDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_checklist_item"),
            containerColor = if (selectedListType == "CHECKLIST") Color(0xFF7C3AED) else Color(0xFFEA580C),
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = if (selectedListType == "CHECKLIST") "Add Checklist" else "Add Task")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (selectedListType == "CHECKLIST") {
                        when (selectedScopeId) {
                            CHECKLIST_SCOPE_GENERAL -> "Add Shop Checklist"
                            CHECKLIST_SCOPE_ALL -> "Add Checklist"
                            else -> "Add Checklist for ${selectedStudent?.name?.split(" ")?.firstOrNull() ?: "Staff"}"
                        }
                    } else {
                        when (selectedScopeId) {
                            CHECKLIST_SCOPE_GENERAL -> "Add Shop Task"
                            CHECKLIST_SCOPE_ALL -> "Add Task"
                            else -> "Add Task for ${selectedStudent?.name?.split(" ")?.firstOrNull() ?: "Staff"}"
                        }
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // DIALOG: ADD NEW CHECKLIST ITEM OR TASK
    if (showAddItemDialog) {
        AddEditChecklistItemDialog(
            item = null,
            initialScopeId = if (selectedScopeId == CHECKLIST_SCOPE_ALL) CHECKLIST_SCOPE_GENERAL else selectedScopeId,
            allStudents = allStudents,
            initialItemType = selectedListType,
            onDismiss = { showAddItemDialog = false },
            onSave = { studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson ->
                onAddItem(studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson)
                showAddItemDialog = false
            },
            onSaveAndSendWhatsApp = { studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson ->
                onAddItem(studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson)
                showAddItemDialog = false
                val targetStudent = allStudents.find { it.id == studentId }
                val dummyItem = ChecklistItemEntity(
                    studentId = studentId,
                    title = title,
                    category = category,
                    notes = notes,
                    assignedStaffName = staffName,
                    itemType = itemType,
                    subtasksJson = subtasksJson
                )
                sendTaskViaWhatsApp(context, dummyItem, targetStudent)
            },
            onSaveAndSendSms = { studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson ->
                onAddItem(studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson)
                showAddItemDialog = false
                val targetStudent = allStudents.find { it.id == studentId }
                val dummyItem = ChecklistItemEntity(
                    studentId = studentId,
                    title = title,
                    category = category,
                    notes = notes,
                    assignedStaffName = staffName,
                    itemType = itemType,
                    subtasksJson = subtasksJson
                )
                sendTaskViaSms(context, dummyItem, targetStudent)
            },
            onSaveAndScheduleSms = { studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson ->
                onAddItem(studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson)
                showAddItemDialog = false
                val dummyItem = ChecklistItemEntity(
                    studentId = studentId,
                    title = title,
                    category = category,
                    notes = notes,
                    assignedStaffName = staffName,
                    itemType = itemType,
                    subtasksJson = subtasksJson
                )
                itemForScheduleSms = dummyItem
            }
        )
    }

    // DIALOG: EDIT EXISTING TASK
    if (editingItem != null) {
        AddEditChecklistItemDialog(
            item = editingItem,
            initialScopeId = editingItem!!.studentId,
            allStudents = allStudents,
            initialItemType = editingItem!!.itemType,
            onDismiss = { editingItem = null },
            onSave = { studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson ->
                val current = editingItem!!
                val updated = current.copy(
                    studentId = studentId,
                    title = title,
                    category = category,
                    notes = notes,
                    assignedStaffName = staffName,
                    reminderTimestamp = reminderTimestamp,
                    itemType = itemType,
                    subtasksJson = subtasksJson
                )
                onUpdateItem(updated)
                if (reminderTimestamp > System.currentTimeMillis()) {
                    onSetReminder(updated, reminderTimestamp)
                } else if (current.reminderTimestamp > 0L && reminderTimestamp == 0L) {
                    onCancelReminder(updated)
                }
                editingItem = null
            },
            onSaveAndSendWhatsApp = { studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson ->
                val current = editingItem!!
                val updated = current.copy(
                    studentId = studentId,
                    title = title,
                    category = category,
                    notes = notes,
                    assignedStaffName = staffName,
                    reminderTimestamp = reminderTimestamp,
                    itemType = itemType,
                    subtasksJson = subtasksJson
                )
                onUpdateItem(updated)
                editingItem = null
                val targetStudent = allStudents.find { it.id == studentId }
                sendTaskViaWhatsApp(context, updated, targetStudent)
            },
            onSaveAndSendSms = { studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson ->
                val current = editingItem!!
                val updated = current.copy(
                    studentId = studentId,
                    title = title,
                    category = category,
                    notes = notes,
                    assignedStaffName = staffName,
                    reminderTimestamp = reminderTimestamp,
                    itemType = itemType,
                    subtasksJson = subtasksJson
                )
                onUpdateItem(updated)
                editingItem = null
                val targetStudent = allStudents.find { it.id == studentId }
                sendTaskViaSms(context, updated, targetStudent)
            },
            onSaveAndScheduleSms = { studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson ->
                val current = editingItem!!
                val updated = current.copy(
                    studentId = studentId,
                    title = title,
                    category = category,
                    notes = notes,
                    assignedStaffName = staffName,
                    reminderTimestamp = reminderTimestamp,
                    itemType = itemType,
                    subtasksJson = subtasksJson
                )
                onUpdateItem(updated)
                editingItem = null
                itemForScheduleSms = updated
            }
        )
    }

    // DIALOG: SCHEDULE TASK VIA SMS (Custom date, time, recipient, message & auto-send)
    if (itemForScheduleSms != null) {
        val targetStudent = allStudents.find { it.id == itemForScheduleSms!!.studentId }
        ScheduleTaskSmsDialog(
            item = itemForScheduleSms!!,
            student = targetStudent,
            allStudents = allStudents,
            onDismiss = { itemForScheduleSms = null },
            onScheduled = { scheduledItem ->
                scheduledSmsRefreshKey++
                itemForScheduleSms = null
            },
            onSendImmediately = { phone, message ->
                TaskSmsSchedulerHelper.sendSmsImmediately(context, phone, message)
                itemForScheduleSms = null
            },
            onCancelSchedule = { smsId ->
                TaskSmsSchedulerHelper.cancelScheduledSms(context, smsId)
                scheduledSmsRefreshKey++
            }
        )
    }

    // DIALOG: SCHEDULED TASK SMS LIST & MANAGEMENT
    if (showScheduledSmsList) {
        ScheduledSmsListDialog(
            allStudents = allStudents,
            onDismiss = { showScheduledSmsList = false },
            onCancelItem = { smsId ->
                TaskSmsSchedulerHelper.cancelScheduledSms(context, smsId)
                scheduledSmsRefreshKey++
            },
            onSendNow = { smsItem ->
                TaskSmsSchedulerHelper.sendSmsImmediately(context, smsItem.recipientPhone, smsItem.customMessage)
                TaskSmsSchedulerHelper.cancelScheduledSms(context, smsItem.id)
                scheduledSmsRefreshKey++
            }
        )
    }

    // DIALOG: SET TASK REMINDER / NOTIFICATION
    if (reminderItem != null) {
        SetTaskReminderDialog(
            item = reminderItem!!,
            student = allStudents.find { it.id == reminderItem!!.studentId },
            onDismiss = { reminderItem = null },
            onSetReminder = { triggerMillis ->
                onSetReminder(reminderItem!!, triggerMillis)
                val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                Toast.makeText(context, "⏰ Reminder set for ${sdf.format(Date(triggerMillis))}", Toast.LENGTH_SHORT).show()
                reminderItem = null
            },
            onCancelReminder = {
                onCancelReminder(reminderItem!!)
                Toast.makeText(context, "Reminder cancelled", Toast.LENGTH_SHORT).show()
                reminderItem = null
            }
        )
    }

    // DIALOG: CONFIRM DELETE
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Checklist Task?") },
            text = { Text("Are you sure you want to delete \"${itemToDelete!!.title}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteItem(itemToDelete!!)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: DAILY NOTE / COMMENT FOR CHECKLIST ITEM (Resets each new day)
    if (itemForDailyComment != null) {
        DailyCommentDialog(
            item = itemForDailyComment!!,
            onDismiss = { itemForDailyComment = null },
            onSave = { comment ->
                onUpdateDailyComment(itemForDailyComment!!, comment)
                itemForDailyComment = null
            }
        )
    }

    // DIALOG: CONFIRM RESET CHECKS (New Day)
    if (showResetConfirmDialog) {
        val targetName = when (selectedScopeId) {
            CHECKLIST_SCOPE_GENERAL -> "General Shop tasks"
            CHECKLIST_SCOPE_ALL -> "ALL tasks across the shop and staff"
            else -> "${selectedStudent?.name ?: "Employee"}'s tasks"
        }
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Start Fresh / Reset Checks?") },
            text = { Text("This will uncheck all completed items for $targetName so you can start a new day.") },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedScopeId == CHECKLIST_SCOPE_ALL) {
                            onResetScope(null) // all
                        } else {
                            onResetScope(selectedScopeId)
                        }
                        showResetConfirmDialog = false
                    }
                ) {
                    Text("Reset Checks")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: CONFIRM RESTORE STANDARD TASKS
    if (showRestoreDefaultsDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDefaultsDialog = false },
            title = { Text("Apply Standard Tasks?") },
            text = {
                Text(
                    when (selectedScopeId) {
                        CHECKLIST_SCOPE_GENERAL -> "This will add the recommended opening, cash tally, saree showcase, and closing checklist items for the shop."
                        CHECKLIST_SCOPE_ALL -> "This will load standard daily tasks for the general shop."
                        else -> "This will add 5 standard daily duties (punch-in, saree shelf arranging, customer trial assistance, T-Hisab entry, rack folding) for ${selectedStudent?.name ?: "this employee"}."
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetId = if (selectedScopeId == CHECKLIST_SCOPE_ALL) CHECKLIST_SCOPE_GENERAL else selectedScopeId
                        val targetName = selectedStudent?.name ?: ""
                        onRestoreDefaults(targetId, targetName)
                        showRestoreDefaultsDialog = false
                    }
                ) {
                    Text("Apply Tasks")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDefaultsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: REARRANGE TASK (SINGLE ITEM)
    if (rearrangeDialogItem != null) {
        RearrangeTaskDialog(
            item = rearrangeDialogItem!!,
            taskList = rearrangeScopeList.ifEmpty { listOf(rearrangeDialogItem!!) },
            onDismiss = { rearrangeDialogItem = null },
            onMoveUp = {
                val current = rearrangeDialogItem!!
                val idx = rearrangeScopeList.indexOfFirst { it.id == current.id }
                if (idx > 0) {
                    val mutable = rearrangeScopeList.toMutableList()
                    val temp = mutable[idx]
                    mutable[idx] = mutable[idx - 1]
                    mutable[idx - 1] = temp
                    rearrangeScopeList = mutable
                    onReorderItems(mutable)
                } else {
                    onMoveUp(current)
                }
            },
            onMoveDown = {
                val current = rearrangeDialogItem!!
                val idx = rearrangeScopeList.indexOfFirst { it.id == current.id }
                if (idx >= 0 && idx < rearrangeScopeList.size - 1) {
                    val mutable = rearrangeScopeList.toMutableList()
                    val temp = mutable[idx]
                    mutable[idx] = mutable[idx + 1]
                    mutable[idx + 1] = temp
                    rearrangeScopeList = mutable
                    onReorderItems(mutable)
                } else {
                    onMoveDown(current)
                }
            },
            onMoveToPosition = { targetIdx ->
                val current = rearrangeDialogItem!!
                val currentIdx = rearrangeScopeList.indexOfFirst { it.id == current.id }
                if (currentIdx != -1 && targetIdx in 0 until rearrangeScopeList.size) {
                    val mutable = rearrangeScopeList.toMutableList()
                    val moved = mutable.removeAt(currentIdx)
                    mutable.add(targetIdx, moved)
                    rearrangeScopeList = mutable
                    onReorderItems(mutable)
                } else {
                    onMoveToPosition(current, targetIdx)
                }
            }
        )
    }

    // DIALOG: BULK REORDER TASKS FOR PARTICULAR EMPLOYEE / SCOPE
    if (bulkReorderTaskList != null) {
        RearrangeEmployeeTasksDialog(
            scopeName = bulkReorderScopeTitle,
            tasks = bulkReorderTaskList!!,
            onDismiss = { bulkReorderTaskList = null },
            onSaveOrder = { newOrderedList ->
                onReorderItems(newOrderedList)
                bulkReorderTaskList = null
            }
        )
    }
}

/**
 * Scope Selector Chip component for Top Horizontal Navigation
 */
@Composable
fun ScopeSelectorChip(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        shadowElevation = if (isSelected) 3.dp else 1.dp,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1
                )
                if (isCompleted) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Done",
                        tint = if (isSelected) Color(0xFF86EFAC) else Color(0xFF16A34A),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            )
        }
    }
}

/**
 * Dialog to view, edit, or add a daily note/comment for a checklist item.
 * Automatically clears/resets every new calendar day.
 */
@Composable
fun DailyCommentDialog(
    item: ChecklistItemEntity,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val initialComment = remember(item.dailyComment, item.dailyCommentDate) { item.todayComment }
    var commentText by remember { mutableStateOf(initialComment) }
    val todayFormatted = remember {
        val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
        sdf.format(Date())
    }
    val history = remember(item.dailyCommentHistoryJson) { item.getDailyCommentHistory() }
    var showHistory by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Daily Note / Progress",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = todayFormatted,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Task context
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = item.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (item.assignedStaffName.isNotBlank()) {
                            Text(
                                text = "Assigned to: ${item.assignedStaffName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Daily Reset explanation badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🌅", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "This note stays saved for today and empties automatically when tomorrow begins.",
                            fontSize = 10.5.sp,
                            color = Color(0xFF92400E),
                            lineHeight = 14.sp
                        )
                    }
                }

                // Input field
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Today's Note or Progress") },
                    placeholder = { Text("e.g., Done morning batch, need 5 units from storeroom, follow up...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 180.dp)
                        .testTag("input_daily_comment"),
                    maxLines = 6,
                    shape = RoundedCornerShape(10.dp)
                )

                // Quick clear button if present
                if (commentText.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { commentText = "" },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear Note", fontSize = 11.sp)
                        }
                    }
                }

                // Past History Section
                if (history.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showHistory = !showHistory }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📜", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Past Daily Notes (${history.size})",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                imageVector = if (showHistory) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (showHistory) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp)
                        ) {
                            history.take(7).forEach { histItem ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = histItem.dateString,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = histItem.text,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(commentText.trim())
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD97706)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_save_daily_comment")
            ) {
                Text("Save Today's Note", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Section Header for Grouped Views
 */
@Composable
fun ChecklistSectionHeader(
    title: String,
    count: String,
    color: Color,
    onReorderClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color
            ),
            modifier = Modifier.weight(1f, fill = false),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onReorderClick != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                    modifier = Modifier.clickable { onReorderClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Reorder Tasks",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Reorder",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = color.copy(alpha = 0.12f)
            ) {
                Text(
                    text = count,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Individual Checklist Item Card featuring small WhatsApp, SMS Text, and Reminder icons
 */
@Composable
fun ChecklistItemCard(
    item: ChecklistItemEntity,
    student: StudentEntity?,
    showStaffName: Boolean,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onOpenReminder: () -> Unit,
    onSendWhatsApp: () -> Unit,
    onSendSms: () -> Unit,
    onScheduleSms: () -> Unit = {},
    onToggleSubtask: (String) -> Unit = {},
    onOpenDailyComment: () -> Unit = {},
    canMoveUp: Boolean = true,
    canMoveDown: Boolean = true,
    onMoveUp: () -> Unit = {},
    onMoveDown: () -> Unit = {},
    onOpenRearrange: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDone = item.isDoneToday
    val hasReminder = item.reminderTimestamp > 0L && item.reminderTimestamp > System.currentTimeMillis()

    val containerColor by animateColorAsState(
        targetValue = if (isDone) MaterialTheme.colorScheme.surface.copy(alpha = 0.65f) else MaterialTheme.colorScheme.surface,
        label = "card_bg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("checklist_item_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDone) 0.5.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Main Top Row: Checkbox + Title + Priority/Category + Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Checkbox
                Checkbox(
                    checked = isDone,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = if (item.isChecklist) Color(0xFF7C3AED) else Color(0xFFEA580C),
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .testTag("checkbox_item_${item.id}")
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Title & Details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onToggle() }
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isDone) FontWeight.Normal else FontWeight.SemiBold,
                            color = if (isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f) else MaterialTheme.colorScheme.onSurface,
                            textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Tags row: Category Tag / Checklist Daily badge, Priority, Due Time, Assigned Staff
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Tag or Daily Checklist Badge
                        if (item.isTask) {
                            if (item.category.isNotBlank() && item.category.uppercase() != "TASK") {
                                item {
                                    val cat = item.category.trim()
                                    val tagEmoji = when (cat.uppercase()) {
                                        "OPENING" -> "🌅"
                                        "DAILY" -> "📋"
                                        "STOCK" -> "📦"
                                        "SALES" -> "💰"
                                        "CLOSING" -> "🌙"
                                        "URGENT" -> "⚡"
                                        else -> "🏷️"
                                    }
                                    val tagText = if (cat.startsWith("🌅") || cat.startsWith("📋") || cat.startsWith("📦") || cat.startsWith("💰") || cat.startsWith("🌙") || cat.startsWith("⚡") || cat.startsWith("🏷️")) {
                                        cat
                                    } else {
                                        "$tagEmoji $cat"
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFF1E293B),
                                        border = BorderStroke(1.dp, Color(0xFF475569))
                                    ) {
                                        Text(
                                            text = tagText,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFF1F5F9),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            // CHECKLIST ITEM -> Daily reset indicator badge
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF7C3AED).copy(alpha = 0.12f),
                                    border = BorderStroke(0.8.dp, Color(0xFF7C3AED).copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = "📅 Daily",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6D28D9),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Assigned Staff Tag if enabled
                        val staffDisplayName = if (item.assignedStaffName.isNotBlank()) item.assignedStaffName else student?.name ?: ""
                        if (showStaffName && staffDisplayName.isNotBlank()) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFF3E8FF)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color(0xFF7E22CE),
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = staffDisplayName,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF7E22CE)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Notes if present
                    if (item.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Note: ${item.notes}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Active Reminder Tag Pill
                    if (item.reminderTimestamp > 0L) {
                        val isUpcoming = item.reminderTimestamp > System.currentTimeMillis()
                        val reminderDateStr = remember(item.reminderTimestamp) {
                            val sdf = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault())
                            sdf.format(Date(item.reminderTimestamp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isUpcoming) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (isUpcoming) Color(0xFFF59E0B) else Color.Transparent),
                            modifier = Modifier.clickable { onOpenReminder() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isUpcoming) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = if (isUpcoming) Color(0xFFB45309) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isUpcoming) "⏰ Reminder: $reminderDateStr" else "Reminder passed ($reminderDateStr)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUpcoming) Color(0xFF92400E) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Active Scheduled SMS Pill
                    val cardContext = LocalContext.current
                    val activeSchedSms = remember(item.id) {
                        TaskSmsSchedulerHelper.getScheduledSmsForTask(cardContext, item.id)
                    }
                    if (activeSchedSms != null) {
                        val isUpcomingSms = activeSchedSms.scheduledTimestamp > System.currentTimeMillis()
                        val smsDateStr = remember(activeSchedSms.scheduledTimestamp) {
                            val sdf = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault())
                            sdf.format(Date(activeSchedSms.scheduledTimestamp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isUpcomingSms) Color(0xFFEFF6FF) else Color(0xFFF3F4F6),
                            border = BorderStroke(1.dp, if (isUpcomingSms) Color(0xFF3B82F6) else Color.Transparent),
                            modifier = Modifier
                                .clickable { onScheduleSms() }
                                .testTag("pill_scheduled_sms_${item.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (isUpcomingSms) Color(0xFF1D4ED8) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isUpcomingSms) "📅 SMS Scheduled: $smsDateStr → ${activeSchedSms.recipientName}" else "SMS Scheduled ($smsDateStr)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUpcomingSms) Color(0xFF1E40AF) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Completed Timestamp
                    if (isDone && item.completedAt > 0) {
                        val completedTimeStr = remember(item.completedAt) {
                            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                            sdf.format(Date(item.completedAt))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "✓ Completed at $completedTimeStr",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp
                            )
                        )
                    }

                    // Subtasks Section
                    val subtasks = remember(item.subtasksJson) { item.getSubtasks() }
                    if (subtasks.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        val doneSubtasks = subtasks.count { it.isCompleted }
                        val subtaskProgress = doneSubtasks.toFloat() / subtasks.size

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Subtasks ($doneSubtasks/${subtasks.size})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${(subtaskProgress * 100).toInt()}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (doneSubtasks == subtasks.size) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            LinearProgressIndicator(
                                progress = { subtaskProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = if (doneSubtasks == subtasks.size) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            subtasks.forEach { subtask ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onToggleSubtask(subtask.id) }
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (subtask.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (subtask.isCompleted) Color(0xFF16A34A) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = subtask.title,
                                        fontSize = 11.5.sp,
                                        color = if (subtask.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                                        textDecoration = if (subtask.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Today's Daily Note (automatically clears each new day)
                    val todayNote = remember(item.dailyComment, item.dailyCommentDate) { item.todayComment }
                    if (todayNote.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF3C7).copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenDailyComment() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = "Today's note",
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 1.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Today's Note",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                        Text(
                                            text = "Resets tomorrow",
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFB45309).copy(alpha = 0.8f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = todayNote,
                                        fontSize = 11.sp,
                                        color = Color(0xFF78350F),
                                        lineHeight = 15.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit today's note",
                                    tint = Color(0xFFB45309).copy(alpha = 0.7f),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }

                // Rearrange Controls (Up / Down)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(1.dp)
                        ) {
                            // Move Up Button
                            Box(
                                modifier = Modifier
                                    .size(width = 34.dp, height = 28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (canMoveUp) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent)
                                    .clickable(enabled = canMoveUp) { onMoveUp() }
                                    .testTag("btn_move_up_${item.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Move task up",
                                    tint = if (canMoveUp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.width(22.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )

                            // Move Down Button
                            Box(
                                modifier = Modifier
                                    .size(width = 34.dp, height = 28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (canMoveDown) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent)
                                    .clickable(enabled = canMoveDown) { onMoveDown() }
                                    .testTag("btn_move_down_${item.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Move task down",
                                    tint = if (canMoveDown) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            Spacer(modifier = Modifier.height(4.dp))

            // Action Toolbar under task: WhatsApp, SMS, Reminder, Edit, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT: Messaging & Reminder Actions (horizontally scrollable so right buttons never clip)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. WhatsApp Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF25D366).copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.35f)),
                        modifier = Modifier
                            .clickable { onSendWhatsApp() }
                            .testTag("btn_whatsapp_task_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Send WhatsApp",
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "WhatsApp",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF166534)
                            )
                        }
                    }

                    // 2. SMS Text Message Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF2563EB).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.35f)),
                        modifier = Modifier
                            .clickable { onSendSms() }
                            .testTag("btn_sms_task_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sms,
                                contentDescription = "Send SMS",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "SMS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1D4ED8)
                            )
                        }
                    }

                    // 2b. Schedule Task via SMS Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF6366F1).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.35f)),
                        modifier = Modifier
                            .clickable { onScheduleSms() }
                            .testTag("btn_schedule_sms_task_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Schedule SMS",
                                tint = Color(0xFF4F46E5),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Sched SMS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF4338CA)
                            )
                        }
                    }

                    // 3. Reminder / Notification Alarm Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (hasReminder) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, if (hasReminder) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clickable { onOpenReminder() }
                            .testTag("btn_reminder_task_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (hasReminder) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = "Set Reminder",
                                tint = if (hasReminder) Color(0xFFB45309) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (hasReminder) "Reminder On" else "Remind Me",
                                fontSize = 10.sp,
                                fontWeight = if (hasReminder) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = if (hasReminder) Color(0xFF92400E) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 4. Daily Note / Progress Button (Resets each new day)
                    val hasDailyNote = remember(item.dailyComment, item.dailyCommentDate) { item.todayComment.isNotBlank() }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (hasDailyNote) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, if (hasDailyNote) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clickable { onOpenDailyComment() }
                            .testTag("btn_daily_comment_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "Daily Note",
                                tint = if (hasDailyNote) Color(0xFFB45309) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (hasDailyNote) "Note Added" else "+ Note Today",
                                fontSize = 10.sp,
                                fontWeight = if (hasDailyNote) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = if (hasDailyNote) Color(0xFF92400E) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // RIGHT: Rearrange, Edit & Delete Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onOpenRearrange,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("btn_rearrange_task_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Rearrange Task",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.75f),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dialog to rearrange task position with direct buttons, jump-to-position selector,
 * and Move Up / Move Down / Move to Top / Move to Bottom actions.
 */
@Composable
fun RearrangeTaskDialog(
    item: ChecklistItemEntity,
    taskList: List<ChecklistItemEntity>,
    onDismiss: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onMoveToPosition: (Int) -> Unit
) {
    val currentIndex = taskList.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
    val totalCount = taskList.size.coerceAtLeast(1)
    val isFirst = currentIndex <= 0
    val isLast = currentIndex >= totalCount - 1

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.SwapVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Rearrange Task",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Current: #${currentIndex + 1} of $totalCount",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Task preview
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = item.title,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (item.assignedStaffName.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Assigned: ${item.assignedStaffName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    text = "Shift Position",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Move Up / Move Down
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onMoveUp,
                        enabled = !isFirst,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Move Up", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onMoveDown,
                        enabled = !isLast,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Move Down", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Move to Top / Move to Bottom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onMoveToPosition(0)
                            onDismiss()
                        },
                        enabled = !isFirst,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("⬆️ To Top", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = {
                            onMoveToPosition(totalCount - 1)
                            onDismiss()
                        },
                        enabled = !isLast,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("⬇️ To Bottom", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                if (totalCount > 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Jump Directly to Position",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(totalCount) { posIdx ->
                            val isCurrent = posIdx == currentIndex
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(
                                    1.dp,
                                    if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable(enabled = !isCurrent) {
                                        onMoveToPosition(posIdx)
                                        onDismiss()
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${posIdx + 1}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * Full list reorder dialog for a particular employee (or scope).
 * Supports both drag-and-drop (via the ☰ drag handle) and 1-tap Up / Down / To Top buttons.
 */
@Composable
fun RearrangeEmployeeTasksDialog(
    scopeName: String,
    tasks: List<ChecklistItemEntity>,
    onDismiss: () -> Unit,
    onSaveOrder: (List<ChecklistItemEntity>) -> Unit
) {
    val orderedItems = remember(tasks) {
        mutableStateListOf<ChecklistItemEntity>().apply {
            addAll(tasks)
        }
    }
    val itemHeights = remember { mutableStateMapOf<Long, Int>() }
    var draggedItemId by remember { mutableStateOf<Long?>(null) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    val hasChanges = remember(orderedItems.toList(), tasks) {
        orderedItems.map { it.id } != tasks.map { it.id }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.SwapVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Reorder Tasks",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "👤 $scopeName • ${orderedItems.size} tasks",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Quick helpers row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Drag ☰ or tap ▲ / ▼ to reorder",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .clickable {
                                    val pendingSorted = orderedItems
                                        .filter { !it.isDoneToday }
                                        .sortedBy { it.title.lowercase(Locale.getDefault()) }
                                    val doneSorted = orderedItems
                                        .filter { it.isDoneToday }
                                        .sortedBy { it.title.lowercase(Locale.getDefault()) }
                                    orderedItems.clear()
                                    orderedItems.addAll(pendingSorted + doneSorted)
                                }
                                .testTag("btn_sort_tasks_az")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SortByAlpha,
                                    contentDescription = "Sort A to Z",
                                    modifier = Modifier.size(13.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "A → Z",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (hasChanges) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f),
                                modifier = Modifier
                                    .clickable {
                                        orderedItems.clear()
                                        orderedItems.addAll(tasks)
                                    }
                                    .testTag("btn_reset_task_order")
                            ) {
                                Text(
                                    text = "Reset",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Scrollable task list with drag-handle + up/down controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    orderedItems.forEachIndexed { index, task ->
                        val isDragging = draggedItemId == task.id
                        val isDone = task.isDoneToday
                        val canMoveUp = index > 0 && orderedItems[index - 1].isDoneToday == isDone
                        val canMoveDown = index < orderedItems.size - 1 && orderedItems[index + 1].isDoneToday == isDone

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDragging) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                            } else if (isDone) {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            },
                            border = BorderStroke(
                                width = if (isDragging) 1.5.dp else 1.dp,
                                color = if (isDragging) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                                }
                            ),
                            shadowElevation = if (isDragging) 6.dp else 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .zIndex(if (isDragging) 1f else 0f)
                                .graphicsLayer {
                                    if (isDragging) {
                                        translationY = dragOffsetY
                                        scaleX = 1.02f
                                        scaleY = 1.02f
                                    }
                                }
                                .onSizeChanged { size ->
                                    itemHeights[task.id] = size.height
                                }
                                .testTag("reorder_task_row_${task.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Drag Handle
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .pointerInput(task.id) {
                                            detectVerticalDragGestures(
                                                onDragStart = {
                                                    draggedItemId = task.id
                                                    dragOffsetY = 0f
                                                },
                                                onDragEnd = {
                                                    draggedItemId = null
                                                    dragOffsetY = 0f
                                                },
                                                onDragCancel = {
                                                    draggedItemId = null
                                                    dragOffsetY = 0f
                                                },
                                                onVerticalDrag = { change, dragAmount ->
                                                    change.consume()
                                                    dragOffsetY += dragAmount
                                                    val curIdx = orderedItems.indexOfFirst { it.id == task.id }
                                                    if (curIdx == -1) return@detectVerticalDragGestures

                                                    val curDone = orderedItems[curIdx].isDoneToday
                                                    if (dragOffsetY > 0f && curIdx < orderedItems.size - 1) {
                                                        val nextItem = orderedItems[curIdx + 1]
                                                        if (nextItem.isDoneToday == curDone) {
                                                            val threshold = (itemHeights[nextItem.id] ?: 90) * 0.55f
                                                            if (dragOffsetY > threshold) {
                                                                val swapHeight = (itemHeights[nextItem.id] ?: 90).toFloat()
                                                                orderedItems[curIdx] = nextItem
                                                                orderedItems[curIdx + 1] = task
                                                                dragOffsetY -= swapHeight
                                                            }
                                                        }
                                                    } else if (dragOffsetY < 0f && curIdx > 0) {
                                                        val prevItem = orderedItems[curIdx - 1]
                                                        if (prevItem.isDoneToday == curDone) {
                                                            val threshold = (itemHeights[prevItem.id] ?: 90) * 0.55f
                                                            if (-dragOffsetY > threshold) {
                                                                val swapHeight = (itemHeights[prevItem.id] ?: 90).toFloat()
                                                                orderedItems[curIdx] = prevItem
                                                                orderedItems[curIdx - 1] = task
                                                                dragOffsetY += swapHeight
                                                            }
                                                        }
                                                    }
                                                }
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DragHandle,
                                        contentDescription = "Drag to reorder",
                                        tint = if (isDragging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Position Badge
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDone) Color(0xFF16A34A).copy(alpha = 0.16f)
                                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isDone) Color(0xFF15803D) else MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Task Title + Type/Done badge
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        fontSize = 12.5.sp,
                                        fontWeight = if (isDone) FontWeight.Normal else FontWeight.SemiBold,
                                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                                        color = if (isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f) else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (task.isChecklist) "📅 Daily" else "📌 ${task.category.ifBlank { "Task" }}",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (task.isChecklist) Color(0xFF7C3AED) else Color(0xFFEA580C)
                                        )
                                        if (isDone) {
                                            Text(
                                                text = "• Done",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF15803D)
                                            )
                                        }
                                    }
                                }

                                // Quick Move Controls: Top, Up, Down
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            val firstSameStatusIdx = orderedItems.indexOfFirst { it.isDoneToday == isDone }
                                            if (firstSameStatusIdx in 0 until index) {
                                                val moved = orderedItems.removeAt(index)
                                                orderedItems.add(firstSameStatusIdx, moved)
                                            }
                                        },
                                        enabled = canMoveUp,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .testTag("btn_bulk_move_top_${task.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VerticalAlignTop,
                                            contentDescription = "Move to Top",
                                            tint = if (canMoveUp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            if (canMoveUp) {
                                                val prev = orderedItems[index - 1]
                                                orderedItems[index - 1] = task
                                                orderedItems[index] = prev
                                            }
                                        },
                                        enabled = canMoveUp,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .testTag("btn_bulk_move_up_${task.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowUp,
                                            contentDescription = "Move Up",
                                            tint = if (canMoveUp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            if (canMoveDown) {
                                                val next = orderedItems[index + 1]
                                                orderedItems[index + 1] = task
                                                orderedItems[index] = next
                                            }
                                        },
                                        enabled = canMoveDown,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .testTag("btn_bulk_move_down_${task.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Move Down",
                                            tint = if (canMoveDown) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSaveOrder(orderedItems.toList()) },
                modifier = Modifier.testTag("btn_save_reordered_tasks")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save Order", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Dialog to configure local in-app task reminders with one-tap presets and custom time picker
 */
@Composable
fun SetTaskReminderDialog(
    item: ChecklistItemEntity,
    student: StudentEntity?,
    onDismiss: () -> Unit,
    onSetReminder: (Long) -> Unit,
    onCancelReminder: () -> Unit
) {
    val context = LocalContext.current
    var dateChoice by remember { mutableStateOf(0) } // 0 = Today, 1 = Tomorrow, 2 = Pick Date
    var customDateCalendar by remember {
        mutableStateOf(Calendar.getInstance().apply {
            if (item.reminderTimestamp > System.currentTimeMillis()) {
                timeInMillis = item.reminderTimestamp
            }
        })
    }

    var selectedHour by remember {
        mutableStateOf(
            if (item.reminderTimestamp > System.currentTimeMillis()) {
                Calendar.getInstance().apply { timeInMillis = item.reminderTimestamp }.get(Calendar.HOUR_OF_DAY)
            } else {
                val cal = Calendar.getInstance()
                cal.get(Calendar.HOUR_OF_DAY)
            }
        )
    }
    var selectedMinute by remember {
        mutableStateOf(
            if (item.reminderTimestamp > System.currentTimeMillis()) {
                Calendar.getInstance().apply { timeInMillis = item.reminderTimestamp }.get(Calendar.MINUTE)
            } else {
                val cal = Calendar.getInstance()
                cal.get(Calendar.MINUTE)
            }
        )
    }

    val selectedTimeDisplayStr = remember(selectedHour, selectedMinute) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
        }
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
    }

    val selectedDateDisplayStr = remember(dateChoice, customDateCalendar) {
        when (dateChoice) {
            0 -> "Today"
            1 -> "Tomorrow"
            else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(customDateCalendar.time)
        }
    }

    val hasExistingReminder = item.reminderTimestamp > 0L

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Set Task Reminder",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Task Overview banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = item.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Assigned to: ${if (student != null) student.name else (if (item.assignedStaffName.isNotBlank()) item.assignedStaffName else "General Shop")}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (item.timeDue.isNotBlank()) {
                            Text(
                                text = "Target Due: ${item.timeDue}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (hasExistingReminder) {
                    val existingDateStr = remember(item.reminderTimestamp) {
                        val sdf = SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault())
                        sdf.format(Date(item.reminderTimestamp))
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Current Reminder Active:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = existingDateStr,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFB45309)
                                )
                            }
                            OutlinedButton(
                                onClick = onCancelReminder,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.5f)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Remove", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Interactive Clock Time Picker Section
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Clock",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Clock Dial Time Picker",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "Interactive Clock",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Day selector chips
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
                                modifier = Modifier.weight(1.2f)
                            )
                        }

                        // Interactive Clock Opener Card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
                                .testTag("btn_clock_dial_trigger")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Reminder Time (Tap to change clock):",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "$selectedTimeDisplayStr • $selectedDateDisplayStr",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.padding(start = 6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = "Open Clock",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Open Clock",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }

                        // Confirm Button for clock time
                        Button(
                            onClick = {
                                val cal = Calendar.getInstance().apply {
                                    when (dateChoice) {
                                        0 -> {}
                                        1 -> add(Calendar.DAY_OF_YEAR, 1)
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
                                onSetReminder(cal.timeInMillis)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AlarmOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Set Reminder ($selectedTimeDisplayStr)", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = "Or Choose Quick Presets",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 1-Tap Presets
                val now = System.currentTimeMillis()
                val presets = listOf(
                    "⚡ In 15 Minutes" to (now + 15 * 60 * 1000L),
                    "⚡ In 30 Minutes" to (now + 30 * 60 * 1000L),
                    "⏰ In 1 Hour" to (now + 60 * 60 * 1000L),
                    "🌅 Morning (09:30 AM)" to getTodayTimeMillis(9, 30),
                    "🕛 Noon (12:00 PM)" to getTodayTimeMillis(12, 0),
                    "☕ Afternoon (03:30 PM)" to getTodayTimeMillis(15, 30),
                    "🌙 Evening Closing (08:30 PM)" to getTodayTimeMillis(20, 30)
                )

                presets.forEach { (label, millis) ->
                    val isPast = millis <= System.currentTimeMillis()
                    val targetMillis = if (isPast) millis + (24 * 60 * 60 * 1000L) else millis
                    val subLabel = if (isPast) "(Tomorrow)" else "(Today)"

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSetReminder(targetMillis) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = subLabel,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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

/**
 * Add / Edit Dialog supporting Scope assignment, reminder setup, and instant WhatsApp/SMS dispatch
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditChecklistItemDialog(
    item: ChecklistItemEntity?,
    initialScopeId: Long,
    allStudents: List<StudentEntity>,
    initialItemType: String = "CHECKLIST",
    onDismiss: () -> Unit,
    onSave: (studentId: Long, title: String, category: String, notes: String, staffName: String, reminderTimestamp: Long, itemType: String, subtasksJson: String) -> Unit,
    onSaveAndSendWhatsApp: (studentId: Long, title: String, category: String, notes: String, staffName: String, reminderTimestamp: Long, itemType: String, subtasksJson: String) -> Unit,
    onSaveAndSendSms: (studentId: Long, title: String, category: String, notes: String, staffName: String, reminderTimestamp: Long, itemType: String, subtasksJson: String) -> Unit,
    onSaveAndScheduleSms: ((studentId: Long, title: String, category: String, notes: String, staffName: String, reminderTimestamp: Long, itemType: String, subtasksJson: String) -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    var assignedStudentId by remember { mutableStateOf(item?.studentId ?: initialScopeId) }
    var itemType by remember { mutableStateOf(item?.itemType ?: initialItemType) }
    var title by remember { mutableStateOf(item?.title ?: "") }
    var category by remember { mutableStateOf(item?.category ?: if (initialItemType == "TASK") "STOCK" else "DAILY") }
    var customTagInput by remember { mutableStateOf("") }
    var subtasks by remember { mutableStateOf(item?.getSubtasks() ?: emptyList()) }
    var newSubtaskInput by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf(item?.notes ?: "") }
    var reminderTimestamp by remember { mutableStateOf(item?.reminderTimestamp ?: 0L) }
    var showMoreDetails by remember {
        mutableStateOf(item != null && (item.notes.isNotBlank() || item.reminderTimestamp > 0L))
    }

    var isTitleError by remember { mutableStateOf(false) }

    fun resolveEffectiveCategory(): String {
        return if (itemType == "TASK") {
            if (customTagInput.isNotBlank()) customTagInput.trim() else category
        } else {
            "DAILY"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (item == null) {
                    if (itemType == "CHECKLIST") "Add Daily Checklist" else "Add Tagged Task"
                } else {
                    if (itemType == "CHECKLIST") "Edit Daily Checklist" else "Edit Tagged Task"
                },
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. LIST TYPE SELECTOR: CHECKLIST vs TASK (2-Lists Only)
                Column {
                    Text(
                        text = "List Type",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // CHECKLIST OPTION
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (itemType == "CHECKLIST") Color(0xFF7C3AED) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(
                                1.dp,
                                if (itemType == "CHECKLIST") Color(0xFF6D28D9) else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    itemType = "CHECKLIST"
                                    category = "DAILY"
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "📋 CHECKLIST",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = if (itemType == "CHECKLIST") Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Daily • Resets tomorrow",
                                    fontSize = 9.5.sp,
                                    color = if (itemType == "CHECKLIST") Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // TASK OPTION
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (itemType == "TASK") Color(0xFFEA580C) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(
                                1.dp,
                                if (itemType == "TASK") Color(0xFFC2410C) else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { itemType = "TASK" }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "📌 TASKS",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = if (itemType == "TASK") Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tagged with categories",
                                    fontSize = 9.5.sp,
                                    color = if (itemType == "TASK") Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // 2. Scope / Assignment Selector
                Column {
                    Text(
                        text = "Assign To",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            val isGen = assignedStudentId == 0L
                            FilterChip(
                                selected = isGen,
                                onClick = { assignedStudentId = 0L },
                                label = { Text("🏪 General Shop", fontSize = 11.sp, fontWeight = if (isGen) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        items(allStudents) { student ->
                            val isSel = assignedStudentId == student.id
                            FilterChip(
                                selected = isSel,
                                onClick = { assignedStudentId = student.id },
                                label = { Text("👤 ${student.name}", fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // 3. Task Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) isTitleError = false
                    },
                    label = { Text(if (itemType == "CHECKLIST") "Checklist Description *" else "Task Description *") },
                    placeholder = {
                        Text(
                            if (assignedStudentId == 0L) {
                                if (itemType == "CHECKLIST") "e.g. Open shop, switch on lights & AC"
                                else "e.g. Count new bridal saree arrival stock"
                            } else {
                                if (itemType == "CHECKLIST") "e.g. Morning counter dusting & attendance punch"
                                else "e.g. Deliver customer alteration packet"
                            }
                        )
                    },
                    isError = isTitleError,
                    supportingText = if (isTitleError) {
                        { Text("Please enter a description") }
                    } else null,
                    singleLine = false,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // 4. CATEGORY TAGS (Shown when TASK is selected)
                if (itemType == "TASK") {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Category Tag (Acts as Task Category)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )

                        val standardTags = listOf(
                            "OPENING" to "🌅 Opening",
                            "DAILY" to "📋 Daily",
                            "STOCK" to "📦 Stock",
                            "SALES" to "💰 Sales",
                            "CLOSING" to "🌙 Closing",
                            "URGENT" to "⚡ Urgent"
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(standardTags) { (tagKey, tagLabel) ->
                                val isSel = customTagInput.isBlank() && category.equals(tagKey, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSel) Color(0xFFEA580C) else Color(0xFF1E293B),
                                    border = BorderStroke(1.dp, if (isSel) Color(0xFFEA580C) else Color(0xFF475569)),
                                    modifier = Modifier.clickable {
                                        category = tagKey
                                        customTagInput = ""
                                    }
                                ) {
                                    Text(
                                        text = tagLabel,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) Color.White else Color(0xFFE2E8F0),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Custom Tag Input Field
                        OutlinedTextField(
                            value = customTagInput,
                            onValueChange = { customTagInput = it },
                            label = { Text("Or Type Custom Tag") },
                            placeholder = { Text("e.g. Bridal Sarees, VIP Packing, Billing") },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // SUBTASKS SECTION
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Subtasks (${subtasks.count { it.isCompleted }}/${subtasks.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (subtasks.isNotEmpty()) {
                            Text(
                                text = "Tap checkbox to toggle",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Existing Subtasks list
                    if (subtasks.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        subtasks.forEachIndexed { index, subtask ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = subtask.isCompleted,
                                    onCheckedChange = { checked ->
                                        subtasks = subtasks.mapIndexed { idx, item ->
                                            if (idx == index) item.copy(isCompleted = checked) else item
                                        }
                                    },
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = subtask.title,
                                    fontSize = 12.sp,
                                    textDecoration = if (subtask.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                    color = if (subtask.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        subtasks = subtasks.filterIndexed { idx, _ -> idx != index }
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove subtask",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Input to add a new subtask
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newSubtaskInput,
                            onValueChange = { newSubtaskInput = it },
                            placeholder = { Text("Add subtask...", fontSize = 12.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (newSubtaskInput.isNotBlank()) {
                                        subtasks = subtasks + SubtaskItem(
                                            id = "sub_${System.currentTimeMillis()}_${(100..999).random()}",
                                            title = newSubtaskInput.trim(),
                                            isCompleted = false
                                        )
                                        newSubtaskInput = ""
                                    }
                                }
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilledIconButton(
                            onClick = {
                                if (newSubtaskInput.isNotBlank()) {
                                    subtasks = subtasks + SubtaskItem(
                                        id = "sub_${System.currentTimeMillis()}_${(100..999).random()}",
                                        title = newSubtaskInput.trim(),
                                        isCompleted = false
                                    )
                                    newSubtaskInput = ""
                                }
                            },
                            enabled = newSubtaskInput.isNotBlank(),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add subtask", modifier = Modifier.size(18.dp))
                        }
                    }

                    // Quick suggestion subtask chips
                    Spacer(modifier = Modifier.height(6.dp))
                    val suggestions = listOf("Check count", "Clean area", "Take photo", "Verify stock", "Get sign", "Confirm bill")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(suggestions) { sugg ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.clickable {
                                    subtasks = subtasks + SubtaskItem(
                                        id = "sub_${System.currentTimeMillis()}_${(100..999).random()}",
                                        title = sugg,
                                        isCompleted = false
                                    )
                                }
                            ) {
                                Text(
                                    text = "+ $sugg",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                // Optional More Details Toggle (Notes & Reminder)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMoreDetails = !showMoreDetails }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showMoreDetails) "▲ Hide extra options (Notes, Reminder)" else "▼ More options (Notes, Reminder)...",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (showMoreDetails) {
                    val context = LocalContext.current

                    // Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes / Specific Instructions (Optional)") },
                        placeholder = { Text("e.g. Keep keys on manager table") },
                        singleLine = false,
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Quick Reminder setting inside dialog
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("In-App Reminder Notification", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            if (reminderTimestamp > 0L) {
                                val rDateStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(reminderTimestamp))
                                Text("Active: $rDateStr", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            item {
                                val isCustomClock = reminderTimestamp > 0L
                                FilterChip(
                                    selected = isCustomClock,
                                    onClick = {
                                        val cal = Calendar.getInstance()
                                        TimePickerDialog(
                                            context,
                                            { _, h, m ->
                                                val targetCal = Calendar.getInstance().apply {
                                                    set(Calendar.HOUR_OF_DAY, h)
                                                    set(Calendar.MINUTE, m)
                                                    set(Calendar.SECOND, 0)
                                                    set(Calendar.MILLISECOND, 0)
                                                    if (timeInMillis <= System.currentTimeMillis()) {
                                                        add(Calendar.DAY_OF_YEAR, 1)
                                                    }
                                                }
                                                reminderTimestamp = targetCal.timeInMillis
                                            },
                                            cal.get(Calendar.HOUR_OF_DAY),
                                            cal.get(Calendar.MINUTE),
                                            false
                                        ).show()
                                    },
                                    label = { Text("🕒 Clock Dial...", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                                )
                            }
                            val reminderOptions = listOf(
                                "No Reminder" to 0L,
                                "In 15m" to (System.currentTimeMillis() + 15 * 60 * 1000L),
                                "In 30m" to (System.currentTimeMillis() + 30 * 60 * 1000L),
                                "In 1 hr" to (System.currentTimeMillis() + 60 * 60 * 1000L),
                                "At 09:30 AM" to getTodayTimeMillis(9, 30),
                                "At 08:30 PM" to getTodayTimeMillis(20, 30)
                            )
                            items(reminderOptions) { (rLabel, rTime) ->
                                val isSel = (rTime == 0L && reminderTimestamp == 0L) || (rTime > 0L && reminderTimestamp > 0L && Math.abs(reminderTimestamp - rTime) < 60000)
                                FilterChip(
                                    selected = isSel,
                                    onClick = { reminderTimestamp = rTime },
                                    label = { Text(rLabel, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Save Button
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            isTitleError = true
                            return@Button
                        }
                        val staffName = if (assignedStudentId > 0L) {
                            allStudents.find { it.id == assignedStudentId }?.name ?: ""
                        } else ""
                        val finalCategory = resolveEffectiveCategory()
                        onSave(assignedStudentId, title.trim(), finalCategory, notes.trim(), staffName, reminderTimestamp, itemType, subtasks.toJson())
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (itemType == "CHECKLIST") Color(0xFF7C3AED) else Color(0xFFEA580C)
                    )
                ) {
                    Text(if (itemType == "CHECKLIST") "Save Daily Checklist" else "Save Task")
                }

                // Quick Save & Send actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (title.isBlank()) {
                                isTitleError = true
                                return@OutlinedButton
                            }
                            val staffName = if (assignedStudentId > 0L) {
                                allStudents.find { it.id == assignedStudentId }?.name ?: ""
                            } else ""
                            val finalCategory = resolveEffectiveCategory()
                            onSaveAndSendWhatsApp(assignedStudentId, title.trim(), finalCategory, notes.trim(), staffName, reminderTimestamp, itemType, subtasks.toJson())
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF15803D)),
                        border = BorderStroke(1.dp, Color(0xFF25D366))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("WhatsApp", fontSize = 10.5.sp, maxLines = 1)
                    }

                    OutlinedButton(
                        onClick = {
                            if (title.isBlank()) {
                                isTitleError = true
                                return@OutlinedButton
                            }
                            val staffName = if (assignedStudentId > 0L) {
                                allStudents.find { it.id == assignedStudentId }?.name ?: ""
                            } else ""
                            val finalCategory = resolveEffectiveCategory()
                            onSaveAndSendSms(assignedStudentId, title.trim(), finalCategory, notes.trim(), staffName, reminderTimestamp, itemType, subtasks.toJson())
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1D4ED8)),
                        border = BorderStroke(1.dp, Color(0xFF2563EB))
                    ) {
                        Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("SMS", fontSize = 10.5.sp, maxLines = 1)
                    }

                    if (onSaveAndScheduleSms != null) {
                        OutlinedButton(
                            onClick = {
                                if (title.isBlank()) {
                                    isTitleError = true
                                    return@OutlinedButton
                                }
                                val staffName = if (assignedStudentId > 0L) {
                                    allStudents.find { it.id == assignedStudentId }?.name ?: ""
                                } else ""
                                val finalCategory = resolveEffectiveCategory()
                                onSaveAndScheduleSms(assignedStudentId, title.trim(), finalCategory, notes.trim(), staffName, reminderTimestamp, itemType, subtasks.toJson())
                            },
                            modifier = Modifier.weight(1.1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4338CA)),
                            border = BorderStroke(1.dp, Color(0xFF6366F1))
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Sched SMS", fontSize = 10.5.sp, maxLines = 1)
                        }
                    }
                }
            }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item != null && onDelete != null) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

/**
 * Sends a single task assignment directly via WhatsApp with employee phone lookup
 */
fun sendTaskViaWhatsApp(context: Context, item: ChecklistItemEntity, student: StudentEntity?) {
    val staffPrefix = if (student != null) "📋 *Task for ${student.displayName}:*\n" else ""
    val priorityBadge = when (item.priority) {
        "URGENT" -> "⚡ *URGENT:* "
        "HIGH" -> "🔥 *HIGH:* "
        else -> ""
    }
    val dueStr = if (item.timeDue.isNotBlank()) "\n⏰ Due: ${item.timeDue}" else ""
    val notesStr = if (item.notes.isNotBlank()) "\n📝 Notes: ${item.notes}" else ""
    val dailyNoteStr = if (item.todayComment.isNotBlank()) "\n💬 Today's Note: ${item.todayComment}" else ""
    val messageText = "${staffPrefix}📌 $priorityBadge*${item.title.trim()}*$dueStr$notesStr$dailyNoteStr"
    val phone = student?.phone?.trim() ?: ""
    val cleanPhone = cleanPhoneNumber(phone)

    if (cleanPhone.isNotBlank()) {
        try {
            val encodedMsg = Uri.encode(messageText)
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMsg")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.whatsapp")
            }
            context.startActivity(intent)
            return
        } catch (_: Exception) {
            try {
                val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(messageText)}")
                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                return
            } catch (_: Exception) { }
        }
    }

    if (cleanPhone.isBlank() && student != null) {
        Toast.makeText(context, "No phone for ${student.displayName}. Opening WhatsApp...", Toast.LENGTH_SHORT).show()
    }

    // Fallback: General WhatsApp share
    try {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, messageText)
            setPackage("com.whatsapp")
        }
        context.startActivity(sendIntent)
    } catch (_: Exception) {
        val chooserIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, messageText)
        }
        context.startActivity(Intent.createChooser(chooserIntent, "Send Task via"))
    }
}

/**
 * Sends a single task assignment directly via SMS text message
 */
fun sendTaskViaSms(context: Context, item: ChecklistItemEntity, student: StudentEntity?) {
    val staffPrefix = if (student != null) "Task for ${student.displayName}: " else "Task: "
    val priorityBadge = when (item.priority) {
        "URGENT" -> "[URGENT] "
        "HIGH" -> "[HIGH] "
        else -> ""
    }
    val dueStr = if (item.timeDue.isNotBlank()) " (Due: ${item.timeDue})" else ""
    val notesStr = if (item.notes.isNotBlank()) " - Notes: ${item.notes}" else ""
    val dailyNoteStr = if (item.todayComment.isNotBlank()) " [Today's Note: ${item.todayComment}]" else ""
    val smsBody = "$staffPrefix$priorityBadge${item.title.trim()}$dueStr$notesStr$dailyNoteStr"

    val phone = student?.phone?.trim() ?: ""
    val cleanPhone = cleanPhoneNumber(phone)

    if (cleanPhone.isBlank() && student != null) {
        Toast.makeText(context, "No phone for ${student.displayName}. Opening SMS...", Toast.LENGTH_SHORT).show()
    }

    try {
        val uri = if (cleanPhone.isNotBlank()) Uri.parse("smsto:$cleanPhone") else Uri.parse("smsto:")
        val smsIntent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", smsBody)
        }
        context.startActivity(smsIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open SMS messenger", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Sends full task agenda of an employee via SMS
 */
fun sendFullTaskListViaSms(
    context: Context,
    student: StudentEntity,
    items: List<ChecklistItemEntity>,
    dateStr: String
) {
    if (items.isEmpty()) {
        Toast.makeText(context, "No tasks assigned to ${student.name}", Toast.LENGTH_SHORT).show()
        return
    }

    val sb = StringBuilder()
    sb.append("Chobbare Wali Dukan - Tasks for ${student.name} ($dateStr):\n")
    items.forEachIndexed { i, item ->
        val status = if (item.isCompleted) "[DONE]" else "[PENDING]"
        val due = if (item.timeDue.isNotBlank()) " @${item.timeDue}" else ""
        sb.append("${i + 1}. $status ${item.title}$due\n")
    }

    val phone = student.phone.trim()
    val cleanPhone = cleanPhoneNumber(phone)

    try {
        val uri = if (cleanPhone.isNotBlank()) Uri.parse("smsto:$cleanPhone") else Uri.parse("smsto:")
        val smsIntent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", sb.toString())
        }
        context.startActivity(smsIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open SMS messenger", Toast.LENGTH_SHORT).show()
    }
}

/**
 * WhatsApp Sharing helper for Shop General, Individual Staff, or Store Combined
 */
fun shareChecklistToWhatsApp(
    context: Context,
    dateStr: String,
    scopeId: Long,
    student: StudentEntity?,
    items: List<ChecklistItemEntity>,
    allItems: List<ChecklistItemEntity>,
    allStudents: List<StudentEntity>
) {
    if (items.isEmpty() && scopeId != CHECKLIST_SCOPE_ALL) {
        Toast.makeText(context, "No checklist tasks to share!", Toast.LENGTH_SHORT).show()
        return
    }

    val sb = StringBuilder()

    when (scopeId) {
        CHECKLIST_SCOPE_GENERAL -> {
            val total = items.size
            val done = items.count { it.isCompleted }
            val percent = if (total > 0) (done * 100 / total) else 0

            sb.append("🏪 *CHOBBARE WALI DUKAN - GENERAL SHOP CHECKLIST*\n")
            sb.append("📅 Date: $dateStr\n")
            sb.append("📊 Progress: $done of $total Tasks Completed ($percent%)\n")
            sb.append("━━━━━━━━━━━━━━━━━━━━\n\n")

            items.forEachIndexed { index, item ->
                val status = if (item.isCompleted) "✅" else "⏳"
                val timeTag = if (item.timeDue.isNotBlank()) " (${item.timeDue})" else ""
                val priorityTag = if (item.priority.equals("URGENT", true)) " ⚡[URGENT]" else if (item.priority.equals("HIGH", true)) " [HIGH]" else ""
                sb.append("${index + 1}. $status ${item.title}$timeTag$priorityTag\n")
                if (item.notes.isNotBlank()) {
                    sb.append("   ↳ Note: ${item.notes}\n")
                }
                if (item.todayComment.isNotBlank()) {
                    sb.append("   💬 Today: ${item.todayComment}\n")
                }
            }
            sb.append("\n━━━━━━━━━━━━━━━━━━━━\n")
            sb.append("Automated Shop Attendance & Checklist System")
        }

        CHECKLIST_SCOPE_ALL -> {
            val totalAll = allItems.size
            val doneAll = allItems.count { it.isCompleted }
            val percentAll = if (totalAll > 0) (doneAll * 100 / totalAll) else 0

            sb.append("🏪 *CHOBBARE WALI DUKAN - DAILY OPERATIONS REPORT*\n")
            sb.append("📅 Date: $dateStr\n")
            sb.append("📊 Overall Readiness: $doneAll / $totalAll Tasks Done ($percentAll%)\n\n")

            // Shop General
            val genList = allItems.filter { it.studentId == 0L }
            if (genList.isNotEmpty()) {
                val gDone = genList.count { it.isCompleted }
                sb.append("🏪 *GENERAL SHOP TASKS* ($gDone/${genList.size})\n")
                genList.forEach { item ->
                    val status = if (item.isCompleted) "✅" else "⏳"
                    val timeTag = if (item.timeDue.isNotBlank()) " (${item.timeDue})" else ""
                    sb.append("• $status ${item.title}$timeTag\n")
                }
                sb.append("\n")
            }

            // Each Employee
            allStudents.forEach { st ->
                val empTasks = allItems.filter { it.studentId == st.id }
                if (empTasks.isNotEmpty()) {
                    val eDone = empTasks.count { it.isCompleted }
                    sb.append("👤 *${st.name.uppercase()}* ($eDone/${empTasks.size})\n")
                    empTasks.forEach { item ->
                        val status = if (item.isCompleted) "✅" else "⏳"
                        val timeTag = if (item.timeDue.isNotBlank()) " (${item.timeDue})" else ""
                        sb.append("• $status ${item.title}$timeTag\n")
                    }
                    sb.append("\n")
                }
            }

            sb.append("━━━━━━━━━━━━━━━━━━━━\n")
            sb.append("Chobbare Wali Dukan Management")
        }

        else -> {
            // Specific Employee
            val empName = student?.name ?: "Employee"
            val total = items.size
            val done = items.count { it.isCompleted }
            val percent = if (total > 0) (done * 100 / total) else 0

            sb.append("👤 *DAILY DUTY CHECKLIST: ${empName.uppercase()}*\n")
            sb.append("🏪 Chobbare Wali Dukan\n")
            sb.append("📅 Date: $dateStr\n")
            sb.append("📊 Your Progress: $done of $total Tasks Completed ($percent%)\n")
            sb.append("━━━━━━━━━━━━━━━━━━━━\n\n")

            items.forEachIndexed { index, item ->
                val status = if (item.isCompleted) "✅" else "⏳"
                val timeTag = if (item.timeDue.isNotBlank()) " (${item.timeDue})" else ""
                val priorityTag = if (item.priority.equals("URGENT", true)) " ⚡[URGENT]" else if (item.priority.equals("HIGH", true)) " [HIGH]" else ""
                sb.append("${index + 1}. $status ${item.title}$timeTag$priorityTag\n")
                if (item.notes.isNotBlank()) {
                    sb.append("   ↳ Note: ${item.notes}\n")
                }
                if (item.todayComment.isNotBlank()) {
                    sb.append("   💬 Today: ${item.todayComment}\n")
                }
            }

            sb.append("\n━━━━━━━━━━━━━━━━━━━━\n")
            sb.append("Please complete and check off your duties today. Thank you!")
        }
    }

    val message = sb.toString()
    val cleanPhone = if (student != null) cleanPhoneNumber(student.phone) else ""

    if (cleanPhone.isNotBlank()) {
        try {
            val encodedMsg = Uri.encode(message)
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMsg")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.whatsapp")
            }
            context.startActivity(intent)
            return
        } catch (_: Exception) { }
    }

    try {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            setPackage("com.whatsapp")
        }
        context.startActivity(sendIntent)
    } catch (e: Exception) {
        val chooserIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(chooserIntent, "Share Checklist via"))
    }
}

private fun cleanPhoneNumber(phone: String): String {
    val digits = phone.filter { it.isDigit() }
    if (digits.isBlank()) return ""
    return if (digits.length == 10) "91$digits" else digits
}

private fun getTodayTimeMillis(hour: Int, minute: Int): Long {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}
