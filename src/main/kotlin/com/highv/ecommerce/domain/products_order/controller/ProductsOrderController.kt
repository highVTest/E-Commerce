package com.highv.ecommerce.domain.products_order.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.products_order.repository.ProductsOrderRepository
import com.highv.ecommerce.domain.products_order.service.ProductsOrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.io.encoding.Base64

@RestController
@RequestMapping("/api/v1/")
class ProductsOrderController(
    private val productsOrderService: ProductsOrderService
){

    @PostMapping("/payments/{cartId}")
    fun requestPayment(@PathVariable("cartId") cartId: Long): ResponseEntity<DefaultResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(productsOrderService.requestPayment(cartId))
    }
}