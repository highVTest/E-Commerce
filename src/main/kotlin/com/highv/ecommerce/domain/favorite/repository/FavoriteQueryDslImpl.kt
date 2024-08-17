package com.highv.ecommerce.domain.favorite.repository

import com.highv.ecommerce.domain.favorite.dto.FavoriteCount
import com.highv.ecommerce.domain.favorite.entity.QFavorite.favorite
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory

class FavoriteQueryDslImpl(
    private val queryFactory: JPAQueryFactory
) : FavoriteQueryDsl {

    override fun favoritesCount(productIds: Collection<Long>): List<FavoriteCount> {
        val query = queryFactory
            .select(
                Projections.constructor(
                    FavoriteCount::class.java,
                    favorite.productId,
                    favorite.count()
                )
            )
            .from(favorite)
            .where(favorite.productId.`in`(productIds))
            .groupBy(favorite.productId)
            .fetch()

        return query
    }
}