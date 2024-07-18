package com.highv.ecommerce.domain.favorite.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "favorite")
class Favorite(

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "buyer_id", nullable = false)
    val buyerId: Long,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null
}
