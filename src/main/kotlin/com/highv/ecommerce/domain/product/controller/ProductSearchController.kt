package com.highv.ecommerce.domain.product.controller

import com.highv.ecommerce.domain.product.dto.ProductResponse
import com.highv.ecommerce.domain.product.dto.TopSearchKeyword
import com.highv.ecommerce.domain.product.service.ProductSearchService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductSearchController(
    private val productSearchService: ProductSearchService
) {
    @GetMapping("/top10")
    fun topSearch10(
        @RequestParam(defaultValue = "10") limit: Long
    ): ResponseEntity<Set<TopSearchKeyword>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(productSearchService.topSearch10(limit))

    @GetMapping("/search")
    fun searchByRedis(
        @RequestParam keyword: String,
        @RequestParam(required = false, defaultValue = "price") sortBy: String,
        @RequestParam(required = false, defaultValue = "ASC") sortOrder: String,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): ResponseEntity<Page<ProductResponse>> {
        val pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order(Sort.Direction.fromString(sortOrder), sortBy)))
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(productSearchService.searchByRedis(keyword, pageRequest))
    }

    @GetMapping("/all")
    fun getAllProducts(
        @PageableDefault(size = 10, page = 0) pageable: Pageable
    ): ResponseEntity<Page<ProductResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(productSearchService.getAllProducts(pageable))
}