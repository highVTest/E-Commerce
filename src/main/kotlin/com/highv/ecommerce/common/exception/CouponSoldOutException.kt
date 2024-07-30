package com.highv.ecommerce.common.exception

data class CouponSoldOutException(
    override val errorCode: Int = 400,
    override val message: String
) : CustomRuntimeException(errorCode, message)
