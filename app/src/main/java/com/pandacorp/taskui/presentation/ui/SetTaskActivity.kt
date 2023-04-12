package com.pandacorp.taskui.presentation.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.ActivitySetTaskBinding
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.presentation.ui.widget.WidgetProvider
import com.pandacorp.taskui.presentation.utils.Constans
import com.pandacorp.taskui.presentation.utils.PreferenceHandler
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SetTaskActivity : AppCompatActivity() {
    companion object {
        const val TAG = "SetTaskActivity"
    }
    
    private var _binding: ActivitySetTaskBinding? = null
    private val binding get() = _binding!!
    
    private val now = Calendar.getInstance()
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var datePickerDialog: DatePickerDialog
    private var isTimeSet = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySetTaskBinding.inflate(layoutInflater)
        PreferenceHandler(this).load()
        setContentView(binding.root)
        
        setSupportActionBar(binding.setTaskToolbarInclude.toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.createTask)
        
        initViews()
    }
    
    private fun initViews() {
        val datePickerDialogListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                now[Calendar.YEAR] = year
                now[Calendar.MONTH] = month
                now[Calendar.DAY_OF_MONTH] = dayOfMonth
                timePickerDialog.show()
            }
        val timePickerDialogListener =
            TimePickerDialog.OnTimeSetListener { _: TimePicker?, hourOfDay: Int, minute: Int ->
                now[Calendar.HOUR_OF_DAY] = hourOfDay
                now[Calendar.MINUTE] = minute
                now[Calendar.SECOND] = 0
                // val task_time =
                //     String.format("%02d:%02d", now[Calendar.HOUR_OF_DAY], now[Calendar.MINUTE])
                // set_time_btn.setText(task_time) //TODO: Add day and uncomment
                isTimeSet = true
            }
        datePickerDialog = DatePickerDialog(
                this,
                R.style.TimePickerStyle, //TODO: Check how it works
                datePickerDialogListener,
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH])
        timePickerDialog = TimePickerDialog(
                this,
                R.style.TimePickerStyle,
                timePickerDialogListener, //TODO: Check how it works
                now[Calendar.HOUR_OF_DAY],
                now[Calendar.MINUTE],
                true
        )
        binding.setTaskOKButton.setOnClickListener {
            if (binding.setTaskEditText.text.isNotEmpty()) {
                setTask()
                WidgetProvider.update(this)
            }
        }
        binding.setTimeBtn.setOnClickListener {
            datePickerDialog.show()
        }
    }
    
    private fun setTask() {
        val text = java.lang.String.valueOf(binding.setTaskEditText.text)
        // Needed to set time from 10:0 to 10:00
        val time = String.format(
                "%02d:%02d",
                now[Calendar.HOUR_OF_DAY], //TODO: What if I don't set time?
                now[Calendar.MINUTE]) //TODO: Add day
        val priority = when (binding.setPriorityRadioGroup.checkedRadioButtonId) {
            R.id.whiteRadioButton -> TaskItem.WHITE
            R.id.yellowRadioButton -> TaskItem.YELLOW
            R.id.redRadioButton -> TaskItem.RED
            else -> throw IllegalStateException("setTask: Unexpected value: " + binding.setPriorityRadioGroup.checkedRadioButtonId)
        }
        val task = TaskItem(text = text, time = time, priority = priority)
        val i = Intent()
        i.putExtra(Constans.IntentItem, task)
        setResult(RESULT_OK, i)
        finish()
    }
    
    // private fun setNotification() {
    //     val time = now.timeInMillis - Calendar.getInstance().timeInMillis
    //     val _currentTime = System.currentTimeMillis()
    //     val _triggerReminder = _currentTime + time
    //     val title = resources.getString(R.string.show_layout_task)
    //     val content: String = set_task_editText.getText().toString()
    //     val notification_id =
    //         now[Calendar.HOUR_OF_DAY] + now[Calendar.MINUTE] + now[Calendar.SECOND]
    //     val sp = getSharedPreferences("notifications_id", MODE_PRIVATE)
    //     val editor = sp.edit()
    //     editor.putInt(set_task_editText.getText().toString(), notification_id)
    //     editor.apply()
    //
    //     //        NotificationUtils notificationUtils = new NotificationUtils(this);
    //     //        notificationUtils.setNotification(title, content, _triggerReminder);
    //     //TODO: Notification
    // }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}