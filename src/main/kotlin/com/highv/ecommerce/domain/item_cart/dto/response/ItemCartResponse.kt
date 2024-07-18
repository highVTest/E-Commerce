package com.highv.ecommerce.domain.item_cart.dto.response

import com.highv.ecommerce.domain.item_cart.entity.ItemCart

data class ItemCartResponse(
    val productName: String,
    val productQuantity: Int,
    val totalPrice: Int
) {
    companion object {
        fun from(itemCart: ItemCart): ItemCartResponse = ItemCartResponse(
            productName = itemCart.productName,
            productQuantity = itemCart.quantity,
            totalPrice = itemCart.price
        )
    }
}
