package com.highv.ecommerce.domain.order_status.repository

import com.highv.ecommerce.domain.order_status.entity.OrderStatus

interface OrderStatusRepository {

    fun findAllByShopIdAndBuyerId(shopId: Long, buyerId: Long): List<OrderStatus>

    fun findAllByBuyerId(id: Long): List<OrderStatus>

    fun save(orderStatus: OrderStatus): OrderStatus

    fun saveAll(orderStatuses: List<OrderStatus>): List<OrderStatus>

    fun findAllByShopId(shopId: Long): List<OrderStatus>

    fun findByItemCartIdAndBuyerId(orderStatusId: Long, buyerId: Long): OrderStatus?

    fun findByIdAndShopId(orderStatusId: Long, shopId: Long): OrderStatus?
}