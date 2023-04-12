package com.pandacorp.taskui.presentation.utils

import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Utils {
    companion object {
        const val TAG = "CustomUtils"
        
        //This function is needed for coroutines logs work on Xiaomi devices.
        fun setupExceptionHandler() {
            Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
                throw (throwable)
                
            }
        }
        
        //TODO: Add later hiding the FAB on scroll and use this function
        /**
         * This function checks if user removed item when floating action button was hided, and if so, it shows it again, it fixes the bug when fab could been not seen on a screen and recyclerview wasn't scrollable.
         */
        fun handleShowingFAB(recyclerView: RecyclerView, fab: FloatingActionButton) {
            if (!recyclerView.canScrollVertically(1) || !recyclerView.canScrollVertically(-1)) {
                val layoutParams: ViewGroup.LayoutParams = fab.layoutParams
                if (layoutParams is CoordinatorLayout.LayoutParams) {
                    val behavior = layoutParams.behavior
                    if (behavior is HideBottomViewOnScrollBehavior) {
                        if (fab.visibility == View.VISIBLE) {
                            behavior.slideUp(fab)
                        } else {
                            behavior.slideDown(fab)
                        }
                    }
                }
            }
        }
    }
}