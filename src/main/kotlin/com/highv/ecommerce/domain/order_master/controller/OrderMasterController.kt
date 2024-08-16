package com.highv.ecommerce.domain.order_master.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.order_master.dto.PaymentRequest
import com.highv.ecommerce.domain.order_master.service.OrderMasterService
import com.highv.ecommerce.infra.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/")
class OrderMasterController(
    private val productsOrderService: OrderMasterService
) {
    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/payments")
    fun requestPayment(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody couponRequest: PaymentRequest,
        bindingResult: BindingResult
    ): ResponseEntity<DefaultResponse>{

        if (bindingResult.hasErrors()) {
            throw CustomRuntimeException(400, bindingResult.fieldError?.defaultMessage.toString())
        }

        return ResponseEntity.status(HttpStatus.OK).body(productsOrderService.requestPayment(userPrincipal.id, couponRequest))
    }

}