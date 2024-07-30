package com.highv.ecommerce.common.exception

data class LoginException(override val message: String) : RuntimeException()
