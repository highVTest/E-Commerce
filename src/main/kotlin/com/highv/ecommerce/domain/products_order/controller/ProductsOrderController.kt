package com.highv.ecommerce.domain.products_order.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.products_order.dto.DescriptionRequest
import com.highv.ecommerce.domain.products_order.dto.OrderStatusRequest
import com.highv.ecommerce.domain.products_order.repository.ProductsOrderRepository
import com.highv.ecommerce.domain.products_order.service.ProductsOrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.io.encoding.Base64

@RestController
@RequestMapping("/api/v1/")
class ProductsOrderController(
    private val productsOrderService: ProductsOrderService
){

    @PostMapping("/payments/{cartId}")
    fun requestPayment(@PathVariable("cartId") cartId: Long): ResponseEntity<DefaultResponse>
        = ResponseEntity.status(HttpStatus.OK).body(productsOrderService.requestPayment(cartId))

    @PatchMapping("/order_status/{orderId}")
    fun updateOrderStatus(
        @PathVariable("orderId") orderId: Long,
        @RequestBody orderStatusRequest: OrderStatusRequest
    ): ResponseEntity<DefaultResponse>
        = ResponseEntity.status(HttpStatus.OK).body(productsOrderService.updateOrderStatus(orderId, orderStatusRequest))

    @PatchMapping("/refund/{orderId}")
    fun requestRefund(
        @PathVariable("orderId") orderId: Long,
        @RequestBody descriptionRequest: DescriptionRequest
    ): ResponseEntity<DefaultResponse>
        = ResponseEntity.status(HttpStatus.OK).body(productsOrderService.requestRefund(orderId, descriptionRequest))

    @PatchMapping("/refund/reject/{orderId}")
    fun requestRefundReject(
        @PathVariable("orderId") orderId: Long,
        @RequestBody descriptionRequest: DescriptionRequest
    ): ResponseEntity<DefaultResponse>
        = ResponseEntity.status(HttpStatus.OK).body(productsOrderService.requestRefundReject(orderId, descriptionRequest))

    @PatchMapping("/exchange/{orderId}")
    fun requestExchange(
        @PathVariable("orderId") orderId: Long,
        @RequestBody descriptionRequest: DescriptionRequest
    ): ResponseEntity<DefaultResponse>
       = ResponseEntity.status(HttpStatus.OK).body(productsOrderService.requestExchange(orderId, descriptionRequest))

    @PatchMapping("/exchange/reject/{orderId}")
    fun requestExchangeReject(
        @PathVariable("orderId") orderId: Long,
        @RequestBody descriptionRequest: DescriptionRequest
    ): ResponseEntity<DefaultResponse>
        = ResponseEntity.status(HttpStatus.OK).body(productsOrderService.requestExchangeReject(orderId, descriptionRequest))

    @GetMapping("/order_status/{orderId}")
    fun getOrderDetails(@PathVariable("orderId") orderId: Long,): ResponseEntity<ProductsOrderResponse>
        = ResponseEntity.status(HttpStatus.OK).body(productsOrderService.getOrderDetails(orderId))

    @PatchMapping("/order_cancelled/{orderId}")
    fun requestOrderCanceled(@PathVariable("orderId") orderId: Long): ResponseEntity<DefaultResponse>
        = ResponseEntity.status(HttpStatus.OK).body(productsOrderService.requestOrderCanceled(orderId))


}