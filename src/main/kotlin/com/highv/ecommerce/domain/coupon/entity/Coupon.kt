package com.highv.ecommerce.domain.coupon.entity

import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import com.highv.ecommerce.domain.product.entity.Product
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

    @Column(name = "seller_id", nullable = false)
    val sellerId: Long,

){

    fun update(updateCouponRequest: UpdateCouponRequest) {

        if(updateCouponRequest.discountPolicy == DiscountPolicy.DISCOUNT_RATE && updateCouponRequest.discount > 40){
            throw CustomRuntimeException(400, "할인율은 40%를 넘길 수 없습니다")
        }

        discountPolicy = updateCouponRequest.discountPolicy
        discount = updateCouponRequest.discount
        expiredAt = updateCouponRequest.expiredAt
        quantity = updateCouponRequest.quantity
    }

    fun spendCoupon() {
        if(quantity <= 0) throw CustomRuntimeException(400, "쿠폰이 매진되었습니다")
        quantity -= 1
    }

    fun validExpiredAt() {
        if(expiredAt <= LocalDateTime.now()) throw CustomRuntimeException(400, "쿠폰 유효 기간이 지났습니다")
    }
}