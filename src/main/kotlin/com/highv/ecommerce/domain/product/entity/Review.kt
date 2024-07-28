package com.highv.ecommerce.domain.product.entity

import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.product.dto.ReviewResponse
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "review")
class Review(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(name = "buyer_id")
    val buyerId: Long,

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