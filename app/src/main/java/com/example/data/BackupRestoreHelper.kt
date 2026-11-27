package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupRestoreHelper {

    private const val PREFS_BACKUP_KEY = "offline_full_backup_json"
    private const val PREFS_BACKUP_TIME = "offline_full_backup_time"

    suspend fun createBackupJson(repository: AttendanceRepository): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        root.put("dateFormatted", sdf.format(Date()))

        // 1. Classes / Departments
        val classes = repository.getAllClassesList()
        val classesArray = JSONArray()
        for (c in classes) {
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("name", c.name)
            obj.put("colorHex", c.colorHex)
            obj.put("displayOrder", c.displayOrder)
            classesArray.put(obj)
        }
        root.put("classes", classesArray)

        // 2. Shifts
        val shifts = repository.getAllShiftsList()
        val shiftsArray = JSONArray()
        for (s in shifts) {
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("name", s.name)
            obj.put("timings", s.timings)
            shiftsArray.put(obj)
        }
        root.put("shifts", shiftsArray)

        // 3. Students / Staff
        val students = repository.getAllStudentsList()
        val studentsArray = JSONArray()
        for (st in students) {
            val obj = JSONObject()
            obj.put("id", st.id)
            obj.put("classId", st.classId)
            obj.put("name", st.name)
            obj.put("rollNumber", st.rollNumber)
            obj.put("phone", st.phone)
            obj.put("email", st.email)
            if (st.shiftId != null) obj.put("shiftId", st.shiftId)
            studentsArray.put(obj)
        }
        root.put("students", studentsArray)

        // 4. Attendance Records
        val records = repository.getAllRecordsList()
        val recordsArray = JSONArray()
        for (r in records) {
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("studentId", r.studentId)
            obj.put("classId", r.classId)
            obj.put("dateString", r.dateString)
            obj.put("status", r.status)
            obj.put("checkInTime", r.checkInTime)
            obj.put("checkOutTime", r.checkOutTime)
            obj.put("notes", r.notes)
            obj.put("reason", r.reason)
            obj.put("timestamp", r.timestamp)
            recordsArray.put(obj)
        }
        root.put("records", recordsArray)

        // 5. Hisab Products
        val products = repository.getAllHisabProductsList()
        val productsArray = JSONArray()
        for (p in products) {
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("name", p.name)
            obj.put("category", p.category)
            obj.put("ratePerPiece", p.ratePerPiece)
            obj.put("displayOrder", p.displayOrder)
            productsArray.put(obj)
        }
        root.put("hisabProducts", productsArray)

        // 6. Hisab Ledger Entries
        val entries = repository.getAllHisabEntriesList()
        val entriesArray = JSONArray()
        for (e in entries) {
            val obj = JSONObject()
            obj.put("id", e.id)
            obj.put("studentId", e.studentId)
            obj.put("dateString", e.dateString)
            obj.put("entryType", e.entryType)
            obj.put("itemBreakdownJson", e.itemBreakdownJson)
            obj.put("totalPieces", e.totalPieces)
            obj.put("totalEarned", e.totalEarned)
            obj.put("advancePaid", e.advancePaid)
            obj.put("notes", e.notes)
            obj.put("timestamp", e.timestamp)
            entriesArray.put(obj)
        }
        root.put("hisabEntries", entriesArray)

        // 7. Checklist Items
        val checklist = repository.getAllChecklistItemsList()
        val checklistArray = JSONArray()
        for (item in checklist) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("studentId", item.studentId)
            obj.put("title", item.title)
            obj.put("category", item.category)
            obj.put("isCompleted", item.isCompleted)
            obj.put("priority", item.priority)
            obj.put("notes", item.notes)
            obj.put("timeDue", item.timeDue)
            obj.put("assignedStaffName", item.assignedStaffName)
            obj.put("displayOrder", item.displayOrder)
            obj.put("completedAt", item.completedAt)
            obj.put("createdAt", item.createdAt)
            obj.put("itemType", item.itemType)
            obj.put("subtasksJson", item.subtasksJson)
            obj.put("dailyComment", item.dailyComment)
            obj.put("dailyCommentDate", item.dailyCommentDate)
            obj.put("dailyCommentHistoryJson", item.dailyCommentHistoryJson)
            checklistArray.put(obj)
        }
        root.put("checklistItems", checklistArray)

        root.toString(2)
    }

    suspend fun autoSaveBackup(context: Context, repository: AttendanceRepository): String? = withContext(Dispatchers.IO) {
        try {
            val jsonStr = createBackupJson(repository)
            
            // 1. Save to SharedPreferences
            val prefs = context.getSharedPreferences("app_backup_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString(PREFS_BACKUP_KEY, jsonStr)
                .putLong(PREFS_BACKUP_TIME, System.currentTimeMillis())
                .apply()

            // 2. Save to external files dir (persistent across app launches)
            val extDir = context.getExternalFilesDir(null)
            if (extDir != null) {
                val backupFile = File(extDir, "Attendance_AutoBackup.json")
                backupFile.writeText(jsonStr)
            }

            // 3. Save to Public Downloads folder if possible
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (downloadsDir != null && (downloadsDir.exists() || downloadsDir.mkdirs())) {
                    val pubFile = File(downloadsDir, "AttendanceApp_AutoBackup.json")
                    pubFile.writeText(jsonStr)
                }
            } catch (_: Exception) {
            }

            jsonStr
        } catch (e: Exception) {
            null
        }
    }

    suspend fun restoreFromBackupJson(jsonString: String, repository: AttendanceRepository): Result<String> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            
            // Parse Classes
            val classesArray = root.optJSONArray("classes") ?: JSONArray()
            val classList = mutableListOf<ClassEntity>()
            for (i in 0 until classesArray.length()) {
                val obj = classesArray.getJSONObject(i)
                classList.add(
                    ClassEntity(
                        id = obj.optLong("id", 0L),
                        name = obj.getString("name"),
                        colorHex = obj.optString("colorHex", "#2563EB"),
                        displayOrder = obj.optInt("displayOrder", i)
                    )
                )
            }

            // Parse Shifts
            val shiftsArray = root.optJSONArray("shifts") ?: JSONArray()
            val shiftList = mutableListOf<ShiftEntity>()
            for (i in 0 until shiftsArray.length()) {
                val obj = shiftsArray.getJSONObject(i)
                shiftList.add(
                    ShiftEntity(
                        id = obj.optLong("id", 0L),
                        name = obj.getString("name"),
                        timings = obj.optString("timings", "")
                    )
                )
            }

            // Parse Students
            val studentsArray = root.optJSONArray("students") ?: JSONArray()
            val studentList = mutableListOf<StudentEntity>()
            for (i in 0 until studentsArray.length()) {
                val obj = studentsArray.getJSONObject(i)
                val shiftId = if (obj.has("shiftId") && !obj.isNull("shiftId")) obj.getLong("shiftId") else null
                studentList.add(
                    StudentEntity(
                        id = obj.optLong("id", 0L),
                        classId = obj.getLong("classId"),
                        name = obj.getString("name").cleanEmployeeName(),
                        rollNumber = obj.optString("rollNumber", "EMP-${i + 1}").trim(),
                        phone = obj.optString("phone", ""),
                        email = obj.optString("email", ""),
                        shiftId = shiftId
                    )
                )
            }

            // Parse Records
            val recordsArray = root.optJSONArray("records") ?: JSONArray()
            val recordList = mutableListOf<AttendanceRecordEntity>()
            for (i in 0 until recordsArray.length()) {
                val obj = recordsArray.getJSONObject(i)
                recordList.add(
                    AttendanceRecordEntity(
                        id = obj.optLong("id", 0L),
                        studentId = obj.getLong("studentId"),
                        classId = obj.getLong("classId"),
                        dateString = obj.getString("dateString"),
                        status = obj.optString("status", "P"),
                        checkInTime = obj.optString("checkInTime", ""),
                        checkOutTime = obj.optString("checkOutTime", ""),
                        notes = obj.optString("notes", ""),
                        reason = obj.optString("reason", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }

            // Parse Hisab Products
            val productsArray = root.optJSONArray("hisabProducts") ?: JSONArray()
            val productList = mutableListOf<HisabProductEntity>()
            for (i in 0 until productsArray.length()) {
                val obj = productsArray.getJSONObject(i)
                productList.add(
                    HisabProductEntity(
                        id = obj.optLong("id", 0L),
                        name = obj.getString("name"),
                        category = obj.optString("category", "SAREE"),
                        ratePerPiece = obj.optDouble("ratePerPiece", 0.0),
                        displayOrder = obj.optInt("displayOrder", i)
                    )
                )
            }

            // Parse Hisab Entries
            val entriesArray = root.optJSONArray("hisabEntries") ?: JSONArray()
            val entryList = mutableListOf<HisabLedgerEntryEntity>()
            for (i in 0 until entriesArray.length()) {
                val obj = entriesArray.getJSONObject(i)
                entryList.add(
                    HisabLedgerEntryEntity(
                        id = obj.optLong("id", 0L),
                        studentId = obj.getLong("studentId"),
                        dateString = obj.getString("dateString"),
                        entryType = obj.optString("entryType", "SALES"),
                        itemBreakdownJson = obj.optString("itemBreakdownJson", ""),
                        totalPieces = obj.optInt("totalPieces", 0),
                        totalEarned = obj.optDouble("totalEarned", 0.0),
                        advancePaid = obj.optDouble("advancePaid", 0.0),
                        notes = obj.optString("notes", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }

            // Parse Checklist Items
            val checklistArray = root.optJSONArray("checklistItems") ?: JSONArray()
            val checklistItemList = mutableListOf<ChecklistItemEntity>()
            for (i in 0 until checklistArray.length()) {
                val obj = checklistArray.getJSONObject(i)
                checklistItemList.add(
                    ChecklistItemEntity(
                        id = obj.optLong("id", 0L),
                        studentId = obj.optLong("studentId", 0L),
                        title = obj.getString("title"),
                        category = obj.optString("category", "DAILY"),
                        isCompleted = obj.optBoolean("isCompleted", false),
                        priority = obj.optString("priority", "NORMAL"),
                        notes = obj.optString("notes", ""),
                        timeDue = obj.optString("timeDue", ""),
                        assignedStaffName = obj.optString("assignedStaffName", ""),
                        displayOrder = obj.optInt("displayOrder", i),
                        completedAt = obj.optLong("completedAt", 0L),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        itemType = obj.optString("itemType", "CHECKLIST"),
                        subtasksJson = obj.optString("subtasksJson", ""),
                        dailyComment = obj.optString("dailyComment", ""),
                        dailyCommentDate = obj.optLong("dailyCommentDate", 0L),
                        dailyCommentHistoryJson = obj.optString("dailyCommentHistoryJson", "")
                    )
                )
            }

            if (classList.isEmpty() && studentList.isEmpty() && productList.isEmpty() && checklistItemList.isEmpty()) {
                return@withContext Result.failure(Exception("Backup file does not contain valid data."))
            }

            // Safe insertion into repository
            val insertedClassIds = mutableSetOf<Long>()
            for (c in classList) {
                try {
                    val id = repository.insertClass(c)
                    insertedClassIds.add(id)
                    insertedClassIds.add(c.id)
                } catch (e: Exception) { }
            }

            for (s in shiftList) {
                try {
                    repository.insertShift(s)
                } catch (e: Exception) { }
            }

            val insertedStudentIds = mutableSetOf<Long>()
            val fallbackClassId = insertedClassIds.firstOrNull() ?: 1L
            for (st in studentList) {
                try {
                    val safeStudent = if (insertedClassIds.contains(st.classId)) st else st.copy(classId = fallbackClassId)
                    val id = repository.insertStudent(safeStudent)
                    insertedStudentIds.add(id)
                    insertedStudentIds.add(st.id)
                } catch (e: Exception) { }
            }

            for (r in recordList) {
                try {
                    if (insertedStudentIds.contains(r.studentId) && insertedClassIds.contains(r.classId)) {
                        repository.insertRecord(r)
                    }
                } catch (e: Exception) { }
            }

            if (productList.isNotEmpty()) {
                try {
                    repository.insertHisabProducts(productList)
                } catch (e: Exception) { }
            }

            for (e in entryList) {
                try {
                    if (insertedStudentIds.contains(e.studentId)) {
                        repository.insertHisabEntry(e)
                    }
                } catch (ex: Exception) { }
            }

            if (checklistItemList.isNotEmpty()) {
                try {
                    repository.insertChecklistItems(checklistItemList)
                } catch (e: Exception) { }
            }

            Result.success("Restored ${classList.size} departments, ${studentList.size} staff members, ${recordList.size} attendance records, ${entryList.size} T-Hisab entries, and ${checklistItemList.size} checklist items!")
        } catch (e: Exception) {
            Result.failure(Exception("Failed to restore backup: ${e.message}"))
        }
    }

    fun findExistingBackup(context: Context): String? {
        // 1. Check SharedPreferences
        val prefs = context.getSharedPreferences("app_backup_prefs", Context.MODE_PRIVATE)
        val savedJson = prefs.getString(PREFS_BACKUP_KEY, null)
        if (!savedJson.isNullOrBlank()) {
            return savedJson
        }

        // 2. Check Public Downloads
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val pubFile = File(downloadsDir, "AttendanceApp_AutoBackup.json")
            if (pubFile.exists() && pubFile.length() > 20) {
                return pubFile.readText()
            }
        } catch (_: Exception) {
        }

        // 3. Check External files dir
        try {
            val extDir = context.getExternalFilesDir(null)
            val backupFile = File(extDir, "Attendance_AutoBackup.json")
            if (backupFile.exists() && backupFile.length() > 20) {
                return backupFile.readText()
            }
        } catch (_: Exception) {
        }

        return null
    }

    fun shareBackupFile(context: Context, jsonString: String): File? {
        return try {
            val cacheDir = context.cacheDir
            val backupDir = File(cacheDir, "backups")
            if (!backupDir.exists()) backupDir.mkdirs()
            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val file = File(backupDir, "Attendance_Backup_${sdf.format(Date())}.json")
            file.writeText(jsonString)

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Attendance App Backup")
                putExtra(Intent.EXTRA_TEXT, "Here is the backup file for Attendance Manager. You can restore your staff and attendance anytime!")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Share / Save Backup File")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)

            file
        } catch (e: Exception) {
            null
        }
    }
}
