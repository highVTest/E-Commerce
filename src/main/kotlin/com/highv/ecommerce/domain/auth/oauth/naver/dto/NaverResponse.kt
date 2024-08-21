package com.highv.ecommerce.domain.auth.oauth.naver.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class NaverResponse<T>(
    @JsonProperty("resultcode") val code: String,
    val message: String,
    val response: T
)

