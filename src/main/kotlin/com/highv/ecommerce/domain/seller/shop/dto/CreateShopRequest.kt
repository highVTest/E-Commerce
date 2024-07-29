package com.highv.ecommerce.domain.seller.shop.dto

data class CreateShopRequest(
    val name: String,
    val description: String,
    val shopImage: String
)
