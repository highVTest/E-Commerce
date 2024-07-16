package com.highv.ecommerce.domain.coupon.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.coupon.dto.CouponResponse
import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class CouponService {


    fun createCoupon(couponRequest: CreateCouponRequest): DefaultResponse{

        val coupon = couponRepository.save(
            Coupon(
                productId = couponRequest.productId,
                discountRate = couponRequest.discountRate,
                discountPrice = couponRequest.discountPrice,
                expiredAt = couponRequest.expiredAt,
                createdAt = LocalDateTime.now(),
                isDeleted = false,
                deletedAt = null
            )
        )

        return DefaultResponse.from("쿠폰 생성이 완료 되었습니다")
    }

    fun updateCoupon(couponId: Long, updateCouponRequest: UpdateCouponRequest): DefaultResponse {

        val result = couponRepository.findByIdOrNull(couponId) ?: throw ModelNotFoundException()

        result.update(updateCouponRequest)

        return DefaultResponse.from("쿠폰 업데이트가 완료 되었습니다")
    }

    fun deleteCoupon(couponId: Long): DefaultResponse {

        val result = couponRepository.findByIdOrNull(couponId) ?: throw ModelNotFoundException()

        couponRepository.delete(result)

        return DefaultResponse.from("쿠폰 삭제가 완료 되었습니다")
    }

    @Transactional(readOnly = true)
    fun getSellerCouponById(couponId: Long): CouponResponse {
        val result = couponRepository.findByIdOrNull(couponId) ?: throw ModelNotFoundException()

        return CouponResponse.from(result)
    }

    @Transactional(readOnly = true)
    fun getSellerCouponList(): List<CouponResponse>? {
        return couponRepository.findAll().let{ CouponResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun getBuyerCouponById(couponId: Long): CouponResponse {

        val result = couponRepository.findByIdOrNull(couponId) ?: throw ModelNotFoundException()

        return CouponResponse.from(result)
    }


}