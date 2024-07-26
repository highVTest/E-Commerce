package com.highv.ecommerce.domain.order_details.dto

data class BuyerOrderShopResponse(
    val shopId: Long,
    val productsOrders: MutableList<BuyerOrderDetailProductResponse>
) {
    companion object {
        fun from(shopId: Long, productsOrders: MutableList<BuyerOrderDetailProductResponse>): BuyerOrderShopResponse {
            return BuyerOrderShopResponse(
                shopId = shopId,
                productsOrders = productsOrders
            )
        }
    }
}