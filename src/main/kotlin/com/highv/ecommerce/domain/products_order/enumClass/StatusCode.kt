package com.highv.ecommerce.domain.products_order.enumClass

enum class StatusCode {
    ORDER_CANCELED,
    ORDERED,
    PAYMENTED,

    DELIVERED_PREPARING,
    SHIPPING,
    DELIVERED,

    REFUND_REQUESTED,
    REFUNDED,
    REFUND_REJECTED,
}
