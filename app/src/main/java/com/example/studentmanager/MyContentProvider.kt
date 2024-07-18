package com.example.studentmanager

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import kotlinx.coroutines.runBlocking

class StudentContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.studentmanager.provider"
        val URI_STUDENT: Uri = Uri.parse("content://$AUTHORITY/student")

        const val STUDENT = 1
        const val STUDENT_ID = 2

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "student", STUDENT)
            addURI(AUTHORITY, "student/#", STUDENT_ID)
        }
    }

    private lateinit var db: AppDatabase

    override fun onCreate(): Boolean {
        db = Room.databaseBuilder(
            context!!,
            AppDatabase::class.java, "database-name"
        ).build()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            STUDENT -> db.studentdao().getStudentsCursor()
            STUDENT_ID -> {
                val id = ContentUris.parseId(uri)
                db.studentdao().getStudentByIdCursor(id)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            STUDENT -> "vnd.android.cursor.dir/$AUTHORITY.student"
            STUDENT_ID -> "vnd.android.cursor.item/$AUTHORITY.student"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return when (uriMatcher.match(uri)) {
            STUDENT -> {
                val id = runBlocking {
                    db.studentdao().Insert(Student.fromContentValues(values))
                }
                context?.contentResolver?.notifyChange(uri, null)
                ContentUris.withAppendedId(uri, id)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return when (uriMatcher.match(uri)) {
            STUDENT -> {
                val rowsDeleted = runBlocking {
                    db.studentdao().deleteAll()
                }
                context?.contentResolver?.notifyChange(uri, null)
                rowsDeleted
            }
            STUDENT_ID -> {
                val id = ContentUris.parseId(uri)
                val rowsDeleted = runBlocking {
                    db.studentdao().deleteById(id)
                }
                context?.contentResolver?.notifyChange(uri, null)
                rowsDeleted
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return when (uriMatcher.match(uri)) {
            STUDENT_ID -> {
                val id = ContentUris.parseId(uri).toInt()
                val student = Student.fromContentValues(values).copy(id = id)
                val rowsUpdated = runBlocking {
                    db.studentdao().update(student)
                }
                context?.contentResolver?.notifyChange(uri, null)
                rowsUpdated
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}
