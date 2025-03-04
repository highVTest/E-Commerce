package com.highv.ecommerce.domain.seller.entity

import com.highv.ecommerce.domain.seller.dto.ActiveStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "seller")
class Seller(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "nickname")
    var nickname: String,
    @Column(name = "password")
    var password: String,
    @Column(name = "email")
    val email: String,
    @Column(name = "profile_image")
    var profileImage: String,
    @Column(name = "phone_number")
    var phoneNumber: String,
    @Column(name = "address")
    var address: String,
    @Column(name = "active_status")
    @Enumerated(EnumType.STRING)
    var activeStatus: ActiveStatus
)
