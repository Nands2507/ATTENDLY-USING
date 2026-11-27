package com.example.ui

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.animation.animateColorAsState
import kotlin.math.roundToInt
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Schedule
import com.example.reminder.TaskSmsSchedulerHelper
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ClassEntity
import com.example.data.StudentEntity
import com.example.data.cleanEmployeeName
import com.example.data.displayName
import com.example.data.ShiftEntity
import com.example.data.ChecklistItemEntity
import com.example.data.isTask
import com.example.data.isChecklist
import com.example.data.isDoneToday
import com.example.data.SubtaskItem
import com.example.data.getSubtasks
import com.example.data.toJson
import com.example.data.createSubtasksJson
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AttendanceViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val allClasses by viewModel.allClasses.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentStudents by viewModel.currentStudents.collectAsState()
    val currentAttendanceRecords by viewModel.currentAttendanceRecords.collectAsState()
    val studentAttendanceRates by viewModel.studentAttendanceRates.collectAsState()
    val classStats by viewModel.classStats.collectAsState()
    val allShifts by viewModel.allShifts.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val orderedHisabStudents by viewModel.orderedHisabStudents.collectAsState()
    val allRecordsForSelectedClass by viewModel.allRecordsForSelectedClass.collectAsState()
    val allHisabProducts by viewModel.allHisabProducts.collectAsState()
    val allHisabLedgerEntries by viewModel.allHisabLedgerEntries.collectAsState()
    val selectedHisabStudent by viewModel.selectedHisabStudent.collectAsState()
    val hisabEntriesForSelectedStudent by viewModel.hisabEntriesForSelectedStudent.collectAsState()
    var showAssignShiftForStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var showEmployeeReportForStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var showEmployeeProfileForStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var showEditStudentForStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var selectedChecklistScopeId by remember { mutableStateOf(CHECKLIST_SCOPE_ALL) }

    // Dialog flags
    var showAddClassDialog by remember { mutableStateOf(false) }
    var showRearrangeDepartmentsDialog by remember { mutableStateOf(false) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Active Tab State (0 = Mark Attendance, 1 = Classes & Students, 2 = Analytics)
    var selectedTab by remember { mutableIntStateOf(0) }

    val googleSheetId by viewModel.googleSheetId.collectAsState()
    val googleWebAppUrl by viewModel.googleWebAppUrl.collectAsState()
    val googleAccessToken by viewModel.googleAccessToken.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val syncResult by viewModel.syncResult.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()
    val backupStatus by viewModel.backupStatus.collectAsState()

    val firebaseUrl by viewModel.firebaseUrl.collectAsState()
    val firebaseWorkspace by viewModel.firebaseWorkspace.collectAsState()
    val firebaseAuthToken by viewModel.firebaseAuthToken.collectAsState()
    val firebaseAutoSyncEnabled by viewModel.firebaseAutoSyncEnabled.collectAsState()
    val isFirebaseSyncing by viewModel.isFirebaseSyncing.collectAsState()
    val firebaseSyncStatus by viewModel.firebaseSyncStatus.collectAsState()
    val firebaseLastSyncTime by viewModel.firebaseLastSyncTime.collectAsState()
    val portalBaseUrl by viewModel.portalBaseUrl.collectAsState()

    val customCategories by viewModel.customCategories.collectAsState()
    val deletedCategories by viewModel.deletedCategories.collectAsState()
    val allChecklistItems by viewModel.allChecklistItems.collectAsState()

    var showGoogleSyncDialog by remember { mutableStateOf(false) }
    var showFirebaseSyncDialog by remember { mutableStateOf(false) }
    var showBackupRestoreDialog by remember { mutableStateOf(false) }
    var showAiLensDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val restoreFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                if (!content.isNullOrBlank()) {
                    viewModel.restoreBackupFromString(content)
                }
            } catch (e: Exception) {
                // handle
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                    .statusBarsPadding()
                    .padding(top = 4.dp, bottom = 4.dp)
            ) {
                // App Title & Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HowToReg,
                            contentDescription = "App Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Shop Attendance",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 13.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // AI Lens & Smart Search Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(
                                        listOf(Color(0xFF2563EB), Color(0xFF7C3AED))
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { showAiLensDialog = true }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .testTag("ai_lens_top_bar_trigger")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Lens & Smart Search",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Lens",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 9.5.sp
                                ),
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        // Firebase Cloud Sync Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFFF6F00).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { showFirebaseSyncDialog = true }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .testTag("firebase_sync_top_bar_trigger")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Firebase Auto Cloud Sync",
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Firebase",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    fontSize = 9.5.sp
                                ),
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        // Backup & Restore Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.85f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { showBackupRestoreDialog = true }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .testTag("backup_restore_top_bar_trigger")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Backup & Restore",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Backup",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    fontSize = 9.5.sp
                                ),
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        // Google Sheets Sync Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { showGoogleSyncDialog = true }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .testTag("sheets_sync_top_bar_trigger")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Backup,
                                contentDescription = "Google Sheets Sync",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Sheets",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 9.5.sp
                                ),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Class selector label and horizontal list
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DEPARTMENTS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp
                        )
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (allClasses.size > 1) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { showRearrangeDepartmentsDialog = true }
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                    .testTag("rearrange_departments_top_bar_trigger")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = "Rearrange Departments",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Rearrange",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // + Add Staff Button
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier
                                .clickable { showAddStudentDialog = true }
                                .testTag("top_bar_add_staff_trigger")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Staff",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Staff",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // + New Department Button
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                            modifier = Modifier
                                .clickable { showAddClassDialog = true }
                                .testTag("add_class_trigger")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "New Department",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Dept",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                if (allClasses.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No departments found. Add a department to begin!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp)
                    ) {
                        items(allClasses) { clazz ->
                            val isSelected = selectedClass?.id == clazz.id
                            val borderThemeColor = parseColor(clazz.colorHex)
                            val containerColor = if (isSelected) borderThemeColor else MaterialTheme.colorScheme.surface
                            val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

                            Card(
                                onClick = { viewModel.selectClass(clazz) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = containerColor),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("class_card_${clazz.id}"),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White else borderThemeColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = clazz.name,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = textColor,
                                            fontSize = 11.sp
                                        )
                                    )
                                    val staffInClassCount = allStudents.count { it.classId == clazz.id }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color.White.copy(alpha = 0.28f) else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.padding(vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "$staffInClassCount",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 9.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Backup & Auto-Restore Status Banner
            if (!backupStatus.isNullOrBlank() || !firebaseSyncStatus.isNullOrBlank()) {
                val activeMsg = firebaseSyncStatus ?: backupStatus ?: ""
                val isFirebaseMsg = !firebaseSyncStatus.isNullOrBlank()
                Surface(
                    color = if (isFirebaseMsg) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isFirebaseMsg) Icons.Default.CloudDone else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isFirebaseMsg) Color(0xFFE65100) else MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = activeMsg,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = if (isFirebaseMsg) Color(0xFFBF360C) else MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.clearBackupStatus()
                                viewModel.clearFirebaseStatus()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = if (isFirebaseMsg) Color(0xFFBF360C) else MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Main Dashboard Navigation Bar Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Attend", fontWeight = FontWeight.Bold, fontSize = 8.5.sp, maxLines = 1, softWrap = false) },
                    icon = { Icon(Icons.Default.HowToReg, contentDescription = null, modifier = Modifier.size(19.dp)) },
                    modifier = Modifier.testTag("tab_take_attendance")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { 
                        selectedTab = 1
                        selectedChecklistScopeId = CHECKLIST_SCOPE_ALL
                    },
                    text = { Text("Tasks", fontWeight = FontWeight.Bold, fontSize = 8.5.sp, maxLines = 1, softWrap = false) },
                    icon = { Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(19.dp)) },
                    modifier = Modifier.testTag("tab_checklist")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("T-Hisab", fontWeight = FontWeight.Bold, fontSize = 8.5.sp, maxLines = 1, softWrap = false) },
                    icon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(19.dp)) },
                    modifier = Modifier.testTag("tab_thisab")
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Staff", fontWeight = FontWeight.Bold, fontSize = 8.5.sp, maxLines = 1, softWrap = false) },
                    icon = { Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(19.dp)) },
                    modifier = Modifier.testTag("tab_classes_students")
                )
                Tab(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    text = { Text("Analytics", fontWeight = FontWeight.Bold, fontSize = 8.5.sp, maxLines = 1, softWrap = false) },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(19.dp)) },
                    modifier = Modifier.testTag("tab_analytics")
                )
                Tab(
                    selected = selectedTab == 5,
                    onClick = { selectedTab = 5 },
                    text = { Text("Payroll", fontWeight = FontWeight.Bold, fontSize = 8.5.sp, maxLines = 1, softWrap = false) },
                    icon = { Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(19.dp)) },
                    modifier = Modifier.testTag("tab_payroll")
                )
            }

            // Central tab body container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (selectedTab == 1) {
                    // Employee & General Shop Checklist
                    ChecklistView(
                        checklistItems = allChecklistItems,
                        allStudents = allStudents,
                        initialScopeId = selectedChecklistScopeId,
                        onScopeSelected = { selectedChecklistScopeId = it },
                        onToggleItem = { viewModel.toggleChecklistItem(it) },
                        onAddItem = { studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson ->
                            viewModel.addChecklistItem(
                                studentId = studentId,
                                title = title,
                                category = category,
                                priority = "",
                                timeDue = "",
                                notes = notes,
                                assignedStaffName = staffName,
                                reminderTimestamp = reminderTimestamp,
                                itemType = itemType,
                                subtasksJson = subtasksJson
                            )
                        },
                        onUpdateItem = { viewModel.updateChecklistItem(it) },
                        onDeleteItem = { item -> viewModel.deleteChecklistItem(item, context) },
                        onSetReminder = { item, reminderTime -> viewModel.setChecklistReminder(context, item, reminderTime) },
                        onCancelReminder = { item -> viewModel.cancelChecklistReminder(context, item) },
                        onResetScope = { studentId ->
                            if (studentId == null) viewModel.resetAllChecklist()
                            else viewModel.resetChecklistForScope(studentId)
                        },
                        onRestoreDefaults = { studentId, studentName ->
                            viewModel.restoreDefaultChecklist(studentId, studentName)
                        },
                        onMoveUp = { viewModel.moveChecklistItemUp(it) },
                        onMoveDown = { viewModel.moveChecklistItemDown(it) },
                        onSwapItems = { item1, item2 -> viewModel.swapChecklistItems(item1, item2) },
                        onMoveToPosition = { item, pos -> viewModel.moveChecklistItemToPosition(item, pos) },
                        onReorderItems = { orderedList -> viewModel.reorderChecklistSubset(orderedList) },
                        onToggleSubtask = { item, subtaskId -> viewModel.toggleSubtask(item, subtaskId) },
                        onAddSubtask = { item, title -> viewModel.addSubtaskToItem(item, title) },
                        onDeleteSubtask = { item, subtaskId -> viewModel.deleteSubtaskFromItem(item, subtaskId) },
                        onUpdateDailyComment = { item, comment -> viewModel.updateDailyComment(item, comment) }
                    )
                } else if (selectedTab == 2) {
                    // T-HISAB View is accessible across all staff
                    HisabView(
                        allStudents = orderedHisabStudents,
                        allClasses = allClasses,
                        allProducts = allHisabProducts,
                        selectedStudent = selectedHisabStudent,
                        ledgerEntries = hisabEntriesForSelectedStudent,
                        customCategories = customCategories,
                        deletedCategories = deletedCategories,
                        onSelectStudent = { viewModel.selectHisabStudent(it) },
                        onAddStudent = { showAddStudentDialog = true },
                        onAddProduct = { name, category, rate -> viewModel.addHisabProduct(name, category, rate) },
                        onUpdateProduct = { viewModel.updateHisabProduct(it) },
                        onDeleteProduct = { viewModel.deleteHisabProduct(it) },
                        onRestoreDefaultProducts = { viewModel.restoreDefaultHisabProducts() },
                        onAddCategory = { viewModel.addCustomCategory(it) },
                        onRenameCategory = { oldCat, newCat -> viewModel.renameCategory(oldCat, newCat) },
                        onDeleteCategory = { catName, deleteItems, fallback -> viewModel.deleteCategory(catName, deleteItems, fallback) },
                        onAddSalesEntry = { studentId, dateString, breakdownJson, totalPieces, totalEarned, notes ->
                            viewModel.addSalesLedgerEntry(studentId, dateString, breakdownJson, totalPieces, totalEarned, notes)
                        },
                        onAddAdvanceEntry = { studentId, dateString, advanceAmount, notes ->
                            viewModel.addAdvanceLedgerEntry(studentId, dateString, advanceAmount, notes)
                        },
                        onUpdateEntry = { viewModel.updateHisabEntry(it) },
                        onDeleteEntry = { viewModel.deleteHisabEntry(it) },
                        onMoveStudentUp = { viewModel.moveHisabStudentUp(it) },
                        onMoveStudentDown = { viewModel.moveHisabStudentDown(it) },
                        onSortStudents = { viewModel.sortHisabStudents(it) },
                        onReorderStudents = { viewModel.reorderHisabStudents(it) },
                        portalBaseUrl = portalBaseUrl,
                        firebaseUrl = firebaseUrl,
                        firebaseWorkspace = firebaseWorkspace,
                        isFirebaseSyncing = isFirebaseSyncing,
                        firebaseLastSyncTime = firebaseLastSyncTime,
                        onSavePortalBaseUrl = { viewModel.setPortalBaseUrl(it) },
                        onSyncToFirebaseNow = { viewModel.syncToFirebase(showFeedback = true) }
                    )
                } else if (selectedClass == null) {
                    // Empty state warning
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Department Selected",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please select or create a department above to start tracking attendance.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showAddClassDialog = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Create My First Department")
                        }
                    }
                } else {
                    val activeClass = selectedClass!!
                    when (selectedTab) {
                        0 -> MarkAttendanceView(
                            activeClass = activeClass,
                            dateStr = selectedDate,
                            students = currentStudents,
                            allStudentsCount = allStudents.size,
                            records = currentAttendanceRecords,
                            allShifts = allShifts,
                            allChecklistItems = allChecklistItems,
                            onOpenCalendar = { showDatePicker = true },
                            onPreviousDay = { viewModel.previousDay() },
                            onNextDay = { viewModel.nextDay() },
                            onMark = { studentId, status -> viewModel.markAttendance(studentId, status) },
                            onUpdateCheckIn = { studentId, checkIn -> viewModel.updateCheckInTime(studentId, checkIn) },
                            onUpdateCheckOut = { studentId, checkOut -> viewModel.updateCheckOutTime(studentId, checkOut) },
                            onUpdateNotes = { studentId, notes -> viewModel.updateNotes(studentId, notes) },
                            onSaveRemarksAndOvertime = { studentId, notes, reason, hasOt, inT, outT ->
                                viewModel.saveRemarksAndOvertime(
                                    studentId = studentId,
                                    notes = notes,
                                    reason = reason,
                                    hasOvertime = hasOt,
                                    checkInTime = inT,
                                    checkOutTime = outT
                                )
                            },
                            onBulkMark = { status -> viewModel.bulkMark(status) },
                            onAddStudentTrigger = { showAddStudentDialog = true },
                            onViewProfile = { showEmployeeProfileForStudent = it },
                            onViewReport = { showEmployeeReportForStudent = it },
                            onViewChecklist = { student ->
                                selectedChecklistScopeId = student.id
                                selectedTab = 1
                            },
                            onViewHisab = { student ->
                                viewModel.selectHisabStudent(student)
                                selectedTab = 2
                            },
                            onToggleChecklistItem = { item ->
                                viewModel.toggleChecklistItem(item)
                            },
                            onAddChecklistItem = { studentId, title, itemType, category, subtasksJson ->
                                val staffName = currentStudents.find { it.id == studentId }?.displayName ?: ""
                                viewModel.addChecklistItem(
                                    studentId = studentId,
                                    title = title,
                                    category = category,
                                    priority = "",
                                    timeDue = "",
                                    assignedStaffName = staffName,
                                    itemType = itemType,
                                    subtasksJson = subtasksJson
                                )
                            },
                            onToggleSubtask = { item, subtaskId ->
                                viewModel.toggleSubtask(item, subtaskId)
                            },
                            onRestoreDefaultItemsForStudent = { studentId, studentName ->
                                viewModel.restoreDefaultChecklist(studentId, studentName)
                            },
                            onUpdateChecklistItem = { updatedItem ->
                                viewModel.updateChecklistItem(updatedItem)
                            },
                            onDeleteChecklistItem = { itemToDelete ->
                                viewModel.deleteChecklistItem(itemToDelete, context)
                            }
                        )
                        3 -> ClassesStudentsView(
                            activeClass = activeClass,
                            students = currentStudents,
                            allStudentsCount = allStudents.size,
                            rates = studentAttendanceRates,
                            allShifts = allShifts,
                            allChecklistItems = allChecklistItems,
                            onAddStudent = { showAddStudentDialog = true },
                            onEditStudent = { showEditStudentForStudent = it },
                            onDeleteStudent = { viewModel.deleteStudent(it) },
                            onDeleteClass = { viewModel.deleteClass(activeClass) },
                            onEditClass = { name, color -> viewModel.updateClass(activeClass, name, color) },
                            onRearrangeClasses = { showRearrangeDepartmentsDialog = true },
                            onAssignShift = { showAssignShiftForStudent = it },
                            onViewProfile = { showEmployeeProfileForStudent = it },
                            onViewReport = { showEmployeeReportForStudent = it },
                            onViewChecklist = { student ->
                                selectedChecklistScopeId = student.id
                                selectedTab = 1
                            },
                            onViewHisab = { student ->
                                viewModel.selectHisabStudent(student)
                                selectedTab = 2
                            },
                            onAddShift = { name, timings -> viewModel.addShift(name, timings) },
                            onUpdateShift = { viewModel.updateShift(it) },
                            onDeleteShift = { viewModel.deleteShift(it) },
                            portalBaseUrl = portalBaseUrl,
                            firebaseUrl = firebaseUrl,
                            firebaseWorkspace = firebaseWorkspace,
                            onSavePortalBaseUrl = { viewModel.setPortalBaseUrl(it) }
                        )
                        4 -> AnalyticsView(
                            activeClass = activeClass,
                            stats = classStats,
                            students = currentStudents,
                            allRecords = allRecordsForSelectedClass,
                            allShifts = allShifts,
                            onUpdateRecord = { studentId, dateString, status, inT, outT, notes, reason ->
                                viewModel.updateSpecificAttendanceRecord(
                                    classId = activeClass.id,
                                    studentId = studentId,
                                    dateString = dateString,
                                    status = status,
                                    checkInTime = inT,
                                    checkOutTime = outT,
                                    notes = notes,
                                    reason = reason
                                )
                            },
                            onViewReport = { showEmployeeReportForStudent = it }
                        )
                        5 -> PayrollView(
                            activeClass = activeClass,
                            students = currentStudents,
                            allClasses = allClasses,
                            onSelectClass = { viewModel.selectClass(it) },
                            allRecords = allRecordsForSelectedClass,
                            allHisabLedgerEntries = allHisabLedgerEntries,
                            onRecordAdvance = { studentId, dateString, amount, notes ->
                                viewModel.addAdvanceLedgerEntry(studentId, dateString, amount, notes)
                            },
                            onUpdateRecord = { studentId, dateString, status, inT, outT, notes, reason ->
                                viewModel.updateSpecificAttendanceRecord(
                                    classId = activeClass.id,
                                    studentId = studentId,
                                    dateString = dateString,
                                    status = status,
                                    checkInTime = inT,
                                    checkOutTime = outT,
                                    notes = notes,
                                    reason = reason
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    // DatePicker Dialog
    if (showDatePicker) {
        val initialMillis = remember(selectedDate) {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                sdf.parse(selectedDate)?.time ?: System.currentTimeMillis()
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val dateStr = sdf.format(Date(selectedMillis))
                            viewModel.selectDate(dateStr)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Firebase Cloud Sync Dialog Trigger
    if (showFirebaseSyncDialog) {
        FirebaseSyncDialog(
            firebaseUrl = firebaseUrl,
            workspace = firebaseWorkspace,
            authToken = firebaseAuthToken,
            autoSyncEnabled = firebaseAutoSyncEnabled,
            portalBaseUrl = portalBaseUrl,
            isSyncing = isFirebaseSyncing,
            syncStatus = firebaseSyncStatus,
            lastSyncTime = firebaseLastSyncTime,
            onDismiss = { showFirebaseSyncDialog = false },
            onSaveConfig = { url, ws, token, autoSync ->
                viewModel.setFirebaseConfig(url, ws, token, autoSync)
            },
            onSavePortalBaseUrl = { viewModel.setPortalBaseUrl(it) },
            onSyncNow = {
                viewModel.syncToFirebase()
            },
            onRestoreNow = {
                viewModel.restoreFromFirebase()
            },
            onTestConnection = {
                viewModel.testFirebaseConnection()
            },
            onClearStatus = {
                viewModel.clearFirebaseStatus()
            }
        )
    }

    // Google Sheets Sync Dialog Trigger
    if (showGoogleSyncDialog) {
        GoogleSheetsSyncDialog(
            sheetId = googleSheetId,
            webAppUrl = googleWebAppUrl,
            accessToken = googleAccessToken,
            isSyncing = isSyncing,
            syncResult = syncResult,
            lastSyncTime = lastSyncTime,
            departmentName = selectedClass?.name ?: "Selected Department",
            onDismiss = { showGoogleSyncDialog = false },
            onSaveConfig = { sheetId, webAppUrl, token ->
                viewModel.saveGoogleSheetsConfig(sheetId, webAppUrl, token)
            },
            onSync = { useWebApp ->
                viewModel.syncSelectedClassToGoogleSheets(useWebApp)
            },
            onRestore = { useWebApp ->
                viewModel.restoreFromGoogleSheets(useWebApp)
            },
            onCreateSheet = { title ->
                viewModel.createGoogleSheet(title)
            },
            onClearResult = {
                viewModel.clearSyncResult()
            }
        )
    }

    // Backup & Restore Dialog Trigger
    if (showBackupRestoreDialog) {
        BackupRestoreDialog(
            onDismiss = { showBackupRestoreDialog = false },
            onCreateBackup = {
                viewModel.exportAndShareBackup(context)
            },
            onRestoreAutoBackup = {
                viewModel.restoreLatestAutoBackup(context)
            },
            onSelectBackupFile = {
                restoreFileLauncher.launch("*/*")
            },
            onOpenFirebaseSync = {
                showBackupRestoreDialog = false
                showFirebaseSyncDialog = true
            }
        )
    }

    // AI Lens & Smart Search Dialog Trigger
    if (showAiLensDialog) {
        AiLensDialog(
            allClasses = allClasses,
            allStudents = allStudents,
            allShifts = allShifts,
            allHisabProducts = allHisabProducts,
            allHisabLedgerEntries = allHisabLedgerEntries,
            allChecklistItems = allChecklistItems,
            todayRecords = currentAttendanceRecords,
            selectedClass = selectedClass,
            onDismiss = { showAiLensDialog = false },
            onSelectDepartment = { dept ->
                viewModel.selectClass(dept)
            },
            onSelectStudentAttendance = { student ->
                val dept = allClasses.firstOrNull { it.id == student.classId }
                if (dept != null) viewModel.selectClass(dept)
                selectedTab = 0
            },
            onSelectStudentChecklist = { student ->
                selectedChecklistScopeId = student.id
                selectedTab = 1
            },
            onSelectStudentHisab = { student ->
                viewModel.selectHisabStudent(student)
                selectedTab = 2
            },
            onSelectStudentReport = { student ->
                showEmployeeReportForStudent = student
            },
            onTriggerAddStaff = { showAddStudentDialog = true },
            onTriggerAddDepartment = { showAddClassDialog = true },
            onTriggerBackup = { showBackupRestoreDialog = true },
            onTriggerFirebaseSync = { showFirebaseSyncDialog = true },
            onTriggerGoogleSync = { showGoogleSyncDialog = true },
            onTriggerChecklistTab = { selectedTab = 1 },
            onTriggerHisabTab = { selectedTab = 2 },
            onTriggerStaffTab = { selectedTab = 3 }
        )
    }

    if (showRearrangeDepartmentsDialog) {
        RearrangeDepartmentsDialog(
            departments = allClasses,
            onDismiss = { showRearrangeDepartmentsDialog = false },
            onMoveUp = { viewModel.moveClassUp(it) },
            onMoveDown = { viewModel.moveClassDown(it) },
            onSort = { viewModel.sortClasses(it) }
        )
    }

    // Add Class Dialog
    if (showAddClassDialog) {
        var className by remember { mutableStateOf("") }
        val colors = listOf("#4F46E5", "#10B981", "#EF4444", "#F59E0B", "#06B6D4", "#8B5CF6", "#F43F5E")
        var selectedColor by remember { mutableStateOf(colors[0]) }

        AlertDialog(
            onDismissRequest = { showAddClassDialog = false },
            title = { Text("Create New Department", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = className,
                        onValueChange = { className = it },
                        label = { Text("Department Name") },
                        placeholder = { Text("e.g. Sales Counter") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_class_name")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Pick Department Accent Color",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        colors.forEach { hex ->
                            val color = parseColor(hex)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { selectedColor = hex }
                                    .padding(2.dp)
                            ) {
                                if (selectedColor == hex) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.5f))
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (className.isNotBlank()) {
                            viewModel.createClass(className.trim(), selectedColor)
                            showAddClassDialog = false
                        }
                    },
                    modifier = Modifier.testTag("submit_create_class")
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddClassDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Student Dialog
    if (showAddStudentDialog) {
        var studentName by remember { mutableStateOf("") }
        var rollNumber by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var selectedDepartmentId by remember { mutableStateOf(selectedClass?.id ?: allClasses.firstOrNull()?.id) }
        var showDeptSelectorDialog by remember { mutableStateOf(false) }
        var selectedShiftId by remember { mutableStateOf<Long?>(null) }
        var showShiftSelectorDialog by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddStudentDialog = false },
            title = { Text("Add New Staff Member", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val chosenDept = allClasses.find { it.id == selectedDepartmentId } ?: selectedClass
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (allClasses.size > 1) showDeptSelectorDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Department",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = chosenDept?.name ?: "Select Department",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (allClasses.size > 1) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Change Department",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it.replace("\r", "").replace("\n", "") },
                        label = { Text("Staff Full Name") },
                        placeholder = { Text("e.g. Charlie Brown") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_student_name")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = rollNumber,
                        onValueChange = { rollNumber = it },
                        label = { Text("Employee ID") },
                        placeholder = { Text("e.g. EMP-01") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_student_roll")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone / WhatsApp (Optional)") },
                        placeholder = { Text("e.g. +91 98765 43210") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        trailingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_student_phone")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address (Optional)") },
                        placeholder = { Text("e.g. staff@shop.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        trailingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_student_email")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val chosenShift = allShifts.find { it.id == selectedShiftId }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showShiftSelectorDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Assigned Shift",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = chosenShift?.let { "${it.name} (${it.timings})" } ?: "No Shift (Unassigned)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (chosenShift == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Select Shift",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (studentName.isNotBlank() && rollNumber.isNotBlank()) {
                            viewModel.addStudent(
                                name = studentName.cleanEmployeeName(),
                                rollNumber = rollNumber.trim(),
                                phone = phone.trim(),
                                email = email.trim(),
                                shiftId = selectedShiftId,
                                targetClassId = selectedDepartmentId
                            )
                            showAddStudentDialog = false
                        }
                    },
                    modifier = Modifier.testTag("submit_add_student")
                ) {
                    Text("Add Staff Member")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStudentDialog = false }) {
                    Text("Cancel")
                }
            }
        )

        if (showDeptSelectorDialog) {
            AlertDialog(
                onDismissRequest = { showDeptSelectorDialog = false },
                title = { Text("Select Department", fontWeight = FontWeight.Bold) },
                text = {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(allClasses) { dept ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedDepartmentId = dept.id
                                        showDeptSelectorDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedDepartmentId == dept.id,
                                    onClick = {
                                        selectedDepartmentId = dept.id
                                        showDeptSelectorDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(dept.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showDeptSelectorDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showShiftSelectorDialog) {
            AlertDialog(
                onDismissRequest = { showShiftSelectorDialog = false },
                title = { Text("Select Employee Shift", fontWeight = FontWeight.Bold) },
                text = {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedShiftId = null
                                        showShiftSelectorDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selectedShiftId == null, onClick = {
                                    selectedShiftId = null
                                    showShiftSelectorDialog = false
                                })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("No Shift (Unassigned)", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        items(allShifts) { shift ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedShiftId = shift.id
                                        showShiftSelectorDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selectedShiftId == shift.id, onClick = {
                                    selectedShiftId = shift.id
                                    showShiftSelectorDialog = false
                                })
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(shift.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                                    Text(shift.timings, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showShiftSelectorDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }

    // Edit Student Details Dialog
    if (showEditStudentForStudent != null) {
        val student = showEditStudentForStudent!!
        var editName by remember(student) { mutableStateOf(student.name) }
        var editRollNumber by remember(student) { mutableStateOf(student.rollNumber) }
        var editPhone by remember(student) { mutableStateOf(student.phone) }
        var editEmail by remember(student) { mutableStateOf(student.email) }
        var selectedDepartmentId by remember(student) { mutableStateOf(student.classId) }
        var showDeptSelectorDialog by remember { mutableStateOf(false) }
        var selectedShiftId by remember(student) { mutableStateOf(student.shiftId) }
        var showShiftSelectorDialog by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showEditStudentForStudent = null },
            title = { Text("Edit Staff Member Details", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val chosenDept = allClasses.find { it.id == selectedDepartmentId } ?: allClasses.firstOrNull { it.id == student.classId }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (allClasses.size > 1) showDeptSelectorDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (chosenDept != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(parseColor(chosenDept.colorHex))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                }
                                Column {
                                    Text(
                                        text = "Department",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = chosenDept?.name ?: "Select Department",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            if (allClasses.size > 1) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Change Department",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it.replace("\r", "").replace("\n", "") },
                        label = { Text("Staff Full Name") },
                        placeholder = { Text("e.g. Charlie Brown") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_student_name")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editRollNumber,
                        onValueChange = { editRollNumber = it },
                        label = { Text("Employee ID") },
                        placeholder = { Text("e.g. EMP-01") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_student_roll")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone / WhatsApp (Optional)") },
                        placeholder = { Text("e.g. +91 98765 43210") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        trailingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_student_phone")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email Address (Optional)") },
                        placeholder = { Text("e.g. staff@shop.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        trailingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_student_email")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val chosenShift = allShifts.find { it.id == selectedShiftId }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showShiftSelectorDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Assigned Shift",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = chosenShift?.let { "${it.name} (${it.timings})" } ?: "No Shift (Unassigned)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (chosenShift == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Select Shift",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank() && editRollNumber.isNotBlank()) {
                            val deptChanged = selectedDepartmentId != student.classId
                            viewModel.updateStudent(
                                student.copy(
                                    name = editName.cleanEmployeeName(),
                                    rollNumber = editRollNumber.trim(),
                                    phone = editPhone.trim(),
                                    email = editEmail.trim(),
                                    shiftId = selectedShiftId,
                                    classId = selectedDepartmentId
                                )
                            )
                            if (deptChanged) {
                                val targetDept = allClasses.find { it.id == selectedDepartmentId }
                                if (targetDept != null) {
                                    viewModel.selectClass(targetDept)
                                }
                                Toast.makeText(
                                    context,
                                    "${editName.trim()} moved to ${targetDept?.name ?: "new"} department",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            showEditStudentForStudent = null
                        }
                    },
                    modifier = Modifier.testTag("submit_edit_student")
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditStudentForStudent = null }) {
                    Text("Cancel")
                }
            }
        )

        if (showDeptSelectorDialog) {
            AlertDialog(
                onDismissRequest = { showDeptSelectorDialog = false },
                title = { Text("Select Department", fontWeight = FontWeight.Bold) },
                text = {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(allClasses) { dept ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedDepartmentId = dept.id
                                        showDeptSelectorDialog = false
                                    }
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedDepartmentId == dept.id,
                                    onClick = {
                                        selectedDepartmentId = dept.id
                                        showDeptSelectorDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(parseColor(dept.colorHex))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(dept.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showDeptSelectorDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showShiftSelectorDialog) {
            AlertDialog(
                onDismissRequest = { showShiftSelectorDialog = false },
                title = { Text("Select Employee Shift", fontWeight = FontWeight.Bold) },
                text = {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedShiftId = null
                                        showShiftSelectorDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selectedShiftId == null, onClick = {
                                    selectedShiftId = null
                                    showShiftSelectorDialog = false
                                })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("No Shift (Unassigned)", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        items(allShifts) { shift ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedShiftId = shift.id
                                        showShiftSelectorDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selectedShiftId == shift.id, onClick = {
                                    selectedShiftId = shift.id
                                    showShiftSelectorDialog = false
                                })
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(shift.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                                    Text(shift.timings, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showShiftSelectorDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }

    // Direct Assign Shift Dialog
    if (showAssignShiftForStudent != null) {
        val student = showAssignShiftForStudent!!
        var tempShiftId by remember(student) { mutableStateOf(student.shiftId) }

        AlertDialog(
            onDismissRequest = { showAssignShiftForStudent = null },
            title = { Text("Assign Shift for ${student.name}", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { tempShiftId = null }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = tempShiftId == null, onClick = { tempShiftId = null })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("No Shift (Unassigned)", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    items(allShifts) { shift ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { tempShiftId = shift.id }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = tempShiftId == shift.id, onClick = { tempShiftId = shift.id })
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(shift.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                                Text(shift.timings, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateStudentShift(student, tempShiftId)
                        showAssignShiftForStudent = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAssignShiftForStudent = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEmployeeReportForStudent != null && selectedClass != null) {
        EmployeeMonthlyReportDialog(
            student = showEmployeeReportForStudent!!,
            activeClass = selectedClass!!,
            allStudents = currentStudents,
            records = allRecordsForSelectedClass,
            allShifts = allShifts,
            onDismiss = { showEmployeeReportForStudent = null },
            onSelectStudent = { showEmployeeReportForStudent = it },
            onUpdateRecord = { studentId, dateString, status, checkIn, checkOut, notes, reason ->
                viewModel.updateSpecificAttendanceRecord(
                    classId = selectedClass!!.id,
                    studentId = studentId,
                    dateString = dateString,
                    status = status,
                    checkInTime = checkIn,
                    checkOutTime = checkOut,
                    notes = notes,
                    reason = reason
                )
            }
        )
    }

    // 360-Degree Complete Employee Profile Dialog (Attendance, T-Hisab, Payroll, Tasks)
    if (showEmployeeProfileForStudent != null && selectedClass != null) {
        val targetStudent = showEmployeeProfileForStudent!!
        val studentShift = allShifts.find { it.id == targetStudent.shiftId }
        EmployeeProfileDialog(
            student = targetStudent,
            activeClass = selectedClass!!,
            shift = studentShift,
            allRecords = allRecordsForSelectedClass,
            allHisabEntries = allHisabLedgerEntries,
            allChecklistItems = allChecklistItems,
            onDismiss = { showEmployeeProfileForStudent = null },
            onOpenAttendanceTab = {
                showEmployeeProfileForStudent = null
                selectedTab = 0
            },
            onOpenHisabTab = { student ->
                showEmployeeProfileForStudent = null
                viewModel.selectHisabStudent(student)
                selectedTab = 2
            },
            onOpenPayrollTab = { student ->
                showEmployeeProfileForStudent = null
                selectedTab = 5
            },
            onOpenChecklistTab = { student ->
                showEmployeeProfileForStudent = null
                selectedChecklistScopeId = student.id
                selectedTab = 1
            },
            onScheduleSmsForTask = { taskItem ->
                showEmployeeProfileForStudent = null
                selectedChecklistScopeId = taskItem.studentId
                selectedTab = 1
            },
            onReorderTasks = { orderedList ->
                viewModel.reorderChecklistSubset(orderedList)
            }
        )
    }
}

@Composable
fun MarkAttendanceView(
    activeClass: ClassEntity,
    dateStr: String,
    students: List<StudentEntity>,
    allStudentsCount: Int = 0,
    records: List<com.example.data.AttendanceRecordEntity> = emptyList(),
    allShifts: List<ShiftEntity> = emptyList(),
    allChecklistItems: List<ChecklistItemEntity> = emptyList(),
    onOpenCalendar: () -> Unit,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onMark: (Long, String) -> Unit,
    onUpdateCheckIn: (Long, String) -> Unit,
    onUpdateCheckOut: (Long, String) -> Unit,
    onUpdateNotes: (Long, String) -> Unit,
    onSaveRemarksAndOvertime: (studentId: Long, notes: String, reason: String, hasOvertime: Boolean, inTime: String, outTime: String) -> Unit = { _, _, _, _, _, _ -> },
    onBulkMark: (String) -> Unit,
    onAddStudentTrigger: () -> Unit,
    onViewProfile: (StudentEntity) -> Unit = {},
    onViewReport: (StudentEntity) -> Unit = {},
    onViewChecklist: (StudentEntity) -> Unit = {},
    onViewHisab: (StudentEntity) -> Unit = {},
    onToggleChecklistItem: (ChecklistItemEntity) -> Unit = {},
    onAddChecklistItem: (studentId: Long, title: String, itemType: String, category: String, subtasksJson: String) -> Unit = { _, _, _, _, _ -> },
    onRestoreDefaultItemsForStudent: (studentId: Long, studentName: String) -> Unit = { _, _ -> },
    onUpdateChecklistItem: (ChecklistItemEntity) -> Unit = {},
    onDeleteChecklistItem: (ChecklistItemEntity) -> Unit = {},
    onToggleSubtask: (ChecklistItemEntity, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val recordsList = records

    // State for Time Edit Dialog
    var showTimeDialogForStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var editIsCheckIn by remember { mutableStateOf(true) } // true: Check-In, false: Check-Out
    var customTimeValue by remember { mutableStateOf("") }

    // State for Remarks & Reason Dialog
    var showRemarksDialogForStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var customRemarksValue by remember { mutableStateOf("") }
    var customReasonValue by remember { mutableStateOf("") }
    var customHasOvertime by remember { mutableStateOf(false) }
    var customOtInTime by remember { mutableStateOf("06:00 PM") }
    var customOtOutTime by remember { mutableStateOf("09:30 PM") }

    // State for Checklist / Task Quick Add Dialog
    var showAddItemDialogForStudent by remember { mutableStateOf<Pair<StudentEntity, String>?>(null) } // Pair(student, "CHECKLIST" or "TASK")

    // State for Editing Item Directly within Take Attendance Tab
    var editingItemForAttendance by remember { mutableStateOf<ChecklistItemEntity?>(null) }
    var itemToDeleteForAttendance by remember { mutableStateOf<ChecklistItemEntity?>(null) }
    var itemForScheduleSmsForAttendance by remember { mutableStateOf<ChecklistItemEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Active Date Selector Header with Forward & Backward controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Backward / Previous Date Button
                IconButton(
                    onClick = onPreviousDay,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
                        .testTag("prev_date_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Day (Move Date Backward)",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Middle Date Display & Clickable area
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenCalendar() }
                        .padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Select Date",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Attendance Date",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = AttendanceViewModel.formatDisplayDate(dateStr),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Right action buttons: Forward & Calendar Picker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Forward / Next Date Button
                    IconButton(
                        onClick = onNextDay,
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
                            .testTag("next_date_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Day (Move Date Forward)",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Calendar Picker Button
                    IconButton(
                        onClick = onOpenCalendar,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                            .size(40.dp)
                            .testTag("calendar_trigger")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Pick Calendar Date",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (students.isEmpty()) {
            // Empty state placeholder
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No Staff in ${activeClass.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (allStudentsCount > 0)
                        "This department currently has 0 staff members. You have $allStudentsCount staff member(s) in other departments — tap a department tab above to switch to them, or add staff to ${activeClass.name} below."
                    else
                        "Add staff members to this department first so you can track their daily attendance and work hours.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onAddStudentTrigger, shape = RoundedCornerShape(8.dp)) {
                    Text("+ Add Staff Member")
                }
            }
        } else {
            // Student daily marking list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(students) { student ->
                    val record = recordsList.find { it.studentId == student.id }
                    val status = record?.status ?: "UNMARKED"
                    val employeeAllItems = remember(allChecklistItems, student.id, student.displayName, student.name) {
                        allChecklistItems.filter {
                            it.studentId == student.id ||
                            (it.assignedStaffName.isNotBlank() && (it.assignedStaffName.equals(student.displayName, ignoreCase = true) || it.assignedStaffName.equals(student.name, ignoreCase = true)))
                        }
                    }
                    val employeeChecklist = remember(employeeAllItems) {
                        employeeAllItems.filter { it.isChecklist }
                            .sortedWith(compareBy<ChecklistItemEntity> { it.isDoneToday }.thenBy { it.displayOrder }.thenBy { it.id })
                    }
                    val employeeTasks = remember(employeeAllItems) {
                        employeeAllItems.filter { it.isTask }
                            .sortedWith(compareBy<ChecklistItemEntity> { it.isDoneToday }.thenBy { it.displayOrder }.thenBy { it.id })
                    }
                    val pendingTasksCount = remember(employeeAllItems) {
                        employeeAllItems.count { !it.isCompleted }
                    }
                    var isChecklistExpanded by rememberSaveable(student.id) { mutableStateOf(false) }
                    var isTasksExpanded by rememberSaveable(student.id) { mutableStateOf(false) }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            // Staff Info Header: Avatar, Full Name & ID, Shift, Quick Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onViewProfile(student) }
                                        .padding(vertical = 1.dp, horizontal = 1.dp)
                                        .testTag("employee_profile_click_${student.id}"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val cleanStaffName = student.displayName
                                    // Dynamic letter badge representing initials
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(parseColor(activeClass.colorHex).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cleanStaffName.take(1).uppercase(Locale.getDefault()),
                                            color = parseColor(activeClass.colorHex),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = cleanStaffName,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                            ) {
                                                Text(
                                                    text = "Profile ↗",
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(1.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Emp ID: ${student.rollNumber.trim()}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            val studentShift = allShifts.find { it.id == student.shiftId }
                                            if (studentShift != null) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "• ⏰ ${studentShift.name.cleanEmployeeName()}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                                    color = MaterialTheme.colorScheme.primary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            if (pendingTasksCount > 0) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "• 📋 $pendingTasksCount pending",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color(0xFFDC2626)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    // 1. Checklist / Tasks Icon with Pending Count Badge
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconButton(
                                            onClick = { onViewChecklist(student) },
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(
                                                    if (pendingTasksCount > 0) Color(0xFF7C3AED).copy(alpha = 0.18f) else Color(0xFF7C3AED).copy(alpha = 0.10f),
                                                    shape = RoundedCornerShape(6.dp)
                                                )
                                                .testTag("employee_checklist_mark_${student.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Checklist,
                                                contentDescription = "Checklist & Tasks for ${student.name} ($pendingTasksCount pending)",
                                                tint = Color(0xFF7C3AED),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        if (pendingTasksCount > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = 3.dp, y = (-3).dp)
                                                    .background(
                                                        color = Color(0xFFDC2626),
                                                        shape = CircleShape
                                                    )
                                                    .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                                    .defaultMinSize(minWidth = 14.dp, minHeight = 14.dp)
                                                    .padding(horizontal = 2.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = if (pendingTasksCount > 99) "99+" else "$pendingTasksCount",
                                                    color = Color.White,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    lineHeight = 9.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }

                                    // 2. T-HISAB / Ledger & Salary Icon
                                    IconButton(
                                        onClick = { onViewHisab(student) },
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(Color(0xFF059669).copy(alpha = 0.10f), shape = RoundedCornerShape(6.dp))
                                            .testTag("employee_thisab_mark_${student.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = "T-HISAB & Salary for ${student.name}",
                                            tint = Color(0xFF059669),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    // 3. Analytics / Full Monthly Report Icon
                                    IconButton(
                                        onClick = { onViewReport(student) },
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f), shape = RoundedCornerShape(6.dp))
                                            .testTag("employee_report_mark_${student.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Assessment,
                                            contentDescription = "Full Monthly Report & Analytics for ${student.name}",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(5.dp))

                            // Status Selector row: A, P, HD MOR, HD EVE
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AttendanceButton(
                                    label = "A",
                                    sign = "✗",
                                    activeColor = Color(0xFFC62828),
                                    isActive = status == "A" || status == "ABSENT",
                                    onClick = { 
                                        if (status == "A" || status == "ABSENT") {
                                            onMark(student.id, "UNMARKED")
                                        } else {
                                            onMark(student.id, "A")
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .testTag("mark_a_${student.id}")
                                )

                                AttendanceButton(
                                    label = "P",
                                    sign = "✓",
                                    activeColor = Color(0xFF2E7D32),
                                    isActive = status == "P" || status == "PRESENT",
                                    onClick = { 
                                        if (status == "P" || status == "PRESENT") {
                                            onMark(student.id, "UNMARKED")
                                        } else {
                                            onMark(student.id, "P")
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .testTag("mark_p_${student.id}")
                                )

                                AttendanceButton(
                                    label = "HD MOR",
                                    activeColor = Color(0xFFE65100),
                                    isActive = status == "HD MOR" || status == "HALF-DAY",
                                    onClick = { 
                                        if (status == "HD MOR" || status == "HALF-DAY") {
                                            onMark(student.id, "UNMARKED")
                                        } else {
                                            onMark(student.id, "HD MOR")
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1.35f)
                                        .testTag("mark_hd_mor_${student.id}")
                                )

                                AttendanceButton(
                                    label = "HD EVE",
                                    activeColor = Color(0xFF7B1FA2),
                                    isActive = status == "HD EVE",
                                    onClick = { 
                                        if (status == "HD EVE") {
                                            onMark(student.id, "UNMARKED")
                                        } else {
                                            onMark(student.id, "HD EVE")
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1.35f)
                                        .testTag("mark_hd_eve_${student.id}")
                                )
                            }

                            // Show Incoming and Outgoing Times if marked Present/Half-Day
                            if (status == "P" || status == "PRESENT" || status == "HD MOR" || status == "HD EVE" || status == "HALF-DAY") {
                                val checkInVal = record?.checkInTime ?: ""
                                val checkOutVal = record?.checkOutTime ?: ""
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Check-In (Incoming) Box
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                            .clickable {
                                                showTimeDialogForStudent = student
                                                editIsCheckIn = true
                                                customTimeValue = checkInVal.ifBlank { getCurrentTimeString() }
                                            }
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Login,
                                            contentDescription = "Incoming Time",
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text("Incoming", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(
                                                text = checkInVal.ifBlank { "Tap to log" },
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (checkInVal.isBlank()) FontWeight.Normal else FontWeight.Bold),
                                                color = if (checkInVal.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    // Check-Out (Outgoing) Box
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                            .clickable {
                                                showTimeDialogForStudent = student
                                                editIsCheckIn = false
                                                customTimeValue = checkOutVal.ifBlank { getCurrentTimeString() }
                                            }
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Logout,
                                            contentDescription = "Outgoing Time",
                                            tint = Color(0xFFC62828),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text("Outgoing", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(
                                                text = checkOutVal.ifBlank { "Tap to log" },
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (checkOutVal.isBlank()) FontWeight.Normal else FontWeight.Bold),
                                                color = if (checkOutVal.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    // Display Hours Worked
                                    val workDuration = calculateHours(checkInVal, checkOutVal)
                                    if (workDuration.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.primaryContainer)
                                                .padding(horizontal = 8.dp, vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(Icons.Default.AccessTime, contentDescription = "Work hours", modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                                Text(
                                                    text = workDuration,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    }
                                }

                                // Show Overtime Banner if check-in is before shift start or check-out is after shift end
                                val studentShift = allShifts.find { it.id == student.shiftId }
                                val shiftStartMins = parseShiftStartTimeToMinutes(studentShift?.timings) ?: (9 * 60 + 30)
                                val shiftEndMins = parseShiftEndTimeToMinutes(studentShift?.timings) ?: (20 * 60)
                                val cardOtResult = calculateOvertime(checkInVal, checkOutVal, status, shiftStartMins, shiftEndMins)
                                if (cardOtResult.totalOvertimeMinutes > 0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFFF3E0))
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = "Overtime",
                                            tint = Color(0xFFE65100),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Overtime: ${cardOtResult.formattedString} (${cardOtResult.breakdownString})",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFFE65100)
                                        )
                                    }
                                }
                            }

                            // If Absent: Show Option of Phone / MSG & Choice to mark Right (✓, Green) or Wrong (✗, Red) along with Overtime
                            if (status == "A" || status == "ABSENT") {
                                val checkInVal = record?.checkInTime ?: ""
                                val checkOutVal = record?.checkOutTime ?: ""
                                val hasOvertime = checkInVal.isNotEmpty() || checkOutVal.isNotEmpty()
                                val context = LocalContext.current
                                val studentPhone = student.phone.trim()

                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                // Absent Quick Contact & Status Choice Card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                    border = BorderStroke(1.dp, Color(0xFFFFCDD2))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Header Row: Absent Sign + Status Toggle Choices (Right ✓ vs Wrong ✗)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "✗",
                                                    color = Color(0xFFC62828),
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 15.sp
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "ABSENT TODAY",
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                                    color = Color(0xFFC62828)
                                                )
                                            }

                                            // Choice to Mark as RIGHT (✓, Green) or WRONG (✗, Red)
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                // RIGHT (✓ Green) Choice button
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFF2E7D32),
                                                    modifier = Modifier
                                                        .clickable { onMark(student.id, "P") }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text("✓", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text("Present", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                                                    }
                                                }

                                                // WRONG (✗ Red) Choice badge
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFFC62828),
                                                    modifier = Modifier
                                                        .clickable { onMark(student.id, "A") }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text("✗", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text("Absent", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                                                    }
                                                }
                                            }
                                        }

                                        // Contact row: Phone Call, SMS MSG, WhatsApp
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Call Button
                                            OutlinedButton(
                                                onClick = {
                                                    if (studentPhone.isNotEmpty()) {
                                                        try {
                                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$studentPhone"))
                                                            context.startActivity(intent)
                                                        } catch (e: Exception) {
                                                            Toast.makeText(context, "Could not open dialer", Toast.LENGTH_SHORT).show()
                                                        }
                                                    } else {
                                                        Toast.makeText(context, "No phone number saved for ${student.name}. Edit profile to add phone.", Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                                                border = BorderStroke(1.dp, Color(0xFFEF9A9A)),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Call", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            // SMS MSG Button
                                            OutlinedButton(
                                                onClick = {
                                                    if (studentPhone.isNotEmpty()) {
                                                        try {
                                                            val msgText = "Hello ${student.name}, you are marked Absent today ($dateStr). Please update us on the reason or apply for leave."
                                                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$studentPhone")).apply {
                                                                putExtra("sms_body", msgText)
                                                            }
                                                            context.startActivity(intent)
                                                        } catch (e: Exception) {
                                                            Toast.makeText(context, "Could not open SMS app", Toast.LENGTH_SHORT).show()
                                                        }
                                                    } else {
                                                        Toast.makeText(context, "No phone number saved for ${student.name}.", Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1565C0)),
                                                border = BorderStroke(1.dp, Color(0xFF90CAF9)),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Sms, contentDescription = "SMS", modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("SMS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            // WhatsApp MSG Button
                                            OutlinedButton(
                                                onClick = {
                                                    if (studentPhone.isNotEmpty()) {
                                                        try {
                                                            val cleanDigits = studentPhone.replace(Regex("[^0-9]"), "")
                                                            val formattedNum = if (cleanDigits.length == 10) "91$cleanDigits" else cleanDigits
                                                            val msgText = "Hello ${student.name}, you are marked Absent today ($dateStr). Kindly inform us regarding your absence/leave."
                                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$formattedNum?text=${Uri.encode(msgText)}"))
                                                            context.startActivity(intent)
                                                        } catch (e: Exception) {
                                                            Toast.makeText(context, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
                                                        }
                                                    } else {
                                                        Toast.makeText(context, "No phone number saved for ${student.name}.", Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32)),
                                                border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                                modifier = Modifier.weight(1.1f)
                                            ) {
                                                Icon(Icons.Default.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (hasOvertime) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                    ),
                                    border = BorderStroke(1.dp, if (hasOvertime) Color(0xFFFFB74D) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Bolt,
                                                    contentDescription = "Overtime",
                                                    tint = if (hasOvertime) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Add Overtime (OT)",
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = if (hasOvertime) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                            Switch(
                                                checked = hasOvertime,
                                                onCheckedChange = { enableOt ->
                                                    if (enableOt) {
                                                        val inT = checkInVal.ifBlank { "06:00 PM" }
                                                        val outT = checkOutVal.ifBlank { "09:30 PM" }
                                                        onUpdateCheckIn(student.id, inT)
                                                        onUpdateCheckOut(student.id, outT)
                                                    } else {
                                                        onUpdateCheckIn(student.id, "")
                                                        onUpdateCheckOut(student.id, "")
                                                    }
                                                },
                                                modifier = Modifier.testTag("toggle_ot_${student.id}")
                                            )
                                        }

                                        if (hasOvertime) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // OT In-Time
                                                Row(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color.White.copy(alpha = 0.9f))
                                                        .clickable {
                                                            showTimeDialogForStudent = student
                                                            editIsCheckIn = true
                                                            customTimeValue = checkInVal.ifBlank { "06:00 PM" }
                                                        }
                                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.Login,
                                                        contentDescription = "OT In Time",
                                                        tint = Color(0xFF2E7D32),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Column {
                                                        Text("OT In-Time", style = MaterialTheme.typography.labelSmall, color = Color(0xFF555555))
                                                        Text(
                                                            text = checkInVal.ifBlank { "Tap to log" },
                                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                            color = Color(0xFF1E293B)
                                                        )
                                                    }
                                                }

                                                // OT Out-Time
                                                Row(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color.White.copy(alpha = 0.9f))
                                                        .clickable {
                                                            showTimeDialogForStudent = student
                                                            editIsCheckIn = false
                                                            customTimeValue = checkOutVal.ifBlank { "09:30 PM" }
                                                        }
                                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.Logout,
                                                        contentDescription = "OT Out Time",
                                                        tint = Color(0xFFC62828),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Column {
                                                        Text("OT Out-Time", style = MaterialTheme.typography.labelSmall, color = Color(0xFF555555))
                                                        Text(
                                                            text = checkOutVal.ifBlank { "Tap to log" },
                                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                            color = Color(0xFF1E293B)
                                                        )
                                                    }
                                                }

                                                // Total OT Hours
                                                val otDuration = calculateHours(checkInVal, checkOutVal)
                                                if (otDuration.isNotEmpty()) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color(0xFFFFE0B2))
                                                            .padding(horizontal = 8.dp, vertical = 8.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                            Icon(Icons.Default.Bolt, contentDescription = "OT hours", modifier = Modifier.size(12.dp), tint = Color(0xFFE65100))
                                                            Text(
                                                                text = otDuration,
                                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                                color = Color(0xFFE65100)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            val reasonVal = record?.reason ?: ""
                            val notesVal = record?.notes ?: ""
                            val checkInVal = record?.checkInTime ?: ""
                            val checkOutVal = record?.checkOutTime ?: ""
                            val hasReason = reasonVal.isNotBlank()
                            val hasNotes = notesVal.isNotBlank()
                            val hasAnyNote = hasReason || hasNotes

                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (hasAnyNote) Color(0xFFFFF8E1) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                    .border(
                                        width = 1.dp,
                                        color = if (hasAnyNote) Color(0xFFFFCA28) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        showRemarksDialogForStudent = student
                                        customRemarksValue = notesVal
                                        customReasonValue = reasonVal
                                        val isA = status == "A" || status == "ABSENT"
                                        val hasOt = isA && (checkInVal.isNotEmpty() || checkOutVal.isNotEmpty())
                                        customHasOvertime = hasOt
                                        customOtInTime = checkInVal.ifBlank { "06:00 PM" }
                                        customOtOutTime = checkOutVal.ifBlank { "09:30 PM" }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = "Reason & Remarks",
                                    tint = if (hasAnyNote) Color(0xFFE65100) else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    if (hasReason) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "🏷️ Reason: ",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1565C0)
                                            )
                                            Text(
                                                text = reasonVal,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = Color(0xFF0D47A1),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    if (hasNotes) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "🔒 Remarks: ",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFE65100)
                                            )
                                            Text(
                                                text = notesVal,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF3E2723),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    if (!hasAnyNote) {
                                        Text(
                                            text = "Reason & Remarks",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Tap to add reason / private remarks...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = if (hasAnyNote) Icons.Default.Edit else Icons.Default.Add,
                                    contentDescription = "Edit Reason & Remarks",
                                    tint = if (hasAnyNote) Color(0xFFE65100) else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Divider between Reason & Remarks and Checklist/Tasks
                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                            thickness = 1.dp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // ─── 1. CHECKLIST FIELD ─────────────────────────────────────────────
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checklist_field_${student.id}"),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF3E8FF).copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, Color(0xFF7C3AED).copy(alpha = 0.25f))
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                // Header Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { isChecklistExpanded = !isChecklistExpanded }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(Color(0xFF7C3AED), RoundedCornerShape(6.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Checklist,
                                                contentDescription = "Checklist",
                                                tint = Color.White,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "1. CHECKLIST",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 0.6.sp
                                            ),
                                            color = Color(0xFF5B21B6)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))

                                        val completedChecklistCount = employeeChecklist.count { it.isCompleted }
                                        val totalChecklistCount = employeeChecklist.size
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (totalChecklistCount > 0 && completedChecklistCount == totalChecklistCount) {
                                                Color(0xFFDCFCE7)
                                            } else if (totalChecklistCount > 0) {
                                                Color(0xFFDDD6FE)
                                            } else {
                                                Color(0xFFF3F4F6)
                                            }
                                        ) {
                                            Text(
                                                text = if (totalChecklistCount == 0) {
                                                    "0 items"
                                                } else if (completedChecklistCount == totalChecklistCount) {
                                                    "✓ All Done ($totalChecklistCount)"
                                                } else {
                                                    "$completedChecklistCount/$totalChecklistCount Done"
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (totalChecklistCount > 0 && completedChecklistCount == totalChecklistCount) {
                                                    Color(0xFF15803D)
                                                } else if (totalChecklistCount > 0) {
                                                    Color(0xFF5B21B6)
                                                } else {
                                                    Color(0xFF6B7280)
                                                },
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    // Quick actions on header: Add & Expand/Collapse
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF7C3AED).copy(alpha = 0.12f),
                                            modifier = Modifier
                                                .clickable {
                                                    showAddItemDialogForStudent = Pair(student, "CHECKLIST")
                                                }
                                                .testTag("btn_add_checklist_${student.id}")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add Checklist",
                                                    tint = Color(0xFF6D28D9),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "Add",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF6D28D9)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        IconButton(
                                            onClick = { isChecklistExpanded = !isChecklistExpanded },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isChecklistExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = "Toggle Checklist",
                                                tint = Color(0xFF6D28D9),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                // Expanded checklist items
                                if (isChecklistExpanded) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (employeeChecklist.isEmpty()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "No checklist items for ${student.displayName}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF6B7280),
                                                fontStyle = FontStyle.Italic
                                            )
                                            TextButton(
                                                onClick = {
                                                    onRestoreDefaultItemsForStudent(student.id, student.displayName)
                                                },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "+ Load Standard",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF6D28D9)
                                                )
                                            }
                                        }
                                    } else {
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            contentPadding = PaddingValues(vertical = 2.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                             items(employeeChecklist, key = { it.id }) { item ->
                                                EmployeeItemRow(
                                                    item = item,
                                                    accentColor = Color(0xFF7C3AED),
                                                    onToggle = { onToggleChecklistItem(item) },
                                                    onEdit = { editingItemForAttendance = item },
                                                    onSendWhatsApp = {
                                                        sendTaskViaWhatsApp(context, item, student)
                                                    },
                                                    onSendSms = {
                                                        sendTaskViaSms(context, item, student)
                                                    },
                                                    onScheduleSms = {
                                                        itemForScheduleSmsForAttendance = item
                                                    },
                                                    onToggleSubtask = { subtaskId -> onToggleSubtask(item, subtaskId) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // ─── 2. TASKS FIELD ─────────────────────────────────────────────────
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tasks_field_${student.id}"),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFF7ED).copy(alpha = 0.55f),
                            border = BorderStroke(1.dp, Color(0xFFEA580C).copy(alpha = 0.25f))
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                // Header Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { isTasksExpanded = !isTasksExpanded }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(Color(0xFFEA580C), RoundedCornerShape(6.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.TaskAlt,
                                                contentDescription = "Tasks",
                                                tint = Color.White,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "2. TASKS",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 0.6.sp
                                            ),
                                            color = Color(0xFF9A3412)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))

                                        val pendingEmpTasks = employeeTasks.count { !it.isCompleted }
                                        val totalTasksCount = employeeTasks.size
                                        val hasUrgent = employeeTasks.any { !it.isCompleted && it.priority == "URGENT" }
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (totalTasksCount > 0 && pendingEmpTasks == 0) {
                                                Color(0xFFDCFCE7)
                                            } else if (hasUrgent) {
                                                Color(0xFFFEE2E2)
                                            } else if (totalTasksCount > 0) {
                                                Color(0xFFFFEDD5)
                                            } else {
                                                Color(0xFFF3F4F6)
                                            }
                                        ) {
                                            Text(
                                                text = if (totalTasksCount == 0) {
                                                    "0 tasks"
                                                } else if (pendingEmpTasks == 0) {
                                                    "✓ All Done ($totalTasksCount)"
                                                } else if (hasUrgent) {
                                                    "⚡ $pendingEmpTasks Pending"
                                                } else {
                                                    "$pendingEmpTasks Pending"
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (totalTasksCount > 0 && pendingEmpTasks == 0) {
                                                    Color(0xFF15803D)
                                                } else if (hasUrgent) {
                                                    Color(0xFFDC2626)
                                                } else if (totalTasksCount > 0) {
                                                    Color(0xFFC2410C)
                                                } else {
                                                    Color(0xFF6B7280)
                                                },
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    // Quick actions on header: Assign & Expand/Collapse
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFEA580C).copy(alpha = 0.12f),
                                            modifier = Modifier
                                                .clickable {
                                                    showAddItemDialogForStudent = Pair(student, "TASK")
                                                }
                                                .testTag("btn_add_task_${student.id}")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Assign Task",
                                                    tint = Color(0xFFC2410C),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "Assign",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFC2410C)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        IconButton(
                                            onClick = { isTasksExpanded = !isTasksExpanded },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isTasksExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = "Toggle Tasks",
                                                tint = Color(0xFFC2410C),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                // Expanded tasks items
                                if (isTasksExpanded) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (employeeTasks.isEmpty()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "No active tasks for ${student.displayName}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF6B7280),
                                                fontStyle = FontStyle.Italic
                                            )
                                            TextButton(
                                                onClick = {
                                                    onRestoreDefaultItemsForStudent(student.id, student.displayName)
                                                },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "+ Load Defaults",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFC2410C)
                                                )
                                            }
                                        }
                                    } else {
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            contentPadding = PaddingValues(vertical = 2.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                             items(employeeTasks, key = { it.id }) { item ->
                                                EmployeeItemRow(
                                                    item = item,
                                                    accentColor = Color(0xFFEA580C),
                                                    onToggle = { onToggleChecklistItem(item) },
                                                    onEdit = { editingItemForAttendance = item },
                                                    onSendWhatsApp = {
                                                        sendTaskViaWhatsApp(context, item, student)
                                                    },
                                                    onSendSms = {
                                                        sendTaskViaSms(context, item, student)
                                                    },
                                                    onScheduleSms = {
                                                        itemForScheduleSmsForAttendance = item
                                                    },
                                                    onToggleSubtask = { subtaskId -> onToggleSubtask(item, subtaskId) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Time Edit Dialog
    if (showTimeDialogForStudent != null) {
        val student = showTimeDialogForStudent!!
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = { showTimeDialogForStudent = null },
            title = {
                Text(
                    text = if (editIsCheckIn) "Log Incoming / In-Time" else "Log Outgoing / Out-Time",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Set time for ${student.name} (${student.rollNumber})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showTimePickerDialog(context, customTimeValue) { selectedTime ->
                                    customTimeValue = selectedTime
                                }
                            }
                    ) {
                        OutlinedTextField(
                            value = customTimeValue,
                            onValueChange = {},
                            label = { Text("Time (e.g. 09:00 AM)") },
                            placeholder = { Text("hh:mm AM/PM") },
                            singleLine = true,
                            readOnly = true,
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.primary
                            ),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Pick time",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Quick Presets",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick buttons row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = if (editIsCheckIn) {
                            listOf("09:00 AM", "09:30 AM", "06:00 PM")
                        } else {
                            listOf("06:00 PM", "08:00 PM", "09:30 PM")
                        }

                        // Preset: "Now" button
                        Button(
                            onClick = { customTimeValue = getCurrentTimeString() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Text("Now", fontSize = 11.sp, maxLines = 1)
                        }

                        presets.forEach { preset ->
                            Button(
                                onClick = { customTimeValue = preset },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(preset.replace(" ", "\n"), fontSize = 11.sp, maxLines = 2, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (customTimeValue.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                if (editIsCheckIn) {
                                    onUpdateCheckIn(student.id, "")
                                } else {
                                    onUpdateCheckOut(student.id, "")
                                }
                                showTimeDialogForStudent = null
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828))
                        ) {
                            Text("Clear")
                        }
                    }
                    Button(
                        onClick = {
                            if (editIsCheckIn) {
                                onUpdateCheckIn(student.id, customTimeValue.trim())
                            } else {
                                onUpdateCheckOut(student.id, customTimeValue.trim())
                            }
                            showTimeDialogForStudent = null
                        }
                    ) {
                        Text("Save")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTimeDialogForStudent = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Remarks & Reason Dialog
    if (showRemarksDialogForStudent != null) {
        val student = showRemarksDialogForStudent!!
        val currentRecord = recordsList.find { it.studentId == student.id }
        val currentStatus = currentRecord?.status ?: "UNMARKED"
        val isAbsentStatus = currentStatus == "A" || currentStatus == "ABSENT"
        val isUnmarked = currentStatus == "UNMARKED" || currentStatus.isBlank()
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { showRemarksDialogForStudent = null },
            title = {
                Column {
                    Text(
                        text = if (isAbsentStatus) "Absent Reason, Remarks & OT" else if (isUnmarked) "Reason & Remarks (Unmarked)" else "Add / Edit Reason & Remarks",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isUnmarked) "ℹ️ Status: UNMARKED (Reason & remarks saved for record)" else "ℹ️ Status: $currentStatus",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isUnmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Staff: ${student.name} (${student.rollNumber})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 1. REASON FIELD (Public / Official Attendance Reason)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🏷️ Reason (Attendance / Absence Reason)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0)
                            )
                        }
                        OutlinedTextField(
                            value = customReasonValue,
                            onValueChange = { customReasonValue = it },
                            label = { Text("Reason") },
                            placeholder = {
                                Text(
                                    if (isAbsentStatus) "e.g. Sick Leave, Family Emergency, Out of Station"
                                    else if (isUnmarked) "e.g. Weekly Off, Holiday, Approved Leave"
                                    else "e.g. Traffic Delay, Doctor Appointment, Client Meeting"
                                )
                            },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF1565C0))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_reason_field")
                        )

                        // Quick Presets for Reason
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val reasonPresets = if (isAbsentStatus) {
                                listOf("Sick Leave", "Family Function", "Emergency", "Out of Station")
                            } else if (isUnmarked) {
                                listOf("Weekly Off", "Holiday", "Approved Leave", "Half Day")
                            } else {
                                listOf("Traffic / Delay", "Doctor Visit", "Client Meeting", "Personal Work")
                            }
                            reasonPresets.forEach { preset ->
                                SuggestionChip(
                                    onClick = {
                                        customReasonValue = preset
                                    },
                                    label = { Text(preset, fontSize = 9.sp) }
                                )
                            }
                        }

                        if (customReasonValue.isNotBlank()) {
                            TextButton(
                                onClick = { customReasonValue = "" },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Clear", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Clear Reason", fontSize = 11.sp)
                            }
                        }
                    }

                    // 2. REMARKS / NOTES FIELD (Private Personal Notes)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Remarks / Notes (Private for You)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                        Text(
                            text = "🔒 Kept private by default. Will not be shared in reports unless you enable the share toggle.",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = customRemarksValue,
                            onValueChange = { customRemarksValue = it },
                            label = { Text("Private Remarks") },
                            placeholder = { 
                                Text(
                                    if (isAbsentStatus) "e.g. Called in morning, informed manager, will join tomorrow" 
                                    else if (isUnmarked) "e.g. Personal notes about schedule..."
                                    else "e.g. Informed about delay, will compensate hours..."
                                ) 
                            },
                            singleLine = false,
                            maxLines = 3,
                            leadingIcon = {
                                Icon(Icons.Default.EditNote, contentDescription = null, tint = Color(0xFFE65100))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_remarks_field")
                        )

                        // Quick suggestion chips for Remarks
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val presets = if (isAbsentStatus) {
                                listOf("Informed", "Uninformed", "Will Join Tomorrow", "Salary Cut")
                            } else if (isUnmarked) {
                                listOf("Weekly Off", "Holiday Note", "Compensatory", "Duty Tour")
                            } else {
                                listOf("Informed Prior", "Informed on Call", "Compensate Hours", "Overtime Allowed")
                            }
                            presets.forEach { preset ->
                                SuggestionChip(
                                    onClick = {
                                        customRemarksValue = if (customRemarksValue.isBlank()) preset else "$customRemarksValue, $preset"
                                    },
                                    label = { Text(preset, fontSize = 9.sp) }
                                )
                            }
                        }

                        if (customRemarksValue.isNotBlank()) {
                            TextButton(
                                onClick = { customRemarksValue = "" },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Clear", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Clear Remarks", fontSize = 11.sp)
                            }
                        }
                    }

                    // If status is Absent: Provide Add Overtime option with In-Time & Out-Time inputs
                    if (isAbsentStatus) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (customHasOvertime) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = BorderStroke(1.dp, if (customHasOvertime) Color(0xFFFFB74D) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = "Overtime",
                                            tint = if (customHasOvertime) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Add Overtime (OT)",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (customHasOvertime) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Switch(
                                        checked = customHasOvertime,
                                        onCheckedChange = { customHasOvertime = it },
                                        modifier = Modifier.testTag("dialog_toggle_ot")
                                    )
                                }

                                if (customHasOvertime) {
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // In-Time Input
                                    Column {
                                        Text("In-Time (OT Start)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = customOtInTime,
                                            onValueChange = { customOtInTime = it },
                                            placeholder = { Text("e.g. 06:00 PM") },
                                            singleLine = true,
                                            leadingIcon = {
                                                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                            },
                                            trailingIcon = {
                                                IconButton(onClick = {
                                                    showTimePickerDialog(context, customOtInTime) { selectedTime ->
                                                        customOtInTime = selectedTime
                                                    }
                                                }) {
                                                    Icon(Icons.Default.AccessTime, contentDescription = "Pick Time", modifier = Modifier.size(18.dp))
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("dialog_ot_in_time")
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 2.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            listOf("05:00 PM", "06:00 PM", "07:00 PM", "08:00 PM").forEach { preset ->
                                                SuggestionChip(
                                                    onClick = { customOtInTime = preset },
                                                    label = { Text(preset, fontSize = 9.sp) }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Out-Time Input
                                    Column {
                                        Text("Out-Time (OT End)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = customOtOutTime,
                                            onValueChange = { customOtOutTime = it },
                                            placeholder = { Text("e.g. 09:30 PM") },
                                            singleLine = true,
                                            leadingIcon = {
                                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(18.dp))
                                            },
                                            trailingIcon = {
                                                IconButton(onClick = {
                                                    showTimePickerDialog(context, customOtOutTime) { selectedTime ->
                                                        customOtOutTime = selectedTime
                                                    }
                                                }) {
                                                    Icon(Icons.Default.AccessTime, contentDescription = "Pick Time", modifier = Modifier.size(18.dp))
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("dialog_ot_out_time")
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 2.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            listOf("08:00 PM", "09:00 PM", "10:00 PM", "11:00 PM").forEach { preset ->
                                                SuggestionChip(
                                                    onClick = { customOtOutTime = preset },
                                                    label = { Text(preset, fontSize = 9.sp) }
                                                )
                                            }
                                        }
                                    }

                                    val calculatedOt = calculateHours(customOtInTime, customOtOutTime)
                                    if (calculatedOt.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFFE0B2),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "⚡ Calculated Overtime: $calculatedOt",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFFE65100),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                textAlign = TextAlign.Center
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
                        val trimmedRemarks = customRemarksValue.trim()
                        val trimmedReason = customReasonValue.trim()
                        val hasOt = if (isAbsentStatus) customHasOvertime else false
                        val otIn = if (isAbsentStatus && customHasOvertime) customOtInTime.trim() else ""
                        val otOut = if (isAbsentStatus && customHasOvertime) customOtOutTime.trim() else ""
                        onSaveRemarksAndOvertime(
                            student.id,
                            trimmedRemarks,
                            trimmedReason,
                            hasOt,
                            otIn,
                            otOut
                        )
                        showRemarksDialogForStudent = null
                    },
                    modifier = Modifier.testTag("submit_remarks")
                ) {
                    Text("Save Record")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemarksDialogForStudent = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Quick Add Checklist Item / Task Dialog for Student
    if (showAddItemDialogForStudent != null) {
        val (studentForDialog, itemTypeMode) = showAddItemDialogForStudent!!
        var newItemTitle by remember { mutableStateOf("") }
        var newItemCategory by remember { mutableStateOf(if (itemTypeMode == "TASK") "SALES" else "DAILY") }
        var subtasksList by remember { mutableStateOf<List<SubtaskItem>>(emptyList()) }
        var newSubtaskInputText by remember { mutableStateOf("") }

        val quickChips = if (itemTypeMode == "TASK") {
            listOf(
                "Customer Trial Assistance",
                "VIP Saree Packing",
                "Count Day's Sales for T-Hisab",
                "Stock Tally Verification",
                "Customer Order Followup"
            )
        } else {
            listOf(
                "Morning Section Opening",
                "Saree Showcase Arrangement",
                "Dust & Arrange Saree Shelves",
                "Fold Sampled Sarees & Cover Racks",
                "Evening Counter Closing"
            )
        }

        AlertDialog(
            onDismissRequest = { showAddItemDialogForStudent = null },
            icon = {
                Icon(
                    imageVector = if (itemTypeMode == "TASK") Icons.Default.TaskAlt else Icons.Default.Checklist,
                    contentDescription = null,
                    tint = if (itemTypeMode == "TASK") Color(0xFFEA580C) else Color(0xFF7C3AED)
                )
            },
            title = {
                Text(
                    text = if (itemTypeMode == "TASK") "Assign Task to ${studentForDialog.displayName}" else "Add Checklist for ${studentForDialog.displayName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newItemTitle,
                        onValueChange = { newItemTitle = it },
                        label = { Text(if (itemTypeMode == "TASK") "Task Description" else "Checklist Item Title") },
                        placeholder = { Text(if (itemTypeMode == "TASK") "e.g. Help customer in Bridal section..." else "e.g. Dust showcase & fold sarees...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_item_title"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Quick Suggestions:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickChips) { suggestion ->
                            val isSelected = newItemTitle == suggestion
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) {
                                    if (itemTypeMode == "TASK") Color(0xFFFFEDD5) else Color(0xFFEDE9FE)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                },
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) {
                                        if (itemTypeMode == "TASK") Color(0xFFEA580C) else Color(0xFF7C3AED)
                                    } else Color.Transparent
                                ),
                                modifier = Modifier.clickable { newItemTitle = suggestion }
                            ) {
                                Text(
                                    text = suggestion,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = if (isSelected) {
                                        if (itemTypeMode == "TASK") Color(0xFFC2410C) else Color(0xFF6D28D9)
                                    } else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    // Subtasks Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Subtasks (${subtasksList.count { it.isCompleted }}/${subtasksList.size})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (itemTypeMode == "TASK") Color(0xFFEA580C) else Color(0xFF7C3AED)
                            )
                        }

                        if (subtasksList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            subtasksList.forEachIndexed { index, subtask ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = subtask.isCompleted,
                                        onCheckedChange = { checked ->
                                            subtasksList = subtasksList.mapIndexed { idx, item ->
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
                                            subtasksList = subtasksList.filterIndexed { idx, _ -> idx != index }
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newSubtaskInputText,
                                onValueChange = { newSubtaskInputText = it },
                                placeholder = { Text("Add subtask...", fontSize = 12.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (newSubtaskInputText.isNotBlank()) {
                                            subtasksList = subtasksList + SubtaskItem(
                                                id = "sub_${System.currentTimeMillis()}_${(100..999).random()}",
                                                title = newSubtaskInputText.trim(),
                                                isCompleted = false
                                            )
                                            newSubtaskInputText = ""
                                        }
                                    }
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (itemTypeMode == "TASK") Color(0xFFEA580C) else Color(0xFF7C3AED),
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable(enabled = newSubtaskInputText.isNotBlank()) {
                                        if (newSubtaskInputText.isNotBlank()) {
                                            subtasksList = subtasksList + SubtaskItem(
                                                id = "sub_${System.currentTimeMillis()}_${(100..999).random()}",
                                                title = newSubtaskInputText.trim(),
                                                isCompleted = false
                                            )
                                            newSubtaskInputText = ""
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add subtask",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        val suggestions = listOf("Check count", "Clean area", "Take photo", "Verify stock", "Confirm bill")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(suggestions) { sugg ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.clickable {
                                        subtasksList = subtasksList + SubtaskItem(
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newItemTitle.isNotBlank()) {
                            onAddChecklistItem(
                                studentForDialog.id,
                                newItemTitle.trim(),
                                itemTypeMode,
                                newItemCategory,
                                subtasksList.toJson()
                            )
                            showAddItemDialogForStudent = null
                        }
                    },
                    enabled = newItemTitle.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (itemTypeMode == "TASK") Color(0xFFEA580C) else Color(0xFF7C3AED)
                    ),
                    modifier = Modifier.testTag("btn_confirm_add_item")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddItemDialogForStudent = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Directly Edit Task / Checklist Item in Take Attendance Tab
    if (editingItemForAttendance != null) {
        val editing = editingItemForAttendance!!
        AddEditChecklistItemDialog(
            item = editing,
            initialScopeId = editing.studentId,
            allStudents = students,
            initialItemType = editing.itemType,
            onDismiss = { editingItemForAttendance = null },
            onSave = { studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson ->
                val updated = editing.copy(
                    studentId = studentId,
                    title = title,
                    category = category,
                    notes = notes,
                    assignedStaffName = staffName,
                    reminderTimestamp = reminderTimestamp,
                    itemType = itemType,
                    subtasksJson = subtasksJson
                )
                onUpdateChecklistItem(updated)
                editingItemForAttendance = null
            },
            onSaveAndSendWhatsApp = { studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson ->
                val updated = editing.copy(
                    studentId = studentId,
                    title = title,
                    category = category,
                    notes = notes,
                    assignedStaffName = staffName,
                    reminderTimestamp = reminderTimestamp,
                    itemType = itemType,
                    subtasksJson = subtasksJson
                )
                onUpdateChecklistItem(updated)
                editingItemForAttendance = null
                val targetStudent = students.find { it.id == studentId }
                sendTaskViaWhatsApp(context, updated, targetStudent)
            },
            onSaveAndSendSms = { studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson ->
                val updated = editing.copy(
                    studentId = studentId,
                    title = title,
                    category = category,
                    notes = notes,
                    assignedStaffName = staffName,
                    reminderTimestamp = reminderTimestamp,
                    itemType = itemType,
                    subtasksJson = subtasksJson
                )
                onUpdateChecklistItem(updated)
                editingItemForAttendance = null
                val targetStudent = students.find { it.id == studentId }
                sendTaskViaSms(context, updated, targetStudent)
            },
            onSaveAndScheduleSms = { studentId, title, category, notes, staffName, reminderTimestamp, itemType, subtasksJson ->
                val updated = editing.copy(
                    studentId = studentId,
                    title = title,
                    category = category,
                    notes = notes,
                    assignedStaffName = staffName,
                    reminderTimestamp = reminderTimestamp,
                    itemType = itemType,
                    subtasksJson = subtasksJson
                )
                onUpdateChecklistItem(updated)
                editingItemForAttendance = null
                itemForScheduleSmsForAttendance = updated
            },
            onDelete = {
                itemToDeleteForAttendance = editing
                editingItemForAttendance = null
            }
        )
    }

    // Confirm Delete Dialog for Checklist/Task Item in Take Attendance Tab
    if (itemToDeleteForAttendance != null) {
        AlertDialog(
            onDismissRequest = { itemToDeleteForAttendance = null },
            title = { Text("Delete Task?") },
            text = { Text("Are you sure you want to delete \"${itemToDeleteForAttendance!!.title}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteChecklistItem(itemToDeleteForAttendance!!)
                        itemToDeleteForAttendance = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDeleteForAttendance = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Schedule Task via SMS Dialog
    if (itemForScheduleSmsForAttendance != null) {
        val targetStudent = students.find { it.id == itemForScheduleSmsForAttendance!!.studentId }
        ScheduleTaskSmsDialog(
            item = itemForScheduleSmsForAttendance!!,
            student = targetStudent,
            allStudents = students,
            onDismiss = { itemForScheduleSmsForAttendance = null },
            onScheduled = { itemForScheduleSmsForAttendance = null },
            onSendImmediately = { phone, message ->
                TaskSmsSchedulerHelper.sendSmsImmediately(context, phone, message)
                itemForScheduleSmsForAttendance = null
            },
            onCancelSchedule = { smsId ->
                TaskSmsSchedulerHelper.cancelScheduledSms(context, smsId)
            }
        )
    }
}

@Composable
private fun EmployeeItemRow(
    item: ChecklistItemEntity,
    accentColor: Color,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onSendWhatsApp: () -> Unit,
    onSendSms: () -> Unit,
    onScheduleSms: () -> Unit = {},
    onToggleSubtask: (String) -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .width(220.dp)
            .testTag("item_row_${item.id}"),
        shape = RoundedCornerShape(10.dp),
        color = if (item.isCompleted) Color(0xFFF9FAFB) else Color.White,
        border = BorderStroke(
            1.dp,
            if (item.isCompleted) Color(0xFFE5E7EB) else accentColor.copy(alpha = 0.35f)
        ),
        shadowElevation = if (item.isCompleted) 0.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            // Top Row: Checkbox + Done Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Interactive Checkbox icon (tap toggles completion)
                Box(
                    modifier = Modifier
                        .size(19.dp)
                        .background(
                            color = if (item.isCompleted) Color(0xFF10B981) else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .border(
                            width = 1.4.dp,
                            color = if (item.isCompleted) Color(0xFF10B981) else Color(0xFF9CA3AF),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable(onClick = onToggle)
                        .testTag("checkbox_item_${item.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (item.isCompleted) {
                    Text(
                        text = "✓ Done",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Title (clickable to edit)
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (item.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                    textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp
                ),
                color = if (item.isCompleted) Color(0xFF9CA3AF) else Color(0xFF1F2937),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onEdit)
            )

            // Optional Note snippet
            if (item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "📝 ${item.notes}",
                    fontSize = 9.5.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF6B7280),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(onClick = onEdit)
                )
            }

            // Subtasks Section if present
            val subtasks = remember(item.subtasksJson) { item.getSubtasks() }
            if (subtasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                val doneCount = subtasks.count { it.isCompleted }
                val progress = doneCount.toFloat() / subtasks.size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Subtasks ($doneCount/${subtasks.size})",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (doneCount == subtasks.size) Color(0xFF16A34A) else accentColor
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (doneCount == subtasks.size) Color(0xFF16A34A) else accentColor,
                    trackColor = Color(0xFFE5E7EB)
                )
                Spacer(modifier = Modifier.height(3.dp))
                subtasks.take(3).forEach { sub ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleSubtask(sub.id) }
                            .padding(vertical = 1.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .border(
                                    width = 1.2.dp,
                                    color = if (sub.isCompleted) Color(0xFF16A34A) else Color(0xFF9CA3AF),
                                    shape = CircleShape
                                )
                                .background(
                                    color = if (sub.isCompleted) Color(0xFF16A34A) else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (sub.isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(8.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = sub.title,
                            fontSize = 9.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = if (sub.isCompleted) TextDecoration.LineThrough else null,
                            color = if (sub.isCompleted) Color(0xFF9CA3AF) else Color(0xFF374151)
                        )
                    }
                }
                if (subtasks.size > 3) {
                    Text(
                        text = "+${subtasks.size - 3} more subtasks",
                        fontSize = 8.5.sp,
                        color = accentColor,
                        modifier = Modifier.clickable(onClick = onEdit)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Bottom Action Footer: Type Pill + Quick Action Icons (Edit, WhatsApp, SMS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Category or Type Label
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = accentColor.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = if (item.category.isNotBlank() && item.category != "DAILY") item.category else if (item.isTask) "Task" else "Checklist",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                // 3 Small Action Icons: Edit, WhatsApp, SMS
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Edit Button
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF3F4F6),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        modifier = Modifier
                            .size(26.dp)
                            .clickable(onClick = onEdit)
                            .testTag("btn_edit_item_${item.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Task",
                                tint = Color(0xFF4B5563),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    // WhatsApp Button
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFDCFCE7),
                        border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                        modifier = Modifier
                            .size(26.dp)
                            .clickable(onClick = onSendWhatsApp)
                            .testTag("btn_whatsapp_item_${item.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Send on WhatsApp",
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    // SMS Button
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE0F2FE),
                        border = BorderStroke(1.dp, Color(0xFF7DD3FC)),
                        modifier = Modifier
                            .size(26.dp)
                            .clickable(onClick = onSendSms)
                            .testTag("btn_sms_item_${item.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Sms,
                                contentDescription = "Send via SMS",
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    // Schedule SMS Button
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFEEF2FF),
                        border = BorderStroke(1.dp, Color(0xFFA5B4FC)),
                        modifier = Modifier
                            .size(26.dp)
                            .clickable(onClick = onScheduleSms)
                            .testTag("btn_sched_sms_item_${item.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Schedule Task SMS",
                                tint = Color(0xFF4F46E5),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceButton(
    label: String,
    activeColor: Color,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sign: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) activeColor else MaterialTheme.colorScheme.surfaceVariant,
        label = "bgColorAnimation"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "contentColorAnimation"
    )

    val isShort = label.length <= 2
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (sign != null) {
                Text(
                    text = sign,
                    color = if (isActive) Color.White else activeColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.5.sp
                )
                Spacer(modifier = Modifier.width(3.dp))
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isActive) Color.White else activeColor,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
            Text(
                text = label,
                color = contentColor,
                fontWeight = FontWeight.Black,
                fontSize = if (isShort) 13.5.sp else 10.5.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
fun ClassesStudentsView(
    activeClass: ClassEntity,
    students: List<StudentEntity>,
    allStudentsCount: Int = 0,
    rates: Map<Long, Double>,
    allShifts: List<ShiftEntity>,
    allChecklistItems: List<ChecklistItemEntity> = emptyList(),
    onAddStudent: () -> Unit,
    onEditStudent: (StudentEntity) -> Unit,
    onDeleteStudent: (StudentEntity) -> Unit,
    onDeleteClass: () -> Unit,
    onEditClass: (newName: String, newColorHex: String) -> Unit,
    onRearrangeClasses: () -> Unit = {},
    onAssignShift: (StudentEntity) -> Unit,
    onViewProfile: (StudentEntity) -> Unit = {},
    onViewReport: (StudentEntity) -> Unit = {},
    onViewChecklist: (StudentEntity) -> Unit = {},
    onViewHisab: (StudentEntity) -> Unit = {},
    onAddShift: (name: String, timings: String) -> Unit = { _, _ -> },
    onUpdateShift: (ShiftEntity) -> Unit = {},
    onDeleteShift: (ShiftEntity) -> Unit = {},
    portalBaseUrl: String = "",
    firebaseUrl: String = "https://gen-lang-client-0830360424-default-rtdb.firebaseio.com",
    firebaseWorkspace: String = "shop_attendance",
    onSavePortalBaseUrl: (String) -> Unit = {}
) {
    var selectedSubSection by remember { mutableIntStateOf(0) } // 0 = Staff, 1 = Shifts
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var shareLinkStudent by remember { mutableStateOf<StudentEntity?>(null) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Segmented Pill Toggle for Staff vs Work Shifts
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    onClick = { selectedSubSection = 0 },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedSubSection == 0) MaterialTheme.colorScheme.surface else Color.Transparent,
                    shadowElevation = if (selectedSubSection == 0) 2.dp else 0.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = if (selectedSubSection == 0) parseColor(activeClass.colorHex) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Staff Members (${students.size})",
                            fontWeight = if (selectedSubSection == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.5.sp,
                            color = if (selectedSubSection == 0) parseColor(activeClass.colorHex) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    onClick = { selectedSubSection = 1 },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedSubSection == 1) MaterialTheme.colorScheme.surface else Color.Transparent,
                    shadowElevation = if (selectedSubSection == 1) 2.dp else 0.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = if (selectedSubSection == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Work Shifts (${allShifts.size})",
                            fontWeight = if (selectedSubSection == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.5.sp,
                            color = if (selectedSubSection == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedSubSection == 0) {
            // Staff Management View
            // Class Control Panel
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Active Department Group",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = activeClass.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = parseColor(activeClass.colorHex)
                            )
                        }

                        Row {
                            IconButton(
                                onClick = onRearrangeClasses,
                                modifier = Modifier.testTag("rearrange_class_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = "Rearrange Departments",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = { showEditDialog = true },
                                modifier = Modifier.testTag("edit_class_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Department",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = { showDeleteConfirm = true },
                                modifier = Modifier.testTag("delete_class_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Department",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Student Enrollment Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DEPARTMENT STAFF (${students.size})",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onAddStudent,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = parseColor(activeClass.colorHex)),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("add_student_trigger")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Staff", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (students.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (allStudentsCount > 0)
                            "No staff members in ${activeClass.name}.\nYou have $allStudentsCount staff member(s) in other departments (tap a department above to switch)."
                        else
                            "No staff members added to this department yet.\nTap 'Add Staff' to begin.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(students) { student ->
                        val rate = rates[student.id] ?: -1.0
                        val rateColor = when {
                            rate == -1.0 -> MaterialTheme.colorScheme.onSurfaceVariant
                            rate < 75.0 -> Color(0xFFC62828) // Warning red
                            else -> Color(0xFF2E7D32) // Healthy green
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                // Top Row: Avatar + Name + ID + Attendance Rate Badge
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onViewProfile(student) }
                                            .padding(vertical = 2.dp, horizontal = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val cleanStaffName = student.displayName
                                        // Initial Circle Avatar
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(parseColor(activeClass.colorHex).copy(alpha = 0.18f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = cleanStaffName.take(1).uppercase(Locale.getDefault()),
                                                color = parseColor(activeClass.colorHex),
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = cleanStaffName,
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = "Profile ↗",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                                ) {
                                                    Text(
                                                        text = "ID: ${student.rollNumber}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                    )
                                                }

                                                if (student.phone.isNotBlank()) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Phone,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(11.dp),
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                        Spacer(modifier = Modifier.width(2.dp))
                                                        Text(
                                                            text = student.phone,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Attendance rate badge
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = rateColor.copy(alpha = 0.14f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, rateColor.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = if (rate == -1.0) "No Record" else "${rate.toInt()}% Avg",
                                            color = rateColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                // Middle Row: Shift assignment badge + optional email
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val studentShift = allShifts.find { it.id == student.shiftId }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                        modifier = Modifier.clickable { onAssignShift(student) }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AccessTime,
                                                contentDescription = "Shift",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = studentShift?.let { "${it.name} (${it.timings})" } ?: "No Shift (Tap to assign)",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 10.5.sp
                                            )
                                        }
                                    }

                                    if (student.email.isNotBlank()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Email,
                                                contentDescription = null,
                                                modifier = Modifier.size(11.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = student.email,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                    thickness = 0.8.dp
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                // Bottom Action Row: Checklist, T-HISAB, Edit, Share, Analytics, Delete
                                val pendingTasksCount = remember(allChecklistItems, student.id, student.name) {
                                    allChecklistItems.count {
                                        (it.studentId == student.id || (it.assignedStaffName.isNotBlank() && it.assignedStaffName.equals(student.name, ignoreCase = true))) && !it.isCompleted
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Checklist with Pending Count Badge
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconButton(
                                            onClick = { onViewChecklist(student) },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (pendingTasksCount > 0) Color(0xFF7C3AED).copy(alpha = 0.20f) else Color(0xFF7C3AED).copy(alpha = 0.12f)
                                                )
                                                .testTag("employee_checklist_btn_${student.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Checklist,
                                                contentDescription = "Checklist & Tasks for ${student.name} ($pendingTasksCount pending)",
                                                tint = Color(0xFF7C3AED),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        if (pendingTasksCount > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = 4.dp, y = (-4).dp)
                                                    .background(
                                                        color = Color(0xFFDC2626),
                                                        shape = CircleShape
                                                    )
                                                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                                    .defaultMinSize(minWidth = 17.dp, minHeight = 17.dp)
                                                    .padding(horizontal = 3.5.dp, vertical = 1.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = if (pendingTasksCount > 99) "99+" else "$pendingTasksCount",
                                                    color = Color.White,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    lineHeight = 10.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }

                                    // T-HISAB
                                    IconButton(
                                        onClick = { onViewHisab(student) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF059669).copy(alpha = 0.12f))
                                            .testTag("employee_thisab_btn_${student.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = "T-HISAB & Salary",
                                            tint = Color(0xFF059669),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Edit Staff (with department selector)
                                    IconButton(
                                        onClick = { onEditStudent(student) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                            .testTag("employee_edit_btn_${student.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Staff Details",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Share Passbook Link
                                    IconButton(
                                        onClick = { shareLinkStudent = student },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF2563EB).copy(alpha = 0.12f))
                                            .testTag("employee_share_link_btn_${student.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share Staff Link",
                                            tint = Color(0xFF2563EB),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Full Monthly Report
                                    IconButton(
                                        onClick = { onViewReport(student) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))
                                            .testTag("employee_report_btn_${student.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Assessment,
                                            contentDescription = "Full Monthly Report",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Remove Staff
                                    IconButton(
                                        onClick = { onDeleteStudent(student) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove Staff",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.75f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Work Shifts View
            ShiftsView(
                shifts = allShifts,
                onAddShift = onAddShift,
                onUpdateShift = onUpdateShift,
                onDeleteShift = onDeleteShift,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
        }
    }

    // Class delete confirmation dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Department?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete '${activeClass.name}'? This will erase all staff members and historical attendance records under this department. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClass()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Class edit dialog
    if (showEditDialog) {
        var editName by remember { mutableStateOf(activeClass.name) }
        val colors = listOf("#4F46E5", "#10B981", "#EF4444", "#F59E0B", "#06B6D4", "#8B5CF6", "#F43F5E")
        var selectedColor by remember { mutableStateOf(if (colors.contains(activeClass.colorHex)) activeClass.colorHex else colors.firstOrNull() ?: "#4F46E5") }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Department", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Department Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_class_name")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Department Accent Color",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        colors.forEach { hex ->
                            val color = parseColor(hex)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { selectedColor = hex }
                                    .padding(2.dp)
                            ) {
                                if (selectedColor.equals(hex, ignoreCase = true)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.5f))
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            onEditClass(editName.trim(), selectedColor)
                            showEditDialog = false
                        }
                    },
                    modifier = Modifier.testTag("submit_edit_class")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Share Employee Link / Passbook Modal Dialog
    shareLinkStudent?.let { student ->
        val assignedClass = activeClass
        val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        var lockToStaffOnly by remember { mutableStateOf(true) }
        val passbookLink = buildEmployeePassbookLink(
            portalBaseUrl = portalBaseUrl,
            student = student,
            firebaseUrl = firebaseUrl,
            workspace = firebaseWorkspace,
            lockToEmployee = lockToStaffOnly
        )
        var staffPortalDraft by remember(portalBaseUrl) { mutableStateOf(portalBaseUrl) }
        var editingStaffPortal by remember(portalBaseUrl) { mutableStateOf(portalBaseUrl.isBlank()) }

        val summaryStatement = buildString {
            appendLine("📋 *STAFF PROFILE & ATTENDANCE PASSBOOK*")
            appendLine("━━━━━━━━━━━━━━━━━━━")
            appendLine("👤 *Staff:* ${student.name}")
            appendLine("🆔 *Emp ID:* ${student.rollNumber}")
            appendLine("🏢 *Department:* ${assignedClass.name}")
            if (student.phone.isNotBlank()) appendLine("📞 *Phone:* ${student.phone}")
            appendLine("📅 *Date:* $formattedDate")
            appendLine("━━━━━━━━━━━━━━━━━━━")
            if (passbookLink.isNotBlank()) {
                appendLine("🔗 *LIVE VIEW-ONLY PASSBOOK LINK:*")
                appendLine(passbookLink)
                appendLine("━━━━━━━━━━━━━━━━━━━")
            }
            appendLine("Generated via Attendance & T-HISAB Manager")
        }

        AlertDialog(
            onDismissRequest = { shareLinkStudent = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Staff Passbook Details", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = student.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Emp ID: ${student.rollNumber} • Dept: ${assignedClass.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (student.phone.isNotBlank()) {
                                Text(
                                    text = "Phone: ${student.phone}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // GitHub Pages Passbook Link Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = if (portalBaseUrl.isNotBlank()) Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                        border = BorderStroke(
                            1.dp,
                            if (portalBaseUrl.isNotBlank()) Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFFF59E0B).copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (portalBaseUrl.isNotBlank()) "🌐 GitHub Pages Passbook Link" else "⚠️ Set GitHub Pages URL",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (portalBaseUrl.isNotBlank()) Color(0xFF065F46) else Color(0xFF92400E)
                                )
                                if (portalBaseUrl.isNotBlank() && !editingStaffPortal) {
                                    TextButton(
                                        onClick = { editingStaffPortal = true },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                        modifier = Modifier.height(24.dp)
                                    ) {
                                        Text("Edit", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            if (editingStaffPortal || portalBaseUrl.isBlank()) {
                                OutlinedTextField(
                                    value = staffPortalDraft,
                                    onValueChange = { staffPortalDraft = it },
                                    placeholder = { Text("https://username.github.io/repo", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Button(
                                    onClick = {
                                        val cleaned = normalizePortalBaseUrl(staffPortalDraft)
                                        onSavePortalBaseUrl(cleaned)
                                        staffPortalDraft = cleaned
                                        editingStaffPortal = false
                                    },
                                    modifier = Modifier.fillMaxWidth().height(34.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Save GitHub Pages URL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (passbookLink.isNotBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🔒 Lock to ${student.name} only (View-Only)",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF065F46),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Switch(
                                        checked = lockToStaffOnly,
                                        onCheckedChange = { lockToStaffOnly = it }
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF1E242B), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = passbookLink,
                                        fontSize = 10.sp,
                                        color = Color(0xFF93C5FD),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(passbookLink))
                                            android.widget.Toast.makeText(context, "Passbook link copied!", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Link", tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            try {
                                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(passbookLink)))
                                            } catch (_: Exception) {}
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(Icons.Default.OpenInNew, contentDescription = "Open Link", tint = Color(0xFF34D399), modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E242B),
                        border = BorderStroke(1.dp, Color(0xFF3B4758))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Passbook Statement Preview:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFB74D)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = summaryStatement,
                                fontSize = 11.sp,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // WhatsApp
                        Button(
                            onClick = {
                                val cleanPhone = student.phone.replace(Regex("[^0-9]"), "")
                                val intent = if (cleanPhone.length >= 10) {
                                    val fullNumber = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$fullNumber?text=${Uri.encode(summaryStatement)}"))
                                } else {
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(summaryStatement)}"))
                                }
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, summaryStatement)
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Statement"))
                                }
                                shareLinkStudent = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send Statement via WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // System share
                        Button(
                            onClick = {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Passbook Details - ${student.name}")
                                    putExtra(Intent.EXTRA_TEXT, summaryStatement)
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Staff Details"))
                                shareLinkStudent = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share via SMS / Other Apps", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Copy Text
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(summaryStatement))
                                android.widget.Toast.makeText(context, "Passbook statement copied to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
                                shareLinkStudent = null
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Copy Statement Text", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { shareLinkStudent = null }) {
                    Text("Close")
                }
            }
        )
    }
}

// Data holder for monthly report rows
data class DailyReportRow(
    val dayNumber: Int,
    val dateString: String,
    val dayOfWeek: String,
    val displayDate: String,
    val isWeekend: Boolean,
    val status: String,
    val checkInTime: String,
    val checkOutTime: String,
    val lateMinutes: Int,
    val lateTimeString: String,
    val earlyMinutes: Int,
    val earlyTimeString: String,
    val overtimeMinutes: Int = 0,
    val overtimeTimeString: String = "-",
    val earlyInOtMinutes: Int = 0,
    val lateOutOtMinutes: Int = 0,
    val overtimeBreakdown: String = "",
    val reason: String = "",
    val notes: String = ""
)

data class OvertimeResult(
    val earlyInMinutes: Int = 0,
    val lateOutMinutes: Int = 0,
    val totalOvertimeMinutes: Int = 0,
    val formattedString: String = "-",
    val breakdownString: String = ""
)

fun parseTimeToMinutes(timeStr: String): Int? {
    if (timeStr.isBlank()) return null
    val cleanStr = timeStr.trim()
    return try {
        val normalized = cleanStr.replace(Regex("(?i)(\\d+:\\d+)\\s*([AP]M)"), "$1 $2").uppercase(Locale.US)
        val sdf12 = SimpleDateFormat("hh:mm a", Locale.US)
        val date = try {
            sdf12.parse(normalized)
        } catch (e: Exception) {
            val sdf12Short = SimpleDateFormat("h:mm a", Locale.US)
            try {
                sdf12Short.parse(normalized)
            } catch (e2: Exception) {
                val sdf24 = SimpleDateFormat("HH:mm", Locale.US)
                try {
                    sdf24.parse(normalized)
                } catch (e3: Exception) {
                    val sdf24Short = SimpleDateFormat("H:mm", Locale.US)
                    try { sdf24Short.parse(normalized) } catch (e4: Exception) { null }
                }
            }
        }
        if (date != null) {
            val cal = Calendar.getInstance()
            cal.time = date
            cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        } else null
    } catch (e: Exception) {
        null
    }
}

fun parseShiftStartTimeToMinutes(timings: String?): Int? {
    if (timings.isNullOrBlank()) return null
    val parts = when {
        timings.contains(" - ") -> timings.split(" - ")
        timings.contains(" to ", ignoreCase = true) -> timings.split(Regex("(?i) to "))
        timings.contains("-") -> timings.split("-")
        else -> listOf(timings)
    }
    if (parts.isNotEmpty()) {
        return parseTimeToMinutes(parts[0].trim())
    }
    return null
}

fun parseShiftEndTimeToMinutes(timings: String?): Int? {
    if (timings.isNullOrBlank()) return null
    val parts = when {
        timings.contains(" - ") -> timings.split(" - ")
        timings.contains(" to ", ignoreCase = true) -> timings.split(Regex("(?i) to "))
        timings.contains("-") -> timings.split("-")
        else -> listOf(timings)
    }
    if (parts.size >= 2) {
        return parseTimeToMinutes(parts[1].trim())
    }
    return null
}

fun calculateLateTime(checkInTime: String, status: String, shiftStartTimeMins: Int = 9 * 60 + 30): Pair<Int, String> {
    if (status == "A" || status == "ABSENT" || status == "OFF" || status.startsWith("OFF") || status == "NO RECORD" || checkInTime.isBlank()) {
        return Pair(0, "-")
    }
    val checkInMins = parseTimeToMinutes(checkInTime) ?: return Pair(0, "-")

    if (checkInMins > shiftStartTimeMins) {
        val diffMins = checkInMins - shiftStartTimeMins
        val hrs = diffMins / 60
        val mins = diffMins % 60
        val lateStr = when {
            hrs > 0 && mins > 0 -> "${hrs}h ${mins}m late"
            hrs > 0 -> "${hrs}h late"
            else -> "${mins}m late"
        }
        return Pair(diffMins, lateStr)
    }
    return Pair(0, "-")
}

fun calculateGoneEarlyTime(checkOutTime: String, status: String, shiftEndTimeMins: Int = 20 * 60): Pair<Int, String> {
    if ((status != "P" && status != "PRESENT") || checkOutTime.isBlank()) {
        return Pair(0, "-")
    }
    val outMins = parseTimeToMinutes(checkOutTime) ?: return Pair(0, "-")

    if (outMins < shiftEndTimeMins) {
        val diffMins = shiftEndTimeMins - outMins
        val hrs = diffMins / 60
        val mins = diffMins % 60
        val earlyStr = when {
            hrs > 0 && mins > 0 -> "${hrs}h ${mins}m early"
            hrs > 0 -> "${hrs}h early"
            else -> "${mins}m early"
        }
        return Pair(diffMins, earlyStr)
    }
    return Pair(0, "-")
}

fun calculateOvertime(
    checkInTime: String,
    checkOutTime: String,
    status: String,
    shiftStartMins: Int = 9 * 60 + 30,
    shiftEndMins: Int = 20 * 60
): OvertimeResult {
    if (status == "A" || status == "ABSENT") {
        if (checkInTime.isBlank() || checkOutTime.isBlank()) {
            return OvertimeResult()
        }
        val inMins = parseTimeToMinutes(checkInTime) ?: return OvertimeResult()
        val outMins = parseTimeToMinutes(checkOutTime) ?: return OvertimeResult()
        val totalMins = if (outMins >= inMins) outMins - inMins else (outMins + 24 * 60 - inMins)
        if (totalMins <= 0) return OvertimeResult()
        val hrs = totalMins / 60
        val mins = totalMins % 60
        val otStr = when {
            hrs > 0 && mins > 0 -> "${hrs}h ${mins}m"
            hrs > 0 -> "${hrs}h"
            else -> "${mins}m"
        }
        return OvertimeResult(
            earlyInMinutes = 0,
            lateOutMinutes = totalMins,
            totalOvertimeMinutes = totalMins,
            formattedString = otStr,
            breakdownString = "$otStr (Off-duty OT)"
        )
    }

    if (status == "OFF" || status.startsWith("OFF") || status == "NO RECORD") {
        return OvertimeResult()
    }

    var earlyInMins = 0
    var lateOutMins = 0

    if (checkInTime.isNotBlank()) {
        val inMins = parseTimeToMinutes(checkInTime)
        if (inMins != null && inMins < shiftStartMins) {
            earlyInMins = shiftStartMins - inMins
        }
    }

    if (checkOutTime.isNotBlank()) {
        val outMins = parseTimeToMinutes(checkOutTime)
        if (outMins != null && outMins > shiftEndMins) {
            lateOutMins = outMins - shiftEndMins
        }
    }

    val totalMins = earlyInMins + lateOutMins
    if (totalMins <= 0) {
        return OvertimeResult()
    }

    val hrs = totalMins / 60
    val mins = totalMins % 60
    val otStr = when {
        hrs > 0 && mins > 0 -> "${hrs}h ${mins}m"
        hrs > 0 -> "${hrs}h"
        else -> "${mins}m"
    }

    val breakdownParts = mutableListOf<String>()
    if (earlyInMins > 0) {
        val eH = earlyInMins / 60
        val eM = earlyInMins % 60
        val eStr = if (eH > 0 && eM > 0) "${eH}h ${eM}m" else if (eH > 0) "${eH}h" else "${eM}m"
        breakdownParts.add("+$eStr Early")
    }
    if (lateOutMins > 0) {
        val lH = lateOutMins / 60
        val lM = lateOutMins % 60
        val lStr = if (lH > 0 && lM > 0) "${lH}h ${lM}m" else if (lH > 0) "${lH}h" else "${lM}m"
        breakdownParts.add("+$lStr Late")
    }

    return OvertimeResult(
        earlyInMinutes = earlyInMins,
        lateOutMinutes = lateOutMins,
        totalOvertimeMinutes = totalMins,
        formattedString = otStr,
        breakdownString = breakdownParts.joinToString(" + ")
    )
}

@Composable
fun ReportStatChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                fontSize = 8.5.sp,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AnalyticsView(
    activeClass: ClassEntity,
    stats: ClassStats,
    students: List<StudentEntity>,
    allRecords: List<com.example.data.AttendanceRecordEntity> = emptyList(),
    allShifts: List<ShiftEntity> = emptyList(),
    onUpdateRecord: (studentId: Long, dateString: String, status: String, checkInTime: String, checkOutTime: String, notes: String, reason: String) -> Unit = { _, _, _, _, _, _, _ -> },
    onViewReport: (StudentEntity) -> Unit = {}
) {
    // 0 = OVERVIEW, 1..N = Employees
    var selectedTabIndex by remember { mutableIntStateOf(if (students.isNotEmpty()) 1 else 0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Linear Tab Row arranged in horizontal scrollable style
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .testTag("analytics_linear_tabs_row"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Overview Tab
                item {
                    val isSelected = selectedTabIndex == 0
                    Surface(
                        onClick = { selectedTabIndex = 0 },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .height(42.dp)
                            .testTag("analytics_tab_overview")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "OVERVIEW",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                    fontSize = 12.5.sp
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Linear Employee Tabs
                itemsIndexed(students) { index, student ->
                    val tabIndex = index + 1
                    val isSelected = selectedTabIndex == tabIndex

                    Surface(
                        onClick = { selectedTabIndex = tabIndex },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .height(42.dp)
                            .testTag("analytics_tab_employee_${student.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.name.take(1).uppercase(Locale.getDefault()),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = student.name.uppercase(Locale.getDefault()),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Tab Content Display
        if (selectedTabIndex == 0 || students.isEmpty()) {
            AnalyticsOverviewContent(
                activeClass = activeClass,
                stats = stats,
                students = students,
                allRecords = allRecords,
                allShifts = allShifts,
                onUpdateRecord = onUpdateRecord,
                onSelectEmployeeTab = { studentIndex ->
                    selectedTabIndex = studentIndex + 1
                }
            )
        } else {
            val student = students.getOrNull(selectedTabIndex - 1)
            if (student != null) {
                EmployeeReportContentView(
                    selectedStudent = student,
                    activeClass = activeClass,
                    records = allRecords,
                    allShifts = allShifts,
                    onUpdateRecord = onUpdateRecord,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

enum class MonthMatrixViewMode {
    IN_TIME,
    STATUS,
    IN_OUT
}

data class DayColInfo(
    val dayNumber: Int,
    val dateLabel: String,
    val dayOfWeek: String,
    val dateKey: String,
    val isWeekend: Boolean,
    val fullDisplayDate: String
)

data class MatrixCellEditState(
    val student: StudentEntity,
    val dayNumber: Int,
    val dateKey: String,
    val displayDate: String,
    val initialStatus: String,
    val initialInTime: String,
    val initialOutTime: String,
    val initialNotes: String,
    val initialReason: String
)

data class StudentMonthSummaryStats(
    val presentCount: Int,
    val absentCount: Int,
    val halfDayCount: Int,
    val overtimeMinutes: Int,
    val lateCount: Int
)

@Composable
fun AllStaffMonthlyMatrixCard(
    activeClass: ClassEntity,
    students: List<StudentEntity>,
    allRecords: List<com.example.data.AttendanceRecordEntity>,
    allShifts: List<ShiftEntity>,
    onUpdateRecord: (studentId: Long, dateString: String, status: String, checkInTime: String, checkOutTime: String, notes: String, reason: String) -> Unit,
    onSelectEmployeeTab: (Int) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val calendar = remember { Calendar.getInstance() }
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) } // 1..12
    var viewMode by remember { mutableStateOf(MonthMatrixViewMode.IN_TIME) }
    var showMonthYearPicker by remember { mutableStateOf(false) }
    var editingCell by remember { mutableStateOf<MatrixCellEditState?>(null) }

    val monthNames = remember {
        listOf("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER")
    }
    val currentMonthName = monthNames.getOrElse(selectedMonth - 1) { "AUGUST" }

    val maxDays = remember(selectedYear, selectedMonth) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, selectedYear)
        cal.set(Calendar.MONTH, selectedMonth - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    val dayCols = remember(selectedYear, selectedMonth, maxDays) {
        (1..maxDays).map { day ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, selectedYear)
                set(Calendar.MONTH, selectedMonth - 1)
                set(Calendar.DAY_OF_MONTH, day)
            }
            val dayOfWeek = SimpleDateFormat("EEE", Locale.US).format(cal.time)
            val isWeekend = dayOfWeek == "Sat" || dayOfWeek == "Sun"
            val dateKey = String.format(Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth, day)
            val dateLabel = String.format(Locale.US, "%02d/%02d", day, selectedMonth)
            val fullDisplayDate = SimpleDateFormat("dd MMMM yyyy (EEE)", Locale.US).format(cal.time)
            DayColInfo(day, dateLabel, dayOfWeek, dateKey, isWeekend, fullDisplayDate)
        }
    }

    val monthPrefix = remember(selectedYear, selectedMonth) {
        String.format(Locale.US, "%04d-%02d", selectedYear, selectedMonth)
    }

    val recordsByStudent = remember(allRecords, monthPrefix) {
        allRecords.filter { it.dateString.startsWith(monthPrefix) }
            .groupBy { it.studentId }
            .mapValues { (_, list) -> list.associateBy { it.dateString } }
    }

    val studentStatsMap = remember(students, recordsByStudent, dayCols, allShifts, activeClass) {
        students.associate { student ->
            val recMap = recordsByStudent[student.id] ?: emptyMap()
            var p = 0
            var a = 0
            var hd = 0
            var late = 0
            var otMinutes = 0

            val studentShift = allShifts.find { it.id == student.shiftId }
                ?: allShifts.firstOrNull()
            val shiftStartMins = parseShiftStartTimeToMinutes(studentShift?.timings) ?: (9 * 60 + 30)
            val shiftEndMins = parseShiftEndTimeToMinutes(studentShift?.timings) ?: (20 * 60)

            for (day in dayCols) {
                val rec = recMap[day.dateKey]
                val status = rec?.status ?: ""
                if (status == "P" || status == "PRESENT") {
                    p++
                    val inTime = rec?.checkInTime ?: ""
                    val outTime = rec?.checkOutTime ?: ""
                    if (inTime.isNotBlank()) {
                        val (lateM, _) = calculateLateTime(inTime, status, shiftStartMins)
                        if (lateM > 0) late++
                    }
                    if (outTime.isNotBlank()) {
                        val otResult = calculateOvertime(inTime, outTime, status, shiftStartMins, shiftEndMins)
                        otMinutes += otResult.totalOvertimeMinutes
                    }
                } else if (status == "A" || status == "ABSENT") {
                    a++
                } else if (status.startsWith("HD") || status == "HALF-DAY") {
                    hd++
                }
            }
            student.id to StudentMonthSummaryStats(p, a, hd, otMinutes, late)
        }
    }

    val dailyPresentCounts = remember(dayCols, students, recordsByStudent) {
        dayCols.map { day ->
            students.count { student ->
                val rec = recordsByStudent[student.id]?.get(day.dateKey)
                val st = rec?.status ?: ""
                st == "P" || st == "PRESENT" || st.startsWith("HD") || st == "HALF-DAY"
            }
        }
    }

    val totalPresentOverall = remember(studentStatsMap) { studentStatsMap.values.sumOf { it.presentCount } }
    val totalAbsentOverall = remember(studentStatsMap) { studentStatsMap.values.sumOf { it.absentCount } }
    val totalHalfDaysOverall = remember(studentStatsMap) { studentStatsMap.values.sumOf { it.halfDayCount } }
    val totalManDaysPossible = students.size * maxDays
    val effectivePresent = totalPresentOverall + (totalHalfDaysOverall * 0.5)
    val monthlyAttendancePercent = if (totalManDaysPossible > 0) ((effectivePresent / totalManDaysPossible) * 100).toInt() else 0

    fun generateExportText(): String {
        val sb = StringBuilder()
        sb.appendLine("==========================================")
        sb.appendLine("MONTHLY ATTENDANCE REGISTER - $currentMonthName $selectedYear")
        sb.appendLine("Department: ${activeClass.name} | Staff Count: ${students.size}")
        sb.appendLine("==========================================")
        sb.appendLine("NAME\t" + dayCols.joinToString("\t") { it.dateLabel } + "\tP\tA\tHD\tOT")
        for (st in students) {
            val recMap = recordsByStudent[st.id] ?: emptyMap()
            val stats = studentStatsMap[st.id] ?: StudentMonthSummaryStats(0, 0, 0, 0, 0)
            val dayValues = dayCols.joinToString("\t") { day ->
                val rec = recMap[day.dateKey]
                val s = rec?.status ?: if (day.isWeekend) "OFF" else "-"
                val inT = rec?.checkInTime ?: ""
                when {
                    s == "P" || s == "PRESENT" -> inT.ifEmpty { "P" }
                    s == "A" || s == "ABSENT" -> "A"
                    s.startsWith("HD") || s == "HALF-DAY" -> if (inT.isNotEmpty()) inT else "HD"
                    day.isWeekend -> "OFF"
                    else -> "-"
                }
            }
            val otStr = if (stats.overtimeMinutes > 0) "${stats.overtimeMinutes / 60}h" else "0h"
            sb.appendLine("${st.name}\t$dayValues\t${stats.presentCount}\t${stats.absentCount}\t${stats.halfDayCount}\t$otStr")
        }
        sb.appendLine("==========================================")
        return sb.toString()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Title, Subtitle, Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Monthly Staff Register",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "All employees attendance & in-times in one sheet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            val text = generateExportText()
                            clipboardManager.setText(AnnotatedString(text))
                            Toast.makeText(context, "Copied register to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Register",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val text = generateExportText()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, text)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Monthly Register")
                            context.startActivity(shareIntent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Register",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Month & Year Navigator: ◀  AUGUST 2026  ▶
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (selectedMonth == 1) {
                                selectedMonth = 12
                                selectedYear -= 1
                            } else {
                                selectedMonth -= 1
                            }
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clickable { showMonthYearPicker = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$currentMonthName $selectedYear",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            if (selectedMonth == 12) {
                                selectedMonth = 1
                                selectedYear += 1
                            } else {
                                selectedMonth += 1
                            }
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
                    }
                }
            }

            // View Mode Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Display Mode:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = viewMode == MonthMatrixViewMode.IN_TIME,
                        onClick = { viewMode = MonthMatrixViewMode.IN_TIME },
                        label = { Text("09:00AM (In-Time)", fontSize = 10.5.sp, fontWeight = FontWeight.Bold) }
                    )
                    FilterChip(
                        selected = viewMode == MonthMatrixViewMode.STATUS,
                        onClick = { viewMode = MonthMatrixViewMode.STATUS },
                        label = { Text("P / A / HD", fontSize = 10.5.sp, fontWeight = FontWeight.Bold) }
                    )
                    FilterChip(
                        selected = viewMode == MonthMatrixViewMode.IN_OUT,
                        onClick = { viewMode = MonthMatrixViewMode.IN_OUT },
                        label = { Text("In → Out", fontSize = 10.5.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Quick Month Metrics Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ReportStatChip(
                    label = "Staff",
                    value = "${students.size}",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                ReportStatChip(
                    label = "Days",
                    value = "$maxDays",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
                ReportStatChip(
                    label = "Present",
                    value = "$totalPresentOverall",
                    color = Color(0xFF1B5E20),
                    modifier = Modifier.weight(1f)
                )
                ReportStatChip(
                    label = "Absent",
                    value = "$totalAbsentOverall",
                    color = Color(0xFFB71C1C),
                    modifier = Modifier.weight(1f)
                )
                ReportStatChip(
                    label = "Att Rate",
                    value = "$monthlyAttendancePercent%",
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }

            // The Master Matrix Table
            if (students.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No staff in ${activeClass.name}. Add employees to see monthly sheet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Pinned Left Column: NAME (width 95.dp)
                        Column(modifier = Modifier.width(95.dp)) {
                            // Header: NAME
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 6.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "NAME",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            HorizontalDivider()

                            // Staff Rows
                            students.forEachIndexed { sIndex, student ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .background(
                                            if (sIndex % 2 == 0) MaterialTheme.colorScheme.surface
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                        )
                                        .clickable { onSelectEmployeeTab(sIndex) }
                                        .padding(horizontal = 6.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Column(verticalArrangement = Arrangement.Center) {
                                        Text(
                                            text = student.name.uppercase(),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 10.5.sp,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (student.rollNumber.isNotEmpty()) "#${student.rollNumber}" else "Staff",
                                            fontSize = 8.5.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                HorizontalDivider()
                            }

                            // Bottom Row: PRESENT
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 6.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "TOTAL P",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.5.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Vertical Separator
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(46.dp + (students.size * 51).dp + 38.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )

                        // Horizontally Scrollable Days + Summary Section
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Column {
                                // Header Row of Dates + Summary
                                Row(modifier = Modifier.height(46.dp)) {
                                    dayCols.forEach { day ->
                                        Box(
                                            modifier = Modifier
                                                .width(74.dp)
                                                .fillMaxHeight()
                                                .background(
                                                    if (day.isWeekend) Color(0xFFFFF3E0)
                                                    else MaterialTheme.colorScheme.surfaceVariant
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = day.dateLabel, // "01/08" as in video!
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 10.5.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = day.dayOfWeek,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 8.sp,
                                                    color = if (day.isWeekend) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    // Summary Columns Headers
                                    Box(
                                        modifier = Modifier
                                            .width(38.dp)
                                            .fillMaxHeight()
                                            .background(Color(0xFFE8F5E9)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("P", fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color(0xFF1B5E20))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(38.dp)
                                            .fillMaxHeight()
                                            .background(Color(0xFFFFEBEE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("A", fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color(0xFFB71C1C))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .fillMaxHeight()
                                            .background(Color(0xFFFFF3E0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("HD", fontWeight = FontWeight.Black, fontSize = 10.5.sp, color = Color(0xFFBF360C))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(48.dp)
                                            .fillMaxHeight()
                                            .background(Color(0xFFE0F2F1)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("OT", fontWeight = FontWeight.Black, fontSize = 10.5.sp, color = Color(0xFF00695C))
                                    }
                                }

                                HorizontalDivider()

                                // Staff Rows
                                students.forEachIndexed { sIndex, student ->
                                    val recMap = recordsByStudent[student.id] ?: emptyMap()
                                    val stats = studentStatsMap[student.id] ?: StudentMonthSummaryStats(0, 0, 0, 0, 0)
                                    val rowBg = if (sIndex % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)

                                    Row(
                                        modifier = Modifier
                                            .height(50.dp)
                                            .background(rowBg),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        dayCols.forEach { day ->
                                            val rec = recMap[day.dateKey]
                                            val status = rec?.status ?: if (day.isWeekend) "OFF" else ""
                                            val inTime = rec?.checkInTime ?: ""
                                            val outTime = rec?.checkOutTime ?: ""

                                            Box(
                                                modifier = Modifier
                                                    .width(74.dp)
                                                    .fillMaxHeight()
                                                    .padding(2.dp)
                                                    .clickable {
                                                        editingCell = MatrixCellEditState(
                                                            student = student,
                                                            dayNumber = day.dayNumber,
                                                            dateKey = day.dateKey,
                                                            displayDate = day.fullDisplayDate,
                                                            initialStatus = status,
                                                            initialInTime = inTime,
                                                            initialOutTime = outTime,
                                                            initialNotes = rec?.notes ?: "",
                                                            initialReason = rec?.reason ?: ""
                                                        )
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                when (viewMode) {
                                                    MonthMatrixViewMode.IN_TIME -> {
                                                        when {
                                                            status == "P" || status == "PRESENT" -> {
                                                                Surface(
                                                                    shape = RoundedCornerShape(5.dp),
                                                                    color = Color(0xFFE8F5E9),
                                                                    border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                                                                    modifier = Modifier.padding(1.dp)
                                                                ) {
                                                                    Box(
                                                                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 2.dp),
                                                                        contentAlignment = Alignment.Center
                                                                    ) {
                                                                        Text(
                                                                            text = inTime.ifEmpty { "P" }, // "09:00AM" as in video!
                                                                            fontWeight = FontWeight.Black,
                                                                            fontSize = if (inTime.isNotEmpty()) 8.5.sp else 10.5.sp,
                                                                            color = Color(0xFF1B5E20),
                                                                            maxLines = 1
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                            status == "A" || status == "ABSENT" -> {
                                                                Surface(
                                                                    shape = RoundedCornerShape(5.dp),
                                                                    color = Color(0xFFFFEBEE),
                                                                    border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                                                                    modifier = Modifier.padding(1.dp)
                                                                ) {
                                                                    Box(
                                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                                        contentAlignment = Alignment.Center
                                                                    ) {
                                                                        Text(
                                                                            text = "A",
                                                                            fontWeight = FontWeight.Black,
                                                                            fontSize = 10.5.sp,
                                                                            color = Color(0xFFB71C1C)
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                            status.startsWith("HD") || status == "HALF-DAY" -> {
                                                                Surface(
                                                                    shape = RoundedCornerShape(5.dp),
                                                                    color = Color(0xFFFFF3E0),
                                                                    border = BorderStroke(1.dp, Color(0xFFFFE0B2)),
                                                                    modifier = Modifier.padding(1.dp)
                                                                ) {
                                                                    Box(
                                                                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 2.dp),
                                                                        contentAlignment = Alignment.Center
                                                                    ) {
                                                                        Text(
                                                                            text = if (inTime.isNotEmpty()) inTime else "HD",
                                                                            fontWeight = FontWeight.Bold,
                                                                            fontSize = 8.sp,
                                                                            color = Color(0xFFBF360C),
                                                                            maxLines = 1
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                            day.isWeekend || status.startsWith("OFF") -> {
                                                                Text(
                                                                    text = "OFF",
                                                                    fontSize = 8.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color(0xFFB0BEC5)
                                                                )
                                                            }
                                                            else -> {
                                                                Text(
                                                                    text = "-",
                                                                    fontSize = 11.sp,
                                                                    color = Color(0xFFCFD8DC),
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                            }
                                                        }
                                                    }
                                                    MonthMatrixViewMode.STATUS -> {
                                                        when {
                                                            status == "P" || status == "PRESENT" -> {
                                                                Text("P", fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color(0xFF1B5E20))
                                                            }
                                                            status == "A" || status == "ABSENT" -> {
                                                                Text("A", fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color(0xFFB71C1C))
                                                            }
                                                            status.startsWith("HD") || status == "HALF-DAY" -> {
                                                                Text("HD", fontWeight = FontWeight.Bold, fontSize = 9.5.sp, color = Color(0xFFBF360C))
                                                            }
                                                            day.isWeekend || status.startsWith("OFF") -> {
                                                                Text("OFF", fontSize = 8.sp, color = Color(0xFFB0BEC5), fontWeight = FontWeight.Bold)
                                                            }
                                                            else -> {
                                                                Text("-", fontSize = 11.sp, color = Color(0xFFCFD8DC))
                                                            }
                                                        }
                                                    }
                                                    MonthMatrixViewMode.IN_OUT -> {
                                                        if (inTime.isNotEmpty() || outTime.isNotEmpty()) {
                                                            Column(
                                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                                verticalArrangement = Arrangement.Center
                                                            ) {
                                                                if (inTime.isNotEmpty()) {
                                                                    Text(inTime, fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), maxLines = 1)
                                                                }
                                                                if (outTime.isNotEmpty()) {
                                                                    Text(outTime, fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1), maxLines = 1)
                                                                }
                                                            }
                                                        } else if (status == "A" || status == "ABSENT") {
                                                            Text("A", fontWeight = FontWeight.Black, fontSize = 10.5.sp, color = Color(0xFFB71C1C))
                                                        } else {
                                                            Text("-", fontSize = 11.sp, color = Color(0xFFCFD8DC))
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Summary columns
                                        Box(
                                            modifier = Modifier.width(38.dp).fillMaxHeight(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("${stats.presentCount}", fontWeight = FontWeight.Black, fontSize = 10.5.sp, color = Color(0xFF1B5E20))
                                        }
                                        Box(
                                            modifier = Modifier.width(38.dp).fillMaxHeight(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("${stats.absentCount}", fontWeight = FontWeight.Black, fontSize = 10.5.sp, color = Color(0xFFB71C1C))
                                        }
                                        Box(
                                            modifier = Modifier.width(40.dp).fillMaxHeight(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("${stats.halfDayCount}", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = Color(0xFFBF360C))
                                        }
                                        Box(
                                            modifier = Modifier.width(48.dp).fillMaxHeight(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val otText = if (stats.overtimeMinutes > 0) "${stats.overtimeMinutes / 60}h" else "0h"
                                            Text(otText, fontWeight = FontWeight.Bold, fontSize = 9.5.sp, color = Color(0xFF00695C))
                                        }
                                    }

                                    HorizontalDivider()
                                }

                                // Bottom Daily Total Row
                                Row(
                                    modifier = Modifier
                                        .height(38.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    dailyPresentCounts.forEach { pCount ->
                                        Box(
                                            modifier = Modifier
                                                .width(74.dp)
                                                .fillMaxHeight(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$pCount",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Box(modifier = Modifier.width(38.dp).fillMaxHeight())
                                    Box(modifier = Modifier.width(38.dp).fillMaxHeight())
                                    Box(modifier = Modifier.width(40.dp).fillMaxHeight())
                                    Box(modifier = Modifier.width(48.dp).fillMaxHeight())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Month & Year Picker Dialog
    if (showMonthYearPicker) {
        AlertDialog(
            onDismissRequest = { showMonthYearPicker = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Month & Year",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    IconButton(onClick = { showMonthYearPicker = false }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Select Year", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val availableYears = listOf(2024, 2025, 2026, 2027)
                        availableYears.forEach { yr ->
                            val isYrSelected = yr == selectedYear
                            FilterChip(
                                selected = isYrSelected,
                                onClick = { selectedYear = yr },
                                label = { Text(yr.toString(), fontWeight = if (isYrSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) }
                            )
                        }
                    }

                    Text("Select Month", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val shortMonths = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                    for (rowIndex in 0..3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (colIndex in 0..2) {
                                val mIndex = rowIndex * 3 + colIndex
                                val mNumber = mIndex + 1
                                val isMSelected = mNumber == selectedMonth
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .background(
                                            color = if (isMSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedMonth = mNumber
                                            showMonthYearPicker = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = shortMonths[mIndex],
                                        fontWeight = if (isMSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = if (isMSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    TextButton(
                        onClick = {
                            val nowCal = Calendar.getInstance()
                            selectedYear = nowCal.get(Calendar.YEAR)
                            selectedMonth = nowCal.get(Calendar.MONTH) + 1
                            showMonthYearPicker = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Jump to Current Month (${SimpleDateFormat("MMM yyyy", Locale.US).format(Date())})", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showMonthYearPicker = false }) {
                    Text("Done")
                }
            }
        )
    }

    // Cell Quick Edit Dialog
    editingCell?.let { cellData ->
        var editStatus by remember(cellData) {
            mutableStateOf(
                if (cellData.initialStatus.isNotBlank() && cellData.initialStatus != "NO RECORD" && !cellData.initialStatus.startsWith("OFF")) {
                    cellData.initialStatus
                } else {
                    "P"
                }
            )
        }
        var editInTime by remember(cellData) { mutableStateOf(cellData.initialInTime) }
        var editOutTime by remember(cellData) { mutableStateOf(cellData.initialOutTime) }
        var editNotes by remember(cellData) { mutableStateOf(cellData.initialNotes) }
        var editReason by remember(cellData) { mutableStateOf(cellData.initialReason) }

        AlertDialog(
            onDismissRequest = { editingCell = null },
            title = {
                Column {
                    Text(
                        text = "Edit Attendance",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "${cellData.student.name.uppercase()} • ${cellData.displayDate}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Status", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val statusOptions = listOf(
                            "P" to "Present",
                            "A" to "Absent",
                            "HD MOR" to "Half(M)",
                            "HD EVE" to "Half(E)"
                        )
                        statusOptions.forEach { (code, label) ->
                            val isSel = editStatus == code
                            val selBg = when (code) {
                                "P" -> Color(0xFF1B5E20)
                                "A" -> Color(0xFFB71C1C)
                                "HD MOR" -> Color(0xFFBF360C)
                                else -> Color(0xFF6A1B9A)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .background(
                                        color = if (isSel) selBg else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        editStatus = code
                                        if (code == "A") {
                                            editInTime = ""
                                            editOutTime = ""
                                        } else if (editInTime.isEmpty()) {
                                            editInTime = "09:00 AM"
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = code,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    if (editStatus != "A") {
                        OutlinedTextField(
                            value = editInTime,
                            onValueChange = { editInTime = it },
                            label = { Text("Check-In Time") },
                            placeholder = { Text("e.g. 09:00 AM") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            trailingIcon = {
                                TextButton(onClick = {
                                    editInTime = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
                                }) {
                                    Text("Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        )

                        OutlinedTextField(
                            value = editOutTime,
                            onValueChange = { editOutTime = it },
                            label = { Text("Check-Out Time") },
                            placeholder = { Text("e.g. 06:00 PM") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            trailingIcon = {
                                TextButton(onClick = {
                                    editOutTime = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
                                }) {
                                    Text("Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                    }

                    OutlinedTextField(
                        value = editNotes,
                        onValueChange = { editNotes = it },
                        label = { Text("Notes / Remarks (Optional)") },
                        placeholder = { Text("Late reason, task, or notes") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateRecord(
                            cellData.student.id,
                            cellData.dateKey,
                            editStatus,
                            editInTime,
                            editOutTime,
                            editNotes,
                            editReason
                        )
                        Toast.makeText(context, "Saved for ${cellData.student.name}", Toast.LENGTH_SHORT).show()
                        editingCell = null
                    }
                ) {
                    Text("Save Attendance", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCell = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AnalyticsOverviewContent(
    activeClass: ClassEntity,
    stats: ClassStats,
    students: List<StudentEntity>,
    allRecords: List<com.example.data.AttendanceRecordEntity> = emptyList(),
    allShifts: List<ShiftEntity> = emptyList(),
    onUpdateRecord: (studentId: Long, dateString: String, status: String, checkInTime: String, checkOutTime: String, notes: String, reason: String) -> Unit = { _, _, _, _, _, _, _ -> },
    onSelectEmployeeTab: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // FULL REPORT OF ALL EMPLOYEES IN ONE PAGE - MONTHWISE (Learned from video)
        item {
            AllStaffMonthlyMatrixCard(
                activeClass = activeClass,
                students = students,
                allRecords = allRecords,
                allShifts = allShifts,
                onUpdateRecord = onUpdateRecord,
                onSelectEmployeeTab = onSelectEmployeeTab
            )
        }
        // High level overview rate card with custom radial draw canvas
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(90.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val color = parseColor(activeClass.colorHex)
                        Canvas(modifier = Modifier.size(80.dp)) {
                            drawCircle(
                                color = color.copy(alpha = 0.12f),
                                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = color,
                                startAngle = -90f,
                                sweepAngle = (stats.averageAttendanceRate / 100f * 360f).toFloat(),
                                useCenter = false,
                                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${stats.averageAttendanceRate.toInt()}%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Rate",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Department Summary",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = activeClass.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Column {
                                Text(text = "${stats.totalStudents}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(text = "Staff", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column {
                                Text(text = "${stats.totalSessions}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(text = "Sessions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Quick Staff List with Direct 1-Click Tab Switch
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Staff Reports (Switch Tabs Above)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Click any employee below or their tab above to view the complete monthly report.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (students.isEmpty()) {
                        Text(
                            text = "No staff members in this department.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        students.forEachIndexed { index, student ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectEmployeeTab(index) }
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = student.name.take(1).uppercase(Locale.getDefault()),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = student.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Emp ID: ${student.rollNumber}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onSelectEmployeeTab(index) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Assessment,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "View Tab",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Custom horizontal state ledger bars
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Aggregated Record Metrics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val total = (stats.presentCount + stats.absentCount + stats.halfDayCount).toDouble()
                    val pPercent = if (total == 0.0) 0.0 else (stats.presentCount / total) * 100
                    val aPercent = if (total == 0.0) 0.0 else (stats.absentCount / total) * 100
                    val hdMorPercent = if (total == 0.0) 0.0 else (stats.halfDayMorCount / total) * 100
                    val hdEvePercent = if (total == 0.0) 0.0 else (stats.halfDayEveCount / total) * 100

                    StatBar(label = "P (Present)", count = stats.presentCount, percentage = pPercent, color = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.height(10.dp))
                    StatBar(label = "A (Absent)", count = stats.absentCount, percentage = aPercent, color = Color(0xFFC62828))
                    Spacer(modifier = Modifier.height(10.dp))
                    StatBar(label = "HD MOR (Morning Half-Day)", count = stats.halfDayMorCount, percentage = hdMorPercent, color = Color(0xFFE65100))
                    Spacer(modifier = Modifier.height(10.dp))
                    StatBar(label = "HD EVE (Evening Half-Day)", count = stats.halfDayEveCount, percentage = hdEvePercent, color = Color(0xFF7B1FA2))
                }
            }
        }

        // Low attendance risk warning banner
        if (stats.lowAttendanceStudents.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ATTENTION REQUIRED (<75% Attendance)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        stats.lowAttendanceStudents.forEach { (student, rate) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${student.name} (${student.rollNumber})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                Text(
                                    text = "${rate.toInt()}%",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Excellent! No staff members have attendance below 75% at the moment.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeeReportContentView(
    selectedStudent: StudentEntity,
    activeClass: ClassEntity,
    records: List<com.example.data.AttendanceRecordEntity>,
    allShifts: List<ShiftEntity>,
    onUpdateRecord: (studentId: Long, dateString: String, status: String, checkInTime: String, checkOutTime: String, notes: String, reason: String) -> Unit = { _, _, _, _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var recipientPhone by remember(selectedStudent) { mutableStateOf(selectedStudent.phone) }
    val calendar = remember { Calendar.getInstance() }
    var currentYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var currentMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) }

    var copyNoticeMessage by remember { mutableStateOf<String?>(null) }
    var editingRow by remember { mutableStateOf<DailyReportRow?>(null) }
    var includeRemarksInShare by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val monthNames = remember {
        listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    }

    val studentShift = allShifts.find { it.id == selectedStudent.shiftId }
    val shiftStartMins = parseShiftStartTimeToMinutes(studentShift?.timings) ?: (9 * 60 + 30) // default 09:30 AM
    val shiftEndMins = parseShiftEndTimeToMinutes(studentShift?.timings) ?: (20 * 60) // default 20:00 (08:00 PM)

    val daysInMonth = remember(currentYear, currentMonth, selectedStudent, records, studentShift) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, currentYear)
        cal.set(Calendar.MONTH, currentMonth - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val sdfDateKey = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val sdfDayName = SimpleDateFormat("EEE", Locale.US)

        val list = mutableListOf<DailyReportRow>()
        val studentRecs = records.filter { it.studentId == selectedStudent.id }

        for (day in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dateStr = sdfDateKey.format(cal.time)
            val dayName = sdfDayName.format(cal.time)
            val displayDate = String.format(Locale.US, "%02d %s (%s)", day, monthNames[currentMonth - 1].take(3), dayName)
            val isWeekend = dayName == "Sat" || dayName == "Sun"

            val record = studentRecs.find { it.dateString == dateStr }
            val status = record?.status ?: if (isWeekend) "OFF (WEEKEND)" else "NO RECORD"
            val inTime = record?.checkInTime ?: ""
            val outTime = record?.checkOutTime ?: ""
            val notes = record?.notes ?: ""
            val reason = record?.reason ?: ""

            val (lateMins, lateStr) = calculateLateTime(inTime, status, shiftStartMins)
            val (earlyMins, earlyStr) = calculateGoneEarlyTime(outTime, status, shiftEndMins)
            val otResult = calculateOvertime(inTime, outTime, status, shiftStartMins, shiftEndMins)

            list.add(
                DailyReportRow(
                    dayNumber = day,
                    dateString = dateStr,
                    dayOfWeek = dayName,
                    displayDate = displayDate,
                    isWeekend = isWeekend,
                    status = status,
                    checkInTime = inTime,
                    checkOutTime = outTime,
                    lateMinutes = lateMins,
                    lateTimeString = lateStr,
                    earlyMinutes = earlyMins,
                    earlyTimeString = earlyStr,
                    overtimeMinutes = otResult.totalOvertimeMinutes,
                    overtimeTimeString = otResult.formattedString,
                    earlyInOtMinutes = otResult.earlyInMinutes,
                    lateOutOtMinutes = otResult.lateOutMinutes,
                    overtimeBreakdown = otResult.breakdownString,
                    reason = reason,
                    notes = notes
                )
            )
        }
        list
    }

    val totalDays = daysInMonth.size
    val presentCount = daysInMonth.count { it.status == "P" || it.status == "PRESENT" }
    val absentCount = daysInMonth.count { it.status == "A" || it.status == "ABSENT" }
    val halfDayMorCount = daysInMonth.count { it.status == "HD MOR" }
    val halfDayEveCount = daysInMonth.count { it.status == "HD EVE" }
    val legacyHalfDayCount = daysInMonth.count { it.status == "HALF-DAY" }
    val totalHalfDayCount = halfDayMorCount + halfDayEveCount + legacyHalfDayCount
    val offCount = daysInMonth.count { it.status == "OFF (WEEKEND)" || it.status == "NO RECORD" || it.status == "OFF" }
    val lateDaysCount = daysInMonth.count { it.lateMinutes > 0 }
    val totalLateMinutes = daysInMonth.sumOf { it.lateMinutes }
    val earlyDaysCount = daysInMonth.count { it.earlyMinutes > 0 }
    val totalEarlyMinutes = daysInMonth.sumOf { it.earlyMinutes }

    val overtimeDaysCount = daysInMonth.count { it.overtimeMinutes > 0 }
    val totalOvertimeMinutes = daysInMonth.sumOf { it.overtimeMinutes }
    val totalEarlyInOtMinutes = daysInMonth.sumOf { it.earlyInOtMinutes }
    val totalLateOutOtMinutes = daysInMonth.sumOf { it.lateOutOtMinutes }

    val lateHrs = totalLateMinutes / 60
    val lateMins = totalLateMinutes % 60
    val formattedTotalLate = when {
        lateHrs > 0 && lateMins > 0 -> "${lateHrs}h ${lateMins}m"
        lateHrs > 0 -> "${lateHrs}h"
        lateMins > 0 -> "${lateMins}m"
        else -> "0m"
    }

    val earlyHrs = totalEarlyMinutes / 60
    val earlyMins = totalEarlyMinutes % 60
    val formattedTotalEarly = when {
        earlyHrs > 0 && earlyMins > 0 -> "${earlyHrs}h ${earlyMins}m"
        earlyHrs > 0 -> "${earlyHrs}h"
        earlyMins > 0 -> "${earlyMins}m"
        else -> "0m"
    }

    val otHrs = totalOvertimeMinutes / 60
    val otMins = totalOvertimeMinutes % 60
    val formattedTotalOvertime = when {
        otHrs > 0 && otMins > 0 -> "${otHrs}h ${otMins}m"
        otHrs > 0 -> "${otHrs}h"
        otMins > 0 -> "${otMins}m"
        else -> "0m"
    }

    val eOtHrs = totalEarlyInOtMinutes / 60
    val eOtMins = totalEarlyInOtMinutes % 60
    val formattedEarlyInOt = when {
        eOtHrs > 0 && eOtMins > 0 -> "${eOtHrs}h ${eOtMins}m"
        eOtHrs > 0 -> "${eOtHrs}h"
        eOtMins > 0 -> "${eOtMins}m"
        else -> "0m"
    }

    val lOtHrs = totalLateOutOtMinutes / 60
    val lOtMins = totalLateOutOtMinutes % 60
    val formattedLateOutOt = when {
        lOtHrs > 0 && lOtMins > 0 -> "${lOtHrs}h ${lOtMins}m"
        lOtHrs > 0 -> "${lOtHrs}h"
        lOtMins > 0 -> "${lOtMins}m"
        else -> "0m"
    }

    val avgLatePerDay = if (lateDaysCount > 0) totalLateMinutes / lateDaysCount else 0
    val avgLateHrs = avgLatePerDay / 60
    val avgLateMins = avgLatePerDay % 60
    val formattedAvgLate = when {
        avgLateHrs > 0 && avgLateMins > 0 -> "${avgLateHrs}h ${avgLateMins}m"
        avgLateHrs > 0 -> "${avgLateHrs}h"
        avgLateMins > 0 -> "${avgLateMins}m"
        else -> "0m"
    }

    val avgEarlyPerDay = if (earlyDaysCount > 0) totalEarlyMinutes / earlyDaysCount else 0
    val avgEarlyHrs = avgEarlyPerDay / 60
    val avgEarlyMins = avgEarlyPerDay % 60
    val formattedAvgEarly = when {
        avgEarlyHrs > 0 && avgEarlyMins > 0 -> "${avgEarlyHrs}h ${avgEarlyMins}m"
        avgEarlyHrs > 0 -> "${avgEarlyHrs}h"
        avgEarlyMins > 0 -> "${avgEarlyMins}m"
        else -> "0m"
    }

    val avgOtPerDay = if (overtimeDaysCount > 0) totalOvertimeMinutes / overtimeDaysCount else 0
    val avgOtHrs = avgOtPerDay / 60
    val avgOtMins = avgOtPerDay % 60
    val formattedAvgOvertime = when {
        avgOtHrs > 0 && avgOtMins > 0 -> "${avgOtHrs}h ${avgOtMins}m"
        avgOtHrs > 0 -> "${avgOtHrs}h"
        avgOtMins > 0 -> "${avgOtMins}m"
        else -> "0m"
    }

    fun generateReportText(isWhatsApp: Boolean = false): String {
        val b = StringBuilder()
        val bold = if (isWhatsApp) "*" else ""

        b.appendLine("${bold}EMPLOYEE MONTHLY ATTENDANCE REPORT${bold}")
        b.appendLine("===============================")
        b.appendLine("Employee: ${selectedStudent.name}")
        b.appendLine("Emp ID / Roll: ${selectedStudent.rollNumber}")
        b.appendLine("Department: ${activeClass.name}")
        b.appendLine("Shift: ${studentShift?.name ?: "Default"} (${studentShift?.timings ?: "09:30 AM - 08:00 PM"})")
        b.appendLine("Month: ${monthNames[currentMonth - 1]} $currentYear")
        b.appendLine("===============================")
        b.appendLine("${bold}SUMMARY:${bold}")
        b.appendLine("• Present (P): $presentCount days")
        b.appendLine("• Absent (A): $absentCount days")
        b.appendLine("• Half-Day MOR: $halfDayMorCount days")
        b.appendLine("• Half-Day EVE: $halfDayEveCount days")
        if (legacyHalfDayCount > 0) b.appendLine("• Half-Day (Legacy): $legacyHalfDayCount days")
        b.appendLine("• Total Half-Days: $totalHalfDayCount days")
        b.appendLine("• Off / Weekend: $offCount days")
        b.appendLine("• Late In: $lateDaysCount days (Total: $formattedTotalLate, Avg: $formattedAvgLate/day)")
        b.appendLine("• Gone Early: $earlyDaysCount days (Total: $formattedTotalEarly, Avg: $formattedAvgEarly/day)")
        b.appendLine("• Overtime (OT): $overtimeDaysCount days (Total: $formattedTotalOvertime, Avg: $formattedAvgOvertime/day)")
        if (totalEarlyInOtMinutes > 0 || totalLateOutOtMinutes > 0) {
            b.appendLine("  - Advance In OT: $formattedEarlyInOt")
            b.appendLine("  - Late Out OT: $formattedLateOutOt")
        }
        b.appendLine("===============================")
        b.appendLine("${bold}DAILY ATTENDANCE LOG:${bold}")
        if (includeRemarksInShare) {
            b.appendLine("Date | Status | In | Out | Late | Early | OT | Reason | Remarks")
        } else {
            b.appendLine("Date | Status | In | Out | Late | Early | OT | Reason")
        }
        b.appendLine("-------------------------------------------------------")
        daysInMonth.forEach { row ->
            val lateTxt = if (row.lateMinutes > 0) "${row.lateMinutes}m late" else "-"
            val earlyTxt = if (row.earlyMinutes > 0) "${row.earlyMinutes}m early" else "-"
            val otTxt = if (row.overtimeMinutes > 0) row.overtimeTimeString else "-"
            val reasonTxt = if (row.reason.isNotBlank()) " | Reason: ${row.reason}" else ""
            val notesTxt = if (includeRemarksInShare && row.notes.isNotBlank()) " | Remarks: ${row.notes}" else ""
            b.appendLine("${row.displayDate}: ${row.status} | IN: ${row.checkInTime.ifEmpty { "-" }} | OUT: ${row.checkOutTime.ifEmpty { "-" }} | LATE: $lateTxt | EARLY: $earlyTxt | OT: $otTxt$reasonTxt$notesTxt")
        }
        b.appendLine("===============================")
        b.appendLine("Generated via Attendance App")
        return b.toString()
    }

    fun sendViaWhatsApp() {
        val message = generateReportText(isWhatsApp = true)
        var cleanPhone = recipientPhone.replace(Regex("[^0-9+]"), "").trim()
        if (cleanPhone.isNotEmpty() && !cleanPhone.startsWith("+") && cleanPhone.length == 10) {
            cleanPhone = "+91$cleanPhone"
        }
        val encodedMessage = Uri.encode(message)
        val uriStr = if (cleanPhone.isNotEmpty()) {
            "https://wa.me/${cleanPhone.replace("+", "")}?text=$encodedMessage"
        } else {
            "https://wa.me/?text=$encodedMessage"
        }
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(uriStr)
            setPackage("com.whatsapp")
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val waBusiness = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(uriStr)
                    setPackage("com.whatsapp.w4b")
                }
                context.startActivity(waBusiness)
            } catch (e2: Exception) {
                val genericIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uriStr))
                context.startActivity(genericIntent)
            }
        }
    }

    fun sendViaSms() {
        val message = generateReportText(isWhatsApp = false)
        val cleanPhone = recipientPhone.trim()
        val smsUri = if (cleanPhone.isNotEmpty()) Uri.parse("smsto:$cleanPhone") else Uri.parse("smsto:")
        val intent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
            putExtra("sms_body", message)
        }
        context.startActivity(intent)
    }

    fun shareViaSystem() {
        val message = generateReportText(isWhatsApp = false)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_SUBJECT, "${selectedStudent.name} - Monthly Attendance Report (${monthNames[currentMonth - 1]} $currentYear)")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Attendance Report"))
    }

    // Edit Attendance Dialog for selected row
    if (editingRow != null) {
        val targetRow = editingRow!!
        var editStatus by remember(targetRow) {
            mutableStateOf(if (targetRow.status == "NO RECORD" || targetRow.status.startsWith("OFF")) "PRESENT" else targetRow.status)
        }
        var editInTime by remember(targetRow) { mutableStateOf(targetRow.checkInTime) }
        var editOutTime by remember(targetRow) { mutableStateOf(targetRow.checkOutTime) }
        var editReason by remember(targetRow) { mutableStateOf(targetRow.reason) }
        var editRemarks by remember(targetRow) { mutableStateOf(targetRow.notes) }
        var editHasOvertime by remember(targetRow) {
            mutableStateOf((targetRow.status == "A" || targetRow.status == "ABSENT") && (targetRow.checkInTime.isNotEmpty() || targetRow.checkOutTime.isNotEmpty()))
        }

        AlertDialog(
            onDismissRequest = { editingRow = null },
            title = {
                Column {
                    Text("Edit Day Attendance", fontWeight = FontWeight.Bold)
                    Text(
                        text = "${selectedStudent.name} • ${targetRow.displayDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    Text("Status", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("A", "P", "HD MOR", "HD EVE", "OFF").forEach { st ->
                            val isSelected = editStatus == st || (st == "P" && editStatus == "PRESENT") || (st == "A" && editStatus == "ABSENT")
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    editStatus = st
                                    when (st) {
                                        "P", "HD MOR", "HD EVE" -> {}
                                        "A" -> {
                                            if (!editHasOvertime) {
                                                editInTime = ""
                                                editOutTime = ""
                                            }
                                        }
                                        "OFF" -> {
                                            editInTime = ""
                                            editOutTime = ""
                                        }
                                    }
                                },
                                label = {
                                    val chipLabel = when (st) {
                                        "A" -> "✗ A"
                                        "P" -> "✓ P"
                                        else -> st
                                    }
                                    Text(chipLabel, fontSize = 9.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (editStatus == "A" || editStatus == "ABSENT") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (editHasOvertime) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = BorderStroke(1.dp, if (editHasOvertime) Color(0xFFFFB74D) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = "Overtime",
                                            tint = if (editHasOvertime) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Overtime on Absent Day",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (editHasOvertime) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Switch(
                                        checked = editHasOvertime,
                                        onCheckedChange = { checked ->
                                            editHasOvertime = checked
                                            if (checked) {
                                                if (editInTime.isBlank()) editInTime = "06:00 PM"
                                                if (editOutTime.isBlank()) editOutTime = "09:30 PM"
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (editStatus != "A" && editStatus != "ABSENT" && editStatus != "OFF" || editHasOvertime) {
                        Text(
                            text = if (editHasOvertime) "Overtime Working Hours" else "Shift In / Out Timings",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (editHasOvertime) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = editInTime,
                                onValueChange = { editInTime = it },
                                label = { Text(if (editHasOvertime) "OT In Time" else "In Time", fontSize = 11.sp) },
                                placeholder = { Text("09:30 AM") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = editOutTime,
                                onValueChange = { editOutTime = it },
                                label = { Text(if (editHasOvertime) "OT Out Time" else "Out Time", fontSize = 11.sp) },
                                placeholder = { Text("08:00 PM") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SuggestionChip(
                                onClick = {
                                    editInTime = "09:30 AM"
                                    editOutTime = "08:00 PM"
                                },
                                label = { Text("Standard", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            SuggestionChip(
                                onClick = {
                                    editInTime = "06:00 PM"
                                    editOutTime = "09:30 PM"
                                },
                                label = { Text("Evening OT", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            SuggestionChip(
                                onClick = {
                                    editInTime = ""
                                    editOutTime = ""
                                },
                                label = { Text("Clear", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Reason Field (Public/Report reason)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "🏷️ Reason (Public / Attendance Reason)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        OutlinedTextField(
                            value = editReason,
                            onValueChange = { editReason = it },
                            label = { Text("Reason") },
                            placeholder = { Text("e.g. Sick Leave, Emergency, Delay, Meeting") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Remarks Field (Private note)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "🔒 Remarks / Notes (Private)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        OutlinedTextField(
                            value = editRemarks,
                            onValueChange = { editRemarks = it },
                            label = { Text("Remarks (Private)") },
                            placeholder = { Text("e.g. Informed manager, will compensate hours") },
                            singleLine = false,
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalStatus = when (editStatus) {
                            "P" -> "PRESENT"
                            "A" -> "ABSENT"
                            else -> editStatus
                        }
                        onUpdateRecord(
                            selectedStudent.id,
                            targetRow.dateString,
                            finalStatus,
                            editInTime.trim(),
                            editOutTime.trim(),
                            editRemarks.trim(),
                            editReason.trim()
                        )
                        editingRow = null
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingRow = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Month Selector Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (currentMonth == 1) {
                                currentMonth = 12
                                currentYear -= 1
                            } else {
                                currentMonth -= 1
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Month")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${monthNames[currentMonth - 1]} $currentYear",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        val nowCal = Calendar.getInstance()
                        val isCurrent = currentYear == nowCal.get(Calendar.YEAR) && currentMonth == (nowCal.get(Calendar.MONTH) + 1)
                        if (!isCurrent) {
                            Text(
                                text = "Tap to jump to current month",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.clickable {
                                    currentYear = nowCal.get(Calendar.YEAR)
                                    currentMonth = nowCal.get(Calendar.MONTH) + 1
                                }
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            if (currentMonth == 12) {
                                currentMonth = 1
                                currentYear += 1
                            } else {
                                currentMonth += 1
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Month")
                    }
                }
            }
        }

        // Employee Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedStudent.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Emp ID: ${selectedStudent.rollNumber} • ${activeClass.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${studentShift?.name ?: "Standard"} Shift",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Shift Timings: ${studentShift?.timings ?: "09:30 AM - 08:00 PM"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Key Summary Metric Grid
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Monthly Overview Statistics",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    // Row 1: Attendance Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val attendancePercent = if (totalDays > 0) ((presentCount + (totalHalfDayCount * 0.5)) / totalDays * 100).toInt() else 0
                        ReportStatChip(label = "Attendance", value = "$attendancePercent%", color = Color(0xFF1565C0), modifier = Modifier.weight(1f))
                        ReportStatChip(label = "Present (P)", value = "$presentCount", color = Color(0xFF2E7D32), modifier = Modifier.weight(1f))
                        ReportStatChip(label = "Absent (A)", value = "$absentCount", color = Color(0xFFC62828), modifier = Modifier.weight(1f))
                        ReportStatChip(label = "HD MOR", value = "$halfDayMorCount", color = Color(0xFFE65100), modifier = Modifier.weight(1f))
                        ReportStatChip(label = "HD EVE", value = "$halfDayEveCount", color = Color(0xFF7B1FA2), modifier = Modifier.weight(1f))
                        ReportStatChip(label = "Off / Wknd", value = "$offCount", color = Color(0xFF616161), modifier = Modifier.weight(1f))
                    }

                    // Row 2: Late Arrivals
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ReportStatChip(label = "Late Days", value = "$lateDaysCount d", color = Color(0xFFD84315), modifier = Modifier.weight(1f))
                        ReportStatChip(label = "Total Late", value = formattedTotalLate, color = Color(0xFFD84315), modifier = Modifier.weight(1.5f))
                        ReportStatChip(label = "Avg Late/Day", value = formattedAvgLate, color = Color(0xFFD84315), modifier = Modifier.weight(1.5f))
                    }

                    // Row 3: Gone Early
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ReportStatChip(label = "Gone Early", value = "$earlyDaysCount d", color = Color(0xFFC2185B), modifier = Modifier.weight(1f))
                        ReportStatChip(label = "Total Early", value = formattedTotalEarly, color = Color(0xFFC2185B), modifier = Modifier.weight(1.5f))
                        ReportStatChip(label = "Avg Early/Day", value = formattedAvgEarly, color = Color(0xFFC2185B), modifier = Modifier.weight(1.5f))
                    }

                    // Row 4: Overtime
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ReportStatChip(label = "OT Days", value = "$overtimeDaysCount d", color = Color(0xFF00897B), modifier = Modifier.weight(1f))
                        ReportStatChip(label = "Total Overtime", value = formattedTotalOvertime, color = Color(0xFF00897B), modifier = Modifier.weight(1.5f))
                        ReportStatChip(label = "Avg OT/Day", value = formattedAvgOvertime, color = Color(0xFF00897B), modifier = Modifier.weight(1.5f))
                    }
                }
            }
        }

        // WhatsApp, SMS, Share & Copy Actions
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = recipientPhone,
                            onValueChange = { recipientPhone = it },
                            label = { Text("Phone Number / WhatsApp") },
                            placeholder = { Text("+91 98765 43210") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = { sendViaWhatsApp() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { sendViaSms() },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SMS", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val reportText = generateReportText(isWhatsApp = false)
                                clipboardManager.setText(AnnotatedString(reportText))
                                copyNoticeMessage = "Monthly Report copied to clipboard!"
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { shareViaSystem() },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontSize = 11.sp)
                        }
                    }

                    if (copyNoticeMessage != null) {
                        Text(
                            text = copyNoticeMessage!!,
                            color = Color(0xFF2E7D32),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Daily Ledger Breakdown Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Day-by-Day Attendance Log (${monthNames[currentMonth - 1]} $currentYear)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap the edit icon on any row to modify status, timings, remarks or overtime.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Table Header
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("DATE", fontWeight = FontWeight.ExtraBold, fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.width(72.dp))
                            Text("STATUS", fontWeight = FontWeight.ExtraBold, fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.width(52.dp))
                            Text("IN / OUT", fontWeight = FontWeight.ExtraBold, fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                            Text("OT / LATE", fontWeight = FontWeight.ExtraBold, fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                            Text("EDIT", fontWeight = FontWeight.ExtraBold, fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Table Rows
                    daysInMonth.forEach { row ->
                        val isMarked = row.status in listOf("P", "PRESENT", "A", "ABSENT", "HD MOR", "HD EVE", "HALF-DAY")
                        val rowBg = when (row.status) {
                            "P", "PRESENT" -> Color(0xFFE8F5E9)
                            "A", "ABSENT" -> Color(0xFFFFEBEE)
                            "HD MOR" -> Color(0xFFFFF3E0)
                            "HD EVE" -> Color(0xFFF3E5F5)
                            else -> if (row.isWeekend) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else Color.Transparent
                        }

                        val statusColor = when (row.status) {
                            "P", "PRESENT" -> Color(0xFF1B5E20)
                            "A", "ABSENT" -> Color(0xFFB71C1C)
                            "HD MOR" -> Color(0xFFBF360C)
                            "HD EVE" -> Color(0xFF6A1B9A)
                            else -> if (isMarked) Color(0xFF1F2937) else MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        // High-contrast text colors: On light pastel backgrounds, use dark, high-contrast text so it's 100% readable in both light and dark mode
                        val dateTextColor = if (isMarked) Color(0xFF0F172A) else if (row.isWeekend) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        val timeTextColor = if (isMarked) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface
                        val noteTextColor = if (isMarked) Color(0xFF334155) else MaterialTheme.colorScheme.onSurfaceVariant
                        val dashTextColor = if (isMarked) Color(0xFF475569) else MaterialTheme.colorScheme.onSurfaceVariant
                        val editIconTint = if (isMarked) Color(0xFF1565C0) else MaterialTheme.colorScheme.primary

                        Surface(
                            color = rowBg,
                            shape = RoundedCornerShape(5.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Date
                                    Text(
                                        text = row.displayDate,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = dateTextColor,
                                        modifier = Modifier.width(72.dp)
                                    )

                                    // Status Badge
                                    Text(
                                        text = row.status.take(6),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = statusColor,
                                        modifier = Modifier.width(52.dp)
                                    )

                                    // Timings
                                    Column(modifier = Modifier.weight(1f)) {
                                        if (row.checkInTime.isNotEmpty() || row.checkOutTime.isNotEmpty()) {
                                            Text(
                                                text = "${row.checkInTime.ifEmpty { "--" }} -> ${row.checkOutTime.ifEmpty { "--" }}",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = timeTextColor
                                            )
                                        } else {
                                            Text(
                                                text = "-",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = dashTextColor
                                            )
                                        }
                                    }

                                    // OT / Late / Early tags
                                    Column(modifier = Modifier.weight(1f)) {
                                        if (row.overtimeMinutes > 0) {
                                            Text(
                                                text = "⚡ +${row.overtimeTimeString}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF00695C)
                                            )
                                        }
                                        if (row.lateMinutes > 0) {
                                            Text(
                                                text = "⏳ ${row.lateTimeString}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFFBF360C)
                                            )
                                        }
                                        if (row.earlyMinutes > 0) {
                                            Text(
                                                text = "🏃 ${row.earlyTimeString}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF880E4F)
                                            )
                                        }
                                        if (row.overtimeMinutes == 0 && row.lateMinutes == 0 && row.earlyMinutes == 0) {
                                            Text(
                                                text = "-",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = dashTextColor
                                            )
                                        }
                                    }

                                    // Edit Action
                                    IconButton(
                                        onClick = { editingRow = row },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Day",
                                            tint = editIconTint,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }

                                if (row.notes.isNotEmpty()) {
                                    Text(
                                        text = "💬 Note: ${row.notes}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = noteTextColor,
                                        modifier = Modifier.padding(top = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBar(
    label: String,
    count: Int,
    percentage: Double,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(
                text = "$count records (${percentage.toInt()}%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Progress bar track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((percentage / 100f).toFloat())
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

// Utility function to safely parse a HEX string color
fun parseColor(colorHex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        Color(0xFF4F46E5) // Default Indigo color
    }
}

fun getCurrentTimeString(): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date())
}

fun showTimePickerDialog(
    context: android.content.Context,
    initialTimeStr: String,
    onTimeSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    var hour = calendar.get(Calendar.HOUR_OF_DAY)
    var minute = calendar.get(Calendar.MINUTE)

    if (initialTimeStr.isNotBlank()) {
        try {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = sdf.parse(initialTimeStr)
            if (date != null) {
                val cal = Calendar.getInstance()
                cal.time = date
                hour = cal.get(Calendar.HOUR_OF_DAY)
                minute = cal.get(Calendar.MINUTE)
            }
        } catch (e: Exception) {
            // Try fallback formats if any
            val fallbacks = listOf(
                SimpleDateFormat("h:mm a", Locale.getDefault()),
                SimpleDateFormat("HH:mm", Locale.getDefault()),
                SimpleDateFormat("H:mm", Locale.getDefault())
            )
            for (f in fallbacks) {
                try {
                    val date = f.parse(initialTimeStr)
                    if (date != null) {
                        val cal = Calendar.getInstance()
                        cal.time = date
                        hour = cal.get(Calendar.HOUR_OF_DAY)
                        minute = cal.get(Calendar.MINUTE)
                        break
                    }
                } catch (ex: Exception) {}
            }
        }
    }

    android.app.TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, selectedHour)
            cal.set(Calendar.MINUTE, selectedMinute)
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            onTimeSelected(sdf.format(cal.time))
        },
        hour,
        minute,
        false // 12-hour format
    ).show()
}

fun calculateHours(checkIn: String, checkOut: String): String {
    if (checkIn.isBlank() || checkOut.isBlank()) return ""
    val formats = listOf(
        SimpleDateFormat("hh:mm a", Locale.getDefault()),
        SimpleDateFormat("h:mm a", Locale.getDefault()),
        SimpleDateFormat("HH:mm", Locale.getDefault()),
        SimpleDateFormat("H:mm", Locale.getDefault())
    )
    
    var dateIn: Date? = null
    var dateOut: Date? = null
    
    for (f in formats) {
        try {
            if (dateIn == null) dateIn = f.parse(checkIn)
        } catch (e: Exception) {}
    }
    
    for (f in formats) {
        try {
            if (dateOut == null) dateOut = f.parse(checkOut)
        } catch (e: Exception) {}
    }
    
    if (dateIn != null && dateOut != null) {
        var diffMs = dateOut.time - dateIn.time
        if (diffMs < 0) {
            diffMs += 24 * 60 * 60 * 1000L
        }
        val hours = diffMs.toDouble() / (1000 * 60 * 60)
        return String.format(Locale.getDefault(), "%.1f hrs", hours)
    }
    return ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleSheetsSyncDialog(
    sheetId: String,
    webAppUrl: String,
    accessToken: String,
    isSyncing: Boolean,
    syncResult: String?,
    lastSyncTime: Long,
    departmentName: String,
    onDismiss: () -> Unit,
    onSaveConfig: (String, String, String) -> Unit,
    onSync: (useWebApp: Boolean) -> Unit,
    onRestore: (useWebApp: Boolean) -> Unit,
    onCreateSheet: (title: String) -> Unit,
    onClearResult: () -> Unit
) {
    var localSheetId by remember { mutableStateOf(sheetId) }
    var localWebAppUrl by remember { mutableStateOf(webAppUrl) }
    var localAccessToken by remember { mutableStateOf(accessToken) }
    var useWebApp by remember { mutableStateOf(webAppUrl.isNotEmpty() || sheetId.isEmpty()) }

    val clipboardManager = LocalClipboardManager.current

    val appsScriptCode = """
function doGet(e) {
  var sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
  var data = sheet.getDataRange().getValues();
  if (data.length <= 1) {
    return ContentService.createTextOutput(JSON.stringify({
      status: "success",
      records: []
    })).setMimeType(ContentService.MimeType.JSON);
  }
  var records = [];
  for (var i = 1; i < data.length; i++) {
    var row = data[i];
    if (row[0] && row[1]) {
      records.push({
        date: row[0] instanceof Date ? Utilities.formatDate(row[0], Session.getScriptTimeZone(), "yyyy-MM-dd") : String(row[0]),
        staffName: String(row[1]),
        rollNumber: String(row[2] || ""),
        status: String(row[3] || "P"),
        checkIn: row[4] instanceof Date ? Utilities.formatDate(row[4], Session.getScriptTimeZone(), "hh:mm a") : String(row[4] || ""),
        checkOut: row[5] instanceof Date ? Utilities.formatDate(row[5], Session.getScriptTimeZone(), "hh:mm a") : String(row[5] || ""),
        notes: String(row[6] || "")
      });
    }
  }
  return ContentService.createTextOutput(JSON.stringify({
    status: "success",
    records: records
  })).setMimeType(ContentService.MimeType.JSON);
}

function doPost(e) {
  var data = JSON.parse(e.postData.contents);
  var sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
  
  if (data.action === "fetch_data" || data.action === "restore") {
    return doGet(e);
  }
  
  if (data.action === "sync_attendance") {
    if (sheet.getLastRow() === 0) {
      sheet.appendRow(["Date", "Staff Name", "Employee ID", "Status", "Check In", "Check Out", "Notes"]);
      var headerRange = sheet.getRange("A1:G1");
      headerRange.setBackground("#4F46E5");
      headerRange.setFontColor("#FFFFFF");
      headerRange.setFontWeight("bold");
    }
    
    data.records.forEach(function(r) {
      sheet.appendRow([r.date, r.staffName, r.rollNumber, r.status, r.checkIn, r.checkOut, r.notes]);
    });
    
    for (var col = 1; col <= 7; col++) {
      sheet.autoResizeColumn(col);
    }
    
    return ContentService.createTextOutput(JSON.stringify({
      status: "success",
      message: "Synced " + data.records.length + " records successfully!"
    })).setMimeType(ContentService.MimeType.JSON);
  }
}
    """.trimIndent()

    AlertDialog(
        onDismissRequest = { 
            onClearResult()
            onDismiss() 
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Backup,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Google Sheets Sync", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Reinstalled app or switching phones? Paste your Web App URL below and tap 'Restore from Sheet' to import your previous data.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                item {
                    Column {
                        Text(
                            text = "SYNC METHOD",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { useWebApp = true }
                        ) {
                            RadioButton(selected = useWebApp, onClick = { useWebApp = true })
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Apps Script Web App (Recommended)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("Supports both Push & Restore sync", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { useWebApp = false }
                        ) {
                            RadioButton(selected = !useWebApp, onClick = { useWebApp = false })
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Direct Google Sheets REST API", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("Uses developer OAuth token", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                if (useWebApp) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Google Apps Script Web App URL",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            OutlinedTextField(
                                value = localWebAppUrl,
                                onValueChange = { 
                                    localWebAppUrl = it
                                    onSaveConfig(localSheetId, it, localAccessToken)
                                },
                                placeholder = { Text("https://script.google.com/macros/s/.../exec") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "How to set up Apps Script:",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "1. Open your Google Sheet.\n" +
                                           "2. Click Extensions > Apps Script.\n" +
                                           "3. Paste the code below and save.\n" +
                                           "4. Click Deploy > New Deployment (Type: Web App, Access: Anyone).\n" +
                                           "5. Copy and paste the Web App URL above.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "APPS SCRIPT CODE (Push & Pull):",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                         .fillMaxWidth()
                                         .height(130.dp)
                                         .background(Color.Black.copy(alpha = 0.05f), shape = RoundedCornerShape(8.dp))
                                         .padding(8.dp)
                                ) {
                                    Text(
                                        text = appsScriptCode,
                                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { clipboardManager.setText(AnnotatedString(appsScriptCode)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy Script", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Spreadsheet ID",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                OutlinedTextField(
                                    value = localSheetId,
                                    onValueChange = { 
                                        localSheetId = it
                                        onSaveConfig(it, localWebAppUrl, localAccessToken)
                                    },
                                    placeholder = { Text("Enter Spreadsheet ID") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "OAuth Access Token",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                OutlinedTextField(
                                    value = localAccessToken,
                                    onValueChange = { 
                                        localAccessToken = it
                                        onSaveConfig(localSheetId, localWebAppUrl, it)
                                    },
                                    placeholder = { Text("Paste Google OAuth access token") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = { onCreateSheet("Shop Attendance Logs") },
                                    enabled = localAccessToken.isNotEmpty() && !isSyncing,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Auto-Create Spreadsheet")
                                }
                            }
                        }
                    }
                }

                if (syncResult != null) {
                    item {
                        val isError = syncResult.contains("Error", ignoreCase = true) || syncResult.contains("Fail", ignoreCase = true)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f) 
                                                 else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isError) Icons.Default.Warning else Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = syncResult,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                if (lastSyncTime > 0) {
                    item {
                        Text(
                            text = "Last synced: " + SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(lastSyncTime)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (useWebApp) {
                    OutlinedButton(
                        onClick = { onRestore(useWebApp) },
                        enabled = !isSyncing && localWebAppUrl.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restore from Sheet")
                    }
                }

                Button(
                    onClick = { onSync(useWebApp) },
                    enabled = !isSyncing && (if (useWebApp) localWebAppUrl.isNotEmpty() else localSheetId.isNotEmpty() && localAccessToken.isNotEmpty()),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("sheets_sync_now_button")
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Working...")
                    } else {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sync to Sheet")
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { 
                    onClearResult()
                    onDismiss() 
                }
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
fun FirebaseSyncDialog(
    firebaseUrl: String,
    workspace: String,
    authToken: String,
    autoSyncEnabled: Boolean,
    portalBaseUrl: String = "",
    isSyncing: Boolean,
    syncStatus: String?,
    lastSyncTime: Long,
    onDismiss: () -> Unit,
    onSaveConfig: (url: String, workspace: String, token: String, autoSync: Boolean) -> Unit,
    onSavePortalBaseUrl: ((String) -> Unit)? = null,
    onSyncNow: () -> Unit,
    onRestoreNow: () -> Unit,
    onTestConnection: () -> Unit,
    onClearStatus: () -> Unit
) {
    var localUrl by remember(firebaseUrl) { mutableStateOf(firebaseUrl) }
    var localWorkspace by remember(workspace) { mutableStateOf(workspace) }
    var localToken by remember(authToken) { mutableStateOf(authToken) }
    var localAutoSync by remember(autoSyncEnabled) { mutableStateOf(autoSyncEnabled) }
    var localPortalUrl by remember(portalBaseUrl) { mutableStateOf(portalBaseUrl) }
    var showHelpGuide by remember { mutableStateOf(false) }
    var showAuthSecret by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            onClearStatus()
            onDismiss()
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFE0B2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null,
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Firebase Cloud Sync", fontWeight = FontWeight.Bold)
                    Text(
                        text = "Automatic & Permanent Cloud Backup",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Safety Highlight
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "100% Safe Across Reinstalls",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF1B5E20)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "With Firebase connected, all your departments, staff members, shifts, and attendance logs are securely synced to the cloud. When uninstalling and reinstalling the app on any device, simply enter your URL and tap 'Restore from Cloud' to bring back everything instantly!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E7D32),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Automatic Real-Time Sync Switch
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Automatic Cloud Sync",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Automatically push changes to Firebase every time attendance is marked, staff is added, or records are edited.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = localAutoSync,
                                onCheckedChange = {
                                    localAutoSync = it
                                    onSaveConfig(localUrl, localWorkspace, localToken, it)
                                },
                                modifier = Modifier.testTag("firebase_auto_sync_switch")
                            )
                        }
                    }
                }

                // Database URL Configuration
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Firebase Realtime Database URL *",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        OutlinedTextField(
                            value = localUrl,
                            onValueChange = {
                                localUrl = it
                                onSaveConfig(it, localWorkspace, localToken, localAutoSync)
                            },
                            placeholder = { Text("https://your-shop-db-default-rtdb.firebaseio.com") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("firebase_url_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            trailingIcon = {
                                if (localUrl.isNotEmpty()) {
                                    IconButton(onClick = {
                                        localUrl = ""
                                        onSaveConfig("", localWorkspace, localToken, localAutoSync)
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        )
                    }
                }

                // Workspace / Shop Identifier
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Shop / Store Identifier",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        OutlinedTextField(
                            value = localWorkspace,
                            onValueChange = {
                                localWorkspace = it
                                onSaveConfig(localUrl, it, localToken, localAutoSync)
                            },
                            placeholder = { Text("e.g. shop_attendance") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("firebase_workspace_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }

                // Database Secret / Auth Token (Owner-Only Write Protection)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (localToken.isNotBlank()) Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                            border = BorderStroke(
                                1.dp,
                                if (localToken.isNotBlank()) Color(0xFF10B981).copy(alpha = 0.5f) else Color(0xFFF59E0B).copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (localToken.isNotBlank()) Color(0xFF047857) else Color(0xFFB45309),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (localToken.isNotBlank()) {
                                        "Owner-Only Write Lock Active: Shared links are 100% View-Only. Only your app with this secret can update cloud data."
                                    } else {
                                        "Make Shared Links Strictly View-Only: Set Firebase Rules to \".write\": false and paste your Database Secret below so nobody else can edit."
                                    },
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp,
                                    color = if (localToken.isNotBlank()) Color(0xFF065F46) else Color(0xFF92400E)
                                )
                            }
                        }

                        Text(
                            text = "Owner Database Secret / Auth Token",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        OutlinedTextField(
                            value = localToken,
                            onValueChange = {
                                localToken = it
                                onSaveConfig(localUrl, localWorkspace, it, localAutoSync)
                            },
                            placeholder = { Text("Paste Firebase Database Secret (kept private in app)") },
                            visualTransformation = if (showAuthSecret) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showAuthSecret = !showAuthSecret }) {
                                    Icon(
                                        imageVector = if (showAuthSecret) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (showAuthSecret) "Hide Secret" else "Show Secret",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("firebase_auth_token_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }

                // GitHub Pages Passbook Portal URL
                if (onSavePortalBaseUrl != null) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "GitHub Pages T-Hisab Link (For Staff Sharing)",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            OutlinedTextField(
                                value = localPortalUrl,
                                onValueChange = {
                                    localPortalUrl = it
                                    onSavePortalBaseUrl.invoke(it)
                                },
                                placeholder = { Text("https://username.github.io/shop-attendance") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("firebase_dialog_portal_url_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                trailingIcon = {
                                    if (localPortalUrl.isNotEmpty()) {
                                        IconButton(onClick = {
                                            localPortalUrl = ""
                                            onSavePortalBaseUrl.invoke("")
                                        }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            )
                            Text(
                                text = "Host docs/index.html via GitHub Pages (Settings → Pages → /docs). Never shares your Database Secret.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }

                // Quick Setup Guide Accordion
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showHelpGuide = !showHelpGuide },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Owner-Only View-Only Security Rules (4 steps)",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Icon(
                                    imageVector = if (showHelpGuide) Icons.Default.ChevronLeft else Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            if (showHelpGuide) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "1. Open console.firebase.google.com → Build → Realtime Database.\n" +
                                           "2. Under 'Rules' tab, set READ-ONLY for everyone:\n" +
                                           "   {\n" +
                                           "     \"rules\": {\n" +
                                           "       \".read\": true,\n" +
                                           "       \".write\": false\n" +
                                           "     }\n" +
                                           "   }\n" +
                                           "3. Go to Project Settings (gear icon) → Service accounts → Database secrets → copy secret & paste into 'Owner Database Secret' above.\n" +
                                           "4. Now ONLY your app can edit/sync cloud data, while shared GitHub Pages links are 100% View-Only!",
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Sync Result Feedback Card
                if (syncStatus != null) {
                    item {
                        val isError = syncStatus.contains("Error", ignoreCase = true) || syncStatus.contains("Fail", ignoreCase = true)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
                                else Color(0xFFE8F5E9)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isError) Icons.Default.Warning else Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = if (isError) MaterialTheme.colorScheme.onErrorContainer else Color(0xFF2E7D32),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = syncStatus,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = if (isError) MaterialTheme.colorScheme.onErrorContainer else Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                }

                if (lastSyncTime > 0) {
                    item {
                        Text(
                            text = "Last cloud sync: " + SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(lastSyncTime)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onTestConnection,
                        enabled = !isSyncing && localUrl.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Text("Test Link", fontSize = 11.5.sp, maxLines = 1)
                    }

                    OutlinedButton(
                        onClick = onRestoreNow,
                        enabled = !isSyncing && localUrl.isNotBlank(),
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Restore Cloud", fontSize = 11.5.sp, maxLines = 1)
                    }
                }

                Button(
                    onClick = onSyncNow,
                    enabled = !isSyncing && localUrl.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("firebase_sync_now_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Syncing to Firebase...")
                    } else {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Backup to Firebase Now", fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(
                    onClick = {
                        onClearStatus()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        dismissButton = null
    )
}

@Composable
fun ShiftsView(
    shifts: List<ShiftEntity>,
    onAddShift: (name: String, timings: String) -> Unit,
    onUpdateShift: (ShiftEntity) -> Unit,
    onDeleteShift: (ShiftEntity) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    var showAddShiftDialog by remember { mutableStateOf(false) }
    var editingShift by remember { mutableStateOf<ShiftEntity?>(null) }
    var shiftName by remember { mutableStateOf("") }
    var shiftTimings by remember { mutableStateOf("") }

    Column(
        modifier = modifier
    ) {
        // Shift Info Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Shift Configuration",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Work Shifts & Hours",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = {
                            shiftName = ""
                            shiftTimings = ""
                            showAddShiftDialog = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_shift_trigger")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Shift", fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Configure work timings to assign to your department staff. These help keep track of staff rotas and scheduling.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "AVAILABLE SHIFTS (${shifts.size})",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (shifts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No custom shifts created yet.\nTap 'Add Shift' to define one.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(shifts) { shift ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = shift.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Timings: ${shift.timings}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { editingShift = shift },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Shift",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteShift(shift) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Shift",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddShiftDialog) {
        var shiftStartTime by remember { mutableStateOf("09:00 AM") }
        var shiftEndTime by remember { mutableStateOf("05:00 PM") }
        val context = LocalContext.current

        LaunchedEffect(shiftStartTime, shiftEndTime) {
            shiftTimings = "$shiftStartTime - $shiftEndTime"
        }

        AlertDialog(
            onDismissRequest = { showAddShiftDialog = false },
            title = { Text("Add Custom Shift", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = shiftName,
                        onValueChange = { shiftName = it },
                        label = { Text("Shift Name") },
                        placeholder = { Text("e.g. Night Shift, Weekend Shift") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_shift_name")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Shift Hours (Use clock to select)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Start Time Field (Clickable)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    showTimePickerDialog(context, shiftStartTime) { selectedTime ->
                                        shiftStartTime = selectedTime
                                    }
                                }
                        ) {
                            OutlinedTextField(
                                value = shiftStartTime,
                                onValueChange = {},
                                label = { Text("Start Time") },
                                singleLine = true,
                                readOnly = true,
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.primary
                                ),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Pick start time",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // End Time Field (Clickable)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    showTimePickerDialog(context, shiftEndTime) { selectedTime ->
                                        shiftEndTime = selectedTime
                                    }
                                }
                        ) {
                            OutlinedTextField(
                                value = shiftEndTime,
                                onValueChange = {},
                                label = { Text("End Time") },
                                singleLine = true,
                                readOnly = true,
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.primary
                                ),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Pick end time",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Total Shift Timings: $shiftTimings",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (shiftName.isNotBlank() && shiftTimings.isNotBlank()) {
                            onAddShift(shiftName.trim(), shiftTimings.trim())
                            showAddShiftDialog = false
                        }
                    },
                    modifier = Modifier.testTag("submit_add_shift")
                ) {
                    Text("Add Shift")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddShiftDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (editingShift != null) {
        val shift = editingShift!!
        var editName by remember(shift) { mutableStateOf(shift.name) }
        val timingsParts = shift.timings.split(" - ")
        var editStartTime by remember(shift) { mutableStateOf(timingsParts.getOrNull(0) ?: "09:00 AM") }
        var editEndTime by remember(shift) { mutableStateOf(timingsParts.getOrNull(1) ?: "05:00 PM") }
        var editTimings by remember(shift) { mutableStateOf(shift.timings) }
        val context = LocalContext.current

        LaunchedEffect(editStartTime, editEndTime) {
            editTimings = "$editStartTime - $editEndTime"
        }

        AlertDialog(
            onDismissRequest = { editingShift = null },
            title = { Text("Edit Custom Shift", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Shift Name") },
                        placeholder = { Text("e.g. Night Shift, Weekend Shift") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_shift_name")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Shift Hours (Use clock to select)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Start Time Field (Clickable)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    showTimePickerDialog(context, editStartTime) { selectedTime ->
                                        editStartTime = selectedTime
                                    }
                                }
                        ) {
                            OutlinedTextField(
                                value = editStartTime,
                                onValueChange = {},
                                label = { Text("Start Time") },
                                singleLine = true,
                                readOnly = true,
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.primary
                                ),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Pick start time",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // End Time Field (Clickable)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    showTimePickerDialog(context, editEndTime) { selectedTime ->
                                        editEndTime = selectedTime
                                    }
                                }
                        ) {
                            OutlinedTextField(
                                value = editEndTime,
                                onValueChange = {},
                                label = { Text("End Time") },
                                singleLine = true,
                                readOnly = true,
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.primary
                                ),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Pick end time",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Total Shift Timings: $editTimings",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank() && editTimings.isNotBlank()) {
                            onUpdateShift(shift.copy(name = editName.trim(), timings = editTimings.trim()))
                            editingShift = null
                        }
                    },
                    modifier = Modifier.testTag("submit_edit_shift")
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingShift = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeMonthlyReportDialog(
    student: StudentEntity,
    activeClass: ClassEntity,
    allStudents: List<StudentEntity>,
    records: List<com.example.data.AttendanceRecordEntity>,
    allShifts: List<ShiftEntity>,
    onDismiss: () -> Unit,
    onSelectStudent: (StudentEntity) -> Unit,
    onUpdateRecord: (studentId: Long, dateString: String, status: String, checkInTime: String, checkOutTime: String, notes: String, reason: String) -> Unit = { _, _, _, _, _, _, _ -> }
) {
    var selectedStudent by remember(student) { mutableStateOf(student) }
    var recipientPhone by remember(selectedStudent) { mutableStateOf(selectedStudent.phone) }
    var isFullScreen by remember { mutableStateOf(false) }
    val calendar = remember { Calendar.getInstance() }
    var currentYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var currentMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) }

    var showStudentDropdown by remember { mutableStateOf(false) }
    var copyNoticeMessage by remember { mutableStateOf<String?>(null) }
    var editingRow by remember { mutableStateOf<DailyReportRow?>(null) }
    var includeRemarksInShare by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val monthNames = remember {
        listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    }

    val studentShift = allShifts.find { it.id == selectedStudent.shiftId }
    val shiftStartMins = parseShiftStartTimeToMinutes(studentShift?.timings) ?: (9 * 60 + 30) // default 09:30 AM
    val shiftEndMins = parseShiftEndTimeToMinutes(studentShift?.timings) ?: (20 * 60) // default 20:00 (08:00 PM)

    val daysInMonth = remember(currentYear, currentMonth, selectedStudent, records, studentShift) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, currentYear)
        cal.set(Calendar.MONTH, currentMonth - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val sdfDateKey = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val sdfDayName = SimpleDateFormat("EEE", Locale.US)

        val list = mutableListOf<DailyReportRow>()
        val studentRecs = records.filter { it.studentId == selectedStudent.id }

        for (day in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dateStr = sdfDateKey.format(cal.time)
            val dayName = sdfDayName.format(cal.time)
            val displayDate = String.format(Locale.US, "%02d %s (%s)", day, monthNames[currentMonth - 1].take(3), dayName)
            val isWeekend = dayName == "Sat" || dayName == "Sun"

            val record = studentRecs.find { it.dateString == dateStr }
            val status = record?.status ?: if (isWeekend) "OFF (WEEKEND)" else "NO RECORD"
            val inTime = record?.checkInTime ?: ""
            val outTime = record?.checkOutTime ?: ""
            val notes = record?.notes ?: ""
            val reason = record?.reason ?: ""

            val (lateMins, lateStr) = calculateLateTime(inTime, status, shiftStartMins)
            val (earlyMins, earlyStr) = calculateGoneEarlyTime(outTime, status, shiftEndMins)
            val otResult = calculateOvertime(inTime, outTime, status, shiftStartMins, shiftEndMins)

            list.add(
                DailyReportRow(
                    dayNumber = day,
                    dateString = dateStr,
                    dayOfWeek = dayName,
                    displayDate = displayDate,
                    isWeekend = isWeekend,
                    status = status,
                    checkInTime = inTime,
                    checkOutTime = outTime,
                    lateMinutes = lateMins,
                    lateTimeString = lateStr,
                    earlyMinutes = earlyMins,
                    earlyTimeString = earlyStr,
                    overtimeMinutes = otResult.totalOvertimeMinutes,
                    overtimeTimeString = otResult.formattedString,
                    earlyInOtMinutes = otResult.earlyInMinutes,
                    lateOutOtMinutes = otResult.lateOutMinutes,
                    overtimeBreakdown = otResult.breakdownString,
                    reason = reason,
                    notes = notes
                )
            )
        }
        list
    }

    val totalDays = daysInMonth.size
    val presentCount = daysInMonth.count { it.status == "P" || it.status == "PRESENT" }
    val absentCount = daysInMonth.count { it.status == "A" || it.status == "ABSENT" }
    val halfDayMorCount = daysInMonth.count { it.status == "HD MOR" }
    val halfDayEveCount = daysInMonth.count { it.status == "HD EVE" }
    val legacyHalfDayCount = daysInMonth.count { it.status == "HALF-DAY" }
    val totalHalfDayCount = halfDayMorCount + halfDayEveCount + legacyHalfDayCount
    val offCount = daysInMonth.count { it.status == "OFF (WEEKEND)" || it.status == "NO RECORD" || it.status == "OFF" }
    val lateDaysCount = daysInMonth.count { it.lateMinutes > 0 }
    val totalLateMinutes = daysInMonth.sumOf { it.lateMinutes }
    val earlyDaysCount = daysInMonth.count { it.earlyMinutes > 0 }
    val totalEarlyMinutes = daysInMonth.sumOf { it.earlyMinutes }

    val overtimeDaysCount = daysInMonth.count { it.overtimeMinutes > 0 }
    val totalOvertimeMinutes = daysInMonth.sumOf { it.overtimeMinutes }
    val totalEarlyInOtMinutes = daysInMonth.sumOf { it.earlyInOtMinutes }
    val totalLateOutOtMinutes = daysInMonth.sumOf { it.lateOutOtMinutes }

    val lateHrs = totalLateMinutes / 60
    val lateMins = totalLateMinutes % 60
    val formattedTotalLate = when {
        lateHrs > 0 && lateMins > 0 -> "${lateHrs}h ${lateMins}m"
        lateHrs > 0 -> "${lateHrs}h"
        lateMins > 0 -> "${lateMins}m"
        else -> "0m"
    }

    val earlyHrs = totalEarlyMinutes / 60
    val earlyMins = totalEarlyMinutes % 60
    val formattedTotalEarly = when {
        earlyHrs > 0 && earlyMins > 0 -> "${earlyHrs}h ${earlyMins}m"
        earlyHrs > 0 -> "${earlyHrs}h"
        earlyMins > 0 -> "${earlyMins}m"
        else -> "0m"
    }

    val otHrs = totalOvertimeMinutes / 60
    val otMins = totalOvertimeMinutes % 60
    val formattedTotalOvertime = when {
        otHrs > 0 && otMins > 0 -> "${otHrs}h ${otMins}m"
        otHrs > 0 -> "${otHrs}h"
        otMins > 0 -> "${otMins}m"
        else -> "0m"
    }

    val eOtHrs = totalEarlyInOtMinutes / 60
    val eOtMins = totalEarlyInOtMinutes % 60
    val formattedEarlyInOt = when {
        eOtHrs > 0 && eOtMins > 0 -> "${eOtHrs}h ${eOtMins}m"
        eOtHrs > 0 -> "${eOtHrs}h"
        eOtMins > 0 -> "${eOtMins}m"
        else -> "0m"
    }

    val lOtHrs = totalLateOutOtMinutes / 60
    val lOtMins = totalLateOutOtMinutes % 60
    val formattedLateOutOt = when {
        lOtHrs > 0 && lOtMins > 0 -> "${lOtHrs}h ${lOtMins}m"
        lOtHrs > 0 -> "${lOtHrs}h"
        lOtMins > 0 -> "${lOtMins}m"
        else -> "0m"
    }

    val activeDays = presentCount + totalHalfDayCount
    val avgLateTotalMinutes = if (activeDays > 0) (totalLateMinutes.toDouble() / activeDays).roundToInt() else 0
    val avgLateHrs = avgLateTotalMinutes / 60
    val avgLateRemainingMins = avgLateTotalMinutes % 60
    val formattedAvgLate = when {
        avgLateHrs > 0 && avgLateRemainingMins > 0 -> "${avgLateHrs}h ${avgLateRemainingMins}m"
        avgLateHrs > 0 -> "${avgLateHrs}h"
        avgLateRemainingMins > 0 -> "${avgLateRemainingMins}m"
        else -> "0m"
    }

    val avgEarlyTotalMinutes = if (activeDays > 0) (totalEarlyMinutes.toDouble() / activeDays).roundToInt() else 0
    val avgEarlyHrs = avgEarlyTotalMinutes / 60
    val avgEarlyRemainingMins = avgEarlyTotalMinutes % 60
    val formattedAvgEarly = when {
        avgEarlyHrs > 0 && avgEarlyRemainingMins > 0 -> "${avgEarlyHrs}h ${avgEarlyRemainingMins}m"
        avgEarlyHrs > 0 -> "${avgEarlyHrs}h"
        avgEarlyRemainingMins > 0 -> "${avgEarlyRemainingMins}m"
        else -> "0m"
    }

    val avgOtTotalMinutes = if (activeDays > 0) (totalOvertimeMinutes.toDouble() / activeDays).roundToInt() else 0
    val avgOtHrs = avgOtTotalMinutes / 60
    val avgOtRemainingMins = avgOtTotalMinutes % 60
    val formattedAvgOvertime = when {
        avgOtHrs > 0 && avgOtRemainingMins > 0 -> "${avgOtHrs}h ${avgOtRemainingMins}m"
        avgOtHrs > 0 -> "${avgOtHrs}h"
        avgOtRemainingMins > 0 -> "${avgOtRemainingMins}m"
        else -> "0m"
    }

    fun generateReportText(isWhatsApp: Boolean = false): String {
        return if (isWhatsApp) {
            buildString {
                appendLine("📋 *MONTHLY ATTENDANCE REPORT*")
                appendLine("━━━━━━━━━━━━━━━━━━━━")
                appendLine("👤 *Staff:* ${selectedStudent.name}")
                appendLine("🆔 *Emp ID:* ${selectedStudent.rollNumber}")
                appendLine("🏢 *Department:* ${activeClass.name}")
                if (studentShift != null) {
                    appendLine("⏰ *Shift:* ${studentShift.name} (${studentShift.timings})")
                }
                appendLine("📅 *Month:* ${monthNames[currentMonth - 1]} $currentYear")
                appendLine("━━━━━━━━━━━━━━━━━━━━")
                appendLine("📊 *ATTENDANCE SUMMARY:*")
                appendLine("• Total Days: $totalDays")
                appendLine("• ✅ P (Present): $presentCount")
                appendLine("• ❌ A (Absent): $absentCount")
                appendLine("• 🌅 HD MOR: $halfDayMorCount")
                appendLine("• 🌇 HD EVE: $halfDayEveCount")
                if (legacyHalfDayCount > 0) {
                    appendLine("• 🌗 Half-Days (Other): $legacyHalfDayCount")
                }
                appendLine("• 💤 Off / No Record: $offCount")
                appendLine("• ⏳ Late Days: $lateDaysCount ($formattedTotalLate total late)")
                appendLine("• ⏱️ Avg Late / Day: $formattedAvgLate")
                appendLine("• 🏃 Gone Early: $earlyDaysCount ($formattedTotalEarly total early)")
                appendLine("• ⏱️ Avg Early / Day: $formattedAvgEarly")
                appendLine("━━━━━━━━━━━━━━━━━━━━")
                appendLine("⚡ *OVERTIME (OT) SUMMARY:*")
                appendLine("• Total OT Days: $overtimeDaysCount")
                appendLine("• Total Overtime: $formattedTotalOvertime")
                if (totalEarlyInOtMinutes > 0) {
                    appendLine("• ☀️ Advance In (Before Shift): $formattedEarlyInOt")
                }
                if (totalLateOutOtMinutes > 0) {
                    appendLine("• 🌙 Late Out (After Shift): $formattedLateOutOt")
                }
                appendLine("• ⏱️ Avg OT / Day: $formattedAvgOvertime")
                appendLine("━━━━━━━━━━━━━━━━━━━━")
                appendLine("🗓️ *DAILY BREAKDOWN:*")
                daysInMonth.forEach { r ->
                    val statusEmoji = when (r.status) {
                        "P", "PRESENT" -> "✅"
                        "A", "ABSENT" -> "❌"
                        "HD MOR" -> "🌅"
                        "HD EVE" -> "🌇"
                        "HALF-DAY" -> "🌗"
                        else -> "⚪"
                    }
                    val times = when {
                        r.checkInTime.isNotBlank() && r.checkOutTime.isNotBlank() -> " (In: ${r.checkInTime}, Out: ${r.checkOutTime})"
                        r.checkInTime.isNotBlank() -> " (In: ${r.checkInTime})"
                        else -> ""
                    }
                    val late = if (r.lateMinutes > 0) " [⏳ ${r.lateTimeString}]" else ""
                    val early = if (r.earlyMinutes > 0) " [🏃 ${r.earlyTimeString}]" else ""
                    val ot = if (r.overtimeMinutes > 0) " [⚡ OT: ${r.overtimeTimeString} (${r.overtimeBreakdown})]" else ""
                    val reason = if (r.reason.isNotBlank()) " | 🏷️ ${r.reason}" else ""
                    val remarks = if (includeRemarksInShare && r.notes.isNotBlank()) " | 📝 ${r.notes}" else ""
                    appendLine("$statusEmoji ${r.displayDate}: ${r.status}$times$late$early$ot$reason$remarks")
                }
                appendLine("━━━━━━━━━━━━━━━━━━━━")
            }
        } else {
            buildString {
                appendLine("=======================================================================================================================")
                appendLine("EMPLOYEE MONTHLY ATTENDANCE & OVERTIME REPORT")
                appendLine("=======================================================================================================================")
                appendLine("Employee Name : ${selectedStudent.name}")
                appendLine("Emp ID / Roll : ${selectedStudent.rollNumber}")
                appendLine("Department    : ${activeClass.name}")
                if (studentShift != null) {
                    appendLine("Shift         : ${studentShift.name} (${studentShift.timings})")
                }
                appendLine("Month / Year  : ${monthNames[currentMonth - 1]} $currentYear")
                appendLine("-----------------------------------------------------------------------------------------------------------------------")
                appendLine("ATTENDANCE SUMMARY:")
                appendLine("- Total Days   : $totalDays")
                appendLine("- P (Present)  : $presentCount")
                appendLine("- A (Absent)   : $absentCount")
                appendLine("- HD MOR       : $halfDayMorCount")
                appendLine("- HD EVE       : $halfDayEveCount")
                if (legacyHalfDayCount > 0) {
                    appendLine("- Half-Days    : $legacyHalfDayCount")
                }
                appendLine("- Off/Unmarked : $offCount")
                appendLine("- Late Days    : $lateDaysCount ($formattedTotalLate total late)")
                appendLine("- Avg Late/Day : $formattedAvgLate")
                appendLine("- Gone Early   : $earlyDaysCount ($formattedTotalEarly total early)")
                appendLine("- Avg Early/Day: $formattedAvgEarly")
                appendLine("-----------------------------------------------------------------------------------------------------------------------")
                appendLine("OVERTIME (OT) SUMMARY:")
                appendLine("- Total OT Days : $overtimeDaysCount")
                appendLine("- Total Overtime: $formattedTotalOvertime")
                appendLine("- Advance In    : $formattedEarlyInOt (before shift start)")
                appendLine("- Late Out      : $formattedLateOutOt (after shift end)")
                appendLine("- Avg OT / Day  : $formattedAvgOvertime")
                appendLine("=======================================================================================================================")
                if (includeRemarksInShare) {
                    appendLine(String.format(Locale.US, "%-14s | %-9s | %-8s | %-8s | %-9s | %-11s | %-10s | %-12s | %-12s", "DATE", "STATUS", "IN TIME", "OUT TIME", "LATE", "GONE EARLY", "OVERTIME", "REASON", "REMARKS"))
                } else {
                    appendLine(String.format(Locale.US, "%-14s | %-9s | %-8s | %-8s | %-9s | %-11s | %-10s | %-12s", "DATE", "STATUS", "IN TIME", "OUT TIME", "LATE", "GONE EARLY", "OVERTIME", "REASON"))
                }
                appendLine("-----------------------------------------------------------------------------------------------------------------------")
                daysInMonth.forEach { r ->
                    if (includeRemarksInShare) {
                        appendLine(String.format(Locale.US, "%-14s | %-9s | %-8s | %-8s | %-9s | %-11s | %-10s | %-12s | %-12s", r.displayDate, r.status, r.checkInTime.ifEmpty { "-" }, r.checkOutTime.ifEmpty { "-" }, r.lateTimeString, r.earlyTimeString, if (r.overtimeMinutes > 0) r.overtimeTimeString else "-", r.reason.ifEmpty { "-" }, r.notes.ifEmpty { "-" }))
                    } else {
                        appendLine(String.format(Locale.US, "%-14s | %-9s | %-8s | %-8s | %-9s | %-11s | %-10s | %-12s", r.displayDate, r.status, r.checkInTime.ifEmpty { "-" }, r.checkOutTime.ifEmpty { "-" }, r.lateTimeString, r.earlyTimeString, if (r.overtimeMinutes > 0) r.overtimeTimeString else "-", r.reason.ifEmpty { "-" }))
                    }
                }
                appendLine("=======================================================================================================================")
            }
        }
    }

    fun sendViaWhatsApp() {
        val message = generateReportText(isWhatsApp = true)
        val cleanedPhone = recipientPhone.replace(Regex("[^0-9+]"), "")
        val encodedMessage = Uri.encode(message)
        val url = if (cleanedPhone.isNotBlank()) {
            val num = if (cleanedPhone.startsWith("+")) cleanedPhone.removePrefix("+") else cleanedPhone
            "https://wa.me/$num?text=$encodedMessage"
        } else {
            "https://wa.me/?text=$encodedMessage"
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
    }

    fun sendViaSms() {
        val message = generateReportText(isWhatsApp = false)
        val cleanedPhone = recipientPhone.replace(Regex("[^0-9+]"), "")
        val smsUri = if (cleanedPhone.isNotBlank()) {
            Uri.parse("smsto:$cleanedPhone")
        } else {
            Uri.parse("smsto:")
        }
        val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
            putExtra("sms_body", message)
        }
        try {
            context.startActivity(smsIntent)
        } catch (e: Exception) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Send SMS via"))
        }
    }

    fun shareViaSystem() {
        val message = generateReportText(isWhatsApp = false)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_SUBJECT, "${selectedStudent.name} - Monthly Attendance Report (${monthNames[currentMonth - 1]} $currentYear)")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Attendance Report"))
    }

    // Edit Attendance Dialog for selected row
    if (editingRow != null) {
        val targetRow = editingRow!!
        var editStatus by remember(targetRow) {
            mutableStateOf(if (targetRow.status == "NO RECORD" || targetRow.status.startsWith("OFF")) "PRESENT" else targetRow.status)
        }
        var editInTime by remember(targetRow) { mutableStateOf(targetRow.checkInTime) }
        var editOutTime by remember(targetRow) { mutableStateOf(targetRow.checkOutTime) }
        var editReason by remember(targetRow) { mutableStateOf(targetRow.reason) }
        var editRemarks by remember(targetRow) { mutableStateOf(targetRow.notes) }
        var editHasOvertime by remember(targetRow) {
            mutableStateOf((targetRow.status == "A" || targetRow.status == "ABSENT") && (targetRow.checkInTime.isNotEmpty() || targetRow.checkOutTime.isNotEmpty()))
        }

        AlertDialog(
            onDismissRequest = { editingRow = null },
            title = {
                Column {
                    Text("Edit Day Attendance", fontWeight = FontWeight.Bold)
                    Text(
                        text = "${selectedStudent.name} • ${targetRow.displayDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    // Status selector chips: A, P, HD MOR, HD EVE, OFF
                    Text("Status", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("A", "P", "HD MOR", "HD EVE", "OFF").forEach { st ->
                            val isSelected = editStatus == st || (st == "P" && editStatus == "PRESENT") || (st == "A" && editStatus == "ABSENT")
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    editStatus = st
                                    when (st) {
                                        "P", "HD MOR", "HD EVE" -> {
                                            // Keep user's custom filled times
                                        }
                                        "A" -> {
                                            if (!editHasOvertime) {
                                                editInTime = ""
                                                editOutTime = ""
                                            }
                                        }
                                        "OFF" -> {
                                            editInTime = ""
                                            editOutTime = ""
                                        }
                                    }
                                },
                                label = {
                                    val chipLabel = when (st) {
                                        "A" -> "✗ A"
                                        "P" -> "✓ P"
                                        else -> st
                                    }
                                    Text(chipLabel, fontSize = 9.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // For Absent: Option to add Overtime along with Remarks
                    if (editStatus == "A" || editStatus == "ABSENT") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (editHasOvertime) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = BorderStroke(1.dp, if (editHasOvertime) Color(0xFFFFB74D) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = "Overtime",
                                            tint = if (editHasOvertime) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Add Overtime (OT)",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (editHasOvertime) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Switch(
                                        checked = editHasOvertime,
                                        onCheckedChange = { checked ->
                                            editHasOvertime = checked
                                            if (checked) {
                                                if (editInTime.isEmpty()) editInTime = "06:00 PM"
                                                if (editOutTime.isEmpty()) editOutTime = "09:30 PM"
                                            }
                                        },
                                        modifier = Modifier.testTag("edit_dialog_toggle_ot")
                                    )
                                }

                                if (editHasOvertime) {
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // In Time Field + Quick Presets
                                    Column {
                                        Text("In-Time (OT Start)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = editInTime,
                                            onValueChange = { editInTime = it },
                                            placeholder = { Text("e.g. 06:00 PM") },
                                            singleLine = true,
                                            leadingIcon = {
                                                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                            },
                                            trailingIcon = {
                                                if (editInTime.isNotEmpty()) {
                                                    IconButton(onClick = { editInTime = "" }) {
                                                        Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("edit_ot_in_time_input")
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 2.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            listOf("05:00 PM", "06:00 PM", "07:00 PM", "08:00 PM").forEach { preset ->
                                                SuggestionChip(
                                                    onClick = { editInTime = preset },
                                                    label = { Text(preset, fontSize = 9.sp) }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Out Time Field + Quick Presets
                                    Column {
                                        Text("Out-Time (OT End)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = editOutTime,
                                            onValueChange = { editOutTime = it },
                                            placeholder = { Text("e.g. 09:30 PM") },
                                            singleLine = true,
                                            leadingIcon = {
                                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(18.dp))
                                            },
                                            trailingIcon = {
                                                if (editOutTime.isNotEmpty()) {
                                                    IconButton(onClick = { editOutTime = "" }) {
                                                        Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("edit_ot_out_time_input")
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 2.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            listOf("08:00 PM", "09:00 PM", "10:00 PM", "11:00 PM").forEach { preset ->
                                                SuggestionChip(
                                                    onClick = { editOutTime = preset },
                                                    label = { Text(preset, fontSize = 9.sp) }
                                                )
                                            }
                                        }
                                    }

                                    val calculatedOt = calculateHours(editInTime, editOutTime)
                                    if (calculatedOt.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFFE0B2),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "⚡ Calculated Overtime: $calculatedOt",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFFE65100),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else if (editStatus != "OFF") {
                        // In Time Field + Quick Presets
                        Column {
                            OutlinedTextField(
                                value = editInTime,
                                onValueChange = { editInTime = it },
                                label = { Text("In Time (Check-In)") },
                                placeholder = { Text("e.g. 09:00 AM") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                },
                                trailingIcon = {
                                    if (editInTime.isNotEmpty()) {
                                        IconButton(onClick = { editInTime = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("edit_in_time_input")
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("09:00 AM", "09:30 AM", "10:00 AM", "08:00 PM").forEach { preset ->
                                    SuggestionChip(
                                        onClick = { editInTime = preset },
                                        label = { Text(preset, fontSize = 9.sp) }
                                    )
                                }
                            }
                        }

                        // Out Time Field + Quick Presets
                        Column {
                            OutlinedTextField(
                                value = editOutTime,
                                onValueChange = { editOutTime = it },
                                label = { Text("Out Time (Check-Out)") },
                                placeholder = { Text("e.g. 06:00 PM") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(18.dp))
                                },
                                trailingIcon = {
                                    if (editOutTime.isNotEmpty()) {
                                        IconButton(onClick = { editOutTime = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("edit_out_time_input")
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("05:00 PM", "06:00 PM", "07:00 PM", "08:00 AM").forEach { preset ->
                                    SuggestionChip(
                                        onClick = { editOutTime = preset },
                                        label = { Text(preset, fontSize = 9.sp) }
                                    )
                                }
                            }
                        }
                    }

                    // Reason Field (Public/Attendance reason)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "🏷️ Reason (Public / Attendance Reason)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        OutlinedTextField(
                            value = editReason,
                            onValueChange = { editReason = it },
                            label = { Text("Reason") },
                            placeholder = { Text("e.g. Sick Leave, Emergency, Delay, Meeting") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_reason_input")
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val reasonPresets = if (editStatus == "A" || editStatus == "ABSENT") {
                                listOf("Sick Leave", "Emergency", "Personal", "Exam")
                            } else {
                                listOf("Meeting", "Late Commute", "Approved Leave", "Half Day")
                            }
                            reasonPresets.forEach { preset ->
                                SuggestionChip(
                                    onClick = { editReason = preset },
                                    label = { Text(preset, fontSize = 9.sp) }
                                )
                            }
                        }
                    }

                    // Remarks Field (Private note)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "🔒 Remarks / Notes (Private)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        OutlinedTextField(
                            value = editRemarks,
                            onValueChange = { editRemarks = it },
                            label = { Text("Remarks (Private)") },
                            placeholder = { Text("e.g. Approved by HR, Will compensate hours") },
                            leadingIcon = {
                                Icon(Icons.Default.EditNote, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                if (editRemarks.isNotEmpty()) {
                                    IconButton(onClick = { editRemarks = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_remarks_input"),
                            minLines = 2,
                            maxLines = 3
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val presets = if (editStatus == "A" || editStatus == "ABSENT") {
                                listOf("Approved Leave", "Sick Leave", "Emergency", "OT Only")
                            } else {
                                listOf("Leave Approved", "Half Day", "Client Visit", "Overtime 1h")
                            }
                            presets.forEach { preset ->
                                SuggestionChip(
                                    onClick = { editRemarks = preset },
                                    label = { Text(preset, fontSize = 9.sp) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val isA = editStatus == "A" || editStatus == "ABSENT"
                        val finalIn = when {
                            isA -> if (editHasOvertime) editInTime.trim() else ""
                            editStatus == "OFF" -> ""
                            else -> editInTime.trim()
                        }
                        val finalOut = when {
                            isA -> if (editHasOvertime) editOutTime.trim() else ""
                            editStatus == "OFF" -> ""
                            else -> editOutTime.trim()
                        }
                        onUpdateRecord(
                            selectedStudent.id,
                            targetRow.dateString,
                            editStatus,
                            finalIn,
                            finalOut,
                            editRemarks.trim(),
                            editReason.trim()
                        )
                        editingRow = null
                    },
                    modifier = Modifier.testTag("save_edit_record_btn")
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingRow = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = !isFullScreen),
        modifier = if (isFullScreen) {
            Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 6.dp)
        } else {
            Modifier
                .fillMaxWidth(0.98f)
                .fillMaxHeight(0.92f)
        },
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "MONTHLY ATTENDANCE REPORT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Box {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showStudentDropdown = true }
                                    .padding(vertical = 1.dp)
                            ) {
                                Text(
                                    text = selectedStudent.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Staff",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            DropdownMenu(
                                expanded = showStudentDropdown,
                                onDismissRequest = { showStudentDropdown = false }
                            ) {
                                allStudents.forEach { st ->
                                    DropdownMenuItem(
                                        text = { Text("${st.name} (${st.rollNumber})") },
                                        onClick = {
                                            selectedStudent = st
                                            recipientPhone = st.phone
                                            onSelectStudent(st)
                                            showStudentDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { isFullScreen = !isFullScreen },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = if (isFullScreen) "Exit Fullscreen" else "Full Screen",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Emp ID: ${selectedStudent.rollNumber} • Dept: ${activeClass.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (studentShift != null) {
                    Text(
                        text = "Shift: ${studentShift.name} (${studentShift.timings})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Month & Year Navigator (compact)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (currentMonth == 1) {
                                    currentMonth = 12
                                    currentYear--
                                } else {
                                    currentMonth--
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Month", modifier = Modifier.size(20.dp))
                        }

                        Text(
                            text = "${monthNames[currentMonth - 1]} $currentYear",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        IconButton(
                            onClick = {
                                if (currentMonth == 12) {
                                    currentMonth = 1
                                    currentYear++
                                } else {
                                    currentMonth++
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Month", modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Monthly Summary Stat Grid (Compact rows + Overtime block)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        ReportStatChip(
                            label = "P",
                            value = "$presentCount",
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.weight(1f)
                        )
                        ReportStatChip(
                            label = "A",
                            value = "$absentCount",
                            color = Color(0xFFC62828),
                            modifier = Modifier.weight(1f)
                        )
                        ReportStatChip(
                            label = "HD MOR",
                            value = "$halfDayMorCount",
                            color = Color(0xFFE65100),
                            modifier = Modifier.weight(1.15f)
                        )
                        ReportStatChip(
                            label = "HD EVE",
                            value = "$halfDayEveCount",
                            color = Color(0xFF7B1FA2),
                            modifier = Modifier.weight(1.15f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ReportStatChip(
                            label = "Late Days",
                            value = if (lateDaysCount > 0) "$lateDaysCount ($formattedTotalLate)" else "0",
                            color = Color(0xFF6A1B9A),
                            modifier = Modifier.weight(1f)
                        )
                        ReportStatChip(
                            label = "Avg Late / Day",
                            value = formattedAvgLate,
                            color = Color(0xFF8E24AA),
                            modifier = Modifier.weight(1f)
                        )
                        ReportStatChip(
                            label = "Gone Early",
                            value = if (earlyDaysCount > 0) "$earlyDaysCount ($formattedTotalEarly)" else "0",
                            color = Color(0xFFE65100),
                            modifier = Modifier.weight(1f)
                        )
                        ReportStatChip(
                            label = "Avg Early / Day",
                            value = formattedAvgEarly,
                            color = Color(0xFFD84315),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Dedicated OVERTIME (OT) Summary Block in Report
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        border = BorderStroke(1.dp, Color(0xFFFFB74D))
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = "Overtime",
                                        tint = Color(0xFFE65100),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "OVERTIME (OT)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100)
                                    )
                                }
                                Text(
                                    text = "$overtimeDaysCount Days • $formattedTotalOvertime Total",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                ReportStatChip(
                                    label = "Total OT",
                                    value = formattedTotalOvertime,
                                    color = Color(0xFFE65100),
                                    modifier = Modifier.weight(1f)
                                )
                                ReportStatChip(
                                    label = "Advance In",
                                    value = formattedEarlyInOt,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.weight(1f)
                                )
                                ReportStatChip(
                                    label = "Late Out",
                                    value = formattedLateOutOt,
                                    color = Color(0xFFC2185B),
                                    modifier = Modifier.weight(1f)
                                )
                                ReportStatChip(
                                    label = "Avg OT / Day",
                                    value = formattedAvgOvertime,
                                    color = Color(0xFFD84315),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Compact Send & Share Section Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = recipientPhone,
                            onValueChange = { recipientPhone = it },
                            placeholder = { Text("Phone No.", fontSize = 10.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.primary)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("report_recipient_phone_input"),
                            textStyle = TextStyle(fontSize = 11.sp)
                        )

                        // WhatsApp Button
                        Button(
                            onClick = { sendViaWhatsApp() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25D366),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("send_whatsapp_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("WhatsApp", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }

                        // SMS Button
                        Button(
                            onClick = { sendViaSms() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("send_sms_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("SMS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }

                        // Share System Button
                        OutlinedButton(
                            onClick = { shareViaSystem() },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("share_system_btn")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (copyNoticeMessage != null) {
                    Text(
                        text = copyNoticeMessage!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                // Table Header with Gone Early, Overtime, Remarks and Edit columns
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .padding(horizontal = 4.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("DATE", fontWeight = FontWeight.ExtraBold, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(0.95f))
                    Text("STATUS", fontWeight = FontWeight.ExtraBold, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(0.8f))
                    Text("IN", fontWeight = FontWeight.ExtraBold, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(0.7f))
                    Text("OUT", fontWeight = FontWeight.ExtraBold, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(0.7f))
                    Text("LATE", fontWeight = FontWeight.ExtraBold, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(0.65f))
                    Text("EARLY", fontWeight = FontWeight.ExtraBold, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(0.7f))
                    Text("OT", fontWeight = FontWeight.ExtraBold, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(0.75f))
                    Text("REMARKS", fontWeight = FontWeight.ExtraBold, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(0.85f))
                    Text("EDIT", fontWeight = FontWeight.ExtraBold, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(0.4f), textAlign = TextAlign.Center)
                }

                HorizontalDivider()

                // Daily Breakdown Ledger List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(daysInMonth) { row ->
                        val isMarked = row.status in listOf("P", "PRESENT", "HD MOR", "HD EVE", "HALF-DAY", "A", "ABSENT")
                        val rowBg = when (row.status) {
                            "P", "PRESENT" -> Color(0xFFE8F5E9)
                            "HD MOR" -> Color(0xFFFFF3E0)
                            "HD EVE" -> Color(0xFFF3E5F5)
                            "HALF-DAY" -> Color(0xFFFFF3E0)
                            "A", "ABSENT" -> Color(0xFFFFEBEE)
                            else -> if (row.isWeekend) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                        }

                        // High contrast text colors for marked/unmarked rows
                        val dateTextColor = if (isMarked) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface
                        val timeTextColor = if (isMarked) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface
                        val dashTextColor = if (isMarked) Color(0xFF475569) else MaterialTheme.colorScheme.onSurfaceVariant
                        val editIconTint = if (isMarked) Color(0xFF1565C0) else MaterialTheme.colorScheme.primary

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(rowBg)
                                .clickable { editingRow = row }
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Date
                                Text(
                                    text = row.displayDate,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    fontSize = 8.5.sp,
                                    color = dateTextColor,
                                    modifier = Modifier.weight(0.95f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                // Status badge
                                Box(modifier = Modifier.weight(0.8f)) {
                                    val (statusColor, statusBg) = when (row.status) {
                                        "P", "PRESENT" -> Pair(Color(0xFF1B5E20), Color(0xFFC8E6C9))
                                        "A", "ABSENT" -> Pair(Color(0xFFB71C1C), Color(0xFFFFCDD2))
                                        "HD MOR" -> Pair(Color(0xFFBF360C), Color(0xFFFFE0B2))
                                        "HD EVE" -> Pair(Color(0xFF6A1B9A), Color(0xFFE1BEE7))
                                        "HALF-DAY" -> Pair(Color(0xFFBF360C), Color(0xFFFFE0B2))
                                        else -> Pair(MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(statusBg)
                                            .padding(horizontal = 2.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = row.status,
                                            color = statusColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 7.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                // In Time
                                Text(
                                    text = row.checkInTime.ifEmpty { "-" },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (row.checkInTime.isNotEmpty()) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = timeTextColor,
                                    fontSize = 8.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(0.7f)
                                )

                                // Out Time
                                Text(
                                    text = row.checkOutTime.ifEmpty { "-" },
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = timeTextColor,
                                    fontSize = 8.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(0.7f)
                                )

                                // Late Time Column
                                Box(modifier = Modifier.weight(0.65f)) {
                                    if (row.lateMinutes > 0) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFFCE4EC))
                                                .padding(horizontal = 2.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = row.lateTimeString,
                                                color = Color(0xFFC2185B),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 7.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "-",
                                            color = dashTextColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.sp
                                        )
                                    }
                                }

                                // Gone Early Column
                                Box(modifier = Modifier.weight(0.7f)) {
                                    if (row.earlyMinutes > 0) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFFFF3E0))
                                                .padding(horizontal = 2.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = row.earlyTimeString,
                                                color = Color(0xFFE65100),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 7.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "-",
                                            color = dashTextColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.sp
                                        )
                                    }
                                }

                                // Overtime Column
                                Box(modifier = Modifier.weight(0.75f)) {
                                    if (row.overtimeMinutes > 0) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFFFE0B2))
                                                .padding(horizontal = 2.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "⚡ ${row.overtimeTimeString}",
                                                color = Color(0xFFE65100),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 7.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "-",
                                            color = dashTextColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.sp
                                        )
                                    }
                                }

                                // Remarks Column
                                Box(modifier = Modifier.weight(0.85f)) {
                                    if (row.notes.isNotBlank()) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFFFF8E1))
                                                .padding(horizontal = 2.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "📝 ${row.notes}",
                                                color = Color(0xFF78350F),
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 7.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "-",
                                            color = dashTextColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.sp
                                        )
                                    }
                                }

                                // Edit Icon Button
                                Box(
                                    modifier = Modifier.weight(0.4f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(
                                        onClick = { editingRow = row },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .testTag("edit_report_row_${row.dayNumber}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Attendance",
                                            tint = editIconTint,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            // Dedicated Remarks Highlight line below the row if remarks exist
                            if (row.notes.isNotBlank()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 4.dp, top = 2.dp, end = 4.dp, bottom = 2.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFFF8E1))
                                        .border(0.5.dp, Color(0xFFFFD54F), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EditNote,
                                        contentDescription = null,
                                        tint = Color(0xFFE65100),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Remarks: ${row.notes}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.5.sp
                                        ),
                                        color = Color(0xFFE65100),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
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
                    val reportText = generateReportText(isWhatsApp = false)
                    clipboardManager.setText(AnnotatedString(reportText))
                    copyNoticeMessage = "Report copied to clipboard!"
                },
                modifier = Modifier.testTag("copy_monthly_report_btn")
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Text")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun BackupRestoreDialog(
    onDismiss: () -> Unit,
    onCreateBackup: () -> Unit,
    onRestoreAutoBackup: () -> Unit,
    onSelectBackupFile: () -> Unit,
    onOpenFirebaseSync: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Backup & Safe Restore", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Firebase Automatic Cloud Protection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    border = BorderStroke(1.dp, Color(0xFFFFB74D))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Cloud, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Firebase Automatic Cloud Sync",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFFBF360C)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Automatic cloud protection! Even after reinstalling or on another phone, all your staff, shifts, and attendance logs are safely restored automatically.",
                            fontSize = 11.5.sp,
                            color = Color(0xFFD84315),
                            lineHeight = 16.sp
                        )
                        Button(
                            onClick = onOpenFirebaseSync,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                        ) {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Configure Firebase Cloud Sync", fontSize = 12.5.sp)
                        }
                    }
                }

                // Auto-protection info box
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Auto-Protection Active",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF1B5E20)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Your staff and attendance records are continuously backed up to device storage on every change. If you ever reinstall the app, data can be restored with 1 tap.",
                                fontSize = 11.5.sp,
                                color = Color(0xFF2E7D32),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Action 1: Create & Share Backup
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Save / Export Backup File",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Generates a full snapshot file (.json) that you can send to WhatsApp, Google Drive, Gmail, or save to your Downloads folder.",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Button(
                            onClick = {
                                onCreateBackup()
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create & Share Backup (.json)", fontSize = 12.5.sp)
                        }
                    }
                }

                // Action 2: Restore from Device Storage
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Restore Previous Data",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Instantly restore all staff members, shifts, and attendance logs from your phone's auto-backup.",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onRestoreAutoBackup()
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Auto-Restore", fontSize = 11.5.sp)
                            }
                            Button(
                                onClick = {
                                    onSelectBackupFile()
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pick File", fontSize = 11.5.sp)
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

@Composable
fun RearrangeDepartmentsDialog(
    departments: List<ClassEntity>,
    onDismiss: () -> Unit,
    onMoveUp: (ClassEntity) -> Unit,
    onMoveDown: (ClassEntity) -> Unit,
    onSort: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Rearrange Departments", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Change the display order of departments. Use the ▲ and ▼ buttons or quick sort presets.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Sort Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { onSort("AZ") },
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.SortByAlpha, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("A → Z", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onSort("ZA") },
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.SortByAlpha, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Z → A", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onSort("ID_ASC") },
                        modifier = Modifier
                            .weight(1.1f)
                            .height(34.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Original", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (departments.isEmpty()) {
                    Text(
                        text = "No departments to rearrange.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    // List of Departments with Up/Down buttons
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 340.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(departments) { index, dept ->
                            val color = parseColor(dept.colorHex)
                            val isFirst = index == 0
                            val isLast = index == departments.size - 1

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Position number badge
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(color.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Black,
                                                    color = color
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        // Color Dot
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = dept.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Move Up Button
                                        IconButton(
                                            onClick = { onMoveUp(dept) },
                                            enabled = !isFirst,
                                            modifier = Modifier
                                                .size(32.dp)
                                                .testTag("move_up_dept_${dept.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropUp,
                                                contentDescription = "Move Up",
                                                tint = if (!isFirst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        // Move Down Button
                                        IconButton(
                                            onClick = { onMoveDown(dept) },
                                            enabled = !isLast,
                                            modifier = Modifier
                                                .size(32.dp)
                                                .testTag("move_down_dept_${dept.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Move Down",
                                                tint = if (!isLast) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
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
                onClick = onDismiss,
                modifier = Modifier.testTag("close_rearrange_dialog")
            ) {
                Text("Done")
            }
        }
    )
}
