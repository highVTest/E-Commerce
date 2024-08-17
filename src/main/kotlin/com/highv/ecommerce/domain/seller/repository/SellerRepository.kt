package com.highv.ecommerce.domain.seller.repository

import com.highv.ecommerce.domain.seller.entity.Seller
import org.springframework.data.jpa.repository.JpaRepository

interface SellerRepository : JpaRepository<Seller, Long> {
    fun findByEmail(email: String): Seller?
    fun existsByEmail(email: String): Boolean
}