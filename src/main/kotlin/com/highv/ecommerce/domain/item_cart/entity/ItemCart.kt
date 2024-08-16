package com.highv.ecommerce.domain.item_cart.entity

import com.highv.ecommerce.common.exception.InvalidQuantityException
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.seller.shop.entity.Shop
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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    val shop: Shop,
    // @Column(name = "shop_id", nullable = false)
    // val shopId: Long,

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    fun updateQuantity(quantity: Int) {

        if (quantity < 1) throw InvalidQuantityException(400, "상품의 수량이 1개보다 적을 수 없습니다.")

        this.quantity = quantity
    }
}