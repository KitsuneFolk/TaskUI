package com.pandacorp.taskui.presentation.ui.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.ScreenAddTaskBinding
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.domain.usecases.AddTaskUseCase
import com.pandacorp.taskui.presentation.notifications.NotificationUtils
import com.pandacorp.taskui.presentation.ui.MainActivity
import com.pandacorp.taskui.presentation.utils.Constants
import com.pandacorp.taskui.presentation.utils.app
import com.pandacorp.taskui.presentation.utils.supportActionBar
import com.pandacorp.taskui.presentation.widget.WidgetProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AddTaskScreen : Fragment() {
    private var _binding: ScreenAddTaskBinding? = null
    private val binding get() = _binding!!

    private val fragulaNavController by lazy { (requireActivity() as MainActivity).getFragulaNavController() }

    @Inject
    lateinit var addTaskUseCase: AddTaskUseCase

    private val dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault())
    private var selectedDateCalendar = Calendar.getInstance(Locale.getDefault())
    private var selectedTime: Calendar? = null
    private lateinit var timePickerDialog: MaterialTimePicker
    private lateinit var datePickerDialog: MaterialDatePicker<*>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ScreenAddTaskBinding.inflate(layoutInflater)
        initViews(savedInstanceState)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(Constants.TaskItem.TIME, selectedTime?.timeInMillis ?: 0L)
        outState.putString(Constants.TaskItem.TITLE, binding.setTaskEditText.text.toString())
        outState.putString(Constants.TaskItem.PRIORITY, binding.setPriorityRadioGroup.checkedRadioButtonId.toString())
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.setTitle(R.string.createTask)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun initViews(savedInstanceState: Bundle?) {
        // Restore selected values on device rotation

        savedInstanceState?.apply {
            val time = getLong(Constants.TaskItem.TIME, 0L)
            if (time != 0L) {
                selectedDateCalendar.timeInMillis = time
                selectedTime!!.timeInMillis = time
                binding.setTimeBtn.text = dateFormatter.format(selectedTime!!.time)
            }
            binding.setTaskEditText.setText(getString(Constants.TaskItem.TITLE, null))
            binding.setPriorityRadioGroup.check(getInt(Constants.TaskItem.PRIORITY, 0))

            // Close the opened dialogs due to callbacks lose after device rotation.
            val dialog = requireActivity().supportFragmentManager.findFragmentByTag("DatePickerDialog")
            val dialog2 = requireActivity().supportFragmentManager.findFragmentByTag("TimePickerDialog")
            if (dialog != null) requireActivity().supportFragmentManager.beginTransaction().remove(dialog).commit()
            if (dialog2 != null) requireActivity().supportFragmentManager.beginTransaction().remove(dialog2).commit()
        }
        datePickerDialog = MaterialDatePicker.Builder.datePicker().build().apply {
            addOnPositiveButtonClickListener { selectedDate ->
                selectedDateCalendar.timeInMillis = selectedDate
                timePickerDialog.show(requireActivity().supportFragmentManager, "TimePickerDialog")
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
                WidgetProvider.update(requireActivity())
            }
        }
        binding.setTimeBtn.setOnClickListener {
            if (requireActivity().supportFragmentManager.findFragmentByTag("DatePickerDialog") != null)
                return@setOnClickListener // avoid multi clicks if the dialog is already started
            datePickerDialog.show(requireActivity().supportFragmentManager, "DatePickerDialog")
        }
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
        lifecycleScope.launch {
            val taskItem = TaskItem(text = text, time = selectedTime?.timeInMillis, priority = priority)
            taskItem.id = withContext(Dispatchers.IO) {
                addTaskUseCase(taskItem)
            }
            if (taskItem.time != null) NotificationUtils.create(requireContext(), taskItem)
            WidgetProvider.update(requireContext())
            app.taskItem = taskItem
            fragulaNavController.popBackStack()
        }
    }
}