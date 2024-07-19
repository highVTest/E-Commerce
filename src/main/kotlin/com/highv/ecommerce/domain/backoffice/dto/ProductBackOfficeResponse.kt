package com.highv.ecommerce.domain.backoffice.dto

import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice

data class ProductBackOfficeResponse(
    val id: Long,
    val quantity: Int,
    val price: Int
) {
    companion object {
        fun from(productBackOffice: ProductBackOffice) = ProductBackOfficeResponse(
            productBackOffice.id,
            productBackOffice.quantity,
            productBackOffice.price
        )
    }
}