package com.highv.ecommerce.domain.products_order.entity

import com.highv.ecommerce.domain.products_order.enumClass.StatusCode
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "products_order")
class ProductsOrder(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status_code", nullable = false)
    val statusCode : StatusCode,

    @Column(name = "buyer_id", nullable = false)
    val buyerId: Long,

    @Column(name = "paid_yn", nullable = false)
    val isPaid : Boolean,

    @Column(name = "pay_dt", nullable = false)
    val payDate : LocalDateTime,

    @Column(name = "total_price", nullable = false)
    val totalPrice : Int,

    @Column(name = "delivery_start_at", nullable = false)
    val deliveryStartAt: LocalDateTime,

    @Column(name = "delivery_end_at", nullable = false)
    val deliveryEndAt: LocalDateTime,

    @Column(name = "cancel_yn", nullable = false)
    val isCancelled : Boolean,

    @Column(name= "cancel_dt", nullable = false)
    val cancelDate : LocalDateTime?,

    @Column(name= "cancel_desc", nullable = false)
    val cancelDescription : String?,

    @Column(name="refund_yn", nullable = false)
    val isRefund : Boolean,

    @Column(name = "refund_dt", nullable = false)
    val refundDate : LocalDateTime?,

    @Column(name="refund_desc", nullable = false)
    val refundDescription : String?,

    @Column(name = "refund_reject_yn", nullable = false)
    val isRefundReject : Boolean,

    @Column(name = "refund_reject_dt")
    val refundRejectDate : LocalDateTime?,

    @Column(name="refund_reject_desc", nullable = false)
    val refundRejectDescription : String?,

    @Column(name="reg_dt", nullable = false)
    val regDate : LocalDateTime,

    @Column(name="deleted_at", nullable = false)
    val deletedAt: LocalDateTime?,

    @Column(name = "is_deleted", nullable = false)
    val isDeleted: Boolean
)