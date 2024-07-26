package com.highv.ecommerce.domain.order_master.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "order_master")
class OrderMaster(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    @Column(name = "reg_dt", nullable = false)
    val regDateTime: LocalDateTime = LocalDateTime.now(),
)