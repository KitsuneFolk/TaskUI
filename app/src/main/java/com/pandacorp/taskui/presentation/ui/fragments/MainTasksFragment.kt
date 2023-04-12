package com.pandacorp.taskui.presentation.ui.fragments

import android.animation.AnimatorInflater
import android.content.Intent
import android.os.Bundle
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
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.FragmentMainTasksBinding
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.presentation.ui.SetTaskActivity
import com.pandacorp.taskui.presentation.ui.TasksAdapter
import com.pandacorp.taskui.presentation.ui.widget.WidgetProvider
import com.pandacorp.taskui.presentation.utils.Constans
import com.pandacorp.taskui.presentation.utils.CustomItemTouchHelper
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
    
    private var addTaskResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { data -> // null if no success
                val taskItem = data.getSerializableExtra(Constans.IntentItem) as TaskItem
                vm.addItem(taskItem)
            }
        }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainTasksBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }
    
    private fun initViews() {
        tasksAdapter = TasksAdapter(requireContext(), TaskItem.MAIN)
        vm.tasksList.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
        }
        binding.mainRecyclerView.apply {
            val recyclerViewDivider = DividerItemDecoration(
                    requireContext(), DividerItemDecoration.VERTICAL)
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
                    val taskItem =
                        TaskItem(text = text)
                    vm.addItem(taskItem)
                    binding.fastTypeEditText.setText("")
                    WidgetProvider.update(requireContext())
                }
            }
        }
        binding.addFab.apply {
            stateListAnimator = AnimatorInflater.loadStateListAnimator(
                    requireContext(),
                    R.animator.increase_size_normal_animator)
            setOnClickListener {
                val intent = Intent(activity, SetTaskActivity::class.java)
                addTaskResultLauncher.launch(intent)
            }
        }
        binding.mainDeleteFab.apply {
            stateListAnimator = AnimatorInflater.loadStateListAnimator(
                    requireContext(),
                    R.animator.increase_size_normal_animator)
            setOnClickListener {
                vm.removeAll()
                binding.fastTypeEditText.setText("")
                WidgetProvider.update(requireContext())
            }
        }
        binding.mainDeleteForeverFab.apply {
            stateListAnimator = AnimatorInflater.loadStateListAnimator(
                    requireContext(),
                    R.animator.increase_size_normal_animator)
            setOnClickListener {
                vm.removeAllForever()
                binding.fastTypeEditText.setText("")
                WidgetProvider.update(requireContext())
            }
        }
    }
    
    private fun enableSwipe() {
        val itemTouchHelperCallback = CustomItemTouchHelper(
                requireContext(),
                Constans.ITHKey.MAIN,
                object : CustomItemTouchHelper.OnTouchListener {
                    override fun onSwiped(
                        viewHolder: RecyclerView.ViewHolder,
                        direction: Int,
                        key: Constans.ITHKey
                    ) {
                        if (key == Constans.ITHKey.MAIN) {
                            val item = vm.tasksList.value!![viewHolder.bindingAdapterPosition]
                            vm.removeItem(viewHolder.bindingAdapterPosition, item)
                            WidgetProvider.update(requireContext())
                            // cancelNotification(item) //TODO: Notification
                        }
                    }
                })
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.mainRecyclerView)
    }

// private fun cancelNotification(deletedModel: TaskItem) {
//     val sp: SharedPreferences =
//         requireContext().getSharedPreferences("notifications_id", Context.MODE_PRIVATE)
//     val notificationId: Int =
//         sp.getInt(
//                 deletedModel.text,
//                 0) //TODO: Have no idea what's happening here, why do I use sp?
//     NotificationUtils.cancelNotification(requireContext(), notificationId)
// } //TODO: Notification
    
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}