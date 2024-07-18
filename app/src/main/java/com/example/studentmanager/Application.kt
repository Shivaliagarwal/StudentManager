package com.example.studentmanager


import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.room.Room

class MyApplication : Application() {
    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        // Initialize the database
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
    }

}
