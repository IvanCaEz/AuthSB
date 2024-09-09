package com.ivancaez.auth.services.impl

import com.ivancaez.auth.domain.Role
import com.ivancaez.auth.domain.UserUpdateRequest
import com.ivancaez.auth.domain.entities.UserEntity
import com.ivancaez.auth.repositories.UserRepository
import com.ivancaez.auth.services.FileStorageService
import com.ivancaez.auth.services.UserService
import jakarta.transaction.Transactional
import org.springframework.core.io.FileSystemResource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val fileStorageService: FileStorageService,
    private val encoder: PasswordEncoder
    ): UserService {

    override fun saveUser(userEntity: UserEntity): UserEntity {
        require(userEntity.id == null) { "User ID must be null" }
        val userEncoded = userEntity.copy(password = encoder.encode(userEntity.password))
        return userRepository.save(userEncoded)
    }

    override fun getUsers(): List<UserEntity> {
        return userRepository.findAll()
    }

    override fun getUserById(id: Long): UserEntity? {
        return userRepository.findByIdOrNull(id)
    }

    override fun getUserByEmail(email: String): UserEntity? {
        return userRepository.findByEmail(email)
    }

    @Transactional
    override fun updateUser(id: Long, userEntity: UserEntity): UserEntity {
        val existingUser = userRepository.findByIdOrNull(id)
        checkNotNull(existingUser) { "User with ID $id not found" }

        val currentUser = getCurrentAuthenticatedUser()

        val roleToSet = if (currentUser.role == Role.ADMIN) {
            userEntity.role
        } else {
            existingUser.role
        }

        val normalisedUser = userEntity.copy(
            id = id,
            password = encoder.encode(userEntity.password),
            role = roleToSet
        )
        return userRepository.save(normalisedUser)
    }

    @Transactional
    override fun patchUser(id: Long, userUpdateRequest: UserUpdateRequest): UserEntity {
        val existingUser = userRepository.findByIdOrNull(id)
        checkNotNull(existingUser) { "User with ID $id not found" }

        val currentUser = getCurrentAuthenticatedUser()

        val roleToSet = if (currentUser.role == Role.ADMIN) {
            userUpdateRequest.role
        } else {
            existingUser.role
        }

        val patchedUser = existingUser.copy(
            username = userUpdateRequest.username ?: existingUser.username,
            email = userUpdateRequest.email ?: existingUser.email,
            image = userUpdateRequest.image ?: existingUser.image,
            password = userUpdateRequest.password ?: existingUser.password,
            role = roleToSet ?: existingUser.role
        )
        return userRepository.save(patchedUser)
    }

    @Transactional
    override fun uploadUserImage(id: Long, image: MultipartFile): UserEntity {
        val existingUser = userRepository.findByIdOrNull(id)
        checkNotNull(existingUser) { "User with ID $id not found" }

        existingUser.image.let { previousImagePath ->
            if (previousImagePath != "uploads/default.png") fileStorageService.deleteFile(previousImagePath)
        }

        val imagePath = fileStorageService.saveFile(image)

        val updatedUser = existingUser.copy(image = imagePath)
        return userRepository.save(updatedUser)
    }

    override fun deleteUserById(id: Long) {
        check(userRepository.existsById(id)) { "User with ID $id not found" }
        return userRepository.deleteById(id)
    }

    private fun getCurrentAuthenticatedUser(): UserEntity {
        val authentication = SecurityContextHolder.getContext().authentication
        return userRepository.findByEmail(authentication.name) ?: throw IllegalArgumentException("Authenticated user not found")
    }

}