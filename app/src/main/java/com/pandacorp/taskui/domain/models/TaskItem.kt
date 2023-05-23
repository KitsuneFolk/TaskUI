package com.pandacorp.taskui.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskItem(
    var id: Long = 0,
    var text: String,
    var time: Long? = null,
    var priority: Int? = null,
    var status: Int = MAIN
) : Parcelable {
    companion object {
        // Priority
        const val WHITE = 0
        const val YELLOW = 1
        const val RED = 2

        // Status
        const val MAIN = 0
        const val COMPLETED = 1
        const val DELETED = 2
    }
}