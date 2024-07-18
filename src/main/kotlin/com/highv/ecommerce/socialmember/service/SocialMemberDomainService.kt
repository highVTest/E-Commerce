package com.highv.ecommerce.socialmember.service

import com.highv.ecommerce.Oauth.client.dto.KakaoLoginUserInfoResponse
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import org.springframework.stereotype.Service

@Service
class SocialMemberDomainService(
    private val buyerRepository: BuyerRepository
) {

    fun registerIfAbsent(userInfo: KakaoLoginUserInfoResponse): Buyer {
        return buyerRepository.findByProviderNameAndProviderId("KAKAO", userInfo.id)
            ?: buyerRepository.save(
                Buyer(
                    // ----------------------
                    // 수정하기
                    email = "null", // 소셜 로그인 한 사람은 업데이트 못하게 하기
                    password = "null", // 소셜 로그인 한 사람은 업데이트 못하게 하기
                    phoneNumber = "null", // 소셜 로그인 한 사람은 업데이트 하기
                    address = "null", // 소셜 로그인 한 사람은 업데이트 하기
                    // -----------------
                    providerName = "KAKAO",
                    providerId = userInfo.id,
                    nickname = userInfo.properties.nickname ?: "테스트",
                    profileImage = userInfo.properties.profileImage
                )
            )
    }
}
