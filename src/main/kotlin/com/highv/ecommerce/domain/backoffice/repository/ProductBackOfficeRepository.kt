package com.highv.ecommerce.domain.backoffice.repository

import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProductBackOfficeRepository : JpaRepository<ProductBackOffice, Long> {
    @Query("SELECT SUM(p.soldQuantity) FROM ProductBackOffice p WHERE p.product.id IN :productIds")
    fun findTotalSoldQuantitiesByProductIds(productIds: List<Long>): Int

    @Query("SELECT SUM(p.soldQuantity * p.price) FROM ProductBackOffice p WHERE p.product.id IN :productIds")
    fun findTotalSalesAmountByProductIds(productIds: List<Long>): Int

    fun findProductBackOfficesByProductId(productId: Long): ProductBackOffice?
}