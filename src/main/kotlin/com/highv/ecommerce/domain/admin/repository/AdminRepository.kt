package com.highv.ecommerce.domain.admin.repository

import com.highv.ecommerce.domain.admin.entity.Admin
import org.springframework.data.jpa.repository.JpaRepository

interface AdminRepository : JpaRepository<Admin, Long> {
    fun findByEmail(email: String): Admin?
}