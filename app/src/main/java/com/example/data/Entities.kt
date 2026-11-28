package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "classes")
data class ClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String,
    val displayOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "students",
    foreignKeys = [
        ForeignKey(
            entity = ClassEntity::class,
            parentColumns = ["id"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["classId"])]
)
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classId: Long,
    val name: String,
    val rollNumber: String,
    val phone: String = "",
    val email: String = "",
    val shiftId: Long? = null, // Associated shift
    val createdAt: Long = System.currentTimeMillis()
)

fun String.cleanEmployeeName(): String =
    this.replace("\r", "")
        .replace("\n", " ")
        .replace("\t", " ")
        .replace("\u00A0", " ")
        .replace("\u200B", "")
        .replace("\u2028", " ")
        .replace("\u2029", " ")
        .replace(Regex("[\\s\\p{Z}]+"), " ")
        .trim()

val StudentEntity.displayName: String
    get() = name.cleanEmployeeName()

@Entity(tableName = "shifts")
data class ShiftEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g. "General Shift", "Morning Shift"
    val timings: String, // e.g. "09:00 AM - 06:00 PM"
    val createdAt: Long = System.currentTimeMillis()
)

val ShiftEntity.displayName: String
    get() = name.cleanEmployeeName()

@Entity(
    tableName = "attendance_records",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ClassEntity::class,
            parentColumns = ["id"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["studentId"]),
        Index(value = ["classId"]),
        Index(value = ["classId", "dateString"])
    ]
)
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val classId: Long,
    val dateString: String, // e.g. "2026-07-04"
    val status: String, // "PRESENT", "ABSENT", "HALF-DAY"
    val checkInTime: String = "", // e.g. "09:00 AM" or empty if absent
    val checkOutTime: String = "", // e.g. "06:00 PM" or empty
    val notes: String = "",
    val reason: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "hisab_products")
data class HisabProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String = "SAREE",
    val ratePerPiece: Double,
    val displayOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "hisab_ledger_entries",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["studentId"]),
        Index(value = ["studentId", "dateString"])
    ]
)
data class HisabLedgerEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val dateString: String, // e.g. "2026-08-23" or "23/08/2026"
    val entryType: String, // "SALES" or "ADVANCE"
    val itemBreakdownJson: String = "", // e.g. "[{\"name\":\"SET-WALI SAREE\",\"qty\":10,\"rate\":100.0}]"
    val totalPieces: Int = 0,
    val totalEarned: Double = 0.0,
    val advancePaid: Double = 0.0,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "checklist_items",
    indices = [Index(value = ["studentId"])]
)
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long = 0L, // 0L = General Shop, >0 = specific Employee/Staff
    val title: String,
    val category: String = "DAILY", // "OPENING", "DAILY", "STOCK", "CLOSING", "GENERAL", "SALES", "CLEANING"
    val isCompleted: Boolean = false,
    val priority: String = "", // Deprecated: preserved for schema backwards-compatibility
    val notes: String = "",
    val timeDue: String = "", // Deprecated: preserved for schema backwards-compatibility
    val assignedStaffName: String = "",
    val displayOrder: Int = 0,
    val reminderTimestamp: Long = 0L,
    val completedAt: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val itemType: String = "CHECKLIST", // "CHECKLIST" or "TASK"
    val subtasksJson: String = "", // JSON array of SubtaskItem
    val dailyComment: String = "", // Daily note/comment for this item
    val dailyCommentDate: Long = 0L, // Timestamp of when dailyComment was written
    val dailyCommentHistoryJson: String = "" // JSON array of past daily notes
)

data class SubtaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false
)

fun ChecklistItemEntity.getSubtasks(): List<SubtaskItem> {
    if (subtasksJson.isBlank()) return emptyList()
    return try {
        val arr = JSONArray(subtasksJson)
        val list = mutableListOf<SubtaskItem>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val t = obj.optString("title", "").trim()
            if (t.isNotBlank()) {
                list.add(
                    SubtaskItem(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        title = t,
                        isCompleted = obj.optBoolean("isCompleted", false)
                    )
                )
            }
        }
        list
    } catch (_: Exception) {
        emptyList()
    }
}

fun List<SubtaskItem>.toJson(): String {
    if (isEmpty()) return ""
    val arr = JSONArray()
    for (item in this) {
        if (item.title.isNotBlank()) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("title", item.title.trim())
            obj.put("isCompleted", item.isCompleted)
            arr.put(obj)
        }
    }
    return arr.toString()
}

fun createSubtasksJson(vararg titles: String): String {
    val list = titles.filter { it.isNotBlank() }.map { SubtaskItem(title = it.trim(), isCompleted = false) }
    return list.toJson()
}

val ChecklistItemEntity.isTask: Boolean
    get() = when {
        itemType.equals("TASK", ignoreCase = true) -> true
        itemType.equals("CHECKLIST", ignoreCase = true) -> false
        else -> category.equals("STOCK", ignoreCase = true) ||
                category.equals("SALES", ignoreCase = true) ||
                category.equals("TASK", ignoreCase = true)
    }

val ChecklistItemEntity.isChecklist: Boolean
    get() = !isTask

fun isSameCalendarDay(time1: Long, time2: Long): Boolean {
    if (time1 <= 0L || time2 <= 0L) return false
    val c1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
           c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}

/**
 * Returns whether this item is currently marked done.
 * - Daily CHECKLIST items: ONLY marked done if completed on the current calendar day (today).
 *   If ticked today, it is ticked today. For tomorrow or subsequent days, it will automatically show un-ticked.
 * - TASKS: Remain completed until explicitly unticked.
 */
val ChecklistItemEntity.isDoneToday: Boolean
    get() {
        if (!isCompleted || completedAt <= 0L) return false
        if (isTask) return isCompleted
        return isSameCalendarDay(completedAt, System.currentTimeMillis())
    }

/**
 * Returns today's active note/comment for this checklist item.
 * Automatically empties if the note was saved on a previous day.
 */
val ChecklistItemEntity.todayComment: String
    get() {
        if (dailyComment.isBlank() || dailyCommentDate <= 0L) return ""
        return if (isSameCalendarDay(dailyCommentDate, System.currentTimeMillis())) dailyComment else ""
    }

data class DailyCommentHistoryItem(
    val dateString: String,
    val timestamp: Long,
    val text: String
)

fun ChecklistItemEntity.getDailyCommentHistory(): List<DailyCommentHistoryItem> {
    if (dailyCommentHistoryJson.isBlank()) return emptyList()
    return try {
        val arr = JSONArray(dailyCommentHistoryJson)
        val list = mutableListOf<DailyCommentHistoryItem>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val text = obj.optString("text", "").trim()
            if (text.isNotBlank()) {
                list.add(
                    DailyCommentHistoryItem(
                        dateString = obj.optString("dateString", ""),
                        timestamp = obj.optLong("timestamp", 0L),
                        text = text
                    )
                )
            }
        }
        list
    } catch (_: Exception) {
        emptyList()
    }
}

@Entity(
    tableName = "staff_feedback",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["studentId"])]
)
data class StaffFeedbackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val entryType: String = "APPRECIATION", // "APPRECIATION" or "COMPLAINT"
    val title: String,
    val details: String = "",
    val category: String = "GENERAL", // "WORK", "BEHAVIOUR", "PUNCTUALITY", "CUSTOMER", "SALES", "GENERAL"
    val dateString: String, // "yyyy-MM-dd"
    val isResolved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
