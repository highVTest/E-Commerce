package com.highv.ecommerce.domain.products_order.dto

import com.highv.ecommerce.domain.products_order.enumClass.StatusCode

data class OrderStatusRequest(
    val statusCode: StatusCode
)