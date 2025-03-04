package com.highv.ecommerce.domain.order_details.entity

import com.highv.ecommerce.common.exception.InvalidRequestException
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.order_details.dto.BuyerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.dto.SellerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "order_details")
class OrderDetails(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    var orderStatus: OrderStatus,

    @Enumerated(EnumType.STRING)
    @Column(name = "complain_status", nullable = false)
    var complainStatus: ComplainStatus,

    @Column(name = "buyer_req_dt", nullable = true)
    var buyerDateTime: LocalDateTime? = null,

    @Column(name = "buyer_req_desc", nullable = true)
    var buyerDescription: String? = null,

    @Column(name = "seller_rej_dt", nullable = true)
    var sellerDateTime: LocalDateTime? = null,

    @Column(name = "seller_rej_desc", nullable = true)
    var sellerDescription: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    val buyer: Buyer,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

    @Column(name = "order_master_id", nullable = false)
    val orderMasterId: Long,

    @Column(name = "product_quantity", nullable = false)
    var productQuantity: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    val shop: Shop,

    @Column(name = "total_price", nullable = false)
    val totalPrice: Int
) {
    fun buyerUpdate(orderStatus: OrderStatus, buyerOrderStatusRequest: BuyerOrderStatusRequest) {

        when (buyerOrderStatusRequest.complainType.name) {
            "EXCHANGE" -> {
                if (this.orderStatus != OrderStatus.DELIVERED) throw InvalidRequestException(
                    400,
                    "물건 수령 전에는 교환 요청이 어렵습니다"
                )
                this.complainStatus = ComplainStatus.EXCHANGE_REQUESTED
                this.orderStatus = OrderStatus.PENDING
            }

            "REFUND" -> {
                when (this.orderStatus) {
                    OrderStatus.DELIVERY_PREPARING -> throw InvalidRequestException(400, "배송 준비 중에는 환불 요청이 어렵습니다")
                    OrderStatus.SHIPPING -> throw InvalidRequestException(400, "배송 중에는 환불 요청이 어렵습니다")
                    OrderStatus.PENDING -> throw InvalidRequestException(400, "이미 환불 및 교환 요청이 접수 되었습니다")
                    else -> this.orderStatus = OrderStatus.PENDING
                }
                this.complainStatus = ComplainStatus.REFUND_REQUESTED
            }
        }

        this.buyerDateTime = LocalDateTime.now()
        this.buyerDescription = buyerOrderStatusRequest.description
    }

    fun sellerUpdate(
        orderStatus: OrderStatus,
        sellerOrderStatusRequest: SellerOrderStatusRequest,
        complainStatus: ComplainStatus
    ) {

        when (orderStatus) {
            OrderStatus.ORDER_CANCELED -> if (this.complainStatus == ComplainStatus.REFUNDED) throw InvalidRequestException(
                404,
                "이미 환불 처리된 상황 입니다"
            )

            OrderStatus.PRODUCT_PREPARING -> if (this.complainStatus == ComplainStatus.EXCHANGED) throw InvalidRequestException(
                404,
                "이미 교환 완료 된 상태 입니다"
            )

            else -> {
                this.orderStatus = orderStatus

                when (complainStatus) {
                    ComplainStatus.EXCHANGE_REQUESTED -> this.complainStatus = ComplainStatus.EXCHANGE_REJECTED
                    ComplainStatus.REFUND_REQUESTED -> this.complainStatus = ComplainStatus.REFUND_REJECTED
                    ComplainStatus.REFUNDED -> {
                        this.complainStatus = ComplainStatus.REFUNDED
                        this.orderStatus = OrderStatus.ORDER_CANCELED
                    }

                    ComplainStatus.EXCHANGED -> {
                        this.complainStatus = ComplainStatus.EXCHANGED
                        this.orderStatus = OrderStatus.PRODUCT_PREPARING
                    }

                    else -> throw InvalidRequestException(400, "이미 작업을 처리 하였거나 존재 하지 않는 작업 입니다")
                }
            }
        }
        this.sellerDateTime = LocalDateTime.now()
        this.sellerDescription = sellerOrderStatusRequest.description
    }

    fun updateDeliveryStatus(orderStatus: OrderStatus) {
        when (orderStatus) {
            OrderStatus.PRODUCT_PREPARING -> {
                if (this.orderStatus != OrderStatus.ORDERED) throw InvalidRequestException(
                    400,
                    "주문 접수 일때만 배송 중으로 변경이 가능합니다."
                )
                this.orderStatus = orderStatus
            }

            OrderStatus.DELIVERY_PREPARING -> {
                if (this.orderStatus != OrderStatus.PRODUCT_PREPARING) throw InvalidRequestException(
                    400,
                    "상품 준비 중일 때만 배송 중으로 변경이 가능합니다."
                )
                this.orderStatus = orderStatus
            }

            OrderStatus.SHIPPING -> {
                if (this.orderStatus != OrderStatus.DELIVERY_PREPARING) throw InvalidRequestException(
                    400,
                    "배송 준비 중일 때만 배송 중으로 변경이 가능합니다."
                )
                this.orderStatus = orderStatus
            }

            OrderStatus.DELIVERED -> {
                if (this.orderStatus != OrderStatus.SHIPPING) throw InvalidRequestException(
                    400,
                    "배송 중일 때만 배송 완료로 변경이 가능합니다."
                )
                this.orderStatus = orderStatus
            }

            else -> {
                throw InvalidRequestException(
                    400,
                    "잘못 요청 보냈습니다."
                )
            }
        }
    }
}