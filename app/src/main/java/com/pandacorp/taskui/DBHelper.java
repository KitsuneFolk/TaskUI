package com.pandacorp.taskui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MyDatabase";
    public static final String KEY_ID = "id";
    public static final String KEY_TASK_TEXT = "task_text";
    public static final String KEY_TASK_TIME = "task_time";
    public static final String KEY_TASK_PRIORITY = "task_priority";
    public static final String MAIN_TASKS_TABLE_NAME = "MainTasks";
    public static final String COMPLETED_TASKS_TABLE_NAME = "CompletedTasks";
    public static final String DELETED_TASKS_TABLE_NAME = "DeletedTasks";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + MAIN_TASKS_TABLE_NAME + "(" + KEY_ID
                + " integer primary key," + KEY_TASK_TEXT + " text, " + KEY_TASK_TIME + " time, " + KEY_TASK_PRIORITY + " priority)");
        db.execSQL("create table " + COMPLETED_TASKS_TABLE_NAME + "(" + KEY_ID
                + " integer primary key," + KEY_TASK_TEXT + " text, " + KEY_TASK_TIME + " time, " + KEY_TASK_PRIORITY + " priority)");
        db.execSQL("create table " + DELETED_TASKS_TABLE_NAME + "(" + KEY_ID
                + " integer primary key," + KEY_TASK_TEXT + " text, " + KEY_TASK_TIME + " time, " + KEY_TASK_PRIORITY + " priority)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + MAIN_TASKS_TABLE_NAME);
        db.execSQL("drop table if exists " + COMPLETED_TASKS_TABLE_NAME);
        db.execSQL("drop table if exists " + DELETED_TASKS_TABLE_NAME);

        onCreate(db);
    }
}
