package com.highv.ecommerce.domain.order_status.entity

import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.order_status.enumClass.RejectReason
import com.highv.ecommerce.domain.products_order.dto.DescriptionRequest
import com.highv.ecommerce.domain.products_order.entity.ProductsOrder
import com.highv.ecommerce.domain.products_order.enumClass.OrderStatusType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "order_status")
class OrderStatus(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "reject_reason", nullable = false)
    var rejectReason: RejectReason,

    @Column(name = "buyer_dt",nullable = true)
    var buyerDateTime : LocalDateTime? = null,

    @Column(name = "buyer_desc",nullable = true)
    var buyerDescription : String? = null,

    @Column(name = "seller_reject_dt",nullable = true)
    var sellerDateTime : LocalDateTime? = null,

    @Column(name = "seller_reject_desc",nullable = true)
    var sellerDescription : String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_cart_id", nullable = false)
    val itemCart: ItemCart,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "products_order_id", nullable = false)
    val productsOrder: ProductsOrder,

    @Column(name = "deleted_at", nullable = true)
    val deletedAt: LocalDateTime? = null,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false,

    @Column(name = "shop_id", nullable = false)
    val shopId : Long,

    @Column(name = "buyer_id", nullable = false)
    val buyerId: Long,
){
    fun <T> buyerUpdate(orderStatusType: OrderStatusType, description: T) {

        when (orderStatusType.name) {
            "EXCHANGE" -> this.rejectReason = RejectReason.EXCHANGE_REQUESTED
            "REFUND" -> this.rejectReason = RejectReason.REFUND_REQUESTED
        }

        this.buyerDateTime = LocalDateTime.now()
        if(description is DescriptionRequest){
            this.buyerDescription = description.description
        }else if(description is String){
            this.buyerDescription = description
        }else{
            throw RuntimeException("잘못된 접근 입니다")
        }
    }

    fun <T> sellerUpdate(orderStatusType: OrderStatusType, description: T) {
        when (orderStatusType.name) {
            "EXCHANGE" -> this.rejectReason = RejectReason.EXCHANGE_REJECTED
            "REFUND" -> this.rejectReason = RejectReason.REFUND_REJECTED
        }
        this.sellerDateTime = LocalDateTime.now()
        if(description is DescriptionRequest){
            this.sellerDescription = description.description
        }else if(description is String){
            this.sellerDescription = description
        }else{
            throw RuntimeException("잘못된 접근 입니다")
        }
    }

}