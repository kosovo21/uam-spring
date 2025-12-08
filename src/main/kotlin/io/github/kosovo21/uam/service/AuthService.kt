package io.github.kosovo21.uam.service

import io.github.kosovo21.uam.dto.LoginRequest
import io.github.kosovo21.uam.dto.RefreshTokenRequest
import io.github.kosovo21.uam.exception.InvalidCredentialsException
import io.github.kosovo21.uam.exception.InvalidTokenException
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

    fun login(req: LoginRequest): Pair<String, String> {
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

        val token = jwtService.generateToken(req.email, user.roles)
        val refreshToken = jwtService.generateRefreshToken(req.email)
        logger.info("Login successful for email: ${req.email}")
        return Pair(token, refreshToken)
    }

    fun refreshToken(req: RefreshTokenRequest): String {
        logger.info("Token refresh attempt")
        
        if (!jwtService.isRefreshToken(req.token)) {
            logger.warn("Token refresh failed: Invalid refresh token")
            throw InvalidTokenException("Invalid refresh token")
        }

        val email = jwtService.extractEmail(req.token)
            ?: throw InvalidTokenException("Invalid or expired refresh token")

        val user = userRepository.findByEmail(email)
            .orElseThrow {
                logger.warn("Token refresh failed: User not found for email: $email")
                UserNotFoundException("User not found with email: $email")
            }

        val newToken = jwtService.generateToken(email, user.roles)
        logger.info("Token refreshed successfully for email: $email")
        return newToken
    }

}