package com.highv.ecommerce.domain.coupon.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.coupon.dto.CouponResponse
import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.repository.CouponRepository
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class CouponService(
    private val couponRepository: CouponRepository
){


    fun createCoupon(couponRequest: CreateCouponRequest, userPrincipal: UserPrincipal): DefaultResponse{

        val coupon = couponRepository.save(
            Coupon(
                productId = couponRequest.productId,
                discountRate = couponRequest.discountRate,
                discountPrice = couponRequest.discountPrice,
                expiredAt = LocalDateTime.now(),
                createdAt = LocalDateTime.now(),
                isDeleted = false,
                deletedAt = null,
                quantity = couponRequest.quantity,
            )
        )

        return DefaultResponse.from("쿠폰 생성이 완료 되었습니다")
    }

    fun updateCoupon(couponId: Long, updateCouponRequest: UpdateCouponRequest): DefaultResponse {

        val result = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")
        result.update(updateCouponRequest)

        return DefaultResponse.from("쿠폰 업데이트가 완료 되었습니다")
    }

    fun deleteCoupon(couponId: Long): DefaultResponse {

        val result = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

        couponRepository.delete(result)

        return DefaultResponse.from("쿠폰 삭제가 완료 되었습니다")
    }

    @Transactional(readOnly = true)
    fun getSellerCouponById(couponId: Long): CouponResponse {
        val result = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

        return CouponResponse.from(result)
    }

    @Transactional(readOnly = true)
    fun getSellerCouponList(): List<CouponResponse>? {
        return couponRepository.findAll().map{ CouponResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun getBuyerCouponById(couponId: Long): CouponResponse {

        val result = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

        return CouponResponse.from(result)
    }

    @Transactional(readOnly = true)
    fun getBuyerCouponList(): List<CouponResponse>? {
        return couponRepository.findAll().map{ CouponResponse.from(it) }
    }

    fun issuedCoupon(couponId: Long): DefaultResponse {

        val result = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

        result.validExpiredAt()
        result.spendCoupon()

        return DefaultResponse.from("쿠폰이 지급 되었습니다")
    }

}