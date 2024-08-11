package com.highv.ecommerce.domain.favorite.repository

import com.highv.ecommerce.domain.favorite.entity.Favorite
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface FavoriteRepository : JpaRepository<Favorite, Long> {
    fun findAllByProductIdAndBuyerId(productId: Long, id: Long): List<Favorite>

    fun existsByProductIdAndBuyerId(productId: Long, buyerId: Long): Boolean

    @Modifying(clearAutomatically = true)
    @Query("delete from Favorite f where f.productId = :productId and f.buyerId = :buyerId")
    fun deleteFavorite(productId: Long, buyerId: Long)

    fun findAllByBuyerId(buyerId: Long): List<Favorite>

    fun countFavoriteByProductId(productId: Long): Int
}
