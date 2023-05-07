package com.pandacorp.taskui.data.database

import androidx.room.*
import com.pandacorp.taskui.data.models.TaskDataItem

@Dao
interface TasksDao {
    @Query("SELECT * FROM taskDataItem ORDER BY id DESC")
    fun getAll(): MutableList<TaskDataItem>
    
    @Insert
    fun insert(item: TaskDataItem): Long

    @Insert
    fun insertList(list: List<TaskDataItem>)

    @Delete
    fun remove(item: TaskDataItem)

    @Delete
    fun removeList(list: List<TaskDataItem>)

    @Query("DELETE FROM taskDataItem")
    fun removeAll()
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: TaskDataItem)
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateList(list: List<TaskDataItem>)
}
    