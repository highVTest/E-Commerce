package com.highv.ecommerce.domain.products_order.repository

import com.highv.ecommerce.domain.products_order.entity.ProductsOrder
import org.springframework.data.jpa.repository.JpaRepository

interface ProductsOrderRepository: JpaRepository<ProductsOrder, Long> {
}