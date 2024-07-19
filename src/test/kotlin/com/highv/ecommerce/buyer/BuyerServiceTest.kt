package com.highv.ecommerce.buyer

import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerPasswordRequest
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.buyer.service.BuyerService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder

class BuyerServiceTest : BehaviorSpec({
    val buyerRepository: BuyerRepository = mockk<BuyerRepository>()
    val passwordEncoder: PasswordEncoder = mockk<PasswordEncoder>()
    val buyerService: BuyerService = BuyerService(buyerRepository, passwordEncoder)

    afterEach {
        clearAllMocks()
    }

    Given("일반 회원이 비밀번호를 바꿀 때") {
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


        When("현재 비밀번호와 확인 비밀번호가 일치하지 않는다면") {

            val request = UpdateBuyerPasswordRequest(
                currentPassword = "testPassword",
                newPassword = "NewPassword",
                confirmNewPassword = "NewPassword"
            )
            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }
            every { passwordEncoder.matches(any(), any()) } returns false

            Then("예외가 발생한다.") {
                shouldThrow<RuntimeException> {
                    buyerService.changePassword(request, buyerId)
                }.run {
                    message shouldBe "비밀번호가 일치하지 않습니다."
                }
            }
        }

        When("변경할 비밀번호와 변경할 비밀번호 확인 값이 다르다면") {

            val request = UpdateBuyerPasswordRequest(
                currentPassword = "testPassword",
                newPassword = "NewPassword",
                confirmNewPassword = "NewPassword1"
            )

            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }
            every { passwordEncoder.matches(any(), any()) } returns true

            Then("예외가 발생한다.") {
                shouldThrow<RuntimeException> {
                    buyerService.changePassword(request, buyerId)
                }.run {
                    message shouldBe "변경할 비밀번호와 확인 비밀번호가 다릅니다."
                }
            }
        }

        When("현재 비밀번호와 변경할 비밀번호가 같다면") {
            val request = UpdateBuyerPasswordRequest(
                currentPassword = "testPassword",
                newPassword = "testPassword",
                confirmNewPassword = "testPassword"
            )

            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }
            every { passwordEncoder.matches(request.currentPassword, buyer.password) } returns true
            every { passwordEncoder.matches(request.newPassword, buyer.password) } returns true

            Then("예외가 발생한다.") {
                shouldThrow<RuntimeException> {
                    buyerService.changePassword(request, buyerId)
                }.run {
                    message shouldBe "현재 비밀번호와 수정할 비밀번호가 같습니다."
                }
            }
        }

        When("현재 비밀번호 확인도 맞고 변경할 비밀번호와 현재 비밀번호가 다르다면") {
            val request = UpdateBuyerPasswordRequest(
                currentPassword = "testPassword",
                newPassword = "testPassword1",
                confirmNewPassword = "testPassword1"
            )

            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }
            every { passwordEncoder.matches(request.currentPassword, buyer.password) } returns true
            every { passwordEncoder.matches(request.newPassword, buyer.password) } returns false
            every { passwordEncoder.encode(any()) } returns "testPassword1"

            Then("비밀번호가 변경된다.") {
                buyerService.changePassword(request, buyerId)
            }
        }

    }

    Given("비밀번호를 바꿀때") {

        val buyerId = 1L
        val buyer = Buyer(
            nickname = "TestName",
            email = "null",
            profileImage = "testImage",
            phoneNumber = "null",
            address = "null",
            password = "null",
            providerId = 123132,
            providerName = "naver"
        )

        When("소셜 로그인 사용자이면") {

            val request = UpdateBuyerPasswordRequest(
                currentPassword = "testPassword",
                newPassword = "testPassword1",
                confirmNewPassword = "testPassword1"
            )
            every { buyerRepository.findByIdOrNull(any()) } returns buyer.apply { id = buyerId }

            Then("예외가 발생한다.") {
                shouldThrow<RuntimeException> {
                    buyerService.changePassword(request, buyerId)
                }.apply {
                    message shouldBe "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다."
                }
            }
        }

    }

})