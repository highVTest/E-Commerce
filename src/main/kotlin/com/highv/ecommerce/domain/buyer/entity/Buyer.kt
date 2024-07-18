package com.highv.ecommerce.domain.buyer.entity

import jakarta.persistence.*

@Entity
@Table(name = "buyer")
class Buyer(
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

   @Column(name = "provider_name")
   val providerName: String?,
   @Column(name = "provider_id")
   val providerId: Long?
)
