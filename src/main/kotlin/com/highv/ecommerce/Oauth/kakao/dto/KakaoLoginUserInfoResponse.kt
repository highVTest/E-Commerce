package com.highv.ecommerce.Oauth.kakao.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.highv.ecommerce.Oauth.naver.dto.OAuthLoginUserInfo
import com.highv.ecommerce.common.type.OAuthProvider

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