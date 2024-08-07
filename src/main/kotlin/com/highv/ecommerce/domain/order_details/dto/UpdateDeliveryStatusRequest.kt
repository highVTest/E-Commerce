package com.highv.ecommerce.domain.order_details.dto

import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus

data class UpdateDeliveryStatusRequest(
    val deliveryStatus: OrderStatus,
)
