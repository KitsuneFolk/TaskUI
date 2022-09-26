package com.pandacorp.taskui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.preference.PreferenceManager;

import com.pandacorp.taskui.Adapter.ListItem;
import com.pandacorp.taskui.Notifications.NotificationUtils;
import com.pandacorp.taskui.Widget.WidgetProvider;
import com.pandacorp.taskui.ui.settings.MySettings;

import java.util.Calendar;

public class SetTaskActivity extends AppCompatActivity implements View.OnClickListener {
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

    private boolean isTimeSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MySettings mySettings = new MySettings(this);
        mySettings.load();
        setContentView(R.layout.activity_set_task);
        initViews();

    }
    private void initViews(){
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

        initDialogPickers();

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

    }
    private void initDialogPickers(){
        String pickerDialogThemeSP = PreferenceManager.getDefaultSharedPreferences(this).getString("Themes", MySettings.Theme_Blue);

        int pickerDialogTheme;
        switch (pickerDialogThemeSP){
            case MySettings.Theme_Blue:
                pickerDialogTheme = R.style.BlueTheme_TimePicker;
                break;
            case MySettings.Theme_Dark:
                pickerDialogTheme = R.style.DarkTheme_TimePicker;
                break;
            case MySettings.Theme_Red:
                pickerDialogTheme = R.style.RedTheme_TimePicker;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + pickerDialogThemeSP);
        }
        DatePickerDialog.OnDateSetListener datePickerDialogListener = (view, year, month, dayOfMonth) -> {
            now.set(Calendar.YEAR, year);
            now.set(Calendar.MONTH, month);
            now.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            timePickerDialog.show();
        };
        TimePickerDialog.OnTimeSetListener timePickerDialogListener = (view, hourOfDay, minute) -> {
            now.set(Calendar.HOUR_OF_DAY, hourOfDay);
            now.set(Calendar.MINUTE, minute);
            now.set(Calendar.SECOND, 0);

            String task_time = String.format("%02d:%02d", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
            set_time_btn.setText(task_time);
            isTimeSet = true;
        };
        datePickerDialog = new DatePickerDialog(this,
                pickerDialogTheme,
                datePickerDialogListener,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        timePickerDialog = new TimePickerDialog(
                this,
                pickerDialogTheme,
                timePickerDialogListener,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_task_accept_btn:

                if (set_task_editText.getText().length() != 0) {
                    setTask();
                    updateWidgetOnItemAdded();

                }
                break;
            case R.id.set_time_btn:
                datePickerDialog.show();
                break;
        }
    }

    private void setTask() {
        String task_priority;


        String task_text = String.valueOf(set_task_editText.getText());
        //Needed for set time from 10:0 to 10:00
        String task_time = String.format("%02d:%02d", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));

        switch (set_priority_radio_group.getCheckedRadioButtonId()) {
            case R.id.white_radio_button:
                task_priority = ListItem.white;
                break;
            case R.id.orange_radio_button:
                task_priority = ListItem.yellow;
                break;
            case R.id.red_radio_button:
                task_priority = ListItem.red;
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
        if (isTimeSet) {
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

        int notification_id = now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) + now.get(Calendar.SECOND);
        SharedPreferences sp = getSharedPreferences("notifications_id", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(set_task_editText.getText().toString(), notification_id);

        editor.apply();

        NotificationUtils _notificationUtils = new NotificationUtils(this);
        _notificationUtils.setNotification(title, content);
        _notificationUtils.setReminder(_triggerReminder);


    }

    private void updateWidgetOnItemAdded() {
        WidgetProvider.Companion.sendRefreshBroadcast(this);

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

}
