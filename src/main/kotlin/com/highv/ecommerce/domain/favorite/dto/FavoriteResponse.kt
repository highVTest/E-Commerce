package com.highv.ecommerce.domain.favorite.dto

data class FavoriteResponse(
    // val id: Long,
    val productId: Long,
    val productName: String,
    val productPrice: Int,
    val productImageUrl: String,
)