package com.highv.ecommerce.domain.products_order.enumClass

enum class StatusCode {
    ORDER_CANCELED,
    ORDERED,
    PAYMENTED,

    DELIVERED_PREPARING,
    SHIPPING,
    DELIVERED,

    EXCHANGE_REQUESTED,
    EXCHANGED,
    EXCHANGE_REJECTED,

    REFUND_REQUESTED,
    REFUNDED,
    REFUND_REJECTED,
}
