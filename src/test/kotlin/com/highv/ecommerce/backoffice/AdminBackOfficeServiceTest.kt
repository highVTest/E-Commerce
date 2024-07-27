package com.highv.ecommerce.backoffice

import com.highv.ecommerce.domain.backoffice.admin.entity.BlackList
import com.highv.ecommerce.domain.backoffice.admin.repository.BlackListRepository
import com.highv.ecommerce.domain.backoffice.admin.service.AdminService
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.LocalDateTime
import org.springframework.data.repository.findByIdOrNull

class AdminBackOfficeServiceTest : BehaviorSpec({

    val sellerRepository: SellerRepository = mockk()
    val productRepository: ProductRepository = mockk()
    val blackListRepository: BlackListRepository = mockk()
    val adminService = AdminService(sellerRepository, productRepository, blackListRepository)

    afterEach {
        clearAllMocks()
    }

    Given("관리자가 판매자를 제재할 때") {
        val sellerId = 1L
        val seller = Seller(
            id = sellerId,
            email = "seller@test.com",
            nickname = "TestSeller",
            password = "password",
            profileImage = "profileImage",
            phoneNumber = "010-1234-5678",
            address = "서울시 용산구"
        )
        val blackListSlot = slot<BlackList>()

        When("판매자가 존재하고 블랙리스트에 없으면") {
            every { sellerRepository.findByIdOrNull(sellerId) } returns seller
            every { blackListRepository.findByEmail(seller.email) } returns null
            every { blackListRepository.save(capture(blackListSlot)) } returns blackListSlot.captured

            Then("블랙리스트에 추가된다") {
                val response = adminService.sanctionSeller(sellerId)
                response.msg shouldBe "판매자 제재 완료"
                verify { blackListRepository.save(any()) }
                blackListSlot.captured.email shouldBe seller.email
                blackListSlot.captured.nickname shouldBe seller.nickname
                blackListSlot.captured.sanctionsCount shouldBe 1
            }
        }

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

            Then("블랙리스트의 제재 횟수가 증가한다") {
                val response = adminService.sanctionSeller(sellerId)
                response.msg shouldBe "판매자 제재 완료"
                verify { blackListRepository.save(existingBlackList) }
                existingBlackList.sanctionsCount shouldBe 4
            }
        }
    }

    Given("관리자가 상품을 제재할 때") {
        val productId = 1L
        val sellerId = 2L
        val seller = Seller(
            email = "seller@test.com",
            nickname = "TestSeller",
            password = "password",
            profileImage = "profileImage",
            phoneNumber = "010-1234-5678",
            address = "서울시 용산구"
        )
        val product = Product(
            name = "TestProduct",
            description = "Description",
            productImage = "image",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            categoryId = 1L,
            shop = mockk {
                every { this@mockk.sellerId } returns sellerId
            }
        )
        val blackListSlot = slot<BlackList>()

        When("상품과 판매자가 존재하고 블랙리스트에 없으면") {
            every { productRepository.findByIdOrNull(productId) } returns product
            every { sellerRepository.findByIdOrNull(sellerId) } returns seller
            every { blackListRepository.findByEmail(seller.email) } returns null
            every { blackListRepository.save(capture(blackListSlot)) } returns blackListSlot.captured

            Then("블랙리스트에 추가된다") {
                val response = adminService.sanctionProduct(productId)
                response.msg shouldBe "상품 제재 완료"
                verify { blackListRepository.save(any()) }
                blackListSlot.captured.email shouldBe seller.email
                blackListSlot.captured.nickname shouldBe seller.nickname
                blackListSlot.captured.sanctionsCount shouldBe 1
            }
        }

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

            Then("블랙리스트의 제재 횟수가 증가한다") {
                val response = adminService.sanctionProduct(productId)
                response.msg shouldBe "상품 제재 완료"
                verify { blackListRepository.save(existingBlackList) }
                existingBlackList.sanctionsCount shouldBe 5
                existingBlackList.isSanctioned shouldBe true
            }
        }
    }

    Given("관리자가 블랙리스트를 조회할 때") {
        When("블랙리스트가 존재하면") {
            val blackLists = listOf(
                BlackList(nickname = "nickname1", email = "email1", sanctionsCount = 1, isSanctioned = true),
                BlackList(nickname = "nickname2", email = "email2", sanctionsCount = 2, isSanctioned = false)
            )
            every { blackListRepository.findAll() } returns blackLists

            Then("블랙리스트 목록을 반환한다") {
                val response = adminService.getBlackLists()
                response.size shouldBe 2
                response[0].email shouldBe "email1"
                response[1].sanctionsCount shouldBe 2
            }
        }
    }

    Given("관리자가 특정 블랙리스트를 조회할 때") {
        val blackListId = 1L

        When("블랙리스트 항목이 존재하면") {
            val blackList = BlackList(nickname = "nickname", email = "email", sanctionsCount = 3, isSanctioned = true)
            every { blackListRepository.findByIdOrNull(blackListId) } returns blackList

            Then("블랙리스트 항목을 반환한다") {
                val response = adminService.getBlackList(blackListId)
                response.email shouldBe "email"
                response.sanctionsCount shouldBe 3
            }
        }
    }

    Given("관리자가 블랙리스트 항목을 삭제할 때") {
        val blackListId = 1L

        When("블랙리스트 항목이 존재하면") {
            val blackList = BlackList(
                id = blackListId,
                nickname = "nickname",
                email = "email",
                sanctionsCount = 3,
                isSanctioned = true
            )
            every { blackListRepository.findByIdOrNull(blackListId) } returns blackList
            every { blackListRepository.delete(blackList) } returns Unit

            Then("블랙리스트 항목을 삭제하고 응답을 반환한다") {
                val response = adminService.deleteBlackList(blackListId)
                response.msg shouldBe "블랙리스트 삭제 완료"
                verify { blackListRepository.delete(blackList) }
            }
        }
    }
})
