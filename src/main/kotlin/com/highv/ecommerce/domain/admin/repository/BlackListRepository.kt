package com.highv.ecommerce.domain.admin.repository

import com.highv.ecommerce.domain.admin.entity.BlackList
import org.springframework.data.jpa.repository.JpaRepository

interface BlackListRepository : JpaRepository<BlackList, Long> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): BlackList?
}