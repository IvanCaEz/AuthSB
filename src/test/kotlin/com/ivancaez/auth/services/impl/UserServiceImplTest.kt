package com.ivancaez.auth.services.impl

import com.ivancaez.auth.domain.UserUpdateRequest
import com.ivancaez.auth.domain.entities.UserEntity
import com.ivancaez.auth.repositories.UserRepository
import com.ivancaez.auth.services.TokenService
import com.ivancaez.auth.testUserEntityA
import com.ivancaez.auth.testUserEntityB
import com.ivancaez.auth.testUserUpdateRequest
import com.ninjasquad.springmockk.MockkBean
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootTest
@Transactional
class UserServiceImplTest @Autowired constructor(
    private val underTest: UserServiceImpl,
    private val userRepository: UserRepository,
    private val encoder: BCryptPasswordEncoder
) {

    @MockkBean
    private lateinit var tokenService: TokenService

    @Nested
    @DisplayName("postRequest()")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PostRequestTest {
        @Test
        fun `should save and persist User in the database`() {
            // Given
            val savedUser = underTest.saveUser(testUserEntityA())
            // When
            val recalledUser = userRepository.findByIdOrNull(savedUser.id!!)
            // Then
            assertThat(savedUser.id).isNotNull()
            assertThat(recalledUser).isNotNull()
            // As the password is encoded, we have to check that others fields are equal
            assertThat(recalledUser?.username).isEqualTo(testUserEntityA().username)
            assertThat(recalledUser?.email).isEqualTo(testUserEntityA().email)
            assertThat(recalledUser?.image).isEqualTo(testUserEntityA().image)
            assertThat(recalledUser?.role).isEqualTo(testUserEntityA().role)
            // Then that the password matches
            assertThat(encoder.matches(testUserEntityA().password, recalledUser?.password)).isTrue()
        }
    }

    @Nested
    @DisplayName("getRequest()")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetRequestTests {
        @Test
        fun `returns empty list when there's no users in the database`() {
            // Given
            // When
            val result = underTest.getUsers()

            // Then
            assertThat(result).isEmpty()
        }
        @Test
        fun `returns a user list when there's users in the database`() {
            // Given
            val savedUser = userRepository.save(testUserEntityA())
            val expected = listOf(savedUser)

            // When
            val result = underTest.getUsers()

            // Then
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `returns a null when user not present in the database`() {
            // Given
            // When
            val result = underTest.getUserById(674)
            // Then
            assertThat(result).isNull()
        }

        @Test
        fun `returns user when user is present in the database`() {
            // Given
            val savedUser = userRepository.save(testUserEntityA())
            // When
            val result = underTest.getUserById(savedUser.id!!)
            // Then
            assertThat(result).isEqualTo(savedUser)
        }

        @Test
        fun `an user with an ID throws IllegalArgumentException`() {
            // Given
            val existingUser = testUserEntityA(777)

            // When / Then
            assertThrows<IllegalArgumentException> {
                underTest.saveUser(existingUser)
            }
        }
    }


    @Nested
    @DisplayName("putRequest()")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PutRequestTests {
        @Test
        fun `put request successfully updates an user in the database`() {
            // Given
            val existingUser = userRepository.save(testUserEntityA())
            val existingUserId = existingUser.id!!

            val updatedUser = testUserEntityB(id = existingUserId)
            // When
            val result = underTest.updateUser(existingUserId, updatedUser)
            val retrievedUser = userRepository.findByIdOrNull(existingUserId)
            // Then
            assertThat(result).isEqualTo(updatedUser)
            assertThat(retrievedUser).isNotNull()
            assertThat(retrievedUser).isEqualTo(updatedUser)
        }

        @Test
        fun `put request throws IllegalStateException when user does not exists in the database`() {
            // Given
            val nonExistingUserId = 675L
            val updatedUser = testUserEntityB(id = nonExistingUserId)
            // When / Then
            assertThrows<IllegalStateException> {
                underTest.updateUser(nonExistingUserId, updatedUser)
            }
        }
    }


    @Nested
    @DisplayName("patchRequest()")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PatchRequestTests {
        @Test
        fun `patch request throws IllegalStateException when user does not exists in the database`() {
            // Given
            val nonExistingUserId = 675L
            val patchedUser = testUserUpdateRequest(id = nonExistingUserId)
            // When / Then
            assertThrows<IllegalStateException> {
                underTest.patchUser(nonExistingUserId, patchedUser)
            }
        }

        @Test
        fun `patch request does not update user when all values are null`() {
            // Given
            val existingUser = userRepository.save(testUserEntityA())
            val existingUserId = existingUser.id!!
            // When
            val updatedUser = underTest.patchUser(existingUserId, UserUpdateRequest())
            // Then
            assertThat(updatedUser).isEqualTo(existingUser)
        }

        @Test
        fun `patch request updates a user's username `() {
            val newName = "New Name"
            val existingUser = testUserEntityA()
            val expectedUser = existingUser.copy(username = newName)
            val userUpdateRequest = UserUpdateRequest(username = newName)
            assertThatUserPartialUpdateIsUpdated(existingUser, expectedUser, userUpdateRequest )
        }

        @Test
        fun `patch request updates a user's email `() {
            val newEmail = "updatedEmail@test.com"
            val existingUser = testUserEntityA()
            val expectedUser = existingUser.copy(email = newEmail)
            val userUpdateRequest = UserUpdateRequest(email = newEmail)
            assertThatUserPartialUpdateIsUpdated(existingUser, expectedUser, userUpdateRequest )
        }

        @Test
        fun `patch request updates a user's image `() {
            val newImage = "updated-image.jpg"
            val existingUser = testUserEntityA()
            val expectedUser = existingUser.copy(image = newImage)
            val userUpdateRequest = UserUpdateRequest(image = newImage)
            assertThatUserPartialUpdateIsUpdated(existingUser, expectedUser, userUpdateRequest )
        }

        @Test
        fun `patch request updates a user's password `() {
            val newPassword = "newPassword9"
            val existingUser = testUserEntityA()
            val expectedUser = existingUser.copy(password = newPassword)
            val userUpdateRequest = UserUpdateRequest(password = newPassword)
            assertThatUserPartialUpdateIsUpdated(existingUser, expectedUser, userUpdateRequest )
        }

        private fun assertThatUserPartialUpdateIsUpdated(
            existingUser : UserEntity,
            expectedUser : UserEntity,
            userUpdateRequest: UserUpdateRequest
        ) {
            // Given
            val savedExistingUser = userRepository.save(existingUser)
            val existingUserId = savedExistingUser.id!!
            val expected = expectedUser.copy(id = existingUserId)
            // When
            val updatedUser = underTest.patchUser(existingUserId, userUpdateRequest)
            val retrievedUser = userRepository.findByIdOrNull(existingUserId)

            // Then
            assertThat(updatedUser).isEqualTo(expected)
            assertThat(retrievedUser).isNotNull()
            assertThat(retrievedUser).isEqualTo(expected)
        }
    }

    @Nested
    @DisplayName("deleteUserById()")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class DeleteUserByIdTest {
        @Test
        fun `test that deletes User from the database`() {
            // Given
            val savedUser = userRepository.save(testUserEntityA())
            val userId = savedUser.id!!
            // When
            underTest.deleteUserById(userId)
            val result = userRepository.existsById(userId)
            // Then
            assertThat(result).isFalse()
        }
        @Test
        fun `test that tries to delete a non-existing User from the database throws IllegalStateException`() {
            // Given
            val nonExistingUserId = 652L
            // When / Then
            assertThrows<IllegalStateException> {
                underTest.deleteUserById(nonExistingUserId)
            }

        }
    }

}