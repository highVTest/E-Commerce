package com.highv.ecommerce.item_cart

import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

class ItemCartEntityTest : AnnotationSpec() {

    @Test
    fun `상품 수량을 변경할 때 수량이 1개 보다 적으면 예외가 발생한다`() {
        val cart = ItemCart(
            productId = 1L,
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
            productId = 1L,
            productName = "testName",
            price = 3000,
            quantity = 1,
            buyerId = 1L
        ).apply { id = 1L }

        cart.updateQuantity(6)
    }
}