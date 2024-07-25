package com.highv.ecommerce.domain.order_details.dto

data class SellerOrderStatusRequest (
    val buyerId: Long,
    val description: String
)