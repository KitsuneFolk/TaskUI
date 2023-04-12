package com.pandacorp.taskui.domain.models

import java.io.Serializable

data class TaskItem(
    var id: Long = 0,
    var text: String,
    var time: String? = null,
    var priority: Int? = null,
    var status: Int = MAIN
): Serializable {
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
    //TODO: Remove later files from .gradle in disk C, because I changed the folder to D disk
}