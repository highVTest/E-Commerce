package com.highv.ecommerce.domain.backoffice.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class SalesHistory(
    @Column(name = "seller_id")
    var sellerId: Long,

    @Column(name = "price")
    var price: Int,

    @Column(name = "reg_dt")
    var regDt: LocalDateTime,

    @Column(name = "buyer_name")
    var buyerName: String,

    @Column(name = "order_id")
    var orderId: Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}