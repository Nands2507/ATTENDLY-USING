package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ClassEntity
import com.example.data.HisabLedgerEntryEntity
import com.example.data.HisabProductEntity
import com.example.data.StudentEntity
import com.example.data.displayName
import com.example.data.cleanEmployeeName
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HisabView(
    allStudents: List<StudentEntity>,
    allClasses: List<ClassEntity>,
    allProducts: List<HisabProductEntity>,
    selectedStudent: StudentEntity?,
    ledgerEntries: List<HisabLedgerEntryEntity>,
    customCategories: Set<String> = emptySet(),
    deletedCategories: Set<String> = emptySet(),
    onSelectStudent: (StudentEntity?) -> Unit,
    onAddStudent: () -> Unit,
    onAddProduct: (name: String, category: String, rate: Double) -> Unit,
    onUpdateProduct: (HisabProductEntity) -> Unit,
    onDeleteProduct: (HisabProductEntity) -> Unit,
    onRestoreDefaultProducts: () -> Unit,
    onAddSalesEntry: (studentId: Long, dateString: String, breakdownJson: String, totalPieces: Int, totalEarned: Double, notes: String) -> Unit,
    onAddAdvanceEntry: (studentId: Long, dateString: String, advanceAmount: Double, notes: String) -> Unit,
    onUpdateEntry: ((HisabLedgerEntryEntity) -> Unit)? = null,
    onDeleteEntry: (HisabLedgerEntryEntity) -> Unit,
    onAddCategory: ((String) -> Unit)? = null,
    onRenameCategory: ((oldCategory: String, newCategory: String) -> Unit)? = null,
    onDeleteCategory: ((categoryName: String, deleteItems: Boolean, fallbackCategory: String) -> Unit)? = null,
    onMoveStudentUp: ((StudentEntity) -> Unit)? = null,
    onMoveStudentDown: ((StudentEntity) -> Unit)? = null,
    onSortStudents: ((String) -> Unit)? = null,
    onReorderStudents: ((List<StudentEntity>) -> Unit)? = null,
    portalBaseUrl: String = "",
    firebaseUrl: String = "https://gen-lang-client-0830360424-default-rtdb.firebaseio.com",
    firebaseWorkspace: String = "shop_attendance",
    isFirebaseSyncing: Boolean = false,
    firebaseLastSyncTime: Long = 0L,
    onSavePortalBaseUrl: ((String) -> Unit)? = null,
    onSyncToFirebaseNow: (() -> Unit)? = null,
    onAutoSyncForShare: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Auto-select first salesman if none selected
    LaunchedEffect(allStudents, selectedStudent) {
        if (selectedStudent == null && allStudents.isNotEmpty()) {
            onSelectStudent(allStudents.first())
        }
    }

    // Rearrange staff dialog state
    var showRearrangeEmployeesDialog by remember { mutableStateOf(false) }

    // Edit ledger entry state
    var editingEntry by remember { mutableStateOf<HisabLedgerEntryEntity?>(null) }

    // Input mode: 0 = Quick Clicker Grid, 1 = Manual Form, 2 = Log Advance / Return / Chai
    var inputMode by remember { mutableIntStateOf(0) }
    // Sort option: 0 = By Category, 1 = By Name, 2 = Manual Order
    var sortOption by remember { mutableIntStateOf(0) }

    // Clicker counts map (productId -> count)
    val clickerCounts = remember { mutableStateMapOf<Long, Int>() }

    // Date for logging
    var logDate by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())) }
    var logNotes by remember { mutableStateOf("") }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // Advance Payment state
    var advanceDate by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())) }
    var advanceModeTab by remember { mutableIntStateOf(0) } // 0 = Cash Advance, 1 = Chai/Thanda, 2 = Return Items

    // 0: Cash Advance
    var advanceAmountText by remember { mutableStateOf("") }
    var advanceNotes by remember { mutableStateOf("") }

    // 1: Chai/Thanda
    var chaiSelectedItem by remember { mutableStateOf("Chai (Tea)") }
    var chaiCustomItemName by remember { mutableStateOf("") }
    var chaiQtyText by remember { mutableStateOf("1") }
    var chaiRateText by remember { mutableStateOf("10") }
    var chaiCustomAmountText by remember { mutableStateOf("10") }
    var chaiNotes by remember { mutableStateOf("") }

    // 2: Return Items (number of sarees return & their per piece price)
    var returnSelectedProduct by remember(allProducts) { mutableStateOf(allProducts.firstOrNull()) }
    var returnCustomProductName by remember { mutableStateOf("") }
    var returnQtyText by remember { mutableStateOf("1") }
    var returnRateText by remember(returnSelectedProduct) {
        mutableStateOf(returnSelectedProduct?.ratePerPiece?.toInt()?.toString() ?: "100")
    }
    var returnNotes by remember { mutableStateOf("") }

    var isAdvanceCardExpanded by remember { mutableStateOf(true) }

    // Category management states
    var showAddNewCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryInputText by remember { mutableStateOf("") }
    var showManageCategoriesDialog by remember { mutableStateOf(false) }
    var categoryToRename by remember { mutableStateOf<String?>(null) }
    var renameCategoryInputText by remember { mutableStateOf("") }
    var categoryToDelete by remember { mutableStateOf<String?>(null) }

    // Category filter for Quick Clicker grid (Matching custom category bar)
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var categoryFilterDropdownExpanded by remember { mutableStateOf(false) }

    // Product edit dialog state
    var editingProduct by remember { mutableStateOf<HisabProductEntity?>(null) }
    var editProductNameText by remember { mutableStateOf("") }
    var editProductCategoryText by remember { mutableStateOf("") }
    var editRateText by remember { mutableStateOf("") }
    var editCategoryDropdownExpanded by remember { mutableStateOf(false) }

    // Delete product card confirmation
    var productToDelete by remember { mutableStateOf<HisabProductEntity?>(null) }

    // Distinct categories: all from products + custom added - deleted
    val allCategoryNames = remember(allProducts, customCategories, deletedCategories) {
        val fromProds = allProducts.map { it.category.trim().uppercase(Locale.getDefault()) }
        val combined = (fromProds + customCategories).toSet() - deletedCategories
        val filtered = combined.filter { it.isNotBlank() }.sorted()
        if (filtered.isEmpty()) listOf("GENERAL") else filtered
    }

    // Custom Saree / Item Dialog state
    var customItemName by remember { mutableStateOf("") }
    var customItemCategory by remember(allCategoryNames, selectedCategoryFilter) {
        mutableStateOf(if (selectedCategoryFilter != "ALL" && selectedCategoryFilter in allCategoryNames) selectedCategoryFilter else (allCategoryNames.firstOrNull() ?: "GENERAL"))
    }
    var customItemRateText by remember { mutableStateOf("") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(allCategoryNames) {
        if (customItemCategory !in allCategoryNames) {
            customItemCategory = allCategoryNames.firstOrNull() ?: "GENERAL"
        }
        if (selectedCategoryFilter != "ALL" && selectedCategoryFilter !in allCategoryNames) {
            selectedCategoryFilter = "ALL"
        }
    }

    // Dashboard Summary Period Filter (0 = Complete / All-Time, 1 = Month-Wise, 2 = Custom Range)
    var dashboardPeriodMode by remember { mutableIntStateOf(0) }
    var dashboardSelectedMonth by remember { mutableStateOf(SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(Date())) }
    var dashboardCustomFromDate by remember { mutableStateOf("01/" + SimpleDateFormat("MM/yyyy", Locale.ENGLISH).format(Date())) }
    var dashboardCustomToDate by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())) }
    var showDashboardFromDatePicker by remember { mutableStateOf(false) }
    var showDashboardToDatePicker by remember { mutableStateOf(false) }

    // Statement history filter, sorting & grouping
    var groupByMode by remember { mutableIntStateOf(0) } // 0 = Month-Wise, 1 = Date-Wise, 2 = Flat List
    var sortDateMode by remember { mutableIntStateOf(0) } // 0 = Newest Date First, 1 = Oldest Date First, 2 = Highest Pcs, 3 = Highest Net/Amount
    var filterTypeMode by remember { mutableIntStateOf(0) } // 0 = ALL, 1 = SALES ONLY, 2 = ADVANCES ONLY
    var selectedMonthFilter by remember { mutableStateOf("ALL") }
    var specificDateFilter by remember { mutableStateOf<String?>(null) }
    var showFilterDatePickerDialog by remember { mutableStateOf(false) }

    // Share Employee Link / Passbook Dialog State
    var showShareEmployeeDialog by remember { mutableStateOf<StudentEntity?>(null) }
    var shareFormatDetailed by remember { mutableStateOf(false) } // false = Option 1: Standard Summary, true = Option 2: Detailed with Saree Names
    var sharePeriodMode by remember { mutableIntStateOf(0) } // 0 = Whole Month (Default), 1 = Custom Range, 2 = Complete / All-Time
    var shareSelectedMonth by remember { mutableStateOf(SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(Date())) }
    var shareCustomFromDate by remember { mutableStateOf("01/" + SimpleDateFormat("MM/yyyy", Locale.ENGLISH).format(Date())) }
    var shareCustomToDate by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())) }
    var showShareFromDatePicker by remember { mutableStateOf(false) }
    var showShareToDatePicker by remember { mutableStateOf(false) }

    // Delete entry confirmation
    var entryToDelete by remember { mutableStateOf<HisabLedgerEntryEntity?>(null) }

    // Overall / Complete calculations for selected salesman
    val totalEarned = remember(ledgerEntries) {
        ledgerEntries.filter { it.entryType == "SALES" }.sumOf { it.totalEarned }
    }
    val totalPieces = remember(ledgerEntries) {
        ledgerEntries.filter { it.entryType == "SALES" }.sumOf { it.totalPieces }
    }
    val totalAdvance = remember(ledgerEntries) {
        ledgerEntries.filter { it.entryType == "ADVANCE" }.sumOf { it.advancePaid }
    }
    val netOutstanding = totalEarned - totalAdvance

    // Date parsing helper to ensure strict date-wise ordering and grouping
    fun parseEntryDateMillis(dateStr: String): Long {
        val trimmed = dateStr.trim()
        val formats = arrayOf(
            "dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd", "yyyy/MM/dd",
            "d/M/yyyy", "d-M-yyyy", "d/MM/yyyy", "dd/M/yyyy",
            "dd/MM/yy", "dd-MM-yy", "d/M/yy", "d-M-yy", "yyyy/M/d", "yyyy-M-d",
            "dd.MM.yyyy", "d.M.yyyy", "yyyy.MM.dd"
        )
        for (fmt in formats) {
            try {
                val sdf = SimpleDateFormat(fmt, Locale.ENGLISH)
                sdf.isLenient = true
                val d = sdf.parse(trimmed)
                if (d != null) {
                    val cal = Calendar.getInstance()
                    cal.time = d
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    return cal.timeInMillis
                }
            } catch (_: Exception) {}
        }
        return 0L
    }

    fun getEntryCalendarMillis(entry: HisabLedgerEntryEntity): Long {
        val parsed = parseEntryDateMillis(entry.dateString)
        if (parsed > 0L) return parsed
        if (entry.timestamp > 0L) {
            val cal = Calendar.getInstance()
            cal.timeInMillis = entry.timestamp
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }
        return 0L
    }

    fun getEntryMonthKey(dateStr: String, timestamp: Long): String {
        val parsed = parseEntryDateMillis(dateStr)
        val time = if (parsed > 0L) parsed else timestamp
        if (time > 0L) {
            val cal = Calendar.getInstance()
            cal.timeInMillis = time
            return SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(cal.time)
        }
        val parts = dateStr.trim().split("/", "-", ".")
        if (parts.size >= 3) {
            val m = parts[1].toIntOrNull()
            val y = parts[2].toIntOrNull()
            if (m != null && y != null && m in 1..12) {
                val cal = Calendar.getInstance()
                cal.set(Calendar.MONTH, m - 1)
                cal.set(Calendar.YEAR, if (y < 100) 2000 + y else y)
                return SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(cal.time)
            }
        }
        return "Other"
    }

    fun getEntryMonthFullTitle(dateStr: String, timestamp: Long): String {
        val parsed = parseEntryDateMillis(dateStr)
        val time = if (parsed > 0L) parsed else timestamp
        if (time > 0L) {
            val cal = Calendar.getInstance()
            cal.timeInMillis = time
            return SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(cal.time)
        }
        val parts = dateStr.trim().split("/", "-", ".")
        if (parts.size >= 3) {
            val m = parts[1].toIntOrNull()
            val y = parts[2].toIntOrNull()
            if (m != null && y != null && m in 1..12) {
                val cal = Calendar.getInstance()
                cal.set(Calendar.MONTH, m - 1)
                cal.set(Calendar.YEAR, if (y < 100) 2000 + y else y)
                return SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(cal.time)
            }
        }
        return "Unspecified Month"
    }

    // Dashboard Timeframe-Filtered calculations for selected salesman
    val dashboardEntries = remember(ledgerEntries, dashboardPeriodMode, dashboardSelectedMonth, dashboardCustomFromDate, dashboardCustomToDate) {
        when (dashboardPeriodMode) {
            0 -> ledgerEntries // COMPLETE / ALL-TIME
            1 -> {
                // MONTH-WISE
                ledgerEntries.filter { entry ->
                    val mKey = getEntryMonthKey(entry.dateString, entry.timestamp)
                    val mFull = getEntryMonthFullTitle(entry.dateString, entry.timestamp)
                    mKey.equals(dashboardSelectedMonth, ignoreCase = true) ||
                    mFull.equals(dashboardSelectedMonth, ignoreCase = true) ||
                    entry.dateString.contains(dashboardSelectedMonth, ignoreCase = true)
                }
            }
            2 -> {
                // CUSTOM RANGE
                val fromMillis = parseEntryDateMillis(dashboardCustomFromDate)
                val toMillis = parseEntryDateMillis(dashboardCustomToDate)
                val endInclusive = if (toMillis > 0L) toMillis + (24 * 60 * 60 * 1000L - 1) else Long.MAX_VALUE
                ledgerEntries.filter { entry ->
                    val time = getEntryCalendarMillis(entry)
                    if (fromMillis > 0L && toMillis > 0L) {
                        time in fromMillis..endInclusive
                    } else if (fromMillis > 0L) {
                        time >= fromMillis
                    } else if (toMillis > 0L) {
                        time <= endInclusive
                    } else {
                        true
                    }
                }
            }
            else -> ledgerEntries
        }
    }

    val dashboardTotalEarned = remember(dashboardEntries) {
        dashboardEntries.filter { it.entryType == "SALES" }.sumOf { it.totalEarned }
    }
    val dashboardTotalPieces = remember(dashboardEntries) {
        dashboardEntries.filter { it.entryType == "SALES" }.sumOf { it.totalPieces }
    }
    val dashboardTotalAdvance = remember(dashboardEntries) {
        dashboardEntries.filter { it.entryType == "ADVANCE" }.sumOf { it.advancePaid }
    }
    val dashboardNetOutstanding = dashboardTotalEarned - dashboardTotalAdvance

    // Current Running Calculation in Quick Clicker Grid
    val runningCalculation = remember(clickerCounts.toMap(), allProducts) {
        var totalPcs = 0
        var totalCost = 0.0
        val itemsList = mutableListOf<Triple<String, Int, Double>>()
        for ((pId, count) in clickerCounts) {
            if (count > 0) {
                val prod = allProducts.find { it.id == pId }
                if (prod != null) {
                    totalPcs += count
                    totalCost += count * prod.ratePerPiece
                    itemsList.add(Triple(prod.name, count, prod.ratePerPiece))
                }
            }
        }
        Triple(totalPcs, totalCost, itemsList)
    }

    // Sorted products
    val sortedProducts = remember(allProducts, sortOption) {
        when (sortOption) {
            0 -> allProducts.sortedWith(compareBy({ it.category }, { it.displayOrder }, { it.name }))
            1 -> allProducts.sortedBy { it.name }
            else -> allProducts.sortedBy { it.displayOrder }
        }
    }

    // Displayed products filtered by active category
    val displayedProducts = remember(sortedProducts, selectedCategoryFilter) {
        if (selectedCategoryFilter == "ALL") sortedProducts
        else sortedProducts.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    // Available distinct months extracted from ledger entries (sorted newest first)
    val availableMonths = remember(ledgerEntries) {
        val monthTimeMap = linkedMapOf<String, Long>()
        for (entry in ledgerEntries) {
            val key = getEntryMonthKey(entry.dateString, entry.timestamp)
            if (key != "Other") {
                val time = getEntryCalendarMillis(entry)
                if (!monthTimeMap.containsKey(key) || time > (monthTimeMap[key] ?: 0L)) {
                    monthTimeMap[key] = time
                }
            }
        }
        monthTimeMap.entries.sortedByDescending { it.value }.map { it.key }
    }

    // Filtered & Sorted ledger entries
    val filteredEntries = remember(ledgerEntries, selectedMonthFilter, specificDateFilter, filterTypeMode, sortDateMode) {
        var list = ledgerEntries
        if (filterTypeMode == 1) {
            list = list.filter { it.entryType == "SALES" }
        } else if (filterTypeMode == 2) {
            list = list.filter { it.entryType == "ADVANCE" }
        }
        if (selectedMonthFilter != "ALL") {
            list = list.filter { entry ->
                val mKey = getEntryMonthKey(entry.dateString, entry.timestamp)
                val mFull = getEntryMonthFullTitle(entry.dateString, entry.timestamp)
                mKey.equals(selectedMonthFilter, ignoreCase = true) ||
                mFull.equals(selectedMonthFilter, ignoreCase = true) ||
                entry.dateString.contains(selectedMonthFilter, ignoreCase = true)
            }
        }
        if (specificDateFilter != null) {
            val filterMillis = parseEntryDateMillis(specificDateFilter!!)
            list = list.filter { entry ->
                entry.dateString.trim() == specificDateFilter!!.trim() ||
                (filterMillis > 0L && parseEntryDateMillis(entry.dateString) == filterMillis)
            }
        }
        when (sortDateMode) {
            0 -> list.sortedWith(
                compareBy<HisabLedgerEntryEntity> { getEntryCalendarMillis(it) }
                    .thenBy { it.timestamp }
                    .thenBy { it.id }
            ) // 1. DATEWISE (ASCENDING) - Same day entries are strictly adjacent
            1 -> list.sortedWith(
                compareByDescending<HisabLedgerEntryEntity> { getEntryCalendarMillis(it) }
                    .thenByDescending { it.timestamp }
                    .thenByDescending { it.id }
            ) // 2. DATEWISE (DESCENDING) - Same day entries are strictly adjacent
            2 -> list.sortedWith(
                compareByDescending<HisabLedgerEntryEntity> { if (it.timestamp > 0) it.timestamp else getEntryCalendarMillis(it) }
                    .thenByDescending { it.id }
            ) // 3. NEWEST FIRST
            3 -> list.sortedWith(
                compareBy<HisabLedgerEntryEntity> { if (it.timestamp > 0) it.timestamp else getEntryCalendarMillis(it) }
                    .thenBy { it.id }
            ) // 4. OLDEST FIRST
            else -> list.sortedWith(
                compareByDescending<HisabLedgerEntryEntity> { getEntryCalendarMillis(it) }
                    .thenByDescending { it.timestamp }
                    .thenByDescending { it.id }
            )
        }
    }

    val clipboardManager = LocalClipboardManager.current

    // Employee Share Link Generator (GitHub Pages Live Passbook - View-Only)
    fun getEmployeeShareLink(
        student: StudentEntity,
        monthFilter: String? = null,
        lockToEmployee: Boolean = true
    ): String {
        return buildEmployeePassbookLink(
            portalBaseUrl = portalBaseUrl,
            student = student,
            firebaseUrl = firebaseUrl,
            workspace = firebaseWorkspace,
            monthFilter = monthFilter,
            lockToEmployee = lockToEmployee
        )
    }

    // Passbook & Statement Formatter
    fun generateEmployeeShareText(
        student: StudentEntity,
        isWhatsApp: Boolean = false,
        includeSareeDetails: Boolean = false,
        periodMode: Int = 0, // 0 = Whole Month (Default), 1 = Custom Range, 2 = Complete / All-Time
        selectedMonth: String = SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(Date()),
        customFromDate: String = "01/" + SimpleDateFormat("MM/yyyy", Locale.ENGLISH).format(Date()),
        customToDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date()),
        lockToEmployee: Boolean = true
    ): String {
        val assignedClass = allClasses.find { it.id == student.classId }
        val shareLink = getEmployeeShareLink(
            student = student,
            monthFilter = if (periodMode == 0) selectedMonth else null,
            lockToEmployee = lockToEmployee
        )
        val sb = StringBuilder()
        val bold = if (isWhatsApp) "*" else ""
        val italic = if (isWhatsApp) "_" else ""

        val periodText = when (periodMode) {
            0 -> "Month: ${selectedMonth.uppercase()} (Whole Month)"
            1 -> "Date Range: $customFromDate to $customToDate"
            else -> "Complete / All-Time (Entire History)"
        }

        if (includeSareeDetails) {
            sb.append("📋 ${bold}STAFF DETAILED INCENTIVE STATEMENT${bold}\n")
            sb.append("${italic}(Detailed Saree Breakdown & Item Rates)${italic}\n")
        } else {
            sb.append("📋 ${bold}STAFF PASSBOOK & INCENTIVE STATEMENT${bold}\n")
            sb.append("${italic}(Summary Statement)${italic}\n")
        }
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("👤 ${bold}Staff:${bold} ${student.name} (Emp ID: ${student.rollNumber})\n")
        if (assignedClass != null) sb.append("🏢 ${bold}Department:${bold} ${assignedClass.name}\n")
        if (student.phone.isNotBlank()) sb.append("📞 ${bold}Phone:${bold} ${student.phone}\n")
        sb.append("📅 ${bold}Date Generated:${bold} ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())}\n")
        sb.append("⏳ ${bold}Timeframe:${bold} $periodText\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")

        // Filter ledger entries according to selected period
        val targetEntries = when (periodMode) {
            0 -> {
                // WHOLE MONTH
                ledgerEntries.filter { entry ->
                    val mKey = getEntryMonthKey(entry.dateString, entry.timestamp)
                    val mFull = getEntryMonthFullTitle(entry.dateString, entry.timestamp)
                    mKey.equals(selectedMonth, ignoreCase = true) ||
                    mFull.equals(selectedMonth, ignoreCase = true) ||
                    entry.dateString.contains(selectedMonth, ignoreCase = true)
                }
            }
            1 -> {
                // CUSTOM DATE RANGE
                val fromMillis = parseEntryDateMillis(customFromDate)
                val toMillis = parseEntryDateMillis(customToDate)
                val endInclusive = if (toMillis > 0L) toMillis + (24 * 60 * 60 * 1000L - 1) else Long.MAX_VALUE
                ledgerEntries.filter { entry ->
                    val time = getEntryCalendarMillis(entry)
                    if (fromMillis > 0L && toMillis > 0L) {
                        time in fromMillis..endInclusive
                    } else if (fromMillis > 0L) {
                        time >= fromMillis
                    } else if (toMillis > 0L) {
                        time <= endInclusive
                    } else {
                        true
                    }
                }
            }
            else -> {
                // COMPLETE / ALL-TIME
                ledgerEntries
            }
        }

        // Sort entries date-wise ascending (so month reads chronologically from day 1 to day 30/31)
        val sortedEntries = targetEntries.sortedWith(
            compareBy<HisabLedgerEntryEntity> { getEntryCalendarMillis(it) }
                .thenBy { it.timestamp }
                .thenBy { it.id }
        )

        val periodTotalEarned = targetEntries.filter { it.entryType == "SALES" }.sumOf { it.totalEarned }
        val periodTotalPieces = targetEntries.filter { it.entryType == "SALES" }.sumOf { it.totalPieces }
        val periodTotalAdvance = targetEntries.filter { it.entryType == "ADVANCE" }.sumOf { it.advancePaid }
        val periodNetBalance = periodTotalEarned - periodTotalAdvance

        sb.append("🟢 ${bold}TOTAL EARNED:${bold} ₹${periodTotalEarned.toInt()}\n")
        sb.append("🟠 ${bold}TOTAL SAREES/PCS SOLD:${bold} $periodTotalPieces Pcs\n")
        sb.append("🔵 ${bold}ADVANCE PAID / SETTLED:${bold} ₹${periodTotalAdvance.toInt()}\n")
        val balanceLabel = if (periodNetBalance >= 0) "NET PAYABLE TO STAFF" else "NET DUE FROM STAFF"
        sb.append("🔴 ${bold}$balanceLabel:${bold} ₹${kotlin.math.abs(periodNetBalance).toInt()}\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        if (shareLink.isNotBlank()) {
            sb.append("🔗 ${bold}LIVE VIEW-ONLY T-HISAB LINK:${bold}\n")
            sb.append("$shareLink\n")
            sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        }

        if (includeSareeDetails) {
            sb.append("📝 ${bold}DETAILED SAREE SALES & TRANSACTIONS:${bold}\n\n")
            if (sortedEntries.isEmpty()) {
                sb.append("No transactions logged for this period ($periodText).\n\n")
            } else {
                sortedEntries.forEach { entry ->
                    if (entry.entryType == "SALES") {
                        sb.append("🗓️ ${bold}${entry.dateString} - Sales: ${entry.totalPieces} Pcs (Earned: ₹${entry.totalEarned.toInt()})${bold}\n")
                        if (entry.itemBreakdownJson.isNotBlank()) {
                            try {
                                val arr = JSONArray(entry.itemBreakdownJson)
                                for (i in 0 until arr.length()) {
                                    val obj = arr.getJSONObject(i)
                                    val name = obj.optString("name", "Item")
                                    val qty = obj.optInt("qty", 0)
                                    val rate = obj.optDouble("rate", 0.0)
                                    val subtotal = (qty * rate).toInt()
                                    sb.append("   ▪️ $name: $qty pcs @ ₹${rate.toInt()} = ₹$subtotal\n")
                                }
                            } catch (_: Exception) {
                                sb.append("   ▪️ Saree Sales: ${entry.totalPieces} Pcs → ₹${entry.totalEarned.toInt()}\n")
                            }
                        }
                        if (entry.notes.isNotBlank()) {
                            sb.append("   📝 Note: ${entry.notes}\n")
                        }
                        sb.append("\n")
                    } else {
                        sb.append("💵 ${bold}${entry.dateString} - Cash Advance: ₹${entry.advancePaid.toInt()} (Settled)${bold}\n")
                        if (entry.notes.isNotBlank()) {
                            sb.append("   📝 Note: ${entry.notes}\n")
                        }
                        sb.append("\n")
                    }
                }
            }

            // Item-wise aggregate breakdown across this period
            val itemTotals = linkedMapOf<String, Pair<Int, Double>>()
            targetEntries.filter { it.entryType == "SALES" && it.itemBreakdownJson.isNotBlank() }.forEach { entry ->
                try {
                    val arr = JSONArray(entry.itemBreakdownJson)
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        val name = obj.optString("name", "Item").trim()
                        val qty = obj.optInt("qty", 0)
                        val rate = obj.optDouble("rate", 0.0)
                        val cur = itemTotals[name] ?: Pair(0, 0.0)
                        itemTotals[name] = Pair(cur.first + qty, cur.second + (qty * rate))
                    }
                } catch (_: Exception) {}
            }

            if (itemTotals.isNotEmpty()) {
                sb.append("━━━━━━━━━━━━━━━━━━━━\n")
                sb.append("🛍️ ${bold}ITEM-WISE TOTAL SUMMARY ($periodText):${bold}\n")
                itemTotals.forEach { (name, pair) ->
                    sb.append("• ${bold}$name:${bold} ${pair.first} Pcs → ₹${pair.second.toInt()}\n")
                }
            }
        } else {
            sb.append("📝 ${bold}STATEMENT TRANSACTIONS (SUMMARY):${bold}\n")
            if (sortedEntries.isEmpty()) {
                sb.append("No transactions logged for this period ($periodText).\n")
            } else {
                sortedEntries.forEach { entry ->
                    if (entry.entryType == "SALES") {
                        sb.append("• ${entry.dateString} - Sales: ${entry.totalPieces} Pcs → ₹${entry.totalEarned.toInt()}")
                        if (entry.notes.isNotBlank()) sb.append(" (${entry.notes})")
                        sb.append("\n")
                    } else {
                        sb.append("• ${entry.dateString} - Advance: ₹${entry.advancePaid.toInt()}")
                        if (entry.notes.isNotBlank()) sb.append(" (${entry.notes})")
                        sb.append("\n")
                    }
                }
            }
        }
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("Generated by Attendance & T-Ledger App")
        return sb.toString()
    }

    fun shareEmployee(
        student: StudentEntity,
        mode: Int,
        includeSareeDetails: Boolean = false,
        periodMode: Int = 0,
        selectedMonth: String = SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(Date()),
        customFromDate: String = "01/" + SimpleDateFormat("MM/yyyy", Locale.ENGLISH).format(Date()),
        customToDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date()),
        lockToEmployee: Boolean = true
    ) {
        // mode: 0 = WhatsApp, 1 = System Share Chooser, 2 = Copy Link Only, 3 = Copy Full Passbook Text
        if (mode == 0 || mode == 1 || mode == 2) {
            onAutoSyncForShare?.invoke()
        }
        val link = getEmployeeShareLink(
            student = student,
            monthFilter = if (periodMode == 0) selectedMonth else null,
            lockToEmployee = lockToEmployee
        )
        val fullText = generateEmployeeShareText(
            student = student,
            isWhatsApp = (mode == 0),
            includeSareeDetails = includeSareeDetails,
            periodMode = periodMode,
            selectedMonth = selectedMonth,
            customFromDate = customFromDate,
            customToDate = customToDate,
            lockToEmployee = lockToEmployee
        )
        when (mode) {
            0 -> {
                val phoneClean = student.phone.replace("[^0-9]".toRegex(), "")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = if (phoneClean.isNotBlank()) {
                        val phoneWithCountry = if (phoneClean.length == 10) "91$phoneClean" else phoneClean
                        Uri.parse("https://api.whatsapp.com/send?phone=$phoneWithCountry&text=${Uri.encode(fullText)}")
                    } else {
                        Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(fullText)}")
                    }
                }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, fullText)
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share Statement via WhatsApp"))
                }
            }
            1 -> {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, fullText)
                    putExtra(Intent.EXTRA_SUBJECT, "Statement: ${student.name}")
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share Employee Statement"))
            }
            2 -> {
                if (link.isBlank()) {
                    showShareEmployeeDialog = student
                    Toast.makeText(context, "Please set your GitHub Pages URL first", Toast.LENGTH_SHORT).show()
                } else {
                    clipboardManager.setText(AnnotatedString(link))
                    Toast.makeText(context, "Passbook link copied to clipboard!", Toast.LENGTH_SHORT).show()
                }
            }
            3 -> {
                clipboardManager.setText(AnnotatedString(fullText))
                Toast.makeText(context, if (includeSareeDetails) "Detailed statement copied to clipboard!" else "Summary statement copied to clipboard!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // WhatsApp Statement Formatter
    fun shareStatement(isWhatsApp: Boolean) {
        if (selectedStudent == null) {
            Toast.makeText(context, "Please select a staff member first", Toast.LENGTH_SHORT).show()
            return
        }
        showShareEmployeeDialog = selectedStudent
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Top Salesman Selector Row & Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E242B)), // Dark stylish card like video
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFFFFB74D),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SALESMAN / STAFF",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFB74D)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Button(
                                onClick = { showRearrangeEmployeesDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 7.dp, vertical = 3.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151)),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(Icons.Default.SwapVert, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFFFFB74D))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Rearrange", fontSize = 10.5.sp, color = Color.White)
                            }

                            Button(
                                onClick = onAddStudent,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151)),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Add", fontSize = 10.5.sp, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Salesmen Chips Carousel
                    if (allStudents.isEmpty()) {
                        Text(
                            text = "No staff/salesmen added yet. Tap '+ Add' to enroll staff members.",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(allStudents) { student ->
                                val isSelected = selectedStudent?.id == student.id
                                Surface(
                                    onClick = { onSelectStudent(student) },
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) Color(0xFF2563EB) else Color(0xFF2B3544),
                                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4758))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color.White.copy(alpha = 0.25f) else Color(0xFF4B5563)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = student.displayName.take(1).uppercase(),
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = student.displayName,
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Selected Salesman Profile Header
                    if (selectedStudent != null) {
                        val assignedClass = allClasses.find { it.id == selectedStudent.classId }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF28313D))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E40AF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = selectedStudent.name.take(1).uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = selectedStudent.name.uppercase(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "EMP ID: ${selectedStudent.rollNumber} ${if (assignedClass != null) "• ${assignedClass.name}" else ""}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF9CA3AF)
                                    )
                                    if (selectedStudent.phone.isNotBlank()) {
                                        Text(
                                            text = "Phone: ${selectedStudent.phone}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF60A5FA)
                                        )
                                    }
                                }
                            }

                            // Action Buttons (WhatsApp, Share Link & Dialog)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = { shareStatement(true) },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    contentPadding = PaddingValues(horizontal = 7.dp, vertical = 3.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "WhatsApp",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("WhatsApp", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Button(
                                    onClick = { showShareEmployeeDialog = selectedStudent },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                    contentPadding = PaddingValues(horizontal = 7.dp, vertical = 3.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = "Share Link",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Share Link", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // TIMEFRAME SELECTOR HEADER
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF131920))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = Color(0xFFA5B4FC),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "DATA TIMEFRAME",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFA5B4FC)
                                    )
                                }

                                // 3 Period Modes: COMPLETE / ALL, MONTH-WISE, CUSTOM RANGE
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1. Complete / All Time
                                    Surface(
                                        onClick = { dashboardPeriodMode = 0 },
                                        shape = RoundedCornerShape(5.dp),
                                        color = if (dashboardPeriodMode == 0) Color(0xFF4F46E5) else Color(0xFF232B36),
                                        border = if (dashboardPeriodMode == 0) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF818CF8)) else null
                                    ) {
                                        Text(
                                            text = "ALL",
                                            fontSize = 9.sp,
                                            fontWeight = if (dashboardPeriodMode == 0) FontWeight.Bold else FontWeight.Normal,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }

                                    // 2. Month-Wise
                                    Surface(
                                        onClick = {
                                            dashboardPeriodMode = 1
                                            if (availableMonths.isNotEmpty() && !availableMonths.contains(dashboardSelectedMonth)) {
                                                dashboardSelectedMonth = availableMonths.first()
                                            }
                                        },
                                        shape = RoundedCornerShape(5.dp),
                                        color = if (dashboardPeriodMode == 1) Color(0xFF10B981) else Color(0xFF232B36),
                                        border = if (dashboardPeriodMode == 1) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF34D399)) else null
                                    ) {
                                        Text(
                                            text = "MONTH",
                                            fontSize = 9.sp,
                                            fontWeight = if (dashboardPeriodMode == 1) FontWeight.Bold else FontWeight.Normal,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }

                                    // 3. Custom Range
                                    Surface(
                                        onClick = { dashboardPeriodMode = 2 },
                                        shape = RoundedCornerShape(5.dp),
                                        color = if (dashboardPeriodMode == 2) Color(0xFFD97706) else Color(0xFF232B36),
                                        border = if (dashboardPeriodMode == 2) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBBF24)) else null
                                    ) {
                                        Text(
                                            text = "CUSTOM",
                                            fontSize = 9.sp,
                                            fontWeight = if (dashboardPeriodMode == 2) FontWeight.Bold else FontWeight.Normal,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            // Sub-Row when MONTH-WISE is active
                            if (dashboardPeriodMode == 1) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("MONTH:", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                                    val monthsToDisplay = remember(availableMonths) {
                                        val cur = SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(Date())
                                        if (availableMonths.contains(cur)) availableMonths else listOf(cur) + availableMonths
                                    }
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(monthsToDisplay) { mKey ->
                                            val isSelected = dashboardSelectedMonth.equals(mKey, ignoreCase = true)
                                            Surface(
                                                onClick = { dashboardSelectedMonth = mKey },
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isSelected) Color(0xFF10B981) else Color(0xFF28313D),
                                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF34D399)) else null
                                            ) {
                                                Text(
                                                    text = mKey.uppercase(),
                                                    fontSize = 9.5.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Sub-Row when CUSTOM RANGE is active
                            if (dashboardPeriodMode == 2) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // From Date
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("FROM DATE", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Surface(
                                                onClick = { showDashboardFromDatePicker = true },
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF28313D),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.7f))
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 8.dp, vertical = 5.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = dashboardCustomFromDate,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                    Icon(
                                                        Icons.Default.CalendarToday,
                                                        contentDescription = null,
                                                        tint = Color(0xFFFBBF24),
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // To Date
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("TO DATE", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Surface(
                                                onClick = { showDashboardToDatePicker = true },
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF28313D),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.7f))
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 8.dp, vertical = 5.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = dashboardCustomToDate,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                    Icon(
                                                        Icons.Default.CalendarToday,
                                                        contentDescription = null,
                                                        tint = Color(0xFFFBBF24),
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Quick Range Presets
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())
                                        val calM1 = Calendar.getInstance()
                                        calM1.set(Calendar.DAY_OF_MONTH, 1)
                                        val firstDayThisMonth = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(calM1.time)

                                        val calPrev = Calendar.getInstance()
                                        calPrev.add(Calendar.MONTH, -1)
                                        calPrev.set(Calendar.DAY_OF_MONTH, 1)
                                        val firstDayLastMonth = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(calPrev.time)
                                        calPrev.set(Calendar.DAY_OF_MONTH, calPrev.getActualMaximum(Calendar.DAY_OF_MONTH))
                                        val lastDayLastMonth = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(calPrev.time)

                                        val cal30 = Calendar.getInstance()
                                        cal30.add(Calendar.DAY_OF_YEAR, -30)
                                        val thirtyDaysAgo = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(cal30.time)

                                        Surface(
                                            onClick = {
                                                dashboardCustomFromDate = firstDayThisMonth
                                                dashboardCustomToDate = todayStr
                                            },
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF28313D)
                                        ) {
                                            Text(
                                                "This Month",
                                                fontSize = 8.5.sp,
                                                color = Color(0xFF93C5FD),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Surface(
                                            onClick = {
                                                dashboardCustomFromDate = firstDayLastMonth
                                                dashboardCustomToDate = lastDayLastMonth
                                            },
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF28313D)
                                        ) {
                                            Text(
                                                "Last Month",
                                                fontSize = 8.5.sp,
                                                color = Color(0xFF93C5FD),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Surface(
                                            onClick = {
                                                dashboardCustomFromDate = thirtyDaysAgo
                                                dashboardCustomToDate = todayStr
                                            },
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF28313D)
                                        ) {
                                            Text(
                                                "Last 30 Days",
                                                fontSize = 8.5.sp,
                                                color = Color(0xFF93C5FD),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Surface(
                                            onClick = {
                                                dashboardCustomFromDate = todayStr
                                                dashboardCustomToDate = todayStr
                                            },
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF28313D)
                                        ) {
                                            Text(
                                                "Today",
                                                fontSize = 8.5.sp,
                                                color = Color(0xFF93C5FD),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Active Period Banner Indicator
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp),
                                color = when (dashboardPeriodMode) {
                                    1 -> Color(0xFF064E3B)
                                    2 -> Color(0xFF451A03)
                                    else -> Color(0xFF1E1B4B)
                                },
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when (dashboardPeriodMode) {
                                        1 -> Color(0xFF10B981)
                                        2 -> Color(0xFFD97706)
                                        else -> Color(0xFF6366F1)
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = when (dashboardPeriodMode) {
                                                1 -> Icons.Default.DateRange
                                                2 -> Icons.Default.CalendarToday
                                                else -> Icons.Default.DoneAll
                                            },
                                            contentDescription = null,
                                            tint = when (dashboardPeriodMode) {
                                                1 -> Color(0xFF34D399)
                                                2 -> Color(0xFFFBBF24)
                                                else -> Color(0xFFA5B4FC)
                                            },
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = when (dashboardPeriodMode) {
                                                1 -> "MONTH: ${dashboardSelectedMonth.uppercase()}"
                                                2 -> "CUSTOM: $dashboardCustomFromDate → $dashboardCustomToDate"
                                                else -> "COMPLETE (ALL DAYS)"
                                            },
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                    Text(
                                        text = "${dashboardEntries.size} transactions",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (dashboardPeriodMode) {
                                            1 -> Color(0xFFA7F3D0)
                                            2 -> Color(0xFFFDE68A)
                                            else -> Color(0xFFC7D2FE)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // 4 Key Metric Summary Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Total Earned
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B382B))
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                    Text("TOTAL EARNED", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF86EFAC))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("₹${dashboardTotalEarned.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4ADE80))
                                }
                            }

                            // Sarees Sold
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF382D1B))
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                    Text("SAREES SOLD", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE047))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("$dashboardTotalPieces Pcs", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFBBF24))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Paid / Settled
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                    Text("PAID / SETTLED", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("₹${dashboardTotalAdvance.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                            }

                            // Net Outstanding
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (dashboardNetOutstanding >= 0) Color(0xFF143026) else Color(0xFF3B1E1E)
                                )
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                    Text(
                                        text = if (dashboardNetOutstanding >= 0) "NET PAYABLE" else "NET DUE",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (dashboardNetOutstanding >= 0) Color(0xFF86EFAC) else Color(0xFFFCA5A5)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "₹${dashboardNetOutstanding.toInt()}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (dashboardNetOutstanding >= 0) Color(0xFF4ADE80) else Color(0xFFF87171)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. LOG SALES LEDGER RECORD SECTION
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LOG SALES LEDGER RECORD",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Mode Toggle: QUICK CLICKER GRID vs MANUAL FORM vs LOG ADVANCE
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            onClick = { inputMode = 0 },
                            shape = RoundedCornerShape(6.dp),
                            color = if (inputMode == 0) MaterialTheme.colorScheme.surface else Color.Transparent,
                            shadowElevation = if (inputMode == 0) 1.dp else 0.dp,
                            modifier = Modifier.weight(1.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 5.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("QUICK CLICKER", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            onClick = { inputMode = 1 },
                            shape = RoundedCornerShape(6.dp),
                            color = if (inputMode == 1) MaterialTheme.colorScheme.surface else Color.Transparent,
                            shadowElevation = if (inputMode == 1) 1.dp else 0.dp,
                            modifier = Modifier.weight(1.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 5.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("MANUAL FORM", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            onClick = { inputMode = 2 },
                            shape = RoundedCornerShape(6.dp),
                            color = if (inputMode == 2) Color(0xFFEA580C) else Color.Transparent,
                            shadowElevation = if (inputMode == 2) 1.dp else 0.dp,
                            modifier = Modifier.weight(1.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 5.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Payments,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = if (inputMode == 2) Color.White else Color(0xFFEA580C)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    "LOG ADVANCE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (inputMode == 2) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (inputMode == 0) {
                        // Category Header Card (Styled exactly as requested image)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF151D24)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF263342))
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Top Action Row: Dropdown Pill + Add Category + Delete Category
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1. Teal Dropdown Pill: [ ≡ CAT: $selectedCategoryFilter ($count) ▼ ]
                                    Box(modifier = Modifier.weight(1.4f)) {
                                        Surface(
                                            onClick = { categoryFilterDropdownExpanded = true },
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF13222B),
                                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00C7BE))
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 8.dp, vertical = 7.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Menu,
                                                        contentDescription = null,
                                                        tint = Color(0xFF00E5FF),
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "CAT: $selectedCategoryFilter (${if (selectedCategoryFilter == "ALL") allProducts.size else allProducts.count { it.category.equals(selectedCategoryFilter, ignoreCase = true) }})",
                                                        fontSize = 10.5.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color(0xFF00E5FF),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                                Icon(
                                                    Icons.Default.ArrowDropDown,
                                                    contentDescription = null,
                                                    tint = Color(0xFF00E5FF),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        DropdownMenu(
                                            expanded = categoryFilterDropdownExpanded,
                                            onDismissRequest = { categoryFilterDropdownExpanded = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("ALL (${allProducts.size})", fontWeight = FontWeight.Bold) },
                                                onClick = {
                                                    selectedCategoryFilter = "ALL"
                                                    categoryFilterDropdownExpanded = false
                                                }
                                            )
                                            allCategoryNames.forEach { cat ->
                                                val catCount = allProducts.count { it.category.equals(cat, ignoreCase = true) }
                                                DropdownMenuItem(
                                                    text = { Text("$cat ($catCount)") },
                                                    onClick = {
                                                        selectedCategoryFilter = cat
                                                        categoryFilterDropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    // 2. Amber/Gold "+ ADD" Button
                                    Surface(
                                        onClick = {
                                            newCategoryInputText = ""
                                            showAddNewCategoryDialog = true
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF2C2210),
                                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                                        modifier = Modifier.weight(0.9f)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 6.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = null,
                                                tint = Color(0xFFFBBF24),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "+ ADD",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFFFBBF24)
                                            )
                                        }
                                    }

                                    // 3. Red/Coral "DELETE CAT" Button (Opens Manage / Delete Categories dialog)
                                    Surface(
                                        onClick = { showManageCategoriesDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF2D1616),
                                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444)),
                                        modifier = Modifier.weight(1.1f)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 6.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = Color(0xFFF87171),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "DELETE CAT",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFFF87171)
                                            )
                                        }
                                    }
                                }

                                // Bottom Horizontal Scrollable Category Chips
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // ALL Chip
                                    item {
                                        val isSelected = selectedCategoryFilter == "ALL"
                                        Surface(
                                            onClick = { selectedCategoryFilter = "ALL" },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) Color(0xFF2B2516) else Color(0xFF1E2833),
                                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B)) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                                        ) {
                                            Text(
                                                text = "ALL",
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                                color = if (isSelected) Color(0xFFFBBF24) else Color(0xFF94A3B8),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }

                                    // Each Distinct Category Chip
                                    items(allCategoryNames) { cat ->
                                        val isSelected = selectedCategoryFilter.equals(cat, ignoreCase = true)
                                        Surface(
                                            onClick = { selectedCategoryFilter = cat },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) Color(0xFF2B2516) else Color(0xFF1E2833),
                                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B)) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                                        ) {
                                            Text(
                                                text = cat,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                                color = if (isSelected) Color(0xFFFBBF24) else Color(0xFF94A3B8),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Sort pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("SORT GRID:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            listOf("BY CATEGORY", "BY NAME", "MANUAL ORDER").forEachIndexed { index, title ->
                                Surface(
                                    onClick = { sortOption = index },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (sortOption == index) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 9.5.sp,
                                        fontWeight = if (sortOption == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (sortOption == index) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Products Clicker Grid (2-column layout filtered by displayedProducts)
                        if (displayedProducts.isEmpty()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF1E242B),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No items found under \"$selectedCategoryFilter\"",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "You can add a product in this category below using '+ ADD CUSTOM PRODUCT'.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF9CA3AF),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Render items in pairs (2 columns)
                                val chunked = displayedProducts.chunked(2)
                                chunked.forEach { rowItems ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowItems.forEach { product ->
                                            val count = clickerCounts[product.id] ?: 0
                                            Card(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        // Quick click increments count by 1
                                                        clickerCounts[product.id] = (clickerCounts[product.id] ?: 0) + 1
                                                    },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (count > 0) Color(0xFF1E2C3D) else Color(0xFF181F26)
                                                ),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (count > 0) Color(0xFF3B82F6) else Color(0xFF28313D)
                                                )
                                            ) {
                                                Column(modifier = Modifier.padding(8.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Surface(
                                                            shape = RoundedCornerShape(4.dp),
                                                            color = Color(0xFF374151)
                                                        ) {
                                                            Text(
                                                                text = product.category,
                                                                fontSize = 8.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF93C5FD),
                                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                            )
                                                        }

                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            // Edit Item Icon
                                                            IconButton(
                                                                onClick = {
                                                                    editingProduct = product
                                                                    editProductNameText = product.name
                                                                    editProductCategoryText = product.category
                                                                    editRateText = product.ratePerPiece.toInt().toString()
                                                                },
                                                                modifier = Modifier.size(22.dp)
                                                            ) {
                                                                Icon(
                                                                    Icons.Default.Edit,
                                                                    contentDescription = "Edit Item",
                                                                    tint = Color(0xFF9CA3AF),
                                                                    modifier = Modifier.size(13.dp)
                                                                )
                                                            }

                                                            // Delete Item Card Icon
                                                            IconButton(
                                                                onClick = {
                                                                    productToDelete = product
                                                                },
                                                                modifier = Modifier.size(22.dp)
                                                            ) {
                                                                Icon(
                                                                    Icons.Default.Delete,
                                                                    contentDescription = "Delete Item Card",
                                                                    tint = Color(0xFFEF4444),
                                                                    modifier = Modifier.size(13.dp)
                                                                )
                                                            }
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.height(4.dp))

                                                    Text(
                                                        text = product.name,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color.White,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )

                                                    Text(
                                                        text = "₹${product.ratePerPiece.toInt()}",
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF38BDF8)
                                                    )

                                                    Spacer(modifier = Modifier.height(6.dp))

                                                    // Stepper controls
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(Color(0xFF242E3B))
                                                            .padding(horizontal = 4.dp, vertical = 2.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        IconButton(
                                                            onClick = {
                                                                val curr = clickerCounts[product.id] ?: 0
                                                                if (curr > 0) clickerCounts[product.id] = curr - 1
                                                            },
                                                            modifier = Modifier.size(26.dp)
                                                        ) {
                                                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White, modifier = Modifier.size(14.dp))
                                                        }

                                                        Text(
                                                            text = "$count Pcs",
                                                            fontSize = 11.5.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = if (count > 0) Color(0xFFFBBF24) else Color(0xFF9CA3AF)
                                                        )

                                                        IconButton(
                                                            onClick = {
                                                                clickerCounts[product.id] = (clickerCounts[product.id] ?: 0) + 1
                                                            },
                                                            modifier = Modifier.size(26.dp)
                                                        ) {
                                                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White, modifier = Modifier.size(14.dp))
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Fill empty column if odd item count
                                        if (rowItems.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }

                            // Add Custom Saree / Item Box
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E242B)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "+ ADD CUSTOM SAREE/ITEM",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF60A5FA)
                                        )

                                        TextButton(
                                            onClick = { showManageCategoriesDialog = true },
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                            modifier = Modifier.height(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Category,
                                                contentDescription = null,
                                                modifier = Modifier.size(11.dp),
                                                tint = Color(0xFFFBBF24)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                "Manage Categories",
                                                fontSize = 9.5.sp,
                                                color = Color(0xFFFBBF24),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Input row: Item Name, Category Dropdown (in between), ₹/pc, and + ADD Button
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Block 1: Item Name
                                        OutlinedTextField(
                                            value = customItemName,
                                            onValueChange = { customItemName = it },
                                            placeholder = { Text("Item Name (e.g. SILK SAREE)", fontSize = 10.5.sp, color = Color.Gray) },
                                            modifier = Modifier.weight(1.3f),
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedBorderColor = Color(0xFF3B82F6),
                                                unfocusedBorderColor = Color(0xFF4B5563)
                                            )
                                        )

                                        // Block In-Between: Category Dropdown Selector
                                        Box(modifier = Modifier.weight(1.1f)) {
                                            OutlinedCard(
                                                onClick = { categoryDropdownExpanded = true },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(52.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF28313D)),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4B5563))
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(horizontal = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text("CATEGORY", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF93C5FD))
                                                        Text(
                                                            text = customItemCategory,
                                                            fontSize = 10.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowDropDown,
                                                        contentDescription = "Select Category",
                                                        tint = Color(0xFF93C5FD),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }

                                            DropdownMenu(
                                                expanded = categoryDropdownExpanded,
                                                onDismissRequest = { categoryDropdownExpanded = false },
                                                modifier = Modifier.background(Color(0xFF1F2937))
                                            ) {
                                                DropdownMenuItem(
                                                    text = {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(15.dp))
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text("+ Add New Category...", color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                                        }
                                                    },
                                                    onClick = {
                                                        categoryDropdownExpanded = false
                                                        newCategoryInputText = ""
                                                        showAddNewCategoryDialog = true
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(15.dp))
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text("⚙️ Manage Categories...", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                                        }
                                                    },
                                                    onClick = {
                                                        categoryDropdownExpanded = false
                                                        showManageCategoriesDialog = true
                                                    }
                                                )
                                                HorizontalDivider(color = Color(0xFF374151))
                                                allCategoryNames.forEach { cat ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text(
                                                                    text = cat,
                                                                    color = if (cat == customItemCategory) Color(0xFF60A5FA) else Color.White,
                                                                    fontWeight = if (cat == customItemCategory) FontWeight.Bold else FontWeight.Normal,
                                                                    fontSize = 11.5.sp
                                                                )
                                                                if (cat == customItemCategory) {
                                                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(14.dp))
                                                                }
                                                            }
                                                        },
                                                        onClick = {
                                                            customItemCategory = cat
                                                            categoryDropdownExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }

                                        // Block 2: ₹/pc
                                        OutlinedTextField(
                                            value = customItemRateText,
                                            onValueChange = { customItemRateText = it },
                                            placeholder = { Text("₹/pc", fontSize = 10.5.sp, color = Color.Gray) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(0.85f),
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedBorderColor = Color(0xFF3B82F6),
                                                unfocusedBorderColor = Color(0xFF4B5563)
                                            )
                                        )

                                        // + ADD Button
                                        Button(
                                            onClick = {
                                                val rate = customItemRateText.toDoubleOrNull()
                                                if (customItemName.isNotBlank() && rate != null && rate > 0) {
                                                    onAddProduct(customItemName, customItemCategory, rate)
                                                    customItemName = ""
                                                    customItemRateText = ""
                                                    Toast.makeText(context, "Added new product item!", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "Enter valid name and price", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                            contentPadding = PaddingValues(horizontal = 8.dp),
                                            modifier = Modifier.height(52.dp)
                                        ) {
                                            Text("+ ADD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }

                            // Restore defaults button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        onRestoreDefaultProducts()
                                        Toast.makeText(context, "Default products restored!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("RESTORE DEFAULT PRODUCTS", fontSize = 10.sp)
                                }
                            }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Live Added Products Preview (Cart Breakdown before logging to ledger)
                        val selectedProductsList = remember(clickerCounts.toMap(), allProducts) {
                            clickerCounts.entries
                                .filter { it.value > 0 }
                                .mapNotNull { entry ->
                                    val prod = allProducts.find { it.id == entry.key }
                                    if (prod != null) Triple(prod, entry.value, entry.value * prod.ratePerPiece) else null
                                }
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF161F2A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedProductsList.isNotEmpty()) Color(0xFF2563EB) else Color(0xFF2B3748))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.ShoppingCart,
                                            contentDescription = null,
                                            tint = if (selectedProductsList.isNotEmpty()) Color(0xFF60A5FA) else Color(0xFF94A3B8),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "ADDED PRODUCTS (${runningCalculation.first} Pcs • ₹${runningCalculation.second.toInt()})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (selectedProductsList.isNotEmpty()) Color(0xFF60A5FA) else Color(0xFF94A3B8)
                                        )
                                    }

                                    if (selectedProductsList.isNotEmpty()) {
                                        TextButton(
                                            onClick = { clickerCounts.clear() },
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                            modifier = Modifier.height(26.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = null,
                                                tint = Color(0xFFF87171),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Clear All",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFF87171)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                if (selectedProductsList.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF0F172A))
                                            .padding(horizontal = 10.dp, vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "👉 Tap '+' on product cards above to add items here",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        selectedProductsList.forEach { (prod, count, itemTotal) ->
                                            Surface(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFF1E293B),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    // Product Details (Name, Category, Rate breakdown)
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text(
                                                                text = prod.name,
                                                                fontSize = 12.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Surface(
                                                                shape = RoundedCornerShape(4.dp),
                                                                color = Color(0xFF374151)
                                                            ) {
                                                                Text(
                                                                    text = prod.category,
                                                                    fontSize = 8.5.sp,
                                                                    fontWeight = FontWeight.Medium,
                                                                    color = Color(0xFF93C5FD),
                                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                                )
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = "₹${prod.ratePerPiece.toInt()} × $count pcs  =  ₹${itemTotal.toInt()}",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = Color(0xFF38BDF8)
                                                        )
                                                    }

                                                    // Stepper Controls & Delete
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                    ) {
                                                        IconButton(
                                                            onClick = {
                                                                val curr = clickerCounts[prod.id] ?: 0
                                                                if (curr > 1) {
                                                                    clickerCounts[prod.id] = curr - 1
                                                                } else {
                                                                    clickerCounts.remove(prod.id)
                                                                }
                                                            },
                                                            modifier = Modifier.size(24.dp)
                                                        ) {
                                                            Icon(
                                                                Icons.Default.RemoveCircleOutline,
                                                                contentDescription = "Decrease",
                                                                tint = Color(0xFFFBBF24),
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }

                                                        Text(
                                                            text = "$count",
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = Color(0xFFFDE047),
                                                            modifier = Modifier.padding(horizontal = 2.dp)
                                                        )

                                                        IconButton(
                                                            onClick = {
                                                                clickerCounts[prod.id] = (clickerCounts[prod.id] ?: 0) + 1
                                                            },
                                                            modifier = Modifier.size(24.dp)
                                                        ) {
                                                            Icon(
                                                                Icons.Default.AddCircleOutline,
                                                                contentDescription = "Increase",
                                                                tint = Color(0xFF4ADE80),
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }

                                                        IconButton(
                                                            onClick = {
                                                                clickerCounts.remove(prod.id)
                                                            },
                                                            modifier = Modifier.size(24.dp)
                                                        ) {
                                                            Icon(
                                                                Icons.Default.DeleteOutline,
                                                                contentDescription = "Remove Item",
                                                                tint = Color(0xFFF87171),
                                                                modifier = Modifier.size(16.dp)
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

                        Spacer(modifier = Modifier.height(10.dp))

                        // Running Total & Log Date summary box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("LOG DATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        OutlinedButton(
                                            onClick = { showDatePickerDialog = true },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(logDate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("RUNNING TOTAL VALUE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "₹${runningCalculation.second.toInt()} (${runningCalculation.first} Pcs)",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = logNotes,
                                    onValueChange = { logNotes = it },
                                    placeholder = { Text("Add sheet note / counter note...", fontSize = 12.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        if (selectedStudent == null) {
                                            Toast.makeText(context, "Please select a staff member first", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        if (runningCalculation.first == 0) {
                                            Toast.makeText(context, "Please increase piece count for at least one item", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }

                                        // Build breakdown JSON
                                        val jsonArray = JSONArray()
                                        for ((name, qty, rate) in runningCalculation.third) {
                                            val obj = JSONObject()
                                            obj.put("name", name)
                                            obj.put("qty", qty)
                                            obj.put("rate", rate)
                                            jsonArray.put(obj)
                                        }

                                        onAddSalesEntry(
                                            selectedStudent.id,
                                            logDate,
                                            jsonArray.toString(),
                                            runningCalculation.first,
                                            runningCalculation.second,
                                            logNotes
                                        )

                                        // Reset clickers
                                        clickerCounts.clear()
                                        logNotes = ""
                                        Toast.makeText(context, "Added to Ledger: ₹${runningCalculation.second.toInt()} (${runningCalculation.first} Pcs)!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                                ) {
                                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ADD TO LEDGER", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    } else if (inputMode == 1) {
                        // MANUAL FORM MODE - allows typing any new saree name or selecting with live pre-existing suggestions
                        var manualProductName by remember { mutableStateOf("") }
                        var manualSelectedProduct by remember { mutableStateOf<HisabProductEntity?>(null) }
                        var manualQtyText by remember { mutableStateOf("") }
                        var manualRateText by remember { mutableStateOf("100") }
                        var isSuggestionsDropdownOpen by remember { mutableStateOf(false) }
                        var saveNewProductToCatalog by remember { mutableStateOf(true) }

                        val queryTrimmed = manualProductName.trim()
                        val matchingProducts = remember(queryTrimmed, allProducts) {
                            if (queryTrimmed.isEmpty()) {
                                allProducts
                            } else {
                                allProducts.filter {
                                    it.name.contains(queryTrimmed, ignoreCase = true) ||
                                    it.category.contains(queryTrimmed, ignoreCase = true)
                                }
                            }
                        }
                        val isExistingProduct = allProducts.any { it.name.equals(queryTrimmed, ignoreCase = true) }

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // 1. Saree / Product Name Input Field with Pre-Existing Suggestions
                            Column {
                                Text(
                                    "SAREE / PRODUCT NAME",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = manualProductName,
                                        onValueChange = { input ->
                                            manualProductName = input
                                            isSuggestionsDropdownOpen = true
                                            val match = allProducts.firstOrNull { it.name.equals(input.trim(), ignoreCase = true) }
                                            if (match != null) {
                                                manualSelectedProduct = match
                                                manualRateText = match.ratePerPiece.toInt().toString()
                                            }
                                        },
                                        label = { Text("Enter Saree Name (New or Existing)") },
                                        placeholder = { Text("Type new name (e.g. KANJIVARAM) or select...") },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.ShoppingCart,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        trailingIcon = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (manualProductName.isNotEmpty()) {
                                                    IconButton(
                                                        onClick = {
                                                            manualProductName = ""
                                                            manualSelectedProduct = null
                                                            isSuggestionsDropdownOpen = true
                                                        },
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                                IconButton(
                                                    onClick = { isSuggestionsDropdownOpen = !isSuggestionsDropdownOpen },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        if (isSuggestionsDropdownOpen) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                        contentDescription = "Show suggestions"
                                                    )
                                                }
                                            }
                                        },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("manual_saree_name_field")
                                    )

                                    DropdownMenu(
                                        expanded = isSuggestionsDropdownOpen && matchingProducts.isNotEmpty(),
                                        onDismissRequest = { isSuggestionsDropdownOpen = false },
                                        modifier = Modifier
                                            .fillMaxWidth(0.9f)
                                            .heightIn(max = 260.dp)
                                    ) {
                                        Text(
                                            text = if (queryTrimmed.isEmpty()) "PRE-EXISTING STORE PRODUCTS (${allProducts.size})" else "SUGGESTIONS MATCHING \"$queryTrimmed\" (${matchingProducts.size})",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                        Divider()
                                        matchingProducts.forEach { prod ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Column(modifier = Modifier.weight(1f)) {
                                                            Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                            Text("Category: ${prod.category}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                        }
                                                        Surface(
                                                            shape = RoundedCornerShape(6.dp),
                                                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                                                        ) {
                                                            Text(
                                                                "₹${prod.ratePerPiece.toInt()}",
                                                                color = Color(0xFF059669),
                                                                fontWeight = FontWeight.ExtraBold,
                                                                fontSize = 12.sp,
                                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                            )
                                                        }
                                                    }
                                                },
                                                onClick = {
                                                    manualProductName = prod.name
                                                    manualSelectedProduct = prod
                                                    manualRateText = prod.ratePerPiece.toInt().toString()
                                                    isSuggestionsDropdownOpen = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // If typing a brand-new saree name, show badge + catalog save toggle
                                if (queryTrimmed.isNotEmpty() && !isExistingProduct) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF3B82F6).copy(alpha = 0.10f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.AddCircle,
                                                    contentDescription = null,
                                                    tint = Color(0xFF2563EB),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    "New saree: \"${queryTrimmed.uppercase(Locale.getDefault())}\"",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1E40AF)
                                                )
                                            }
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.clickable { saveNewProductToCatalog = !saveNewProductToCatalog }
                                            ) {
                                                Checkbox(
                                                    checked = saveNewProductToCatalog,
                                                    onCheckedChange = { saveNewProductToCatalog = it },
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Save in Catalog", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurface)
                                            }
                                        }
                                    }
                                }

                                // Horizontal Quick Suggestion Chips below input
                                if (matchingProducts.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            if (queryTrimmed.isEmpty()) "PRE-EXISTING PRODUCTS:" else "SUGGESTIONS (${matchingProducts.size}):",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (matchingProducts.size > 5) {
                                            Text(
                                                "Scroll ➔",
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(matchingProducts) { p ->
                                            val isSelected = manualProductName.equals(p.name, ignoreCase = true)
                                            Surface(
                                                onClick = {
                                                    manualProductName = p.name
                                                    manualSelectedProduct = p
                                                    manualRateText = p.ratePerPiece.toInt().toString()
                                                    isSuggestionsDropdownOpen = false
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = p.name,
                                                        fontSize = 11.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "₹${p.ratePerPiece.toInt()}",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF10B981)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = manualQtyText,
                                    onValueChange = { manualQtyText = it },
                                    label = { Text("Saree Count (Qty)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = manualRateText,
                                    onValueChange = { manualRateText = it },
                                    label = { Text("Incentive / Piece (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            OutlinedTextField(
                                value = logNotes,
                                onValueChange = { logNotes = it },
                                placeholder = { Text("Notes / Counter sheet remarks...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    if (selectedStudent == null) {
                                        Toast.makeText(context, "Please select a staff member first", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    val enteredName = manualProductName.trim()
                                    if (enteredName.isBlank()) {
                                        Toast.makeText(context, "Please enter or select a saree / product name", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    val qty = manualQtyText.toIntOrNull() ?: 0
                                    val rate = manualRateText.toDoubleOrNull() ?: 0.0
                                    if (qty <= 0 || rate <= 0.0) {
                                        Toast.makeText(context, "Please enter valid quantity and rate", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    val finalProdName = enteredName.uppercase(Locale.getDefault())
                                    val totalEarnedVal = qty * rate

                                    // If this is a new saree name not yet in allProducts, save it to catalog if checked
                                    val existingProd = allProducts.firstOrNull { it.name.equals(finalProdName, ignoreCase = true) }
                                    if (existingProd == null && saveNewProductToCatalog) {
                                        onAddProduct(finalProdName, "SAREE", rate)
                                    }

                                    val jsonArray = JSONArray()
                                    val obj = JSONObject()
                                    obj.put("name", finalProdName)
                                    obj.put("qty", qty)
                                    obj.put("rate", rate)
                                    jsonArray.put(obj)

                                    onAddSalesEntry(
                                        selectedStudent.id,
                                        logDate,
                                        jsonArray.toString(),
                                        qty,
                                        totalEarnedVal,
                                        logNotes
                                    )

                                    manualQtyText = ""
                                    manualProductName = ""
                                    logNotes = ""
                                    Toast.makeText(context, "Logged $qty Pcs of $finalProdName @ ₹$rate = ₹${totalEarnedVal.toInt()}", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Icon(Icons.Default.AddCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ADD TO LEDGER", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (inputMode == 2) {
                        // LOG ADVANCE / CHAI / RETURN ITEMS MODE TAB
                        Column(modifier = Modifier.padding(top = 4.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Sub-mode pill selectors (Cash Advance, Chai/Thanda, Return Items)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1E242B), RoundedCornerShape(10.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    onClick = { advanceModeTab = 0 },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (advanceModeTab == 0) Color(0xFFEA580C) else Color.Transparent,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = if (advanceModeTab == 0) Color.White else Color(0xFF9CA3AF),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "CASH ADVANCE",
                                            fontSize = 10.5.sp,
                                            fontWeight = if (advanceModeTab == 0) FontWeight.Bold else FontWeight.Medium,
                                            color = if (advanceModeTab == 0) Color.White else Color(0xFF9CA3AF)
                                        )
                                    }
                                }

                                Surface(
                                    onClick = {
                                        advanceModeTab = 1
                                        if (chaiNotes.isBlank()) {
                                            chaiNotes = "[CHAI/THANDA] $chaiQtyText $chaiSelectedItem"
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (advanceModeTab == 1) Color(0xFFD97706) else Color.Transparent,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.EmojiFoodBeverage,
                                            contentDescription = null,
                                            tint = if (advanceModeTab == 1) Color.White else Color(0xFF9CA3AF),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "CHAI / THANDA",
                                            fontSize = 10.5.sp,
                                            fontWeight = if (advanceModeTab == 1) FontWeight.Bold else FontWeight.Medium,
                                            color = if (advanceModeTab == 1) Color.White else Color(0xFF9CA3AF)
                                        )
                                    }
                                }

                                Surface(
                                    onClick = {
                                        advanceModeTab = 2
                                        if (returnSelectedProduct == null && allProducts.isNotEmpty()) {
                                            returnSelectedProduct = allProducts.first()
                                            returnRateText = returnSelectedProduct?.ratePerPiece?.toInt()?.toString() ?: "100"
                                        }
                                        val sName = returnCustomProductName.ifBlank { returnSelectedProduct?.name ?: "Saree" }
                                        val qty = returnQtyText.toIntOrNull() ?: 1
                                        val rate = returnRateText.toDoubleOrNull() ?: 100.0
                                        val tot = (qty * rate).toInt()
                                        returnNotes = "[RETURN] $sName: $qty Pcs @ ₹${rate.toInt()} = ₹$tot Return Deduction"
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (advanceModeTab == 2) Color(0xFFE11D48) else Color.Transparent,
                                    modifier = Modifier.weight(1.1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.AssignmentReturn,
                                            contentDescription = null,
                                            tint = if (advanceModeTab == 2) Color.White else Color(0xFF9CA3AF),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "RETURN ITEMS",
                                            fontSize = 10.5.sp,
                                            fontWeight = if (advanceModeTab == 2) FontWeight.Bold else FontWeight.Medium,
                                            color = if (advanceModeTab == 2) Color.White else Color(0xFF9CA3AF)
                                        )
                                    }
                                }
                            }

                            // 1. CASH ADVANCE SUB-VIEW
                            if (advanceModeTab == 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("TRANSACTION DATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        OutlinedButton(
                                            onClick = { showDatePickerDialog = true },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp)
                                        ) {
                                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(advanceDate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1.3f)) {
                                        Text("ADVANCE AMOUNT (₹)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        OutlinedTextField(
                                            value = advanceAmountText,
                                            onValueChange = { advanceAmountText = it },
                                            placeholder = { Text("Enter amount ₹...", fontSize = 12.sp) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                    }
                                }

                                // Quick Amount Presets
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(100, 500, 1000, 2000, 5000).forEach { presetVal ->
                                        Surface(
                                            onClick = {
                                                val curr = advanceAmountText.toDoubleOrNull() ?: 0.0
                                                advanceAmountText = (curr + presetVal).toInt().toString()
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF2B3544),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4758)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "+₹$presetVal",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFB74D),
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 5.dp)
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = advanceNotes,
                                    onValueChange = { advanceNotes = it },
                                    placeholder = { Text("Notes / remarks (e.g. Cash advance, weekly kharcha)...", fontSize = 12.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(6.dp)
                                )

                                Button(
                                    onClick = {
                                        if (selectedStudent == null) {
                                            Toast.makeText(context, "Please select a staff member first", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        val amt = advanceAmountText.toDoubleOrNull() ?: 0.0
                                        if (amt <= 0.0) {
                                            Toast.makeText(context, "Please enter a valid advance amount", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }

                                        onAddAdvanceEntry(
                                            selectedStudent.id,
                                            advanceDate,
                                            amt,
                                            advanceNotes
                                        )

                                        advanceAmountText = ""
                                        advanceNotes = ""
                                        Toast.makeText(context, "Recorded cash advance: ₹${amt.toInt()}!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C))
                                ) {
                                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("RECORD CASH ADVANCE", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            // 2. CHAI / THANDA SUB-VIEW
                            if (advanceModeTab == 1) {
                                val refreshments = listOf(
                                    "Chai (Tea)" to 10.0,
                                    "Special Chai" to 15.0,
                                    "Cold Drink/Thanda" to 20.0,
                                    "Nashta/Snacks" to 30.0,
                                    "Samosa/Kachori" to 15.0,
                                    "Biscuits/Namkeen" to 20.0,
                                    "Custom Item" to 0.0
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // 1. Transaction Date & Item Selector Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("TRANSACTION DATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            OutlinedButton(
                                                onClick = { showDatePickerDialog = true },
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(44.dp)
                                            ) {
                                                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(advanceDate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Column(modifier = Modifier.weight(1.3f)) {
                                            Text("ITEM / REFRESHMENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            OutlinedTextField(
                                                value = chaiCustomItemName.ifBlank { chaiSelectedItem },
                                                onValueChange = {
                                                    chaiCustomItemName = it
                                                    val q = chaiQtyText.toIntOrNull() ?: 1
                                                    val r = chaiRateText.toDoubleOrNull() ?: 0.0
                                                    val total = (q * r).toInt()
                                                    chaiNotes = "[CHAI/THANDA] $q Pcs $it @ ₹${r.toInt()} = ₹$total"
                                                },
                                                placeholder = { Text("Item name (e.g. Chai, Samosa)...", fontSize = 12.sp) },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                        }
                                    }

                                    // Refreshment Item Chips
                                    Text("QUICK ITEM PRESETS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(refreshments) { (itemName, itemRate) ->
                                            val isSel = chaiSelectedItem == itemName && chaiCustomItemName.isBlank()
                                            Surface(
                                                onClick = {
                                                    chaiSelectedItem = itemName
                                                    chaiCustomItemName = if (itemName == "Custom Item") "" else itemName
                                                    if (itemRate > 0.0) {
                                                        chaiRateText = itemRate.toInt().toString()
                                                        val q = chaiQtyText.toIntOrNull() ?: 1
                                                        val total = (itemRate * q).toInt()
                                                        chaiCustomAmountText = total.toString()
                                                        chaiNotes = "[CHAI/THANDA] $q Pcs $itemName @ ₹${itemRate.toInt()} = ₹$total"
                                                    } else {
                                                        chaiNotes = "[CHAI/THANDA] $itemName"
                                                    }
                                                },
                                                shape = RoundedCornerShape(16.dp),
                                                color = if (isSel) Color(0xFFD97706) else Color(0xFF2B3544),
                                                border = if (isSel) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4758))
                                            ) {
                                                Text(
                                                    text = if (itemRate > 0.0) "$itemName (₹${itemRate.toInt()})" else itemName,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                                )
                                            }
                                        }
                                    }

                                    // 2. Quantity (Pcs / Cups) and Rate Per Unit Inputs
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Quantity / Pcs Column
                                        Column(modifier = Modifier.weight(1.2f)) {
                                            Text("QUANTITY / PCS / CUPS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                // Decrement button
                                                Surface(
                                                    onClick = {
                                                        val current = chaiQtyText.toIntOrNull() ?: 1
                                                        val newQty = (current - 1).coerceAtLeast(1)
                                                        chaiQtyText = newQty.toString()
                                                        val r = chaiRateText.toDoubleOrNull() ?: 0.0
                                                        val tot = (newQty * r).toInt()
                                                        chaiCustomAmountText = tot.toString()
                                                        val itemName = chaiCustomItemName.ifBlank { chaiSelectedItem }
                                                        chaiNotes = "[CHAI/THANDA] $newQty Pcs $itemName @ ₹${r.toInt()} = ₹$tot"
                                                    },
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFF2B3544),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4758)),
                                                    modifier = Modifier.size(44.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                    }
                                                }

                                                OutlinedTextField(
                                                    value = chaiQtyText,
                                                    onValueChange = {
                                                        chaiQtyText = it
                                                        val q = it.toIntOrNull() ?: 0
                                                        val r = chaiRateText.toDoubleOrNull() ?: 0.0
                                                        val tot = (q * r).toInt()
                                                        chaiCustomAmountText = tot.toString()
                                                        val itemName = chaiCustomItemName.ifBlank { chaiSelectedItem }
                                                        chaiNotes = "[CHAI/THANDA] $q Pcs $itemName @ ₹${r.toInt()} = ₹$tot"
                                                    },
                                                    placeholder = { Text("Qty...", fontSize = 12.sp) },
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    modifier = Modifier.weight(1f),
                                                    singleLine = true,
                                                    shape = RoundedCornerShape(6.dp)
                                                )

                                                // Increment button
                                                Surface(
                                                    onClick = {
                                                        val current = chaiQtyText.toIntOrNull() ?: 1
                                                        val newQty = current + 1
                                                        chaiQtyText = newQty.toString()
                                                        val r = chaiRateText.toDoubleOrNull() ?: 0.0
                                                        val tot = (newQty * r).toInt()
                                                        chaiCustomAmountText = tot.toString()
                                                        val itemName = chaiCustomItemName.ifBlank { chaiSelectedItem }
                                                        chaiNotes = "[CHAI/THANDA] $newQty Pcs $itemName @ ₹${r.toInt()} = ₹$tot"
                                                    },
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFFD97706),
                                                    modifier = Modifier.size(44.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                    }
                                                }
                                            }
                                        }

                                        // Rate Per Pc Column
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("RATE / PC (₹)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            OutlinedTextField(
                                                value = chaiRateText,
                                                onValueChange = {
                                                    chaiRateText = it
                                                    val q = chaiQtyText.toIntOrNull() ?: 1
                                                    val r = it.toDoubleOrNull() ?: 0.0
                                                    val tot = (q * r).toInt()
                                                    chaiCustomAmountText = tot.toString()
                                                    val itemName = chaiCustomItemName.ifBlank { chaiSelectedItem }
                                                    chaiNotes = "[CHAI/THANDA] $q Pcs $itemName @ ₹${r.toInt()} = ₹$tot"
                                                },
                                                placeholder = { Text("Rate ₹...", fontSize = 12.sp) },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                                singleLine = true,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                        }
                                    }

                                    // Quick Quantity Presets (Pcs / Cups)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Pcs:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        listOf(1, 2, 3, 4, 5, 10, 20).forEach { q ->
                                            val isQSel = (chaiQtyText.toIntOrNull() ?: 0) == q
                                            Surface(
                                                onClick = {
                                                    chaiQtyText = q.toString()
                                                    val r = chaiRateText.toDoubleOrNull() ?: 0.0
                                                    val total = (q * r).toInt()
                                                    chaiCustomAmountText = total.toString()
                                                    val itemName = chaiCustomItemName.ifBlank { chaiSelectedItem }
                                                    chaiNotes = "[CHAI/THANDA] $q Pcs $itemName @ ₹${r.toInt()} = ₹$total"
                                                },
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isQSel) Color(0xFFD97706) else Color(0xFF1E242B),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isQSel) Color(0xFFF59E0B) else Color(0xFF3B4758)),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = "$q",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isQSel) Color.White else Color(0xFF9CA3AF),
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.padding(vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }

                                    // 3. Live Total Calculation Banner & Direct Amount Field
                                    val currentQty = chaiQtyText.toIntOrNull() ?: 0
                                    val currentRate = chaiRateText.toDoubleOrNull() ?: 0.0
                                    val computedTotal = (currentQty * currentRate).toInt()

                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF261D12),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.5f))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "CALCULATED TOTAL",
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFFB74D)
                                                )
                                                Text(
                                                    text = "$currentQty Pcs × ₹${currentRate.toInt()}/pc",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFFE2E8F0)
                                                )
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "Total ₹:",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFFD54F)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                OutlinedTextField(
                                                    value = chaiCustomAmountText,
                                                    onValueChange = {
                                                        chaiCustomAmountText = it
                                                        val a = it.toDoubleOrNull() ?: 0.0
                                                        val itemName = chaiCustomItemName.ifBlank { chaiSelectedItem }
                                                        chaiNotes = "[CHAI/THANDA] $itemName: ₹${a.toInt()}"
                                                    },
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    modifier = Modifier.width(90.dp).height(42.dp),
                                                    singleLine = true,
                                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F)),
                                                    shape = RoundedCornerShape(6.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Quick Amount Chips
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(10, 20, 30, 50, 100, 150, 200).forEach { pVal ->
                                            Surface(
                                                onClick = {
                                                    chaiCustomAmountText = pVal.toString()
                                                    val itemName = chaiCustomItemName.ifBlank { chaiSelectedItem }
                                                    chaiNotes = "[CHAI/THANDA] $itemName: ₹$pVal"
                                                },
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF2B3544),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4758)),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = "₹$pVal",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFCD34D),
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.padding(vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = chaiNotes,
                                        onValueChange = { chaiNotes = it },
                                        placeholder = { Text("Chai / Thanda remarks or bill note...", fontSize = 12.sp) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(6.dp)
                                    )

                                    Button(
                                        onClick = {
                                            if (selectedStudent == null) {
                                                Toast.makeText(context, "Please select a staff member first", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            val amt = chaiCustomAmountText.toDoubleOrNull() ?: 0.0
                                            if (amt <= 0.0) {
                                                Toast.makeText(context, "Please enter a valid Chai/Thanda expense amount", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }

                                            val itemName = chaiCustomItemName.ifBlank { chaiSelectedItem }
                                            val q = chaiQtyText.toIntOrNull() ?: 1
                                            val r = chaiRateText.toDoubleOrNull() ?: 0.0
                                            val finalNotes = if (chaiNotes.isBlank()) "[CHAI/THANDA] $q Pcs $itemName @ ₹${r.toInt()} = ₹${amt.toInt()}" else chaiNotes

                                            onAddAdvanceEntry(
                                                selectedStudent.id,
                                                advanceDate,
                                                amt,
                                                finalNotes
                                            )

                                            chaiNotes = ""
                                            Toast.makeText(context, "Logged Chai/Thanda expense ($q Pcs): ₹${amt.toInt()}!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                                    ) {
                                        Icon(Icons.Default.EmojiFoodBeverage, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        val q = chaiQtyText.toIntOrNull() ?: 1
                                        Text("LOG CHAI / THANDA ($q PCS • ₹${chaiCustomAmountText.ifBlank { "0" }})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            // 3. RETURN ITEMS SUB-VIEW (Number of Sarees Returned & Per Piece Price)
                            if (advanceModeTab == 2) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("RETURN DATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            OutlinedButton(
                                                onClick = { showDatePickerDialog = true },
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(44.dp)
                                            ) {
                                                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(advanceDate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Column(modifier = Modifier.weight(1.3f)) {
                                            Text("SAREE / PRODUCT NAME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            OutlinedTextField(
                                                value = returnCustomProductName.ifBlank { returnSelectedProduct?.name ?: "Saree" },
                                                onValueChange = {
                                                    returnCustomProductName = it
                                                    val qty = returnQtyText.toIntOrNull() ?: 1
                                                    val rate = returnRateText.toDoubleOrNull() ?: 0.0
                                                    val tot = (qty * rate).toInt()
                                                    returnNotes = "[RETURN] $it: $qty Pcs @ ₹${rate.toInt()} = ₹$tot Return Deduction"
                                                },
                                                placeholder = { Text("Saree / Item name...", fontSize = 12.sp) },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                        }
                                    }

                                    // Quick Saree Product Chips
                                    Text("SELECT RETURNED SAREE / ITEM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(allProducts) { prod ->
                                            val isSel = returnSelectedProduct?.id == prod.id
                                            Surface(
                                                onClick = {
                                                    returnSelectedProduct = prod
                                                    returnCustomProductName = prod.name
                                                    returnRateText = prod.ratePerPiece.toInt().toString()
                                                    val qty = returnQtyText.toIntOrNull() ?: 1
                                                    val tot = (qty * prod.ratePerPiece).toInt()
                                                    returnNotes = "[RETURN] ${prod.name}: $qty Pcs @ ₹${prod.ratePerPiece.toInt()} = ₹$tot Return Deduction"
                                                },
                                                shape = RoundedCornerShape(16.dp),
                                                color = if (isSel) Color(0xFFE11D48) else Color(0xFF2B3544),
                                                border = if (isSel) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4758))
                                            ) {
                                                Text(
                                                    text = "${prod.name} (₹${prod.ratePerPiece.toInt()})",
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Number of Sarees Return & Per Piece Price Inputs
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("NO. OF SAREES RETURN (PCS)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            OutlinedTextField(
                                                value = returnQtyText,
                                                onValueChange = {
                                                    returnQtyText = it
                                                    val q = it.toIntOrNull() ?: 0
                                                    val r = returnRateText.toDoubleOrNull() ?: 0.0
                                                    val sName = returnCustomProductName.ifBlank { returnSelectedProduct?.name ?: "Saree" }
                                                    val tot = (q * r).toInt()
                                                    returnNotes = "[RETURN] $sName: $q Pcs @ ₹${r.toInt()} = ₹$tot Return Deduction"
                                                },
                                                placeholder = { Text("Qty...", fontSize = 12.sp) },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("PER PIECE PRICE (₹)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            OutlinedTextField(
                                                value = returnRateText,
                                                onValueChange = {
                                                    returnRateText = it
                                                    val q = returnQtyText.toIntOrNull() ?: 0
                                                    val r = it.toDoubleOrNull() ?: 0.0
                                                    val sName = returnCustomProductName.ifBlank { returnSelectedProduct?.name ?: "Saree" }
                                                    val tot = (q * r).toInt()
                                                    returnNotes = "[RETURN] $sName: $q Pcs @ ₹${r.toInt()} = ₹$tot Return Deduction"
                                                },
                                                placeholder = { Text("Price per pc ₹...", fontSize = 12.sp) },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                        }
                                    }

                                    // Quick Quantity Stepper Buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Quick Saree Count:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf(1, 2, 3, 5, 10).forEach { qVal ->
                                                val isCur = returnQtyText == qVal.toString()
                                                Surface(
                                                    onClick = {
                                                        returnQtyText = qVal.toString()
                                                        val r = returnRateText.toDoubleOrNull() ?: 0.0
                                                        val sName = returnCustomProductName.ifBlank { returnSelectedProduct?.name ?: "Saree" }
                                                        val tot = (qVal * r).toInt()
                                                        returnNotes = "[RETURN] $sName: $qVal Pcs @ ₹${r.toInt()} = ₹$tot Return Deduction"
                                                    },
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = if (isCur) Color(0xFFE11D48) else Color(0xFF1E242B),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isCur) Color(0xFFFB7185) else Color(0xFF3B4758))
                                                ) {
                                                    Text(
                                                        text = "$qVal Pc${if (qVal > 1) "s" else ""}",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isCur) Color.White else Color(0xFF9CA3AF),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Live Return Calculation Card
                                    val returnCalcQty = returnQtyText.toIntOrNull() ?: 0
                                    val returnCalcRate = returnRateText.toDoubleOrNull() ?: 0.0
                                    val returnTotalDeduction = returnCalcQty * returnCalcRate

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1920)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF9F1239)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "RETURN CALCULATION DEDUCTION",
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFDA4AF)
                                                )
                                                Text(
                                                    text = "$returnCalcQty Sarees × ₹${returnCalcRate.toInt()} / pc",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFFFECDD3)
                                                )
                                            }
                                            Text(
                                                text = "-₹${returnTotalDeduction.toInt()}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFFFB7185)
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = returnNotes,
                                        onValueChange = { returnNotes = it },
                                        placeholder = { Text("Return reason or customer bill remarks...", fontSize = 12.sp) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(6.dp)
                                    )

                                    Button(
                                        onClick = {
                                            if (selectedStudent == null) {
                                                Toast.makeText(context, "Please select a staff member first", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            if (returnCalcQty <= 0) {
                                                Toast.makeText(context, "Please enter number of sarees returned", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            if (returnCalcRate <= 0.0) {
                                                Toast.makeText(context, "Please enter valid per piece price", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }

                                            val sName = returnCustomProductName.ifBlank { returnSelectedProduct?.name ?: "Saree" }
                                            val finalNotes = if (returnNotes.isBlank()) {
                                                "[RETURN] $sName: $returnCalcQty Pcs @ ₹${returnCalcRate.toInt()} = ₹${returnTotalDeduction.toInt()} Return Deduction"
                                            } else {
                                                returnNotes
                                            }

                                            onAddAdvanceEntry(
                                                selectedStudent.id,
                                                advanceDate,
                                                returnTotalDeduction,
                                                finalNotes
                                            )

                                            returnQtyText = "1"
                                            returnNotes = ""
                                            Toast.makeText(context, "Recorded Return Deduction: -₹${returnTotalDeduction.toInt()} ($returnCalcQty Sarees @ ₹${returnCalcRate.toInt()})", Toast.LENGTH_LONG).show()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                                    ) {
                                        Icon(Icons.Default.AssignmentReturn, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("RECORD RETURN DEDUCTION (₹${returnTotalDeduction.toInt()})", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. INCENTIVES LEDGER STATEMENT HISTORY
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header Title
                    Text(
                        text = "INCENTIVES LEDGER STATEMENT HISTORY",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    // Action buttons in a horizontal scrolling row to prevent any overflow/blank areas
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            Button(
                                onClick = { shareStatement(true) },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("WhatsApp", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        item {
                            Button(
                                onClick = { showShareEmployeeDialog = selectedStudent },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Share Link", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        item {
                            Button(
                                onClick = {
                                    if (selectedStudent != null) {
                                        shareEmployee(selectedStudent, 2)
                                    } else {
                                        Toast.makeText(context, "Select a staff member first", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B5563)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Copy Link", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        item {
                            Button(
                                onClick = { shareStatement(false) },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Text Msg", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    // Row 1: VIEW Mode (Month-Wise, Date-Wise, Flat List)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("VIEW:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Surface(
                            onClick = { groupByMode = 0 },
                            shape = RoundedCornerShape(6.dp),
                            color = if (groupByMode == 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = if (groupByMode == 0) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Text(
                                text = "MONTH-WISE",
                                fontSize = 9.5.sp,
                                fontWeight = if (groupByMode == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (groupByMode == 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
                            )
                        }
                        Surface(
                            onClick = { groupByMode = 1 },
                            shape = RoundedCornerShape(6.dp),
                            color = if (groupByMode == 1) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = if (groupByMode == 1) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Text(
                                text = "DATE-WISE",
                                fontSize = 9.5.sp,
                                fontWeight = if (groupByMode == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (groupByMode == 1) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
                            )
                        }
                        Surface(
                            onClick = { groupByMode = 2 },
                            shape = RoundedCornerShape(6.dp),
                            color = if (groupByMode == 2) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = if (groupByMode == 2) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Text(
                                text = "FLAT LIST",
                                fontSize = 9.5.sp,
                                fontWeight = if (groupByMode == 2) FontWeight.Bold else FontWeight.Normal,
                                color = if (groupByMode == 2) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
                            )
                        }
                    }

                    // Row 2: MONTH FILTER CHIPS (Quickly view only August, September, etc.)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("MONTH:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                val isSelected = selectedMonthFilter == "ALL"
                                Surface(
                                    onClick = { selectedMonthFilter = "ALL" },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) Color(0xFF6366F1) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF818CF8)) else null
                                ) {
                                    Text(
                                        text = "ALL MONTHS",
                                        fontSize = 9.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
                                    )
                                }
                            }
                            items(availableMonths) { mKey ->
                                val isSelected = selectedMonthFilter.equals(mKey, ignoreCase = true)
                                Surface(
                                    onClick = { selectedMonthFilter = if (isSelected) "ALL" else mKey },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF34D399)) else null
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
                                    ) {
                                        Text(
                                            text = mKey.uppercase(),
                                            fontSize = 9.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Clear month filter",
                                                modifier = Modifier.size(11.dp),
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Row 3: SORT Options (All 4 options requested by user)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("SORT BY:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val sortItems = listOf(
                                0 to "1. DATEWISE (ASCENDING)",
                                1 to "2. DATEWISE (DESCENDING)",
                                2 to "3. NEWEST FIRST",
                                3 to "4. OLDEST FIRST"
                            )
                            items(sortItems) { (mode, label) ->
                                val isSelected = sortDateMode == mode
                                Surface(
                                    onClick = { sortDateMode = mode },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) Color(0xFFF59E0B) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD97706)) else null
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 9.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Row 4: Filtering Controls (Type, Specific Date)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Type Filter Chips
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("TYPE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Surface(
                                onClick = { filterTypeMode = 0 },
                                shape = RoundedCornerShape(6.dp),
                                color = if (filterTypeMode == 0) Color(0xFF3B82F6) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ) {
                                Text(
                                    text = "ALL",
                                    fontSize = 9.sp,
                                    fontWeight = if (filterTypeMode == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (filterTypeMode == 0) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                onClick = { filterTypeMode = 1 },
                                shape = RoundedCornerShape(6.dp),
                                color = if (filterTypeMode == 1) Color(0xFF10B981) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ) {
                                Text(
                                    text = "SALES ONLY",
                                    fontSize = 9.sp,
                                    fontWeight = if (filterTypeMode == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (filterTypeMode == 1) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                onClick = { filterTypeMode = 2 },
                                shape = RoundedCornerShape(6.dp),
                                color = if (filterTypeMode == 2) Color(0xFFEF4444) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ) {
                                Text(
                                    text = "ADVANCE ONLY",
                                    fontSize = 9.sp,
                                    fontWeight = if (filterTypeMode == 2) FontWeight.Bold else FontWeight.Normal,
                                    color = if (filterTypeMode == 2) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Specific Date Filter Button / Clear
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (specificDateFilter != null) {
                                Surface(
                                    onClick = { specificDateFilter = null },
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEF4444).copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$specificDateFilter ✕",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFEF4444)
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    onClick = { showFilterDatePickerDialog = true },
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(10.dp), tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("FILTER DATE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }

                    // Active Month Filter Banner Indicator
                    if (selectedMonthFilter != "ALL") {
                        val filterSales = filteredEntries.filter { it.entryType == "SALES" }.sumOf { it.totalEarned }
                        val filterPcs = filteredEntries.filter { it.entryType == "SALES" }.sumOf { it.totalPieces }
                        val filterAdv = filteredEntries.filter { it.entryType == "ADVANCE" }.sumOf { it.advancePaid }
                        val filterNet = filterSales - filterAdv
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF064E3B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FilterList, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ONLY ${selectedMonthFilter.uppercase()}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF047857)) {
                                        Text(
                                            text = "${filteredEntries.size} entries",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFA7F3D0),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (filterPcs > 0) {
                                        Text(text = "$filterPcs Pcs", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                                    }
                                    Text(text = "+₹${filterSales.toInt()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80))
                                    if (filterAdv > 0) {
                                        Text(text = "-₹${filterAdv.toInt()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF87171))
                                    }
                                    Text(text = "Net: ₹${filterNet.toInt()}", fontSize = 10.5.sp, fontWeight = FontWeight.ExtraBold, color = if (filterNet >= 0) Color(0xFF4ADE80) else Color(0xFFF87171))
                                    IconButton(
                                        onClick = { selectedMonthFilter = "ALL" },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear month filter", tint = Color.White, modifier = Modifier.size(13.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Ledger Statement Table Header
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E242B)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("CATEGORY / DETAILS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF), modifier = Modifier.weight(1.8f))
                            Text("SAREES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF), textAlign = TextAlign.Center, modifier = Modifier.weight(0.8f))
                            Text("ADVANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF), textAlign = TextAlign.Center, modifier = Modifier.weight(0.8f))
                            Text("NET (₹)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF), textAlign = TextAlign.End, modifier = Modifier.weight(0.9f))
                            Text("ACTION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF), textAlign = TextAlign.Center, modifier = Modifier.weight(1.0f))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Entries List
                    if (filteredEntries.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (specificDateFilter != null)
                                    "No entries found for date: $specificDateFilter"
                                else if (selectedMonthFilter != "ALL")
                                    "No entries found for $selectedMonthFilter."
                                else
                                    "No transactions logged for ${selectedStudent?.name ?: "this staff member"}.\nUse the clicker grid above to log sarees/items or record advances.",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        if (groupByMode == 0) {
                            // MONTH-WISE GROUPED VIEW
                            val groupedByMonth = remember(filteredEntries) {
                                val map = linkedMapOf<String, MutableList<HisabLedgerEntryEntity>>()
                                for (entry in filteredEntries) {
                                    val key = getEntryMonthFullTitle(entry.dateString, entry.timestamp)
                                    map.getOrPut(key) { mutableListOf() }.add(entry)
                                }
                                map
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                groupedByMonth.forEach { (monthTitle, entriesInMonth) ->
                                    val monthSalesTotal = entriesInMonth.filter { it.entryType == "SALES" }.sumOf { it.totalEarned }
                                    val monthPcsTotal = entriesInMonth.filter { it.entryType == "SALES" }.sumOf { it.totalPieces }
                                    val monthAdvanceTotal = entriesInMonth.filter { it.entryType == "ADVANCE" }.sumOf { it.advancePaid }
                                    val monthNet = monthSalesTotal - monthAdvanceTotal

                                    // Month Group Header Banner
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF1E1B4B),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.7f))
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 5.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFFA5B4FC), modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(text = monthTitle.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF3730A3)) {
                                                    Text(text = "${entriesInMonth.size} entries", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC7D2FE), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                                if (monthPcsTotal > 0) {
                                                    Text(text = "$monthPcsTotal Pcs", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                                                }
                                                if (monthSalesTotal > 0) {
                                                    Text(text = "+₹${monthSalesTotal.toInt()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80))
                                                }
                                                if (monthAdvanceTotal > 0) {
                                                    Text(text = "-₹${monthAdvanceTotal.toInt()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF87171))
                                                }
                                                Text(text = "Net: ₹${monthNet.toInt()}", fontSize = 10.5.sp, fontWeight = FontWeight.ExtraBold, color = if (monthNet >= 0) Color(0xFF4ADE80) else Color(0xFFF87171))
                                            }
                                        }
                                    }

                                    // Entry cards for this month
                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        entriesInMonth.forEach { entry ->
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(6.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (entry.entryType == "ADVANCE") Color(0xFF2B1D1D) else Color(0xFF1E242B)
                                                ),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (entry.entryType == "ADVANCE") Color(0xFF7C2D12) else Color(0xFF28313D)
                                                )
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 8.dp, vertical = 5.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                // Category / details
                                                Column(modifier = Modifier.weight(2f)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        val isChai = entry.notes.contains("[CHAI/THANDA]")
                                                        val isReturn = entry.notes.contains("[RETURN]")
                                                        val badgeTag = when {
                                                            isChai -> "CHAI/THANDA"
                                                            isReturn -> "RETURN"
                                                            entry.entryType == "ADVANCE" -> "ADVANCE"
                                                            else -> entry.entryType
                                                        }
                                                        val badgeBg = when {
                                                            isChai -> Color(0xFFD97706)
                                                            isReturn -> Color(0xFFE11D48)
                                                            entry.entryType == "ADVANCE" -> Color(0xFFEA580C)
                                                            else -> Color(0xFF2563EB)
                                                        }
                                                        Surface(
                                                            shape = RoundedCornerShape(4.dp),
                                                            color = badgeBg
                                                        ) {
                                                            Text(
                                                                text = badgeTag,
                                                                fontSize = 8.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White,
                                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = entry.dateString,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.height(2.dp))

                                                    // Breakdown items if sales
                                                    val breakdownItems = remember(entry.itemBreakdownJson) {
                                                        val list = mutableListOf<String>()
                                                        if (entry.entryType == "SALES" && entry.itemBreakdownJson.isNotBlank()) {
                                                            try {
                                                                val arr = JSONArray(entry.itemBreakdownJson)
                                                                for (i in 0 until arr.length()) {
                                                                    val obj = arr.getJSONObject(i)
                                                                    list.add("• ${obj.optString("name")}: ${obj.optInt("qty")} pcs @ ₹${obj.optDouble("rate").toInt()}")
                                                                }
                                                            } catch (_: Exception) {
                                                            }
                                                        }
                                                        list
                                                    }

                                                    breakdownItems.forEach { itemText ->
                                                        Text(
                                                            text = itemText,
                                                            fontSize = 10.sp,
                                                            color = Color(0xFF93C5FD)
                                                        )
                                                    }

                                                    if (entry.notes.isNotBlank()) {
                                                        Text(
                                                            text = "Note: ${entry.notes}",
                                                            fontSize = 9.5.sp,
                                                            color = Color(0xFF9CA3AF)
                                                        )
                                                    }
                                                }

                                                // Sarees count
                                                Text(
                                                    text = if (entry.entryType == "SALES") "${entry.totalPieces} Pcs" else "-",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (entry.entryType == "SALES") Color(0xFFFBBF24) else Color(0xFF6B7280),
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.weight(0.8f)
                                                )

                                                // Advance
                                                Text(
                                                    text = if (entry.entryType == "ADVANCE") "₹${entry.advancePaid.toInt()}" else "-",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (entry.entryType == "ADVANCE") Color(0xFFF87171) else Color(0xFF6B7280),
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.weight(0.8f)
                                                )

                                                // Net Earned
                                                Text(
                                                    text = if (entry.entryType == "SALES") "+₹${entry.totalEarned.toInt()}" else "-₹${entry.advancePaid.toInt()}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = if (entry.entryType == "SALES") Color(0xFF4ADE80) else Color(0xFFEF4444),
                                                    textAlign = TextAlign.End,
                                                    modifier = Modifier.weight(0.9f)
                                                )

                                                // Action Column: Edit & Delete
                                                Row(
                                                    modifier = Modifier.weight(1.0f),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    IconButton(
                                                        onClick = { editingEntry = entry },
                                                        modifier = Modifier.size(26.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Edit,
                                                            contentDescription = "Edit Entry",
                                                            tint = Color(0xFF60A5FA),
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    IconButton(
                                                        onClick = { entryToDelete = entry },
                                                        modifier = Modifier.size(26.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Delete,
                                                            contentDescription = "Delete Entry",
                                                            tint = Color(0xFFEF4444),
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    }
                                }
                            }
                        } else if (groupByMode == 1) {
                            // DATE-WISE GROUPED VIEW
                            val groupedByDate = remember(filteredEntries) {
                                val map = linkedMapOf<String, MutableList<HisabLedgerEntryEntity>>()
                                for (entry in filteredEntries) {
                                    val key = entry.dateString.ifBlank { "Unspecified Date" }
                                    map.getOrPut(key) { mutableListOf() }.add(entry)
                                }
                                map
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                groupedByDate.forEach { (dateKey, entriesOnDate) ->
                                    val daySalesTotal = entriesOnDate.filter { it.entryType == "SALES" }.sumOf { it.totalEarned }
                                    val dayPcsTotal = entriesOnDate.filter { it.entryType == "SALES" }.sumOf { it.totalPieces }
                                    val dayAdvanceTotal = entriesOnDate.filter { it.entryType == "ADVANCE" }.sumOf { it.advancePaid }
                                    val dayNet = daySalesTotal - dayAdvanceTotal

                                    // Date Group Header Banner
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF131B26),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.5f))
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 5.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(text = dateKey, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF1E3A8A)) {
                                                    Text(text = "${entriesOnDate.size} entries", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF93C5FD), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                                if (dayPcsTotal > 0) {
                                                    Text(text = "$dayPcsTotal Pcs", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                                                }
                                                if (daySalesTotal > 0) {
                                                    Text(text = "+₹${daySalesTotal.toInt()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80))
                                                }
                                                if (dayAdvanceTotal > 0) {
                                                    Text(text = "-₹${dayAdvanceTotal.toInt()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF87171))
                                                }
                                                Text(text = "Net: ₹${dayNet.toInt()}", fontSize = 10.5.sp, fontWeight = FontWeight.ExtraBold, color = if (dayNet >= 0) Color(0xFF4ADE80) else Color(0xFFF87171))
                                            }
                                        }
                                    }

                                    // Entry cards for this date
                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        entriesOnDate.forEach { entry ->
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(6.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (entry.entryType == "ADVANCE") Color(0xFF2B1D1D) else Color(0xFF1E242B)
                                                ),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (entry.entryType == "ADVANCE") Color(0xFF7C2D12) else Color(0xFF28313D)
                                                )
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 8.dp, vertical = 5.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                // Category / details
                                                Column(modifier = Modifier.weight(2f)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        val isChai = entry.notes.contains("[CHAI/THANDA]")
                                                        val isReturn = entry.notes.contains("[RETURN]")
                                                        val badgeTag = when {
                                                            isChai -> "CHAI/THANDA"
                                                            isReturn -> "RETURN"
                                                            entry.entryType == "ADVANCE" -> "ADVANCE"
                                                            else -> entry.entryType
                                                        }
                                                        val badgeBg = when {
                                                            isChai -> Color(0xFFD97706)
                                                            isReturn -> Color(0xFFE11D48)
                                                            entry.entryType == "ADVANCE" -> Color(0xFFEA580C)
                                                            else -> Color(0xFF2563EB)
                                                        }
                                                        Surface(
                                                            shape = RoundedCornerShape(4.dp),
                                                            color = badgeBg
                                                        ) {
                                                            Text(
                                                                text = badgeTag,
                                                                fontSize = 8.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White,
                                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = entry.dateString,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.height(2.dp))

                                                    // Breakdown items if sales
                                                    val breakdownItems = remember(entry.itemBreakdownJson) {
                                                        val list = mutableListOf<String>()
                                                        if (entry.entryType == "SALES" && entry.itemBreakdownJson.isNotBlank()) {
                                                            try {
                                                                val arr = JSONArray(entry.itemBreakdownJson)
                                                                for (i in 0 until arr.length()) {
                                                                    val obj = arr.getJSONObject(i)
                                                                    list.add("• ${obj.optString("name")}: ${obj.optInt("qty")} pcs @ ₹${obj.optDouble("rate").toInt()}")
                                                                }
                                                            } catch (_: Exception) {
                                                            }
                                                        }
                                                        list
                                                    }

                                                    breakdownItems.forEach { itemText ->
                                                        Text(
                                                            text = itemText,
                                                            fontSize = 10.sp,
                                                            color = Color(0xFF93C5FD)
                                                        )
                                                    }

                                                    if (entry.notes.isNotBlank()) {
                                                        Text(
                                                            text = "Note: ${entry.notes}",
                                                            fontSize = 9.5.sp,
                                                            color = Color(0xFF9CA3AF)
                                                        )
                                                    }
                                                }

                                                // Sarees count
                                                Text(
                                                    text = if (entry.entryType == "SALES") "${entry.totalPieces} Pcs" else "-",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (entry.entryType == "SALES") Color(0xFFFBBF24) else Color(0xFF6B7280),
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.weight(0.8f)
                                                )

                                                // Advance
                                                Text(
                                                    text = if (entry.entryType == "ADVANCE") "₹${entry.advancePaid.toInt()}" else "-",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (entry.entryType == "ADVANCE") Color(0xFFF87171) else Color(0xFF6B7280),
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.weight(0.8f)
                                                )

                                                // Net Earned
                                                Text(
                                                    text = if (entry.entryType == "SALES") "+₹${entry.totalEarned.toInt()}" else "-₹${entry.advancePaid.toInt()}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = if (entry.entryType == "SALES") Color(0xFF4ADE80) else Color(0xFFEF4444),
                                                    textAlign = TextAlign.End,
                                                    modifier = Modifier.weight(0.9f)
                                                )

                                                // Action Column: Edit & Delete
                                                Row(
                                                    modifier = Modifier.weight(1.0f),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    IconButton(
                                                        onClick = { editingEntry = entry },
                                                        modifier = Modifier.size(26.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Edit,
                                                            contentDescription = "Edit Entry",
                                                            tint = Color(0xFF60A5FA),
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    IconButton(
                                                        onClick = { entryToDelete = entry },
                                                        modifier = Modifier.size(26.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Delete,
                                                            contentDescription = "Delete Entry",
                                                            tint = Color(0xFFEF4444),
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    }
                                }
                            }
                        } else {
                            // FLAT LIST VIEW
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                filteredEntries.forEach { entry ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (entry.entryType == "ADVANCE") Color(0xFF2B1D1D) else Color(0xFF1E242B)
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (entry.entryType == "ADVANCE") Color(0xFF7C2D12) else Color(0xFF28313D)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // Category / details
                                            Column(modifier = Modifier.weight(2f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    val isChai = entry.notes.contains("[CHAI/THANDA]")
                                                    val isReturn = entry.notes.contains("[RETURN]")
                                                    val badgeTag = when {
                                                        isChai -> "CHAI/THANDA"
                                                        isReturn -> "RETURN"
                                                        entry.entryType == "ADVANCE" -> "ADVANCE"
                                                        else -> entry.entryType
                                                    }
                                                    val badgeBg = when {
                                                        isChai -> Color(0xFFD97706)
                                                        isReturn -> Color(0xFFE11D48)
                                                        entry.entryType == "ADVANCE" -> Color(0xFFEA580C)
                                                        else -> Color(0xFF2563EB)
                                                    }
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = badgeBg
                                                    ) {
                                                        Text(
                                                            text = badgeTag,
                                                            fontSize = 8.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White,
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = entry.dateString,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(2.dp))

                                                // Breakdown items if sales
                                                val breakdownItems = remember(entry.itemBreakdownJson) {
                                                    val list = mutableListOf<String>()
                                                    if (entry.entryType == "SALES" && entry.itemBreakdownJson.isNotBlank()) {
                                                        try {
                                                            val arr = JSONArray(entry.itemBreakdownJson)
                                                            for (i in 0 until arr.length()) {
                                                                val obj = arr.getJSONObject(i)
                                                                list.add("• ${obj.optString("name")}: ${obj.optInt("qty")} pcs @ ₹${obj.optDouble("rate").toInt()}")
                                                            }
                                                        } catch (_: Exception) {
                                                        }
                                                    }
                                                    list
                                                }

                                                breakdownItems.forEach { itemText ->
                                                    Text(
                                                        text = itemText,
                                                        fontSize = 10.sp,
                                                        color = Color(0xFF93C5FD)
                                                    )
                                                }

                                                if (entry.notes.isNotBlank()) {
                                                    Text(
                                                        text = "Note: ${entry.notes}",
                                                        fontSize = 9.5.sp,
                                                        color = Color(0xFF9CA3AF)
                                                    )
                                                }
                                            }

                                            // Sarees count
                                            Text(
                                                text = if (entry.entryType == "SALES") "${entry.totalPieces} Pcs" else "-",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (entry.entryType == "SALES") Color(0xFFFBBF24) else Color(0xFF6B7280),
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.weight(0.8f)
                                            )

                                            // Advance
                                            Text(
                                                text = if (entry.entryType == "ADVANCE") "₹${entry.advancePaid.toInt()}" else "-",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (entry.entryType == "ADVANCE") Color(0xFFF87171) else Color(0xFF6B7280),
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.weight(0.8f)
                                            )

                                            // Net Earned
                                            Text(
                                                text = if (entry.entryType == "SALES") "+₹${entry.totalEarned.toInt()}" else "-₹${entry.advancePaid.toInt()}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (entry.entryType == "SALES") Color(0xFF4ADE80) else Color(0xFFEF4444),
                                                textAlign = TextAlign.End,
                                                modifier = Modifier.weight(0.9f)
                                            )

                                            // Action Column: Edit & Delete
                                            Row(
                                                modifier = Modifier.weight(1.0f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                IconButton(
                                                    onClick = { editingEntry = entry },
                                                    modifier = Modifier.size(26.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = "Edit Entry",
                                                        tint = Color(0xFF60A5FA),
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(2.dp))
                                                IconButton(
                                                    onClick = { entryToDelete = entry },
                                                    modifier = Modifier.size(26.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete Entry",
                                                        tint = Color(0xFFEF4444),
                                                        modifier = Modifier.size(15.dp)
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

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Dialog: Edit Product Details & Price & Option to Delete
    editingProduct?.let { product ->
        AlertDialog(
            onDismissRequest = { editingProduct = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Edit Item Card", fontWeight = FontWeight.Bold)
                    IconButton(
                        onClick = {
                            val prod = product
                            editingProduct = null
                            productToDelete = prod
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Product", tint = MaterialTheme.colorScheme.error)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Item Name:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = editProductNameText,
                        onValueChange = { editProductNameText = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Category:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedCard(
                            onClick = { editCategoryDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = editProductCategoryText.ifBlank { "SAREE" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }

                        DropdownMenu(
                            expanded = editCategoryDropdownExpanded,
                            onDismissRequest = { editCategoryDropdownExpanded = false }
                        ) {
                            allCategoryNames.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, fontWeight = if (cat == editProductCategoryText) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        editProductCategoryText = cat
                                        editCategoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Text("Incentive / Rate per piece (₹):", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = editRateText,
                        onValueChange = { editRateText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newRate = editRateText.toDoubleOrNull()
                        val newName = editProductNameText.trim().uppercase(Locale.getDefault())
                        val newCat = editProductCategoryText.trim().uppercase(Locale.getDefault()).ifBlank { product.category }
                        if (newName.isNotBlank() && newRate != null && newRate > 0) {
                            onUpdateProduct(product.copy(name = newName, category = newCat, ratePerPiece = newRate))
                            editingProduct = null
                            Toast.makeText(context, "Product card updated!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Enter a valid name and price", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = {
                            val prod = product
                            editingProduct = null
                            productToDelete = prod
                        }
                    ) {
                        Text("Delete Card", color = MaterialTheme.colorScheme.error)
                    }
                    TextButton(onClick = { editingProduct = null }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    // Dialog: Delete Product Card Confirmation
    productToDelete?.let { prod ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete '${prod.name}' Card?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete this product card from T-HISAB?\n\n• Name: ${prod.name}\n• Category: ${prod.category}\n• Rate: ₹${prod.ratePerPiece.toInt()}/pc"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProduct(prod)
                        clickerCounts.remove(prod.id)
                        productToDelete = null
                        Toast.makeText(context, "Deleted '${prod.name}' card!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Card")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Add New Category
    if (showAddNewCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddNewCategoryDialog = false },
            title = { Text("Add New Product Category", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Enter the category name (e.g. SAREE, KIDS, SUIT, LEHENGA, DRESS, ACCESSORIES):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newCategoryInputText,
                        onValueChange = { newCategoryInputText = it },
                        placeholder = { Text("Category Name (e.g. KIDS)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = newCategoryInputText.trim().uppercase(Locale.getDefault())
                        if (trimmed.isNotBlank()) {
                            onAddCategory?.invoke(trimmed)
                            customItemCategory = trimmed
                            showAddNewCategoryDialog = false
                            Toast.makeText(context, "Category '$trimmed' added!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Please enter a category name", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Add Category")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNewCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Manage Categories
    if (showManageCategoriesDialog) {
        AlertDialog(
            onDismissRequest = { showManageCategoriesDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Manage Categories", fontWeight = FontWeight.Bold)
                    IconButton(
                        onClick = {
                            newCategoryInputText = ""
                            showAddNewCategoryDialog = true
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add Category", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Rename or delete product categories:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(allCategoryNames) { cat ->
                            val count = allProducts.count { it.category.equals(cat, ignoreCase = true) }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = cat,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "$count ${if (count == 1) "item" else "items"}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Rename button
                                        IconButton(
                                            onClick = {
                                                categoryToRename = cat
                                                renameCategoryInputText = cat
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Rename Category",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        // Delete button
                                        IconButton(
                                            onClick = {
                                                categoryToDelete = cat
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete Category",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
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
                Button(onClick = { showManageCategoriesDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    // Dialog: Rename Category
    categoryToRename?.let { oldCat ->
        AlertDialog(
            onDismissRequest = { categoryToRename = null },
            title = { Text("Rename Category", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Rename category '$oldCat' to:")
                    OutlinedTextField(
                        value = renameCategoryInputText,
                        onValueChange = { renameCategoryInputText = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newCat = renameCategoryInputText.trim().uppercase(Locale.getDefault())
                        if (newCat.isNotBlank() && !newCat.equals(oldCat, ignoreCase = true)) {
                            onRenameCategory?.invoke(oldCat, newCat)
                            if (customItemCategory.equals(oldCat, ignoreCase = true)) {
                                customItemCategory = newCat
                            }
                            Toast.makeText(context, "Renamed '$oldCat' to '$newCat'!", Toast.LENGTH_SHORT).show()
                        }
                        categoryToRename = null
                    }
                ) {
                    Text("Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToRename = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Delete Category
    categoryToDelete?.let { cat ->
        val itemsInCat = allProducts.filter { it.category.equals(cat, ignoreCase = true) }
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Delete Category '$cat'?", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (itemsInCat.isEmpty()) {
                        Text("Are you sure you want to remove the category '$cat'?")
                    } else {
                        Text("This category contains ${itemsInCat.size} product item(s):\n• ${itemsInCat.take(4).joinToString(", ") { it.name }}${if (itemsInCat.size > 4) "..." else ""}\n\nWhat would you like to do with these items?")
                    }
                }
            },
            confirmButton = {
                if (itemsInCat.isEmpty()) {
                    Button(
                        onClick = {
                            onDeleteCategory?.invoke(cat, false, "GENERAL")
                            if (customItemCategory.equals(cat, ignoreCase = true)) {
                                customItemCategory = allCategoryNames.firstOrNull { !it.equals(cat, ignoreCase = true) } ?: "GENERAL"
                            }
                            categoryToDelete = null
                            Toast.makeText(context, "Category '$cat' removed!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                onDeleteCategory?.invoke(cat, false, "GENERAL")
                                if (customItemCategory.equals(cat, ignoreCase = true)) {
                                    customItemCategory = "GENERAL"
                                }
                                categoryToDelete = null
                                Toast.makeText(context, "Items moved to GENERAL and category '$cat' deleted!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Move Items to GENERAL", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onDeleteCategory?.invoke(cat, true, "GENERAL")
                                itemsInCat.forEach { clickerCounts.remove(it.id) }
                                if (customItemCategory.equals(cat, ignoreCase = true)) {
                                    customItemCategory = allCategoryNames.firstOrNull { !it.equals(cat, ignoreCase = true) } ?: "GENERAL"
                                }
                                categoryToDelete = null
                                Toast.makeText(context, "Deleted category and ${itemsInCat.size} item(s)!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Delete All ${itemsInCat.size} Items in Category", fontSize = 12.sp)
                        }

                        TextButton(
                            onClick = { categoryToDelete = null },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            },
            dismissButton = if (itemsInCat.isEmpty()) {
                {
                    TextButton(onClick = { categoryToDelete = null }) {
                        Text("Cancel")
                    }
                }
            } else null
        )
    }

    // Dialog: Delete Entry Confirmation
    entryToDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Delete Ledger Entry?") },
            text = {
                Text(
                    text = if (entry.entryType == "SALES")
                        "Are you sure you want to delete this sales record of ${entry.totalPieces} pcs (₹${entry.totalEarned.toInt()}) on ${entry.dateString}?"
                    else
                        "Are you sure you want to delete this advance payment record of ₹${entry.advancePaid.toInt()} on ${entry.dateString}?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteEntry(entry)
                        entryToDelete = null
                        Toast.makeText(context, "Entry deleted!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Pick Date for Logging Entry
    if (showDatePickerDialog) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        android.app.DatePickerDialog(
            context,
            { _, y, m, d ->
                val formatted = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y)
                logDate = formatted
                advanceDate = formatted
                showDatePickerDialog = false
            },
            year,
            month,
            day
        ).show()
        showDatePickerDialog = false
    }

    // Dialog: Filter Date Picker for Statement
    if (showFilterDatePickerDialog) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        android.app.DatePickerDialog(
            context,
            { _, y, m, d ->
                val formatted = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y)
                specificDateFilter = formatted
                showFilterDatePickerDialog = false
            },
            year,
            month,
            day
        ).show()
        showFilterDatePickerDialog = false
    }

    // Dialog: Dashboard Custom From Date Picker
    if (showDashboardFromDatePicker) {
        val cal = Calendar.getInstance()
        val parsedMillis = parseEntryDateMillis(dashboardCustomFromDate)
        if (parsedMillis > 0L) cal.timeInMillis = parsedMillis
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        android.app.DatePickerDialog(
            context,
            { _, y, m, d ->
                dashboardCustomFromDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y)
                showDashboardFromDatePicker = false
            },
            year,
            month,
            day
        ).show()
        showDashboardFromDatePicker = false
    }

    // Dialog: Dashboard Custom To Date Picker
    if (showDashboardToDatePicker) {
        val cal = Calendar.getInstance()
        val parsedMillis = parseEntryDateMillis(dashboardCustomToDate)
        if (parsedMillis > 0L) cal.timeInMillis = parsedMillis
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        android.app.DatePickerDialog(
            context,
            { _, y, m, d ->
                dashboardCustomToDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y)
                showDashboardToDatePicker = false
            },
            year,
            month,
            day
        ).show()
        showDashboardToDatePicker = false
    }

    // Dialog: Share Custom From Date Picker
    if (showShareFromDatePicker) {
        val cal = Calendar.getInstance()
        val parsedMillis = parseEntryDateMillis(shareCustomFromDate)
        if (parsedMillis > 0L) cal.timeInMillis = parsedMillis
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        android.app.DatePickerDialog(
            context,
            { _, y, m, d ->
                shareCustomFromDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y)
                showShareFromDatePicker = false
            },
            year,
            month,
            day
        ).show()
        showShareFromDatePicker = false
    }

    // Dialog: Share Custom To Date Picker
    if (showShareToDatePicker) {
        val cal = Calendar.getInstance()
        val parsedMillis = parseEntryDateMillis(shareCustomToDate)
        if (parsedMillis > 0L) cal.timeInMillis = parsedMillis
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        android.app.DatePickerDialog(
            context,
            { _, y, m, d ->
                shareCustomToDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y)
                showShareToDatePicker = false
            },
            year,
            month,
            day
        ).show()
        showShareToDatePicker = false
    }

    // Dialog: Share Employee Passbook & Incentive Statement Link
    showShareEmployeeDialog?.let { student ->
        LaunchedEffect(student.id) {
            onAutoSyncForShare?.invoke()
        }
        var shareLockToEmployee by remember { mutableStateOf(true) }
        val empLink = getEmployeeShareLink(
            student = student,
            monthFilter = if (sharePeriodMode == 0) shareSelectedMonth else null,
            lockToEmployee = shareLockToEmployee
        )
        val assignedClass = allClasses.find { it.id == student.classId }
        var portalUrlDraft by remember(portalBaseUrl) { mutableStateOf(portalBaseUrl) }
        var isEditingPortalUrl by remember(portalBaseUrl) { mutableStateOf(portalBaseUrl.isBlank()) }
        var showGitHubPagesGuide by remember { mutableStateOf(false) }

        val previewText = remember(
            student,
            shareFormatDetailed,
            sharePeriodMode,
            shareSelectedMonth,
            shareCustomFromDate,
            shareCustomToDate,
            ledgerEntries,
            portalBaseUrl,
            firebaseUrl,
            firebaseWorkspace,
            shareLockToEmployee
        ) {
            generateEmployeeShareText(
                student = student,
                isWhatsApp = true,
                includeSareeDetails = shareFormatDetailed,
                periodMode = sharePeriodMode,
                selectedMonth = shareSelectedMonth,
                customFromDate = shareCustomFromDate,
                customToDate = shareCustomToDate,
                lockToEmployee = shareLockToEmployee
            )
        }

        AlertDialog(
            onDismissRequest = { showShareEmployeeDialog = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        tint = Color(0xFF25D366),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share Incentive Statement",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
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
                    // Employee Info Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = student.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Emp ID: ${student.rollNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (assignedClass != null || student.phone.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (assignedClass != null) {
                                        Text(
                                            text = assignedClass.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (student.phone.isNotBlank()) {
                                        Text(
                                            text = "📞 ${student.phone}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF25D366),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 1. TIMEFRAME & DATE RANGE SELECTION
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SELECT STATEMENT TIMEFRAME:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (sharePeriodMode) {
                                    0 -> Color(0xFF10B981).copy(alpha = 0.15f)
                                    1 -> Color(0xFF3B82F6).copy(alpha = 0.15f)
                                    else -> Color(0xFF8B5CF6).copy(alpha = 0.15f)
                                }
                            ) {
                                Text(
                                    text = when (sharePeriodMode) {
                                        0 -> "Whole Month"
                                        1 -> "Custom Range"
                                        else -> "All-Time"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (sharePeriodMode) {
                                        0 -> Color(0xFF10B981)
                                        1 -> Color(0xFF3B82F6)
                                        else -> Color(0xFF8B5CF6)
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Timeframe Mode Selector (Whole Month, Custom Range, All-Time)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Mode 0: Whole Month (Default)
                            FilterChip(
                                selected = (sharePeriodMode == 0),
                                onClick = { sharePeriodMode = 0 },
                                label = { Text("📅 Whole Month", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )

                            // Mode 1: Custom Date Range
                            FilterChip(
                                selected = (sharePeriodMode == 1),
                                onClick = { sharePeriodMode = 1 },
                                label = { Text("🗓️ Date Range", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )

                            // Mode 2: Complete / All-Time
                            FilterChip(
                                selected = (sharePeriodMode == 2),
                                onClick = { sharePeriodMode = 2 },
                                label = { Text("🌐 All Time", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }

                        // Sub-options based on selected timeframe
                        when (sharePeriodMode) {
                            0 -> {
                                // WHOLE MONTH SELECTOR
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Select Month to Send:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    val monthsList = remember(availableMonths) {
                                        val curMonth = SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(Date())
                                        if (curMonth in availableMonths) availableMonths else listOf(curMonth) + availableMonths
                                    }
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(monthsList) { mName ->
                                            val isSelected = mName.equals(shareSelectedMonth, ignoreCase = true)
                                            Surface(
                                                onClick = { shareSelectedMonth = mName },
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                                )
                                            ) {
                                                Text(
                                                    text = mName,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "✓ Includes all 30/31 days of ${shareSelectedMonth.uppercase()}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            1 -> {
                                // CUSTOM DATE RANGE SELECTOR
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Quick range presets
                                    Text(
                                        text = "Quick Presets:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        item {
                                            SuggestionChip(
                                                onClick = {
                                                    val cal = Calendar.getInstance()
                                                    val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(cal.time)
                                                    cal.add(Calendar.DAY_OF_MONTH, -7)
                                                    val fromStr = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(cal.time)
                                                    shareCustomFromDate = fromStr
                                                    shareCustomToDate = todayStr
                                                },
                                                label = { Text("Last 7 Days", fontSize = 10.sp) }
                                            )
                                        }
                                        item {
                                            SuggestionChip(
                                                onClick = {
                                                    val cal = Calendar.getInstance()
                                                    val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(cal.time)
                                                    cal.add(Calendar.DAY_OF_MONTH, -15)
                                                    val fromStr = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(cal.time)
                                                    shareCustomFromDate = fromStr
                                                    shareCustomToDate = todayStr
                                                },
                                                label = { Text("Last 15 Days", fontSize = 10.sp) }
                                            )
                                        }
                                        item {
                                            SuggestionChip(
                                                onClick = {
                                                    val cal = Calendar.getInstance()
                                                    val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(cal.time)
                                                    cal.add(Calendar.DAY_OF_MONTH, -30)
                                                    val fromStr = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(cal.time)
                                                    shareCustomFromDate = fromStr
                                                    shareCustomToDate = todayStr
                                                },
                                                label = { Text("Last 30 Days", fontSize = 10.sp) }
                                            )
                                        }
                                        item {
                                            SuggestionChip(
                                                onClick = {
                                                    val cal = Calendar.getInstance()
                                                    val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(cal.time)
                                                    val monthStartStr = "01/" + SimpleDateFormat("MM/yyyy", Locale.ENGLISH).format(cal.time)
                                                    shareCustomFromDate = monthStartStr
                                                    shareCustomToDate = todayStr
                                                },
                                                label = { Text("This Month (1st-Today)", fontSize = 10.sp) }
                                            )
                                        }
                                    }

                                    // From & To Date clickable boxes
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // From Date
                                        Surface(
                                            onClick = { showShareFromDatePicker = true },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column {
                                                    Text("From Date", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(shareCustomFromDate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        // To Date
                                        Surface(
                                            onClick = { showShareToDatePicker = true },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column {
                                                    Text("To Date", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(shareCustomToDate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else -> {
                                // ALL-TIME NOTE
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ) {
                                    Text(
                                        text = "🌐 Statement will include all historical transactions from start to present.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 2. FORMAT SELECTION: Option 1 vs Option 2
                    Text(
                        text = "SELECT STATEMENT FORMAT:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Option 1: Standard Summary
                        Surface(
                            onClick = { shareFormatDetailed = false },
                            shape = RoundedCornerShape(8.dp),
                            color = if (!shareFormatDetailed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (!shareFormatDetailed) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = !shareFormatDetailed,
                                        onClick = { shareFormatDetailed = false },
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "1. Summary",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (!shareFormatDetailed) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Total Sales (Pcs & ₹) + Advance & Balance",
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Option 2: Detailed Saree Names
                        Surface(
                            onClick = { shareFormatDetailed = true },
                            shape = RoundedCornerShape(8.dp),
                            color = if (shareFormatDetailed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (shareFormatDetailed) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = shareFormatDetailed,
                                        onClick = { shareFormatDetailed = true },
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "2. Detailed",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (shareFormatDetailed) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Saree Names, Quantities, Rates & Item Totals",
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // GitHub Pages Live Passbook Link Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = if (portalBaseUrl.isNotBlank()) Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (portalBaseUrl.isNotBlank()) Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFFF59E0B).copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (portalBaseUrl.isNotBlank()) Icons.Default.Public else Icons.Default.Link,
                                        contentDescription = null,
                                        tint = if (portalBaseUrl.isNotBlank()) Color(0xFF047857) else Color(0xFFB45309),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (portalBaseUrl.isNotBlank()) "GitHub Pages Live Link Active" else "Setup GitHub Pages Live Link",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (portalBaseUrl.isNotBlank()) Color(0xFF065F46) else Color(0xFF92400E)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (portalBaseUrl.isNotBlank() && !isEditingPortalUrl) {
                                        TextButton(
                                            onClick = { isEditingPortalUrl = true },
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                            modifier = Modifier.height(26.dp)
                                        ) {
                                            Text("Edit URL", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    TextButton(
                                        onClick = { showGitHubPagesGuide = !showGitHubPagesGuide },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text(
                                            text = if (showGitHubPagesGuide) "Hide Guide" else "How to Setup?",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            if (showGitHubPagesGuide) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "1. Push this code folder (contains docs/index.html) to your GitHub repo. Only you can edit your GitHub repo.\n" +
                                               "2. Repo Settings → Pages → Source: 'Deploy from a branch' → Branch: main, Folder: /docs → Save.\n" +
                                               "3. For strict Owner-Only editing: in Firebase Rules set \".read\": true, \".write\": false and paste your Database Secret in the app's Firebase Sync dialog.\n" +
                                               "4. Paste your live GitHub Pages URL below (e.g. https://username.github.io/repo-name).",
                                        fontSize = 10.5.sp,
                                        lineHeight = 14.sp,
                                        color = Color(0xFF1E293B),
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }

                            if (isEditingPortalUrl || portalBaseUrl.isBlank()) {
                                OutlinedTextField(
                                    value = portalUrlDraft,
                                    onValueChange = { portalUrlDraft = it },
                                    placeholder = { Text("https://username.github.io/shop-attendance", fontSize = 11.sp) },
                                    label = { Text("GitHub Pages Portal URL", fontSize = 11.sp) },
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("github_pages_url_input")
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (portalBaseUrl.isNotBlank()) {
                                        OutlinedButton(
                                            onClick = {
                                                portalUrlDraft = portalBaseUrl
                                                isEditingPortalUrl = false
                                            },
                                            modifier = Modifier.weight(1f).height(34.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                        ) {
                                            Text("Cancel", fontSize = 11.sp)
                                        }
                                    }
                                    Button(
                                        onClick = {
                                            val cleaned = normalizePortalBaseUrl(portalUrlDraft)
                                            onSavePortalBaseUrl?.invoke(cleaned)
                                            portalUrlDraft = cleaned
                                            isEditingPortalUrl = false
                                            Toast.makeText(
                                                context,
                                                if (cleaned.isNotBlank()) "GitHub Pages URL saved!" else "Portal link cleared",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(34.dp)
                                            .testTag("save_github_pages_url_btn"),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Text("Save Portal URL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (empLink.isNotBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "🔒 Lock link to ${student.name} only (View-Only)",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF065F46)
                                        )
                                        Text(
                                            text = if (shareLockToEmployee) "Staff cannot edit or view other employees" else "Staff selector dropdown visible",
                                            fontSize = 9.5.sp,
                                            color = Color(0xFF047857)
                                        )
                                    }
                                    Switch(
                                        checked = shareLockToEmployee,
                                        onCheckedChange = { shareLockToEmployee = it }
                                    )
                                }

                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E242B),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4758))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = empLink,
                                            fontSize = 10.sp,
                                            color = Color(0xFF93C5FD),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = {
                                                onAutoSyncForShare?.invoke()
                                                clipboardManager.setText(AnnotatedString(empLink))
                                                Toast.makeText(context, "View-only link copied!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.ContentCopy,
                                                contentDescription = "Copy Link",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                onAutoSyncForShare?.invoke()
                                                try {
                                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(empLink))
                                                    context.startActivity(browserIntent)
                                                } catch (_: Exception) {
                                                    Toast.makeText(context, "Unable to open browser for URL", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.OpenInNew,
                                                contentDescription = "Open Link in Browser",
                                                tint = Color(0xFF34D399),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                if (onSyncToFirebaseNow != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val syncText = if (firebaseLastSyncTime > 0L) {
                                            "Cloud synced: " + SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(firebaseLastSyncTime))
                                        } else {
                                            "Sync to Firebase so live link shows latest Hisab"
                                        }
                                        Text(
                                            text = syncText,
                                            fontSize = 9.5.sp,
                                            color = Color(0xFF065F46),
                                            modifier = Modifier.weight(1f)
                                        )
                                        TextButton(
                                            onClick = { onSyncToFirebaseNow.invoke() },
                                            enabled = !isFirebaseSyncing,
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                            modifier = Modifier.height(26.dp)
                                        ) {
                                            Text(
                                                text = if (isFirebaseSyncing) "Syncing..." else "☁️ Sync Now",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Message Preview Box
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (shareFormatDetailed) "Message Preview (Detailed Format):" else "Message Preview (Summary Format):",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = when (sharePeriodMode) {
                                    0 -> "📅 ${shareSelectedMonth}"
                                    1 -> "🗓️ Custom Range"
                                    else -> "🌐 All Time"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 150.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Text(
                                text = previewText,
                                fontSize = 10.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = Color(0xFFE2E8F0),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .verticalScroll(rememberScrollState())
                            )
                        }
                    }

                    // Action Buttons Grid
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // WhatsApp Share Button
                        Button(
                            onClick = {
                                shareEmployee(
                                    student = student,
                                    mode = 0,
                                    includeSareeDetails = shareFormatDetailed,
                                    periodMode = sharePeriodMode,
                                    selectedMonth = shareSelectedMonth,
                                    customFromDate = shareCustomFromDate,
                                    customToDate = shareCustomToDate,
                                    lockToEmployee = shareLockToEmployee
                                )
                                showShareEmployeeDialog = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (shareFormatDetailed) "Send Detailed on WhatsApp" else "Send Summary on WhatsApp",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }

                        // Share to other apps (System Chooser)
                        Button(
                            onClick = {
                                shareEmployee(
                                    student = student,
                                    mode = 1,
                                    includeSareeDetails = shareFormatDetailed,
                                    periodMode = sharePeriodMode,
                                    selectedMonth = shareSelectedMonth,
                                    customFromDate = shareCustomFromDate,
                                    customToDate = shareCustomToDate,
                                    lockToEmployee = shareLockToEmployee
                                )
                                showShareEmployeeDialog = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share via Other Apps (SMS / Email)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        }

                        // Copy Statement Text Button
                        OutlinedButton(
                            onClick = {
                                shareEmployee(
                                    student = student,
                                    mode = 3,
                                    includeSareeDetails = shareFormatDetailed,
                                    periodMode = sharePeriodMode,
                                    selectedMonth = shareSelectedMonth,
                                    customFromDate = shareCustomFromDate,
                                    customToDate = shareCustomToDate,
                                    lockToEmployee = shareLockToEmployee
                                )
                                showShareEmployeeDialog = null
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (shareFormatDetailed) "Copy Detailed Statement Text" else "Copy Summary Statement Text",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showShareEmployeeDialog = null }) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Rearrange Employees Dialog
    if (showRearrangeEmployeesDialog) {
        RearrangeHisabEmployeesDialog(
            students = allStudents,
            classes = allClasses,
            onDismiss = { showRearrangeEmployeesDialog = false },
            onMoveUp = { student ->
                onMoveStudentUp?.invoke(student)
            },
            onMoveDown = { student ->
                onMoveStudentDown?.invoke(student)
            },
            onSort = { sortMode ->
                onSortStudents?.invoke(sortMode)
            },
            onSelectStudent = {
                onSelectStudent(it)
                showRearrangeEmployeesDialog = false
            }
        )
    }

    // Edit Ledger Entry Dialog
    editingEntry?.let { entry ->
        EditLedgerEntryDialog(
            entry = entry,
            allProducts = allProducts,
            onDismiss = { editingEntry = null },
            onSave = { updated ->
                onUpdateEntry?.invoke(updated)
                editingEntry = null
            }
        )
    }
}

@Composable
fun RearrangeHisabEmployeesDialog(
    students: List<StudentEntity>,
    classes: List<ClassEntity> = emptyList(),
    onDismiss: () -> Unit,
    onMoveUp: (StudentEntity) -> Unit,
    onMoveDown: (StudentEntity) -> Unit,
    onSort: (String) -> Unit,
    onSelectStudent: (StudentEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredStudents = remember(students, searchQuery) {
        if (searchQuery.isBlank()) students
        else students.filter {
            val clsName = classes.find { c -> c.id == it.classId }?.name ?: ""
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.rollNumber.contains(searchQuery, ignoreCase = true) ||
                    clsName.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = null,
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rearrange Employees",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF374151)
                ) {
                    Text(
                        text = "${students.size} Staff",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 460.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Change staff display order in T-HISAB. Use ▲ / ▼ buttons to move positions, or pick quick automatic sorting.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Quick Auto-Sort Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        onClick = { onSort("A_Z") },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF2B3544),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4758)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "🔤 A → Z",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFFB74D),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    Surface(
                        onClick = { onSort("Z_A") },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF2B3544),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4758)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "🔤 Z → A",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFFB74D),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    Surface(
                        onClick = { onSort("CLASS") },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF2B3544),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4758)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "👥 By Role",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFFB74D),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    Surface(
                        onClick = { onSort("RESET") },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF2B3544),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4758)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "🔄 Reset",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE2E8F0),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }

                // Search Filter
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search staff name...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // List of Reorderable Staff
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(filteredStudents) { index, student ->
                        val globalIndex = students.indexOfFirst { it.id == student.id }
                        val isFirst = globalIndex <= 0
                        val isLast = globalIndex >= students.size - 1

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E242B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3846)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Index Badge + Avatar + Name
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSelectStudent(student) }
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF2B3544),
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${globalIndex + 1}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFB74D)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column {
                                        Text(
                                            text = student.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = classes.find { c -> c.id == student.classId }?.name ?: student.rollNumber.ifBlank { "Staff" },
                                            fontSize = 10.5.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                }

                                // Move Up & Down Buttons
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { onMoveUp(student) },
                                        enabled = !isFirst,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                color = if (!isFirst) Color(0xFF374151) else Color(0xFF222831),
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowUp,
                                            contentDescription = "Move Up",
                                            tint = if (!isFirst) Color(0xFFFFB74D) else Color(0xFF6B7280),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onMoveDown(student) },
                                        enabled = !isLast,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                color = if (!isLast) Color(0xFF374151) else Color(0xFF222831),
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Move Down",
                                            tint = if (!isLast) Color(0xFFFFB74D) else Color(0xFF6B7280),
                                            modifier = Modifier.size(18.dp)
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
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D))
            ) {
                Text("DONE", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    )
}

data class EditBreakdownItemModel(
    val id: Long,
    val name: String,
    val qtyText: String,
    val rateText: String
)

@Composable
fun EditLedgerEntryDialog(
    entry: HisabLedgerEntryEntity,
    allProducts: List<HisabProductEntity>,
    onDismiss: () -> Unit,
    onSave: (HisabLedgerEntryEntity) -> Unit
) {
    val context = LocalContext.current
    var dateString by remember { mutableStateOf(entry.dateString) }
    var showDatePicker by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf(entry.notes) }

    val isSales = entry.entryType == "SALES"

    // Advance fields
    var advanceAmountText by remember { mutableStateOf(if (entry.advancePaid > 0) entry.advancePaid.toInt().toString() else "") }

    // Sales fields
    var totalPiecesText by remember { mutableStateOf(entry.totalPieces.toString()) }
    var totalEarnedText by remember { mutableStateOf(entry.totalEarned.toInt().toString()) }

    // Sales breakdown items
    val breakdownItems = remember {
        val list = mutableStateListOf<EditBreakdownItemModel>()
        if (isSales && entry.itemBreakdownJson.isNotBlank()) {
            try {
                val arr = JSONArray(entry.itemBreakdownJson)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    list.add(
                        EditBreakdownItemModel(
                            id = System.currentTimeMillis() + i,
                            name = obj.optString("name", "Item"),
                            qtyText = obj.optInt("qty", 1).toString(),
                            rateText = obj.optDouble("rate", 10.0).toInt().toString()
                        )
                    )
                }
            } catch (_: Exception) {}
        }
        list
    }

    // Helper to calculate & sync totals from breakdown
    fun syncTotalsFromBreakdown() {
        if (breakdownItems.isNotEmpty()) {
            var sumPcs = 0
            var sumEarned = 0.0
            for (item in breakdownItems) {
                val q = item.qtyText.toIntOrNull() ?: 0
                val r = item.rateText.toDoubleOrNull() ?: 0.0
                sumPcs += q
                sumEarned += (q * r)
            }
            totalPiecesText = sumPcs.toString()
            totalEarnedText = sumEarned.toInt().toString()
        } else {
            totalPiecesText = "0"
            totalEarnedText = "0"
        }
    }

    if (showDatePicker) {
        val cal = Calendar.getInstance()
        val formats = arrayOf("dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd", "yyyy/MM/dd")
        for (fmt in formats) {
            try {
                val sdf = SimpleDateFormat(fmt, Locale.getDefault())
                val d = sdf.parse(dateString.trim())
                if (d != null) {
                    cal.time = d
                    break
                }
            } catch (_: Exception) {}
        }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        android.app.DatePickerDialog(
            context,
            { _, y, m, d ->
                dateString = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y)
                showDatePicker = false
            },
            year,
            month,
            day
        ).show()
        showDatePicker = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = if (isSales) Color(0xFF10B981) else Color(0xFFF59E0B),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSales) "Edit Sales Record" else "Edit Advance Record",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isSales) Color(0xFF065F46) else Color(0xFF7C2D12)
                ) {
                    Text(
                        text = entry.entryType,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Date Row
                Text("ENTRY DATE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = dateString,
                        onValueChange = { dateString = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("DD/MM/YYYY") },
                        shape = RoundedCornerShape(8.dp)
                    )
                    IconButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF2B3544), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF3B4758), RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date", tint = Color(0xFFFFB74D), modifier = Modifier.size(20.dp))
                    }
                }

                if (isSales) {
                    var showAddProductMenu by remember { mutableStateOf(false) }

                    // Breakdown Editor
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ITEM BREAKDOWN:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Box {
                            TextButton(
                                onClick = {
                                    if (allProducts.isNotEmpty()) {
                                        showAddProductMenu = true
                                    } else {
                                        breakdownItems.add(
                                            EditBreakdownItemModel(
                                                id = System.currentTimeMillis(),
                                                name = "Item",
                                                qtyText = "1",
                                                rateText = "10"
                                            )
                                        )
                                        syncTotalsFromBreakdown()
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Add Item", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            if (allProducts.isNotEmpty()) {
                                DropdownMenu(
                                    expanded = showAddProductMenu,
                                    onDismissRequest = { showAddProductMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Select Saree to Add:",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        },
                                        onClick = {},
                                        enabled = false
                                    )
                                    allProducts.forEach { prod ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(prod.name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text(
                                                        "₹${prod.ratePerPiece.toInt()}/pc",
                                                        color = Color(0xFF4ADE80),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            },
                                            onClick = {
                                                breakdownItems.add(
                                                    EditBreakdownItemModel(
                                                        id = System.currentTimeMillis(),
                                                        name = prod.name,
                                                        qtyText = "1",
                                                        rateText = prod.ratePerPiece.toInt().toString()
                                                    )
                                                )
                                                syncTotalsFromBreakdown()
                                                showAddProductMenu = false
                                            }
                                        )
                                    }
                                    HorizontalDivider(color = Color(0xFF374151))
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFFF59E0B))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("+ Custom Saree / Other", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                                            }
                                        },
                                        onClick = {
                                            breakdownItems.add(
                                                EditBreakdownItemModel(
                                                    id = System.currentTimeMillis(),
                                                    name = "",
                                                    qtyText = "1",
                                                    rateText = "10"
                                                )
                                            )
                                            syncTotalsFromBreakdown()
                                            showAddProductMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (breakdownItems.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            breakdownItems.forEachIndexed { index, item ->
                                var itemDropdownExpanded by remember { mutableStateOf(false) }
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF1E242B),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151))
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.weight(1.8f)) {
                                                OutlinedTextField(
                                                    value = item.name,
                                                    onValueChange = { newName ->
                                                        breakdownItems[index] = item.copy(name = newName)
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    placeholder = { Text("Item Name", fontSize = 11.sp) },
                                                    singleLine = true,
                                                    shape = RoundedCornerShape(4.dp),
                                                    trailingIcon = if (allProducts.isNotEmpty()) {
                                                        {
                                                            IconButton(
                                                                onClick = { itemDropdownExpanded = true },
                                                                modifier = Modifier.size(22.dp)
                                                            ) {
                                                                Icon(
                                                                    Icons.Default.ArrowDropDown,
                                                                    contentDescription = "Pick Saree",
                                                                    tint = MaterialTheme.colorScheme.primary,
                                                                    modifier = Modifier.size(18.dp)
                                                                )
                                                            }
                                                        }
                                                    } else null
                                                )

                                                if (allProducts.isNotEmpty()) {
                                                    DropdownMenu(
                                                        expanded = itemDropdownExpanded,
                                                        onDismissRequest = { itemDropdownExpanded = false }
                                                    ) {
                                                        DropdownMenuItem(
                                                            text = {
                                                                Text("Choose Saree:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                                            },
                                                            onClick = {},
                                                            enabled = false
                                                        )
                                                        allProducts.forEach { prod ->
                                                            DropdownMenuItem(
                                                                text = {
                                                                    Row(
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                                        verticalAlignment = Alignment.CenterVertically
                                                                    ) {
                                                                        Text(prod.name, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                                                                        Spacer(modifier = Modifier.width(10.dp))
                                                                        Text("₹${prod.ratePerPiece.toInt()}", color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                                    }
                                                                },
                                                                onClick = {
                                                                    breakdownItems[index] = item.copy(
                                                                        name = prod.name,
                                                                        rateText = prod.ratePerPiece.toInt().toString()
                                                                    )
                                                                    syncTotalsFromBreakdown()
                                                                    itemDropdownExpanded = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            OutlinedTextField(
                                                value = item.qtyText,
                                                onValueChange = { newQty ->
                                                    breakdownItems[index] = item.copy(qtyText = newQty)
                                                    syncTotalsFromBreakdown()
                                                },
                                                modifier = Modifier.weight(1f),
                                                placeholder = { Text("Qty", fontSize = 11.sp) },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            OutlinedTextField(
                                                value = item.rateText,
                                                onValueChange = { newRate ->
                                                    breakdownItems[index] = item.copy(rateText = newRate)
                                                    syncTotalsFromBreakdown()
                                                },
                                                modifier = Modifier.weight(1f),
                                                placeholder = { Text("₹/pc", fontSize = 11.sp) },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            IconButton(
                                                onClick = {
                                                    breakdownItems.removeAt(index)
                                                    syncTotalsFromBreakdown()
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Remove Item", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                            }
                                        }
                                        val q = item.qtyText.toIntOrNull() ?: 0
                                        val r = item.rateText.toDoubleOrNull() ?: 0.0
                                        Text(
                                            text = "Subtotal: $q pcs × ₹${r.toInt()} = ₹${(q * r).toInt()}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF93C5FD),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Total Pieces and Total Earned
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("TOTAL PCS / SAREES:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(2.dp))
                            OutlinedTextField(
                                value = totalPiecesText,
                                onValueChange = { totalPiecesText = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("TOTAL AMOUNT (₹):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(2.dp))
                            OutlinedTextField(
                                value = totalEarnedText,
                                onValueChange = { totalEarnedText = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    // Advance Amount
                    Text("ADVANCE / DEDUCTION AMOUNT (₹):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = advanceAmountText,
                        onValueChange = { advanceAmountText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        placeholder = { Text("Advance Amount ₹...") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Quick Note Presets
                    Text("TAG / QUICK CATEGORY:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            "💵 Cash" to "Cash Advance",
                            "☕ Chai" to "[CHAI/THANDA] Refreshment",
                            "🔄 Return" to "[RETURN] Return Deduction"
                        ).forEach { (lbl, noteSnippet) ->
                            Surface(
                                onClick = {
                                    notes = if (notes.isBlank()) noteSnippet else "$noteSnippet - $notes"
                                },
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF2B3544),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4758)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = lbl,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFFFB74D),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                // Notes Field
                Text("NOTES / DESCRIPTION:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Optional notes / remarks...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (dateString.isBlank()) {
                        Toast.makeText(context, "Please enter a valid date", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (isSales) {
                        val pcs = totalPiecesText.toIntOrNull() ?: 0
                        val earned = totalEarnedText.toDoubleOrNull() ?: 0.0

                        // Build updated breakdown JSON
                        val arr = JSONArray()
                        for (item in breakdownItems) {
                            val q = item.qtyText.toIntOrNull() ?: 0
                            val r = item.rateText.toDoubleOrNull() ?: 0.0
                            if (item.name.isNotBlank() && q > 0) {
                                val obj = JSONObject()
                                obj.put("name", item.name.trim())
                                obj.put("qty", q)
                                obj.put("rate", r)
                                arr.put(obj)
                            }
                        }
                        val json = if (arr.length() > 0) arr.toString() else ""

                        val updated = entry.copy(
                            dateString = dateString.trim(),
                            totalPieces = pcs,
                            totalEarned = earned,
                            itemBreakdownJson = json,
                            notes = notes.trim()
                        )
                        onSave(updated)
                    } else {
                        val adv = advanceAmountText.toDoubleOrNull() ?: 0.0
                        val updated = entry.copy(
                            dateString = dateString.trim(),
                            advancePaid = adv,
                            notes = notes.trim()
                        )
                        onSave(updated)
                    }
                    Toast.makeText(context, "Ledger entry updated!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSales) Color(0xFF10B981) else Color(0xFFEA580C)
                )
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
