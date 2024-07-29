package com.highv.ecommerce.domain.item_cart.entity

import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.product.entity.Product
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "cart")
class ItemCart(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

    @Column(name = "quantity", nullable = false)
    var quantity: Int,

    @Column(name = "buyer_id", nullable = false)
    val buyerId: Long,

    @Column(name = "shop_id", nullable = false)
    val shopId: Long,

    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    fun updateQuantity(quantity: Int) {

        if (quantity <= 0) throw CustomRuntimeException(400, "물품의 수량이 0보다 작거나 같을 수 없습니다.")

        this.quantity = quantity
    }
}