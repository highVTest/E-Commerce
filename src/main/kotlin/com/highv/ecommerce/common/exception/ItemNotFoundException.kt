package com.highv.ecommerce.common.exception

data class ItemNotFoundException(
    override val errorCode: Int = 404,
    override val message: String
) : CustomRuntimeException(errorCode, message)

