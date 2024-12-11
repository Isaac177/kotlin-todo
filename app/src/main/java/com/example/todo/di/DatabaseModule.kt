package com.example.todo.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.todo.data.local.TodoDatabase
import com.example.todo.data.local.TodoDao
import com.example.todo.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideTodoDatabase(
        @ApplicationContext context: Context
    ): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: TodoDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideTodoDao(database: TodoDatabase): TodoDao {
        return database.todoDao()
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }
}
