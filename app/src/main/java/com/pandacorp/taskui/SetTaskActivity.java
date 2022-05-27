package com.pandacorp.taskui;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pandacorp.taskui.ui.MySettings;
import com.pandacorp.taskui.ui.NotificationUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class SetTaskActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private String TAG = "MyLogs";

    private Button set_task_accept_btn;
    private Button set_time_btn;
    private EditText set_task_editText;
    private RadioGroup set_priority_radio_group;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    Calendar now = Calendar.getInstance();

    //SQLite database objects.
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;

    private boolean isTimeSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MySettings mySettings = new MySettings(this);
        mySettings.load();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_task);
        Toolbar toolbar = findViewById(R.id.set_task_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        set_task_accept_btn = findViewById(R.id.set_task_accept_btn);
        set_task_accept_btn.setOnClickListener(this);
        set_time_btn = findViewById(R.id.set_time_btn);
        set_time_btn.setOnClickListener(this);
        set_task_editText = findViewById(R.id.set_task_editText);

        set_priority_radio_group = findViewById(R.id.set_priority_radio_group);

        datePickerDialog = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        timePickerDialog = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.MAIN_TASKS_TABLE_NAME, null, null, null, null, null, null);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_task_accept_btn:

                if (set_task_editText.getText().length() != 0) {
                    setTask();
                    Log.d(TAG, "onClick: set_task_editText.getText() = " + set_task_editText.getText());
                }
                break;
            case R.id.set_time_btn:
                datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
                break;
        }
    }

    private void setTask() {
        String task_text;
        String task_time;
        String task_priority;


        task_text = String.valueOf(set_task_editText.getText());
        //Needed for set time from 10:0 to 10:00
        task_time = String.format("%02d:%02d", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
        
        switch (set_priority_radio_group.getCheckedRadioButtonId()) {
            case R.id.white_radio_button:
                task_priority = "white";
                break;
            case R.id.orange_radio_button:
                task_priority = "yellow";
                break;
            case R.id.red_radio_button:
                task_priority = "red";
                break;
            default:
                throw new IllegalStateException("SetTaskActivity.settask: Unexpected value: " + set_priority_radio_group.getCheckedRadioButtonId());
        }
        
        Log.d(TAG, "onClick: task_time = " + task_time);
        Log.d("MyLogs", "onClick: task_text = " + task_text);
        Log.d(TAG, "onClick: task_prioriy = " + task_time);

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_TASK_TEXT, task_text);
        contentValues.put(DBHelper.KEY_TASK_PRIORITY, task_priority);
        if (isTimeSet ){
            contentValues.put(DBHelper.KEY_TASK_TIME, task_time);
            setNotification();

        }

        database.insert(DBHelper.MAIN_TASKS_TABLE_NAME, null, contentValues);

        dbHelper.close();

        setResult(RESULT_OK);

        this.finish();
    }

    private void setNotification() {
        long time = now.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        long _currentTime = System.currentTimeMillis();
        long _triggerReminder = _currentTime + time;
        String title = getResources().getString(R.string.show_layout_task);
        String content = set_task_editText.getText().toString();

        NotificationUtils _notificationUtils = new NotificationUtils(this);
        _notificationUtils.setNotification(title, content);
        _notificationUtils.setReminder(_triggerReminder);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //Метод обработки нажатия на кнопку home.
                this.finish();
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        now.set(Calendar.YEAR, year);
        now.set(Calendar.MONTH, monthOfYear);
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        Log.d(TAG, "onTimeSet: hourOfDay, minute = " + hourOfDay + ", " + minute);
        now.set(Calendar.HOUR_OF_DAY, hourOfDay);
        now.set(Calendar.MINUTE, minute);
        now.set(Calendar.SECOND, 0);

        String task_time = String.format("%02d:%02d", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
        set_time_btn.setText(task_time);
        isTimeSet = true;

    }
}
