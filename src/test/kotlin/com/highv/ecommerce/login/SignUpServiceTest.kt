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
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.clearAllMocks
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.multipart.MultipartFile

class SignUpServiceTest : BehaviorSpec({

    val buyerRepository: BuyerRepository = mockk()
    val passwordEncoder: PasswordEncoder = mockk()
    val s3Manager: S3Manager = mockk()
    val buyerService: BuyerService = BuyerService(buyerRepository, passwordEncoder, s3Manager)
    val shopRepository = mockk<ShopRepository>()

    val sellerRepository = mockk<SellerRepository>()
    val sellerService = SellerService(sellerRepository, passwordEncoder, s3Manager, shopRepository)

    afterEach {
        clearAllMocks()
    }

    Given("이메일 인증을 마친 구매자가 회원가입 시") {
        val buyerId = 1L
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
            address = "테스트 주소"
        )
        val file: MultipartFile? = null

        every { buyerRepository.findByIdOrNull(buyerId) } returns buyer
        every { buyerRepository.save(buyer) } returns buyer
        every { passwordEncoder.encode(request.password) } returns "123456"

        When("구매자가 로그인을 시도할 때") {
            val response = buyerService.signUp(request, file)

            Then("성공한다") {
                response.id shouldBe buyerId
                response.nickname shouldBe request.nickname
                response.email shouldBe request.email
                response.address shouldBe request.address
                response.phoneNumber shouldBe request.phoneNumber
                response.profileImage shouldBe ""
            }
        }
    }

    Given("이메일 인증이 안된 경우 구매자 회원가입 시") {
        val buyerId = 1L
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
            address = "테스트 주소"
        )
        val file: MultipartFile? = null

        every { buyerRepository.findByIdOrNull(buyerId) } returns buyer

        When("구매자가 회원가입을 시도할 때") {
            Then("UnauthorizedEmailException이 발생한다") {
                shouldThrow<UnauthorizedEmailException> {
                    buyerService.signUp(request, file)
                }.message shouldBe "인증되지 않은 이메일입니다."
            }
        }

        every { buyerRepository.findByIdOrNull(buyerId) } returns null

        When("구매자가 회원가입을 시도할 때") {
            Then("EmailNotVerifiedException이 발생한다") {
                shouldThrow<EmailNotVerifiedException> {
                    buyerService.signUp(request, file)
                }.message shouldBe "이메일 인증된 회원 정보가 없습니다."
            }
        }
    }

    Given("이메일 인증을 마친 판매자 회원가입 시") {
        val sellerId = 1L
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
        val file: MultipartFile? = null

        every { sellerRepository.findByIdOrNull(sellerId) } returns seller
        every { sellerRepository.save(seller) } returns seller
        every { passwordEncoder.encode(request.password) } returns "123456"

        When("판매자가 회원가입을 시도할 때") {
            val response = sellerService.signUp(request, file)

            Then("성공한다") {
                response.id shouldBe sellerId
                response.nickname shouldBe request.nickname
                response.email shouldBe request.email
                response.address shouldBe request.address
                response.phoneNumber shouldBe request.phoneNumber
                response.profileImage shouldBe ""
            }
        }
    }

    Given("이메일 인증이 안된 경우 판매자 회원가입 시") {
        val sellerId = 1L
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
        val file: MultipartFile? = null

        every { sellerRepository.findByIdOrNull(sellerId) } returns seller

        When("판매자가 회원가입을 시도할 때") {
            Then("UnverifiedEmailException이 발생한다") {
                shouldThrow<UnverifiedEmailException> {
                    sellerService.signUp(request, file)
                }.message shouldBe "인증되지 않은 이메일입니다."
            }
        }

        every { sellerRepository.findByIdOrNull(sellerId) } returns null

        When("판매자가 회원가입을 시도할 때") {
            Then("EmailVerificationNotFoundException이 발생한다") {
                shouldThrow<EmailVerificationNotFoundException> {
                    sellerService.signUp(request, file)
                }.message shouldBe "이메일 인증된 회원 정보가 없습니다."
            }
        }
    }
})
