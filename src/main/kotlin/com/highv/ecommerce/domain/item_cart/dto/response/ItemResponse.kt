package com.highv.ecommerce.domain.item_cart.dto.response

import com.highv.ecommerce.domain.item_cart.entity.ItemCart

data class ItemResponse(
    val cartId: Long,
    val productId: Long,
    val productName: String,
    val productQuantity: Int,
    val productPrice: Int,
    // val totalPrice: Int, // TODO: 없어도 되지 않을까? 프론트에서 처리하면 될 거 같은데...
    val productImageUrl: String
) {
    companion object {
        fun from(itemCart: ItemCart): ItemResponse = ItemResponse(
            cartId = itemCart.id!!,
            productId = itemCart.product.id!!,
            productName = itemCart.product.name,
            productQuantity = itemCart.quantity,
            productPrice = itemCart.product.productBackOffice!!.price,
            // totalPrice = itemCart.product.productBackOffice!!.price * itemCart.quantity,
            productImageUrl = itemCart.product.productImage
        )
    }
}
