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
import com.pandacorp.taskui.databinding.FragmentDeletedTasksBinding
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.presentation.ui.TasksAdapter
import com.pandacorp.taskui.presentation.ui.widget.WidgetProvider
import com.pandacorp.taskui.presentation.utils.Constans
import com.pandacorp.taskui.presentation.utils.CustomItemTouchHelper
import com.pandacorp.taskui.presentation.vm.DeletedTasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeletedTasksFragment : Fragment() {
    companion object {
        const val TAG = "DeletedTasksFragment"
    }
    
    private var _binding: FragmentDeletedTasksBinding? = null
    private val binding get() = _binding!!
    
    val vm: DeletedTasksViewModel by viewModels()
    
    private lateinit var tasksAdapter: TasksAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeletedTasksBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }
    
    private fun initViews() {
        tasksAdapter = TasksAdapter(requireContext(), TaskItem.DELETED)
        vm.tasksList.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
        }
        
        binding.deletedDeleteForeverFab.apply {
            stateListAnimator = AnimatorInflater.loadStateListAnimator(
                    requireContext(),
                    R.animator.increase_size_normal_animator)
            setOnClickListener {
                vm.removeAllForever()
            }
        }
        binding.deletedRecyclerView.apply {
            val recyclerViewDivider = DividerItemDecoration(
                    requireContext(), DividerItemDecoration.VERTICAL)
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
                Constans.ITHKey.DELETED,
                object : CustomItemTouchHelper.OnTouchListener {
                    override fun onSwiped(
                        viewHolder: RecyclerView.ViewHolder,
                        direction: Int,
                        key: Constans.ITHKey
                    ) {
                        if (key == Constans.ITHKey.DELETED) {
                            val item = vm.tasksList.value!![viewHolder.bindingAdapterPosition]
                            vm.removeItem(viewHolder.bindingAdapterPosition, item)
                            WidgetProvider.update(requireContext())
                        }
                    }
                })
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.deletedRecyclerView)
    }
    
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
    
}