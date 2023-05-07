package com.pandacorp.taskui.presentation.di.modules

import android.content.Context
import androidx.room.Room
import com.pandacorp.taskui.data.database.Database
import com.pandacorp.taskui.data.database.TasksDao
import com.pandacorp.taskui.data.repositories.TasksRepositoryImpl
import com.pandacorp.taskui.domain.repositories.TasksRepository
import com.pandacorp.taskui.presentation.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TasksModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): Database =
        Room.databaseBuilder(context, Database::class.java, Constants.Room.NAME).build()

    @Provides
    fun provideTasksDao(database: Database): TasksDao = database.tasksDao()

    @Provides
    fun provideTasksRepository(repository: TasksRepositoryImpl): TasksRepository = repository
}
