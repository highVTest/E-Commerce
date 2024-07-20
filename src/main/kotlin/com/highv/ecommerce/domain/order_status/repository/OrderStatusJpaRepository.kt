package com.highv.ecommerce.domain.order_status.repository

import com.highv.ecommerce.domain.order_status.entity.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository

interface OrderStatusJpaRepository: JpaRepository<OrderStatus, Long> {

    fun findAllByShopIdAndBuyerId(shopId: Long, buyerId: Long): List<OrderStatus>

    fun findAllByBuyerId(id: Long): List<OrderStatus>
}