package com.highv.ecommerce.domain.backoffice.dto.productbackoffice

import jakarta.validation.constraints.Min

data class QuantityRequest(
    @field: Min(value = 1, message = "수량은 1개 보다 적을 수 없습니다.")
    val quantity: Int
)