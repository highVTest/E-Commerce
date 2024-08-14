package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.Coupon
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface CouponJpaRepository: JpaRepository<Coupon, Long>{

    fun existsByProductId(productId: Long): Boolean

    fun findByIdAndSellerId(id: Long, sellerId: Long): Coupon?


    fun findAllBySellerId(sellerId: Long): List<Coupon>

    @Query("SELECT c.id FROM Coupon c WHERE c.product.id in :productIdList ")
    fun findAllByProductIdList(productIdList: List<Long>): List<Long>

    fun findByProductId(productId: Long): Coupon?

    @Modifying
    @Transactional
    @Query("DELETE FROM Coupon c WHERE c.expiredAt <= CURRENT_TIMESTAMP")
    fun deleteAllByExpiredAt()
}