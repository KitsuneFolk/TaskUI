package com.pandacorp.taskui.presentation.ui.screen

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.pandacorp.taskui.R
import com.pandacorp.taskui.presentation.utils.supportActionBar

class MainScreen : Fragment(R.layout.screen_main) {
    private val navHostFragment by lazy {
        childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private val navController by lazy {
        navHostFragment.navController
    }

    fun navigateFragment(id: Int, options: Bundle? = bundleOf()) {
        navController.navigate(id, options)
        setTitle()
    }

    override fun onResume() {
        super.onResume()
        setTitle()
    }

    private fun setTitle() {
        val stringId = when (navController.currentDestination?.id) {
            R.id.nav_main_tasks -> R.string.main_tasks
            R.id.nav_completed_tasks -> R.string.completed_tasks
            R.id.nav_deleted_tasks -> R.string.deleted_tasks
            else -> R.string.main_tasks
        }
        supportActionBar?.setTitle(stringId)
    }
}