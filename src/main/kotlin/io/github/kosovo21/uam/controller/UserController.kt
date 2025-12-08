package io.github.kosovo21.uam.controller

import io.github.kosovo21.uam.dto.ChangePasswordRequest
import io.github.kosovo21.uam.dto.RegisterRequest
import io.github.kosovo21.uam.dto.UpdateUserRequest
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
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
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
        description = "Retrieve a list of all registered users. Requires JWT authentication and ADMIN role."
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
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden - ADMIN role required"
            )
        ]
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
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

    @Operation(
        summary = "Update user information",
        description = "Update user email or roles. Requires JWT authentication. Users can only update their own account unless they have admin privileges."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User updated successfully",
                content = [Content(schema = Schema(implementation = UserResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error - invalid email format"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - missing or invalid JWT token"
            ),
            ApiResponse(
                responseCode = "404",
                description = "User not found"
            ),
            ApiResponse(
                responseCode = "409",
                description = "User already exists with the provided email"
            )
        ]
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{email}")
    fun updateUser(
        @Parameter(description = "User email address", required = true, example = "user@example.com")
        @PathVariable email: String,
        @Valid @RequestBody req: UpdateUserRequest
    ): ResponseEntity<UserResponse> {
        val user = service.updateUser(email, req)
        return ResponseEntity.status(HttpStatus.OK)
            .body(user)
    }

    @Operation(
        summary = "Change user password",
        description = "Change the password for a user account. Requires current password verification. Requires JWT authentication."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Password changed successfully",
                content = [Content(schema = Schema(implementation = UserResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error - invalid password format"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - missing or invalid JWT token, or incorrect current password"
            ),
            ApiResponse(
                responseCode = "404",
                description = "User not found"
            )
        ]
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/{email}/password")
    fun changePassword(
        @Parameter(description = "User email address", required = true, example = "user@example.com")
        @PathVariable email: String,
        @Valid @RequestBody req: ChangePasswordRequest
    ): ResponseEntity<UserResponse> {
        val user = service.changePassword(email, req)
        return ResponseEntity.status(HttpStatus.OK)
            .body(user)
    }

    @Operation(
        summary = "Delete user account",
        description = "Permanently delete a user account. Requires JWT authentication. This action cannot be undone."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "User deleted successfully"
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
    @DeleteMapping("/{email}")
    fun deleteUser(
        @Parameter(description = "User email address", required = true, example = "user@example.com")
        @PathVariable email: String
    ): ResponseEntity<Void> {
        service.deleteUser(email)
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .build()
    }

}