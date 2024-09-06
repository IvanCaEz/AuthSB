package com.ivancaez.auth.domain.entities

import com.ivancaez.auth.domain.Role
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    val id: Long?,
    @Column(name = "username")
    val username: String,
    @Column(name = "email")
    val email: String,
    @Column(name = "image")
    val image: String,
    @Column(name = "password")
    val password: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    val role: Role
)