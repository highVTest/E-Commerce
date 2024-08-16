package com.highv.ecommerce.domain.order_master.dto

import jakarta.validation.constraints.Size

data class PaymentRequest(
    @field:Size(min = 0, message = "상품을 선택 해주세요")
    val cartIdList: List<Long>,
    val couponIdList: List<Long>
)