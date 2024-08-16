package com.highv.ecommerce.domain.auth.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class EmailAuthRequest(
    @field: NotBlank(message = "공백일 수 없습니다.")
    @field: Pattern(
        regexp = "^[A-Za-z0-9]+@((gmail)|(naver))[.]com$",
        message = "이메일은 네이버 또는 gmail 가능합니다."
    )
    val email: String,
    val role: UserRole
)
