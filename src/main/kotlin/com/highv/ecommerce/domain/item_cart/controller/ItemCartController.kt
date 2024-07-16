package com.highv.ecommerce.domain.item_cart.controller

import com.highv.ecommerce.domain.item_cart.dto.request.AddItemIntoCartRequest
import com.highv.ecommerce.domain.item_cart.dto.request.UpdateItemIntoCartRequest
import com.highv.ecommerce.domain.item_cart.dto.response.ItemCartResponse
import com.highv.ecommerce.domain.item_cart.service.ItemCartService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/cart")
class ItemCartController(
    private val itemCartService: ItemCartService
) {

    @PostMapping("/{productId}")
    fun addItemIntoCart(
        @PathVariable(value = "productId") productId: Long,
        @RequestParam(value = "buyerId") buyerId: Long, // 추후 인증 인가 완료시 삭제
        @RequestBody request: AddItemIntoCartRequest
    ): ResponseEntity<Unit> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(itemCartService.addItemIntoCart(productId, request, buyerId))

    @GetMapping()
    fun getMyCart(
        @RequestParam(value = "buyerId") buyerId: Long, // 추후 인증 인가 완료시 삭제
    ): ResponseEntity<List<ItemCartResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(itemCartService.getMyCart(buyerId))

    @PutMapping("/{productId}")
    fun updateIntoCart(
        @PathVariable(value = "productId") productId: Long,
        @RequestParam(value = "buyerId") buyerId: Long, // 추후 인증 인가 완료시 삭제
        @RequestBody request: UpdateItemIntoCartRequest
    ): ResponseEntity<Unit> = ResponseEntity
        .status(HttpStatus.OK)
        .body(itemCartService.updateItemIntoCart(productId, request, buyerId))

    @DeleteMapping("/{productId}")
    fun deleteItemIntoCart(
        @PathVariable(value = "productId") productId: Long,
        @RequestParam(value = "buyerId") buyerId: Long // 추후 인증 인가 완료시 삭제
    ): ResponseEntity<Unit> = ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(itemCartService.deleteItemIntoCart(productId, buyerId))
}