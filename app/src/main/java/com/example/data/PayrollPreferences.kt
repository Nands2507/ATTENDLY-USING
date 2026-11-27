package com.example.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class SalaryAdvanceRecord(
    val id: String = UUID.randomUUID().toString(),
    val studentId: Long,
    val dateString: String, // e.g. "2026-08-15"
    val amount: Double,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class PayrollPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("payroll_settings_prefs", Context.MODE_PRIVATE)

    fun getSalary(studentId: Long): Double {
        return prefs.getFloat("salary_$studentId", 0.0f).toDouble()
    }

    fun setSalary(studentId: Long, salary: Double) {
        prefs.edit().putFloat("salary_$studentId", salary.toFloat()).apply()
    }

    fun getTeaAllowance(studentId: Long, yearMonth: String): Double {
        if (prefs.contains("tea_${studentId}_$yearMonth")) {
            return prefs.getFloat("tea_${studentId}_$yearMonth", 0.0f).toDouble()
        }
        return prefs.getFloat("default_tea_$studentId", 0.0f).toDouble()
    }

    fun setTeaAllowance(studentId: Long, yearMonth: String, amount: Double) {
        prefs.edit().putFloat("tea_${studentId}_$yearMonth", amount.toFloat()).apply()
    }

    fun setDefaultTeaAllowance(studentId: Long, amount: Double) {
        prefs.edit().putFloat("default_tea_$studentId", amount.toFloat()).apply()
    }

    // ---------------------------------------------------------
    // SALARY ADVANCE RECORDS (SEPARATE FROM T-HISAB ADVANCE)
    // ---------------------------------------------------------
    fun getSalaryAdvances(studentId: Long, yearMonth: String): List<SalaryAdvanceRecord> {
        val all = getAllSalaryAdvances()
        return all.filter { it.studentId == studentId && it.dateString.startsWith(yearMonth) }
    }

    fun getAllSalaryAdvances(): List<SalaryAdvanceRecord> {
        val jsonStr = prefs.getString("salary_advances_list", "[]") ?: "[]"
        val list = mutableListOf<SalaryAdvanceRecord>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    SalaryAdvanceRecord(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        studentId = obj.optLong("studentId"),
                        dateString = obj.optString("dateString"),
                        amount = obj.optDouble("amount", 0.0),
                        notes = obj.optString("notes", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun addSalaryAdvance(studentId: Long, dateString: String, amount: Double, notes: String): SalaryAdvanceRecord {
        val all = getAllSalaryAdvances().toMutableList()
        val newRecord = SalaryAdvanceRecord(
            id = UUID.randomUUID().toString(),
            studentId = studentId,
            dateString = dateString,
            amount = amount,
            notes = notes.trim(),
            timestamp = System.currentTimeMillis()
        )
        all.add(0, newRecord)
        saveAllSalaryAdvances(all)
        return newRecord
    }

    fun updateSalaryAdvance(record: SalaryAdvanceRecord) {
        val all = getAllSalaryAdvances().toMutableList()
        val idx = all.indexOfFirst { it.id == record.id }
        if (idx != -1) {
            all[idx] = record
            saveAllSalaryAdvances(all)
        }
    }

    fun deleteSalaryAdvance(id: String) {
        val all = getAllSalaryAdvances().toMutableList()
        all.removeAll { it.id == id }
        saveAllSalaryAdvances(all)
    }

    private fun saveAllSalaryAdvances(list: List<SalaryAdvanceRecord>) {
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("studentId", item.studentId)
            obj.put("dateString", item.dateString)
            obj.put("amount", item.amount)
            obj.put("notes", item.notes)
            obj.put("timestamp", item.timestamp)
            jsonArray.put(obj)
        }
        prefs.edit().putString("salary_advances_list", jsonArray.toString()).apply()
    }

    fun getOtherBonus(studentId: Long, yearMonth: String): Double {
        return prefs.getFloat("bonus_${studentId}_$yearMonth", 0.0f).toDouble()
    }

    fun setOtherBonus(studentId: Long, yearMonth: String, amount: Double) {
        prefs.edit().putFloat("bonus_${studentId}_$yearMonth", amount.toFloat()).apply()
    }

    fun getWorkingDaysMode(): String {
        return prefs.getString("working_days_mode", "FIXED_30") ?: "FIXED_30"
    }

    fun setWorkingDaysMode(mode: String) {
        prefs.edit().putString("working_days_mode", mode).apply()
    }

    fun isSettled(studentId: Long, yearMonth: String): Boolean {
        return prefs.getBoolean("settled_${studentId}_$yearMonth", false)
    }

    fun setSettled(studentId: Long, yearMonth: String, settled: Boolean, mode: String = "CASH") {
        prefs.edit()
            .putBoolean("settled_${studentId}_$yearMonth", settled)
            .putString("settle_mode_${studentId}_$yearMonth", mode)
            .putLong("settle_time_${studentId}_$yearMonth", System.currentTimeMillis())
            .apply()
    }

    fun getSettledInfo(studentId: Long, yearMonth: String): Pair<Boolean, String> {
        val settled = prefs.getBoolean("settled_${studentId}_$yearMonth", false)
        val mode = prefs.getString("settle_mode_${studentId}_$yearMonth", "CASH") ?: "CASH"
        return Pair(settled, mode)
    }
}
