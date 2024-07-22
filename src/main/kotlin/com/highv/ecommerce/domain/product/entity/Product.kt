package com.highv.ecommerce.domain.product.entity

import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.shop.entity.Shop
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
    var deletedAt: LocalDateTime? = null,

    @Column(name = "is_deleted")
    var isDeleted:Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    val shop: Shop,

    @Column(name = "category_id")
    var categoryId: Long,

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY, optional = false)
    var productBackOffice: ProductBackOffice? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}