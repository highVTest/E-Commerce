package com.highv.ecommerce.common.exception

class AdminLoginFailedException(
    override val errorCode: Int = 401,
    override val message: String
) : CustomRuntimeException(errorCode, message)
