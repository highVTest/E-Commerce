package com.highv.ecommerce.domain.item_cart.repository

import com.highv.ecommerce.domain.item_cart.entity.ItemCart

interface ItemCartQueryDsl {
    fun findByBuyerId(buyerId: Long): List<ItemCart>
    fun findByProductIdAndBuyerId(productId: Long, buyerId: Long): ItemCart?
}