package com.example.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StaffFeedbackEntity
import com.example.data.StudentEntity
import com.example.data.displayName
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val FEEDBACK_CATEGORIES = listOf(
    "WORK" to "Work Quality",
    "PUNCTUALITY" to "Punctuality",
    "CUSTOMER" to "Customer Care",
    "BEHAVIOUR" to "Behaviour",
    "SALES" to "Sales / Target",
    "GENERAL" to "General"
)

private fun feedbackCategoryLabel(key: String): String {
    return FEEDBACK_CATEGORIES.find { it.first.equals(key, ignoreCase = true) }?.second ?: key.ifBlank { "General" }
}

private fun formatFeedbackDisplayDate(dateStr: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val parsed = parser.parse(dateStr) ?: return dateStr
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(parsed)
    } catch (_: Exception) {
        dateStr
    }
}

@Composable
fun StaffFeedbackTabContent(
    student: StudentEntity,
    feedbackList: List<StaffFeedbackEntity>,
    onAddFeedback: (studentId: Long, entryType: String, title: String, details: String, category: String, dateString: String) -> Unit,
    onUpdateFeedback: (StaffFeedbackEntity) -> Unit,
    onToggleResolved: (StaffFeedbackEntity) -> Unit,
    onDeleteFeedback: (StaffFeedbackEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val empFeedback = remember(feedbackList, student.id) {
        feedbackList
            .filter { it.studentId == student.id }
            .sortedWith(compareByDescending<StaffFeedbackEntity> { it.dateString }.thenByDescending { it.timestamp })
    }

    val praiseCount = empFeedback.count { it.entryType == "APPRECIATION" }
    val complaintCount = empFeedback.count { it.entryType == "COMPLAINT" }
    val openComplaintCount = empFeedback.count { it.entryType == "COMPLAINT" && !it.isResolved }

    var selectedFilter by remember(student.id) { mutableStateOf("ALL") } // ALL, APPRECIATION, COMPLAINT, OPEN
    var showAddDialogWithType by remember { mutableStateOf<String?>(null) }
    var editingFeedback by remember { mutableStateOf<StaffFeedbackEntity?>(null) }
    var deletingFeedback by remember { mutableStateOf<StaffFeedbackEntity?>(null) }

    val filteredFeedback = remember(empFeedback, selectedFilter) {
        when (selectedFilter) {
            "APPRECIATION" -> empFeedback.filter { it.entryType == "APPRECIATION" }
            "COMPLAINT" -> empFeedback.filter { it.entryType == "COMPLAINT" }
            "OPEN" -> empFeedback.filter { it.entryType == "COMPLAINT" && !it.isResolved }
            else -> empFeedback
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // KPI Summary Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF16A34A).copy(alpha = 0.10f),
                border = BorderStroke(1.dp, Color(0xFF16A34A).copy(alpha = 0.30f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedFilter = "APPRECIATION" }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFF16A34A).copy(alpha = 0.16f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Appreciations",
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "$praiseCount",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF15803D)
                        )
                        Text(
                            text = "Appreciation",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFDC2626).copy(alpha = 0.10f),
                border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.30f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedFilter = "COMPLAINT" }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFDC2626).copy(alpha = 0.16f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Complaints",
                            tint = Color(0xFFB91C1C),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "$complaintCount",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFB91C1C)
                            )
                            if (openComplaintCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        text = "$openComplaintCount Open",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB45309),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Complaints",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                    }
                }
            }
        }

        // Quick Add Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showAddDialogWithType = "APPRECIATION" },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_add_appreciation_${student.id}")
            ) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("+ Appreciation", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }

            Button(
                onClick = { showAddDialogWithType = "COMPLAINT" },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_add_complaint_${student.id}")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("+ Complaint", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }
        }

        // Filter Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val filters = listOf(
                "ALL" to "All (${empFeedback.size})",
                "APPRECIATION" to "Appreciation ($praiseCount)",
                "COMPLAINT" to "Complaints ($complaintCount)",
                "OPEN" to "Open Issues ($openComplaintCount)"
            )
            items(filters) { (key, label) ->
                FilterChip(
                    selected = selectedFilter == key,
                    onClick = { selectedFilter = key },
                    label = { Text(label, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (key) {
                            "APPRECIATION" -> Color(0xFFDCFCE7)
                            "COMPLAINT" -> Color(0xFFFEE2E2)
                            "OPEN" -> Color(0xFFFEF3C7)
                            else -> MaterialTheme.colorScheme.primaryContainer
                        },
                        selectedLabelColor = when (key) {
                            "APPRECIATION" -> Color(0xFF15803D)
                            "COMPLAINT" -> Color(0xFFB91C1C)
                            "OPEN" -> Color(0xFFB45309)
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    ),
                    modifier = Modifier.testTag("chip_feedback_filter_$key")
                )
            }
        }

        // Feedback List
        if (filteredFeedback.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = when (selectedFilter) {
                            "APPRECIATION" -> "No appreciations logged for ${student.displayName} yet."
                            "COMPLAINT" -> "No complaints logged for ${student.displayName}."
                            "OPEN" -> "No open complaints! Everything is resolved."
                            else -> "No complaints or appreciations recorded for ${student.displayName}."
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap + Appreciation or + Complaint above to record performance notes.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredFeedback, key = { it.id }) { item ->
                    val isPraise = item.entryType == "APPRECIATION"
                    val accent = if (isPraise) Color(0xFF16A34A) else Color(0xFFDC2626)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, accent.copy(alpha = if (item.isResolved) 0.2f else 0.45f)),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_feedback_${item.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Top Row: Type badge + Category + Date + Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = accent.copy(alpha = 0.14f)
                                    ) {
                                        Text(
                                            text = if (isPraise) "🌟 APPRECIATION" else "⚠️ COMPLAINT",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = accent,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = feedbackCategoryLabel(item.category),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = formatFeedbackDisplayDate(item.dateString),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Title + Resolved Pill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )

                                if (!isPraise) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (item.isResolved) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                        modifier = Modifier
                                            .padding(start = 6.dp)
                                            .clickable { onToggleResolved(item) }
                                    ) {
                                        Text(
                                            text = if (item.isResolved) "✓ Resolved" else "⏳ Open",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.isResolved) Color(0xFF15803D) else Color(0xFFB45309),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            if (item.details.isNotBlank()) {
                                Text(
                                    text = item.details,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Action Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!isPraise) {
                                    OutlinedButton(
                                        onClick = { onToggleResolved(item) },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        border = BorderStroke(
                                            1.dp,
                                            if (item.isResolved) Color(0xFF6B7280).copy(alpha = 0.4f) else Color(0xFF16A34A).copy(alpha = 0.5f)
                                        ),
                                        modifier = Modifier
                                            .height(34.dp)
                                            .testTag("btn_toggle_resolved_${item.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = if (item.isResolved) Color(0xFF6B7280) else Color(0xFF16A34A),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (item.isResolved) "Reopen" else "Mark Resolved",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.isResolved) Color(0xFF4B5563) else Color(0xFF15803D)
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // WhatsApp Share Feedback
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFDCFCE7),
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clickable {
                                                val typeHeader = if (isPraise) "🌟 Appreciation Note" else "⚠️ Feedback / Notice"
                                                val msg = buildString {
                                                    appendLine("$typeHeader - ${student.displayName}")
                                                    appendLine("Date: ${formatFeedbackDisplayDate(item.dateString)}")
                                                    appendLine("Category: ${feedbackCategoryLabel(item.category)}")
                                                    appendLine("Subject: ${item.title}")
                                                    if (item.details.isNotBlank()) appendLine("Details: ${item.details}")
                                                }
                                                val clean = student.phone.replace(Regex("[^0-9]"), "")
                                                val formatted = if (clean.length == 10) "91$clean" else clean
                                                val url = if (formatted.isNotBlank()) {
                                                    "https://wa.me/$formatted?text=${Uri.encode(msg)}"
                                                } else {
                                                    "https://wa.me/?text=${Uri.encode(msg)}"
                                                }
                                                try {
                                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                                } catch (_: Exception) {
                                                    Toast.makeText(context, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            .testTag("btn_share_feedback_${item.id}")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Share via WhatsApp",
                                                tint = Color(0xFF15803D),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    // Edit
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clickable { editingFeedback = item }
                                            .testTag("btn_edit_feedback_${item.id}")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Feedback",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    // Delete
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFEE2E2),
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clickable { deletingFeedback = item }
                                            .testTag("btn_delete_feedback_${item.id}")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Feedback",
                                                tint = Color(0xFFB91C1C),
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
    }

    if (showAddDialogWithType != null) {
        AddEditStaffFeedbackDialog(
            studentName = student.displayName,
            initialType = showAddDialogWithType!!,
            existing = null,
            onDismiss = { showAddDialogWithType = null },
            onSave = { entryType, title, details, category, dateString ->
                onAddFeedback(student.id, entryType, title, details, category, dateString)
                showAddDialogWithType = null
            }
        )
    }

    if (editingFeedback != null) {
        val current = editingFeedback!!
        AddEditStaffFeedbackDialog(
            studentName = student.displayName,
            initialType = current.entryType,
            existing = current,
            onDismiss = { editingFeedback = null },
            onSave = { entryType, title, details, category, dateString ->
                onUpdateFeedback(
                    current.copy(
                        entryType = entryType,
                        title = title,
                        details = details,
                        category = category,
                        dateString = dateString
                    )
                )
                editingFeedback = null
            }
        )
    }

    if (deletingFeedback != null) {
        val target = deletingFeedback!!
        AlertDialog(
            onDismissRequest = { deletingFeedback = null },
            title = { Text("Delete Entry?") },
            text = { Text("Remove \"${target.title}\" from ${student.displayName}'s Complaint & Appreciation log?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteFeedback(target)
                        deletingFeedback = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingFeedback = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AddEditStaffFeedbackDialog(
    studentName: String,
    initialType: String,
    existing: StaffFeedbackEntity?,
    onDismiss: () -> Unit,
    onSave: (entryType: String, title: String, details: String, category: String, dateString: String) -> Unit
) {
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val yesterdayStr = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    var entryType by remember(existing, initialType) { mutableStateOf(existing?.entryType ?: initialType) }
    var title by remember(existing) { mutableStateOf(existing?.title ?: "") }
    var details by remember(existing) { mutableStateOf(existing?.details ?: "") }
    var category by remember(existing) { mutableStateOf(existing?.category ?: "GENERAL") }
    var dateString by remember(existing) { mutableStateOf(existing?.dateString ?: todayStr) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = if (existing == null) "Record Feedback" else "Edit Feedback",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = studentName,
                    fontSize = 12.sp,
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
                // Type Switcher: Appreciation vs Complaint
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isPraise = entryType == "APPRECIATION"
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isPraise) Color(0xFF16A34A) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clickable { entryType = "APPRECIATION" }
                            .testTag("btn_type_appreciation")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "🌟 Appreciation",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPraise) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (!isPraise) Color(0xFFDC2626) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clickable { entryType = "COMPLAINT" }
                            .testTag("btn_type_complaint")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "⚠️ Complaint",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isPraise) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Category Chips
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Category",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(FEEDBACK_CATEGORIES) { (key, label) ->
                            FilterChip(
                                selected = category.equals(key, ignoreCase = true),
                                onClick = { category = key },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = {
                        Text(
                            if (entryType == "APPRECIATION") "Appreciation Title *" else "Complaint Summary *"
                        )
                    },
                    placeholder = {
                        Text(
                            if (entryType == "APPRECIATION") {
                                "e.g. Fast saree folding & polite customer service"
                            } else {
                                "e.g. Arrived 40 mins late without informing"
                            }
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_feedback_title")
                )

                // Details
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Details / Remarks (Optional)") },
                    placeholder = { Text("Add incident details, context, or manager note...") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_feedback_details")
                )

                // Date Picker Chips + Field
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = dateString == todayStr,
                            onClick = { dateString = todayStr },
                            label = { Text("Today", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = dateString == yesterdayStr,
                            onClick = { dateString = yesterdayStr },
                            label = { Text("Yesterday", fontSize = 11.sp) }
                        )
                    }
                    OutlinedTextField(
                        value = dateString,
                        onValueChange = { dateString = it },
                        label = { Text("Date (yyyy-MM-dd)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_feedback_date")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            entryType,
                            title.trim(),
                            details.trim(),
                            category,
                            dateString.trim().ifBlank { todayStr }
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (entryType == "APPRECIATION") Color(0xFF16A34A) else Color(0xFFDC2626)
                ),
                modifier = Modifier.testTag("btn_save_feedback")
            ) {
                Text(if (existing == null) "Save" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
