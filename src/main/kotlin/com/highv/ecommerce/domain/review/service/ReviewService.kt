package com.highv.ecommerce.domain.review.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.review.dto.ReviewRequest
import com.highv.ecommerce.domain.review.dto.ReviewResponse
import com.highv.ecommerce.domain.review.entity.Review
import com.highv.ecommerce.domain.product.repository.ProductRepository
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
){
    fun addReview(productId: Long, reviewRequest: ReviewRequest, buyerId: Long): ReviewResponse {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw RuntimeException("Product id $productId not found")

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
        reviewId:Long,
        reviewRequest: ReviewRequest,
        buyerId: Long
    ): ReviewResponse {
        val review = reviewRepository.findByIdOrNull(reviewId)?: throw RuntimeException("Review id $reviewId not found")

        review.apply {
            rate = reviewRequest.rate
            content = reviewRequest.content
        }
        val savedReview = reviewRepository.saveAndFlush(review)
        updateShopAverageRate(productId)
        return ReviewResponse.from(savedReview)
    }

    fun deleteReview(productId: Long, reviewId:Long, buyerId: Long): DefaultResponse {
        val review = reviewRepository.findByIdOrNull(reviewId)?: throw RuntimeException("Review id $reviewId not found")

        reviewRepository.delete(review)
        updateShopAverageRate(productId)
        return DefaultResponse("Review deleted successfully")
    }

    fun getProductReviews(productId: Long): List<ReviewResponse> {
        return reviewRepository.findAllByProductId(productId).map { ReviewResponse.from(it) }
    }

    fun getBuyerReviews(buyerId: Long): List<ReviewResponse> {
        val reviews = reviewRepository.findAllByBuyerId(buyerId)
        return reviews.map { ReviewResponse.from(it)}
    }

    private fun updateShopAverageRate(productId:Long){
        val reviews = reviewRepository.findAllByProductId(productId)
        val shopId = productRepository.findByIdOrNull(productId)?.shop?.id ?:throw RuntimeException("Product id $productId not found")
        val shop = shopRepository.findByIdOrNull(shopId)?: throw RuntimeException("Shop not found for this product and shop")

        val avgRate = if(reviews.isNotEmpty()){
            reviews.map{it.rate}.average().toFloat()
        }else{
            0f
        }

        shop.rate = round(avgRate*100)/100 //// 상점의 평점을 소수점 둘째 자리까지 반올림하여 업데이트
        shopRepository.save(shop)
    }
}