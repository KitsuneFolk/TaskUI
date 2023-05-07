package com.pandacorp.taskui.presentation.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.FragmentMainTasksBinding
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.presentation.notifications.NotificationUtils
import com.pandacorp.taskui.presentation.ui.SetTaskActivity
import com.pandacorp.taskui.presentation.ui.TasksAdapter
import com.pandacorp.taskui.presentation.ui.widget.WidgetProvider
import com.pandacorp.taskui.presentation.utils.Constants
import com.pandacorp.taskui.presentation.utils.CustomItemTouchHelper
import com.pandacorp.taskui.presentation.utils.Utils
import com.pandacorp.taskui.presentation.utils.getSerializableExtraSupport
import com.pandacorp.taskui.presentation.vm.MainTasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainTasksFragment : Fragment() {
    companion object {
        const val TAG = "MainTasksFragment"
    }

    private var _binding: FragmentMainTasksBinding? = null
    private val binding get() = _binding!!

    val vm: MainTasksViewModel by viewModels()
    private lateinit var tasksAdapter: TasksAdapter

    /**
     * Get item from widget to update it in the viewModel
     */
    private val completeTaskReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val taskItem =
                intent.getSerializableExtraSupport(Constants.IntentItem, TaskItem::class.java)!!
            vm.completeItemVmOnly(taskItem)
            if (taskItem.time != null)
                NotificationUtils.cancel(requireContext(), taskItem)
        }
    }

    private var addTaskResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        it.data?.let { data -> // null if no success
            val taskItem =
                data.getSerializableExtraSupport(Constants.IntentItem, TaskItem::class.java)!!
            vm.addItemVmOnly(taskItem)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainTasksBinding.inflate(layoutInflater)
        requireContext().registerReceiver(
            completeTaskReceiver,
            IntentFilter(Constants.Widget.COMPLETE_TASK_ACTION)
        )
        initViews()
        return binding.root
    }

    private fun initViews() {
        tasksAdapter = TasksAdapter(requireContext(), TaskItem.MAIN)
        tasksAdapter.taskAdapterListener = object : TasksAdapter.TaskAdapterListener {
            override fun onCompleteButtonClicked(position: Int, taskItem: TaskItem) {
                vm.completeItem(taskItem)
                WidgetProvider.update(requireContext())
                if (taskItem.time != null)
                    NotificationUtils.cancel(requireContext(), taskItem)
                val snackBar =
                    Snackbar.make(binding.fastTypeContainer, R.string.successfully, Snackbar.LENGTH_LONG)
                snackBar.apply {
                    setAction(R.string.undo) {
                        taskItem.status = TaskItem.MAIN
                        vm.restoreItem(position, taskItem)
                        if (taskItem.time != null)
                            NotificationUtils.create(requireContext(), taskItem)
                        WidgetProvider.update(requireContext())
                    }
                    anchorView = binding.fastTypeContainer
                    show()
                }
            }
        }
        vm.tasksList.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
            if (it.isEmpty()) {
                // Add a fade animation
                val transition = Fade()
                transition.duration = Constants.ANIMATION_DURATION
                transition.addTarget(binding.mainRecyclerView)
                transition.addTarget(binding.includeHint.EmptyHintRoot)

                TransitionManager.beginDelayedTransition(binding.mainTasksRoot, transition)
                binding.mainRecyclerView.visibility = View.GONE
                binding.includeHint.EmptyHintRoot.visibility = View.VISIBLE
            } else {
                if (binding.includeHint.EmptyHintRoot.visibility != View.VISIBLE) return@observe // skip, user just entered the fragment
                val transition = Fade()
                transition.duration = Constants.ANIMATION_DURATION
                transition.addTarget(binding.mainRecyclerView)
                transition.addTarget(binding.includeHint.EmptyHintRoot)

                TransitionManager.beginDelayedTransition(binding.mainTasksRoot, transition)
                binding.mainRecyclerView.visibility = View.VISIBLE
                binding.includeHint.EmptyHintRoot.visibility = View.GONE
            }
        }

        binding.mainRecyclerView.apply {
            val recyclerViewDivider = DividerItemDecoration(
                requireContext(), DividerItemDecoration.VERTICAL
            )
            addItemDecoration(recyclerViewDivider)
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = tasksAdapter
            enableSwipe()
            registerForContextMenu(this)
        }

        binding.fastTypeButton.apply {
            setOnClickListener {
                val text = binding.fastTypeEditText.text.toString()
                if (text.isNotEmpty()) {
                    val taskItem = TaskItem(text = text)
                    vm.addItem(taskItem)
                    binding.fastTypeEditText.setText("")
                    WidgetProvider.update(requireContext())
                }
            }
        }
        binding.addFab.apply {
            setOnClickListener {
                val intent = Intent(requireActivity(), SetTaskActivity::class.java)
                addTaskResultLauncher.launch(intent)
            }
            Utils.addDecreaseSizeOnTouch(this)
        }
        binding.mainDeleteFab.apply {
            setOnClickListener {
                // Start a handler, if user clicked the snackBar's button, cancel the handler, otherwise cancel all
                // notifications
                val handler = Handler(Looper.getMainLooper())
                val runnable = Runnable {
                    NotificationUtils.cancelAll(requireContext(), vm.tasksList.value!!)
                }
                handler.postDelayed(runnable, 6000)

                val tasksList = vm.tasksList.value!!.toMutableList()
                vm.removeAll()
                WidgetProvider.update(requireContext())
                val snackBar =
                    Snackbar.make(binding.mainFabsContainer, R.string.successfully, Snackbar.LENGTH_LONG)
                snackBar.apply {
                    setAction(R.string.undo) {
                        vm.undoRemoveAll(tasksList)
                        WidgetProvider.update(requireContext())
                        NotificationUtils.cancelAll(requireContext(), tasksList)
                    }
                    anchorView = binding.mainFabsContainer
                    show()
                }
            }
            Utils.addDecreaseSizeOnTouch(this)
        }
        binding.mainDeleteForeverFab.apply {
            setOnClickListener {
                // Start a handler, if user clicked the snackBar's button, cancel the handler, otherwise cancel all
                // notifications
                val handler = Handler(Looper.getMainLooper())
                val runnable = Runnable {
                    NotificationUtils.cancelAll(requireContext(), vm.tasksList.value!!)
                }
                handler.postDelayed(runnable, 6000)

                val tasksList = vm.tasksList.value!!.toMutableList()
                vm.removeAllForever()
                WidgetProvider.update(requireContext())
                val snackBar =
                    Snackbar.make(binding.mainFabsContainer, R.string.successfully, Snackbar.LENGTH_LONG)
                snackBar.apply {
                    setAction(R.string.undo) {
                        vm.undoRemoveAllForever(tasksList)
                        WidgetProvider.update(requireContext())
                        NotificationUtils.cancelAll(requireContext(), tasksList)
                    }
                    anchorView = binding.mainFabsContainer
                    show()
                }
            }
            Utils.addDecreaseSizeOnTouch(this)
        }
    }

    private fun enableSwipe() {
        val itemTouchHelperCallback = CustomItemTouchHelper(
            requireContext(),
            Constants.ITHKey.MAIN,
            object : CustomItemTouchHelper.OnTouchListener {
                override fun onSwipedStart(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int,
                    key: Constants.ITHKey
                ) {
                    if (key == Constants.ITHKey.MAIN) {
                        val position = viewHolder.adapterPosition
                        val taskItem = vm.tasksList.value!![position]
                        vm.removeItem(taskItem)
                        WidgetProvider.update(requireContext())
                        if (taskItem.time != null)
                            NotificationUtils.cancel(requireContext(), taskItem)
                        val snackBar =
                            Snackbar.make(binding.fastTypeContainer, R.string.successfully, Snackbar.LENGTH_LONG)
                        snackBar.apply {
                            setAction(R.string.undo) {
                                taskItem.status = TaskItem.MAIN
                                vm.restoreItem(position, taskItem)
                                WidgetProvider.update(requireContext())
                            }
                            anchorView = binding.fastTypeContainer
                            show()
                        }
                    }
                }

                override fun onSwipedEnd(viewHolder: RecyclerView.ViewHolder, direction: Int, key: Constants.ITHKey) {}
            }, ItemTouchHelper.START
        )
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.mainRecyclerView)
    }

    override fun onDestroy() {
        _binding = null
        try {
            requireContext().unregisterReceiver(completeTaskReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver was not registered, ignore the exception
        }
        super.onDestroy()
    }
}