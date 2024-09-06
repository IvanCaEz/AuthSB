package com.ivancaez.auth.services

import com.ivancaez.auth.domain.UserUpdateRequest
import com.ivancaez.auth.domain.entities.UserEntity

interface UserService {
    fun saveUser(userEntity: UserEntity): UserEntity
    fun getUsers(): List<UserEntity>
    fun getUserById(id: Long): UserEntity?
    fun getUserByEmail(email: String): UserEntity?
    fun updateUser(id: Long, userEntity: UserEntity): UserEntity
    fun patchUser(id: Long, userUpdateRequest: UserUpdateRequest): UserEntity
    fun deleteUserById(id: Long)
}