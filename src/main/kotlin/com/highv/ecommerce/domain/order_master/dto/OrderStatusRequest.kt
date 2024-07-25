package com.highv.ecommerce.domain.order_master.dto

import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus

data class OrderStatusRequest(
    val statusCode: OrderStatus
)