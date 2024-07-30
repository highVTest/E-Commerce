package com.highv.ecommerce.domain.order_details.controller

import com.highv.ecommerce.domain.order_details.dto.BuyerOrderResponse
import com.highv.ecommerce.domain.order_details.dto.BuyerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.dto.OrderStatusResponse
import com.highv.ecommerce.domain.order_details.dto.SellerOrderResponse
import com.highv.ecommerce.domain.order_details.dto.SellerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.service.OrderDetailsService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/")
class OrderDetailsController(
    private val orderDetailsService: OrderDetailsService
) {

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/buyer/complain/{shopId}/{orderId}")
    fun buyerRequestComplain(
        @PathVariable("shopId") shopId: Long,
        @PathVariable("orderId") orderId: Long,
        @RequestBody buyerOrderStatusRequest: BuyerOrderStatusRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
    ): ResponseEntity<OrderStatusResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(orderDetailsService.buyerRequestComplain(buyerOrderStatusRequest, userPrincipal.id, shopId, orderId))

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/buyer/order-details")
    fun getBuyerOrders(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
    ): ResponseEntity<List<BuyerOrderResponse>> =
        ResponseEntity.status(HttpStatus.OK).body(orderDetailsService.getBuyerOrders(userPrincipal.id))

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/buyer/order-details/{orderId}")
    fun getBuyerOrderDetails(
        @PathVariable("orderId") orderId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<BuyerOrderResponse> =
        ResponseEntity.status(HttpStatus.OK).body(orderDetailsService.getBuyerOrderDetails(userPrincipal.id, orderId))

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/seller/complain/{shopId}/{orderId}")
    fun requestComplainReject(
        @PathVariable("shopId") shopId: Long,
        @PathVariable("orderId") orderId: Long,
        @RequestBody sellerOrderStatusRequest: SellerOrderStatusRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
    ): ResponseEntity<OrderStatusResponse> = ResponseEntity.status(HttpStatus.OK)
        .body(orderDetailsService.requestComplainReject(sellerOrderStatusRequest, shopId, orderId))

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/shop/order-details/{shopId}")
    fun getSellerOrderDetailsAll(
        @PathVariable("shopId") shopId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
    ): ResponseEntity<List<SellerOrderResponse>> = ResponseEntity.status(HttpStatus.OK)
        .body(orderDetailsService.getSellerOrderDetailsAll(shopId))

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/shop/order-details/{shopId}/{orderId}")
    fun getSellerOrderDetailsBuyer(
        @PathVariable("shopId") shopId: Long,  // Shop 추가 시 논의 후에 삭제 예정
        @PathVariable("orderId") orderId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
    ): ResponseEntity<SellerOrderResponse> = ResponseEntity.status(HttpStatus.OK)
        .body(orderDetailsService.getSellerOrderDetailsBuyer(shopId, orderId))

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/shop/complain/{shopId}/{orderId}/accept")
    fun requestComplainAccept(
        @PathVariable("shopId") shopId: Long,  // Shop 추가 시 논의 후에 삭제 예정
        @PathVariable("orderId") orderId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestBody sellerOrderStatusRequest: SellerOrderStatusRequest
    ): ResponseEntity<OrderStatusResponse> = ResponseEntity.status(HttpStatus.OK)
        .body(orderDetailsService.requestComplainAccept(shopId, orderId, sellerOrderStatusRequest))
}