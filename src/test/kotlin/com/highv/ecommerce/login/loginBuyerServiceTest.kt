//package com.highv.ecommerce.login
//
//import com.highv.ecommerce.domain.buyer.dto.request.CreateBuyerRequest
//import com.highv.ecommerce.domain.buyer.entity.Buyer
//import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
//import com.highv.ecommerce.domain.buyer.service.BuyerService
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//import org.mockito.Mockito.*
//import org.springframework.security.crypto.password.PasswordEncoder
//
//// Mockito를 사용하여 Mock 객체를 활용
//class loginBuyerServiceTest {
//
//    private val buyerRepository: BuyerRepository = mock(BuyerRepository::class.java)
//    private val passwordEncoder: PasswordEncoder = mock(PasswordEncoder::class.java)
//    private val buyerService: BuyerService = BuyerService(buyerRepository, passwordEncoder)
//
//    @Test
//    fun `회원 가입 성공 테스트`() {
//        // given
//        val request = CreateBuyerRequest(
//            id = 1L,
//            email = "test@example.com",
//            nickname = "tester",
//            password = "password123",
//            phoneNumber = "010-1234-5678",
//            address = "Gwangju, Korea"
//        )
//
//        `when`(buyerRepository.existsByEmail(request.email)).thenReturn(false)
//        `when`(passwordEncoder.encode(request.password)).thenReturn("encodedPassword")
//        `when`(buyerRepository.save(any(Buyer::class.java))).thenReturn(
//            Buyer(
//                id = 1L,
//                email = request.email,
//                nickname = request.nickname,
//                password = "encodedPassword",
//                profileImage = "test@example.com",
//                phoneNumber = request.phoneNumber,
//                address = request.address,
//                providerName = null,
//                providerId = null
//            )
//        )
//
//        // when
//        val response = buyerService.signUp(request)
//
//        // then
//        assertEquals(request.email, response.email)
//        assertEquals(request.nickname, response.nickname)
//        assertNotNull(response.id)
//    }
//
//    @Test
//    fun `이미 존재하는 이메일로 회원 가입 실패 테스트`() {
//        // given
//        val request = CreateBuyerRequest(
//            email = "test@example.com",
//            nickname = "tester",
//            password = "password123",
//            profileImage = "profile.png",
//            phoneNumber = "010-1234-5678",
//            address = "Gwangju, Korea"
//        )
//
//        `when`(buyerRepository.existsByEmail(request.email)).thenReturn(true)
//
//        // when & then
//        val exception = assertThrows<RuntimeException> { buyerService.signUp(request) }
//        assertEquals("이미 존재하는 이메일입니다. 가입할 수 없습니다.", exception.message)
//    }
//}