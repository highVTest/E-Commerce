package com.highv.ecommerce.domain.order_details.dto

import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import java.time.LocalDateTime

data class SellerComplainResponse(
    val orderDetailId: Long,
    val productName: String,
    val productImage: String,
    val statusCode: OrderStatus,
    val complainStatus: ComplainStatus,
    val complainBuyerName: String,
    val buyerComplainReason: String?,
    val buyerComplainDate: LocalDateTime?,
    val sellerComplainRejectReason: String?,
    val sellerComplainDate: LocalDateTime?,
) {
    companion object {
        fun from(orderDetail: OrderDetails): SellerComplainResponse {
            return SellerComplainResponse(
                orderDetailId = orderDetail.id!!,
                productName = orderDetail.product.name,
                productImage = orderDetail.product.productImage,
                complainBuyerName = orderDetail.buyer.nickname,
                statusCode = orderDetail.orderStatus,
                complainStatus = orderDetail.complainStatus,
                buyerComplainReason = orderDetail.buyerDescription,
                buyerComplainDate = orderDetail.buyerDateTime,
                sellerComplainRejectReason = orderDetail.sellerDescription,
                sellerComplainDate = orderDetail.sellerDateTime
            )
        }
    }
}
