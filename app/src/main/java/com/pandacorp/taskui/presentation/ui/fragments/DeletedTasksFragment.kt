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
import com.pandacorp.taskui.databinding.FragmentDeletedTasksBinding
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.presentation.ui.TasksAdapter
import com.pandacorp.taskui.presentation.widget.WidgetProvider
import com.pandacorp.taskui.presentation.utils.Constants
import com.pandacorp.taskui.presentation.utils.CustomItemTouchHelper
import com.pandacorp.taskui.presentation.utils.Utils
import com.pandacorp.taskui.presentation.vm.DeletedTasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeletedTasksFragment : Fragment(R.layout.fragment_deleted_tasks) {
    companion object {
        const val TAG = "DeletedTasksFragment"
    }

    private var _binding: FragmentDeletedTasksBinding? = null
    private val binding get() = _binding!!

    val vm: DeletedTasksViewModel by viewModels()

    private lateinit var tasksAdapter: TasksAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeletedTasksBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun initViews() {
        tasksAdapter = TasksAdapter(requireContext(), TaskItem.DELETED)
        tasksAdapter.taskAdapterListener = object : TasksAdapter.TaskAdapterListener {
            override fun onCompleteButtonClicked(position: Int, taskItem: TaskItem) {
                vm.completeItem(taskItem)
            }
        }
        vm.tasksList.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
        }

        binding.deleteForeverFab.apply {
            setOnClickListener {
                val tasksList = vm.tasksList.value!!.toMutableList()

                val snackBar =
                    Snackbar.make(binding.deletedFabsContainer, R.string.successfully, Snackbar.LENGTH_LONG)
                snackBar.apply {
                    setAction(R.string.undo) {
                        vm.undoRemoveAllForever(tasksList)
                    }
                    anchorView = binding.deletedFabsContainer
                    show()
                }
                vm.removeAllForever()
            }
            Utils.addDecreaseSizeOnTouch(this)
        }
        binding.deletedRecyclerView.apply {
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
    }

    private fun enableSwipe() {
        val itemTouchHelperCallback = CustomItemTouchHelper(
            requireContext(),
            Constants.ITHKey.DELETED,
            object : CustomItemTouchHelper.OnTouchListener {
                override fun onSwipedStart(viewHolder: RecyclerView.ViewHolder, direction: Int, key: Constants.ITHKey) {
                    if (key == Constants.ITHKey.DELETED) {
                        val position = viewHolder.adapterPosition
                        val taskItem = vm.tasksList.value!![position]
                        vm.removeItem(taskItem)
                        val snackBar =
                            Snackbar.make(binding.deletedFabsContainer, R.string.successfully, Snackbar.LENGTH_LONG)
                        snackBar.apply {
                            setAction(R.string.undo) {
                                vm.restoreItem(position, taskItem)
                                WidgetProvider.update(requireContext())
                            }
                            anchorView = binding.deletedFabsContainer
                            show()
                        }
                    }
                }

                override fun onSwipedEnd(viewHolder: RecyclerView.ViewHolder, direction: Int, key: Constants.ITHKey) {
                    if (key == Constants.ITHKey.DELETED) {
                        val position = viewHolder.adapterPosition
                        val taskItem = vm.tasksList.value!![position]
                        vm.moveItemToMain(taskItem)
                    }
                }
            }, ItemTouchHelper.START or ItemTouchHelper.END
        )
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.deletedRecyclerView)
    }

}