package com.highv.ecommerce.domain.order_reject.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.order_reject.service.OrderRejectService
import com.highv.ecommerce.domain.products_order.dto.DescriptionRequest
import com.highv.ecommerce.domain.products_order.dto.ProductsOrderResponse
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/")
class OrderRejectController(
    private val orderRejectService: OrderRejectService
){

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/buyer/refund/{orderId}")
    fun requestRefund(
        @PathVariable("orderId") orderId: Long,
        @RequestBody descriptionRequest: DescriptionRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<DefaultResponse> {
        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(orderRejectService.requestRefund(orderId, descriptionRequest, userPrincipal))
    }

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/seller/refund/reject/{orderId}")
    fun requestRefundReject(
        @PathVariable("orderId") orderId: Long,
        @RequestBody descriptionRequest: DescriptionRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<DefaultResponse> {

        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(orderRejectService.requestRefundReject(orderId, descriptionRequest, userPrincipal))
    }

    @PreAuthorize("hasRole('SELLER') or hasRole('BUYER')")
    @GetMapping("/order_status/{orderId}")
    fun getOrderDetails(
        @PathVariable("orderId") orderId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<ProductsOrderResponse> {

        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(orderRejectService.getOrderDetails(orderId, userPrincipal))
    }


    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/buyer/order_cancelled/{orderId}")
    fun requestOrderCanceled(
        @PathVariable("orderId") orderId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<DefaultResponse> {

        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(orderRejectService.requestOrderCanceled(orderId, userPrincipal))
    }
}