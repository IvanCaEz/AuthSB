package com.ivancaez.auth.services.impl

import com.ivancaez.auth.domain.UserUpdateRequest
import com.ivancaez.auth.domain.entities.UserEntity
import com.ivancaez.auth.repositories.UserRepository
import com.ivancaez.auth.services.UserService
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository): UserService {

    override fun saveUser(userEntity: UserEntity): UserEntity {
        require(userEntity.id == null) { "User ID must be null" }
        return userRepository.save(userEntity)
    }

    override fun getUsers(): List<UserEntity> {
        return userRepository.findAll()
    }

    override fun getUserById(id: Long): UserEntity? {
        return userRepository.findByIdOrNull(id)
    }

    @Transactional
    override fun updateUser(id: Long, userEntity: UserEntity): UserEntity {
        check(userRepository.existsById(id)) { "User with ID $id not found" }
        val normalisedUser = userEntity.copy(id = id)
        return userRepository.save(normalisedUser)
    }

    @Transactional
    override fun patchUser(id: Long, userUpdateRequest: UserUpdateRequest): UserEntity {
        val existingUser = userRepository.findByIdOrNull(id)
        checkNotNull(existingUser) { "User with ID $id not found" }
        val patchedUser = existingUser.copy(
            username = userUpdateRequest.username ?: existingUser.username,
            email = userUpdateRequest.email ?: existingUser.email,
            image = userUpdateRequest.image ?: existingUser.image,
            password = userUpdateRequest.password ?: existingUser.password
        )
        return userRepository.save(patchedUser)
    }

    override fun deleteUserById(id: Long) {
        check(userRepository.existsById(id)) { "User with ID $id not found" }
        return userRepository.deleteById(id)
    }


}