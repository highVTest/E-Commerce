package com.highv.ecommerce.domain.products_order.entity

import com.highv.ecommerce.domain.products_order.dto.DescriptionRequest
import com.highv.ecommerce.domain.products_order.dto.OrderStatusRequest
import com.highv.ecommerce.domain.products_order.enumClass.StatusCode
import jakarta.persistence.*
import org.springframework.boot.context.properties.bind.DefaultValue
import java.time.LocalDateTime

@Entity
@Table(name = "products_order")
class ProductsOrder(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status_code", nullable = false)
    var statusCode : StatusCode,

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

    @Column(name="reg_dt", nullable = false)
    val regDate : LocalDateTime,

    @Column(name="deleted_at", nullable = false)
    val deletedAt: LocalDateTime? = null,

    @DefaultValue("false")
    @Column(name = "is_deleted", nullable = false)
    val isDeleted: Boolean = false
){
    fun <T> update(orderStatusRequest: T) {

        if(orderStatusRequest is OrderStatusRequest){
            statusCode = orderStatusRequest.statusCode
        }else if(orderStatusRequest is StatusCode){
            statusCode = orderStatusRequest
        }else throw RuntimeException("잘못된 값 입니다")

    }




}

//Refund
//RefundReject
//OrderCanceled