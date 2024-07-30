package com.highv.ecommerce.common.exception

data class BuyerLoginFailedException(
    override val errorCode: Int = 401,
    override val message: String
) : CustomRuntimeException(errorCode, message)
