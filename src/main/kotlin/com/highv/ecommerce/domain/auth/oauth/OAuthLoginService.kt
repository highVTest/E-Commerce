package com.highv.ecommerce.domain.auth.oauth

import com.highv.ecommerce.common.type.OAuthProvider
import com.highv.ecommerce.domain.buyer.service.BuyerService
import com.highv.ecommerce.infra.security.jwt.JwtPlugin
import org.springframework.stereotype.Service

@Service
class OAuthLoginService(
    private val oAuth2ClientService: OAuthClientService,
    private val buyerService: BuyerService,
    private val jwtPlugin: JwtPlugin,
) {

    fun login(provider: OAuthProvider, authorizationCode: String): String {
        return oAuth2ClientService.login(provider, authorizationCode)
            .let { buyerService.registerIfAbsent(it) }
            .let { jwtPlugin.generateAccessToken(it.id!!.toString(), it.email, "BUYER") }
    }
}