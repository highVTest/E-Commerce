package com.highv.ecommerce.domain.backoffice.admin.dto

data class CreateBlackListRequest(
    val sellerId: Long,
    val email: String
)
