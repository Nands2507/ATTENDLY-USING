package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AttendanceRecordEntity
import com.example.data.ChecklistItemEntity
import com.example.data.ClassEntity
import com.example.data.HisabLedgerEntryEntity
import com.example.data.HisabProductEntity
import com.example.data.ShiftEntity
import com.example.data.StudentEntity
import kotlinx.coroutines.launch
import java.util.Locale

data class AppGuideItem(
    val title: String,
    val category: String,
    val description: String,
    val steps: List<String>,
    val actionLabel: String? = null,
    val actionType: String? = null
)

val APP_GUIDE_DATABASE = listOf(
    AppGuideItem(
        title = "How to Mark Daily Attendance & Timings",
        category = "Attendance",
        description = "Quickly mark staff as Present, Absent, Half-Day Morning/Evening, or Leave.",
        steps = listOf(
            "Go to 'Take Attendance' tab (Tab 1).",
            "Select the department from top bar.",
            "Tap 'P' for Full Present, 'A' for Absent, 'HD-M' or 'HD-E' for Half Day, 'L' for Leave.",
            "Tap the Note/Clock icon on any employee card to record specific In/Out timings, notes, and remarks.",
            "Use 'Mark All Present' for bulk marking in seconds."
        ),
        actionLabel = "Go to Attendance",
        actionType = "attendance"
    ),
    AppGuideItem(
        title = "How to use T-HISAB (Piece-Rate, Advances & Salary Ledger)",
        category = "T-Hisab",
        description = "Track worker piece-rate production, advance loan payments, and calculate net balances.",
        steps = listOf(
            "Switch to 'T-HISAB' tab (Tab 3) or tap the green T-Hisab icon next to any staff member.",
            "Select the employee to open their active ledger.",
            "Add Piece-Rate Work: Enter quantity produced and select item rate (e.g. Saree, Stitching, Packing).",
            "Add Advance / Kharcha: Record any advance cash or salary paid.",
            "View live Net Balance: The app automatically calculates Total Earnings - Advances = Remaining Balance.",
            "Export / Share passbook statements via WhatsApp or PDF/Excel."
        ),
        actionLabel = "Open T-HISAB",
        actionType = "hisab"
    ),
    AppGuideItem(
        title = "How to Manage Employee Checklist & Daily Tasks",
        category = "Checklist",
        description = "Assign and track daily tasks for individual employees or departments with clock-dial reminders.",
        steps = listOf(
            "Go to 'Checklist' tab (Tab 2) or tap the purple Checklist icon next to any staff name.",
            "Filter tasks by Specific Employee or Department.",
            "Tap '+ Add Task' to type title, description, priority, and assign to a staff member.",
            "Set exact Due Date & Clock-Dial reminder to get notified on phone.",
            "Tap the checkbox to mark tasks completed."
        ),
        actionLabel = "Open Checklist",
        actionType = "checklist"
    ),
    AppGuideItem(
        title = "How to Export Monthly PDF & Excel Attendance Reports",
        category = "Reports",
        description = "Generate and share full monthly employee attendance & salary registers.",
        steps = listOf(
            "Go to 'Staff & Shifts' tab (Tab 4) or tap the Monthly Report icon next to any employee.",
            "Select the Month and Year.",
            "Review full attendance breakdown: Total Present, Absent, Half Days, and Attendance Rate %.",
            "Tap 'Export PDF' to generate a clean printable document or 'Export Excel (CSV)' for spreadsheet records.",
            "Share directly via WhatsApp, Email, or Google Drive."
        ),
        actionLabel = "View Staff & Reports",
        actionType = "staff"
    ),
    AppGuideItem(
        title = "How to Backup & Restore Data Locally or to Cloud",
        category = "Backup & Cloud",
        description = "Safeguard your entire business database, staff records, and financial ledgers.",
        steps = listOf(
            "Tap the 'Backup' button on the top header.",
            "Local Backup: Tap 'Export Backup File (.json)' and save to your Downloads/Drive.",
            "Cloud Sync: Use the 'Firebase' button on top to enable real-time automatic cloud sync across devices.",
            "Google Sheets Sync: Use 'Sheets' button on top to push attendance registers directly into Google Sheets.",
            "To restore on a new phone: Open Backup dialog, tap 'Import & Restore', and select your backup file."
        ),
        actionLabel = "Open Backup Options",
        actionType = "backup"
    ),
    AppGuideItem(
        title = "How to Add Departments & Rearrange Their Order",
        category = "Departments",
        description = "Organize staff into custom departments (e.g. Sales, Godown, Tailoring, Office).",
        steps = listOf(
            "Tap '+ Department' on the top bar to create a new section.",
            "Enter department name and optional color/order.",
            "To rearrange order: Tap the 'Rearrange' button on the top bar and use the Up/Down arrows to position departments."
        ),
        actionLabel = "Add Department",
        actionType = "add_department"
    ),
    AppGuideItem(
        title = "How to Add Staff Members & Assign Work Shifts",
        category = "Staff",
        description = "Register employees with phone, email, roll ID, and work shift timings.",
        steps = listOf(
            "Tap '+ Add Staff' on the top bar.",
            "Enter staff name, roll/employee ID, department, phone, and email.",
            "Assign an active Work Shift (e.g. Morning 9 AM - 6 PM, Night Shift).",
            "You can create custom shifts in the 'Staff & Shifts' tab."
        ),
        actionLabel = "Add Staff",
        actionType = "add_staff"
    )
)

@Composable
fun AiLensDialog(
    allClasses: List<ClassEntity>,
    allStudents: List<StudentEntity>,
    allShifts: List<ShiftEntity>,
    allHisabProducts: List<HisabProductEntity>,
    allHisabLedgerEntries: List<HisabLedgerEntryEntity>,
    allChecklistItems: List<ChecklistItemEntity>,
    todayRecords: List<AttendanceRecordEntity>,
    selectedClass: ClassEntity?,
    onDismiss: () -> Unit,
    onSelectDepartment: (ClassEntity) -> Unit,
    onSelectStudentAttendance: (StudentEntity) -> Unit,
    onSelectStudentChecklist: (StudentEntity) -> Unit,
    onSelectStudentHisab: (StudentEntity) -> Unit,
    onSelectStudentReport: (StudentEntity) -> Unit,
    onTriggerAddStaff: () -> Unit,
    onTriggerAddDepartment: () -> Unit,
    onTriggerBackup: () -> Unit,
    onTriggerFirebaseSync: () -> Unit,
    onTriggerGoogleSync: () -> Unit,
    onTriggerChecklistTab: () -> Unit,
    onTriggerHisabTab: () -> Unit,
    onTriggerStaffTab: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: All, 1: Staff, 2: Depts, 3: T-Hisab, 4: Tasks, 5: App Guide, 6: Ask AI
    var aiAnswer by remember { mutableStateOf<String?>(null) }
    var isAiLoading by remember { mutableStateOf(false) }
    var aiQuerySent by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val filterTabs = listOf("All", "Staff", "Depts", "T-Hisab", "Tasks", "App Guide", "Ask AI")

    // Filter staff
    val filteredStudents = remember(searchQuery, allStudents) {
        if (searchQuery.isBlank()) allStudents.take(8)
        else {
            val q = searchQuery.trim().lowercase(Locale.getDefault())
            allStudents.filter {
                it.name.lowercase(Locale.getDefault()).contains(q) ||
                        it.rollNumber.lowercase(Locale.getDefault()).contains(q) ||
                        it.phone.lowercase(Locale.getDefault()).contains(q) ||
                        it.email.lowercase(Locale.getDefault()).contains(q)
            }
        }
    }

    // Filter departments
    val filteredClasses = remember(searchQuery, allClasses) {
        if (searchQuery.isBlank()) allClasses
        else {
            val q = searchQuery.trim().lowercase(Locale.getDefault())
            allClasses.filter { it.name.lowercase(Locale.getDefault()).contains(q) }
        }
    }

    // Filter checklist
    val filteredTasks = remember(searchQuery, allChecklistItems) {
        if (searchQuery.isBlank()) allChecklistItems.take(5)
        else {
            val q = searchQuery.trim().lowercase(Locale.getDefault())
            allChecklistItems.filter {
                it.title.lowercase(Locale.getDefault()).contains(q) ||
                        it.notes.lowercase(Locale.getDefault()).contains(q) ||
                        it.assignedStaffName.lowercase(Locale.getDefault()).contains(q)
            }
        }
    }

    // Filter hisab
    val filteredHisab = remember(searchQuery, allHisabLedgerEntries) {
        if (searchQuery.isBlank()) allHisabLedgerEntries.take(5)
        else {
            val q = searchQuery.trim().lowercase(Locale.getDefault())
            allHisabLedgerEntries.filter {
                it.notes.lowercase(Locale.getDefault()).contains(q) ||
                        it.entryType.lowercase(Locale.getDefault()).contains(q) ||
                        it.itemBreakdownJson.lowercase(Locale.getDefault()).contains(q)
            }
        }
    }

    // Filter guide
    val filteredGuide = remember(searchQuery) {
        if (searchQuery.isBlank()) APP_GUIDE_DATABASE
        else {
            val q = searchQuery.trim().lowercase(Locale.getDefault())
            APP_GUIDE_DATABASE.filter {
                it.title.lowercase(Locale.getDefault()).contains(q) ||
                        it.category.lowercase(Locale.getDefault()).contains(q) ||
                        it.description.lowercase(Locale.getDefault()).contains(q) ||
                        it.steps.any { step -> step.lowercase(Locale.getDefault()).contains(q) }
            }
        }
    }

    // Today's stats calculation
    val todayPresent = todayRecords.count { it.status.equals("PRESENT", ignoreCase = true) || it.status.equals("Present", ignoreCase = true) }
    val todayAbsent = todayRecords.count { it.status.equals("ABSENT", ignoreCase = true) || it.status.equals("Absent", ignoreCase = true) }
    val todayHalfDay = todayRecords.count { it.status.startsWith("HALF", ignoreCase = true) || it.status.startsWith("Half", ignoreCase = true) }
    val todayLeave = todayRecords.count { it.status.equals("LEAVE", ignoreCase = true) || it.status.equals("Leave", ignoreCase = true) }

    // Quick AI / Smart Q&A function
    val executeAiQuery: (String) -> Unit = { query ->
        val trimmedQuery = query.trim()
        if (trimmedQuery.isNotBlank()) {
            searchQuery = trimmedQuery
            aiQuerySent = trimmedQuery
            isAiLoading = true
            aiAnswer = null
            selectedFilterIndex = 6 // Switch to AI Q&A tab

            coroutineScope.launch {
                val systemContext = """
                    You are AI Lens, the intelligent assistant for the "Shop Attendance & Staff Manager" Android application.
                    Current Live Business State:
                    - Total Staff Members: ${allStudents.size}
                    - Departments: ${allClasses.map { it.name }.joinToString(", ")}
                    - Today's Attendance Status: Present: $todayPresent, Absent: $todayAbsent, Half-Day: $todayHalfDay, Leave: $todayLeave
                    - Total Checklist Items: ${allChecklistItems.size} (Pending: ${allChecklistItems.count { !it.isCompleted }})
                    - Total T-Hisab Ledger Entries: ${allHisabLedgerEntries.size}
                    
                    App Features Overview:
                    1. Attendance Tab: Daily roll-call, Present/Absent/Half-Day Morning/Evening/Leave, in/out timings, remarks.
                    2. Checklist Tab: Task management for staff and departments with clock-dial reminders and priorities.
                    3. T-HISAB Tab: Piece-rate production ledger, advance cash payment tracking, automatic balance calculation, passbook statements.
                    4. Staff & Shifts Tab: Staff management, shift timings assignment, full monthly PDF/Excel reports and analytics.
                    5. Top Bar: Quick '+ Add Staff', '+ Department', 'Rearrange', 'Backup & Restore', 'Firebase Cloud Sync', 'Google Sheets Sync'.

                    Instructions:
                    Answer the user's question concisely, practically, and helpfully.
                    If they ask about data (attendance, staff, hisab, tasks), summarize clearly using the live statistics above.
                    If they ask "how to do something", provide step-by-step instructions.
                """.trimIndent()

                val result = AiLensService.askGemini(trimmedQuery, systemContext)
                isAiLoading = false

                if (result.isSuccess) {
                    aiAnswer = result.getOrNull()
                } else {
                    // Fallback to rich built-in smart assistant engine
                    aiAnswer = buildSmartFallbackAnswer(
                        query = trimmedQuery,
                        allStudents = allStudents,
                        allClasses = allClasses,
                        allChecklistItems = allChecklistItems,
                        allHisabLedgerEntries = allHisabLedgerEntries,
                        todayRecords = todayRecords,
                        todayPresent = todayPresent,
                        todayAbsent = todayAbsent,
                        todayHalfDay = todayHalfDay,
                        todayLeave = todayLeave
                    )
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
                .testTag("ai_lens_dialog_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header with AI Lens Badge and Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        listOf(Color(0xFF2563EB), Color(0xFF7C3AED), Color(0xFFDB2777))
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Lens",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AI Lens & Smart Search",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF7C3AED).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "GEMINI AI",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF7C3AED)
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Search staff, hisab, checklists, guides & ask anything",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("ai_lens_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search staff, tasks, hisab or ask 'who is absent?'...",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchQuery.isNotBlank()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            IconButton(
                                onClick = { executeAiQuery(searchQuery) },
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                                    .testTag("ai_lens_ask_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Ask AI",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { executeAiQuery(searchQuery) }),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_lens_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick AI Suggestion Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    val suggestions = listOf(
                        "👥 Who is absent today?",
                        "📊 Today's attendance summary",
                        "💰 Pending T-Hisab advances",
                        "📋 Incomplete checklist tasks",
                        "❓ How to calculate salary & piece rates?",
                        "💾 How to backup database?",
                        "⏱️ How to set clock reminders?"
                    )
                    items(suggestions) { chipText ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                            modifier = Modifier
                                .clickable { executeAiQuery(chipText.replace(Regex("^[\\p{So}\\p{Sk}\\p{Sm}\\p{Sc}\\p{Cn}]+\\s*"), "")) }
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        ) {
                            Text(
                                text = chipText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Tabs (All, Staff, Depts, T-Hisab, Tasks, App Guide, Ask AI)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(filterTabs) { tab ->
                        val index = filterTabs.indexOf(tab)
                        val isSelected = selectedFilterIndex == index
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .clickable { selectedFilterIndex = index }
                                .testTag("ai_lens_tab_$tab")
                        ) {
                            Text(
                                text = tab,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                // Results Content View
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        // 1. AI Answer Box (if active or loaded)
                        if (isAiLoading) {
                            item {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.5.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "AI Lens is analyzing query...",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "Checking database context & guidelines",
                                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            )
                                        }
                                    }
                                }
                            }
                        } else if (!aiAnswer.isNullOrBlank()) {
                            item {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF).copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                                        .testTag("ai_lens_response_card")
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = "AI Response",
                                                    tint = Color(0xFF7C3AED),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "AI Lens Answer",
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF6B21A8)
                                                    )
                                                )
                                            }
                                            if (aiQuerySent != null) {
                                                Text(
                                                    text = "\"$aiQuerySent\"",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = Color(0xFF6B21A8).copy(alpha = 0.7f)
                                                    ),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.widthIn(max = 140.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = aiAnswer ?: "",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 13.5.sp,
                                                lineHeight = 20.sp,
                                                color = Color(0xFF1F2937)
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        // Quick deep link shortcuts
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Button(
                                                onClick = {
                                                    onDismiss()
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp)
                                            ) {
                                                Text("Attendance", fontSize = 11.sp)
                                            }

                                            Button(
                                                onClick = {
                                                    onDismiss()
                                                    onTriggerHisabTab()
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp)
                                            ) {
                                                Text("T-HISAB", fontSize = 11.sp)
                                            }

                                            Button(
                                                onClick = {
                                                    onDismiss()
                                                    onTriggerChecklistTab()
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp)
                                            ) {
                                                Text("Checklist", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Staff Members Found
                        if (selectedFilterIndex == 0 || selectedFilterIndex == 1) {
                            if (filteredStudents.isNotEmpty()) {
                                item {
                                    SectionHeader(
                                        title = "Staff Members (${filteredStudents.size})",
                                        icon = Icons.Default.People,
                                        onAction = onTriggerAddStaff,
                                        actionText = "+ Add Staff"
                                    )
                                }
                                items(filteredStudents) { student ->
                                    val deptName = allClasses.firstOrNull { it.id == student.classId }?.name ?: "Department"
                                    StaffResultCard(
                                        student = student,
                                        deptName = deptName,
                                        onMarkAttendance = {
                                            onDismiss()
                                            val dept = allClasses.firstOrNull { it.id == student.classId }
                                            if (dept != null) onSelectDepartment(dept)
                                            onSelectStudentAttendance(student)
                                        },
                                        onChecklist = {
                                            onDismiss()
                                            onSelectStudentChecklist(student)
                                        },
                                        onHisab = {
                                            onDismiss()
                                            onSelectStudentHisab(student)
                                        },
                                        onReport = {
                                            onDismiss()
                                            onSelectStudentReport(student)
                                        }
                                    )
                                }
                            } else if (selectedFilterIndex == 1) {
                                item {
                                    EmptyStateView(message = "No staff members matched '$searchQuery'")
                                }
                            }
                        }

                        // 3. Departments Found
                        if (selectedFilterIndex == 0 || selectedFilterIndex == 2) {
                            if (filteredClasses.isNotEmpty()) {
                                item {
                                    SectionHeader(
                                        title = "Departments (${filteredClasses.size})",
                                        icon = Icons.Default.Storefront,
                                        onAction = onTriggerAddDepartment,
                                        actionText = "+ Department"
                                    )
                                }
                                items(filteredClasses) { dept ->
                                    val staffCount = allStudents.count { it.classId == dept.id }
                                    DepartmentResultCard(
                                        dept = dept,
                                        staffCount = staffCount,
                                        isSelected = selectedClass?.id == dept.id,
                                        onSelect = {
                                            onDismiss()
                                            onSelectDepartment(dept)
                                        }
                                    )
                                }
                            } else if (selectedFilterIndex == 2) {
                                item {
                                    EmptyStateView(message = "No departments matched '$searchQuery'")
                                }
                            }
                        }

                        // 4. T-Hisab Ledger & Advances
                        if (selectedFilterIndex == 0 || selectedFilterIndex == 3) {
                            if (filteredHisab.isNotEmpty()) {
                                item {
                                    SectionHeader(
                                        title = "T-HISAB Records (${filteredHisab.size})",
                                        icon = Icons.Default.Description,
                                        onAction = {
                                            onDismiss()
                                            onTriggerHisabTab()
                                        },
                                        actionText = "Open T-Hisab"
                                    )
                                }
                                items(filteredHisab) { hisab ->
                                    val student = allStudents.firstOrNull { it.id == hisab.studentId }
                                    HisabResultCard(
                                        entry = hisab,
                                        studentName = student?.name ?: "Staff #${hisab.studentId}",
                                        onOpen = {
                                            onDismiss()
                                            if (student != null) onSelectStudentHisab(student)
                                            else onTriggerHisabTab()
                                        }
                                    )
                                }
                            } else if (selectedFilterIndex == 3) {
                                item {
                                    EmptyStateView(message = "No T-Hisab ledger entries found.")
                                }
                            }
                        }

                        // 5. Checklist Tasks Found
                        if (selectedFilterIndex == 0 || selectedFilterIndex == 4) {
                            if (filteredTasks.isNotEmpty()) {
                                item {
                                    SectionHeader(
                                        title = "Checklist & Tasks (${filteredTasks.size})",
                                        icon = Icons.Default.Checklist,
                                        onAction = {
                                            onDismiss()
                                            onTriggerChecklistTab()
                                        },
                                        actionText = "Open Checklist"
                                    )
                                }
                                items(filteredTasks) { task ->
                                    TaskResultCard(
                                        task = task,
                                        onOpen = {
                                            onDismiss()
                                            val student = allStudents.firstOrNull { it.id == task.studentId }
                                            if (student != null) onSelectStudentChecklist(student)
                                            else onTriggerChecklistTab()
                                        }
                                    )
                                }
                            } else if (selectedFilterIndex == 4) {
                                item {
                                    EmptyStateView(message = "No checklist tasks matched.")
                                }
                            }
                        }

                        // 6. App Guide & How-To FAQ
                        if (selectedFilterIndex == 0 || selectedFilterIndex == 5) {
                            if (filteredGuide.isNotEmpty()) {
                                item {
                                    SectionHeader(
                                        title = "App Help & How-To Guides",
                                        icon = Icons.Default.HelpOutline
                                    )
                                }
                                items(filteredGuide) { guide ->
                                    GuideResultCard(
                                        guide = guide,
                                        onAction = {
                                            onDismiss()
                                            when (guide.actionType) {
                                                "attendance" -> { /* Stay / Go to attendance */ }
                                                "hisab" -> onTriggerHisabTab()
                                                "checklist" -> onTriggerChecklistTab()
                                                "staff" -> onTriggerStaffTab()
                                                "backup" -> onTriggerBackup()
                                                "add_staff" -> onTriggerAddStaff()
                                                "add_department" -> onTriggerAddDepartment()
                                            }
                                        }
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
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    onAction: (() -> Unit)? = null,
    actionText: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        if (onAction != null && actionText != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier
                    .clickable { onAction() }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
private fun StaffResultCard(
    student: StudentEntity,
    deptName: String,
    onMarkAttendance: () -> Unit,
    onChecklist: () -> Unit,
    onHisab: () -> Unit,
    onReport: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.name.take(1).uppercase(Locale.getDefault()),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$deptName • Roll: ${if (student.rollNumber.isBlank()) "N/A" else student.rollNumber}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 4 Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attendance
                    IconButton(
                        onClick = onMarkAttendance,
                        modifier = Modifier
                            .size(30.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.HowToReg,
                            contentDescription = "Attendance",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Checklist
                    IconButton(
                        onClick = onChecklist,
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color(0xFF7C3AED).copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = "Checklist",
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // T-Hisab
                    IconButton(
                        onClick = onHisab,
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color(0xFF059669).copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "T-Hisab",
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Monthly Report
                    IconButton(
                        onClick = onReport,
                        modifier = Modifier
                            .size(30.dp)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = "Report",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DepartmentResultCard(
    dept: ClassEntity,
    staffCount: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = dept.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "$staffCount staff members",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            ) {
                Text(
                    text = if (isSelected) "Active" else "Switch",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun HisabResultCard(
    entry: HisabLedgerEntryEntity,
    studentName: String,
    onOpen: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = studentName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                val isAdvance = entry.entryType.equals("ADVANCE", ignoreCase = true)
                Text(
                    text = "${entry.entryType}: ${if (entry.notes.isBlank()) (if (isAdvance) "Cash Advance" else "${entry.totalPieces} pieces") else entry.notes}",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                )
            }
            val isAdvance = entry.entryType.equals("ADVANCE", ignoreCase = true)
            val amount = if (isAdvance) entry.advancePaid else entry.totalEarned
            Text(
                text = "₹${String.format(Locale.getDefault(), "%.2f", amount)}",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isAdvance) Color(0xFFDC2626) else Color(0xFF059669)
                )
            )
        }
    }
}

@Composable
private fun TaskResultCard(
    task: ChecklistItemEntity,
    onOpen: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (task.isCompleted) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Assigned: ${if (task.assignedStaffName.isBlank()) "All" else task.assignedStaffName} • Due: ${if (task.timeDue.isBlank()) "Standard" else task.timeDue}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GuideResultCard(
    guide: AppGuideItem,
    onAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = guide.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = guide.category,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = guide.description,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Steps
            guide.steps.forEachIndexed { i, step ->
                Text(
                    text = "${i + 1}. $step",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                )
            }

            if (guide.actionLabel != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onAction,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(text = guide.actionLabel, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center
        )
    }
}

// Built-in intelligent rule engine answer generator
private fun buildSmartFallbackAnswer(
    query: String,
    allStudents: List<StudentEntity>,
    allClasses: List<ClassEntity>,
    allChecklistItems: List<ChecklistItemEntity>,
    allHisabLedgerEntries: List<HisabLedgerEntryEntity>,
    todayRecords: List<AttendanceRecordEntity>,
    todayPresent: Int,
    todayAbsent: Int,
    todayHalfDay: Int,
    todayLeave: Int
): String {
    val q = query.lowercase(Locale.getDefault())

    return when {
        q.contains("absent") || q.contains("who is absent") -> {
            val absentIds = todayRecords.filter { it.status.equals("ABSENT", ignoreCase = true) || it.status.equals("Absent", ignoreCase = true) }.map { it.studentId }
            val absentStudents = allStudents.filter { it.id in absentIds }
            if (absentStudents.isEmpty()) {
                "✅ Great news! No staff members are marked Absent today. (Present: $todayPresent, Half-Day: $todayHalfDay, Leave: $todayLeave)."
            } else {
                "⚠️ Absent Staff Members Today (${absentStudents.size}):\n" +
                        absentStudents.joinToString("\n") { "• ${it.name} (${allClasses.firstOrNull { c -> c.id == it.classId }?.name ?: "Staff"})" } +
                        "\n\nTotal Present today: $todayPresent / ${allStudents.size}"
            }
        }
        q.contains("attendance") || q.contains("today") || q.contains("summary") -> {
            """
                📊 Today's Attendance Overview:
                • Full Present: $todayPresent
                • Absent: $todayAbsent
                • Half-Day: $todayHalfDay
                • Leave: $todayLeave
                • Total Registered Staff: ${allStudents.size} across ${allClasses.size} departments.
            """.trimIndent()
        }
        q.contains("hisab") || q.contains("advance") || q.contains("salary") || q.contains("piece") -> {
            val totalAdvances = allHisabLedgerEntries.filter { it.entryType.equals("ADVANCE", ignoreCase = true) }.sumOf { it.advancePaid }
            val totalPieceWork = allHisabLedgerEntries.filter { it.entryType.equals("SALES", ignoreCase = true) || it.entryType.equals("PIECE_WORK", ignoreCase = true) }.sumOf { it.totalEarned }
            """
                💰 T-HISAB Financial Ledger Summary:
                • Total Advance Cash Paid: ₹${String.format(Locale.getDefault(), "%.2f", totalAdvances)}
                • Total Piece Work Recorded: ₹${String.format(Locale.getDefault(), "%.2f", totalPieceWork)}
                • Total Ledger Entries: ${allHisabLedgerEntries.size}
                
                👉 Go to T-HISAB tab to view individual worker passbooks, add advances, record piece counts, and share statements.
            """.trimIndent()
        }
        q.contains("task") || q.contains("checklist") || q.contains("pending") -> {
            val pendingTasks = allChecklistItems.filter { !it.isCompleted }
            val completedTasks = allChecklistItems.count { it.isCompleted }
            if (pendingTasks.isEmpty()) {
                "🎉 All checklist tasks are completed! ($completedTasks total done)."
            } else {
                "📋 Pending Tasks (${pendingTasks.size}):\n" +
                        pendingTasks.take(5).joinToString("\n") { "• ${it.title} (Assigned: ${if (it.assignedStaffName.isBlank()) "All" else it.assignedStaffName}, Due: ${if (it.timeDue.isBlank()) "Standard" else it.timeDue})" } +
                        (if (pendingTasks.size > 5) "\n...and ${pendingTasks.size - 5} more." else "")
            }
        }
        q.contains("backup") || q.contains("export") || q.contains("restore") -> {
            """
                💾 Backup & Cloud Sync Guide:
                1. Local Backup: Tap 'Backup' on top header to export a full .json database backup file.
                2. Firebase Cloud: Tap 'Firebase' button on top for real-time multi-device cloud synchronization.
                3. Google Sheets: Tap 'Sheets' button on top to export live registers directly into Google Sheets.
                4. Restore: Open Backup dialog and select 'Import & Restore' to load previous records safely.
            """.trimIndent()
        }
        q.contains("shift") || q.contains("time") || q.contains("reminder") -> {
            """
                ⏱️ Shifts & Clock Dial Guide:
                • Create and assign work shifts (e.g. 9:00 AM - 6:00 PM) in the 'Staff & Shifts' tab.
                • Set interactive Clock-Dial reminders in the 'Checklist' tab to receive notifications on employee task deadlines.
            """.trimIndent()
        }
        else -> {
            """
                🤖 AI Lens Assistant:
                I found your query regarding '$query'.
                
                App Quick Overview:
                • Total Staff: ${allStudents.size} registered
                • Departments: ${allClasses.joinToString(", ") { it.name }}
                • Today: $todayPresent Present, $todayAbsent Absent, $todayHalfDay Half-Day
                • T-Hisab Ledger: ${allHisabLedgerEntries.size} records
                • Checklist: ${allChecklistItems.count { !it.isCompleted }} pending tasks
                
                You can tap any staff member or guide below to take immediate action!
            """.trimIndent()
        }
    }
}
