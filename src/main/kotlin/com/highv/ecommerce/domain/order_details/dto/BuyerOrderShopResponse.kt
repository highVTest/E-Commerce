package com.highv.ecommerce.domain.order_details.dto

import com.highv.ecommerce.domain.seller.shop.entity.Shop

data class BuyerOrderShopResponse(
    val shopId: Long,
    val shopName: String,
    val productsOrders: MutableList<BuyerOrderDetailProductResponse>
) {
    companion object {
        fun from(shop: Shop, productsOrders: MutableList<BuyerOrderDetailProductResponse>): BuyerOrderShopResponse {
            return BuyerOrderShopResponse(
                shopId = shop.id!!,
                shopName = shop.name,
                productsOrders = productsOrders
            )
        }
    }
}