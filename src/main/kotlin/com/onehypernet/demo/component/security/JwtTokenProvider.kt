package com.onehypernet.demo.component.security

import com.onehypernet.demo.exception.TokenInvalidException
import com.onehypernet.demo.model.enumerate.UserRole
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Component
open class JwtTokenProvider {
    /**
     * THIS IS NOT A SECURE PRACTICE! For simplicity, we are storing a static key here. Ideally, in a
     * microservices environment, this key would be kept on a config-server.
     */
    @Value("\${security.jwt.token.secret-key:secret-key}")
    private var secretKey: String? = null

    @Value("\${security.jwt.token.expire-length:1440000000}")
    private val validityInMilliseconds: Long = 24 * 60 * 1000 // 24h

    @Autowired
    private lateinit var userDetailService: UserDetailService

    @PostConstruct
    protected open fun init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey!!.toByteArray())
    }

    fun createToken(userId: String, role: UserRole): String {
        val claims = Jwts.claims().setSubject(userId)
        claims["auth"] = role.name

        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val userId = resolveId(token)
        val userDetails = userDetailService.loadUserByUsername(userId)
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun resolveId(token: String): String {
        return Jwts.parser().setSigningKey(secretKey)
            .parseClaimsJws(getTokenFromAuthHeader(token))
            .body.subject
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader("Authorization") ?: return null
        return getTokenFromAuthHeader(bearerToken)
    }

    fun verify(token: String?) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
        } catch (e: JwtException) {
            throw TokenInvalidException()
        } catch (e: IllegalArgumentException) {
            throw TokenInvalidException()
        }
    }

    fun getRole(accessToken: String): UserRole {
        val body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).body
        return body["auth"].let { UserRole.valueOf(it.toString()) }
    }

    private fun getTokenFromAuthHeader(authHeader: String): String {
        return if (authHeader.startsWith("Bearer ")) {
            authHeader.substring(7)
        } else authHeader
    }
}