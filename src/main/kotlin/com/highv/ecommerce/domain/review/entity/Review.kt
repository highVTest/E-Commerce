package com.highv.ecommerce.domain.review.entity

import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.product.entity.Product
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "review")
class Review(

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    val buyer: Buyer,

    @Column(name = "deleted_at")
    val deletedAt: LocalDateTime? = null,

    @Column(name = "is_deleted")
    val isDeleted: Boolean = false,

    @Column(name = "rate")
    var rate: Float,

    @Column(name = "content")
    var content: String,

    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null


}