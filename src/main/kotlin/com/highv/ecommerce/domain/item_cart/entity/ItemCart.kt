package com.highv.ecommerce.domain.item_cart.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "cart_item")
class ItemCart(

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "product_name", nullable = false)
    var productName : String,

    @Column(name = "price", nullable = false)
    var price : Int,

    @Column(name = "quantity", nullable = false)
    var quantity : Int,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted : Boolean = false,

    @Column(name = "buyer_id", nullable = false)
    val buyerId : Long,


    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @Column(name = "order_id", nullable = true)
    var orderId : Long? = null

    @Column(name = "deleted_at", nullable = true)
    var deletedAt : LocalDateTime? = null


}