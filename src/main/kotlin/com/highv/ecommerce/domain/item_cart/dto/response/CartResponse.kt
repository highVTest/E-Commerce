package com.highv.ecommerce.domain.item_cart.dto.response

data class CartResponse(
    val shopId: Long,
    val products: List<ItemCartResponse>
)