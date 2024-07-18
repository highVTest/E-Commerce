package com.highv.ecommerce.domain.item_cart.controller

import com.highv.ecommerce.domain.item_cart.dto.request.AddItemIntoCartRequest
import com.highv.ecommerce.domain.item_cart.dto.request.UpdateItemIntoCartRequest
import com.highv.ecommerce.domain.item_cart.dto.response.ItemCartResponse
import com.highv.ecommerce.domain.item_cart.service.ItemCartService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/cart")
class ItemCartController(
    private val itemCartService: ItemCartService
) {

    @PostMapping("/{productId}")
    @PreAuthorize("hasRole('BUYER')")
    fun addItemIntoCart(
        @PathVariable(value = "productId") productId: Long,
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: AddItemIntoCartRequest
    ): ResponseEntity<Unit> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(itemCartService.addItemIntoCart(productId, request, user.id))
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping()
    fun getMyCart(
        @AuthenticationPrincipal user: UserPrincipal,
    ): ResponseEntity<List<ItemCartResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(itemCartService.getMyCart(user.id))

    @PreAuthorize("hasRole('BUYER')")
    @PutMapping("/{productId}")
    fun updateIntoCart(
        @PathVariable(value = "productId") productId: Long,
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: UpdateItemIntoCartRequest
    ): ResponseEntity<Unit> = ResponseEntity
        .status(HttpStatus.OK)
        .body(itemCartService.updateItemIntoCart(productId, request, user.id))

    @PreAuthorize("hasRole('BUYER')")
    @DeleteMapping("/{productId}")
    fun deleteItemIntoCart(
        @PathVariable(value = "productId") productId: Long,
        @AuthenticationPrincipal user: UserPrincipal,
    ): ResponseEntity<Unit> = ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(itemCartService.deleteItemIntoCart(productId, user.id))
}