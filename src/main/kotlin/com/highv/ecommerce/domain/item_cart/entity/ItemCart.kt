package com.highv.ecommerce.domain.item_cart.entity

import com.highv.ecommerce.domain.product.entity.Product
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "cart_item")
class ItemCart(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

    @Column(name = "product_name", nullable = false)
    var productName: String,

    @Column(name = "price", nullable = false)
    var price: Int,

    @Column(name = "quantity", nullable = false)
    var quantity: Int,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false,

    @Column(name = "buyer_id", nullable = false)
    val buyerId: Long,

    @Column(name = "is_coupon", nullable = false)
    var isCoupon: Boolean = false,

    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @Column(name = "order_id", nullable = true)
    var orderId: Long? = null

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: LocalDateTime? = null

    fun updateQuantity(quantity: Int) {

        if (quantity <= 0) throw RuntimeException("물품의 수량이 0보다 작거나 같을 수 없습니다.")

        this.quantity = quantity
    }

    fun paymentUpdate(productsOrderId: Long) {
        this.orderId = productsOrderId
        this.deletedAt = LocalDateTime.now()
        this.isDeleted = true
    }

    fun useCoupon() {
        this.isCoupon = !isCoupon
    }
}