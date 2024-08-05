package com.highv.ecommerce.domain.product.dto

import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.ProductBackOfficeRequest

class CreateRequest(
    val createProductRequest: CreateProductRequest,
    val createProductBackOfficeRequest: ProductBackOfficeRequest
)