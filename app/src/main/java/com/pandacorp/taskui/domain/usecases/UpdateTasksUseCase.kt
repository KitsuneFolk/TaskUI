package com.pandacorp.taskui.domain.usecases

import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.domain.repositories.TasksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateTasksUseCase @Inject constructor(private val repository: TasksRepository) {
    suspend operator fun invoke(items: List<TaskItem>) = withContext(Dispatchers.IO) {
        repository.updateItems(items)
    }
}