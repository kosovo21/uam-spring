package io.github.kosovo21.uam.controller

import io.github.kosovo21.uam.dto.LoginRequest
import io.github.kosovo21.uam.dto.LoginResponse
import io.github.kosovo21.uam.dto.RefreshTokenRequest
import io.github.kosovo21.uam.dto.RefreshTokenResponse
import io.github.kosovo21.uam.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for user login and token management")
class AuthController(
    private val authService: AuthService
) {

    @Operation(
        summary = "User login",
        description = "Authenticate a user with email and password. Returns a JWT token for subsequent API calls."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Login successful",
                content = [Content(schema = Schema(implementation = LoginResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error - invalid email or password format"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Invalid credentials"
            ),
            ApiResponse(
                responseCode = "404",
                description = "User not found"
            )
        ]
    )
    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest): ResponseEntity<LoginResponse> {
        val (token, refreshToken) = authService.login(req)
        return ResponseEntity.status(HttpStatus.OK)
            .body(LoginResponse(token, refreshToken))
    }

    @Operation(
        summary = "Refresh access token",
        description = "Generate a new access token using a valid refresh token. The refresh token should be obtained during login."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Token refreshed successfully",
                content = [Content(schema = Schema(implementation = RefreshTokenResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error - token is required"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Invalid or expired refresh token"
            )
        ]
    )
    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody req: RefreshTokenRequest): ResponseEntity<RefreshTokenResponse> {
        val token = authService.refreshToken(req)
        return ResponseEntity.status(HttpStatus.OK)
            .body(RefreshTokenResponse(token))
    }

}