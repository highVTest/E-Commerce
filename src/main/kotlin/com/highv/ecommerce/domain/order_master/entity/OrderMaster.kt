package com.highv.ecommerce.domain.order_master.entity

import com.highv.ecommerce.domain.order_master.dto.OrderStatusRequest
import com.highv.ecommerce.domain.order_master.enumClass.StatusCode
import jakarta.persistence.*
import org.springframework.boot.context.properties.bind.DefaultValue
import java.time.LocalDateTime

@Entity
@Table(name = "order_master")
class OrderMaster(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    @Column(name = "reg_dt", nullable = false)
    val regDateTime: LocalDateTime,
)