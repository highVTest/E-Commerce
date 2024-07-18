package com.highv.ecommerce.domain.seller.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateSellerRequest(
    val nickname: String,
    @field : NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @field : Size(min = 8, max = 30, message = "비밀번호는 최소 8자 이상 최대 30 이하입니다.")
    val password: String,
    @field: NotBlank(message = "이메일은 공백일 수 없습니다.")
    @field:Email(message = "이메일 형식이 아닙니다.")
    val email: String,
    val profileImage: String,
    val phoneNumber: String,
    val address: String,
)