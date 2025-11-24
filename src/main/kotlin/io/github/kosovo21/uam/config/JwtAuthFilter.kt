package io.github.kosovo21.uam.config

import io.github.kosovo21.uam.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(JwtAuthFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val token = authHeader.substring(7)
            val email = jwtService.extractEmail(token)

            if (email != null && SecurityContextHolder.getContext().authentication == null) {
                val authentication = UsernamePasswordAuthenticationToken(
                    email, null, emptyList()
                )
                SecurityContextHolder.getContext().authentication = authentication
                logger.debug("Authenticated user: $email")
            } else if (email == null) {
                logger.warn("Failed to extract email from JWT token")
            }
        } catch (e: Exception) {
            logger.error("Error processing JWT token: ${e.message}", e)
            // Continue filter chain - let Spring Security handle unauthorized requests
        }

        filterChain.doFilter(request, response)
    }
}