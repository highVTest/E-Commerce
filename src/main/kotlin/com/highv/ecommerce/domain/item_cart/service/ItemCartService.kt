package com.highv.ecommerce.domain.item_cart.service

import com.highv.ecommerce.domain.item_cart.dto.request.AddItemIntoCartRequest
import com.highv.ecommerce.domain.item_cart.dto.request.UpdateItemIntoCartRequest
import com.highv.ecommerce.domain.item_cart.dto.response.ItemCartResponse
import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemCartService(
    private val itemCartRepository: ItemCartRepository,
    private val productRepository: ProductRepository
) {
    fun addItemIntoCart(productId: Long, request: AddItemIntoCartRequest, buyerId: Long) {

        if (request.quantity < 1) {
            throw RuntimeException("상품의 개수가 1개보다 적을 수 없습니다.")
        }

        val product: Product =
            productRepository.findByIdOrNull(productId) ?: throw RuntimeException("Product not found")

        val item: ItemCart = ItemCart(
            productId = productId,
            productName = product.name,
            price = 3000 * request.quantity, // 추후 프로덕트에서 price 관련된 게 생길 예정
            quantity = request.quantity,
            buyerId = buyerId
        )

        itemCartRepository.save(item)
    }

    @Transactional(readOnly = true)
    fun getMyCart(buyerId: Long): List<ItemCartResponse> {
        val cart: List<ItemCart> = itemCartRepository.findByBuyerIdAndIsDeletedFalse(buyerId)

        return cart.map { ItemCartResponse.from(it) }
    }

    @Transactional
    fun updateItemIntoCart(productId: Long, request: UpdateItemIntoCartRequest, buyerId: Long) {

        val product: Product =
            productRepository.findByIdOrNull(productId) ?: throw RuntimeException("Product not found")

        val item: ItemCart =
            itemCartRepository.findByProductIdAndBuyerIdAndIsDeletedFalse(productId, buyerId)
                ?: throw RuntimeException("Item not found")

        item.updateQuantityAndPrice(request.quantity, 3000) // 추후 프로덕트에서 price 관련된 게 생길 예정

        itemCartRepository.save(item)
    }

    @Transactional
    fun deleteItemIntoCart(productId: Long, buyerId: Long) {

        val item: ItemCart = itemCartRepository.findByProductIdAndBuyerIdAndIsDeletedFalse(productId, buyerId)
            ?: throw RuntimeException("Item not found")

        // 구매자가 장바구니에서 물품을 지우는 경우 하드? 소프트?
        // 소프트인 경우 사용자가 뭘 관심있어하는지 알고리즘에 이용할 수 있음 --> 데이터 분석
        itemCartRepository.delete(item)
    }
}
