package com.highv.ecommerce.common.exception

class InvalidQuantityException(
    override val errorCode: Int = 400,
    override val message: String
) : CustomRuntimeException(errorCode, message)
