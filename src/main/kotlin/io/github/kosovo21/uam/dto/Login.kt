package io.github.kosovo21.uam.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:Email(message = "Email must be valid")
    @field:NotBlank(message = "Email is required")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String
)

data class LoginResponse(
    val token: String,
    val refreshToken: String? = null
)

data class RegisterRequest(
    @field:Email(message = "Email must be valid")
    @field:NotBlank(message = "Email is required")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String
)

data class UserResponse(
    val email: String,
    val roles: String,
    val createdAt: String?,
    val updatedAt: String?
)

data class UpdateUserRequest(
    @field:Email(message = "Email must be valid")
    val email: String? = null,
    
    val roles: String? = null
)

data class ChangePasswordRequest(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,
    
    @field:NotBlank(message = "New password is required")
    @field:Size(min = 8, message = "New password must be at least 8 characters")
    val newPassword: String
)

data class RefreshTokenRequest(
    @field:NotBlank(message = "Token is required")
    val token: String
)

data class RefreshTokenResponse(
    val token: String
)