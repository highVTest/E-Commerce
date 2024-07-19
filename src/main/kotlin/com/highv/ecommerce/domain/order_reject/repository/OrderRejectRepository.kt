package com.highv.ecommerce.domain.order_reject.repository

import com.highv.ecommerce.domain.order_reject.entity.OrderReject
import com.highv.ecommerce.domain.products_order.entity.ProductsOrder
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRejectRepository: JpaRepository<OrderReject, Long> {

    fun findByProductsOrder(productsOrder: ProductsOrder) : OrderReject
}