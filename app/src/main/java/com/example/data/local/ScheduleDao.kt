package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RecitationSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
  @Query("SELECT * FROM recitation_schedules ORDER BY scheduledDate ASC, scheduledTime ASC")
  fun getAllSchedules(): Flow<List<RecitationSchedule>>

  @Query("SELECT * FROM recitation_schedules WHERE studentId = :studentId ORDER BY scheduledDate ASC, scheduledTime ASC")
  fun getSchedulesByStudent(studentId: Long): Flow<List<RecitationSchedule>>

  @Query("SELECT * FROM recitation_schedules WHERE scheduledDate = :date ORDER BY scheduledTime ASC")
  fun getSchedulesByDate(date: String): Flow<List<RecitationSchedule>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSchedule(schedule: RecitationSchedule): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(schedules: List<RecitationSchedule>)

  @Update
  suspend fun updateSchedule(schedule: RecitationSchedule)

  @Delete
  suspend fun deleteSchedule(schedule: RecitationSchedule)

  @Query("DELETE FROM recitation_schedules")
  suspend fun deleteAll()
}
