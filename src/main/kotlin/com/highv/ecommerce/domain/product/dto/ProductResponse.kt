package com.highv.ecommerce.domain.product.dto

import com.highv.ecommerce.domain.product.entity.Product
import java.time.LocalDateTime

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: Int,
    val productImage: String,
    val favorite: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val quantity: Int,
    val isSoldOut: Boolean,
    val deletedAt: LocalDateTime,
    val isDeleted: Boolean,
    val shopId: Long,
    val categoryId: Long,
) {
    companion object {
        fun from(product: Product) = ProductResponse(
            id = product.id!!,
            name = product.name,
            description = product.description,
            price = product.price,
            productImage = product.productImage,
            favorite = product.favorite,
            createdAt = product.createdAt,
            updatedAt = product.updatedAt,
            quantity = product.quantity,
            isSoldOut = product.isSoldOut,
            deletedAt = product.deletedAt,
            isDeleted = product.isDeleted,
            shopId = product.shopId,
            categoryId = product.categoryId,
        )
    }
}