package com.highv.ecommerce.domain.order_status.dto

import com.highv.ecommerce.domain.products_order.enumClass.OrderStatusType

data class OrderListRequest(
    val shopId : Long,
    val orderStatusType: OrderStatusType,
    val description: String
)