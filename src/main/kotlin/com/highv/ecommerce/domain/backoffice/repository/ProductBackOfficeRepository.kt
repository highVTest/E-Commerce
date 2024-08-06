package com.highv.ecommerce.domain.backoffice.repository

import com.highv.ecommerce.domain.backoffice.dto.salesstatics.TotalSalesResponse
import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.backoffice.entity.QProductBackOffice
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface ProductBackOfficeRepository : JpaRepository<ProductBackOffice, Long>, QueryProductBackOfficeRepository

interface QueryProductBackOfficeRepository {
    fun findTotalSalesStatisticsByProductIds(productIds: List<Long>): TotalSalesResponse
}

@Repository
class QueryProductBackOfficeRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : QueryProductBackOfficeRepository {

    override fun findTotalSalesStatisticsByProductIds(productIds: List<Long>): TotalSalesResponse {
        val productBackOffice = QProductBackOffice.productBackOffice

        val result = jpaQueryFactory
            .select(
                productBackOffice.soldQuantity.sum(),
                productBackOffice.soldQuantity.multiply(productBackOffice.price).sum()
            )
            .from(productBackOffice)
            .where(productBackOffice.product().id.`in`(productIds))
            .fetchOne()

        val totalSalesQuantity = result?.get(0, Long::class.java)?.toInt() ?: 0
        val totalSalesAmount = result?.get(1, Long::class.java)?.toInt() ?: 0

        return TotalSalesResponse(totalSalesQuantity, totalSalesAmount)
    }
}