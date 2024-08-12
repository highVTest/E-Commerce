package com.highv.ecommerce.common.exception

data class ModelNotFoundException(
    val errorCD: Int = 404,
    val msg: String
) : CustomRuntimeException(errorCD, msg)
