package com.highv.ecommerce.common.exception

data class KakaoAccessTokenException(
    override val errorCode: Int = 500,
    override val message: String
) : CustomRuntimeException(errorCode, message)