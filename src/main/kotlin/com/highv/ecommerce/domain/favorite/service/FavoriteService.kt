package com.highv.ecommerce.domain.favorite.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.ProductNotFoundException
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.favorite.dto.FavoriteResponse
import com.highv.ecommerce.domain.favorite.entity.Favorite
import com.highv.ecommerce.domain.favorite.repository.FavoriteRepository
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FavoriteService(
    private val favoriteRepository: FavoriteRepository,
    private val productRepository: ProductRepository,
    private val buyerRepository: BuyerRepository,
) {

    @Transactional
    fun favoriteAdd(productId: Long, buyerId: Long): DefaultResponse {

        if (!productRepository.existsById(productId)) {
            throw ProductNotFoundException(404, "해당 상품이 존재하지 않습니다.")
        }

        val favorite = Favorite(
            productId = productId,
            buyerId = buyerId
        )

        favoriteRepository.save(favorite)

        return DefaultResponse("찜 목록에 추가 했습니다.")
    }

    @Transactional
    fun favoriteDelete(productId: Long, buyerId: Long): DefaultResponse {
        favoriteRepository.deleteFavorite(productId, buyerId)
        return DefaultResponse("찜 목록에서 삭제했습니다.")
    }

    fun getFavorites(buyerId: Long): List<FavoriteResponse> {

        val favorites: List<Favorite> = favoriteRepository.findAllByBuyerId(buyerId)
        val products: List<Product> = productRepository.findAllById(favorites.map { it.productId })

        return products.map { FavoriteResponse(it.id!!, it.name, it.productBackOffice!!.price, it.productImage) }
    }

    fun countFavorite(productId: Long): Int {
        return favoriteRepository.countFavoriteByProductId(productId)
    }
}
