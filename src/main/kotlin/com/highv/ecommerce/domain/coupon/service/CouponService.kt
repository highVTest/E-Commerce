package com.highv.ecommerce.domain.coupon.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.coupon.dto.CouponResponse
import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.repository.CouponRepository
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val productRepository: ProductRepository,
){

    @Transactional
    fun createCoupon(couponRequest: CreateCouponRequest, userPrincipal: UserPrincipal): DefaultResponse{

        val product = productRepository.findByIdOrNull(couponRequest.productId) ?: throw RuntimeException()

        couponRepository.save(
            Coupon(
                product = product,
                discountPolicy = couponRequest.discountPolicy,
                discount = couponRequest.discount,
                expiredAt = couponRequest.expiredAt,
                createdAt = LocalDateTime.now(),
                isDeleted = false,
                deletedAt = null,
                quantity = couponRequest.quantity,
            )
        )

        return DefaultResponse.from("쿠폰 생성이 완료 되었습니다")
    }

    @Transactional
    fun updateCoupon(couponId: Long, updateCouponRequest: UpdateCouponRequest, userPrincipal: UserPrincipal): DefaultResponse {

        val result = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

        result.update(updateCouponRequest)

        return DefaultResponse.from("쿠폰 업데이트가 완료 되었습니다")
    }

    @Transactional
    fun deleteCoupon(couponId: Long, userPrincipal: UserPrincipal): DefaultResponse {

        val result = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

        couponRepository.delete(result)

        return DefaultResponse.from("쿠폰 삭제가 완료 되었습니다")
    }


    fun getSellerCouponById(couponId: Long, userPrincipal: UserPrincipal): CouponResponse {
        val result = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

        return CouponResponse.from(result)
    }

    fun getSellerCouponList(userPrincipal: UserPrincipal): List<CouponResponse>? {
        return couponRepository.findAll().map{ CouponResponse.from(it) }
    }

    fun getBuyerCouponById(couponId: Long, userPrincipal: UserPrincipal): CouponResponse {

        val result = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

        return CouponResponse.from(result)
    }

    fun getBuyerCouponList(userPrincipal: UserPrincipal): List<CouponResponse>? {
        return couponRepository.findAll().map{ CouponResponse.from(it) }
    }

    fun issuedCoupon(couponId: Long, userPrincipal: UserPrincipal): DefaultResponse {

        val result = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

        result.validExpiredAt()
        result.spendCoupon()

        return DefaultResponse.from("쿠폰이 지급 되었습니다")
    }

}