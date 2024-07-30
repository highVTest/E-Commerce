package com.highv.ecommerce.common.exception

class InsufficientStockException(
    override val errorCode: Int = 400,
    override val message: String
) : CustomRuntimeException(errorCode, message)