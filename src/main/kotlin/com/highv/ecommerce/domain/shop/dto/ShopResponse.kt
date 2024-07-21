package com.highv.ecommerce.domain.shop.dto

import com.highv.ecommerce.domain.shop.entity.Shop

data class ShopResponse(
    val id: Long,
    val sellerId: Long,
    val name: String,
    val description: String,
    val shopImage: String,
    val rate: Float
) {
    companion object {
        fun from(shop: Shop) = ShopResponse(
            id = shop.id!!,
            sellerId = shop.sellerId,
            name = shop.name,
            description = shop.description,
            shopImage = shop.shopImage,
            rate = shop.rate
        )
    }
}
