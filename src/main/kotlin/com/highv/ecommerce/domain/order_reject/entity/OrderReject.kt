package com.highv.ecommerce.domain.order_reject.entity

import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.order_reject.enumClass.RejectReason
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "order_reject")
class OrderReject(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "reject_reason", nullable = false)
    val rejectReason: RejectReason,

    @Column(name = "buyer_yn",nullable = false)
    val isBuyer: Boolean,

    @Column(name = "buyer_dt",nullable = false)
    val buyerDateTime : LocalDateTime,

    @Column(name = "buyer_desc",nullable = false)
    val buyerDescription : String,

    @Column(name = "seller_reject_yn",nullable = false)
    val isSellerReject: Boolean,

    @Column(name = "seller_reject_dt",nullable = false)
    val sellerDateTime : LocalDateTime,

    @Column(name = "seller_reject_desc",nullable = false)
    val sellerDescription : String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_cart_id", nullable = false)
    val itemCart: ItemCart,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "products_order_id", nullable = false)
    val productsOrderId: ItemCart

){

}