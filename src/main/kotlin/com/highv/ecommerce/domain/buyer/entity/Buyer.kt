package com.highv.ecommerce.domain.buyer.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Buyer(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val nickname: String,
    val password: String,
    val email: String,
    val profileImage: String,
    val phoneNumber: String,
    val address: String,
)
