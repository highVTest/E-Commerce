package com.highv.ecommerce.domain.backoffice.dto.sellerInfo

data class UpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)