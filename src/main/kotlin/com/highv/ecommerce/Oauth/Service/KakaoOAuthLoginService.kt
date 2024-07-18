package com.highv.ecommerce.Oauth.Service

import com.highv.ecommerce.Oauth.client.KakaoOAuthLoginClient
import com.highv.ecommerce.infra.security.jwt.JwtPlugin
import com.highv.ecommerce.socialmember.service.SocialMemberDomainService
import org.springframework.stereotype.Service

@Service
class KakaoOAuthLoginService(
    private val kakaoOAuthLoginClient: KakaoOAuthLoginClient,
    private val socialMemberDomainService: SocialMemberDomainService,
    private val jwtPlugin: JwtPlugin

) {
    fun generateLoginUrl(): String {
        return kakaoOAuthLoginClient.generateLoginUrl()

    }

    fun login(code: String): String {

        return kakaoOAuthLoginClient.getAccessToken(code) // code를 통해서 AccessToken 발급
            .let { kakaoOAuthLoginClient.retrieveUserInfo(it) } // 사용자 정보 조회
            .let { socialMemberDomainService.registerIfAbsent(it) } // 사용자정보로 카카오 회원가입 & 조회
            .let { jwtPlugin.generateAccessToken(it.id!!.toString(),it.email,"buyer")} // 카카오쪽 AccessToken을 만들어서 반환

    }
}