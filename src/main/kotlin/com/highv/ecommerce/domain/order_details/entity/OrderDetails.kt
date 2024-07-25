package com.highv.ecommerce.domain.order_details.entity

import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.order_details.dto.BuyerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.dto.SellerOrderStatusRequest
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
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

    @Column(name= "order_master_id", nullable = false)
    val orderMasterId: Long,

    @Column(name = "product_quantity", nullable = false)
    var productQuantity: Int,

    @Column(name = "shop_id", nullable = false)
    val shopId: Long,

    @Column(name="total_price", nullable = false)
    val totalPrice: Int
){
    fun buyerUpdate(orderStatus: OrderStatus, buyerOrderStatusRequest: BuyerOrderStatusRequest) {

        when (buyerOrderStatusRequest.complainType.name) {
            "EXCHANGE" -> {
                if(this.orderStatus != OrderStatus.DELIVERED) throw RuntimeException("물건 수령 전에는 교환 요청이 어렵습니다")
                this.complainStatus = ComplainStatus.EXCHANGE_REQUESTED
            }
            "REFUND" -> {
                when(this.orderStatus){
                    OrderStatus.DELIVERY_PREPARING -> throw RuntimeException("배송 준비 중에는 환불 요청이 어렵습니다")
                    OrderStatus.SHIPPING -> throw RuntimeException("배송 중에는 환불 요청이 어렵 습니다")
                    OrderStatus.PENDING -> throw RuntimeException("이미 환불 및 교환 요청이 접수 되었습니다")
                    else -> this.orderStatus = orderStatus
                }
                this.complainStatus = ComplainStatus.REFUND_REQUESTED
            }
        }

        this.buyerDateTime = LocalDateTime.now()
        this.buyerDescription = buyerOrderStatusRequest.description
    }

    fun sellerUpdate(orderStatus: OrderStatus, sellerOrderStatusRequest: SellerOrderStatusRequest, complainStatus: ComplainStatus) {

        this.orderStatus = orderStatus

        when (complainStatus) {
            ComplainStatus.EXCHANGE_REQUESTED -> this.complainStatus = ComplainStatus.EXCHANGE_REJECTED
            ComplainStatus.REFUND_REQUESTED -> this.complainStatus = ComplainStatus.REFUND_REJECTED
            ComplainStatus.REFUNDED -> this.complainStatus = ComplainStatus.REFUNDED
            ComplainStatus.EXCHANGED -> this.complainStatus = ComplainStatus.EXCHANGED
            else -> throw RuntimeException("잘못된 접근 입니다")
        }
        this.sellerDateTime = LocalDateTime.now()
        this.sellerDescription = sellerOrderStatusRequest.description
    }

}