package com.highv.ecommerce.domain.shop.repository

import com.highv.ecommerce.domain.shop.entity.Shop
import org.springframework.data.jpa.repository.JpaRepository

interface ShopRepository : JpaRepository<Shop, Long> {
    fun findShopBySellerId(sellerId: Long): Shop
}