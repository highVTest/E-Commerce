package com.highv.ecommerce.OAuth

import com.highv.ecommerce.Oauth.naver.dto.OAuthLoginUserInfo
import com.highv.ecommerce.common.type.OAuthProvider
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.socialmember.service.SocialMemberDomainService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import kotlin.test.Test

class SocialMemberDomainServiceTest (

) {
    private val buyerRepository: BuyerRepository = mockk()

    private val socialMemberDomainService = SocialMemberDomainService(buyerRepository)

    @Test
    fun `사용자 정보 없을 경우 회원가입처리`() {
        //GIVEN
        buyerRepository.deleteAll() //테스트전 사용자정보 전부 삭제
        val userinfo =
            OAuthLoginUserInfo(provider = OAuthProvider.KAKAO, id = "0729", nickname = "hysup", profileImage = "String")

        //WHEN
        val result = socialMemberDomainService.registerIfAbsent(userinfo)

        //THEN
        result.address shouldBe
            result.providerName shouldBe OAuthProvider.KAKAO
        result.id shouldNotBe null
        result.nickname shouldBe 1L
        result.profileImage shouldBe "String"
        result.providerId shouldBe "0729"
        buyerRepository.findAll().toList().let {
            it.size shouldBe 1
            it[0].id shouldBe 1L
            it[0].nickname shouldBe "Hysup"
            it[0].profileImage shouldBe "String"
            it[0].providerId shouldBe "0729"

        }

    }
}
