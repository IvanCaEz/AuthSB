package com.ivancaez.auth

import com.ivancaez.auth.domain.UserUpdateRequest
import com.ivancaez.auth.domain.dto.UserDto
import com.ivancaez.auth.domain.dto.UserUpdateRequestDto
import com.ivancaez.auth.domain.entities.UserEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

fun UserEntity.toUserDto() = UserDto(
    id = this.id,
    username = this.username,
    email = this.email,
    image = this.image,
    password = this.password,
    role = this.role
)

fun UserDto.toUserEntity() = UserEntity(
    id = this.id,
    username = this.username,
    email = this.email,
    image = this.image,
    password = this.password,
    role = this.role
)

fun UserUpdateRequestDto.toUserUpdateRequest() = UserUpdateRequest(
    id = this.id,
    username = this.username,
    email = this.email,
    image = this.image,
    password = this.password,
    role = this.role
)

fun UserEntity.mapToUserDetails(): UserDetails {
    return User.builder()
        .username(this.email)
        .password(this.password)
        .roles(this.role.toString())
        .build()
}