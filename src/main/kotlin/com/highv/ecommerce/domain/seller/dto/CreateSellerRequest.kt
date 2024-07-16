package com.highv.ecommerce.domain.seller.dto

data class CreateSellerRequest(
    val nickname: String,
    val password: String,
    val email: String,
    val profileImage: String,
    val phoneNumber: String,
    val address: String,
)