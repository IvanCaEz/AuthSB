package com.ivancaez.auth.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ivancaez.auth.*
import com.ivancaez.auth.config.JwtProperties
import com.ivancaez.auth.domain.auth.AuthRequest
import com.ivancaez.auth.domain.auth.AuthResponse
import com.ivancaez.auth.domain.entities.UserEntity
import com.ivancaez.auth.services.AuthenticationService
import com.ivancaez.auth.services.CustomUserDetailsService
import com.ivancaez.auth.services.TokenService
import com.ivancaez.auth.services.UserService
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
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.test.web.servlet.*
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    @MockkBean val userService: UserService,
    @MockkBean val tokenService: TokenService,
    @MockkBean val authService: AuthenticationService,
    val jwtProperties: JwtProperties
) {

    @MockkBean lateinit var userDetailsService: CustomUserDetailsService

    private val baseUrl = "/v1/users"
    private val authUrl = "/v1/auth"

    lateinit var token: String

    @BeforeEach
    fun beforeEach() {
        every { userService.saveUser(any()) } answers { firstArg() }
        token = createAdminUser()
    }

    @Nested
    @DisplayName("createUser()")
    @TestInstance(Lifecycle.PER_CLASS)
        inner class CreateUserTest {
            @Test
            fun `should create User and save User`() {
                // Given

                val newUser = testUserDtoA()

                val expected = testUserEntityA()

                // When
                mockMvc.post(baseUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(newUser)
                }
                    .andDo { print() }
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
                    header("Authorization", "Bearer $token")
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
                header("Authorization", "Bearer $token")
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
                header("Authorization", "Bearer $token")
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
                header("Authorization", "Bearer $token")
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
                header("Authorization", "Bearer $token")
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
                header("Authorization", "Bearer $token")
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
                header("Authorization", "Bearer $token")
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
                header("Authorization", "Bearer $token")
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
                header("Authorization", "Bearer $token")
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
                header("Authorization", "Bearer $token")
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
                header("Authorization", "Bearer $token")
            }
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }
                }
        }
    }
    @Test
    fun `should return the user with the specified email and 200 when there's a user with provided ID in the database`() {
        // Given
        every { userService.getUserByEmail(any()) } answers { testUserEntityA(999) }
        // When / Then

        mockMvc.get("$baseUrl/email/test@test.com"){
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    json(objectMapper.writeValueAsString(testUserEntityA(999)))
                }
            }
    }

    private fun createAdminUser(): String {
        val adminUser = testAdminUserEntityA()
        every { userDetailsService.loadUserByUsername(any()) } answers {
            adminUser.mapToUserDetails()
        }
        val user = userDetailsService.loadUserByUsername(adminUser.email)

        every { tokenService.generate(any(), any(), any()) } answers {
            "TOKEN"
        }

        every { authService.authenticate(any()) } answers {
            val authRequest = it.invocation.args[0] as AuthRequest
            val token = tokenService.generate(user, Date(System.currentTimeMillis() + jwtProperties.expirationMs))
            AuthResponse(token)
        }

        every { tokenService.extractEmail(any()) } answers { "admin@test.com" }

        // Creates an admin user
        mockMvc.post(baseUrl) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(adminUser)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
        }

        // Authenticate the admin user to get the token
        val authRequest = AuthRequest(adminUser.email, adminUser.password)

        val result = mockMvc.post(authUrl) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(authRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // Extrae el token JWT de la respuesta
        val jsonResponse = result.response.contentAsString
        println(objectMapper.readTree(jsonResponse).get("accessToken").asText())
        return objectMapper.readTree(jsonResponse).get("accessToken").asText()
    }
}