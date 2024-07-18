package com.example.studentmanager

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Upsert
    suspend fun Insert(student: Student): Long

    @Delete
    suspend fun Delete(student: Student)

    @Update
    suspend fun update(student: Student): Int

    @Query("DELETE FROM Student WHERE id = :studentId")
    suspend fun deleteById(studentId: Long): Int

    @Query("DELETE FROM Student")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM Student ORDER BY name ASC")
    fun getOrderedByName(): Flow<List<Student>>

    @Query("SELECT * FROM Student WHERE id = :studentId")
    fun getStudentById(studentId: Int): Flow<Student?>

    @Query("SELECT * FROM Student ORDER BY name ASC")
    fun getStudentsCursor(): Cursor

    @Query("SELECT * FROM Student WHERE id = :studentId")
    fun getStudentByIdCursor(studentId: Long): Cursor
}
