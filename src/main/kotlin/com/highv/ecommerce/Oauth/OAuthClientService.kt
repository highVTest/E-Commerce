package com.highv.ecommerce.Oauth

import com.highv.ecommerce.Oauth.naver.dto.OAuthLoginUserInfo
import com.highv.ecommerce.common.type.OAuthProvider
import jakarta.transaction.NotSupportedException
import org.springframework.stereotype.Service

@Service
class OAuthClientService(private val clients: List<OAuthClient> //해당 인터페이스를 구현하는 모든 객체가 포함된 리스트가 자동주입
) {

    fun generateLoginUrl(provider: OAuthProvider): String {
        val client = this.selectClient(provider)
        return client.generateLoginUrl()
    }

    fun login(provider: OAuthProvider, authorizationCode: String): OAuthLoginUserInfo {
        val client = this.selectClient(provider)
        return client.getAccessToken(authorizationCode)
            .let { client.retrieveUserInfo(it) }
    }

    private fun selectClient(provider: OAuthProvider): OAuthClient {
        return clients.find { it.supports(provider) }
            ?: throw NotSupportedException("지원하지 않는 OAuth Provider 입니다.")
    }
}
