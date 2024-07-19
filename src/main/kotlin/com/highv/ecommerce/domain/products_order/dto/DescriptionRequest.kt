package com.highv.ecommerce.domain.products_order.dto

import com.highv.ecommerce.domain.products_order.enumClass.OrderStatusType

data class DescriptionRequest(
    val orderStatusType: OrderStatusType,
    val description: String
)
