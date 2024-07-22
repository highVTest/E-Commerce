package com.highv.ecommerce.item_cart

import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.shop.entity.Shop
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class ItemCartEntityTest : AnnotationSpec() {

    @Test
    fun `상품 수량을 변경할 때 수량이 1개 보다 적으면 예외가 발생한다`() {
        val cart = ItemCart(
            product = product,
            productName = "testName",
            price = 3000,
            quantity = 1,
            buyerId = 1L
        ).apply { id = 1L }

        shouldThrow<RuntimeException> {
            cart.updateQuantity(0)
        }.let {
            it.message shouldBe "물품의 수량이 0보다 작거나 같을 수 없습니다."
        }
    }

    @Test
    fun `상품 수량을 변경할 때 1개보다 많으면 변경된다`() {
        val cart = ItemCart(
            product = product,
            productName = "testName",
            price = 3000,
            quantity = 1,
            buyerId = 1L
        ).apply { id = 1L }

        cart.updateQuantity(6)
    }

    companion object{
        private val shop = Shop(
            sellerId = 1L,
            name = "testName",
            description = "testDescription",
            shopImage = "testImage",
            rate = 1f
        )
        private val product = Product(
            name = "productName",
            description = "productDescription",
            productImage = "image",
            favorite = 1,
            createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
            updatedAt = LocalDateTime.of(2021, 1, 1, 1, 1, 1),
            isSoldOut = false,
            shop = shop,
            categoryId = 1L
        )
    }
}