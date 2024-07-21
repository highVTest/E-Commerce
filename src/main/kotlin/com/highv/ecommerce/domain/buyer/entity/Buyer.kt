package com.highv.ecommerce.domain.buyer.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "buyer")
class Buyer(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "nickname")
    var nickname: String,

    @Column(name = "password")
    var password: String,

    @Column(name = "email")
    var email: String,

    @Column(name = "profile_image")
    var profileImage: String,

    @Column(name = "phone_number")
    var phoneNumber: String,

    @Column(name = "address")
    var address: String,

   @Column(name = "provider_name")
   val providerName: String?,
   @Column(name = "provider_id")
   val providerId: String?
)
