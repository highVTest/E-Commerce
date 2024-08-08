package com.highv.ecommerce.domain.seller.entity

import com.highv.ecommerce.domain.seller.shop.entity.Shop
import jakarta.persistence.*

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
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: Status
) {
    enum class Status {
        PENDING,
        APPROVED,
        RESIGNED
    }
}
