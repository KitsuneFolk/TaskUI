package com.pandacorp.taskui.presentation.ui.fragments

import android.animation.AnimatorInflater
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
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.FragmentCompletedTasksBinding
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.presentation.ui.TasksAdapter
import com.pandacorp.taskui.presentation.utils.Constans
import com.pandacorp.taskui.presentation.utils.CustomItemTouchHelper
import com.pandacorp.taskui.presentation.vm.CompletedTasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompletedTasksFragment : Fragment() {
    companion object {
        const val TAG = "CompletedTasksFragment"
    }
    
    private var _binding: FragmentCompletedTasksBinding? = null
    private val binding get() = _binding!!
    
    val vm: CompletedTasksViewModel by viewModels()
    
    private lateinit var tasksAdapter: TasksAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedTasksBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }
    
    private fun initViews() {
        tasksAdapter = TasksAdapter(requireContext(), TaskItem.COMPLETED)
    
        vm.tasksList.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
        }
    
        binding.completedDeleteFab.apply {
            stateListAnimator = AnimatorInflater.loadStateListAnimator(
                    requireContext(),
                    R.animator.increase_size_normal_animator)
            setOnClickListener {
                vm.removeAll()
            }
        }
        binding.completedFabDeleteForever.apply {
            stateListAnimator = AnimatorInflater.loadStateListAnimator(
                    requireContext(),
                    R.animator.increase_size_normal_animator)
            setOnClickListener {
                vm.removeAllForever()
            }
        }
        binding.completedRecyclerView.apply {
            val recyclerViewDivider = DividerItemDecoration(
                    requireContext(), DividerItemDecoration.VERTICAL)
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
                Constans.ITHKey.COMPLETED,
                object : CustomItemTouchHelper.OnTouchListener {
                    override fun onSwiped(
                        viewHolder: RecyclerView.ViewHolder,
                        direction: Int,
                        key: Constans.ITHKey
                    ) {
                        if (key == Constans.ITHKey.COMPLETED) {
                            val item = vm.tasksList.value!![viewHolder.bindingAdapterPosition]
                            vm.removeItem(viewHolder.bindingAdapterPosition, item)
                        }
                    }
                })
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.completedRecyclerView)
    }
    
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}