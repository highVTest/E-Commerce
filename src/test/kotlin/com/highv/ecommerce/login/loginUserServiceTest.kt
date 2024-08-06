//package com.highv.ecommerce.login
//
//import com.highv.ecommerce.common.exception.BuyerLoginFailedException
//import com.highv.ecommerce.common.exception.LoginException
//import com.highv.ecommerce.domain.auth.dto.LoginRequest
//import com.highv.ecommerce.domain.auth.service.UserService
//import com.highv.ecommerce.domain.buyer.entity.Buyer
//import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
//import com.highv.ecommerce.domain.seller.entity.Seller
//import com.highv.ecommerce.domain.seller.repository.SellerRepository
//import com.highv.ecommerce.infra.email.EmailUtils
//import com.highv.ecommerce.infra.redis.RedisUtils
//import com.highv.ecommerce.infra.security.jwt.JwtPlugin
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.matchers.shouldBe
//import io.mockk.every
//import io.mockk.mockk
//import org.junit.jupiter.api.Test
//import org.springframework.security.crypto.password.PasswordEncoder
//
//class loginUserServiceTest {
//
//    private val buyerRepository: BuyerRepository = mockk<BuyerRepository>()
//    private val sellerRepository: SellerRepository = mockk<SellerRepository>()
//    private val passwordEncoder: PasswordEncoder = mockk<PasswordEncoder>()
//    private val jwtPlugin: JwtPlugin = mockk<JwtPlugin>()
//    private val redisUtils = mockk<RedisUtils>()
//    private val emailUtils = mockk<EmailUtils>()
//    private val userService: UserService =
//        UserService(buyerRepository, sellerRepository, passwordEncoder, jwtPlugin, redisUtils, emailUtils, 5000)
//
//    @Test
//    fun `구매자 로그인 성공 테스트`() {
//        // given
//        val loginRequest = LoginRequest(email = "buyer@example.com", password = "password123")
//        val buyer = Buyer(
//            id = 1L,
//            email = "buyer@example.com",
//            password = "encodedPassword",
//            nickname = "buyer",
//            profileImage = "profile.png",
//            phoneNumber = "010-1234-5678",
//            address = "Seoul, Korea",
//            providerName = null,
//            providerId = null
//        )
//
//        every { buyerRepository.findByEmail(loginRequest.email) } returns buyer
//        every { passwordEncoder.matches(loginRequest.password, buyer.password) } returns true
//        every { jwtPlugin.generateAccessToken(buyer.id.toString(), buyer.email, "BUYER") } returns "token"
//
//        // when
//        val response = userService.loginBuyer(loginRequest)
//
//        // then
//        response.accessToken shouldBe "token"
//    }
//
//    @Test
//    fun `판매자 로그인 성공 테스트`() {
//        // given
//        val loginRequest = LoginRequest(email = "seller@example.com", password = "password123")
//        val seller = Seller(
//            id = 1L,
//            email = "seller@example.com",
//            password = "encodedPassword",
//            nickname = "seller",
//            profileImage = "profile.png",
//            phoneNumber = "010-1234-5678",
//            address = "Seoul, Korea"
//        )
//
//        every { sellerRepository.findByEmail(loginRequest.email) } returns seller
//        every { passwordEncoder.matches(loginRequest.password, seller.password) } returns true
//        every { jwtPlugin.generateAccessToken(seller.id.toString(), seller.email, "SELLER") } returns "token"
//
//        // when
//        val response = userService.loginSeller(loginRequest)
//
//        // then
//        response.accessToken shouldBe "token"
//    }
//
//    @Test
//    fun `로그인 실패 테스트 - 잘못된 이메일 또는 비밀번호`() {
//        // given
//        val loginRequest = LoginRequest(email = "nonexistent@example.com", password = "wrongpassword")
//
//        every { buyerRepository.findByEmail(loginRequest.email) } returns null
//        every { passwordEncoder.matches(loginRequest.password, any()) } returns false
//
//        // when & then
//        val exception = shouldThrow<BuyerLoginFailedException> { userService.loginBuyer(loginRequest) }
//        exception.message shouldBe "구매자 로그인 실패"
//    }
//}