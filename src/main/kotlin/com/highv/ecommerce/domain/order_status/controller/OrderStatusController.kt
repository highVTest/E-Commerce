package com.highv.ecommerce.domain.order_status.controller

import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.order_status.dto.OrderStatusResponse
import com.highv.ecommerce.domain.order_status.service.OrderStatusService
import com.highv.ecommerce.domain.order_status.dto.OrderRejectRequest
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
    @PatchMapping("/buyer/order-status/{itemCartId}")
    fun requestOrderStatusChange(
        @PathVariable("itemCartId") itemCartId: Long,
        @RequestBody buyerOrderStatusRequest: BuyerOrderStatusRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
    ): ResponseEntity<OrderStatusResponse>
         = ResponseEntity.status(HttpStatus.OK).body(orderStatusService.requestOrderStatusChange(itemCartId, buyerOrderStatusRequest, userPrincipal))


    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/seller/order-status/{shopId}/{orderId}")
    fun requestOrderStatusReject(
        @PathVariable("shopId") shopId: Long, // Shop 추가 시 논의 후에 삭제 예정
        @PathVariable("orderId") orderId: Long,
        @RequestBody sellerOrderStatusRequest: SellerOrderStatusRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
    ): ResponseEntity<OrderStatusResponse>
        = ResponseEntity.status(HttpStatus.OK).body(orderStatusService.requestOrderStatusReject(orderId, shopId, sellerOrderStatusRequest, userPrincipal))


    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/buyer/order-status")
    fun requestOrderStatusChangeList(
        @RequestBody buyerOrderStatusRequest: BuyerOrderStatusRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
    ): ResponseEntity<OrderStatusResponse>
        = ResponseEntity.status(HttpStatus.OK).body(orderStatusService.requestOrderStatusChangeList(buyerOrderStatusRequest, userPrincipal))

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/seller/order-status")
    fun requestOrderStatusRejectList(
        @RequestBody sellerOrderStatusRequest: SellerOrderStatusRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
    ): ResponseEntity<OrderStatusResponse>
        = ResponseEntity.status(HttpStatus.OK).body(orderStatusService.requestOrderStatusRejectList(sellerOrderStatusRequest, userPrincipal))

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/order_details/buyer")
    fun getBuyerOrderDetails(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
    ): ResponseEntity<List<ProductsOrderResponse>>
        = ResponseEntity.status(HttpStatus.OK).body(orderStatusService.getBuyerOrderDetails(userPrincipal))

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/order_details/seller/{shopId}")
    fun getSellerOrderDetails(
        @PathVariable("shopId") shopId: Long,  // Shop 추가 시 논의 후에 삭제 예정
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
    ): ResponseEntity<List<ProductsOrderResponse>>
        = ResponseEntity.status(HttpStatus.OK).body(orderStatusService.getSellerOrderDetails(shopId, userPrincipal))




}