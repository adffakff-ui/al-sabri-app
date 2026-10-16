package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class ParentNotification(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val studentId: Long,
  val studentName: String,
  val parentPhone: String,
  val title: String,
  val message: String,
  val notificationType: String = "تقرير يومي", // تقرير يومي, تنبيه غياب, إنجاز وتفوق, إشعار عام
  val date: String,
  val isSent: Boolean = true,
  val associationId: String = "HAIEL_SAEED",
  val timestamp: Long = System.currentTimeMillis()
)
