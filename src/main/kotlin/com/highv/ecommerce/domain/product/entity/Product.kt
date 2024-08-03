package com.highv.ecommerce.domain.product.entity

import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

//product와 관련된 모든 sql에 적용된다. 삭제된 데이터를 불러온다거나 하는 기능이 있을 경우 사용하면 안된다.
@SQLRestriction("is_deleted=false")
@Entity
class Product(
    @Column(name = "name")
    var name: String,

    @Column(name = "description")
    var description: String,

    @Column(name = "product_image")
    var productImage: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime,

    @Column(name = "is_sold_out")
    var isSoldOut: Boolean,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,

    @Column(name = "is_deleted")
    var isDeleted: Boolean = false,

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