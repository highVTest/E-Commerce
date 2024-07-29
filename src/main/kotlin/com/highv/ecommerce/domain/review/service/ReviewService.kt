package com.highv.ecommerce.domain.review.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.review.dto.CreateReviewRequest
import com.highv.ecommerce.domain.review.dto.ReviewResponse
import com.highv.ecommerce.domain.review.dto.UpdateReviewRequest

import com.highv.ecommerce.domain.review.entity.Review
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.review.repository.ReviewRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,

    ) {


    fun addReview(productId: Long, reviewRequest: CreateReviewRequest, buyerId: Long): DefaultResponse {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw CustomRuntimeException(404, "Product id $productId not found")

        val review = Review(
            buyerId = buyerId,
            product = product,
            rate = reviewRequest.rate,
            content = reviewRequest.content
        )

        reviewRepository.save(review)
        return DefaultResponse("Review added successfully")
    }

    fun getReviews(productId: Long): List<ReviewResponse> {
        return reviewRepository.findAllByProductId(productId).map { review ->
            ReviewResponse(
                id = review.id!!,
                rate = review.rate,
                content = review.content
            )
        }
    }

    fun updateReview(productId: Long, reviewRequest: UpdateReviewRequest, buyerId: Long): DefaultResponse {
        val review = reviewRepository.findByIdAndBuyerId(productId, buyerId) //수정필요
            ?: throw CustomRuntimeException(404, "Review not found for this product and buyer")

        if (review.buyerId != buyerId) {
            throw CustomRuntimeException(403, "Unauthorized action")
        }

        review.apply {
            rate = reviewRequest.rate
            content = reviewRequest.content
        }

        reviewRepository.save(review)
        return DefaultResponse("Review updated successfully")

    }


    fun deleteReview(productId: Long, buyerId: Long): DefaultResponse {
        val review = reviewRepository.findByIdAndBuyerId(productId, buyerId) //수정필요
            ?: throw CustomRuntimeException(404, "Review not found for this product and buyer")


        if (review.buyerId != buyerId) {
            throw CustomRuntimeException(403, "Unauthorized action")
        }

        reviewRepository.delete(review)
        return DefaultResponse("Review deleted successfully")

    }

    fun getBuyerReviews(buyerId: Long): List<ReviewResponse> {
        val reviews = reviewRepository.findAllByBuyerId(buyerId)

        return reviews.map { review ->
            ReviewResponse(
                id = review.id!!,
                rate = review.rate,
                content = review.content
            )
        }
    }
}

