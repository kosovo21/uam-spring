package io.github.kosovo21.uam.repository

import io.github.kosovo21.uam.entity.User
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface UserRepository : CrudRepository<User, String> {
    fun findByEmail(email: String): Optional<User>
}