package com.ivancaez.auth

import com.ivancaez.auth.domain.UserUpdateRequest
import com.ivancaez.auth.domain.dto.UserDto
import com.ivancaez.auth.domain.dto.UserUpdateRequestDto
import com.ivancaez.auth.domain.entities.UserEntity

fun UserEntity.toUserDto() = UserDto(
    id = this.id,
    username = this.username,
    email = this.email,
    image = this.image,
    password = this.password
)

fun UserDto.toUserEntity() = UserEntity(
    id = this.id,
    username = this.username,
    email = this.email,
    image = this.image,
    password = this.password
)

fun UserUpdateRequestDto.toUserUpdateRequest() = UserUpdateRequest(
    id = this.id,
    username = this.username,
    email = this.email,
    image = this.image,
    password = this.password
)