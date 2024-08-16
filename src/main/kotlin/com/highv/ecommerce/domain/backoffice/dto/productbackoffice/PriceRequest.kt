package com.highv.ecommerce.domain.backoffice.dto.productbackoffice

import jakarta.validation.constraints.Min

data class PriceRequest(
    @field:Min(value = 1, message = "가격은 1원 보다 적을 수 없습니다.")
    val price: Int
)