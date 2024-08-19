package com.highv.ecommerce.domain.item_cart.dto.response

import com.highv.ecommerce.domain.item_cart.entity.ItemCart

data class ItemResponse(
    val cartId: Long,
    val productId: Long,
    val productName: String,
    val productQuantity: Int,
    val productPrice: Int,
    val productImageUrl: String
) {
    companion object {
        fun from(itemCart: ItemCart): ItemResponse = ItemResponse(
            cartId = itemCart.id!!,
            productId = itemCart.product.id!!,
            productName = itemCart.product.name,
            productQuantity = itemCart.quantity,
            productPrice = itemCart.product.productBackOffice!!.price,
            productImageUrl = itemCart.product.productImage
        )
    }
}
