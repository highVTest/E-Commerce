package com.highv.ecommerce.domain.coupon.entity

import com.highv.ecommerce.domain.buyer.entity.Buyer
import jakarta.persistence.*

@Entity
@Table(name = "coupon_to_buyer")
class CouponToBuyer(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    val coupon: Coupon,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coupon_id")
    val buyer: Buyer,

)