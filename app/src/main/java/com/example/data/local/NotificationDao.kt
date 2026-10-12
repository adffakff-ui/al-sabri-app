package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ParentNotification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
  @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
  fun getAllNotifications(): Flow<List<ParentNotification>>

  @Query("SELECT * FROM notifications WHERE studentId = :studentId ORDER BY timestamp DESC")
  fun getNotificationsByStudent(studentId: Long): Flow<List<ParentNotification>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: ParentNotification): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(notifications: List<ParentNotification>)

  @Delete
  suspend fun deleteNotification(notification: ParentNotification)

  @Query("DELETE FROM notifications")
  suspend fun deleteAll()
}
