package com.highv.ecommerce.domain.product.dto

import jakarta.validation.constraints.NotBlank

data class CreateProductRequest(
    @field:NotBlank(message = "상품 명은 공백일 수 없습니다.")
    val name: String,
    val description: String,
    val categoryId: Long,
    val imageUrl: String,
)