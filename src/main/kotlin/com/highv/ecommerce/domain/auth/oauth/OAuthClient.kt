package com.highv.ecommerce.domain.auth.oauth

import com.highv.ecommerce.common.type.OAuthProvider
import com.highv.ecommerce.domain.auth.oauth.naver.dto.OAuthLoginUserInfo

interface OAuthClient {
    fun generateLoginUrl(): String
    fun getAccessToken(authorizationCode: String): String
    fun retrieveUserInfo(accessToken: String): OAuthLoginUserInfo
    fun supports(provider: OAuthProvider): Boolean
}