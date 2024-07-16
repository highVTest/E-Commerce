package com.highv.ecommerce.domain.product.repository

import com.highv.ecommerce.domain.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long>