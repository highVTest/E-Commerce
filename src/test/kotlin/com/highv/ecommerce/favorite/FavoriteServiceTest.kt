package com.highv.ecommerce.favorite

import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.favorite.entity.Favorite
import com.highv.ecommerce.domain.favorite.repository.FavoriteRepository
import com.highv.ecommerce.domain.favorite.service.FavoriteService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class FavoriteServiceTest : BehaviorSpec() {

    init {
        val favoriteRepository: FavoriteRepository = mockk<FavoriteRepository>()
        val productRepository: ProductRepository = mockk<ProductRepository>()
        val buyerRepository: BuyerRepository = mockk<BuyerRepository>()
        val favoriteService: FavoriteService = FavoriteService(favoriteRepository, productRepository, buyerRepository)

        afterContainer {
            clearAllMocks()
        }

        Given("구매자가 상품을 찜 할 때") {
            val productId = 1L
            val buyerId = 1L

            val favorite: Favorite = Favorite(
                buyerId = buyerId,
                productId = productId,
            ).apply { id = 1L }

            When("찜 목록에 상품이 없으면") {
                every { productRepository.existsById(any()) } returns true
                every { buyerRepository.existsById(any()) } returns true
                every { favoriteRepository.findByProductIdAndBuyerId(any(), any()) } returns null

                Then("찜 목록에 상품이 추가된다.") {
                    every { favoriteRepository.save(any()) } returns favorite

                    favoriteService.management(productId, buyerId)
                }
            }

            When("찜 목록에 상품이 있으면") {
                every { productRepository.existsById(any()) } returns true
                every { buyerRepository.existsById(any()) } returns true
                every { favoriteRepository.findByProductIdAndBuyerId(any(), any()) } returns favorite

                Then("찜 목록에서 상품이 삭제된다.") {
                    every { favoriteRepository.delete(any()) } returns Unit
                    favoriteService.management(productId, buyerId)
                }
            }

            When("상품이 없으면") {
                every { productRepository.existsById(any()) } returns false

                Then("상품이 존재하지 않는다고 예외가 발생한다.") {
                    shouldThrow<RuntimeException> {
                        favoriteService.management(productId, buyerId)
                    }.let {
                        it.message shouldBe "Product with ID ${productId} not found"
                    }

                }
            }

            // 존재하는 케이스가 맞나?
            When("구매자가 존재하지 않으면") {
                every { productRepository.existsById(any()) } returns true
                every { buyerRepository.existsById(buyerId) } returns false

                Then("구매자가 존재하지 않는다고 예외가 발생한다.") {
                    shouldThrow<RuntimeException> {
                        favoriteService.management(productId, buyerId)
                    }.let {
                        it.message shouldBe "Buyer with ID ${buyerId} not found"
                    }
                }
            }

        }
    }
}