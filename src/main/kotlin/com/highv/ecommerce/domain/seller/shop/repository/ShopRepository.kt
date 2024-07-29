package com.highv.ecommerce.domain.seller.shop.repository

import com.highv.ecommerce.domain.seller.shop.entity.Shop
import org.springframework.data.jpa.repository.JpaRepository

interface ShopRepository : JpaRepository<Shop, Long> {
    fun existsBySellerId(sellerId: Long): Boolean
    fun findShopBySellerId(sellerId: Long): Shop
}