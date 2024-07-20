package com.highv.ecommerce.domain.order_status.enumClass


enum class RejectReason {
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
    EXCHANGED,
    EXCHANGING,
    EXCHANGE_REJECTED,

}
