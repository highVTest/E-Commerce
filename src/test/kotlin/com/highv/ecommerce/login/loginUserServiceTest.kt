package com.highv.ecommerce.login

import com.highv.ecommerce.common.exception.BuyerLoginFailedException
import com.highv.ecommerce.domain.auth.dto.LoginRequest
import com.highv.ecommerce.domain.auth.service.UserService
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.infra.email.EmailUtils
import com.highv.ecommerce.infra.redis.RedisUtils
import com.highv.ecommerce.infra.security.jwt.JwtPlugin
import com.highv.ecommerce.infra.s3.S3Manager // S3Manager 추가
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.clearAllMocks
import org.springframework.security.crypto.password.PasswordEncoder

class LoginUserServiceTest : BehaviorSpec({

    val buyerRepository: BuyerRepository = mockk()
    val sellerRepository: SellerRepository = mockk()
    val passwordEncoder: PasswordEncoder = mockk()
    val jwtPlugin: JwtPlugin = mockk()
    val redisUtils = mockk<RedisUtils>()
    val emailUtils = mockk<EmailUtils>()
    val s3Manager = mockk<S3Manager>() // 추가된 파라미터

    val userService: UserService = UserService(
        buyerRepository, sellerRepository, passwordEncoder, jwtPlugin, redisUtils, emailUtils, s3Manager, 5000L
    )

    afterEach {
        clearAllMocks()
    }

    Given("구매자가 존재하고 비밀번호가 맞는 경우") {
        val loginRequest = LoginRequest(email = "buyer@example.com", password = "password123")
        val buyer = Buyer(
            id = 1L,
            email = "buyer@example.com",
            password = "encodedPassword",
            nickname = "buyer",
            profileImage = "profile.png",
            phoneNumber = "010-1234-5678",
            address = "Gwangju, Korea",
            providerName = null,
            providerId = null
        )

        every { buyerRepository.findByEmail(loginRequest.email) } returns buyer
        every { passwordEncoder.matches(loginRequest.password, buyer.password) } returns true
        every { jwtPlugin.generateAccessToken(buyer.id.toString(), buyer.email, "BUYER") } returns "token"

        When("구매자가 로그인을 시도할 때") {
            val response = userService.loginBuyer(loginRequest)

            Then("토큰을 반환한다") {
                response.accessToken shouldBe "token"
            }
        }
    }

    Given("판매자가 존재하고 비밀번호가 맞는 경우") {
        val loginRequest = LoginRequest(email = "seller@example.com", password = "password123")
        val seller = Seller(
            id = 1L,
            email = "seller@example.com",
            password = "encodedPassword",
            nickname = "seller",
            profileImage = "profile.png",
            phoneNumber = "010-1234-5678",
            address = "Gwangju, Korea"
        )

        every { sellerRepository.findByEmail(loginRequest.email) } returns seller
        every { passwordEncoder.matches(loginRequest.password, seller.password) } returns true
        every { jwtPlugin.generateAccessToken(seller.id.toString(), seller.email, "SELLER") } returns "token"

        When("판매자가 로그인을 시도할 때") {
            val response = userService.loginSeller(loginRequest)

            Then("토큰을 반환한다") {
                response.accessToken shouldBe "token"
            }
        }
    }

    Given("구매자가 존재하지 않거나 비밀번호가 틀린 경우") {
        val loginRequest = LoginRequest(email = "nonexistent@example.com", password = "wrongpassword")

        every { buyerRepository.findByEmail(loginRequest.email) } returns null
        every { passwordEncoder.matches(loginRequest.password, any()) } returns false

        When("구매자가 로그인을 시도할 때") {
            Then("예외를 발생시킨다") {
                val exception = shouldThrow<BuyerLoginFailedException> { userService.loginBuyer(loginRequest) }
                exception.message shouldBe "구매자 로그인 실패"
            }
        }
    }
})
