package com.highv.ecommerce.common.exception

class UnauthorizedException(
    override val errorCode: Int = 403,
    override val message: String
) : CustomRuntimeException(errorCode, message)