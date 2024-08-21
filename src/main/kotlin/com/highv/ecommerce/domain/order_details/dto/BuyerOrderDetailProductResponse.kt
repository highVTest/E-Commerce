package com.highv.ecommerce.domain.order_details.dto

import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus

data class BuyerOrderDetailProductResponse(
    val orderDetailId: Long,
    val orderStatus: OrderStatus,
    val complainStatus: ComplainStatus,
    val productName: String,
    val productPrice: Int,
    val productQuantity: Int,
    val productImageUrl: String,
) {
    companion object {
        fun from(
            orderDetails: OrderDetails,
        ): BuyerOrderDetailProductResponse =
            BuyerOrderDetailProductResponse(
                orderDetailId = orderDetails.id!!,
                orderStatus = orderDetails.orderStatus,
                complainStatus = orderDetails.complainStatus,
                productName = orderDetails.product.name,
                productPrice = orderDetails.totalPrice,
                productQuantity = orderDetails.productQuantity,
                productImageUrl = orderDetails.product.productImage
            )
    }
}