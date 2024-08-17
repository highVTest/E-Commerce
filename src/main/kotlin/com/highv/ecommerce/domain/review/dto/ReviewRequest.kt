package com.highv.ecommerce.domain.review.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class ReviewRequest(

    @field: Min(0, message = "0점 보다 적은 점수를 줄 수 없습니다")
    @field: Max(5, message = "5점 보다 많은 점수를 줄 수 없습니다")
    val rate: Float,
    val content: String
)