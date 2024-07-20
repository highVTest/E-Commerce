package com.highv.ecommerce.domain.products_order.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.order_status.dto.BuyerOrderStatusRequest
import com.highv.ecommerce.domain.products_order.dto.OrderStatusRequest
import com.highv.ecommerce.domain.products_order.service.ProductsOrderService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/")
class ProductsOrderController(
    private val productsOrderService: ProductsOrderService
) {

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/payments")
    fun requestPayment(
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    )
            : ResponseEntity<DefaultResponse> {

        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(productsOrderService.requestPayment(userPrincipal))
    }

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/order_status/{orderId}")
    fun updateOrderStatus(
        @PathVariable("orderId") orderId: Long,
        @RequestBody orderStatusRequest: OrderStatusRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<DefaultResponse> {

        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(productsOrderService.updateOrderStatus(orderId, orderStatusRequest, userPrincipal))
    }




}