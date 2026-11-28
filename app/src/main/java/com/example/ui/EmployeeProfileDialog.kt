package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import com.example.data.PayrollPreferences
import com.example.data.ShiftEntity
import com.example.data.StaffFeedbackEntity
import com.example.data.StudentEntity
import com.example.data.cleanEmployeeName
import com.example.data.displayName
import com.example.data.isChecklist
import com.example.data.isDoneToday
import com.example.data.isTask
import com.example.data.todayComment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 360-Degree Complete Employee Profile Dialog
 * Displays Attendance, T-Hisab ledger, Payroll compensation, and Tasks in a unified view.
 */
@Composable
fun EmployeeProfileDialog(
    student: StudentEntity,
    activeClass: ClassEntity,
    shift: ShiftEntity?,
    allRecords: List<AttendanceRecordEntity>,
    allHisabEntries: List<HisabLedgerEntryEntity>,
    allChecklistItems: List<ChecklistItemEntity>,
    allStaffFeedback: List<StaffFeedbackEntity> = emptyList(),
    onDismiss: () -> Unit,
    onOpenAttendanceTab: () -> Unit = {},
    onOpenHisabTab: (StudentEntity) -> Unit = {},
    onOpenPayrollTab: (StudentEntity) -> Unit = {},
    onOpenChecklistTab: (StudentEntity) -> Unit = {},
    onToggleChecklistItem: (ChecklistItemEntity) -> Unit = {},
    onScheduleSmsForTask: (ChecklistItemEntity) -> Unit = {},
    onReorderTasks: (List<ChecklistItemEntity>) -> Unit = {},
    onAddFeedback: (studentId: Long, entryType: String, title: String, details: String, category: String, dateString: String) -> Unit = { _, _, _, _, _, _ -> },
    onUpdateFeedback: (StaffFeedbackEntity) -> Unit = {},
    onToggleFeedbackResolved: (StaffFeedbackEntity) -> Unit = {},
    onDeleteFeedback: (StaffFeedbackEntity) -> Unit = {}
) {
    val context = LocalContext.current
    val payrollPrefs = remember { PayrollPreferences(context) }

    // Tab state: 0 = Overview, 1 = Attendance, 2 = T-Hisab, 3 = Payroll, 4 = Tasks & Checklist, 5 = Complaint & Praise
    var selectedTab by remember { mutableIntStateOf(0) }

    val cleanName = student.displayName
    val employeeRecords = remember(allRecords, student.id) {
        allRecords.filter { it.studentId == student.id }
            .sortedByDescending { it.dateString }
    }

    val employeeHisabEntries = remember(allHisabEntries, student.id) {
        allHisabEntries.filter { it.studentId == student.id }
            .sortedByDescending { it.dateString }
    }

    val employeeTasks = remember(allChecklistItems, student.id, student.name) {
        allChecklistItems.filter {
            it.studentId == student.id ||
            (it.assignedStaffName.isNotBlank() && (it.assignedStaffName.equals(student.displayName, ignoreCase = true) || it.assignedStaffName.equals(student.name, ignoreCase = true)))
        }.sortedWith(
            compareBy<ChecklistItemEntity> { it.isDoneToday }
                .thenBy { it.displayOrder }
                .thenBy { it.id }
        )
    }

    // -------------------------------------------------------------
    // ATTENDANCE STATS
    // -------------------------------------------------------------
    val totalRecordedDays = employeeRecords.size
    val presentCount = employeeRecords.count { it.status == "P" || it.status == "PRESENT" }
    val absentCount = employeeRecords.count { it.status == "A" || it.status == "ABSENT" }
    val hdMorCount = employeeRecords.count { it.status == "HD MOR" }
    val hdEveCount = employeeRecords.count { it.status == "HD EVE" }
    val halfDayCount = hdMorCount + hdEveCount + employeeRecords.count { it.status == "HALF-DAY" }
    val effectivePresentDays = presentCount.toDouble() + (halfDayCount.toDouble() * 0.5)
    val attendanceRate = if (totalRecordedDays > 0) {
        (effectivePresentDays / totalRecordedDays.toDouble()) * 100.0
    } else 0.0

    // Current Month String
    val currentMonthKey = remember {
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    }
    val currentMonthDisplayName = remember {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    }

    val currentMonthRecords = remember(employeeRecords, currentMonthKey) {
        employeeRecords.filter { it.dateString.startsWith(currentMonthKey) }
    }
    val monthPresentCount = currentMonthRecords.count { it.status == "P" || it.status == "PRESENT" }
    val monthAbsentCount = currentMonthRecords.count { it.status == "A" || it.status == "ABSENT" }
    val monthHalfDayCount = currentMonthRecords.count { it.status.startsWith("HD") || it.status == "HALF-DAY" }

    // -------------------------------------------------------------
    // T-HISAB STATS
    // -------------------------------------------------------------
    val totalPieces = employeeHisabEntries.sumOf { it.totalPieces }
    val totalEarned = employeeHisabEntries.sumOf { it.totalEarned }
    val totalAdvancePaid = employeeHisabEntries.sumOf { it.advancePaid }
    val netHisabBalance = totalEarned - totalAdvancePaid

    // -------------------------------------------------------------
    // PAYROLL STATS (30-Days Style Payroll: Salary / 30 = Daily Rate, Sundays Paid)
    // -------------------------------------------------------------
    val monthlyBaseSalary = remember(student.id) { payrollPrefs.getSalary(student.id) }
    // Rule: PER day salary of employee == Salary of employee / 30 (for every month of year)
    val dailyWageRate = if (monthlyBaseSalary > 0) monthlyBaseSalary / 30.0 else 0.0

    // Calculate Sundays in current month (Paid @ full daily wage rate, e.g. 800rs for Sunday)
    val currentMonthSundaysCount = remember(currentMonthKey) {
        val cal = Calendar.getInstance()
        val parts = currentMonthKey.split("-")
        val y = parts.getOrNull(0)?.toIntOrNull() ?: cal.get(Calendar.YEAR)
        val m = parts.getOrNull(1)?.toIntOrNull() ?: (cal.get(Calendar.MONTH) + 1)
        cal.set(Calendar.YEAR, y)
        cal.set(Calendar.MONTH, m - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val maxD = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        (1..maxD).count { day ->
            cal.set(Calendar.DAY_OF_MONTH, day)
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        }
    }
    val sundayRecords = remember(currentMonthRecords) {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        currentMonthRecords.filter { rec ->
            try {
                val d = sdf.parse(rec.dateString)
                if (d != null) {
                    cal.time = d
                    cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                } else false
            } catch (_: Exception) { false }
        }
    }
    val sundayWorkedCount = remember(sundayRecords) { sundayRecords.count { it.status == "P" || it.status == "PRESENT" } }
    val sundayHalfDayCount = remember(sundayRecords) { sundayRecords.count { it.status.startsWith("HD") || it.status == "HALF-DAY" } }
    val sundayBonusDays = sundayWorkedCount.toDouble() + (0.5 * sundayHalfDayCount.toDouble())
    val sundayExtraBonusAmount = sundayBonusDays * dailyWageRate

    val weekdayRecords = currentMonthRecords.filter { it !in sundayRecords }
    val weekdayPresent = weekdayRecords.count { it.status == "P" || it.status == "PRESENT" }
    val weekdayHalfDay = weekdayRecords.count { it.status.startsWith("HD") || it.status == "HALF-DAY" }
    val weekdayAbsent = weekdayRecords.count { it.status == "A" || it.status == "ABSENT" }

    val fullDayAbsentDeduction = weekdayAbsent.toDouble() * dailyWageRate
    val halfDayDeduction = weekdayHalfDay.toDouble() * (dailyWageRate / 2.0)
    val absentDeduction = fullDayAbsentDeduction + halfDayDeduction

    val earnedBaseSalary = if (monthlyBaseSalary > 0) (monthlyBaseSalary - absentDeduction).coerceAtLeast(0.0) else 0.0
    val earnedSalaryThisMonth = earnedBaseSalary + sundayExtraBonusAmount

    val totalDeductionDays = weekdayAbsent.toDouble() + (0.5 * weekdayHalfDay.toDouble())
    val monthEffectivePaidDays = (30.0 - totalDeductionDays + sundayBonusDays).coerceAtLeast(0.0)

    val teaAllowance = remember(student.id, currentMonthKey) { payrollPrefs.getTeaAllowance(student.id, currentMonthKey) }
    val otherBonus = remember(student.id, currentMonthKey) { payrollPrefs.getOtherBonus(student.id, currentMonthKey) }
    val salaryAdvances = remember(student.id, currentMonthKey) { payrollPrefs.getSalaryAdvances(student.id, currentMonthKey) }
    val totalSalaryAdvance = salaryAdvances.sumOf { it.amount }
    val grossSalaryThisMonth = earnedSalaryThisMonth + teaAllowance + otherBonus
    val netSalaryPayableThisMonth = grossSalaryThisMonth - totalSalaryAdvance
    val (isSettled, settleMode) = remember(student.id, currentMonthKey) { payrollPrefs.getSettledInfo(student.id, currentMonthKey) }

    // -------------------------------------------------------------
    // TASKS & CHECKLIST STATS
    // -------------------------------------------------------------
    val completedTasksCount = employeeTasks.count { it.isDoneToday }
    val pendingTasksCount = employeeTasks.count { !it.isDoneToday }

    // -------------------------------------------------------------
    // COMPLAINT & APPRECIATION STATS
    // -------------------------------------------------------------
    val employeeFeedback = remember(allStaffFeedback, student.id) {
        allStaffFeedback.filter { it.studentId == student.id }
    }
    val appreciationCount = employeeFeedback.count { it.entryType == "APPRECIATION" }
    val complaintCount = employeeFeedback.count { it.entryType == "COMPLAINT" }
    val openComplaintCount = employeeFeedback.count { it.entryType == "COMPLAINT" && !it.isResolved }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("employee_profile_dialog_${student.id}"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ==========================================
                // 1. TOP HEADER: Staff Identity & Actions
                // ==========================================
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Avatar circle
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2563EB).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cleanName.take(1).uppercase(Locale.getDefault()),
                                        color = Color(0xFF2563EB),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 22.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = cleanName,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = "ID: ${student.rollNumber.trim()}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                            )
                                        }

                                        if (shift != null) {
                                            Text(
                                                text = "⏰ ${shift.displayName} (${shift.timings})",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }

                            // Close Button
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        // Contact actions toolbar if phone is available
                        val phone = student.phone.trim()
                        if (phone.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = phone,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                // Direct Call
                                FilledActionPill(
                                    icon = Icons.Default.Call,
                                    label = "Call",
                                    color = Color(0xFF2563EB),
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                        context.startActivity(intent)
                                    }
                                )

                                // Direct WhatsApp
                                FilledActionPill(
                                    icon = Icons.Default.Share,
                                    label = "WhatsApp",
                                    color = Color(0xFF16A34A),
                                    onClick = {
                                        val clean = phone.replace(Regex("[^0-9]"), "")
                                        val formatted = if (clean.length == 10) "91$clean" else clean
                                        val uri = Uri.parse("https://wa.me/$formatted")
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        try {
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                            Toast.makeText(context, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )

                                // Direct SMS
                                FilledActionPill(
                                    icon = Icons.Default.Sms,
                                    label = "SMS",
                                    color = Color(0xFF4F46E5),
                                    onClick = {
                                        val uri = Uri.parse("smsto:$phone")
                                        val intent = Intent(Intent.ACTION_SENDTO, uri)
                                        try {
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                            Toast.makeText(context, "Could not open SMS", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 2. TAB ROW
                // ==========================================
                val tabs = listOf(
                    "Overview" to Icons.Default.TrendingUp,
                    "Attendance" to Icons.Default.CalendarMonth,
                    "T-Hisab" to Icons.Default.ReceiptLong,
                    "Payroll" to Icons.Default.Payments,
                    "Tasks & Check ($pendingTasksCount)" to Icons.Default.Checklist,
                    "Complaint & Praise (${employeeFeedback.size})" to Icons.Default.ThumbUp
                )

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 12.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, (label, icon) ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            modifier = Modifier.testTag("profile_tab_$index"),
                            text = {
                                Text(
                                    text = label,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            },
                            icon = {
                                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }
                }

                // ==========================================
                // 3. TAB CONTENT
                // ==========================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (selectedTab) {
                        0 -> ProfileOverviewTab(
                            student = student,
                            totalRecordedDays = totalRecordedDays,
                            presentCount = presentCount,
                            absentCount = absentCount,
                            halfDayCount = halfDayCount,
                            attendanceRate = attendanceRate,
                            totalPieces = totalPieces,
                            totalEarned = totalEarned,
                            totalAdvancePaid = totalAdvancePaid,
                            netHisabBalance = netHisabBalance,
                            monthlyBaseSalary = monthlyBaseSalary,
                            grossSalaryThisMonth = grossSalaryThisMonth,
                            netSalaryPayableThisMonth = netSalaryPayableThisMonth,
                            totalSalaryAdvance = totalSalaryAdvance,
                            isSettled = isSettled,
                            completedTasksCount = completedTasksCount,
                            pendingTasksCount = pendingTasksCount,
                            appreciationCount = appreciationCount,
                            complaintCount = complaintCount,
                            openComplaintCount = openComplaintCount,
                            recentRecords = employeeRecords.take(5),
                            onGoToAttendance = { selectedTab = 1 },
                            onGoToHisab = {
                                onDismiss()
                                onOpenHisabTab(student)
                            },
                            onGoToPayroll = {
                                onDismiss()
                                onOpenPayrollTab(student)
                            },
                            onGoToTasks = { selectedTab = 4 },
                            onGoToFeedback = { selectedTab = 5 }
                        )
                        1 -> ProfileAttendanceTab(
                            student = student,
                            records = employeeRecords,
                            totalRecordedDays = totalRecordedDays,
                            presentCount = presentCount,
                            absentCount = absentCount,
                            halfDayCount = halfDayCount,
                            attendanceRate = attendanceRate,
                            currentMonthDisplayName = currentMonthDisplayName,
                            monthPresent = monthPresentCount,
                            monthAbsent = monthAbsentCount,
                            monthHalfDay = monthHalfDayCount,
                            onOpenFullAttendance = {
                                onDismiss()
                                onOpenAttendanceTab()
                            }
                        )
                        2 -> ProfileHisabTab(
                            student = student,
                            entries = employeeHisabEntries,
                            totalPieces = totalPieces,
                            totalEarned = totalEarned,
                            totalAdvance = totalAdvancePaid,
                            netBalance = netHisabBalance,
                            onOpenFullHisab = {
                                onDismiss()
                                onOpenHisabTab(student)
                            }
                        )
                        3 -> ProfilePayrollTab(
                            student = student,
                            monthlyBaseSalary = monthlyBaseSalary,
                            dailyWageRate = dailyWageRate,
                            paidSundaysCount = sundayBonusDays,
                            sundayExtraBonusAmount = sundayExtraBonusAmount,
                            absentDaysCount = weekdayAbsent.toDouble(),
                            halfDaysCount = weekdayHalfDay.toDouble(),
                            monthEffectivePaidDays = monthEffectivePaidDays,
                            earnedSalary = earnedSalaryThisMonth,
                            teaAllowance = teaAllowance,
                            otherBonus = otherBonus,
                            grossSalary = grossSalaryThisMonth,
                            totalSalaryAdvance = totalSalaryAdvance,
                            netSalaryPayable = netSalaryPayableThisMonth,
                            isSettled = isSettled,
                            settleMode = settleMode,
                            currentMonthName = currentMonthDisplayName,
                            advancesList = salaryAdvances,
                            onOpenFullPayroll = {
                                onDismiss()
                                onOpenPayrollTab(student)
                            }
                        )
                        4 -> ProfileTasksTab(
                            student = student,
                            tasks = employeeTasks,
                            onToggleChecklistItem = onToggleChecklistItem,
                            onScheduleSmsForTask = onScheduleSmsForTask,
                            onReorderTasks = onReorderTasks,
                            onOpenFullTasks = {
                                onDismiss()
                                onOpenChecklistTab(student)
                            }
                        )
                        5 -> StaffFeedbackTabContent(
                            student = student,
                            feedbackList = employeeFeedback,
                            onAddFeedback = onAddFeedback,
                            onUpdateFeedback = onUpdateFeedback,
                            onToggleResolved = onToggleFeedbackResolved,
                            onDeleteFeedback = onDeleteFeedback
                        )
                    }
                }
            }
        }
    }
}

/**
 * Quick Action Button helper
 */
@Composable
private fun FilledActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

/**
 * Overview Tab: High-level 360 summary of Attendance, T-Hisab, Payroll, and Tasks
 */
@Composable
private fun ProfileOverviewTab(
    student: StudentEntity,
    totalRecordedDays: Int,
    presentCount: Int,
    absentCount: Int,
    halfDayCount: Int,
    attendanceRate: Double,
    totalPieces: Int,
    totalEarned: Double,
    totalAdvancePaid: Double,
    netHisabBalance: Double,
    monthlyBaseSalary: Double,
    grossSalaryThisMonth: Double,
    netSalaryPayableThisMonth: Double,
    totalSalaryAdvance: Double,
    isSettled: Boolean,
    completedTasksCount: Int,
    pendingTasksCount: Int,
    appreciationCount: Int,
    complaintCount: Int,
    openComplaintCount: Int,
    recentRecords: List<AttendanceRecordEntity>,
    onGoToAttendance: () -> Unit,
    onGoToHisab: () -> Unit,
    onGoToPayroll: () -> Unit,
    onGoToTasks: () -> Unit,
    onGoToFeedback: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 4 KPI Metric Cards in a 2x2 Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Attendance Rate KPI
            MetricSummaryCard(
                title = "Attendance",
                value = if (totalRecordedDays > 0) String.format(Locale.getDefault(), "%.1f%%", attendanceRate) else "No Logs",
                subtitle = "$presentCount Present • $absentCount Absent",
                badge = if (attendanceRate >= 85.0) "Excellent" else if (attendanceRate >= 70.0) "Good" else "Low",
                color = if (attendanceRate >= 80.0) Color(0xFF16A34A) else if (attendanceRate >= 65.0) Color(0xFFD97706) else Color(0xFFDC2626),
                modifier = Modifier.weight(1f),
                onClick = onGoToAttendance
            )

            // 2. T-Hisab KPI
            MetricSummaryCard(
                title = "T-Hisab Balance",
                value = String.format(Locale.getDefault(), "₹%.0f", netHisabBalance),
                subtitle = "$totalPieces Pcs • Earned ₹${totalEarned.toInt()}",
                badge = if (netHisabBalance >= 0) "Payable" else "Adv Excess",
                color = Color(0xFF2563EB),
                modifier = Modifier.weight(1f),
                onClick = onGoToHisab
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 3. Payroll KPI
            MetricSummaryCard(
                title = "Monthly Payroll",
                value = if (monthlyBaseSalary > 0) String.format(Locale.getDefault(), "₹%.0f", netSalaryPayableThisMonth) else "Not Set",
                subtitle = if (monthlyBaseSalary > 0) "Base ₹${monthlyBaseSalary.toInt()} • Adv ₹${totalSalaryAdvance.toInt()}" else "Tap to configure wage",
                badge = if (isSettled) "✓ Settled" else "Pending",
                color = if (isSettled) Color(0xFF15803D) else Color(0xFF7C3AED),
                modifier = Modifier.weight(1f),
                onClick = onGoToPayroll
            )

            // 4. Tasks & Checklist KPI
            MetricSummaryCard(
                title = "Tasks & Checklist",
                value = "$completedTasksCount Done",
                subtitle = if (pendingTasksCount > 0) "$pendingTasksCount Pending • Tap to reorder" else "All Done ✓",
                badge = if (pendingTasksCount == 0) "Clean" else "$pendingTasksCount Active",
                color = if (pendingTasksCount > 0) Color(0xFFEA580C) else Color(0xFF16A34A),
                modifier = Modifier.weight(1f),
                onClick = onGoToTasks
            )
        }

        // Complaint & Appreciation Overview KPI Banner
        MetricSummaryCard(
            title = "Complaint & Appreciation",
            value = "$appreciationCount Appreciation • $complaintCount Complaints",
            subtitle = if (openComplaintCount > 0) "$openComplaintCount open complaint(s) need attention" else "Tap to view or log praise & complaints",
            badge = if (openComplaintCount > 0) "$openComplaintCount Open" else "Good Record",
            color = if (openComplaintCount > 0) Color(0xFFDC2626) else Color(0xFF16A34A),
            modifier = Modifier.fillMaxWidth(),
            onClick = onGoToFeedback
        )

        // Quick Jump Section Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Quick Module Jump",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onGoToHisab,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("T-Hisab", fontSize = 11.sp, maxLines = 1)
                    }

                    OutlinedButton(
                        onClick = onGoToPayroll,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Payroll", fontSize = 11.sp, maxLines = 1)
                    }

                    OutlinedButton(
                        onClick = onGoToTasks,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tasks", fontSize = 11.sp, maxLines = 1)
                    }
                }
            }
        }

        // Recent 5 Attendance Logs Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Attendance History",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    TextButton(
                        onClick = onGoToAttendance,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("View All", fontSize = 11.sp)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp))
                    }
                }

                if (recentRecords.isEmpty()) {
                    Text(
                        text = "No attendance records registered yet.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    recentRecords.forEach { rec ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = rec.dateString,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                val timeText = when {
                                    rec.checkInTime.isNotBlank() && rec.checkOutTime.isNotBlank() ->
                                        "${rec.checkInTime} - ${rec.checkOutTime}"
                                    rec.checkInTime.isNotBlank() -> "In: ${rec.checkInTime}"
                                    else -> "No time logged"
                                }
                                Text(
                                    text = timeText,
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            StatusBadge(status = rec.status)
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 2.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Metric summary card for KPI overview
 */
@Composable
private fun MetricSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    badge: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = color.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Attendance Tab: Detailed Breakdown & Full Attendance Log
 */
@Composable
private fun ProfileAttendanceTab(
    student: StudentEntity,
    records: List<AttendanceRecordEntity>,
    totalRecordedDays: Int,
    presentCount: Int,
    absentCount: Int,
    halfDayCount: Int,
    attendanceRate: Double,
    currentMonthDisplayName: String,
    monthPresent: Int,
    monthAbsent: Int,
    monthHalfDay: Int,
    onOpenFullAttendance: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Summary Header Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Attendance Summary",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$totalRecordedDays total days recorded",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Large circular progress gauge
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { (attendanceRate / 100.0).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier.size(48.dp),
                            color = if (attendanceRate >= 80) Color(0xFF16A34A) else if (attendanceRate >= 65) Color(0xFFD97706) else Color(0xFFDC2626),
                            strokeWidth = 5.dp,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Text(
                            text = "${attendanceRate.toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Breakdown Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatPill(label = "Present", count = presentCount, color = Color(0xFF16A34A), modifier = Modifier.weight(1f))
                    StatPill(label = "Half-Day", count = halfDayCount, color = Color(0xFFD97706), modifier = Modifier.weight(1f))
                    StatPill(label = "Absent", count = absentCount, color = Color(0xFFDC2626), modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$currentMonthDisplayName: $monthPresent Present • $monthHalfDay Half-Day • $monthAbsent Absent",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Attendance Records List
        Text(
            text = "Full Attendance Logs (${records.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No attendance history for this employee.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(records, key = { it.id }) { rec ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = rec.dateString,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                val timings = when {
                                    rec.checkInTime.isNotBlank() && rec.checkOutTime.isNotBlank() ->
                                        "In: ${rec.checkInTime}  •  Out: ${rec.checkOutTime}"
                                    rec.checkInTime.isNotBlank() -> "In: ${rec.checkInTime}"
                                    else -> "No check-in time recorded"
                                }
                                Text(
                                    text = timings,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (rec.notes.isNotBlank() || rec.reason.isNotBlank()) {
                                    val noteText = listOf(rec.notes, rec.reason).filter { it.isNotBlank() }.joinToString(" • ")
                                    Text(
                                        text = "Note: $noteText",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            StatusBadge(status = rec.status)
                        }
                    }
                }
            }
        }
    }
}

/**
 * T-Hisab Tab: Sales, Piece-Rate Earnings, and Advance Records
 */
@Composable
private fun ProfileHisabTab(
    student: StudentEntity,
    entries: List<HisabLedgerEntryEntity>,
    totalPieces: Int,
    totalEarned: Double,
    totalAdvance: Double,
    netBalance: Double,
    onOpenFullHisab: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // T-Hisab Balance Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFEFF6FF),
            border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "T-Hisab Ledger Balance",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8)
                        )
                        Text(
                            text = String.format(Locale.getDefault(), "₹%.2f", netBalance),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = if (netBalance >= 0) Color(0xFF15803D) else Color(0xFFDC2626)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF2563EB).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (netBalance >= 0) "Payable to Staff" else "Advance Excess",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFBFDBFE))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Pieces", fontSize = 10.5.sp, color = Color(0xFF1E40AF))
                        Text("$totalPieces Pcs", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                    }
                    Column {
                        Text("Total Earned", fontSize = 10.5.sp, color = Color(0xFF1E40AF))
                        Text(String.format(Locale.getDefault(), "₹%.2f", totalEarned), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                    }
                    Column {
                        Text("Advance Taken", fontSize = 10.5.sp, color = Color(0xFF1E40AF))
                        Text(String.format(Locale.getDefault(), "₹%.2f", totalAdvance), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                    }
                }
            }
        }

        // Ledger Entries List
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ledger History (${entries.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextButton(
                onClick = onOpenFullHisab,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("Open in T-Hisab", fontSize = 11.sp)
                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
            }
        }

        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No T-Hisab sales/ledger entries recorded.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = when (entry.entryType) {
                                            "ADVANCE" -> Color(0xFFFEE2E2)
                                            "SETTLEMENT" -> Color(0xFFFEF3C7)
                                            else -> Color(0xFFDCFCE7)
                                        }
                                    ) {
                                        Text(
                                            text = entry.entryType,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (entry.entryType) {
                                                "ADVANCE" -> Color(0xFFB91C1C)
                                                "SETTLEMENT" -> Color(0xFFB45309)
                                                else -> Color(0xFF15803D)
                                            },
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = entry.dateString,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Text(
                                    text = if (entry.entryType == "ADVANCE")
                                        String.format(Locale.getDefault(), "- ₹%.2f", entry.advancePaid)
                                    else
                                        String.format(Locale.getDefault(), "+ ₹%.2f", entry.totalEarned),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (entry.entryType == "ADVANCE") Color(0xFFDC2626) else Color(0xFF15803D)
                                )
                            }

                            if (entry.totalPieces > 0) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Pieces: ${entry.totalPieces}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (entry.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Notes: ${entry.notes}",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Payroll Tab: Monthly Salary, Working Days, Deductions & Net Pay
 */
@Composable
private fun ProfilePayrollTab(
    student: StudentEntity,
    monthlyBaseSalary: Double,
    dailyWageRate: Double,
    paidSundaysCount: Double = 0.0,
    sundayExtraBonusAmount: Double = 0.0,
    absentDaysCount: Double = 0.0,
    halfDaysCount: Double = 0.0,
    monthEffectivePaidDays: Double,
    earnedSalary: Double,
    teaAllowance: Double,
    otherBonus: Double,
    grossSalary: Double,
    totalSalaryAdvance: Double,
    netSalaryPayable: Double,
    isSettled: Boolean,
    settleMode: String,
    currentMonthName: String,
    advancesList: List<com.example.data.SalaryAdvanceRecord>,
    onOpenFullPayroll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Net Payable Hero Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFFAF5FF),
            border = BorderStroke(1.dp, Color(0xFFC084FC).copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Net Payable ($currentMonthName)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7E22CE)
                        )
                        Text(
                            text = String.format(Locale.getDefault(), "₹%.2f", netSalaryPayable),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF6B21A8)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSettled) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = if (isSettled) "✓ Settled ($settleMode)" else "⏳ Pending Payout",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSettled) Color(0xFF15803D) else Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFE9D5FF))
                Spacer(modifier = Modifier.height(8.dp))

                // Breakdown Line Items
                PayrollLineItem(title = "Monthly Base Wage (30-day style)", amount = String.format(Locale.getDefault(), "₹%.2f", monthlyBaseSalary))
                PayrollLineItem(title = "PER Day Salary (Salary ÷ 30)", amount = String.format(Locale.getDefault(), "₹%.2f / day", dailyWageRate))
                if (sundayExtraBonusAmount > 0) {
                    PayrollLineItem(
                        title = "🌟 Sunday Extra Bonus (${paidSundaysCount} Sun)",
                        amount = String.format(Locale.getDefault(), "+ ₹%.2f", sundayExtraBonusAmount),
                        color = Color(0xFF16A34A)
                    )
                }
                PayrollLineItem(title = "Effective Paid Days", amount = String.format(Locale.getDefault(), "%.1f days", monthEffectivePaidDays))
                if (absentDaysCount > 0) {
                    PayrollLineItem(
                        title = "❌ Absent Deduction (${absentDaysCount.toInt()}d)",
                        amount = String.format(Locale.getDefault(), "- ₹%.2f", absentDaysCount * dailyWageRate),
                        color = Color(0xFFDC2626)
                    )
                }
                if (halfDaysCount > 0) {
                    val halfRate = dailyWageRate / 2.0
                    PayrollLineItem(
                        title = "⏳ Half-Day (${halfDaysCount.toInt()} HD @ PER Day ÷ 2)",
                        amount = String.format(Locale.getDefault(), "- ₹%.2f", halfDaysCount * halfRate),
                        color = Color(0xFFDC2626)
                    )
                }
                PayrollLineItem(title = "Total Earned Salary (कुल अर्जित)", amount = String.format(Locale.getDefault(), "₹%.2f", earnedSalary))
                if (teaAllowance > 0) {
                    PayrollLineItem(title = "Tea Allowance (+)", amount = String.format(Locale.getDefault(), "+ ₹%.2f", teaAllowance), color = Color(0xFF16A34A))
                }
                if (otherBonus > 0) {
                    PayrollLineItem(title = "Other Bonus (+)", amount = String.format(Locale.getDefault(), "+ ₹%.2f", otherBonus), color = Color(0xFF16A34A))
                }
                if (totalSalaryAdvance > 0) {
                    PayrollLineItem(title = "Salary Advance (-)", amount = String.format(Locale.getDefault(), "- ₹%.2f", totalSalaryAdvance), color = Color(0xFFDC2626))
                }
            }
        }

        // Open in full payroll button
        OutlinedButton(
            onClick = onOpenFullPayroll,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Open Full Payroll Sheet", fontWeight = FontWeight.Bold)
        }

        // Salary Advances History this month
        if (advancesList.isNotEmpty()) {
            Text(
                text = "Salary Advances Taken (${advancesList.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            advancesList.forEach { adv ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(adv.dateString, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            if (adv.notes.isNotBlank()) {
                                Text("Note: ${adv.notes}", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Text(
                            text = String.format(Locale.getDefault(), "- ₹%.2f", adv.amount),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PayrollLineItem(title: String, amount: String, color: Color = Color.Unspecified) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(amount, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

/**
 * Tasks & Daily Checklist Tab: Separated Daily Checklist vs Assigned Tasks with Drag/Up/Down Reordering
 */
@Composable
private fun ProfileTasksTab(
    student: StudentEntity,
    tasks: List<ChecklistItemEntity>,
    onToggleChecklistItem: (ChecklistItemEntity) -> Unit,
    onScheduleSmsForTask: (ChecklistItemEntity) -> Unit,
    onReorderTasks: (List<ChecklistItemEntity>) -> Unit,
    onOpenFullTasks: () -> Unit
) {
    val checklistItems = remember(tasks) { tasks.filter { it.isChecklist } }
    val taskItems = remember(tasks) { tasks.filter { it.isTask } }

    var selectedListType by remember(student.id) {
        mutableStateOf(if (checklistItems.isNotEmpty() || taskItems.isEmpty()) "CHECKLIST" else "TASK")
    }
    val activeList = if (selectedListType == "CHECKLIST") checklistItems else taskItems
    val isChecklistTab = selectedListType == "CHECKLIST"
    val accentColor = if (isChecklistTab) Color(0xFF7C3AED) else Color(0xFFEA580C)

    var showReorderDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Sub-Tab Switcher: Daily Checklist vs Tasks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val checklistDone = checklistItems.count { it.isDoneToday }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isChecklistTab) Color(0xFF7C3AED) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clickable { selectedListType = "CHECKLIST" }
                    .testTag("btn_profile_subtab_checklist")
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text(
                        text = "Checklist ($checklistDone/${checklistItems.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isChecklistTab) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            val taskDone = taskItems.count { it.isDoneToday }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (!isChecklistTab) Color(0xFFEA580C) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clickable { selectedListType = "TASK" }
                    .testTag("btn_profile_subtab_tasks")
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text(
                        text = "Tasks ($taskDone/${taskItems.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isChecklistTab) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Header + Reorder + Manage
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isChecklistTab) "Daily Checklist (${activeList.size})" else "Assigned Tasks (${activeList.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = accentColor,
                modifier = Modifier.weight(1f, fill = false),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (activeList.size > 1) {
                    OutlinedButton(
                        onClick = { showReorderDialog = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("btn_profile_reorder_${selectedListType.lowercase()}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = if (isChecklistTab) "Reorder Checklist" else "Reorder Tasks",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Reorder", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(
                    onClick = onOpenFullTasks,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Manage", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
                }
            }
        }

        if (activeList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isChecklistTab) {
                        "No daily checklist items assigned to ${student.displayName}."
                    } else {
                        "No tasks assigned to ${student.displayName}."
                    },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(activeList, key = { it.id }) { item ->
                    val index = activeList.indexOfFirst { it.id == item.id }
                    val isDone = item.isDoneToday
                    val canMoveUp = index > 0 && activeList[index - 1].isDoneToday == isDone
                    val canMoveDown = index >= 0 && index < activeList.size - 1 && activeList[index + 1].isDoneToday == isDone

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            1.dp,
                            if (isDone) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f) else accentColor.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onToggleChecklistItem(item) }
                                        .testTag("checkbox_profile_item_${item.id}"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.Today,
                                        contentDescription = if (isDone) "Completed, tap to unmark" else "Pending, tap to complete",
                                        tint = if (isDone) Color(0xFF16A34A) else accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isDone) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                    modifier = Modifier.clickable { onToggleChecklistItem(item) }
                                ) {
                                    Text(
                                        text = if (isDone) "✓ Done" else "Pending",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDone) Color(0xFF15803D) else Color(0xFFB45309),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            if (item.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Notes: ${item.notes}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (item.todayComment.isNotBlank()) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "📌 Today's Note: ${item.todayComment}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF92400E)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (activeList.size > 1) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (canMoveUp) accentColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clickable(enabled = canMoveUp) {
                                                    if (canMoveUp) {
                                                        val mutable = activeList.toMutableList()
                                                        mutable[index] = mutable[index - 1]
                                                        mutable[index - 1] = item
                                                        onReorderTasks(mutable)
                                                    }
                                                }
                                                .testTag("btn_profile_task_up_${item.id}")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.KeyboardArrowUp,
                                                    contentDescription = "Move up",
                                                    tint = if (canMoveUp) accentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (canMoveDown) accentColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clickable(enabled = canMoveDown) {
                                                    if (canMoveDown) {
                                                        val mutable = activeList.toMutableList()
                                                        mutable[index] = mutable[index + 1]
                                                        mutable[index + 1] = item
                                                        onReorderTasks(mutable)
                                                    }
                                                }
                                                .testTag("btn_profile_task_down_${item.id}")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.KeyboardArrowDown,
                                                    contentDescription = "Move down",
                                                    tint = if (canMoveDown) accentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                OutlinedButton(
                                    onClick = { onScheduleSmsForTask(item) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    border = BorderStroke(1.dp, Color(0xFF4F46E5).copy(alpha = 0.4f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4F46E5))
                                ) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Schedule SMS", fontSize = 10.5.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showReorderDialog) {
        RearrangeEmployeeTasksDialog(
            scopeName = "${student.displayName} • ${if (isChecklistTab) "Checklist" else "Tasks"}",
            tasks = activeList,
            dialogTitle = if (isChecklistTab) "Reorder Daily Checklist" else "Reorder Tasks",
            itemLabel = if (isChecklistTab) "items" else "tasks",
            onDismiss = { showReorderDialog = false },
            onSaveOrder = { newOrderedList ->
                onReorderTasks(newOrderedList)
                showReorderDialog = false
            }
        )
    }
}

/**
 * Helper status badge for Attendance
 */
@Composable
private fun StatusBadge(status: String) {
    val (label, bg, fg) = when (status) {
        "P", "PRESENT" -> Triple("PRESENT", Color(0xFFDCFCE7), Color(0xFF15803D))
        "A", "ABSENT" -> Triple("ABSENT", Color(0xFFFEE2E2), Color(0xFFB91C1C))
        "HD MOR" -> Triple("HD MOR", Color(0xFFFEF3C7), Color(0xFFB45309))
        "HD EVE" -> Triple("HD EVE", Color(0xFFFEF3C7), Color(0xFFB45309))
        "HALF-DAY" -> Triple("HALF DAY", Color(0xFFFEF3C7), Color(0xFFB45309))
        else -> Triple("UNMARKED", Color(0xFFF3F4F6), Color(0xFF6B7280))
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bg
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

/**
 * Breakdown pill for Attendance stats
 */
@Composable
private fun StatPill(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = color
            )
        }
    }
}
