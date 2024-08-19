package com.highv.ecommerce.domain.backoffice.entity

import com.highv.ecommerce.domain.product.entity.Product
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "product_backoffice")
class ProductBackOffice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "quantity")
    var quantity: Int,

    @Column(name = "price")
    var price: Int,

    @Column(name = "sold_quantity")
    var soldQuantity: Long,

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    var product: Product
)