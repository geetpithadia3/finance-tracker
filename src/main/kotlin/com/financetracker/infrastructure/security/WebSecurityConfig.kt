package com.financetracker.infrastructure.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
class WebSecurityConfig(private val jwtRequestFilter: JwtRequestFilter) {

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
        .csrf { it.disable() }
        .cors {
          it.configurationSource {
            val configuration = CorsConfiguration()
            configuration.allowedOrigins = listOf("*")
            configuration.allowedMethods = listOf("*")
            configuration.allowedHeaders = listOf("Authorization", "Content-Type")
            configuration
          }
        }
        .authorizeHttpRequests { auth ->
          auth
              .requestMatchers("/auth/register", "/auth/login")
              .permitAll()
              .anyRequest()
              .authenticated()
        }
        .sessionManagement { session ->
          session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)

    return http.build()
  }

  @Bean
  fun authenticationManager(
      authenticationConfiguration: AuthenticationConfiguration
  ): AuthenticationManager {
    return authenticationConfiguration.authenticationManager
  }

  @Bean
  fun passwordEncoder(): PasswordEncoder {
    return BCryptPasswordEncoder()
  }
}
