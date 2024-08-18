package com.highv.ecommerce.domain.review.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.review.dto.BuyerReviewResponse
import com.highv.ecommerce.domain.review.dto.ReviewRequest
import com.highv.ecommerce.domain.review.dto.ReviewResponse
import com.highv.ecommerce.domain.review.service.ReviewService
import com.highv.ecommerce.infra.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/reviews")
class ReviewController(
    private val reviewService: ReviewService
) {
    @PostMapping("/{productId}")
    @PreAuthorize("hasRole('BUYER')")
    fun addReview(
        @PathVariable productId: Long,
        @AuthenticationPrincipal buyerId: UserPrincipal,
        @Valid @RequestBody reviewRequest: ReviewRequest,
        bindingResult: BindingResult
    ): ResponseEntity<DefaultResponse> {

        if (bindingResult.hasErrors()) {
            throw CustomRuntimeException(400, bindingResult.fieldError?.defaultMessage.toString())
        }

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(reviewService.addReview(productId, reviewRequest, buyerId.id))
    }

    @PutMapping("/{productId}/{reviewId}")
    @PreAuthorize("hasRole('BUYER')")
    fun updateReview(
        @PathVariable productId: Long,
        @PathVariable reviewId: Long,
        @AuthenticationPrincipal buyerId: UserPrincipal,
        @Valid @RequestBody reviewRequest: ReviewRequest,
        bindingResult: BindingResult
    ): ResponseEntity<DefaultResponse> {

        if (bindingResult.hasErrors()) {
            throw CustomRuntimeException(400, bindingResult.fieldError?.defaultMessage.toString())
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewService.updateReview(productId, reviewId, reviewRequest, buyerId.id))
    }

    @DeleteMapping("/{productId}/{reviewId}")
    @PreAuthorize("hasRole('BUYER')")
    fun deleteReview(
        @PathVariable productId: Long,
        @PathVariable reviewId: Long,
        @AuthenticationPrincipal buyerId: UserPrincipal
    ): ResponseEntity<DefaultResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewService.deleteReview(productId, reviewId, buyerId.id))
    }

    @GetMapping
    fun getProductReviews(
        @RequestParam productId: Long,
    ): ResponseEntity<List<ReviewResponse>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewService.getProductReviews(productId))
    }

    @GetMapping("/buyer")
    @PreAuthorize("hasRole('BUYER')")
    fun getBuyerReviews(
        @AuthenticationPrincipal buyerId: UserPrincipal
    ): ResponseEntity<List<BuyerReviewResponse>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewService.getBuyerReviews(buyerId.id))
    }

}