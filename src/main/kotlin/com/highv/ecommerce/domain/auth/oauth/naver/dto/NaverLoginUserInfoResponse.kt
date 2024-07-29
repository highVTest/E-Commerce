package com.highv.ecommerce.domain.auth.oauth.naver.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.highv.ecommerce.common.type.OAuthProvider

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class NaverLoginUserInfoResponse(
    id: String,
    nickname: String,
    profileImage: String
) : OAuthLoginUserInfo(
    provider = OAuthProvider.NAVER,
    id = id,
    nickname = nickname,
   profileImage = profileImage,
) {
}