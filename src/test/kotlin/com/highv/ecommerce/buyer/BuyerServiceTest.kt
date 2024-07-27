package com.highv.ecommerce.buyer

import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerPasswordRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerProfileRequest
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.buyer.service.BuyerService
import com.highv.ecommerce.s3.config.S3Manager
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.password.PasswordEncoder
import java.nio.charset.StandardCharsets

class BuyerServiceTest : DescribeSpec({
    val buyerRepository: BuyerRepository = mockk<BuyerRepository>()
    val passwordEncoder: PasswordEncoder = mockk<PasswordEncoder>()
    val s3Manager: S3Manager = mockk<S3Manager>()
    val buyerService: BuyerService = BuyerService(buyerRepository, passwordEncoder, s3Manager)

    afterEach {
        clearAllMocks()
    }

    describe("일반 회원이 비밀번호를 바꿀 때") {
        val buyerId = 1L
        val buyer = Buyer(
            nickname = "TestName",
            email = "test@test.com",
            profileImage = "testImage",
            phoneNumber = "010-1234-5678",
            address = "서울시-용산구-용산로-용산2길 19",
            password = "testPassword",
            providerId = null,
            providerName = null
        )


        context("현재 비밀번호와 확인 비밀번호가 일치하지 않는다면") {

            val request = UpdateBuyerPasswordRequest(
                currentPassword = "testPassword1",
                newPassword = "NewPassword",
                confirmNewPassword = "NewPassword"
            )
            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }
            every { passwordEncoder.matches(any(), any()) } returns false

            it("예외가 발생한다.") {
                shouldThrow<RuntimeException> {
                    buyerService.changePassword(request, buyerId)
                }.run {
                    message shouldBe "비밀번호가 일치하지 않습니다."
                }
            }
        }

        context("새 비밀번호와 확인 비밀번호 값이 다르다면") {

            val request = UpdateBuyerPasswordRequest(
                currentPassword = "testPassword",
                newPassword = "NewPassword",
                confirmNewPassword = "NewPassword1"
            )

            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }
            every { passwordEncoder.matches(any(), any()) } returns true

            it("예외가 발생한다.") {
                shouldThrow<RuntimeException> {
                    buyerService.changePassword(request, buyerId)
                }.run {
                    message shouldBe "변경할 비밀번호와 확인 비밀번호가 다릅니다."
                }
            }
        }

        context("변경 전 비밀번호와 변경 후 비밀번호가 같다면") {
            val request = UpdateBuyerPasswordRequest(
                currentPassword = "testPassword",
                newPassword = "testPassword",
                confirmNewPassword = "testPassword"
            )

            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }
            every { passwordEncoder.matches(request.currentPassword, buyer.password) } returns true
            every { passwordEncoder.matches(request.newPassword, buyer.password) } returns true

            it("예외가 발생한다.") {
                shouldThrow<RuntimeException> {
                    buyerService.changePassword(request, buyerId)
                }.run {
                    message shouldBe "현재 비밀번호와 수정할 비밀번호가 같습니다."
                }
            }
        }

        context("모든 조건이 만족될 경우") {
            val request = UpdateBuyerPasswordRequest(
                currentPassword = "testPassword",
                newPassword = "testPassword1",
                confirmNewPassword = "testPassword1"
            )

            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }
            every { passwordEncoder.matches(request.currentPassword, buyer.password) } returns true
            every { passwordEncoder.matches(request.newPassword, buyer.password) } returns false
            every { passwordEncoder.encode(any()) } returns "testPassword1"

            it("비밀번호가 변경된다.") {
                buyerService.changePassword(request, buyerId)
            }
        }

    }

    describe("소셜 로그인 이용자가") {

        val buyerId = 1L
        val buyer = Buyer(
            nickname = "TestName",
            email = "null",
            profileImage = "testImage",
            phoneNumber = "null",
            address = "null",
            password = "null",
            providerId = "123321",
            providerName = "naver"
        )

        context("비밀번호를 바꿀때") {

            val request = UpdateBuyerPasswordRequest(
                currentPassword = "testPassword",
                newPassword = "testPassword1",
                confirmNewPassword = "testPassword1"
            )
            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }

            it("예외가 발생한다.") {
                shouldThrow<RuntimeException> {
                    buyerService.changePassword(request, buyerId)
                }.apply {
                    message shouldBe "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다."
                }
            }
        }

    }

    describe("회원이 프로필 이미지를 변경하면") {
        val buyerId = 1L

        context("소셜 회원이면") {
            val buyer = Buyer(
                nickname = "TestName",
                email = "null",
                profileImage = "testImage",
                phoneNumber = "null",
                address = "null",
                password = "null",
                providerId = "123321",
                providerName = "naver"
            )

            val file = MockMultipartFile(
                "file", "test.txt", "text/plain", "hello file".byteInputStream(
                    StandardCharsets.UTF_8
                )
            )

            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }
            every { s3Manager.uploadFile(any()) } returns "lemon.jpg"
            every { s3Manager.getFile(any()) } returns "lemon.jpg"
            every { buyerRepository.save(any()) } returns buyer

            it("이미지가 변경된다.") {
                buyerService.changeProfileImage(buyerId, file)
            }
        }

        context("일반 회원이면") {
            val buyer = Buyer(
                nickname = "TestName",
                email = "test@test.com",
                profileImage = "testImage",
                phoneNumber = "010-1234-5678",
                address = "서울시-용산구-용산로-용산2길 19",
                password = "testPassword",
                providerId = null,
                providerName = null
            )

            val file = MockMultipartFile(
                "file", "test.txt", "text/plain", "hello file".byteInputStream(
                    StandardCharsets.UTF_8
                )
            )

            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }
            every { s3Manager.uploadFile(any()) } returns "lemon.jpg"
            every { s3Manager.getFile(any()) } returns "lemon.jpg"
            every { buyerRepository.save(any()) } returns buyer

            it("이미지가 변경된다.") {
                buyerService.changeProfileImage(buyerId, file)
            }
        }

    }

    describe("프로필을 수정할 때") {

        val buyerId = 1L

        context("일반 로그인 유저이면") {
            val buyer = Buyer(
                nickname = "TestName",
                email = "test@test.com",
                profileImage = "testImage",
                phoneNumber = "010-1234-5678",
                address = "서울시-용산구-용산로-용산2길 19",
                password = "testPassword",
                providerId = null,
                providerName = null
            ).apply { id = buyerId }

            val request: UpdateBuyerProfileRequest = UpdateBuyerProfileRequest(
                nickname = "수정한 닉네임",
                phoneNumber = "수정한 번호",
                address = "수정한 주소"
            )

            every { buyerRepository.findByIdOrNull(any()) } returns buyer
            every { buyerRepository.save(any()) } returns buyer

            it("닉네임, 핸드폰 번호, 주소가 수정된다.") {
                val result = buyerService.changeProfile(request, buyerId)

                result.nickname shouldBe "수정한 닉네임"
                result.phoneNumber shouldBe "수정한 번호"
                result.address shouldBe "수정한 주소"

            }

        }

        context("소셜 로그인 유저면") {

            val buyer = Buyer(
                nickname = "TestName",
                email = "null",
                profileImage = "testImage",
                phoneNumber = "null",
                address = "null",
                password = "testPassword",
                providerId = "123321",
                providerName = "kakao"
            ).apply { id = buyerId }

            val request: UpdateBuyerProfileRequest = UpdateBuyerProfileRequest(
                nickname = "수정한 닉네임",
                phoneNumber = "수정한 번호",
                address = "수정한 주소"
            )

            every { buyerRepository.findByIdOrNull(any()) } returns buyer
            every { buyerRepository.save(any()) } returns buyer

            it("핸드폰 번호, 주소만 수정된다.") {
                val result = buyerService.changeProfile(request, buyerId)

                result.nickname shouldBe "TestName"
                result.phoneNumber shouldBe "수정한 번호"
                result.address shouldBe "수정한 주소"
            }

        }
    }

})