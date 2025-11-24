package io.github.kosovo21.uam.service

import io.github.kosovo21.uam.dto.RegisterRequest
import io.github.kosovo21.uam.dto.UserResponse
import io.github.kosovo21.uam.entity.User
import io.github.kosovo21.uam.exception.UserAlreadyExistsException
import io.github.kosovo21.uam.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class UserService(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun register(req: RegisterRequest): UserResponse {
        logger.info("Registration attempt for email: ${req.email}")
        
        if (repository.findByEmail(req.email).isPresent) {
            logger.warn("Registration failed: User already exists with email: ${req.email}")
            throw UserAlreadyExistsException("User already exists with email: ${req.email}")
        }

        val user = User(
            email = req.email,
            password = passwordEncoder.encode(req.password),
            roles = "USER"
        )
        
        val savedUser = repository.save(user)
        logger.info("User registered successfully: ${req.email}")
        
        return toUserResponse(savedUser)
    }

    fun findAll(): List<UserResponse> {
        logger.debug("Fetching all users")
        return repository.findAll().map { toUserResponse(it) }
    }

    fun findByEmail(email: String): UserResponse {
        logger.debug("Fetching user by email: $email")
        val user = repository.findByEmail(email)
            .orElseThrow { io.github.kosovo21.uam.exception.UserNotFoundException("User not found with email: $email") }
        return toUserResponse(user)
    }

    private fun toUserResponse(user: User): UserResponse {
        return UserResponse(
            email = user.email,
            roles = user.roles,
            createdAt = user.createdAt?.format(dateFormatter),
            updatedAt = user.updatedAt?.format(dateFormatter)
        )
    }

}