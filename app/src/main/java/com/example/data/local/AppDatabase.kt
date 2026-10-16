package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AttendanceRecord
import com.example.data.model.ParentNotification
import com.example.data.model.RecitationRecord
import com.example.data.model.RecitationSchedule
import com.example.data.model.Student

@Database(
  entities = [
    Student::class,
    AttendanceRecord::class,
    RecitationRecord::class,
    ParentNotification::class,
    RecitationSchedule::class
  ],
  version = 5,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun studentDao(): StudentDao
  abstract fun attendanceDao(): AttendanceDao
  abstract fun recitationDao(): RecitationDao
  abstract fun notificationDao(): NotificationDao
  abstract fun scheduleDao(): ScheduleDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "akram_quran_db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
