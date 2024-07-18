package com.highv.ecommerce.Oauth.client

import com.highv.ecommerce.Oauth.client.dto.KakaoLoginUserInfoResponse
import com.highv.ecommerce.Oauth.client.dto.KakaoTokenResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class KakaoOAuthLoginClient(
    @Value("\${oauth2.kakao.client_id}")  val clientId: String,
    @Value("\${oauth2.kakao.redirect_url}") val redirectUrl: String,
    @Value("\${oauth2.kakao.auth_server_base_url}") val authServerBaseUrl: String,
    @Value("\${oauth2.kakao.resource_server_base_url}") val resourceServerBaseUrl: String,
    private val restClient: RestClient

) {

    fun generateLoginUrl(): String {
        return StringBuilder(authServerBaseUrl)
            .append("/oauth/authorize")
            .append("?client_id=").append(clientId)
            .append("&redirect_uri=").append(redirectUrl)
            .append("&response_type=").append("code")
            .toString()
    }

    fun getAccessToken(code: String): String {
        val requestData = mutableMapOf(
            "grant_type" to "authorization_code",
            "client_id" to clientId,
            "code" to code
        )
        return restClient.post()
            .uri("$authServerBaseUrl/oauth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(LinkedMultiValueMap<String, String>().apply { this.setAll(requestData) })
            .retrieve()
            .onStatus(HttpStatusCode::isError) { _, _ ->
                throw RuntimeException("카카오 AccessToken 조회 실패")
            }
            .body<KakaoTokenResponse>()
            ?.accessToken
            ?: throw RuntimeException("카카오 AccessToken 조회 실패")
    }

    fun retrieveUserInfo(accessToken: String): KakaoLoginUserInfoResponse {
        return restClient.get()
            .uri("$resourceServerBaseUrl/v2/user/me")
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .onStatus(HttpStatusCode::isError) { _, _ ->
                throw RuntimeException("카카오 UserInfo 조회 실패")
            }
            .body<KakaoLoginUserInfoResponse>()
            ?: throw RuntimeException("카카오 UserInfo 조회 실패")
    }
}