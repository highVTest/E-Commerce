package com.highv.ecommerce.domain.admin.dto

data class CreateBlackListRequest(
    val sellerId: Long,
    val email: String
)
