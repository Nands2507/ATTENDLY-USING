package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AttendanceDatabase
import com.example.data.AttendanceRecordEntity
import com.example.data.AttendanceRepository
import com.example.data.ClassEntity
import com.example.data.StudentEntity
import com.example.data.ShiftEntity
import com.example.data.HisabProductEntity
import com.example.data.HisabLedgerEntryEntity
import com.example.data.ChecklistItemEntity
import com.example.data.StaffFeedbackEntity
import com.example.data.cleanEmployeeName
import com.example.data.displayName
import com.example.data.isChecklist
import com.example.data.isDoneToday
import com.example.data.isSameCalendarDay
import com.example.data.SubtaskItem
import com.example.data.getSubtasks
import com.example.data.toJson
import com.example.data.createSubtasksJson
import com.example.data.todayComment
import com.example.data.DailyCommentHistoryItem
import com.example.data.getDailyCommentHistory
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ClassStats(
    val totalStudents: Int,
    val totalSessions: Int,
    val averageAttendanceRate: Double,
    val presentCount: Int,
    val absentCount: Int,
    val halfDayMorCount: Int = 0,
    val halfDayEveCount: Int = 0,
    val halfDayCount: Int = 0,
    val lowAttendanceStudents: List<Pair<StudentEntity, Double>> // Students with < 75% attendance
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AttendanceViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AttendanceDatabase.getDatabase(application)
    private val repository = AttendanceRepository(
        database.classDao(),
        database.studentDao(),
        database.attendanceRecordDao(),
        database.shiftDao(),
        database.hisabProductDao(),
        database.hisabLedgerEntryDao(),
        database.checklistItemDao(),
        database.staffFeedbackDao()
    )

    private val sharedPrefs = application.getSharedPreferences("google_sheets_pref", Context.MODE_PRIVATE)
    private val backupSyncMutex = Mutex()

    private val _customCategories = MutableStateFlow(
        sharedPrefs.getStringSet("hisab_custom_categories", null)?.toSet() ?: setOf("SAREE", "MEN", "WOMEN", "INNERWEAR", "GENERAL")
    )
    val customCategories: StateFlow<Set<String>> = _customCategories.asStateFlow()

    private val _deletedCategories = MutableStateFlow(
        sharedPrefs.getStringSet("hisab_deleted_categories", null)?.toSet() ?: emptySet()
    )
    val deletedCategories: StateFlow<Set<String>> = _deletedCategories.asStateFlow()

    private val _googleSheetId = MutableStateFlow(sharedPrefs.getString("google_sheet_id", "") ?: "")
    val googleSheetId: StateFlow<String> = _googleSheetId.asStateFlow()

    private val _googleWebAppUrl = MutableStateFlow(sharedPrefs.getString("google_webapp_url", "") ?: "")
    val googleWebAppUrl: StateFlow<String> = _googleWebAppUrl.asStateFlow()

    private val _googleAccessToken = MutableStateFlow(sharedPrefs.getString("google_access_token", "") ?: "")
    val googleAccessToken: StateFlow<String> = _googleAccessToken.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncResult = MutableStateFlow<String?>(null)
    val syncResult: StateFlow<String?> = _syncResult.asStateFlow()

    private val _lastSyncTime = MutableStateFlow(sharedPrefs.getLong("last_sync_time", 0L))
    val lastSyncTime: StateFlow<Long> = _lastSyncTime.asStateFlow()

    private val _backupStatus = MutableStateFlow<String?>(null)
    val backupStatus: StateFlow<String?> = _backupStatus.asStateFlow()

    // Firebase Cloud Sync Configuration & State
    private val _firebaseUrl = MutableStateFlow(sharedPrefs.getString("firebase_url", "https://gen-lang-client-0830360424-default-rtdb.firebaseio.com") ?: "https://gen-lang-client-0830360424-default-rtdb.firebaseio.com")
    val firebaseUrl: StateFlow<String> = _firebaseUrl.asStateFlow()

    private val _firebaseWorkspace = MutableStateFlow(sharedPrefs.getString("firebase_workspace", "shop_attendance") ?: "shop_attendance")
    val firebaseWorkspace: StateFlow<String> = _firebaseWorkspace.asStateFlow()

    private val _firebaseAuthToken = MutableStateFlow(sharedPrefs.getString("firebase_auth_token", "") ?: "")
    val firebaseAuthToken: StateFlow<String> = _firebaseAuthToken.asStateFlow()

    private val _firebaseAutoSyncEnabled = MutableStateFlow(sharedPrefs.getBoolean("firebase_auto_sync_enabled", true))
    val firebaseAutoSyncEnabled: StateFlow<Boolean> = _firebaseAutoSyncEnabled.asStateFlow()

    private val _isFirebaseSyncing = MutableStateFlow(false)
    val isFirebaseSyncing: StateFlow<Boolean> = _isFirebaseSyncing.asStateFlow()

    private val _firebaseSyncStatus = MutableStateFlow<String?>(null)
    val firebaseSyncStatus: StateFlow<String?> = _firebaseSyncStatus.asStateFlow()

    private val _firebaseLastSyncTime = MutableStateFlow(sharedPrefs.getLong("firebase_last_sync_time", 0L))
    val firebaseLastSyncTime: StateFlow<Long> = _firebaseLastSyncTime.asStateFlow()

    private val _portalBaseUrl = MutableStateFlow(sharedPrefs.getString("github_pages_portal_url", "") ?: "")
    val portalBaseUrl: StateFlow<String> = _portalBaseUrl.asStateFlow()

    val allClasses: StateFlow<List<ClassEntity>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allShifts: StateFlow<List<ShiftEntity>> = repository.allShifts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudents: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHisabProducts: StateFlow<List<HisabProductEntity>> = repository.allHisabProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHisabLedgerEntries: StateFlow<List<HisabLedgerEntryEntity>> = repository.allHisabLedgerEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allChecklistItems: StateFlow<List<ChecklistItemEntity>> = repository.allChecklistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStaffFeedback: StateFlow<List<StaffFeedbackEntity>> = repository.allStaffFeedback
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedHisabStudent = MutableStateFlow<StudentEntity?>(null)
    val selectedHisabStudent: StateFlow<StudentEntity?> = _selectedHisabStudent.asStateFlow()

    val hisabEntriesForSelectedStudent: StateFlow<List<HisabLedgerEntryEntity>> = _selectedHisabStudent
        .flatMapLatest { student ->
            if (student == null) flowOf(emptyList())
            else repository.getHisabEntriesByStudent(student.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        initDefaultHisabProductsIfNeeded()
        initDefaultChecklistIfNeeded()
        cleanAllEmployeeNamesIfNeeded()
        resetExpiredDailyChecklists()
    }

    fun resetExpiredDailyChecklists() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allItems = repository.getAllChecklistItemsList()
                val now = System.currentTimeMillis()
                for (item in allItems) {
                    if (item.isChecklist && item.isCompleted && !isSameCalendarDay(item.completedAt, now)) {
                        repository.updateChecklistCompletion(item.id, false, 0L)
                    }
                }
            } catch (_: Exception) { }
        }
    }

    private fun cleanAllEmployeeNamesIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allStudents = repository.getAllStudentsList()
                var updated = 0
                for (student in allStudents) {
                    val cleanedName = student.name.cleanEmployeeName()
                    val cleanedRoll = student.rollNumber.trim()
                    if (cleanedName != student.name || cleanedRoll != student.rollNumber) {
                        repository.updateStudent(student.copy(name = cleanedName, rollNumber = cleanedRoll))
                        updated++
                    }
                }
                val allShifts = repository.getAllShiftsList()
                for (shift in allShifts) {
                    val cleanedShiftName = shift.name.cleanEmployeeName()
                    val cleanedTimings = shift.timings.cleanEmployeeName()
                    if (cleanedShiftName != shift.name || cleanedTimings != shift.timings) {
                        repository.updateShift(shift.copy(name = cleanedShiftName, timings = cleanedTimings))
                        updated++
                    }
                }
                val allClasses = repository.getAllClassesList()
                for (cls in allClasses) {
                    val cleanedClassName = cls.name.cleanEmployeeName()
                    if (cleanedClassName != cls.name) {
                        repository.updateClass(cls.copy(name = cleanedClassName))
                        updated++
                    }
                }
                if (updated > 0) {
                    triggerAutoBackup()
                }
            } catch (e: Exception) {
                // Ignore initialization failures
            }
        }
    }

    private fun initDefaultChecklistIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existing = repository.getAllChecklistItemsList()
                if (existing.none { it.studentId == 0L }) {
                    val defaults = listOf(
                        ChecklistItemEntity(studentId = 0L, title = "Open cash register & verify opening float", category = "OPENING", displayOrder = 1, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Count cash drawer", "Check float change coins", "Verify UPI QR stand")),
                        ChecklistItemEntity(studentId = 0L, title = "Turn on showroom lights, AC & sign board", category = "OPENING", displayOrder = 2, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Main showroom lights", "Mannequin spotlight", "AC set to 24°C", "Signboard ON")),
                        ChecklistItemEntity(studentId = 0L, title = "Mark morning staff attendance & check shifts", category = "DAILY", displayOrder = 3, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Check staff arrivals", "Confirm shift schedules")),
                        ChecklistItemEntity(studentId = 0L, title = "Clean billing counter & customer lounge area", category = "OPENING", displayOrder = 4, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Wipe counter glass", "Empty waste bin", "Sanitize POS terminal")),
                        ChecklistItemEntity(studentId = 0L, title = "Inspect saree racks, mannequin display & stock", category = "STOCK", displayOrder = 5, itemType = "TASK", subtasksJson = createSubtasksJson("Check bridal rack display", "Organize Silk section", "Restock bestsellers")),
                        ChecklistItemEntity(studentId = 0L, title = "Record afternoon saree sales / piece-rates in T-HISAB", category = "DAILY", displayOrder = 6, itemType = "TASK", subtasksJson = createSubtasksJson("Tally sales slips", "Enter pieces in T-Hisab")),
                        ChecklistItemEntity(studentId = 0L, title = "Count evening cash, check UPI QR & tally register", category = "CLOSING", displayOrder = 7, itemType = "TASK", subtasksJson = createSubtasksJson("Tally cash in hand", "Match UPI transaction log", "Close day register")),
                        ChecklistItemEntity(studentId = 0L, title = "Switch off AC, lights & lock showroom shutters securely", category = "CLOSING", displayOrder = 8, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Turn off AC units", "Switch off lights", "Lock main shutters", "Check CCTV active"))
                    )
                    repository.insertChecklistItems(defaults)
                }

                // Ensure all existing employees have their default checklist & tasks populated if currently empty
                val allStudents = repository.getAllStudentsList()
                val currentItems = repository.getAllChecklistItemsList()
                for (student in allStudents) {
                    val hasItems = currentItems.any {
                        it.studentId == student.id || (it.assignedStaffName.isNotBlank() && it.assignedStaffName.equals(student.displayName, ignoreCase = true))
                    }
                    if (!hasItems) {
                        val nameTag = student.displayName.ifBlank { "Employee" }
                        val empDefaults = listOf(
                            // 1. CHECKLIST (Routine operational checklist)
                            ChecklistItemEntity(studentId = student.id, assignedStaffName = nameTag, title = "Morning punch-in & check assigned section", category = "OPENING", displayOrder = 1, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Punch in on app", "Inspect section sarees")),
                            ChecklistItemEntity(studentId = student.id, assignedStaffName = nameTag, title = "Dust and arrange sarees on assigned rack shelves", category = "DAILY", displayOrder = 2, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Dust glass shelves", "Align saree hangers", "Refold loose sarees")),
                            ChecklistItemEntity(studentId = student.id, assignedStaffName = nameTag, title = "Fold all sampled sarees & cover racks before checkout", category = "CLOSING", displayOrder = 3, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Fold sampled sarees", "Cover rack dust covers")),

                            // 2. TASKS (Specific assigned sales & customer duties)
                            ChecklistItemEntity(studentId = student.id, assignedStaffName = nameTag, title = "Assist customers with showcase, folding & fitting trials", category = "DAILY", displayOrder = 4, itemType = "TASK", subtasksJson = createSubtasksJson("Greet customer", "Show requested fabric/color", "Fold trial sarees")),
                            ChecklistItemEntity(studentId = student.id, assignedStaffName = nameTag, title = "Submit daily piece-rate sales count for T-Hisab", category = "DAILY", displayOrder = 5, itemType = "TASK", subtasksJson = createSubtasksJson("Count pieces sold", "Verify rate per piece", "Submit to admin"))
                        )
                        repository.insertChecklistItems(empDefaults)
                    }
                }
            } catch (e: Exception) {
                // Ignore initialization failures
            }
        }
    }

    fun restoreDefaultChecklist(studentId: Long = 0L, studentName: String = "") {
        viewModelScope.launch {
            if (studentId == 0L) {
                val defaults = listOf(
                    ChecklistItemEntity(studentId = 0L, title = "Open cash register & verify opening float", category = "OPENING", displayOrder = 1, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Count cash drawer", "Check float change coins", "Verify UPI QR stand")),
                    ChecklistItemEntity(studentId = 0L, title = "Turn on showroom lights, AC & sign board", category = "OPENING", displayOrder = 2, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Main showroom lights", "Mannequin spotlight", "AC set to 24°C", "Signboard ON")),
                    ChecklistItemEntity(studentId = 0L, title = "Mark morning staff attendance & check shifts", category = "DAILY", displayOrder = 3, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Check staff arrivals", "Confirm shift schedules")),
                    ChecklistItemEntity(studentId = 0L, title = "Clean billing counter & customer lounge area", category = "OPENING", displayOrder = 4, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Wipe counter glass", "Empty waste bin", "Sanitize POS terminal")),
                    ChecklistItemEntity(studentId = 0L, title = "Inspect saree racks, mannequin display & stock", category = "STOCK", displayOrder = 5, itemType = "TASK", subtasksJson = createSubtasksJson("Check bridal rack display", "Organize Silk section", "Restock bestsellers")),
                    ChecklistItemEntity(studentId = 0L, title = "Record afternoon saree sales / piece-rates in T-HISAB", category = "DAILY", displayOrder = 6, itemType = "TASK", subtasksJson = createSubtasksJson("Tally sales slips", "Enter pieces in T-Hisab")),
                    ChecklistItemEntity(studentId = 0L, title = "Count evening cash, check UPI QR & tally register", category = "CLOSING", displayOrder = 7, itemType = "TASK", subtasksJson = createSubtasksJson("Tally cash in hand", "Match UPI transaction log", "Close day register")),
                    ChecklistItemEntity(studentId = 0L, title = "Switch off AC, lights & lock showroom shutters securely", category = "CLOSING", displayOrder = 8, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Turn off AC units", "Switch off lights", "Lock main shutters", "Check CCTV active"))
                )
                repository.insertChecklistItems(defaults)
            } else {
                val nameTag = if (studentName.isNotBlank()) studentName else "Employee"
                val defaults = listOf(
                    // 1. CHECKLIST (Routine operational checklist)
                    ChecklistItemEntity(studentId = studentId, assignedStaffName = nameTag, title = "Morning punch-in & check assigned section", category = "OPENING", displayOrder = 1, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Punch in on app", "Inspect section sarees")),
                    ChecklistItemEntity(studentId = studentId, assignedStaffName = nameTag, title = "Dust and arrange sarees on assigned rack shelves", category = "DAILY", displayOrder = 2, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Dust glass shelves", "Align saree hangers", "Refold loose sarees")),
                    ChecklistItemEntity(studentId = studentId, assignedStaffName = nameTag, title = "Fold all sampled sarees & cover racks before checkout", category = "CLOSING", displayOrder = 3, itemType = "CHECKLIST", subtasksJson = createSubtasksJson("Fold sampled sarees", "Cover rack dust covers")),

                    // 2. TASKS (Specific assigned sales & customer duties)
                    ChecklistItemEntity(studentId = studentId, assignedStaffName = nameTag, title = "Assist customers with showcase, folding & fitting trials", category = "DAILY", displayOrder = 4, itemType = "TASK", subtasksJson = createSubtasksJson("Greet customer", "Show requested fabric/color", "Fold trial sarees")),
                    ChecklistItemEntity(studentId = studentId, assignedStaffName = nameTag, title = "Submit daily piece-rate sales count for T-Hisab", category = "DAILY", displayOrder = 5, itemType = "TASK", subtasksJson = createSubtasksJson("Count pieces sold", "Verify rate per piece", "Submit to admin"))
                )
                repository.insertChecklistItems(defaults)
            }
        }
    }

    fun addChecklistItem(
        studentId: Long = 0L,
        title: String,
        category: String = "DAILY",
        priority: String = "",
        timeDue: String = "",
        notes: String = "",
        assignedStaffName: String = "",
        reminderTimestamp: Long = 0L,
        itemType: String = "CHECKLIST",
        subtasksJson: String = ""
    ) {
        viewModelScope.launch {
            val currentList = repository.getAllChecklistItemsList()
            val newOrder = (currentList.filter { it.studentId == studentId }.maxOfOrNull { it.displayOrder } ?: 0) + 1
            val cleanCat = category.trim().let { cat ->
                val upper = cat.uppercase()
                if (upper in listOf("OPENING", "DAILY", "STOCK", "SALES", "CLOSING", "URGENT", "GENERAL", "TASK", "CHECKLIST")) {
                    upper
                } else {
                    cat
                }
            }
            repository.insertChecklistItem(
                ChecklistItemEntity(
                    studentId = studentId,
                    title = title.trim(),
                    category = cleanCat,
                    priority = "",
                    timeDue = "",
                    notes = notes.trim(),
                    assignedStaffName = assignedStaffName.trim(),
                    displayOrder = newOrder,
                    reminderTimestamp = reminderTimestamp,
                    isCompleted = false,
                    completedAt = 0L,
                    itemType = itemType.trim().uppercase(),
                    subtasksJson = subtasksJson.trim()
                )
            )
            runAutoBackupAndSync()
        }
    }

    fun toggleChecklistItem(item: ChecklistItemEntity) {
        viewModelScope.launch {
            val currentlyDone = item.isDoneToday
            val newCompleted = !currentlyDone
            val completedTime = if (newCompleted) System.currentTimeMillis() else 0L
            // If parent is toggled, also toggle all subtasks to match
            val currentSubtasks = item.getSubtasks()
            val updatedSubtasksJson = if (currentSubtasks.isNotEmpty()) {
                currentSubtasks.map { it.copy(isCompleted = newCompleted) }.toJson()
            } else item.subtasksJson

            val updated = item.copy(
                isCompleted = newCompleted,
                completedAt = completedTime,
                subtasksJson = updatedSubtasksJson
            )
            repository.updateChecklistItem(updated)
            triggerAutoBackup()
        }
    }

    fun toggleSubtask(parentItem: ChecklistItemEntity, subtaskId: String) {
        viewModelScope.launch {
            val currentSubtasks = parentItem.getSubtasks().toMutableList()
            val idx = currentSubtasks.indexOfFirst { it.id == subtaskId }
            if (idx != -1) {
                val oldSub = currentSubtasks[idx]
                val newSub = oldSub.copy(isCompleted = !oldSub.isCompleted)
                currentSubtasks[idx] = newSub
                val updatedJson = currentSubtasks.toJson()
                val allDone = currentSubtasks.isNotEmpty() && currentSubtasks.all { it.isCompleted }
                val newCompleted = if (allDone) true else if (!newSub.isCompleted && parentItem.isCompleted) false else parentItem.isCompleted
                val completedTime = if (newCompleted) {
                    if (parentItem.completedAt > 0L) parentItem.completedAt else System.currentTimeMillis()
                } else 0L
                val updatedItem = parentItem.copy(
                    subtasksJson = updatedJson,
                    isCompleted = newCompleted,
                    completedAt = completedTime
                )
                repository.updateChecklistItem(updatedItem)
                triggerAutoBackup()
            }
        }
    }

    fun addSubtaskToItem(parentItem: ChecklistItemEntity, subtaskTitle: String) {
        if (subtaskTitle.isBlank()) return
        viewModelScope.launch {
            val currentSubtasks = parentItem.getSubtasks().toMutableList()
            currentSubtasks.add(SubtaskItem(title = subtaskTitle.trim(), isCompleted = false))
            val updatedItem = parentItem.copy(
                subtasksJson = currentSubtasks.toJson(),
                isCompleted = false
            )
            repository.updateChecklistItem(updatedItem)
            triggerAutoBackup()
        }
    }

    fun deleteSubtaskFromItem(parentItem: ChecklistItemEntity, subtaskId: String) {
        viewModelScope.launch {
            val currentSubtasks = parentItem.getSubtasks().filter { it.id != subtaskId }
            val allDone = currentSubtasks.isNotEmpty() && currentSubtasks.all { it.isCompleted }
            val updatedItem = parentItem.copy(
                subtasksJson = currentSubtasks.toJson(),
                isCompleted = if (currentSubtasks.isEmpty()) parentItem.isCompleted else allDone
            )
            repository.updateChecklistItem(updatedItem)
            triggerAutoBackup()
        }
    }

    fun updateChecklistItem(item: ChecklistItemEntity) {
        viewModelScope.launch {
            repository.updateChecklistItem(item)
            runAutoBackupAndSync()
        }
    }

    fun updateDailyComment(item: ChecklistItemEntity, newComment: String) {
        viewModelScope.launch {
            val trimmed = newComment.trim()
            val now = System.currentTimeMillis()

            // If there was an existing comment from a previous calendar day, preserve it into history
            val history = item.getDailyCommentHistory().toMutableList()
            if (item.dailyComment.isNotBlank() && item.dailyCommentDate > 0L && !isSameCalendarDay(item.dailyCommentDate, now)) {
                val prevDateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(item.dailyCommentDate))
                if (history.none { it.timestamp == item.dailyCommentDate }) {
                    history.add(0, DailyCommentHistoryItem(dateString = prevDateStr, timestamp = item.dailyCommentDate, text = item.dailyComment))
                }
            }

            val historyJson = if (history.isNotEmpty()) {
                val arr = JSONArray()
                for (h in history.take(30)) {
                    val obj = JSONObject()
                    obj.put("dateString", h.dateString)
                    obj.put("timestamp", h.timestamp)
                    obj.put("text", h.text)
                    arr.put(obj)
                }
                arr.toString()
            } else item.dailyCommentHistoryJson

            val updatedItem = item.copy(
                dailyComment = trimmed,
                dailyCommentDate = if (trimmed.isNotBlank()) now else 0L,
                dailyCommentHistoryJson = historyJson
            )
            repository.updateChecklistItem(updatedItem)
            triggerAutoBackup()
        }
    }

    fun moveChecklistItemUp(item: ChecklistItemEntity) {
        viewModelScope.launch {
            val allItems = repository.getAllChecklistItemsList()
            val scopeItems = allItems.filter { it.studentId == item.studentId }
                .sortedWith(compareBy({ it.displayOrder }, { it.id }))
                .toMutableList()
            val index = scopeItems.indexOfFirst { it.id == item.id }
            if (index > 0) {
                val moved = scopeItems.removeAt(index)
                scopeItems.add(index - 1, moved)
                val updated = scopeItems.mapIndexed { idx, task -> task.copy(displayOrder = idx + 1) }
                repository.updateChecklistItems(updated)
                triggerAutoBackup()
            }
        }
    }

    fun moveChecklistItemDown(item: ChecklistItemEntity) {
        viewModelScope.launch {
            val allItems = repository.getAllChecklistItemsList()
            val scopeItems = allItems.filter { it.studentId == item.studentId }
                .sortedWith(compareBy({ it.displayOrder }, { it.id }))
                .toMutableList()
            val index = scopeItems.indexOfFirst { it.id == item.id }
            if (index >= 0 && index < scopeItems.size - 1) {
                val moved = scopeItems.removeAt(index)
                scopeItems.add(index + 1, moved)
                val updated = scopeItems.mapIndexed { idx, task -> task.copy(displayOrder = idx + 1) }
                repository.updateChecklistItems(updated)
                triggerAutoBackup()
            }
        }
    }

    fun swapChecklistItems(item1: ChecklistItemEntity, item2: ChecklistItemEntity) {
        viewModelScope.launch {
            val allItems = repository.getAllChecklistItemsList()
            val scopeItems = allItems.filter { it.studentId == item1.studentId }
                .sortedWith(compareBy({ it.displayOrder }, { it.id }))
                .toMutableList()
            val idx1 = scopeItems.indexOfFirst { it.id == item1.id }
            val idx2 = scopeItems.indexOfFirst { it.id == item2.id }
            if (idx1 != -1 && idx2 != -1) {
                val temp = scopeItems[idx1]
                scopeItems[idx1] = scopeItems[idx2]
                scopeItems[idx2] = temp
                val updated = scopeItems.mapIndexed { idx, task -> task.copy(displayOrder = idx + 1) }
                repository.updateChecklistItems(updated)
                triggerAutoBackup()
            }
        }
    }

    fun moveChecklistItemToPosition(item: ChecklistItemEntity, targetIndex: Int) {
        viewModelScope.launch {
            val allItems = repository.getAllChecklistItemsList()
            val scopeItems = allItems.filter { it.studentId == item.studentId }
                .sortedWith(compareBy({ it.displayOrder }, { it.id }))
                .toMutableList()
            val currentIndex = scopeItems.indexOfFirst { it.id == item.id }
            if (currentIndex != -1 && targetIndex in 0 until scopeItems.size) {
                val moved = scopeItems.removeAt(currentIndex)
                scopeItems.add(targetIndex, moved)
                val updated = scopeItems.mapIndexed { idx, task -> task.copy(displayOrder = idx + 1) }
                repository.updateChecklistItems(updated)
                triggerAutoBackup()
            }
        }
    }

    fun reorderChecklistItems(items: List<ChecklistItemEntity>) {
        viewModelScope.launch {
            val updated = items.mapIndexed { idx, task ->
                task.copy(displayOrder = idx + 1)
            }
            repository.updateChecklistItems(updated)
            triggerAutoBackup()
        }
    }

    /**
     * Reorders a specific subset of checklist/task items (for example, one employee's
     * Daily Checklist items or Tagged Tasks) to match [orderedSubset] while preserving
     * the relative order of all other items belonging to that employee/scope.
     */
    fun reorderChecklistSubset(orderedSubset: List<ChecklistItemEntity>) {
        if (orderedSubset.size <= 1) return
        viewModelScope.launch {
            val allItems = repository.getAllChecklistItemsList()
            val subsetIds = orderedSubset.map { it.id }.toSet()
            val distinctStudentIds = orderedSubset.map { it.studentId }.distinct()

            if (distinctStudentIds.size == 1) {
                val scopeStudentId = distinctStudentIds.first()
                val scopeSorted = allItems
                    .filter { it.studentId == scopeStudentId }
                    .sortedWith(compareBy<ChecklistItemEntity> { it.displayOrder }.thenBy { it.id })

                // Normalize 1..N across the scope so every item has a unique slot
                val normalizedSlots = scopeSorted.mapIndexed { idx, entity -> entity.id to (idx + 1) }.toMap()
                val targetSlots = scopeSorted
                    .filter { it.id in subsetIds }
                    .mapNotNull { normalizedSlots[it.id] }
                    .sorted()

                val updatedById = mutableMapOf<Long, ChecklistItemEntity>()
                scopeSorted.forEachIndexed { idx, entity ->
                    val normOrder = idx + 1
                    if (entity.displayOrder != normOrder) {
                        updatedById[entity.id] = entity.copy(displayOrder = normOrder)
                    }
                }

                orderedSubset.forEachIndexed { idx, item ->
                    val newSlot = targetSlots.getOrNull(idx) ?: (idx + 1)
                    val latest = allItems.find { it.id == item.id } ?: item
                    updatedById[item.id] = latest.copy(displayOrder = newSlot)
                }

                if (updatedById.isNotEmpty()) {
                    repository.updateChecklistItems(updatedById.values.toList())
                    triggerAutoBackup()
                }
            } else {
                // Multi-scope fallback (e.g., employee profile showing items assigned by name + studentId)
                val allSorted = allItems.sortedWith(compareBy<ChecklistItemEntity> { it.displayOrder }.thenBy { it.id })
                val targetSlots = allSorted
                    .mapIndexedNotNull { idx, entity -> if (entity.id in subsetIds) idx + 1 else null }
                    .sorted()

                val updatedById = mutableMapOf<Long, ChecklistItemEntity>()
                allSorted.forEachIndexed { idx, entity ->
                    val normOrder = idx + 1
                    if (entity.displayOrder != normOrder) {
                        updatedById[entity.id] = entity.copy(displayOrder = normOrder)
                    }
                }

                orderedSubset.forEachIndexed { idx, item ->
                    val newSlot = targetSlots.getOrNull(idx) ?: (idx + 1)
                    val latest = allItems.find { it.id == item.id } ?: item
                    updatedById[item.id] = latest.copy(displayOrder = newSlot)
                }

                if (updatedById.isNotEmpty()) {
                    repository.updateChecklistItems(updatedById.values.toList())
                    triggerAutoBackup()
                }
            }
        }
    }

    fun setChecklistReminder(context: Context, item: ChecklistItemEntity, reminderTimestamp: Long) {
        viewModelScope.launch {
            repository.updateChecklistReminder(item.id, reminderTimestamp)
            if (reminderTimestamp > System.currentTimeMillis()) {
                com.example.reminder.TaskReminderHelper.scheduleReminder(
                    context = context,
                    taskId = item.id,
                    taskTitle = item.title,
                    staffName = item.assignedStaffName,
                    dueTime = item.timeDue,
                    notes = item.notes,
                    triggerAtMillis = reminderTimestamp
                )
            } else {
                com.example.reminder.TaskReminderHelper.cancelReminder(context, item.id)
            }
        }
    }

    fun cancelChecklistReminder(context: Context, item: ChecklistItemEntity) {
        viewModelScope.launch {
            repository.updateChecklistReminder(item.id, 0L)
            com.example.reminder.TaskReminderHelper.cancelReminder(context, item.id)
        }
    }

    fun deleteChecklistItem(item: ChecklistItemEntity, context: Context? = null) {
        viewModelScope.launch {
            if (context != null || item.reminderTimestamp > 0L) {
                context?.let { com.example.reminder.TaskReminderHelper.cancelReminder(it, item.id) }
            }
            repository.deleteChecklistItem(item)
            runAutoBackupAndSync()
        }
    }

    fun resetAllChecklist() {
        viewModelScope.launch {
            repository.resetAllChecklistCompletions()
            runAutoBackupAndSync()
        }
    }

    fun resetChecklistForScope(studentId: Long) {
        viewModelScope.launch {
            repository.resetChecklistForStudent(studentId)
            runAutoBackupAndSync()
        }
    }

    // =========================================================================
    // STAFF COMPLAINT & APPRECIATION CRUD
    // =========================================================================
    fun addStaffFeedback(
        studentId: Long,
        entryType: String,
        title: String,
        details: String,
        category: String,
        dateString: String
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val item = StaffFeedbackEntity(
                studentId = studentId,
                entryType = entryType,
                title = title.trim(),
                details = details.trim(),
                category = category.trim().ifBlank { "GENERAL" },
                dateString = dateString.ifBlank {
                    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                },
                isResolved = false,
                timestamp = System.currentTimeMillis()
            )
            repository.insertStaffFeedback(item)
            triggerAutoBackup()
        }
    }

    fun updateStaffFeedback(feedback: StaffFeedbackEntity) {
        viewModelScope.launch {
            repository.updateStaffFeedback(feedback)
            triggerAutoBackup()
        }
    }

    fun toggleFeedbackResolved(feedback: StaffFeedbackEntity) {
        viewModelScope.launch {
            repository.updateStaffFeedback(feedback.copy(isResolved = !feedback.isResolved))
            triggerAutoBackup()
        }
    }

    fun deleteStaffFeedback(feedback: StaffFeedbackEntity) {
        viewModelScope.launch {
            repository.deleteStaffFeedback(feedback)
            triggerAutoBackup()
        }
    }

    private fun initDefaultHisabProductsIfNeeded() {
        viewModelScope.launch {
            val existing = repository.getAllHisabProductsList()
            if (existing.isEmpty()) {
                val defaults = listOf(
                    HisabProductEntity(name = "SET-WALI SAREE", category = "SAREE", ratePerPiece = 100.0, displayOrder = 1),
                    HisabProductEntity(name = "T-WALI SAREE", category = "SAREE", ratePerPiece = 125.0, displayOrder = 2),
                    HisabProductEntity(name = "DHOTI", category = "MEN", ratePerPiece = 10.0, displayOrder = 3),
                    HisabProductEntity(name = "KURTA-PAJAMA", category = "MEN", ratePerPiece = 15.0, displayOrder = 4),
                    HisabProductEntity(name = "PENT-SHIRT", category = "MEN", ratePerPiece = 100.0, displayOrder = 5),
                    HisabProductEntity(name = "LANCHE", category = "WOMEN", ratePerPiece = 250.0, displayOrder = 6),
                    HisabProductEntity(name = "LEHENGE", category = "WOMEN", ratePerPiece = 350.0, displayOrder = 7),
                    HisabProductEntity(name = "BANIYAN", category = "INNERWEAR", ratePerPiece = 5.0, displayOrder = 8),
                    HisabProductEntity(name = "PIECES", category = "GENERAL", ratePerPiece = 8.0, displayOrder = 9)
                )
                repository.insertHisabProducts(defaults)
            }
        }
    }

    fun restoreDefaultHisabProducts() {
        viewModelScope.launch {
            val defaults = listOf(
                HisabProductEntity(name = "SET-WALI SAREE", category = "SAREE", ratePerPiece = 100.0, displayOrder = 1),
                HisabProductEntity(name = "T-WALI SAREE", category = "SAREE", ratePerPiece = 125.0, displayOrder = 2),
                HisabProductEntity(name = "DHOTI", category = "MEN", ratePerPiece = 10.0, displayOrder = 3),
                HisabProductEntity(name = "KURTA-PAJAMA", category = "MEN", ratePerPiece = 15.0, displayOrder = 4),
                HisabProductEntity(name = "PENT-SHIRT", category = "MEN", ratePerPiece = 100.0, displayOrder = 5),
                HisabProductEntity(name = "LANCHE", category = "WOMEN", ratePerPiece = 250.0, displayOrder = 6),
                HisabProductEntity(name = "LEHENGE", category = "WOMEN", ratePerPiece = 350.0, displayOrder = 7),
                HisabProductEntity(name = "BANIYAN", category = "INNERWEAR", ratePerPiece = 5.0, displayOrder = 8),
                HisabProductEntity(name = "PIECES", category = "GENERAL", ratePerPiece = 8.0, displayOrder = 9)
            )
            repository.insertHisabProducts(defaults)
            runAutoBackupAndSync()
        }
    }

    fun addHisabProduct(name: String, category: String, ratePerPiece: Double) {
        viewModelScope.launch {
            val currentList = repository.getAllHisabProductsList()
            val newOrder = (currentList.maxOfOrNull { it.displayOrder } ?: 0) + 1
            repository.insertHisabProduct(
                HisabProductEntity(
                    name = name.trim().uppercase(Locale.getDefault()),
                    category = category.trim().uppercase(Locale.getDefault()).ifBlank { "SAREE" },
                    ratePerPiece = ratePerPiece,
                    displayOrder = newOrder
                )
            )
            runAutoBackupAndSync()
        }
    }

    fun updateHisabProduct(product: HisabProductEntity) {
        viewModelScope.launch {
            repository.updateHisabProduct(product)
            runAutoBackupAndSync()
        }
    }

    fun deleteHisabProduct(product: HisabProductEntity) {
        viewModelScope.launch {
            repository.deleteHisabProduct(product)
            runAutoBackupAndSync()
        }
    }

    fun addCustomCategory(category: String) {
        val trimmed = category.trim().uppercase(Locale.getDefault())
        if (trimmed.isBlank()) return
        val currentCustom = _customCategories.value.toMutableSet().apply { add(trimmed) }
        val currentDeleted = _deletedCategories.value.toMutableSet().apply { remove(trimmed) }
        _customCategories.value = currentCustom
        _deletedCategories.value = currentDeleted
        sharedPrefs.edit()
            .putStringSet("hisab_custom_categories", currentCustom)
            .putStringSet("hisab_deleted_categories", currentDeleted)
            .apply()
    }

    fun renameCategory(oldCategory: String, newCategory: String) {
        val trimmedOld = oldCategory.trim().uppercase(Locale.getDefault())
        val trimmedNew = newCategory.trim().uppercase(Locale.getDefault())
        if (trimmedNew.isBlank() || trimmedNew == trimmedOld) return

        val currentCustom = _customCategories.value.toMutableSet().apply {
            remove(trimmedOld)
            add(trimmedNew)
        }
        val currentDeleted = _deletedCategories.value.toMutableSet().apply {
            remove(trimmedNew)
        }
        _customCategories.value = currentCustom
        _deletedCategories.value = currentDeleted
        sharedPrefs.edit()
            .putStringSet("hisab_custom_categories", currentCustom)
            .putStringSet("hisab_deleted_categories", currentDeleted)
            .apply()

        viewModelScope.launch {
            val list = repository.getAllHisabProductsList()
            val toUpdate = list.filter { it.category.equals(trimmedOld, ignoreCase = true) }
            for (item in toUpdate) {
                repository.updateHisabProduct(item.copy(category = trimmedNew))
            }
            if (toUpdate.isNotEmpty()) {
                runAutoBackupAndSync()
            }
        }
    }

    fun deleteCategory(categoryName: String, deleteItems: Boolean, fallbackCategory: String = "GENERAL") {
        val trimmed = categoryName.trim().uppercase(Locale.getDefault())
        val fallback = fallbackCategory.trim().uppercase(Locale.getDefault()).ifBlank { "GENERAL" }

        val currentCustom = _customCategories.value.toMutableSet().apply { remove(trimmed) }
        val currentDeleted = _deletedCategories.value.toMutableSet().apply { add(trimmed) }
        _customCategories.value = currentCustom
        _deletedCategories.value = currentDeleted
        sharedPrefs.edit()
            .putStringSet("hisab_custom_categories", currentCustom)
            .putStringSet("hisab_deleted_categories", currentDeleted)
            .apply()

        viewModelScope.launch {
            val list = repository.getAllHisabProductsList()
            val matching = list.filter { it.category.equals(trimmed, ignoreCase = true) }
            for (item in matching) {
                if (deleteItems) {
                    repository.deleteHisabProduct(item)
                } else {
                    repository.updateHisabProduct(item.copy(category = fallback))
                }
            }
            if (matching.isNotEmpty()) {
                runAutoBackupAndSync()
            }
        }
    }

    fun selectHisabStudent(student: StudentEntity?) {
        _selectedHisabStudent.value = student
    }

    private val _hisabStudentOrderIds = MutableStateFlow(
        sharedPrefs.getString("hisab_student_order_ids", "")
            ?.split(",")
            ?.mapNotNull { it.trim().toLongOrNull() }
            ?: emptyList()
    )

    val orderedHisabStudents: StateFlow<List<StudentEntity>> = combine(
        allStudents,
        _hisabStudentOrderIds
    ) { students, orderIds ->
        if (orderIds.isEmpty()) {
            students
        } else {
            val idToRank = orderIds.mapIndexed { index, id -> id to index }.toMap()
            students.sortedWith(
                compareBy<StudentEntity> { idToRank[it.id] ?: Int.MAX_VALUE }
                    .thenBy { it.rollNumber }
                    .thenBy { it.name }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun reorderHisabStudents(newOrder: List<StudentEntity>) {
        val ids = newOrder.map { it.id }
        _hisabStudentOrderIds.value = ids
        sharedPrefs.edit().putString("hisab_student_order_ids", ids.joinToString(",")).apply()
    }

    fun moveHisabStudentUp(student: StudentEntity) {
        val current = orderedHisabStudents.value.toMutableList()
        val idx = current.indexOfFirst { it.id == student.id }
        if (idx > 0) {
            val item = current.removeAt(idx)
            current.add(idx - 1, item)
            reorderHisabStudents(current)
        }
    }

    fun moveHisabStudentDown(student: StudentEntity) {
        val current = orderedHisabStudents.value.toMutableList()
        val idx = current.indexOfFirst { it.id == student.id }
        if (idx >= 0 && idx < current.size - 1) {
            val item = current.removeAt(idx)
            current.add(idx + 1, item)
            reorderHisabStudents(current)
        }
    }

    fun sortHisabStudents(mode: String) {
        val current = orderedHisabStudents.value
        val sorted = when (mode.uppercase(Locale.getDefault())) {
            "AZ" -> current.sortedBy { it.name.lowercase(Locale.getDefault()) }
            "ZA" -> current.sortedByDescending { it.name.lowercase(Locale.getDefault()) }
            "ROLL" -> current.sortedBy { it.rollNumber.lowercase(Locale.getDefault()) }
            "ORIGINAL" -> current.sortedBy { it.id }
            else -> current
        }
        reorderHisabStudents(sorted)
    }

    fun addSalesLedgerEntry(
        studentId: Long,
        dateString: String,
        breakdownJson: String,
        totalPieces: Int,
        totalEarned: Double,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.insertHisabEntry(
                HisabLedgerEntryEntity(
                    studentId = studentId,
                    dateString = dateString,
                    entryType = "SALES",
                    itemBreakdownJson = breakdownJson,
                    totalPieces = totalPieces,
                    totalEarned = totalEarned,
                    advancePaid = 0.0,
                    notes = notes.trim(),
                    timestamp = System.currentTimeMillis()
                )
            )
            runAutoBackupAndSync()
        }
    }

    fun addAdvanceLedgerEntry(
        studentId: Long,
        dateString: String,
        advanceAmount: Double,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.insertHisabEntry(
                HisabLedgerEntryEntity(
                    studentId = studentId,
                    dateString = dateString,
                    entryType = "ADVANCE",
                    itemBreakdownJson = "",
                    totalPieces = 0,
                    totalEarned = 0.0,
                    advancePaid = advanceAmount,
                    notes = notes.trim(),
                    timestamp = System.currentTimeMillis()
                )
            )
            runAutoBackupAndSync()
        }
    }

    fun updateHisabEntry(entry: HisabLedgerEntryEntity) {
        viewModelScope.launch {
            repository.updateHisabEntry(entry)
            runAutoBackupAndSync()
        }
    }

    fun deleteHisabEntry(entry: HisabLedgerEntryEntity) {
        viewModelScope.launch {
            repository.deleteHisabEntry(entry)
            runAutoBackupAndSync()
        }
    }

    private val _selectedClass = MutableStateFlow<ClassEntity?>(null)
    val selectedClass: StateFlow<ClassEntity?> = _selectedClass.asStateFlow()

    private val _selectedDate = MutableStateFlow(getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Students in the selected class
    val currentStudents: StateFlow<List<StudentEntity>> = _selectedClass
        .flatMapLatest { clazz ->
            if (clazz == null) flowOf(emptyList())
            else repository.getStudentsByClass(clazz.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Attendance records for the selected class and selected date
    val currentAttendanceRecords: StateFlow<List<AttendanceRecordEntity>> = combine(
        _selectedClass, _selectedDate
    ) { clazz, date ->
        clazz to date
    }.flatMapLatest { (clazz, date) ->
        if (clazz == null) flowOf(emptyList())
        else repository.getRecordsByDate(clazz.id, date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All attendance records for the selected class (for stats)
    val allRecordsForSelectedClass: StateFlow<List<AttendanceRecordEntity>> = _selectedClass
        .flatMapLatest { clazz ->
            if (clazz == null) flowOf(emptyList())
            else repository.getAllRecordsByClass(clazz.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Attendance rates for each student in the selected class
    val studentAttendanceRates: StateFlow<Map<Long, Double>> = combine(
        currentStudents, allRecordsForSelectedClass
    ) { students, records ->
        val rates = mutableMapOf<Long, Double>()
        students.forEach { student ->
            val studentRecords = records.filter { it.studentId == student.id }
            if (studentRecords.isEmpty()) {
                rates[student.id] = -1.0 // No records yet
            } else {
                val attended = studentRecords.count { 
                    it.status == "P" || it.status == "PRESENT" || 
                    it.status == "HD MOR" || it.status == "HD EVE" || 
                    it.status == "HALF-DAY" 
                }
                rates[student.id] = (attended.toDouble() / studentRecords.size.toDouble()) * 100.0
            }
        }
        rates
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Statistics summary for the selected class
    val classStats: StateFlow<ClassStats> = combine(
        currentStudents, allRecordsForSelectedClass
    ) { students, records ->
        if (students.isEmpty()) {
            return@combine ClassStats(0, 0, 0.0, 0, 0, 0, 0, 0, emptyList())
        }

        val totalStudents = students.size
        // Unique dates in records represent the total sessions held
        val totalSessions = records.map { it.dateString }.distinct().size

        val totalRecordsCount = records.size
        val presentCount = records.count { it.status == "P" || it.status == "PRESENT" }
        val absentCount = records.count { it.status == "A" || it.status == "ABSENT" }
        val hdMorCount = records.count { it.status == "HD MOR" }
        val hdEveCount = records.count { it.status == "HD EVE" }
        val halfDayCount = hdMorCount + hdEveCount + records.count { it.status == "HALF-DAY" }

        val averageAttendanceRate = if (totalRecordsCount == 0) 100.0 else {
            val attendedCount = presentCount + halfDayCount
            (attendedCount.toDouble() / totalRecordsCount.toDouble()) * 100.0
        }

        // Calculate rates per student to find those needing critical attention (< 75%)
        val lowAttendance = mutableListOf<Pair<StudentEntity, Double>>()
        students.forEach { student ->
            val studentRecords = records.filter { it.studentId == student.id }
            if (studentRecords.isNotEmpty()) {
                val attended = studentRecords.count { 
                    it.status == "P" || it.status == "PRESENT" || 
                    it.status == "HD MOR" || it.status == "HD EVE" || 
                    it.status == "HALF-DAY" 
                }
                val rate = (attended.toDouble() / studentRecords.size.toDouble()) * 100.0
                if (rate < 75.0) {
                    lowAttendance.add(student to rate)
                }
            }
        }

        ClassStats(
            totalStudents = totalStudents,
            totalSessions = totalSessions,
            averageAttendanceRate = averageAttendanceRate,
            presentCount = presentCount,
            absentCount = absentCount,
            halfDayMorCount = hdMorCount,
            halfDayEveCount = hdEveCount,
            halfDayCount = halfDayCount,
            lowAttendanceStudents = lowAttendance.sortedBy { it.second }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ClassStats(0, 0, 0.0, 0, 0, 0, 0, 0, emptyList()))

    private var isPrepopulating = false

    init {
        viewModelScope.launch {
            try {
                // Observe allClasses to handle empty state populate, auto-restore, and auto-selection
                repository.allClasses.collect { classes ->
                    try {
                        if (classes.isEmpty()) {
                            if (!isPrepopulating) {
                                isPrepopulating = true
                                var restored = false
                                try {
                                    val existingBackup = com.example.data.BackupRestoreHelper.findExistingBackup(getApplication())
                                    if (!existingBackup.isNullOrBlank()) {
                                        val restoreRes = com.example.data.BackupRestoreHelper.restoreFromBackupJson(existingBackup, repository)
                                        if (restoreRes.isSuccess) {
                                            _backupStatus.value = "Auto-restored your previous backup! " + restoreRes.getOrNull()
                                            restored = true
                                        }
                                    }
                                } catch (e: Exception) {
                                    restored = false
                                }
                                if (!restored) {
                                    prepopulateDatabase()
                                }
                                isPrepopulating = false
                            }
                        } else {
                            val current = _selectedClass.value
                            val savedClassId = sharedPrefs.getLong("last_selected_class_id", -1L)
                            if (current == null || classes.none { it.id == current.id }) {
                                val matched = classes.find { it.id == savedClassId }
                                _selectedClass.value = matched ?: classes.firstOrNull()
                            } else {
                                val updated = classes.find { it.id == current.id }
                                if (updated != null && updated != current) {
                                    _selectedClass.value = updated
                                }
                            }
                        }
                    } catch (e: Exception) {
                        isPrepopulating = false
                    }
                }
            } catch (e: Exception) {
                // Ignore flow collection failure
            }
        }
    }

    private suspend fun runAutoBackupAndSync() {
        backupSyncMutex.withLock {
            com.example.data.BackupRestoreHelper.autoSaveBackup(getApplication(), repository)

            // Automatic Cloud Sync via Firebase if configured & enabled
            if (_firebaseAutoSyncEnabled.value && _firebaseUrl.value.isNotBlank()) {
                val syncResult = com.example.sync.FirebaseSyncHelper.syncToFirebase(
                    firebaseUrl = _firebaseUrl.value,
                    workspaceKey = _firebaseWorkspace.value,
                    authToken = _firebaseAuthToken.value,
                    repository = repository
                )
                syncResult.onSuccess {
                    val now = System.currentTimeMillis()
                    _firebaseLastSyncTime.value = now
                    sharedPrefs.edit().putLong("firebase_last_sync_time", now).apply()
                    _firebaseSyncStatus.value = "Auto-synced to Firebase cloud at ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(now))}"
                }.onFailure { err ->
                    _firebaseSyncStatus.value = "Auto-sync notice: ${err.message}"
                }
            }
        }
    }

    private fun triggerAutoBackup() {
        viewModelScope.launch {
            runAutoBackupAndSync()
        }
    }

    fun clearBackupStatus() {
        _backupStatus.value = null
    }

    fun exportAndShareBackup(context: Context) {
        viewModelScope.launch {
            val json = com.example.data.BackupRestoreHelper.createBackupJson(repository)
            com.example.data.BackupRestoreHelper.autoSaveBackup(context, repository)
            com.example.data.BackupRestoreHelper.shareBackupFile(context, json)
            _backupStatus.value = "Backup created and saved to storage!"
        }
    }

    fun restoreBackupFromString(jsonString: String) {
        viewModelScope.launch {
            val result = com.example.data.BackupRestoreHelper.restoreFromBackupJson(jsonString, repository)
            if (result.isSuccess) {
                _backupStatus.value = result.getOrNull()
                com.example.data.BackupRestoreHelper.autoSaveBackup(getApplication(), repository)
            } else {
                _backupStatus.value = "Restore Error: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun restoreLatestAutoBackup(context: Context) {
        viewModelScope.launch {
            val existing = com.example.data.BackupRestoreHelper.findExistingBackup(context)
            if (!existing.isNullOrBlank()) {
                val result = com.example.data.BackupRestoreHelper.restoreFromBackupJson(existing, repository)
                if (result.isSuccess) {
                    _backupStatus.value = "Successfully restored! " + result.getOrNull()
                } else {
                    _backupStatus.value = "Restore Failed: ${result.exceptionOrNull()?.message}"
                }
            } else {
                _backupStatus.value = "No existing backup file found in device storage or downloads."
            }
        }
    }

    fun selectClass(classEntity: ClassEntity) {
        _selectedClass.value = classEntity
        sharedPrefs.edit().putLong("last_selected_class_id", classEntity.id).apply()
    }

    fun selectDate(dateString: String) {
        _selectedDate.value = dateString
    }

    fun previousDay() {
        navigateDate(-1)
    }

    fun nextDay() {
        navigateDate(1)
    }

    fun navigateDate(daysOffset: Int) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()
            val currentDate = sdf.parse(_selectedDate.value) ?: Date()
            cal.time = currentDate
            cal.add(Calendar.DAY_OF_YEAR, daysOffset)
            _selectedDate.value = sdf.format(cal.time)
        } catch (e: Exception) {
            // Fallback
        }
    }

    fun createClass(name: String, colorHex: String) {
        viewModelScope.launch {
            val existingList = repository.getAllClassesList()
            val nextOrder = if (existingList.isEmpty()) 0 else (existingList.maxOfOrNull { it.displayOrder } ?: 0) + 1
            val newClass = ClassEntity(name = name, colorHex = colorHex, displayOrder = nextOrder)
            val newId = repository.insertClass(newClass)
            val created = newClass.copy(id = newId)
            _selectedClass.value = created
            sharedPrefs.edit().putLong("last_selected_class_id", newId).apply()
            triggerAutoBackup()
        }
    }

    fun updateClass(classEntity: ClassEntity, newName: String, newColorHex: String) {
        viewModelScope.launch {
            val updatedClass = classEntity.copy(name = newName, colorHex = newColorHex)
            repository.updateClass(updatedClass)
            if (_selectedClass.value?.id == classEntity.id) {
                _selectedClass.value = updatedClass
            }
            triggerAutoBackup()
        }
    }

    fun reorderClasses(orderedList: List<ClassEntity>) {
        viewModelScope.launch {
            val updated = orderedList.mapIndexed { index, classEntity ->
                classEntity.copy(displayOrder = index)
            }
            repository.updateClasses(updated)
            triggerAutoBackup()
        }
    }

    fun moveClassUp(classEntity: ClassEntity) {
        viewModelScope.launch {
            val currentList = repository.getAllClassesList().toMutableList()
            val index = currentList.indexOfFirst { it.id == classEntity.id }
            if (index > 0) {
                val item = currentList.removeAt(index)
                currentList.add(index - 1, item)
                val updated = currentList.mapIndexed { idx, c -> c.copy(displayOrder = idx) }
                repository.updateClasses(updated)
                triggerAutoBackup()
            }
        }
    }

    fun moveClassDown(classEntity: ClassEntity) {
        viewModelScope.launch {
            val currentList = repository.getAllClassesList().toMutableList()
            val index = currentList.indexOfFirst { it.id == classEntity.id }
            if (index >= 0 && index < currentList.size - 1) {
                val item = currentList.removeAt(index)
                currentList.add(index + 1, item)
                val updated = currentList.mapIndexed { idx, c -> c.copy(displayOrder = idx) }
                repository.updateClasses(updated)
                triggerAutoBackup()
            }
        }
    }

    fun sortClasses(mode: String) {
        viewModelScope.launch {
            val currentList = repository.getAllClassesList()
            val sorted = when (mode.uppercase()) {
                "AZ" -> currentList.sortedBy { it.name.lowercase() }
                "ZA" -> currentList.sortedByDescending { it.name.lowercase() }
                "ID_ASC" -> currentList.sortedBy { it.id }
                "ID_DESC" -> currentList.sortedByDescending { it.id }
                else -> currentList
            }
            val updated = sorted.mapIndexed { idx, c -> c.copy(displayOrder = idx) }
            repository.updateClasses(updated)
            triggerAutoBackup()
        }
    }

    fun deleteClass(classEntity: ClassEntity) {
        viewModelScope.launch {
            repository.deleteClass(classEntity)
            _selectedClass.value = repository.allClasses.stateIn(this).value.firstOrNull()
            triggerAutoBackup()
        }
    }

    fun addStudent(
        name: String,
        rollNumber: String,
        phone: String = "",
        email: String = "",
        shiftId: Long? = null,
        targetClassId: Long? = null
     ) {
        viewModelScope.launch {
            val classesList = repository.getAllClassesList()
            val targetId = targetClassId ?: _selectedClass.value?.id ?: classesList.firstOrNull()?.id ?: return@launch
            val newStudent = StudentEntity(
                classId = targetId,
                name = name.cleanEmployeeName(),
                rollNumber = rollNumber.trim(),
                phone = phone.trim(),
                email = email.trim(),
                shiftId = shiftId
            )
            repository.insertStudent(newStudent)
            
            // Switch active department to this student's department if needed so user immediately sees them
            val targetClass = classesList.find { it.id == targetId }
            if (targetClass != null && _selectedClass.value?.id != targetId) {
                _selectedClass.value = targetClass
                sharedPrefs.edit().putLong("last_selected_class_id", targetId).apply()
            }
            triggerAutoBackup()
        }
    }

    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch {
            val cleaned = student.copy(
                name = student.name.cleanEmployeeName(),
                rollNumber = student.rollNumber.trim()
            )
            repository.updateStudent(cleaned)
            triggerAutoBackup()
        }
    }

    fun updateStudentDetails(studentId: Long, name: String, rollNumber: String, phone: String, email: String, shiftId: Long?, classId: Long? = null) {
        viewModelScope.launch {
            val existingList = repository.getAllStudentsList()
            val existing = existingList.find { it.id == studentId }
            if (existing != null) {
                val updated = existing.copy(
                    name = name.cleanEmployeeName(),
                    rollNumber = rollNumber.trim(),
                    phone = phone.trim(),
                    email = email.trim(),
                    shiftId = shiftId,
                    classId = classId ?: existing.classId
                )
                repository.updateStudent(updated)
                triggerAutoBackup()
            }
        }
    }

    fun deleteStudent(studentEntity: StudentEntity) {
        viewModelScope.launch {
            repository.deleteStudent(studentEntity)
            triggerAutoBackup()
        }
    }

    fun addShift(name: String, timings: String) {
        viewModelScope.launch {
            repository.insertShift(ShiftEntity(name = name, timings = timings))
            triggerAutoBackup()
        }
    }

    fun updateShift(shift: ShiftEntity) {
        viewModelScope.launch {
            repository.updateShift(shift)
            triggerAutoBackup()
        }
    }

    fun deleteShift(shift: ShiftEntity) {
        viewModelScope.launch {
            repository.deleteShift(shift)
            triggerAutoBackup()
        }
    }

    fun updateStudentShift(student: StudentEntity, shiftId: Long?) {
        viewModelScope.launch {
            repository.updateStudent(student.copy(shiftId = shiftId))
            triggerAutoBackup()
        }
    }

    private suspend fun resolveActiveClass(studentId: Long? = null): ClassEntity? {
        val current = _selectedClass.value
        if (current != null) return current
        val classesList = repository.getAllClassesList()
        if (studentId != null) {
            val student = repository.getAllStudentsList().find { it.id == studentId }
            if (student != null) {
                val clazz = classesList.find { it.id == student.classId }
                if (clazz != null) {
                    _selectedClass.value = clazz
                    return clazz
                }
            }
        }
        val first = classesList.firstOrNull()
        if (first != null) {
            _selectedClass.value = first
        }
        return first
    }

    fun markAttendance(studentId: Long, status: String, checkInTime: String = "", checkOutTime: String = "", notes: String = "", reason: String = "") {
        val activeDate = _selectedDate.value
        viewModelScope.launch {
            val activeClass = resolveActiveClass(studentId) ?: return@launch
            val existing = repository.getRecordsByDateList(activeClass.id, activeDate)
            val record = existing.find { it.studentId == studentId }
            val isAbsent = status == "A" || status == "ABSENT"
            val isUnmarked = status == "UNMARKED"

            // Do not pre-fill times: let user fill them manually
            val inTime = if (checkInTime.isNotEmpty()) checkInTime else if (isAbsent || isUnmarked) "" else (record?.checkInTime ?: "")
            val outTime = if (checkOutTime.isNotEmpty()) checkOutTime else if (isAbsent || isUnmarked) "" else (record?.checkOutTime ?: "")

            val newRecord = if (record != null) {
                record.copy(
                    status = status,
                    checkInTime = inTime,
                    checkOutTime = outTime,
                    notes = if (notes.isNotEmpty()) notes else record.notes,
                    reason = if (reason.isNotEmpty()) reason else record.reason,
                    timestamp = System.currentTimeMillis()
                )
            } else {
                AttendanceRecordEntity(
                    studentId = studentId,
                    classId = activeClass.id,
                    dateString = activeDate,
                    status = status,
                    checkInTime = inTime,
                    checkOutTime = outTime,
                    notes = notes,
                    reason = reason
                )
            }
            repository.insertRecord(newRecord)
            triggerAutoBackup()
        }
    }

    fun updateCheckInTime(studentId: Long, checkIn: String) {
        val activeDate = _selectedDate.value
        viewModelScope.launch {
            val activeClass = resolveActiveClass(studentId) ?: return@launch
            val existing = repository.getRecordsByDateList(activeClass.id, activeDate)
            val record = existing.find { it.studentId == studentId }
            val currentStatus = record?.status ?: "P"
            val newStatus = if (currentStatus == "UNMARKED") "P" else currentStatus
            val newRecord = if (record != null) {
                record.copy(
                    status = newStatus,
                    checkInTime = checkIn,
                    timestamp = System.currentTimeMillis()
                )
            } else {
                AttendanceRecordEntity(
                    studentId = studentId,
                    classId = activeClass.id,
                    dateString = activeDate,
                    status = "P",
                    checkInTime = checkIn
                )
            }
            repository.insertRecord(newRecord)
            triggerAutoBackup()
        }
    }

    fun updateCheckOutTime(studentId: Long, checkOut: String) {
        val activeDate = _selectedDate.value
        viewModelScope.launch {
            val activeClass = resolveActiveClass(studentId) ?: return@launch
            val existing = repository.getRecordsByDateList(activeClass.id, activeDate)
            val record = existing.find { it.studentId == studentId }
            val currentStatus = record?.status ?: "P"
            val newStatus = if (currentStatus == "UNMARKED") "P" else currentStatus
            val newRecord = if (record != null) {
                record.copy(
                    status = newStatus,
                    checkOutTime = checkOut,
                    timestamp = System.currentTimeMillis()
                )
            } else {
                AttendanceRecordEntity(
                    studentId = studentId,
                    classId = activeClass.id,
                    dateString = activeDate,
                    status = "P",
                    checkOutTime = checkOut
                )
            }
            repository.insertRecord(newRecord)
            triggerAutoBackup()
        }
    }

    fun updateAbsentWithOvertime(studentId: Long, notes: String, hasOvertime: Boolean, inTime: String, outTime: String, reason: String = "") {
        saveRemarksAndOvertime(
            studentId = studentId,
            notes = notes,
            reason = reason,
            hasOvertime = hasOvertime,
            checkInTime = inTime,
            checkOutTime = outTime,
            statusOverride = "A"
        )
    }

    fun saveRemarksAndOvertime(
        studentId: Long,
        notes: String,
        reason: String = "",
        hasOvertime: Boolean = false,
        checkInTime: String = "",
        checkOutTime: String = "",
        statusOverride: String? = null
    ) {
        val activeDate = _selectedDate.value
        viewModelScope.launch {
            val activeClass = resolveActiveClass(studentId) ?: return@launch
            val existing = repository.getRecordsByDateList(activeClass.id, activeDate)
            val record = existing.find { it.studentId == studentId }
            val currentStatus = record?.status ?: "UNMARKED"
            val newStatus = when {
                statusOverride != null -> statusOverride
                else -> currentStatus
            }
            val isAbsent = newStatus == "A" || newStatus == "ABSENT"

            val finalIn = when {
                isAbsent -> if (hasOvertime) checkInTime else ""
                checkInTime.isNotEmpty() -> checkInTime
                else -> record?.checkInTime ?: ""
            }

            val finalOut = when {
                isAbsent -> if (hasOvertime) checkOutTime else ""
                checkOutTime.isNotEmpty() -> checkOutTime
                else -> record?.checkOutTime ?: ""
            }

            val newRecord = if (record != null) {
                record.copy(
                    status = newStatus,
                    checkInTime = finalIn,
                    checkOutTime = finalOut,
                    notes = notes,
                    reason = reason,
                    timestamp = System.currentTimeMillis()
                )
            } else {
                AttendanceRecordEntity(
                    studentId = studentId,
                    classId = activeClass.id,
                    dateString = activeDate,
                    status = newStatus,
                    checkInTime = finalIn,
                    checkOutTime = finalOut,
                    notes = notes,
                    reason = reason
                )
            }
            repository.insertRecord(newRecord)
            triggerAutoBackup()
        }
    }

    fun updateNotes(studentId: Long, notes: String) {
        saveRemarksAndOvertime(studentId = studentId, notes = notes)
    }

    fun updateReason(studentId: Long, reason: String) {
        viewModelScope.launch {
            val activeDate = _selectedDate.value
            val activeClass = resolveActiveClass(studentId) ?: return@launch
            val existing = repository.getRecordsByDateList(activeClass.id, activeDate)
            val record = existing.find { it.studentId == studentId }
            if (record != null) {
                repository.insertRecord(record.copy(reason = reason, timestamp = System.currentTimeMillis()))
            } else {
                repository.insertRecord(
                    AttendanceRecordEntity(
                        studentId = studentId,
                        classId = activeClass.id,
                        dateString = activeDate,
                        status = "UNMARKED",
                        reason = reason
                    )
                )
            }
            triggerAutoBackup()
        }
    }

    fun updateSpecificAttendanceRecord(
        classId: Long,
        studentId: Long,
        dateString: String,
        status: String,
        checkInTime: String,
        checkOutTime: String,
        notes: String,
        reason: String = ""
    ) {
        viewModelScope.launch {
            val existingList = repository.getRecordsByDateList(classId, dateString)
            val existing = existingList.find { it.studentId == studentId }
            val newRecord = if (existing != null) {
                existing.copy(
                    status = status,
                    checkInTime = checkInTime,
                    checkOutTime = checkOutTime,
                    notes = notes,
                    reason = reason,
                    timestamp = System.currentTimeMillis()
                )
            } else {
                AttendanceRecordEntity(
                    studentId = studentId,
                    classId = classId,
                    dateString = dateString,
                    status = status,
                    checkInTime = checkInTime,
                    checkOutTime = checkOutTime,
                    notes = notes,
                    reason = reason
                )
            }
            repository.insertRecord(newRecord)
            triggerAutoBackup()
        }
    }

    fun bulkMark(status: String) {
        val activeDate = _selectedDate.value
        viewModelScope.launch {
            val activeClass = resolveActiveClass() ?: return@launch
            val studentsList = repository.getStudentsByClassList(activeClass.id)
            val existing = repository.getRecordsByDateList(activeClass.id, activeDate)
            val isAbsent = status == "A" || status == "ABSENT"
            val newRecords = studentsList.map { student ->
                val record = existing.find { it.studentId == student.id }
                val inTime = if (isAbsent) "" else (record?.checkInTime ?: "")
                val outTime = if (isAbsent) "" else (record?.checkOutTime ?: "")
                if (record != null) {
                    record.copy(
                        status = status,
                        checkInTime = inTime,
                        checkOutTime = outTime,
                        timestamp = System.currentTimeMillis()
                    )
                } else {
                    AttendanceRecordEntity(
                        studentId = student.id,
                        classId = activeClass.id,
                        dateString = activeDate,
                        status = status,
                        checkInTime = inTime,
                        checkOutTime = outTime
                    )
                }
            }
            repository.insertRecords(newRecords)
            triggerAutoBackup()
        }
    }

    private suspend fun prepopulateDatabase() {
        // Create standard shifts
        val morningShiftId = repository.insertShift(ShiftEntity(name = "Morning Shift", timings = "08:00 AM - 04:00 PM"))
        val dayShiftId = repository.insertShift(ShiftEntity(name = "Day Shift", timings = "09:00 AM - 05:00 PM"))
        val eveningShiftId = repository.insertShift(ShiftEntity(name = "Evening Shift", timings = "04:00 PM - 12:00 AM"))
        val nightShiftId = repository.insertShift(ShiftEntity(name = "Night Shift", timings = "12:00 AM - 08:00 AM"))

        // Create templates shop departments
        val salesId = repository.insertClass(ClassEntity(name = "Sales & Customer Counter", colorHex = "#4F46E5", displayOrder = 0))
        val cashierId = repository.insertClass(ClassEntity(name = "Billing & Accounts", colorHex = "#EF4444", displayOrder = 1))
        val warehouseId = repository.insertClass(ClassEntity(name = "Inventory & Stockroom", colorHex = "#10B981", displayOrder = 2))

        // Add staff members (previously students)
        val salesStaff = listOf(
            StudentEntity(classId = salesId, name = "Alice Smith", rollNumber = "EMP-01", email = "alice@shop.com", shiftId = dayShiftId),
            StudentEntity(classId = salesId, name = "Bob Johnson", rollNumber = "EMP-02", email = "bob@shop.com", shiftId = dayShiftId),
            StudentEntity(classId = salesId, name = "Clara Oswald", rollNumber = "EMP-03", email = "clara@shop.com", shiftId = morningShiftId),
            StudentEntity(classId = salesId, name = "David Miller", rollNumber = "EMP-04", email = "david@shop.com", shiftId = morningShiftId),
            StudentEntity(classId = salesId, name = "Emma Watson", rollNumber = "EMP-05", email = "emma@shop.com", shiftId = eveningShiftId),
            StudentEntity(classId = salesId, name = "Frank Wright", rollNumber = "EMP-06", email = "frank@shop.com", shiftId = eveningShiftId)
        )
        for (staff in salesStaff) repository.insertStudent(staff)

        val cashierStaff = listOf(
            StudentEntity(classId = cashierId, name = "Grace Hopper", rollNumber = "EMP-07", email = "grace@shop.com", shiftId = dayShiftId),
            StudentEntity(classId = cashierId, name = "Henry Cavill", rollNumber = "EMP-08", email = "henry@shop.com", shiftId = dayShiftId),
            StudentEntity(classId = cashierId, name = "Irene Adler", rollNumber = "EMP-09", email = "irene@shop.com", shiftId = nightShiftId),
            StudentEntity(classId = cashierId, name = "Jack Reacher", rollNumber = "EMP-10", email = "jack@shop.com", shiftId = nightShiftId)
        )
        for (staff in cashierStaff) repository.insertStudent(staff)

        val warehouseStaff = listOf(
            StudentEntity(classId = warehouseId, name = "Karen Blixen", rollNumber = "EMP-11", email = "karen@shop.com", shiftId = morningShiftId),
            StudentEntity(classId = warehouseId, name = "Leo Tolstoy", rollNumber = "EMP-12", email = "leo@shop.com", shiftId = dayShiftId),
            StudentEntity(classId = warehouseId, name = "Mark Twain", rollNumber = "EMP-13", email = "mark@shop.com", shiftId = eveningShiftId)
        )
        for (staff in warehouseStaff) repository.insertStudent(staff)

        // Seed history for Sales Counter
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayMs = 24 * 60 * 60 * 1000L
        val today = Date()
        val yesterday = Date(today.time - dayMs)
        val dayBefore = Date(today.time - 2 * dayMs)

        // Yesterday
        val staffList = repository.getStudentsByClassList(salesId)
        val yesterdayDateStr = sdf.format(yesterday)
        val seedRecords = mutableListOf<AttendanceRecordEntity>()
        staffList.forEachIndexed { idx, staff ->
            val status = when (idx % 4) {
                0 -> "A"
                1 -> "HD MOR"
                2 -> "HD EVE"
                else -> "P"
            }
            val checkIn = when (status) {
                "P" -> "08:55 AM"
                "HD MOR" -> "09:20 AM"
                "HD EVE" -> "02:05 PM"
                else -> ""
            }
            val checkOut = when (status) {
                "P" -> "06:05 PM"
                "HD MOR" -> "01:30 PM"
                "HD EVE" -> "08:00 PM"
                else -> ""
            }
            seedRecords.add(
                AttendanceRecordEntity(
                    studentId = staff.id,
                    classId = salesId,
                    dateString = yesterdayDateStr,
                    status = status,
                    checkInTime = checkIn,
                    checkOutTime = checkOut
                )
            )
        }

        // Day Before Yesterday
        val dayBeforeDateStr = sdf.format(dayBefore)
        staffList.forEachIndexed { idx, staff ->
            val status = if (idx % 3 == 0) "A" else "P"
            val checkIn = if (status == "P") "09:02 AM" else ""
            val checkOut = if (status == "P") "06:00 PM" else ""
            seedRecords.add(
                AttendanceRecordEntity(
                    studentId = staff.id,
                    classId = salesId,
                    dateString = dayBeforeDateStr,
                    status = status,
                    checkInTime = checkIn,
                    checkOutTime = checkOut
                )
            )
        }

        repository.insertRecords(seedRecords)
    }

    fun saveGoogleSheetsConfig(sheetId: String, webAppUrl: String, accessToken: String) {
        _googleSheetId.value = sheetId.trim()
        _googleWebAppUrl.value = webAppUrl.trim()
        _googleAccessToken.value = accessToken.trim()
        sharedPrefs.edit().apply {
            putString("google_sheet_id", sheetId.trim())
            putString("google_webapp_url", webAppUrl.trim())
            putString("google_access_token", accessToken.trim())
            apply()
        }
    }

    fun clearSyncResult() {
        _syncResult.value = null
    }

    fun createGoogleSheet(title: String) {
        val token = _googleAccessToken.value
        if (token.isEmpty()) {
            _syncResult.value = "Error: Access Token is required to create a spreadsheet."
            return
        }
        viewModelScope.launch {
            _isSyncing.value = true
            _syncResult.value = "Creating Google Sheet..."
            val result = com.example.sync.GoogleSheetsSyncHelper.createSpreadsheet(token, title)
            result.onSuccess { newSheetId ->
                _googleSheetId.value = newSheetId
                sharedPrefs.edit().putString("google_sheet_id", newSheetId).apply()
                _syncResult.value = "Created spreadsheet successfully!"
            }.onFailure { exception ->
                _syncResult.value = "Error: ${exception.message}"
            }
            _isSyncing.value = false
        }
    }

    fun syncSelectedClassToGoogleSheets(useWebApp: Boolean) {
        val activeClass = _selectedClass.value
        if (activeClass == null) {
            _syncResult.value = "Error: No department selected to sync."
            return
        }

        viewModelScope.launch {
            _isSyncing.value = true
            _syncResult.value = "Fetching local attendance records..."

            val staffList = repository.getStudentsByClassList(activeClass.id)
            val allRecordsFlow = repository.getAllRecordsByClass(activeClass.id)
            
            val recordsList = try {
                allRecordsFlow.first()
            } catch (e: Exception) {
                emptyList()
            }

            if (recordsList.isEmpty()) {
                _syncResult.value = "No records found to sync for this department."
                _isSyncing.value = false
                return@launch
            }

            _syncResult.value = "Syncing ${recordsList.size} records..."
            
            val syncResultObj = if (useWebApp) {
                val webAppUrl = _googleWebAppUrl.value
                com.example.sync.GoogleSheetsSyncHelper.syncViaWebApp(webAppUrl, activeClass, staffList, recordsList)
            } else {
                val sheetId = _googleSheetId.value
                val token = _googleAccessToken.value
                com.example.sync.GoogleSheetsSyncHelper.syncViaRestApi(sheetId, token, activeClass, staffList, recordsList)
            }

            syncResultObj.onSuccess { message ->
                _syncResult.value = message
                val currentTime = System.currentTimeMillis()
                _lastSyncTime.value = currentTime
                sharedPrefs.edit().putLong("last_sync_time", currentTime).apply()
            }.onFailure { exception ->
                _syncResult.value = "Sync Failed: ${exception.message}"
            }
            _isSyncing.value = false
        }
    }

    fun restoreFromGoogleSheets(useWebApp: Boolean) {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncResult.value = "Connecting to Google Sheet & fetching data..."

            val webAppUrl = _googleWebAppUrl.value
            if (webAppUrl.isEmpty()) {
                _syncResult.value = "Error: Please enter your Google Apps Script Web App URL first."
                _isSyncing.value = false
                return@launch
            }

            val fetchResult = com.example.sync.GoogleSheetsSyncHelper.fetchRecordsFromWebApp(webAppUrl)
            fetchResult.onSuccess { sheetRecords ->
                if (sheetRecords.isEmpty()) {
                    _syncResult.value = "No records found in the Google Sheet, or the script needs update to support 'doGet' fetching."
                    _isSyncing.value = false
                    return@onSuccess
                }

                _syncResult.value = "Restoring ${sheetRecords.size} records into local database..."

                var targetClass = _selectedClass.value
                if (targetClass == null) {
                    val classes = repository.getAllClassesList()
                    targetClass = classes.firstOrNull()
                    if (targetClass == null) {
                        val newClassId = repository.insertClass(
                            ClassEntity(name = "Main Department", colorHex = "#4F46E5", displayOrder = 0)
                        )
                        targetClass = ClassEntity(id = newClassId, name = "Main Department", colorHex = "#4F46E5", displayOrder = 0)
                    }
                    _selectedClass.value = targetClass
                }

                // Existing staff in target class
                var existingStaff = repository.getStudentsByClassList(targetClass.id)
                val staffMap = mutableMapOf<String, StudentEntity>() // roll or name -> StudentEntity
                existingStaff.forEach {
                    if (it.rollNumber.isNotBlank()) staffMap[it.rollNumber.lowercase()] = it
                    staffMap[it.name.lowercase()] = it
                }

                var restoredStaffCount = 0
                // Create staff members if they don't exist
                val uniqueStaffEntries = sheetRecords.distinctBy { it.rollNumber.ifBlank { it.staffName } }
                for (entry in uniqueStaffEntries) {
                    val keyRoll = entry.rollNumber.lowercase()
                    val keyName = entry.staffName.lowercase()
                    if (!staffMap.containsKey(keyRoll) && !staffMap.containsKey(keyName)) {
                        val newStaffId = repository.insertStudent(
                            StudentEntity(
                                classId = targetClass.id,
                                name = entry.staffName,
                                rollNumber = entry.rollNumber.ifBlank { "EMP-${System.currentTimeMillis() % 10000}" },
                                phone = "",
                                email = ""
                            )
                        )
                        val created = StudentEntity(
                            id = newStaffId,
                            classId = targetClass.id,
                            name = entry.staffName,
                            rollNumber = entry.rollNumber.ifBlank { "EMP-${System.currentTimeMillis() % 10000}" },
                            phone = "",
                            email = ""
                        )
                        if (entry.rollNumber.isNotBlank()) staffMap[keyRoll] = created
                        staffMap[keyName] = created
                        restoredStaffCount++
                    }
                }

                // Refresh staff list
                existingStaff = repository.getStudentsByClassList(targetClass.id)
                val finalStaffMap = mutableMapOf<String, StudentEntity>()
                existingStaff.forEach {
                    if (it.rollNumber.isNotBlank()) finalStaffMap[it.rollNumber.lowercase()] = it
                    finalStaffMap[it.name.lowercase()] = it
                }

                // Insert attendance records
                val recordsToInsert = mutableListOf<AttendanceRecordEntity>()
                for (rec in sheetRecords) {
                    val staff = finalStaffMap[rec.rollNumber.lowercase()] ?: finalStaffMap[rec.staffName.lowercase()]
                    if (staff != null && rec.date.isNotBlank()) {
                        recordsToInsert.add(
                            AttendanceRecordEntity(
                                studentId = staff.id,
                                classId = targetClass.id,
                                dateString = rec.date,
                                status = rec.status.ifBlank { "P" },
                                checkInTime = rec.checkIn,
                                checkOutTime = rec.checkOut,
                                notes = rec.notes,
                                reason = rec.reason
                            )
                        )
                    }
                }

                if (recordsToInsert.isNotEmpty()) {
                    repository.insertRecords(recordsToInsert)
                }

                triggerAutoBackup()
                val currentTime = System.currentTimeMillis()
                _lastSyncTime.value = currentTime
                sharedPrefs.edit().putLong("last_sync_time", currentTime).apply()

                _syncResult.value = "Successfully restored ${recordsToInsert.size} records across ${staffMap.size} staff into '${targetClass.name}'!"
            }.onFailure { exception ->
                _syncResult.value = "Restore Failed: ${exception.message}"
            }

            _isSyncing.value = false
        }
    }

    fun setFirebaseConfig(url: String, workspace: String, authToken: String, autoSync: Boolean) {
        _firebaseUrl.value = url.trim()
        _firebaseWorkspace.value = workspace.trim().ifBlank { "shop_attendance" }
        _firebaseAuthToken.value = authToken.trim()
        _firebaseAutoSyncEnabled.value = autoSync

        sharedPrefs.edit()
            .putString("firebase_url", url.trim())
            .putString("firebase_workspace", workspace.trim().ifBlank { "shop_attendance" })
            .putString("firebase_auth_token", authToken.trim())
            .putBoolean("firebase_auto_sync_enabled", autoSync)
            .apply()
    }

    fun setPortalBaseUrl(rawUrl: String) {
        val normalized = normalizePortalBaseUrl(rawUrl)
        _portalBaseUrl.value = normalized
        sharedPrefs.edit().putString("github_pages_portal_url", normalized).apply()
    }

    fun syncToFirebase(showFeedback: Boolean = true) {
        viewModelScope.launch {
            val url = _firebaseUrl.value
            if (url.isBlank()) {
                if (showFeedback) {
                    _firebaseSyncStatus.value = "Error: Please enter your Firebase Database URL first."
                }
                return@launch
            }

            backupSyncMutex.withLock {
                _isFirebaseSyncing.value = true
                if (showFeedback) _firebaseSyncStatus.value = "Connecting and syncing to Firebase cloud..."

                val res = com.example.sync.FirebaseSyncHelper.syncToFirebase(
                    firebaseUrl = url,
                    workspaceKey = _firebaseWorkspace.value,
                    authToken = _firebaseAuthToken.value,
                    repository = repository
                )

                res.onSuccess { msg ->
                    val now = System.currentTimeMillis()
                    _firebaseLastSyncTime.value = now
                    sharedPrefs.edit().putLong("firebase_last_sync_time", now).apply()
                    if (showFeedback) {
                        _firebaseSyncStatus.value = msg
                    }
                }.onFailure { err ->
                    if (showFeedback) {
                        _firebaseSyncStatus.value = "Firebase Sync Failed: ${err.message}"
                    }
                }
                _isFirebaseSyncing.value = false
            }
        }
    }

    fun restoreFromFirebase() {
        viewModelScope.launch {
            val url = _firebaseUrl.value
            if (url.isBlank()) {
                _firebaseSyncStatus.value = "Error: Please enter your Firebase Database URL first."
                return@launch
            }

            _isFirebaseSyncing.value = true
            _firebaseSyncStatus.value = "Fetching and restoring data from Firebase..."

            val res = com.example.sync.FirebaseSyncHelper.restoreFromFirebase(
                firebaseUrl = url,
                workspaceKey = _firebaseWorkspace.value,
                authToken = _firebaseAuthToken.value,
                repository = repository
            )

            res.onSuccess { msg ->
                val now = System.currentTimeMillis()
                _firebaseLastSyncTime.value = now
                sharedPrefs.edit().putLong("firebase_last_sync_time", now).apply()
                _firebaseSyncStatus.value = msg
                triggerAutoBackup()
            }.onFailure { err ->
                _firebaseSyncStatus.value = "Firebase Restore Failed: ${err.message}"
            }
            _isFirebaseSyncing.value = false
        }
    }

    fun testFirebaseConnection() {
        viewModelScope.launch {
            val url = _firebaseUrl.value
            if (url.isBlank()) {
                _firebaseSyncStatus.value = "Error: Please enter a Firebase Database URL to test."
                return@launch
            }

            _isFirebaseSyncing.value = true
            _firebaseSyncStatus.value = "Testing Firebase connection..."

            val res = com.example.sync.FirebaseSyncHelper.testConnection(
                firebaseUrl = url,
                workspaceKey = _firebaseWorkspace.value,
                authToken = _firebaseAuthToken.value
            )

            res.onSuccess { msg ->
                _firebaseSyncStatus.value = msg
            }.onFailure { err ->
                _firebaseSyncStatus.value = err.message
            }
            _isFirebaseSyncing.value = false
        }
    }

    fun clearFirebaseStatus() {
        _firebaseSyncStatus.value = null
    }

    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }

        fun formatDisplayDate(dateString: String): String {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                val date = parser.parse(dateString)
                if (date != null) {
                    formatter.format(date)
                } else {
                    dateString
                }
            } catch (e: Exception) {
                dateString
            }
        }
    }
}

fun normalizePortalBaseUrl(rawUrl: String): String {
    var clean = rawUrl.trim()
    if (clean.isBlank()) return ""
    if (!clean.startsWith("http://", ignoreCase = true) && !clean.startsWith("https://", ignoreCase = true)) {
        clean = "https://$clean"
    }
    clean = clean.trimEnd('/')
    if (clean.endsWith("/index.html", ignoreCase = true)) {
        clean = clean.dropLast("/index.html".length).trimEnd('/')
    }
    return clean
}

fun buildEmployeePassbookLink(
    portalBaseUrl: String,
    student: StudentEntity,
    firebaseUrl: String = "https://gen-lang-client-0830360424-default-rtdb.firebaseio.com",
    workspace: String = "shop_attendance",
    monthFilter: String? = null,
    lockToEmployee: Boolean = true
): String {
    val cleanBase = normalizePortalBaseUrl(portalBaseUrl)
    if (cleanBase.isBlank()) return ""
    val sb = StringBuilder(
        "$cleanBase/?staffId=${student.id}&emp=${Uri.encode(student.rollNumber)}&name=${Uri.encode(student.name)}"
    )
    if (lockToEmployee) {
        sb.append("&lock=1")
    }
    val defaultFb = "https://gen-lang-client-0830360424-default-rtdb.firebaseio.com"
    val cleanFb = firebaseUrl.trim().trimEnd('/').removeSuffix(".json")
    val cleanWs = workspace.trim().ifBlank { "shop_attendance" }
    if (cleanFb.isNotBlank() && !cleanFb.equals(defaultFb, ignoreCase = true)) {
        sb.append("&fb=").append(Uri.encode(cleanFb))
    }
    if (!cleanWs.equals("shop_attendance", ignoreCase = true)) {
        sb.append("&ws=").append(Uri.encode(cleanWs))
    }
    if (!monthFilter.isNullOrBlank()) {
        sb.append("&month=").append(Uri.encode(monthFilter))
    }
    return sb.toString()
}
