package com.highv.ecommerce.backoffice

import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdatePasswordRequest
import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdateSellerRequest
import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdateShopRequest
import com.highv.ecommerce.domain.backoffice.service.SellerInfoService
import com.highv.ecommerce.domain.seller.dto.ActiveStatus
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder

class SellerInfoServiceTest : BehaviorSpec({
    val shopRepository = mockk<ShopRepository>()
    val sellerRepository = mockk<SellerRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()

    val sellerInfoService = SellerInfoService(shopRepository, sellerRepository, passwordEncoder)

    Given("updateShopInfo 를 실행 시킬 때") {
        val updateShopRequest = UpdateShopRequest(
            description = "변경된 정보"
        )

        every { shopRepository.findShopBySellerId(seller.id!!) } returns shop

        shop.description = updateShopRequest.description

        every { shopRepository.save(any()) } returns shop

        When("shop 정보를 수정할때") {
            val result = sellerInfoService.updateShopInfo(seller.id!!, updateShopRequest)
            Then("변경된 정보(shop 의 설명)를 리턴한다.") {
                result.description shouldBe updateShopRequest.description
            }
        }
    }

    Given("updateSellerInfo 를 실행 시킬 때") {
        val sellerId = 1L
        val updateSellerRequest = UpdateSellerRequest(
            nickname = "HR1",
            phoneNumber = "010-9876-4321",
            address = "집주소"
        )

        every { sellerRepository.findByIdOrNull(sellerId) } returns seller

        seller.nickname = updateSellerRequest.nickname
        seller.phoneNumber = updateSellerRequest.phoneNumber
        seller.address = updateSellerRequest.address

        every { sellerRepository.save(any()) } returns seller

        When("seller 정보를 수정할때") {
            val result = sellerInfoService.updateSellerInfo(sellerId, updateSellerRequest)
            Then("변경된 정보(seller 의 이름, 주소, 전화번호, 이미지)를 리턴한다.") {
                result.address shouldBe updateSellerRequest.address
                result.nickname shouldBe updateSellerRequest.nickname
                result.phoneNumber shouldBe updateSellerRequest.phoneNumber
            }
        }
    }

    Given("changePassword 를 실행 시킬 때") {
        val oldPassword = "oldPassword"
        val newPassword = "newPassword"

        val updatePasswordRequest = UpdatePasswordRequest(
            currentPassword = oldPassword,
            newPassword = newPassword,
            confirmNewPassword = newPassword
        )

        val updatedSeller = Seller(
            id = 1L,
            nickname = "HR",
            password = updatePasswordRequest.newPassword,
            email = "HR@test.com",
            profileImage = "profileImage",
            phoneNumber = "010-1234-5678",
            address = "주소입니다",
            activeStatus = ActiveStatus.APPROVED
        )

        val passwordSlot = slot<String>()

        every { sellerRepository.findByIdOrNull(seller.id!!) } returns seller
        every { passwordEncoder.matches(oldPassword, seller.password) } returns true
        every { passwordEncoder.matches(newPassword, seller.password) } returns false
        every { passwordEncoder.encode(capture(passwordSlot)) } returns newPassword
        every { sellerRepository.save(any()) } returns updatedSeller

        When("seller 의 비밀번호를 수정 할 때") {
            val result = sellerInfoService.changePassword(seller.id!!, updatePasswordRequest)

            Then("비밀번호 변경 완료 문자를 리턴한다.") {
                result.msg shouldBe "비밀번호가 변경되었습니다."
            }

            Then("비밀번호가 성공적으로 변경된다.") {
                passwordSlot.captured shouldBe newPassword
                every { passwordEncoder.encode(newPassword) } returns newPassword
            }
        }
    }

    afterEach {
        clearAllMocks()
    }
}) {
    companion object {
        private val shop = Shop(
            sellerId = 1L,
            name = "name",
            description = "description",
            shopImage = "shopImage",
            rate = 1f
        ).apply { id = 1L }

        private val seller = Seller(
            id = 1L,
            nickname = "HR",
            password = "123456789",
            email = "HR@test.com",
            profileImage = "profileImage",
            phoneNumber = "010-1234-5678",
            address = "주소입니다",
            activeStatus = ActiveStatus.APPROVED
        )
    }
}