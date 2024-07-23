package com.highv.ecommerce.domain.products_order.repository

import com.highv.ecommerce.domain.products_order.entity.ProductsOrder

interface ProductsOrderRepository {

    fun saveAndFlush(productsOrder: ProductsOrder): ProductsOrder

    fun findByIdOrNull(Id: Long): ProductsOrder?

    fun save(productsOrder: ProductsOrder): ProductsOrder

    fun discountTotalPriceList(buyerId: Long, couponIdList: List<Long>, ): Int
}