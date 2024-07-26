package com.highv.ecommerce.domain.backoffice.admin.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "black_list")
class BlackList(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "nickname")
    val nickname: String,

    @Column(name = "email")
    val email: String,

    @Column(name = "sanctions_count")
    var sanctionsCount: Int = 1,

    @Column(name = "is_sanctioned")
    var isSanctioned: Boolean = false

)