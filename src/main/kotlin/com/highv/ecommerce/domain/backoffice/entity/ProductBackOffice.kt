package com.highv.ecommerce.domain.backoffice.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class ProductBackOffice(
    @Id
    var id: Long,

    @Column(name = "quantity")
    var quantity: Int,

    @Column(name = "price")
    var price: Int,

    @Column(name = "product_id")
    var productId: Long,

    @Column(name = "sold_quantity")
    var soldQuantity: Int,
)