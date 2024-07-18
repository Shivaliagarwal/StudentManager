package com.example.studentmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentmanager.AppDatabase
import com.example.studentmanager.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

class StudentViewModel(private val db: AppDatabase) : ViewModel() {

    private val studentDao = db.studentdao()

    fun getStudents(): Flow<List<Student>> {
        return studentDao.getOrderedByName()
    }

    fun insertStudent(student: Student) {
        viewModelScope.launch {
            studentDao.Insert(student)
        }
    }
    fun deleteStudent(student: Student){
        viewModelScope.launch {
            studentDao.Delete(student)
        }
    }
    fun updateStudent(student: Student) {
        viewModelScope.launch {
            studentDao.Insert(student)
        }
    }


}

