package com.highv.ecommerce.domain.item_cart.repository

import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ItemCartRepository : JpaRepository<ItemCart, Long>, ItemCartQueryDsl {
    @Modifying(clearAutomatically = true)
    @Query("delete from ItemCart c where c.product.id = :productId and c.buyer.id = :buyerId")
    fun deleteByProductIdAndBuyerId(productId: Long, buyerId: Long)

    fun findAllByBuyerId(buyerId: Long): List<ItemCart>
}