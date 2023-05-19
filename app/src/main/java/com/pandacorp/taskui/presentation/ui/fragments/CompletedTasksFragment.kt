package com.pandacorp.taskui.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.FragmentCompletedTasksBinding
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.presentation.ui.TasksAdapter
import com.pandacorp.taskui.presentation.widget.WidgetProvider
import com.pandacorp.taskui.presentation.utils.Constants
import com.pandacorp.taskui.presentation.utils.CustomItemTouchHelper
import com.pandacorp.taskui.presentation.utils.Utils
import com.pandacorp.taskui.presentation.vm.CompletedTasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompletedTasksFragment : Fragment(R.layout.fragment_completed_tasks) {
    companion object {
        const val TAG = "CompletedTasksFragment"
    }

    private var _binding: FragmentCompletedTasksBinding? = null
    private val binding get() = _binding!!

    val vm: CompletedTasksViewModel by viewModels()

    private lateinit var tasksAdapter: TasksAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCompletedTasksBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun initViews() {
        tasksAdapter = TasksAdapter(requireContext(), TaskItem.COMPLETED)

        vm.tasksList.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
        }

        binding.completedDeleteFab.apply {
            setOnClickListener {
                val tasksList = vm.tasksList.value!!.toMutableList()
                vm.removeAll()
                WidgetProvider.update(requireContext())
                val snackBar =
                    Snackbar.make(binding.completedFabsContainer, R.string.successfully, Snackbar.LENGTH_LONG)
                snackBar.apply {
                    setAction(R.string.undo) {
                        vm.undoRemoveAll(tasksList)
                        WidgetProvider.update(requireContext())
                    }
                    anchorView = binding.completedFabsContainer
                    show()
                }
            }
            Utils.addDecreaseSizeOnTouch(this)
        }
        binding.completedFabDeleteForever.apply {
            setOnClickListener {
                val tasksList = vm.tasksList.value!!.toMutableList()
                vm.removeAllForever()
                val snackBar =
                    Snackbar.make(binding.completedFabsContainer, R.string.successfully, Snackbar.LENGTH_LONG)
                snackBar.apply {
                    setAction(R.string.undo) {
                        vm.undoRemoveAllForever(tasksList)
                    }
                    anchorView = binding.completedFabsContainer
                    show()
                }
            }
            Utils.addDecreaseSizeOnTouch(this)
        }
        binding.completedRecyclerView.apply {
            val recyclerViewDivider = DividerItemDecoration(
                requireContext(), DividerItemDecoration.VERTICAL
            )
            addItemDecoration(recyclerViewDivider)
            setHasFixedSize(true)
            adapter = tasksAdapter
            layoutManager = LinearLayoutManager(context)
            enableSwipe()
            registerForContextMenu(this)
        }
    }

    private fun enableSwipe() {
        val itemTouchHelperCallback = CustomItemTouchHelper(
            requireContext(),
            Constants.ITHKey.COMPLETED,
            object : CustomItemTouchHelper.OnTouchListener {
                override fun onSwipedStart(viewHolder: RecyclerView.ViewHolder, direction: Int, key: Constants.ITHKey) {
                    if (key == Constants.ITHKey.COMPLETED) {
                        val position = viewHolder.adapterPosition
                        val taskItem = vm.tasksList.value!![position]
                        vm.removeItem(taskItem)
                        WidgetProvider.update(requireContext())
                        val snackBar =
                            Snackbar.make(binding.completedFabsContainer, R.string.successfully, Snackbar.LENGTH_LONG)
                        snackBar.apply {
                            setAction(R.string.undo) {
                                taskItem.status = TaskItem.COMPLETED
                                vm.restoreItem(position, taskItem)
                                WidgetProvider.update(requireContext())
                            }
                            anchorView = binding.completedFabsContainer
                            show()
                        }
                    }
                }

                override fun onSwipedEnd(viewHolder: RecyclerView.ViewHolder, direction: Int, key: Constants.ITHKey) {
                    if (key == Constants.ITHKey.COMPLETED) {
                        val position = viewHolder.adapterPosition
                        val taskItem = vm.tasksList.value!![position]
                        vm.moveItemToMain(taskItem)
                    }
                }
            }, ItemTouchHelper.START or ItemTouchHelper.END
        )
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.completedRecyclerView)
    }
}