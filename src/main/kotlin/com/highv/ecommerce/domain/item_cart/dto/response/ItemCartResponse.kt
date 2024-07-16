package com.highv.ecommerce.domain.item_cart.dto.response

data class ItemCartResponse(
    val productName: String,
    val productQuantity: Int,
    val totalPrice: Int
)
