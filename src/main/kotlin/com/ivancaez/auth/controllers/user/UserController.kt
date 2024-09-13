package com.ivancaez.auth.controllers.user

import com.ivancaez.auth.domain.dto.UserDto
import com.ivancaez.auth.domain.dto.UserUpdateRequestDto
import com.ivancaez.auth.services.UserService
import com.ivancaez.auth.toUserDto
import com.ivancaez.auth.toUserEntity
import com.ivancaez.auth.toUserUpdateRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.lang.IllegalArgumentException

@RestController
@RequestMapping("/v1/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun createUser(@RequestBody userDto: UserDto): ResponseEntity<UserDto> {
        return try {
            val createdUser =  userService.saveUser(
                userDto.toUserEntity()
            ).toUserDto()
            ResponseEntity(createdUser, HttpStatus.CREATED)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping
    fun getUsers(): ResponseEntity<List<UserDto>> {
        val users = userService.getUsers().map { it.toUserDto() }
        return ResponseEntity(users, HttpStatus.OK)
    }

    @PreAuthorize("@securityService.canAccessUserId(#id)")
    @GetMapping(path = ["/{id}"])
    fun getUserById(@PathVariable("id") id: Long): ResponseEntity<UserDto> {
        val foundUser = userService.getUserById(id)?.toUserDto()
        return foundUser?.let {
            ResponseEntity(it, HttpStatus.OK)
        } ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PreAuthorize("@securityService.canAccessUserEmail(#email)")
    @GetMapping(path = ["/email/{email}"])
    fun getUserByEmail(@PathVariable("email") email: String): ResponseEntity<UserDto> {
        val foundUser = userService.getUserByEmail(email)?.toUserDto()
        return foundUser?.let {
            ResponseEntity(it, HttpStatus.OK)
        } ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PreAuthorize("@securityService.canAccessUserId(#id)")
    @PutMapping(path = ["/{id}"])
    fun updateUser(@PathVariable("id") id: Long, @RequestBody userDto: UserDto
    ): ResponseEntity<UserDto> {
        return try {
            val updatedUser = userService.updateUser(id, userDto.toUserEntity())
            ResponseEntity(updatedUser.toUserDto(), HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }
    @PreAuthorize("@securityService.canAccessUserId(#id)")
    @PatchMapping(path = ["/{id}"])
    fun patchUser(@PathVariable("id") id: Long, @RequestBody userUpdateRequest: UserUpdateRequestDto
    ): ResponseEntity<UserDto> {
        return try {
            val patchedUser = userService.patchUser(id, userUpdateRequest.toUserUpdateRequest())
            ResponseEntity(patchedUser.toUserDto(), HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }


    @PreAuthorize("@securityService.canAccessUserId(#id)")
    @PatchMapping(path = ["/{id}/upload"])
    fun uploadUserImage(
        @PathVariable("id") id: Long,
        @RequestParam("image") image: MultipartFile
    ): ResponseEntity<UserDto> {
        return try {
            val updatedUser = userService.uploadUserImage(id, image)
            ResponseEntity(updatedUser.toUserDto(), HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @PreAuthorize("@securityService.canAccessUserId(#id)")
    @DeleteMapping(path = ["/{id}"])
    fun deleteUserById(@PathVariable("id") id: Long): ResponseEntity<Unit> {
        userService.deleteUserById(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

}