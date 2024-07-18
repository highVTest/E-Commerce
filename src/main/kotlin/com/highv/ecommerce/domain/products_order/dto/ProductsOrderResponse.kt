package com.highv.ecommerce.domain.products_order.dto

import com.highv.ecommerce.domain.products_order.enumClass.StatusCode
import java.time.LocalDateTime

data class ProductsOrderResponse(
    val id : Long,
    val statusCode: StatusCode,
    val updatedDate: LocalDateTime,
    val statusDescription : String,
    val buyerName : String,
    val totalPrice : Int,
)