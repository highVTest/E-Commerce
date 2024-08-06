package com.highv.ecommerce.domain.backoffice.dto.salesstatics

data class ProductSalesResponse(
    val productName: String,
    val productQuantity: Long,
    val productPrice: Long,
)
