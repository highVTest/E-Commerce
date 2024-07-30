package com.highv.ecommerce.common.exception

data class EmailAlreadyExistsException(
    override val errorCode: Int = 409,
    override val message: String
) : CustomRuntimeException(errorCode, message)
