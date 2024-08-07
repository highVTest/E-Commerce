package com.highv.ecommerce.login

import com.highv.ecommerce.common.exception.EmailNotVerifiedException
import com.highv.ecommerce.common.exception.EmailVerificationNotFoundException
import com.highv.ecommerce.common.exception.UnauthorizedEmailException
import com.highv.ecommerce.common.exception.UnverifiedEmailException
import com.highv.ecommerce.domain.buyer.dto.request.CreateBuyerRequest
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.buyer.service.BuyerService
import com.highv.ecommerce.domain.seller.dto.CreateSellerRequest
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.domain.seller.service.SellerService
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import com.highv.ecommerce.infra.s3.S3Manager
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.multipart.MultipartFile

class SignUpServiceTest {

    // 구매자 테스트
    private val buyerRepository: BuyerRepository = mockk<BuyerRepository>()
    private val passwordEncoder: PasswordEncoder = mockk<PasswordEncoder>()
    private val s3Manager: S3Manager = mockk<S3Manager>()
    private val buyerService: BuyerService = BuyerService(buyerRepository, passwordEncoder)
    private val shopRepository = mockk<ShopRepository>()

    @Test
    fun `이메일 인증을 마친 구매자가 회원가입 시 성공한다`() {
        // Given
        val buyerId = 1L
        // 이메일 인증 완료 시 생긴 구매자 객체
        val buyer = Buyer(
            id = buyerId,
            nickname = "",
            password = "",
            email = "test@email.com",
            profileImage = "",
            phoneNumber = "",
            address = ""
        )

        val request = CreateBuyerRequest(
            id = buyerId,
            nickname = "테스트 닉네임",
            password = "테스트 비밀번호",
            email = "test@email.com",
            phoneNumber = "테스트 폰 번호",
            address = "테스트 주소",
            profileImage = "테스트 이미지"
        )

        // When
        every { buyerRepository.findByIdOrNull(buyerId) } returns buyer
        every { buyerRepository.save(buyer) } returns buyer
        every { passwordEncoder.encode(request.password) } returns "123456"

        val response = buyerService.signUp(request)

        // then
        response.id shouldBe buyerId
        response.nickname shouldBe request.nickname
        response.email shouldBe request.email
        response.address shouldBe request.address
        response.phoneNumber shouldBe request.phoneNumber
        response.profileImage shouldBe "테스트 이미지"
    }

    @Test
    fun `이메일 인증이 안된 경우 구매자 회원가입 시 예외가 발생한다`() {
        // Given
        val buyerId = 1L
        // 이메일 인증 완료 시 생긴 구매자 객체
        val buyer = Buyer(
            id = buyerId,
            nickname = "",
            password = "",
            email = "test@email.com",
            profileImage = "",
            phoneNumber = "",
            address = ""
        )

        val request = CreateBuyerRequest(
            id = buyerId,
            nickname = "테스트 닉네임",
            password = "테스트 비밀번호",
            email = "test2@email.com",
            phoneNumber = "테스트 폰 번호",
            address = "테스트 주소",
            profileImage = ""
        )

        // when & then
        // 회원 정보가 있으나 내가 인증한 이메일이 아닌 경우
        every { buyerRepository.findByIdOrNull(buyerId) } returns buyer
        shouldThrow<UnauthorizedEmailException> {
            buyerService.signUp(request)
        }.message shouldBe "인증되지 않은 이메일입니다."

        // When & then
        // 이메일 인증을 안 한 경우
        every { buyerRepository.findByIdOrNull(buyerId) } returns null
        shouldThrow<EmailNotVerifiedException> {
            buyerService.signUp(request)
        }.message shouldBe "이메일 인증된 회원 정보가 없습니다."
    }

    // 판매자 테스트
    private val sellerRepository = mockk<SellerRepository>()
    private val sellerService = SellerService(sellerRepository, passwordEncoder, s3Manager, shopRepository)

    @Test
    fun `이메일 인증을 마친 판매자 회원가입 시 성공한다`() {
        // Given
        val sellerId = 1L
        // 이메일 인증 완료 시 생긴 구매자 객체
        val seller = Seller(
            id = sellerId,
            nickname = "",
            password = "",
            email = "test@email.com",
            profileImage = "",
            phoneNumber = "",
            address = ""
        )

        val request = CreateSellerRequest(
            id = sellerId,
            nickname = "테스트 닉네임",
            password = "테스트 비밀번호",
            email = "test@email.com",
            phoneNumber = "테스트 폰 번호",
            address = "테스트 주소"
        )

        // 이미지 없음
        val file: MultipartFile? = null

        // When
        every { sellerRepository.findByIdOrNull(sellerId) } returns seller
        every { sellerRepository.save(seller) } returns seller
        every { passwordEncoder.encode(request.password) } returns "123456"

        val response = sellerService.signUp(request, file)

        // then
        response.id shouldBe sellerId
        response.nickname shouldBe request.nickname
        response.email shouldBe request.email
        response.address shouldBe request.address
        response.phoneNumber shouldBe request.phoneNumber
        response.profileImage shouldBe ""
    }

    @Test
    fun `이메일 인증이 안된 경우 팜매자 회원가입 시 예외가 발생한다`() {
        // Given
        val sellerId = 1L
        // 이메일 인증 완료 시 생긴 구매자 객체
        val seller = Seller(
            id = sellerId,
            nickname = "",
            password = "",
            email = "test@email.com",
            profileImage = "",
            phoneNumber = "",
            address = ""
        )

        val request = CreateSellerRequest(
            id = sellerId,
            nickname = "테스트 닉네임",
            password = "테스트 비밀번호",
            email = "test22@email.com",
            phoneNumber = "테스트 폰 번호",
            address = "테스트 주소"
        )

        // 이미지 없음
        val file: MultipartFile? = null

        // when & then
        every { sellerRepository.findByIdOrNull(sellerId) } returns seller
        shouldThrow<UnverifiedEmailException> {
            sellerService.signUp(request, file)
        }.message shouldBe "인증되지 않은 이메일입니다."

        // When & then
        every { sellerRepository.findByIdOrNull(sellerId) } returns null
        shouldThrow<EmailVerificationNotFoundException> {
            sellerService.signUp(request, file)
        }.message shouldBe "이메일 인증된 회원 정보가 없습니다."
    }
}