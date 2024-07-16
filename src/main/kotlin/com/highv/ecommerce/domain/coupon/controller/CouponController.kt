package com.highv.ecommerce.domain.coupon.controller

import com.highv.ecommerce.common.dto.DefaultResponse
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
    ): ResponseEntity<DefaultResponse> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(couponService.createCoupon(couponRequest))

    @PatchMapping("/{couponId}")
    fun updateCoupon(
        @PathVariable("couponId") couponId:Long,
        updateCouponRequest: UpdateCouponRequest
    ): ResponseEntity<DefaultResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.updateCoupon(couponId, updateCouponRequest))

    @DeleteMapping("/{couponId}")
    fun deleteCoupon(
        @PathVariable couponId: Long
    ): ResponseEntity<DefaultResponse> = ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(couponService.deleteCoupon(couponId))

    @GetMapping("/seller/{couponId}")
    fun getSellerCouponById(
        @PathVariable("couponId") couponId: Long
    ): ResponseEntity<CouponResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.getSellerCouponById(couponId))


    @GetMapping("/seller")
    fun getSellerCouponList(): ResponseEntity<List<CouponResponse>> = ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.getSellerCouponList())

    @GetMapping("/buyer/{couponId}")
    fun getBuyerCouponById(
        @PathVariable("couponId") couponId: Long
    ): ResponseEntity<CouponResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.getBuyerCouponById(couponId))


    @GetMapping("/buyer")
    fun getBuyerCouponList(): ResponseEntity<List<CouponResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.getBuyerCouponList(couponId))


}