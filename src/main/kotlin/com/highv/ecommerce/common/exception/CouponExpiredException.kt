package com.highv.ecommerce.common.exception

data class CouponExpiredException(
    override val errorCode: Int = 400,
    override val message: String
) : CustomRuntimeException(errorCode, message)
