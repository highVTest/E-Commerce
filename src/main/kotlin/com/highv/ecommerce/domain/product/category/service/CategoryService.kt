package com.highv.ecommerce.domain.product.category.service

import com.highv.ecommerce.domain.product.category.dto.CategoryResponse
import com.highv.ecommerce.domain.product.category.entity.Category
import com.highv.ecommerce.domain.product.category.repository.CategoryRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    @Transactional
    fun addCategory(categoryName: String): CategoryResponse {
        val category = Category(
            name = categoryName
        )
        val savedCategory = categoryRepository.save(category)
        return CategoryResponse.from(savedCategory)
    }

    @Transactional
    fun deleteCategory(categoryId: Long) {
        val category = categoryRepository.findByIdOrNull(categoryId)
            ?: throw EntityNotFoundException("Category Not Found")
        categoryRepository.delete(category)
    }
}