package com.highv.ecommerce.domain.auth.dto

data class LoginRequest(
    val email: String,
    val password: String,
    val role: String
)