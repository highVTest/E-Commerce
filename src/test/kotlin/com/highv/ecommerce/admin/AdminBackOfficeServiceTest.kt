package com.highv.ecommerce.admin

import com.highv.ecommerce.common.exception.BlackListNotFoundException
import com.highv.ecommerce.common.exception.SellerNotFoundException
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
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class AdminBackOfficeServiceTest : BehaviorSpec({

    val sellerRepository: SellerRepository = mockk()
    val productRepository: ProductRepository = mockk()
    val blackListRepository: BlackListRepository = mockk()

    val adminService = AdminService(sellerRepository, productRepository, blackListRepository)

    afterEach {
        clearAllMocks()
    }

    Given("판매자가 존재하고 블랙리스트에 없으면") {
        val sellerId = 1L
        val seller = Seller(
            id = sellerId,
            email = "seller@test.com",
            nickname = "TestSeller",
            password = "password",
            profileImage = "profileImage",
            phoneNumber = "010-1234-5678",
            address = "광주광역시 서구",
            activeStatus = Seller.ActiveStatus.PENDING
        )
        val blackListSlot = slot<BlackList>()
        val blackList = BlackList(
            nickname = seller.nickname,
            email = seller.email,
            sanctionsCount = 1
        )

        every { sellerRepository.findByIdOrNull(sellerId) } returns seller
        every { blackListRepository.findByEmail(seller.email) } returns null
        every { blackListRepository.save(capture(blackListSlot)) } returns blackList

        When("관리자가 판매자를 제재할 때") {
            val response = adminService.sanctionSeller(sellerId)

            Then("블랙리스트에 추가된다") {
                response.msg shouldBe "판매자 제재 완료"
                verify { blackListRepository.save(any()) }
                blackListSlot.captured.email shouldBe seller.email
                blackListSlot.captured.nickname shouldBe seller.nickname
                blackListSlot.captured.sanctionsCount shouldBe 1
            }
        }
    }

    Given("판매자가 존재하고 블랙리스트에 이미 있으면") {
        val sellerId = 1L
        val seller = Seller(
            id = sellerId,
            email = "seller@test.com",
            nickname = "TestSeller",
            password = "password",
            profileImage = "profileImage",
            phoneNumber = "010-1234-5678",
            address = "광주광역시 서구"
        )
        val existingBlackList = BlackList(
            nickname = seller.nickname,
            email = seller.email,
            sanctionsCount = 3,
            isSanctioned = false
        )

        every { sellerRepository.findByIdOrNull(sellerId) } returns seller
        every { blackListRepository.findByEmail(seller.email) } returns existingBlackList
        every { blackListRepository.save(existingBlackList) } returns existingBlackList

        When("관리자가 판매자를 제재할 때") {
            val response = adminService.sanctionSeller(sellerId)

            Then("블랙리스트의 제재 횟수가 증가한다") {
                response.msg shouldBe "판매자 제재 완료"
                verify { blackListRepository.save(existingBlackList) }
                existingBlackList.sanctionsCount shouldBe 4
            }
        }
    }

    Given("상품과 판매자가 존재하고 블랙리스트에 없으면") {
        val productId = 1L
        val sellerId = 2L
        val seller = Seller(
            id = sellerId,
            email = "seller@test.com",
            nickname = "TestSeller",
            password = "password",
            profileImage = "profileImage",
            phoneNumber = "010-1234-5678",
            address = "광주광역시 서구",
            activeStatus = Seller.ActiveStatus.PENDING
        )
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

        every { productRepository.findByIdOrNull(productId) } returns product
        every { sellerRepository.findByIdOrNull(sellerId) } returns seller
        every { blackListRepository.findByEmail(seller.email) } returns null
        every { blackListRepository.save(capture(blackListSlot)) } returns blackList

        When("관리자가 상품을 제재할 때") {
            val response = adminService.sanctionProduct(productId)

            Then("블랙리스트에 추가된다") {
                response.msg shouldBe "상품 제재 완료"
                verify { blackListRepository.save(any()) }
                blackListSlot.captured.email shouldBe seller.email
                blackListSlot.captured.nickname shouldBe seller.nickname
                blackListSlot.captured.sanctionsCount shouldBe 1
            }
        }
    }

    Given("상품과 판매자가 존재하고 블랙리스트에 이미 있으면") {
        val productId = 1L
        val sellerId = 2L
        val seller = Seller(
            id = sellerId,
            email = "seller@test.com",
            nickname = "TestSeller",
            password = "password",
            profileImage = "profileImage",
            phoneNumber = "010-1234-5678",
            address = "광주광역시 서구"
        )
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

        When("관리자가 상품을 제재할 때") {
            val response = adminService.sanctionProduct(productId)

            Then("블랙리스트의 제재 횟수가 증가한다") {
                response.msg shouldBe "상품 제재 완료"
                verify { blackListRepository.save(existingBlackList) }
                existingBlackList.sanctionsCount shouldBe 5
                existingBlackList.isSanctioned shouldBe true
            }
        }
    }

    Given("블랙리스트가 존재하는 경우") {
        val blackLists = listOf(
            BlackList(nickname = "nickname1", email = "email1", sanctionsCount = 1, isSanctioned = true),
            BlackList(nickname = "nickname2", email = "email2", sanctionsCount = 2, isSanctioned = false)
        )

        every { blackListRepository.findAll() } returns blackLists

        When("관리자가 블랙리스트를 조회할 때") {
            val response = adminService.getBlackLists()

            Then("블랙리스트 목록을 반환한다") {
                response.size shouldBe 2
                response[0].email shouldBe "email1"
                response[1].sanctionsCount shouldBe 2
            }
        }
    }

    Given("특정 블랙리스트 항목이 존재하는 경우") {
        val blackListId = 1L
        val blackList = BlackList(nickname = "nickname", email = "email", sanctionsCount = 3, isSanctioned = true)

        every { blackListRepository.findByIdOrNull(blackListId) } returns blackList

        When("관리자가 특정 블랙리스트를 조회할 때") {
            val response = adminService.getBlackList(blackListId)

            Then("블랙리스트 항목을 반환한다") {
                response.email shouldBe "email"
                response.sanctionsCount shouldBe 3
            }
        }
    }

    Given("특정 블랙리스트 항목이 존재하지 않는 경우") {
        val blackListId = 1L

        every { blackListRepository.findByIdOrNull(blackListId) } returns null

        When("관리자가 특정 블랙리스트를 조회할 때") {
            Then("BlackListNotFoundException를 발생시킨다") {
                val exception = shouldThrow<BlackListNotFoundException> {
                    adminService.getBlackList(blackListId)
                }
                exception.message shouldBe "블랙리스트가 존재하지 않습니다."
            }
        }
    }

    Given("블랙리스트 항목이 존재하는 경우") {
        val blackListId = 1L
        val blackList = BlackList(
            id = blackListId,
            nickname = "nickname",
            email = "email",
            sanctionsCount = 3,
            isSanctioned = true
        )

        every { blackListRepository.findByIdOrNull(blackListId) } returns blackList

        When("관리자가 블랙리스트 항목을 삭제할 때") {
            every { blackListRepository.delete(blackList) } returns Unit

            Then("블랙리스트 항목을 삭제하고 응답을 반환한다") {
                val response = adminService.deleteBlackList(blackListId)
                response.msg shouldBe "블랙리스트 삭제 완료"
                verify { blackListRepository.delete(blackList) }
            }
        }
    }

    Given("블랙리스트 항목이 존재하지 않는 경우") {
        val blackListId = 1L

        every { blackListRepository.findByIdOrNull(blackListId) } returns null

        When("관리자가 블랙리스트 항목을 삭제할 때") {
            Then("BlackListNotFoundException를 발생시킨다") {
                val exception = shouldThrow<BlackListNotFoundException> {
                    adminService.deleteBlackList(blackListId)
                }
                exception.message shouldBe "블랙리스트 id $blackListId 존재하지 않습니다."
            }
        }
    }
    Given("판매자가 존재하면") {
        val sellerId = 1L
        val seller = Seller(
            id = sellerId,
            email = "seller@test.com",
            nickname = "TestSeller",
            password = "password",
            profileImage = "profileImage",
            phoneNumber = "010-1234-5678",
            address = "광주광역시 서구",
            activeStatus = Seller.ActiveStatus.PENDING
        )

        When("판매자 탈퇴 대기 회원을 승인할 때") {
            every { sellerRepository.findByIdOrNull(sellerId) } returns seller
            every { sellerRepository.save(seller) } returns seller

            Then("판매자의 상태를 RESIGNED로 변경한다") {
                val response = adminService.approveSellerResignation(sellerId)
                response.msg shouldBe "판매자 탈퇴 승인 완료"
                seller.activeStatus shouldBe Seller.ActiveStatus.RESIGNED
                verify { sellerRepository.save(seller) }
            }
        }
    }

    Given("판매자가 존재하지 않으면") {
        val sellerId = 1L

        When("판매자 탈퇴 대기 회원을 승인할 때") {
            every { sellerRepository.findByIdOrNull(sellerId) } returns null

            Then("SellerNotFoundException를 발생시킨다") {
                val exception = shouldThrow<SellerNotFoundException> {
                    adminService.approveSellerResignation(sellerId)
                }
                exception.message shouldBe "판매자 id $sellerId not found"
            }
        }
    }

    Given("판매자가 존재하면") {
        val sellerId = 1L
        val seller = Seller(
            id = sellerId,
            email = "seller@test.com",
            nickname = "TestSeller",
            password = "password",
            profileImage = "profileImage",
            phoneNumber = "010-1234-5678",
            address = "광주광역시 서구",
            activeStatus = Seller.ActiveStatus.PENDING
        )

        When("판매자 승인 대기 회원을 승격할 때") {
            every { sellerRepository.findByIdOrNull(sellerId) } returns seller
            every { sellerRepository.save(seller) } returns seller

            Then("판매자의 상태를 APPROVED로 변경한다") {
                val response = adminService.promotePendingSeller(sellerId)
                response.msg shouldBe "판매자 승인 완료"
                seller.activeStatus shouldBe Seller.ActiveStatus.APPROVED
                verify { sellerRepository.save(seller) }
            }
        }
    }

    Given("판매자가 존재하지 않으면") {
        val sellerId = 1L

        When("판매자 승인 대기 회원을 승격할 때") {
            every { sellerRepository.findByIdOrNull(sellerId) } returns null

            Then("SellerNotFoundException를 발생시킨다") {
                val exception = shouldThrow<SellerNotFoundException> {
                    adminService.promotePendingSeller(sellerId)
                }
                exception.message shouldBe "판매자 id $sellerId not found"
            }
        }
    }
})

