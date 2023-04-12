package com.pandacorp.taskui.domain.usecases

import com.pandacorp.taskui.domain.repositories.TasksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoveAllTasksUseCase @Inject constructor(private val repository: TasksRepository) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        repository.removeAll()
    }
}