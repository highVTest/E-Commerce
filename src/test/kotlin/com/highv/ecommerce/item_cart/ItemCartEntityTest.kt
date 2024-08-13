//package com.highv.ecommerce.item_cart
//
//import com.highv.ecommerce.common.exception.InvalidQuantityException
//import com.highv.ecommerce.domain.item_cart.entity.ItemCart
//import com.highv.ecommerce.domain.product.entity.Product
//import com.highv.ecommerce.domain.seller.shop.entity.Shop
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.core.spec.style.AnnotationSpec
//import io.kotest.matchers.shouldBe
//import java.time.LocalDateTime
//
//class ItemCartEntityTest : AnnotationSpec() {
//
//    @Test
//    fun `상품 수량을 변경할 때 수량이 1개 보다 적으면 예외가 발생한다`() {
//        val shop = Shop(
//            sellerId = 1L,
//            name = "testName",
//            description = "testDescription",
//            shopImage = "testImage",
//            rate = 0.0f
//        )
//
//        val product = Product(
//            name = "Test Product",
//            description = "Test Description",
//            productImage = "image.jpg",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = LocalDateTime.now(),
//            isDeleted = false,
//            shop = shop,
//            categoryId = 1L
//        ).apply { id = 1L }
//
//        val cart = ItemCart(
//            product = product,
//            quantity = 1,
//            buyerId = 1L,
//            shop = shop
//        ).apply { id = 1L }
//
//        shouldThrow<InvalidQuantityException> {
//            cart.updateQuantity(0)
//        }.let {
//            it.message shouldBe "상품의 수량이 1개보다 적을 수 없습니다."
//        }
//    }
//
//    @Test
//    fun `상품 수량을 변경할 때 1개보다 많으면 변경된다`() {
//
//        val shop = Shop(
//            sellerId = 1L,
//            name = "testName",
//            description = "testDescription",
//            shopImage = "testImage",
//            rate = 0.0f
//        )
//
//        val product = Product(
//            name = "Test Product",
//            description = "Test Description",
//            productImage = "image.jpg",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = LocalDateTime.now(),
//            isDeleted = false,
//            shop = shop,
//            categoryId = 1L
//        ).apply { id = 1L }
//
//        val cart = ItemCart(
//            product = product,
//            shop = shop,
//            quantity = 1,
//            buyerId = 1L,
//        ).apply { id = 1L }
//
//        cart.updateQuantity(6)
//    }
//
//    companion object {
//        private val shop = Shop(
//            sellerId = 1L,
//            name = "testName",
//            description = "testDescription",
//            shopImage = "testImage",
//            rate = 1f
//        ).apply { id = 1L }
//
//        private val product = Product(
//            name = "productName",
//            description = "productDescription",
//            productImage = "image",
//            createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
//            updatedAt = LocalDateTime.of(2021, 1, 1, 1, 1, 1),
//            isSoldOut = false,
//            shop = shop,
//            categoryId = 1L
//        )
//    }
//}