package com.highv.ecommerce.domain.backoffice.admin.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.backoffice.admin.dto.BlackListResponse
import com.highv.ecommerce.domain.backoffice.admin.dto.CreateBlackListRequest
import com.highv.ecommerce.domain.backoffice.admin.service.AdminService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val adminService: AdminService
) {
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

    /* 기능 추가할 부분이 많아서 V2에서 추가할 예정
        // 판매자 탈퇴 대기 회원 승인
        @DeleteMapping("/seller/resign/{sellerId}")
        @PreAuthorize("hasRole('ADMIN')")
        fun approveSellerResignation(@PathVariable sellerId: Long): ResponseEntity<DefaultResponse> =
            ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.approveSellerResignation(sellerId))

        // 판매자 승인 대기 회원 승격
        //Seller Entity에 status가 없어서 주석처리
        @PatchMapping("/seller/approval/{sellerId}")
        @PreAuthorize("hasRole('ADMIN')")
        fun promotePendingSeller(@PathVariable sellerId: Long): ResponseEntity<DefaultResponse> =
            ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.promotePendingSeller(sellerId))
        */
}
