package com.highv.ecommerce.domain.order_details.dto

import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus

data class SellerOrderStatusRequest (
    val shopId: Long, // 삭제 예정
    val buyerId: Long,
    val orderStatusType: OrderStatus,
    val description: String
)