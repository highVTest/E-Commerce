package com.highv.ecommerce.domain.product.category.controller

import com.highv.ecommerce.domain.product.category.dto.CategoryResponse
import com.highv.ecommerce.domain.product.category.service.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/category")
class CategoryController(
    private val categoryService: CategoryService
) {
    //추가
    @PostMapping
    fun addCategory(
        @RequestParam category: String
    ): ResponseEntity<CategoryResponse> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(categoryService.addCategory(category))

    //삭제
    @DeleteMapping("/{categoryId}")
    fun deleteCategory(
        @PathVariable categoryId: Long
    ): ResponseEntity<Unit> = ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(categoryService.deleteCategory(categoryId))
}