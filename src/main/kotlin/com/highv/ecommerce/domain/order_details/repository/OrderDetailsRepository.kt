package com.highv.ecommerce.domain.order_details.repository

import com.highv.ecommerce.domain.order_details.entity.OrderDetails

interface OrderDetailsRepository {

    fun findAllByShopIdAndOrderMasterId(shopId: Long, productsOrderId: Long): List<OrderDetails>

    fun findAllByBuyerId(id: Long): List<OrderDetails>

    fun save(orderStatus: OrderDetails): OrderDetails

    fun saveAll(orderStatuses: List<OrderDetails>): List<OrderDetails>

    fun findAllByShopId(shopId: Long): List<OrderDetails>

    fun findByItemCartIdAndBuyerId(orderStatusId: Long, buyerId: Long): OrderDetails?

    fun findByIdAndShopId(orderStatusId: Long, shopId: Long): OrderDetails?

    fun findAllByShopIdAndOrderMasterIdAndBuyerId(shopId: Long, orderId: Long, buyerId: Long): List<OrderDetails>
}