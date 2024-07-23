package com.highv.ecommerce.domain.order_status.dto

import com.highv.ecommerce.domain.products_order.enumClass.OrderStatusType
import com.highv.ecommerce.domain.products_order.enumClass.StatusCode

data class BuyerOrderStatusRequest(
    val shopId: Long,
    val orderStatusType: OrderStatusType,
    val description: String
)