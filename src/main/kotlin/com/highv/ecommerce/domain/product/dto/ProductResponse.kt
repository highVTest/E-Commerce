package com.highv.ecommerce.domain.product.dto

import com.highv.ecommerce.domain.product.entity.Product
import java.time.LocalDateTime

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val productImage: String,
    val favorite: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isSoldOut: Boolean,
    val deletedAt: LocalDateTime?,
    val isDeleted: Boolean,
    val shopId: Long,
    val categoryId: Long,
) {
    companion object {
        fun from(product: Product) = ProductResponse(
            id = product.id!!,
            name = product.name,
            description = product.description,
            productImage = product.productImage,
            favorite = product.favorite,
            createdAt = product.createdAt,
            updatedAt = product.updatedAt,
            isSoldOut = product.isSoldOut,
            deletedAt = null,
            isDeleted = product.isDeleted,
            shopId = product.shop.id!!,
            categoryId = product.categoryId,
        )
    }
}