package com.highv.ecommerce.domain.buyer.dto.response

import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus

data class BuyerHistoryProductResponse(
    val orderStatusId: Long,
    val complainStatus: ComplainStatus,
    val productName: String,
    val productPrice: Int,
    val productQuantity: Int,
    val productImageUrl: String,
    val productTotalPrice: Int
) {
    companion object {
        // TODO("수정 필요")
        fun from(
//            cart: ItemCart, 수정 필요
            complainStatus: ComplainStatus,
            orderStatusId: Long
        ): BuyerHistoryProductResponse =
            BuyerHistoryProductResponse(
                orderStatusId = orderStatusId,
                productName = "productName",
                productPrice = 1000,
                productQuantity = 100,
                productImageUrl = "cart.product.productImage",
                complainStatus = complainStatus,
                productTotalPrice = 1
            )
    }
}