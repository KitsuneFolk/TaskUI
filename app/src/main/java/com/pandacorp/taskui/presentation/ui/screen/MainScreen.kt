package com.pandacorp.taskui.presentation.ui.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.ScreenMainBinding
import com.pandacorp.taskui.presentation.utils.app
import com.pandacorp.taskui.presentation.utils.supportActionBar

class MainScreen : Fragment() {
    private var _binding: ScreenMainBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy {
        val nestedNavHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        nestedNavHostFragment!!.navController
    }

    fun navigateFragment(id: Int, options: Bundle = bundleOf()) {
        navController.navigate(id, options)
        setTitle()
    }

    private fun setTitle() {
        val stringId = when (app.selectedNavigationItemId) {
            R.id.nav_main_tasks -> R.string.main_tasks
            R.id.nav_completed_tasks -> R.string.completed_tasks
            R.id.nav_deleted_tasks -> R.string.deleted_tasks
            else -> R.string.main_tasks
        }
        supportActionBar?.setTitle(stringId)
    }

    private fun initViews() {
        navigateFragment(app.selectedNavigationItemId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ScreenMainBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        setTitle()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}