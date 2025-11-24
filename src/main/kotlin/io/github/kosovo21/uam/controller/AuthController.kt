package io.github.kosovo21.uam.controller

import io.github.kosovo21.uam.dto.LoginRequest
import io.github.kosovo21.uam.dto.LoginResponse
import io.github.kosovo21.uam.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest): ResponseEntity<LoginResponse> {
        val token = authService.login(req)
        return ResponseEntity.status(HttpStatus.OK)
            .body(LoginResponse(token))
    }

}