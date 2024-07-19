package com.highv.ecommerce.domain.coupon.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.coupon.dto.CouponResponse
import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.service.CouponService
import com.highv.ecommerce.infra.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/coupon")
class CouponController(
    private val couponService: CouponService
){

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping
    fun createCoupon(
        @Valid @RequestBody couponRequest: CreateCouponRequest,
        bindingResult: BindingResult,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<DefaultResponse>{

        if(bindingResult.hasErrors()) throw RuntimeException(bindingResult.fieldError?.defaultMessage.toString())
        if(userPrincipal == null) throw RuntimeException()

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(couponService.createCoupon(couponRequest, userPrincipal))
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/{couponId}")
    fun updateCoupon(
        @PathVariable("couponId") couponId:Long,
        @Valid @RequestBody updateCouponRequest: UpdateCouponRequest,
        bindingResult: BindingResult,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<DefaultResponse> {

        if(bindingResult.hasErrors()) throw RuntimeException(bindingResult.fieldError?.defaultMessage.toString())

        if(userPrincipal == null) throw RuntimeException()

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.updateCoupon(couponId, updateCouponRequest, userPrincipal))
    }



    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/{couponId}")
    fun deleteCoupon(
        @PathVariable couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<DefaultResponse> {

        if(userPrincipal == null) throw RuntimeException()

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(couponService.deleteCoupon(couponId, userPrincipal))
    }


    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller/{couponId}")
    fun getSellerCouponById(
        @PathVariable("couponId") couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<CouponResponse> {

        if(userPrincipal == null) throw RuntimeException()

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.getSellerCouponById(couponId, userPrincipal))
    }


    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller")
    fun getSellerCouponList(
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<List<CouponResponse>> {

        if(userPrincipal == null) throw RuntimeException()

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.getSellerCouponList(userPrincipal))
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/buyer/{couponId}")
    fun getBuyerCouponById(
        @PathVariable("couponId") couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<CouponResponse> {

        if(userPrincipal == null) throw RuntimeException()

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.getBuyerCouponById(couponId, userPrincipal))
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/buyer")
    fun getBuyerCouponList(
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<List<CouponResponse>> {

        if(userPrincipal == null) throw RuntimeException()

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.getBuyerCouponList(userPrincipal))
    }

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/{couponId}")
    fun issuedCoupon(
        @PathVariable couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<DefaultResponse> {

        if(userPrincipal == null) throw RuntimeException()

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.issuedCoupon(couponId, userPrincipal))
    }
}