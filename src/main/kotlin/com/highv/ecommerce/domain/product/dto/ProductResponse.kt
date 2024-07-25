package com.highv.ecommerce.domain.product.dto

import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.product.entity.Product
import java.io.Serializable
import java.time.LocalDateTime

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val productImage: String,
    val price: Int,
    val likes: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isSoldOut: Boolean,
    val shopId: Long,
    val categoryId: Long,
) : Serializable {
    companion object {
        fun from(product: Product, productBO: ProductBackOffice, likes: Int = 0) = ProductResponse(
            id = product.id!!,
            name = product.name,
            description = product.description,
            productImage = product.productImage,
            price = productBO.price,
            likes = likes,
            createdAt = product.createdAt,
            updatedAt = product.updatedAt,
            isSoldOut = product.isSoldOut,
            shopId = product.shop.id!!,
            categoryId = product.categoryId,
        )
    }
}
