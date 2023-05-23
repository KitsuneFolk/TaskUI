package com.pandacorp.taskui.presentation.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.taskui.presentation.ui.TasksAdapter

class CustomItemTouchHelper(
    context: Context,
    private val key: Constants.ITHKey,
    private val onTouchListener: OnTouchListener,
    swipeDirs: Int
) :
    ItemTouchHelper.SimpleCallback(0, swipeDirs) {

    private val isRoundedCornersEnabled: Boolean = true // Should we round the corners on a swipe or no

    interface OnTouchListener {
        fun onSwipedStart(viewHolder: RecyclerView.ViewHolder, direction: Int, key: Constants.ITHKey)
        fun onSwipedEnd(viewHolder: RecyclerView.ViewHolder, direction: Int, key: Constants.ITHKey)
    }

    companion object {
        private const val CORNERS_RADIUS = 15f
    }

    private var isRoundCorners: Boolean? = null
    private var isSwipingEndToStart: Boolean? = null

    private val backgroundColor by lazy {
        val tv = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorBackground, tv, true)
        tv.data
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.START -> onTouchListener.onSwipedStart(viewHolder, direction, key)
            ItemTouchHelper.END -> onTouchListener.onSwipedEnd(viewHolder, direction, key)
            else -> throw IllegalArgumentException("Invalid swipe direction: $direction")
        }
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val foregroundView = getForegroundView(viewHolder)

        setBackgroundView(viewHolder, dX)
        setCorners(dX, isCurrentlyActive, foregroundView)

        getDefaultUIUtil().onDraw(
            c, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive
        )
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            val foregroundView = viewHolder.itemView
            getDefaultUIUtil().onSelected(foregroundView)
        }
    }

    override fun onChildDrawOver(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val foregroundView = getForegroundView(viewHolder)

        getDefaultUIUtil().onDraw(
            c, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive
        )
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView = getForegroundView(viewHolder)

        setCorners(0f, true, foregroundView)

        getDefaultUIUtil().clearView(foregroundView)
    }

    /**
     * Set rounded corners if dX < 0 (for swipe from end to start) or if dY > 0 (for swipe from start to end) and
     * remove if dx == 0
     */
    private fun setCorners(
        dX: Float,
        isCurrentlyActive: Boolean,
        foregroundView: View
    ) {
        if (!isRoundedCornersEnabled) return
        if (dX < 0) {
            // Needed to set the corners case when user swipes too fast, thus dX is not 0,
            if (isSwipingEndToStart == false) isRoundCorners = true

            if (isRoundCorners == true || isRoundCorners == null) {
                isRoundCorners = false
                isSwipingEndToStart = true
                val shapeDrawable = ShapeDrawable(
                    RoundRectShape(
                        floatArrayOf(
                            0f,
                            0f,
                            CORNERS_RADIUS,
                            CORNERS_RADIUS,
                            CORNERS_RADIUS,
                            CORNERS_RADIUS,
                            0f,
                            0f
                        ),
                        null,
                        null
                    )
                )
                shapeDrawable.paint.color = backgroundColor
                foregroundView.background = shapeDrawable
                ValueAnimator.ofFloat(0f, CORNERS_RADIUS).apply {
                    duration = 250
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        shapeDrawable.shape = RoundRectShape(
                            floatArrayOf(0f, 0f, value, value, value, value, 0f, 0f),
                            null,
                            null
                        )
                        shapeDrawable.paint.color = backgroundColor
                        foregroundView.background = shapeDrawable
                    }
                }.start()
            }
        } else if (dX > 0) {
            // Needed to set the corners case when user swipes too fast, thus dX is not 0,
            if (isSwipingEndToStart == true) isRoundCorners = true

            if (isRoundCorners == true || isRoundCorners == null) {
                isRoundCorners = false
                isSwipingEndToStart = false
                val shapeDrawable = ShapeDrawable(
                    RoundRectShape(
                        floatArrayOf(
                            CORNERS_RADIUS,
                            CORNERS_RADIUS,
                            0f,
                            0f,
                            0f,
                            0f,
                            CORNERS_RADIUS,
                            CORNERS_RADIUS
                        ),
                        null,
                        null
                    )
                )
                shapeDrawable.paint.color = backgroundColor
                foregroundView.background = shapeDrawable
                ValueAnimator.ofFloat(0f, CORNERS_RADIUS).apply {
                    duration = 250
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        shapeDrawable.shape = RoundRectShape(
                            floatArrayOf(value, value, 0f, 0f, 0f, 0f, value, value),
                            null,
                            null
                        )
                        shapeDrawable.paint.color = backgroundColor
                        foregroundView.background = shapeDrawable
                    }
                }.start()
            }
        } else {
            if (!isCurrentlyActive) return // needed to remove bug when rounded corners added and removed twice or trice
            if (isRoundCorners == false) { // check if the else branch called the first time
                isRoundCorners = true
                val shapeDrawable = ShapeDrawable(
                    RoundRectShape(
                        floatArrayOf(
                            0f,
                            0f,
                            CORNERS_RADIUS,
                            CORNERS_RADIUS,
                            CORNERS_RADIUS,
                            CORNERS_RADIUS,
                            0f,
                            0f
                        ),
                        null,
                        null
                    )
                )
                shapeDrawable.paint.color = backgroundColor
                foregroundView.background = shapeDrawable

                ValueAnimator.ofFloat(CORNERS_RADIUS, 0f).apply {
                    duration = 250
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        val values =
                            when (isSwipingEndToStart) {
                                true -> floatArrayOf(0f, 0f, value, value, value, value, 0f, 0f)
                                false -> floatArrayOf(value, value, 0f, 0f, 0f, 0f, value, value)
                                else -> return@addUpdateListener
                            }
                        shapeDrawable.shape = RoundRectShape(
                            values,
                            null,
                            null
                        )
                        foregroundView.background = shapeDrawable
                    }
                }.start()
            }
        }

    }

    private fun getForegroundView(viewHolder: RecyclerView.ViewHolder) = when (viewHolder) {
        is TasksAdapter.MainTaskViewHolder -> viewHolder.binding.foreground
        is TasksAdapter.CompletedTaskViewHolder -> viewHolder.binding.foreground
        else -> throw IllegalArgumentException("Invalid viewHolder: $viewHolder")
    }

    /**
     * Set background of the item if we have 2 swipe directions
     */
    private fun setBackgroundView(viewHolder: RecyclerView.ViewHolder, dX: Float) = when (viewHolder) {
        is TasksAdapter.MainTaskViewHolder ->
            if (dX < 0) {
                viewHolder.binding.includeBackground.startItemBackground.visibility = View.GONE
                viewHolder.binding.includeBackground.endItemBackground.visibility = View.VISIBLE
            } else if (dX > 0) {
                viewHolder.binding.includeBackground.endItemBackground.visibility = View.GONE
                viewHolder.binding.includeBackground.startItemBackground.visibility = View.VISIBLE
            } else { /* skip */
            }

        is TasksAdapter.CompletedTaskViewHolder ->
            if (dX < 0) {
                viewHolder.binding.includeBackground.startItemBackground.visibility = View.GONE
                viewHolder.binding.includeBackground.endItemBackground.visibility = View.VISIBLE
            } else if (dX > 0) {
                viewHolder.binding.includeBackground.endItemBackground.visibility = View.GONE
                viewHolder.binding.includeBackground.startItemBackground.visibility = View.VISIBLE
            } else { /* skip */
            }

        else -> throw IllegalArgumentException("Invalid viewHolder: $viewHolder")
    }
}