package com.highv.ecommerce.Oauth.client.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class KakaoLoginUserInfoResponse(
    val id: Long,
    val properties: KakaoUserPropertiesResponse
)