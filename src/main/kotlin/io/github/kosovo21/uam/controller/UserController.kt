package io.github.kosovo21.uam.controller

import io.github.kosovo21.uam.dto.RegisterRequest
import io.github.kosovo21.uam.dto.UserResponse
import io.github.kosovo21.uam.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
@Tag(name = "User Management", description = "User registration and management endpoints")
class UserController(
    private val service: UserService
) {

    @Operation(
        summary = "Register a new user",
        description = "Create a new user account. This endpoint is publicly accessible and does not require authentication."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "User created successfully",
                content = [Content(schema = Schema(implementation = UserResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error - invalid email or password format"
            ),
            ApiResponse(
                responseCode = "409",
                description = "User already exists with the provided email"
            )
        ]
    )
    @PostMapping
    fun register(@Valid @RequestBody req: RegisterRequest): ResponseEntity<UserResponse> {
        val user = service.register(req)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(user)
    }

    @Operation(
        summary = "Get all users",
        description = "Retrieve a list of all registered users. Requires JWT authentication."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "List of users retrieved successfully",
                content = [Content(schema = Schema(implementation = Array<UserResponse>::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - missing or invalid JWT token"
            )
        ]
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    fun findAll(): ResponseEntity<List<UserResponse>> {
        val users = service.findAll()
        return ResponseEntity.status(HttpStatus.OK)
            .body(users)
    }

    @Operation(
        summary = "Get user by email",
        description = "Retrieve a specific user by their email address. Requires JWT authentication."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User found",
                content = [Content(schema = Schema(implementation = UserResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - missing or invalid JWT token"
            ),
            ApiResponse(
                responseCode = "404",
                description = "User not found"
            )
        ]
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{email}")
    fun findByEmail(
        @Parameter(description = "User email address", required = true, example = "user@example.com")
        @PathVariable email: String
    ): ResponseEntity<UserResponse> {
        val user = service.findByEmail(email)
        return ResponseEntity.status(HttpStatus.OK)
            .body(user)
    }

}