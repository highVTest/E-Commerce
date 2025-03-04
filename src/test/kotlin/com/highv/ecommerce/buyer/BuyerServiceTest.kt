package com.highv.ecommerce.buyer

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.DuplicatePasswordException
import com.highv.ecommerce.common.exception.PasswordMismatchException
import com.highv.ecommerce.common.exception.SocialLoginException
import com.highv.ecommerce.common.exception.ValidationException
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerImageRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerPasswordRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerProfileRequest
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.buyer.service.BuyerService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder

class BuyerServiceTest : DescribeSpec({
    val buyerRepository: BuyerRepository = mockk<BuyerRepository>()
    val passwordEncoder: PasswordEncoder = mockk<PasswordEncoder>()
    val buyerService: BuyerService = BuyerService(buyerRepository, passwordEncoder)

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
            every { passwordEncoder.matches(request.currentPassword, buyer.password) } returns false

            it("예외가 발생한다.") {
                shouldThrow<PasswordMismatchException> {
                    buyerService.changePassword(request, buyerId)
                }.run {
                    message shouldBe "현재 비밀번호가 일치하지 않습니다."
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
            every { passwordEncoder.matches(request.currentPassword, buyer.password) } returns true

            it("예외가 발생한다.") {
                shouldThrow<PasswordMismatchException> {
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
                shouldThrow<DuplicatePasswordException> {
                    buyerService.changePassword(request, buyerId)
                }.run {
                    message shouldBe "현재 비밀번호와 수정할 비밀번호가 같습니다."
                }
            }
        }

        context("모든 조건이 만족될 경우") {
            val request = UpdateBuyerPasswordRequest(
                currentPassword = "testPassword",
                newPassword = "new password",
                confirmNewPassword = "new password",
            )
            val buyerSlot = slot<Buyer>()
            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }
            every { passwordEncoder.matches(request.currentPassword, buyer.password) } returns true
            every { passwordEncoder.matches(request.newPassword, buyer.password) } returns false
            every { passwordEncoder.encode(any()) } returns "new password"
            every { buyerRepository.save(capture(buyerSlot)) } returns buyer

            it("비밀번호가 변경된다.") {
                val response = buyerService.changePassword(request, buyerId)
                response.msg shouldBe "비밀번호가 변경되었습니다."

                buyerSlot.captured.password shouldBe "new password"
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
                shouldThrow<SocialLoginException> {
                    buyerService.changePassword(request, buyerId)
                }.apply {
                    message shouldBe "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다."
                }
            }
        }

    }

    describe("회원이 프로필 이미지를 변경하면") {
        val buyerId = 1L
        val request = UpdateBuyerImageRequest(imageUrl = "change Image")

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

            val buyerSlot = slot<Buyer>()


            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }
            every { buyerRepository.save(capture(buyerSlot)) } returns buyer



            it("이미지가 변경된다.") {
                val response = buyerService.changeProfileImage(request, buyerId)

                response.msg shouldBe "프로필 이미지가 변경되었습니다."

                buyerSlot.captured.profileImage shouldBe "change Image"
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

            val buyerSlot = slot<Buyer>()

            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }
            every { buyerRepository.save(capture(buyerSlot)) } returns buyer

            it("이미지가 변경된다.") {
                val response: DefaultResponse = buyerService.changeProfileImage(request, buyerId)

                response.msg shouldBe "프로필 이미지가 변경되었습니다."

                buyerSlot.captured.profileImage shouldBe "change Image"
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

        context("일반 회원이고 변경 닉네임이 공백이면") {
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
                nickname = "",
                phoneNumber = "수정한 번호",
                address = "수정한 주소"
            )

            every { buyerRepository.findByIdOrNull(any()) } returns buyer
            every { buyerRepository.save(any()) } returns buyer

            it("예외가 발생한다.") {
                shouldThrow<ValidationException> {
                    buyerService.changeProfile(request, buyerId)
                }.run {
                    message shouldBe "닉네임이 공백일 수 없습니다."
                }
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

    describe("회원 정보 조회 시") {
        val buyerId = 1L

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
            ).apply { id = buyerId }

            every { buyerRepository.findByIdOrNull(buyerId) } returns buyer

            it("회원 정보가 반환된다.") {
                val response = buyerService.getMyProfile(buyerId)

                response.id shouldBe buyerId
                response.nickname shouldBe "TestName"
                response.email shouldBe "test@test.com"
                response.phoneNumber shouldBe "010-1234-5678"
                response.address shouldBe "서울시-용산구-용산로-용산2길 19"

            }
        }

        context("소셜 회원이면") {
            val buyer = Buyer(
                nickname = "TestName",
                email = "null",
                profileImage = "testImage",
                phoneNumber = "010-1234-5678",
                address = "서울시-용산구-용산로-용산2길 19",
                password = "testPassword",
                providerId = "123321",
                providerName = "kakao"
            ).apply { id = buyerId }

            every { buyerRepository.findByIdOrNull(buyerId) } returns buyer

            it("회원정보가 반환된다.") {
                val response = buyerService.getMyProfile(buyerId)

                response.id shouldBe buyerId
                response.nickname shouldBe "TestName"
                response.email shouldBe "null"
                response.phoneNumber shouldBe "010-1234-5678"
                response.address shouldBe "서울시-용산구-용산로-용산2길 19"
            }
        }

    }
})

