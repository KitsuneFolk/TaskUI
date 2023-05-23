package com.pandacorp.taskui.presentation.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.fragula2.animation.SwipeController
import com.fragula2.utils.findSwipeController
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.ActivityMainBinding
import com.pandacorp.taskui.presentation.di.app.App
import com.pandacorp.taskui.presentation.ui.screen.MainScreen
import com.pandacorp.taskui.presentation.utils.Constants
import com.pandacorp.taskui.presentation.utils.PreferenceHandler
import com.pandacorp.taskui.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val app by lazy { application as App }

    private lateinit var fragulaNavController: NavController
    private lateinit var swipeController: SwipeController

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var mainScreen: MainScreen? = null

    fun getFragulaNavController(): NavController = fragulaNavController

    private fun initViews() {
        binding.fragulaNavHostFragment.getFragment<NavHostFragment>().also {
            swipeController = it.findSwipeController()
            fragulaNavController = it.navController
            val swipeBackFragment = it.childFragmentManager.fragments.first()
            swipeBackFragment.childFragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    /**
                     * Find screens here, the method is called even if the activity got rotated
                     */
                    override fun onFragmentViewCreated(
                        fm: FragmentManager,
                        f: Fragment,
                        v: View,
                        savedInstanceState: Bundle?
                    ) {
                        super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                        if (f is MainScreen) {
                            mainScreen = f
                        }
                    }

                    /**
                     *  This method is called only once for the Activity(even on rotation doesn't trigger it),
                     *  so perform the intent checks here
                     */
                    override fun onFragmentCreated(fm: FragmentManager, fragment: Fragment, savedInstanceState: Bundle?) {
                        if (fragment is MainScreen) {
                            mainScreen = fragment
                            // Check if the activity is launched from the widget
                            intent.apply {
                                when (action) {
                                    Constants.Fragment.ADD_TASK -> {
                                        onNewIntent(this)
                                    }

                                    Intent.ACTION_SEND -> {
                                        /* android doesn't call onNewIntent if activity is just created,
                                        so we have to call it manually */
                                        onNewIntent(this)
                                    }
                                }
                                action = null
                                data = null
                                flags = 0
                            }
                        }
                    }
                }, false
            )
        }

        binding.navView.apply {
            setCheckedItem(app.selectedNavigationItemId)
            setNavigationItemSelectedListener {
                // Uncheck selectedNavigationItemId in App and set the previous one
                app.selectedNavigationItemId = it.itemId
                lifecycleScope.launch {
                    delay(200) // add delay
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    delay(300) // add delay
                    when (it.itemId) {
                        R.id.nav_main_tasks -> mainScreen!!.navigateFragment(it.itemId)
                        R.id.nav_completed_tasks -> mainScreen!!.navigateFragment(it.itemId)
                        R.id.nav_deleted_tasks -> mainScreen!!.navigateFragment(it.itemId)

                        R.id.nav_settings_screen -> {
                            fragulaNavController.navigate(R.id.nav_settings_screen)
                            // Deselect selectedNavigationItemId in App and set the previous one
                            app.selectedNavigationItemId =
                                binding.navView.checkedItem?.itemId ?: R.id.nav_main_tasks

                        }

                        R.id.nav_share -> {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.shareText))
                                type = "text/plain"
                            }
                            startActivity(Intent.createChooser(sendIntent, getString(R.string.menu_share)))
                            // Deselect selectedNavigationItemId in App and set the previous one
                            app.selectedNavigationItemId =
                                binding.navView.checkedItem?.itemId ?: R.id.nav_main_tasks
                        }
                    }
                }
                true
            }
        }

        // Animate arrow icon
        DrawerArrowDrawable(this@MainActivity).also { arrow ->
            binding.appBarMainToolbarInclude.toolbar.navigationIcon = arrow.apply {
                color = Color.WHITE
            }
            binding.appBarMainToolbarInclude.toolbar.setNavigationOnClickListener {
                when (arrow.progress) {
                    0f -> binding.drawerLayout.openDrawer(GravityCompat.START)
                    1f -> fragulaNavController.popBackStack()
                }
            }
            swipeController.addOnSwipeListener { position, positionOffset, _ ->
                arrow.progress = if (position > 0) 1f else positionOffset
                if (position > 0) {
                    binding.drawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                        GravityCompat.START,
                    )
                } else {
                    binding.drawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_UNLOCKED,
                        GravityCompat.START,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.apply {
            when (action) {
                Intent.ACTION_SEND -> {
                    val options = bundleOf(
                        Pair(
                            Constants.Fragment.Action,
                            Constants.Fragment.ADD_TASK
                        ),
                        Pair(
                            Constants.TaskItem.TITLE,
                            getStringExtra(Intent.EXTRA_TEXT)
                        )
                    )
                    // The Main coroutine fixes a bug when navHostFragment is null in MainScreen
                    CoroutineScope(Dispatchers.Main).launch {
                        mainScreen!!.navigateFragment(R.id.nav_main_tasks, options)
                    }
                }

                Constants.Fragment.ADD_TASK -> {
                    val options = bundleOf(
                        Pair(
                            Constants.Fragment.Action,
                            Constants.Fragment.ADD_TASK
                        )
                    )
                    // The Main coroutine fixes a bug when navHostFragment is null in MainScreen
                    CoroutineScope(Dispatchers.Main).launch {
                        mainScreen!!.navigateFragment(R.id.nav_main_tasks, options)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // Handle the splash screen transition.
        super.onCreate(savedInstanceState)
        Utils.setupExceptionHandler()
        PreferenceHandler.load(this)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(binding.appBarMainToolbarInclude.toolbar)
        setContentView(binding.root)

        initViews()
    }

    override fun onSupportNavigateUp(): Boolean {
        return fragulaNavController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        _binding = null
        mainScreen = null
        super.onDestroy()
    }
}