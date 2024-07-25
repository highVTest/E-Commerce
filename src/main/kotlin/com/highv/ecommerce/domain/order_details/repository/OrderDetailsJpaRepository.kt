package com.highv.ecommerce.domain.order_details.repository

import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import org.springframework.data.jpa.repository.JpaRepository

interface OrderDetailsJpaRepository : JpaRepository<OrderDetails, Long> {

    //TODO("shop_id 우선 buyer_id 로 변경")
    fun findByIdAndBuyerId(id: Long, shopId: Long): OrderDetails?

    fun findAllByOrderMasterId(orderIds: Long): List<OrderDetails>
    fun findAllByBuyerId(buyerId: Long): List<OrderDetails>
    fun findAllByBuyerIdAndOrderMasterId(buyerId: Long, orderId: Long): List<OrderDetails>
    fun findAllByShopIdAndOrderMasterIdAndBuyerId(shopId: Long, orderId: Long, buyerId: Long): List<OrderDetails>

    fun findAllByShopIdAndBuyerId(shopId: Long, buyerId: Long): List<OrderDetails>
}