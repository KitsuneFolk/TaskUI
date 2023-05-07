package com.pandacorp.taskui.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.ActivityMainBinding
import com.pandacorp.taskui.presentation.di.app.App
import com.pandacorp.taskui.presentation.ui.settings.SettingsActivity
import com.pandacorp.taskui.presentation.utils.PreferenceHandler
import com.pandacorp.taskui.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val app by lazy { application as App }

    private var mAppBarConfiguration: AppBarConfiguration? = null

    private val navController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
    }

    companion object {
        const val TAG = "MainActivity"
    }

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val preferencesResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(0, R.anim.slide_out_right)
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
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        binding.navView.setCheckedItem(app.selectedNavigationItemId)
        binding.navView.setNavigationItemSelectedListener {
            lifecycleScope.launch {
                delay(200) // add delay
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                delay(250) // add delay
                when (it.itemId) {
                    R.id.nav_main_tasks -> {
                        Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
                            .navigate(R.id.nav_main_tasks)
                    }

                    R.id.nav_completed_tasks -> {
                        Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
                            .navigate(R.id.nav_completed_tasks)
                    }

                    R.id.nav_deleted_tasks -> {
                        Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
                            .navigate(R.id.nav_deleted_tasks)
                    }

                    R.id.nav_settings -> {
                        preferencesResultLauncher.launch(
                            Intent(
                                this@MainActivity,
                                SettingsActivity::class.java
                            )
                        )
                        // Uncheck selectedNavigationItemId in App and set the previous one
                        app.selectedNavigationItemId =
                            binding.navView.checkedItem?.itemId ?: R.id.nav_main_tasks
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
                        // Uncheck selectedNavigationItemId in App and set the previous one
                        app.selectedNavigationItemId =
                            binding.navView.checkedItem?.itemId ?: R.id.nav_main_tasks
                    }
                }
            }
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}