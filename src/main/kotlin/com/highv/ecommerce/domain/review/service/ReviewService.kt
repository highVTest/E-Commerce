package com.highv.ecommerce.domain.review.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.BuyerNotFoundException
import com.highv.ecommerce.common.exception.ProductNotFoundException
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.product.dto.ReviewProductDto
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.review.dto.BuyerReviewResponse
import com.highv.ecommerce.domain.review.dto.ReviewRequest
import com.highv.ecommerce.domain.review.dto.ReviewResponse
import com.highv.ecommerce.domain.review.entity.Review
import com.highv.ecommerce.domain.review.repository.ReviewRepository
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.round

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val shopRepository: ShopRepository,
    private val buyerRepository: BuyerRepository,
    private val productRepository: ProductRepository
) {

    @Transactional
    fun addReview(productId: Long, reviewRequest: ReviewRequest, buyerId: Long): DefaultResponse {

        if (!productRepository.existsById(productId)) {
            throw ProductNotFoundException(404, "상품이 없습니다")
        }
        val buyer = buyerRepository.findByIdOrNull(buyerId)
            ?: throw BuyerNotFoundException(404, "Buyer id $buyerId not found")

        val review = Review(
            buyer = buyer,
            productId = productId,
            rate = reviewRequest.rate,
            content = reviewRequest.content
        )
        reviewRepository.save(review)
        return DefaultResponse("리뷰가 등록되었습니다.")
    }

    @Transactional
    fun updateReview(
        reviewId: Long,
        reviewRequest: ReviewRequest,
        buyerId: Long
    ): DefaultResponse {

        reviewRepository.updateByReviewIdAndBuyerId(reviewId, buyerId, reviewRequest.rate, reviewRequest.content)

        return DefaultResponse("리뷰가 수정되었습니다.")
    }

    @Transactional
    fun deleteReview(reviewId: Long, buyerId: Long): DefaultResponse {
        reviewRepository.deleteByReviewIdAndBuyerId(reviewId, buyerId)
        return DefaultResponse("리뷰가 삭제되었습니다.")
    }

    fun getProductReviews(productId: Long): List<ReviewResponse> {
        val reviews = reviewRepository.findAllByProductId(productId)
        return reviews.map { ReviewResponse.from(it) }
    }

    fun getBuyerReviews(buyerId: Long): List<BuyerReviewResponse> {
        val reviews = reviewRepository.findAllByBuyerId(buyerId)
        val product = productRepository.findAllByProductId(reviews.map { it.productId })

        val reviewResponseMap = mutableMapOf<Long, ReviewProductDto>()

        product.forEach {
            reviewResponseMap[it.productId] = it
        }

        return reviews.map { BuyerReviewResponse.from(it, reviewResponseMap[it.productId]!!) }
    }

    @Scheduled(cron = "0 0 * * * *")
    fun updateAllShopsAverageRate() {
        val shops = shopRepository.findAll()
        for (shop in shops) {
            updateShopAverageRate(shop)
        }
    }

    fun updateShopAverageRate(shop: Shop) {
        val reviews = reviewRepository.findAllByShopId(shop.id!!)
        val avgRate = if (reviews.isNotEmpty()) reviews.average().toFloat() else 0f
        shop.rate = round(avgRate * 100) / 100
        shopRepository.save(shop)
    }
}