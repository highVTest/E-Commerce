package com.highv.ecommerce.domain.buyer.dto.request

import jakarta.validation.constraints.Pattern

data class UpdateBuyerProfileRequest(
    val nickname: String,
    @field: Pattern(
        regexp = "^(010)-[0-9]{4}-[0-9]{4}$",
        message = "010-XXXX-XXXX형식으로 입력해주세요"
    )
    val phoneNumber: String,
    val address: String
)
