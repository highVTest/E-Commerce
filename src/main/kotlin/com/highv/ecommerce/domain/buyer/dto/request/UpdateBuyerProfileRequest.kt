package com.highv.ecommerce.domain.buyer.dto.request

data class UpdateBuyerProfileRequest(
    val nickname: String,
    val phoneNumber: String,
    val address: String
)
