package com.highv.ecommerce.infra.security.config

import com.highv.ecommerce.infra.security.CustomAuthenticationEntryPoint
import com.highv.ecommerce.infra.security.jwt.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val authenticationEntrypoint: AuthenticationEntryPoint,
    private val accessDeniedHandler: AccessDeniedHandler,
) {
    @Bean
    fun filterChain(
        http: HttpSecurity,
        customAuthenticationEntryPoint: CustomAuthenticationEntryPoint
    ): SecurityFilterChain {
        return http
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .csrf { it.disable() }
            .cors { }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/v1/login",
                    "/api/v1/buyer/user_signup",
                    "/api/v1/seller/user_signup",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/api/v1/**",
                    "/oauth/login/**",
                    "/api/v1/emails/**",
                ).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling {
                it.authenticationEntryPoint(authenticationEntrypoint)
                it.accessDeniedHandler(accessDeniedHandler)
            }
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        configuration.allowedOrigins = listOf("https://www.highvecommerce.com", "http://localhost:5173")

        configuration.allowedMethods = listOf("POST", "GET", "DELETE", "PUT", "PATCH")

        configuration.allowedHeaders = listOf("*")

        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}