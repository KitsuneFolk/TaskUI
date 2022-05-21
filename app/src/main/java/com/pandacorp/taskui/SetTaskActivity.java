package com.pandacorp.taskui;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.allyants.notifyme.NotifyMe;
import com.pandacorp.taskui.ui.MySettings;
import com.pandacorp.taskui.ui.NotificationUtils;
import com.pandacorp.taskui.ui.main_tasks.PostponeActivity;
import com.pandacorp.taskui.ui.settings.SettingsActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Random;

public class SetTaskActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private String TAG = "MyLogs";

    private Button set_task_accept_btn;
    private Button set_time_btn;
    private EditText set_task_editText;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    Calendar now = Calendar.getInstance();

    //Need for understand program is the time chosed or no;
    boolean isTimeChoosed = false;

    //SQLite database objects.
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;

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

                    String task_text = String.valueOf(set_task_editText.getText());
                    Log.d("MyLogs", "onClick: task_text = " + task_text);

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DBHelper.KEY_TASK_TEXT, task_text);

                    database.insert(DBHelper.MAIN_TASKS_TABLE_NAME, null, contentValues);
                    String timeData = now.getTime().toString();
                    Log.d(TAG, "onClick: timeData = " + timeData);
                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put(DBHelper.KEY_TASK_TIME, timeData);
                    setNotification();
                    dbHelper.close();

                    setResult(RESULT_OK);

                    this.finish();


                break;
            case R.id.set_time_btn:
                datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
                break;
        }
    }
    private void setNotification(){
//        Intent done_intent = new Intent(getApplicationContext(), SettingsActivity.class);
//        NotifyMe notifyMe = new NotifyMe.Builder(getApplicationContext())
//                .title(getResources().getString(R.string.show_layout_task))
//                .content(set_task_editText.getText().toString())
//                .color(255, 0, 0, 255)
//                .led_color(0, 0, 255, 255)
//                .key("test")
//                .time(now)
//                .time(Calendar.getInstance())
//                .large_icon(R.drawable.ic_complete)
//                .rrule("FREQ=MINUTELY;INTERVAL=5;COUNT=2")
//                .build();
//
        long time = now.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        reminderNotification(time);

    }
    public void reminderNotification(long time)
    {
        NotificationUtils _notificationUtils = new NotificationUtils(this);
        long _currentTime = System.currentTimeMillis();
        long _triggerReminder = _currentTime + time; //triggers a reminder after 10 seconds.
        String title = getResources().getString(R.string.show_layout_task);
        String content = set_task_editText.getText().toString();
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
        now.set(Calendar.HOUR_OF_DAY, hourOfDay);
        now.set(Calendar.MINUTE, minute);
        now.set(Calendar.SECOND, 0);
        isTimeChoosed  = true;
    }
}
