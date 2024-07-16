package com.highv.ecommerce.domain.coupon.entity

import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.product.entity.Product
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "coupon")
class Coupon(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "discount_rate", nullable = true)
    var discountRate: Int? = null,

    @Column(name = "discount_price", nullable = true)
    var discountPrice: Int? = null,

    @Column(name = "expired_at", nullable = false)
    var expiredAt: LocalDateTime,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "deleted_At", nullable = true)
    val deletedAt: LocalDateTime? = null,

    @Column(name = "is_deleted", nullable = false)
    val isDeleted: Boolean = false,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    val product: Product
){

    fun update(updateCouponRequest: UpdateCouponRequest) {
        discountRate = updateCouponRequest.discountRate
        discountPrice = updateCouponRequest.discountPrice
        expiredAt = updateCouponRequest.expiredAt
    }
}