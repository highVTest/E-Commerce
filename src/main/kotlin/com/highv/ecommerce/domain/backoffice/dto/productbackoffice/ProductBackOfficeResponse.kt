package com.highv.ecommerce.domain.backoffice.dto.productbackoffice

import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice

data class ProductBackOfficeResponse(
    val quantity: Int,
    val price: Int
) {
    companion object {
        fun from(productBackOffice: ProductBackOffice) = ProductBackOfficeResponse(
            productBackOffice.quantity,
            productBackOffice.price
        )
    }
}