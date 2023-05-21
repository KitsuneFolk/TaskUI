package com.pandacorp.taskui.presentation.utils

import android.animation.AnimatorInflater
import android.view.View
import com.pandacorp.taskui.R

class Utils {
    companion object {
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
            view.stateListAnimator =
                AnimatorInflater.loadStateListAnimator(view.context, R.animator.decrease_size_normal_animator)
        }
    }
}