package com.example.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

enum class PayrollViewMode {
    ALL_STAFF_REGISTER,
    INDIVIDUAL_STAFF
}

data class DayPayrollInfo(
    val dayNumber: Int,
    val dateKey: String, // "YYYY-MM-DD"
    val dateLabel: String, // "01/08"
    val dayOfWeek: String, // "Mon", "Tue", ..., "Sun"
    val isSunday: Boolean,
    val fullDisplayDate: String
)

data class StaffPayrollCalculation(
    val student: StudentEntity,
    val monthlySalary: Double,
    val workingDaysDivisor: Double,
    val dailyRate: Double,
    val baseWorkingDays: Int, // Mon-Sat in month
    val weekdayPresent: Int,
    val weekdayHalfDay: Int,
    val weekdayAbsent: Int,
    val weekdayUnmarked: Int,
    val totalSundaysInMonth: Int = 0,
    val sundayWorked: Int, // Sunday attended
    val sundayHalfDayWorked: Int,
    val sundayAbsent: Int = 0,
    val sundayOffOrAbsent: Int,
    val paidSundays: Double = 0.0,
    val totalEffectivePaidDays: Double,
    val earnedBaseSalary: Double,
    val absentDeduction: Double,
    val fullDayAbsentDeduction: Double = 0.0,
    val halfDayDeduction: Double = 0.0,
    val sundayExtraPay: Double,
    val teaAllowance: Double,
    val otherBonus: Double,
    val grossEarnings: Double,
    // SEPARATE SALARY ADVANCE (Deducted from monthly salary)
    val salaryAdvances: List<SalaryAdvanceRecord>,
    val totalSalaryAdvance: Double,
    // SEPARATE T-HISAB ADVANCE (Strictly informational, settled in T-Hisab against sales)
    val tHisabAdvanceTotal: Double,
    val tHisabAdvanceEntries: List<HisabLedgerEntryEntity>,
    val finalAdvanceDeducted: Double, // Equals totalSalaryAdvance
    val netPayable: Double,
    val isSettled: Boolean,
    val settleMode: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayrollView(
    activeClass: ClassEntity,
    students: List<StudentEntity>,
    allClasses: List<ClassEntity>,
    onSelectClass: (ClassEntity) -> Unit,
    allRecords: List<AttendanceRecordEntity>,
    allHisabLedgerEntries: List<HisabLedgerEntryEntity>,
    onRecordAdvance: (studentId: Long, dateString: String, amount: Double, notes: String) -> Unit,
    onUpdateRecord: (studentId: Long, dateString: String, status: String, inT: String, outT: String, notes: String, reason: String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val payrollPrefs = remember { PayrollPreferences(context) }

    val calendar = remember { Calendar.getInstance() }
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) } // 1..12
    var viewMode by remember { mutableStateOf(PayrollViewMode.ALL_STAFF_REGISTER) }
    var selectedStudentId by remember { mutableStateOf(students.firstOrNull()?.id ?: 0L) }
    var searchQuery by remember { mutableStateOf("") }
    var showMonthPicker by remember { mutableStateOf(false) }

    // Dialog States
    var showPayrollSettingsDialog by remember { mutableStateOf(false) }
    var editingSalaryForStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var editingTeaForStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var recordingSalaryAdvanceForStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var editingSalaryAdvanceRecord by remember { mutableStateOf<Pair<StudentEntity, SalaryAdvanceRecord>?>(null) }
    var showingSalarySlipForStaff by remember { mutableStateOf<StaffPayrollCalculation?>(null) }
    var settlingPaymentForStaff by remember { mutableStateOf<StaffPayrollCalculation?>(null) }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    val monthNames = remember {
        listOf("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER")
    }
    val currentMonthName = monthNames.getOrElse(selectedMonth - 1) { "AUGUST" }
    val yearMonthKey = String.format(Locale.US, "%04d-%02d", selectedYear, selectedMonth)

    // Calculate Days in Selected Month
    val maxDays = remember(selectedYear, selectedMonth) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, selectedYear)
        cal.set(Calendar.MONTH, selectedMonth - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    val dayInfos = remember(selectedYear, selectedMonth, maxDays) {
        (1..maxDays).map { day ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, selectedYear)
                set(Calendar.MONTH, selectedMonth - 1)
                set(Calendar.DAY_OF_MONTH, day)
            }
            val dayOfWeek = SimpleDateFormat("EEE", Locale.US).format(cal.time)
            val isSunday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
            val dateKey = String.format(Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth, day)
            val dateLabel = String.format(Locale.US, "%02d/%02d", day, selectedMonth)
            val fullDisplayDate = SimpleDateFormat("dd MMMM yyyy (EEE)", Locale.US).format(cal.time)
            DayPayrollInfo(day, dateKey, dateLabel, dayOfWeek, isSunday, fullDisplayDate)
        }
    }

    // Standard 6-day working week: Mon to Sat
    val baseWorkingDaysCount = remember(dayInfos) {
        dayInfos.count { !it.isSunday }
    }

    // Filter attendance records for selected month
    val recordsByStudent = remember(allRecords, yearMonthKey) {
        allRecords.filter { it.dateString.startsWith(yearMonthKey) }
            .groupBy { it.studentId }
            .mapValues { (_, list) -> list.associateBy { it.dateString } }
    }

    // Helper to check if a T-Hisab advance falls in this month
    fun isEntryInSelectedMonth(entry: HisabLedgerEntryEntity): Boolean {
        val dateStr = entry.dateString.trim()
        if (dateStr.startsWith(yearMonthKey)) return true
        val parts = dateStr.split("/", "-", ".")
        if (parts.size >= 3) {
            val p0 = parts[0].toIntOrNull()
            val p1 = parts[1].toIntOrNull()
            val p2 = parts[2].toIntOrNull()
            if (p0 != null && p1 != null && p2 != null) {
                val y = if (p2 >= 100) p2 else if (p0 >= 100) p0 else 2000 + p2
                val m = if (p2 >= 100) p1 else if (p0 >= 100) p1 else p1
                if (y == selectedYear && m == selectedMonth) return true
            }
        }
        if (entry.timestamp > 0L) {
            val cal = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
            if (cal.get(Calendar.YEAR) == selectedYear && (cal.get(Calendar.MONTH) + 1) == selectedMonth) {
                return true
            }
        }
        return false
    }

    // T-Hisab advances by student for selected month (SEPARATE from salary advances)
    val tHisabAdvancesByStudent = remember(allHisabLedgerEntries, selectedYear, selectedMonth) {
        allHisabLedgerEntries.filter { it.entryType == "ADVANCE" && isEntryInSelectedMonth(it) }
            .groupBy { it.studentId }
    }

    // Compute Payroll Calculation for every student
    val payrollCalculations = remember(
        students, recordsByStudent, tHisabAdvancesByStudent, dayInfos, baseWorkingDaysCount,
        refreshTrigger, selectedYear, selectedMonth
    ) {
        students.map { student ->
            val monthlySalary = payrollPrefs.getSalary(student.id)
            val totalSundaysCount = dayInfos.count { it.isSunday }
            // Rule: PER day salary of employee == Salary of employee / 30 (divided by 30 for every month of year)
            val divisor = 30.0
            val dailyRate = if (monthlySalary > 0) monthlySalary / 30.0 else 0.0

            val recMap = recordsByStudent[student.id] ?: emptyMap()
            var weekdayPresent = 0
            var weekdayHalfDay = 0
            var weekdayAbsent = 0
            var weekdayUnmarked = 0
            var sundayWorked = 0
            var sundayHalfDayWorked = 0
            var sundayMarkedAbsent = 0
            var sundayOff = 0

            for (day in dayInfos) {
                val rec = recMap[day.dateKey]
                val status = rec?.status ?: ""
                if (!day.isSunday) {
                    when {
                        status == "P" || status == "PRESENT" -> weekdayPresent++
                        status.startsWith("HD") || status == "HALF-DAY" -> weekdayHalfDay++
                        status == "A" || status == "ABSENT" -> weekdayAbsent++
                        else -> weekdayUnmarked++
                    }
                } else {
                    when {
                        status == "P" || status == "PRESENT" -> sundayWorked++
                        status.startsWith("HD") || status == "HALF-DAY" -> sundayHalfDayWorked++
                        status == "A" || status == "ABSENT" -> sundayMarkedAbsent++
                        else -> sundayOff++
                    }
                }
            }

            // In 30-days style payroll:
            // 1. PER day salary of employee == Salary of employee / 30 (for every month of year)
            // 2. One Sunday wage = per day salary of employee (e.g. ₹800/Sunday)
            // 3. Sunday Extra Bonus: Working on Sunday adds +1 Sunday wage (per day salary) to earnings
            // 4. Half day present subtracts PER day salary / 2 in absent deductions
            val sundayBonusDays = sundayWorked.toDouble() + (0.5 * sundayHalfDayWorked.toDouble())
            val sundayExtraPay = sundayBonusDays * dailyRate

            val fullDayAbsentDeduction = weekdayAbsent.toDouble() * dailyRate
            val halfDayDeduction = weekdayHalfDay.toDouble() * (dailyRate / 2.0)
            val absentDeduction = fullDayAbsentDeduction + halfDayDeduction

            // Base salary earned after absent deductions (e.g. ₹24,000 - ₹7,200 - ₹400 = ₹16,400)
            val earnedBaseSalary = if (monthlySalary > 0) (monthlySalary - absentDeduction).coerceAtLeast(0.0) else 0.0

            // Total Earned Salary INCLUDING Sunday Extra Bonus (e.g. ₹16,400 + ₹3,200 = ₹19,600)
            val totalEarnedSalary = earnedBaseSalary + sundayExtraPay

            // Effective paid days = 30 base days - absent days + Sunday bonus days (e.g. 30 - 9.5 + 4 = 24.5 days)
            val totalDeductionDays = weekdayAbsent.toDouble() + (0.5 * weekdayHalfDay.toDouble())
            val totalEffectivePaidDays = (30.0 - totalDeductionDays + sundayBonusDays).coerceAtLeast(0.0)

            val teaAllowance = payrollPrefs.getTeaAllowance(student.id, yearMonthKey)
            val otherBonus = payrollPrefs.getOtherBonus(student.id, yearMonthKey)
            // Gross Earnings includes Base Earned + Sunday Extra Bonus + Tea Allowance + Other Bonus
            val grossEarnings = totalEarnedSalary + teaAllowance + otherBonus

            // 1. DEDICATED SALARY ADVANCES (Specific to monthly salary, strictly separate from T-Hisab)
            val salaryAdvancesList = payrollPrefs.getSalaryAdvances(student.id, yearMonthKey)
            val totalSalaryAdvance = salaryAdvancesList.sumOf { it.amount }

            // 2. SEPARATE T-HISAB ADVANCES (Informational only, settled in T-Hisab against sales)
            val tHisabAdvanceList = tHisabAdvancesByStudent[student.id] ?: emptyList()
            val tHisabAdvanceTotal = tHisabAdvanceList.sumOf { it.advancePaid }

            // Salary advance is the ONLY advance deducted from monthly salary
            val finalAdvanceDeducted = totalSalaryAdvance
            val netPayable = grossEarnings - finalAdvanceDeducted
            val (isSettled, settleMode) = payrollPrefs.getSettledInfo(student.id, yearMonthKey)

            StaffPayrollCalculation(
                student = student,
                monthlySalary = monthlySalary,
                workingDaysDivisor = divisor,
                dailyRate = dailyRate,
                baseWorkingDays = baseWorkingDaysCount,
                weekdayPresent = weekdayPresent,
                weekdayHalfDay = weekdayHalfDay,
                weekdayAbsent = weekdayAbsent,
                weekdayUnmarked = weekdayUnmarked,
                totalSundaysInMonth = totalSundaysCount,
                sundayWorked = sundayWorked,
                sundayHalfDayWorked = sundayHalfDayWorked,
                sundayAbsent = sundayMarkedAbsent,
                sundayOffOrAbsent = sundayOff,
                paidSundays = sundayBonusDays,
                totalEffectivePaidDays = totalEffectivePaidDays,
                earnedBaseSalary = earnedBaseSalary,
                absentDeduction = absentDeduction,
                fullDayAbsentDeduction = fullDayAbsentDeduction,
                halfDayDeduction = halfDayDeduction,
                sundayExtraPay = sundayExtraPay,
                teaAllowance = teaAllowance,
                otherBonus = otherBonus,
                grossEarnings = grossEarnings,
                salaryAdvances = salaryAdvancesList,
                totalSalaryAdvance = totalSalaryAdvance,
                tHisabAdvanceTotal = tHisabAdvanceTotal,
                tHisabAdvanceEntries = tHisabAdvanceList,
                finalAdvanceDeducted = finalAdvanceDeducted,
                netPayable = netPayable,
                isSettled = isSettled,
                settleMode = settleMode
            )
        }
    }

    // Filtered by Search
    val filteredCalculations = remember(payrollCalculations, searchQuery) {
        if (searchQuery.isBlank()) payrollCalculations
        else payrollCalculations.filter {
            it.student.name.contains(searchQuery, ignoreCase = true) ||
            it.student.rollNumber.contains(searchQuery, ignoreCase = true)
        }
    }

    // Overall Shop Metrics
    val totalStaff = students.size
    val totalSalaryAdvanceDeductions = remember(payrollCalculations) { payrollCalculations.sumOf { it.totalSalaryAdvance } }
    val totalTHisabAdvances = remember(payrollCalculations) { payrollCalculations.sumOf { it.tHisabAdvanceTotal } }
    val totalNetPayable = remember(payrollCalculations) { payrollCalculations.sumOf { it.netPayable } }
    val totalTeaExtras = remember(payrollCalculations) { payrollCalculations.sumOf { it.teaAllowance + it.otherBonus } }

    // Helper to generate full WhatsApp / Copy text for a Salary Slip
    fun generateSalarySlipText(calc: StaffPayrollCalculation): String {
        val s = calc.student
        val sb = StringBuilder()
        sb.appendLine("========================================")
        sb.appendLine("        🏪 SHOP SALARY SLIP")
        sb.appendLine("      $currentMonthName $selectedYear")
        sb.appendLine("========================================")
        sb.appendLine("👤 Employee: ${s.name.uppercase()} ${if (s.rollNumber.isNotEmpty()) "(#${s.rollNumber})" else ""}")
        sb.appendLine("🏢 Department: ${activeClass.name}")
        sb.appendLine("📅 Pay Period: 01/$selectedMonth/$selectedYear to $maxDays/$selectedMonth/$selectedYear")
        sb.appendLine("⏰ Schedule: 30-Day Monthly Payroll (Salary ÷ 30 for all months)")
        sb.appendLine("----------------------------------------")
        sb.appendLine("📊 ATTENDANCE BREAKDOWN:")
        sb.appendLine("• Standard Month Base: 30 Days (Salary ÷ 30)")
        sb.appendLine("• Weekdays Present: ${calc.weekdayPresent} Days (₹${(calc.weekdayPresent * calc.dailyRate).toInt()})")
        if (calc.weekdayHalfDay > 0) sb.appendLine("• Weekdays Half-Day (0.5x): ${calc.weekdayHalfDay} Days (₹${(calc.weekdayHalfDay * 0.5 * calc.dailyRate).toInt()})")
        if (calc.weekdayAbsent > 0) sb.appendLine("• Weekdays Absent: ${calc.weekdayAbsent} Days (-₹${(calc.weekdayAbsent * calc.dailyRate).toInt()})")
        if (calc.sundayExtraPay > 0) {
            sb.appendLine("• Sundays Worked (Holiday Bonus): ${String.format(Locale.US, "%.1f", calc.paidSundays)} Sundays (+₹${calc.sundayExtraPay.toInt()})")
        } else {
            sb.appendLine("• Sundays: Holiday (Not worked — nothing added or deducted)")
        }
        sb.appendLine("• Total Paid Days: ${String.format(Locale.US, "%.1f", calc.totalEffectivePaidDays)} Days")
        sb.appendLine("----------------------------------------")
        sb.appendLine("💰 EARNINGS BREAKDOWN:")
        sb.appendLine("• Base Monthly Salary: ₹${calc.monthlySalary.toInt()}")
        sb.appendLine("• PER Day Salary (Salary ÷ 30): ₹${String.format(Locale.US, "%.2f", calc.dailyRate)} / day")
        sb.appendLine("• Sunday Wage (Holiday Bonus rate): ₹${String.format(Locale.US, "%.2f", calc.dailyRate)} / Sunday")
        if (calc.sundayExtraPay > 0) {
            sb.appendLine("• 🌟 Sunday Extra Bonus (${calc.sundayWorked} Sun @ ₹${calc.dailyRate.toInt()}/Sun): +₹${calc.sundayExtraPay.toInt()}")
        }
        sb.appendLine("• Total Earned Salary: ₹${(calc.earnedBaseSalary + calc.sundayExtraPay).toInt()}")
        if (calc.teaAllowance > 0) {
            sb.appendLine("• ☕ Tea & Refreshment Allowance: +₹${calc.teaAllowance.toInt()}")
        }
        if (calc.otherBonus > 0) {
            sb.appendLine("• 🎁 Other Bonus/OT: +₹${calc.otherBonus.toInt()}")
        }
        sb.appendLine("• Total Gross Earnings: ₹${calc.grossEarnings.toInt()}")
        sb.appendLine("----------------------------------------")
        sb.appendLine("🔻 DEDUCTIONS:")
        val fullAbsentCount = calc.weekdayAbsent
        val halfDayCount = calc.weekdayHalfDay
        if (fullAbsentCount > 0) {
            sb.appendLine("• ❌ Full Day Absent (${fullAbsentCount}d @ ₹${calc.dailyRate.toInt()}/d): -₹${calc.fullDayAbsentDeduction.toInt()}")
        }
        if (halfDayCount > 0) {
            val halfRate = calc.dailyRate / 2.0
            sb.appendLine("• ⏳ Half-Day Deductions (${halfDayCount} HD @ PER Day ÷ 2 = ₹${halfRate.toInt()}): -₹${calc.halfDayDeduction.toInt()}")
        }
        sb.appendLine("• 💵 Salary Advance (वेतन पेशगी): -₹${calc.totalSalaryAdvance.toInt()}")
        if (calc.salaryAdvances.isNotEmpty()) {
            calc.salaryAdvances.forEach { adv ->
                val note = if (adv.notes.isNotEmpty()) " (${adv.notes})" else ""
                sb.appendLine("   - ${adv.dateString}: ₹${adv.amount.toInt()}$note")
            }
        }
        sb.appendLine("----------------------------------------")
        sb.appendLine("💵 NET IN-HAND PAYABLE: ₹${calc.netPayable.toInt()}")
        sb.appendLine("Status: ${if (calc.isSettled) "✅ SETTLED (${calc.settleMode})" else "⏳ PENDING PAYMENT"}")
        if (calc.tHisabAdvanceTotal > 0) {
            sb.appendLine("----------------------------------------")
            sb.appendLine("ℹ️ Note: T-Hisab piece-rate advance (₹${calc.tHisabAdvanceTotal.toInt()}) is tracked separately in T-Hisab.")
        }
        sb.appendLine("========================================")
        sb.appendLine("Shop Attendance & Payroll System")
        return sb.toString()
    }

    // Helper to generate full master payroll register text
    fun generateAllPayrollText(): String {
        val sb = StringBuilder()
        sb.appendLine("==================================================================")
        sb.appendLine("MASTER PAYROLL REGISTER - $currentMonthName $selectedYear")
        sb.appendLine("Department: ${activeClass.name} | Staff Count: ${students.size}")
        sb.appendLine("Rule: Sunday is holiday (Present = Sunday wage added as bonus; otherwise nothing added). PER Day = Salary ÷ 30.")
        sb.appendLine("Notice: Salary Advance is separate from T-Hisab Advance")
        sb.appendLine("==================================================================")
        sb.appendLine("NAME\tSALARY\tDAILY-RATE\tPAID-DAYS\tEARNED\tTEA\tSAL-ADV\tT-HISAB-ADV\tNET-PAY\tSTATUS")
        for (c in payrollCalculations) {
            val status = if (c.isSettled) "PAID" else "PENDING"
            val totalEarned = (c.earnedBaseSalary + c.sundayExtraPay).toInt()
            sb.appendLine("${c.student.name}\t₹${c.monthlySalary.toInt()}\t₹${String.format(Locale.US, "%.1f", c.dailyRate)}/d\t${String.format(Locale.US, "%.1f", c.totalEffectivePaidDays)}\t₹$totalEarned\t+₹${c.teaAllowance.toInt()}\t-₹${c.totalSalaryAdvance.toInt()}\t[₹${c.tHisabAdvanceTotal.toInt()}]\t₹${c.netPayable.toInt()}\t$status")
        }
        sb.appendLine("==================================================================")
        sb.appendLine("TOTAL PAYOUT: ₹${totalNetPayable.toInt()} | SALARY ADVANCES: ₹${totalSalaryAdvanceDeductions.toInt()} | TEA: ₹${totalTeaExtras.toInt()}")
        sb.appendLine("==================================================================")
        return sb.toString()
    }

    var isSummaryExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // COMPACT, HIGH-VISIBILITY TOP HEADER (Takes ~90dp instead of ~350dp)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // ROW 1: Month Selector & Quick Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Month Navigator Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clickable { showMonthPicker = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Month", modifier = Modifier.size(16.dp))
                            }

                            Text(
                                text = "$currentMonthName $selectedYear",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            IconButton(
                                onClick = {
                                    if (selectedMonth == 12) {
                                        selectedMonth = 1
                                        selectedYear += 1
                                    } else {
                                        selectedMonth += 1
                                    }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Action Icons (Settings, Copy, Share)
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showPayrollSettingsDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = "Payroll Style",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = {
                                val text = generateAllPayrollText()
                                clipboardManager.setText(AnnotatedString(text))
                                Toast.makeText(context, "Payroll Register Copied!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Register", modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = {
                                val text = generateAllPayrollText()
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, text)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Master Payroll"))
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share Register", modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // ROW 2: View Mode Switcher (Full Width 50-50, Clean & Spacious)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(2.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (viewMode == PayrollViewMode.ALL_STAFF_REGISTER) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewMode = PayrollViewMode.ALL_STAFF_REGISTER }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 7.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Assessment,
                                    contentDescription = null,
                                    tint = if (viewMode == PayrollViewMode.ALL_STAFF_REGISTER) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "All Staff Sheet (${students.size})",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (viewMode == PayrollViewMode.ALL_STAFF_REGISTER) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (viewMode == PayrollViewMode.INDIVIDUAL_STAFF) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewMode = PayrollViewMode.INDIVIDUAL_STAFF }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 7.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (viewMode == PayrollViewMode.INDIVIDUAL_STAFF) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Employee Slip & Calc",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (viewMode == PayrollViewMode.INDIVIDUAL_STAFF) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // ROW 3: Slim Quick Summary Banner (Expandable to preserve screen space)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9),
                    border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isSummaryExpanded = !isSummaryExpanded }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF1B5E20),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "30-Day Pay (₹800/d) • Payout: ₹${totalNetPayable.toInt()} • Adv: -₹${totalSalaryAdvanceDeductions.toInt()}",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(
                            imageVector = if (isSummaryExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle Summary",
                            tint = Color(0xFF1B5E20),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // If expanded, show the full 5 metric badges
                AnimatedVisibility(visible = isSummaryExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            PayrollMetricBadge(label = "Total Staff", value = "$totalStaff", color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                            PayrollMetricBadge(label = "Pay Base", value = "30 Days", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f))
                            PayrollMetricBadge(label = "Sal Adv.", value = "₹${totalSalaryAdvanceDeductions.toInt()}", color = Color(0xFFB71C1C), modifier = Modifier.weight(1f))
                            PayrollMetricBadge(label = "Tea Extra", value = "₹${totalTeaExtras.toInt()}", color = Color(0xFFE65100), modifier = Modifier.weight(1f))
                            PayrollMetricBadge(label = "Net Payout", value = "₹${totalNetPayable.toInt()}", color = Color(0xFF1B5E20), modifier = Modifier.weight(1.1f))
                        }
                        Text(
                            text = "Sunday is Holiday: If present, Sunday wage is added as Sunday Extra Bonus (Salary ÷ 30). Otherwise nothing is added or deducted.",
                            fontSize = 9.5.sp,
                            color = Color(0xFF2E7D32),
                            lineHeight = 12.sp
                        )
                    }
                }
            }
        }

        // VIEW CONTENT BASED ON VIEW MODE
        if (students.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No staff in ${activeClass.name}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Add employees from the Staff tab to manage payroll.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else if (viewMode == PayrollViewMode.ALL_STAFF_REGISTER) {
            // ALL STAFF PAYROLL SHEET / REGISTER
            AllStaffPayrollSheetView(
                calculations = filteredCalculations,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onEditSalary = { editingSalaryForStudent = it },
                onEditTea = { editingTeaForStudent = it },
                onViewSlip = { showingSalarySlipForStaff = it },
                onRecordSalaryAdvance = { recordingSalaryAdvanceForStudent = it },
                onSettle = { settlingPaymentForStaff = it },
                onSelectStaffTab = { staffId ->
                    selectedStudentId = staffId
                    viewMode = PayrollViewMode.INDIVIDUAL_STAFF
                }
            )
        } else {
            // INDIVIDUAL STAFF CALCULATOR & SLIP VIEW
            val currentSelectedCalc = filteredCalculations.find { it.student.id == selectedStudentId }
                ?: filteredCalculations.firstOrNull()
                ?: payrollCalculations.first()

            IndividualStaffPayrollView(
                calculation = currentSelectedCalc,
                allStaff = students,
                onSelectStaff = { selectedStudentId = it.id },
                dayInfos = dayInfos,
                allRecordsForStudent = recordsByStudent[currentSelectedCalc.student.id] ?: emptyMap(),
                onEditSalary = { editingSalaryForStudent = currentSelectedCalc.student },
                onEditTea = { editingTeaForStudent = currentSelectedCalc.student },
                onRecordSalaryAdvance = { recordingSalaryAdvanceForStudent = currentSelectedCalc.student },
                onEditSalaryAdvanceRecord = { record ->
                    editingSalaryAdvanceRecord = Pair(currentSelectedCalc.student, record)
                },
                onDeleteSalaryAdvanceRecord = { advId ->
                    payrollPrefs.deleteSalaryAdvance(advId)
                    refreshTrigger++
                    Toast.makeText(context, "Salary Advance deleted", Toast.LENGTH_SHORT).show()
                },
                onViewSlip = { showingSalarySlipForStaff = currentSelectedCalc },
                onSettle = { settlingPaymentForStaff = currentSelectedCalc },
                onUpdateAttendance = { dateKey, status ->
                    onUpdateRecord(currentSelectedCalc.student.id, dateKey, status, "", "", "", "")
                    refreshTrigger++
                },
                onRefresh = { refreshTrigger++ }
            )
        }
    }

    // DIALOG: EDIT MONTHLY SALARY
    if (editingSalaryForStudent != null) {
        val student = editingSalaryForStudent!!
        var salaryInput by remember { mutableStateOf(payrollPrefs.getSalary(student.id).toInt().let { if (it > 0) "$it" else "" }) }

        AlertDialog(
            onDismissRequest = { editingSalaryForStudent = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Set Monthly Salary", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Employee: ${student.name.uppercase()} ${if (student.rollNumber.isNotEmpty()) "(#${student.rollNumber})" else ""}",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = salaryInput,
                        onValueChange = { salaryInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Monthly Fixed Salary (₹)") },
                        placeholder = { Text("e.g. 15000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        prefix = { Text("₹ ", fontWeight = FontWeight.Bold) }
                    )

                    // Quick presets
                    Text("Quick Presets:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(8000, 10000, 12000, 15000, 18000, 20000, 24000, 25000, 30000).forEach { preset ->
                            SuggestionChip(
                                onClick = { salaryInput = "$preset" },
                                label = { Text("₹$preset", fontSize = 11.sp) }
                            )
                        }
                    }

                    Text(
                        text = "• Salary is saved for this employee and carries forward automatically.\n• Daily rate = Monthly Salary ÷ 30 days (e.g. ₹24,000 ÷ 30 = ₹800/day).\n• Sunday is Holiday: If present, Sunday wage is added as Sunday bonus; otherwise nothing added.\n• Weekday absences deduct ₹800/day (half-day deducts half).",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sal = salaryInput.toDoubleOrNull() ?: 0.0
                        payrollPrefs.setSalary(student.id, sal)
                        refreshTrigger++
                        editingSalaryForStudent = null
                        Toast.makeText(context, "Salary saved for ${student.name}!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Save Salary")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSalaryForStudent = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: PAYROLL CALCULATION STYLE SETTINGS (30-DAYS FIXED vs OTHERS)
    if (showPayrollSettingsDialog) {
        val currentMode = payrollPrefs.getWorkingDaysMode()
        var selectedMode by remember { mutableStateOf(currentMode) }

        AlertDialog(
            onDismissRequest = { showPayrollSettingsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Payroll Calculation Style", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Choose how daily wage rate and Sundays are calculated for monthly staff:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Option 1: 30-Days Style Payroll (PRIMARY & DEFAULT)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedMode == "FIXED_30") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (selectedMode == "FIXED_30") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth().clickable { selectedMode = "FIXED_30" }
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedMode == "FIXED_30",
                                onClick = { selectedMode = "FIXED_30" }
                            )
                            Spacer(Modifier.width(6.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("30-Days Style Payroll", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE8F5E9)) {
                                        Text("ACTIVE", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                    }
                                }
                                Text(
                                    text = "• PER day salary = Salary ÷ 30 (for every month)\n• Sunday is Holiday: If present, Sunday wage is added as Sunday bonus\n• Otherwise nothing is added or deducted for Sunday\n• Weekday absences deduct Salary ÷ 30/day",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }

                    // Option 2: 26-Days Mon-Sat
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedMode == "FIXED_26") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (selectedMode == "FIXED_26") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth().clickable { selectedMode = "FIXED_26" }
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedMode == "FIXED_26",
                                onClick = { selectedMode = "FIXED_26" }
                            )
                            Spacer(Modifier.width(6.dp))
                            Column {
                                Text("26-Days Working Month", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = "• Salary ÷ 26 days (Mon-Sat working)\n• Sundays are unpaid off days",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Option 3: Actual Days
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedMode == "ACTUAL_DAYS") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (selectedMode == "ACTUAL_DAYS") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth().clickable { selectedMode = "ACTUAL_DAYS" }
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedMode == "ACTUAL_DAYS",
                                onClick = { selectedMode = "ACTUAL_DAYS" }
                            )
                            Spacer(Modifier.width(6.dp))
                            Column {
                                Text("Actual Calendar Days", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = "• Salary ÷ exact days in month ($maxDays days)\n• Daily rate changes each month",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        payrollPrefs.setWorkingDaysMode(selectedMode)
                        refreshTrigger++
                        showPayrollSettingsDialog = false
                        Toast.makeText(context, "Payroll style updated!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Apply Style")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPayrollSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: EDIT TEA & REFRESHMENT ALLOWANCE
    if (editingTeaForStudent != null) {
        val student = editingTeaForStudent!!
        val currentTea = payrollPrefs.getTeaAllowance(student.id, yearMonthKey)
        var teaInput by remember { mutableStateOf(if (currentTea > 0) currentTea.toInt().toString() else "") }
        var isMonthlyDefault by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { editingTeaForStudent = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.LocalCafe, contentDescription = null, tint = Color(0xFFE65100))
                    Text("Tea & Refreshment Allowance", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Employee: ${student.name.uppercase()} ($currentMonthName $selectedYear)",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )

                    OutlinedTextField(
                        value = teaInput,
                        onValueChange = { teaInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Tea / Snacks Extra Amount (₹)") },
                        placeholder = { Text("e.g. 500") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        prefix = { Text("₹ ", fontWeight = FontWeight.Bold) }
                    )

                    Text("Quick Amounts:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(200, 300, 500, 600, 1000, 1200, 1500).forEach { amt ->
                            SuggestionChip(
                                onClick = { teaInput = "$amt" },
                                label = { Text("+₹$amt", fontSize = 11.sp) }
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { isMonthlyDefault = !isMonthlyDefault }
                    ) {
                        Checkbox(
                            checked = isMonthlyDefault,
                            onCheckedChange = { isMonthlyDefault = it }
                        )
                        Text(
                            text = "Set as permanent monthly tea allowance for ${student.name}",
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = teaInput.toDoubleOrNull() ?: 0.0
                        payrollPrefs.setTeaAllowance(student.id, yearMonthKey, amt)
                        if (isMonthlyDefault) {
                            payrollPrefs.setDefaultTeaAllowance(student.id, amt)
                        }
                        refreshTrigger++
                        editingTeaForStudent = null
                        Toast.makeText(context, "Tea allowance saved!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTeaForStudent = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: RECORD NEW SALARY ADVANCE (SEPARATE FROM T-HISAB)
    if (recordingSalaryAdvanceForStudent != null) {
        val student = recordingSalaryAdvanceForStudent!!
        var advAmount by remember { mutableStateOf("") }
        var advNotes by remember { mutableStateOf("") }
        val todayStr = remember {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        }
        var advDate by remember { mutableStateOf(todayStr) }

        AlertDialog(
            onDismissRequest = { recordingSalaryAdvanceForStudent = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFFB71C1C))
                    Text("Give Salary Advance (सैलरी पेशगी)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "To: ${student.name.uppercase()}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFEBEE),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "This is a SALARY ADVANCE deducted specifically from monthly salary payout. (Kept separate from T-Hisab sales advance).",
                            fontSize = 10.5.sp,
                            color = Color(0xFFB71C1C),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    OutlinedTextField(
                        value = advAmount,
                        onValueChange = { advAmount = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Salary Advance Amount (₹)") },
                        placeholder = { Text("e.g. 2000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        prefix = { Text("₹ ", fontWeight = FontWeight.Bold) }
                    )

                    OutlinedTextField(
                        value = advNotes,
                        onValueChange = { advNotes = it },
                        label = { Text("Reason / Remarks (Optional)") },
                        placeholder = { Text("e.g. Personal advance / Family / Medical") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = advDate,
                        onValueChange = { advDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = advAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            payrollPrefs.addSalaryAdvance(student.id, advDate, amt, advNotes)
                            refreshTrigger++
                            recordingSalaryAdvanceForStudent = null
                            Toast.makeText(context, "₹${amt.toInt()} Salary Advance recorded for ${student.name}!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Please enter valid amount", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                ) {
                    Text("Save Salary Advance")
                }
            },
            dismissButton = {
                TextButton(onClick = { recordingSalaryAdvanceForStudent = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: EDIT EXISTING SALARY ADVANCE RECORD
    if (editingSalaryAdvanceRecord != null) {
        val (student, record) = editingSalaryAdvanceRecord!!
        var editAmt by remember { mutableStateOf(record.amount.toInt().toString()) }
        var editDate by remember { mutableStateOf(record.dateString) }
        var editNote by remember { mutableStateOf(record.notes) }

        AlertDialog(
            onDismissRequest = { editingSalaryAdvanceRecord = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Edit Salary Advance", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Employee: ${student.name.uppercase()}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = editAmt,
                        onValueChange = { editAmt = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        prefix = { Text("₹ ", fontWeight = FontWeight.Bold) }
                    )

                    OutlinedTextField(
                        value = editDate,
                        onValueChange = { editDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editNote,
                        onValueChange = { editNote = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = editAmt.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            payrollPrefs.updateSalaryAdvance(
                                record.copy(amount = amt, dateString = editDate, notes = editNote.trim())
                            )
                            refreshTrigger++
                            editingSalaryAdvanceRecord = null
                            Toast.makeText(context, "Salary Advance updated", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            payrollPrefs.deleteSalaryAdvance(record.id)
                            refreshTrigger++
                            editingSalaryAdvanceRecord = null
                            Toast.makeText(context, "Salary Advance deleted", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                    TextButton(onClick = { editingSalaryAdvanceRecord = null }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    // DIALOG: FULL SALARY SLIP
    if (showingSalarySlipForStaff != null) {
        val calc = showingSalarySlipForStaff!!
        val slipText = remember(calc) { generateSalarySlipText(calc) }

        AlertDialog(
            onDismissRequest = { showingSalarySlipForStaff = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Salary Slip (वेतन पर्ची)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    IconButton(onClick = { showingSalarySlipForStaff = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
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
                    SalarySlipDisplayCard(calc = calc, monthName = currentMonthName, year = selectedYear, maxDays = maxDays)
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, slipText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Send Salary Slip via WhatsApp"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("WhatsApp Slip")
                    }

                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(slipText))
                            Toast.makeText(context, "Salary Slip copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Copy")
                    }
                }
            }
        )
    }

    // DIALOG: MARK PAYMENT SETTLED
    if (settlingPaymentForStaff != null) {
        val calc = settlingPaymentForStaff!!
        var paymentMode by remember { mutableStateOf("CASH") }

        AlertDialog(
            onDismissRequest = { settlingPaymentForStaff = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF1B5E20))
                    Text("Settle & Mark Paid", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Employee: ${calc.student.name.uppercase()}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Net In-Hand Payable: ₹${calc.netPayable.toInt()}",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color(0xFF1B5E20)
                    )

                    Text("Select Payment Mode:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("CASH", "UPI / GPAY", "BANK").forEach { mode ->
                            FilterChip(
                                selected = paymentMode == mode,
                                onClick = { paymentMode = mode },
                                label = { Text(mode, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        payrollPrefs.setSettled(calc.student.id, yearMonthKey, true, paymentMode)
                        refreshTrigger++
                        settlingPaymentForStaff = null
                        Toast.makeText(context, "Marked as Paid via $paymentMode!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Text("Confirm Paid")
                }
            },
            dismissButton = {
                if (calc.isSettled) {
                    TextButton(
                        onClick = {
                            payrollPrefs.setSettled(calc.student.id, yearMonthKey, false)
                            refreshTrigger++
                            settlingPaymentForStaff = null
                            Toast.makeText(context, "Payment marked as Pending", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Unmark / Set Pending", color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    TextButton(onClick = { settlingPaymentForStaff = null }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    // MONTH & YEAR PICKER DIALOG
    if (showMonthPicker) {
        AlertDialog(
            onDismissRequest = { showMonthPicker = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Month & Year", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = { showMonthPicker = false }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Year", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(2024, 2025, 2026, 2027).forEach { yr ->
                            FilterChip(
                                selected = selectedYear == yr,
                                onClick = { selectedYear = yr },
                                label = { Text("$yr", fontSize = 12.sp) }
                            )
                        }
                    }

                    Text("Month", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val shortMonths = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                    for (rowIndex in 0..3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (colIndex in 0..2) {
                                val mIdx = rowIndex * 3 + colIndex
                                val mNum = mIdx + 1
                                val isMSelected = selectedMonth == mNum
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedMonth = mNum },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isMSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, if (isMSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = shortMonths[mIdx],
                                            fontWeight = if (isMSelected) FontWeight.Black else FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = if (isMSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showMonthPicker = false }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
fun PayrollMetricBadge(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 12.5.sp,
                color = color,
                maxLines = 1
            )
            Text(
                text = label,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

// -------------------------------------------------------------
// ALL STAFF PAYROLL SHEET (REGISTER VIEW)
// -------------------------------------------------------------
@Composable
fun AllStaffPayrollSheetView(
    calculations: List<StaffPayrollCalculation>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onEditSalary: (StudentEntity) -> Unit,
    onEditTea: (StudentEntity) -> Unit,
    onViewSlip: (StaffPayrollCalculation) -> Unit,
    onRecordSalaryAdvance: (StudentEntity) -> Unit,
    onSettle: (StaffPayrollCalculation) -> Unit,
    onSelectStaffTab: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search employee by name or roll...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Cards List of Staff Payroll
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(calculations, key = { it.student.id }) { calc ->
                StaffPayrollRowCard(
                    calc = calc,
                    onEditSalary = { onEditSalary(calc.student) },
                    onEditTea = { onEditTea(calc.student) },
                    onViewSlip = { onViewSlip(calc) },
                    onRecordSalaryAdvance = { onRecordSalaryAdvance(calc.student) },
                    onSettle = { onSettle(calc) },
                    onOpenDetail = { onSelectStaffTab(calc.student.id) }
                )
            }
        }
    }
}

@Composable
fun StaffPayrollRowCard(
    calc: StaffPayrollCalculation,
    onEditSalary: () -> Unit,
    onEditTea: () -> Unit,
    onViewSlip: () -> Unit,
    onRecordSalaryAdvance: () -> Unit,
    onSettle: () -> Unit,
    onOpenDetail: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (calc.isSettled) Color(0xFFA5D6A7) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Top Row: Name, Status badge, Salary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = calc.student.displayName.take(1).uppercase(),
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Column {
                        Text(
                            text = calc.student.displayName.uppercase(),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (calc.student.rollNumber.isNotEmpty()) {
                                Text("#${calc.student.rollNumber}", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
                            }
                            if (calc.isSettled) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFE8F5E9)
                                ) {
                                    Text("PAID (${calc.settleMode})", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFFFF3E0)
                                ) {
                                    Text("PENDING", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                        }
                    }
                }

                // Base Salary Pill (Tap to Edit)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { onEditSalary() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (calc.monthlySalary > 0) "₹${calc.monthlySalary.toInt()}" else "Set Salary",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = if (calc.monthlySalary > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary
                        )
                        Icon(Icons.Default.Edit, contentDescription = "Edit Salary", modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Middle Row: Attendance & Pay Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Days breakdown
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Paid Days: ${String.format(Locale.US, "%.1f", calc.totalEffectivePaidDays)} / ${calc.workingDaysDivisor.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (calc.sundayExtraPay > 0) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFE0F2F1)
                            ) {
                                Text(
                                    text = "Sun Bonus: +₹${calc.sundayExtraPay.toInt()}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00695C),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "Present: ${calc.weekdayPresent}d | Absent: ${calc.weekdayAbsent}d | HD: ${calc.weekdayHalfDay}d",
                        fontSize = 9.5.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // Tea & Advance breakdown
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Tea: +₹${calc.teaAllowance.toInt()}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100),
                            modifier = Modifier.clickable { onEditTea() }
                        )
                        Text(
                            text = "Sal-Adv: -₹${calc.totalSalaryAdvance.toInt()}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB71C1C)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Gross: ₹${calc.grossEarnings.toInt()}",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        if (calc.tHisabAdvanceTotal > 0) {
                            Text(
                                text = "[T-Hisab: ₹${calc.tHisabAdvanceTotal.toInt()}]",
                                fontSize = 9.sp,
                                color = Color(0xFF0277BD),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Bottom Highlight Row: Net In-Hand & Actions
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (calc.isSettled) Color(0xFFE8F5E9) else Color(0xFFF1F8E9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NET IN-HAND PAYABLE",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "₹${calc.netPayable.toInt()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (calc.netPayable >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = onEditTea,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.LocalCafe, contentDescription = "Tea Allowance", tint = Color(0xFFE65100), modifier = Modifier.size(17.dp))
                        }

                        IconButton(
                            onClick = onRecordSalaryAdvance,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Give Salary Advance", tint = Color(0xFFB71C1C), modifier = Modifier.size(17.dp))
                        }

                        IconButton(
                            onClick = onViewSlip,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = "View Slip", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }

                        Button(
                            onClick = onSettle,
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (calc.isSettled) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(if (calc.isSettled) "Paid ✓" else "Pay", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// INDIVIDUAL STAFF PAYROLL CALCULATOR VIEW
// -------------------------------------------------------------
@Composable
fun IndividualStaffPayrollView(
    calculation: StaffPayrollCalculation,
    allStaff: List<StudentEntity>,
    onSelectStaff: (StudentEntity) -> Unit,
    dayInfos: List<DayPayrollInfo>,
    allRecordsForStudent: Map<String, AttendanceRecordEntity>,
    onEditSalary: () -> Unit,
    onEditTea: () -> Unit,
    onRecordSalaryAdvance: () -> Unit,
    onEditSalaryAdvanceRecord: (SalaryAdvanceRecord) -> Unit,
    onDeleteSalaryAdvanceRecord: (String) -> Unit,
    onViewSlip: () -> Unit,
    onSettle: () -> Unit,
    onUpdateAttendance: (String, String) -> Unit,
    onRefresh: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Staff Selector Chips (Compact 34dp height, zero wasted gaps)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
        ) {
            items(allStaff) { st ->
                val isSelected = st.id == calculation.student.id
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
                    modifier = Modifier.clickable { onSelectStaff(st) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            modifier = Modifier.size(16.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = st.name.take(1).uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = st.name.uppercase(),
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            fontSize = 11.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 1. COMBINED SALARY & ATTENDANCE CARD (High-density, compact)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Top row: Salary + Daily Rate + Edit Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (calculation.monthlySalary > 0) "₹${calculation.monthlySalary.toInt()}" else "₹0",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)) {
                                Text("Monthly Base", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                        Text(
                            text = "Rate: ₹${String.format(Locale.US, "%.1f", calculation.dailyRate)}/day (÷30) • Sun Bonus: +₹${String.format(Locale.US, "%.1f", calculation.dailyRate)}/Sun",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    OutlinedButton(
                        onClick = onEditSalary,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("Edit", fontSize = 10.5.sp)
                    }
                }

                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Attendance Badges Row (4 compact badges)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AttendanceStatMiniCard(
                        label = "Present",
                        count = "${calculation.weekdayPresent}d",
                        color = Color(0xFF1B5E20),
                        modifier = Modifier.weight(1f)
                    )
                    AttendanceStatMiniCard(
                        label = "Sun Bonus",
                        count = if (calculation.sundayExtraPay > 0) "+₹${calculation.sundayExtraPay.toInt()}" else "0",
                        color = Color(0xFF00695C),
                        modifier = Modifier.weight(1.1f)
                    )
                    AttendanceStatMiniCard(
                        label = "Half-Day",
                        count = "${calculation.weekdayHalfDay}d",
                        color = Color(0xFFE65100),
                        modifier = Modifier.weight(0.9f)
                    )
                    AttendanceStatMiniCard(
                        label = "Absent",
                        count = "${calculation.weekdayAbsent}d",
                        color = Color(0xFFB71C1C),
                        modifier = Modifier.weight(0.9f)
                    )
                }

                // Compact Sunday note
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0F2F1).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF00695C), modifier = Modifier.size(12.dp))
                    Text(
                        text = if (calculation.sundayWorked > 0)
                            "Sunday is Holiday: Worked ${calculation.sundayWorked} Sun = +₹${calculation.sundayExtraPay.toInt()} added as Sunday Bonus."
                        else
                            "Sunday is Holiday: If present, Sunday wage is added as bonus; otherwise nothing added/deducted.",
                        fontSize = 9.sp,
                        color = Color(0xFF004D40)
                    )
                }
            }
        }

        // 3. TEA & REFRESHMENT ALLOWANCE CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.LocalCafe, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(18.dp))
                        Text("Tea & Refreshment Allowance", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    TextButton(onClick = onEditTea, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Edit Amount", fontSize = 11.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "+₹${calculation.teaAllowance.toInt()}",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "Extra tea / snacks / food allowance added to salary",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Button(
                        onClick = onEditTea,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF3E0)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("+ Add Tea Cash", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    }
                }
            }
        }

        // 4. SALARY ADVANCE CARD (SEPARATE FROM T-HISAB ADVANCE)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, Color(0xFFB71C1C).copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFFB71C1C), modifier = Modifier.size(18.dp))
                        Text("Salary Advance (सैलरी पेशगी)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = onRecordSalaryAdvance,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("Give Advance", fontSize = 10.5.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "-₹${calculation.totalSalaryAdvance.toInt()}",
                                fontWeight = FontWeight.Black,
                                fontSize = 19.sp,
                                color = Color(0xFFB71C1C)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFFEBEE)
                            ) {
                                Text(
                                    text = "Deducted From Salary",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB71C1C),
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Advance taken against this month's fixed salary (${calculation.salaryAdvances.size} entries)",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // List individual salary advances
                if (calculation.salaryAdvances.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            calculation.salaryAdvances.forEach { adv ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("• ${adv.dateString} ${if (adv.notes.isNotEmpty()) "(${adv.notes})" else ""}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("₹${adv.amount.toInt()}", fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color(0xFFB71C1C))
                                        IconButton(onClick = { onEditSalaryAdvanceRecord(adv) }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.outline)
                                        }
                                        IconButton(onClick = { onDeleteSalaryAdvanceRecord(adv.id) }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text("No salary advances taken this month.", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.outline)
                }
            }
        }

        // 5. SEPARATE T-HISAB ADVANCE STATUS CARD (STRICTLY SEPARATE, NOT DEDUCTED FROM SALARY)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFF0277BD).copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f, fill = false), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF0277BD), modifier = Modifier.size(18.dp))
                        Text("T-Hisab Ledger Advance", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE1F5FE)
                    ) {
                        Text(
                            text = "Separate Ledger",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0277BD),
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "₹${calculation.tHisabAdvanceTotal.toInt()}",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color(0xFF0277BD)
                        )
                        Text(
                            text = "T-Hisab piece-rate sales advance (${calculation.tHisabAdvanceEntries.size} entries). NOT deducted from fixed salary.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline,
                            lineHeight = 12.sp
                        )
                    }
                }

                if (calculation.tHisabAdvanceEntries.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F8E9).copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            calculation.tHisabAdvanceEntries.forEach { e ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• ${e.dateString} ${if (e.notes.isNotEmpty()) "(${e.notes})" else ""}", fontSize = 10.5.sp)
                                    Text("₹${e.advancePaid.toInt()}", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = Color(0xFF0277BD))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. MASTER SALARY SETTLEMENT & NET PAYABLE CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (calculation.isSettled) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(2.dp, if (calculation.isSettled) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MONTHLY SALARY SUMMARY",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (calculation.isSettled) Color(0xFF2E7D32) else Color(0xFFE65100)
                    ) {
                        Text(
                            text = if (calculation.isSettled) "PAID (${calculation.settleMode})" else "PENDING",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Breakdown line items
                SalarySummaryLineItem("Base Monthly Salary", "₹${calculation.monthlySalary.toInt()}")
                val fullAbsentCount = calculation.weekdayAbsent
                val halfDayCount = calculation.weekdayHalfDay
                if (fullAbsentCount > 0) {
                    SalarySummaryLineItem("Full Day Absent (${fullAbsentCount}d @ ₹${calculation.dailyRate.toInt()})", "-₹${calculation.fullDayAbsentDeduction.toInt()}", isDeduction = true)
                }
                if (halfDayCount > 0) {
                    val halfRate = calculation.dailyRate / 2.0
                    SalarySummaryLineItem("Half-Day Absent (${halfDayCount} HD @ PER Day ÷ 2 = ₹${halfRate.toInt()})", "-₹${calculation.halfDayDeduction.toInt()}", isDeduction = true)
                }
                if (fullAbsentCount == 0 && halfDayCount == 0) {
                    SalarySummaryLineItem("Absent & Leave Deductions", "₹0")
                }
                if (calculation.sundayExtraPay > 0) {
                    val sunCountStr = if (calculation.sundayWorked > 0) "${calculation.sundayWorked} Sun" else "Sundays"
                    SalarySummaryLineItem("Sunday Extra Bonus ($sunCountStr @ ₹${calculation.dailyRate.toInt()})", "+₹${calculation.sundayExtraPay.toInt()}", isHighlight = true)
                }
                val totalEarnedSalary = calculation.earnedBaseSalary + calculation.sundayExtraPay
                SalarySummaryLineItem("Total Earned Salary (कुल अर्जित वेतन)", "₹${totalEarnedSalary.toInt()}", isBold = true)
                if (calculation.teaAllowance > 0) {
                    SalarySummaryLineItem("Tea & Refreshment Allowance", "+₹${calculation.teaAllowance.toInt()}", isHighlight = true)
                }
                if (calculation.otherBonus > 0) {
                    SalarySummaryLineItem("Other Bonus / OT", "+₹${calculation.otherBonus.toInt()}", isHighlight = true)
                }
                SalarySummaryLineItem("Salary Advance Taken (सैलरी पेशगी)", "-₹${calculation.totalSalaryAdvance.toInt()}", isDeduction = true)

                HorizontalDivider(thickness = 1.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

                // FINAL NET PAYABLE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NET IN-HAND PAYABLE",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "Monthly salary take-home",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Text(
                        text = "₹${calculation.netPayable.toInt()}",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = if (calculation.netPayable >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                    )
                }

                // Action buttons: WhatsApp Slip, Copy, Settle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onViewSlip,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("View Slip", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onSettle,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (calculation.isSettled) Color(0xFF2E7D32) else Color(0xFF1B5E20)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (calculation.isSettled) "Paid ✓" else "Mark Paid", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceStatMiniCard(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count, fontWeight = FontWeight.Black, fontSize = 13.sp, color = color)
            Text(label, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SalarySummaryLineItem(
    label: String,
    amount: String,
    isDeduction: Boolean = false,
    isHighlight: Boolean = false,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = amount,
            fontSize = 11.5.sp,
            fontWeight = if (isBold) FontWeight.Black else FontWeight.Bold,
            color = when {
                isDeduction -> Color(0xFFB71C1C)
                isHighlight -> Color(0xFFE65100)
                isBold -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

// -------------------------------------------------------------
// SALARY SLIP DISPLAY CARD (FOR PREVIEW / PRINT)
// -------------------------------------------------------------
@Composable
fun SalarySlipDisplayCard(
    calc: StaffPayrollCalculation,
    monthName: String,
    year: Int,
    maxDays: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏪 CHOBBARE WALI DUKAN",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
                Text(
                    text = "STAFF SALARY SLIP - $monthName $year",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Pay Period: 01/$monthName to $maxDays/$monthName $year",
                    fontSize = 9.5.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            HorizontalDivider()

            // Staff Details
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Employee: ${calc.student.name.uppercase()}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    if (calc.student.rollNumber.isNotEmpty()) {
                        Text("Roll / Code: #${calc.student.rollNumber}", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (calc.isSettled) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                ) {
                    Text(
                        text = if (calc.isSettled) "PAID" else "PENDING",
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = if (calc.isSettled) Color(0xFF1B5E20) else Color(0xFFE65100),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            HorizontalDivider()

            // Attendance section
            Text("ATTENDANCE (30-Day Monthly Payroll)", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.outline)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Standard 30-Day Base: ${calc.workingDaysDivisor.toInt()} Days", fontSize = 10.5.sp)
                Text("Weekdays Present: ${calc.weekdayPresent}d", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val sunText = if (calc.paidSundays > 0) "Sun Worked: ${String.format(Locale.US, "%.1f", calc.paidSundays)}d (+₹${calc.sundayExtraPay.toInt()})" else "Sundays: Holiday"
                Text(sunText, fontSize = 10.5.sp, color = Color(0xFF00695C), fontWeight = FontWeight.Bold)
                Text("Absents: ${calc.weekdayAbsent}d | Half-Days: ${calc.weekdayHalfDay}d", fontSize = 10.5.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Effective Paid Days:", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                Text("${String.format(Locale.US, "%.1f", calc.totalEffectivePaidDays)} Days", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B5E20))
            }

            HorizontalDivider()

            // Financials
            Text("EARNINGS & DEDUCTIONS", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.outline)
            SalarySummaryLineItem("Monthly Fixed Salary", "₹${calc.monthlySalary.toInt()}")
            SalarySummaryLineItem("PER Day Salary (Salary ÷ 30)", "₹${String.format(Locale.US, "%.2f", calc.dailyRate)} / day")
            SalarySummaryLineItem("Sunday Wage (1 Sun = Per Day)", "₹${String.format(Locale.US, "%.2f", calc.dailyRate)} / Sunday")
            val fullAbsentCount = calc.weekdayAbsent
            val halfDayCount = calc.weekdayHalfDay
            if (fullAbsentCount > 0) {
                SalarySummaryLineItem("❌ Full Day Absent (${fullAbsentCount}d)", "-₹${calc.fullDayAbsentDeduction.toInt()}", isDeduction = true)
            }
            if (halfDayCount > 0) {
                val halfRate = calc.dailyRate / 2.0
                SalarySummaryLineItem("⏳ Half-Day (${halfDayCount} HD @ PER Day ÷ 2)", "-₹${calc.halfDayDeduction.toInt()}", isDeduction = true)
            }
            if (calc.sundayExtraPay > 0) {
                val sunCountStr = if (calc.sundayWorked > 0) "${calc.sundayWorked} Sun" else "Sundays"
                SalarySummaryLineItem("🌟 Sunday Extra Bonus ($sunCountStr)", "+₹${calc.sundayExtraPay.toInt()}", isHighlight = true)
            }
            val totalEarned = calc.earnedBaseSalary + calc.sundayExtraPay
            SalarySummaryLineItem("Total Earned Salary (कुल अर्जित वेतन)", "₹${totalEarned.toInt()}", isBold = true)
            if (calc.teaAllowance > 0) {
                SalarySummaryLineItem("☕ Tea & Refreshment Allowance", "+₹${calc.teaAllowance.toInt()}", isHighlight = true)
            }
            if (calc.otherBonus > 0) {
                SalarySummaryLineItem("🎁 Other Bonus / OT", "+₹${calc.otherBonus.toInt()}", isHighlight = true)
            }
            SalarySummaryLineItem("💵 Salary Advance Taken (सैलरी पेशगी)", "-₹${calc.totalSalaryAdvance.toInt()}", isDeduction = true)

            HorizontalDivider(thickness = 1.5.dp)

            // Net In-Hand
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("NET IN-HAND SALARY:", fontWeight = FontWeight.Black, fontSize = 12.5.sp, color = Color(0xFF1B5E20))
                Text("₹${calc.netPayable.toInt()}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF1B5E20))
            }

            if (calc.tHisabAdvanceTotal > 0) {
                Spacer(Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF1F8E9)
                ) {
                    Text(
                        text = "ℹ️ Note: T-Hisab sales advance (₹${calc.tHisabAdvanceTotal.toInt()}) is tracked separately in T-Hisab.",
                        fontSize = 9.sp,
                        color = Color(0xFF33691E),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
