package com.highv.ecommerce.domain.backoffice.dto.sellerInfo

import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.shop.entity.Shop

data class AllShopResponse(
    val id: Long,
    val sellerId: Long,
    val name: String,
    val description: String,
    val shopImage: String,
    val rate: Float,
    val sellerName: String,
    val sellerEmail: String,
) {
    companion object {
        fun from(shop: Shop, seller: Seller) = AllShopResponse(
            id = shop.id!!,
            sellerId = shop.sellerId,
            name = shop.name,
            description = shop.description,
            shopImage = shop.shopImage,
            rate = shop.rate,
            sellerName = seller.nickname,
            sellerEmail = seller.email,
        )
    }
}