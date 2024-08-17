package com.highv.ecommerce.domain.backoffice.service

import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.PriceRequest
import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.ProductBackOfficeResponse
import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.QuantityRequest
import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.SellersProductResponse
import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class InventoryManagementService(
    private val productBackOfficeRepository: ProductBackOfficeRepository,
    private val productRepository: ProductRepository,
    private val shopRepository: ShopRepository
) {
    fun changeQuantity(sellerId: Long, productId: Long, quantity: QuantityRequest): ProductBackOfficeResponse {
        val productBackOffice = validateProductBO(sellerId, productId)
        productBackOffice.quantity = quantity.quantity
        val changedProduct = productBackOfficeRepository.save(productBackOffice)
        return ProductBackOfficeResponse.from(changedProduct)
    }

    fun changePrice(sellerId: Long, productId: Long, price: PriceRequest): ProductBackOfficeResponse {
        val productBackOffice = validateProductBO(sellerId, productId)
        productBackOffice.price = price.price
        val changedProduct = productBackOfficeRepository.save(productBackOffice)
        return ProductBackOfficeResponse.from(changedProduct)
    }

    fun getSellerProducts(sellerId: Long, pageable: Pageable): Page<SellersProductResponse> {
        val shop = shopRepository.findBySellerId(sellerId)
        val products = productRepository.findPaginatedByShopId(shop.id!!, pageable)
        return products.map { SellersProductResponse.from(it, it.productBackOffice!!) }
    }

    private fun validateProductBO(sellerId: Long, productId: Long): ProductBackOffice {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw IllegalArgumentException("Product with id $productId not found")
        if (product.shop.sellerId != sellerId) throw IllegalArgumentException("No Authority")
        val productBO = product.productBackOffice ?: throw IllegalArgumentException("No ProductBackOffice")

        return productBO
    }
}