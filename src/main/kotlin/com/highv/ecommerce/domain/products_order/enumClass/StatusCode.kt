package com.highv.ecommerce.domain.products_order.enumClass

enum class StatusCode {
    ORDER_CANCELED,
    ORDERED,
    PAYMENTED,
    SHIPPING,
    DELIVERED,
    EXCHANGE_REQUESTED,
    EXCHANGED,
    EXCHANGE_REJECTED,
    REFUND_REQUESTED,
    REFUNDED,
    REFUND_REJECTED,
}

//- 결제 완료
//- 주문 취소
//- 배송중
//- 배송 완료
//- 교환 / 환불
