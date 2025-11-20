package io.github.kosovo21.uam.service

import io.github.kosovo21.uam.entity.User
import io.github.kosovo21.uam.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(user: User) {
        val savedUser = User(
            email = user.email,
            password = passwordEncoder.encode(user.password)
        )
        repository.save(savedUser)
    }

    fun findAll(): Iterable<User> {
        return repository.findAll()
    }

}