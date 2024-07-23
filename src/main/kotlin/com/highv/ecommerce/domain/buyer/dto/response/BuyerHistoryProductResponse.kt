package com.highv.ecommerce.domain.buyer.dto.response

import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.order_status.enumClass.OrderPendingReason

data class BuyerHistoryProductResponse(
    val orderStatusId: Long,
    val orderPendingReason: OrderPendingReason,
    val productName: String,
    val productPrice: Int,
    val productQuantity: Int,
    val productImageUrl: String
) {
    companion object {
        fun from(
            cart: ItemCart,
            orderPendingReason: OrderPendingReason,
            orderStatusId: Long
        ): BuyerHistoryProductResponse =
            BuyerHistoryProductResponse(
                productName = cart.productName,
                productPrice = cart.price,
                productQuantity = cart.quantity,
                productImageUrl = cart.product.productImage,
                orderPendingReason = orderPendingReason,
                orderStatusId = orderStatusId
            )
    }
}