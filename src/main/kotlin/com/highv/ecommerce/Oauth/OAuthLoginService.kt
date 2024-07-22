package com.highv.ecommerce.Oauth

import com.highv.ecommerce.common.type.OAuthProvider
import com.highv.ecommerce.infra.security.jwt.JwtPlugin
import com.highv.ecommerce.socialmember.service.SocialMemberDomainService
import org.springframework.stereotype.Service

@Service
class OAuthLoginService (
    private val oAuth2ClientService: OAuthClientService,
    private val socialMemberService: SocialMemberDomainService,
    private val jwtPlugin: JwtPlugin
) {

    fun login(provider: OAuthProvider, authorizationCode: String): String {
        return oAuth2ClientService.login(provider, authorizationCode)
            .let { socialMemberService.registerIfAbsent(it) }
            .let { jwtPlugin.generateAccessToken(it.id!!.toString(), it.email, "BUYER") }
    }
}