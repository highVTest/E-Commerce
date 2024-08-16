package com.highv.ecommerce.domain.backoffice.dto.sellerInfo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UpdateSellerRequest(
    @field: NotBlank(message = "닉네임은 공백일 수 없습니다.")
    val nickname: String,
    @field: Pattern(
        regexp = "^(010)-?[0-9]{4}-?[0-9]{4}$",
        message = "유효한 핸드폰 번호가 아닙니다."
    )
    val phoneNumber: String,
    val address: String,
)