package io.github.kosovo21.uam.controller

import io.github.kosovo21.uam.dto.RegisterRequest
import io.github.kosovo21.uam.dto.UserResponse
import io.github.kosovo21.uam.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val service: UserService
) {

    @PostMapping
    fun register(@Valid @RequestBody req: RegisterRequest): ResponseEntity<UserResponse> {
        val user = service.register(req)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(user)
    }

    @GetMapping
    fun findAll(): ResponseEntity<List<UserResponse>> {
        val users = service.findAll()
        return ResponseEntity.status(HttpStatus.OK)
            .body(users)
    }

    @GetMapping("/{email}")
    fun findByEmail(@PathVariable email: String): ResponseEntity<UserResponse> {
        val user = service.findByEmail(email)
        return ResponseEntity.status(HttpStatus.OK)
            .body(user)
    }

}