package com.highv.ecommerce.favorite

import com.highv.ecommerce.common.exception.BuyerNotFoundException
import com.highv.ecommerce.common.exception.ProductNotFoundException
import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.favorite.entity.Favorite
import com.highv.ecommerce.domain.favorite.repository.FavoriteRepository
import com.highv.ecommerce.domain.favorite.service.FavoriteService
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime

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

                    val response = favoriteService.management(productId, buyerId)

                    response.msg shouldBe "찜 목록에 추가 했습니다."
                }
            }

            When("찜 목록에 상품이 있으면") {
                every { productRepository.existsById(any()) } returns true
                every { buyerRepository.existsById(any()) } returns true
                every { favoriteRepository.findByProductIdAndBuyerId(any(), any()) } returns favorite

                Then("찜 목록에서 상품이 삭제된다.") {
                    every { favoriteRepository.delete(any()) } returns Unit
                    val response = favoriteService.management(productId, buyerId)

                    response.msg shouldBe "찜 목록에서 삭제했습니다."

                }
            }

            When("해당 상품이 없으면") {
                every { productRepository.existsById(any()) } returns false

                Then("상품이 존재하지 않는다고 예외가 발생한다.") {
                    shouldThrow<ProductNotFoundException> {
                        favoriteService.management(productId, buyerId)
                    }.let {
                        it.message shouldBe "해당 상품이 존재하지 않습니다."
                    }

                }
            }

            // 존재하는 케이스가 맞나?
            When("구매자가 존재하지 않으면") {
                every { productRepository.existsById(any()) } returns true
                every { buyerRepository.existsById(buyerId) } returns false

                Then("구매자가 존재하지 않는다고 예외가 발생한다.") {
                    shouldThrow<BuyerNotFoundException> {
                        favoriteService.management(productId, buyerId)
                    }.let {
                        it.message shouldBe "구매자 정보가 존재하지 않습니다."
                    }
                }
            }

        }

        Given("찜 목록이 존재할 때") {
            val buyerId = 1L
            var productId = 1L

            val favorites: MutableList<Favorite> = mutableListOf()
            val products: MutableList<Product> = mutableListOf()

            val shop = Shop(
                sellerId = 1L,
                name = "가게1",
                description = "설명1",
                shopImage = "이미지1",
                rate = 5.0f
            ).also {
                it.id = 1L
            }

            for (i in 1..10) {
                val product = Product(
                    name = "상품${i}",
                    description = "설명${i}",
                    productImage = "이미지${i}",
                    createdAt = LocalDateTime.of(2022, 2, 2, 2, 22, 22),
                    updatedAt = LocalDateTime.of(2022, 2, 2, 2, 22, 22),
                    isSoldOut = false,
                    deletedAt = null,
                    isDeleted = false,
                    shop = shop,
                    categoryId = 1L
                ).also {
                    it.id = i.toLong()
                }
                products.add(product)
            }

            val productBackOffices: List<ProductBackOffice> = products.map {
                ProductBackOffice(
                    quantity = it.id!!.toInt() * 15,
                    price = it.id!!.toInt() * 1000,
                    soldQuantity = 0,
                    product = it
                ).apply { id = it.id }
            }

            for (i in 0 until products.size) {
                products[i].productBackOffice = productBackOffices[i]
            }

            products.forEach {
                val favorite = Favorite(
                    productId = it.id!!,
                    buyerId = buyerId
                ).apply { id = it.id!! }

                favorites.add(favorite)
            }

            When("구매자가 찜 목록을 조회하면") {
                every { favoriteRepository.findAllByBuyerId(buyerId) } returns favorites
                every { productRepository.findAllById(any()) } returns products

                val response = favoriteService.getFavorites(buyerId)

                Then("목록을 반환한다.") {
                    response.size shouldBe 10
                }
            }
        }
    }
}