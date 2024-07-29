package com.highv.ecommerce.backoffice

import com.highv.ecommerce.domain.admin.entity.BlackList
import com.highv.ecommerce.domain.admin.repository.BlackListRepository
import com.highv.ecommerce.domain.admin.service.AdminService
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class AdminBackOfficeServiceTest : BehaviorSpec({

    // 필요한 리포지토리들을 Mock 객체로 생성합니다.
    val sellerRepository: SellerRepository = mockk()
    val productRepository: ProductRepository = mockk()
    val blackListRepository: BlackListRepository = mockk()

    // AdminService 객체를 생성합니다.
    val adminService = AdminService(sellerRepository, productRepository, blackListRepository)

    // 각 테스트 후 모든 Mock 객체들을 초기화합니다.
    afterEach {
        clearAllMocks()
    }

    // 판매자 제재 관련 테스트
    Given("관리자가 판매자를 제재할 때") {
        val sellerId = 1L
        // 판매자 객체를 생성합니다.
        val seller = Seller(
            id = sellerId,
            email = "seller@test.com",
            nickname = "TestSeller",
            password = "password",
            profileImage = "profileImage",
            phoneNumber = "010-1234-5678",
            address = "광주광역시 서구"
        )
        // BlackList 객체를 캡처하기 위해 슬롯을 사용합니다.
        val blackListSlot = slot<BlackList>()
        // 블랙리스트 객체를 생성합니다.
        val blackList = BlackList(
            nickname = seller.nickname,
            email = seller.email,
            sanctionsCount = 1
        )

        // 판매자가 존재하고 블랙리스트에 없을 때
        When("판매자가 존재하고 블랙리스트에 없으면") {
            every { sellerRepository.findByIdOrNull(sellerId) } returns seller
            every { blackListRepository.findByEmail(seller.email) } returns null
            every { blackListRepository.save(capture(blackListSlot)) } returns blackList

            // 블랙리스트에 추가하는 테스트
            Then("블랙리스트에 추가된다") {
                val response = adminService.sanctionSeller(sellerId)
                response.msg shouldBe "판매자 제재 완료"
                verify { blackListRepository.save(any()) }
                blackListSlot.captured.email shouldBe seller.email
                blackListSlot.captured.nickname shouldBe seller.nickname
                blackListSlot.captured.sanctionsCount shouldBe 1
            }
        }

        // 판매자가 존재하고 블랙리스트에 이미 있을 때
        When("판매자가 존재하고 블랙리스트에 이미 있으면") {
            val existingBlackList = BlackList(
                nickname = seller.nickname,
                email = seller.email,
                sanctionsCount = 3,
                isSanctioned = false
            )
            every { sellerRepository.findByIdOrNull(sellerId) } returns seller
            every { blackListRepository.findByEmail(seller.email) } returns existingBlackList
            every { blackListRepository.save(existingBlackList) } returns existingBlackList

            // 블랙리스트의 제재 횟수를 증가시키는 테스트
            Then("블랙리스트의 제재 횟수가 증가한다") {
                val response = adminService.sanctionSeller(sellerId)
                response.msg shouldBe "판매자 제재 완료"
                verify { blackListRepository.save(existingBlackList) }
                existingBlackList.sanctionsCount shouldBe 4
            }
        }
    }

    // 상품 제재 관련 테스트
    Given("관리자가 상품을 제재할 때") {
        val productId = 1L
        val sellerId = 2L
        // 판매자 객체를 생성합니다.
        val seller = Seller(
            email = "seller@test.com",
            nickname = "TestSeller",
            password = "password",
            profileImage = "profileImage",
            phoneNumber = "010-1234-5678",
            address = "광주광역시 서구"
        )
        // 상품 객체를 생성합니다.
        val specificDate = LocalDateTime.of(1995, 4, 29, 0, 0)

        val product = Product(
            name = "TestProduct",
            description = "Description",
            productImage = "image",
            createdAt = specificDate,
            updatedAt = specificDate,
            isSoldOut = false,
            categoryId = 1L,
            shop = Shop(
                sellerId = sellerId,
                name = "testShop",
                description = "testShopDescription",
                shopImage = "testShopImage",
                rate = 0.5f
            ).apply { id = 1L }
        )
        val blackListSlot = slot<BlackList>()

        val blackList = BlackList(
            nickname = seller.nickname,
            email = seller.email,
            sanctionsCount = 1
        )

        // 상품과 판매자가 존재하고 블랙리스트에 없을 때
        When("상품과 판매자가 존재하고 블랙리스트에 없으면") {
            every { productRepository.findByIdOrNull(productId) } returns product
            every { sellerRepository.findByIdOrNull(sellerId) } returns seller
            every { blackListRepository.findByEmail(seller.email) } returns null
            every { blackListRepository.save(capture(blackListSlot)) } returns blackList

            // 블랙리스트에 추가하는 테스트
            Then("블랙리스트에 추가된다") {
                val response = adminService.sanctionProduct(productId)
                response.msg shouldBe "상품 제재 완료"
                verify { blackListRepository.save(any()) }
                blackListSlot.captured.email shouldBe seller.email
                blackListSlot.captured.nickname shouldBe seller.nickname
                blackListSlot.captured.sanctionsCount shouldBe 1
            }
        }

        // 상품과 판매자가 존재하고 블랙리스트에 이미 있을 때
        When("상품과 판매자가 존재하고 블랙리스트에 이미 있으면") {
            val existingBlackList = BlackList(
                nickname = seller.nickname,
                email = seller.email,
                sanctionsCount = 4,
                isSanctioned = false
            )
            every { productRepository.findByIdOrNull(productId) } returns product
            every { sellerRepository.findByIdOrNull(sellerId) } returns seller
            every { blackListRepository.findByEmail(seller.email) } returns existingBlackList
            every { blackListRepository.save(existingBlackList) } returns existingBlackList

            // 블랙리스트의 제재 횟수를 증가시키는 테스트
            Then("블랙리스트의 제재 횟수가 증가한다") {
                val response = adminService.sanctionProduct(productId)
                response.msg shouldBe "상품 제재 완료"
                verify { blackListRepository.save(existingBlackList) }
                existingBlackList.sanctionsCount shouldBe 5
                existingBlackList.isSanctioned shouldBe true
            }
        }
    }

    // 블랙리스트 조회 관련 테스트
    Given("관리자가 블랙리스트를 조회할 때") {
        // 블랙리스트가 존재할 때
        When("블랙리스트가 존재하면") {
            val blackLists = listOf(
                BlackList(nickname = "nickname1", email = "email1", sanctionsCount = 1, isSanctioned = true),
                BlackList(nickname = "nickname2", email = "email2", sanctionsCount = 2, isSanctioned = false)
            )
            every { blackListRepository.findAll() } returns blackLists

            // 블랙리스트 목록을 반환하는 테스트
            Then("블랙리스트 목록을 반환한다") {
                val response = adminService.getBlackLists()
                response.size shouldBe 2
                response[0].email shouldBe "email1"
                response[1].sanctionsCount shouldBe 2
            }
        }
    }

    // 특정 블랙리스트 항목 조회 관련 테스트
    Given("관리자가 특정 블랙리스트를 조회할 때") {
        val blackListId = 1L

        // 블랙리스트 항목이 존재할 때
        When("블랙리스트 항목이 존재하면") {
            val blackList = BlackList(nickname = "nickname", email = "email", sanctionsCount = 3, isSanctioned = true)
            every { blackListRepository.findByIdOrNull(blackListId) } returns blackList

            // 블랙리스트 항목을 반환하는 테스트
            Then("블랙리스트 항목을 반환한다") {
                val response = adminService.getBlackList(blackListId)
                response.email shouldBe "email"
                response.sanctionsCount shouldBe 3
            }
        }
        // 블랙리스트 항목이 존재하지 않을 때
        When("블랙리스트 항목이 존재하지 않으면") {
            every { blackListRepository.findByIdOrNull(blackListId) } returns null

            // 예외를 발생시키는 테스트
            Then("예외를 발생시킨다") {
                val exception = shouldThrow<RuntimeException> {
                    adminService.getBlackList(blackListId)
                }
                exception.message shouldBe "블랙리스트가 존재하지 않습니다."
            }
        }
    }

// 블랙리스트 항목 삭제 관련 테스트
    Given("블랙리스트 항목이 존재해서") {
        val blackListId = 1L

        // 블랙리스트 항목 객체를 생성합니다.
        val blackList = BlackList(
            id = blackListId,
            nickname = "nickname",
            email = "email",
            sanctionsCount = 3,
            isSanctioned = true
        )
        every { blackListRepository.findByIdOrNull(blackListId) } returns blackList

        When("관리자가 블랙리스트 항목을 삭제하면") {
            every { blackListRepository.delete(blackList) } returns Unit

            Then("블랙리스트 항목을 삭제하고 응답을 반환한다") {
                val response = adminService.deleteBlackList(blackListId)
                response.msg shouldBe "블랙리스트 삭제 완료"
                verify { blackListRepository.delete(blackList) }
            }
        }
    }

// 블랙리스트 항목이 존재하지 않을 때
    Given("블랙리스트 항목이 존재하지 않아서") {
        val blackListId = 1L

        every { blackListRepository.findByIdOrNull(blackListId) } returns null

        When("관리자가 블랙리스트 항목을 삭제하면") {
            // 예외를 발생시키는 테스트
            Then("예외를 발생시킨다") {
                val exception = shouldThrow<RuntimeException> {
                    adminService.deleteBlackList(blackListId)
                }
                exception.message shouldBe "블랙리스트 id $blackListId 존재하지 않습니다."
            }
        }
    }
})