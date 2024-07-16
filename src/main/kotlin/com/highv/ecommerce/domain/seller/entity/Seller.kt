package com.highv.ecommerce.domain.seller.entity

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

data class Seller(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val nickname: String,
    val password: String,
    val email: String,
    val profileImage: String,
    val phoneNumber: String,
    val address: String,
)
