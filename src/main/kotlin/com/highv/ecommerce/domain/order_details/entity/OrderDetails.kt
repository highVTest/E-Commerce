package com.highv.ecommerce.domain.order_details.entity

import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.order_details.dto.BuyerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.dto.SellerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import com.highv.ecommerce.domain.product.entity.Product
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "order_details")
class OrderDetails(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    var orderStatus: OrderStatus,

    @Enumerated(EnumType.STRING)
    @Column(name = "complain_status", nullable = false)
    var complainStatus: ComplainStatus,


    @Column(name = "buyer_req_dt",nullable = true)
    var buyerDateTime : LocalDateTime? = null,

    @Column(name = "buyer_req_desc",nullable = true)
    var buyerDescription : String? = null,

    @Column(name = "seller_rej_dt",nullable = true)
    var sellerDateTime : LocalDateTime? = null,

    @Column(name = "seller_rej_desc",nullable = true)
    var sellerDescription : String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    val buyer: Buyer,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_master_id")
    val orderMaster: OrderMaster,

    @Column(name = "product_quantity", nullable = false)
    var productQuantity: Int,
){
    fun buyerUpdate(buyerOrderStatusRequest: BuyerOrderStatusRequest) {

        when (buyerOrderStatusRequest.orderStatusType.name) {
            "EXCHANGE" -> this.complainStatus = ComplainStatus.EXCHANGE_REQUESTED
            "REFUND" -> this.complainStatus = ComplainStatus.REFUND_REQUESTED
        }

        this.buyerDateTime = LocalDateTime.now()
        this.buyerDescription = buyerOrderStatusRequest.description
    }

    fun sellerUpdate(sellerOrderStatusRequest: SellerOrderStatusRequest) {
        when (sellerOrderStatusRequest.orderStatusType.name) {
            "EXCHANGE" -> this.complainStatus = ComplainStatus.EXCHANGE_REJECTED
            "REFUND" -> this.complainStatus = ComplainStatus.REFUND_REJECTED
        }
        this.sellerDateTime = LocalDateTime.now()
        this.sellerDescription = sellerOrderStatusRequest.description
    }

}