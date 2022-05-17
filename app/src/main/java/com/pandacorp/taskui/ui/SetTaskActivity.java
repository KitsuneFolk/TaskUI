package com.pandacorp.taskui.ui;

import android.content.ContentValues;
import android.content.Intent;
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

import com.allyants.notifyme.NotifyMe;
import com.pandacorp.taskui.DBHelper;
import com.pandacorp.taskui.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class SetTaskActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Button set_task_accept_btn;
    private Button set_time_btn;
    private EditText set_task_editText;
    private DBHelper dbHelper;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    Calendar now = Calendar.getInstance();

    //Need for understand program is the time chosed or no;
    boolean isTimeChoosed = false;

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
                now.get(Calendar.MINUTE) + 10,
                true
        );

        dbHelper = new DBHelper(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_task_accept_btn:

                if (String.valueOf(set_task_editText.getText()) != "") {
                    String task_text = String.valueOf(set_task_editText.getText());
                    Log.d("MyLogs", "onClick: task_text = " + task_text);
                    SQLiteDatabase database = dbHelper.getWritableDatabase();

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DBHelper.KEY_TASK_TEXT, task_text);

                    database.insert(DBHelper.MAIN_TASKS_TABLE_NAME, null, contentValues);
                    dbHelper.close();
                    setResult(RESULT_OK);

                    setNotification();

                    this.finish();

                }
                break;
            case R.id.set_time_btn:
                datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
                break;
        }
    }
    private void setNotification(){
        Intent intent = new Intent(getApplicationContext(), SetTaskActivity.class);
        NotifyMe notifyMe = new NotifyMe.Builder(getApplicationContext())
                .content(R.string.show_layout_task)
                .title(set_task_editText.getText().toString())
                .color(255, 0, 0, 255)
                .led_color(0, 0, 255, 255)
                .key("test")
                .time(now)
                .addAction(new Intent(), getResources().getString(R.string.postpone))
                .addAction(intent, getResources().getString(R.string.done))
                .large_icon(R.mipmap.ic_launcher_round)
                .rrule("FREQ=MINUTELY;INTERVAL=5;COUNT=2")
                .build();
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

    }
}
