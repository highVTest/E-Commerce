package com.highv.ecommerce.login

import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.login.dto.LoginRequest
import com.highv.ecommerce.domain.login.service.UserService
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.infra.security.jwt.JwtPlugin
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.security.crypto.password.PasswordEncoder

class loginUserServiceTest {

    private val buyerRepository: BuyerRepository = mock(BuyerRepository::class.java)
    private val sellerRepository: SellerRepository = mock(SellerRepository::class.java)
    private val passwordEncoder: PasswordEncoder = mock(PasswordEncoder::class.java)
    private val jwtPlugin: JwtPlugin = mock(JwtPlugin::class.java)
    private val userService: UserService = UserService(buyerRepository, sellerRepository, passwordEncoder, jwtPlugin)

    @Test
    fun `구매자 로그인 성공 테스트`() {
        // given
        val loginRequest = LoginRequest(email = "buyer@example.com", password = "password123", role = "BUYER")
        val buyer = Buyer(
            id = 1L,
            email = "buyer@example.com",
            password = "encodedPassword",
            nickname = "buyer",
            profileImage = "profile.png",
            phoneNumber = "010-1234-5678",
            address = "Seoul, Korea",
            providerName = null,
            providerId = null
        )

        `when`(buyerRepository.findByEmail(loginRequest.email)).thenReturn(buyer)
        `when`(passwordEncoder.matches(loginRequest.password, buyer.password)).thenReturn(true)
        `when`(jwtPlugin.generateAccessToken(buyer.id.toString(), buyer.email, "BUYER")).thenReturn("token")

        // when
        val response = userService.login(loginRequest)

        // then
        assertEquals("token", response.accessToken)
    }

    @Test
    fun `판매자 로그인 성공 테스트`() {
        // given
        val loginRequest = LoginRequest(email = "seller@example.com", password = "password123", role = "SELLER")
        val seller = Seller(
            id = 1L,
            email = "seller@example.com",
            password = "encodedPassword",
            nickname = "seller",
            profileImage = "profile.png",
            phoneNumber = "010-1234-5678",
            address = "Seoul, Korea"
        )

        `when`(sellerRepository.findByEmail(loginRequest.email)).thenReturn(seller)
        `when`(passwordEncoder.matches(loginRequest.password, seller.password)).thenReturn(true)
        `when`(jwtPlugin.generateAccessToken(seller.id.toString(), seller.email, "SELLER")).thenReturn("token")

        // when
        val response = userService.login(loginRequest)

        // then
        assertEquals("token", response.accessToken)
    }

    @Test
    fun `로그인 실패 테스트 - 잘못된 이메일 또는 비밀번호`() {
        // given
        val loginRequest = LoginRequest(email = "nonexistent@example.com", password = "wrongpassword", role = "BUYER")

        `when`(buyerRepository.findByEmail(loginRequest.email)).thenReturn(null)

        // when & then
        val exception = assertThrows<IllegalArgumentException> { userService.login(loginRequest) }
        assertEquals("Invalid email or password", exception.message)
    }

    @Test
    fun `로그인 실패 테스트 - 역할 누락`() {
        // given
        val loginRequest = LoginRequest(email = "buyer@example.com", password = "password123", role = "UNKNOWN")

        // when & then
        val exception = assertThrows<IllegalArgumentException> { userService.login(loginRequest) }
        assertEquals("Invalid email or password", exception.message)
    }
}