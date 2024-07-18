package com.highv.ecommerce.domain.product.category.dto

import com.highv.ecommerce.domain.product.category.entity.Category

data class CategoryResponse(
    val id: Long,
    val name: String
) {
    companion object {
        fun from(category: Category) = CategoryResponse(
            id = category.id!!,
            name = category.name
        )
    }
}