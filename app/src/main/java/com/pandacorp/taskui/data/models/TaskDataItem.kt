package com.pandacorp.taskui.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskDataItem(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "time") var time: Long?,
    @ColumnInfo(name = "priority") var priority: Int?,
    @ColumnInfo(name = "status") var status: Int
)