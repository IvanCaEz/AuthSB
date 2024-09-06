package com.ivancaez.auth

import com.ivancaez.auth.domain.Role
import com.ivancaez.auth.domain.UserUpdateRequest
import com.ivancaez.auth.domain.dto.UserDto
import com.ivancaez.auth.domain.dto.UserUpdateRequestDto
import com.ivancaez.auth.domain.entities.UserEntity

fun testAdminUserDtoA(id: Long? = null) = UserDto(
    id = id,
    username = "Test Admin User",
    email = "admin@test.com",
    image = "test.jpg",
    password = "admin",
    role = Role.ADMIN
)

fun testAdminUserEntityA(id: Long? = null) = UserEntity(
    id = id,
    username = "Test Admin User",
    email = "admin@test.com",
    image = "test.jpg",
    password = "admin",
    role = Role.ADMIN
)

fun testUserDtoA(id: Long? = null) = UserDto(
    id = id,
    username = "Test User",
    email = "test@test.com",
    image = "test.jpg",
    password = "test",
    role = Role.USER
)
fun testUserEntityA(id: Long? = null) = UserEntity(
    id = id,
    username = "Test User",
    email = "test@test.com",
    image = "test.jpg",
    password = "test",
    role = Role.USER
)

fun testUserEntityB(id: Long? = null) = UserEntity(
    id = id,
    username = "newUsername",
    image = "newImage.jpg",
    password = "newPassword",
    email = "newEmail",
    role = Role.USER
)

fun testUserUpdateRequestDto(id: Long? = null) = UserUpdateRequestDto(
    id = id,
    username = "newUsername",
    image = "newImage.jpg",
    password = "newPassword",
    email = "newEmail",
    role = Role.ADMIN
)

fun testUserUpdateRequest(id: Long? = null) = UserUpdateRequest(
    id = id,
    username = "newUsername",
    image = "newImage.jpg",
    password = "newPassword",
    email = "newEmail",
    role = Role.ADMIN
)
