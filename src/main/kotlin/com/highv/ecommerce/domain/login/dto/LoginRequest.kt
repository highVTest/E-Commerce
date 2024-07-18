package com.highv.ecommerce.domain.login.dto

data class LoginRequest(
    val email: String,
    val password: String,
    val role: String
)