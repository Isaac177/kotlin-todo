package com.example.todo.data.repository

import com.example.todo.data.local.UserDao
import com.example.todo.data.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }

    suspend fun register(user: User): Long {
        return userDao.insertUser(user)
    }

    fun getUserById(id: Int): Flow<User?> {
        return userDao.getUserById(id)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }
}
