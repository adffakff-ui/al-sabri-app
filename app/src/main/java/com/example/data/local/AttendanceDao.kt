package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
  @Query("SELECT * FROM attendance ORDER BY timestamp DESC")
  fun getAllAttendance(): Flow<List<AttendanceRecord>>

  @Query("SELECT * FROM attendance WHERE date = :date ORDER BY studentName ASC")
  fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>>

  @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY timestamp DESC")
  fun getAttendanceByStudent(studentId: Long): Flow<List<AttendanceRecord>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAttendance(record: AttendanceRecord): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(records: List<AttendanceRecord>)

  @Delete
  suspend fun deleteAttendance(record: AttendanceRecord)

  @Query("DELETE FROM attendance WHERE studentId = :studentId AND date = :date")
  suspend fun deleteByStudentAndDate(studentId: Long, date: String)

  @Query("DELETE FROM attendance")
  suspend fun deleteAll()
}
