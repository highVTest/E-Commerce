package com.highv.ecommerce.common.exception

data class CustomRuntimeException(val errorCode: Int, override val message: String) : RuntimeException(message)
