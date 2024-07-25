package com.highv.ecommerce.domain.auth.dto

data class EmailAuthResponse(
    val id: Long,
    val isApproved: Boolean,
    val role: UserRole
)