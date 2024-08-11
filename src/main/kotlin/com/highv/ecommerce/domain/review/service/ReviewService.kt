package com.highv.ecommerce.domain.review.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.common.exception.ProductNotFoundException
import com.highv.ecommerce.common.exception.ReviewNotFoundException
import com.highv.ecommerce.common.exception.ShopNotFoundException
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.review.dto.ReviewRequest
import com.highv.ecommerce.domain.review.dto.ReviewResponse
import com.highv.ecommerce.domain.review.entity.Review
import com.highv.ecommerce.domain.review.repository.ReviewRepository
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.math.round

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,
    private val shopRepository: ShopRepository,
) {
    fun addReview(productId: Long, reviewRequest: ReviewRequest, buyerId: Long): ReviewResponse {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw ProductNotFoundException(404, "Product id $productId not found")

        val review = Review(
            buyerId = buyerId,
            product = product,
            rate = reviewRequest.rate,
            content = reviewRequest.content
        )
        val savedReview = reviewRepository.saveAndFlush(review)
        updateShopAverageRate(productId)
        return ReviewResponse.from(savedReview)
    }

    fun updateReview(
        productId: Long,
        reviewId: Long,
        reviewRequest: ReviewRequest,
        buyerId: Long
    ): ReviewResponse {
        val review = reviewRepository.findByIdOrNull(reviewId) ?: throw ReviewNotFoundException(
            404,
            "Review id $reviewId not found"
        )

        if (review.buyerId != buyerId) {
            throw CustomRuntimeException(400, "자기 리뷰가 아닙니다.")
        }

        review.apply {
            rate = reviewRequest.rate
            content = reviewRequest.content
        }
        val savedReview = reviewRepository.saveAndFlush(review)
        updateShopAverageRate(productId)
        return ReviewResponse.from(savedReview)
    }

    fun deleteReview(productId: Long, reviewId: Long, buyerId: Long): DefaultResponse {
        val review = reviewRepository.findByIdOrNull(reviewId) ?: throw ReviewNotFoundException(
            404,
            "Review id $reviewId not found"
        )

        if (review.buyerId != buyerId) {
            throw CustomRuntimeException(400, "자기 리뷰가 아닙니다.")
        }
        
        reviewRepository.delete(review)
        updateShopAverageRate(productId)
        return DefaultResponse("Review deleted successfully")
    }

    fun getProductReviews(productId: Long): List<ReviewResponse> {
        return reviewRepository.findAllByProductId(productId).map { ReviewResponse.from(it) }
    }

    fun getBuyerReviews(buyerId: Long): List<ReviewResponse> {
        val reviews = reviewRepository.findAllByBuyerId(buyerId)
        return reviews.map { ReviewResponse.from(it) }
    }

    fun updateShopAverageRate(productId: Long) {
        val reviews = reviewRepository.findAllByProductId(productId)
        val shopId = productRepository.findByIdOrNull(productId)?.shop?.id ?: throw ProductNotFoundException(
            404,
            "Product id $productId not found"
        )
        val shop = shopRepository.findByIdOrNull(shopId) ?: throw ShopNotFoundException(
            404,
            "Shop not found for this product and shop"
        )

        val avgRate = if (reviews.isNotEmpty()) {
            reviews.map { it.rate }.average().toFloat()
        } else {
            0f
        }

        shop.rate = round(avgRate * 100) / 100
        shopRepository.save(shop)
    }
}