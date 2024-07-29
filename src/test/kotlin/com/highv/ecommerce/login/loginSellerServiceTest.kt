//package com.highv.ecommerce.login
//
//import com.highv.ecommerce.domain.seller.dto.CreateSellerRequest
//import com.highv.ecommerce.domain.seller.entity.Seller
//import com.highv.ecommerce.domain.seller.repository.SellerRepository
//import com.highv.ecommerce.domain.seller.service.SellerService
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//import org.mockito.Mockito.*
//import org.springframework.security.crypto.password.PasswordEncoder
//
//// Mockito를 사용하여 Mock 객체를 활용
//class loginSellerServiceTest {
//
//    private val sellerRepository: SellerRepository = mock(SellerRepository::class.java)
//    private val passwordEncoder: PasswordEncoder = mock(PasswordEncoder::class.java)
//    private val sellerService: SellerService = SellerService(sellerRepository, passwordEncoder)
//
//    @Test
//    fun `회원 가입 성공 테스트`() {
//        // given
//        val request = CreateSellerRequest(
//            email = "test@example.com",
//            nickname = "tester",
//            password = "password123",
//            profileImage = "profile.png",
//            phoneNumber = "010-1234-5678",
//            address = "Gwangju, Korea"
//        )
//
//        `when`(sellerRepository.existsByEmail(request.email)).thenReturn(false)
//        `when`(passwordEncoder.encode(request.password)).thenReturn("encodedPassword")
//        `when`(sellerRepository.save(any(Seller::class.java))).thenReturn(
//            Seller(
//                id = 1L,
//                email = request.email,
//                nickname = request.nickname,
//                password = "encodedPassword",
//                profileImage = request.profileImage,
//                phoneNumber = request.phoneNumber,
//                address = request.address
//            )
//        )
//
//        // when
//        val response = sellerService.signUp(request)
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
//        val request = CreateSellerRequest(
//            email = "test@example.com",
//            nickname = "tester",
//            password = "password123",
//            profileImage = "profile.png",
//            phoneNumber = "010-1234-5678",
//            address = "Gwangju, Korea"
//        )
//
//        `when`(sellerRepository.existsByEmail(request.email)).thenReturn(true)
//
//        // when & then
//        val exception = assertThrows<RuntimeException> { sellerService.signUp(request) }
//        assertEquals("이미 존재하는 이메일입니다. 가입할 수 없습니다.", exception.message)
//    }
//}