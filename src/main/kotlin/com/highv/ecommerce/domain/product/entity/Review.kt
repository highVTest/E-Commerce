package com.highv.ecommerce.domain.product.entity

import jakarta.persistence.*

@Entity
@Table(name = "review")
class Review(
    @Column(name = "product_id")
    val productId: Long,

    @Column(name = "reviewer_name")
    val reviewerName: String, //이메일? , 닉네임?

    @Column(name = "rating")
    val rating: Int,

    @Column(name = "comment")
    val comment: String,

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

}