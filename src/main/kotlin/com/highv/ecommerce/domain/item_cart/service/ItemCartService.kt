package com.highv.ecommerce.domain.item_cart.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.item_cart.dto.request.SelectProductQuantity
import com.highv.ecommerce.domain.item_cart.dto.response.CartResponse
import com.highv.ecommerce.domain.item_cart.dto.response.ItemResponse
import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemCartService(
    private val itemCartRepository: ItemCartRepository,
    private val productRepository: ProductRepository
) {
    @Transactional
    fun addItemIntoCart(productId: Long, request: SelectProductQuantity, buyerId: Long): DefaultResponse {

        if (request.quantity < 1) {
            throw CustomRuntimeException(400, "상품의 개수가 1개보다 적을 수 없습니다.")
        }

        val product: Product =
            productRepository.findByIdOrNull(productId) ?: throw CustomRuntimeException(404, "Product not found")

        val existsCart: ItemCart? = itemCartRepository.findByProductIdAndBuyerId(productId, buyerId)

        if (existsCart != null) {
            val quantity: Int = existsCart.quantity + request.quantity
            existsCart.updateQuantity(quantity)

            itemCartRepository.save(existsCart)
        } else {
            val item: ItemCart = ItemCart(
                product = product,
                quantity = request.quantity,
                buyerId = buyerId,
                shopId = product.shop.id!!
            )

            itemCartRepository.save(item)
        }

        return DefaultResponse("장바구니에 상품이 추가 됐습니다.")
    }

    @Transactional(readOnly = true)
    fun getMyCart(buyerId: Long): List<CartResponse> {
        val cart: List<ItemCart> = itemCartRepository.findByBuyerId(buyerId)
        val shopGroupItem: MutableMap<Long, MutableList<ItemResponse>> = mutableMapOf()

        cart.forEach {
            if (!shopGroupItem.containsKey(it.shopId)) {
                shopGroupItem[it.shopId] = mutableListOf()
            }
            shopGroupItem[it.shopId]!!.add(ItemResponse.from(it))
        }

        return shopGroupItem.map { (key, value) -> CartResponse(key, value) }
    }

    @Transactional
    fun updateItemIntoCart(productId: Long, request: SelectProductQuantity, buyerId: Long): DefaultResponse {

        val product: Product =
            productRepository.findByIdOrNull(productId) ?: throw CustomRuntimeException(404, "Product not found")

        val item: ItemCart =
            itemCartRepository.findByProductIdAndBuyerId(productId, buyerId)
                ?: throw CustomRuntimeException(404, "Item not found")

        item.updateQuantity(request.quantity) // 추후 프로덕트에서 price 관련된 게 생길 예정

        itemCartRepository.save(item)
        return DefaultResponse("상품 수량이 변경 됐습니다.")
    }

    @Transactional
    fun deleteItemIntoCart(productId: Long, buyerId: Long): DefaultResponse {

        val item: ItemCart = itemCartRepository.findByProductIdAndBuyerId(productId, buyerId)
            ?: throw CustomRuntimeException(404, "Item not found")

        // 구매자가 장바구니에서 물품을 지우는 경우 하드? 소프트?
        // 소프트인 경우 사용자가 뭘 관심있어하는지 알고리즘에 이용할 수 있음 --> 데이터 분석
        itemCartRepository.delete(item)
        return DefaultResponse("상품이 삭제 됐습니다.")
    }
}
