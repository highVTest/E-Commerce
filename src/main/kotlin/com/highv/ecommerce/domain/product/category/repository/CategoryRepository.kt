package com.highv.ecommerce.domain.product.category.repository

import com.highv.ecommerce.domain.product.category.entity.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long>