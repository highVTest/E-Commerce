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
    @PostMapping("/sanctions/seller/{seller_id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun sanctionSeller(@PathVariable sellerId: Long): ResponseEntity<DefaultResponse> {
        adminService.sanctionSeller(sellerId)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(DefaultResponse("판매자 제재 완료"))
    }

    // 상품 제재
    @PostMapping("/sanctions/product/{product_id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun sanctionProduct(@PathVariable productId: Long): ResponseEntity<DefaultResponse> {
        adminService.sanctionProduct(productId)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(DefaultResponse("상품 제재 완료"))
    }

    // // 구매자 제재 (미구현)
    // @PostMapping("/sanctions/buyer/{buyer_id}")
    // @PreAuthorize("hasRole('ADMIN')")
    // fun sanctionBuyer(@PathVariable buyerId: Long): ResponseEntity<DefaultResponse> {
    //     adminService.sanctionBuyer(buyerId)
    //     return ResponseEntity
    //     .status(HttpStatus.OK)
    //     .body(DefaultResponse("구매자 제재 완료"))
    // }

    // 블랙리스트 생성
    @PostMapping("/black-list")
    @PreAuthorize("hasRole('ADMIN')")
    fun createBlackList(@RequestBody request: CreateBlackListRequest): ResponseEntity<DefaultResponse> {
        adminService.createBlackList(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(DefaultResponse("블랙리스트 생성 완료"))
    }

    // 블랙리스트 조회
    @GetMapping("/black-list")
    @PreAuthorize("hasRole('ADMIN')")
    fun getBlackLists(): ResponseEntity<List<BlackListResponse>> {
        val blackLists = adminService.getBlackLists()
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(blackLists)
    }

    // 블랙리스트 단건 조회
    @GetMapping("/black-list/{black-list_id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getBlackList(@PathVariable blackListId: Long): ResponseEntity<BlackListResponse> {
        val blackList = adminService.getBlackList(blackListId)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(blackList)
    }

    // 블랙리스트 삭제
    @DeleteMapping("/black-list/{black-list_id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteBlackList(@PathVariable blackListId: Long): ResponseEntity<DefaultResponse> {
        adminService.deleteBlackList(blackListId)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(DefaultResponse("블랙리스트 삭제 완료"))
    }
}