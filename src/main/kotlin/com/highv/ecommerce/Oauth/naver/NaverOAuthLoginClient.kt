package com.highv.ecommerce.Oauth.naver

import com.highv.ecommerce.Oauth.OAuthClient
import com.highv.ecommerce.Oauth.naver.dto.NaverLoginUserInfoResponse
import com.highv.ecommerce.Oauth.naver.dto.NaverResponse
import com.highv.ecommerce.Oauth.naver.dto.NaverTokenResponse
import com.highv.ecommerce.Oauth.naver.dto.OAuthLoginUserInfo
import com.highv.ecommerce.common.type.OAuthProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class NaverOAuthLoginClient(
    @Value("\${oauth2.naver.client_id}") val clientId: String,
    @Value("\${oauth2.naver.client_secret}") val clientSecret: String,
    @Value("\${oauth2.naver.redirect_url}") val redirectUrl: String,

  private val restClient: RestClient
) : OAuthClient {

    override fun generateLoginUrl(): String {
        return StringBuilder(NAVER_AUTH_BASE_URL)
            .append("/authorize")
            .append("?response_type=").append("code")
            .append("&client_id=").append(clientId)
            .append("&redirect_uri=").append(redirectUrl)
            .toString()
    }

    override fun getAccessToken(authorizationCode: String): String {
        val requestData = mutableMapOf(
            "grant_type" to "authorization_code",
            "client_id" to clientId,
           "client_secret" to clientSecret,
            "code" to authorizationCode
        )
        return restClient.post()
            .uri("$NAVER_AUTH_BASE_URL/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(LinkedMultiValueMap<String, String>().apply { this.setAll(requestData) })
            .retrieve()
            .body<NaverTokenResponse>()
            ?.accessToken
            ?: throw RuntimeException("네이버 AccessToken 조회 실패")
    }

    override fun retrieveUserInfo(accessToken: String): OAuthLoginUserInfo {
        return restClient.post()
            .uri("$NAVER_API_BASE_URL/nid/me")
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .body<NaverResponse<NaverLoginUserInfoResponse>>()
            ?.response
            ?: throw RuntimeException("네이버 UserInfo 조회 실패")
    }

    override fun supports(provider: OAuthProvider): Boolean {
        return provider == OAuthProvider.NAVER
    }

    companion object {
        private const val NAVER_AUTH_BASE_URL = "https://nid.naver.com/oauth2.0"
        private const val NAVER_API_BASE_URL = "https://openapi.naver.com/v1"
    }
}