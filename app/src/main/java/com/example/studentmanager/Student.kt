package com.example.studentmanager

import android.content.ContentValues
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val contactNumber: String,
    val age: Int,
    val gender: String,
    val address: String
) {
    companion object {
        fun fromContentValues(values: ContentValues?): Student {
            if (values == null) {
                return Student(name = "", contactNumber = "", age = 0, gender = "", address = "")
            }
            return Student(
                id = values.getAsInteger("id") ?: 0,
                name = values.getAsString("name") ?: "",
                contactNumber = values.getAsString("contactNumber") ?: "",
                age = values.getAsInteger("age") ?: 0,
                gender = values.getAsString("gender") ?: "",
                address = values.getAsString("address") ?: ""
            )
        }
    }
}
