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
    // val productTotalPrice: Int
) {
    companion object {
        // TODO("수정 필요")
        fun from(
            orderDetails: OrderDetails,
        ): BuyerOrderDetailProductResponse =
            BuyerOrderDetailProductResponse(
                orderDetailId = orderDetails.id!!,
                orderStatus = orderDetails.orderStatus,
                complainStatus = orderDetails.complainStatus,
                productName = orderDetails.product.name,
                productPrice = orderDetails.product.productBackOffice!!.price,
                productQuantity = orderDetails.productQuantity,
                productImageUrl = orderDetails.product.productImage
                // productTotalPrice = cart.product.p
            )
    }
}