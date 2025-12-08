package io.github.kosovo21.uam.service

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date


@Service
class JwtService(
    @Value("\${jwt.secret}")
    private val secret: String,

    @Value("\${jwt.expiration}")
    private val expiration: Long,

    @Value("\${jwt.refresh-expiration:86400000}") // Default 24 hours
    private val refreshExpiration: Long
) {

    private val logger = LoggerFactory.getLogger(JwtService::class.java)
    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(email: String, roles: String? = null): String {
        val builder = Jwts.builder()
            .subject(email)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expiration))
        
        // Add roles to token claims if provided
        if (!roles.isNullOrBlank()) {
            builder.claim("roles", roles)
        }
        
        return builder
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }

    fun generateRefreshToken(email: String): String {
        return Jwts.builder()
            .subject(email)
            .claim("type", "refresh")
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + refreshExpiration))
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }

    fun extractEmail(token: String): String? {
        return try {
            val claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .payload
            
            // Additional check for expiration (though parser should catch this)
            if (claims.expiration.before(Date())) {
                logger.warn("Token is expired")
                return null
            }
            
            claims.subject
        } catch (e: ExpiredJwtException) {
            logger.warn("JWT token is expired: ${e.message}")
            null
        } catch (e: MalformedJwtException) {
            logger.warn("Invalid JWT token format: ${e.message}")
            null
        } catch (e: SignatureException) {
            logger.warn("JWT signature validation failed: ${e.message}")
            null
        } catch (e: Exception) {
            logger.error("Error extracting email from JWT: ${e.message}", e)
            null
        }
    }

    fun extractRoles(token: String): String? {
        return try {
            val claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .payload
            
            if (claims.expiration.before(Date())) {
                return null
            }
            
            claims["roles"] as? String
        } catch (e: Exception) {
            logger.warn("Error extracting roles from JWT: ${e.message}")
            null
        }
    }

    fun isRefreshToken(token: String): Boolean {
        return try {
            val claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .payload
            
            if (claims.expiration.before(Date())) {
                return false
            }
            
            claims["type"] == "refresh"
        } catch (e: Exception) {
            false
        }
    }

    fun isTokenValid(token: String): Boolean {
        return extractEmail(token) != null
    }

}