package com.highv.ecommerce.domain.coupon.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.coupon.dto.CouponResponse
import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.service.CouponService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/coupon")
class CouponController(
    private val couponService: CouponService
){

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping
    fun createCoupon(
        couponRequest: CreateCouponRequest,
    ): ResponseEntity<DefaultResponse> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(couponService.createCoupon(couponRequest))

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/{couponId}")
    fun updateCoupon(
        @PathVariable("couponId") couponId:Long,
        updateCouponRequest: UpdateCouponRequest
    ): ResponseEntity<DefaultResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.updateCoupon(couponId, updateCouponRequest))

    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/{couponId}")
    fun deleteCoupon(
        @PathVariable couponId: Long
    ): ResponseEntity<DefaultResponse> = ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(couponService.deleteCoupon(couponId))

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller/{couponId}")
    fun getSellerCouponById(
        @PathVariable("couponId") couponId: Long
    ): ResponseEntity<CouponResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.getSellerCouponById(couponId))


    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller")
    fun getSellerCouponList(): ResponseEntity<List<CouponResponse>> = ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.getSellerCouponList())

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/buyer/{couponId}")
    fun getBuyerCouponById(
        @PathVariable("couponId") couponId: Long
    ): ResponseEntity<CouponResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.getBuyerCouponById(couponId))

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/buyer")
    fun getBuyerCouponList(): ResponseEntity<List<CouponResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.getBuyerCouponList())

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/{couponId}")
    fun issuedCoupon(@PathVariable couponId: Long): ResponseEntity<DefaultResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.issuedCoupon(couponId))
}