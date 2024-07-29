package com.highv.ecommerce.domain.favorite.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.favorite.dto.FavoriteResponse
import com.highv.ecommerce.domain.favorite.entity.Favorite
import com.highv.ecommerce.domain.favorite.repository.FavoriteRepository
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class FavoriteService(
    private val favoriteRepository: FavoriteRepository,
    private val productRepository: ProductRepository,
    private val buyerRepository: BuyerRepository,
) {

    fun management(productId: Long, buyerId: Long): DefaultResponse {

        if (!productRepository.existsById(productId)) {
            throw CustomRuntimeException(404, "Product with ID $productId not found")
        }

        if (!buyerRepository.existsById(buyerId)) {
            throw CustomRuntimeException(404, "Buyer with ID $buyerId not found")
        }

        val existsFavorite: Favorite? = favoriteRepository.findByProductIdAndBuyerId(productId, buyerId)

        if (existsFavorite != null) {
            favoriteRepository.delete(existsFavorite)
            return DefaultResponse("찜 목록에서 삭제했습니다.")
        }

        val favorite = Favorite(
            productId = productId,
            buyerId = buyerId
        )

        favoriteRepository.save(favorite)

        return DefaultResponse("찜 목록에 추가 했습니다.")
    }

    fun getFavorites(buyerId: Long): List<FavoriteResponse> {
        // TODO : 상품 정보를 담아서 보낼지? 아니면 상품의 id만 보낼지 추후 결정

        val favorites: List<Favorite> = favoriteRepository.findAllByBuyerId(buyerId)
        val products: List<Product> = productRepository.findAllById(favorites.map { it.productId })

        // return favorites.map { FavoriteResponse(it.id!!, it.productId) }
        return products.map { FavoriteResponse(it.id!!, it.name, it.productBackOffice!!.price, it.productImage) }
    }

    fun countFavorite(productId: Long): Int {
        return favoriteRepository.countFavoriteByProductId(productId)
    }
}
