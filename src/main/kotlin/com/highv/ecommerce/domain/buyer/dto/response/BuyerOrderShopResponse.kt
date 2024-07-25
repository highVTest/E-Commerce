package com.highv.ecommerce.domain.buyer.dto.response

data class BuyerOrderShopResponse(
    val shopId: Long,
    val productsOrders: MutableList<BuyerHistoryProductResponse>
) {
    companion object {
        fun from(shopId: Long, productsOrders: MutableList<BuyerHistoryProductResponse>): BuyerOrderShopResponse {
            return BuyerOrderShopResponse(
                shopId = shopId,
                productsOrders = productsOrders
            )
        }
    }
}