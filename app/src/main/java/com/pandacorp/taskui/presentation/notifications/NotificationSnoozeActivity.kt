package com.pandacorp.taskui.presentation.notifications

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.DialogSnoozeBinding
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.presentation.utils.Constants
import com.pandacorp.taskui.presentation.utils.Utils
import com.pandacorp.taskui.presentation.utils.getParcelableExtraSupport
import java.util.Calendar
import java.util.Locale


class NotificationSnoozeActivity : AppCompatActivity() {
    companion object {
        private const val TAG = NotificationUtils.TAG
    }

    private var _binding: DialogSnoozeBinding? = null
    private val binding get() = _binding!!

    private lateinit var timePickerDialog: MaterialTimePicker
    private lateinit var datePickerDialog: MaterialDatePicker<*>

    private var selectedTime = Calendar.getInstance(Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setupExceptionHandler()

        // Set the theme and get the background id, setTheme mandatory before the binding
        _binding = DialogSnoozeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.background.background = ContextCompat.getDrawable(this, R.drawable.rounded_dark_background)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Remove background to show the custom one

        // Change the activity size
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        window.setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT)

        initViews()
    }

    private fun initViews() {
        Log.d(TAG, "initViews: ")
        val taskItem = intent.getParcelableExtraSupport(Constants.IntentItem, TaskItem::class.java)!!
        binding.snooze5Min.apply {
            setOnClickListener {
                val snoozedTime: Long = System.currentTimeMillis() + (5 * 60 * 1000)
                NotificationUtils.snoozeNotification(this@NotificationSnoozeActivity, taskItem, snoozedTime)
                finish()
            }
        }
        binding.snooze30Min.apply {
            setOnClickListener {
                val snoozedTime: Long = System.currentTimeMillis() + (30 * 60 * 1000)
                NotificationUtils.snoozeNotification(this@NotificationSnoozeActivity, taskItem, snoozedTime)
                finish()
            }
        }
        binding.snooze60Min.apply {
            setOnClickListener {
                val snoozedTime: Long = System.currentTimeMillis() + (60 * 60 * 1000)
                NotificationUtils.snoozeNotification(this@NotificationSnoozeActivity, taskItem, snoozedTime)
                finish()
            }
        }
        binding.snooze4Hours.apply {
            setOnClickListener {
                val snoozedTime: Long = System.currentTimeMillis() + (4 * 60 * 60 * 1000)
                NotificationUtils.snoozeNotification(this@NotificationSnoozeActivity, taskItem, snoozedTime)
                finish()
            }
        }
        binding.snooze8Hours.apply {
            setOnClickListener {
                val snoozedTime: Long = System.currentTimeMillis() + (8 * 60 * 60 * 1000)
                NotificationUtils.snoozeNotification(
                    this@NotificationSnoozeActivity, taskItem, snoozedTime
                )
                finish()
            }
        }
        binding.snooze24Hours.apply {
            setOnClickListener {
                val snoozedTime: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
                NotificationUtils.snoozeNotification(this@NotificationSnoozeActivity, taskItem, snoozedTime)
                finish()
            }
        }
        binding.snoozeCustom.apply {
            // avoid multi clicks if the dialog is already started
            setOnClickListener {
                if (supportFragmentManager.findFragmentByTag("SnoozeDatePickerDialog") != null) return@setOnClickListener
                datePickerDialog.show(supportFragmentManager, "SnoozeDatePickerDialog")
            }
        }
        binding.snoozeCancel.apply {
            setOnClickListener {
                finish()
            }
        }

        datePickerDialog = MaterialDatePicker.Builder.datePicker().build().apply {
            addOnPositiveButtonClickListener { selectedDate ->
                selectedTime.timeInMillis = selectedDate
                timePickerDialog.show(supportFragmentManager, "SnoozeTimePickerDialog")
            }
        }

        timePickerDialog = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(Calendar.getInstance(Locale.getDefault())[Calendar.HOUR_OF_DAY])
            .setMinute(Calendar.getInstance(Locale.getDefault())[Calendar.MINUTE]).build().apply {
                addOnPositiveButtonClickListener {
                    selectedTime[Calendar.HOUR_OF_DAY] = timePickerDialog.hour
                    selectedTime[Calendar.MINUTE] = timePickerDialog.minute
                    selectedTime[Calendar.SECOND] = 0
                    NotificationUtils.snoozeNotification(
                        this@NotificationSnoozeActivity,
                        taskItem,
                        selectedTime.timeInMillis
                    )
                    finish()
                }
            }
    }

}