package com.highv.ecommerce.domain.order_details.repository

import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface OrderDetailsJpaRepository : JpaRepository<OrderDetails, Long> {
    fun findByIdAndBuyerId(id: Long, shopId: Long): OrderDetails?
    fun findAllByBuyerId(buyerId: Long): List<OrderDetails>
    fun findAllByShopIdAndBuyerId(shopId: Long, buyerId: Long): List<OrderDetails>

    @Modifying(clearAutomatically = true)
    @Query("UPDATE OrderDetails od SET od.orderStatus = :changeStatus WHERE od.orderStatus = :whereStatus")
    fun updateDeliveryStatus(changeStatus: OrderStatus, whereStatus: OrderStatus)
}