package com.highv.ecommerce.domain.buyer.dto.request

import jakarta.validation.constraints.Pattern

data class UpdateBuyerProfileRequest(
    val nickname: String,
    @field: Pattern(
        regexp = "^(010)-?[0-9]{4}-?[0-9]{4}$",
        message = "유효한 핸드폰 번호가 아닙니다."
    )
    val phoneNumber: String,
    val address: String
)
