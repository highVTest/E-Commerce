package com.highv.ecommerce.domain.auth.oauth

import com.highv.ecommerce.domain.auth.oauth.naver.dto.OAuthLoginUserInfo
import com.highv.ecommerce.common.type.OAuthProvider
import jakarta.transaction.NotSupportedException
import org.springframework.stereotype.Service

@Service
class OAuthClientService(private val clients: List<OAuthClient>
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
