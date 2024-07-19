package com.highv.ecommerce.domain.products_order.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.products_order.dto.DescriptionRequest
import com.highv.ecommerce.domain.products_order.dto.OrderStatusRequest
import com.highv.ecommerce.domain.products_order.dto.ProductsOrderResponse
import com.highv.ecommerce.domain.products_order.repository.ProductsOrderRepository
import com.highv.ecommerce.domain.products_order.service.ProductsOrderService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import kotlin.io.encoding.Base64

@RestController
@RequestMapping("/api/v1/")
class ProductsOrderController(
    private val productsOrderService: ProductsOrderService
) {

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/payments/{cartId}")
    fun requestPayment(
        @PathVariable("cartId") cartId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    )
            : ResponseEntity<DefaultResponse> {

        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(productsOrderService.requestPayment(cartId, userPrincipal))
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

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/refund/{orderId}")
    fun requestRefund(
        @PathVariable("orderId") orderId: Long,
        @RequestBody descriptionRequest: DescriptionRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<DefaultResponse> {
        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(productsOrderService.requestRefund(orderId, descriptionRequest, userPrincipal))
    }

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/refund/reject/{orderId}")
    fun requestRefundReject(
        @PathVariable("orderId") orderId: Long,
        @RequestBody descriptionRequest: DescriptionRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<DefaultResponse> {

        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(productsOrderService.requestRefundReject(orderId, descriptionRequest, userPrincipal))
    }

    @PreAuthorize("hasRole('SELLER') or hasRole('BUYER')")
    @GetMapping("/order_status/{orderId}")
    fun getOrderDetails(
        @PathVariable("orderId") orderId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<ProductsOrderResponse> {

        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(productsOrderService.getOrderDetails(orderId, userPrincipal))
    }


    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/order_cancelled/{orderId}")
    fun requestOrderCanceled(
        @PathVariable("orderId") orderId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<DefaultResponse> {

        if (userPrincipal == null) throw RuntimeException("로그인 실패!!")

        return ResponseEntity.status(HttpStatus.OK).body(productsOrderService.requestOrderCanceled(orderId, userPrincipal))
    }


}