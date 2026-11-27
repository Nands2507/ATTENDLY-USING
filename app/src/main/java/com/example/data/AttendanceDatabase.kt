package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Database(
    entities = [
        ClassEntity::class,
        StudentEntity::class,
        AttendanceRecordEntity::class,
        ShiftEntity::class,
        HisabProductEntity::class,
        HisabLedgerEntryEntity::class,
        ChecklistItemEntity::class
    ],
    version = 13,
    exportSchema = false
)
abstract class AttendanceDatabase : RoomDatabase() {
    abstract fun classDao(): ClassDao
    abstract fun studentDao(): StudentDao
    abstract fun attendanceRecordDao(): AttendanceRecordDao
    abstract fun shiftDao(): ShiftDao
    abstract fun hisabProductDao(): HisabProductDao
    abstract fun hisabLedgerEntryDao(): HisabLedgerEntryDao
    abstract fun checklistItemDao(): ChecklistItemDao

    companion object {
        @Volatile
        private var INSTANCE: AttendanceDatabase? = null

        private fun hasColumn(db: SupportSQLiteDatabase, tableName: String, columnName: String): Boolean {
            return try {
                db.query("PRAGMA table_info(`$tableName`)").use { cursor ->
                    val nameIndex = cursor.getColumnIndex("name")
                    if (nameIndex != -1) {
                        while (cursor.moveToNext()) {
                            if (columnName.equals(cursor.getString(nameIndex), ignoreCase = true)) {
                                return@use true
                            }
                        }
                    }
                    false
                }
            } catch (_: Exception) {
                false
            }
        }

        private fun safeAddColumnIfNotExists(
            db: SupportSQLiteDatabase,
            tableName: String,
            columnName: String,
            columnDef: String
        ) {
            if (!hasColumn(db, tableName, columnName)) {
                try {
                    db.execSQL("ALTER TABLE `$tableName` ADD COLUMN $columnDef")
                } catch (_: Exception) { }
            }
        }

        private fun ensureAllTablesAndIndices(db: SupportSQLiteDatabase) {
            // 1. Classes
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS classes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    colorHex TEXT NOT NULL,
                    displayOrder INTEGER NOT NULL DEFAULT 0,
                    createdAt INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent())
            safeAddColumnIfNotExists(db, "classes", "displayOrder", "displayOrder INTEGER NOT NULL DEFAULT 0")
            safeAddColumnIfNotExists(db, "classes", "createdAt", "createdAt INTEGER NOT NULL DEFAULT 0")

            // 2. Shifts
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS shifts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    timings TEXT NOT NULL,
                    createdAt INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent())

            // 3. Students
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS students (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    classId INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    rollNumber TEXT NOT NULL,
                    phone TEXT NOT NULL DEFAULT '',
                    email TEXT NOT NULL DEFAULT '',
                    shiftId INTEGER DEFAULT NULL,
                    createdAt INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY(classId) REFERENCES classes(id) ON DELETE CASCADE
                )
            """.trimIndent())
            safeAddColumnIfNotExists(db, "students", "phone", "phone TEXT NOT NULL DEFAULT ''")
            safeAddColumnIfNotExists(db, "students", "email", "email TEXT NOT NULL DEFAULT ''")
            safeAddColumnIfNotExists(db, "students", "shiftId", "shiftId INTEGER DEFAULT NULL")
            safeAddColumnIfNotExists(db, "students", "createdAt", "createdAt INTEGER NOT NULL DEFAULT 0")
            try {
                db.execSQL("CREATE INDEX IF NOT EXISTS index_students_classId ON students(classId)")
            } catch (_: Exception) { }

            // 4. Attendance records
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS attendance_records (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    studentId INTEGER NOT NULL,
                    classId INTEGER NOT NULL,
                    dateString TEXT NOT NULL,
                    status TEXT NOT NULL,
                    checkInTime TEXT NOT NULL DEFAULT '',
                    checkOutTime TEXT NOT NULL DEFAULT '',
                    notes TEXT NOT NULL DEFAULT '',
                    reason TEXT NOT NULL DEFAULT '',
                    timestamp INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY(studentId) REFERENCES students(id) ON DELETE CASCADE,
                    FOREIGN KEY(classId) REFERENCES classes(id) ON DELETE CASCADE
                )
            """.trimIndent())
            safeAddColumnIfNotExists(db, "attendance_records", "checkInTime", "checkInTime TEXT NOT NULL DEFAULT ''")
            safeAddColumnIfNotExists(db, "attendance_records", "checkOutTime", "checkOutTime TEXT NOT NULL DEFAULT ''")
            safeAddColumnIfNotExists(db, "attendance_records", "notes", "notes TEXT NOT NULL DEFAULT ''")
            safeAddColumnIfNotExists(db, "attendance_records", "reason", "reason TEXT NOT NULL DEFAULT ''")
            safeAddColumnIfNotExists(db, "attendance_records", "timestamp", "timestamp INTEGER NOT NULL DEFAULT 0")
            try {
                db.execSQL("CREATE INDEX IF NOT EXISTS index_attendance_records_studentId ON attendance_records(studentId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_attendance_records_classId ON attendance_records(classId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_attendance_records_classId_dateString ON attendance_records(classId, dateString)")
            } catch (_: Exception) { }

            // 5. Hisab Products
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS hisab_products (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    category TEXT NOT NULL DEFAULT 'SAREE',
                    ratePerPiece REAL NOT NULL,
                    displayOrder INTEGER NOT NULL DEFAULT 0,
                    createdAt INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent())

            // 6. Hisab Ledger Entries
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS hisab_ledger_entries (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    studentId INTEGER NOT NULL,
                    dateString TEXT NOT NULL,
                    entryType TEXT NOT NULL,
                    itemBreakdownJson TEXT NOT NULL DEFAULT '',
                    totalPieces INTEGER NOT NULL DEFAULT 0,
                    totalEarned REAL NOT NULL DEFAULT 0.0,
                    advancePaid REAL NOT NULL DEFAULT 0.0,
                    notes TEXT NOT NULL DEFAULT '',
                    timestamp INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY(studentId) REFERENCES students(id) ON DELETE CASCADE
                )
            """.trimIndent())
            try {
                db.execSQL("CREATE INDEX IF NOT EXISTS index_hisab_ledger_entries_studentId ON hisab_ledger_entries(studentId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_hisab_ledger_entries_studentId_dateString ON hisab_ledger_entries(studentId, dateString)")
            } catch (_: Exception) { }

            // 7. Checklist Items
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS checklist_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    studentId INTEGER NOT NULL DEFAULT 0,
                    title TEXT NOT NULL,
                    category TEXT NOT NULL DEFAULT 'DAILY',
                    isCompleted INTEGER NOT NULL DEFAULT 0,
                    priority TEXT NOT NULL DEFAULT 'NORMAL',
                    notes TEXT NOT NULL DEFAULT '',
                    timeDue TEXT NOT NULL DEFAULT '',
                    assignedStaffName TEXT NOT NULL DEFAULT '',
                    displayOrder INTEGER NOT NULL DEFAULT 0,
                    reminderTimestamp INTEGER NOT NULL DEFAULT 0,
                    completedAt INTEGER NOT NULL DEFAULT 0,
                    createdAt INTEGER NOT NULL DEFAULT 0,
                    itemType TEXT NOT NULL DEFAULT 'CHECKLIST'
                )
            """.trimIndent())
            safeAddColumnIfNotExists(db, "checklist_items", "studentId", "studentId INTEGER NOT NULL DEFAULT 0")
            safeAddColumnIfNotExists(db, "checklist_items", "assignedStaffName", "assignedStaffName TEXT NOT NULL DEFAULT ''")
            safeAddColumnIfNotExists(db, "checklist_items", "timeDue", "timeDue TEXT NOT NULL DEFAULT ''")
            safeAddColumnIfNotExists(db, "checklist_items", "notes", "notes TEXT NOT NULL DEFAULT ''")
            safeAddColumnIfNotExists(db, "checklist_items", "priority", "priority TEXT NOT NULL DEFAULT 'NORMAL'")
            safeAddColumnIfNotExists(db, "checklist_items", "reminderTimestamp", "reminderTimestamp INTEGER NOT NULL DEFAULT 0")
            safeAddColumnIfNotExists(db, "checklist_items", "completedAt", "completedAt INTEGER NOT NULL DEFAULT 0")
            safeAddColumnIfNotExists(db, "checklist_items", "itemType", "itemType TEXT NOT NULL DEFAULT 'CHECKLIST'")
            safeAddColumnIfNotExists(db, "checklist_items", "subtasksJson", "subtasksJson TEXT NOT NULL DEFAULT ''")
            safeAddColumnIfNotExists(db, "checklist_items", "dailyComment", "dailyComment TEXT NOT NULL DEFAULT ''")
            safeAddColumnIfNotExists(db, "checklist_items", "dailyCommentDate", "dailyCommentDate INTEGER NOT NULL DEFAULT 0")
            safeAddColumnIfNotExists(db, "checklist_items", "dailyCommentHistoryJson", "dailyCommentHistoryJson TEXT NOT NULL DEFAULT ''")
            try {
                db.execSQL("CREATE INDEX IF NOT EXISTS index_checklist_items_studentId ON checklist_items(studentId)")
            } catch (_: Exception) { }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_2_3 = object : Migration(2, 3) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_3_4 = object : Migration(3, 4) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_4_5 = object : Migration(4, 5) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_5_6 = object : Migration(5, 6) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_6_7 = object : Migration(6, 7) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_7_8 = object : Migration(7, 8) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_8_9 = object : Migration(8, 9) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_9_10 = object : Migration(9, 10) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_10_11 = object : Migration(10, 11) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_11_12 = object : Migration(11, 12) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_12_13 = object : Migration(12, 13) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }

        // Direct jumps from any version to version 13
        private val MIGRATION_1_13 = object : Migration(1, 13) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_2_13 = object : Migration(2, 13) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_3_13 = object : Migration(3, 13) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_4_13 = object : Migration(4, 13) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_5_13 = object : Migration(5, 13) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_6_13 = object : Migration(6, 13) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_7_13 = object : Migration(7, 13) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_8_13 = object : Migration(8, 13) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_9_13 = object : Migration(9, 13) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_10_13 = object : Migration(10, 13) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_11_13 = object : Migration(11, 13) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }

        // Direct jumps from any version to version 12 (backwards compatibility)
        private val MIGRATION_1_12 = object : Migration(1, 12) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_2_12 = object : Migration(2, 12) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_3_12 = object : Migration(3, 12) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_4_12 = object : Migration(4, 12) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_5_12 = object : Migration(5, 12) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_6_12 = object : Migration(6, 12) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_7_12 = object : Migration(7, 12) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_8_12 = object : Migration(8, 12) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_9_12 = object : Migration(9, 12) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_10_12 = object : Migration(10, 12) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }

        // Direct jumps from any version to version 11 (backwards compatibility)
        private val MIGRATION_1_11 = object : Migration(1, 11) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_2_11 = object : Migration(2, 11) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_3_11 = object : Migration(3, 11) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_4_11 = object : Migration(4, 11) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_5_11 = object : Migration(5, 11) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_6_11 = object : Migration(6, 11) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_7_11 = object : Migration(7, 11) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_8_11 = object : Migration(8, 11) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }
        private val MIGRATION_9_11 = object : Migration(9, 11) { override fun migrate(db: SupportSQLiteDatabase) = ensureAllTablesAndIndices(db) }

        private fun makeSafetyBackup(context: Context) {
            try {
                val dbFile = context.getDatabasePath("attendance_database")
                if (dbFile.exists() && dbFile.length() > 0) {
                    val backupFile = File(context.filesDir, "attendance_database.safety_backup")
                    FileInputStream(dbFile).use { input ->
                        FileOutputStream(backupFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            } catch (_: Exception) { }
        }

        fun getDatabase(context: Context): AttendanceDatabase {
            return INSTANCE ?: synchronized(this) {
                makeSafetyBackup(context.applicationContext)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AttendanceDatabase::class.java,
                    "attendance_database"
                )
                .addMigrations(
                    MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13,
                    MIGRATION_1_13, MIGRATION_2_13, MIGRATION_3_13, MIGRATION_4_13, MIGRATION_5_13, MIGRATION_6_13, MIGRATION_7_13, MIGRATION_8_13, MIGRATION_9_13, MIGRATION_10_13, MIGRATION_11_13,
                    MIGRATION_1_12, MIGRATION_2_12, MIGRATION_3_12, MIGRATION_4_12, MIGRATION_5_12, MIGRATION_6_12, MIGRATION_7_12, MIGRATION_8_12, MIGRATION_9_12, MIGRATION_10_12,
                    MIGRATION_1_11, MIGRATION_2_11, MIGRATION_3_11, MIGRATION_4_11, MIGRATION_5_11, MIGRATION_6_11, MIGRATION_7_11, MIGRATION_8_11, MIGRATION_9_11
                )
                .fallbackToDestructiveMigrationOnDowngrade(false)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
