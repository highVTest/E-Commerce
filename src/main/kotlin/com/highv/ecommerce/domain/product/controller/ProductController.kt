package com.highv.ecommerce.domain.product.controller

import com.highv.ecommerce.domain.product.dto.CreateProductRequest
import com.highv.ecommerce.domain.product.dto.ProductResponse
import com.highv.ecommerce.domain.product.dto.UpdateProductRequest
import com.highv.ecommerce.domain.product.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {

    //상품 등록
    @PostMapping
    fun createProduct(
        productRequest: CreateProductRequest,
    ):ResponseEntity<ProductResponse> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(productService.createProduct(productRequest))

    //상품 수정
    @PatchMapping("/{productId}")
    fun updateProduct(
        @PathVariable("productId") productId:Long,
        updateProductRequest: UpdateProductRequest
    ):ResponseEntity<ProductResponse> = ResponseEntity
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
}