package com.highv.ecommerce.domain.coupon.controller

import com.highv.ecommerce.domain.coupon.dto.CouponResponse
import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.service.CouponService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/coupon")
class CouponController(
    private val couponService: CouponService
){

    @PostMapping
    fun createCoupon(
        couponRequest: CreateCouponRequest,
    ): ResponseEntity<CouponResponse> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(couponService.createCoupon(couponRequest))

    @PatchMapping("/{couponId}")
    fun updateCoupon(
        @PathVariable("couponId") couponId:Long,
        updateCouponRequest: UpdateCouponRequest
    ): ResponseEntity<CouponResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.updatecoupon(couponId, updateCouponRequest))

    @DeleteMapping("/{couponId}")
    fun deleteCoupon(
        @PathVariable couponId: Long
    ): ResponseEntity<Unit> = ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(couponService.deletecoupon(couponId))

    @GetMapping("/{couponId}")
    fun getCouponById(
        @PathVariable("couponId") couponId: Long
    ): ResponseEntity<CouponResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.getcouponById(couponId))

}