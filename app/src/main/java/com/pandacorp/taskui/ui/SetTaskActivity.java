package com.pandacorp.taskui.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pandacorp.taskui.DBHelper;
import com.pandacorp.taskui.R;
import com.pandacorp.taskui.ui.main_tasks.MainTasksFragment;

public class SetTaskActivity extends AppCompatActivity implements View.OnClickListener {
    private Button set_task_accept_btn;
    private EditText set_task_editText;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MySettings mySettings = new MySettings(this);
        mySettings.load();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_task);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        set_task_accept_btn = findViewById(R.id.set_task_accept_btn);
        set_task_accept_btn.setOnClickListener(this);
        set_task_editText = findViewById(R.id.set_task_editText);
        dbHelper = new DBHelper(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_task_accept_btn:

                if (String.valueOf(set_task_editText.getText()) != ""){
                    String task_text = String.valueOf(set_task_editText.getText());
                    Log.d("MyLogs", "onClick: task_text = " + task_text);
                    SQLiteDatabase database = dbHelper.getWritableDatabase();

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DBHelper.KEY_TASK_TEXT, task_text);

                    database.insert(DBHelper.MAIN_TASKS_TABLE_NAME, null, contentValues);
                    dbHelper.close();
                    setResult(RESULT_OK);

                    this.finish();

                }
            break;
        }
    }
}
