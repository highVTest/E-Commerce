package com.highv.ecommerce.OAuth

import com.highv.ecommerce.common.type.OAuthProvider
import com.highv.ecommerce.domain.auth.oauth.naver.dto.OAuthLoginUserInfo
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.buyer.service.BuyerService
import com.highv.ecommerce.infra.s3.S3Manager
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.Test

class SocialMemberDomainServiceTest  (

) {
    private val buyerRepository: BuyerRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private val s3Manager: S3Manager = mockk()

    private val buyerService = BuyerService(buyerRepository, passwordEncoder, s3Manager)


    @Test
    fun `사용자 정보 없을 경우 회원가입처리`() {
        //GIVEN
        val buyer = Buyer(
            // ----------------------
            // 수정하기
            email = "null", // 소셜 로그인 한 사람은 업데이트 못하게 하기
            password = "null", // 소셜 로그인 한 사람은 업데이트 못하게 하기
            phoneNumber = "null", // 소셜 로그인 한 사람은 업데이트 하기
            address = "null", // 소셜 로그인 한 사람은 업데이트 하기
            // -----------------
            providerName = OAuthProvider.KAKAO.name,
            providerId = "0729",
            nickname = "hysup",
            profileImage = "String"
        ).apply { id = 1L }

        val userinfo =
            OAuthLoginUserInfo(provider = OAuthProvider.KAKAO, id = "0729", nickname = "hysup", profileImage = "String")

        every { buyerRepository.findByProviderNameAndProviderId(any(), any()) } returns buyer
        every { buyerRepository.save(any()) } returns buyer
        //WHEN
        val result = buyerService.registerIfAbsent(userinfo)

        //THEN
        result.providerName shouldBe OAuthProvider.KAKAO.name
        result.id shouldNotBe null
        result.nickname shouldBe "hysup"
        result.profileImage shouldBe "String"
        result.providerId shouldBe "0729"

    }


    @Test
    fun `사용자 정보가 이미 존재하는 경우`() {
        //GIVEN
        val 기존사용자정보 = Buyer(
            // ----------------------
            // 수정하기
            email = "null", // 소셜 로그인 한 사람은 업데이트 못하게 하기
            password = "null", // 소셜 로그인 한 사람은 업데이트 못하게 하기
            phoneNumber = "null", // 소셜 로그인 한 사람은 업데이트 하기
            address = "null", // 소셜 로그인 한 사람은 업데이트 하기
            // -----------------
            providerName = OAuthProvider.KAKAO.name,
            providerId = "0729",
            nickname = "hysup",
            profileImage = "String"
        ).apply { id = 1L }

        val userinfo =
            OAuthLoginUserInfo(provider = OAuthProvider.KAKAO, id = "0729", nickname = "hysup", profileImage = "String")

        every { buyerRepository.findByProviderNameAndProviderId(any(), any()) } returns 기존사용자정보
        every { buyerRepository.save(any()) } returns 기존사용자정보

        //WHEN
        val result = buyerService.registerIfAbsent(userinfo)

        //THEN
        result shouldBe 기존사용자정보

    }
}

