package com.highv.ecommerce.domain.order_details.repository

import com.highv.ecommerce.domain.order_details.entity.OrderDetails

interface OrderStatusRepository {

    fun findAllByShopIdAndProductsOrderId(shopId: Long, productsOrderId: Long): List<OrderDetails>

    fun findAllByBuyerId(id: Long): List<OrderDetails>

    fun save(orderStatus: OrderDetails): OrderDetails

    fun saveAll(orderStatuses: List<OrderDetails>): List<OrderDetails>

    fun findAllByShopId(shopId: Long): List<OrderDetails>

    fun findByItemCartIdAndBuyerId(orderStatusId: Long, buyerId: Long): OrderDetails?

    fun findByIdAndShopId(orderStatusId: Long, shopId: Long): OrderDetails?
}