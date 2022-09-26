package com.pandacorp.taskui

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.pandacorp.taskui.Adapter.ListItem


class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val TAG = "MyLogs"
    override fun onCreate(db: SQLiteDatabase) {
        val create_main_tasks_table = ("create table " + MAIN_TASKS_TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TASK_TEXT + " STRING, "
                + KEY_TASK_TIME + " STRING, "
                + KEY_TASK_PRIORITY + " STRING " +
                " )")
        val create_completed_tasks_table = ("create table " + COMPLETED_TASKS_TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TASK_TEXT + " STRING, "
                + KEY_TASK_TIME + " STRING, "
                + KEY_TASK_PRIORITY + " STRING " +
                " )")
        val create_deleted_tasks_table = ("create table " + DELETED_TASKS_TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TASK_TEXT + " STRING,"
                + KEY_TASK_TIME + " STRING,"
                + KEY_TASK_PRIORITY + " STRING " + ")")
        
        db.execSQL(create_main_tasks_table)
        db.execSQL(create_completed_tasks_table)
        db.execSQL(create_deleted_tasks_table)
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists " + MAIN_TASKS_TABLE_NAME)
        db.execSQL("drop table if exists " + COMPLETED_TASKS_TABLE_NAME)
        db.execSQL("drop table if exists " + DELETED_TASKS_TABLE_NAME)
        onCreate(db)
    }
    
    fun getCursor(TABLE_NAME: String): Cursor =  writableDatabase.query(TABLE_NAME, null, null, null, null, null, null);
    
    fun getDatabaseItemIdByRecyclerViewItemId(table: String, id: Int): Int {
        //This method by a recyclerview item id returns id of the item in database
        val cursor = getCursor(table)
        //var of Id number in Timer_Table to understand how much there elements is
        var numberOfIds = 0
        //var of position of the deleted item
        var deletedPosition: Int?
        
        if (cursor.moveToFirst()) {
            // Log.d(TAG, "getDatabaseItemIdByRecyclerViewItemId: cursor!!.moveToFirst")
            val ID_COL = cursor.getColumnIndex(KEY_ID)
            // Log.d(TAG, "getDatabaseItemIdByRecyclerViewItemId: ID_COL = " + ID_COL)
            do {
                // Log.d(TAG, "getDatabaseItemIdByRecyclerViewItemId: do while()")
                numberOfIds++
                if (numberOfIds == id + 1) {
                    
                    deletedPosition = cursor.getInt(ID_COL)
                    // Log.d(TAG, "getDatabaseItemIdByRecyclerViewItemId: deletedPosition = " + deletedPosition)
    
                    if (deletedPosition == null) {
                        throw Exception("deletedPosition cannot be null!")
                    }
                    // Log.d(TAG, "getDatabaseItemIdByRecyclerViewItemId: returned " + deletedPosition)
                    return deletedPosition
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        Log.d(TAG, "getDatabaseItemIdByRecyclerViewItemId: numberOfIds = " + numberOfIds)
        Log.d(TAG, "getDatabaseItemIdByRecyclerViewItemId: deletedPosition = null")
        throw Exception("getDatabaseItemIdByRecyclerViewItemId method returns null!")
        
        
    }
    fun removeById(table: String, id: Int) {
        
        val db = this.writableDatabase
        val deletedPosition = getDatabaseItemIdByRecyclerViewItemId(table, id)
        Log.d(TAG, "removeById: id = $id, deletedPosition = $deletedPosition")
        
        db.delete(table, "$KEY_ID=?", arrayOf("$deletedPosition"))
        
        
    }
    
    fun add(table: String, timerListItem: ListItem) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(KEY_TASK_TEXT, timerListItem.mainText)
        cv.put(KEY_TASK_TIME, timerListItem.time)
        cv.put(KEY_TASK_PRIORITY, timerListItem.priority)
        db.insert(table, null, cv)
    }
    
    companion object {
        const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "MyDatabase"
        const val KEY_ID = "id"
        const val KEY_TASK_TEXT = "task_text"
        const val KEY_TASK_TIME = "task_time"
        const val KEY_TASK_PRIORITY = "task_priority"
        const val MAIN_TASKS_TABLE_NAME = "MainTasks"
        const val COMPLETED_TASKS_TABLE_NAME = "CompletedTasks"
        const val DELETED_TASKS_TABLE_NAME = "DeletedTasks"
        
    }
}
