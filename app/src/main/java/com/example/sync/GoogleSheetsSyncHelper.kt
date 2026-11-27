package com.example.sync

import android.util.Log
import com.example.data.AttendanceRecordEntity
import com.example.data.ClassEntity
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

data class SheetAttendanceRecord(
    val date: String,
    val staffName: String,
    val rollNumber: String,
    val status: String,
    val checkIn: String,
    val checkOut: String,
    val notes: String,
    val reason: String = ""
)

object GoogleSheetsSyncHelper {
    private const val TAG = "GoogleSheetsSyncHelper"

    // OkHttpClient with generous timeouts for Google Apps Script redirects
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    /**
     * Fetches all attendance records from Google Sheets via Apps Script Web App (GET or POST fetch_data).
     */
    suspend fun fetchRecordsFromWebApp(webAppUrl: String): Result<List<SheetAttendanceRecord>> = withContext(Dispatchers.IO) {
        try {
            if (webAppUrl.isEmpty() || !webAppUrl.startsWith("http")) {
                return@withContext Result.failure(Exception("Invalid Web App URL. Please enter a valid Google Apps Script Web App URL."))
            }

            // First try GET request
            val getRequest = Request.Builder()
                .url(webAppUrl)
                .get()
                .build()

            var responseStr: String? = null
            try {
                client.newCall(getRequest).execute().use { response ->
                    if (response.isSuccessful) {
                        responseStr = response.body?.string()
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "GET failed, will try POST", e)
            }

            // If GET was empty or failed, try POST with action "fetch_data"
            if (responseStr.isNullOrBlank()) {
                val payload = JSONObject().apply {
                    put("action", "fetch_data")
                }
                val postRequest = Request.Builder()
                    .url(webAppUrl)
                    .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
                    .build()
                client.newCall(postRequest).execute().use { response ->
                    if (response.isSuccessful) {
                        responseStr = response.body?.string()
                    } else {
                        return@withContext Result.failure(Exception("HTTP Error ${response.code}: ${response.message}"))
                    }
                }
            }

            val body = responseStr ?: return@withContext Result.failure(Exception("Empty response received from Apps Script."))
            Log.d(TAG, "Fetch Response: $body")

            val json = JSONObject(body)
            val recordsJson = json.optJSONArray("records") ?: JSONArray()
            val list = mutableListOf<SheetAttendanceRecord>()

            for (i in 0 until recordsJson.length()) {
                val item = recordsJson.getJSONObject(i)
                val date = item.optString("date", "").trim()
                val name = item.optString("staffName", item.optString("name", "")).trim()
                val roll = item.optString("rollNumber", item.optString("roll", "")).trim()
                val status = item.optString("status", "P").trim()
                val checkIn = item.optString("checkIn", item.optString("checkInTime", "")).trim()
                val checkOut = item.optString("checkOut", item.optString("checkOutTime", "")).trim()
                val notes = item.optString("notes", "").trim()
                val reason = item.optString("reason", "").trim()

                if (name.isNotEmpty() || roll.isNotEmpty()) {
                    list.add(
                        SheetAttendanceRecord(
                            date = date,
                            staffName = if (name.isNotEmpty()) name else "Staff $roll",
                            rollNumber = if (roll.isNotEmpty()) roll else "EMP-${i + 1}",
                            status = status,
                            checkIn = checkIn,
                            checkOut = checkOut,
                            notes = notes,
                            reason = reason
                        )
                    )
                }
            }

            Result.success(list)
        } catch (e: Exception) {
            Log.e(TAG, "fetchRecordsFromWebApp failed", e)
            Result.failure(Exception("Failed to fetch records from Web App: ${e.message}"))
        }
    }

    /**
     * Syncs attendance records to a Google Sheet using a Google Apps Script Web App.
     * This is the most reliable, robust method for mobile apps.
     */
    suspend fun syncViaWebApp(
        webAppUrl: String,
        department: ClassEntity,
        staffList: List<StudentEntity>,
        records: List<AttendanceRecordEntity>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (webAppUrl.isEmpty() || !webAppUrl.startsWith("http")) {
                return@withContext Result.failure(Exception("Invalid Web App URL. Please configure a valid Google Apps Script Web App URL."))
            }

            // Group records by staff ID for easier processing
            val recordsJson = JSONArray()
            records.forEach { record ->
                val staff = staffList.find { it.id == record.studentId }
                if (staff != null) {
                    val rObj = JSONObject().apply {
                        put("date", record.dateString)
                        put("staffName", staff.name)
                        put("rollNumber", staff.rollNumber)
                        put("status", record.status)
                        put("checkIn", record.checkInTime)
                        put("checkOut", record.checkOutTime)
                        put("notes", record.notes)
                        put("reason", record.reason)
                    }
                    recordsJson.put(rObj)
                }
            }

            val payload = JSONObject().apply {
                put("action", "sync_attendance")
                put("departmentName", department.name)
                put("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
                put("records", recordsJson)
            }

            val requestBody = payload.toString().toRequestBody(JSON_MEDIA_TYPE)
            val request = Request.Builder()
                .url(webAppUrl)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                Log.d(TAG, "WebApp Response: Code ${response.code}, Body: $responseBody")

                if (response.isSuccessful) {
                    // Try to parse JSON success response
                    val isSuccess = try {
                        val json = JSONObject(responseBody)
                        json.optString("status") == "success" || json.optString("result") == "success"
                    } catch (e: Exception) {
                        // If not valid JSON, but response is 200, assume success if response contains "success" or "ok"
                        responseBody.lowercase().contains("success") || responseBody.lowercase().contains("ok") || responseBody.isEmpty()
                    }

                    if (isSuccess) {
                        Result.success("Successfully synced ${records.size} attendance records to Google Sheet via Web App!")
                    } else {
                        Result.failure(Exception("Apps Script returned error or unconfirmed status: $responseBody"))
                    }
                } else {
                    Result.failure(Exception("HTTP Error ${response.code}: ${response.message}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sync via WebApp failed", e)
            Result.failure(e)
        }
    }

    /**
     * Direct sync via Google Sheets REST API.
     * Appends rows directly to the specified spreadsheet.
     */
    suspend fun syncViaRestApi(
        spreadsheetId: String,
        accessToken: String,
        department: ClassEntity,
        staffList: List<StudentEntity>,
        records: List<AttendanceRecordEntity>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (spreadsheetId.isEmpty()) {
                return@withContext Result.failure(Exception("Spreadsheet ID is required for direct API sync."))
            }
            if (accessToken.isEmpty()) {
                return@withContext Result.failure(Exception("OAuth Access Token is required. Please authorize Google Sheets sync first."))
            }

            // Sheets API endpoint to append values
            // We write to a sheet named after the department. Sheets API automatically creates it if it doesn't exist,
            // or we write to a general sheet. Let's use the department name as sheet name.
            val sheetName = department.name.replace("[^a-zA-Z0-9 ]".toRegex(), "").take(20)
            val range = "'$sheetName'!A1"

            // Construct values matrix: row-by-row
            // First we need to make sure headers exist.
            // Let's first read the sheet or just append. Google Sheets append automatically inserts rows.
            // If the sheet is empty, it appends at the top.
            val valuesArray = JSONArray()
            
            // If we want to append header too, we can do that or rely on Appending values
            records.forEach { record ->
                val staff = staffList.find { it.id == record.studentId }
                if (staff != null) {
                    val row = JSONArray().apply {
                        put(record.dateString)
                        put(staff.name)
                        put(staff.rollNumber)
                        put(record.status)
                        put(record.checkInTime)
                        put(record.checkOutTime)
                        put(record.notes)
                    }
                    valuesArray.put(row)
                }
            }

            if (valuesArray.length() == 0) {
                return@withContext Result.failure(Exception("No attendance records to sync."))
            }

            // Build request payload for batchUpdate/append
            val payload = JSONObject().apply {
                put("range", range)
                put("majorDimension", "ROWS")
                put("values", valuesArray)
            }

            val requestBody = payload.toString().toRequestBody(JSON_MEDIA_TYPE)
            val url = "https://sheets.googleapis.com/v1/spreadsheets/$spreadsheetId/values/$range:append?valueInputOption=USER_ENTERED"

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $accessToken")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                Log.d(TAG, "RestAPI Response: Code ${response.code}, Body: $responseBody")

                if (response.isSuccessful) {
                    Result.success("Direct Sheets API Sync Success! Synced ${records.size} rows.")
                } else {
                    val errorMsg = try {
                        JSONObject(responseBody).getJSONObject("error").getString("message")
                    } catch (e: Exception) {
                        response.message
                    }
                    Result.failure(Exception("Google Sheets API Error ($errorMsg). Please check spreadsheet permissions and access token validity."))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sync via REST API failed", e)
            Result.failure(e)
        }
    }

    /**
     * Direct API call to create a new spreadsheet and return its ID.
     */
    suspend fun createSpreadsheet(
        accessToken: String,
        title: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (accessToken.isEmpty()) {
                return@withContext Result.failure(Exception("OAuth Access Token is required to create a spreadsheet."))
            }

            val payload = JSONObject().apply {
                put("properties", JSONObject().apply {
                    put("title", title)
                })
                // Add default sheets with headers
                put("sheets", JSONArray().apply {
                    put(JSONObject().apply {
                        put("properties", JSONObject().apply {
                            put("title", "Attendance Log")
                        })
                    })
                })
            }

            val requestBody = payload.toString().toRequestBody(JSON_MEDIA_TYPE)
            val request = Request.Builder()
                .url("https://sheets.googleapis.com/v1/spreadsheets")
                .header("Authorization", "Bearer $accessToken")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                Log.d(TAG, "Create Spreadsheet Response: ${response.code}, Body: $responseBody")

                if (response.isSuccessful) {
                    val json = JSONObject(responseBody)
                    val spreadsheetId = json.getString("spreadsheetId")
                    
                    // Now, write headers to this newly created sheet
                    writeHeadersToSpreadsheet(spreadsheetId, accessToken, "Attendance Log")
                    
                    Result.success(spreadsheetId)
                } else {
                    val errorMsg = try {
                        JSONObject(responseBody).getJSONObject("error").getString("message")
                    } catch (e: Exception) {
                        response.message
                    }
                    Result.failure(Exception("Failed to create spreadsheet: $errorMsg"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Create spreadsheet failed", e)
            Result.failure(e)
        }
    }

    private suspend fun writeHeadersToSpreadsheet(
        spreadsheetId: String,
        accessToken: String,
        sheetName: String
    ) = withContext(Dispatchers.IO) {
        try {
            val headers = JSONArray().apply {
                put(JSONArray().apply {
                    put("Date")
                    put("Staff/Employee Name")
                    put("Roll/Employee ID")
                    put("Status")
                    put("Check In Time")
                    put("Check Out Time")
                    put("Notes")
                })
            }

            val payload = JSONObject().apply {
                put("range", "'$sheetName'!A1:G1")
                put("majorDimension", "ROWS")
                put("values", headers)
            }

            val requestBody = payload.toString().toRequestBody(JSON_MEDIA_TYPE)
            val url = "https://sheets.googleapis.com/v1/spreadsheets/$spreadsheetId/values/'$sheetName'!A1:G1?valueInputOption=USER_ENTERED"

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $accessToken")
                .put(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                Log.d(TAG, "Header Write Response: Code ${response.code}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write headers to new spreadsheet", e)
        }
    }
}
