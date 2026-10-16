package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val phone: String = "",
  val parentName: String = "",
  val parentPhone: String = "",
  val groupName: String = "حلقة عثمان بن عفان",
  val level: String = "متوسط",
  val currentSurah: String = "البقرة",
  val currentAyah: Int = 1,
  val memorizedJuzCount: Int = 3,
  val avatarColorHex: Long = 0xFF0D6850,
  val notes: String = "",
  val isGirlsSection: Boolean = false,
  val associationId: String = "HAIEL_SAEED",
  val createdAt: Long = System.currentTimeMillis()
)
