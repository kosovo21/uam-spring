package io.github.kosovo21.uam.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    val email: String,
    val password: String,
    val roles: String = "USER",
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    @Column(nullable = false)
    val updatedAt: LocalDateTime? = null
)
