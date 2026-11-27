package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AttendanceRepository(
    private val classDao: ClassDao? = null,
    private val studentDao: StudentDao? = null,
    private val recordDao: AttendanceRecordDao? = null,
    private val shiftDao: ShiftDao? = null,
    private val hisabProductDao: HisabProductDao? = null,
    private val hisabLedgerEntryDao: HisabLedgerEntryDao? = null,
    private val checklistItemDao: ChecklistItemDao? = null
) {
    val allClasses: Flow<List<ClassEntity>> = classDao?.getAllClasses() ?: flowOf(emptyList())
    val allShifts: Flow<List<ShiftEntity>> = shiftDao?.getAllShifts() ?: flowOf(emptyList())
    val allStudents: Flow<List<StudentEntity>> = studentDao?.getAllStudents() ?: flowOf(emptyList())
    val allHisabProducts: Flow<List<HisabProductEntity>> = hisabProductDao?.getAllProducts() ?: flowOf(emptyList())
    val allHisabLedgerEntries: Flow<List<HisabLedgerEntryEntity>> = hisabLedgerEntryDao?.getAllEntries() ?: flowOf(emptyList())
    val allChecklistItems: Flow<List<ChecklistItemEntity>> = checklistItemDao?.getAllItems() ?: flowOf(emptyList())

    // Classes
    suspend fun getAllClassesList(): List<ClassEntity> = classDao?.getAllClassesList() ?: emptyList()
    suspend fun getClassById(classId: Long): ClassEntity? = classDao?.getClassById(classId)
    suspend fun insertClass(classEntity: ClassEntity): Long = classDao?.insertClass(classEntity) ?: -1L
    suspend fun insertClasses(classes: List<ClassEntity>) = classDao?.insertClasses(classes) ?: Unit
    suspend fun updateClass(classEntity: ClassEntity) = classDao?.updateClass(classEntity) ?: Unit
    suspend fun updateClasses(classes: List<ClassEntity>) = classDao?.updateClasses(classes) ?: Unit
    suspend fun deleteClass(classEntity: ClassEntity) = classDao?.deleteClass(classEntity) ?: Unit

    // Students
    suspend fun getAllStudentsList(): List<StudentEntity> = studentDao?.getAllStudentsList() ?: emptyList()
    fun getStudentsByClass(classId: Long): Flow<List<StudentEntity>> = studentDao?.getStudentsByClass(classId) ?: flowOf(emptyList())
    suspend fun getStudentsByClassList(classId: Long): List<StudentEntity> = studentDao?.getStudentsByClassList(classId) ?: emptyList()
    fun getStudentCountByClass(classId: Long): Flow<Int> = studentDao?.getStudentCountByClass(classId) ?: flowOf(0)
    suspend fun insertStudent(studentEntity: StudentEntity): Long = studentDao?.insertStudent(studentEntity) ?: -1L
    suspend fun updateStudent(studentEntity: StudentEntity) = studentDao?.updateStudent(studentEntity) ?: Unit
    suspend fun updateStudents(students: List<StudentEntity>) = studentDao?.updateStudents(students) ?: Unit
    suspend fun deleteStudent(studentEntity: StudentEntity) = studentDao?.deleteStudent(studentEntity) ?: Unit

    // Shifts
    suspend fun getAllShiftsList(): List<ShiftEntity> = shiftDao?.getAllShiftsList() ?: emptyList()
    suspend fun getShiftById(shiftId: Long): ShiftEntity? = shiftDao?.getShiftById(shiftId)
    suspend fun insertShift(shift: ShiftEntity): Long = shiftDao?.insertShift(shift) ?: -1L
    suspend fun updateShift(shift: ShiftEntity) = shiftDao?.updateShift(shift) ?: Unit
    suspend fun deleteShift(shift: ShiftEntity) = shiftDao?.deleteShift(shift) ?: Unit

    // Attendance Records
    fun getRecordsByDate(classId: Long, dateString: String): Flow<List<AttendanceRecordEntity>> =
        recordDao?.getRecordsByDate(classId, dateString) ?: flowOf(emptyList())
    suspend fun getRecordsByDateList(classId: Long, dateString: String): List<AttendanceRecordEntity> =
        recordDao?.getRecordsByDateList(classId, dateString) ?: emptyList()
    fun getAllRecordsByClass(classId: Long): Flow<List<AttendanceRecordEntity>> =
        recordDao?.getAllRecordsByClass(classId) ?: flowOf(emptyList())
    suspend fun getAllRecordsList(): List<AttendanceRecordEntity> =
        recordDao?.getAllRecordsList() ?: emptyList()
    fun getRecordsByStudent(studentId: Long): Flow<List<AttendanceRecordEntity>> =
        recordDao?.getRecordsByStudent(studentId) ?: flowOf(emptyList())
    suspend fun insertRecord(record: AttendanceRecordEntity): Long =
        recordDao?.insertRecord(record) ?: -1L
    suspend fun insertRecords(records: List<AttendanceRecordEntity>) =
        recordDao?.insertRecords(records) ?: Unit
    suspend fun deleteRecordsByDate(classId: Long, dateString: String) =
        recordDao?.deleteRecordsByDate(classId, dateString) ?: Unit

    // Hisab Products
    suspend fun getAllHisabProductsList(): List<HisabProductEntity> =
        hisabProductDao?.getAllProductsList() ?: emptyList()
    suspend fun insertHisabProduct(product: HisabProductEntity): Long =
        hisabProductDao?.insertProduct(product) ?: -1L
    suspend fun insertHisabProducts(products: List<HisabProductEntity>) =
        hisabProductDao?.insertProducts(products) ?: Unit
    suspend fun updateHisabProduct(product: HisabProductEntity) =
        hisabProductDao?.updateProduct(product) ?: Unit
    suspend fun deleteHisabProduct(product: HisabProductEntity) =
        hisabProductDao?.deleteProduct(product) ?: Unit

    // Hisab Ledger Entries
    suspend fun getAllHisabEntriesList(): List<HisabLedgerEntryEntity> =
        hisabLedgerEntryDao?.getAllEntriesList() ?: emptyList()
    fun getHisabEntriesByStudent(studentId: Long): Flow<List<HisabLedgerEntryEntity>> =
        hisabLedgerEntryDao?.getEntriesByStudent(studentId) ?: flowOf(emptyList())
    suspend fun insertHisabEntry(entry: HisabLedgerEntryEntity): Long =
        hisabLedgerEntryDao?.insertEntry(entry) ?: -1L
    suspend fun insertHisabEntries(entries: List<HisabLedgerEntryEntity>) =
        hisabLedgerEntryDao?.insertEntries(entries) ?: Unit
    suspend fun updateHisabEntry(entry: HisabLedgerEntryEntity) =
        hisabLedgerEntryDao?.updateEntry(entry) ?: Unit
    suspend fun deleteHisabEntry(entry: HisabLedgerEntryEntity) =
        hisabLedgerEntryDao?.deleteEntry(entry) ?: Unit

    // Checklist Items
    suspend fun getAllChecklistItemsList(): List<ChecklistItemEntity> =
        checklistItemDao?.getAllItemsList() ?: emptyList()
    suspend fun insertChecklistItem(item: ChecklistItemEntity): Long =
        checklistItemDao?.insertItem(item) ?: -1L
    suspend fun insertChecklistItems(items: List<ChecklistItemEntity>) =
        checklistItemDao?.insertItems(items) ?: Unit
    suspend fun updateChecklistItem(item: ChecklistItemEntity) =
        checklistItemDao?.updateItem(item) ?: Unit
    suspend fun updateChecklistItems(items: List<ChecklistItemEntity>) =
        checklistItemDao?.updateItems(items) ?: Unit
    suspend fun updateChecklistDisplayOrder(id: Long, order: Int) =
        checklistItemDao?.updateDisplayOrder(id, order) ?: Unit
    suspend fun deleteChecklistItem(item: ChecklistItemEntity) =
        checklistItemDao?.deleteItem(item) ?: Unit
    suspend fun deleteAllChecklistItems() =
        checklistItemDao?.deleteAllItems() ?: Unit
    suspend fun deleteChecklistForStudent(studentId: Long) =
        checklistItemDao?.deleteItemsForStudent(studentId) ?: Unit
    suspend fun updateChecklistCompletion(id: Long, isCompleted: Boolean, completedAt: Long) =
        checklistItemDao?.updateCompletion(id, isCompleted, completedAt) ?: Unit
    suspend fun updateChecklistReminder(id: Long, reminderTimestamp: Long) =
        checklistItemDao?.updateReminder(id, reminderTimestamp) ?: Unit
    suspend fun resetAllChecklistCompletions() =
        checklistItemDao?.resetAllCompletions() ?: Unit
    suspend fun resetChecklistForStudent(studentId: Long) =
        checklistItemDao?.resetCompletionsForStudent(studentId) ?: Unit
}
