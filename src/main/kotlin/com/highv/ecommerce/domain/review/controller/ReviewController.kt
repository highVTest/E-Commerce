package com.highv.ecommerce.domain.review.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.review.dto.CreateReviewRequest
import com.highv.ecommerce.domain.review.dto.ReviewResponse
import com.highv.ecommerce.domain.review.dto.UpdateReviewRequest
import com.highv.ecommerce.domain.review.service.ReviewService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/reviews")
class ReviewController(
    private val reviewService: ReviewService
) {
    @PostMapping("/{productId}") //현재는 reviewId사용중
    @PreAuthorize("hasRole('BUYER')")
    fun addReview(
        @PathVariable productId: Long, //현재는 reviewId사용중
        @RequestBody reviewRequest: CreateReviewRequest,
        @AuthenticationPrincipal buyerId: UserPrincipal
    ): ResponseEntity<DefaultResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(reviewService.addReview(productId, reviewRequest, buyerId.id))
    }

    @GetMapping()
    fun getReviews(
        @RequestParam productId: Long,
    ): ResponseEntity<List<ReviewResponse>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewService.getReviews(productId))
    }

    @PatchMapping("/{productId}")
    @PreAuthorize("hasRole('BUYER')")
    fun updateReview(
        @PathVariable productId: Long, //현재는 reviewId사용중
        @RequestBody reviewRequest: UpdateReviewRequest,
        @AuthenticationPrincipal buyerId: UserPrincipal
    ): ResponseEntity<DefaultResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewService.updateReview(productId, reviewRequest, buyerId.id))
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('BUYER')")
    fun deleteReview(
        @PathVariable productId: Long, //현재는 reviewId사용중
        @AuthenticationPrincipal buyerId: UserPrincipal
    ): ResponseEntity<DefaultResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewService.deleteReview(productId, buyerId.id))
    }

    @GetMapping("/buyer")
    @PreAuthorize("hasRole('BUYER')")
    fun getBuyerReviews(
        @AuthenticationPrincipal buyerId: UserPrincipal
    ): ResponseEntity<List<ReviewResponse>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewService.getBuyerReviews(buyerId.id))
    }

}

