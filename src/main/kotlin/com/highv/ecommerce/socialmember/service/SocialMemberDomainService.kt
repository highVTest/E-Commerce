package com.highv.ecommerce.socialmember.service

import com.highv.ecommerce.Oauth.client.dto.KakaoLoginUserInfoResponse
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.socialmember.entity.KakaoSocialMember
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
                    email = "test@test.com",
                    password = "null", // <== 널러블 필요 없음
                    profileImage = "testProfileImage",
                    phoneNumber = "null",
                    address = "null", // 나중에 프로필 수정할 때 추가할 수 있게끔 하는 게 편할듯?
                    // -----------------
                    providerName = "KAKAO",
                    providerId = userInfo.id,
                    nickname = userInfo.properties?.nickname ?: "테스트"
                )
            )

    }
}
