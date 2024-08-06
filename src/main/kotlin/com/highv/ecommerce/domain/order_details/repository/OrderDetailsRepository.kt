package com.highv.ecommerce.domain.order_details.repository

import com.highv.ecommerce.domain.order_details.entity.OrderDetails

interface OrderDetailsRepository {

    fun save(orderStatus: OrderDetails): OrderDetails

    fun saveAll(orderStatuses: List<OrderDetails>): List<OrderDetails>

    fun findAllByShopId(shopId: Long): List<OrderDetails>

    fun findByItemCartIdAndBuyerId(orderStatusId: Long, buyerId: Long): OrderDetails?

    fun findByIdAndShopId(orderStatusId: Long, shopId: Long): OrderDetails?

    // ----------------------- 판매자 가게 주문 단건 조회
    fun findAllByShopIdAndOrderMasterId(shopId: Long, orderMasterId: Long): List<OrderDetails>

    // --------------------
    fun findAllByShopIdAndBuyerId(shopId: Long, buyerId: Long): List<OrderDetails>

    fun findAllByBuyerId(buyerId: Long): List<OrderDetails>

    fun findAllByBuyerIdAndOrderMasterId(buyerId: Long, orderId: Long): List<OrderDetails>
}