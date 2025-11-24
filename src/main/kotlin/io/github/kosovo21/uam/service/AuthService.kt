package io.github.kosovo21.uam.service

import io.github.kosovo21.uam.dto.LoginRequest
import io.github.kosovo21.uam.exception.InvalidCredentialsException
import io.github.kosovo21.uam.exception.UserNotFoundException
import io.github.kosovo21.uam.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {

    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    fun login(req: LoginRequest): String {
        logger.info("Login attempt for email: ${req.email}")
        
        val user = userRepository.findByEmail(req.email)
            .orElseThrow { 
                logger.warn("Login failed: User not found for email: ${req.email}")
                UserNotFoundException("User not found with email: ${req.email}")
            }

        if (!passwordEncoder.matches(req.password, user.password)) {
            logger.warn("Login failed: Invalid password for email: ${req.email}")
            throw InvalidCredentialsException("Invalid credentials")
        }

        val token = jwtService.generateToken(req.email)
        logger.info("Login successful for email: ${req.email}")
        return token
    }

}