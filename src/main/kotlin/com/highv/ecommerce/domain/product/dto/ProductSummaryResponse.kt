package com.highv.ecommerce.domain.product.dto

import java.io.Serializable

data class ProductSummaryResponse(
    val id: Long,
    val image: String,
    val name: String,
    val price: Int,
    val likes: Long,
) : Serializable {
    companion object {
        fun from(product: ProductSummaryDto, likes: Long = 0) = ProductSummaryResponse(
            id = product.id,
            image = product.image,
            name = product.name,
            price = product.price,
            likes = likes
        )
    }
}