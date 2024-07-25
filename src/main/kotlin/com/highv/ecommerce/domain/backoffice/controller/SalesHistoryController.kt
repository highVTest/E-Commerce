package com.highv.ecommerce.domain.backoffice.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.backoffice.dto.saleshistory.SalesHistoryResponse
import com.highv.ecommerce.domain.backoffice.service.SalesHistoryService
import com.highv.ecommerce.domain.order_master.dto.OrderStatusRequest
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/seller/order-status")
class SalesHistoryController(
    private val salesHistoryService: SalesHistoryService
) {
    // 주문 상태 확인
    // TODO("수정 필요")
//    @PreAuthorize("hasRole('SELLER')")
//    @GetMapping("/{orderId}")
//    fun getOrderStatus(
//        @AuthenticationPrincipal seller: UserPrincipal,
//        @PathVariable orderId: Long
//    ): ResponseEntity<SalesHistoryResponse> = ResponseEntity
//        .status(HttpStatus.OK)
//        .body(salesHistoryService.getSalesHistory(seller.id, orderId))

    // 주문 상태 수정
    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/{orderId}")
    fun salesStatusChange(
        @AuthenticationPrincipal seller: UserPrincipal,
        @PathVariable orderId: Long,
        orderStatusRequest: OrderStatusRequest
    ): ResponseEntity<DefaultResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(salesHistoryService.salesStatusChange(seller.id, orderId, orderStatusRequest))
}