package com.highv.ecommerce.socialmember.service

import com.highv.ecommerce.Oauth.client.dto.KakaoLoginUserInfoResponse
import com.highv.ecommerce.socialmember.entity.KakaoSocialMember
import com.highv.ecommerce.socialmember.repository.SocialMemberRepository
import org.springframework.stereotype.Service

@Service
class SocialMemberDomainService(
    private val socialMemberRepository: SocialMemberRepository
) {

    fun registerIfAbsent(userInfo: KakaoLoginUserInfoResponse): KakaoSocialMember {
        return socialMemberRepository.findByProviderNameAndProviderId("KAKAO", userInfo.id.toString())
            ?: socialMemberRepository.save(
                KakaoSocialMember(
                    providerName = "KAKAO",
                    providerId = userInfo.id.toString(),
                    nickname = userInfo.properties.nickname
                )
            )
    }
}
