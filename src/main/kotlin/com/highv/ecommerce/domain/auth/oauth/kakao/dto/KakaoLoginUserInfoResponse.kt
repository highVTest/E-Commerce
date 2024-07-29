package com.highv.ecommerce.domain.auth.oauth.kakao.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.highv.ecommerce.common.type.OAuthProvider
import com.highv.ecommerce.domain.auth.oauth.naver.dto.OAuthLoginUserInfo

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
 class KakaoLoginUserInfoResponse(
     id: String,
     properties: KakaoUserPropertiesResponse
) : OAuthLoginUserInfo(
    id = id,
    provider = OAuthProvider.KAKAO,
    nickname = properties.nickname,
    profileImage = properties.profileImage,

    )