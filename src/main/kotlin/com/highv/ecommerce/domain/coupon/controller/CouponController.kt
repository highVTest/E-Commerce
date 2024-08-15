package com.highv.ecommerce.domain.coupon.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.InvalidCouponRequestException
import com.highv.ecommerce.domain.coupon.dto.BuyerCouponResponse
import com.highv.ecommerce.domain.coupon.dto.SellerCouponResponse
import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.service.CouponService
import com.highv.ecommerce.infra.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class CouponController(
    private val couponService: CouponService
) {

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/seller/coupon")
    fun createCoupon(
        @Valid @RequestBody couponRequest: CreateCouponRequest,
        bindingResult: BindingResult,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<DefaultResponse> {


        if (bindingResult.hasErrors()) throw InvalidCouponRequestException(
            400,
            bindingResult.fieldError?.defaultMessage.toString()
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(couponService.createCoupon(couponRequest, userPrincipal.id))
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/seller/coupon/{couponId}")
    fun updateCoupon(
        @PathVariable("couponId") couponId: Long,
        @Valid @RequestBody updateCouponRequest: UpdateCouponRequest,
        bindingResult: BindingResult,
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
    ): ResponseEntity<DefaultResponse> {

        if (bindingResult.hasErrors()) throw InvalidCouponRequestException(
            400,
            bindingResult.fieldError?.defaultMessage.toString()
        )

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.updateCoupon(couponId, updateCouponRequest, userPrincipal.id))
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller/coupon/{couponId}")
    fun getSellerCouponById(
        @PathVariable("couponId") couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<SellerCouponResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.getSellerCouponById(couponId, userPrincipal.id))


    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller/coupon")
    fun getSellerCouponList(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<List<SellerCouponResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.getSellerCouponList(userPrincipal.id))

    @GetMapping("/coupon/{productId}")
    fun getDetailCoupon(
        @PathVariable("productId") productId: Long,
    ): ResponseEntity<SellerCouponResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.getDetailCoupon(productId))


    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/buyer/coupon/{productId}")
    fun getBuyerCouponById(
        @PathVariable("productId") productId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<BuyerCouponResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.getBuyerCouponById(productId, userPrincipal.id))


    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/buyer/coupon")
    fun getBuyerCouponList(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<List<BuyerCouponResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.getBuyerCouponList(userPrincipal.id))


    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/buyer/coupon/{couponId}")
    fun issuedCoupon(
        @PathVariable couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<DefaultResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(couponService.issuedCoupon(couponId, userPrincipal.id))

    @PreAuthorize("hasRole('BUYER')")
    @DeleteMapping("/buyer/coupon/{couponId}")
    fun deleteBuyerCoupon(
        @PathVariable couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ) : ResponseEntity<DefaultResponse> = ResponseEntity.status(HttpStatus.OK).body(couponService.deleteBuyerCoupon(couponId, userPrincipal.id))

}