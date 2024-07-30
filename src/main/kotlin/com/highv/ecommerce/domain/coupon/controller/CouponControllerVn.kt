package com.highv.ecommerce.domain.coupon.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.UnauthorizedUserException
import com.highv.ecommerce.domain.coupon.service.CouponServiceVn
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class CouponControllerVn(
    private val couponService: CouponServiceVn
) {
    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/v2/buyer/coupon/{couponId}")
    fun issuedCouponV2(
        @PathVariable couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<DefaultResponse> {

        if (userPrincipal == null) throw UnauthorizedUserException(401, "인증되지 않은 사용자입니다.")

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.issuedCouponV2(couponId, userPrincipal))
    }

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/v3/buyer/coupon/{couponId}")
    fun issuedCouponV3(
        @PathVariable couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<DefaultResponse> {

        if (userPrincipal == null) throw UnauthorizedUserException(401, "인증되지 않은 사용자입니다.")

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.issuedCouponV3(couponId, userPrincipal))
    }

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/v4/buyer/coupon/{couponId}")
    fun issuedCouponV4(
        @PathVariable couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<DefaultResponse> {

        if (userPrincipal == null) throw UnauthorizedUserException(401, "인증되지 않은 사용자입니다.")

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.issuedCouponV4(couponId, userPrincipal))
    }
}