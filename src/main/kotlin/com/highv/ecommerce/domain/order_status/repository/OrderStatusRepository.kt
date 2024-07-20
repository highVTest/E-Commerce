package com.highv.ecommerce.domain.order_status.repository

import com.highv.ecommerce.domain.order_status.entity.OrderStatus
import com.highv.ecommerce.domain.products_order.entity.ProductsOrder
import org.springframework.data.jpa.repository.JpaRepository

interface OrderStatusRepository: JpaRepository<OrderStatus, Long> {

    fun findByProductsOrder(productsOrder: ProductsOrder) : OrderStatus

    fun findAllByShopIdAndBuyerId(shopId: Long, buyerId: Long): List<OrderStatus>
}