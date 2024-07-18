package com.highv.ecommerce.domain.buyer.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateBuyerRequest(
    val nickname: String,
    @field : NotBlank
    @field : Size(min = 8, max = 30)
    val password: String,
    @field: NotBlank
    @field:Email
    val email: String,
    val profileImage: String,
    val phoneNumber: String,
    val address: String,
)
