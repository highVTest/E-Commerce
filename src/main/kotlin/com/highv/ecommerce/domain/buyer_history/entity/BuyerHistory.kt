package com.highv.ecommerce.domain.buyer_history.entity

import com.highv.ecommerce.domain.buyer.entity.Buyer
import jakarta.persistence.*

@Entity
@Table(name = "buyer_history")
class BuyerHistory(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name= "buyer_id", nullable = false)
    val buyerId: Long,

    @Column(name= "order_id", nullable = false)
    val orderId: Long,

)