package com.highv.ecommerce.common.exception

open class CustomRuntimeException(
    open val errorCode: Int,
    override val message: String
) : RuntimeException(message)
