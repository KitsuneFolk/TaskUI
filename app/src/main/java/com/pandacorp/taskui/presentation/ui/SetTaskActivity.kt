package com.pandacorp.taskui.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.ActivitySetTaskBinding
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.domain.usecases.AddTaskUseCase
import com.pandacorp.taskui.presentation.notifications.NotificationUtils
import com.pandacorp.taskui.presentation.ui.widget.WidgetProvider
import com.pandacorp.taskui.presentation.utils.Constants
import com.pandacorp.taskui.presentation.utils.PreferenceHandler
import com.pandacorp.taskui.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SetTaskActivity : AppCompatActivity() {
    companion object {
        const val TAG = "SetTaskActivity"
    }

    @Inject
    lateinit var addTaskUseCase: AddTaskUseCase

    private var _binding: ActivitySetTaskBinding? = null
    private val binding get() = _binding!!

    private val dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault())
    private var selectedDateCalendar = Calendar.getInstance(Locale.getDefault())
    private var selectedTime: Calendar? = null
    private lateinit var timePickerDialog: MaterialTimePicker
    private lateinit var datePickerDialog: MaterialDatePicker<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setupExceptionHandler()
        PreferenceHandler(this).load()
        _binding = ActivitySetTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.setTaskToolbarInclude.toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.createTask)

        // Restore selected values on the device rotation
        savedInstanceState?.apply {
            val time = getLong(Constants.TIME, 0L)
            if (time != 0L) {
                selectedDateCalendar.timeInMillis = time
                selectedTime!!.timeInMillis = time
                binding.setTimeBtn.text = dateFormatter.format(selectedTime!!.time)
            }
            binding.setTaskEditText.setText(getString(Constants.TITLE, null))
            binding.setPriorityRadioGroup.check(getInt(Constants.PRIORITY, 0))

            // Close the opened dialogs due to the callbacks loosing after the device rotation.
            val dialog = supportFragmentManager.findFragmentByTag("DatePickerDialog")
            if (dialog != null) {
                supportFragmentManager.beginTransaction().remove(dialog).commit()
            }
            val dialog2 = supportFragmentManager.findFragmentByTag("TimePickerDialog")
            if (dialog2 != null) {
                supportFragmentManager.beginTransaction().remove(dialog2).commit()
            }
        }

        initViews()
    }

    private fun initViews() {
        datePickerDialog = MaterialDatePicker.Builder.datePicker().build().apply {
            addOnPositiveButtonClickListener { selectedDate ->
                selectedDateCalendar.timeInMillis = selectedDate
                timePickerDialog.show(supportFragmentManager, "TimePickerDialog")
            }
        }

        timePickerDialog = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(Calendar.getInstance(Locale.getDefault())[Calendar.HOUR_OF_DAY])
            .setMinute(Calendar.getInstance(Locale.getDefault())[Calendar.MINUTE]).build().apply {
                addOnPositiveButtonClickListener {
                    selectedDateCalendar[Calendar.HOUR_OF_DAY] = timePickerDialog.hour
                    selectedDateCalendar[Calendar.MINUTE] = timePickerDialog.minute
                    selectedDateCalendar[Calendar.SECOND] = 0

                    selectedTime = Calendar.getInstance(Locale.getDefault())
                    selectedTime!!.timeInMillis = selectedDateCalendar.timeInMillis

                    binding.setTimeBtn.text = dateFormatter.format(selectedTime!!.time)
                }
            }
        binding.OKBtn.setOnClickListener {
            if (binding.setTaskEditText.text.isNotEmpty()) {
                setTask()
                WidgetProvider.update(this)
            }
        }
        binding.setTimeBtn.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag("DatePickerDialog") != null) return@setOnClickListener // avoid multi clicks if the dialog is already started
            datePickerDialog.show(supportFragmentManager, "DatePickerDialog")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(Constants.TIME, selectedTime?.timeInMillis ?: 0L)
        outState.putString(Constants.TITLE, binding.setTaskEditText.text.toString())
        outState.putString(Constants.PRIORITY, binding.setPriorityRadioGroup.checkedRadioButtonId.toString())
    }

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

    private fun setTask() {
        val text = binding.setTaskEditText.text.toString()
        val priority = when (binding.setPriorityRadioGroup.checkedRadioButtonId) {
            R.id.nullRadioButton -> null
            R.id.whiteRadioButton -> TaskItem.WHITE
            R.id.yellowRadioButton -> TaskItem.YELLOW
            R.id.redRadioButton -> TaskItem.RED
            else -> throw IllegalStateException("Unexpected value: ${binding.setPriorityRadioGroup.checkedRadioButtonId}")
        }
        CoroutineScope(Dispatchers.IO).launch {
            val taskItem = TaskItem(text = text, time = selectedTime?.timeInMillis, priority = priority)
            taskItem.id = addTaskUseCase(taskItem)
            val intent = Intent()
            intent.putExtra(Constants.IntentItem, taskItem)
            setResult(RESULT_OK, intent)
            withContext(Dispatchers.Main) {
                if (taskItem.time != null)
                    NotificationUtils.create(this@SetTaskActivity, taskItem)
                WidgetProvider.update(this@SetTaskActivity)
                finish()
            }
        }
    }
}