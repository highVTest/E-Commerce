package com.highv.ecommerce.domain.favorite.service

import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.favorite.entity.Favorite
import com.highv.ecommerce.domain.favorite.repository.FavoriteRepository
import com.highv.ecommerce.domain.product.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class FavoriteService(
    private val favoriteRepository: FavoriteRepository,
    private val productRepository: ProductRepository,
    private val buyerRepository: BuyerRepository,
) {

    fun management(productId: Long, buyerId: Long): String {

        if (!productRepository.existsById(productId)) {
            throw RuntimeException("Product with ID $productId not found")
        }

        if (!buyerRepository.existsById(buyerId)) {
            throw RuntimeException("Buyer with ID $buyerId not found")
        }

        val existsFavorite: Favorite? = favoriteRepository.findByProductIdAndBuyerId(productId, buyerId)

        if (existsFavorite != null) {
            favoriteRepository.delete(existsFavorite)
            return "찜 목록에서 삭제했습니다."
        }

        val favorite = Favorite(
            productId = productId,
            buyerId = buyerId
        )

        favoriteRepository.save(favorite)

        return "찜 목록에 추가 했습니다."
    }

    fun countFavorite(productId: Long): Int {
        return favoriteRepository.countFavoriteByProductId(productId)
    }
}
