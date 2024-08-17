package com.highv.ecommerce.domain.admin.entity

import jakarta.persistence.*


@Entity
@Table(name = "admin")
class Admin(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "password")
    var password: String,

    @Column(name = "email")
    val email: String,
)