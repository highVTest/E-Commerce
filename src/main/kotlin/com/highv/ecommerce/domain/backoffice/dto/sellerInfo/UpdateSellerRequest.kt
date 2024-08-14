package com.highv.ecommerce.domain.backoffice.dto.sellerInfo

import jakarta.validation.constraints.NotBlank

data class UpdateSellerRequest(
    @field: NotBlank(message = "닉네임은 공백일 수 없습니다.")
    val nickname: String,
    val phoneNumber: String,
    val address: String,
)