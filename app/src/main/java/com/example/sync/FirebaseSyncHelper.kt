package com.example.sync

import android.util.Log
import com.example.data.AttendanceRecordEntity
import com.example.data.AttendanceRepository
import com.example.data.BackupRestoreHelper
import com.example.data.ClassEntity
import com.example.data.ShiftEntity
import com.example.data.StudentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object FirebaseSyncHelper {
    private const val TAG = "FirebaseSyncHelper"
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    /**
     * Sanitizes a Firebase Realtime Database URL and constructs the full endpoint for the workspace node.
     */
    fun buildEndpointUrl(rawUrl: String, workspaceKey: String, authToken: String = ""): String {
        var base = rawUrl.trim()
        if (!base.startsWith("http://") && !base.startsWith("https://")) {
            base = "https://$base"
        }
        if (base.endsWith("/")) {
            base = base.substring(0, base.length - 1)
        }
        if (base.endsWith(".json")) {
            base = base.substring(0, base.length - 5)
        }

        val cleanKey = workspaceKey.trim().ifBlank { "shop_attendance" }
            .replace("[.#$\\[\\]/ ]".toRegex(), "_")

        val urlBuilder = StringBuilder("$base/$cleanKey.json")
        if (authToken.isNotBlank()) {
            urlBuilder.append("?auth=").append(authToken.trim())
        }
        return urlBuilder.toString()
    }

    /**
     * Pushes complete state (classes, shifts, staff, attendance records) to Firebase Realtime Database.
     */
    suspend fun syncToFirebase(
        firebaseUrl: String,
        workspaceKey: String,
        authToken: String,
        repository: AttendanceRepository
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (firebaseUrl.isBlank()) {
                return@withContext Result.failure(Exception("Firebase Database URL is required."))
            }

            val endpoint = buildEndpointUrl(firebaseUrl, workspaceKey, authToken)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            val classes = repository.getAllClassesList()
            val shifts = repository.getAllShiftsList()
            val students = repository.getAllStudentsList()
            val records = repository.getAllRecordsList()
            val hisabProducts = repository.getAllHisabProductsList()
            val hisabEntries = repository.getAllHisabEntriesList()

            val rootJson = JSONObject().apply {
                put("version", 1)
                put("lastSyncTimestamp", System.currentTimeMillis())
                put("lastSyncFormatted", sdf.format(Date()))
                put("workspace", workspaceKey.ifBlank { "Main Workspace" })

                // Classes
                val classesArray = JSONArray()
                classes.forEach { c ->
                    classesArray.put(JSONObject().apply {
                        put("id", c.id)
                        put("name", c.name)
                        put("colorHex", c.colorHex)
                        put("displayOrder", c.displayOrder)
                    })
                }
                put("classes", classesArray)

                // Shifts
                val shiftsArray = JSONArray()
                shifts.forEach { s ->
                    shiftsArray.put(JSONObject().apply {
                        put("id", s.id)
                        put("name", s.name)
                        put("timings", s.timings)
                    })
                }
                put("shifts", shiftsArray)

                // Students / Staff
                val studentsArray = JSONArray()
                students.forEach { st ->
                    studentsArray.put(JSONObject().apply {
                        put("id", st.id)
                        put("classId", st.classId)
                        put("name", st.name)
                        put("rollNumber", st.rollNumber)
                        put("phone", st.phone)
                        put("email", st.email)
                        if (st.shiftId != null) put("shiftId", st.shiftId)
                    })
                }
                put("students", studentsArray)

                // Attendance Records
                val recordsArray = JSONArray()
                records.forEach { r ->
                    recordsArray.put(JSONObject().apply {
                        put("id", r.id)
                        put("studentId", r.studentId)
                        put("classId", r.classId)
                        put("dateString", r.dateString)
                        put("status", r.status)
                        put("checkInTime", r.checkInTime)
                        put("checkOutTime", r.checkOutTime)
                        put("notes", r.notes)
                        put("reason", r.reason)
                        put("timestamp", r.timestamp)
                    })
                }
                put("records", recordsArray)

                // Hisab Products
                val productsArray = JSONArray()
                hisabProducts.forEach { p ->
                    productsArray.put(JSONObject().apply {
                        put("id", p.id)
                        put("name", p.name)
                        put("category", p.category)
                        put("ratePerPiece", p.ratePerPiece)
                        put("displayOrder", p.displayOrder)
                    })
                }
                put("hisabProducts", productsArray)

                // Hisab Ledger Entries
                val entriesArray = JSONArray()
                hisabEntries.forEach { e ->
                    entriesArray.put(JSONObject().apply {
                        put("id", e.id)
                        put("studentId", e.studentId)
                        put("dateString", e.dateString)
                        put("entryType", e.entryType)
                        put("itemBreakdownJson", e.itemBreakdownJson)
                        put("totalPieces", e.totalPieces)
                        put("totalEarned", e.totalEarned)
                        put("advancePaid", e.advancePaid)
                        put("notes", e.notes)
                        put("timestamp", e.timestamp)
                    })
                }
                put("hisabEntries", entriesArray)
            }

            val requestBody = rootJson.toString().toRequestBody(JSON_MEDIA_TYPE)
            val request = Request.Builder()
                .url(endpoint)
                .put(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: "HTTP ${response.code}"
                return@withContext Result.failure(Exception("Firebase returned error ${response.code}: $errBody"))
            }

            Result.success("Auto-synced to Firebase: ${students.size} staff, ${records.size} records synced safely to the cloud!")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing to Firebase", e)
            Result.failure(Exception(e.message ?: "Failed to connect to Firebase"))
        }
    }

    /**
     * Pulls entire database snapshot from Firebase and restores it into Room local database.
     */
    suspend fun restoreFromFirebase(
        firebaseUrl: String,
        workspaceKey: String,
        authToken: String,
        repository: AttendanceRepository
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (firebaseUrl.isBlank()) {
                return@withContext Result.failure(Exception("Firebase Database URL is required."))
            }

            val endpoint = buildEndpointUrl(firebaseUrl, workspaceKey, authToken)
            val request = Request.Builder()
                .url(endpoint)
                .get()
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: "HTTP ${response.code}"
                return@withContext Result.failure(Exception("Firebase error ${response.code}: $errBody"))
            }

            val responseBody = response.body?.string()
            if (responseBody.isNullOrBlank() || responseBody == "null") {
                return@withContext Result.failure(Exception("No cloud data found in Firebase for this workspace."))
            }

            // Restore from JSON payload using BackupRestoreHelper logic
            val restoreResult = BackupRestoreHelper.restoreFromBackupJson(responseBody, repository)
            if (restoreResult.isSuccess) {
                Result.success("Successfully restored from Firebase! All staff, shifts, and attendance records are now available.")
            } else {
                Result.failure(restoreResult.exceptionOrNull() ?: Exception("Failed to parse Firebase data."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error restoring from Firebase", e)
            Result.failure(Exception(e.message ?: "Failed to connect to Firebase"))
        }
    }

    /**
     * Tests connectivity to the provided Firebase URL.
     */
    suspend fun testConnection(
        firebaseUrl: String,
        workspaceKey: String,
        authToken: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (firebaseUrl.isBlank()) {
                return@withContext Result.failure(Exception("Please enter a Firebase Database URL."))
            }
            val endpoint = buildEndpointUrl(firebaseUrl, workspaceKey, authToken)
            val request = Request.Builder()
                .url(endpoint)
                .get()
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success("Connection Successful! Firebase Database is reachable and ready for automatic cloud sync.")
            } else {
                val code = response.code
                val body = response.body?.string() ?: ""
                val errorExplanation = when (code) {
                    404 -> "Database Not Found (404). This means Realtime Database has not been created yet in your Firebase console. Please go to console.firebase.google.com -> Realtime Database -> click 'Create Database' and copy the URL provided there."
                    401, 403 -> "Permission Denied ($code). Please open Firebase Console -> Realtime Database -> Rules tab, and set \".read\": true and \".write\": true (then tap Publish)."
                    else -> "Connection Failed ($code): $body. Check your Database URL and internet connection."
                }
                Result.failure(Exception(errorExplanation))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Connection Failed: ${e.localizedMessage ?: "Unknown error"}. Check internet or URL."))
        }
    }
}
