package com.highv.ecommerce.domain.backoffice.dto.productbackoffice

import com.highv.ecommerce.domain.product.entity.Product

data class ProductBackOfficeRequest(
    val quantity: Int,
    val price: Int,
    val product: Product
)