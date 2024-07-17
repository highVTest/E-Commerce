package com.highv.ecommerce.domain.seller.entity

import jakarta.persistence.*

@Entity
@Table(name = "seller")
class Seller(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "nickname")
    val nickname: String,
    @Column(name = "password")
    val password: String,
    @Column(name = "email")
    val email: String,
    @Column(name = "profile_image")
    val profileImage: String,
    @Column(name = "phone_number")
    val phoneNumber: String,
    @Column(name = "address")
    val address: String,
)
