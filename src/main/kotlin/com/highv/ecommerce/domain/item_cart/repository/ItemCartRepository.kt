package com.highv.ecommerce.domain.item_cart.repository

import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import org.springframework.data.jpa.repository.JpaRepository

interface ItemCartRepository : JpaRepository<ItemCart, Long>, ItemCartQueryDsl {
    // fun findByProductIdAndBuyerId(productId: Long, buyerId: Long): ItemCart?

    // fun findByBuyerId(buyerId: Long): List<ItemCart>

    fun findAllByBuyerId(buyerId: Long): List<ItemCart>

    // @Query("SELECT ic FROM ItemCart ic WHERE ic.id IN :id AND ic.buyerId = :buyerId")
    // fun findAllByIdAndBuyerId(id: List<Long>, buyerId: Long): List<ItemCart>
}