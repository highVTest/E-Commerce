package com.highv.ecommerce.domain.buyer.dto

data class CreateBuyerRequest (
    val nickname: String,
    val password: String,
    val email: String,
    val profileImage: String,
    val phoneNumber: String,
    val address: String,
)
