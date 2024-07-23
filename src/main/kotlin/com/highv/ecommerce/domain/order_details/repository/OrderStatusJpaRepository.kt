package com.highv.ecommerce.domain.order_details.repository

import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import org.springframework.data.jpa.repository.JpaRepository

interface OrderStatusJpaRepository : JpaRepository<OrderDetails, Long> {

    fun findAllByShopIdAndProductsOrderIdAndIsDeletedFalse(shopId: Long, productsOrderId: Long): List<OrderDetails>

    fun findByIdAndShopIdAndIsDeletedFalse(id: Long, shopId: Long): OrderDetails?

    fun findByItemCartIdAndBuyerIdAndIsDeletedFalse(itemCartId: Long, buyerId: Long): OrderDetails?

    fun findAllByProductsOrderId(orderIds: Long): List<OrderDetails>
    fun findAllByBuyerId(buyerId: Long): List<OrderDetails>
    fun findAllByBuyerIdAndProductsOrderId(buyerId: Long, orderId: Long): List<OrderDetails>
}