package com.highv.ecommerce.domain.coupon.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class CouponService {


    fun createCoupon(couponRequest: CreateCouponRequest): DefaultResponse{

        couponRepository.save(
            Coupon(
                productId = couponRequest.productId,
                discountRate = couponRequest.discountRate,
                discountPrice = couponRequest.discountPrice,
                expiredAt = couponRequest.expiredAt,
                quantity = couponRequest.quantity,
                createdAt = LocalDateTime.now(),
                isDeleted = false,
                deletedAt = null
            )
        )

        return DefaultResponse.from("쿠폰 생성이 완료 되었습니다")
    }

}