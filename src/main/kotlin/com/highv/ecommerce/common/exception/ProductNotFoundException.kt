package com.highv.ecommerce.common.exception

data class ProductNotFoundException(
    override val errorCode: Int = 404,
    override val message: String
) : CustomRuntimeException(errorCode, message)

