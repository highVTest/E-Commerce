package com.highv.ecommerce.domain.buyer.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateBuyerRequest(
    val id: Long,
    @field:NotBlank(message = "닉네임은 공백일 수 없습니다.")
    val nickname: String,

    @field : NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @field : Size(min = 8, max = 30, message = "비밀번호는 최소 8자 이상 최대 30 이하입니다.")
    val password: String,

    @field: NotBlank(message = "이메일은 공백일 수 없습니다.")
    @field: Pattern(
        regexp = "^[A-Za-z0-9]+@((gmail)|(naver))[.]com$",
        message = "이메일은 네이버 또는 지메일만 가능합니다."
    )
    val email: String,

    val profileImage: String,
    @field: Pattern(
        regexp = "^(010)-?[0-9]{4}-?[0-9]{4}$",
        message = "유효한 핸드폰 번호가 아닙니다."
    )
    val phoneNumber: String,
    val address: String,
)
