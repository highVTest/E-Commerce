package com.highv.ecommerce.domain.product.controller

import com.highv.ecommerce.domain.product.dto.CreateReviewRequest
import com.highv.ecommerce.domain.product.dto.ReviewResponse
import com.highv.ecommerce.domain.product.service.ReviewService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/product/reviews")
class ReviewController(
    private val reviewService: ReviewService
) {
    @PostMapping("/review/{productId}")
    fun addReview(
        @PathVariable productId: Long,
        @RequestBody reviewRequest: CreateReviewRequest
    ): ResponseEntity<ReviewResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(reviewService.addReview(productId, reviewRequest))
    }

    @GetMapping("/{productId}")
    fun getReviews(
        @PathVariable productId: Long
    ): ResponseEntity<List<ReviewResponse>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewService.getReviews(productId))
    }
}

