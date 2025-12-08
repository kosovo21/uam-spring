package io.github.kosovo21.uam.service

import io.github.kosovo21.uam.dto.ChangePasswordRequest
import io.github.kosovo21.uam.dto.RegisterRequest
import io.github.kosovo21.uam.dto.UpdateUserRequest
import io.github.kosovo21.uam.dto.UserResponse
import io.github.kosovo21.uam.entity.User
import io.github.kosovo21.uam.exception.InvalidCredentialsException
import io.github.kosovo21.uam.exception.UserAlreadyExistsException
import io.github.kosovo21.uam.exception.UserNotFoundException
import io.github.kosovo21.uam.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
            .orElseThrow { UserNotFoundException("User not found with email: $email") }
        return toUserResponse(user)
    }

    @Transactional
    fun updateUser(email: String, req: UpdateUserRequest): UserResponse {
        logger.info("Updating user: $email")
        
        val user = repository.findByEmail(email)
            .orElseThrow { UserNotFoundException("User not found with email: $email") }

        // Check if new email is provided and doesn't already exist
        if (req.email != null && req.email != email) {
            if (repository.findByEmail(req.email).isPresent) {
                logger.warn("Update failed: Email already exists: ${req.email}")
                throw UserAlreadyExistsException("User already exists with email: ${req.email}")
            }
        }

        val updatedUser = User(
            email = req.email ?: user.email,
            password = user.password, // Password is not updated here
            roles = req.roles ?: user.roles,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )

        val savedUser = repository.save(updatedUser)
        logger.info("User updated successfully: ${savedUser.email}")
        
        return toUserResponse(savedUser)
    }

    @Transactional
    fun changePassword(email: String, req: ChangePasswordRequest): UserResponse {
        logger.info("Changing password for user: $email")
        
        val user = repository.findByEmail(email)
            .orElseThrow { UserNotFoundException("User not found with email: $email") }

        // Verify current password
        if (!passwordEncoder.matches(req.currentPassword, user.password)) {
            logger.warn("Password change failed: Invalid current password for email: $email")
            throw InvalidCredentialsException("Current password is incorrect")
        }

        // Update password
        val updatedUser = User(
            email = user.email,
            password = passwordEncoder.encode(req.newPassword),
            roles = user.roles,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )

        val savedUser = repository.save(updatedUser)
        logger.info("Password changed successfully for user: $email")
        
        return toUserResponse(savedUser)
    }

    @Transactional
    fun deleteUser(email: String) {
        logger.info("Deleting user: $email")
        
        if (!repository.existsById(email)) {
            logger.warn("Delete failed: User not found with email: $email")
            throw UserNotFoundException("User not found with email: $email")
        }

        repository.deleteById(email)
        logger.info("User deleted successfully: $email")
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