package com.highv.ecommerce.domain.backoffice.dto.sellerInfo

data class UpdateSellerRequest(
    val nickname: String,
    val phoneNumber: String,
    val address: String,
)