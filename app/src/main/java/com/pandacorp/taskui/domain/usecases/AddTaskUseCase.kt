package com.pandacorp.taskui.domain.usecases

import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.domain.repositories.TasksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(private val repository: TasksRepository) {
    suspend operator fun invoke(item: TaskItem): Long = withContext(Dispatchers.IO) {
        return@withContext repository.insert(item)
    }
}