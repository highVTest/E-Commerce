package com.highv.ecommerce.domain.order_status.repository

import com.highv.ecommerce.domain.order_status.entity.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository

interface OrderStatusJpaRepository : JpaRepository<OrderStatus, Long> {

    fun findAllByShopIdAndProductsOrderIdAndIsDeletedFalse(shopId: Long, productsOrderId: Long): List<OrderStatus>

    fun findByIdAndShopIdAndIsDeletedFalse(id: Long, shopId: Long): OrderStatus?

    fun findByItemCartIdAndBuyerIdAndIsDeletedFalse(itemCartId: Long, buyerId: Long): OrderStatus?

    fun findAllByProductsOrderId(orderIds: Long): List<OrderStatus>
    fun findAllByBuyerId(buyerId: Long): List<OrderStatus>
    fun findAllByBuyerIdAndProductsOrderId(buyerId: Long, orderId: Long): List<OrderStatus>
}