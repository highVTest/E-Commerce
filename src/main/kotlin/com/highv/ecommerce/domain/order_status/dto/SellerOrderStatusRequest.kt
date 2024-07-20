package com.highv.ecommerce.domain.order_status.dto

import com.highv.ecommerce.domain.products_order.enumClass.OrderStatusType

data class SellerOrderStatusRequest (
    val shopId: Long, // 삭제 예정
    val buyerId: Long,
    val orderStatusType: OrderStatusType,
    val description: String
)