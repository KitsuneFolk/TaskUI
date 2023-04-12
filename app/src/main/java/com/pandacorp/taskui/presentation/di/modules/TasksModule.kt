package com.pandacorp.taskui.presentation.di.modules

import android.content.Context
import androidx.room.Room
import com.pandacorp.taskui.data.database.Database
import com.pandacorp.taskui.data.database.TasksDao
import com.pandacorp.taskui.data.repositories.TasksRepositoryImpl
import com.pandacorp.taskui.domain.repositories.TasksRepository
import com.pandacorp.taskui.domain.usecases.*
import com.pandacorp.taskui.presentation.utils.Constans
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object TasksModule {
    
    // @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): Database =
        Room.databaseBuilder(context, Database::class.java, Constans.Room.NAME).build()
    
    // @Singleton
    @Provides
    fun provideTasksDao(database: Database): TasksDao = database.tasksDao()
    
    // @Singleton
    // @Provides
    // fun provideTasksRepositoryImpl(
    //     dao: TasksDao,
    //     mapper: TasksMapper
    // ): TasksRepository = TasksRepositoryImpl(dao = dao, mapper = mapper)
    //
    @Provides
    fun provideTasksRepository(repository: TasksRepositoryImpl): TasksRepository = repository
    
    // @Singleton
    @Provides
    fun provideGetTasksUseCase(repository: TasksRepository): GetTasksUseCase =
        GetTasksUseCase(repository = repository)
    
    // @Singleton
    @Provides
    fun provideAddTaskUseCase(repository: TasksRepository): AddTaskUseCase =
        AddTaskUseCase(repository = repository)
    
    // @Singleton
    @Provides
    fun provideRemoveTaskUseCase(repository: TasksRepository): RemoveTaskUseCase =
        RemoveTaskUseCase(repository = repository)
    
    // @Singleton
    @Provides
    fun provideUpdateTaskUseCase(repository: TasksRepository): UpdateTaskUseCase =
        UpdateTaskUseCase(repository = repository)
    
    // @Singleton
    @Provides
    fun provideRemoveAllUseCase(repository: TasksRepository): RemoveAllTasksUseCase =
        RemoveAllTasksUseCase(repository = repository)
    
    // @Singleton
    // @Provides
    // fun provideMainTasksViewModel(
    //     getTasksUseCase: GetTasksUseCase,
    //     addTaskUseCase: AddTaskUseCase,
    //     removeAllUseCase: RemoveAllTasksUseCase,
    //     updateTaskUseCase: UpdateTaskUseCase,
    //     updateTasksUseCase: UpdateTasksUseCase
    // ): MainTasksViewModel =
    //     MainTasksViewModel(
    //             getItemsUseCase = getTasksUseCase,
    //             addItemUseCase = addTaskUseCase,
    //             updateItemUseCase = updateTaskUseCase,
    //             removeAllUseCase = removeAllUseCase,
    //             updateItemsUseCase = updateTasksUseCase,
    //     )
    //
    // // @Singleton
    // @Provides
    // fun provideCompletedTasksViewModel(
    //     getTasksUseCase: GetTasksUseCase,
    //     removeAllUseCase: RemoveAllTasksUseCase,
    //     updateTasksUseCase: UpdateTasksUseCase,
    //     updateTaskUseCase: UpdateTaskUseCase
    // ): CompletedTasksViewModel =
    //     CompletedTasksViewModel(
    //             getItemsUseCase = getTasksUseCase,
    //             updateItemUseCase = updateTaskUseCase,
    //             updateItemsUseCase = updateTasksUseCase,
    //             removeAllUseCase = removeAllUseCase
    //     )
    //
    // // @Singleton
    // @Provides
    // fun provideDeletedTasksViewModel(
    //     getTasksUseCase: GetTasksUseCase,
    //     removeAllUseCase: RemoveAllTasksUseCase,
    //     updateTaskUseCase: UpdateTaskUseCase
    // ): DeletedTasksViewModel =
    //     DeletedTasksViewModel(
    //             getItemsUseCase = getTasksUseCase,
    //             updateItemUseCase = updateTaskUseCase,
    //             removeAllUseCase = removeAllUseCase
    //     )
    //
    
}
