package com.ivancaez.auth.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ivancaez.auth.domain.entities.UserEntity
import com.ivancaez.auth.services.UserService
import com.ivancaez.auth.testUserDtoA
import com.ivancaez.auth.testUserEntityA
import com.ivancaez.auth.testUserEntityB
import com.ivancaez.auth.testUserUpdateRequestDto
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    @MockkBean val userService: UserService
) {

    private val baseUrl = "/v1/users"

    @BeforeEach
    fun beforeEach() {
        every { userService.saveUser(any()) } answers { firstArg() }
    }

    @Nested
    @DisplayName("createUser()")
    @TestInstance(Lifecycle.PER_CLASS)
        inner class CreateUserTest {
            @Test
            fun `should create User and save User`() {
                // Given

                val newUser = testUserDtoA()

                val expected = UserEntity(
                    id = null,
                    username = "Test User",
                    email = "test@test.com",
                    image = "test.jpg",
                    password = "test"
                )

                // When
                val postRequest = mockMvc.post(baseUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(newUser)

                }.andDo { print() }
                // Then
                verify { userService.saveUser(eq(expected)) }
            }

            @Test
            fun `should create User and return IS CREATED status`() {
                // Given

                val newUser = testUserDtoA()

                // When
                val postRequest = mockMvc.post(baseUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(newUser)
                }
                // Then
                postRequest
                    .andDo { print() }
                    .andExpect {
                        status { isCreated() }
                    }
            }
        @Test
        fun `should return 400 status when IllegalException is thrown`() {
            // Given
            every { userService.saveUser(any()) } throws IllegalArgumentException()

            val newUser = testUserDtoA()

            // When
            val postRequest = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                accept = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(newUser)
            }
            // Then
            postRequest
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                }
        }
        }


    @Nested
    @DisplayName("getUsers()")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class GetUsersTest {
        @Test
        fun `should return an empty list when there's no user data in the database`() {
            // Given
            every { userService.getUsers() } answers { emptyList() }
            // When / Then
            mockMvc.get(baseUrl){
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
            }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { json("[]") }
                }
        }
        @Test
        fun `should return list of users and HTTP 200 when there's user data in the database`() {
            // Given
            every { userService.getUsers() } answers { listOf(testUserEntityA(1)) }
            // When / Then
            mockMvc.get(baseUrl){
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
            }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        //jsonPath("$[0].id") {value(1)}
                        json(objectMapper.writeValueAsString(listOf(testUserEntityA(1))))}
                }
        }
    }


    @Nested
    @DisplayName("getUserById()")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class GetUserByIdTest {
        @Test
        fun `should return 404 when there's no user with provided ID in the databse`() {
            // Given
            every { userService.getUserById(any()) } answers { null }
            // When / Then
            mockMvc.get("$baseUrl/222"){
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
            }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
        }

        @Test
        fun `should return the user with the specified ID and 200 when there's a user with provided ID in the databse`() {
            // Given
            every { userService.getUserById(any()) } answers { testUserEntityA(999) }
            // When / Then
            mockMvc.get("$baseUrl/999"){
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
            }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        json(objectMapper.writeValueAsString(testUserEntityA(999)))
                    }
                }
        }

    }

    @Nested
    @DisplayName("putUser()")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class UpdateUserTest {
        @Test
        fun `should full update an User and return HTTP 200 and the updated user`() {
            // Given
            every { userService.updateUser(any(), any()) } answers { secondArg() }

            val userToUpdate = testUserDtoA(999)

            // When / Then
            val putRequest = mockMvc.put("$baseUrl/999") {
                contentType = MediaType.APPLICATION_JSON
                accept = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(userToUpdate)
            }
            putRequest.andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        json(objectMapper.writeValueAsString(userToUpdate))
                    }
                }
        }
        @Test
        fun `should return HTTP 400 on IllegalArgumentException on full update`() {
            // Given
            every { userService.updateUser(any(), any()) } throws IllegalArgumentException()

            val userToUpdate = testUserDtoA(999)

            // When / Then
            val putRequest = mockMvc.put("$baseUrl/999") {
                contentType = MediaType.APPLICATION_JSON
                accept = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(userToUpdate)
            }
            putRequest.andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                }
        }

    }


    @Nested
    @DisplayName("patchUser()")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class PatchUserTest {
        @Test
        fun `should partial update an User and return HTTP 200 and the updated user`() {
            // Given
            every { userService.patchUser(any(), any()) } answers { testUserEntityB(999) }

            val userToUpdate = testUserUpdateRequestDto(999)

            // When / Then
            val patchRequest = mockMvc.patch("$baseUrl/999") {
                contentType = MediaType.APPLICATION_JSON
                accept = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(userToUpdate)
            }
            patchRequest.andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        json(objectMapper.writeValueAsString(userToUpdate))
                    }
                }
        }
        @Test
        fun `should return HTTP 400 on IllegalArgumentException on partial update`() {
            // Given
            every { userService.patchUser(any(), any()) } throws IllegalArgumentException()

            val userToUpdate = testUserUpdateRequestDto(999)

            // When / Then
            val patchRequest = mockMvc.patch("$baseUrl/999") {
                contentType = MediaType.APPLICATION_JSON
                accept = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(userToUpdate)
            }
            patchRequest.andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                }
        }

    }

    @Nested
    @DisplayName("deleteUserById()")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class DeleteUserByIdTest {
        @Test
        fun `should delete an User and return HTTP 204 on successful delete`() {
            // Given
            every { userService.deleteUserById(any()) } answers {  }
            // When / Then
            mockMvc.delete("$baseUrl/999"){
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
            }
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }
                }
        }
    }
}