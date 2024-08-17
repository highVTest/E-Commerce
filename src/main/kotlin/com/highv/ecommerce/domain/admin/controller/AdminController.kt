package com.highv.ecommerce.domain.admin.controller

import com.highv.ecommerce.common.dto.AccessTokenResponse
import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.admin.dto.AdminBySellerResponse
import com.highv.ecommerce.domain.admin.dto.BlackListResponse
import com.highv.ecommerce.domain.admin.service.AdminService
import com.highv.ecommerce.domain.seller.dto.SellerResponse
import com.highv.ecommerce.domain.auth.dto.LoginRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val adminService: AdminService
) {
    @PostMapping("/login")
    fun loginAdmin(@RequestBody loginRequest: LoginRequest): ResponseEntity<AccessTokenResponse> =
    ResponseEntity.ok().body(adminService.loginAdmin(loginRequest))

    // 판매자 제재
    @PostMapping("/sanctions/seller/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun sanctionSeller(@PathVariable sellerId: Long): ResponseEntity<DefaultResponse> =
        ResponseEntity
            .status(HttpStatus.OK)
            .body(adminService.sanctionSeller(sellerId))

    // 상품 제재
    @PostMapping("/sanctions/product/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun sanctionProduct(@PathVariable productId: Long): ResponseEntity<DefaultResponse> =
        ResponseEntity
            .status(HttpStatus.OK)
            .body(adminService.sanctionProduct(productId))

    // // 구매자 제재 (미구현)
    // @PostMapping("/sanctions/buyer/{buyerId}")
    // @PreAuthorize("hasRole('ADMIN')")
    // fun sanctionBuyer(@PathVariable buyerId: Long): ResponseEntity<DefaultResponse> =
    //     ResponseEntity
    //     .status(HttpStatus.OK)
    //     .body(adminService.sanctionBuyer(buyerId))

    // 블랙리스트 조회
    @GetMapping("/black-list")
    @PreAuthorize("hasRole('ADMIN')")
    fun getBlackLists(): ResponseEntity<List<BlackListResponse>> =
        ResponseEntity
            .status(HttpStatus.OK)
            .body(adminService.getBlackLists())

    // 블랙리스트 단건 조회
    @GetMapping("/black-list/{blackListId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getBlackList(@PathVariable blackListId: Long): ResponseEntity<BlackListResponse> =
        ResponseEntity
            .status(HttpStatus.OK)
            .body(adminService.getBlackList(blackListId))

    // 블랙리스트 삭제
    @DeleteMapping("/black-list/{blackListId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteBlackList(@PathVariable blackListId: Long): ResponseEntity<DefaultResponse> =
        ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(adminService.deleteBlackList(blackListId))

    // 판매자 탈퇴 대기 회원 승인
    @DeleteMapping("/seller/resign/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun approveSellerResignation(@PathVariable sellerId: Long): ResponseEntity<DefaultResponse> =
        ResponseEntity
            .status(HttpStatus.OK)
            .body(adminService.approveSellerResignation(sellerId))

    // 판매자 승인 대기 회원 승격
    @PatchMapping("/seller/approval/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun promotePendingSeller(@PathVariable sellerId: Long): ResponseEntity<DefaultResponse> =
        ResponseEntity
            .status(HttpStatus.OK)
            .body(adminService.promotePendingSeller(sellerId))

    // 판매자 전체 조회
    @GetMapping("/sellers")
    @PreAuthorize("hasRole('ADMIN')")
    fun getSellerLists(): ResponseEntity<List<SellerResponse>> =
        ResponseEntity
            .status(HttpStatus.OK)
            .body(adminService.getSellerLists())

    // 판매자 상세 조회
    @GetMapping("/sellers/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getSellerBySellerId(
        @PathVariable sellerId: Long
    ): ResponseEntity<AdminBySellerResponse> =
        ResponseEntity
            .status(HttpStatus.OK)
            .body(adminService.getSellerBySellerId(sellerId))
}
