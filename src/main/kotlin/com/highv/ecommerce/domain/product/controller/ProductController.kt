package com.highv.ecommerce.domain.product.controller

import com.highv.ecommerce.domain.product.dto.CreateProductRequest
import com.highv.ecommerce.domain.product.dto.ProductResponse
import com.highv.ecommerce.domain.product.dto.UpdateProductRequest
import com.highv.ecommerce.domain.product.service.ProductService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(private val productService: ProductService) {

    //상품 등록
    @PostMapping
    fun createProduct(
        productRequest: CreateProductRequest,
    ): ResponseEntity<ProductResponse> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(productService.createProduct(productRequest))

    //상품 수정
    @PatchMapping("/{productId}")
    fun updateProduct(
        @PathVariable("productId") productId: Long,
        updateProductRequest: UpdateProductRequest
    ): ResponseEntity<ProductResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.updateProduct(productId, updateProductRequest))

    //상품 삭제
    @DeleteMapping("/{productId}")
    fun deleteProduct(
        @PathVariable productId: Long
    ): ResponseEntity<Unit> = ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(productService.deleteProduct(productId))

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
        @RequestParam categoryId: Long,
        @PageableDefault(size = 10, page = 0) pageable: Pageable
    ): ResponseEntity<Page<ProductResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.getProductsByCategory(categoryId, pageable))

    //상품 검색하기
    @GetMapping("/search")
    fun searchProduct(
        @RequestParam keyword: String,
        @PageableDefault(size = 10, page = 0) pageable: Pageable
    ): ResponseEntity<Page<ProductResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.searchProduct(keyword, pageable))
}