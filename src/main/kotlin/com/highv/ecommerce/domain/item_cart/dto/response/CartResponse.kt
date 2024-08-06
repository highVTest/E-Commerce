package com.highv.ecommerce.domain.item_cart.dto.response

data class CartResponse(
    val shopId: Long,
    val shopName: String,
    val items: List<ItemResponse>
)