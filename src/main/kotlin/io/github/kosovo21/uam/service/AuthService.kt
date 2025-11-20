package io.github.kosovo21.uam.service

import io.github.kosovo21.uam.dto.LoginRequest
import io.github.kosovo21.uam.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {

    fun login(req: LoginRequest): String {
        val user = userRepository.findByEmail(req.email)
            .orElseThrow { RuntimeException("user not found") }

        if (!passwordEncoder.matches(req.password, user.password)) {
            throw RuntimeException("invalid credentials")
        }

        return jwtService.generateToken(req.email)
    }

}