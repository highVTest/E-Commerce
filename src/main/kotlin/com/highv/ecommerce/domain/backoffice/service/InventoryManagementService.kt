package com.highv.ecommerce.domain.backoffice.service

import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.PriceRequest
import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.ProductBackOfficeResponse
import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.QuantityRequest
import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.SellersProductResponse
import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import org.springframework.stereotype.Service

@Service
class InventoryManagementService(
    private val productBackOfficeRepository: ProductBackOfficeRepository,
    private val productRepository: ProductRepository,
    private val shopRepository: ShopRepository
) {
    fun getProductsQuantity(sellerId: Long, productId: Long): ProductBackOfficeResponse {
        val product = validateProduct(sellerId, productId)
        return ProductBackOfficeResponse(product.quantity, product.price)
    }

    fun changeQuantity(sellerId: Long, productId: Long, quantity: QuantityRequest): ProductBackOfficeResponse {
        val product = validateProduct(sellerId, productId)
        product.quantity = quantity.quantity
        val changedProduct = productBackOfficeRepository.save(product)
        return ProductBackOfficeResponse.from(changedProduct)
    }

    fun changePrice(sellerId: Long, productId: Long, price: PriceRequest): ProductBackOfficeResponse {
        val product = validateProduct(sellerId, productId)
        product.price = price.price
        val changedProduct = productBackOfficeRepository.save(product)
        return ProductBackOfficeResponse.from(changedProduct)
    }

    fun getSellerProducts(sellerId: Long): List<SellersProductResponse> {
        val shop = shopRepository.findShopBySellerId(sellerId)
        val products = productRepository.findProductByShopId(shop.id!!)
        return products.map { SellersProductResponse.from(it, it.productBackOffice!!) }
    }

    private fun validateProduct(sellerId: Long, productId: Long): ProductBackOffice {
        // val product = productRepository.findByIdOrNull(productId)?.productBackOffice?.product
        //     ?: throw IllegalArgumentException("Product with id $productId not found")
        // if (product.shop.sellerId != sellerId) throw IllegalArgumentException("No Authority")
        // val productBackOffice = productBackOfficeRepository.findProductBackOfficesByProductId(product.id!!)
        //     ?: throw IllegalArgumentException("Product not found for seller $sellerId")

        val product = productRepository.findByIdOrNull(productId)
            ?: throw IllegalArgumentException("Product with id $productId not found")

        if (product.shop.sellerId != sellerId) throw IllegalArgumentException("No Authority")

        val productBackOffice = product.productBackOffice ?: throw IllegalArgumentException("No ProductBackOffice")

        return productBackOffice
    }
}