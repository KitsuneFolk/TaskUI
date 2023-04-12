package com.pandacorp.taskui.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.ActivityMainBinding
import com.pandacorp.taskui.presentation.di.app.App
import com.pandacorp.taskui.presentation.ui.fragments.CompletedTasksFragment
import com.pandacorp.taskui.presentation.ui.fragments.DeletedTasksFragment
import com.pandacorp.taskui.presentation.ui.fragments.MainTasksFragment
import com.pandacorp.taskui.presentation.ui.settings.SettingsActivity
import com.pandacorp.taskui.presentation.utils.Constans
import com.pandacorp.taskui.presentation.utils.PreferenceHandler
import com.pandacorp.taskui.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val app by lazy { application as App }

    private var mAppBarConfiguration: AppBarConfiguration? = null

    companion object {
        private const val TAG = "MainActivity"
    }

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val preferencesResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(0, com.pandacorp.taskui.R.anim.slide_out_right)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setupExceptionHandler()
        PreferenceHandler(this).load()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMainInclude.appBarMainToolbarInclude.toolbar)

        initViews()
    }

    private fun initViews() {
        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_main_tasks,
            R.id.nav_completed_tasks,
            R.id.nav_deleted_tasks
        )
            .setOpenableLayout(binding.drawerLayout)
            .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        binding.navView.setCheckedItem(app.selectedNavigationItemId)
        binding.navView.setNavigationItemSelectedListener {
            if (it.itemId == app.selectedNavigationItemId) return@setNavigationItemSelectedListener true
            // Start animation
            Log.d(TAG, "initViews: previous selectedNavigationItemId = ${app.selectedNavigationItemId}")
            app.selectedNavigationItemId = it.itemId
            Log.d(TAG, "initViews: itemId = ${it.itemId}")
            Handler(Looper.getMainLooper()).postDelayed({
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }, Constans.CLOSE_DELAY)
            true
        }
        /**
         * Drawer State Change Listener
         * Change the fragment after the drawer is closed
         * To avoid lag while closing the drawer
         */
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {

                val selectedItemId = (application as App).selectedNavigationItemId
                Log.d(TAG, "onDrawerClosed: selectedItemId = $selectedItemId")
                if (selectedItemId == binding.navView.checkedItem?.itemId) {
                    if (binding.navView.checkedItem == null)
                        Log.d(TAG, "onDrawerClosed: null")
                    Log.d(TAG, "onDrawerClosed: return")
                    return
                }

                var fragment: Fragment? = null
                when (selectedItemId) {
                    R.id.nav_main_tasks -> {
                        fragment = MainTasksFragment()
                        Log.d(TAG, "onDrawerClosed: main")
                    }

                    R.id.nav_completed_tasks -> {
                        fragment = CompletedTasksFragment()
                        Log.d(TAG, "onDrawerClosed: completed")
                    }

                    R.id.nav_deleted_tasks -> {
                        fragment = DeletedTasksFragment()
                        Log.d(TAG, "onDrawerClosed: deleted")
                    }

                    R.id.nav_settings -> {
                        //TODO: Check if after leaving the activity this item is still selected
                        preferencesResultLauncher.launch(
                            Intent(
                                this@MainActivity,
                                SettingsActivity::class.java
                            )
                        )
                        // Uncheck selectedNavigationItemId in App and set previous one
                        app.selectedNavigationItemId = binding.navView.checkedItem?.itemId ?: R.id.nav_main_tasks
                        Log.d(TAG, "onDrawerClosed: settings")
                    }

                    R.id.nav_share -> {
                        val sendIntent = Intent()
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            resources.getString(R.string.shareText)
                        )
                        sendIntent.type = "text/plain"
                        startActivity(
                            Intent.createChooser(
                                sendIntent,
                                getString(R.string.menu_share)
                            )
                        )
                        // Uncheck selectedNavigationItemId in App and set previous one
                        app.selectedNavigationItemId = binding.navView.checkedItem?.itemId ?: R.id.nav_main_tasks
                        Log.d(TAG, "onDrawerClosed: share")
                    }
                }
                Log.d(TAG, "onDrawerClosed: selectedNavigationItemId = ${app.selectedNavigationItemId}")
                if (fragment != null) {
                    Log.d(TAG, "onDrawerClosed: transaction")
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.nav_host_fragment, fragment)
                        .commit()
                }

            }

            override fun onDrawerStateChanged(newState: Int) {}

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}