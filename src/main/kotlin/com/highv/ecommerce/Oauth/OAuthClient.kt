package com.highv.ecommerce.Oauth

import com.highv.ecommerce.Oauth.naver.dto.OAuthLoginUserInfo
import com.highv.ecommerce.common.type.OAuthProvider

interface OAuthClient {
    fun generateLoginUrl(): String
    fun getAccessToken(authorizationCode: String): String
    fun retrieveUserInfo(accessToken: String): OAuthLoginUserInfo
    fun supports(provider: OAuthProvider): Boolean
}