package com.highv.ecommerce.infra.s3.entity

import jakarta.persistence.*
import org.springframework.stereotype.Component
import java.util.*

@Entity
@Table(name = "image")
@Component
class Image(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var uuid: String = UUID.randomUUID().toString(), //uuid + 파일이름

    @Column(nullable = false)
    var url: String,

    @Column(nullable = false)
    var fileName: String,