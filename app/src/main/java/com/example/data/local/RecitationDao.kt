package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.RecitationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface RecitationDao {
  @Query("SELECT * FROM recitations ORDER BY timestamp DESC")
  fun getAllRecitations(): Flow<List<RecitationRecord>>

  @Query("SELECT * FROM recitations WHERE studentId = :studentId ORDER BY timestamp DESC")
  fun getRecitationsByStudent(studentId: Long): Flow<List<RecitationRecord>>

  @Query("SELECT * FROM recitations WHERE date = :date ORDER BY timestamp DESC")
  fun getRecitationsByDate(date: String): Flow<List<RecitationRecord>>

  @Query("SELECT COUNT(*) FROM recitations")
  fun getRecitationsCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecitation(record: RecitationRecord): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(records: List<RecitationRecord>)

  @Delete
  suspend fun deleteRecitation(record: RecitationRecord)

  @Query("DELETE FROM recitations")
  suspend fun deleteAll()
}
