package com.highv.ecommerce.domain.backoffice.dto.productbackoffice

import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.product.entity.Product
import java.time.LocalDateTime

data class SellersProductResponse(
    val id: Long,
    val name: String,
    val quantity: Int,
    val price: Int,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(product: Product, productBackOffice: ProductBackOffice) = SellersProductResponse(
            product.id!!,
            product.name,
            productBackOffice.quantity,
            productBackOffice.price,
            product.createdAt
        )
    }
}