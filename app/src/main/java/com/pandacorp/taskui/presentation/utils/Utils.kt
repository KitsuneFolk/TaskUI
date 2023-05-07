package com.pandacorp.taskui.presentation.utils

import android.animation.AnimatorInflater
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import com.pandacorp.taskui.R
import java.io.Serializable

class Utils {

    companion object {
        const val TAG = "CustomUtils"
        //This function is needed for coroutines logs work on Xiaomi devices.
        fun setupExceptionHandler() {
            Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
                throw (throwable)
            }
        }

        /**
         * Call this function to add the size decreasing on a touch
         */
        fun addDecreaseSizeOnTouch(view: View) {
            view.stateListAnimator = AnimatorInflater.loadStateListAnimator(view.context, R.animator.decrease_size_normal_animator)
        }
    }
}

/**
 * A compatibility wrapper around Intent's `getSerializableExtra()` method that allows
 * developers to get a Serializable extra from an Intent object regardless of the version of
 * Android running on the device.
 *
 * @param name The name of the extra to retrieve.
 * @param clazz The class of the extra to retrieve.
 * @return The Serializable extra with the specified name and class, or null if it does not exist.
 */
inline fun <reified T : Serializable> Intent.getSerializableExtraSupport(
    name: String,
    clazz: Class<T>
): T? {
    val extra = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(name, clazz)
    } else {
        @Suppress("DEPRECATION") getSerializableExtra(name) as? T
    }
    if (extra is T) {
        return extra
    }
    return null
}

/**
 * A compatibility wrapper around PackageManager's `getPackageInfo()` method that allows
 * developers to use either the old flag-based API or the new enum-based API depending on the
 * version of Android running on the device.
 *
 * @param packageName The name of the package for which to retrieve package information.
 * @param flags Additional flags to control the behavior of the method.
 * @return A PackageInfo object containing information about the specified package.
 */
fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
    }