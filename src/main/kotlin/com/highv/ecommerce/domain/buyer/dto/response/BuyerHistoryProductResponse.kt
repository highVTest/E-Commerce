package com.highv.ecommerce.domain.buyer.dto.response

import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus

data class BuyerHistoryProductResponse(
    val orderStatusId: Long,
    val orderPendingReason: ComplainStatus,
    val productName: String,
    val productPrice: Int,
    val productQuantity: Int,
    val productImageUrl: String,
    val productTotalPrice: Int
) {
    companion object {
        fun from(
            cart: ItemCart,
            orderPendingReason: ComplainStatus,
            orderStatusId: Long
        ): BuyerHistoryProductResponse =
            BuyerHistoryProductResponse(
                orderStatusId = orderStatusId,
                productName = cart.productName,
                productPrice = cart.price,
                productQuantity = cart.quantity,
                productImageUrl = cart.product.productImage,
                orderPendingReason = orderPendingReason,
                productTotalPrice = cart.quantity * cart.price
            )
    }
}