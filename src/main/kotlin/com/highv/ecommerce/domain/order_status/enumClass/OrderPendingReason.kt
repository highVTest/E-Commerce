package com.highv.ecommerce.domain.order_status.enumClass


enum class OrderPendingReason {

    NONE,

//    CANCEL_REQUESTED,
//    CANCELLING,
//    CANCELLED,
//    CANCEL_REJECTED,

    REFUND_REQUESTED,
    REFUNDING,
    REFUNDED,
    REFUND_REJECTED,

    EXCHANGE_REQUESTED,
    EXCHANGING,
    EXCHANGED,
    EXCHANGE_REJECTED,

}
