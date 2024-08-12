//package com.highv.ecommerce.backoffice
//
//import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdatePasswordRequest
//import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdateSellerRequest
//import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdateShopRequest
//import com.highv.ecommerce.domain.backoffice.service.SellerInfoService
//import com.highv.ecommerce.domain.seller.entity.Seller
//import com.highv.ecommerce.domain.seller.repository.SellerRepository
//import com.highv.ecommerce.domain.seller.shop.entity.Shop
//import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
//import io.kotest.core.spec.style.BehaviorSpec
//import io.kotest.matchers.shouldBe
//import io.mockk.clearAllMocks
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.slot
//import org.springframework.data.repository.findByIdOrNull
//import org.springframework.security.crypto.password.PasswordEncoder
//
//class SellerInfoServiceTest : BehaviorSpec({
//    val shopRepository = mockk<ShopRepository>()
//    val sellerRepository = mockk<SellerRepository>()
//    val passwordEncoder = mockk<PasswordEncoder>()
//
//    val sellerInfoService = SellerInfoService(shopRepository, sellerRepository,passwordEncoder)
//
//    Given("updateShopInfo 를 실행 시킬 때"){
//        val sellerId = 1L
//        val updateShopRequest= UpdateShopRequest(
//            description = "변경된 정보",
//            shopImage = "이미지 변경됨"
//        )
//        val updatedShop = Shop(
//            sellerId = sellerId,
//            name = "HR shop",
//            description = updateShopRequest.description,
//            shopImage = updateShopRequest.shopImage,
//            rate = 0.0f
//        ).apply{
//            id = 1L
//        }
//        every { shopRepository.findShopBySellerId(sellerId) } returns shop
//        every { shopRepository.save(any()) } returns updatedShop
//
//        When("shop 정보를 수정할때"){
//            val result = sellerInfoService.updateShopInfo(sellerId, updateShopRequest)
//            Then("변경된 정보(shop 의 이름, 설명, 이미지)를 리턴한다."){
//                result.description shouldBe updateShopRequest.description
//                result.shopImage shouldBe updateShopRequest.shopImage
//            }
//        }
//    }
//
//    Given("updateSellerInfo 를 실행 시킬 때"){
//        val sellerId = 1L
//        val updateSellerRequest= UpdateSellerRequest(
//            nickname="HR1",
//            profileImage="Image1",
//            phoneNumber = "010-9876-4321",
//            address = "집주소"
//        )
//        val updatedSeller = Seller(
//            id = sellerId,
//            nickname =updateSellerRequest.nickname,
//            password = "12345678",
//            email="HR@test.com",
//            profileImage=updateSellerRequest.profileImage,
//            phoneNumber=updateSellerRequest.phoneNumber,
//            address =updateSellerRequest.address
//        )
//
//        every { sellerRepository.findByIdOrNull(sellerId) } returns seller
//        every { sellerRepository.save(any()) } returns updatedSeller
//
//        When("seller 정보를 수정할때"){
//            val result= sellerInfoService.updateSellerInfo(sellerId, updateSellerRequest)
//            Then("변경된 정보(seller 의 이름, 주소, 전화번호, 이미지)를 리턴한다."){
//                result.address shouldBe updateSellerRequest.address
//                result.nickname shouldBe updateSellerRequest.nickname
//                result.phoneNumber shouldBe updateSellerRequest.phoneNumber
//                result.profileImage shouldBe updateSellerRequest.profileImage
//            }
//        }
//    }
//
//    Given("changePassword 를 실행 시킬 때"){
//        val oldPassword = "oldPassword"
//        val newPassword = "newPassword"
//
//        val sellerId = 1L
//        val seller = Seller(
//            id = sellerId,
//            password = oldPassword,
//            nickname = "HR",
//            email = "HR@test.com",
//            profileImage = "이미지~",
//            phoneNumber = "010-9876-4321",
//            address = "주소"
//        )
//        val updatePasswordRequest = UpdatePasswordRequest(
//            oldPassword = oldPassword,
//            newPassword = newPassword
//        )
//        val updatedSeller = Seller(
//            id = sellerId,
//            password = newPassword,
//            nickname = "HR",
//            email = "HR@test.com",
//            profileImage = "이미지~",
//            phoneNumber = "010-9876-4321",
//            address = "주소"
//        )
//
//        val passwordSlot = slot<String>()
//
//        every { sellerRepository.findByIdOrNull(sellerId) } returns seller
//        every { passwordEncoder.matches(oldPassword, seller.password) } returns true
//        every { passwordEncoder.matches(newPassword, seller.password) } returns true
//        every { passwordEncoder.encode(capture(passwordSlot))} returns newPassword
//        every { sellerRepository.save(any()) } returns updatedSeller
//
//        When("seller 의 비밀번호를 수정 할 때"){
//            val result = sellerInfoService.changePassword(sellerId, updatePasswordRequest)
//            Then("비밀번호 변경 완료 문자를 리턴한다."){
//                result shouldBe "Password 변경 완료"
//            }
//            Then("비밀번호가 성공적으로 변경된다.") {
//
//                passwordSlot.captured shouldBe newPassword
//            }
//        }
//    }
//
//    afterEach {
//        clearAllMocks()
//    }
//}){
//    companion object {
//        private val shop = Shop(
//            sellerId = 1L,
//            name = "name",
//            description = "description",
//            shopImage = "shopImage",
//            rate = 1f
//        ).apply { id=1L }
//
//        private val seller = Seller(
//            id = 1L,
//            nickname ="HR",
//            password = "",
//            email="HR@test.com",
//            profileImage="profileImage",
//            phoneNumber="010-1234-5678",
//            address = "주소입니다"
//        )
//    }
//}