package com.pandacorp.taskui.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pandacorp.taskui.data.models.TaskDataItem

@Database(
        entities = [TaskDataItem::class],
        version = 1,
        exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun tasksDao(): TasksDao
    
}