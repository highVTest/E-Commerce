package com.highv.ecommerce.domain.coupon.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.coupon.dto.CouponResponse
import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import com.highv.ecommerce.domain.coupon.repository.CouponRepository
import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val productRepository: ProductRepository,
    private val couponToBuyerRepository: CouponToBuyerRepository,
    private val buyerRepository: BuyerRepository
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

        val buyer = buyerRepository.findByEmail(userPrincipal.email) ?: throw RuntimeException("바이어가 존재 하지 않습니다")
        val coupon = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

        coupon.validExpiredAt()

        couponToBuyerRepository.save(
            CouponToBuyer(
                buyer = buyer,
                coupon = coupon
            )
        )

        coupon.spendCoupon()

        return DefaultResponse.from("쿠폰이 지급 되었습니다")
    }

}