package com.highv.ecommerce.domain.product.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.product.dto.CreateReviewRequest
import com.highv.ecommerce.domain.product.dto.ReviewResponse
import com.highv.ecommerce.domain.product.dto.UpdateReviewRequest
import com.highv.ecommerce.domain.product.service.ReviewService
import com.highv.ecommerce.infra.security.UserPrincipal
import jakarta.persistence.Id
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/product/reviews")
class ReviewController(
    private val reviewService: ReviewService
) {
    @PostMapping("/{productId}")
    @PreAuthorize("hasRole('BUYER')")
    fun addReview(
        @PathVariable productId: Long,
        @RequestBody reviewRequest: CreateReviewRequest,
        @AuthenticationPrincipal buyerId: UserPrincipal
    ): ResponseEntity<DefaultResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(reviewService.addReview(productId, reviewRequest, buyerId.id))
    }

    @GetMapping("/{productId}")
    fun getReviews(
        @PathVariable productId: Long
    ): ResponseEntity<List<ReviewResponse>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewService.getReviews(productId))
    }

    @PatchMapping("/{productId}")
    @PreAuthorize("hasRole('BUYER')")
    fun updateReview(
        @PathVariable productId: Long,
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
        @PathVariable productId: Long,
        @AuthenticationPrincipal buyerId: UserPrincipal
    ): ResponseEntity<Unit> {
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(reviewService.deleteReview(productId, buyerId.id))
    }
}

