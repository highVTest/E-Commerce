package com.highv.ecommerce.domain.seller.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateSellerRequest(
    val id: Long,
    @field:NotBlank(message = "닉네임은 공백일 수 없습니다.")
    val nickname: String,

    @field : NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @field : Size(min = 8, max = 30, message = "비밀번호는 최소 8자 이상 최대 30 이하입니다.")
    val password: String,

    @field: NotBlank(message = "이메일은 공백일 수 없습니다.")
    @field: Pattern(
        regexp = "^[A-Za-z0-9]+@((gmail)|(naver))[.]com$",
        message = "이메일은 네이버 또는 gmail 가능합니다."
    )
    val email: String,
    @field: Pattern(
        regexp = "^(010)-[0-9]{4}-[0-9]{4}$",
        message = "010-XXXX-XXXX형식으로 입력해주세요"
    )
    val phoneNumber: String,
    val address: String,
)