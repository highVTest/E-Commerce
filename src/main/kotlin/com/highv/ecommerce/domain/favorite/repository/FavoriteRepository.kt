package com.highv.ecommerce.domain.favorite.repository

import com.highv.ecommerce.domain.favorite.entity.Favorite
import org.springframework.data.jpa.repository.JpaRepository

interface FavoriteRepository : JpaRepository<Favorite, Long> {
    fun findByProductIdAndBuyerId(productId: Long, id: Long): Favorite?

    fun findAllByBuyerId(buyerId: Long): List<Favorite>
}
