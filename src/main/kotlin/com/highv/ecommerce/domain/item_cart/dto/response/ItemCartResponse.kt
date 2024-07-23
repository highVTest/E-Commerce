package com.highv.ecommerce.domain.item_cart.dto.response

import com.highv.ecommerce.domain.item_cart.entity.ItemCart

data class ItemCartResponse(
    val cartId: Long,
    val productName: String,
    val productQuantity: Int,
    val totalPrice: Int
) {
    companion object {
        fun from(itemCart: ItemCart): ItemCartResponse = ItemCartResponse(
            cartId = itemCart.id!!,
            productName = itemCart.productName,
            productQuantity = itemCart.quantity,
            totalPrice = itemCart.price
        )
    }
}
