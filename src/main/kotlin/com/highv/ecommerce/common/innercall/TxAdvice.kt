package com.highv.ecommerce.common.innercall

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class TxAdvice {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun <T> run(function: () -> T): T {
        return function()
    }
}