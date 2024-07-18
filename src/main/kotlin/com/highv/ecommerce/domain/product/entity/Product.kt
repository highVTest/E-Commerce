package com.highv.ecommerce.domain.product.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Product(
    @Column(name = "name")
    var name: String,

    @Column(name = "description")
    var description: String,

    @Column(name = "product_image")
    var productImage: String,

    @Column(name = "favorite")
    val favorite: Int,

    @Column(name = "created_at")
    val createdAt: LocalDateTime,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime,

    @Column(name = "is_sold_out")
    var isSoldOut: Boolean,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime,

    @Column(name = "is_deleted")
    var isDeleted: Boolean,

    @Column(name = "shop_id")
    val shopId: Long,

    @Column(name = "category_id")
    var categoryId: Long,
){
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}