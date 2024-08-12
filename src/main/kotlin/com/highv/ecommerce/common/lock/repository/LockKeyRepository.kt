package com.highv.ecommerce.common.lock.repository

import com.highv.ecommerce.common.lock.entity.LockKey
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import java.util.*

interface LockKeyRepository: JpaRepository<LockKey, Long>{

    @Lock(value = LockModeType.PESSIMISTIC_WRITE) // Lock 모드를 비관적 락으로 설정 한다
    fun findByCode(code: String): LockKey

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    override fun findById(id: Long): Optional<LockKey>

    fun existsByCode(code: String): Boolean
}