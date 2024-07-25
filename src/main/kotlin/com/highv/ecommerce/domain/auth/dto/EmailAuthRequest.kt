package com.highv.ecommerce.domain.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailAuthRequest(
    @field: NotBlank(message = "공백일 수 없습니다.")
    @field: Email(message = "이메일 형식이 아닙니다.")
    val email: String,
    val role: UserRole
)
