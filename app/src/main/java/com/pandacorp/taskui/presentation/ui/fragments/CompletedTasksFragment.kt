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
import com.pandacorp.taskui.presentation.ui.adapter.TasksAdapter
import com.pandacorp.taskui.presentation.utils.Constants
import com.pandacorp.taskui.presentation.utils.CustomItemTouchHelper
import com.pandacorp.taskui.presentation.utils.Utils
import com.pandacorp.taskui.presentation.vm.CompletedTasksViewModel
import com.pandacorp.taskui.presentation.widget.WidgetProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompletedTasksFragment : Fragment() {
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

        binding.deleteFab.apply {
            setOnClickListener {
                val tasksList = vm.tasksList.value!!.toMutableList()
                if (tasksList.isEmpty()) return@setOnClickListener
                vm.removeAll()
                WidgetProvider.update(requireContext())
                Snackbar.make(binding.fabsLayout, R.string.successfully, Snackbar.LENGTH_LONG).apply {
                    setAction(R.string.undo) {
                        vm.undoRemoveAll(tasksList)
                        WidgetProvider.update(requireContext())
                    }
                    anchorView = binding.fabsLayout
                    show()
                }
            }
            Utils.addDecreaseSizeOnTouch(this)
        }
        binding.deleteForeverFab.apply {
            setOnClickListener {
                val tasksList = vm.tasksList.value!!.toMutableList()
                if (tasksList.isEmpty()) return@setOnClickListener
                vm.removeAllForever()
                val snackBar =
                    Snackbar.make(binding.fabsLayout, R.string.successfully, Snackbar.LENGTH_LONG)
                snackBar.apply {
                    setAction(R.string.undo) {
                        vm.undoRemoveAllForever(tasksList)
                    }
                    anchorView = binding.fabsLayout
                    show()
                }
            }
            Utils.addDecreaseSizeOnTouch(this)
        }
        binding.recyclerView.apply {
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
                        val position = viewHolder.bindingAdapterPosition
                        val taskItem = vm.tasksList.value!![position]
                        vm.removeItem(taskItem)
                        WidgetProvider.update(requireContext())
                        val snackBar =
                            Snackbar.make(binding.fabsLayout, R.string.successfully, Snackbar.LENGTH_LONG)
                        snackBar.apply {
                            setAction(R.string.undo) {
                                taskItem.status = TaskItem.COMPLETED
                                vm.restoreItem(position, taskItem)
                                WidgetProvider.update(requireContext())
                            }
                            anchorView = binding.fabsLayout
                            show()
                        }
                    }
                }

                override fun onSwipedEnd(viewHolder: RecyclerView.ViewHolder, direction: Int, key: Constants.ITHKey) {
                    if (key == Constants.ITHKey.COMPLETED) {
                        val position = viewHolder.bindingAdapterPosition
                        val taskItem = vm.tasksList.value!![position]
                        vm.moveItemToMain(taskItem)
                    }
                }
            }, ItemTouchHelper.START or ItemTouchHelper.END
        )
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerView)
    }
}