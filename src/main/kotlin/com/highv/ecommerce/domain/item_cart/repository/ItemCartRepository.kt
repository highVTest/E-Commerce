package com.highv.ecommerce.domain.item_cart.repository

import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import org.springframework.data.jpa.repository.JpaRepository

interface ItemCartRepository : JpaRepository<ItemCart, Long>, ItemCartQueryDsl {


    fun findAllByBuyerId(buyerId: Long): List<ItemCart>


}