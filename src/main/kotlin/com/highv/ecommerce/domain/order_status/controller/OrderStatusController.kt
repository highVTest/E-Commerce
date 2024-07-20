package com.highv.ecommerce.domain.order_status.controller

import com.highv.ecommerce.domain.order_status.dto.OrderStatusResponse
import com.highv.ecommerce.domain.order_status.service.OrderStatusService
import com.highv.ecommerce.domain.products_order.dto.DescriptionRequest
import com.highv.ecommerce.domain.order_status.dto.BuyerOrderStatusRequest
import com.highv.ecommerce.domain.order_status.dto.SellerOrderStatusRequest
import com.highv.ecommerce.domain.products_order.dto.ProductsOrderResponse
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/")
class OrderStatusController(
    private val orderStatusService: OrderStatusService
){

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/buyer/order-status/{orderId}")
    fun requestOrderStatusChange(
        @PathVariable("orderId") orderId: Long,
        @RequestBody descriptionRequest: DescriptionRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<OrderStatusResponse> {
        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(orderStatusService.requestOrderStatusChange(orderId, descriptionRequest, userPrincipal))
    }

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/seller/order-status/{orderId}")
    fun requestOrderStatusReject(
        @PathVariable("orderId") orderId: Long,
        @RequestBody descriptionRequest: DescriptionRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<OrderStatusResponse> {

        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(orderStatusService.requestOrderStatusReject(orderId, descriptionRequest, userPrincipal))
    }

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/buyer/order-status")
    fun requestOrderStatusChangeList(
        @RequestBody buyerOrderStatusRequest: BuyerOrderStatusRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<OrderStatusResponse> {
        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(orderStatusService.requestOrderStatusChangeList(buyerOrderStatusRequest, userPrincipal))
    }

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/seller/order-status")
    fun requestOrderStatusRejectList(
        @RequestBody sellerOrderStatusRequest: SellerOrderStatusRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<OrderStatusResponse> {

        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(orderStatusService.requestOrderStatusRejectList(sellerOrderStatusRequest, userPrincipal))
    }

    @PreAuthorize("hasRole('SELLER') or hasRole('BUYER')")
    @GetMapping("/order_details/{orderId}")
    fun getOrderDetails(
        @PathVariable("orderId") orderId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<ProductsOrderResponse> {

        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(orderStatusService.getOrderDetails(orderId, userPrincipal))
    }



}