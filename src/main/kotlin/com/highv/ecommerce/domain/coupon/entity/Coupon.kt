package com.highv.ecommerce.domain.coupon.entity

import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import com.highv.ecommerce.domain.product.entity.Product
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "coupon")
class Coupon(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_policy", nullable = false)
    var discountPolicy: DiscountPolicy,

    @Column(name = "discount", nullable = true)
    var discount: Int,

    @Column(name = "quantity", nullable = false)
    var quantity: Int,

    @Column(name = "expired_at", nullable = false)
    var expiredAt: LocalDateTime,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "deleted_At", nullable = true)
    val deletedAt: LocalDateTime? = null,

    @Column(name = "is_deleted", nullable = false)
    val isDeleted: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product

){

    fun update(updateCouponRequest: UpdateCouponRequest) {
        if(updateCouponRequest.discountPolicy == DiscountPolicy.DISCOUNT_RATE && updateCouponRequest.discount > 100){
            throw RuntimeException("할인율은 100%를 넘길 수 없습 니다")
        }

        discountPolicy = updateCouponRequest.discountPolicy
        discount = updateCouponRequest.discount
        expiredAt = updateCouponRequest.expiredAt
        quantity = updateCouponRequest.quantity
    }

    fun spendCoupon() {
        if(quantity <= 0) throw RuntimeException("쿠폰이 매진 되었습니다")
        quantity -= 1
    }

    fun validExpiredAt() {
        if(expiredAt <= LocalDateTime.now()) throw RuntimeException("쿠폰 유호 기간이 지났 습니다")
    }
}