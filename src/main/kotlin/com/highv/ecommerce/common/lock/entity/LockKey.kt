package com.highv.ecommerce.common.lock.entity

import jakarta.persistence.*

@Entity
@Table(name = "lock_key")
class LockKey(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "code", nullable = false)
    val code: String,

    @Column(name="lock_num", nullable = false)
    val lockNumber: Long,

)
