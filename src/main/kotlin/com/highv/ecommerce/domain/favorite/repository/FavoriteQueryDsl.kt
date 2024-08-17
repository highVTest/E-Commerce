package com.highv.ecommerce.domain.favorite.repository

import com.highv.ecommerce.domain.favorite.dto.FavoriteCount

interface FavoriteQueryDsl {

    fun favoritesCount(productIds: Collection<Long>): List<FavoriteCount>
}