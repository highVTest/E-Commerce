package com.highv.ecommerce.Oauth.naver.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class NaverResponse<T>( //아무 타입이든 들어갈수 있음?
    @JsonProperty("resultcode") val code: String,
    val message: String,
    val response: T
)

