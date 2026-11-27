package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassDao {
    @Query("SELECT * FROM classes ORDER BY displayOrder ASC, id ASC")
    fun getAllClasses(): Flow<List<ClassEntity>>

    @Query("SELECT * FROM classes ORDER BY displayOrder ASC, id ASC")
    suspend fun getAllClassesList(): List<ClassEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classEntity: ClassEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasses(classes: List<ClassEntity>)

    @Update
    suspend fun updateClasses(classes: List<ClassEntity>)

    @Update
    suspend fun updateClass(classEntity: ClassEntity)

    @Delete
    suspend fun deleteClass(classEntity: ClassEntity)

    @Query("DELETE FROM classes")
    suspend fun deleteAllClasses()

    @Query("SELECT * FROM classes WHERE id = :classId LIMIT 1")
    suspend fun getClassById(classId: Long): ClassEntity?
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY rollNumber ASC, name ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY rollNumber ASC, name ASC")
    fun getStudentsByClass(classId: Long): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY rollNumber ASC, name ASC")
    suspend fun getStudentsByClassList(classId: Long): List<StudentEntity>

    @Query("SELECT * FROM students ORDER BY id ASC")
    suspend fun getAllStudentsList(): List<StudentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(studentEntity: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(studentEntity: StudentEntity)

    @Update
    suspend fun updateStudents(students: List<StudentEntity>)

    @Delete
    suspend fun deleteStudent(studentEntity: StudentEntity)

    @Query("DELETE FROM students")
    suspend fun deleteAllStudents()

    @Query("SELECT COUNT(*) FROM students WHERE classId = :classId")
    fun getStudentCountByClass(classId: Long): Flow<Int>
}

@Dao
interface AttendanceRecordDao {
    @Query("SELECT * FROM attendance_records WHERE classId = :classId AND dateString = :dateString")
    fun getRecordsByDate(classId: Long, dateString: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records WHERE classId = :classId AND dateString = :dateString")
    suspend fun getRecordsByDateList(classId: Long, dateString: String): List<AttendanceRecordEntity>

    @Query("SELECT * FROM attendance_records WHERE classId = :classId")
    fun getAllRecordsByClass(classId: Long): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records ORDER BY dateString ASC, id ASC")
    suspend fun getAllRecordsList(): List<AttendanceRecordEntity>

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId")
    fun getRecordsByStudent(studentId: Long): Flow<List<AttendanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AttendanceRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<AttendanceRecordEntity>)

    @Query("DELETE FROM attendance_records WHERE classId = :classId AND dateString = :dateString")
    suspend fun deleteRecordsByDate(classId: Long, dateString: String)

    @Query("DELETE FROM attendance_records")
    suspend fun deleteAllRecords()
}

@Dao
interface ShiftDao {
    @Query("SELECT * FROM shifts ORDER BY name ASC")
    fun getAllShifts(): Flow<List<ShiftEntity>>

    @Query("SELECT * FROM shifts ORDER BY id ASC")
    suspend fun getAllShiftsList(): List<ShiftEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: ShiftEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShifts(shifts: List<ShiftEntity>)

    @Update
    suspend fun updateShift(shift: ShiftEntity)

    @Delete
    suspend fun deleteShift(shift: ShiftEntity)

    @Query("DELETE FROM shifts")
    suspend fun deleteAllShifts()

    @Query("SELECT * FROM shifts WHERE id = :shiftId LIMIT 1")
    suspend fun getShiftById(shiftId: Long): ShiftEntity?
}

@Dao
interface HisabProductDao {
    @Query("SELECT * FROM hisab_products ORDER BY displayOrder ASC, id ASC")
    fun getAllProducts(): Flow<List<HisabProductEntity>>

    @Query("SELECT * FROM hisab_products ORDER BY displayOrder ASC, id ASC")
    suspend fun getAllProductsList(): List<HisabProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: HisabProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<HisabProductEntity>)

    @Update
    suspend fun updateProduct(product: HisabProductEntity)

    @Delete
    suspend fun deleteProduct(product: HisabProductEntity)

    @Query("DELETE FROM hisab_products")
    suspend fun deleteAllProducts()
}

@Dao
interface HisabLedgerEntryDao {
    @Query("SELECT * FROM hisab_ledger_entries WHERE studentId = :studentId ORDER BY dateString DESC, timestamp DESC")
    fun getEntriesByStudent(studentId: Long): Flow<List<HisabLedgerEntryEntity>>

    @Query("SELECT * FROM hisab_ledger_entries ORDER BY dateString DESC, timestamp DESC")
    fun getAllEntries(): Flow<List<HisabLedgerEntryEntity>>

    @Query("SELECT * FROM hisab_ledger_entries ORDER BY dateString DESC, timestamp DESC")
    suspend fun getAllEntriesList(): List<HisabLedgerEntryEntity>

    @Query("SELECT * FROM hisab_ledger_entries WHERE studentId = :studentId ORDER BY dateString DESC, timestamp DESC")
    suspend fun getEntriesByStudentList(studentId: Long): List<HisabLedgerEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: HisabLedgerEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<HisabLedgerEntryEntity>)

    @Update
    suspend fun updateEntry(entry: HisabLedgerEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: HisabLedgerEntryEntity)

    @Query("DELETE FROM hisab_ledger_entries WHERE studentId = :studentId")
    suspend fun deleteEntriesByStudent(studentId: Long)

    @Query("DELETE FROM hisab_ledger_entries")
    suspend fun deleteAllEntries()
}

@Dao
interface ChecklistItemDao {
    @Query("SELECT * FROM checklist_items ORDER BY displayOrder ASC, id ASC")
    fun getAllItems(): Flow<List<ChecklistItemEntity>>

    @Query("SELECT * FROM checklist_items ORDER BY displayOrder ASC, id ASC")
    suspend fun getAllItemsList(): List<ChecklistItemEntity>

    @Query("SELECT * FROM checklist_items WHERE studentId = :studentId ORDER BY displayOrder ASC, id ASC")
    fun getItemsForStudent(studentId: Long): Flow<List<ChecklistItemEntity>>

    @Query("SELECT * FROM checklist_items WHERE studentId = 0 ORDER BY displayOrder ASC, id ASC")
    fun getGeneralShopItems(): Flow<List<ChecklistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ChecklistItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ChecklistItemEntity>)

    @Update
    suspend fun updateItem(item: ChecklistItemEntity)

    @Update
    suspend fun updateItems(items: List<ChecklistItemEntity>)

    @Query("UPDATE checklist_items SET displayOrder = :order WHERE id = :id")
    suspend fun updateDisplayOrder(id: Long, order: Int)

    @Delete
    suspend fun deleteItem(item: ChecklistItemEntity)

    @Query("DELETE FROM checklist_items")
    suspend fun deleteAllItems()

    @Query("DELETE FROM checklist_items WHERE studentId = :studentId")
    suspend fun deleteItemsForStudent(studentId: Long)

    @Query("UPDATE checklist_items SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :id")
    suspend fun updateCompletion(id: Long, isCompleted: Boolean, completedAt: Long)

    @Query("UPDATE checklist_items SET reminderTimestamp = :reminderTimestamp WHERE id = :id")
    suspend fun updateReminder(id: Long, reminderTimestamp: Long)

    @Query("SELECT * FROM checklist_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): ChecklistItemEntity?

    @Query("UPDATE checklist_items SET isCompleted = 0, completedAt = 0")
    suspend fun resetAllCompletions()

    @Query("UPDATE checklist_items SET isCompleted = 0, completedAt = 0 WHERE studentId = :studentId")
    suspend fun resetCompletionsForStudent(studentId: Long)
}

