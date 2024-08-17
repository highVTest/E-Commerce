package com.highv.ecommerce.domain.review.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.BuyerNotFoundException
import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.common.exception.ProductNotFoundException
import com.highv.ecommerce.common.exception.ReviewNotFoundException
import com.highv.ecommerce.common.exception.ShopNotFoundException
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.review.dto.ReviewRequest
import com.highv.ecommerce.domain.review.dto.ReviewResponse
import com.highv.ecommerce.domain.review.entity.Review
import com.highv.ecommerce.domain.review.repository.ReviewRepository
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlin.math.round

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,
    private val shopRepository: ShopRepository,
    private val buyerRepository: BuyerRepository,
) {
    fun addReview(productId: Long, reviewRequest: ReviewRequest, buyerId: Long): DefaultResponse {
        val buyer = buyerRepository.findByIdOrNull(buyerId)
            ?: throw BuyerNotFoundException(404, "Buyer id $buyerId not found")
        val product = productRepository.findByIdOrNull(productId)
            ?: throw ProductNotFoundException(404, "Product id $productId not found")
        val review = Review(
            buyer = buyer,
            product = product,
            rate = reviewRequest.rate,
            content = reviewRequest.content
        )

        reviewRepository.saveAndFlush(review)
        updateShopAverageRate(productId)
        return DefaultResponse("리뷰가 등록되었습니다.")
    }

    fun updateReview(
        productId: Long,
        reviewId: Long,
        reviewRequest: ReviewRequest,
        buyerId: Long
    ): DefaultResponse {
        val review = validateBuyer(reviewId, buyerId)
        review.apply {
            rate = reviewRequest.rate
            content = reviewRequest.content
        }
        reviewRepository.saveAndFlush(review)
        return DefaultResponse("리뷰가 수정되었습니다.")
    }

    fun deleteReview(productId: Long, reviewId: Long, buyerId: Long): DefaultResponse {
        val review = validateBuyer(reviewId, buyerId)
        reviewRepository.delete(review)
        updateShopAverageRate(productId)
        return DefaultResponse("리뷰가 삭제되었습니다.")
    }

    fun getProductReviews(productId: Long): List<ReviewResponse> {
        val reviews = reviewRepository.findAllByProductId(productId)
        return reviews.map { ReviewResponse.from(it) }
    }

    fun getBuyerReviews(buyerId: Long): List<ReviewResponse> {
        val reviews = reviewRepository.findAllByBuyerId(buyerId)
        return reviews.map { ReviewResponse.from(it) }
    }

    fun validateBuyer(reviewId: Long, buyerId: Long): Review {
        val review = reviewRepository.findByIdOrNull(reviewId)
            ?: throw ReviewNotFoundException(404, "Review id $reviewId not found")
        if (review.buyer.id != buyerId) throw CustomRuntimeException(400, "본인이 작성한 리뷰가 아닙니다.")
        return review
    }

    @Scheduled(cron = "0 0 * * * *")
    fun updateAllShopsAverageRate() {
        val shops = shopRepository.findAll()
        for (shop in shops) {
            updateShopAverageRate(shop.id!!)
        }
    }

    fun updateShopAverageRate(shopId: Long) {
        val shop = shopRepository.findByIdOrNull(shopId)
            ?: throw ShopNotFoundException(404, "Shop not found for this product and shop")
        val reviews = reviewRepository.findAllByShopId(shopId)

        val avgRate = if (reviews.isNotEmpty()) reviews.map { it.rate }.average().toFloat() else 0f
        shop.rate = round(avgRate * 100) / 100
        shopRepository.save(shop)
    }
}