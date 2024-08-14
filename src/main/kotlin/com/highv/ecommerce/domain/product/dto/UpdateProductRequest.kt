package com.highv.ecommerce.domain.product.dto

import jakarta.validation.constraints.NotBlank

data class UpdateProductRequest(
    @field:NotBlank(message = "상품 명은 공백일 수 없습니다.")
    val name: String,
    val description: String,
    val isSoldOut: Boolean,
    val categoryId: Long,
)