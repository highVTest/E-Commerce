package com.highv.ecommerce.domain.backoffice.admin.repository

import com.highv.ecommerce.domain.backoffice.admin.entity.BlackList
import org.springframework.data.jpa.repository.JpaRepository

interface BlackListRepository : JpaRepository<BlackList, Long> {
    fun findByEmail(email: String): BlackList?
}