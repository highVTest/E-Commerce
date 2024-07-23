package com.highv.ecommerce.domain.order_details.dto

import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus

data class BuyerOrderStatusRequest(
    val shopId: Long,
    val orderStatusType: OrderStatus,
    val description: String
)