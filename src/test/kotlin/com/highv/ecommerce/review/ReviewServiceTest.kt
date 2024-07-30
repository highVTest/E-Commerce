package com.highv.ecommerce.review

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.review.dto.ReviewRequest
import com.highv.ecommerce.domain.review.dto.ReviewResponse
import com.highv.ecommerce.domain.review.entity.Review
import com.highv.ecommerce.domain.review.repository.ReviewRepository
import com.highv.ecommerce.domain.review.service.ReviewService
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class ReviewServiceTest : BehaviorSpec({

    val reviewRepository = mockk<ReviewRepository>()
    val productRepository = mockk<ProductRepository>()
    val shopRepository = mockk<ShopRepository>()
    val reviewService = ReviewService(reviewRepository, productRepository, shopRepository)

    val productId = 1L
    val reviewId = 1L
    val buyerId = 1L
    val shopId = 1L

    product.productBackOffice = productBackOffice

    val reviewRequest = ReviewRequest(rate = 5f, content = "Great product!")
    val review =
        Review(buyerId = buyerId, product = product, rate = 5f, content = "Great product!").apply { id = reviewId }

    afterEach {
        clearAllMocks()
    }

    Given("addReview 를 실행할 때") {
        When("상품이 존재할 때") {
            every { productRepository.findByIdOrNull(productId) } returns product
            every { reviewRepository.saveAndFlush(any()) } returns review
            every { reviewRepository.findAllByProductId(productId) } returns listOf(review)
            every { shopRepository.findByIdOrNull(shopId) } returns product.shop
            every { shopRepository.save(any()) } returns product.shop

            Then("리뷰가 추가되고 상점 평균 평점이 업데이트 된다") {
                val response = reviewService.addReview(productId, reviewRequest, buyerId)
                response shouldBe ReviewResponse.from(review)
                verify(exactly = 1) { reviewRepository.saveAndFlush(any()) }
                verify(exactly = 1) { shopRepository.save(any()) }
            }
        }

        When("상품이 존재하지 않을 때") {
            every { productRepository.findByIdOrNull(productId) } returns null

            Then("CustomRuntimeException 이 발생한다") {
                shouldThrow<CustomRuntimeException> {
                    reviewService.addReview(productId, reviewRequest, buyerId)
                }
            }
        }
    }

    Given("updateReview 를 실행할 때") {
        When("리뷰가 존재할 때") {
            every { productRepository.findByIdOrNull(productId) } returns product
            every { reviewRepository.findByIdOrNull(reviewId) } returns review
            every { reviewRepository.saveAndFlush(any()) } returns review
            every { reviewRepository.findAllByProductId(productId) } returns listOf(review)
            every { shopRepository.findByIdOrNull(shopId) } returns product.shop
            every { shopRepository.save(any()) } returns product.shop

            Then("리뷰와 상점 평균 평점이 업데이트 된다") {
                val response = reviewService.updateReview(productId, reviewId, reviewRequest, buyerId)
                response shouldBe ReviewResponse.from(review)
                verify(exactly = 1) { reviewRepository.saveAndFlush(any()) }
                verify(exactly = 1) { shopRepository.save(any()) }
            }
        }

        When("리뷰가 존재하지 않을 때") {
            every { reviewRepository.findByIdOrNull(reviewId) } returns null

            Then("CustomRuntimeException이 발생한다") {
                shouldThrow<CustomRuntimeException> {
                    reviewService.updateReview(productId, reviewId, reviewRequest, buyerId)
                }
            }
        }
    }

    Given("deleteReview 를 실행할 때") {
        When("리뷰가 존재할 때") {
            every { reviewRepository.findByIdOrNull(reviewId) } returns review
            every { productRepository.findByIdOrNull(productId) } returns product
            every { reviewRepository.delete(any()) } just Runs
            every { reviewRepository.findAllByProductId(productId) } returns listOf()
            every { shopRepository.findByIdOrNull(shopId) } returns product.shop
            every { shopRepository.save(any()) } returns product.shop

            Then("리뷰가 삭제되고 상점 평균 평점이 업데이트 된다") {
                val response = reviewService.deleteReview(productId, reviewId, buyerId)
                response shouldBe DefaultResponse("Review deleted successfully")
                verify(exactly = 1) { reviewRepository.delete(any()) }
                verify(exactly = 1) { shopRepository.save(any()) }
            }
        }
    }

    Given("getProductReviews 를 실행할 때") {
        When("상품에 리뷰가 있을 때") {
            every { reviewRepository.findAllByProductId(productId) } returns listOf(review)

            Then("ReviewResponse 리스트를 반환한다") {
                val response = reviewService.getProductReviews(productId)
                response shouldBe listOf(ReviewResponse.from(review))
            }
        }

        When("상품에 리뷰가 없을 때") {
            every { reviewRepository.findAllByProductId(productId) } returns listOf()

            Then("빈 리스트를 반환한다") {
                val response = reviewService.getProductReviews(productId)
                response shouldBe emptyList()
            }
        }
    }

    Given("getBuyerReviews 를 실행할 때") {
        When("구매자에 리뷰가 있을 때") {
            every { reviewRepository.findAllByBuyerId(buyerId) } returns listOf(review)

            Then("ReviewResponse 리스트를 반환한다") {
                val response = reviewService.getBuyerReviews(buyerId)
                response shouldBe listOf(ReviewResponse.from(review))
            }
        }

        When("구매자에 리뷰가 없을 때") {
            every { reviewRepository.findAllByBuyerId(buyerId) } returns listOf()

            Then("빈 리스트를 반환한다") {
                val response = reviewService.getBuyerReviews(buyerId)
                response shouldBe emptyList()
            }
        }
    }

    Given("updateShopAverageRate 를 실행할 때") {
        val reviews = listOf(
            Review(buyerId = 1L, product = product, rate = 4f, content = "Good").apply { id = 1L },
            Review(buyerId = 2L, product = product, rate = 5f, content = "Excellent").apply { id = 2L }
        )

        When("리뷰가 존재하고 상품과 상점이 모두 존재할 때") {
            every { reviewRepository.findAllByProductId(productId) } returns reviews
            every { productRepository.findByIdOrNull(productId) } returns product
            every { shopRepository.findByIdOrNull(shopId) } returns shop
            every { shopRepository.save(any()) } returns shop

            Then("상점의 평균 평점이 업데이트 된다") {
                reviewService.updateShopAverageRate(productId)
                shop.rate shouldBe 4.5f  // (4 + 5) / 2
                verify(exactly = 1) { shopRepository.save(any()) }
            }
        }

        When("리뷰가 없고 상품과 상점이 모두 존재할 때") {
            every { reviewRepository.findAllByProductId(productId) } returns emptyList()
            every { productRepository.findByIdOrNull(productId) } returns product
            every { shopRepository.findByIdOrNull(shopId) } returns shop
            every { shopRepository.save(any()) } returns shop

            Then("상점의 평균 평점이 0으로 업데이트 된다") {
                reviewService.updateShopAverageRate(productId)
                shop.rate shouldBe 0f
                verify(exactly = 1) { shopRepository.save(any()) }
            }
        }

        When("상품이 존재하지 않을 때") {
            every { reviewRepository.findAllByProductId(productId) } returns reviews
            every { productRepository.findByIdOrNull(productId) } returns null

            Then("CustomRuntimeException이 발생한다") {
                shouldThrow<CustomRuntimeException> {
                    reviewService.updateShopAverageRate(productId)
                }
            }
        }

        When("상점이 존재하지 않을 때") {
            every { reviewRepository.findAllByProductId(productId) } returns reviews
            every { productRepository.findByIdOrNull(productId) } returns product
            every { shopRepository.findByIdOrNull(shopId) } returns null

            Then("CustomRuntimeException이 발생한다") {
                shouldThrow<CustomRuntimeException> {
                    reviewService.updateShopAverageRate(productId)
                }
            }
        }
    }
}) {
    companion object {
        private val shop = Shop(
            sellerId = 1L,
            name = "ShopName",
            description = "ShopDescription",
            rate = 5.0f,
            shopImage = "Image"
        ).apply { id = 1L }

        val product = Product(
            name = "Product Name 1",
            description = "Product Description 1",
            productImage = "image6.jpg",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 3L,
            productBackOffice = null
        ).apply { id = 1L }

        val productBackOffice = ProductBackOffice(
            id = 6L,
            quantity = 50,
            price = 350,
            soldQuantity = 25,
            product = product
        )
    }
}