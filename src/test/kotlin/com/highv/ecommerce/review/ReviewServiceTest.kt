package com.highv.ecommerce.review

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.ProductNotFoundException
import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.product.dto.ReviewProductDto
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
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class ReviewServiceTest : BehaviorSpec({

    val reviewRepository = mockk<ReviewRepository>()
    val productRepository = mockk<ProductRepository>()
    val shopRepository = mockk<ShopRepository>()
    val buyerRepository = mockk<BuyerRepository>()
    val reviewService = ReviewService(reviewRepository, shopRepository, buyerRepository, productRepository)

    val productId = 1L
    val reviewId = 1L
    val buyer = Buyer(
        id = 1L,
        nickname = "buyer1",
        password = "testpassword",
        email = "test1@test.com",
        profileImage = "image1",
        phoneNumber = "010-1234-5678",
        address = "서울시 동작구",
        providerName = "카카오"
    )
    val shopId = 1L

    val reviewRequest = ReviewRequest(rate = 5f, content = "Great product!")
    val review =
        Review(buyer = buyer, productId = productId, rate = 5f, content = "Great product!").apply { id = reviewId }

    afterEach {
        clearAllMocks()
    }

    Given("리뷰를 추가할때") {
        When("상품이 존재할 때") {

            every { productRepository.existsById(productId) } returns true
            every { reviewRepository.save(any()) } returns review
            every { buyerRepository.findByIdOrNull(buyer.id!!) } returns buyer


            Then("리뷰가 추가되고 상점 평균 평점이 업데이트 된다") {
                val response = reviewService.addReview(productId, reviewRequest, buyer.id!!)
                response shouldBe DefaultResponse(msg = "리뷰가 등록되었습니다.")
                verify(exactly = 1) { reviewRepository.save(any()) }
            }
        }

        When("상품이 존재하지 않을 때") {
            every { productRepository.existsById(productId) } returns false

            Then("CustomRuntimeException 이 발생한다") {
                shouldThrow<ProductNotFoundException> {
                    reviewService.addReview(productId, reviewRequest, buyer.id!!)
                }
            }
        }
    }

    Given("updateReview 를 실행할 때") {
        When("리뷰가 존재할 때") {
            every {
                reviewRepository.updateByReviewIdAndBuyerId(
                    reviewId,
                    buyer.id!!,
                    reviewRequest.rate,
                    reviewRequest.content
                )
            } returns Unit



            Then("리뷰와 상점 평균 평점이 업데이트 된다") {
                val response = reviewService.updateReview(reviewId, reviewRequest, buyer.id!!)
                response.msg shouldBe "리뷰가 수정되었습니다."

            }
        }

    }

    Given("deleteReview 를 실행할 때") {
        When("리뷰가 존재할 때") {
            every { reviewRepository.deleteByReviewIdAndBuyerId(reviewId, buyer.id!!) } returns Unit




            Then("리뷰가 삭제되고 상점 평균 평점이 업데이트 된다") {
                val response = reviewService.deleteReview(productId, buyer.id!!)
                response.msg shouldBe "리뷰가 삭제되었습니다."

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

        val products = (1..5).map {
            Product(
                name = "Test Product $it",
                description = "Test Description $it",
                productImage = "image.jpg $it",
                createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
                updatedAt = LocalDateTime.of(2021, 2, 2, 2, 2, 0),
                isSoldOut = false,
                deletedAt = LocalDateTime.of(2023, 3, 3, 3, 3, 0),
                isDeleted = false,
                shop = shop,
                categoryId = 1L,
            ).apply { id = it.toLong() }
        }

        val productBackOffices: List<ProductBackOffice> = products.map {
            ProductBackOffice(
                quantity = it.id!!.toInt() * 15,
                price = it.id!!.toInt() * 1000,
                soldQuantity = 0,
                product = it
            ).apply { id = it.id }
        }

        for (i in 0..2) {
            products[i].productBackOffice = productBackOffices[i]
        }

        val reviews = products.map {
            Review(
                productId = it.id!!,
                buyer = buyer,
                rate = it.id!!.toFloat(),
                content = "테스트"
            ).apply { id = it.id!! }
        }

        val reviewProductDto = products.map {
            ReviewProductDto(
                productId = it.id!!,
                productName = it.name,
                productImage = it.productImage
            )
        }



        When("구매자에 리뷰가 있을 때") {
            every { reviewRepository.findAllByBuyerId(buyer.id!!) } returns reviews
            every { productRepository.findAllByProductId(reviews.map { it.id!! }) } returns reviewProductDto

            Then("ReviewResponse 리스트를 반환한다") {
                val response = reviewService.getBuyerReviews(buyer.id!!)
                response.size shouldBe 5

            }
        }

        When("구매자에 리뷰가 없을 때") {
            every { reviewRepository.findAllByBuyerId(buyer.id!!) } returns listOf()
            every { productRepository.findAllByProductId(emptyList()) } returns emptyList()
            Then("빈 리스트를 반환한다") {
                val response = reviewService.getBuyerReviews(buyer.id!!)
                response shouldBe emptyList()
            }
        }
    }

    Given("updateShopAverageRate 를 실행할 때") {

        val reviews = listOf(
            Review(buyer = buyer, productId = productId, rate = 4f, content = "Good").apply { id = 1L },
            Review(
                buyer = buyer.also { it.id = 2L },
                productId = productId,
                rate = 5f,
                content = "Excellent"
            ).apply { id = 2L }
        )

        When("리뷰가 존재하고 상품과 상점이 모두 존재할 때") {
            every { reviewRepository.findAllByShopId(shop.id!!) } returns reviews.map { it.rate }
            every { shopRepository.save(any()) } returns shop

            Then("상점의 평균 평점이 업데이트 된다") {
                reviewService.updateShopAverageRate(shop)
                shop.rate shouldBe 4.5f  // (4 + 5) / 2
                verify(exactly = 1) { shopRepository.save(any()) }
            }
        }

        When("리뷰가 없고 상품과 상점이 모두 존재할 때") {
            every { reviewRepository.findAllByShopId(shop.id!!) } returns emptyList()
            every { shopRepository.save(any()) } returns shop

            Then("상점의 평균 평점이 0으로 업데이트 된다") {
                reviewService.updateShopAverageRate(shop)
                shop.rate shouldBe 0f
                verify(exactly = 1) { shopRepository.save(any()) }
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