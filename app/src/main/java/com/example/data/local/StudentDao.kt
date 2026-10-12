package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
  @Query("SELECT * FROM students ORDER BY name ASC")
  fun getAllStudents(): Flow<List<Student>>

  @Query("SELECT * FROM students WHERE id = :id")
  suspend fun getStudentById(id: Long): Student?

  @Query("SELECT * FROM students WHERE groupName = :groupName ORDER BY name ASC")
  fun getStudentsByGroup(groupName: String): Flow<List<Student>>

  @Query("SELECT COUNT(*) FROM students")
  fun getStudentsCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStudent(student: Student): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(students: List<Student>)

  @Update
  suspend fun updateStudent(student: Student)

  @Delete
  suspend fun deleteStudent(student: Student)

  @Query("DELETE FROM students")
  suspend fun deleteAll()
}
