package com.highv.ecommerce.domain.order_status.repository

import com.highv.ecommerce.domain.order_status.entity.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository

interface OrderStatusJpaRepository: JpaRepository<OrderStatus, Long> {

    fun findAllByShopIdAndBuyerIdIsDeletedFalse(shopId: Long, buyerId: Long): List<OrderStatus>

    fun findByIdAndShopIdIsDeletedFalse(orderId: Long, shopId: Long): OrderStatus?

    fun findByItemCartIdAndBuyerIdIsDeletedFalse(orderStatusId: Long, buyerId: Long): OrderStatus?
}