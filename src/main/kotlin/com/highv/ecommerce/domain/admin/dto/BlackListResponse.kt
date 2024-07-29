package com.highv.ecommerce.domain.admin.dto

data class BlackListResponse(
    val nickname: String,
    val email: String,
    val sanctionsCount: Int,
    val isSanctioned: Boolean
)