package com.highv.ecommerce.domain.auth.oauth.naver.dto

import com.highv.ecommerce.common.type.OAuthProvider

open class OAuthLoginUserInfo(
    val id: String,
    val provider: OAuthProvider,
    val nickname: String,
    val profileImage: String
) {
}