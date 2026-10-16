package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class AttendanceRecord(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val studentId: Long,
  val studentName: String,
  val date: String,
  val status: String = "حاضر", // حاضر, غائب, مستأذن, متأخر
  val notes: String = "",
  val associationId: String = "HAIEL_SAEED",
  val timestamp: Long = System.currentTimeMillis()
)
