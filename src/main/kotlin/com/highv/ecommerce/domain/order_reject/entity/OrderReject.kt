package com.highv.ecommerce.domain.order_reject.entity

import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.order_reject.enumClass.RejectReason
import com.highv.ecommerce.domain.products_order.dto.DescriptionRequest
import com.highv.ecommerce.domain.products_order.entity.ProductsOrder
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "order_reject")
class OrderReject(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "reject_reason", nullable = false)
    var rejectReason: RejectReason,

    @Column(name = "buyer_yn",nullable = false)
    val isBuyer: Boolean,

    @Column(name = "buyer_dt",nullable = true)
    var buyerDateTime : LocalDateTime? = null,

    @Column(name = "buyer_desc",nullable = true)
    var buyerDescription : String? = null,

    @Column(name = "seller_reject_yn",nullable = false)
    val isSellerReject: Boolean,

    @Column(name = "seller_reject_dt",nullable = true)
    val sellerDateTime : LocalDateTime? = null,

    @Column(name = "seller_reject_desc",nullable = true)
    val sellerDescription : String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_cart_id", nullable = false)
    val itemCart: ItemCart,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "products_order_id", nullable = false)
    val productsOrder: ProductsOrder

){
    fun buyerUpdate(rejectReason: RejectReason, descriptionRequest: DescriptionRequest) {

        this.rejectReason = rejectReason
        this.buyerDateTime = LocalDateTime.now()
        this.buyerDescription = descriptionRequest.description
    }

}