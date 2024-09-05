package com.ivancaez.auth

import com.ivancaez.auth.domain.UserUpdateRequest
import com.ivancaez.auth.domain.dto.UserDto
import com.ivancaez.auth.domain.dto.UserUpdateRequestDto
import com.ivancaez.auth.domain.entities.UserEntity

fun testUserDtoA(id: Long? = null) = UserDto(
    id = id,
    username = "Test User",
    email = "test@test.com",
    image = "test.jpg",
    password = "test"
)
fun testUserEntityA(id: Long? = null) = UserEntity(
    id = id,
    username = "Test User",
    email = "test@test.com",
    image = "test.jpg",
    password = "test"
)

fun testUserEntityB(id: Long? = null) = UserEntity(
    id = id,
    username = "newUsername",
    image = "newImage.jpg",
    password = "newPassword",
    email = "newEmail"
)

fun testUserUpdateRequestDto(id: Long? = null) = UserUpdateRequestDto(
    id = id,
    username = "newUsername",
    image = "newImage.jpg",
    password = "newPassword",
    email = "newEmail"
)

fun testUserUpdateRequest(id: Long? = null) = UserUpdateRequest(
    id = id,
    username = "newUsername",
    image = "newImage.jpg",
    password = "newPassword",
    email = "newEmail"
)
