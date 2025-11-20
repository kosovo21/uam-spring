package io.github.kosovo21.uam.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String
)
