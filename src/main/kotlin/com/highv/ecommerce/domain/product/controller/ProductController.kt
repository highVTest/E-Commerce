package com.highv.ecommerce.domain.product.controller

import com.highv.ecommerce.domain.product.dto.CreateProductRequest
import com.highv.ecommerce.domain.product.dto.ProductResponse
import com.highv.ecommerce.domain.product.dto.UpdateProductRequest
import com.highv.ecommerce.domain.product.service.ProductService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productService: ProductService
) {

    //상품 등록
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    fun createProduct(
        @AuthenticationPrincipal seller: UserPrincipal,
        @RequestPart productRequest: CreateProductRequest,
        @RequestPart (value ="file", required = false) file: MultipartFile
    ): ResponseEntity<ProductResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(productService.createProduct(seller.id, productRequest,file))
    }

    //상품 수정
    @PatchMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    fun updateProduct(
        @AuthenticationPrincipal seller: UserPrincipal,
        @PathVariable("productId") productId: Long,
        updateProductRequest: UpdateProductRequest
    ): ResponseEntity<ProductResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.updateProduct(seller.id, productId, updateProductRequest))

    //상품 삭제
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    fun deleteProduct(
        @AuthenticationPrincipal seller: UserPrincipal,
        @PathVariable productId: Long
    ): ResponseEntity<Unit> = ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(productService.deleteProduct(seller.id, productId))

    //상품 상세보기
    @GetMapping("/{productId}")
    fun getProductById(
        @PathVariable("productId") productId: Long
    ): ResponseEntity<ProductResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.getProductById(productId))

    //상품 전체보기
    //페이지네이션 적용
    @GetMapping("/all")
    fun getAllProducts(
        @PageableDefault(size = 10, page = 0) pageable: Pageable
    ): ResponseEntity<Page<ProductResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.getAllProducts(pageable))

    //카테고리별 상품보기
    @GetMapping("/category")
    fun getProductsByCategory(
        categoryId: Long,
        @PageableDefault(size = 10, page = 0) pageable: Pageable
    ): ResponseEntity<Page<ProductResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.getProductsByCategory(categoryId, pageable))

    //상품 검색하기
    @GetMapping("/search")
    fun searchProduct(
        keyword: String,
        @PageableDefault(size = 10, page = 0) pageable: Pageable
    ): ResponseEntity<Page<ProductResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.searchProduct(keyword, pageable))
}