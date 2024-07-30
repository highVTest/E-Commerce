package com.highv.ecommerce.common.exception

class CartEmptyException(
    override val errorCode: Int = 400,
    override val message: String
) : CustomRuntimeException(errorCode, message)