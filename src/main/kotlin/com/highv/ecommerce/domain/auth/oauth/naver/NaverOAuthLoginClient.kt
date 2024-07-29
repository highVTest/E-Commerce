package com.highv.ecommerce.domain.auth.oauth.naver


import com.highv.ecommerce.common.type.OAuthProvider
import com.highv.ecommerce.domain.auth.oauth.OAuthClient
import com.highv.ecommerce.domain.auth.oauth.naver.dto.NaverLoginUserInfoResponse
import com.highv.ecommerce.domain.auth.oauth.naver.dto.NaverResponse
import com.highv.ecommerce.domain.auth.oauth.naver.dto.NaverTokenResponse
import com.highv.ecommerce.domain.auth.oauth.naver.dto.OAuthLoginUserInfo
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
    @Value("\${oauth2.naver.auth_server_base_url}") val authServerBaseUrl: String,
    @Value("\${oauth2.naver.resource_server_base_url}") val resourceServerBaseUrl: String,

  private val restClient: RestClient
) : OAuthClient {

    override fun generateLoginUrl(): String {
        return StringBuilder(authServerBaseUrl)
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
            .uri("${authServerBaseUrl}/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(LinkedMultiValueMap<String, String>().apply { this.setAll(requestData) })
            .retrieve()
            .body<NaverTokenResponse>()
            ?.accessToken
            ?: throw RuntimeException("네이버 AccessToken 조회 실패")
    }

    override fun retrieveUserInfo(accessToken: String): OAuthLoginUserInfo {
        return restClient.post()
            .uri("${resourceServerBaseUrl}/nid/me")
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .body<NaverResponse<NaverLoginUserInfoResponse>>()
            ?.response
            ?: throw RuntimeException("네이버 UserInfo 조회 실패")
    }

    override fun supports(provider: OAuthProvider): Boolean {
        return provider == OAuthProvider.NAVER
    }

}