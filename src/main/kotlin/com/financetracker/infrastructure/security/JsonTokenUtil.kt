package com.financetracker.infrastructure.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenUtil {

  @Value("\${jwt.secret}") private lateinit var secret: String

  @Value("\${jwt.expiration}") private var jwtExpiration: Long = 0

  fun generateToken(username: String): String {
    val claims: Map<String, Any> = HashMap()
    return createToken(claims, username)
  }

  private fun createToken(claims: Map<String, Any>, subject: String): String {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(Date(System.currentTimeMillis()))
        .setExpiration(Date(System.currentTimeMillis() + jwtExpiration * 1000))
        .signWith(Keys.hmacShaKeyFor(secret.toByteArray()), SignatureAlgorithm.HS256)
        .compact()
  }

  fun validateToken(token: String, userDetails: UserDetails): Boolean {
    val username = extractUsername(token)
    return username == userDetails.username && !isTokenExpired(token)
  }

  fun extractUsername(token: String): String {
    return extractClaim(token) { obj: Claims -> obj.subject }
  }

  private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
    val claims = extractAllClaims(token)
    return claimsResolver(claims)
  }

  private fun extractAllClaims(token: String): Claims {
    return Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(secret.toByteArray()))
        .build()
        .parseClaimsJws(token)
        .body
  }

  private fun isTokenExpired(token: String): Boolean {
    return extractExpiration(token).before(Date())
  }

  private fun extractExpiration(token: String): Date {
    return extractClaim(token) { obj: Claims -> obj.expiration }
  }
}
