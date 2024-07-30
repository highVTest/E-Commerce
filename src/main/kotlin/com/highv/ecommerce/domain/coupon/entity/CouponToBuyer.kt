package com.highv.ecommerce.domain.coupon.entity

import com.highv.ecommerce.domain.buyer.entity.Buyer
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "coupon_to_buyer")
class CouponToBuyer(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    val coupon: Coupon,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    val buyer: Buyer,

    @Column(name = "is_used", nullable = false)
    var isUsed: Boolean = false,

    ) {
    fun useCoupon() {
        isUsed = true
    }

    fun returnCoupon() {
        isUsed = false
    }
}